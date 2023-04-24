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
import com.wishbook.catalog.Utils.networking.NetworkProcessor;
import com.wishbook.catalog.Utils.networking.Response;
import com.wishbook.catalog.Utils.networking.async.future.FutureCallback;
import com.wishbook.catalog.home.models.BuyersList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 20/9/16.
 */
public class AutoCompleteCommonAdapter extends ArrayAdapter<BuyersList> implements Filterable {
    private ArrayList<BuyersList> suggestions = new ArrayList<>();
    private ArrayList<BuyersList> buyerslist = new ArrayList<>();
    private ArrayList<BuyersList> itemsAll;
    private ArrayList<BuyersList> items = new ArrayList<>();
    private ViewHolder viewHolder;
    private String url;


    public String getSelectedObject() {
        return null;
    }

    private static class ViewHolder {
        private TextView itemView;
    }

    public AutoCompleteCommonAdapter(Context context, int textViewResourceId, ArrayList<BuyersList> items,String url) {
        super(context, textViewResourceId, items);
        this.items = items;
        if (items.size() > 0) {
            this.itemsAll = (ArrayList<BuyersList>) items.clone();
        }
        this.url = url;
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

        BuyersList item = getItem(position);
        if (item != null) {
            viewHolder.itemView.setText(item.getCompany_name());
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
            String str = ((BuyersList) (resultValue)).getCompany_name();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                suggestions.clear();
                String s = constraint.toString();
                // itemsAll = (ArrayList<BuyersList>) items.clone();
                /*for (BuyersList buyer : itemsAll) {
                    if (buyer.getCompany_name().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(buyer);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();*/
                HashMap<String, String> params = new HashMap<>();
                HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) getContext());
                if(url.equals("buyerlist_no_deputed")){
                    params.put("without_deputed","true");
                }
                params.put("status", "approved");
                params.put("search",s);
                Log.d("AutoCompete", "getBuyers called with " + s);
                HttpManager.getInstance((Activity) getContext()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getContext(),"buyerlist", ""), params, headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        //change by abu
                        items.clear();
                        BuyersList[] buyers = new Gson().fromJson(response, BuyersList[].class);
                        for (BuyersList buyerList : buyers) {
                            items.add(buyerList);
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

              //  getSyncBuyers(getContext(),constraint.toString().toLowerCase());


               // Log.d("AutoCompete", "filterResults result " + filterResults);
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

    private ArrayList<BuyersList>  getSyncBuyers(final Context context, final String s) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        NetworkProcessor.with(context).load("GET", URLConstants.companyUrl(context, "buyerlist", "")).addHeaders(convertMap(headers)).asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                items.clear();
                BuyersList[] buyers = new Gson().fromJson(result.getResult(), BuyersList[].class);
                for (BuyersList buyerList : buyers) {
                    items.add(buyerList);
                    notifyDataSetChanged();
                }
                Log.d("AutoCompete", "onServerResponse result for "+ s + "items : " + items.size());
            }
        });
        return items;
    }

    private void getBuyers(final Context context, final String s) {
        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        params.put("status", "approved");
        params.put("search",s);
        Log.d("AutoCompete", "getBuyers called with " + s);
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GET, URLConstants.companyUrl(context, "buyerlist", ""), params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                //change by abu
                items.clear();
                BuyersList[] buyers = new Gson().fromJson(response, BuyersList[].class);
                for (BuyersList buyerList : buyers) {
                    items.add(buyerList);
                }
                Log.d("AutoCompete", "onServerResponse result for "+ s + "items : " + items.size());
            }

            @Override
            public void onResponseFailed(ErrorString error) {
              StaticFunctions.showResponseFailedDialog(error);
            }
        });

    }

    public Map<String, List<String>> convertMap(HashMap params) {
        Map<String, List<String>> paramstopost = new HashMap<>();
        if (params != null) {
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                ArrayList<String> stringMap = new ArrayList<>();
                stringMap.add(pair.getValue().toString());
                paramstopost.put(pair.getKey().toString(), stringMap);
                it.remove();
            }
        }
        return paramstopost;
    }
}