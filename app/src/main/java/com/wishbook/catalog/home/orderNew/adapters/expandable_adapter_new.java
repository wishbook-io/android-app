package com.wishbook.catalog.home.orderNew.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.postpatchmodels.Invoice;
import com.wishbook.catalog.commonmodels.responses.Response_Product;
import com.wishbook.catalog.home.catalog.details.ProductReviewBottomSheet;
import com.wishbook.catalog.home.orderNew.details.Activity_OrderDetailsNew;
import com.wishbook.catalog.home.orders.expandable_recyclerview.CatalogListItem;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;


public class expandable_adapter_new extends ExpandableRecyclerAdapter<expandable_adapter_new.MyParentViewHolder, expandable_adapter_new.MyChildViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;
    private boolean isPurchaseOrder;
    private boolean isCancellationRecyclcerView;
    private ArrayList<Invoice> invoices;
    private String order_status;

    ChangeOrderItemsListener changeOrderItemsListener;

    String TAG= "EXPAND";

    public expandable_adapter_new(Context context, List<CatalogListItem> itemList, boolean isPurchaseOrder,
                                  ArrayList<Invoice> invoices, boolean isCancellationRecyclcerView, String order_status) {
        super(itemList);
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.isPurchaseOrder = isPurchaseOrder;
        this.invoices = invoices;
        this.isCancellationRecyclcerView = isCancellationRecyclcerView;
        this.order_status = order_status;
    }

    @Override
    public MyParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.purchase_order_header, viewGroup, false);
        return new MyParentViewHolder(view);
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.purchase_order_item, viewGroup, false);
        return new MyChildViewHolder(view);
    }


    @Override
    public void onBindParentViewHolder(final MyParentViewHolder parentViewHolder, final int position, final ParentListItem parentListItem) {
        final CatalogListItem subcategoryParentListItem = (CatalogListItem) parentListItem;
        String catlogName = "";
        if(subcategoryParentListItem.getChildItemList().size() > 0) {
            if (subcategoryParentListItem.getChildItemList().get(0).getProduct_type() != null
                    && subcategoryParentListItem.getChildItemList().get(0).getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                if(!isPurchaseOrder) {
                    parentViewHolder.txt_seller_note.setVisibility(View.VISIBLE);
                    parentViewHolder.txt_seller_note.setText(mContext.getResources().getString(R.string.seller_note_for_set));
                }
                catlogName = "" + (position + 1) + " . " + subcategoryParentListItem.getCatalog().toString() + " (" + subcategoryParentListItem.getChildItemList().get(0).getNo_of_pcs() + " Designs)";
                int totalPcs = Integer.parseInt(subcategoryParentListItem.getChildItemList().get(0).getNo_of_pcs())
                        *Integer.parseInt(subcategoryParentListItem.getChildItemList().get(0).getQuantity());
                parentViewHolder.txt_total_products.setText(String.valueOf(totalPcs) + " Pcs.");
            } else {
                catlogName = "" + (position + 1) + " . " + subcategoryParentListItem.getCatalog().toString() + " (" + subcategoryParentListItem.getChildItemList().size() + " Designs)";
                parentViewHolder.txt_total_products.setText(String.valueOf((Integer.parseInt(subcategoryParentListItem.getChildItemList().get(0).getQuantity()) * subcategoryParentListItem.getChildItemList().size())) + " Pcs.");
            }
        }




        parentViewHolder.txt_catalog_name.setText(catlogName);

        if (subcategoryParentListItem.getChildItemList().size() > 0 && subcategoryParentListItem.getChildItemList().get(0).getNote() != null) {
            parentViewHolder.txt_catalog_note.setVisibility(View.VISIBLE);
            String temp = "<font color='#e02b2b' >" + subcategoryParentListItem.getChildItemList().get(0).getNote() + "</font>";
            parentViewHolder.txt_catalog_note.setText(Html.fromHtml(temp), TextView.BufferType.SPANNABLE);
        } else {
            parentViewHolder.txt_catalog_note.setVisibility(View.GONE);
        }

        if(isPurchaseOrder) {
            if(!isCancellationRecyclcerView) {
                if(order_status.contains("Dispatched") || order_status.equalsIgnoreCase("Delivered")) {
                    if(subcategoryParentListItem.getChildItemList()!=null && subcategoryParentListItem.getChildItemList().size() > 0) {
                        boolean isEditMode = false;
                        Response_Product.Item_ratings item_ratings = null;
                        if(subcategoryParentListItem.getChildItemList()!=null && subcategoryParentListItem.getChildItemList().get(0).getItem_ratings()!=null  &&  subcategoryParentListItem.getChildItemList().get(0).getItem_ratings().size() > 0){
                            isEditMode = true;
                            item_ratings =  subcategoryParentListItem.getChildItemList().get(0).getItem_ratings().get(0);
                        }
                        if(isEditMode) {
                            parentViewHolder.txt_rate_review.setText("EDIT RATING");
                        } else {
                            parentViewHolder.txt_rate_review.setText("RATE & REVIEW");
                        }
                        parentViewHolder.txt_rate_review.setVisibility(View.VISIBLE);
                        final boolean finalIsEditMode = isEditMode;
                        final Response_Product.Item_ratings finalItem_ratings = item_ratings;
                        parentViewHolder.txt_rate_review.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(subcategoryParentListItem.getChildItemList()!=null && subcategoryParentListItem.getChildItemList().size() > 0) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("order_items",subcategoryParentListItem.getChildItemList().get(0).getId());
                                    bundle.putString("product_id",subcategoryParentListItem.getChildItemList().get(0).getProduct());
                                    bundle.putBoolean("isEditMode", finalIsEditMode);
                                    if(finalIsEditMode) {
                                        bundle.putSerializable("item_rating", finalItem_ratings);
                                    }
                                    ProductReviewBottomSheet productReviewBottomSheet = ProductReviewBottomSheet.newInstance(bundle);
                                    productReviewBottomSheet.setRatingChangeListener(new ProductReviewBottomSheet.RatingChangeListener() {
                                        @Override
                                        public void onChange() {
                                            if(mContext instanceof Activity_OrderDetailsNew) {
                                                ((Activity_OrderDetailsNew)mContext).onRefresh();
                                            }
                                        }
                                    });
                                    productReviewBottomSheet.show(((AppCompatActivity)mContext).getSupportFragmentManager(), productReviewBottomSheet.getTag());
                                }
                            }
                        });
                    }
                }

            } else {
                parentViewHolder.header_card.setClickable(false);
                parentViewHolder.txt_rate_review.setVisibility(View.GONE);
                parentViewHolder.mArrowExpandImageView.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_forward_black_24dp));
                // Cancellation part start
                if(subcategoryParentListItem.getChildItemList()!=null && subcategoryParentListItem.getChildItemList().size() > 0 && subcategoryParentListItem.getChildItemList().get(0).isShow_cancellation_option()) {
                    parentViewHolder.relative_cancellation.setVisibility(View.VISIBLE);
                    if(subcategoryParentListItem.getChildItemList().get(0).getCancellation_option_reason()!=null && !subcategoryParentListItem.getChildItemList().get(0).getCancellation_option_reason().isEmpty())
                        parentViewHolder.txt_expected_date_order_item.setText(subcategoryParentListItem.getChildItemList().get(0).getCancellation_option_reason().replaceAll("_"," "));
                    else
                        parentViewHolder.txt_expected_date_order_item.setText("");

                    parentViewHolder.txt_wait_order_item.setPaintFlags(parentViewHolder.txt_wait_order_item.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    parentViewHolder.txt_cancel_order_item.setPaintFlags(parentViewHolder.txt_cancel_order_item.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    parentViewHolder.txt_wait_order_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new MaterialDialog.Builder(mContext)
                                    .content("Great! You have chosen to wait for your order")
                                    .positiveText("OK")
                                    .negativeText("CANCEL")
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            dialog.dismiss();

                                        }
                                    })
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            dialog.dismiss();
                                            postOrderItemWaitCancellation(subcategoryParentListItem.getChildItemList().get(0).getId(),Constants.WAIT_ORDER_ITEM_CANCELLATION);
                                        }
                                    })
                                    .show();

                        }
                    });

                    parentViewHolder.txt_cancel_order_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new MaterialDialog.Builder(mContext)
                                    .content("Are you sure you want to cancel oder item?")
                                    .positiveText("Yes")
                                    .negativeText("NO")
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            dialog.dismiss();

                                        }
                                    })
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            dialog.dismiss();
                                            postOrderItemWaitCancellation(subcategoryParentListItem.getChildItemList().get(0).getId(),Constants.CANCEL_ORDER_ITEM_CANCELLATION);
                                        }
                                    })
                                    .show();
                        }
                    });
                } else {
                    parentViewHolder.relative_cancellation.setVisibility(View.GONE);
                }
            }
        } else {
            parentViewHolder.relative_cancellation.setVisibility(View.GONE);
            parentViewHolder.txt_rate_review.setVisibility(View.GONE);



        }
        parentViewHolder.onExpansionToggled(true);
    }


    @Override
    public void onBindChildViewHolder(MyChildViewHolder childViewHolder, final int position, Object childListItem) {
        final Response_Product subcategoryChildListItem = (Response_Product) childListItem;
        childViewHolder.minusbut.setVisibility(View.GONE);
        childViewHolder.plusbut.setVisibility(View.GONE);
        childViewHolder.delete.setVisibility(View.GONE);
        if (subcategoryChildListItem.getProduct_title() != null && !subcategoryChildListItem.getProduct_title().equals("null") && !subcategoryChildListItem.getProduct_title().equals("")) {
            childViewHolder.prod_name.setVisibility(View.VISIBLE);
            childViewHolder.prod_name.setText(StringUtils.capitalize(subcategoryChildListItem.getProduct_title()));
        } else {
            childViewHolder.prod_name.setVisibility(View.GONE);
        }
        childViewHolder.prod_price.setText("\u20B9" + subcategoryChildListItem.getRate());
        childViewHolder.qntytext.setEnabled(false);

        if (subcategoryChildListItem.getProduct_type() != null
                && !subcategoryChildListItem.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            childViewHolder.qntytext.setText(subcategoryChildListItem.getQuantity());
        } else {
            childViewHolder.qntytext.setText(String.valueOf(Integer.parseInt(subcategoryChildListItem.getQuantity())));
        }

       // childViewHolder.qntytext.setText(subcategoryChildListItem.getQuantity());

        String image = subcategoryChildListItem.getProduct_image();
        if (image != null & !image.equals("")) {
            //StaticFunctions.loadImage(mContext, image, childViewHolder.prod_img, R.drawable.uploadempty);
            StaticFunctions.loadFresco(mContext, image, childViewHolder.prod_img);
        }

    }


    public void postOrderItemWaitCancellation(String orderItemId,String action) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("show_cancellation_option", "false");
        params.put("order_item",orderItemId);
        params.put("last_action",action);
        HttpManager.getInstance((Activity) mContext).methodPost(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.companyUrl(mContext, "buyer-order-item-crm", ""), params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if(changeOrderItemsListener!=null) {
                    changeOrderItemsListener.onChange();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    public interface ChangeOrderItemsListener {
        void  onChange();
    }

    public void setChangeOrderItemsListener(ChangeOrderItemsListener changeOrderItemsListener) {
        this.changeOrderItemsListener = changeOrderItemsListener;
    }

    public int getTotalCatalogItem(CatalogListItem listItem) {
        int totalItem = 0;
        try {
            for (Response_Product product : listItem.getFeedItemList()) {
                totalItem += Integer.parseInt(product.getQuantity());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalItem;
    }

    public void showImage(String imagepath) {
        Dialog mSplashDialog = new Dialog(mContext, R.style.AppTheme_Dark_Dialog);
        mSplashDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSplashDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSplashDialog.setContentView(R.layout.imagedialog);
        //ImageView image = (ImageView) mSplashDialog.findViewById(R.id.myimg);
        SimpleDraweeView image = (SimpleDraweeView) mSplashDialog.findViewById(R.id.myimg);
        if (imagepath != null & !imagepath.equals("")) {
            //StaticFunctions.loadImage(mContext, imagepath, image, R.drawable.uploadempty);
            StaticFunctions.loadFresco(mContext, imagepath, image);
        }
        mSplashDialog.setCancelable(true);
        mSplashDialog.show();
    }

    public class MyParentViewHolder extends ParentViewHolder {

        public TextView txt_catalog_name;
        public ImageView mArrowExpandImageView;
        public TextView txt_total_products;
        public TextView txt_catalog_note,txt_rate_review;
        public CardView header_card;
        RelativeLayout list_item_parent_horizontal_arrow_imageView;
        private RelativeLayout relative_cancellation;
        private TextView txt_expected_date_order_item, txt_cancel_order_item,txt_wait_order_item, txt_seller_note;

        public MyParentViewHolder(View itemView) {
            super(itemView);
            mArrowExpandImageView = (ImageView) itemView.findViewById(R.id.arrow_img);
            txt_catalog_name = (TextView) itemView.findViewById(R.id.txt_catalog_name);
            txt_total_products = (TextView) itemView.findViewById(R.id.txt_total_products);
            txt_catalog_note = itemView.findViewById(R.id.txt_catalog_note);
            txt_rate_review = itemView.findViewById(R.id.txt_rate_review);
            header_card = itemView.findViewById(R.id.header_card);

            relative_cancellation = itemView.findViewById(R.id.relative_cancellation);
            txt_expected_date_order_item = itemView.findViewById(R.id.txt_expected_date_order_item);
            txt_cancel_order_item = itemView.findViewById(R.id.txt_cancel_order_item);
            txt_wait_order_item = itemView.findViewById(R.id.txt_wait_order_item);
            txt_seller_note = itemView.findViewById(R.id.txt_seller_note);

            list_item_parent_horizontal_arrow_imageView = itemView.findViewById(R.id.list_item_parent_horizontal_arrow_imageView);
        }

        @SuppressLint("NewApi")
        @Override
        public void setExpanded(boolean expanded) {
            super.setExpanded(expanded);
            if (expanded) {
                mArrowExpandImageView.setRotation(180);
            } else {
                mArrowExpandImageView.setRotation(0);
            }
        }
    }


    public class MyChildViewHolder extends ChildViewHolder {

        private SimpleDraweeView prod_img;
        private TextView prod_name;
        private TextView prod_seller;
        private TextView prod_price;
        private EditText qntytext;
        private TextView minusbut;
        private TextView plusbut;
        private ImageView delete;


        public MyChildViewHolder(View itemView) {
            super(itemView);
            prod_img = (SimpleDraweeView) itemView.findViewById(R.id.prod_img);
            prod_name = (TextView) itemView.findViewById(R.id.prod_name);
            prod_seller = (TextView) itemView.findViewById(R.id.prod_seller);
            prod_price = (TextView) itemView.findViewById(R.id.prod_price);
            qntytext = (EditText) itemView.findViewById(R.id.edit_qty);
            minusbut = (TextView) itemView.findViewById(R.id.btn_minus);
            plusbut = (TextView) itemView.findViewById(R.id.btn_plus);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}