package com.wishbook.catalog.home.orderNew.adapters;


import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.TextDrawable;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.util.ColorGenerator;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.home.models.BuyersList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class BuyerSearchAdapter extends RecyclerView.Adapter<BuyerSearchAdapter.CustomViewHolder> implements Filterable {

    private ArrayList<BuyersList> items = new ArrayList<>();
    private ArrayList<BuyersList> itemsAll;
    private String url;
    private Context context;
    private boolean isMultiSelect;
    private ArrayList<BuyersList> arraylist;
    private UpdateBuyerListener updateBuyerListener;
    private boolean isSupplier;



    public BuyerSearchAdapter(Context context, int textViewResourceId, ArrayList<BuyersList> items, String url) {
        this.context = context;
        this.items = items;
        if (items.size() > 0) {
            this.itemsAll = (ArrayList<BuyersList>) items.clone();
        }
        this.url = url;
        this.arraylist = new ArrayList<BuyersList>();
        this.arraylist.addAll(items);
    }

    public BuyerSearchAdapter(Context context,int textViewResourceId, ArrayList<BuyersList> items, String url, boolean isMultiSelect,boolean isSupplier) {
        this.context = context;
        this.items = items;
        if (items.size() > 0) {
            this.itemsAll = (ArrayList<BuyersList>) items.clone();
        }
        this.url = url;
        this.isMultiSelect = isMultiSelect;
        this.arraylist = new ArrayList<BuyersList>();
        this.arraylist.addAll(items);
        this.isSupplier = isSupplier;
        StaticFunctions.selectedBuyers.clear();
        StaticFunctions.selectedBuyers = new ArrayList<>();

    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyer_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final TextDrawable drawable;
        if(isSupplier){
            if(items.get(position).getCompany_name()!=null && !items.get(position).getCompany_name().isEmpty()){
                drawable = TextDrawable.builder()
                        .buildRound(items.get(position).getCompany_name().substring(0, 1), ColorGenerator.MATERIAL.getRandomColor());
                holder.textInt.setImageDrawable(drawable);
            } else {
                drawable = TextDrawable.builder()
                        .buildRound("", ColorGenerator.MATERIAL.getRandomColor());
                holder.textInt.setImageDrawable(drawable);
            }
        } else {
            if (items.get(position).getBuying_person_name() != null && !items.get(position).getBuying_person_name().isEmpty()) {
                drawable = TextDrawable.builder()
                        .buildRound(items.get(position).getBuying_person_name().substring(0, 1), ColorGenerator.MATERIAL.getRandomColor());
                holder.textInt.setImageDrawable(drawable);
            } else {
                if(items.get(position).getCompany_name()!=null && !items.get(position).getCompany_name().isEmpty()){
                    drawable = TextDrawable.builder()
                            .buildRound(items.get(position).getCompany_name().substring(0, 1), ColorGenerator.MATERIAL.getRandomColor());
                    holder.textInt.setImageDrawable(drawable);
                } else {
                    drawable = TextDrawable.builder()
                            .buildRound("", ColorGenerator.MATERIAL.getRandomColor());
                    holder.textInt.setImageDrawable(drawable);
                }

            }
        }


        // holder.textInt.setImageDrawable(drawable);
        final ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(context, R.animator.flipping);
        anim.setTarget(holder.textInt);
        anim.setDuration(300);

        if(StaticFunctions.selectedBuyers.contains(new NameValues(items.get(position).getCompany_name(), items.get(position).getCompany_id()))){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.textInt.setImageDrawable(context.getResources().getDrawable(R.drawable.contactselection));
            }
            holder.contact_cb.setChecked(true);
            holder.main_container.setBackgroundColor(Color.parseColor("#e9f6fe"));
        } else {
            holder.textInt.setImageDrawable(drawable);
            holder.contact_cb.setChecked(false);
            holder.main_container.setBackgroundColor(Color.parseColor("#ffffff"));
        }
       /* if (items.get(position).isContactChecked()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.textInt.setImageDrawable(context.getResources().getDrawable(R.drawable.contactselection));
            }
            holder.contact_cb.setChecked(true);
            holder.main_container.setBackgroundColor(Color.parseColor("#e9f6fe"));

        } else {
            holder.textInt.setImageDrawable(drawable);
            holder.contact_cb.setChecked(false);
            holder.main_container.setBackgroundColor(Color.parseColor("#ffffff"));
        }*/

        if(isSupplier){
            holder.txt_buyer_name.setText(items.get(position).getCompany_name());
            holder.txt_buyer_comapny.setVisibility(View.GONE);
        }else{
            holder.txt_buyer_name.setText(items.get(position).getBuying_person_name());
            holder.txt_buyer_comapny.setText(items.get(position).getCompany_name());
        }

        holder.txt_contact_number.setText(items.get(position).getPhone_number());
        if(isSupplier){
            holder.linear_brokerage.setVisibility(View.VISIBLE);
            holder.txt_brokerage.setText(items.get(position).getBrokerage_fees()+"%");
        }else {
            holder.linear_brokerage.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anim.start();
                if (isMultiSelect) {
                    if (holder.contact_cb.isChecked()) {
                        items.get(holder.getAdapterPosition()).setContactChecked(false);
                        holder.contact_cb.setChecked(false);
                        holder.main_container.setBackgroundColor(Color.parseColor("#ffffff"));
                        holder.textInt.setImageDrawable(drawable);
                        for (int i = 0; i < StaticFunctions.selectedBuyers.size(); i++) {
                            if (StaticFunctions.selectedBuyers.get(i).getPhone().equals(items.get(holder.getAdapterPosition()).getCompany_id())) {
                                StaticFunctions.selectedBuyers.remove(i);
                                break;
                            }
                        }
                        if(updateBuyerListener!=null)
                            updateBuyerListener.getTotalSelected(StaticFunctions.selectedBuyers.size());
                    } else {
                        items.get(holder.getAdapterPosition()).setContactChecked(true);
                        holder.contact_cb.setChecked(true);
                        holder.main_container.setBackgroundColor(Color.parseColor("#e9f6fe"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            holder.textInt.setImageDrawable(context.getResources().getDrawable(R.drawable.contactselection));
                        }
                        StaticFunctions.selectedBuyers.add(new NameValues(items.get(holder.getAdapterPosition()).getCompany_name(), items.get(holder.getAdapterPosition()).getCompany_id()));
                        if(updateBuyerListener!=null)
                            updateBuyerListener.getTotalSelected(StaticFunctions.selectedBuyers.size());
                    }
                } else {
                    Intent searchIntent = new Intent();
                    searchIntent.putExtra("buyer", items.get(holder.getAdapterPosition()));
                    ((Activity) context).setResult(Activity.RESULT_OK, searchIntent);
                    ((Activity) context).finish();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public Filter getFilter() {
        return null;
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
                String s = constraint.toString();
                HashMap<String, String> params = new HashMap<>();
                HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
                if (url.equals("buyerlist_no_deputed")) {
                    params.put("without_deputed", "true");
                }
                params.put("status", "approved");
                params.put("search", s);
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
                notifyDataSetChanged();
            } else {
                notifyDataSetChanged();
            }
        }
    };


    public List<NameValues> getSelectedBuyers() {
        return StaticFunctions.selectedBuyers;
    }

    public ArrayList<BuyersList> getAllItems(){
        return items;
    }

    public void refreshArrayListSearch() {
        if(items!=null){
            this.arraylist = new ArrayList<BuyersList>();
            this.arraylist.addAll(items);
        }
    }
    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        items.clear();
        if (charText.length() == 0) {
            items.addAll(arraylist);
        } else {
            for (BuyersList wp : arraylist) {
                String buying_person = wp.getBuying_person_name() != null ? wp.getBuying_person_name() : "";
                String company = wp.getCompany_name() != null ? wp.getCompany_name() : "";
                String phone = wp.getPhone_number() != null ? wp.getPhone_number() : "";
                if (buying_person.toLowerCase(Locale.getDefault()).contains(charText)) {
                    items.add(wp);
                } else if (company.toLowerCase((Locale.getDefault())).contains(charText)) {
                    items.add(wp);
                } else if (phone.toLowerCase((Locale.getDefault())).contains(charText)) {
                    items.add(wp);
                }
            }

        }
        notifyDataSetChanged();
    }


    public void setSelectedListener (UpdateBuyerListener updateBuyerListener){
        this.updateBuyerListener = updateBuyerListener;
    }

    public interface UpdateBuyerListener{

        void getTotalSelected(int count);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_buyer_name, txt_buyer_comapny, txt_contact_number,txt_brokerage;
        private ImageView textInt;
        private CheckBox contact_cb;
        private LinearLayout linear_brokerage;
        private LinearLayout main_container;

        public CustomViewHolder(View view) {
            super(view);
            txt_buyer_name = (TextView) view.findViewById(R.id.txt_buyer_name);
            txt_buyer_comapny = (TextView) view.findViewById(R.id.txt_buyer_comapny);
            txt_contact_number = (TextView) view.findViewById(R.id.txt_contact_number);
            textInt = (ImageView) view.findViewById(R.id.textInt);
            contact_cb = (CheckBox) view.findViewById(R.id.contact_cb);
            main_container = (LinearLayout) view.findViewById(R.id.main_container);
            linear_brokerage = (LinearLayout) view.findViewById(R.id.linear_brokerage);
            txt_brokerage = (TextView) view.findViewById(R.id.txt_brokerage);

        }
    }

}
