package com.wishbook.catalog.home.catalog.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayout;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.home.models.ProductObj;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BecomeSellerSinglePcAdapter extends RecyclerView.Adapter<BecomeSellerSinglePcAdapter.CustomViewHolder> {


    // ### Variable Init Start ## //
    private ArrayList<ProductObj> productObjs;
    public boolean isFullCatalog, isCatalog;
    Context context;
    RecyclerView recyclerView;
    public boolean isSizeAvailable;
    public  boolean isProductLevelMargin;
    ArrayList<String> system_size;

    private static String TAG = BecomeSellerSinglePcAdapter.class.getSimpleName();


    public BecomeSellerSinglePcAdapter(Context context, ArrayList<ProductObj> productObjs, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.productObjs = productObjs;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.become_seller_single_pc, parent, false);
        return new CustomViewHolder(view, new AddMarginEditListener());
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, int position) {
        ProductObj productObj = productObjs.get(position);
        if (productObj.getImage() != null && productObj.getImage().getThumbnail_small() != null) {
            StaticFunctions.loadFresco(context, productObj.getImage().getThumbnail_small(), holder.product_img);
        }
        holder.txt_design_number.setText(productObj.getSku());
        holder.txt_price_per_design.setText("\u20B9 " + productObj.getPublic_price());
        if(isProductLevelMargin) {
            Log.e(TAG, "onBindViewHolder: isProductLevelMargin====>"+isProductLevelMargin );
            holder.addMarginEditListener.updateHolder(holder);
            holder.addMarginEditListener.updatePosition(holder.getAdapterPosition());
            float max_margin_allowed = Math.max(Float.parseFloat(productObj.getPublic_price()) * 10 / 100, 60);
            double max_percentage_allowed = (max_margin_allowed) / Float.parseFloat(productObj.getPublic_price()) * 100;
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            holder.txt_common_margin_note1.setText(String.format(context.getResources().getString(R.string.max_margin_note_1), String.valueOf(df.format(max_percentage_allowed)) + "%", productObj.getPublic_price(), "60", String.valueOf(max_margin_allowed), "60", String.valueOf(Math.max(max_margin_allowed,Float.parseFloat("60")))));
            holder.txt_common_margin_note2.setText(String.format(context.getResources().getString(R.string.max_margin_note_2), "60", productObj.getPublic_price(), "100", String.valueOf(df.format(max_percentage_allowed))));
            initListener(holder, position, productObj);
        }

        if(isSizeAvailable) {
            Log.e(TAG, "onBindViewHolder: isSizeAvailable====>"+isSizeAvailable );
            updateSizeUI(system_size, holder.flex_select_size, productObj, holder.linear_select_size_container);
        } else {
            holder.linear_select_size_container.setVisibility(View.GONE);
            holder.flex_select_size.removeAllViews();
            productObj.setAvailable_sizes(null);
        }
    }

    public boolean isAllPriceValid() {
        for (int childCount = recyclerView.getChildCount(), i = 0; i < childCount; ++i) {
            final CustomViewHolder holder = (CustomViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
            if (holder.edit_add_margin.getError() != null && !holder.edit_add_margin.getError().toString().isEmpty()) {
                holder.edit_add_margin.requestFocus();
                return false;
            }
        }
        return true;
    }




    public void initListener(final CustomViewHolder viewHolder, final int position, final ProductObj productObj) {
        if (productObj.isAddMarginPriceSelected())
            viewHolder.radio_single_piece_price.setChecked(true);
        else
            viewHolder.radio_single_piece_per.setChecked(true);

        viewHolder.edit_add_margin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.scrollToPosition(position);
                        viewHolder.edit_add_margin.requestFocus();
                    }
                }, 1000);
                return false;
            }
        });

        viewHolder.rg_add_margin.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (viewHolder.radio_single_piece_price.isChecked()) {
                    productObj.setAddMarginPriceSelected(true);
                    viewHolder.addMarginEditListener.changeRadio();
                } else {
                    productObj.setAddMarginPriceSelected(false);
                    viewHolder.addMarginEditListener.changeRadio();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return productObjs.size();
    }

    public void setProducts(ArrayList<ProductObj> prods) {
        this.productObjs = prods;
    }

    public ArrayList<ProductObj> getProducts() {
        return productObjs;
    }

    private class AddMarginEditListener implements TextWatcher {
        private int position;
        private BecomeSellerSinglePcAdapter.CustomViewHolder viewHolder;

        public AddMarginEditListener() {

        }


        public void updateHolder(BecomeSellerSinglePcAdapter.CustomViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        public void updatePosition(int position) {
            this.position = position;
        }

        public void changeRadio() {
            updatesinglePrice(productObjs.get(position).getPublic_price());
        }

        public void updatesinglePrice(String charSequence) {
            try {
                if (charSequence.toString().isEmpty()) {
                    this.viewHolder.txt_single_pc_product_price.setText("Price for single Pc.: " + "\u20B9" + Math.round(0));
                } else {
                    if (!isFullCatalog) {
                        if (this.viewHolder.radio_single_piece_per.isChecked()) {
                            float edit_text_price = 0;
                            if (!charSequence.toString().isEmpty()) {
                                edit_text_price = Float.parseFloat(charSequence.toString());
                            }
                            Float single_price = (edit_text_price / 100) * Float.parseFloat(viewHolder.edit_add_margin.getText().toString()) + edit_text_price;
                            productObjs.get(position).setSingle_piece_price(String.valueOf(Math.round(single_price)));
                            this.viewHolder.txt_single_pc_product_price.setText("Price for single Pc.: " + "\u20B9" + Math.round(single_price));
                        } else if (this.viewHolder.radio_single_piece_price.isChecked()) {
                            float edit_text_price = 0;
                            if (!charSequence.toString().isEmpty()) {
                                edit_text_price = Float.parseFloat(charSequence.toString());
                            }
                            Float single_price = (Float.parseFloat(viewHolder.edit_add_margin.getText().toString()) + edit_text_price);
                            productObjs.get(position).setSingle_piece_price(String.valueOf(Math.round(single_price)));
                            this.viewHolder.txt_single_pc_product_price.setText("Price for single Pc.: " + "\u20B9" + Math.round(single_price));
                        }

                        isValidate(this.viewHolder.radio_single_piece_per.isChecked(),
                                productObjs.get(position).getPublic_price(), this.viewHolder.edit_add_margin.getText().toString(), this.viewHolder);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            try {
                updatesinglePrice(productObjs.get(position).getPublic_price());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    public void clearFocus() {
        KeyboardUtils.hideKeyboard((Activity) context);
        View currentFocus = ((Activity) context).getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
    }

    public void setSystem_size(ArrayList<String> system_size) {
        this.system_size = system_size;
    }

    public List<ProductObj> getProductObjs() {
        return productObjs;
    }

    private void isValidate(boolean isPerChecked, String product_price, String margin_price, CustomViewHolder customViewHolder) {
        float validation_price = 0;
        validation_price = ((Float.parseFloat(product_price) / 100) * 10);
        if (validation_price < 60) {
            validation_price = 60;
        }
        float max_margin_allowed = Math.max(Float.parseFloat(product_price) * 10 / 100, 60);
        float max_percentage_allowed = (max_margin_allowed) / Float.parseFloat(product_price) * 100;

        float new_price = Float.parseFloat(calculateCurrentPrice(isPerChecked, margin_price, product_price));
        Log.e(TAG, "isValidate: ====>" + new_price + "Product Price =====>" + product_price + "Validation Price" + validation_price);
        if ((new_price - Float.parseFloat(product_price)) > validation_price) {
            if (isPerChecked) {
                customViewHolder.edit_add_margin.setError("Margin percentage must be <= " + max_percentage_allowed + "%");
                return;
            } else {
                customViewHolder.edit_add_margin.setError("Margin amount must be <= " + max_margin_allowed);
                return;
            }

        }

    }

    private void updateSizeUI(final ArrayList<String> sizes1, FlexboxLayout root, final ProductObj product, LinearLayout linear_select_size_container) {
        if (sizes1 != null && sizes1.size() > 0) {
            final ArrayList<String> string_size = new ArrayList<>();
            string_size.addAll(sizes1);

            if (string_size != null && string_size.size() > 0) {
                linear_select_size_container.setVisibility(View.VISIBLE);
                root.removeAllViews();
                for (int j = 0; j < string_size.size(); j++) {
                    final CheckBox checkBox = new CheckBox(context);
                    checkBox.setId(j);
                    FlexboxLayout.LayoutParams check_lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
                    check_lp.setMargins(0, 0, 32, 12);
                    setCheckBoxColor(checkBox, R.color.color_primary, R.color.purchase_medium_gray);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        checkBox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    }
                    checkBox.setText(string_size.get(j));
                    checkBox.setTextColor(ContextCompat.getColor(context, R.color.purchase_dark_gray));
                    checkBox.setPadding(0, 0, 20, 0);
                    root.addView(checkBox, check_lp);
                    final int finalJ = j;

                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            if (isChecked) {
                                ArrayList<String> temp_size;
                                temp_size = product.getAvailable_sizes();
                                if (temp_size == null) {
                                    temp_size = new ArrayList<>();
                                }
                                temp_size.add(string_size.get(finalJ));
                                product.setAvailable_sizes(temp_size);
                            } else {
                                ArrayList<String> temp_size;
                                temp_size = product.getAvailable_sizes();
                                if (temp_size != null) {
                                    temp_size.remove(string_size.get(finalJ));
                                    product.setAvailable_sizes(temp_size);
                                }
                            }
                        }
                    });
                    // Don't selected always blank state
                   /* if (product.getAvailable_size_string() != null) {
                        String[] splited = product.getAvailable_size_string().split(",");
                        ArrayList<String> s1 = new ArrayList<>(Arrays.asList(splited));
                        if (s1.contains(string_size.get(finalJ))) {
                            checkBox.setChecked(true);
                        }
                    }*/

                    checkBox.setLayoutParams(check_lp);
                }
            }
        } else {
            linear_select_size_container.setVisibility(View.GONE);
            root.removeAllViews();
            product.setAvailable_sizes(null);
        }


    }

    public void setCheckBoxColor(CheckBox checkBox, int checkedColor, int uncheckedColor) {
        checkBox.setButtonDrawable(ContextCompat.getDrawable(context, R.drawable.checkbox_selector));
    }


    private String calculateCurrentPrice(boolean isPer, String value, String product_price) {
        String new_price = "0";
        if (isPer) {
            // For Percentage
            Float price = ((Float.parseFloat(product_price) / 100) * Float.parseFloat(value)) + Float.parseFloat(product_price);
            new_price = price.toString();
        } else {
            // For Fixed Price
            Float price = (Float.parseFloat(value)) + Float.parseFloat(product_price);
            new_price = price.toString();
        }

        return new_price;
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.product_img)
        SimpleDraweeView product_img;

        @BindView(R.id.txt_design_number)
        TextView txt_design_number;

        @BindView(R.id.txt_price_per_design)
        TextView txt_price_per_design;

        @BindView(R.id.edit_add_margin)
        EditText edit_add_margin;

        @BindView(R.id.rg_add_margin)
        RadioGroup rg_add_margin;

        @BindView(R.id.radio_single_piece_per)
        RadioButton radio_single_piece_per;

        @BindView(R.id.radio_single_piece_price)
        RadioButton radio_single_piece_price;

        @BindView(R.id.txt_single_pc_product_price)
        TextView txt_single_pc_product_price;

        @BindView(R.id.txt_common_margin_note1)
        TextView txt_common_margin_note1;

        @BindView(R.id.txt_common_margin_note2)
        TextView txt_common_margin_note2;

        @BindView(R.id.linear_select_size_container)
        LinearLayout linear_select_size_container;

        @BindView(R.id.flex_select_size)
        FlexboxLayout flex_select_size;

        public AddMarginEditListener addMarginEditListener;

        public CustomViewHolder(View view, AddMarginEditListener addMarginEditListener) {
            super(view);
            ButterKnife.bind(this, view);

            this.addMarginEditListener = addMarginEditListener;
            edit_add_margin.addTextChangedListener(addMarginEditListener);
        }
    }
}
