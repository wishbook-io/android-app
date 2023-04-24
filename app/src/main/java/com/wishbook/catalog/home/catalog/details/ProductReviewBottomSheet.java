package com.wishbook.catalog.home.catalog.details;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.multipleimageselect.activities.AlbumSelectActivity;
import com.wishbook.catalog.Utils.multipleimageselect.models.Image;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.RequestRating;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Response_Product;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductReviewBottomSheet extends BottomSheetDialogFragment {


    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

    @BindView(R.id.edit_review)
    EditText edit_review;

    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.btn_add_photos)
    AppCompatButton btn_add_photos;

    @BindView(R.id.flexbox_image)
    FlexboxLayout flexbox_image;

    @BindView(R.id.txt_rate_order_item_name)
    TextView txt_rate_order_item_name;


    @BindView(R.id.btn_submit_review)
    AppCompatButton btn_submit_review;

    @BindView(R.id.btn_delete_review)
    AppCompatButton btn_delete_review;

    String order_items;
    String product_id;
    Response_Product.Item_ratings item_ratings;
    boolean isEditMode;
    ArrayList<Image> additionalImage;
    RatingChangeListener ratingChangeListener;

    // ### Variable Upload Dialog ### ///
    MaterialDialog dialogCatalogUpload;
    Runnable runnablePostData;
    long BeforeTime, TotalTxBeforeTest;
    Handler handlerPostData;

    public static ProductReviewBottomSheet newInstance(Bundle bundle) {
        ProductReviewBottomSheet f = new ProductReviewBottomSheet();
        if (bundle != null) {
            f.setArguments(bundle);
        }
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.rate_review_bottomsheet, container, false);
        ButterKnife.bind(this, v);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_review.clearFocus();
                hideKeyBoard();
                dismiss();

            }
        });
        btn_delete_review.setVisibility(View.GONE);
        edit_review.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                openFullScreen();
                return false;
            }
        });

        if (getArguments() != null) {
            // order item
            // product id can be bundle id and single product id
            order_items = getArguments().getString("order_items");
            product_id = getArguments().getString("product_id");
            isEditMode = getArguments().getBoolean("isEditMode");
            //String order_item_name = getArguments().getString("order_item_name");
           /* if(order_item_name!=null) {
                txt_rate_order_item_name.setText("Rate Order " + order_item_name);
            }*/
            if (getArguments().getSerializable("item_rating") != null) {
                item_ratings = (Response_Product.Item_ratings) getArguments().getSerializable("item_rating");
                btn_add_photos.setVisibility(View.GONE);
                filldata(item_ratings);
            }
        }


        btn_submit_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (order_items != null && product_id != null && ratingBar.getRating() > 0) {
                    RequestRating requestRating = new RequestRating();
                    requestRating.setOrder_item(order_items);
                    requestRating.setProduct(product_id);
                    requestRating.setRating(String.valueOf(ratingBar.getRating()));
                    if (!edit_review.getText().toString().trim().isEmpty())
                        requestRating.setReview(edit_review.getText().toString());
                    requestRating.setUser(UserInfo.getInstance(getActivity()).getUserId());

                    if (isEditMode) {
                        patchRating(getActivity(), item_ratings.getId(), requestRating);
                    } else {
                        postRating(getActivity(), requestRating);
                    }


                } else {
                    if (edit_review.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "Please enter review", Toast.LENGTH_SHORT).show();
                    }

                    if (ratingBar.getRating() == 0) {
                        Toast.makeText(getActivity(), "Please give rating", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btn_delete_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRating(getActivity(), item_ratings.getId());
            }
        });

        btn_add_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int limit = Constants.UPLOAD_REVIEW_IMAGE_LIMIT;
                if (additionalImage != null)
                    limit -= additionalImage.size();
                if (limit > 0) {
                    Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
                    intent.putExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, limit);
                    ProductReviewBottomSheet.this.startActivityForResult(intent, Application_Singleton.MULTIIMAGE_PRODUCT_REQUEST_CODE);
                } else {
                    Toast.makeText(getActivity(), "You can upload max 5 photos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    public void filldata(Response_Product.Item_ratings item_ratings) {
        if (item_ratings != null) {
            ratingBar.setRating(Float.parseFloat(item_ratings.getRating()));
            edit_review.setText(item_ratings.getReview());
            btn_delete_review.setVisibility(View.VISIBLE);
        }
    }

    public void openFullScreen() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        final View view = getView();
        view.post(new Runnable() {
            @Override
            public void run() {
                View parent = (View) view.getParent();
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
                CoordinatorLayout.Behavior behavior = params.getBehavior();
                BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
                WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                final Point size = new Point();
                display.getSize(size);
                bottomSheetBehavior.setPeekHeight((int) (size.y));
                bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        //Log.e(TAG, "onState: " + newState);
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                        //Log.e(TAG, "onSlide: " + slideOffset);
                    }
                });
                parent.setBackgroundColor(Color.TRANSPARENT);
            }
        });
    }


    private void postRating(final Context context, RequestRating requestRating) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        Log.d("TAG", "postRating: " + Application_Singleton.gson.toJson(requestRating));
        HttpManager.getInstance((Activity) context).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(context, "product-ratings", ""), Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(requestRating), JsonObject.class), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    if (context != null) {
                        if (ratingChangeListener != null) {
                            ratingChangeListener.onChange();
                        }
                        Type type = new TypeToken<RequestRating>() {
                        }.getType();
                        RequestRating requestRating1 = Application_Singleton.gson.fromJson(response, type);
                        if (additionalImage != null && additionalImage.size() > 0) {
                            uploadReviewPhotos(requestRating1.getId(), additionalImage);
                        } else {
                            Toast.makeText(getActivity(), "Successfully posted review", Toast.LENGTH_SHORT).show();
                            hideKeyBoard();
                            dismiss();
                        }
                    }
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TAG", "onActivityResult ProductReviewBottom Images: Request Code" + requestCode + "\n Result Code=>" + resultCode);
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
            }
        }
    }

    private void deleteRating(final Context context, String rating_id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        String url = URLConstants.companyUrl(context, "product-ratings", "") + rating_id + "/";
        final MaterialDialog progressDialog = StaticFunctions.showProgress(getActivity());
        progressDialog.show();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.DELETEWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    try {
                        if (isAdded() && !isDetached()) {
                            if (ratingChangeListener != null) {
                                ratingChangeListener.onChange();
                            }
                            Toast.makeText(getActivity(), "Review deleted Successfully", Toast.LENGTH_SHORT).show();
                            dismiss();
                            hideKeyBoard();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void patchRating(final Context context, String rating_id, RequestRating requestRating) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        String url = URLConstants.companyUrl(context, "product-ratings", "") + rating_id + "/";
        final MaterialDialog progressDialog = StaticFunctions.showProgress(getActivity());
        progressDialog.show();
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, url, Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(requestRating), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                try {
                    if (isAdded() && !isDetached()) {
                        if (ratingChangeListener != null) {
                            ratingChangeListener.onChange();
                        }
                        Toast.makeText(getActivity(), "Review changed successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void uploadReviewPhotos(String review_id, final ArrayList<Image> images) {
        HashMap params = new HashMap();
        final int[] counter = {0};
        showUploadDialog();
        dialogCatalogUpload.setTitle("Uploading Image : " + counter[0] + " / " + images.size());
        for (int i = 0; i < images.size(); i++) {
            params.put("ratingandreview", review_id);
            File outputrenamed = new File(images.get(i).path);
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).requestwithFile(HttpManager.METHOD.FILEUPLOAD, URLConstants.companyUrl(getActivity(), "review-images", review_id), params, headers, "image", "image/jpg", outputrenamed, false, new HttpManager.customCallBack() {
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

    public void loadAdditionalImages() {
        flexbox_image.removeAllViews();
        final ArrayList<Image> images = new ArrayList<>();
        // if there is no image in photos than show one product original image is first image
      /*  if(selectedProductObjAdditionalImage.getPhotos().size()  == 0) {
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
        }*/

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
                if (images.get(i).isDefaultCatalogImage)
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

                if (images.size() >= 5) {
                    disablePayButtons();
                } else {
                    enablePayButtons();
                }

            }

        }
    }

    public interface RatingChangeListener {
        void onChange();
    }

    public void setRatingChangeListener(RatingChangeListener ratingChangeListener) {
        this.ratingChangeListener = ratingChangeListener;
    }

    public void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            // software keyboard was shown
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        } else {
            // software keyboard was not shown
        }
    }

    private void disablePayButtons() {
        btn_add_photos.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_edge_grey_rectengle));
        btn_add_photos.setEnabled(false);
        btn_add_photos.setTextColor(ContextCompat.getColor(getActivity(),R.color.purchase_medium_gray));
    }

    private void enablePayButtons() {
        btn_add_photos.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_edge_border_blue));
        btn_add_photos.setEnabled(true);
        btn_add_photos.setTextColor(ContextCompat.getColor(getActivity(),R.color.color_primary));
    }

}
