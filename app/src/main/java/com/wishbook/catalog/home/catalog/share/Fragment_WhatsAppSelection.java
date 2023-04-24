package com.wishbook.catalog.home.catalog.share;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.ShareImageDownloadUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.WhatsappAdapterChoose;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.home.models.ProductObj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Fragment_WhatsAppSelection extends GATrackedFragment {

    private RecyclerView
            mRecyclerView;
    private View v;
    private ProductObj[] productObjs;
    private WhatsappAdapterChoose allItemAdapter;
    private FloatingActionButton floatmenu;
    private String shareText;
    private String whatsapptype;
    public Fragment_WhatsAppSelection() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.whatshare, container, false);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.appbar);
        toolbar.setTitle("WhatsApp (Pick 30 images to share)");

        toolbar.setNavigationIcon(getActivity().getResources().getDrawable(R.drawable.ic_toolbar_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        floatmenu = (FloatingActionButton) v.findViewById(R.id.wap_item);

        toolbar.setVisibility(View.GONE);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(layoutManager);
        if(getArguments().getString("sharetext")!=null)
        {
            shareText = getArguments().getString("sharetext").toString();
        }
        if(Application_Singleton.selectedshareCatalog!=null){
            showCatalogs();
        }
        return v;
    }
    private void showCatalogs() {
        final String catalog = Application_Singleton.selectedshareCatalog.getId();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

                HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS,URLConstants.companyUrl(getActivity(),"catalogs_expand_true_id",catalog), null, headers, true, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        Log.v("cached response", response);
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Log.v("sync response", response);
                        final Response_catalog response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                        productObjs =response_catalog.getProducts().toArray(new ProductObj[response_catalog.getProducts().size()]);
                        ArrayList<ProductObj> list = new ArrayList<ProductObj>(Arrays.asList(productObjs));
                        allItemAdapter = new WhatsappAdapterChoose((AppCompatActivity) getActivity(), list);
                        mRecyclerView.setAdapter(allItemAdapter);
                        floatmenu.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ArrayList<ProductObj> productObjs1 = new ArrayList<ProductObj>();
                                ArrayList allsel = allItemAdapter.getAllSelected();
                                for (Object sel : allsel) {

                                    if (sel instanceof ProductObj) {
                                       productObjs1.add((ProductObj) sel);
                                    }
                                }
                                ProductObj[] productObjs2 = new ProductObj[productObjs1.size()];
                                productObjs1.toArray(productObjs2);
                                try {
                                    if (getArguments().getString("type", "").equals("whatsapp")) {
                                        ShareImageDownloadUtils.shareProducts((AppCompatActivity) getActivity(), Fragment_WhatsAppSelection.this, productObjs2, StaticFunctions.SHARETYPE.WHATSAPP, shareText,response_catalog.getTitle(),false,false);
                                    } else if (getArguments().getString("type", "").equals("whatsappb")) {
                                        ShareImageDownloadUtils.shareProducts((AppCompatActivity) getActivity(),Fragment_WhatsAppSelection.this, productObjs2, StaticFunctions.SHARETYPE.WHATSAPP_BUSINESS, shareText,response_catalog.getTitle(),false,false);
                                    } else {
                                        ShareImageDownloadUtils.shareProducts((AppCompatActivity) getActivity(),Fragment_WhatsAppSelection.this, productObjs2, StaticFunctions.SHARETYPE.WHATSAPP, shareText,response_catalog.getTitle(),false,false);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {

                    }
                });
            }




}
