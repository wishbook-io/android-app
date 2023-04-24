package com.wishbook.catalog.home.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.ResponseSavedFilters;
import com.wishbook.catalog.home.catalog.details.SavedFilterBottomDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SavedFiltersAdapter extends RecyclerView.Adapter<SavedFiltersAdapter.CustomViewHolder> {


    Context context;
    ArrayList<ResponseSavedFilters> responseSavedFilterses;
    Type type;
    Fragment fragment;
    String previous_selected_id = null;

    public SavedFiltersAdapter(Context context, ArrayList<ResponseSavedFilters> responseSavedFilterses, Fragment fragment, String previous_selected_id) {
        this.context = context;
        this.responseSavedFilterses = responseSavedFilterses;
        this.fragment = fragment;
        this.previous_selected_id = previous_selected_id;
        type = new TypeToken<HashMap<String, String>>() {
        }.getType();

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_filter_item, parent, false);
        return new SavedFiltersAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {

        final ResponseSavedFilters filter = responseSavedFilterses.get(position);
        holder.switch_subscription.setOnCheckedChangeListener(null);

        holder.radioButton.setText(filter.getTitle());

        if (filter.getSub_text() != null && !filter.getSub_text().isEmpty()) {
            holder.txt_subtext.setText(filter.getSub_text());
        } else {
            holder.txt_subtext.setVisibility(View.GONE);
        }

        holder.switch_subscription.setChecked(filter.isSubscribed());


        // holder.radioButton.setOnCheckedChangeListener(null);
        if (previous_selected_id != null) {
            if (previous_selected_id.equals(filter.getId())) {
                holder.radioButton.setChecked(true);
                holder.radioButton.setTextColor(ContextCompat.getColor(context, R.color.color_primary));
            }
        }

        holder.linear_saved_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.radioButton.setChecked(true);
                Application_Singleton.trackEvent("Saved Filter", "Click", filter.getSub_text());
                HashMap<String, String> otherParam = new Gson().fromJson(filter.getParams(), type);
                otherParam.put("saved_filter_id", filter.getId());
                Intent intent = new Intent()
                        .putExtra("parameters", otherParam).putExtra("selected_saved_filter", filter);
                fragment.getTargetFragment().onActivityResult(fragment.getTargetRequestCode(), Activity.RESULT_OK, intent);
                if (fragment instanceof SavedFilterBottomDialog)
                    ((SavedFilterBottomDialog) fragment).dismiss();
            }
        });


        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(context)
                        .title("Delete Saved Filter")
                        .content("Are you sure you want to delete filter?")
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                                callDeleteSavedFilter(filter.getId(), position);
                            }
                        })
                        .negativeText("Cancel")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });

        holder.switch_subscription.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isSubscribe) {
                callSavedFiterSubscription(filter.getId(), position, isSubscribe);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (responseSavedFilterses.size() == 0) {
            if (fragment instanceof SavedFilterBottomDialog) {
                ((SavedFilterBottomDialog) fragment).showEmptyLinear();
                ((SavedFilterBottomDialog) fragment).dismiss();
            }
        }
        return responseSavedFilterses.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.radio_btn)
        RadioButton radioButton;

        @BindView(R.id.txt_subtext)
        TextView txt_subtext;

        @BindView(R.id.img_delete)
        ImageView img_delete;

        @BindView(R.id.linear_saved_item)
        LinearLayout linear_saved_item;

        @BindView(R.id.linear_subscription)
        LinearLayout linear_subscription;

        @BindView(R.id.switch_subscription)
        Switch switch_subscription;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void callDeleteSavedFilter(String deleteid, final int position) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.DELETEWITHPROGRESS, URLConstants.USER_SAVE_FILER + deleteid + "/", null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    responseSavedFilterses.remove(position);
                    notifyItemRemoved(position);
                    Intent intent = new Intent();
                    if (fragment instanceof SavedFilterBottomDialog)
                        fragment.getTargetFragment().onActivityResult(fragment.getTargetRequestCode(), 8000, intent);
                    updateSavedFilter(); // for network update
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void updateSavedFilter() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.USER_SAVE_FILER, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {

            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });

    }

    public void callSavedFiterSubscription(String savedFilterID, final int position, boolean isSubscription) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        ResponseSavedFilters responseSavedFilters = new ResponseSavedFilters();
        responseSavedFilters.setSubscribed(isSubscription);
        JsonObject jsonObject = new Gson().fromJson(new Gson().toJson(responseSavedFilters), JsonObject.class);
        String url = URLConstants.USER_SAVE_FILER + savedFilterID + "/";
        final MaterialDialog progress_dialog = StaticFunctions.showProgress(context);
        progress_dialog.show();
        HttpManager.getInstance((Activity) context).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, url, jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if(progress_dialog!=null) {
                    progress_dialog.dismiss();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if(progress_dialog!=null) {
                    progress_dialog.dismiss();
                }
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }
}
