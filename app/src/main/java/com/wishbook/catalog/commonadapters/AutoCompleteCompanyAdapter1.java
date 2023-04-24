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
import com.wishbook.catalog.login.models.CompanyList;

import java.util.ArrayList;
import java.util.HashMap;

public class AutoCompleteCompanyAdapter1 extends ArrayAdapter<CompanyList> implements Filterable {
    private ArrayList<CompanyList> items = new ArrayList<>();
    private ArrayList<CompanyList> itemsAll;
    private ViewHolder viewHolder;
    private HashMap<String, String> params;


    public String getSelectedObject() {
        return null;
    }

    private static class ViewHolder {
        private TextView itemView;
    }

    public AutoCompleteCompanyAdapter1(Context context, int textViewResourceId, ArrayList<CompanyList> items, HashMap<String, String> params) {
        super(context, textViewResourceId, items);
        this.items = items;
        if (items.size() > 0) {
            this.itemsAll = (ArrayList<CompanyList>) items.clone();
        }
        this.params = params;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.company_suggestion_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemView = (TextView) convertView.findViewById(R.id.spintext);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CompanyList item = getItem(position);
        if (item != null) {
            viewHolder.itemView.setText(item.getName());
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
            String str = ((CompanyList) (resultValue)).getName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                String s = constraint.toString();
                params.put("name", s);
                Log.d("AutoCompete", "getBuyers called with " + s);
                HttpManager.getInstance((Activity) getContext()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getContext(), "companylist", ""), params, null, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        //change by abu
                        items.clear();
                        CompanyList[] companyLists = new Gson().fromJson(response, CompanyList[].class);
                        for (CompanyList companyList : companyLists) {
                            items.add(companyList);
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
          /*  ArrayList<BuyersList> filteredList = (ArrayList<BuyersList>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (BuyersList c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }*/

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