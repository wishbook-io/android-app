package com.wishbook.catalog.home.adapters;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
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
import com.wishbook.catalog.commonmodels.ProductMyDetail;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.home.models.ProductObj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartStopSellingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private List<ProductObj> productObjs = new ArrayList<>();
    UserInfo userInfo;
    ArrayList<String> chars;
    boolean isShowSize;
    private ProductMyDetail productMyDetails;


    public StartStopSellingAdapter(Context context, @NonNull List<ProductObj> productObjs, ArrayList<String> chars, boolean isShowSize, ProductMyDetail productMyDetail) {
        this.context = context;
        this.productObjs = productObjs;
        this.chars = chars;
        this.isShowSize = isShowSize;
        this.productMyDetails = productMyDetail;
        userInfo = UserInfo.getInstance(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.start_stop_selling_item, parent, false);
        return new ProductStartStopViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final ProductObj product = productObjs.get(position);
        final ProductMyDetail productMyDetail = productMyDetails.getProducts().get(position);
        if (holder instanceof StartStopSellingAdapter.ProductStartStopViewHolder) {
            if (product.getImage() != null && product.getImage().getThumbnail_small() != null) {
                StaticFunctions.loadFresco(context, product.getImage().getThumbnail_small(), ((StartStopSellingAdapter.ProductStartStopViewHolder) holder).prod_img);
            }
            ((ProductStartStopViewHolder) holder).txt_design_name.setText(product.getSku());
            ((ProductStartStopViewHolder) holder).txt_price_per_design.setText(product.getPublic_price());

            if (isShowSize) {
                ((ProductStartStopViewHolder) holder).linear_sell_yes_no_container.setVisibility(View.GONE);
                ((ProductStartStopViewHolder) holder).linear_select_size_container.setVisibility(View.VISIBLE);
                updateSizeUI(chars, ((ProductStartStopViewHolder) holder).flex_select_size, product, productMyDetail,((ProductStartStopViewHolder) holder).linear_select_size_container);
            } else {
                ((ProductStartStopViewHolder) holder).linear_sell_yes_no_container.setVisibility(View.VISIBLE);
                ((ProductStartStopViewHolder) holder).linear_select_size_container.setVisibility(View.GONE);

                ((ProductStartStopViewHolder) holder).radio_group_start_stop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        if (((ProductStartStopViewHolder) holder).radio_start.isChecked()) {
                            product.setIs_enable(true);
                        } else {
                            product.setIs_enable(false);
                        }
                    }
                });

                if(productMyDetail.isCurrently_selling()) {
                    ((ProductStartStopViewHolder) holder).radio_start.setChecked(true);
                } else {
                    ((ProductStartStopViewHolder) holder).radio_stop.setChecked(true);
                }
            }
        }
    }

    public List<ProductObj> getProductObjs() {
        return productObjs;
    }


    @Override
    public int getItemCount() {
        return productObjs.size();
    }

    private void updateSizeUI(final ArrayList<String> sizes1, FlexboxLayout root,
                              final ProductObj product,
                              final ProductMyDetail productMyDetail,
                              LinearLayout linear_select_size_container) {
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
                    if (productMyDetail.getAvailable_sizes() != null) {
                        String[] splited = productMyDetail.getAvailable_sizes().split(",");
                        ArrayList<String> s1 = new ArrayList<>(Arrays.asList(splited));
                        if (s1.contains(string_size.get(finalJ))) {
                            checkBox.setChecked(true);
                        }
                    }

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


    class ProductStartStopViewHolder extends RecyclerView.ViewHolder {

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

        @BindView(R.id.radio_group_start_stop)
        RadioGroup radio_group_start_stop;

        @BindView(R.id.radio_start)
        RadioButton radio_start;

        @BindView(R.id.radio_stop)
        RadioButton radio_stop;

        ProductStartStopViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
