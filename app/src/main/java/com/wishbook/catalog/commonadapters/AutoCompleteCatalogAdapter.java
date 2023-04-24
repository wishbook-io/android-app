package com.wishbook.catalog.commonadapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;

import java.util.ArrayList;
import java.util.HashMap;


public class AutoCompleteCatalogAdapter extends ArrayAdapter<Response_catalogMini> implements Filterable {
    private ArrayList<Response_catalogMini> items = new ArrayList<>();
    private ArrayList<Response_catalogMini> itemsAll;
    private ViewHolder viewHolder;
    private HashMap<String, String> params;


    public String getSelectedObject() {
        return null;
    }

    private static class ViewHolder {
        private TextView itemView;
    }

    public AutoCompleteCatalogAdapter(Context context, int textViewResourceId, ArrayList<Response_catalogMini> items, HashMap<String, String> params) {
        super(context, textViewResourceId, items);
        this.items = items;
        if (items.size() > 0) {
            this.itemsAll = (ArrayList<Response_catalogMini>) items.clone();
        }
        this.params = params;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.spinneritem, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemView = (TextView) convertView.findViewById(R.id.spintext);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Response_catalogMini item = getItem(position);
        if (item != null) {
            viewHolder.itemView.setText(item.getTitle());
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((Response_catalogMini) (resultValue)).getTitle();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            // with brand and category
            // dependant on selected dropdown brand and dropdown category
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) getContext());
            final FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                String s = constraint.toString();
                params.put("title",s);
                HttpManager.getInstance((Activity) getContext()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getContext(), "catalog_dropdown", s) , params, headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        //change by abu
                        items.clear();
                        Response_catalogMini[] catalogMinis = new Gson().fromJson(response, Response_catalogMini[].class);
                        for (Response_catalogMini catalogMini : catalogMinis) {
                            items.add(catalogMini);
                        }
                        filterResults.values = items;
                        filterResults.count = items.size();
                        notifyDataSetChanged();
                        Log.d("AutoCompete", "onServerResponse result for items : " + items.size());
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        StaticFunctions.showResponseFailedDialog(error);
                    }
                });
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                Log.d("AutoCompete", "publish result " + results.count);
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
                Log.d("AutoCompete", "notifyDataSetInvalidated publish result " + results.count);
            }

        }
    };
}