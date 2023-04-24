package com.wishbook.catalog.home.rrc.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_sellingoder_catalog;
import com.wishbook.catalog.commonmodels.responses.Rrc_items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RRCOrderItemAdapter extends RecyclerView.Adapter<RRCOrderItemAdapter.RRCOrderItemViewHolder> {


    ArrayList<Response_sellingoder_catalog> itemsArrayList;
    ArrayList<Rrc_items> odl_rrc_items;
    Context context;


    HashMap<String,String> rrc_selection;

    ArrayList<Rrc_items> rrc_items;


    public RRCOrderItemAdapter(Context context, ArrayList<Response_sellingoder_catalog> itemsArrayList, ArrayList<Rrc_items> odl_rrc_items) {
        this.itemsArrayList = itemsArrayList;
        this.context = context;
        this.odl_rrc_items = odl_rrc_items;
        rrc_selection = new HashMap<>();
        rrc_items = new ArrayList<>();

    }

    @NonNull
    @Override
    public RRCOrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rrc_orderitem_item, viewGroup, false);
        return new RRCOrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RRCOrderItemViewHolder rrcOrderItemViewHolder, int position) {
        final Response_sellingoder_catalog catalog = itemsArrayList.get(position);
        if(catalog.getProducts()!=null ) {
            rrcOrderItemViewHolder.chck_rrc_item.setOnCheckedChangeListener(null);
            rrcOrderItemViewHolder.edit_rrc_request_quantity.setTag(rrcOrderItemViewHolder.getAdapterPosition());
            if(odl_rrc_items !=null) {
                for (int j = 0; j< odl_rrc_items.size(); j++) {
                    if(catalog.getProducts().get(0).getId().equalsIgnoreCase(odl_rrc_items.get(j).getOrder_item())){
                        rrcOrderItemViewHolder.chck_rrc_item.setChecked(true);
                        rrcOrderItemViewHolder.edit_rrc_request_quantity.setVisibility(View.VISIBLE);
                        rrcOrderItemViewHolder.edit_rrc_request_quantity.setText(String.valueOf(odl_rrc_items.get(j).getQty()));
                    }
                }
            }

            int RWQTy = catalog.getProducts().get(0).getRWQ_qty();



            if(catalog.getProducts().get(0).getProduct_type().equalsIgnoreCase("single")) {
                rrcOrderItemViewHolder.linear_design_name.setVisibility(View.VISIBLE);
                rrcOrderItemViewHolder.txt_design_label.setVisibility(View.VISIBLE);
                rrcOrderItemViewHolder.txt_design_name.setText(catalog.getProducts().get(0).getProduct_title());
                if( catalog.getProducts().get(0).getProduct_image()!=null)
                    StaticFunctions.loadFresco(context,catalog.getProducts().get(0).getProduct_image(),rrcOrderItemViewHolder.prod_img);
            } else {
                rrcOrderItemViewHolder.linear_design_name.setVisibility(View.VISIBLE);
                rrcOrderItemViewHolder.txt_design_label.setVisibility(View.GONE);
                rrcOrderItemViewHolder.txt_design_name.setText("(Full Catalog)");
                if(catalog.getCatalog_image()!=null)
                    StaticFunctions.loadFresco(context,catalog.getCatalog_image(),rrcOrderItemViewHolder.prod_img);
                else
                    StaticFunctions.loadFresco(context,catalog.getProducts().get(0).getProduct_image(),rrcOrderItemViewHolder.prod_img);
            }


            rrcOrderItemViewHolder.txt_catalog_name.setText(catalog.getName());

            rrcOrderItemViewHolder.txt_order_qty_value.setText(String.valueOf(RWQTy));

            if (catalog.getProducts().size() > 0 && catalog.getProducts().get(0).getNote() != null && !catalog.getProducts().get(0).getNote().isEmpty()) {
                rrcOrderItemViewHolder.txt_catalog_note.setVisibility(View.VISIBLE);
                String temp = "<font color='#e02b2b' >" + catalog.getProducts().get(0).getNote() + "</font>";
                rrcOrderItemViewHolder.txt_catalog_note.setText(Html.fromHtml(temp), TextView.BufferType.SPANNABLE);
            } else {
                rrcOrderItemViewHolder.txt_catalog_note.setVisibility(View.GONE);
            }

            rrcOrderItemViewHolder.chck_rrc_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if(checked) {
                        rrcOrderItemViewHolder.edit_rrc_request_quantity.setVisibility(View.VISIBLE);
                        if(!rrcOrderItemViewHolder.edit_rrc_request_quantity.getText().toString().isEmpty())
                            rrc_selection.put(catalog.getProducts().get(0).getId(),null);
                        else
                            rrc_selection.put(catalog.getProducts().get(0).getId(),rrcOrderItemViewHolder.edit_rrc_request_quantity.getText().toString());
                    } else {
                        if(rrc_selection.containsKey(catalog.getProducts().get(0).getId())){
                            rrc_selection.remove(catalog.getProducts().get(0).getId());
                            rrcOrderItemViewHolder.edit_rrc_request_quantity.setVisibility(View.GONE);
                            rrcOrderItemViewHolder.edit_rrc_request_quantity.setText(null);
                        }
                    }
                }
            });

        }
    }


    public HashMap<String, String> getAllSelectedItems() {
        if(rrc_selection!=null && rrc_selection.size() > 0) {
            for (Map.Entry<String, String> entry : rrc_selection.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Toast toast = Toast.makeText(context,"Please select any one order items",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                if(value == null) {
                    toast.show();
                    return null;
                } else if (value.isEmpty()) {
                    toast = Toast.makeText(context,"Qty can't be empty",Toast.LENGTH_SHORT);
                    toast.show();
                    return null;
                } else if(value.equalsIgnoreCase("0")) {
                    toast.show();
                    return null;
                } else if (!checkRRCValueGreaterOrderQuantity(entry)) {
                    toast = Toast.makeText(context,"Request qty can't greater than delivered qty",Toast.LENGTH_SHORT);
                    toast.show();
                    return null;
                }
            }
        } else {
             Toast.makeText(context,"Please select any one order items",Toast.LENGTH_SHORT).show();
            return null;
        }
        return rrc_selection;
    }

    public boolean checkRRCValueGreaterOrderQuantity(Map.Entry<String, String> entry) {
       for (int i = 0;i< itemsArrayList.size();i++) {
           if(itemsArrayList.get(i).getProducts().get(0).getId().equalsIgnoreCase(entry.getKey())){
               int RWQTy = itemsArrayList.get(i).getProducts().get(0).getRWQ_qty();
               if(entry.getValue()!=null && Integer.parseInt(entry.getValue()) > RWQTy){
                   return false;
               }
           }
       }
        return true;
    }


    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    public class MyTextWatcher implements TextWatcher {
        private EditText editText;
        private String type;

        public MyTextWatcher(EditText editText, String type) {
            this.editText = editText;
            this.type = type;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(type!=null){
                int position = (int) editText.getTag();
                if (!s.toString().isEmpty()) {
                    if(type.equalsIgnoreCase("quantity")){
                        rrc_selection.put(itemsArrayList.get(position).getProducts().get(0).getId(),editText.getText().toString());
                    }
                }
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public class RRCOrderItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.chck_rrc_item)
        CheckBox chck_rrc_item;

        @BindView(R.id.prod_img)
        SimpleDraweeView prod_img;

        @BindView(R.id.txt_design_name)
        TextView txt_design_name;

        @BindView(R.id.txt_order_qty_value)
        TextView txt_order_qty_value;

        @BindView(R.id.edit_rrc_request_quantity)
        EditText edit_rrc_request_quantity;

        @BindView(R.id.txt_catalog_name)
        TextView txt_catalog_name;

        @BindView(R.id.linear_design_name)
        LinearLayout linear_design_name;
        @BindView(R.id.txt_catalog_note)
        TextView txt_catalog_note;

        @BindView(R.id.txt_design_label)
        TextView txt_design_label;


        public RRCOrderItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            MyTextWatcher textWatcher = new MyTextWatcher(edit_rrc_request_quantity,"quantity");
            edit_rrc_request_quantity.addTextChangedListener(textWatcher);
        }
    }


}