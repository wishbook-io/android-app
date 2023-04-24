package com.wishbook.catalog.home.catalog.add;

import android.content.Context;
import com.google.android.material.textfield.TextInputLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.responses.EnumGroupResponse;

import java.util.ArrayList;
import java.util.Locale;


public class FabricSelectAdapter extends RecyclerView.Adapter<FabricSelectAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<EnumGroupResponse> strings;
    private ArrayList<EnumGroupResponse> arraylist;
    private ArrayList<String> checkedArraylist;
    private String otherFabric;
    private String type;

    public FabricSelectAdapter(Context context, ArrayList<EnumGroupResponse> strings, String type) {
        this.context = context;
        this.strings = strings;
        this.arraylist = new ArrayList<EnumGroupResponse>();
        this.arraylist.addAll(strings);
        this.checkedArraylist = new ArrayList<>();
        this.type = type;
    }

    public FabricSelectAdapter(Context context, ArrayList<EnumGroupResponse> strings, ArrayList<String> checkedArraylist, String type) {
        this.context = context;
        this.strings = strings;
        this.arraylist = new ArrayList<EnumGroupResponse>();
        this.arraylist.addAll(strings);
        this.checkedArraylist = checkedArraylist;
        this.type = type;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fabric_select_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {
        holder.checkbox.setOnCheckedChangeListener(null);
        if (checkedArraylist != null) {
            if (checkedArraylist.contains(strings.get(position).getValue())) {
                holder.checkbox.setChecked(true);
            } else {
                holder.checkbox.setChecked(false);
            }
        }
        holder.checkbox.setText(strings.get(position).getValue());
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (checkedArraylist.size() > 3) {
                        if(type!=null){
                            if(type.equals("work")){
                                Toast.makeText(context, "You can add only 4 work", Toast.LENGTH_SHORT).show();
                            } else if(type.equals("fabric")) {
                                Toast.makeText(context, "You can add only 4 fabrics", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "You can add only 4 options", Toast.LENGTH_SHORT).show();
                            }
                        }

                        holder.checkbox.setChecked(false);
                    } else {
                        if (strings.get(position).getValue().equalsIgnoreCase("Other")) {
                            holder.input_other.setVisibility(View.VISIBLE);
                            holder.txt_other_input_value.setVisibility(View.VISIBLE);
                            holder.input_other.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void afterTextChanged(Editable editable) {
                                    /*if(!holder.input_other.getText().toString().trim().isEmpty()){
                                        otherFabric = holder.input_other.getText().toString().trim();
                                    }*/

                                }
                            });
                        }
                        checkedArraylist.add(strings.get(position).getValue());
                    }
                } else {
                    boolean iscontain = checkedArraylist.contains(strings.get(position).getValue());
                    if (iscontain) {
                        if (strings.get(position).getValue().equalsIgnoreCase("Other")) {
                            holder.input_other.setVisibility(View.GONE);
                            holder.txt_other_input_value.setVisibility(View.GONE);
                            otherFabric = null;
                        }
                        checkedArraylist.remove(strings.get(position).getValue());
                    }
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
        private TextInputLayout txt_other_input_value;
        EditText input_other;

        public CustomViewHolder(View view) {
            super(view);
            checkbox = (CheckBox) view.findViewById(R.id.checkbox);
            txt_other_input_value = (TextInputLayout) view.findViewById(R.id.txt_other_input_value);
            input_other = (EditText) view.findViewById(R.id.input_other);
        }
    }

    public ArrayList<String> getSelectedItem() {
        /*for (int i=0;i<checkedArraylist.size();i++){
            if(checkedArraylist.get(i).equals("Other")){
                if(otherFabric!=null){
                    checkedArraylist.add(otherFabric);
                }
            }
        }*/
        return checkedArraylist;
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
