package com.wishbook.catalog.home.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.CartCatalogModel;
import com.wishbook.catalog.commonmodels.UserInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResalePriceChangeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private CartCatalogModel fulldata;
    private ArrayList<CartCatalogModel.Catalogs> catalogs = new ArrayList<>();
    private HashMap<String, String> resale_amt;
    private changeMarginListener changeMarginListener;
    UserInfo userInfo;
    DecimalFormat decimalFormat;


    public ResalePriceChangeAdapter(Context context, @NonNull CartCatalogModel fulldata) {
        this.fulldata = fulldata;
        this.context = context;
        catalogs.addAll(fulldata.getCatalogs());
        resale_amt = new HashMap<>();
        userInfo = UserInfo.getInstance(context);
        decimalFormat = new DecimalFormat("#.##");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.resale_change_price_item, parent, false);
        return new ResaleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CartCatalogModel.Catalogs catalog = catalogs.get(position);
        ((ResaleViewHolder) holder).txt_count.setText("" + (position + 1));
        ((ResaleViewHolder) holder).edit_add_margin.setTag("item_price_" + holder.getAdapterPosition());
        Log.e("TAG", "Bind ViewHolder initView: called ===>" );
        if (catalog.getCatalog_display_amount() > 0) {
            ((ResaleViewHolder) holder).edit_add_margin.setText(String.valueOf(catalog.getCatalog_display_amount()));
        } else {
            // calculate
            /**
             * Remove
             */
            /*Double catalog_price_with_ship = Double.parseDouble(catalog.getCatalog_total_amount()) + Double.parseDouble(catalog.getCatalog_shipping_charges());
            if (userInfo.getResaleDefaultMargin() != null) {
                double price = ((catalog_price_with_ship / 100) * Float.parseFloat(userInfo.getResaleDefaultMargin())) + catalog_price_with_ship;
                ((ResaleViewHolder) holder).edit_add_margin.setText(decimalFormat.format(price));
            }*/
        }
        if (catalog.getIs_full_catalog()) {
            if (catalog.getCatalog_title() != null && catalog.getCatalog_title().length() > 24) {
                ((ResaleViewHolder) holder).item_name.setText(catalog.getCatalog_title().substring(0, 24) + "... ");
            } else if (catalog.getCatalog_title() != null) {
                ((ResaleViewHolder) holder).item_name.setText(catalog.getCatalog_title());
            }
            ((ResaleViewHolder) holder).item_qty.setText("(" + catalog.getProducts().get(0).getQuantity() + ")");
        } else {
            if (catalog.getProducts().get(0).getProduct_sku() != null && catalog.getProducts().get(0).getProduct_sku().length() > 24) {
                ((ResaleViewHolder) holder).item_name.setText(catalog.getProducts().get(0).getProduct_sku().substring(0, 24) + "... ");
            } else if (catalog.getCatalog_title() != null) {
                ((ResaleViewHolder) holder).item_name.setText(catalog.getProducts().get(0).getProduct_sku());
            }
            int quantity = catalog.getProducts().get(0).getQuantity();
            ((ResaleViewHolder) holder).item_qty.setText("(" + quantity + ")");
        }
        if (catalog.getCatalog_total_amount() != null) {
            ((ResaleViewHolder) holder).catalog_total_amount.setText("\u20B9 " + decimalFormat.format(Double.parseDouble(catalog.getCatalog_total_amount()) + Double.parseDouble(catalog.getCatalog_shipping_charges())));
        }
    }

    public HashMap<String, String> getAllResaleAmt() {
        for (int i = 0; i < catalogs.size(); i++) {
            if (resale_amt.containsKey("item_price_" + i)) {
                if (resale_amt.get("item_price_" + i).isEmpty()) {
                    Toast.makeText(context, "Please Enter Resale Amount", Toast.LENGTH_SHORT).show();
                    return null;
                } else if (Double.parseDouble(resale_amt.get("item_price_" + i)) < Double.parseDouble(catalogs.get(i).getCatalog_total_amount()) + Double.parseDouble(catalogs.get(i).getCatalog_shipping_charges())) {
                    Toast.makeText(context, String.format(context.getResources().getString(R.string.resell_amount_error_item)), Toast.LENGTH_SHORT).show();
                    return null;
                } else if(Double.parseDouble(resale_amt.get("item_price_" + i)) >
                        ((Double.parseDouble(catalogs.get(i).getCatalog_total_amount()) +
                                Double.parseDouble(catalogs.get(i).getCatalog_shipping_charges()))*3)) {
                    Toast.makeText(context, context.getResources().getString(R.string.resell_amount_error_3x_item), Toast.LENGTH_SHORT).show();
                    return null;
                }
            } else {
                Toast.makeText(context, "Please Enter Resale Amount", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        return resale_amt;
    }


    public Double calculateTotalResaleAmt() {
        Double total_resale_amt = 0.0;
        for (Map.Entry<String, String> entry : resale_amt.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                if (value != null && !value.isEmpty())
                    total_resale_amt += Double.parseDouble(value);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return total_resale_amt;
    }

    @Override
    public int getItemCount() {
        return catalogs.size();
    }

    public interface changeMarginListener {
        void onChange();
    }

    public void setChangeMaginListner(changeMarginListener changeMaginListner) {
        this.changeMarginListener = changeMaginListner;
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
            if (type != null) {
                String tag = (String) editText.getTag();
                if (!s.toString().isEmpty()) {
                    if (type.equalsIgnoreCase("margin")) {
                        resale_amt.put(tag, s.toString());
                    }
                } else {
                    resale_amt.remove(tag);
                }
                if (changeMarginListener != null) {
                    changeMarginListener.onChange();
                }

            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class ResaleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_name)
        TextView item_name;

        @BindView(R.id.item_qty)
        TextView item_qty;

        @BindView(R.id.txt_count)
        TextView txt_count;

        @BindView(R.id.catalog_total_amount)
        TextView catalog_total_amount;

        @BindView(R.id.edit_add_margin)
        EditText edit_add_margin;

        ResaleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            MyTextWatcher textWatcher2 = new MyTextWatcher(edit_add_margin, "margin");
            edit_add_margin.addTextChangedListener(textWatcher2);
        }
    }


}
