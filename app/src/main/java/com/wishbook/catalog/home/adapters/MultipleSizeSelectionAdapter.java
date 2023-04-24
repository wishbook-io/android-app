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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayout;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MultipleSizeSelectionAdapter extends RecyclerView.Adapter<MultipleSizeSelectionAdapter.ViewHolder> {


    Context context;
    ArrayList<Response_catalogMini> response_catalogMiniArrayList;

    public MultipleSizeSelectionAdapter(Context context, ArrayList<Response_catalogMini> response_catalogMiniArrayList) {
        this.context = context;
        this.response_catalogMiniArrayList = response_catalogMiniArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.multiple_size_select_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.txt_design_number.setText(response_catalogMiniArrayList.get(i).getTitle());
        viewHolder.txt_catalog_name.setText(response_catalogMiniArrayList.get(i).getCatalog_title());
        if(response_catalogMiniArrayList.get(i).getImage()!=null
                && response_catalogMiniArrayList.get(i).getImage().getThumbnail_small()!=null) {
            StaticFunctions.loadFresco(context, response_catalogMiniArrayList.get(i).getImage().getThumbnail_small(), viewHolder.product_img);
        }
        if (response_catalogMiniArrayList.get(i).getAvailable_sizes() != null &&
                !response_catalogMiniArrayList.get(i).getAvailable_sizes().isEmpty()) {
            ArrayList<String> temp = new ArrayList<>(Arrays.asList(response_catalogMiniArrayList.get(i).getAvailable_sizes().split(",")));
            updateSizeUI(temp, viewHolder.flex_select_size, viewHolder.linear_select_size_container,response_catalogMiniArrayList.get(i));
        } else {
            viewHolder.linear_select_size_container.setVisibility(View.GONE);
            viewHolder.flex_select_size.removeAllViews();
            response_catalogMiniArrayList.get(i).setAvailable_sizes_array(null);
        }
    }

    private void updateSizeUI(ArrayList<String> sizes1, FlexboxLayout root, LinearLayout linear_select_size_container, final Response_catalogMini response_catalogMini) {
        if (sizes1 != null && sizes1.size() > 0) {
            ArrayList<String> string_size = new ArrayList<>();
            string_size = sizes1;
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
                    if (response_catalogMini.getAvailable_sizes_array() != null) {
                        ArrayList<String> temp_size;
                        temp_size = response_catalogMini.getAvailable_sizes_array();
                        if (temp_size.contains(string_size.get(finalJ))) {
                            checkBox.setChecked(true);
                        }
                    }
                    final ArrayList<String> finalString_size = string_size;
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            if (isChecked) {
                                ArrayList<String> temp_size;
                                temp_size = response_catalogMini.getAvailable_sizes_array();
                                if (temp_size == null) {
                                    temp_size = new ArrayList<>();
                                }
                                temp_size.add(finalString_size.get(finalJ));
                                response_catalogMini.setAvailable_sizes_array(temp_size);
                            } else {
                                ArrayList<String> temp_size;
                                temp_size = response_catalogMini.getAvailable_sizes_array();
                                if (temp_size != null) {
                                    temp_size.remove(finalString_size.get(finalJ));
                                    response_catalogMini.setAvailable_sizes_array(temp_size);
                                }
                            }
                        }
                    });

                    checkBox.setLayoutParams(check_lp);
                }
            }
        } else {
            linear_select_size_container.setVisibility(View.GONE);
            root.removeAllViews();
            response_catalogMini.setAvailable_sizes_array(null);

        }


    }

    public void setCheckBoxColor(CheckBox checkBox, int checkedColor, int uncheckedColor) {
        checkBox.setButtonDrawable(ContextCompat.getDrawable(context, R.drawable.checkbox_selector));
    }


    public boolean validateSizeSelected() {
        for (Response_catalogMini r :
                response_catalogMiniArrayList) {
            if ((r.getAvailable_sizes_array() == null || r.getAvailable_sizes_array().isEmpty() || r.getAvailable_sizes_array().size() == 0)) {
                Toast.makeText(context,"Please select size",Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    @Override
    public int getItemCount() {
        return response_catalogMiniArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.product_img)
        SimpleDraweeView product_img;

        @BindView(R.id.txt_design_number)
        TextView txt_design_number;

        @BindView(R.id.txt_catalog_name)
        TextView txt_catalog_name;

        @BindView(R.id.linear_select_size_container)
        LinearLayout linear_select_size_container;

        @BindView(R.id.flex_select_size)
        FlexboxLayout flex_select_size;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
