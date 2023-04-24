package com.wishbook.catalog.home.catalog.add;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayout;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.multipleimageselect.activities.AlbumSelectActivity;
import com.wishbook.catalog.Utils.multipleimageselect.models.Image;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.Photos;
import com.wishbook.catalog.home.models.ProductObj;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_AddAdditionalImages extends DialogFragment {


    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.flexbox_image)
    FlexboxLayout flexbox_image;

    @BindView(R.id.attach_button)
    AppCompatButton attach_button;

    @BindView(R.id.btn_upload)
    AppCompatButton btn_upload_additional_images;

    Dialog additionalImageDialog;

    // ### Variable Upload Dialog ### ///
    MaterialDialog dialogCatalogUpload;
    Runnable runnablePostData;
    long BeforeTime, TotalTxBeforeTest;
    Handler handlerPostData;
    ArrayList<Image> additionalImage;
    ProductObj selectedProductObjAdditionalImage;


    AdditionalImageChangeListener additionalImageChangeListener;


    public Fragment_AddAdditionalImages() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add_additional_images);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
        ButterKnife.bind(this, dialog);
        if (getArguments().getSerializable("product") != null) {
            selectedProductObjAdditionalImage = (ProductObj) getArguments().getSerializable("product");
        }
        initDialogLayout(null, dialog, null, selectedProductObjAdditionalImage);
        return dialog;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TAG", "onActivityResult Additional Images: Request Code" + requestCode + "\n Result Code=>" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Application_Singleton.MULTIIMAGE_PRODUCT_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<Image> temp = data.getParcelableArrayListExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_IMAGES);
            if (temp.size() > 0) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (additionalImage != null && additionalImage.size() > 0) {
                    additionalImage.addAll(temp);
                } else {
                    additionalImage = new ArrayList<>();
                    additionalImage.addAll(temp);
                }
                loadAdditionalImages();
                enableUploadAdditionalImagesButtons();
            }
        }
    }

    public void initDialogLayout(final View dialogview, final Dialog dialog, final ArrayList<Image> images, final ProductObj productObj) {


        loadAdditionalImages();
        disableAdditionalImagesButtons();

        attach_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int limit = Constants.ADDITIONAL_IMAGE_LIMIT ;
                if(additionalImage!=null)
                    limit -= additionalImage.size();
                if(selectedProductObjAdditionalImage.getPhotos()!=null && selectedProductObjAdditionalImage.getPhotos().size() > 1)
                    limit -= (selectedProductObjAdditionalImage.getPhotos().size() - 1);

                if(limit > 0) {
                    Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
                    intent.putExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, limit);
                    Fragment_AddAdditionalImages.this.startActivityForResult(intent, Application_Singleton.MULTIIMAGE_PRODUCT_REQUEST_CODE);
                } else {
                    Toast.makeText(getActivity(),"Only 4 images allowed for same design",Toast.LENGTH_SHORT).show();
                }

            }
        });

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        btn_upload_additional_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (additionalImage != null && additionalImage.size() > 0) {
                    uploadProductPhotos(productObj.getId(), additionalImage);
                } else {
                    Toast.makeText(getActivity(), "Please select any one images to upload", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loadAdditionalImages() {
        flexbox_image.removeAllViews();
        final ArrayList<Image> images = new ArrayList<>();
        // if there is no image in photos than show one product original image is first image
        if(selectedProductObjAdditionalImage.getPhotos().size()  == 0) {
            Image image = new Image();
            image.setPhoto_id(selectedProductObjAdditionalImage.getId());
            image.setPath(selectedProductObjAdditionalImage.getImage().getThumbnail_small());
            image.setName(selectedProductObjAdditionalImage.getId());
                image.setDefaultCatalogImage(true);
            if (images != null)
                images.add(image);
        } else {
            for (Photos photo :
                    selectedProductObjAdditionalImage.getPhotos()) {
                Image image = new Image();
                image.setPhoto_id(photo.getId());
                image.setPath(photo.getImage().getThumbnail_small());
                image.setName(photo.getId());
                if(photo.getSet_default())
                    image.setDefaultCatalogImage(true);
                if (images != null)
                    images.add(image);
            }
        }

        if (additionalImage != null)
            images.addAll(additionalImage);
        if (images != null && images.size() > 0) {
            for (int i = 0; i < images.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.item_image, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(params);
                if (images.get(i).getPhoto_id() != null) {
                    // Load from web
                    if (images.get(i).getPath() != null) {
                        StaticFunctions.loadFresco(getActivity(), images.get(i).getPath(), (SimpleDraweeView) view.findViewById(R.id.img_set));
                    }
                } else {
                    // Load from local
                    if (images.get(i).getPath() != null) {
                        StaticFunctions.loadFresco(getActivity(), "file://" + images.get(i).getPath(), (SimpleDraweeView) view.findViewById(R.id.img_set));
                    }
                }

                flexbox_image.addView(view);
                ImageView img_remove_set = view.findViewById(R.id.img_remove_set);
                if(images.get(i).isDefaultCatalogImage)
                    img_remove_set.setVisibility(View.GONE);
                else
                    img_remove_set.setVisibility(View.VISIBLE);

                final int finalI = i;
                final ArrayList<Image> finalImages = images;
                img_remove_set.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                .title("Delete Images")
                                .content("Are you sure you want to delete this images?")
                                .positiveText("OK")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        dialog.dismiss();
                                        if (finalImages.get(finalI).getPhoto_id() != null) {
                                            // Call Server for Delete product-photos
                                            callDeleteProductsPhotos(finalImages.get(finalI).getPhoto_id(), finalI, finalImages);
                                        } else {
                                            // Local Image Delete
                                            additionalImage.remove(images.get(finalI));
                                            loadAdditionalImages();
                                        }
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

                if(images.size() >= 4) {
                    attach_button.setVisibility(View.GONE);
                } else {
                    attach_button.setVisibility(View.VISIBLE);
                }

            }

        }

        flexbox_image.addView(attach_button);
    }

    private void uploadProductPhotos(String product_id, final ArrayList<Image> images) {
        HashMap params = new HashMap();
        final int[] counter = {0};
        showUploadDialog();
        dialogCatalogUpload.setTitle("Uploading Image : " + counter[0] + " / " + images.size());
        for (int i = 0; i < images.size(); i++) {
            // server will be set default product image not required to set client side
            params.put("set_default", String.valueOf(false));
            params.put("product", product_id);
            //params.put("sort_order", String.valueOf((i + 1)));

            File outputrenamed = new File(images.get(i).path);
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).requestwithFile(HttpManager.METHOD.FILEUPLOAD, URLConstants.companyUrl(getActivity(), "products_photos", product_id), params, headers, "image", "image/jpg", outputrenamed, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if (isAdded() && !isDetached()) {
                        counter[0]++;
                        dialogCatalogUpload.setTitle("Uploading Image : " + counter[0] + " / " + images.size());
                        if (counter[0] >= images.size()) {
                            hideUploadDialog();
                            getDialog().dismiss();
                            Toast.makeText(getContext(), "Images uploaded successfully", Toast.LENGTH_SHORT).show();
                            if (additionalImageChangeListener != null) {
                                additionalImageChangeListener.additionalImageChange();
                            }
                        }
                    }

                }

                @Override
                public void onResponseFailed(final ErrorString error) {
                    if (isAdded() && !isDetached()) {
                        hideUploadDialog();
                        new MaterialDialog.Builder(getActivity())
                                .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                                .content(error.getErrormessage())
                                .positiveText("OK")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        dialog.dismiss();
                                    }

                                })
                                .show();
                    }
                }
            });
        }
    }

    public void showUploadDialog() {
        try {
            //initializing loader
            dialogCatalogUpload = new MaterialDialog.Builder(getContext()).title("Uploading ..").build();
            dialogCatalogUpload.setCancelable(false);
            dialogCatalogUpload.show();
            BeforeTime = System.currentTimeMillis();
            TotalTxBeforeTest = TrafficStats.getTotalTxBytes();
            handlerPostData = new Handler();
            runnablePostData = new Runnable() {
                public void run() {
                    if (handlerPostData != null) {
                        long AfterTime = System.currentTimeMillis();
                        long TotalRxAfterTest = TrafficStats.getTotalTxBytes();
                        double rxDiff = TotalRxAfterTest - TotalTxBeforeTest;
                        if ((rxDiff != 0)) {
                            double rxBPS = (rxDiff / 1024); // total rx bytes per second.
                            dialogCatalogUpload.setContent("Uploading speed : " + (int) rxBPS + " KB/s");
                            TotalTxBeforeTest = TotalRxAfterTest;
                        }

                        handlerPostData.postDelayed(this, 1000);
                    }
                }
            };
            handlerPostData.postDelayed(runnablePostData, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideUploadDialog() {
        if (dialogCatalogUpload != null && dialogCatalogUpload.isShowing()) {
            dialogCatalogUpload.dismiss();
        }
    }

    public void callDeleteProductsPhotos(final String deleteid, final int position, ArrayList<Image> images) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.companyUrl(getActivity(), "products_photos", "") + deleteid + "/";
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.DELETEWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    ArrayList<Photos> temp = new ArrayList<>();
                    temp.addAll(selectedProductObjAdditionalImage.getPhotos());
                    for (Photos photo :
                            temp) {
                        if (photo.getId().equals(deleteid)) {
                            selectedProductObjAdditionalImage.getPhotos().remove(photo);
                        }
                    }
                    temp = null;
                    loadAdditionalImages();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void disableAdditionalImagesButtons() {
        btn_upload_additional_images.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_disable_state));
        btn_upload_additional_images.setEnabled(false);
    }

    private void enableUploadAdditionalImagesButtons() {
        btn_upload_additional_images.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_purchase_blue));
        btn_upload_additional_images.setEnabled(true);
    }

    public interface AdditionalImageChangeListener {
        void additionalImageChange();
    }


    public void setAdditionalImageChangeListener(AdditionalImageChangeListener additionalImageChangeListener) {
        this.additionalImageChangeListener = additionalImageChangeListener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideUploadDialog();
    }
}
