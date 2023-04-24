package com.wishbook.catalog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Utils.DownloadImage;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.networking.NetworkProcessor;
import com.wishbook.catalog.Utils.networking.async.future.FutureCallback;
import com.wishbook.catalog.Utils.touchImageView.ExtendedViewPager;
import com.wishbook.catalog.Utils.touchImageView.TouchImageView;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Response_like;
import com.wishbook.catalog.home.models.ProductObj;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.wishbook.catalog.home.Activity_Home.pref;


public class Activity_DisplayProduct extends AppCompatActivity {

    int count = 0;
    private View v;
    private UserInfo userInfo;
    private ExtendedViewPager mViewPager;
    private Toolbar toolbar;
    private String selectedId = "";
    private int position = 0;
    private ProductObj productObj;
    private TextView noProductsDisplay;
    private GestureDetector gestureDetector;


    @Override
    protected void onResume() {
        super.onResume();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_products_in_catalog);
        StaticFunctions.initializeAppsee();
        toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        try {
            pref.edit().putString("hidden", "no").apply();
        }catch (Exception e){

        }

        noProductsDisplay = (TextView) findViewById(R.id.textNoProducts);
        userInfo = UserInfo.getInstance(Activity_DisplayProduct.this);
        mViewPager = (ExtendedViewPager) findViewById(R.id.pager);

        if (getIntent().getStringExtra("productid") != null) {
            getProducts(getIntent().getStringExtra("productid"));
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

    private void getProducts(String id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_DisplayProduct.this);
        HttpManager.getInstance(Activity_DisplayProduct.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(Activity_DisplayProduct.this, "single_product", id), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Log.v("sync response ", response);
                    productObj = Application_Singleton.gson.fromJson(response, ProductObj.class);
                    mViewPager.setAdapter(new CustomPagerAdapter(Activity_DisplayProduct.this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }


    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;


        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = mLayoutInflater.inflate(R.layout.pager_item, container, false);
            final LinearLayout prod_menulay = (LinearLayout) view.findViewById(R.id.subproductmenu);
            final LinearLayout star_container = (LinearLayout) view.findViewById(R.id.star_container);
            final LinearLayout like_container = (LinearLayout) view.findViewById(R.id.like_container);
            //final LinearLayout bottom_container = (LinearLayout) view.findViewById(R.id.bottom_container);
            final LinearLayout container_product_sku = (LinearLayout) view.findViewById(R.id.container_product_sku);

            final AppCompatTextView prod_catalog = (AppCompatTextView) view.findViewById(R.id.prod_catalog);
            final AppCompatTextView prod_sku = (AppCompatTextView) view.findViewById(R.id.prod_sku);
            final AppCompatTextView prod_brand = (AppCompatTextView) view.findViewById(R.id.prod_brand);
            final TextView prod_likes = (TextView) view.findViewById(R.id.prod_likes);
            final AppCompatTextView prod_fabric = (AppCompatTextView) view.findViewById(R.id.prod_fabric);
            final AppCompatTextView prod_work = (AppCompatTextView) view.findViewById(R.id.prod_work);
            final LinearLayout fabric = (LinearLayout) view.findViewById(R.id.container_product_fabric);
            final LinearLayout work = (LinearLayout) view.findViewById(R.id.container_product_work);
            final ImageView like_but = (ImageView) view.findViewById(R.id.like_but);
            final AppCompatButton prod_starbut = (AppCompatButton) view.findViewById(R.id.prod_starbut);
            final AppCompatTextView prod_price = (AppCompatTextView) view.findViewById(R.id.prod_price);
            final RelativeLayout descontent = (RelativeLayout) view.findViewById(R.id.descontent);
            final ImageButton left = (ImageButton) view.findViewById(R.id.left_nav);
            final ImageButton right = (ImageButton) view.findViewById(R.id.right_nav);
            gestureDetector = new GestureDetector(Activity_DisplayProduct.this, new Activity_DisplayProduct.SingleTapConfirm());



            final TouchImageView imageView = (TouchImageView) view.findViewById(R.id.prod_img);
            String image = productObj.getImage().getThumbnail_medium();

            Application_Singleton singleton = new Application_Singleton();
            singleton.trackScreenView("Product/Detail", Activity_DisplayProduct.this);



            //If already have some products that show
           /* if (Activity_DisplayProduct.this.findViewById(R.id.appbar_new) != null) {
                StaticFunctions.setUpselectedProdCounter(Activity_DisplayProduct.this, (Toolbar) Activity_DisplayProduct.this.findViewById(R.id.appbar_new));
            }*/

            if(productObj.getCatalog().getView_permission()!=null) {
                if (productObj.getCatalog().getView_permission().equals("public")) {
                   // bottom_container.setVisibility(View.GONE);
                } else {
                   // bottom_container.setVisibility(View.GONE);
                  //  bottom_container.setVisibility(View.VISIBLE);
                }
            }else{
               // bottom_container.setVisibility(View.GONE);
                //bottom_container.setVisibility(View.VISIBLE);
            }


            if (image != null & !image.equals("")) {

                // get Device Model
                String manufacturer = Build.MANUFACTURER;
                String brand = Build.BRAND;
                String model = Build.MODEL;
                if(brand!=null && brand.toLowerCase().equals("motorola") && model!=null && (model.toLowerCase().equals("MotoG3")||model.toLowerCase().equals("MotoG3-TE") || model.toLowerCase().equals("Moto G (4)") )) {
                     new DownloadImage(imageView).execute(image);
                } else {
                    DataSource<CloseableReference<CloseableImage>> dataSource = Fresco.getImagePipeline().fetchDecodedImage(ImageRequest.fromUri(Uri.parse(image)),getApplicationContext());
                    dataSource.subscribe(new BaseBitmapDataSubscriber() {
                                             @Override
                                             public void onNewResultImpl(@Nullable final Bitmap bitmap) {
                                                 imageView.post(new Runnable() {
                                                     public void run() {
                                                         if(bitmap!=null) {
                                                             imageView.setImageBitmap(bitmap);
                                                         }
                                                     }
                                                 });
                                             }

                                             @Override
                                             public void onFailureImpl(DataSource dataSource) {
                                                 // No cleanup required here.
                                             }
                                         },
                            CallerThreadExecutor.getInstance());
                }


                //StaticFunctions.loadImage(mContext,image,imageView,R.drawable.uploadempty);
               // StaticFunctions.loadFresco(mContext,image,imageView);
                // Picasso.with(mContext).load(image).into(imageView);
            }

            if(pref.getString("hidden","no").equals("no"))
            {
                descontent.setVisibility(View.VISIBLE);
                prod_menulay.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.VISIBLE);

              /*  viewAnimationShow(descontent);
                viewAnimationShow(prod_menulay);
                viewAnimationShow(toolbar);*/
            }
            else
            {
                descontent.setVisibility(View.GONE);
                prod_menulay.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);


               /* viewAnimationHide(descontent);
                viewAnimationHide(prod_menulay);
                viewAnimationHide(toolbar);*/
            }

            if (productObj.getCatalog() != null) {
                if (productObj.getCatalog()!=null) {
                    prod_catalog.setText(productObj.getCatalog().getTitle());
                    prod_brand.setText(productObj.getCatalog().getBrand().getName());
                }
            }

            //added by abu
            if(StaticFunctions.isOnline(Activity_DisplayProduct.this))
            {
                if(productObj.getPush_user_product_id()!=null)
                {
                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_DisplayProduct.this);
                    ArrayList<String> pushid= new ArrayList<String>();
                    ArrayList<String> pushProductid= new ArrayList<String>();
                    pushProductid.add(productObj.getPush_user_product_id().toString());
                    final Activity_DisplayProduct.PostPushProductId postPushUserId = new Activity_DisplayProduct.PostPushProductId(pushid,pushProductid) ;
                    HttpManager.getInstance(Activity_DisplayProduct.this).requestwithObject(HttpManager.METHOD.POSTJSONOBJECT, URLConstants.companyUrl(mContext,"syncactivitylog",""), new Gson().fromJson(new Gson().toJson(postPushUserId), JsonObject.class), headers, true, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {
                            Log.v("cached response", response);
                            onServerResponse(response, false);
                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            try {
                                Log.v("sync response", response);
                                Log.v("Done", "PostingProduct");
                                Log.v("Done", String.valueOf(postPushUserId.getPush_user_product()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                        @Override
                        public void onResponseFailed(ErrorString error) {

                        }
                    });
                }

            }
            else
            {
                if(productObj.getPush_user_product_id()!=null)
                {
                    //added by abu
                    ArrayList<String> pushid= new ArrayList<String>();
                    ArrayList<String> pushProductid= new ArrayList<String>();
                    pushProductid.add(productObj.getPush_user_product_id().toString());
                    addPushId(pushid,pushProductid);

                }
            }



            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if(pref.getString("hidden","no").equals("no"))
                    {
                        descontent.setVisibility(View.VISIBLE);
                        prod_menulay.setVisibility(View.VISIBLE);
                        toolbar.setVisibility(View.VISIBLE);

                              /*  viewAnimationShow(descontent);
                                viewAnimationShow(prod_menulay);
                                viewAnimationShow(toolbar);*/
                    }
                    else
                    {
                        descontent.setVisibility(View.GONE);
                        prod_menulay.setVisibility(View.GONE);
                        toolbar.setVisibility(View.GONE);

                               /* viewAnimationHide(descontent);
                                viewAnimationHide(prod_menulay);
                                viewAnimationHide(toolbar);
*/
                    }
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });


            //changed by abu
           /* if(position==0)
            {
                left.setVisibility(View.INVISIBLE);
            }
            else
            {
                left.setVisibility(View.VISIBLE);
            }
            if(position== productObj.size()-1)
            {
                right.setVisibility(View.INVISIBLE);
            }
            else
            {
                right.setVisibility(View.VISIBLE);
            }
*/
            /*imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (prod_menulay.getVisibility() == View.GONE) {
                        prod_menulay.setVisibility(View.VISIBLE);
                    } else {
                        prod_menulay.setVisibility(View.GONE);
                    }
                }
            });*/


            imageView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View arg0, MotionEvent arg1) {

                    if (gestureDetector.onTouchEvent(arg1)) {
                        // single tap
                        if (descontent.getVisibility() == View.GONE) {
                            prod_menulay.setVisibility(View.VISIBLE);
                            descontent.setVisibility(View.VISIBLE);
                            toolbar.setVisibility(View.VISIBLE);

                           /* viewAnimationShow(descontent);
                            viewAnimationShow(prod_menulay);
                            viewAnimationShow(toolbar);*/
                            pref.edit().putString("hidden","no").apply();
                        } else {
                            descontent.setVisibility(View.GONE);
                            prod_menulay.setVisibility(View.GONE);
                            toolbar.setVisibility(View.GONE);

                            /*viewAnimationHide(descontent);
                            viewAnimationHide(prod_menulay);
                            viewAnimationHide(toolbar);*/
                            pref.edit().putString("hidden","yes").apply();
                        }
                        return true;
                    } else {
                        // your code for move and drag
                    }

                    return false;
                }
            });


            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tab = mViewPager.getCurrentItem();
                    if (tab > 0) {

                        tab--;
                        mViewPager.setCurrentItem(tab);
                    } else if (tab == 0) {

                        mViewPager.setCurrentItem(tab);
                    }
                }
            });

            // Images right navigatin
            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tab = mViewPager.getCurrentItem();
                    tab++;

                    mViewPager.setCurrentItem(tab);
                }
            });

            if (productObj.getProduct_likes() != null) {
                prod_likes.setText(productObj.getProduct_likes()+" likes");
            }

            //For Sku
            if(productObj.getSku()!=null && !productObj.getSku().equals("null") && !productObj.getSku().equals("")) {
                container_product_sku.setVisibility(View.VISIBLE);
                prod_sku.setText(productObj.getSku().toUpperCase());
            }else{
                container_product_sku.setVisibility(View.GONE);
            }


            if(productObj.getFabric()!=null && productObj.getFabric()!="") {
                prod_fabric.setVisibility(View.VISIBLE);
                fabric.setVisibility(View.VISIBLE);
                prod_fabric.setText(productObj.getFabric());
            }
            else
            {
                fabric.setVisibility(View.GONE);
                prod_fabric.setVisibility(View.GONE);
            }
            if(productObj.getWork()!=null && productObj.getWork()!="") {

                prod_work.setVisibility(View.VISIBLE);
                work.setVisibility(View.VISIBLE);
                prod_work.setText(productObj.getWork());
            }
            else
            {
                prod_work.setText("Not available");
                prod_work.setVisibility(View.GONE);
                work.setVisibility(View.GONE);
            }
            if(userInfo.getGroupstatus().equals("2"))
            {
                if(productObj.getSelling_price()!=null && !productObj.getSelling_price().equals("null") && !productObj.getSelling_price().equals("") && !productObj.getSelling_price().equals("0.00")  && !productObj.getSelling_price().equals("0.0")) {
                    prod_price.setVisibility(View.VISIBLE);
                    prod_price.setText("\u20B9" + productObj.getSelling_price());
                }else {
                    prod_price.setVisibility(View.GONE);

                }
            }
            else
            {
                if(productObj.getFinal_price()!=null && !productObj.getFinal_price().equals("null") && !productObj.getFinal_price().equals("") && !productObj.getSelling_price().equals("0.00") && !productObj.getSelling_price().equals("0.0")) {
                    prod_price.setVisibility(View.VISIBLE);
                    prod_price.setText("\u20B9" + productObj.getFinal_price());
                }else{
                    prod_price.setVisibility(View.GONE);
                }
            }
            //changed by abu
            //  prod_menulay.setVisibility(View.VISIBLE);
        /*    proddes_but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (descontent.getVisibility() == View.GONE) {
                        descontent.setVisibility(View.VISIBLE);
                    } else {
                        descontent.setVisibility(View.GONE);
                    }
                }
            });
            descontent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (descontent.getVisibility() == View.GONE) {
                        descontent.setVisibility(View.VISIBLE);
                    } else {
                        descontent.setVisibility(View.GONE);
                    }
                }
            });*/
            if (productObj.getProduct_like_id() != null) {
                like_but.setImageResource(R.drawable.ic_liked);
            }
            if (checkIfExistsSelection(productObj)) {
                prod_starbut.setBackgroundDrawable(ContextCompat.getDrawable(Activity_DisplayProduct.this,R.drawable.card_edge_rec_green));
                prod_starbut.setText("Selected");
            } else {
                prod_starbut.setBackgroundDrawable(ContextCompat.getDrawable(Activity_DisplayProduct.this,R.drawable.card_edge_rec_yellow));
                prod_starbut.setText("Select");
            }
            star_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (productObj.getFinal_price() != null && !productObj.getFinal_price().equals("null") && !productObj.getFinal_price().equals("") && !productObj.getSelling_price().equals("0.00") && !productObj.getSelling_price().equals("0.0")) {
                        addToSelection(productObj);
                        //   if (Activity_DisplayProduct.this.findViewById(R.id.appbar_new) != null) {
                        //        StaticFunctions.setUpselectedProdCounter(Activity_DisplayProduct.this, (Toolbar) Activity_DisplayProduct.this.findViewById(R.id.appbar_new));
                        //    }
                        if (checkIfExistsSelection(productObj)) {
                            prod_starbut.setBackgroundDrawable(ContextCompat.getDrawable(Activity_DisplayProduct.this,R.drawable.card_edge_rec_green));
                            prod_starbut.setText("Selected");
                        } else {
                            prod_starbut.setBackgroundDrawable(ContextCompat.getDrawable(Activity_DisplayProduct.this,R.drawable.card_edge_rec_yellow));
                            prod_starbut.setText("Select");
                        }
                        notifyDataSetChanged();
                    }else{
                        Toast.makeText(Activity_DisplayProduct.this,"Product with 0 price cannot be added to selection",Toast.LENGTH_LONG).show();
                    }
                }

            });
            like_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StaticFunctions.isOnline(mContext)) {
                        final MaterialDialog progressDialog = StaticFunctions.showProgress(mContext);
                        if (productObj.getProduct_like_id() != null) {
                            progressDialog.show();
                            NetworkProcessor.with(mContext)
                                    .load("DELETE", URLConstants.companyUrl(Activity_DisplayProduct.this,"productlike",productObj.getId()) + productObj.getProduct_like_id() + "/").addHeader("Authorization", StaticFunctions.getAuthString(pref.getString("key", "")))
                                    .asString().setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    if (e == null & result != null) {
                                        productObj.setProduct_like_id(null);
                                        int likes = 0;
                                        try {
                                            likes = Integer.parseInt(productObj.getProduct_likes());
                                        } catch (Exception ew) {

                                        }
                                        likes = likes - 1;
                                        productObj.setProduct_likes("" + likes);
                                        prod_likes.setText(likes + " likes");
                                        like_but.setImageResource(R.drawable.ic_fav);
                                        notifyDataSetChanged();
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                        } else {
                            progressDialog.show();
                            NetworkProcessor.with(mContext)
                                    .load(URLConstants.companyUrl(Activity_DisplayProduct.this,"productlike",productObj.getId())).addHeader("Authorization", StaticFunctions.getAuthString(pref.getString("key", ""))).setMultipartParameter("product", "" + productObj.getId())
                                    .asString().setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    if (e == null & result != null) {
                                        Log.v("res", "" + result);

                                        Response_like response_like = Application_Singleton.gson.fromJson(result, Response_like.class);
                                        if (response_like.getId() != null) {
                                            int likes = 0;
                                            try {
                                                likes = Integer.parseInt(productObj.getProduct_likes());
                                            } catch (Exception ew) {

                                            }
                                            likes = likes + 1;
                                            prod_likes.setText(likes + " likes");
                                            productObj.setProduct_likes("" + likes);
                                            productObj.setProduct_like_id(response_like.getId());
                                            like_but.setImageResource(R.drawable.ic_liked);
                                            notifyDataSetChanged();
                                        }
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    } else {
                        StaticFunctions.showNetworkAlert(mContext);
                    }

                }
            });

            container.addView(view);

            return view;
        }

        private boolean checkIfExistsSelection(ProductObj productObj) {
            boolean alreadyExists = false;
            Type listOfProductObj = new TypeToken<ArrayList<ProductObj>>() {
            }.getType();
            String previousProds = pref.getString("selectedProds", null);
            if (previousProds != null) {
                ArrayList<ProductObj> preseletedprods = Application_Singleton.gson.fromJson(previousProds, listOfProductObj);
                for (int i = 0; i < preseletedprods.size(); i++) {
                    if (preseletedprods.get(i).getId().equals(productObj.getId())) {
                        alreadyExists = true;
                    }
                }
            }
            return alreadyExists;
        }

        private boolean addToSelection(ProductObj productObj) {
            boolean alreadyExists = false;
            Type listOfProductObj = new TypeToken<ArrayList<ProductObj>>() {
            }.getType();
            String previousProds = pref.getString("selectedProds", null);
            ArrayList<ProductObj> preseletedprods;
            if (previousProds == null) {
                pref.edit().putString("selectedProds", Application_Singleton.gson.toJson(new ArrayList<>(), listOfProductObj)).apply();
                previousProds = pref.getString("selectedProds", null);
            }
            preseletedprods = Application_Singleton.gson.fromJson(previousProds, listOfProductObj);
            for (int i = 0; i < preseletedprods.size(); i++) {
                if (preseletedprods.get(i).getId().equals(productObj.getId())) {
                    alreadyExists = true;
                    preseletedprods.remove(i);
                }
            }
            if (alreadyExists) {
                pref.edit().putString("selectedProds", Application_Singleton.gson.toJson(preseletedprods, listOfProductObj)).apply();
            } else {
                preseletedprods.add(productObj);
                pref.edit().putString("selectedProds", Application_Singleton.gson.toJson(preseletedprods, listOfProductObj)).apply();

            }


            return !alreadyExists;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((FrameLayout) object);
        }
    }
    private void addPushId(ArrayList<String> pushid,ArrayList<String> pushProductid) {
        List pushIds_Products = loadPushId();
        if (pushIds_Products == null)
            pushIds_Products = new ArrayList();
        Activity_DisplayProduct.PostPushProductId postPushUserId = new Activity_DisplayProduct.PostPushProductId(pushid,pushProductid) ;
        pushIds_Products.add(postPushUserId);
        storePushId((ArrayList<String>) pushIds_Products);

    }

    private ArrayList loadPushId() {
        SharedPreferences pref;
        List push_id;
        pref = this.getSharedPreferences("wishbookprefs", this.MODE_PRIVATE);
        if (pref.contains("Push_Product_Id_List")) {
            String jsonFavorites = pref.getString("Push_Product_Id_List", null);
            Gson gson = new Gson();
            Activity_DisplayProduct.PostPushProductId[] pushIds = gson.fromJson(jsonFavorites,Activity_DisplayProduct.PostPushProductId[].class);
            push_id = Arrays.asList(pushIds);
            push_id = new ArrayList(push_id);
// add elements to al, including duplicates
            Set<String> hs = new HashSet<>();
            hs.addAll(push_id);
            push_id.clear();
            push_id.addAll(hs);
        } else
            return null;
        return (ArrayList) push_id;
    }

    private void storePushId(ArrayList<String> pushid) {
        SharedPreferences pref;
        SharedPreferences.Editor editor;
        pref = this.getSharedPreferences("wishbookprefs", this.MODE_PRIVATE);
        editor = pref.edit();
        Gson gson = new Gson();
        String jsonPushId = gson.toJson(pushid);
        editor.putString("Push_Product_Id_List", jsonPushId);
        editor.commit();
    }
    public class PostPushProductId {
        ArrayList<String> push_user;
        ArrayList<String> push_user_product;

        public PostPushProductId(ArrayList<String> push_user, ArrayList<String> push_user_product) {
            this.push_user = push_user;
            this.push_user_product = push_user_product;
        }

        public ArrayList<String> getPush_user() {
            return push_user;
        }

        public void setPush_user(ArrayList<String> push_user) {
            this.push_user = push_user;
        }

        public ArrayList<String> getPush_user_product() {
            return push_user_product;
        }

        public void setPush_user_product(ArrayList<String> push_user_product) {
            this.push_user_product = push_user_product;
        }
    }

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return true;
        }
    }

    public void viewAnimationHide(final View view)
    {
        view.animate()
                .translationY(view.getHeight())
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.INVISIBLE);
                    }
                });


    }
    public void viewAnimationShow(final View view)
    {
        view.animate()
                .translationY(view.getHeight())
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.VISIBLE);
                    }
                });


    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        StaticFunctions.setUpselectedProdCounter(Activity_DisplayProduct.this,toolbar);
        return super.onPrepareOptionsMenu(menu);
    }


}
