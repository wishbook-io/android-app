package com.wishbook.catalog.login.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import com.wishbook.catalog.R;
import com.wishbook.catalog.login.models.CompanyList;

public class AutoCompleteCompanyAdapter extends ArrayAdapter<CompanyList> implements Filterable {
    private Filter filter = new CustomFilter();
    private ArrayList<CompanyList> suggestions = new ArrayList<>();

    private ArrayList<CompanyList> items=new ArrayList<>();
    private ViewHolder viewHolder;

    public String getSelectedObject() {
        return null;
    }

    private static class ViewHolder {
        private TextView itemView;
    }


    public AutoCompleteCompanyAdapter(Context context, int textViewResourceId, ArrayList<CompanyList> items) {
        super(context, textViewResourceId, items);
        this.items=items;
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

        CompanyList item = getItem(position);
        if (item!= null) {
            viewHolder.itemView.setText(item.getName());
        }

        return convertView;
    }
    @Override
    public Filter getFilter() {
        return filter;
    }
    private class CustomFilter extends Filter {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((CompanyList)(resultValue)).getName();
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            suggestions.clear();

            if (items != null && constraint != null) { // Check if the Original List and Constraint aren't null.
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getName().toLowerCase().contains(constraint)) { // Compare item in original list if it contains constraints.
                        suggestions.add(items.get(i)); // If TRUE add item in Suggestions.
                    }
                }
            }
            FilterResults results = new FilterResults(); // Create new Filter Results and return this to publishResults;
            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}