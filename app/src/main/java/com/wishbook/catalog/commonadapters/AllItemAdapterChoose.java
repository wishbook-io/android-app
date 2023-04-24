package com.wishbook.catalog.commonadapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.CartProductModel;
import com.wishbook.catalog.commonmodels.ProductMyDetail;
import com.wishbook.catalog.commonmodels.Response_catalogapp;
import com.wishbook.catalog.commonmodels.SaveCartData;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Photos;
import com.wishbook.catalog.commonmodels.responses.Response_Selection;
import com.wishbook.catalog.commonmodels.responses.Response_ShareStatus;
import com.wishbook.catalog.home.cart.CartHandler;
import com.wishbook.catalog.home.cart.Fragment_MyCart;
import com.wishbook.catalog.home.cart.SelectSizeBottomSheet;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;
import com.wishbook.catalog.home.catalog.details.Activity_ProductsInCatalog;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.models.Response_Brands;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;


public class AllItemAdapterChoose extends RecyclerView.Adapter<AllItemAdapterChoose.ViewHolder> {

    private int height = 800;
    private int width = 480;
    private AppCompatActivity context;
    private List itemsInSection;
    boolean ownerFlag, sellerFlag, isFullCatalog;
    ProductMyDetail productMyDetail;
    String category_id;
    SharedPreferences preferences;
    GridSelectListener gridSelectListener;

    String catalog_type;

    double full_discount;


    public AllItemAdapterChoose(AppCompatActivity context,
                                ArrayList itemsInSection,
                                boolean ownerFlag, boolean sellerFlag,
                                boolean isFullcatalog, String category_id, String catalog_type,double full_discount) {
        this.context = context;
        this.itemsInSection = itemsInSection;
        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            this.width = size.x;
            this.height = size.y;
            this.ownerFlag = ownerFlag;
            this.sellerFlag = sellerFlag;
            this.isFullCatalog = isFullcatalog;
            this.category_id = category_id;
            this.catalog_type = catalog_type;
            this.full_discount = full_discount;
            preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public AllItemAdapterChoose.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_items_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AllItemAdapterChoose.ViewHolder holder, final int position) {
        try {
            final Object object = itemsInSection.get(position);
            holder.All_item_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (object instanceof Response_Brands) {
                        Bundle bundle = new Bundle();
                        bundle.putString("filtertype", "brand");
                        bundle.putString("filtervalue", ((Response_Brands) itemsInSection.get(holder.getAdapterPosition())).getId());
                        Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs();
                        fragmentCatalogs.setArguments(bundle);
                        context.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragmentCatalogs).addToBackStack(null).commit();
                    }
                    if (object instanceof ProductObj) {
                        Application_Singleton.selectedCatalogProducts = (ArrayList<ProductObj>) itemsInSection;
                        Intent intent = new Intent(context, Activity_ProductsInCatalog.class);
                        intent.putExtra("position", String.valueOf(holder.getAdapterPosition()));
                        intent.putExtra("selectedId", ((ProductObj) itemsInSection.get(holder.getAdapterPosition())).getId());
                        intent.putExtra("sellerFlag", sellerFlag);
                        intent.putExtra("ownerFlag", ownerFlag);
                        intent.putExtra("isFullCatalog", isFullCatalog);
                        intent.putExtra("category_id", category_id);
                        intent.putExtra("product_mydetail", productMyDetail);
                        intent.putExtra("full_discount",full_discount);
                        context.startActivity(intent);
                    }
                }
            });
            // }

            if (object instanceof Response_Brands) {
                StaticFunctions.loadFresco(context, ((Response_Brands) itemsInSection.get(position)).getImage().getThumbnail_small(), holder.All_item_img);
                /*ImageLoader.getInstance().displayImage(((Response_Brands) itemsInSection.get(position)).getImage().getThumbnail_small(), holder.thumb_img, Application_Singleton.options, new SimpleImageLoadingListener() {
                });*/

            }
            if (object instanceof Response_catalogapp) {
                StaticFunctions.loadFresco(context, ((Response_catalogapp) itemsInSection.get(position)).getThumbnail().getThumbnail_small(), holder.All_item_img);
               /* ImageLoader.getInstance().displayImage(((Response_catalogapp) itemsInSection.get(position)).getThumbnail().getThumbnail_small(), holder.thumb_img, Application_Singleton.options, new SimpleImageLoadingListener() {
                });*/

            }

            if (object instanceof Response_Selection) {
                StaticFunctions.loadFresco(context, ((Response_Selection) itemsInSection.get(position)).getImage(), holder.All_item_img);
                 /*ImageLoader.getInstance().displayImage(((Response_Selection) itemsInSection.get(position)).getImage(), holder.thumb_img, Application_Singleton.options, new SimpleImageLoadingListener() {
                });*/

            }
            if (object instanceof Response_ShareStatus) {
                StaticFunctions.loadFresco(context, ((Response_ShareStatus) itemsInSection.get(position)).getImage(), holder.All_item_img);
                /*ImageLoader.getInstance().displayImage(((Response_ShareStatus)itemsInSection.get(position)).getImage(),holder.thumb_img, Application_Singleton.options,new SimpleImageLoadingListener(){
                });*/
            }
            if (object instanceof ProductObj) {
                if (!((ProductObj) object).isEnabled()) {
                    holder.relative_disable.setVisibility(View.VISIBLE);
                } else {
                    holder.relative_disable.setVisibility(View.GONE);
                }
                StaticFunctions.loadFresco(context, ((ProductObj) itemsInSection.get(position)).getImage().getThumbnail_small(), holder.All_item_img);
                /*ImageLoader.getInstance().displayImage(((ProductObj)itemsInSection.get(position)).getImage().getThumbnail_small(),holder.thumb_img, Application_Singleton.options,new SimpleImageLoadingListener(){
                });*/

                if (!isFullCatalog) {

                    /**
                     * new changes done according to new mail by Jay patel (April-30)
                     */
                    /**
                     * changes done Apr - 2
                     * show add to cart button even single product available
                     */
                    // if(itemsInSection.size() > 1) {
                    holder.txt_single_pc_price.setVisibility(View.VISIBLE);
                    holder.txt_single_pc_price.setText("Single Pc: " + "\u20B9 " + ((ProductObj) object).getSingle_piece_price());

                   if(productMyDetail.getProducts().get(position).isCurrently_selling()) {
                       if(!productMyDetail.getProducts().get(position).isI_am_selling_sell_full_catalog()) {
                           holder.txt_single_pc_price.setVisibility(View.VISIBLE);
                           holder.txt_single_pc_price.setText("Single Pc: " + "\u20B9 " + productMyDetail.getProducts().get(position).getSingle_piece_price());
                       } else {
                           holder.txt_single_pc_price.setVisibility(View.GONE);
                       }
                   } else {
                       if(productMyDetail.getProducts().get(position).isI_am_selling_this()) {
                           // if seller selling full catalog than not show price
                           holder.txt_single_pc_price.setVisibility(View.GONE);
                       }
                   }


                    holder.btn_add_to_cart.setVisibility(View.GONE);
                    holder.txt_not_availabel_single_pc.setVisibility(View.GONE);
                    if (!productMyDetail.getProducts().get(position).isIs_owner() &&
                            !isFullCatalog &&
                            !productMyDetail.getProducts().get(position).isI_am_selling_this() &&
                            ((ProductObj) object).getSingle_piece_price() != null &&
                            !((ProductObj) object).getSingle_piece_price().equals("null")) {
                        if(((ProductObj) object).isSelling()) {
                            holder.btn_add_to_cart.setVisibility(View.VISIBLE);
                        } else {
                            holder.txt_not_availabel_single_pc.setVisibility(View.VISIBLE);
                        }

                    }
                    updateAddToCartLabel(holder.btn_add_to_cart, position);
                    holder.btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            add_to_cart(holder.btn_add_to_cart, (ProductObj) object, position);
                        }
                    });

                } else {
                    holder.txt_single_pc_price.setVisibility(View.GONE);
                    holder.btn_add_to_cart.setVisibility(View.GONE);
                }

                if (((ProductObj) object).getAvailable_size_string() != null && !((ProductObj) object).getAvailable_size_string().isEmpty()) {
                    holder.txt_available_sizes.setVisibility(View.VISIBLE);
                    holder.txt_available_sizes.setText("Sizes: " + ((ProductObj) object).getAvailable_size_string());
                } else {
                    holder.txt_available_sizes.setVisibility(View.GONE);
                }
            }


            if(catalog_type!=null && !catalog_type.equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN) && ownerFlag && ((ProductObj) object).isEnabled()) {
                holder.btn_add_images.setText("ADD IMAGES");
                if(((ProductObj)object).getPhotos()!=null && ((ProductObj)object).getPhotos().size() > 1) {
                    holder.txt_additional_image_count.setVisibility(View.VISIBLE);
                    holder.txt_additional_image_count.setText(""+((ProductObj)object).getPhotos().size() +" images");

                    holder.btn_add_images.setText("ADD MORE");
                } else {
                    holder.txt_additional_image_count.setVisibility(View.GONE);
                }
                holder.btn_add_images.setVisibility(View.VISIBLE);
                holder.btn_add_images.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(gridSelectListener!=null) {
                            gridSelectListener.addImage(position,((ProductObj) itemsInSection.get(position)));
                        }
                    }
                });
            } else {
                holder.btn_add_images.setVisibility(View.GONE);
            }


            if (object instanceof Photos) {
                StaticFunctions.loadFresco(context, ((Photos) itemsInSection.get(position)).getImage().getThumbnail_small(), holder.All_item_img);
            }

            // Temporary function
            hideOrderDisableConfig(holder.btn_add_to_cart,holder.btn_add_images);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void updateAddToCartLabel(AppCompatButton btn_add_to_cart, int position) {
        if (!preferences.getString("cartdata", "").equalsIgnoreCase("")) {
            Type cartType = new TypeToken<ArrayList<SaveCartData>>() {
            }.getType();
            ArrayList<SaveCartData> saveCartData = new Gson().fromJson(preferences.getString("cartdata", ""), cartType);
            if (saveCartData != null && saveCartData.size() > 0) {
                for (int i = 0; i < saveCartData.size(); i++) {
                    if (saveCartData.get(i).getProducts().getId().contains(((ProductObj) itemsInSection.get(position)).getId())) {
                        btn_add_to_cart.setText("GO TO CART");
                        break;
                    } else {
                        btn_add_to_cart.setText("ADD TO CART");
                    }
                }
            }
        }
    }

    public void add_to_cart(final AppCompatButton btn_add_to_cart, final ProductObj productObj, final int position) {
        if (btn_add_to_cart.getText().toString().contains("GO")) {
            SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
            int cart_count = preferences.getInt("cartcount", 0);
            Application_Singleton.CONTAINER_TITLE = "My Cart (" + cart_count + " items)";
            Application_Singleton.CONTAINERFRAG = new Fragment_MyCart();
            StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
        } else {
            if (UserInfo.getInstance(context).isGuest()) {
                StaticFunctions.ShowRegisterDialog(context, "Product Add To Cart");
                return;
            }

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                btn_add_to_cart.setText("GO TO CART");
            } else {
                btn_add_to_cart.animate().rotationX(180).setDuration(600).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        btn_add_to_cart.setText("GO TO CART");
                        btn_add_to_cart.setRotationX(0);
                        Animation shake = AnimationUtils.loadAnimation(context, R.anim.swing);
                        //cart.startAnimation(shake);
                    }
                });
            }

            try {
                Application_Singleton.trackEvent("Add to cart", "Click", "From Product");
                final String[] note = {"Nan"};
                final CartHandler cartHandler = new CartHandler(((AppCompatActivity) context));
                cartHandler.new GetSize(context, Application_Singleton.getResponse_catalog().getCategory(), new CartHandler.SizeGetCallbackListener() {
                    @Override
                    public void onSuccess(boolean isSizeMandatory) {
                        if (isSizeMandatory && productObj.getAvailable_size_string() == null) {
                            new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                    .content("Product out of stock")
                                    .positiveText("OK")
                                    .cancelable(false)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            dialog.dismiss();
                                            btn_add_to_cart.clearAnimation();
                                            btn_add_to_cart.setRotationX(0);
                                            btn_add_to_cart.setText("ADD TO CART");
                                        }
                                    })
                                    .show();
                            btn_add_to_cart.clearAnimation();
                            btn_add_to_cart.setRotationX(0);
                            btn_add_to_cart.setText("ADD TO CART");
                            return;
                        }
                        if (Application_Singleton.getResponse_catalog() != null && productObj.getAvailable_size_string() != null && !productObj.getAvailable_size_string().isEmpty()) {
                            if (productObj.getAvailable_size_string() != null && !productObj.getAvailable_size_string().isEmpty() && productObj.getAvailable_size_string().split(",").length > 1) {
                                SelectSizeBottomSheet bottomSheetDialog;
                                if (Application_Singleton.selectedCatalogProducts != null && productObj.getSingle_piece_price() != null && !productObj.getSingle_piece_price().equals("null")) {
                                    bottomSheetDialog = SelectSizeBottomSheet.getInstance(productObj.getAvailable_size_string(), Application_Singleton.getResponse_catalog().getSingle_piece_price_range(), "1", "product");
                                } else {
                                    bottomSheetDialog = SelectSizeBottomSheet.getInstance(productObj.getAvailable_size_string(), Application_Singleton.getResponse_catalog().getPrice_range(), "1", "product");
                                }
                                bottomSheetDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "Custom Bottom Sheet");
                                bottomSheetDialog.setDismissListener(new SelectSizeBottomSheet.DismissListener() {
                                    @Override
                                    public void onDismiss(HashMap<String, Integer> value) {
                                        if (value.size() > 0) {
                                            if (!preferences.getString("cartId", "").equalsIgnoreCase("")) {
                                                handleAddToCartWithSize(value, position, btn_add_to_cart);
                                            } else {
                                                getCartId(value, position, btn_add_to_cart);
                                            }
                                        } else {
                                            btn_add_to_cart.animate().rotationX(0).setDuration(0).setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    btn_add_to_cart.setText("ADD TO CART");
                                                    btn_add_to_cart.setRotationX(0);
                                                    Animation shake = AnimationUtils.loadAnimation(context, R.anim.swing);
                                                    //cart.startAnimation(shake);
                                                }
                                            });

                                        }
                                    }
                                });
                                return;
                            } else {
                                note[0] = "Size : " + productObj.getAvailable_size_string();
                            }
                        }


                        int qtys[] = new int[1];
                        qtys[0] = 1;

                        cartHandler.setAddToCartCallbackListener(new CartHandler.AddToCartCallbackListener() {
                            @Override
                            public void onSuccess(CartProductModel response) {
                                btn_add_to_cart.setText("GO TO CART");
                            }

                            @Override
                            public void onFailure() {
                                btn_add_to_cart.setText("ADD TO CART");
                            }
                        });
                        cartHandler.addProductToCart(Application_Singleton.getResponse_catalog().getId(),
                                productObj.getId(),
                                productObj,
                                qtys, false,
                                "", null,
                                "gallery", (AppCompatActivity) context,
                                note[0], Application_Singleton.selectedCatalogProducts.size());
                    }

                    @Override
                    public void onFailure() {
                        btn_add_to_cart.setText("ADD TO CART");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Temporary function for Order Disable
     */
    public void hideOrderDisableConfig(AppCompatButton btn_add_to_cart, AppCompatButton btn_add_images) {
        // add catalog button hide from catalog level
        if (StaticFunctions.checkOrderDisableConfig(context)) {
            btn_add_to_cart.setVisibility(View.GONE);
            btn_add_images.setVisibility(View.GONE);
        }
    }

    public void handleAddToCartWithSize(HashMap<String, Integer> sizeQtyMap, int selectedProductPostion, final AppCompatButton btn_add_to_cart) {
        List<String> l = new ArrayList<>(sizeQtyMap.keySet());
        CartHandler cartHandler = new CartHandler(((AppCompatActivity) context));
        for (int j = 0; j < sizeQtyMap.size(); j++) {
            int qtys[] = new int[1];
            qtys[0] = sizeQtyMap.get(l.get(j));
            cartHandler.setAddToCartCallbackListener(new CartHandler.AddToCartCallbackListener() {
                @Override
                public void onSuccess(CartProductModel response) {
                    btn_add_to_cart.setText("GO TO CART");
                }

                @Override
                public void onFailure() {
                    btn_add_to_cart.setText("ADD TO CART");
                }
            });
            cartHandler.addProductToCart(Application_Singleton.getResponse_catalog().getId(), ((ProductObj) itemsInSection.get(selectedProductPostion)).getId(),
                    ((ProductObj) itemsInSection.get(selectedProductPostion)),
                    qtys,
                    false, "", null, "productkurti", (AppCompatActivity) context,
                    "Size : " + l.get(j), Application_Singleton.selectedCatalogProducts.size());
        }
    }

    public void getCartId(final HashMap<String, Integer> value, final int selectedProductPostion, final AppCompatButton btn_add_to_cart) {
        try {
            final HttpManager.METHOD method = HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS;
            String url = URLConstants.companyUrl(context, "cart", "");
            List<CartProductModel.Items> items = new ArrayList<>();
            CartProductModel cartProductModel = new CartProductModel(items);
            cartProductModel.setOrder_number("");
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            HttpManager.getInstance((Activity) context).requestwithObject(method, url, new Gson().fromJson(new Gson().toJson(cartProductModel), JsonObject.class), headers, false, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    CartProductModel response_model = new Gson().fromJson(response, CartProductModel.class);
                    CartProductModel CartProductModel = Application_Singleton.gson.fromJson(response, CartProductModel.class);
                    SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit().putString("cartId", "" + CartProductModel.getId());
                    editor.commit();
                    handleAddToCartWithSize(value, selectedProductPostion, btn_add_to_cart);
                }

                @Override
                public void onResponseFailed(ErrorString error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void coverImageClick() {
        Application_Singleton.selectedCatalogProducts = (ArrayList<ProductObj>) itemsInSection;
        Intent intent = new Intent(context, Activity_ProductsInCatalog.class);
        intent.putExtra("position", String.valueOf(0));
        intent.putExtra("selectedId", ((ProductObj) itemsInSection.get(0)).getId());
        intent.putExtra("sellerFlag", sellerFlag);
        intent.putExtra("ownerFlag", ownerFlag);
        intent.putExtra("isFullCatalog", isFullCatalog);
        intent.putExtra("category_id", category_id);
        intent.putExtra("product_mydetail", productMyDetail);
        context.startActivity(intent);

    }

    public void setProductsMyDetails(ProductMyDetail productsMyDeatils) {
        this.productMyDetail = productsMyDeatils;
    }

    public interface GridSelectListener  {

        void addImage(int position,ProductObj productObj);
    }



    public void setGridSelectListener(GridSelectListener gridSelectListener) {
        this.gridSelectListener = gridSelectListener;
    }

    @Override
    public int getItemCount() {
        return itemsInSection.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout itemcontainer;
        private final SimpleDraweeView All_item_img;
        private RelativeLayout relative_disable;
        private TextView txt_single_pc_price, txt_available_sizes, txt_additional_image_count , txt_not_availabel_single_pc;
        private AppCompatButton btn_add_to_cart, btn_add_images;


        public ViewHolder(View itemView) {
            super(itemView);
            itemcontainer = (FrameLayout) itemView.findViewById(R.id.itemcontainer);
            All_item_img = (SimpleDraweeView) itemView.findViewById(R.id.item_img);
            relative_disable = itemView.findViewById(R.id.relative_disable);
            txt_single_pc_price = itemView.findViewById(R.id.txt_single_pc_price);
            txt_available_sizes = itemView.findViewById(R.id.txt_available_sizes);
            txt_additional_image_count = itemView.findViewById(R.id.txt_additional_image_count);
            btn_add_to_cart = itemView.findViewById(R.id.btn_add_to_cart);
            btn_add_images = itemView.findViewById(R.id.btn_add_images);
            txt_not_availabel_single_pc = itemView.findViewById(R.id.txt_not_availabel_single_pc);


            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Math.round(width / 3.1f), Math.round(height / 4.2f));
            itemcontainer.setLayoutParams(lp);
        }
    }
}