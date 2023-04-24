package com.wishbook.catalog.home.catalog.add;


import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.EnumGroupResponse;

import java.util.ArrayList;
import java.util.Locale;

public class FilterSelectAdapter extends RecyclerView.Adapter<FilterSelectAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<EnumGroupResponse> strings;
    private ArrayList<EnumGroupResponse> arraylist;
    private ArrayList<String> checkedArraylist;
    private ArrayList<String> checkedStringValueArraylist;
    private boolean isValueStrore =true;
    private OnFabricCheckedListener onCheckedListener;

    public FilterSelectAdapter(Context context, ArrayList<EnumGroupResponse> strings,boolean isValueStrore) {
        this.context = context;
        this.strings = strings;
        this.arraylist = new ArrayList<EnumGroupResponse>();
        this.arraylist.addAll(strings);
        this.checkedArraylist = new ArrayList<>();
        this.checkedStringValueArraylist = new ArrayList<>();
        this.isValueStrore = isValueStrore;
    }

    public FilterSelectAdapter(Context context, ArrayList<EnumGroupResponse> strings, ArrayList<String> checkedArraylist,boolean isValueStrore) {
        this.context = context;
        this.strings = strings;
        this.arraylist = new ArrayList<EnumGroupResponse>();
        this.arraylist.addAll(strings);
        this.checkedArraylist = checkedArraylist;
        this.isValueStrore = isValueStrore;
        this.checkedStringValueArraylist = new ArrayList<>();
    }

    @Override
    public FilterSelectAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fabric_select_item, parent, false);
        return new FilterSelectAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FilterSelectAdapter.CustomViewHolder holder, final int position) {
        holder.checkbox.setOnCheckedChangeListener(null);
        if (checkedArraylist != null) {
            if(isValueStrore){
                if (checkedArraylist.contains(strings.get(position).getValue())) {
                    holder.checkbox.setChecked(true);
                } else {
                    holder.checkbox.setChecked(false);
                }
            } else {
                if (checkedArraylist.contains(strings.get(position).getId())) {
                    holder.checkbox.setChecked(true);
                } else {
                    holder.checkbox.setChecked(false);
                }
            }

        }
        holder.checkbox.setText(strings.get(position).getValue());
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                try {
                    if (isChecked) {
                        if (isValueStrore) {
                            checkedArraylist.add(strings.get(position).getValue());
                        } else {
                            Log.e("P", "Add onCheckedChanged: "+strings.get(position).getId());
                            checkedArraylist.add(strings.get(position).getId().toString());
                            checkedStringValueArraylist.add(strings.get(position).getValue());
                        }
                    } else {
                        if (isValueStrore) {
                            boolean iscontain = checkedArraylist.contains(strings.get(position).getValue());
                            if (iscontain) {
                                checkedArraylist.remove(strings.get(position).getValue());
                            }
                        } else {
                            boolean iscontain = checkedArraylist.contains(strings.get(position).getId());
                            if (iscontain) {
                                boolean isremoved = checkedArraylist.remove(strings.get(position).getId());
                                checkedStringValueArraylist.remove(strings.get(position).getValue());
                            }
                        }
                    }

                    if (onCheckedListener != null) {
                        onCheckedListener.onFabricChecked();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkbox;

        public CustomViewHolder(View view) {
            super(view);
            checkbox = (CheckBox) view.findViewById(R.id.checkbox);
        }
    }

    public ArrayList<String> getSelectedItem() {
        return checkedArraylist;
    }

    public ArrayList<String> getSelectedItemStringValue() {
        return checkedStringValueArraylist;
    }

    public String getSelectedItemNameCommaSeprated() {
        return StaticFunctions.ArrayListToString(getSelectedItem(), StaticFunctions.COMMASEPRATEDSPACE);
    }

    public void removeSelectedItem() {
        checkedArraylist.clear();
        checkedStringValueArraylist.clear();
    }

    public interface OnFabricCheckedListener {
        public void onFabricChecked();
    }

    public void setOnCountCheckedListener(OnFabricCheckedListener onCheckedListener) {
        this.onCheckedListener = onCheckedListener;
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