package com.wishbook.catalog.home.cart;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.util.Log;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.CartProductModel;
import com.wishbook.catalog.commonmodels.DeleteCartItem;
import com.wishbook.catalog.commonmodels.SaveCartData;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.ResponseCategoryEvp;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.home.Fragment_Home2;
import com.wishbook.catalog.home.Fragment_Summary;
import com.wishbook.catalog.home.catalog.details.Activity_ProductsInCatalog;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;
import com.wishbook.catalog.home.models.ProductObj;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.Fragment;

public class CartHandler {
    public static int cartCount;
    @NonNull
    public AppCompatActivity context;
    private SharedPreferences preferences;
    private static String TAG = "CartHandler";
    AddToCartCallbackListener addToCartCallbackListener;
    SizeGetCallbackListener sizeGetCallbackListener;


    public CartHandler(@NonNull AppCompatActivity context) {
        try {
            this.context = context;
            preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addCatalogToCart(@NonNull final String catalog, final @NonNull String sellerId, final Fragment fragment, final String type, final AppCompatActivity activity, final Button btn_cart, final String note) {


        try {
            if (!preferences.getString("cartId", "").equalsIgnoreCase("")) {
                Log.d("CARTID", "" + preferences.getString("cartId", ""));
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
                HttpManager.getInstance(context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context, "catalogs", "") + catalog + "/?expand=true", null, headers, true, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        Log.v("cached response", response);
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        try {
                            Log.v("sync response", response);
                            Gson gson = new Gson();
                            final Response_catalog responseCatalog = gson.fromJson(response, Response_catalog.class);

                            new GetSize(context, responseCatalog.getCategory(), new SizeGetCallbackListener() {
                                @Override
                                public void onSuccess(boolean isSizeMandatory) {
                                    if(isSizeMandatory && responseCatalog.getAvailable_sizes() == null) {
                                        new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                                .content("Product out of stock")
                                                .positiveText("OK")
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .show();
                                        if(addToCartCallbackListener!=null) {
                                            addToCartCallbackListener.onFailure();
                                        }
                                        return;
                                    }

                                    if(responseCatalog.getProduct_type()!=null &&
                                            !responseCatalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN) &&
                                            responseCatalog.getProduct()!=null &&
                                            responseCatalog.getProduct().length == 1 &&
                                            responseCatalog.getFull_catalog_orders_only().equals("false")) {
                                        handleAddToCartForCatalogProduct1(catalog,sellerId,fragment,type,activity,btn_cart,responseCatalog);
                                        return;
                                    }


                                    if (responseCatalog.getAvailable_sizes() != null) {
                                        if (!type.contains("multipleseller") && responseCatalog.getAvailable_sizes() != null && !responseCatalog.getAvailable_sizes().isEmpty() && responseCatalog.getAvailable_sizes().split(",").length > 1 && responseCatalog.getProduct_type() != null && !responseCatalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                                            SelectSizeBottomSheet bottomSheetDialog = SelectSizeBottomSheet.getInstance(responseCatalog.getAvailable_sizes(), responseCatalog.getPrice_range(), responseCatalog.getTotal_products(), "catalog");
                                            bottomSheetDialog.show((context).getSupportFragmentManager(), "Custom Bottom Sheet");
                                            bottomSheetDialog.setDismissListener(new SelectSizeBottomSheet.DismissListener() {
                                                @Override
                                                public void onDismiss(HashMap<String, Integer> value) {
                                                    if (value.size() > 0) {
                                                        List<String> l = new ArrayList<>(value.keySet());
                                                        List<CartProductModel.Items> data = new ArrayList<>();
                                                        for (int j = 0; j < value.size(); j++) {
                                                            //adding size products to cart
                                                            // No need to check product type set because exclude that
                                                            final ProductObj[] productObjs = responseCatalog.getProduct();
                                                            if (value.containsKey(l.get(j))) {
                                                                String note = "Size : " + l.get(j);
                                                                int quantity = value.get(l.get(j));
                                                                boolean isFullCatalogOrderOnly = false;
                                                                data.addAll(addItemsToCartList(catalog,responseCatalog.getPublic_price(), productObjs, sellerId, quantity, note, true ));
                                                            }
                                                            sendAddToCartAnalyticsData(type, responseCatalog, true, null);
                                                        }
                                                        if(data!=null && data.size() > 0) {
                                                            CartProductModel cartProductModel = new CartProductModel(data);
                                                            cartProductModel.setAdd_quantity(false);
                                                            cartProductModel.setAdd_size(true);
                                                            Log.d(TAG, "Bottom Sheet Post Data====> " + new Gson().toJson(data));
                                                            postItemsToCart(cartProductModel, preferences.getString("cartId", "null").equals("null"), fragment, type, activity);
                                                        }
                                                    } else {
                                                        if (btn_cart != null) {
                                                            btn_cart.setText("ADD TO CART");
                                                        }
                                                        SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                                                        Log.d("CARTDATA", preferences.getString("cartdata", ""));
                                                        if (!preferences.getString("cartdata", "").equalsIgnoreCase("")) {
                                                            Type cartType = new TypeToken<ArrayList<SaveCartData>>() {
                                                            }.getType();
                                                            ArrayList<SaveCartData> saveCartData = new Gson().fromJson(preferences.getString("cartdata", ""), cartType);
                                                            if (saveCartData != null && saveCartData.size() != 0) {
                                                                for (int i = 0; i < saveCartData.size(); i++) {
                                                                    if (saveCartData.get(i).getId().equals(catalog)) {
                                                                        if (saveCartData.get(i).getProducts().getId() == null || saveCartData.get(i).getProducts().getId().size() == 0) {
                                                                            saveCartData.remove(i);
                                                                            Log.d("EXIST", "TRUE");
                                                                        }
                                                                    }

                                                                }

                                                            }

                                                            preferences.edit().putString("cartdata", new Gson().toJson(saveCartData)).commit();
                                                            Log.d("CARTDATA", "" + new Gson().toJson(saveCartData));
                                                        }
                                                        if (fragment != null && type.contains("gallery")) {
                                                            ((Fragment_CatalogsGallery) fragment).initCall(false);
                                                        }
                                                    }

                                                }
                                            });
                                            return;
                                        }
                                    }


                                    ProductObj[] productObjs = null;
                                    if (responseCatalog.getProduct_type() != null
                                            && responseCatalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {

                                        Log.e(TAG, "ADD ITEMS TO LIST:  SCRREN ");
                                        productObjs = new ProductObj[1];
                                        Log.e(TAG, "addProductToCart: ID " + responseCatalog.getId());
                                        ProductObj productObj = new ProductObj(responseCatalog.getId(), responseCatalog.getPrice_range());
                                        productObj.setNo_of_pcs(responseCatalog.getNo_of_pcs());
                                        productObj.setProduct_type(responseCatalog.getProduct_type());
                                        productObjs[0] = productObj;
                                    } else {
                                        //adding products to cart
                                        Log.e(TAG, "ADD ITEMS TO LIST: WITHOUT SCREEN");
                                        productObjs = responseCatalog.getProduct();
                                    }
                                    Log.e(TAG, "ADD ITEMS TO LIST: ");

                                    /**
                                     * WB-3492 Pass Size when size is also one
                                     */
                                    String temp_note = note;
                                    if (responseCatalog.getProduct_type() != null
                                            && !responseCatalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN) && responseCatalog.getAvailable_sizes() != null && !responseCatalog.getAvailable_sizes().isEmpty() && responseCatalog.getAvailable_sizes().split(",").length == 1) {
                                        temp_note = "Size : " + responseCatalog.getAvailable_sizes();
                                    }
                                    boolean isFullCatalogOrderOnly = false;
                                    if (responseCatalog.getFull_catalog_orders_only() != null && responseCatalog.getFull_catalog_orders_only().equals("true")) {
                                        isFullCatalogOrderOnly = true;
                                    }
                                    List<CartProductModel.Items> data = addItemsToCartList(catalog, responseCatalog.getPublic_price(),productObjs, sellerId, 1, temp_note, true);
                                    CartProductModel cartProductModel = new CartProductModel(data);
                                    if (type.contains("multipleseller")) {
                                        cartProductModel.setAdd_quantity(false);
                                    } else {
                                        cartProductModel.setAdd_quantity(true);
                                    }
                                    if (responseCatalog.getProduct_type() != null
                                            && !responseCatalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN) && responseCatalog.getAvailable_sizes() != null && !responseCatalog.getAvailable_sizes().isEmpty() && responseCatalog.getAvailable_sizes().split(",").length == 1) {
                                        cartProductModel.setAdd_size(true);
                                    }
                                    Log.d(TAG, "Post Data====> " + new Gson().toJson(data));
                                    sendAddToCartAnalyticsData(type, responseCatalog, true, null);
                                    postItemsToCart(cartProductModel, preferences.getString("cartId", "null").equals("null"), fragment, type, activity);
                                }

                                @Override
                                public void onFailure() {

                                }
                            });


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {

                    }
                });
            } else {
                getCartId(catalog, sellerId, fragment, type, activity, btn_cart);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<CartProductModel.Items> addItemsToCartList(String catalog,String catalogPublicPrice,
                                                           ProductObj[] productObjs,
                                                           String sellerId, int quantity,
                                                           String note,
                                                           boolean isFullCatalog) {





        //catalog ---- bundle product id

        List<CartProductModel.Items> data = new ArrayList<>();
        boolean isSamePrice = true;
        /*ProductObj anyProduct = null;
        for (ProductObj product : productObjs) {
            if (anyProduct == null) {
                anyProduct = product;
            }
            if (!anyProduct.getPublic_price().isEmpty() && !product.getPublic_price().isEmpty() && !anyProduct.getPublic_price().equals(product.getPublic_price())) {
                isSamePrice = false;
                break;
            }
        }


            // it's for if there is only one product in catalog and it's available single pc.
            //than treat single pc. added
           *//* if (!isFullCatalogOrderOnly) {

                productObjs[0].setPublic_price(productObjs[0].getSingle_piece_price());
                isFullCatalog = false;
            }*//*

        if (productObjs.length > 1 && isSamePrice) {
            Log.e(TAG, "addItemsToCartList: Same Price");
            CartProductModel.Items items = new CartProductModel.Items();
            items.setProduct(catalog);
            items.setQuantity(productObjs.length + "");
            items.setRate(productObjs[0].getPublic_price());
            items.setIs_full_catalog(true);
            if (!note.equalsIgnoreCase("Nan")) {
                items.setNote(note);
            }
            if (sellerId!=null && sellerId.length() > 0) {
                items.setSelling_company(sellerId);
            }
            data.add(items);
        } else {
            Log.e(TAG, "addItemsToCartList: Different Price");
            for (ProductObj product : productObjs) {
                CartProductModel.Items items = new CartProductModel.Items();
                items.setProduct(product.getId());

                try {
                    items.setQuantity(String.valueOf(quantity));
                } catch (ArrayIndexOutOfBoundsException e) {
                    items.setQuantity("1");
                }

                items.setRate(product.getPublic_price());
                items.setIs_full_catalog(isFullCatalog);
                if (!note.equalsIgnoreCase("Nan")) {
                    items.setNote(note);
                }

                if (product.getProduct_type() != null
                        && !product.getProduct_type().isEmpty()
                        && product.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {

                    //set matching case
                    int updatedQuanity = product.getNo_of_pcs() * quantity;
                    items.setQuantity(String.valueOf(updatedQuanity));
                    items.setIs_full_catalog(false);
                }
                if (sellerId!=null && sellerId.length() > 0) {
                    items.setSelling_company(sellerId);
                }
                data.add(items);
            }
        }*/


        /***
         * for send bundle id in case of price range
         * Changes done by Bhavik Gandhi - April-19
         */

        if(isFullCatalog) {

            CartProductModel.Items items = new CartProductModel.Items();

            items.setProduct(catalog);
            items.setRate(catalogPublicPrice);


            if (productObjs!=null && productObjs.length > 0 && productObjs[0].getProduct_type() != null
                    && !productObjs[0].getProduct_type().isEmpty()
                    && productObjs[0].getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {

                //set matching case
                int updatedQuanity = productObjs[0].getNo_of_pcs() * quantity;
                items.setQuantity(String.valueOf(updatedQuanity));
                items.setIs_full_catalog(false);
            } else {
                if(quantity > 1) {
                    items.setQuantity(String.valueOf((productObjs.length*quantity)));
                } else {
                    items.setQuantity(productObjs.length + "");
                }

                items.setIs_full_catalog(true);
            }

            if (!note.equalsIgnoreCase("Nan")) {
                items.setNote(note);
            }

            data.add(items);
        } else {
            for (ProductObj product : productObjs) {
                CartProductModel.Items items = new CartProductModel.Items();
                items.setProduct(product.getId());

                try {
                    items.setQuantity(String.valueOf(quantity));
                } catch (ArrayIndexOutOfBoundsException e) {
                    items.setQuantity("1");
                }

                items.setRate(product.getPublic_price());
                items.setIs_full_catalog(isFullCatalog);
                if (!note.equalsIgnoreCase("Nan")) {
                    items.setNote(note);
                }

                if (product.getProduct_type() != null
                        && !product.getProduct_type().isEmpty()
                        && product.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {

                    //set matching case
                    int updatedQuanity = product.getNo_of_pcs() * quantity;
                    items.setQuantity(String.valueOf(updatedQuanity));
                    items.setIs_full_catalog(false);
                }
                if (sellerId!=null && sellerId.length() > 0) {
                    items.setSelling_company(sellerId);
                }
                data.add(items);
            }
        }


        savePreference(catalog, null);
        return data;

    }

    public void addProductToCart(String catalogId, @NonNull String product, @NonNull ProductObj productObj, @NonNull int quantity[], boolean fromProductDetail, @NonNull String sellerId, final Fragment fragment, final String type, AppCompatActivity activity, String note, int total_selling_products) {
        try {
            List<CartProductModel.Items> data = new ArrayList<>();
            CartProductModel.Items items = new CartProductModel.Items();

            ProductObj[] productObjs = new ProductObj[1];
            Log.e("TAG", "addProductToCart: ID " + product);

            ProductObj productObjTemp = new ProductObj(product, productObj.getSingle_piece_price());

            if (productObj.getSingle_piece_price() == null && productObj.getSingle_piece_price().equals("null")) {
                productObjTemp = new ProductObj(product, productObj.getPublic_price());
            }


            boolean force_full_catalog = false;
            /*if (total_selling_products == 1) {
                force_full_catalog = true;

                //force full price for only 1 selling products
                productObjTemp = new ProductObj(product, productObj.getPublic_price());
            }*/

            productObjs[0] = productObjTemp;

            data.addAll(addItemsToCartList(catalogId,productObj.getPublic_price(), productObjs, sellerId, quantity[0], note, force_full_catalog));

            CartProductModel cartProductModel = new CartProductModel(data);
            if (type.contains("productkurti")) {
                cartProductModel.setAdd_quantity(false);
                cartProductModel.setAdd_size(true);
            }
            if (type.contains("multipleseller")) {
                cartProductModel.setAdd_quantity(false);
                cartProductModel.setAdd_size(false);
            } else {
                cartProductModel.setAdd_quantity(true);
            }
            savePreference(catalogId, product);
            postItemsToCart(cartProductModel, preferences.getString("cartId", "null").equals("null"), fragment, type, activity);

            // ========Send Analytics Data  First Fetch Data============= //
            if (catalogId != null) {
                getCatalogData(catalogId, product);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void postItemsToCart(final CartProductModel cartProductModel, boolean cart_status, final Fragment fragment, final String type, final AppCompatActivity activity) {
        try {

            final HttpManager.METHOD method;
            String url;
            if (cart_status) {
                method = HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS;
                url = URLConstants.companyUrl(context, "cart", "");
            } else {

                if (preferences.getString("cartId", "").equalsIgnoreCase("")) {
                    method = HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS;
                    url = URLConstants.companyUrl(context, "cart", "");
                } else {
                    method = HttpManager.METHOD.PATCHWITHPROGRESS;
                    url = URLConstants.companyUrl(context, "cart", "") + preferences.getString("cartId", "") + "/";
                    Log.d("URL", url);
                }
            }
            String json = new Gson().fromJson(new Gson().toJson(cartProductModel), JsonObject.class).toString();
            Log.d("JSON", json);

            HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
            HttpManager.getInstance(context).requestwithObject(method, url, new Gson().fromJson(new Gson().toJson(cartProductModel), JsonObject.class), headers, false, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    try {
                        CartProductModel response_model = new Gson().fromJson(response, CartProductModel.class);
                        if (method == HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS) {
                            CartProductModel CartProductModel = Application_Singleton.gson.fromJson(response, CartProductModel.class);
                            SharedPreferences.Editor editor = preferences.edit().putString("cartId", "" + CartProductModel.getId());
                            editor.commit();
                        }
                        try {
                            SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                            preferences.edit().putInt("cartcount", Integer.parseInt(response_model.getTotal_cart_items())).commit();


                            if (type != null && (type.contains("summary") || type.contains("recent"))) {
                                ((Fragment_Summary) fragment).updateBadge(Integer.parseInt(response_model.getTotal_cart_items()));
                            }

                            if (type != null && (type.contains("home2") || type.contains("recent"))) {
                                ((Fragment_Home2) fragment).updateBadge(Integer.parseInt(response_model.getTotal_cart_items()));
                            }

                            if (activity != null && type.contains("gallery")) {
                                ((OpenContainer) activity).updateBadge(Integer.parseInt(response_model.getTotal_cart_items()));
                            }
                            if (fragment != null && type.contains("gallery")) {
                                ((Fragment_CatalogsGallery) fragment).initCall(false);
                            }


                            if(activity instanceof OpenContainer) {
                                ((OpenContainer) activity).updateBadge(Integer.parseInt(response_model.getTotal_cart_items()));
                            }

                            if (activity != null && (type.contains("product") || type.contains("productkurti"))) {
                                ((Activity_ProductsInCatalog) activity).updateBadge(Integer.parseInt(response_model.getTotal_cart_items()));
                            }

                            if (activity != null && type.contains("multipleseller")) {
                                activity.setResult(Activity.RESULT_OK);
                                activity.finish();
                            }

                            if(addToCartCallbackListener!=null) {
                                addToCartCallbackListener.onSuccess(response_model);
                            }


                            Fragment_Summary fragment_summary = new Fragment_Summary();
                            fragment_summary.getCartData(context);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        if(addToCartCallbackListener!=null) {
                            addToCartCallbackListener.onFailure();
                        }
                    }

                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    if(addToCartCallbackListener!=null) {
                        addToCartCallbackListener.onFailure();
                    }

                    Fragment_Summary fragment_summary = new Fragment_Summary();
                    fragment_summary.getCartData(context);

                    new MaterialDialog.Builder(context)
                            .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                            .content(error.getErrormessage())
                            .positiveText("OK")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    try {
                                        dialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface AddToCartCallbackListener {
        void onSuccess(CartProductModel response) ;
        void onFailure();
    }


    public void setAddToCartCallbackListener(AddToCartCallbackListener addToCartCallbackListener) {
        this.addToCartCallbackListener = addToCartCallbackListener;
    }

    void deleteCartItem(@NonNull DeleteCartItem deleteCartItem) {
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
            String url = URLConstants.companyUrl(context, "cart", "") + preferences.getString("cartId", "") + "/delete-items/";
            HttpManager.getInstance(context).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, url, new Gson().fromJson(new Gson().toJson(deleteCartItem), JsonObject.class), headers, false, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    Log.d("Deleted from Cart!", response);
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCartId(@NonNull final String catalog, final @NonNull String sellerId, final Fragment fragment, final String type, final AppCompatActivity activity, final Button btn_cart) {
        try {
            final HttpManager.METHOD method = HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS;
            String url = URLConstants.companyUrl(context, "cart", "");
            List<CartProductModel.Items> items = new ArrayList<>();
            CartProductModel cartProductModel = new CartProductModel(items);
            cartProductModel.setOrder_number("");
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
            HttpManager.getInstance(context).requestwithObject(method, url, new Gson().fromJson(new Gson().toJson(cartProductModel), JsonObject.class), headers, false, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    Log.d("Add-cart RESPONSE", response);
                    CartProductModel response_model = new Gson().fromJson(response, CartProductModel.class);
                    CartProductModel CartProductModel = Application_Singleton.gson.fromJson(response, CartProductModel.class);
                    preferences.edit().putString("cartId", "" + CartProductModel.getId()).commit();
                    Log.d("CARTIDPOST", "" + preferences.getString("cartId", ""));
                    addCatalogToCart(catalog, sellerId, fragment, type, activity, btn_cart, "Nan");
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void savePreference(String catalogId, String productID) {
        SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
        if (catalogId != null) {
            if (!preferences.getString("cartdata", "").equalsIgnoreCase("")) {
                Type cartType = new TypeToken<ArrayList<SaveCartData>>() {
                }.getType();
                ArrayList<SaveCartData> saveCartData = new Gson().fromJson(preferences.getString("cartdata", ""), cartType);
                boolean alreadyPresentCatalog = false;
                int pointer = 0;
                for (int i = 0; i < saveCartData.size(); i++) {
                    if (saveCartData.get(i).getId().equalsIgnoreCase(catalogId)) {
                        alreadyPresentCatalog = true;
                        pointer = i;
                        break;
                    }
                }
                SaveCartData saveCartData1 = new SaveCartData();
                saveCartData1.setId(catalogId);
                saveCartData1.setProducts(saveCartData1.getProducts());
                if (productID != null)
                    saveCartData1.getProducts().getId().add(productID);
                if (alreadyPresentCatalog) {
                    saveCartData.add(pointer, saveCartData1);
                } else {
                    saveCartData.add(saveCartData1);
                }
                preferences.edit().putString("cartdata", new Gson().toJson(saveCartData)).commit();
            }
        }
    }

    public void savePreference1(String productID) {
        SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
        if (productID != null) {
            ArrayList<String> saveCartData = new ArrayList<>();
            if (!preferences.getString("cartdataProducts", "").equalsIgnoreCase("")) {
                Type cartType = new TypeToken<ArrayList<String>>() {
                }.getType();
                saveCartData = new Gson().fromJson(preferences.getString("cartdataProducts", ""), cartType);
                if(!saveCartData.contains(productID)) {
                    saveCartData.add(productID);
                }
            } else {
                saveCartData.add(productID);
            }

            preferences.edit().putString("cartdataProducts", new Gson().toJson(saveCartData)).commit();
        }
    }


    public void sendAddToCartAnalyticsData(String source, Response_catalog response_catalog, boolean isFullCatalogAddCart, String product_id) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.ECOMMERCE_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Product_AddToCart");
        HashMap<String, String> prop = new HashMap<>();
        if (source != null) {
            if (source.equalsIgnoreCase("gallery"))
                prop.put("source", "Product Detail Page");
            else if (source.equalsIgnoreCase("summary"))
                prop.put("source", "Home Wishlist");
            else if (source.equalsIgnoreCase("recent"))
                prop.put("source", "Home Recent");
            else if (source.equalsIgnoreCase("contextbased"))
                prop.put("source", "Enquiry Chat Page");
            else
                prop.put("source", "");
        }
        prop.put("is_full_catalog_cart", String.valueOf(isFullCatalogAddCart));
        if (response_catalog.getProduct() != null) {
            for (int i = 0; i < response_catalog.getProduct().length; i++) {
                if (product_id != null && product_id.equalsIgnoreCase(response_catalog.getProduct()[i].getId())) {
                    prop.put("cart_image", response_catalog.getProduct()[i].getImage().getThumbnail_small());
                    prop.put("single_pc_price", response_catalog.getProduct()[i].getPublic_price());
                }
            }
        }
        prop.putAll(getProductAttributes(response_catalog));
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(context, wishbookEvent);
    }

    public HashMap<String, String> getProductAttributes(Response_catalog response_catalog) {
        HashMap<String, String> prop = new HashMap<>();
        if (response_catalog != null) {
            prop.put("product_type", response_catalog.getProduct_type());
            prop.put("full_catalog", response_catalog.full_catalog_orders_only);
            if (response_catalog.getBrand() != null)
                prop.put("brand", response_catalog.getBrand().getName());
            else
                prop.put("brand", "No Brand");
            prop.put("num_items", String.valueOf(response_catalog.getNo_of_pcs()));
            prop.put("product_id", response_catalog.getId());
            prop.put("product_name", response_catalog.getTitle());
            if (response_catalog.getImage() != null && response_catalog.getImage().getThumbnail_small() != null)
                prop.put("product_cover_image", response_catalog.getImage().getThumbnail_small());
            if (response_catalog.getCategory_name() != null) {
                prop.put("category", response_catalog.getCategory_name());
            }
            if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN))
                prop.put("set_type", response_catalog.getCatalog_multi_set_type());
            if (response_catalog.full_catalog_orders_only.equals("true")) {
                prop.put("full_set_price", response_catalog.getPrice_range());
            } else {
                prop.put("full_set_price", response_catalog.getPrice_range());
                if (response_catalog.getSingle_piece_price_range() != null)
                    prop.put("single_pc_price", response_catalog.getSingle_piece_price_range());
            }


        }
        return prop;
    }

    public void getCatalogData(String catalog_id, final String product) {
        String url = URLConstants.companyUrl(context, "catalogs_expand_true_id", catalog_id);
        final HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
        HttpManager.getInstance(context).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override

            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Response_catalog response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                    sendAddToCartAnalyticsData("Product Image View", response_catalog, false, product);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    public void addMultipleProductToCartSinglePrice(ArrayList<ProductObj> oldProductObj) {
        if(oldProductObj!=null && oldProductObj.size() > 0) {
            try {
                List<CartProductModel.Items> data = new ArrayList<>();
                boolean isContainsNote = false;
                ProductObj pArray [] = new ProductObj[oldProductObj.size()];
                for (int i = 0;i<oldProductObj.size();i++) {
                    pArray[i] = oldProductObj.get(i);
                    CartProductModel.Items items = new CartProductModel.Items();
                    items.setProduct(oldProductObj.get(i).getId());
                    if(oldProductObj.get(i).getQuantity()!=null)
                        items.setQuantity(oldProductObj.get(i).getQuantity());
                    else
                        items.setQuantity("1");
                    items.setRate(oldProductObj.get(i).getPublic_price());
                    items.setIs_full_catalog(false);
                    if(oldProductObj.get(i).getNote()!=null
                            && !oldProductObj.get(i).getNote().isEmpty()
                            && !oldProductObj.get(i).getNote().equalsIgnoreCase("Nan")) {
                        items.setNote(oldProductObj.get(i).getNote());
                        isContainsNote = true;
                    }
                    savePreference1(oldProductObj.get(i).getId());
                    data.add(items);
                }
                CartProductModel cartProductModel = new CartProductModel(data);
                if(isContainsNote) {
                    cartProductModel.setAdd_size(true);
                } else {
                    cartProductModel.setAdd_size(false);
                }
                cartProductModel.setAdd_quantity(true);

                postItemsToCart(cartProductModel, preferences.getString("cartId", "null").equals("null"), null, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface SizeGetCallbackListener {
        void onSuccess(boolean isSizeMandatory) ;
        void onFailure();
    }


    public SizeGetCallbackListener getSizeGetCallbackListener() {
        return sizeGetCallbackListener;
    }

    public void setSizeGetCallbackListener(SizeGetCallbackListener sizeGetCallbackListener) {
        this.sizeGetCallbackListener = sizeGetCallbackListener;
    }

    public void handleAddToCartForCatalogProduct1(@NonNull final String catalog, final @NonNull String sellerId, final Fragment fragment, final String type, final AppCompatActivity activity, final Button btn_cart, final Response_catalog response_catalog) {
        Log.e(TAG, "handleAddToCartForCatalogProduct1: ====>" );
        final ProductObj productObj = response_catalog.getProduct()[0];
        final String[] note = {"Nan"};
        if (productObj.getAvailable_size_string() != null && !productObj.getAvailable_size_string().isEmpty() && productObj.getAvailable_size_string().split(",").length > 1) {
            SelectSizeBottomSheet bottomSheetDialog;
            if (productObj!= null && productObj.getSingle_piece_price() != null && !productObj.getSingle_piece_price().equals("null")) {
                bottomSheetDialog = SelectSizeBottomSheet.getInstance(productObj.getAvailable_size_string(), response_catalog.getSingle_piece_price_range(), response_catalog.getTotal_products(), "gallery");
            } else {
                bottomSheetDialog = SelectSizeBottomSheet.getInstance(productObj.getAvailable_size_string(), response_catalog.getPrice_range(), response_catalog.getTotal_products(), "gallery");
            }
            bottomSheetDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "Custom Bottom Sheet");
            bottomSheetDialog.setDismissListener(new SelectSizeBottomSheet.DismissListener() {
                @Override
                public void onDismiss(HashMap<String, Integer> value) {
                    if (value.size() > 0) {
                        if (!preferences.getString("cartId", "").equalsIgnoreCase("")) {
                            handleAddToCartWithSize(value, productObj, (AppCompatButton) btn_cart,response_catalog);
                        } else {
                            getCartId(catalog, sellerId, fragment, type, activity, btn_cart);
                        }
                    } else {
                        if(addToCartCallbackListener!=null) {
                            addToCartCallbackListener.onFailure();
                        }
                    }
                }
            });
            return;
        } else {
            Log.e(TAG, "handleAddToCartForCatalogProduct1: only one size====>" );
            note[0] = "Size : " + productObj.getAvailable_size_string();
        }
        int qtys[] = new int[1];
        qtys[0] = 1;
        addProductToCart(response_catalog.getId(),
                productObj.getId(),
                productObj,
                qtys, false,
                "", null,
                "gallery", (AppCompatActivity) context,
                note[0], response_catalog.getProduct().length);
    }

    public void handleAddToCartWithSize(HashMap<String, Integer> sizeQtyMap, ProductObj productObj, final Button btn_add_to_cart, Response_catalog response_catalog) {
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
            addProductToCart(response_catalog.getId(),productObj.getId(),
                   productObj,
                    qtys,
                    false, "", null, "productkurti", (AppCompatActivity) context,
                    "Size : " + l.get(j), response_catalog.getProduct().length);
        }
    }

    public class GetSize {
        Context context;
        String category_id;
        SizeGetCallbackListener sizeGetCallbackListener;

        public GetSize(Context context, String category_id,SizeGetCallbackListener sizeGetCallbackListener) {
            this.context = context;
            this.category_id = category_id;
            this.sizeGetCallbackListener = sizeGetCallbackListener;
            getSizeEav(context,category_id);
        }


        private void getSizeEav(Context context, String category_id){
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            String url = URLConstants.CATEGORY_EVP_V2 + "?category=" + category_id + "&attribute_slug=" + "size";
            final MaterialDialog progressdialog = StaticFunctions.showProgress(context);
            progressdialog.show();
            HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if (progressdialog != null) {
                        progressdialog.dismiss();
                    }
                    final ResponseCategoryEvp[] evpAttribute = Application_Singleton.gson.fromJson(response, ResponseCategoryEvp[].class);
                    if(evpAttribute!=null) {
                        if (evpAttribute.length > 0) {
                            if(sizeGetCallbackListener!=null) {
                                sizeGetCallbackListener.onSuccess(evpAttribute[0].getIs_required());
                            }
                        } else {
                            if(sizeGetCallbackListener!=null) {
                                sizeGetCallbackListener.onSuccess(false);
                            }
                        }
                    } else {
                        if(sizeGetCallbackListener!=null) {
                            sizeGetCallbackListener.onSuccess(false);
                        }
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    if (progressdialog != null) {
                        progressdialog.dismiss();
                    }

                    if(sizeGetCallbackListener!=null) {
                        sizeGetCallbackListener.onFailure();
                    }
                }
            });
        }
    }




}
