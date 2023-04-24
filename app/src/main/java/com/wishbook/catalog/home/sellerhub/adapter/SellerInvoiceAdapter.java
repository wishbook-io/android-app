package com.wishbook.catalog.home.sellerhub.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayout;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.ToastUtil;
import com.wishbook.catalog.Utils.multipleimageselect.activities.AlbumSelectActivity;
import com.wishbook.catalog.commonmodels.responses.Image;
import com.wishbook.catalog.commonmodels.responses.Photos;
import com.wishbook.catalog.commonmodels.responses.SellerInvoiceResponse;
import com.wishbook.catalog.commonmodels.responses.SellerInvoice_images;
import com.wishbook.catalog.home.catalog.details.Activity_ProductPhotos;
import com.wishbook.catalog.home.sellerhub.Fragment_SellerInvoice_List;
import com.wishbook.catalog.home.sellerhub.SellerInvoiceAPIHandler;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SellerInvoiceAdapter extends RecyclerView.Adapter<SellerInvoiceAdapter.SellerInvoiceViewHolder> {


    ArrayList<SellerInvoiceResponse> itemsArrayList;
    Context context;
    Fragment fragment;

    public static int EDIT_POSITION = 0;
    public static int ADD_MORE_IMAGE_POSITION = 0;

    SellerInvoiceAdapter.SellerInvoiceItemChangeListener sellerInvoiceItemChangeListener;


    public SellerInvoiceAdapter(Context context, ArrayList<SellerInvoiceResponse> itemsArrayList, Fragment fragment) {
        this.itemsArrayList = itemsArrayList;
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public SellerInvoiceAdapter.SellerInvoiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_invoice_item, parent, false);
        return new SellerInvoiceAdapter.SellerInvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerInvoiceViewHolder holder, int position) {
        SellerInvoiceResponse invoiceResponse = itemsArrayList.get(position);
        holder.flexbox_image.removeAllViews();
        if (invoiceResponse.getSeller_invoice_number() != null && !invoiceResponse.getSeller_invoice_number().isEmpty()) {
            holder.txt_seller_invoice_number.setText("#" + invoiceResponse.getSeller_invoice_number());
        }
        holder.txt_invoice_create_date.setText(DateUtils.getFormattedDate(StringUtils.capitalize(itemsArrayList.get(position).getCreated().toLowerCase().trim())));

        if (invoiceResponse.getInvoices() != null && invoiceResponse.getInvoices().size() > 0) {
            holder.txt_invoice_numbers_values.setText(StaticFunctions.ArrayListToString(invoiceResponse.getInvoices(), StaticFunctions.COMMASEPRATEDNEWLINE));
        }


        if (invoiceResponse.getStatus() != null) {
            holder.txt_seller_invoice_status.setText(StringUtils.capitalize(StringUtils.capitalize(invoiceResponse.getStatus().toLowerCase().trim())));
            setColor(invoiceResponse.getStatus(), holder.txt_seller_invoice_status);

        }
        if (invoiceResponse.getImages() != null && invoiceResponse.getImages().size() > 0) {
            for (int i = 0; i < invoiceResponse.getImages().size(); i++) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.manifest_pdf_image_item, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(params);
                view.findViewById(R.id.txt_status).setVisibility(View.GONE);
                if (invoiceResponse.getImages().get(i).getPath() != null) {
                    StaticFunctions.loadFresco(context, "file://" + invoiceResponse.getImages().get(i).getPath(), (SimpleDraweeView) view.findViewById(R.id.img_set));
                } else if (invoiceResponse.getImages() != null) {
                    if (invoiceResponse.getImages().get(i).getImage().contains(".pdf")) {
                        ((SimpleDraweeView) view.findViewById(R.id.img_set)).getHierarchy().setImage(context.getResources().getDrawable(R.drawable.ic_pdf_file), 1, true);
                        if (!invoiceResponse.isEditMode()) {
                            if (checkSellerInvoicePdfIsDownloaded(invoiceResponse.getImages().get(i).getId()) != null) {
                                view.findViewById(R.id.txt_status).setVisibility(View.VISIBLE);
                            }
                            ;
                        }
                    } else {
                        StaticFunctions.loadFresco(context, invoiceResponse.getImages().get(i).getImage(), (SimpleDraweeView) view.findViewById(R.id.img_set));
                    }
                }
                ImageView img_remove_set = view.findViewById(R.id.img_remove_set);
                if (invoiceResponse.isEditMode()) {
                    img_remove_set.setEnabled(true);
                    img_remove_set.setVisibility(View.VISIBLE);
                } else {
                    img_remove_set.setEnabled(false);
                    img_remove_set.setVisibility(View.INVISIBLE);
                }
                final int finalI = i;
                img_remove_set.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                .title("Delete Image")
                                .content("Are you sure you want to delete this invoice image?")
                                .positiveText("OK")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        dialog.dismiss();
                                        ArrayList<SellerInvoice_images> temp = invoiceResponse.getImages();
                                        temp.remove(finalI);
                                        invoiceResponse.setImages(temp);
                                        if (temp.size() == 0 && invoiceResponse.isEditMode()) {
                                            invoiceResponse.setEditMode(false);
                                        }
                                        notifyItemChanged(holder.getAdapterPosition());
                                    }
                                })
                                .negativeText("Cancel")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();

                    }
                });

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (invoiceResponse.getImages().get(finalI).getImage().contains(".pdf")) {
                            if (fragment instanceof Fragment_SellerInvoice_List) {
                                if (checkSellerInvoicePdfIsDownloaded(invoiceResponse.getImages().get(finalI).getId()) != null) {
                                    openPDFFile(invoiceResponse.getImages().get(finalI).getId());
                                } else {
                                    ((Fragment_SellerInvoice_List) fragment).checkWritePermissionAndDownloadInvoice(invoiceResponse.getImages().get(finalI).getImage(), invoiceResponse.getImages().get(finalI).getId(), position);
                                }
                            }
                        } else {
                            ArrayList<Photos> photosArrayList = new ArrayList<>();
                            for (int i = 0; i < invoiceResponse.getImages().size(); i++) {
                                if (!invoiceResponse.getImages().get(finalI).getImage().contains(".pdf")) {
                                    photosArrayList.add(new Photos(invoiceResponse.getImages().get(i).getId(),
                                            new Image(invoiceResponse.getImages().get(i).getImage(), invoiceResponse.getImages().get(i).getImage())));
                                }
                            }
                            Intent photos_intent = new Intent(context, Activity_ProductPhotos.class);
                            photos_intent.putExtra("photos", photosArrayList);
                            photos_intent.putExtra("position", finalI);
                            context.startActivity(photos_intent);
                        }

                    }
                });
                holder.flexbox_image.addView(view);
            }
            if (invoiceResponse.isEditMode()) {
                holder.flexbox_image.addView(holder.attach_button);
            }
        } else {
            if (invoiceResponse.getStatus().equalsIgnoreCase("Created")) {
                holder.flexbox_image.addView(holder.btn_first_additional_image);
            }
        }


        if (invoiceResponse.isEditMode()) {
            holder.btn_save_images.setVisibility(View.VISIBLE);
            holder.btn_cancel_images.setVisibility(View.VISIBLE);
        } else {
            holder.btn_save_images.setVisibility(View.GONE);
            holder.btn_cancel_images.setVisibility(View.GONE);
        }

        holder.btn_first_additional_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ADD_MORE_IMAGE_POSITION = holder.getAdapterPosition();
                Intent intent = new Intent(context, AlbumSelectActivity.class);
                int limit = Constants.UPLOAD_SELLER_INVOICE_LIMIT;
                if (invoiceResponse.getImages() != null) {
                    limit = Constants.UPLOAD_SELLER_INVOICE_LIMIT - invoiceResponse.getImages().size();
                }
                if (limit == 0) {
                    Toast.makeText(context, "You can upload " + Constants.UPLOAD_SELLER_INVOICE_LIMIT + " images", Toast.LENGTH_SHORT).show();
                } else {
                    if (fragment instanceof Fragment_SellerInvoice_List)
                        ((Fragment_SellerInvoice_List) fragment).openTaker(context, limit);
                }
            }
        });

        holder.attach_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ADD_MORE_IMAGE_POSITION = holder.getAdapterPosition();
                Intent intent = new Intent(context, AlbumSelectActivity.class);
                int limit = Constants.UPLOAD_SELLER_INVOICE_LIMIT;
                if (invoiceResponse.getImages() != null) {
                    limit = Constants.UPLOAD_SELLER_INVOICE_LIMIT - invoiceResponse.getImages().size();
                }
                if (limit == 0) {
                    Toast.makeText(context, "You can upload " + Constants.UPLOAD_SELLER_INVOICE_LIMIT + " images", Toast.LENGTH_SHORT).show();
                } else {
                    if (fragment instanceof Fragment_SellerInvoice_List)
                        ((Fragment_SellerInvoice_List) fragment).openTaker(context, limit);
                }
            }
        });

        holder.btn_save_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (invoiceResponse.getImages() != null && invoiceResponse.getImages().size() > 0) {
                    SellerInvoiceAPIHandler apiHandler = new SellerInvoiceAPIHandler((Activity) context);
                    apiHandler.callSellerInvoiceImageUpload(invoiceResponse);
                    apiHandler.setSellerInvoiceAPIListener(new SellerInvoiceAPIHandler.SellerInvoiceAPIListener() {
                        @Override
                        public void onSuccessRequest() {
                            invoiceResponse.setEditMode(false);
                            notifyItemChanged(position);
                            ToastUtil.showShort("Invoice Uploaded successfully");
                            if (sellerInvoiceItemChangeListener != null) {
                                sellerInvoiceItemChangeListener.onUpdate(invoiceResponse.getId(), position);
                            }
                        }

                        @Override
                        public void onSuccessCancel() {

                        }

                        @Override
                        public void onError() {

                        }
                    });
                } else {
                    ToastUtil.showShort("Please select any one image");
                }

            }
        });

        holder.btn_cancel_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invoiceResponse.setEditMode(false);
                invoiceResponse.setImages(null);
                notifyItemChanged(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    public interface SellerInvoiceItemChangeListener {
        void onUpdate(String invoice_id, int position);
    }

    public void setSellerInvoiceItemChangeListener(SellerInvoiceItemChangeListener sellerInvoiceItemChangeListener) {
        this.sellerInvoiceItemChangeListener = sellerInvoiceItemChangeListener;
    }

    public void setColor(String status, TextView textView) {
        if (status.equalsIgnoreCase("Created")) {
            textView.setTextColor(context.getResources().getColor(R.color.red));
        } else if (status.equalsIgnoreCase("Invoice Uploaded")) {
            textView.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            textView.setTextColor(context.getResources().getColor(R.color.purchase_dark_gray));
        }
    }

    public File checkSellerInvoicePdfIsDownloaded(String invoiceID) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + Constants.SELLER_INVOICE_DOWNLOAD_PATH + invoiceID + ".pdf");
            if (file.exists()) {
                return file;
            } else {
                return null;
            }
        }
        return null;
    }

    public void openPDFFile(String invoiceID) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri pdfFileURI = FileProvider.getUriForFile(
                    context,
                    context.getApplicationContext()
                            .getPackageName() + ".provider", checkSellerInvoicePdfIsDownloaded(invoiceID));
            intent.setDataAndType(pdfFileURI, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showShort("No application found to view this file");
        }

    }


    public class SellerInvoiceViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.txt_seller_invoice_number)
        TextView txt_seller_invoice_number;

        @BindView(R.id.txt_invoice_create_date)
        TextView txt_invoice_create_date;

        @BindView(R.id.linear_seller_invoice_status)
        LinearLayout linear_seller_invoice_status;

        @BindView(R.id.txt_seller_invoice_status)
        TextView txt_seller_invoice_status;

        @BindView(R.id.txt_invoice_numbers_values)
        TextView txt_invoice_numbers_values;


        @BindView(R.id.btn_save_images)
        TextView btn_save_images;

        @BindView(R.id.btn_cancel_images)
        TextView btn_cancel_images;

        @BindView(R.id.flexbox_image)
        FlexboxLayout flexbox_image;

        @BindView(R.id.attach_button)
        AppCompatButton attach_button;

        @BindView(R.id.btn_first_additional_image)
        TextView btn_first_additional_image;


        public SellerInvoiceViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}