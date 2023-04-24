package com.wishbook.catalog.home.catalog.details;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;


import com.wishbook.catalog.GATrackedFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DownloadImage;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.networking.NetworkProcessor;
import com.wishbook.catalog.Utils.networking.async.future.FutureCallback;
import com.wishbook.catalog.Utils.touchImageView.ExtendedViewPager;
import com.wishbook.catalog.Utils.touchImageView.TouchImageView;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Response_like;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.models.ProductObj;


public class Fragment_ProductsInCatalog extends GATrackedFragment {

    private View v;
    private UserInfo userInfo;
    private RecyclerViewEmptySupport mRecyclerView;
    private ExtendedViewPager mViewPager;
    private Toolbar toolbar;
    private String selectedId="";
    private int position=0;
    private TextView noProductsDisplay;
    public Fragment_ProductsInCatalog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_products_in_catalog, container, false);
        toolbar = (Toolbar) v.findViewById(R.id.appbar);
        noProductsDisplay = (TextView) v.findViewById(R.id.textNoProducts);
        userInfo = UserInfo.getInstance(getActivity());

      /*  if(getArguments()!=null) {
            selectedId=getArguments().getString("selectedId", "");
            for (int i = 0; i < Application_Singleton.selectedCatalogProducts.size(); i++) {
                if( Application_Singleton.selectedCatalogProducts.get(i).getId().equals(selectedId)){
                    Application_Singleton.selectedCatalogProducts.set(0,Application_Singleton.selectedCatalogProducts.get(i));
                }
            }
        }
*/      //changed by abu
        try{
            selectedId = getArguments().getString("position", "0");
            position = Integer.parseInt(selectedId);
        }
        catch(Exception e)
        {
            position=0;
        }


        mViewPager = (ExtendedViewPager) v.findViewById(R.id.pager);
        mViewPager.setAdapter(new CustomPagerAdapter(getActivity()));
         mViewPager.setCurrentItem(position);
      //  mViewPager.setPageTransformer(true, new CubeOutTransformer());
        toolbar.setVisibility(View.GONE);
        if(Application_Singleton.selectedCatalogProducts.size()==0)
        {
            noProductsDisplay.setVisibility(View.VISIBLE);
        }
        return v;
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
            return Application_Singleton.selectedCatalogProducts.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = mLayoutInflater.inflate(R.layout.pager_item, container, false);
            ImageButton proddes_but = (ImageButton) view.findViewById(R.id.proddes_but);
            final LinearLayout prod_menulay = (LinearLayout) view.findViewById(R.id.subproductmenu);
            final LinearLayout star_container = (LinearLayout) view.findViewById(R.id.star_container);
            final LinearLayout like_container = (LinearLayout) view.findViewById(R.id.like_container);
            final TextView prod_title = (TextView) view.findViewById(R.id.prod_title);
            final TextView prod_catalog = (TextView) view.findViewById(R.id.prod_catalog);
            final TextView prod_brand = (TextView) view.findViewById(R.id.prod_brand);
            final TextView prod_likes = (TextView) view.findViewById(R.id.prod_likes);
            final TextView prod_fabric = (TextView) view.findViewById(R.id.prod_fabric);
            final TextView prod_work = (TextView) view.findViewById(R.id.prod_work);
            final LinearLayout prod_fabric1 = (LinearLayout) view.findViewById(R.id.prod_fabric1);
            final LinearLayout prod_work1 = (LinearLayout) view.findViewById(R.id.prod_work1);
            final LinearLayout fabric = (LinearLayout) view.findViewById(R.id.fabric);
            final LinearLayout work = (LinearLayout) view.findViewById(R.id.work);
            final ImageView like_but = (ImageView) view.findViewById(R.id.like_but);
            final ImageView prod_starbut = (ImageView) view.findViewById(R.id.prod_starbut);
            final TextView prod_price = (TextView) view.findViewById(R.id.prod_price);
            final RelativeLayout descontent = (RelativeLayout) view.findViewById(R.id.descontent);
            final ImageButton left = (ImageButton) view.findViewById(R.id.left_nav);
            final ImageButton right = (ImageButton) view.findViewById(R.id.right_nav);


            TouchImageView imageView = (TouchImageView) view.findViewById(R.id.prod_img);
            String image = Application_Singleton.selectedCatalogProducts.get(position).getImage().getThumbnail_medium();
            if (image != null & !image.equals("")) {
                new DownloadImage(imageView).execute(image);
                //StaticFunctions.loadImage(mContext,image,imageView,R.drawable.uploadempty);
                //StaticFunctions.loadFresco(mContext,image,imageView);
               // Picasso.with(mContext).load(image).into(imageView);
            }
            //added by abu
            if(StaticFunctions.isOnline(getActivity()))
            {
                if(Application_Singleton.selectedCatalogProducts.get(position).getPush_user_product_id()!=null)
                {
                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                    ArrayList<String> pushid= new ArrayList<String>();
                    ArrayList<String> pushProductid= new ArrayList<String>();
                    pushProductid.add(Application_Singleton.selectedCatalogProducts.get(position).getPush_user_product_id().toString());
                    final PostPushProductId postPushUserId = new PostPushProductId(pushid,pushProductid) ;
                    HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECT, URLConstants.companyUrl(getActivity(),"syncactivitylog",""), new Gson().fromJson(new Gson().toJson(postPushUserId), JsonObject.class), headers, true, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {
                            Log.v("cached response", response);
                            onServerResponse(response, false);
                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            Log.v("sync response", response);
                            Log.v("Done","PostingProduct");
                            Log.v("Done", String.valueOf(postPushUserId.getPush_user_product()));
                        }


                        @Override
                        public void onResponseFailed(ErrorString error) {

                        }
                    });
                }

            }
            else
            {
                if(Application_Singleton.selectedCatalogProducts.get(position).getPush_user_product_id()!=null)
                {
                    //added by abu
                    ArrayList<String> pushid= new ArrayList<String>();
                    ArrayList<String> pushProductid= new ArrayList<String>();
                    pushProductid.add(Application_Singleton.selectedCatalogProducts.get(position).getPush_user_product_id().toString());
                    addPushId(pushid,pushProductid);

                }
            }
            //changed by abu
            if(position==0)
            {
                left.setVisibility(View.INVISIBLE);
            }
            else
            {
                left.setVisibility(View.VISIBLE);
            }
            if(position== Application_Singleton.selectedCatalogProducts.size()-1)
            {
                right.setVisibility(View.INVISIBLE);
            }
            else
            {
                right.setVisibility(View.VISIBLE);
            }

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

            if (Application_Singleton.selectedCatalogProducts.get(position).getProduct_likes() != null) {
                prod_likes.setText(Application_Singleton.selectedCatalogProducts.get(position).getProduct_likes());
            }
            prod_title.setText(Application_Singleton.selectedCatalogProducts.get(position).getTitle());
            if(Application_Singleton.selectedCatalogProducts.get(position).getFabric()!=null) {
                prod_fabric1.setVisibility(View.VISIBLE);
                prod_fabric.setVisibility(View.VISIBLE);
                fabric.setVisibility(View.VISIBLE);
                prod_fabric.setText(Application_Singleton.selectedCatalogProducts.get(position).getFabric());
            }
            else
            {
                fabric.setVisibility(View.GONE);
                prod_fabric.setVisibility(View.GONE);
                prod_fabric1.setVisibility(View.GONE);
            }
            if(Application_Singleton.selectedCatalogProducts.get(position).getWork()!=null) {

                prod_work.setVisibility(View.VISIBLE);
                prod_work1.setVisibility(View.VISIBLE);
                work.setVisibility(View.VISIBLE);
                prod_work.setText(Application_Singleton.selectedCatalogProducts.get(position).getWork());
            }
            else
            {
                prod_work.setText("Not available");
                prod_work.setVisibility(View.GONE);
                prod_work1.setVisibility(View.GONE);
                work.setVisibility(View.GONE);
            }
            if(userInfo.getGroupstatus().equals("2"))
            {
                prod_price.setText("\u20B9" + Application_Singleton.selectedCatalogProducts.get(position).getSelling_price());
            }
            else
            {
                prod_price.setText("\u20B9" + Application_Singleton.selectedCatalogProducts.get(position).getFinal_price());
            }
            if (Application_Singleton.selectedCatalogProducts.get(position).getCatalog() != null) {
                if (Application_Singleton.selectedCatalogProducts.get(position).getCatalog()!=null) {
                    prod_catalog.setText(Application_Singleton.selectedCatalogProducts.get(position).getCatalog().getTitle());
                    prod_brand.setText(Application_Singleton.selectedCatalogProducts.get(position).getCatalog().getBrand().getName());
                }
            }
            //changed by abu
            prod_menulay.setVisibility(View.VISIBLE);
            descontent.setVisibility(View.GONE);
            proddes_but.setOnClickListener(new View.OnClickListener() {
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
            });
            if (Application_Singleton.selectedCatalogProducts.get(position).getProduct_like_id() != null) {
                like_but.setImageResource(R.drawable.ic_favsel);
            }
            if (checkIfExistsSelection(Application_Singleton.selectedCatalogProducts.get(position))) {
                prod_starbut.setImageResource(R.drawable.ic_star);
            } else {
                prod_starbut.setImageResource(R.drawable.star_outline);
            }
            star_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToSelection(Application_Singleton.selectedCatalogProducts.get(position));
                    if (getActivity().findViewById(R.id.toolbar) != null) {
                        StaticFunctions.setUpselectedProdCounter((AppCompatActivity) getActivity(), (Toolbar) getActivity().findViewById(R.id.toolbar));
                    }
                    if (checkIfExistsSelection(Application_Singleton.selectedCatalogProducts.get(position))) {
                        prod_starbut.setImageResource(R.drawable.ic_star);
                    } else {
                        prod_starbut.setImageResource(R.drawable.star_outline);
                    }
                    notifyDataSetChanged();
                }
            });
            like_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StaticFunctions.isOnline(mContext)) {
                        final MaterialDialog progressDialog = StaticFunctions.showProgress(mContext);
                        if (Application_Singleton.selectedCatalogProducts.get(position).getProduct_like_id() != null) {
                            progressDialog.show();
                            NetworkProcessor.with(mContext)
                                    .load("DELETE",URLConstants.companyUrl(getActivity(),"productlike",Application_Singleton.selectedCatalogProducts.get(position).getId()) + Application_Singleton.selectedCatalogProducts.get(position).getProduct_like_id() + "/").addHeader("Authorization", StaticFunctions.getAuthString(Activity_Home.pref.getString("key", "")))
                                    .asString().setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    if (e == null & result != null) {
                                        Application_Singleton.selectedCatalogProducts.get(position).setProduct_like_id(null);
                                        int likes = 0;
                                        try {
                                            likes = Integer.parseInt(Application_Singleton.selectedCatalogProducts.get(position).getProduct_likes());
                                        } catch (Exception ew) {

                                        }
                                        likes = likes - 1;
                                        Application_Singleton.selectedCatalogProducts.get(position).setProduct_likes("" + likes);
                                        prod_likes.setText("" + likes);
                                        like_but.setImageResource(R.drawable.ic_fav);
                                        notifyDataSetChanged();
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                        } else {
                            progressDialog.show();
                            NetworkProcessor.with(mContext)
                                    .load(URLConstants.companyUrl(getActivity(),"productlike",Application_Singleton.selectedCatalogProducts.get(position).getId())).addHeader("Authorization", StaticFunctions.getAuthString(Activity_Home.pref.getString("key", ""))).setMultipartParameter("product", "" + Application_Singleton.selectedCatalogProducts.get(position).getId())
                                    .asString().setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    if (e == null & result != null) {
                                        Log.v("res", "" + result);

                                        Response_like response_like = Application_Singleton.gson.fromJson(result, Response_like.class);
                                        if (response_like.getId() != null) {
                                            int likes = 0;
                                            try {
                                                likes = Integer.parseInt(Application_Singleton.selectedCatalogProducts.get(position).getProduct_likes());
                                            } catch (Exception ew) {

                                            }
                                            likes = likes + 1;
                                            prod_likes.setText("" + likes);
                                            Application_Singleton.selectedCatalogProducts.get(position).setProduct_likes("" + likes);
                                            Application_Singleton.selectedCatalogProducts.get(position).setProduct_like_id(response_like.getId());
                                            like_but.setImageResource(R.drawable.ic_favsel);
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
            String previousProds = Activity_Home.pref.getString("selectedProds", null);
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
            String previousProds = Activity_Home.pref.getString("selectedProds", null);
            ArrayList<ProductObj> preseletedprods;
            if (previousProds == null) {
                Activity_Home.pref.edit().putString("selectedProds", Application_Singleton.gson.toJson(new ArrayList<>(), listOfProductObj)).apply();
                previousProds = Activity_Home.pref.getString("selectedProds", null);
            }
            preseletedprods = Application_Singleton.gson.fromJson(previousProds, listOfProductObj);
            for (int i = 0; i < preseletedprods.size(); i++) {
                if (preseletedprods.get(i).getId().equals(productObj.getId())) {
                    alreadyExists = true;
                    preseletedprods.remove(i);
                }
            }
            if (alreadyExists) {
                Activity_Home.pref.edit().putString("selectedProds", Application_Singleton.gson.toJson(preseletedprods, listOfProductObj)).apply();
            } else {
                preseletedprods.add(productObj);
                Activity_Home.pref.edit().putString("selectedProds", Application_Singleton.gson.toJson(preseletedprods, listOfProductObj)).apply();

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
        PostPushProductId postPushUserId = new PostPushProductId(pushid,pushProductid) ;
        pushIds_Products.add(postPushUserId);
        storePushId((ArrayList<String>) pushIds_Products);

    }

    private ArrayList loadPushId() {
        SharedPreferences pref;
        List push_id;
        pref = getActivity().getSharedPreferences("wishbookprefs", getActivity().MODE_PRIVATE);
        if (pref.contains("Push_Product_Id_List")) {
            String jsonFavorites = pref.getString("Push_Product_Id_List", null);
            Gson gson = new Gson();
            PostPushProductId[] pushIds = gson.fromJson(jsonFavorites,PostPushProductId[].class);
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
        pref = getActivity().getSharedPreferences("wishbookprefs", getActivity().MODE_PRIVATE);
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
}
