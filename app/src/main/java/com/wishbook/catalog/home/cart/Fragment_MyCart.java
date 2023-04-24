package com.wishbook.catalog.home.cart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.ToastUtil;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.CartCatalogModel;
import com.wishbook.catalog.commonmodels.CartProductModel;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.ImageBanner;
import com.wishbook.catalog.commonmodels.responses.ResponseShipment;
import com.wishbook.catalog.commonmodels.responses.Response_Promotion;
import com.wishbook.catalog.home.orders.ActivityOrderHolder;
import com.wishbook.catalog.rest.CampaignLogApi;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_MyCart extends GATrackedFragment implements SwipeRefreshLayout.OnRefreshListener {


    RecyclerView cart_items;
    RelativeLayout my_cart_layout;
    int pos;
    CartCatalogModel cart_response;
    TextView grand_total;
    SwipeRefreshLayout swipe_container;
    Handler handler;
    Runnable runnable, runnable_banner;
    boolean isAllowCache = false;
    AppCompatButton place_order;
    TextView empty_cart;
    LinearLayout checkout_bottom;
    CartCatalogAdapter cartCatalogAdapter;

    @BindView(R.id.txt_total_shipping_charges)
    TextView txt_total_shipping_charges;
    @BindView(R.id.card_shipping_details)
    CardView card_shipping_details;

    ViewPager viewPager;
    RelativeLayout banner_container;
    int currentPage = 0;
    Handler handler_banner;
    Timer timer;


    public static int CHANGE_SUPPLIER_REQUEST_CODE = 600;
    private SharedPreferences preferences;

    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        assert v != null;
        ViewGroup ga_container = v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_mycart, ga_container, true);
        ButterKnife.bind(this, v);
        initView(v);
        initSwipeRefresh(v);
        initCall();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }

        if (handler_banner != null && runnable_banner != null) {
            handler_banner.removeCallbacks(runnable_banner);
        }

        if (timer != null) {
            timer.cancel();
        }


    }

    public void initView(@NonNull View v) {
        cart_items = v.findViewById(R.id.cart_items);
        my_cart_layout = v.findViewById(R.id.my_cart_layout);
        grand_total = v.findViewById(R.id.grand_total);
        place_order = v.findViewById(R.id.place_order);
        empty_cart = v.findViewById(R.id.empty_cart);
        checkout_bottom = v.findViewById(R.id.checkout_bottom);
        viewPager = v.findViewById(R.id.viewPager);
        banner_container = v.findViewById(R.id.banner_container);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        cart_items.setLayoutManager(mLayoutManager);
        cart_items.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                swipe_container.setEnabled(topRowVerticalPosition >= 0);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        cart_items.setNestedScrollingEnabled(false);

        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Application_Singleton.trackEvent("Cart Place Order", "Click", "Place Order");
                    boolean isPreordercart = false;
                    if (cart_response.getCatalogs().size() > 0) {
                        for (int i = 0; i < cart_response.getCatalogs().size(); i++) {
                            if (!cart_response.getCatalogs().get(i).isReady_to_ship()) {
                                isPreordercart = true;
                                break;
                            }
                        }
                    }
                    if (isPreordercart) {
                        final PreOrderAlertBottomSheet bottomSheetDialog;
                        bottomSheetDialog = PreOrderAlertBottomSheet.getInstance(cart_response);
                        bottomSheetDialog.show(getFragmentManager(), bottomSheetDialog.getTag());
                        bottomSheetDialog.setPreOrderDoneListener(new PreOrderAlertBottomSheet.PreOrderDoneListener() {
                            @Override
                            public void onDone() {
                                bottomSheetDialog.dismiss();
                                navigateCashPayment(getActivity(), false);
                            }
                        });
                    } else {
                        navigateCashPayment(getActivity(), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Fragment_MyCart.CHANGE_SUPPLIER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            initCall();
        } else {
            if (requestCode == 909 && resultCode == Activity.RESULT_OK) {
                try {
                    startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("type", "purchase").putExtra("position", 0).putExtra("isFromPlaceOrder", true));
                    getActivity().finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void initCall() {
        getBanner();
        getCartData((AppCompatActivity) getActivity(), 0);
    }

    public void patchData(final @NonNull AppCompatActivity context, final boolean choice, final String fromCart) {
        if (cart_response != null && cart_response.getItems().size() > 0) {
            final SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
            try {
                String url = URLConstants.companyUrl(context, "cart", "") + preferences.getString("cartId", "") + "/";
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
                showProgress();
                List<CartProductModel.Items> items = new ArrayList<>();
                CartProductModel products = new CartProductModel(items);
                products.setFinalize(true);
                products.setAdd_quantity(false);


                for (int i = 0; i < cart_response.getCatalogs().size(); i++) {
                    CartProductModel.Items item = new CartProductModel.Items();
                    if (cart_response.getCatalogs().get(i).getProducts().get(0).getCount() == 0) {
                        item.setQuantity("" + 1);
                    } else {
                        item.setQuantity(String.valueOf(cart_response.getCatalogs().get(i).getProducts().get(0).getCount()));
                    }
                    if (cart_response.getCatalogs().get(i).getProducts().get(0).getNote() != null) {
                        item.setNote(cart_response.getCatalogs().get(i).getProducts().get(0).getNote());
                    }
                    item.setProduct(cart_response.getCatalogs().get(i).getProducts().get(0).getProduct());
                    item.setRate(String.valueOf(cart_response.getCatalogs().get(i).getProducts().get(0).getRate()));
                    item.setIs_full_catalog(cart_response.getCatalogs().get(i).getProducts().get(0).getIs_full_catalog());
                    products.getItems().add(item);
                }
                Log.d("PATCHDATA", "" + (new Gson().toJson(products)));
                HttpManager.getInstance(context).requestwithObject(HttpManager.METHOD.PATCHJSONOBJECTWITHPROGRESS, url, new Gson().fromJson(new Gson().toJson(products), JsonObject.class), headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        try {
                            CartCatalogModel temp = Application_Singleton.gson.fromJson(response, CartCatalogModel.class);
                            temp.setCatalogs(cart_response.getCatalogs());
                            cart_response = temp;
                            if (cartCatalogAdapter != null) {
                                cartCatalogAdapter.fullList = cart_response;
                                cartCatalogAdapter.notifyDataSetChanged();
                            }

                            if (choice) {
                                boolean isPreordercart = false;
                                if (cart_response.getCatalogs().size() > 0) {
                                    for (int i = 0; i < cart_response.getCatalogs().size(); i++) {
                                        if (!cart_response.getCatalogs().get(i).isReady_to_ship()) {
                                            isPreordercart = true;
                                            break;
                                        }
                                    }
                                }
                                if (isPreordercart) {
                                    final PreOrderAlertBottomSheet bottomSheetDialog;
                                    bottomSheetDialog = PreOrderAlertBottomSheet.getInstance(cart_response);
                                    bottomSheetDialog.show(getFragmentManager(), bottomSheetDialog.getTag());
                                    bottomSheetDialog.setPreOrderDoneListener(new PreOrderAlertBottomSheet.PreOrderDoneListener() {
                                        @Override
                                        public void onDone() {
                                            bottomSheetDialog.dismiss();
                                            navigateCashPayment(context, false);
                                        }
                                    });
                                } else {
                                    navigateCashPayment(context, false);
                                }

                            } else if (fromCart == null) {
                                CartProductModel response_model = new Gson().fromJson(response, CartProductModel.class);
                                preferences.edit().putInt("cartcount", Integer.parseInt(response_model.getTotal_cart_items())).commit();
                                getActivity().finish();
                            }
                            hideProgress();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        hideProgress();
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                        if (cartCatalogAdapter != null) {
                            cartCatalogAdapter.fullList = cart_response;
                            cartCatalogAdapter.notifyDataSetChanged();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                getActivity().finish();
            } catch (Exception e) {
            }
        }
    }


    public void patchQuantity(final @NonNull AppCompatActivity context, CartProductModel cartProductModel, final int focus_position) {
        if (cart_response != null && cart_response.getItems().size() > 0) {
            final SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
            try {
                String url = URLConstants.companyUrl(context, "cart", "") + preferences.getString("cartId", "") + "/";
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
                showProgress();
                Log.d("PATCHDATA", "" + (new Gson().toJson(cartProductModel)));
                HttpManager.getInstance(context).requestwithObject(HttpManager.METHOD.PATCHJSONOBJECTWITHPROGRESS, url, new Gson().fromJson(new Gson().toJson(cartProductModel), JsonObject.class), headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        try {
                            Log.d("PATCHRESPONSE", response);
                            CartCatalogModel temp = Application_Singleton.gson.fromJson(response, CartCatalogModel.class);
                            getCartData((AppCompatActivity) getActivity(), focus_position);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        hideProgress();
                        StaticFunctions.showResponseFailedDialog(error);
                        if (cartCatalogAdapter != null) {
                            cartCatalogAdapter.fullList = cart_response;
                            cartCatalogAdapter.notifyDataSetChanged();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                getActivity().finish();
            } catch (Exception e) {
            }
        }
    }

    public void getCartData(final @NonNull AppCompatActivity context, final int focus_position) {
        try {
            if (updateUI()) {
                try {
                    String url = URLConstants.companyUrl(context, "cart", "") + preferences.getString("cartId", "") + "/catalogwise/";
                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
                    showProgress();
                    HttpManager.getInstance(context).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, isAllowCache, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {
                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            try {
                                hideProgress();
                                checkout_bottom.setVisibility(View.VISIBLE);
                                cart_response = Application_Singleton.gson.fromJson(response, CartCatalogModel.class);
                                if (cart_response == null || cart_response.getItems() == null || cart_response.getItems().size() == 0) {
                                    checkout_bottom.setVisibility(View.GONE);
                                    card_shipping_details.setVisibility(View.GONE);
                                } else {
                                    getShippingCharges(cart_response.getId());
                                }
                                if (cartCatalogAdapter == null) {
                                    cartCatalogAdapter = new CartCatalogAdapter(cart_response.getCatalogs(), getActivity(), cart_items, my_cart_layout, Fragment_MyCart.this);
                                    cartCatalogAdapter.setListener(new CartCatalogAdapter.AdapterListener() {
                                        public void onUpdateGrandTotal(@NonNull final String name) {
                                            setGrandTotal(0.0);
                                        }
                                    });
                                } else {
                                    cartCatalogAdapter.setCartList(cart_response.getCatalogs());
                                }
                                if (focus_position > 0) {
                                    cartCatalogAdapter.notifyItemChanged(focus_position + 1);
                                    cart_items.scrollToPosition(focus_position + 1);
                                } else {
                                    cart_items.setAdapter(cartCatalogAdapter);
                                    cart_items.scrollToPosition(pos);
                                }
                                updateUI();
                                hideOrderDisableConfig();
                                if (cart_response != null)
                                    StaticFunctions.saveCartData(context, cart_response);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {
                            hideProgress();
                            StaticFunctions.saveCartData(context, cart_response);
                            StaticFunctions.showResponseFailedDialog(error);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean updateUI() {
        preferences = getActivity().getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
        if (preferences.getString("cartId", "").equals("null") || preferences.getString("cartId", "").equals("")) {
            cart_items.setVisibility(View.GONE);
            empty_cart.setVisibility(View.VISIBLE);
            checkout_bottom.setVisibility(View.GONE);
            return false;
        } else {
            if (cart_response != null) {
                preferences.edit().putInt("cartcount", Integer.parseInt(cart_response.getTotal_cart_items())).commit();
                CartCatalogAdapter.count = Integer.parseInt(cart_response.getTotal_cart_items());
                if (CartCatalogAdapter.count == 1) {
                    ((OpenContainer) getActivity()).toolbar.setTitle("My Cart (" + (CartCatalogAdapter.count) + " Item)");
                } else {
                    ((OpenContainer) getActivity()).toolbar.setTitle("My Cart (" + (CartCatalogAdapter.count) + " Items)");
                }
                if (cart_response == null || cart_response.getItems() == null || cart_response.getItems().size() == 0) {
                    cart_items.setVisibility(View.GONE);
                    empty_cart.setVisibility(View.VISIBLE);
                    checkout_bottom.setVisibility(View.GONE);
                } else {
                    cart_items.setVisibility(View.VISIBLE);
                    empty_cart.setVisibility(View.GONE);
                }
            }
            return true;
        }
    }

    public void setGrandTotal(Double total_shipping_charges) {
        if (cart_response != null && cart_response.getTotal_amount() > 0 && cart_response.getShipping_charges() != null) {
            DecimalFormat decimalFormat = new DecimalFormat("#0.##");
            String cartTotal = String.valueOf(decimalFormat.format
                    (
                            (cart_response.getTotal_amount() - Double.parseDouble(cart_response.getShipping_charges()))
                                    + total_shipping_charges));
            grand_total.setText(String.format("Total Value: \u20B9 %s", cartTotal));
        } else {
            checkout_bottom.setVisibility(View.GONE);
        }
    }

    public void navigateCashPayment(Context context, boolean isResellerPatched) {


        Bundle bundle = new Bundle();
        bundle.putBoolean("isBrokerageOrder", false);
        Fragment_Payment fragment_cashPayment_2 = new Fragment_Payment();
        fragment_cashPayment_2.setArguments(bundle);
        Application_Singleton.CONTAINER_TITLE = "Shipment & Payment";
        Application_Singleton.CONTAINERFRAG = fragment_cashPayment_2;
        Intent i = new Intent(context, OpenContainer.class);
        startActivityForResult(i, 909);
        sendChekoutIntializedAnalytics();

    }

    public void initSwipeRefresh(@NonNull View view) {
        try {
            swipe_container = view.findViewById(R.id.swipe_container);
            swipe_container.setOnRefreshListener(this);
            swipe_container.setColorScheme(android.R.color.holo_blue_dark,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onRefresh() {
        runnable = new Runnable() {

            @Override
            public void run() {
                isAllowCache = false;
                swipe_container.setRefreshing(false);
                initCall();
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);
    }

    public void refreshCart() {
        runnable = new Runnable() {

            @Override
            public void run() {
                isAllowCache = false;
                swipe_container.setRefreshing(false);
                initCall();
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);
    }

    private void getBanner() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) getContext());
        HttpManager.getInstance((Activity) getContext()).request(HttpManager.METHOD.GET, URLConstants.BANNER_URL + "?language_code=" + UserInfo.getInstance(getActivity()).getLanguage() + "&only_show_on=cart", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    final Response_Promotion[] promotion = new Gson().fromJson(response, Response_Promotion[].class);
                    if (isAdded() && !isDetached()) {
                        if (promotion.length > 0) {
                            banner_container.setVisibility(View.VISIBLE);
                            viewPager.setVisibility(View.VISIBLE);
                            ArrayList<ImageBanner> imageBanners = new ArrayList<ImageBanner>();
                            for (Response_Promotion response_promotion : promotion) {
                                imageBanners.add(response_promotion.getImage());
                            }

                            final MyCustomPagerAdapter myCustomPagerAdapter = new MyCustomPagerAdapter(getContext(), imageBanners, promotion);
                            viewPager.setAdapter(myCustomPagerAdapter);
                            final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
                            final long PERIOD_MS = 7000; // time in milliseconds between successive task executions.
                            if (promotion.length > 1) {
                                viewPager.setCurrentItem(viewPager.getChildCount() * myCustomPagerAdapter.LOOPS_COUNT / 2, false); // set current item in the adapter to middle
                                handler_banner = new Handler();
                                runnable_banner = new Runnable() {
                                    @Override
                                    public void run() {
                                        currentPage = viewPager.getCurrentItem() + 1;
                                        viewPager.setCurrentItem(currentPage, true);
                                    }
                                };
                                timer = new Timer(); // This will create a new Thread
                                timer.schedule(new TimerTask() { // task to be scheduled
                                    @Override
                                    public void run() {
                                        handler_banner.post(runnable_banner);
                                    }
                                }, DELAY_MS, PERIOD_MS);
                            } else {
                                viewPager.setCurrentItem(0); // set current item in the adapter to middle
                            }

                        } else {
                            banner_container.setVisibility(View.GONE);
                            viewPager.setVisibility(View.GONE);
                        }

                    } else {
                        banner_container.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    banner_container.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                banner_container.setVisibility(View.GONE);
            }
        });
    }

    public class MyCustomPagerAdapter extends PagerAdapter {
        Context context;
        ArrayList<ImageBanner> images = new ArrayList<>();
        LayoutInflater layoutInflater;
        private Response_Promotion[] response_promotions;
        public int LOOPS_COUNT = 100;

        public MyCustomPagerAdapter(Context context, ArrayList<ImageBanner> images, Response_Promotion[] promotion) {
            this.context = context;
            this.images = images;
            this.response_promotions = promotion;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if (images.size() > 1) {
                return images.size() * LOOPS_COUNT;
            } else {
                return images.size();
            }

        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = layoutInflater.inflate(R.layout.banner_item, container, false);
            final int newposition = position % response_promotions.length;
            SimpleDraweeView imageView = (SimpleDraweeView) itemView.findViewById(R.id.imageView);
            if (images.get(newposition).getFull_size().contains(".gif")) {
                StaticFunctions.loadFresco(context, images.get(newposition).getFull_size(), imageView);
            } else {
                StaticFunctions.loadFresco(context, images.get(newposition).getBanner(), imageView);
            }
            container.addView(itemView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (response_promotions[newposition].getLanding_page_type() != null) {
                        HashMap<String, String> prop = new HashMap<>();
                        prop.put("source", "Cart Banner");
                        if (response_promotions[newposition].getLanding_page_type() != null) {
                            prop.put("landing_page_type", response_promotions[newposition].getLanding_page_type());
                        }

                        if (response_promotions[newposition].getLanding_page() != null) {
                            prop.put("landing_page", response_promotions[newposition].getLanding_page());
                        }
                        sendBannerClickAnalytics(prop);
                        new CampaignLogApi(getActivity(), response_promotions[newposition].getCampaign_name());
                        Application_Singleton singleton = new Application_Singleton();
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("type", response_promotions[newposition].getLanding_page_type().toLowerCase());
                        if (!response_promotions[newposition].getLanding_page_type().equals("Webview")) {
                            hashMap.put("page", response_promotions[newposition].getLanding_page() != null ? response_promotions[newposition].getLanding_page().toLowerCase() : "");
                        } else {
                            hashMap.put("page", response_promotions[newposition].getLanding_page());
                        }
                        if (response_promotions[newposition].getLanding_page_type().toLowerCase().equals("deep_link")) {
                            if (response_promotions[newposition].getLanding_page() != null) {
                                if (response_promotions[newposition].getLanding_page() != null) {
                                    String deep_link = response_promotions[newposition].getLanding_page().toLowerCase();
                                    for (final String entry : deep_link.split("&")) {
                                        final String[] parts = entry.split("=");
                                        hashMap.put(parts[0], parts[1]);
                                    }
                                }
                            }
                        }
                        new DeepLinkFunction(hashMap, context);
                    }
                }
            });

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }


    public void sendBannerClickAnalytics(HashMap<String, String> prop) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Banner_Clicked");
        wishbookEvent.setEvent_properties(prop);

        new WishbookTracker(getActivity(), wishbookEvent);
    }

    public void sendChekoutIntializedAnalytics() {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.ECOMMERCE_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Checkout_Initiated");
        HashMap<String, String> prop = new HashMap<>();
        if (cart_response != null)
            prop.put("cart_id", cart_response.getId());
        prop.putAll(getCartAttributes());
        wishbookEvent.setEvent_properties(prop);

        new WishbookTracker(getActivity(), wishbookEvent);
    }

    public HashMap<String, String> getCartAttributes() {
        HashMap<String, String> prop = new HashMap<>();
        try {
            if (cart_response != null) {
                String cart_full_set_flag = null;
                prop.put("cart_value", String.valueOf(cart_response.getTotal_amount()));
                prop.put("shipping_fee", String.valueOf(cart_response.getShipping_charges()));
                prop.put("discount", String.valueOf(cart_response.getSeller_discount()));
                prop.put("wb_money_used", String.valueOf(cart_response.getWb_money_used()));
                prop.put("cart_units", cart_response.getTotal_qty());
                Double cart_avg_price = (cart_response.getTotal_amount() / Integer.parseInt(cart_response.getTotal_qty()));
                prop.put("cart_unit_avg_price", String.valueOf(cart_avg_price));
                ArrayList<String> product_id = new ArrayList<>();
                ArrayList<String> product_cart_images = new ArrayList<>();
                ArrayList<String> product_type = new ArrayList<>();
                ArrayList<String> catalog_names = new ArrayList<>();
                ArrayList<String> product_categories = new ArrayList<>();
                ArrayList<String> catalog_total_amt = new ArrayList<>();
                if (cart_response.getCatalogs() != null) {
                    for (int i = 0; i < cart_response.getCatalogs().size(); i++) {
                        product_id.add(cart_response.getCatalogs().get(i).getProduct_id());
                        product_type.add(cart_response.getCatalogs().get(i).getProducts().get(0).getProduct_type());
                        catalog_names.add(cart_response.getCatalogs().get(i).getCatalog_title());
                        product_categories.add(cart_response.getCatalogs().get(i).getCatalog_category());
                        catalog_total_amt.add(cart_response.getCatalogs().get(i).getCatalog_total_amount());
                        if (cart_response.getCatalogs().get(i).isIs_full_catalog())
                            product_cart_images.add(cart_response.getCatalogs().get(i).getCatalog_image());
                        else
                            product_cart_images.add(cart_response.getCatalogs().get(i).getProducts().get(0).getProduct_image());

                        if (cart_response.getCatalogs().get(i).isIs_full_catalog()) {
                            if (cart_full_set_flag == null) {
                                cart_full_set_flag = "true";
                            }
                        }

                    }
                    prop.put("product_ids", StaticFunctions.ArrayListToString(product_id, StaticFunctions.COMMASEPRATEDSPACE));
                    prop.put("catalog_item_names", StaticFunctions.ArrayListToString(catalog_names, StaticFunctions.COMMASEPRATEDSPACE));
                    prop.put("product_types", StaticFunctions.ArrayListToString(product_type, StaticFunctions.COMMASEPRATEDSPACE));
                    prop.put("product_categories", StaticFunctions.ArrayListToString(product_categories, StaticFunctions.COMMASEPRATEDSPACE));
                    prop.put("cart_image", StaticFunctions.ArrayListToString(product_cart_images, StaticFunctions.COMMASEPRATEDSPACE));
                    prop.put("catalog_total_amt", StaticFunctions.ArrayListToString(catalog_total_amt, StaticFunctions.COMMASEPRATEDSPACE));
                    if (cart_full_set_flag == null) {
                        prop.put("cart_fullset_flag", "false");
                    } else {
                        prop.put("cart_fullset_flag", cart_full_set_flag);
                    }


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return prop;
    }

    public void getShippingCharges(@NonNull String cartID) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.SHIPPING_CHARGE + "?cart=" + cartID, null, headers, false, new HttpManager.customCallBack() {

            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                try {
                    final ArrayList<ResponseShipment> shipments = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<ResponseShipment>>() {
                    }.getType());
                    if (shipments != null && shipments.size() > 0) {
                        card_shipping_details.setVisibility(View.VISIBLE);
                        for (int i = 0; i < shipments.size(); i++) {
                            if (shipments.get(i).isIs_default()) {
                                txt_total_shipping_charges.setText("\u20B9"+ shipments.get(i).getShipping_charge());
                                setGrandTotal(shipments.get(i).getShipping_charge());
                            }
                        }
                    } else {
                        card_shipping_details.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();

            }
        });
    }

    /**
     * Temporary function for Order Disable Config
     */
    public void hideOrderDisableConfig() {
        // add catalog button hide from catalog level
        if (StaticFunctions.checkOrderDisableConfig(getActivity())) {
            place_order.setEnabled(false);
            place_order.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.light_gray));
        }
    }


}
