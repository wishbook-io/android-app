package com.wishbook.catalog.home.sellerhub.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.wishbook.catalog.Utils.imagecropper.PhotoTaker;
import com.wishbook.catalog.Utils.imagecropper.PhotoTakerUtils;
import com.wishbook.catalog.Utils.multipleimageselect.activities.AlbumSelectActivity;
import com.wishbook.catalog.commonmodels.responses.ResponseManifestList;
import com.wishbook.catalog.commonmodels.responses.SellerInvoice_images;
import com.wishbook.catalog.home.orders.Fragment_Manifest_List;
import com.wishbook.catalog.home.sellerhub.SellerManifestAPIHandler;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManifestListAdapter extends RecyclerView.Adapter<ManifestListAdapter.ManifestViewHolder> {


    ArrayList<ResponseManifestList> itemsArrayList;
    Context context;
    Fragment fragment;

    public static int EDIT_POSITION = 0;
    public static int ADD_MORE_IMAGE_POSITION = 0;
    int max_lines_to_show = 6;

    ManifestListAdapter.SellerManifestItemChangeListener sellerManifestItemChangeListener;


    public ManifestListAdapter(Context context, ArrayList<ResponseManifestList> itemsArrayList, Fragment fragment) {
        this.itemsArrayList = itemsArrayList;
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public ManifestListAdapter.ManifestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manifest_list_item, parent, false);
        return new ManifestListAdapter.ManifestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManifestListAdapter.ManifestViewHolder holder, int position) {
        ResponseManifestList responseManifestList = itemsArrayList.get(position);
        holder.flexbox_image.removeAllViews();
        if (responseManifestList.getId() != null) {
            holder.txt_manifest_id.setText("Manifest ID #" + responseManifestList.getId());
        }
        if (responseManifestList.getCreated() != null) {
            holder.txt_manifest_create_date.setText(DateUtils.getFormattedDate(StringUtils.capitalize(itemsArrayList.get(position).getCreated().toLowerCase().trim())));
        }
        if (responseManifestList.getShipping_service_name() != null) {
            holder.txt_shipping_service_value.setText(responseManifestList.getShipping_service_name());
        }

        if (responseManifestList.getItems() != null && responseManifestList.getItems().size() > 0) {
            ArrayList<String>awbnumbers  = new ArrayList<>();
            for (int i = 0; i < responseManifestList.getItems().size(); i++) {
                if (responseManifestList.getItems().get(i).getAwb() != null) {
                    awbnumbers.add(responseManifestList.getItems().get(i).getAwb() );
                }
            }
            if(awbnumbers!=null && awbnumbers.size() > 0) {

                if(awbnumbers.size() > max_lines_to_show) {
                    if(responseManifestList.isExpanded()) {
                        holder.txt_awb_numbers_values.setMaxLines(200);
                    } else {
                        holder.txt_awb_numbers_values.setMaxLines(max_lines_to_show);
                    }
                    holder.txt_see_more.setVisibility(View.VISIBLE);
                    holder.txt_see_more.setText("+ " + (awbnumbers.size() - max_lines_to_show) +" more" );
                    holder.txt_see_more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(responseManifestList.isExpanded()) {
                                responseManifestList.setExpanded(false);
                                holder.txt_awb_numbers_values.setMaxLines(200);
                                holder.txt_see_more.setText("See less");
                            } else {
                                responseManifestList.setExpanded(true);
                                holder.txt_awb_numbers_values.setMaxLines(max_lines_to_show);
                                holder.txt_see_more.setText("+ " + (awbnumbers.size() - max_lines_to_show) +" more" );
                            }
                        }
                    });
                } else {
                    holder.txt_see_more.setVisibility(View.GONE);
                }
                holder.txt_awb_numbers_values.setText(StaticFunctions.ArrayListToString(awbnumbers, StaticFunctions.COMMASEPRATEDNEWLINE));
            }
        }

        if (responseManifestList.getStatus() != null) {
            holder.txt_manifest_status.setText(StringUtils.capitalize(StringUtils.capitalize(responseManifestList.getStatus().toLowerCase().trim())));
            setColor(responseManifestList.getStatus(), holder.txt_manifest_status);
        }


        if (responseManifestList.getImages() != null && responseManifestList.getImages().size() > 0) {
            for (int i = 0; i < responseManifestList.getImages().size(); i++) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.item_image, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(params);
                if (responseManifestList.getImages().get(i).getPath() != null) {
                    StaticFunctions.loadFresco(context, "file://" + responseManifestList.getImages().get(i).getPath(), (SimpleDraweeView) view.findViewById(R.id.img_set));
                } else if (responseManifestList.getImages().get(i).getImage() != null) {
                    if (responseManifestList.getImages().get(i).getImage().contains(".pdf")) {
                        ((SimpleDraweeView) view.findViewById(R.id.img_set)).setActualImageResource(R.drawable.ic_pdf_file);
                    } else {
                        StaticFunctions.loadFresco(context, responseManifestList.getImages().get(i).getImage(), (SimpleDraweeView) view.findViewById(R.id.img_set));
                    }
                }
                ImageView img_remove_set = view.findViewById(R.id.img_remove_set);
                if (responseManifestList.isEditMode()) {
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
                        new MaterialDialog.Builder(context)
                                .title("Delete Image")
                                .content("Are you sure you want to delete this manifest image?")
                                .positiveText("OK")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        dialog.dismiss();
                                        ArrayList<SellerInvoice_images> temp = responseManifestList.getImages();
                                        temp.remove(finalI);
                                        responseManifestList.setImages(temp);
                                        if(temp.size() == 0 && responseManifestList.isEditMode()){
                                            responseManifestList.setEditMode(false);
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
                holder.flexbox_image.addView(view);
            }
            if (responseManifestList.isEditMode()) {
                holder.flexbox_image.addView(holder.attach_button);
            }

        } else {
            if (responseManifestList.getStatus().equalsIgnoreCase("Created")) {
                holder.flexbox_image.addView(holder.btn_first_additional_image);
            }
        }


        if (responseManifestList.isEditMode()) {
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
                int limit = Constants.UPLOAD_SELLER_INVOICE_LIMIT;
                if (responseManifestList.getImages() != null) {
                    limit = Constants.UPLOAD_SELLER_INVOICE_LIMIT - responseManifestList.getImages().size();
                }
                if (limit == 0) {
                    Toast.makeText(context, "You can upload " + Constants.UPLOAD_SELLER_INVOICE_LIMIT + " images", Toast.LENGTH_SHORT).show();
                } else {
                    if(fragment instanceof Fragment_Manifest_List)
                        ((Fragment_Manifest_List) fragment).openTaker(context,limit);
                }
            }
        });

        holder.attach_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ADD_MORE_IMAGE_POSITION = holder.getAdapterPosition();
                int limit = Constants.UPLOAD_SELLER_INVOICE_LIMIT;
                if (responseManifestList.getImages() != null) {
                    limit = Constants.UPLOAD_SELLER_INVOICE_LIMIT - responseManifestList.getImages().size();
                }
                if (limit == 0) {
                    Toast.makeText(context, "You can upload " + Constants.UPLOAD_SELLER_INVOICE_LIMIT + " images", Toast.LENGTH_SHORT).show();
                } else {
                    if(fragment instanceof Fragment_Manifest_List)
                        ((Fragment_Manifest_List) fragment).openTaker(context,limit);
                }
            }
        });


        holder.btn_save_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(responseManifestList.getImages()!=null && responseManifestList.getImages().size() > 0) {
                    SellerManifestAPIHandler apiHandler = new SellerManifestAPIHandler((Activity) context);
                    apiHandler.callManifestImageUpload(responseManifestList);
                    apiHandler.setSellerInvoiceAPIListener(new SellerManifestAPIHandler.SellerManifestAPIListener() {
                        @Override
                        public void onSuccessRequest() {
                            responseManifestList.setEditMode(false);
                            notifyItemChanged(position);
                            ToastUtil.showShort("Manifest Uploaded successfully");
                            if (sellerManifestItemChangeListener != null) {
                                sellerManifestItemChangeListener.onUpdate(responseManifestList.getId(), position);
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
                responseManifestList.setEditMode(false);
                responseManifestList.setImages(null);
                notifyItemChanged(position);
            }
        });


    }


    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }


    public interface SellerManifestItemChangeListener {
        void onUpdate(String manifest_id, int position);
    }

    public void setSellerManifestItemChangeListener(SellerManifestItemChangeListener sellerManifestItemChangeListener) {
        this.sellerManifestItemChangeListener = sellerManifestItemChangeListener;
    }

    public void setColor(String status, TextView textView) {
        if (status.equalsIgnoreCase("Created")) {
            textView.setTextColor(context.getResources().getColor(R.color.red));
        } else if (status.equalsIgnoreCase("Closed")) {
            textView.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            textView.setTextColor(context.getResources().getColor(R.color.purchase_dark_gray));
        }
    }



    public class ManifestViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.txt_manifest_id)
        TextView txt_manifest_id;

        @BindView(R.id.txt_manifest_create_date)
        TextView txt_manifest_create_date;

        @BindView(R.id.linear_manifest_status)
        LinearLayout linear_manifest_status;

        @BindView(R.id.txt_manifest_status)
        TextView txt_manifest_status;

        @BindView(R.id.txt_shipping_service_value)
        TextView txt_shipping_service_value;

        @BindView(R.id.linear_awb_numbers)
        LinearLayout linear_awb_numbers;

        @BindView(R.id.txt_awb_numbers_values)
        TextView txt_awb_numbers_values;


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

        @BindView(R.id.txt_see_more)
        TextView txt_see_more;


        public ManifestViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            txt_see_more.setPaintFlags(txt_see_more.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
    }

}