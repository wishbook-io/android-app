package com.wishbook.catalog.commonadapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.widget.SimpleTextWatcher;
import com.wishbook.catalog.home.cart.SelectSizeBottomSheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SizeSelectAdapter extends RecyclerView.Adapter<SizeSelectAdapter.CustomViewHolder> {

    private ArrayList<String> sizes;
    String type;
    private HashMap<String, Integer> hashmap_size;
    private SelectSizeBottomSheet selectSizeBottomSheet;
    private int count_sets = 0;

    public SizeSelectAdapter(Context context, ArrayList<String> sizes, SelectSizeBottomSheet selectSizeBottomSheet, String type) {

        try {
            this.type = type;
            Context context1 = context;
            this.sizes = sizes;
            this.selectSizeBottomSheet = selectSizeBottomSheet;
            hashmap_size = new HashMap<>();
            for (int i = 0; i < sizes.size(); i++) {
                hashmap_size.put(sizes.get(i), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SizeSelectAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addcart_size_item, parent, false);
        return new SizeSelectAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, int position) {

        try {
            if (type.contains("product")) {
                holder.cart_catalog_setpcs.setVisibility(View.INVISIBLE);
                holder.number_txt.setText("No. of pcs.");
            }

            final String size = sizes.get(position);

            if (size.equalsIgnoreCase("free-size")) {
                holder.txt_size_label.setText(size);
            } else {
                holder.txt_size_label.setText(size + " (" + getFullSizeName(size) + ")");
            }
            holder.cart_catalog_setpcs.setText("0 Pc.");
            holder.btn_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    count_sets++;
                    int qty = hashmap_size.get(size);
                    holder.edit_qty.setText("" + (++qty));
                    int temp = qty * (Integer.parseInt(selectSizeBottomSheet.piece));

                    if (qty == 1) {

                        holder.cart_catalog_setpcs.setText("" + temp + " Pc.");
                    } else {
                        holder.cart_catalog_setpcs.setText("" + temp + " Pcs.");
                    }
                }
            });

            holder.btn_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int qty = hashmap_size.get(size);
                    int temp = qty * (Integer.parseInt(selectSizeBottomSheet.piece));
                    if (qty != 0) {
                        count_sets--;
                        holder.edit_qty.setText("" + (--qty));
                        if (qty == 1) {
                            holder.cart_catalog_setpcs.setText("" + temp + " Pc.");
                        } else {
                            holder.cart_catalog_setpcs.setText("" + temp + " Pcs.");
                        }
                    }
                }
            });


            holder.edit_qty.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    hashmap_size.put(size, Integer.parseInt(s.toString()));
                    selectSizeBottomSheet.changeTitle(count_sets);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return sizes.size();
    }

    private String getFullSizeName(String size) {
        switch (size.toUpperCase()) {
            case "S":
                return "Small";
            case "M":
                return "Medium";
            case "L":
                return "Large";
            case "XL":
                return "Extra Large";
            case "XS":
                return "Extra Small";
            case "2XL":
                return "Double Extra Large";
            case "3XL":
                return "Triple Extra Large";
        }
        return size;
    }


    public HashMap<String, Integer> getData() {
        List<String> l = new ArrayList<>(hashmap_size.keySet());
        HashMap<String, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < hashmap_size.size(); i++) {

            if (hashmap_size.get(l.get(i)) > 0) {
                hashMap.put(l.get(i), hashmap_size.get(l.get(i)));
            }

        }
        return hashMap;
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cart_catalog_setpcs)
        TextView cart_catalog_setpcs;

        @BindView(R.id.edit_qty)
        EditText edit_qty;

        @BindView(R.id.txt_size_label)
        TextView txt_size_label;

        @BindView(R.id.btn_plus)
        TextView btn_plus;

        @BindView(R.id.btn_minus)
        TextView btn_minus;

        @BindView(R.id.number_txt)
        TextView number_txt;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
