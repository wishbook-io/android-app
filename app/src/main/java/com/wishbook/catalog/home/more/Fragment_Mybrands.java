package com.wishbook.catalog.home.more;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.BrandOwnAdapter;
import com.wishbook.catalog.commonadapters.BrandSellAdapter;
import com.wishbook.catalog.commonmodels.AddBrandDistributor;
import com.wishbook.catalog.commonmodels.MultiSelectModel;
import com.wishbook.catalog.commonmodels.PatchFlagBrand;
import com.wishbook.catalog.commonmodels.Response_BrandSell;
import com.wishbook.catalog.home.models.Response_Brands;
import com.wishbook.catalog.home.more.adapters.MultiSelectDialog;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_Mybrands extends GATrackedFragment implements SwipeRefreshLayout.OnRefreshListener {

    private View v;
    private RecyclerView mRecyclerViewOwn;
    private RecyclerView mRecyclerViewSell;
    private ImageView brand_sell_button;
    private ImageView brand_own_button;
    BrandOwnAdapter brandsOwnAdapter;
    BrandSellAdapter brandsSellAdapter;
    Response_BrandSell[] responseSell;
    ArrayList<Response_Brands> response_brandsArrayList = new ArrayList<Response_Brands>();
    ArrayList<Response_Brands> response_brandsSellArrayList = new ArrayList<Response_Brands>();
    final ArrayList<String> selectedbrandsList = new ArrayList<String>();
    final ArrayList<String> listToPost = new ArrayList<String>();
    ArrayList<Response_Brands> brandsList = new ArrayList<Response_Brands>();
    ArrayList<String> alreadySelected = new ArrayList<String>();
    private SharedPreferences pref;

    private SwipeRefreshLayout swipe_container;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    public Fragment_Mybrands() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
      //  super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_mybrands, ga_container, true);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.appbar);
        toolbar.setTitle("My Brands");
        toolbar.setNavigationIcon(getActivity().getResources().getDrawable(R.drawable.ic_toolbar_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        toolbar.setVisibility(View.GONE);
        mRecyclerViewOwn = (RecyclerView) v.findViewById(R.id.recycler_view_own);
        mRecyclerViewSell = (RecyclerView) v.findViewById(R.id.recycler_view_sell);
        brand_sell_button = (ImageView) v.findViewById(R.id.add_brands_sell);
        brand_own_button = (ImageView) v.findViewById(R.id.add_brands_own);

        pref = getActivity().getSharedPreferences(Constants.WISHBOOK_PREFS, getActivity().MODE_PRIVATE);
        brand_sell_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBrands();
            }
        });
        brand_own_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application_Singleton.CONTAINER_TITLE = "Add Brand";
                Application_Singleton.CONTAINERFRAG = new Fragment_AddBrandOwn();
                Fragment_Mybrands.this.startActivityForResult(new Intent(getActivity(), OpenContainer.class), 500);
                // StaticFunctions.switchActivity(getActivity(), OpenContainer.class);

            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewOwn.setLayoutManager(mLayoutManager);
        mRecyclerViewSell.setLayoutManager(mLayoutManager1);
        mRecyclerViewOwn.setNestedScrollingEnabled(false);
      //  mRecyclerViewOwn.setHasFixedSize(true);
        mRecyclerViewSell.setNestedScrollingEnabled(false);
      //  mRecyclerViewSell.setHasFixedSize(true);


        brandsOwnAdapter = new BrandOwnAdapter((AppCompatActivity) getActivity(), response_brandsArrayList);
        brandsSellAdapter = new BrandSellAdapter((AppCompatActivity) getActivity(), response_brandsSellArrayList);
        mRecyclerViewOwn.setAdapter(brandsOwnAdapter);
        mRecyclerViewSell.setAdapter(brandsSellAdapter);

        initSwipeRefresh(v);
        initCall(true);


        hideOrderDisableConfig();
        return v;
    }

    private void initCall(boolean isAllowCache) {
        getBrandsOwn(isAllowCache);
        getBrandsSell(isAllowCache);
    }

    private void getBrandsOwn(boolean isAllowCache) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "brands", "") + "?mycompany=true", null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    hideProgress();
                    response_brandsArrayList.clear();
                    Response_Brands[] response_brands = Application_Singleton.gson.fromJson(response, Response_Brands[].class);
                    if (response_brands.length > 0) {
                        for (int i = 0; i < response_brands.length; i++) {
                            response_brandsArrayList.add(response_brands[i]);
                        }
                        brandsOwnAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });

    }

    private void getBrandsSell(boolean isAllowCache) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "brands_distributor", "") + "?expand=true", null, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    hideProgress();
                    response_brandsSellArrayList.clear();
                    alreadySelected.clear();
                    responseSell = Application_Singleton.gson.fromJson(response, Response_BrandSell[].class);
                    if (responseSell != null && responseSell.length > 0) {
                        Response_Brands[] response_brands = responseSell[0].getBrands();
                        if (response_brands.length > 0) {
                            for (int i = 0; i < response_brands.length; i++) {
                                response_brandsSellArrayList.add(response_brands[i]);
                                alreadySelected.add(response_brands[i].getId());
                            }
                            brandsSellAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });

    }

    private void getBrands() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "brands", "") + "dropdown/?showall=true", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(final String response, boolean dataUpdated) {
                hideProgress();
                Log.v("sync response", response);
                Response_Brands[] response_brands = Application_Singleton.gson.fromJson(response, Response_Brands[].class);
                brandsList.clear();
                brandsList = new ArrayList<Response_Brands>();
                if (response_brands.length > 0 && isAdded() && isVisible() && !isStateSaved()) {
                    if (response_brands[0].getId() != null) {
                        for (int i = 0; i < response_brands.length; i++) {
                            //making all selected false for multiselect
                            response_brands[i].setSelected(false);
                            brandsList.add(response_brands[i]);
                        }

                        ArrayList<MultiSelectModel> model = new ArrayList<MultiSelectModel>();
                        //setting model for multiselect
                        for (int k = 0; k < brandsList.size(); k++) {
                            MultiSelectModel model1 = new MultiSelectModel((brandsList.get(k).getId()), brandsList.get(k).getName());
                            if (brandsList.get(k).getSelected()) {
                                Log.d("brand", brandsList.get(k).getName());
                            }
                            model.add(model1);
                        }

                        if (model.size() > 0) {
                            MultiSelectDialog multiSelectDialog = new MultiSelectDialog().title("Brand I Sell").preSelectIDsList(alreadySelected).multiSelectList(model);
                            multiSelectDialog.setCallbackListener(new MultiSelectDialog.Listener() {
                                @Override
                                public void onDismiss(ArrayList<String> ids, String data, String[] arrayNames) {
                                    //will return list of selected IDS
                                    //will return list of selected IDS
                                    if (responseSell != null && responseSell.length > 0) {
                                        Log.i("TAG", "onDismiss: Patch ==> " + ids.toString());
                                        PatchDistibuter(responseSell[0].getId(), ids);
                                    } else {
                                        Log.i("TAG", "onDismiss: Add ==> " + ids.toString());
                                        AddDistibuter(ids);
                                    }
                                }
                            });
                            multiSelectDialog.show(getActivity().getSupportFragmentManager(), "share");
                        }
                    }
                }

            }


            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }

    private void PatchDistibuter(String id, ArrayList<String> ids) {
        AddBrandDistributor addBrandDistributor = new AddBrandDistributor(ids);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        Gson gson = new Gson();
        HttpManager.getInstance(getActivity()).methodPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "brands_distributor", "") + id + "/", gson.fromJson(gson.toJson(addBrandDistributor), JsonObject.class), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Toast.makeText(getActivity(), "Changes Applied Successfully", Toast.LENGTH_LONG).show();
                HttpManager.getInstance(getActivity()).removeCacheParams(URLConstants.companyUrl(getActivity(), "brands", "") + "?showall=true", null);
                getBrandsSell(false);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void AddDistibuter(ArrayList<String> ids) {
        pref.edit().putString("brandadded", "yes").apply();
        AddBrandDistributor addBrandDistributor = new AddBrandDistributor(pref.getString("company_id", ""), ids);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        Gson gson = new Gson();
        HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "brands_distributor", ""), gson.fromJson(gson.toJson(addBrandDistributor), JsonObject.class), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                //  StaticFunctions.switchActivity(getActivity(), SplashScreen.class);
                PatchFlagBrand patch = new PatchFlagBrand("yes");
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                Gson gson = new Gson();
                HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.COMPANY_URL + pref.getString("company_id", "") + '/', gson.fromJson(gson.toJson(patch), JsonObject.class), headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Toast.makeText(getActivity(), "Brand added successfully", Toast.LENGTH_SHORT).show();
                        HttpManager.getInstance(getActivity()).removeCacheParams(URLConstants.companyUrl(getActivity(), "brands", "") + "?showall=true", null);
                        getBrandsSell(false);
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {

                    }
                });
                // progressDialog.dismiss();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().setResult(Activity.RESULT_OK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 500 && resultCode == Activity.RESULT_OK) {
            // Brand I Own Added so refresh list

            getBrandsOwn(false);
        }
    }



    /**
     * Temp Function for Hide Order Disable Config
     */
    public void hideOrderDisableConfig() {
        // Hide + button for Brands I Own
        // Hide + button for Brands I Sell
        if(StaticFunctions.checkOrderDisableConfig(getActivity())) {
            brand_sell_button.setVisibility(View.GONE);
            brand_own_button.setVisibility(View.GONE);
        }

    }


    public void initSwipeRefresh(View view) {
        swipe_container = view.findViewById(R.id.swipe_container);
        swipe_container.setOnRefreshListener(this);
        swipe_container.setColorScheme(android.R.color.holo_blue_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onRefresh() {
        runnable = new Runnable() {

            @Override
            public void run() {
                isAllowCache = false;
                swipe_container.setRefreshing(false);
                initCall(false);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }
}
