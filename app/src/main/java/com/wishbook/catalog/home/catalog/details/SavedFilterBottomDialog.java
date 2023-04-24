package com.wishbook.catalog.home.catalog.details;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.ResponseSavedFilters;
import com.wishbook.catalog.home.adapters.SavedFiltersAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SavedFilterBottomDialog extends BottomSheetDialogFragment {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.empty_linear)
    LinearLayout empty_linear;

    @BindView(R.id.txt_clear_filter)
    TextView txt_clear_filter;

    @BindView(R.id.txt_bottom_sheet_title)
    TextView txt_bottom_sheet_title;

    @BindView(R.id.relative_bottom_progress)
    RelativeLayout relative_bottom_progress;

    String previous_selected_id = null;

    String catalog_type;

    LinearLayoutManager layoutManager;
    SavedFilterBottomDialog.SavedFilterSelectListener savedFilterSelectListener;


    public static SavedFilterBottomDialog newInstance(String string, boolean isNonCatalog) {
        SavedFilterBottomDialog f = new SavedFilterBottomDialog();
        if (string != null) {
            Bundle args = new Bundle();
            args.putString("previousSelected", string);
            if(isNonCatalog){
                args.putString("catalog_type",Constants.CATALOG_TYPE_NON);
            } else {
                args.putString("catalog_type",Constants.CATALOG_TYPE_CAT);
            }
            f.setArguments(args);
        } else {
            Bundle args = new Bundle();
            if(isNonCatalog){
                args.putString("catalog_type",Constants.CATALOG_TYPE_NON);
            } else {
                args.putString("catalog_type",Constants.CATALOG_TYPE_CAT);
            }
            f.setArguments(args);
        }
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.saved_filter_bottom_sheet_modal, container, false);
        ButterKnife.bind(this, v);
        initRecyclerview();

        Application_Singleton singleton = new Application_Singleton();
        singleton.trackScreenView(getClass().getSimpleName(), getActivity());


        if (getArguments() != null) {

            if(getArguments().getString("catalog_type")!=null) {
                txt_bottom_sheet_title.setText("My Filters");
                catalog_type = getArguments().getString("catalog_type");
            }
            if (getArguments().getString("previousSelected") != null) {
                previous_selected_id = getArguments().getString("previousSelected");
                txt_clear_filter.setVisibility(View.VISIBLE);

            } else
                txt_clear_filter.setVisibility(View.GONE);
        } else {
            txt_clear_filter.setVisibility(View.GONE);
        }
        getSavedFilter();
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        txt_clear_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                Intent intent = new Intent();
                getTargetFragment().onActivityResult(getTargetRequestCode(), 8000, intent);
            }
        });
        return v;
    }

    public void initRecyclerview() {
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public interface SavedFilterSelectListener {
        void onSavedChecked(String check);
    }

    public void setSavedFilterSelectListener(SavedFilterBottomDialog.SavedFilterSelectListener savedFilterSelectListener) {
        this.savedFilterSelectListener = savedFilterSelectListener;
    }

    public void getSavedFilter() {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = URLConstants.USER_SAVE_FILER;
        if(catalog_type!=null){
            url = URLConstants.USER_SAVE_FILER ;
        }
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    hideProgress();
                    if (isAdded() && !isDetached()) {
                        final ResponseSavedFilters[] responseSavedFilterses = new Gson().fromJson(response, ResponseSavedFilters[].class);
                        if (responseSavedFilterses.length > 0) {
                            empty_linear.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            final ArrayList<ResponseSavedFilters> responseSavedFilterses1 = new ArrayList<ResponseSavedFilters>(Arrays.asList(responseSavedFilterses));
                            Collections.reverse(responseSavedFilterses1);
                            SavedFiltersAdapter savedFiltersAdapter = new SavedFiltersAdapter(getActivity(), responseSavedFilterses1, SavedFilterBottomDialog.this, previous_selected_id);
                            recyclerView.setAdapter(savedFiltersAdapter);

                            // scroll to selected position, after some time
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < responseSavedFilterses1.size(); i++) {
                                        if (previous_selected_id != null) {
                                            if (responseSavedFilterses1.get(i).getId().equals(previous_selected_id)) {
                                                layoutManager.scrollToPosition(i);
                                            }
                                        }
                                    }
                                }
                            }, 500);

                        } else {
                            // show empty view;
                            empty_linear.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                empty_linear.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }


    public void showProgress() {
        relative_bottom_progress.setVisibility(View.VISIBLE);
    }

    public void showEmptyLinear() {
        empty_linear.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    public void hideProgress() {
        relative_bottom_progress.setVisibility(View.GONE);
    }
}
