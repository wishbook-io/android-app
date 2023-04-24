package com.wishbook.catalog.home.catalog.details;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.touchImageView.ExtendedViewPager;
import com.wishbook.catalog.Utils.widget.MaterialBadgeTextView;
import com.wishbook.catalog.commonmodels.ProductMyDetail;
import com.wishbook.catalog.commonmodels.SaveCartData;
import com.wishbook.catalog.home.cart.Fragment_MyCart;
import com.wishbook.catalog.home.catalog.adapter.ProductPagerAdapter;
import com.wishbook.catalog.home.catalog.share.ProductSingleShareApi;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;


public class Activity_ProductsInCatalog extends AppCompatActivity {

    public SharedPreferences pref;
    ImageView cart;
    MaterialBadgeTextView cart_count;
    private ExtendedViewPager mViewPager;
    private Toolbar toolbar;
    private String selectedId = "";
    private int position = 0;
    private TextView noProductsDisplay;
    private GestureDetector gestureDetector;
    boolean fullcatalog, singlepiece, both;
    int productCount, fullCount;
    TextView cart_info;

    // ## start Size Inventory Changes ### //
    ProductMyDetail productMyDetail;
    Context mContext;
    boolean isProductWithSize;
    String category_id;

    private static final int REQUESTWRITEPERMISSION = 556;
    ProductPagerAdapter productPagerAdapter;
    private static String TAG = Activity_ProductsInCatalog.class.getSimpleName();


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticFunctions.initializeAppsee();
        mContext = Activity_ProductsInCatalog.this;
        setContentView(R.layout.fragment_products_in_catalog);
        toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fullcatalog = false;
        singlepiece = false;
        both = false;
        productCount = 0;
        fullCount = 0;
        try {
            SharedPreferences preferences = getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
            if (!preferences.getString("cartdata", "").equalsIgnoreCase("")) {
                Type cartType = new TypeToken<ArrayList<SaveCartData>>() {
                }.getType();
                ArrayList<SaveCartData> saveCartData = new Gson().fromJson(preferences.getString("cartdata", ""), cartType);

                if (saveCartData != null && saveCartData.size() > 0) {
                    for (int i = 0; i < saveCartData.size(); i++) {
                        if (saveCartData.get(i).getId().equals(Application_Singleton.getResponse_catalog().getId())) {
                            if (saveCartData.get(i).getProducts() == null || saveCartData.get(i).getProducts().getId() == null || saveCartData.get(i).getProducts().getId().size() == 0) {
                                fullcatalog = true;
                                fullCount++;
                            } else {
                                both = true;
                            }
                        }
                    }

                    for (int i = 0; i < saveCartData.size(); i++) {
                        if (both && saveCartData.get(i).getId().equals(Application_Singleton.getResponse_catalog().getId())) {
                            for (int j = 0; j < Application_Singleton.getResponse_catalog().getProducts().size(); j++) {

                                if (saveCartData.get(i).getProducts().getId().contains(Application_Singleton.getResponse_catalog().getProducts().get(j).getId())) {
                                    productCount = productCount + 1;
                                    singlepiece = true;
                                }
                            }
                        }
                    }


                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        cart = findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                int cart_count = preferences.getInt("cartcount", 0);
                Application_Singleton.CONTAINER_TITLE = "My Cart (" + cart_count + " items)";
                Application_Singleton.CONTAINERFRAG = new Fragment_MyCart();
                StaticFunctions.switchActivity(Activity_ProductsInCatalog.this, OpenContainer.class);
            }
        });
        cart_count = findViewById(R.id.badge_cart_count);

        try {
            SharedPreferences preferences = Activity_ProductsInCatalog.this.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
            int cartcount = preferences.getInt("cartcount", 0);
            if (cartcount == 0) {
                cart_count.setBadgeCount(0);
            } else {
                cart_count.setBadgeCount(cartcount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        pref = StaticFunctions.getAppSharedPreferences(Activity_ProductsInCatalog.this);
        pref.edit().putString("hidden", "no").apply();
        noProductsDisplay = (TextView) findViewById(R.id.textNoProducts);
        try {
            selectedId = getIntent().getExtras().getString("position", "0");
            position = Integer.parseInt(selectedId);
        } catch (Exception e) {
            position = 0;
        }

        if (getIntent() != null && getIntent().getStringExtra("category_id") != null) {
            category_id = getIntent().getStringExtra("category_id");
        }

        if (getIntent() != null && getIntent().getSerializableExtra("product_mydetail") != null) {
            productMyDetail = (ProductMyDetail) getIntent().getSerializableExtra("product_mydetail");
        }


        mViewPager = (ExtendedViewPager) findViewById(R.id.pager);
       /* mViewPager.setAdapter(new CustomPagerAdapter(Activity_ProductsInCatalog.this));
        mViewPager.setCurrentItem(position);*/

        //  mViewPager.setPageTransformer(true, new CubeOutTransformer());
        // toolbar.setVisibility(View.GONE);
        if (Application_Singleton.selectedCatalogProducts.size() == 0) {
            noProductsDisplay.setVisibility(View.VISIBLE);
        }


        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.my_statusbar_color));

        } else {
            // Implement this feature without material design
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        pref = StaticFunctions.getAppSharedPreferences(Activity_ProductsInCatalog.this);
        try {
            selectedId = getIntent().getExtras().getString("position", "0");
            position = Integer.parseInt(selectedId);
        } catch (Exception e) {
            position = 0;
        }


        productPagerAdapter = new ProductPagerAdapter(Activity_ProductsInCatalog.this,
                toolbar, cart, mViewPager, getIntent().getBooleanExtra("isFullCatalog", true),
                category_id, (ProductMyDetail) getIntent().getSerializableExtra("product_mydetail"),
                getIntent().getDoubleExtra("full_discount", 0));
        mViewPager.setAdapter(productPagerAdapter);
        mViewPager.setCurrentItem(position);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        try {
            SharedPreferences preferences = Activity_ProductsInCatalog.this.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
            int cartcount = preferences.getInt("cartcount", 0);
            if (cartcount == 0) {
                cart_count.setBadgeCount(0);
            } else {
                cart_count.setBadgeCount(cartcount);
            }
            productPagerAdapter.showAlreadyAddText(cart_info, productCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateBadge(int count) {
        try {
            if (count == 0) {
                cart_count.setBadgeCount(0);
            } else {
                cart_count.setBadgeCount(count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTWRITEPERMISSION) {
            Map<String, Integer> params = new HashMap<String, Integer>();
            params.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            for (int i = 0; i < permissions.length; i++)
                params.put(permissions[i], grantResults[i]);
            if (params.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                new ProductSingleShareApi(mContext, Application_Singleton.selectedCatalogProducts.get(mViewPager.getCurrentItem()).getId(), StaticFunctions.SHARETYPE.GALLERY, null);
            } else {
                // Permission Denied
                Toast.makeText(mContext, "WRITE_EXTERNAL_STORAGE Permission is Denied", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}