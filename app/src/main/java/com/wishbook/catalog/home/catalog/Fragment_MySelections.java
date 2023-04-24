package com.wishbook.catalog.home.catalog;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.paginate.Paginate;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.SelectedProductsAdapter;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Response_Selection;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_MySelections extends GATrackedFragment implements Paginate.Callbacks {

    private View v;
    private RecyclerViewEmptySupport mRecyclerView;
    private MaterialDialog progressDialog;
    private ArrayList<Response_Selection> response_selections_list = new ArrayList<>();
    SelectedProductsAdapter selectedProductsAdapter;

    //Pagination Variable
    private static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Paginate paginate;
    int page = 0;

    public Fragment_MySelections() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        Loading = true;
        page=0;
        getMySelections(getActivity(), LIMIT, INITIAL_OFFSET, true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_selectedprods, ga_container, true);
        if(UserInfo.getInstance(getActivity()).isGuest()) {
            v.findViewById(R.id.linear_main_guest).setVisibility(View.VISIBLE);
            if(!UserInfo.getInstance(getActivity()).isUser_approval_status()) {
                View dialog = v.findViewById(R.id.linear_main_pending);
                dialog.setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.relative_dialog_action).setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.txt_positive).setVisibility(View.GONE);
                TextView textView = dialog.findViewById(R.id.txt_pending);
                textView.setText(String.format(getResources().getString(R.string.pending_permission_text),UserInfo.getInstance(getActivity()).getCompanyname()));
                StaticFunctions.resetClickListener(getActivity(),dialog);
            } else {
                View  dialog= v.findViewById(R.id.linear_dialog_register_root);
                dialog.setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.txt_later).setVisibility(View.GONE);
                StaticFunctions.registerClickListener(getActivity(),dialog,null);
            }
            return v;
        }


/*
        Activity_Home.tabs.setVisibility(View.VISIBLE);
*/
        mRecyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        v.findViewById(R.id.list_empty1).setVisibility(View.VISIBLE);

        if (paginate != null) {
            paginate.unbind();
        }





        selectedProductsAdapter = new SelectedProductsAdapter((AppCompatActivity) getActivity(), response_selections_list);
        mRecyclerView.setAdapter(selectedProductsAdapter);
        paginate = Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .build();
        Loading = false;
        selectedProductsAdapter.notifyDataSetChanged();
      /*  EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            public Boolean onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                    getMySelections(getActivity(), LIMIT, totalItemsCount);

                //  Toast.makeText(getActivity(),totalItemsCount+"",Toast.LENGTH_LONG).show();

                return true;
            }

            @Override
            public void stopLoading() {

            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);*/


        return v;
    }

    private void getMySelections(FragmentActivity activity, int limit, final int offset, Boolean progress) {

        HttpManager.METHOD methodType;
        if (progress) {
            if (getUserVisibleHint()) {
                Log.d(getClass().getName(), "Visible");
                methodType = HttpManager.METHOD.GETWITHPROGRESS;
                showProgress();
            } else {
                Log.d(getClass().getName(), "Invisible");
                methodType = HttpManager.METHOD.GET;
            }
        } else {
            methodType = HttpManager.METHOD.GET;
        }

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
        HttpManager.getInstance(activity).request(methodType, URLConstants.companyUrl(getActivity(), "selections_expand_false", "") + "?type=my" + "&&limit=" + limit + "&&offset=" + offset, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String result, boolean dataUpdated) {
                hideProgress();
                Log.v("sync response", result);
                Loading = false;
                Response_Selection[] response_selections = Application_Singleton.gson.fromJson(result, Response_Selection[].class);


                if (response_selections.length > 0) {

                    //checking if data updated on 2nd page or more
                    response_selections_list = (ArrayList<Response_Selection>) HttpManager.removeDuplicateIssue(offset, response_selections_list, dataUpdated, LIMIT);


                    for (int i = 0; i < response_selections.length; i++) {
                        response_selections_list.add(response_selections[i]);
                    }

                    page = (int) Math.ceil((double) response_selections_list.size() / LIMIT);


                    if (response_selections.length % LIMIT != 0) {
                        hasLoadedAllItems = true;
                    }

                    if (response_selections_list.size() <= LIMIT) {
                        selectedProductsAdapter.notifyDataSetChanged();
                    } else {
                        selectedProductsAdapter.notifyItemRangeInserted(selectedProductsAdapter.getItemCount(), response_selections_list.size() - 1);

                    }
                } else {
                    hasLoadedAllItems = true;
                    selectedProductsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }

    @Override
    public void onLoadMore() {
        Loading = true;
        if (page == 0) {
            getMySelections(getActivity(), LIMIT, INITIAL_OFFSET, true);
        } else {
            getMySelections(getActivity(), LIMIT, page * LIMIT, false);
        }
    }

    @Override
    public boolean isLoading() {
        return Loading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return hasLoadedAllItems;
    }
}
