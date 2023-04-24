package com.wishbook.catalog.home.catalog.details;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.CategoryTree;
import com.wishbook.catalog.commonmodels.responses.EnumGroupResponse;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterBottomDialog extends BottomSheetDialogFragment {


    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.filter_group)
    RadioGroup filter_group;

    @BindView(R.id.txt_select_all)
    TextView txt_select_all;

    @BindView(R.id.txt_title)
    TextView txt_title;

    String type;

    boolean isProductTypeBoth;


    FilterBottomDialog.FilterBottomSelectListener filterBottomSelectListener;

    String previous_selected_tag = null;

    public static FilterBottomDialog newInstance(Bundle bundle) {
        FilterBottomDialog f = new FilterBottomDialog();
        if (bundle != null) {
            f.setArguments(bundle);
        }
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.filter_bottom_sheet_modal, container, false);
        ButterKnife.bind(this, v);
        Application_Singleton singleton = new Application_Singleton();
        singleton.trackScreenView(getClass().getSimpleName(), getActivity());
        if (getArguments() != null) {
            if (getArguments().getString("type") != null) {
                type = getArguments().getString("type");
                if(getArguments().getBoolean("is_product_type_both")) {
                    isProductTypeBoth = true;
                }
                if (getArguments().getString("previous_selected_tag") != null) {
                    previous_selected_tag = getArguments().getString("previous_selected_tag");
                }

                if (type.equalsIgnoreCase("category")) {
                    txt_title.setText("Category");
                    txt_select_all.setVisibility(View.VISIBLE);
                    getCategory();
                } else if (type.equalsIgnoreCase("sell_full_catalog")) {
                    txt_title.setText("Product Availability");
                    getProductAvailability();
                } else if (type.equalsIgnoreCase("product_type")) {
                    txt_title.setText("Product Type");
                    getCatalogType();
                } else if(type.equalsIgnoreCase("collection_type")) {
                    txt_title.setText("View Type");
                    getCollectionType();
                } else if(type.equalsIgnoreCase("pending_order_items")){
                    txt_title.setText("Sort by");
                    getPendingOrderSort();
                } else {
                    txt_title.setText(type);
                }
            }
        }

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        txt_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filterBottomSelectListener != null) {
                    filterBottomSelectListener.onCheck("");
                }
                dismiss();
            }
        });


        return v;
    }


    public interface FilterBottomSelectListener {
        void onCheck(String check);
    }

    public void setFilterBottomSelectListener(FilterBottomSelectListener filterBottomSelectListener) {
        this.filterBottomSelectListener = filterBottomSelectListener;
    }

    private void getCategory() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        //StaticFunctions.showProgressbar(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "category", "") + "?parent=10", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                //StaticFunctions.hideProgressbar(getActivity());
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    // StaticFunctions.hideProgressbar(getActivity());
                    CategoryTree[] ct = Application_Singleton.gson.fromJson(response, CategoryTree[].class);
                    RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 30, 0, 30);
                    filter_group.removeAllViews();
                    if (ct != null) {
                        if (ct.length > 0) {
                            ArrayList<EnumGroupResponse> enumGroupResponses = new ArrayList<EnumGroupResponse>();
                            for (int i = 0; i < ct.length; i++) {
                                final RadioButton radioButtonView = new RadioButton(getActivity());
                                radioButtonView.setPadding(40, 0, 0, 0);
                                radioButtonView.setId(ct[i].getId());
                                radioButtonView.setText(ct[i].getcategory_name());
                                radioButtonView.setLayoutParams(lp);
                                radioButtonView.setTag(ct[i].getId());
                                filter_group.addView(radioButtonView);

                                if (previous_selected_tag != null && ct[i].getId().toString().equalsIgnoreCase(previous_selected_tag)) {
                                    radioButtonView.setChecked(true);
                                }

                                radioButtonView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                        if (b) {
                                            if (filterBottomSelectListener != null) {
                                                filterBottomSelectListener.onCheck(radioButtonView.getTag().toString());
                                            }
                                            dismiss();
                                        }

                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                // StaticFunctions.hideProgressbar(getActivity());
            }
        });
    }

    private void getProductAvailability() {
        ArrayList<EnumGroupResponse> enumGroupResponseArrayList = new ArrayList<>();
        EnumGroupResponse enum1 = new EnumGroupResponse("1", "Full Catalog ", "true");
        EnumGroupResponse enum2 = new EnumGroupResponse("2", "Single Pcs.", "false");
        EnumGroupResponse enum3 = new EnumGroupResponse("3", "Both", "");
        enumGroupResponseArrayList.add(enum1);
        enumGroupResponseArrayList.add(enum2);
        enumGroupResponseArrayList.add(enum3);

        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 30, 0, 30);
        for (int i = 0; i < enumGroupResponseArrayList.size(); i++) {
            final RadioButton radioButtonView = new RadioButton(getActivity());
            radioButtonView.setPadding(40, 0, 0, 0);
            radioButtonView.setId(Integer.parseInt(enumGroupResponseArrayList.get(i).getId()));
            radioButtonView.setText(enumGroupResponseArrayList.get(i).getValue());
            radioButtonView.setLayoutParams(lp);
            radioButtonView.setTag(enumGroupResponseArrayList.get(i).getTag());
            filter_group.addView(radioButtonView);


            if(previous_selected_tag == null) {
                // Default Selection
                if(enumGroupResponseArrayList.get(i).getTag().equals("")) {
                    radioButtonView.setChecked(true);
                }
            } else {
                if (previous_selected_tag != null && enumGroupResponseArrayList.get(i).getTag().equalsIgnoreCase(previous_selected_tag)) {
                    radioButtonView.setChecked(true);
                }

            }

            radioButtonView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        if (filterBottomSelectListener != null) {
                            filterBottomSelectListener.onCheck(radioButtonView.getTag().toString());
                        }
                        dismiss();
                    }

                }
            });
        }

    }

    private void getCatalogType() {
        ArrayList<EnumGroupResponse> enumGroupResponseArrayList = new ArrayList<>();
        EnumGroupResponse enum1 = new EnumGroupResponse("1", "Catalog", Constants.PRODUCT_TYPE_CAT);
        EnumGroupResponse enum2 = new EnumGroupResponse("2", "Non-Catalog", Constants.PRODUCT_TYPE_NON + "," + Constants.PRODUCT_TYPE_SCREEN);

        enumGroupResponseArrayList.add(enum1);
        enumGroupResponseArrayList.add(enum2);
        if(isProductTypeBoth) {
            EnumGroupResponse enum3 = new EnumGroupResponse("3", "Both", "");
            enumGroupResponseArrayList.add(enum3);
        }

        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 30, 0, 30);
        for (int i = 0; i < enumGroupResponseArrayList.size(); i++) {
            final RadioButton radioButtonView = new RadioButton(getActivity());
            radioButtonView.setPadding(40, 0, 0, 0);
            radioButtonView.setId(Integer.parseInt(enumGroupResponseArrayList.get(i).getId()));
            radioButtonView.setText(enumGroupResponseArrayList.get(i).getValue());
            radioButtonView.setLayoutParams(lp);
            radioButtonView.setTag(enumGroupResponseArrayList.get(i).getTag());
            filter_group.addView(radioButtonView);



            if(previous_selected_tag == null) {
                // Default Selection
                if(isProductTypeBoth) {
                    if( enumGroupResponseArrayList.get(i).getTag().equals("")) {
                        radioButtonView.setChecked(true);
                    }
                } else {
                    if( enumGroupResponseArrayList.get(i).getTag().equals(Constants.PRODUCT_TYPE_CAT)) {
                        radioButtonView.setChecked(true);
                    }
                }

            } else {
                if (previous_selected_tag != null && enumGroupResponseArrayList.get(i).getTag().equals(previous_selected_tag)) {
                    radioButtonView.setChecked(true);
                }
            }

            radioButtonView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        if (filterBottomSelectListener != null) {
                            filterBottomSelectListener.onCheck(radioButtonView.getTag().toString());
                        }
                        dismiss();
                    }
                }
            });
        }


    }

    private void getCollectionType() {
        ArrayList<EnumGroupResponse> enumGroupResponseArrayList = new ArrayList<>();
        EnumGroupResponse enum1 = new EnumGroupResponse("1", "Collection", Constants.COLLECTION_TYPE_CAT);
        EnumGroupResponse enum2 = new EnumGroupResponse("2", "Single Pcs", Constants.COLLECTION_TYPE_PRODUCT);
        enumGroupResponseArrayList.add(enum1);
        enumGroupResponseArrayList.add(enum2);

        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 30, 0, 30);
        for (int i = 0; i < enumGroupResponseArrayList.size(); i++) {
            final RadioButton radioButtonView = new RadioButton(getActivity());
            radioButtonView.setPadding(40, 0, 0, 0);
            radioButtonView.setId(Integer.parseInt(enumGroupResponseArrayList.get(i).getId()));
            radioButtonView.setText(enumGroupResponseArrayList.get(i).getValue());
            radioButtonView.setLayoutParams(lp);
            radioButtonView.setTag(enumGroupResponseArrayList.get(i).getTag());
            filter_group.addView(radioButtonView);

            if(previous_selected_tag == null) {
                // Default Selection
                if( enumGroupResponseArrayList.get(i).getTag().equals(Constants.PRODUCT_TYPE_CAT)) {
                    radioButtonView.setChecked(true);
                }
            } else {
                if (previous_selected_tag != null && enumGroupResponseArrayList.get(i).getTag().equals(previous_selected_tag)) {
                    radioButtonView.setChecked(true);
                }
            }

            radioButtonView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        if (filterBottomSelectListener != null) {
                            filterBottomSelectListener.onCheck(radioButtonView.getTag().toString());
                        }
                        dismiss();
                    }
                }
            });
        }


    }

    private void getPendingOrderSort() {
        ArrayList<EnumGroupResponse> enumGroupResponseArrayList = new ArrayList<>();
        EnumGroupResponse enum1 = new EnumGroupResponse("1", "Pending quantity", "-pending_qty");
        EnumGroupResponse enum2 = new EnumGroupResponse("2", "Expected date", "expected_date");
        EnumGroupResponse enum3 = new EnumGroupResponse("3", "Pending since", "pending_since");
        enumGroupResponseArrayList.add(enum1);
        enumGroupResponseArrayList.add(enum2);
        enumGroupResponseArrayList.add(enum3);

        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 30, 0, 30);
        for (int i = 0; i < enumGroupResponseArrayList.size(); i++) {
            final RadioButton radioButtonView = new RadioButton(getActivity());
            radioButtonView.setPadding(40, 0, 0, 0);
            radioButtonView.setId(Integer.parseInt(enumGroupResponseArrayList.get(i).getId()));
            radioButtonView.setText(enumGroupResponseArrayList.get(i).getValue());
            radioButtonView.setLayoutParams(lp);
            radioButtonView.setTag(enumGroupResponseArrayList.get(i).getTag());
            filter_group.addView(radioButtonView);

            if (previous_selected_tag == null) {
                // Default Selection

            } else {
                if (previous_selected_tag != null && enumGroupResponseArrayList.get(i).getTag().equals(previous_selected_tag)) {
                    radioButtonView.setChecked(true);
                }
            }

            radioButtonView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        if (filterBottomSelectListener != null) {
                            filterBottomSelectListener.onCheck(radioButtonView.getTag().toString());
                        }
                        dismiss();
                    }
                }
            });
        }
    }
}
