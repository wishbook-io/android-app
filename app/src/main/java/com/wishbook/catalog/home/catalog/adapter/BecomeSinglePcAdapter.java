package com.wishbook.catalog.home.catalog.adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayout;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.ResponseBrandDiscount;
import com.wishbook.catalog.home.models.ProductObj;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BecomeSinglePcAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = BecomeSinglePcAdapter.class.getSimpleName();
    Context context;
    private List<ProductObj> productObjs = new ArrayList<>();
    UserInfo userInfo;
    ArrayList<String> system_size;
    ResponseBrandDiscount discountRule;
    public String singlePCAddPer, singlePcAddPrice;
    boolean isFullCatalog = false;


    public BecomeSinglePcAdapter(Context context, @NonNull List<ProductObj> productObjs, @NonNull ArrayList<String> system_size,boolean isFullCatalog) {
        this.context = context;
        this.productObjs = productObjs;
        this.system_size = system_size;
        userInfo = UserInfo.getInstance(context);
        this.isFullCatalog = isFullCatalog;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.start_stop_selling_item, parent, false);
        return new SinglePcSizeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final ProductObj product = productObjs.get(position);
        if (holder instanceof SinglePcSizeViewHolder) {
            ((SinglePcSizeViewHolder) holder).radio_group_start_stop.setOnCheckedChangeListener(null);
            ((SinglePcSizeViewHolder) holder).linear_sell_yes_no_container.setVisibility(View.VISIBLE);
            ((SinglePcSizeViewHolder) holder).txt_design_name.setText(product.getSku());
            ((SinglePcSizeViewHolder) holder).linear_select_size_container.setVisibility(View.VISIBLE);

            if (product.getImage() != null && product.getImage().getThumbnail_small() != null) {
                StaticFunctions.loadFresco(context, product.getImage().getThumbnail_small(), ((SinglePcSizeViewHolder) holder).prod_img);
            }

            if(!isFullCatalog) {
                ((SinglePcSizeViewHolder) holder).price_label.setText("Single Billing Price");
                ((SinglePcSizeViewHolder) holder).txt_price_per_design.setText("" +updateSinglePrice(product.getMwp_price(),position) );
                ((SinglePcSizeViewHolder) holder).linear_sell_yes_no_container.setVisibility(View.VISIBLE);
                if (product.isIs_enable()) {
                    ((SinglePcSizeViewHolder) holder).radio_start.setChecked(true);
                    updateSizeUI(system_size, ((SinglePcSizeViewHolder) holder).flex_select_size, product, ((SinglePcSizeViewHolder) holder).linear_select_size_container);
                } else {
                    ((SinglePcSizeViewHolder) holder).radio_stop.setChecked(true);
                    ((SinglePcSizeViewHolder) holder).linear_select_size_container.setVisibility(View.GONE);
                    ((SinglePcSizeViewHolder) holder).flex_select_size.removeAllViews();
                    product.setAvailable_sizes(null);
                }

                ((SinglePcSizeViewHolder) holder).radio_group_start_stop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        if (((SinglePcSizeViewHolder) holder).radio_start.isChecked()) {
                            product.setIs_enable(true);
                            updateSizeUI(system_size, ((SinglePcSizeViewHolder) holder).flex_select_size, product, ((SinglePcSizeViewHolder) holder).linear_select_size_container);
                        } else {
                            product.setIs_enable(false);
                            ((SinglePcSizeViewHolder) holder).linear_select_size_container.setVisibility(View.GONE);
                            ((SinglePcSizeViewHolder) holder).flex_select_size.removeAllViews();
                            product.setAvailable_sizes(null);
                        }
                    }
                });
            } else {
                ((SinglePcSizeViewHolder) holder).txt_price_per_design.setText("" +updateFullPrice(product.getMwp_price(),position) );
                ((SinglePcSizeViewHolder) holder).price_label.setText("Full Billing Price");
                ((SinglePcSizeViewHolder) holder).linear_sell_yes_no_container.setVisibility(View.GONE);
                ((SinglePcSizeViewHolder) holder).linear_select_size_container.setVisibility(View.GONE);
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




    public double updateSinglePrice(double mwp_single_price, int position) {
        try {
            // formula ---- billing price = [(wholesale price+margin)-B.D]
            if (singlePCAddPer != null) {
                Log.e(TAG, "updateSinglePrice: Position ----->"+position + "\n SingleAddPer"+singlePCAddPer );
                double margin = 0;
                if(!singlePCAddPer.equalsIgnoreCase("0"))
                    margin = (mwp_single_price / 100) * Float.parseFloat(singlePCAddPer);
                double discount = 0;
                if (discountRule.getSingle_pcs_discount() != null && Double.parseDouble(discountRule.getSingle_pcs_discount()) > 0) {
                    discount = ((mwp_single_price + margin) / 100) * Double.parseDouble(discountRule.getSingle_pcs_discount());
                }
                return ((mwp_single_price + margin) - discount);
            } else if (singlePcAddPrice != null) {
                double margin = Double.parseDouble(singlePcAddPrice);
                double discount = 0;
                if (discountRule.getSingle_pcs_discount() != null && Double.parseDouble(discountRule.getSingle_pcs_discount()) > 0) {
                    discount = ((mwp_single_price + margin) / 100) * Double.parseDouble(discountRule.getSingle_pcs_discount());
                }
                return ((mwp_single_price + margin) - discount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mwp_single_price;
    }

    public double updateFullPrice(double mwp_price, int position) {
        try {

            // formula ---- billing price = [(wholesale price-B.D]

            double discount = 0;
            if (discountRule.getCash_discount() != null && Double.parseDouble(discountRule.getCash_discount()) > 0) {
                discount = ((mwp_price) / 100) * Double.parseDouble(discountRule.getCash_discount());
            }
            return (mwp_price - discount);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mwp_price;
    }

    public void setCheckBoxColor(CheckBox checkBox, int checkedColor, int uncheckedColor) {
        checkBox.setButtonDrawable(ContextCompat.getDrawable(context, R.drawable.checkbox_selector));
    }

    public ResponseBrandDiscount getDiscountRule() {
        return discountRule;
    }

    public void setDiscountRule(ResponseBrandDiscount discountRule) {
        this.discountRule = discountRule;
    }

    @Override
    public int getItemCount() {
        return productObjs.size();
    }

    public List<ProductObj> getProductObjs() {
        return productObjs;
    }


    public List<ProductObj> getSellingProductObj() {
        List<ProductObj> selling_product = new ArrayList<>();
        for (ProductObj p :
                productObjs) {
            if (p.isChecked()) {
                selling_product.add(p);
            }
        }
        return selling_product;
    }


    class SinglePcSizeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.prod_img)
        SimpleDraweeView prod_img;

        @BindView(R.id.txt_design_name)
        TextView txt_design_name;

        @BindView(R.id.txt_price_per_design)
        TextView txt_price_per_design;
        @BindView(R.id.linear_select_size_container)
        LinearLayout linear_select_size_container;

        @BindView(R.id.linear_sell_yes_no_container)
        LinearLayout linear_sell_yes_no_container;

        @BindView(R.id.flex_select_size)
        FlexboxLayout flex_select_size;

        @BindView(R.id.radio_start)
        RadioButton radio_start;

        @BindView(R.id.radio_stop)
        RadioButton radio_stop;

        @BindView(R.id.radio_group_start_stop)
        RadioGroup radio_group_start_stop;

        @BindView(R.id.price_label)
                TextView price_label;

        SinglePcSizeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
