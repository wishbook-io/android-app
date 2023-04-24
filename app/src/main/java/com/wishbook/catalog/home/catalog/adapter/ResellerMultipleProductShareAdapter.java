package com.wishbook.catalog.home.catalog.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ResellerMultipleProductShareAdapter extends RecyclerView.Adapter<ResellerMultipleProductShareAdapter.ResellerMultipleProductViewHolder> {


    List<Response_catalogMini> response_catalogMinis;
    Context context;
    boolean isSingleAvailable;
    ResellerShareAdapter.ProductSelectionListener productSelectionListener;

    HashMap<String, String> resale_price;
    DecimalFormat decimalFormat;

    public ResellerMultipleProductShareAdapter(Context context, List<Response_catalogMini> response_catalogMinis, boolean isSingleAvailable) {
        this.response_catalogMinis = response_catalogMinis;
        this.context = context;
        this.isSingleAvailable = isSingleAvailable;
        resale_price = new HashMap<>();
        decimalFormat = new DecimalFormat("#.##");
    }

    @Override
    public ResellerMultipleProductShareAdapter.ResellerMultipleProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.share_multipleproduct_item, parent, false);
        return new ResellerMultipleProductShareAdapter.ResellerMultipleProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ResellerMultipleProductShareAdapter.ResellerMultipleProductViewHolder customViewHolder, final int position) {
        final Response_catalogMini response_catalogMini = response_catalogMinis.get(position);
        customViewHolder.edit_resale_price.setTag(customViewHolder.getAdapterPosition());
        if (response_catalogMini.getImage().getThumbnail_small() != null) {
            StaticFunctions.loadFresco(context, response_catalogMini.getImage().getThumbnail_small(), customViewHolder.product_img);
        }
        if (response_catalogMini.getResale_price() != null) {
            customViewHolder.edit_resale_price.setText(response_catalogMini.getResale_price());
        }

        customViewHolder.txt_product_code_value.setText(response_catalogMini.getId());
        customViewHolder.txt_design_value.setText(response_catalogMini.getTitle());
        double actual_price = response_catalogMini.getPrice_per_design_with_gst() + response_catalogMini.getShipping_charges();

        // start price breakup logic
        double tax = 0.0;
        double single_pc_price = 0.0;
        if (response_catalogMini.getSingle_piece_price_range().contains("-")) {
            single_pc_price = Double.parseDouble(response_catalogMini.getSingle_piece_price());
        } else {
            single_pc_price = Double.parseDouble(response_catalogMini.getSingle_piece_price_range());
        }
        double single_pc_price_gst = response_catalogMini.getPrice_per_design_with_gst();
        double shipping_charge = response_catalogMini.getShipping_charges();
        tax = single_pc_price_gst - single_pc_price;
        customViewHolder.txt_single_pc_price_value.setText("\u20B9" + decimalFormat.format(single_pc_price) + " + " + decimalFormat.format(tax) + "(tax)" + " + " + decimalFormat.format(shipping_charge) + " (shipping)");


        customViewHolder.txt_single_pc_price_final_value.setText("=" + " \u20B9" + decimalFormat.format(actual_price));

    }


    @Override
    public int getItemCount() {
        return response_catalogMinis.size();
    }


    public HashMap<String, String> getAllResalePrice() {

        return resale_price;
    }

    public void updateResaleAmount(String type, String value) {
        if (value.isEmpty())
            value = "0";

        try {
            // To Check valid value
            Float.parseFloat(value);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context,"Please enter valid margin",Toast.LENGTH_SHORT).show();
            return;
        }

        if (type.equalsIgnoreCase("per")) {

            if (!value.isEmpty()) {
                for (int i = 0; i < response_catalogMinis.size(); i++) {
                    double actual_price = response_catalogMinis.get(i).getPrice_per_design_with_gst() + response_catalogMinis.get(i).getShipping_charges();
                    double resale_price = ((actual_price / 100) * Float.parseFloat(value)) + actual_price;
                    response_catalogMinis.get(i).setResale_price(decimalFormat.format(resale_price));
                }
            }
            notifyDataSetChanged();

        } else if (type.equalsIgnoreCase("price")) {
            if (!value.isEmpty()) {
                for (int i = 0; i < response_catalogMinis.size(); i++) {
                    double actual_price = response_catalogMinis.get(i).getPrice_per_design_with_gst() + response_catalogMinis.get(i).getShipping_charges();
                    double resale_price = (Float.parseFloat(value)) + actual_price;
                    response_catalogMinis.get(i).setResale_price(decimalFormat.format(resale_price));
                }
            }
            notifyDataSetChanged();
        }

    }


    public class MyTextWatcher implements TextWatcher {
        private EditText editText;
        private String type;
        private ResellerMultipleProductViewHolder viewHolder;

        public MyTextWatcher(EditText editText, String type, ResellerMultipleProductViewHolder viewHolder) {
            this.editText = editText;
            this.type = type;
            this.viewHolder = viewHolder;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (type != null) {
                int position = (int) editText.getTag();
                if (!s.toString().isEmpty()) {
                    if (type.equalsIgnoreCase("price")) {
                        response_catalogMinis.get(position).setResale_price(editText.getText().toString());
                        //resale_price.put(response_catalogMinis.get(position).getId(), editText.getText().toString());
                        viewHolder.txt_resale_price_value.setText(editText.getText().toString());
                    }
                }
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public class ResellerMultipleProductViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.product_img)
        SimpleDraweeView product_img;

        @BindView(R.id.txt_design_value)
        TextView txt_design_value;

        @BindView(R.id.txt_product_code_value)
        TextView txt_product_code_value;

        @BindView(R.id.txt_single_pc_price_value)
        TextView txt_single_pc_price_value;

        @BindView(R.id.edit_resale_price)
        EditText edit_resale_price;

        @BindView(R.id.txt_resale_price_value)
        TextView txt_resale_price_value;

        @BindView(R.id.txt_single_pc_price_final_value)
        TextView txt_single_pc_price_final_value;

        public ResellerMultipleProductViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            MyTextWatcher textWatcher = new MyTextWatcher(edit_resale_price, "price", this);
            edit_resale_price.addTextChangedListener(textWatcher);
        }
    }

}
