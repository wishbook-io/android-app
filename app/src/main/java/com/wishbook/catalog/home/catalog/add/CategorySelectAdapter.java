package com.wishbook.catalog.home.catalog.add;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.responses.EnumGroupResponse;

import java.util.ArrayList;
import java.util.Locale;


public class CategorySelectAdapter extends RecyclerView.Adapter<CategorySelectAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<EnumGroupResponse> strings;
    private ArrayList<EnumGroupResponse> arraylist;
    private ArrayList<String> checkedArraylist;
    private ArrayList<String> checkedStringValueArraylist;
    private OnCheckedListener onCheckedListener;

    public CategorySelectAdapter(Context context, ArrayList<EnumGroupResponse> strings) {
        this.context = context;
        this.strings = strings;
        this.arraylist = new ArrayList<EnumGroupResponse>();
        this.arraylist.addAll(strings);
        this.checkedArraylist = new ArrayList<>();
        this.checkedStringValueArraylist = new ArrayList<>();
    }

    public CategorySelectAdapter(Context context, ArrayList<EnumGroupResponse> strings, ArrayList<String> checkedArraylist) {
        this.context = context;
        this.strings = strings;
        this.arraylist = new ArrayList<EnumGroupResponse>();
        this.arraylist.addAll(strings);
        this.checkedArraylist = checkedArraylist;
        this.checkedStringValueArraylist = new ArrayList<>();
    }

    @Override
    public CategorySelectAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_select_item, parent, false);
        return new CategorySelectAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategorySelectAdapter.CustomViewHolder holder, final int position) {
        holder.checkbox.setOnCheckedChangeListener(null);
        if (checkedArraylist != null) {
            if (checkedArraylist.contains(strings.get(position).getId())) {
                holder.checkbox.setChecked(true);
                if(onCheckedListener!=null) {
                    onCheckedListener.onChecked(strings.get(position).getId(),true);
                }
            } else {
                holder.checkbox.setChecked(false);
            }
        }
        holder.checkbox.setText(strings.get(position).getValue());
        /*if(checkedArraylist.size() == 0){
            if(strings.get(position).getValue().equalsIgnoreCase("Sarees")){
                holder.checkbox.setChecked(true);
                checkedArraylist.add(strings.get(position).getId());
            }
        }*/
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {
                    Log.e("TAG", "onCheckedChanged: "+isChecked );
                    checkedArraylist.add(strings.get(position).getId());
                    checkedStringValueArraylist.add(strings.get(position).getValue());
                    for (int i = 0; i < strings.size(); i++) {
                        if (i != position) {
                          //  holder.checkbox.setChecked(false);
                            if (checkedArraylist.contains(strings.get(i).getId())) {
                                checkedArraylist.remove(strings.get(i).getId());
                                checkedStringValueArraylist.remove(strings.get(i).getValue());
                            }
                        } else {
                            holder.checkbox.setChecked(true);
                        }
                    }
                    notifyDataSetChanged();
                    if(onCheckedListener!=null) {
                        onCheckedListener.onChecked(strings.get(position).getId(),true);
                    }
                } else {
                    Log.e("TAG", "onCheckedChanged: "+isChecked );
                    try {
                        boolean iscontain = checkedArraylist.contains(strings.get(position).getId());
                        if (iscontain) {
                            holder.checkbox.setChecked(false);
                            checkedArraylist.remove(strings.get(position).getId());
                            checkedStringValueArraylist.remove(strings.get(position).getValue());

                        }
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return strings.size();
    }


    public interface OnCheckedListener {
        public void onChecked(String categoryId,boolean eventFire);
    }

    public void setOnCheckedListener(OnCheckedListener onCheckedListener) {
        this.onCheckedListener = onCheckedListener;
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private RadioButton checkbox;

        public CustomViewHolder(View view) {
            super(view);
            checkbox = (RadioButton) view.findViewById(R.id.radiobutton);
        }
    }

    public ArrayList<String> getSelectedItem() {
        return checkedArraylist;
    }

    public ArrayList<String> getSelectedItemStringValue() {
        return checkedStringValueArraylist;
    }

    public String getSelectedIdValue(String id){
        for (int i=0;i<strings.size();i++){
            if(strings.get(i).getId().equals(id)){
                return strings.get(i).getValue();
            }
        }
        return "";
    }

    public void removeSelectedItem() {
        checkedArraylist.clear();
        checkedStringValueArraylist.clear();
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        strings.clear();
        if (charText.length() == 0) {
            strings.addAll(arraylist);
        } else {
            for (EnumGroupResponse wp : arraylist) {
                if (wp.getValue().toLowerCase(Locale.getDefault()).contains(charText)) {
                    strings.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
