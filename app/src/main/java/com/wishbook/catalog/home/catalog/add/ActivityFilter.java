package com.wishbook.catalog.home.catalog.add;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.CategoryTree;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.EnumGroupResponse;
import com.wishbook.catalog.commonmodels.responses.ResponseCategoryEvp;
import com.wishbook.catalog.commonmodels.responses.ResponseSavedFilters;
import com.wishbook.catalog.home.models.Response_Brands;
import com.wishbook.catalog.home.models.Response_Followed_Brands;
import com.wishbook.catalog.login.models.Response_States;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityFilter extends AppCompatActivity implements CategorySelectAdapter.OnCheckedListener, FilterSelectAdapter.OnFabricCheckedListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.relative_category)
    RelativeLayout relative_category;
    @BindView(R.id.category_label)
    TextView category_label;
    @BindView(R.id.category_count)
    TextView category_count;
    @BindView(R.id.selected_category)
    TextView selected_category;

    @BindView(R.id.relative_strich)
    RelativeLayout relative_strich;
    @BindView(R.id.strich_label)
    TextView strich_label;
    @BindView(R.id.strich_count)
    TextView strich_count;

    @BindView(R.id.relative_brand)
    RelativeLayout relative_brand;
    @BindView(R.id.brand_label)
    TextView brand_label;
    @BindView(R.id.brand_count)
    TextView brand_count;

    @BindView(R.id.relative_fabric)
    RelativeLayout relative_fabric;
    @BindView(R.id.fabric_label)
    TextView fabric_label;
    @BindView(R.id.fabric_count)
    TextView fabric_count;


    @BindView(R.id.relative_work)
    RelativeLayout relative_work;
    @BindView(R.id.work_label)
    TextView work_label;
    @BindView(R.id.work_count)
    TextView work_count;


    @BindView(R.id.relative_style)
    RelativeLayout relative_style;
    @BindView(R.id.style_label)
    TextView style_label;
    @BindView(R.id.style_count)
    TextView style_count;

    @BindView(R.id.relative_state)
    RelativeLayout relative_state;
    @BindView(R.id.state_label)
    TextView state_label;
    @BindView(R.id.state_count)
    TextView state_count;


    @BindView(R.id.relative_price)
    RelativeLayout relative_price;
    @BindView(R.id.price_label)
    TextView price_label;
    @BindView(R.id.price_count)
    TextView price_count;


    @BindView(R.id.relative_ctype)
    RelativeLayout relative_ctype;
    @BindView(R.id.catalog_label)
    TextView catalog_label;
    @BindView(R.id.catalog_count)
    TextView catalog_count;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    @BindView(R.id.catalog_container)
    LinearLayout catalog_container;
    @BindView(R.id.public_supplier_container)
    LinearLayout public_supplier_container;
    @BindView(R.id.linear_showDisabled)
    LinearLayout linear_showDisabled;
    @BindView(R.id.radioGroup_public)
    CheckBox radiogroupsupplier;
    @BindView(R.id.check_buyer_disable)
    CheckBox checkBuyerDisable;
    @BindView(R.id.check_supplier_disable)
    CheckBox checkSupplierDisable;
    @BindView(R.id.linear_checked_trusted_seller)
    LinearLayout linear_checked_trusted_seller;
    @BindView(R.id.check_trusted_seller)
    CheckBox checkTrustedSeller;
    @BindView(R.id.linear_checked_near_me)
    LinearLayout linear_checked_near_me;
    @BindView(R.id.check_near_me)
    CheckBox check_near_me;

    @BindView(R.id.edit_search)
    EditText edit_search;
    @BindView(R.id.edit_catalog_title)
    EditText edit_catalog_title;

    @BindView(R.id.btn_clear_all)
    AppCompatButton btn_clear_all;

    @BindView(R.id.check_full_catalog)
    RadioButton check_full_catalog;
    @BindView(R.id.check_single_piece)
    RadioButton check_single_piece;
    @BindView(R.id.check_both)
    RadioButton check_both;


    // ### Catalog Availability ####


    // ### Catalog Type ####
    @BindView(R.id.radio_catalog)
    RadioButton radio_catalog;

    @BindView(R.id.radio_noncatalog)
    RadioButton radio_noncatalog;
    @BindView(R.id.linear_catalog_type)
    LinearLayout linear_catalog_type;

    @BindView(R.id.chk_prelaunch)
    CheckBox chk_prelaunch;

    @BindView(R.id.txt_view_more)
    TextView txt_view_more;


    boolean isToogle;

    String type;
    Context context;
    CategorySelectAdapter categoryAdapter;
    CategorySelectAdapter eavRadioAdapter;
    FilterSelectAdapter eavMultiAdpter;
    FilterSelectAdapter brandAdaper;
    FilterSelectAdapter fabricAdapter, workAdapter;
    FilterSelectAdapter priceAdapter;
    FilterSelectAdapter stateAdapter;
    CategorySelectAdapter styleAdapter;


    private Boolean is_buyer_disabled = false;
    private Boolean supplier_disabled = false;
    private Boolean is_disable = false;
    private Boolean is_supplier_approved = false;
    private Boolean from_brand = false;
    private boolean show_trusted_seller = false;
    private boolean show_near_me = false;
    private int min = 0, minPrice = 0;
    private int max = 0, maxPrice = 0;
    HashMap<String, String> previousMap = null;
    private boolean isOpenCatalog = false, isNonCatalog = false;

    ResponseSavedFilters responseSavedFilters;

    ResponseSavedFilters responseNonCatalogSavedFilters;

    String category_selected_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = ActivityFilter.this;
        StaticFunctions.initializeAppsee();
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.PRODUCT_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Products_Filter_screen");
        HashMap<String, String> prop = new HashMap<>();
        prop.put("visited", "true");
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(context, wishbookEvent);


        setContentView(R.layout.activity_filter_new);
        ButterKnife.bind(this);
        if (getIntent().getBooleanExtra("isNonCatalog", false)) {
            isNonCatalog = true;
        }
        setToolbar();
        if (getIntent().getBooleanExtra("from_public", false)) {
            type = "from_public";
            relative_state.setVisibility(View.VISIBLE);
        } else if (getIntent().getBooleanExtra("from_followed_brand", false)) {
            type = "from_followed_brand";
        } else if (getIntent().getBooleanExtra("from_received", false)) {
            type = "from_received";
        } else if (getIntent().getBooleanExtra("from_brand", false)) {
            type = "from_brand";
            from_brand = true;
            relative_brand.setVisibility(View.GONE);
            relative_state.setVisibility(View.VISIBLE);
        } else if (getIntent().getBooleanExtra("from_category", false)) {
            type = "from_category";
            relative_category.setVisibility(View.GONE);
            relative_state.setVisibility(View.VISIBLE);
        } else {
            type = "Other";
            relative_brand.setVisibility(View.VISIBLE);
            relative_category.setVisibility(View.VISIBLE);
        }


        if (getIntent().getSerializableExtra("previousParam") != null) {
            previousMap = (HashMap<String, String>) getIntent().getSerializableExtra("previousParam");
            if (previousMap != null && previousMap.containsKey("category")) {
                category_selected_id = previousMap.get("category");
                getCategoryEavAttribute(category_selected_id, false);
            }
            firstTimeChecked();
        }
        initView();
    }

    private void initView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mLayoutManager.setAutoMeasureEnabled(true);
        txt_view_more.setPaintFlags(txt_view_more.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        recyclerview.setLayoutManager(mLayoutManager);
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //  if (!edit_search.getText().toString().isEmpty()) {
                if (relative_category.isSelected()) {
                    if (categoryAdapter != null) {
                        categoryAdapter.filter(charSequence.toString());
                    }
                } else if (relative_brand.isSelected()) {
                    if (brandAdaper != null) {
                        brandAdaper.filter(charSequence.toString());
                    }
                } else if (relative_fabric.isSelected()) {
                    if (fabricAdapter != null) {
                        fabricAdapter.filter(charSequence.toString());
                    }
                } else if (relative_work.isSelected()) {
                    if (workAdapter != null) {
                        workAdapter.filter(charSequence.toString());
                    }
                } else if (relative_price.isSelected()) {

                } else if (relative_state.isSelected()) {
                    if (stateAdapter != null) {
                        stateAdapter.filter(charSequence.toString());
                    }
                }
                //   }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        if (type != null) {
            if (type.equals("from_category")) {
                getSelection(relative_brand);
            } else {
                getSelection(relative_ctype);
            }
        } else {
            getSelection(relative_ctype);
        }
        check_both.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                check_both.setChecked(true);
                check_full_catalog.setChecked(false);
                check_single_piece.setChecked(false);
            }
        });

        check_full_catalog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                check_both.setChecked(false);
                check_full_catalog.setChecked(true);
                check_single_piece.setChecked(false);
            }
        });

        check_single_piece.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                check_both.setChecked(false);
                check_full_catalog.setChecked(false);
                check_single_piece.setChecked(true);
            }
        });

        radio_catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radio_noncatalog.setChecked(false);
                radio_catalog.setChecked(true);
            }
        });

        radio_noncatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radio_noncatalog.setChecked(true);
                radio_catalog.setChecked(false);
            }
        });


        // Default hide state
        relative_style.setVisibility(View.GONE);
        relative_state.setVisibility(View.GONE);

        txt_view_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isToogle) {
                    // hide other filter
                    relative_style.setVisibility(View.GONE);
                    relative_state.setVisibility(View.GONE);
                    txt_view_more.setText("View more");
                    isToogle = false;
                } else {
                    // show other filter
                    relative_style.setVisibility(View.VISIBLE);
                    relative_state.setVisibility(View.VISIBLE);
                    isToogle = true;
                    txt_view_more.setText("View less");
                }
            }
        });


        btn_clear_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (responseSavedFilters != null) {
                    // Delete Filter
                    new MaterialDialog.Builder(context)
                            .content("Are you sure you want to delete this filter?")
                            .positiveText("YES")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                    callDeleteSavedFilter(responseSavedFilters.getId());
                                }
                            })
                            .negativeText("NO")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();

                } else {
                    // Save Filter
                    showSaveFilterDialog();
                }


            }
        });
        checkedListner();
    }


    private void setToolbar() {
        if (getIntent().getSerializableExtra("selected_saved_filter") != null) {
            responseSavedFilters = (ResponseSavedFilters) getIntent().getSerializableExtra("selected_saved_filter");
            toolbar.setTitle(responseSavedFilters.getTitle());

            btn_clear_all.setText("Delete");
        } else {
            toolbar.setTitle(getResources().getString(R.string.filter_title));
        }
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

    }

    @OnClick({R.id.relative_category, R.id.relative_strich, R.id.relative_brand, R.id.relative_fabric, R.id.relative_work, R.id.relative_style, R.id.relative_price, R.id.relative_state, R.id.relative_ctype})
    public void getSelection(View view) {
        view.setSelected(true);
        selectedBackground(view);
        unselectedBackground(view);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_clear_filter) {
            clearAllFilter();
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectedBackground(View view) {
        if (view.getId() == relative_category.getId()) {
            edit_search.setText("");
            edit_search.setVisibility(View.GONE);
            getCategory();
            relative_category.setSelected(true);
            relative_category.setBackgroundColor(getResources().getColor(R.color.white));
            category_label.setTextColor(getResources().getColor(R.color.color_primary));
            category_count.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            selected_category.setTextColor(getResources().getColor(R.color.white));
            selected_category.setVisibility(View.GONE);

        }

        if (view.getId() == relative_strich.getId()) {
            edit_search.setText("");
            edit_search.setVisibility(View.GONE);
            if (categoryAdapter != null && categoryAdapter.getSelectedItem().size() > 0) {
                getCategoryEavAttribute(categoryAdapter.getSelectedItem().get(0), true);
            } else if (category_selected_id != null) {
                getCategoryEavAttribute(category_selected_id, true);
            }
            relative_strich.setSelected(true);
            relative_strich.setBackgroundColor(getResources().getColor(R.color.white));
            strich_label.setTextColor(getResources().getColor(R.color.color_primary));
            strich_count.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
        }

        if (view.getId() == relative_brand.getId()) {
            relative_brand.setSelected(true);
            edit_search.setVisibility(View.VISIBLE);
            edit_search.setText("");
            edit_search.setHint("Search brand");
            getBrands();
            relative_brand.setBackgroundColor(getResources().getColor(R.color.white));
            brand_label.setTextColor(getResources().getColor(R.color.color_primary));
            brand_count.setTextColor(getResources().getColor(R.color.purchase_dark_gray));

        }
        if (view.getId() == relative_fabric.getId()) {
            relative_fabric.setSelected(true);
            edit_search.setVisibility(View.VISIBLE);
            edit_search.setText("");
            edit_search.setHint("Search fabric");
            getFabrics();
            relative_fabric.setBackgroundColor(getResources().getColor(R.color.white));
            fabric_label.setTextColor(getResources().getColor(R.color.color_primary));
            fabric_count.setTextColor(getResources().getColor(R.color.purchase_dark_gray));

        }
        if (view.getId() == relative_work.getId()) {
            relative_work.setSelected(true);
            edit_search.setVisibility(View.VISIBLE);
            edit_search.setText("");
            edit_search.setHint("Search work");
            getWorks();
            relative_work.setBackgroundColor(getResources().getColor(R.color.white));
            work_label.setTextColor(getResources().getColor(R.color.color_primary));
            work_count.setTextColor(getResources().getColor(R.color.purchase_dark_gray));

        }

        if (view.getId() == relative_style.getId()) {
            relative_style.setSelected(true);
            edit_search.setVisibility(View.GONE);
            edit_search.setText("");
            edit_search.setHint("Search");
            getStyle();
            relative_style.setBackgroundColor(getResources().getColor(R.color.white));
            style_label.setTextColor(getResources().getColor(R.color.color_primary));
            style_count.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
        }

        if (view.getId() == relative_price.getId()) {
            edit_search.setText("");
            edit_search.setVisibility(View.GONE);
            getPriceRange();
            relative_price.setSelected(true);
            relative_price.setBackgroundColor(getResources().getColor(R.color.white));
            price_label.setTextColor(getResources().getColor(R.color.color_primary));
            price_count.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
        }

        if (view.getId() == relative_state.getId()) {
            relative_state.setSelected(true);
            edit_search.setVisibility(View.VISIBLE);
            edit_search.setText("");
            edit_search.setHint("Search State");
            getStates();
            relative_state.setBackgroundColor(getResources().getColor(R.color.white));
            state_label.setTextColor(getResources().getColor(R.color.color_primary));
            state_count.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
        }

        if (view.getId() == relative_ctype.getId()) {
            edit_search.setText("");
            edit_search.setVisibility(View.GONE);
            getCatalog();
            relative_ctype.setSelected(true);
            relative_ctype.setBackgroundColor(getResources().getColor(R.color.white));
            catalog_label.setTextColor(getResources().getColor(R.color.color_primary));
            catalog_count.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
        }

    }

    public void unselectedBackground(View view) {
        if (view.getId() != relative_category.getId()) {
            relative_category.setSelected(false);
            relative_category.setBackgroundColor(getResources().getColor(R.color.ios_grey));
            category_label.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            category_count.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            selected_category.setVisibility(View.GONE);
            selected_category.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            if (categoryAdapter != null)
                if (categoryAdapter.getSelectedItem().size() > 0) {
                    category_count.setText("" + categoryAdapter.getSelectedItem().size());
                    selected_category.setVisibility(View.VISIBLE);
                    selected_category.setText(categoryAdapter.getSelectedIdValue(categoryAdapter.getSelectedItem().get(0)));
                }


        }


        if (view.getId() != relative_strich.getId()) {
            relative_strich.setSelected(false);
            relative_strich.setBackgroundColor(getResources().getColor(R.color.ios_grey));
            strich_label.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            strich_count.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            if (eavRadioAdapter != null) {
                if (eavRadioAdapter.getSelectedItem().size() > 0) {
                    strich_count.setText("" + eavRadioAdapter.getSelectedItem().size());
                }
            } else if (eavMultiAdpter != null) {
                if (eavMultiAdpter.getSelectedItem().size() > 0) {
                    strich_count.setText("" + eavMultiAdpter.getSelectedItem().size());
                }
            } else {
                strich_count.setText("");
            }
        }

        if (view.getId() != relative_brand.getId()) {
            relative_brand.setSelected(false);
            relative_brand.setBackgroundColor(getResources().getColor(R.color.ios_grey));
            brand_label.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            brand_count.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            if (brandAdaper != null)
                if (brandAdaper.getSelectedItem().size() > 0)
                    brand_count.setText("" + brandAdaper.getSelectedItem().size());
        }
        if (view.getId() != relative_fabric.getId()) {
            relative_fabric.setSelected(false);
            relative_fabric.setBackgroundColor(getResources().getColor(R.color.ios_grey));
            fabric_label.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            fabric_count.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            if (fabricAdapter != null)
                if (fabricAdapter.getSelectedItem().size() > 0)
                    fabric_count.setText("" + fabricAdapter.getSelectedItem().size());
        }
        if (view.getId() != relative_work.getId()) {
            relative_work.setSelected(false);
            relative_work.setBackgroundColor(getResources().getColor(R.color.ios_grey));
            work_label.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            work_count.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            if (workAdapter != null)
                if (workAdapter.getSelectedItem().size() > 0)
                    work_count.setText("" + workAdapter.getSelectedItem().size());
        }

        if (view.getId() != relative_style.getId()) {
            relative_style.setSelected(false);
            relative_style.setBackgroundColor(getResources().getColor(R.color.ios_grey));
            style_label.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            style_count.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            if (styleAdapter != null)
                if (styleAdapter.getSelectedItem().size() > 0)
                    style_count.setText("" + styleAdapter.getSelectedItem().size());
        }


        if (view.getId() != relative_price.getId()) {
            relative_price.setSelected(false);
            relative_price.setBackgroundColor(getResources().getColor(R.color.ios_grey));
            price_label.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            price_count.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            if (priceAdapter != null) {
                if (priceAdapter.getSelectedItem().size() > 0) {
                    if (previousMap != null && (previousMap.containsKey("min_price") || previousMap.containsKey("max_price"))) {
                        price_count.setText("" + (priceAdapter.getSelectedItem().size() - 1));
                    } else {
                        price_count.setText("" + priceAdapter.getSelectedItem().size());
                    }
                }
            }

        }

        if (view.getId() != relative_state.getId()) {
            relative_state.setSelected(false);
            relative_state.setBackgroundColor(getResources().getColor(R.color.ios_grey));
            state_label.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            state_count.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            if (stateAdapter != null)
                if (stateAdapter.getSelectedItem().size() > 0)
                    state_count.setText("" + stateAdapter.getSelectedItem().size());
        }

        if (view.getId() != relative_ctype.getId()) {
            relative_ctype.setSelected(false);
            relative_ctype.setBackgroundColor(getResources().getColor(R.color.ios_grey));
            catalog_label.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            catalog_count.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
            int count = 0;
            if (radiogroupsupplier.isChecked())
                count++;
            if (checkBuyerDisable.isChecked())
                count++;
            if (checkSupplierDisable.isChecked())
                count++;
            if (checkTrustedSeller.isChecked())
                count++;
            if (check_full_catalog.isChecked())
                count++;
            if (check_single_piece.isChecked())
                count++;
            if (check_both.isChecked())
                count++;
            if (check_near_me.isChecked())
                count++;
            if (count > 0) {
                catalog_count.setText("" + count);
            }
        }

    }

    private void getBrands() {
        recyclerview.setVisibility(View.GONE);
        String url;
        if (type.equals("from_public") || type.equals("from_category")) {
            //Log.i("TAG", "getBrands: if from public ");
            url = URLConstants.companyUrl(context, "brands_dropdown", "") + "?type=public";
        } else if (type.equals("from_followed_brand")) {
            //Log.i("TAG", "getBrands: else if followed brand ");
            url = URLConstants.companyUrl(context, "brands-follow", "");
        } else {
            //Log.i("TAG", "getBrands: Else ");
            url = URLConstants.companyUrl(context, "brands_dropdown", "");
        }

        StaticFunctions.showProgressbar(ActivityFilter.this);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityFilter.this);
        HttpManager.getInstance(ActivityFilter.this).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(ActivityFilter.this);
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.hideProgressbar(ActivityFilter.this);
                Log.v("sync response", response);
                if (type.equals("from_followed_brand")) {
                    Response_Followed_Brands[] response_followed_brandses = Application_Singleton.gson.fromJson(response, Response_Followed_Brands[].class);

                    Response_Brands[] response_brands = new Response_Brands[response_followed_brandses.length];
                    ArrayList<EnumGroupResponse> enumGroupResponses = new ArrayList<EnumGroupResponse>();
                    for (int i = 0; i < response_followed_brandses.length; i++) {
                        enumGroupResponses.add(new EnumGroupResponse(response_followed_brandses[i].getId(), response_followed_brandses[i].getBrand_name()));
                    }
                    recyclerview.setVisibility(View.VISIBLE);
                    catalog_container.setVisibility(View.GONE);
                    if (brandAdaper == null) {
                        if (previousMap != null) {
                            if (previousMap.containsKey("brand")) {
                                String brand = previousMap.get("brand");
                                ArrayList<String> chekedArray = (ArrayList<String>) Arrays.asList(brand.split(","));
                                Log.i("TAG", "Brands --->: " + brand);
                                brandAdaper = new FilterSelectAdapter(context, enumGroupResponses, chekedArray, false);
                                recyclerview.setAdapter(brandAdaper);
                            } else {
                                brandAdaper = new FilterSelectAdapter(context, enumGroupResponses, false);
                                recyclerview.setAdapter(brandAdaper);
                            }
                        } else {
                            brandAdaper = new FilterSelectAdapter(context, enumGroupResponses, false);
                            recyclerview.setAdapter(brandAdaper);
                        }
                    } else {
                        brandAdaper = new FilterSelectAdapter(context, enumGroupResponses, brandAdaper.getSelectedItem(), false);
                        recyclerview.setAdapter(brandAdaper);
                    }
                } else {
                    Response_Brands[] response_brands = Application_Singleton.gson.fromJson(response, Response_Brands[].class);

                    ArrayList<EnumGroupResponse> enumGroupResponses = new ArrayList<EnumGroupResponse>();
                    for (int i = 0; i < response_brands.length; i++) {
                        enumGroupResponses.add(new EnumGroupResponse(response_brands[i].getId(), response_brands[i].getName()));
                    }
                    recyclerview.setVisibility(View.VISIBLE);
                    catalog_container.setVisibility(View.GONE);
                    if (brandAdaper == null) {
                        if (previousMap != null) {
                            if (previousMap.containsKey("brand")) {
                                String brand = previousMap.get("brand");
                                ArrayList<String> chekedArray = new ArrayList<String>(Arrays.asList(brand.split(",")));
                                Log.i("TAG", "Brands --->: " + brand);
                                brandAdaper = new FilterSelectAdapter(context, enumGroupResponses, chekedArray, false);
                                recyclerview.setAdapter(brandAdaper);
                                brandAdaper.setOnCountCheckedListener(ActivityFilter.this);
                            } else {
                                brandAdaper = new FilterSelectAdapter(context, enumGroupResponses, false);
                                recyclerview.setAdapter(brandAdaper);
                                brandAdaper.setOnCountCheckedListener(ActivityFilter.this);
                            }
                        } else {
                            brandAdaper = new FilterSelectAdapter(context, enumGroupResponses, false);
                            recyclerview.setAdapter(brandAdaper);
                            brandAdaper.setOnCountCheckedListener(ActivityFilter.this);
                        }
                    } else {
                        brandAdaper = new FilterSelectAdapter(context, enumGroupResponses, brandAdaper.getSelectedItem(), false);
                        recyclerview.setAdapter(brandAdaper);
                        brandAdaper.setOnCountCheckedListener(ActivityFilter.this);
                    }

                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ActivityFilter.this);
            }
        });
    }

    private void getFabrics() {
        recyclerview.setVisibility(View.GONE);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityFilter.this);
        StaticFunctions.showProgressbar(ActivityFilter.this);
        String url;
        if (category_selected_id != null) {
            url = URLConstants.companyUrl(context, "enumvalues", "") + "?category=" + category_selected_id + "&attribute_slug=" + "fabric";
        } else {
            url = URLConstants.ENUM_GRPUP + "?type=fabric";
        }
        HttpManager.getInstance(ActivityFilter.this).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(ActivityFilter.this);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    StaticFunctions.hideProgressbar(ActivityFilter.this);
                    EnumGroupResponse[] enumGroupResponses = Application_Singleton.gson.fromJson(response, EnumGroupResponse[].class);
                    if (enumGroupResponses != null) {
                        if (enumGroupResponses.length > 0) {
                            ArrayList<EnumGroupResponse> enumGroupResponses1 = new ArrayList<EnumGroupResponse>(Arrays.asList(enumGroupResponses));
                            if (fabricAdapter == null) {
                                recyclerview.setVisibility(View.VISIBLE);
                                catalog_container.setVisibility(View.GONE);
                                if (previousMap != null) {
                                    if (previousMap.containsKey("fabric")) {
                                        String fabric = previousMap.get("fabric");
                                        ArrayList<String> chekedArray = new ArrayList<String>(Arrays.asList(fabric.split(",")));
                                        Log.i("TAG", "Fabric --->: " + fabric);
                                        fabricAdapter = new FilterSelectAdapter(context, enumGroupResponses1, chekedArray, false);
                                        recyclerview.setAdapter(fabricAdapter);
                                        fabricAdapter.setOnCountCheckedListener(ActivityFilter.this);
                                    } else {
                                        fabricAdapter = new FilterSelectAdapter(context, enumGroupResponses1, false);
                                        recyclerview.setAdapter(fabricAdapter);
                                        fabricAdapter.setOnCountCheckedListener(ActivityFilter.this);
                                    }
                                } else {
                                    fabricAdapter = new FilterSelectAdapter(context, enumGroupResponses1, false);
                                    recyclerview.setAdapter(fabricAdapter);
                                    fabricAdapter.setOnCountCheckedListener(ActivityFilter.this);
                                }
                            } else {
                                recyclerview.setVisibility(View.VISIBLE);
                                catalog_container.setVisibility(View.GONE);
                                fabricAdapter = new FilterSelectAdapter(context, enumGroupResponses1, fabricAdapter.getSelectedItem(), false);
                                recyclerview.setAdapter(fabricAdapter);
                                fabricAdapter.setOnCountCheckedListener(ActivityFilter.this);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ActivityFilter.this);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void getWorks() {
        recyclerview.setVisibility(View.GONE);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityFilter.this);
        StaticFunctions.showProgressbar(ActivityFilter.this);
        String url;
        if (category_selected_id != null) {
            url = URLConstants.companyUrl(context, "enumvalues", "") + "?category=" + category_selected_id + "&attribute_slug=" + "work";
        } else {
            url = URLConstants.ENUM_GRPUP + "?type=work";
        }
        HttpManager.getInstance(ActivityFilter.this).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(ActivityFilter.this);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    StaticFunctions.hideProgressbar(ActivityFilter.this);
                    EnumGroupResponse[] enumGroupResponses = Application_Singleton.gson.fromJson(response, EnumGroupResponse[].class);
                    if (enumGroupResponses != null) {
                        if (enumGroupResponses.length > 0) {
                            ArrayList<EnumGroupResponse> enumGroupResponses1 = new ArrayList<EnumGroupResponse>(Arrays.asList(enumGroupResponses));
                            if (workAdapter == null) {
                                recyclerview.setVisibility(View.VISIBLE);
                                catalog_container.setVisibility(View.GONE);
                                if (previousMap != null) {
                                    if (previousMap.containsKey("work")) {
                                        String work = previousMap.get("work");
                                        ArrayList<String> chekedArray = new ArrayList<String>(Arrays.asList(work.split(",")));
                                        Log.i("TAG", "Work --->: " + work);
                                        workAdapter = new FilterSelectAdapter(context, enumGroupResponses1, chekedArray, false);
                                        recyclerview.setAdapter(workAdapter);
                                        workAdapter.setOnCountCheckedListener(ActivityFilter.this);
                                    } else {
                                        workAdapter = new FilterSelectAdapter(context, enumGroupResponses1, false);
                                        recyclerview.setAdapter(workAdapter);
                                        workAdapter.setOnCountCheckedListener(ActivityFilter.this);
                                    }
                                } else {
                                    workAdapter = new FilterSelectAdapter(context, enumGroupResponses1, false);
                                    recyclerview.setAdapter(workAdapter);
                                    workAdapter.setOnCountCheckedListener(ActivityFilter.this);
                                }
                            } else {
                                recyclerview.setVisibility(View.VISIBLE);
                                catalog_container.setVisibility(View.GONE);
                                workAdapter = new FilterSelectAdapter(context, enumGroupResponses1, workAdapter.getSelectedItem(), false);
                                recyclerview.setAdapter(workAdapter);
                                workAdapter.setOnCountCheckedListener(ActivityFilter.this);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ActivityFilter.this);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void getStyle() {
        recyclerview.setVisibility(View.GONE);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityFilter.this);
        StaticFunctions.showProgressbar(ActivityFilter.this);
        String url;
        if (category_selected_id != null) {
            url = URLConstants.companyUrl(context, "enumvalues", "") + "?category=" + category_selected_id + "&attribute_slug=" + "style";
        } else {
            url = URLConstants.ENUM_GRPUP + "?type=style";
        }
        HttpManager.getInstance(ActivityFilter.this).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(ActivityFilter.this);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    StaticFunctions.hideProgressbar(ActivityFilter.this);
                    EnumGroupResponse[] enumGroupResponses = Application_Singleton.gson.fromJson(response, EnumGroupResponse[].class);
                    if (enumGroupResponses != null) {
                        if (enumGroupResponses.length > 0) {
                            ArrayList<EnumGroupResponse> enumGroupResponses1 = new ArrayList<EnumGroupResponse>(Arrays.asList(enumGroupResponses));
                            if (styleAdapter == null) {
                                recyclerview.setVisibility(View.VISIBLE);
                                catalog_container.setVisibility(View.GONE);
                                if (previousMap != null) {
                                    if (previousMap.containsKey("style")) {
                                        String style = previousMap.get("style");
                                        ArrayList<String> chekedArray = new ArrayList<String>(Arrays.asList(style.split(",")));
                                        Log.i("TAG", "Work --->: " + style);
                                        styleAdapter = new CategorySelectAdapter(context, enumGroupResponses1, chekedArray);
                                        recyclerview.setAdapter(styleAdapter);
                                        // styleAdapter.setOnCheckedListener(ActivityFilter.this);
                                    } else {
                                        styleAdapter = new CategorySelectAdapter(context, enumGroupResponses1);
                                        recyclerview.setAdapter(styleAdapter);
                                        // styleAdapter.setOnCheckedListener(ActivityFilter.this);
                                    }
                                } else {
                                    styleAdapter = new CategorySelectAdapter(context, enumGroupResponses1);
                                    recyclerview.setAdapter(styleAdapter);
                                    // styleAdapter.setOnCheckedListener(ActivityFilter.this);
                                }
                            } else {
                                recyclerview.setVisibility(View.VISIBLE);
                                catalog_container.setVisibility(View.GONE);
                                styleAdapter = new CategorySelectAdapter(context, enumGroupResponses1, styleAdapter.getSelectedItem());
                                recyclerview.setAdapter(styleAdapter);
                                // styleAdapter.setOnCheckedListener(ActivityFilter.this);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ActivityFilter.this);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    private void getCategory() {
        recyclerview.setVisibility(View.GONE);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityFilter.this);
        StaticFunctions.showProgressbar(ActivityFilter.this);
        HttpManager.getInstance(ActivityFilter.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(ActivityFilter.this, "category", "") + "?parent=10", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(ActivityFilter.this);
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    StaticFunctions.hideProgressbar(ActivityFilter.this);
                    Log.v("sync response", response);
                    CategoryTree[] ct = Application_Singleton.gson.fromJson(response, CategoryTree[].class);
                    if (ct != null) {
                        if (ct.length > 0) {
                            ArrayList<EnumGroupResponse> enumGroupResponses = new ArrayList<EnumGroupResponse>();
                            for (int i = 0; i < ct.length; i++) {
                                enumGroupResponses.add(new EnumGroupResponse(String.valueOf(ct[i].getId()), ct[i].getcategory_name()));
                            }
                            PrefDatabaseUtils.setCategory(context, Application_Singleton.gson.toJson(enumGroupResponses));
                            if (categoryAdapter == null) {
                                recyclerview.setVisibility(View.VISIBLE);
                                catalog_container.setVisibility(View.GONE);
                                if (previousMap != null) {
                                    if (previousMap.containsKey("category")) {
                                        String category = previousMap.get("category");
                                        ArrayList<String> chekedArray = new ArrayList<String>();
                                        chekedArray.add(category);
                                        Log.i("TAG", "Category --->: " + category);
                                        categoryAdapter = new CategorySelectAdapter(context, enumGroupResponses, chekedArray);
                                        recyclerview.setAdapter(categoryAdapter);
                                        categoryAdapter.setOnCheckedListener(ActivityFilter.this);
                                    } else {
                                        categoryAdapter = new CategorySelectAdapter(context, enumGroupResponses);
                                        recyclerview.setAdapter(categoryAdapter);
                                        categoryAdapter.setOnCheckedListener(ActivityFilter.this);
                                    }
                                } else {
                                    categoryAdapter = new CategorySelectAdapter(context, enumGroupResponses);
                                    recyclerview.setAdapter(categoryAdapter);
                                    categoryAdapter.setOnCheckedListener(ActivityFilter.this);
                                }
                            } else {
                                recyclerview.setVisibility(View.VISIBLE);
                                catalog_container.setVisibility(View.GONE);
                                categoryAdapter = new CategorySelectAdapter(context, enumGroupResponses, categoryAdapter.getSelectedItem());
                                recyclerview.setAdapter(categoryAdapter);
                                categoryAdapter.setOnCheckedListener(ActivityFilter.this);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ActivityFilter.this);
            }
        });
    }

    private void getPriceRange() {
        ArrayList<EnumGroupResponse> enumGroupResponses = new ArrayList<EnumGroupResponse>();
        enumGroupResponses.add(new EnumGroupResponse("1", "\u20B9" + "101 to " + "\u20B9" + "250"));
        enumGroupResponses.add(new EnumGroupResponse("2", "\u20B9" + "251 to " + "\u20B9" + "500"));
        enumGroupResponses.add(new EnumGroupResponse("3", "\u20B9" + "501 to " + "\u20B9" + "750"));
        enumGroupResponses.add(new EnumGroupResponse("4", "\u20B9" + "751 to " + "\u20B9" + "1000"));
        enumGroupResponses.add(new EnumGroupResponse("5", "\u20B9" + "1001 to " + "\u20B9" + "1500"));
        enumGroupResponses.add(new EnumGroupResponse("6", "\u20B9" + "1501 to " + "\u20B9" + "2000"));
        enumGroupResponses.add(new EnumGroupResponse("7", "\u20B9" + "2001 to " + "\u20B9" + "2500"));
        enumGroupResponses.add(new EnumGroupResponse("8", "\u20B9" + "2501 to " + "\u20B9" + "5000"));
        enumGroupResponses.add(new EnumGroupResponse("9", "Above" + "\u20B9" + " 5000"));
        if (priceAdapter == null) {
            recyclerview.setVisibility(View.VISIBLE);
            catalog_container.setVisibility(View.GONE);
            if (previousMap != null) {
                ArrayList<String> cheArrayList = new ArrayList<>();
                if (previousMap.containsKey("min_price") || previousMap.containsKey("max_price")) {
                    getPriceRangeSelected(cheArrayList);
                    priceAdapter = new FilterSelectAdapter(context, enumGroupResponses, cheArrayList, false);
                    recyclerview.setAdapter(priceAdapter);
                    priceAdapter.setOnCountCheckedListener(ActivityFilter.this);
                } else {
                    priceAdapter = new FilterSelectAdapter(context, enumGroupResponses, false);
                    recyclerview.setAdapter(priceAdapter);
                    priceAdapter.setOnCountCheckedListener(ActivityFilter.this);
                }
            } else {
                priceAdapter = new FilterSelectAdapter(context, enumGroupResponses, false);
                recyclerview.setAdapter(priceAdapter);
                priceAdapter.setOnCountCheckedListener(ActivityFilter.this);
            }

        } else {
            Log.e("P", "getPriceRange: Else" + priceAdapter.getSelectedItem());
            recyclerview.setVisibility(View.VISIBLE);
            catalog_container.setVisibility(View.GONE);
            priceAdapter = new FilterSelectAdapter(context, enumGroupResponses, priceAdapter.getSelectedItem(), false);
            recyclerview.setAdapter(priceAdapter);
            priceAdapter.setOnCountCheckedListener(ActivityFilter.this);
        }
    }

    private void getStates() {
        recyclerview.setVisibility(View.GONE);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityFilter.this);
        StaticFunctions.showProgressbar(ActivityFilter.this);
        HttpManager.getInstance(ActivityFilter.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context, "state", "") + "?ordering=catalogwise", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(ActivityFilter.this);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    StaticFunctions.hideProgressbar(ActivityFilter.this);
                    Response_States[] temp = Application_Singleton.gson.fromJson(response, Response_States[].class);
                    if (temp != null) {
                        if (temp.length > 0) {
                            ArrayList<EnumGroupResponse> enumGroupResponses1 = new ArrayList<>();
                            for (int i = 0; i < temp.length; i++) {
                                if (!temp[i].getState_name().equals("-"))
                                    enumGroupResponses1.add(new EnumGroupResponse(temp[i].getId(), temp[i].getState_name()));
                            }
                            if (stateAdapter == null) {
                                recyclerview.setVisibility(View.VISIBLE);
                                catalog_container.setVisibility(View.GONE);
                                if (previousMap != null) {
                                    if (previousMap.containsKey("supplier_state")) {
                                        String state = previousMap.get("supplier_state");
                                        ArrayList<String> chekedArray = new ArrayList<String>(Arrays.asList(state.split(",")));
                                        Log.i("TAG", "supplier_state --->: " + state);
                                        stateAdapter = new FilterSelectAdapter(context, enumGroupResponses1, chekedArray, false);
                                        recyclerview.setAdapter(stateAdapter);
                                        stateAdapter.setOnCountCheckedListener(ActivityFilter.this);
                                    } else {
                                        stateAdapter = new FilterSelectAdapter(context, enumGroupResponses1, false);
                                        recyclerview.setAdapter(stateAdapter);
                                        stateAdapter.setOnCountCheckedListener(ActivityFilter.this);
                                    }
                                } else {
                                    stateAdapter = new FilterSelectAdapter(context, enumGroupResponses1, false);
                                    recyclerview.setAdapter(stateAdapter);
                                    stateAdapter.setOnCountCheckedListener(ActivityFilter.this);
                                }
                            } else {
                                recyclerview.setVisibility(View.VISIBLE);
                                catalog_container.setVisibility(View.GONE);
                                stateAdapter = new FilterSelectAdapter(context, enumGroupResponses1, stateAdapter.getSelectedItem(), false);
                                recyclerview.setAdapter(stateAdapter);
                                stateAdapter.setOnCountCheckedListener(ActivityFilter.this);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ActivityFilter.this);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });


    }

    private void getCatalog() {
        catalog_container.setVisibility(View.VISIBLE);
        recyclerview.setVisibility(View.GONE);

        if (type.equals("from_public") || type.equals("from_brand")) {
            public_supplier_container.setVisibility(View.GONE);
            linear_showDisabled.setVisibility(View.GONE);
            linear_checked_trusted_seller.setVisibility(View.GONE);
            checkTrustedSeller.setVisibility(View.GONE);
            try {
                /**
                 * Hide Seller Near-me filter (Due to Wishbook As a Seller)
                 */
               /* if (UserInfo.getInstance(context).getCompanyType().equals("buyer")) {
                    linear_checked_near_me.setVisibility(View.VISIBLE);
                    check_near_me.setVisibility(View.VISIBLE);
                } else {
                    linear_checked_near_me.setVisibility(View.GONE);
                    check_near_me.setVisibility(View.GONE);
                }*/

                if (UserInfo.getInstance(this).isGuest()) {
                    linear_checked_near_me.setVisibility(View.GONE);
                    public_supplier_container.setVisibility(View.GONE);

                }


            } catch (NullPointerException e) {
                e.printStackTrace();
            }


        } else {
            public_supplier_container.setVisibility(View.GONE);
        }


        if (type.equals("from_received")) {
            linear_checked_trusted_seller.setVisibility(View.GONE);
            checkTrustedSeller.setVisibility(View.GONE);
        }


        if (!check_single_piece.isChecked() && !check_full_catalog.isChecked())
            check_both.setChecked(true);


        if (!isOpenCatalog) {
            // set selected only first time open
            setCatalogTabFill();

        }
        isOpenCatalog = true;

    }

    public void checkedListner() {
        radiogroupsupplier.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    is_supplier_approved = true;
                } else {
                    is_supplier_approved = false;
                }
            }
        });
        checkBuyerDisable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    is_buyer_disabled = true;
                } else {
                    is_buyer_disabled = false;
                }
            }
        });

        checkSupplierDisable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    supplier_disabled = true;
                } else {
                    supplier_disabled = false;
                }
            }
        });

        checkTrustedSeller.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    show_trusted_seller = true;
                } else {
                    show_trusted_seller = false;
                }
            }
        });


        check_near_me.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    show_near_me = true;
                } else {
                    show_near_me = false;
                }
            }
        });
    }

    public int setCatalogTabFill() {
        int count = 0;
        if (previousMap != null) {
            if (previousMap.containsKey("is_supplier_approved")) {
                if (previousMap.get("is_supplier_approved").equals("true")) {
                    radiogroupsupplier.setChecked(true);
                    count++;
                } else {
                    radiogroupsupplier.setChecked(false);
                }
            }

            if (previousMap.containsKey("buyer_disabled")) {
                if (previousMap.get("buyer_disabled").equals("true")) {
                    checkBuyerDisable.setChecked(true);
                    count++;
                } else {
                    checkBuyerDisable.setChecked(false);
                }
            }
            if (previousMap.containsKey("supplier_disabled")) {
                if (previousMap.get("supplier_disabled").equals("true")) {
                    checkSupplierDisable.setChecked(true);
                    count++;
                } else {
                    checkSupplierDisable.setChecked(true);
                }

            }
            if (previousMap.containsKey("trusted_seller")) {
                if (previousMap.get("trusted_seller").equals("true")) {
                    checkTrustedSeller.setChecked(true);
                    Log.i("TAG", "===============setCatalogTabFill===========: Trusted Fill ");
                    count++;
                } else {
                    checkTrustedSeller.setChecked(false);
                }
            }

            if (previousMap.containsKey("near_me")) {
                if (previousMap.get("near_me").equals("true")) {
                    check_near_me.setChecked(true);
                    count++;
                } else {
                    check_near_me.setChecked(false);
                }
            }

            if (previousMap.containsKey("sell_full_catalog")) {
                if (!previousMap.get("sell_full_catalog").equals("false")) {
                    Log.i("TAG", "setCatalogTabFill: True ");
                    check_full_catalog.setChecked(true);
                    check_single_piece.setChecked(false);
                    check_both.setChecked(false);
                    count++;
                } else {
                    Log.i("TAG", "setCatalogTabFill: False ");
                    check_single_piece.setChecked(true);
                    check_full_catalog.setChecked(false);
                    check_both.setChecked(false);
                }
            }

            if (previousMap.containsKey("ready_to_ship")) {
                if (previousMap.get("ready_to_ship").equals("false")) {
                    chk_prelaunch.setChecked(true);
                }
            }


            if (previousMap.containsKey("title")) {
                edit_catalog_title.setText(previousMap.get("title"));
                edit_catalog_title.setSelection(edit_catalog_title.getText().length());
                count++;
            }

            if (previousMap.containsKey("product_type")) {
                if (previousMap.get("product_type").equalsIgnoreCase("catalog")) {
                    radio_catalog.setChecked(true);
                } else if (previousMap.get("product_type").contains("noncatalog")) {
                    radio_noncatalog.setChecked(true);
                }
            }
        }
        return count;
    }

    @OnClick(R.id.btn_filter_apply)
    public void ApplyFilter() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityFilter.this);
        if (responseSavedFilters != null) {
            if (!responseSavedFilters.getParams().equals(Application_Singleton.gson.toJson(generateParams(false)))) {
                new MaterialDialog.Builder(context)
                        .content("You have made some changes in your \"" + responseSavedFilters.getTitle() + "\" filter, do you want to save changes?")
                        .positiveText("YES")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                                StringBuilder commaSepValueBuilder = new StringBuilder();
                                HashMap<String, String> param = generateParams(true);
                                for (String key : param.keySet())
                                    commaSepValueBuilder.append(param.get(key) + ",");
                                String json = Application_Singleton.gson.toJson(generateParams(false));
                                patchSaveFilter(responseSavedFilters.getTitle(), commaSepValueBuilder.toString(), json, responseSavedFilters.getId());
                            }
                        })
                        .negativeText("NO")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                Intent intent = new Intent()
                                        .putExtra("parameters", generateParams(false))
                                        .putExtra("selected_saved_filter", responseSavedFilters);
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            }
                        })
                        .show();
            } else {
                Intent intent = new Intent()
                        .putExtra("parameters", generateParams(false))
                        .putExtra("selected_saved_filter", responseSavedFilters);
                ;
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

        } else {
            Intent intent = new Intent()
                    .putExtra("parameters", generateParams(false));
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private HashMap<String, String> generateParams(boolean isValue) {
        HashMap<String, String> params = new HashMap<String, String>();
        if (categoryAdapter != null || category_selected_id != null) {

            if (categoryAdapter != null && categoryAdapter.getSelectedItem().size() > 0) {
                if (isValue) {
                    if (categoryAdapter.getSelectedItemStringValue().size() > 0) {
                        params.put("category", categoryAdapter.getSelectedItemStringValue().get(0));
                    } else {
                        if (previousMap != null) {
                            if (previousMap.containsKey("category")) {
                                params.put("category", categoryAdapter.getSelectedIdValue(previousMap.get("category")));
                            }
                        }
                    }
                } else {
                    params.put("category", categoryAdapter.getSelectedItem().get(0));
                }

                Application_Singleton.trackEvent("Filter", "Category", categoryAdapter.getSelectedItem().get(0));

            } else if (category_selected_id != null) {
                if (!isValue)
                    params.put("category", category_selected_id);
            }

            if (eavRadioAdapter != null) {
                if (eavRadioAdapter.getSelectedItem().size() > 0) {
                    // for only strichtype
                    if (isValue) {
                        params.put("stitching_type", StaticFunctions.ArrayListToString(eavRadioAdapter.getSelectedItemStringValue(), StaticFunctions.COMMASEPRATED));
                    } else {
                        params.put("stitching_type", StaticFunctions.ArrayListToString(eavRadioAdapter.getSelectedItem(), StaticFunctions.COMMASEPRATED));
                    }

                    Application_Singleton.trackEvent("Filter", "Stitching Type", eavRadioAdapter.getSelectedItem().get(0));
                }
            }

            if (eavMultiAdpter != null) {
                if (eavMultiAdpter.getSelectedItem().size() > 0) {
                    // for only size
                    if (isValue) {
                        params.put("size", StaticFunctions.ArrayListToString(eavMultiAdpter.getSelectedItemStringValue(), StaticFunctions.COMMASEPRATED));
                    } else {
                        params.put("size", StaticFunctions.ArrayListToString(eavMultiAdpter.getSelectedItem(), StaticFunctions.COMMASEPRATED));
                    }

                    Application_Singleton.trackEvent("Filter", "Size", eavMultiAdpter.getSelectedItem().toString());
                }
            }
        } else {
            // pass previous parameter if available
            if (previousMap != null) {
                if (previousMap.containsKey("category")) {
                    if (!isValue)
                        params.put("category", previousMap.get("category"));
                }

                if (previousMap.containsKey("stitching_type")) {
                    if (!isValue)
                        params.put("stitching_type", params.get("stitching_type"));
                }

                if (previousMap.containsKey("size")) {
                    if (!isValue)
                        params.put("size", params.get("size"));
                }
            }
        }

        if (styleAdapter != null) {
            if (styleAdapter.getSelectedItem().size() > 0) {
                StringBuilder workString = new StringBuilder();
                if (isValue) {
                    if (styleAdapter.getSelectedItemStringValue().size() > 0) {
                        params.put("style", styleAdapter.getSelectedItemStringValue().get(0));
                    } else {
                        if (previousMap != null) {
                            if (previousMap.containsKey("style")) {
                                params.put("style", styleAdapter.getSelectedIdValue(previousMap.get("style")));
                            }
                        }
                    }
                } else {
                    params.put("style", styleAdapter.getSelectedItem().get(0));
                }

                Application_Singleton.trackEvent("Filter", "Style", styleAdapter.getSelectedItem().get(0));
            }
        } else {
            if (previousMap != null) {
                if (previousMap.containsKey("style")) {
                    if (!isValue)
                        params.put("style", previousMap.get("style"));
                }
            }
        }

        if (fabricAdapter != null) {
            if (fabricAdapter.getSelectedItem().size() > 0) {
                StringBuilder fabricString = new StringBuilder();
                if (isValue) {
                    fabricString.append(StaticFunctions.ArrayListToString(fabricAdapter.getSelectedItemStringValue(), StaticFunctions.COMMASEPRATED));
                    params.put("fabric", fabricString.toString());
                } else {
                    fabricString.append(StaticFunctions.ArrayListToString(fabricAdapter.getSelectedItem(), StaticFunctions.COMMASEPRATED));
                    params.put("fabric", fabricString.toString());
                }

                Application_Singleton.trackEvent("Filter", "Fabric", fabricString.toString());
            }
        } else {
            // pass previous parameter if available
            if (previousMap != null) {
                if (previousMap.containsKey("fabric")) {
                    if (!isValue)
                        params.put("fabric", previousMap.get("fabric"));
                }
            }
        }


        if (workAdapter != null) {
            if (workAdapter.getSelectedItem().size() > 0) {
                StringBuilder workString = new StringBuilder();
                if (isValue) {
                    workString.append(StaticFunctions.ArrayListToString(workAdapter.getSelectedItemStringValue(), StaticFunctions.COMMASEPRATED));
                    params.put("work", workString.toString());
                } else {
                    workString.append(StaticFunctions.ArrayListToString(workAdapter.getSelectedItem(), StaticFunctions.COMMASEPRATED));
                    params.put("work", workString.toString());
                }

                Application_Singleton.trackEvent("Filter", "Work", workString.toString());
            }
        } else {
            // pass previous parameter if available
            if (previousMap != null) {
                if (previousMap.containsKey("work")) {
                    if (!isValue)
                        params.put("work", previousMap.get("work"));
                }
            }
        }


        if (priceAdapter != null) {
            if (priceAdapter.getSelectedItem().size() > 0) {
                Collections.sort(priceAdapter.getSelectedItem(), new Comparator<String>() {
                    @Override
                    public int compare(String s, String t1) {
                        return ((Integer) Integer.parseInt(s)).compareTo(Integer.parseInt(t1));
                    }
                });

                min = Integer.parseInt(priceAdapter.getSelectedItem().get(0));
                if (min != 0) {
                    switch (min) {
                        case 1:
                            minPrice = 101;
                            break;
                        case 2:
                        case 3:
                        case 4:
                            minPrice = ((min - 1) * 250) + 1;
                            break;
                        case 5:
                            minPrice = 1001;
                            break;
                        case 6:
                            minPrice = 1501;
                            break;
                        case 7:
                            minPrice = 2001;
                            break;
                        case 8:
                            minPrice = 2501;
                            break;
                        case 9:
                            minPrice = 5000;
                            break;
                    }
                }

                max = Integer.parseInt(priceAdapter.getSelectedItem().get(priceAdapter.getSelectedItem().size() - 1));

                if (max != 0) {
                    switch (max) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            maxPrice = (max * 250);
                            break;
                        case 5:
                            maxPrice = 1500;
                            break;
                        case 6:
                            maxPrice = 2000;
                            break;
                        case 7:
                            maxPrice = 2500;
                            break;
                        case 8:
                            maxPrice = 5000;
                            break;
                        case 9:
                            maxPrice = 100000;
                            break;
                    }

                }

                params.put("min_price", "" + minPrice);
                params.put("max_price", "" + maxPrice);
                Application_Singleton.trackEvent("Filter", "Min Price", minPrice + "");
                Application_Singleton.trackEvent("Filter", "Max Price", maxPrice + "");
            }
        } else {
            // pass previous parameter if available
            if (previousMap != null) {
                if (previousMap.containsKey("min_price")) {
                    params.put("min_price", previousMap.get("min_price"));
                }

                if (previousMap.containsKey("max_price")) {
                    params.put("max_price", previousMap.get("max_price"));
                }
            }
        }

        if (stateAdapter != null) {
            if (stateAdapter.getSelectedItem().size() > 0) {
                StringBuilder stateString = new StringBuilder();
                if (isValue) {
                    stateString.append(StaticFunctions.ArrayListToString(stateAdapter.getSelectedItemStringValue(), StaticFunctions.COMMASEPRATED));
                    params.put("supplier_state", stateString.toString());
                } else {
                    stateString.append(StaticFunctions.ArrayListToString(stateAdapter.getSelectedItem(), StaticFunctions.COMMASEPRATED));
                    params.put("supplier_state", stateString.toString());
                }

                Application_Singleton.trackEvent("Filter", "State", stateString.toString());
            }
        } else {
            // pass previous parameter if available
            if (previousMap != null) {
                if (previousMap.containsKey("supplier_state")) {
                    if (!isValue)
                        params.put("supplier_state", previousMap.get("supplier_state"));
                }
            }
        }

        if (brandAdaper != null) {
            if (brandAdaper.getSelectedItem().size() > 0) {
                StringBuilder brandString = new StringBuilder();
                if (isValue) {
                    brandString.append(StaticFunctions.ArrayListToString(brandAdaper.getSelectedItemStringValue(), StaticFunctions.COMMASEPRATED));
                    params.put("brand", brandString.toString());
                } else {
                    brandString.append(StaticFunctions.ArrayListToString(brandAdaper.getSelectedItem(), StaticFunctions.COMMASEPRATED));
                    params.put("brand", brandString.toString());
                }

                Application_Singleton.trackEvent("Filter", "Brand", brandString.toString());
            }
        } else {
            // pass previous parameter if available
            if (previousMap != null) {
                if (previousMap.containsKey("brand")) {
                    if (!isValue)
                        params.put("brand", previousMap.get("brand"));
                }
            }
        }

        if (!isOpenCatalog) {
            //  pass previous vaule of catalog tab
            setCatalogTabFill();
        }

        if (!edit_catalog_title.getText().toString().isEmpty()) {
            params.put("title", "" + edit_catalog_title.getText().toString());
            Application_Singleton.trackEvent("Filter", "CatalogName", edit_catalog_title.getText().toString());
        }

        if (is_disable) {
            params.put("is_disable", String.valueOf(is_disable));
        }

        if (type.equals("from_public")) {
            if (is_supplier_approved) {
                params.put("is_supplier_approved", String.valueOf(is_supplier_approved));
                Application_Singleton.trackEvent("Filter", "Supplier Approved", "True");
            }
        }

        if (type.equals("from_brand")) {
            if (is_supplier_approved) {
                params.put("is_supplier_approved", String.valueOf(is_supplier_approved));
                Application_Singleton.trackEvent("Filter", "Supplier Approved", "True");
            }
        }

        if (is_buyer_disabled) {
            params.put("buyer_disabled", String.valueOf(is_buyer_disabled));
            Application_Singleton.trackEvent("Filter", "Disabled By Me", "True");
        }

        if (supplier_disabled) {
            if (isValue) {
                params.put("supplier_disabled", "Supplier Disable");
            } else {
                params.put("supplier_disabled", String.valueOf(supplier_disabled));
            }

            Application_Singleton.trackEvent("Filter", "Disabled By Supplier", "True");
        }

        if (checkTrustedSeller.isChecked()) {
            if (isValue) {
                params.put("trusted_seller", "Trusted Seller");
            } else {
                params.put("trusted_seller", String.valueOf(show_trusted_seller));
            }

            Application_Singleton.trackEvent("Filter", "Trusted Seller", "True");
        } else {
            if (params.containsKey("trusted_seller"))
                params.remove("trusted_seller");
        }

        if (show_near_me) {
            params.put("near_me", String.valueOf(show_near_me));
            Application_Singleton.trackEvent("Filter", "Near Me", "True");
        }

        if (check_full_catalog.isChecked()) {
            if (isValue) {
                params.put("sell_full_catalog", "Full Catalog Only");
            } else {
                params.put("sell_full_catalog", "true");
            }
            Log.i("TAG", "full_catalog=true");
            Application_Singleton.trackEvent("Filter", "Sell Full Catalog", "True");
        }


        if (check_single_piece.isChecked()) {
            Log.i("TAG", "single_piece=true");
            if (isValue) {
                params.put("sell_full_catalog", "Single Piece");
            } else {
                params.put("sell_full_catalog", "false");
            }
            Application_Singleton.trackEvent("Filter", "Sell Full Catalog", "False");
        }


        // Catalog Type Filter start

        if (radio_catalog.isChecked()) {
            if (isValue) {
                params.put("product_type", Constants.PRODUCT_TYPE_CAT);
            } else {
                params.put("product_type", Constants.PRODUCT_TYPE_CAT);
            }
        }

        if (radio_noncatalog.isChecked()) {
            if (isValue) {
                params.put("product_type", Constants.PRODUCT_TYPE_NON + "," + Constants.PRODUCT_TYPE_SCREEN);
            } else {
                params.put("product_type", Constants.PRODUCT_TYPE_NON + "," + Constants.PRODUCT_TYPE_SCREEN);
            }
        }


        if (chk_prelaunch.isChecked()) {
            if (isValue) {
                params.put("ready_to_ship", "Include Pre-Launch");
            } else {
                params.remove("ready_to_ship");
                //  params.put("ready_to_ship", "false");
            }

        } else {
            if (isValue) {
                params.put("ready_to_ship", "Both Available");
            } else {
                params.put("ready_to_ship", "true");
            }
        }


        return params;
    }

    private void clearAllFilter() {
        Application_Singleton.trackEvent("Filter", "Clear All", "Clear All");
        if (categoryAdapter != null) {
            categoryAdapter.removeSelectedItem();
            categoryAdapter.notifyDataSetChanged();
        }
        category_count.setText("");
        selected_category.setText("");

        if (eavRadioAdapter != null) {
            eavRadioAdapter.removeSelectedItem();
            eavRadioAdapter.notifyDataSetChanged();
        }

        if (eavMultiAdpter != null) {
            eavMultiAdpter.removeSelectedItem();
            eavMultiAdpter.notifyDataSetChanged();
        }

        if (brandAdaper != null) {
            brandAdaper.removeSelectedItem();
            brandAdaper.notifyDataSetChanged();

        }
        brand_count.setText("");


        if (fabricAdapter != null) {
            fabricAdapter.removeSelectedItem();
            fabricAdapter.notifyDataSetChanged();

        }
        fabric_count.setText("");


        if (workAdapter != null) {
            workAdapter.removeSelectedItem();
            workAdapter.notifyDataSetChanged();
        }

        work_count.setText("");

        if (styleAdapter != null) {
            styleAdapter.removeSelectedItem();
            styleAdapter.notifyDataSetChanged();
        }
        style_count.setText("");

        if (priceAdapter != null) {
            priceAdapter.removeSelectedItem();

            priceAdapter.notifyDataSetChanged();
        }
        price_count.setText("");

        if (stateAdapter != null) {
            stateAdapter.removeSelectedItem();
            stateAdapter.notifyDataSetChanged();
        }

        state_count.setText("");

        edit_catalog_title.setText("");
        radiogroupsupplier.setChecked(false);
        checkBuyerDisable.setChecked(false);
        checkTrustedSeller.setChecked(false);
        checkSupplierDisable.setChecked(false);
        check_full_catalog.setChecked(false);
        check_single_piece.setChecked(false);
        check_near_me.setChecked(false);
        chk_prelaunch.setChecked(false);


        catalog_count.setText("");
        fabric_count.setText("");
        selected_category.setText("");
        isOpenCatalog = true;

        if (previousMap != null) {
            previousMap.clear();
            previousMap = null;
        }

        relative_strich.setVisibility(View.GONE);

        Intent intent = new Intent()
                .putExtra("parameters", generateParams(false))
                .putExtra("clearall",true);
        setResult(Activity.RESULT_OK, intent);
        finish();

    }

    public void getCategoryEavAttribute(String categoryId, final boolean isShowRecycler) {
        Log.e("TAG", "getCategoryEavAttribute:======> call");
        StaticFunctions.showProgressbar(ActivityFilter.this);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityFilter.this);
        HttpManager.getInstance(ActivityFilter.this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.CATEGORY_EVP + "?category=" + categoryId, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(ActivityFilter.this);
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                StaticFunctions.hideProgressbar(ActivityFilter.this);
                final ResponseCategoryEvp[] evpAttribute = Application_Singleton.gson.fromJson(response, ResponseCategoryEvp[].class);
                if (evpAttribute.length > 0) {
                    for (int i = 0; i < evpAttribute.length; i++) {
                        if (evpAttribute[i].getAttribute_datatype().equalsIgnoreCase("enum")) {
                            if (evpAttribute[i].getFilterable()) {
                                relative_strich.setVisibility(View.VISIBLE);
                                strich_label.setText(evpAttribute[i].getAttribute_name());

                                if (evpAttribute[i].getAttribute_name().equals("stitching_type") || evpAttribute[i].getAttribute_name().equals("Stitching Type")) {
                                    strich_label.setText("Stitch");
                                }
                                if (previousMap != null) {
                                    if (previousMap.containsKey("stitching_type")) {
                                        strich_count.setText("" + 1);
                                    } else {
                                        strich_count.setText("");
                                    }
                                }

                                if (isShowRecycler) {
                                    strich_count.setText("");
                                    ArrayList<EnumGroupResponse> enumGroupResponses = new ArrayList<EnumGroupResponse>();
                                    for (int j = 0; j < evpAttribute[i].getAttribute_values().size(); j++) {
                                        enumGroupResponses.add(new EnumGroupResponse(String.valueOf(evpAttribute[i].getAttribute_values().get(j).getId()), evpAttribute[i].getAttribute_values().get(j).getValue()));
                                    }
                                    if (eavRadioAdapter == null) {
                                        recyclerview.setVisibility(View.VISIBLE);
                                        catalog_container.setVisibility(View.GONE);
                                        if (previousMap != null) {
                                            if (previousMap.containsKey(evpAttribute[i].getAttribute_name())) {
                                                String category = previousMap.get(evpAttribute[i].getAttribute_name());
                                                ArrayList<String> chekedArray = new ArrayList<String>();
                                                chekedArray.add(category);
                                                eavRadioAdapter = new CategorySelectAdapter(context, enumGroupResponses, chekedArray);
                                                recyclerview.setAdapter(eavRadioAdapter);
                                            } else {
                                                eavRadioAdapter = new CategorySelectAdapter(context, enumGroupResponses);
                                                recyclerview.setAdapter(eavRadioAdapter);
                                            }
                                        } else {
                                            eavRadioAdapter = new CategorySelectAdapter(context, enumGroupResponses);
                                            recyclerview.setAdapter(eavRadioAdapter);
                                        }
                                    } else {
                                        recyclerview.setVisibility(View.VISIBLE);
                                        catalog_container.setVisibility(View.GONE);
                                        eavRadioAdapter = new CategorySelectAdapter(context, enumGroupResponses, eavRadioAdapter.getSelectedItem());
                                        recyclerview.setAdapter(eavRadioAdapter);
                                    }
                                }
                            }

                        } else if (evpAttribute[i].getAttribute_datatype().equalsIgnoreCase("multi")) {
                            if (evpAttribute[i].getFilterable()) {
                                relative_strich.setVisibility(View.VISIBLE);

                                strich_label.setText(StaticFunctions.formatErrorTitle(evpAttribute[i].getAttribute_name()));
                                //strich_count.setText("");
                                if (evpAttribute[i].getAttribute_name().equals("size")) {
                                    strich_label.setText("Size");
                                }
                                if (previousMap != null) {
                                    if (previousMap.containsKey("size")) {
                                        String sizes = previousMap.get("size");
                                        if (sizes != null) {
                                            ArrayList<String> checkedArray = new ArrayList<String>(Arrays.asList(sizes.split(",")));
                                            strich_count.setText("" + checkedArray.size());
                                        }
                                    } else {
                                        strich_count.setText("");
                                    }
                                }

                                if (isShowRecycler) {
                                    ArrayList<EnumGroupResponse> enumGroupResponses = new ArrayList<EnumGroupResponse>();
                                    for (int j = 0; j < evpAttribute[i].getAttribute_values().size(); j++) {
                                        enumGroupResponses.add(new EnumGroupResponse(String.valueOf(evpAttribute[i].getAttribute_values().get(j).getId()), evpAttribute[i].getAttribute_values().get(j).getValue()));
                                    }
                                    recyclerview.setVisibility(View.VISIBLE);
                                    catalog_container.setVisibility(View.GONE);
                                    if (eavMultiAdpter == null) {
                                        if (previousMap != null) {
                                            if (previousMap.containsKey(evpAttribute[i].getAttribute_name())) {
                                                String brand = previousMap.get(evpAttribute[i].getAttribute_name());
                                                ArrayList<String> checkedArray = new ArrayList<String>(Arrays.asList(brand.split(",")));
                                                //ArrayList<Integer> chekedArray = (ArrayList<Integer>) Arrays.asList(brand.split(", "));
                                                eavMultiAdpter = new FilterSelectAdapter(context, enumGroupResponses, checkedArray, false);
                                                recyclerview.setAdapter(eavMultiAdpter);
                                                eavMultiAdpter.setOnCountCheckedListener(ActivityFilter.this);
                                            } else {
                                                eavMultiAdpter = new FilterSelectAdapter(context, enumGroupResponses, false);
                                                recyclerview.setAdapter(eavMultiAdpter);
                                                eavMultiAdpter.setOnCountCheckedListener(ActivityFilter.this);
                                            }
                                        } else {
                                            eavMultiAdpter = new FilterSelectAdapter(context, enumGroupResponses, false);
                                            recyclerview.setAdapter(eavMultiAdpter);
                                            eavMultiAdpter.setOnCountCheckedListener(ActivityFilter.this);
                                        }
                                    } else {
                                        eavMultiAdpter = new FilterSelectAdapter(context, enumGroupResponses, eavMultiAdpter.getSelectedItem(), false);
                                        recyclerview.setAdapter(eavMultiAdpter);
                                        eavMultiAdpter.setOnCountCheckedListener(ActivityFilter.this);
                                    }
                                }
                            }

                        }
                    }
                } else {
                    relative_strich.setVisibility(View.GONE);
                    strich_label.setText("");
                    strich_count.setText("");
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(ActivityFilter.this);
            }
        });
    }


    @Override
    public void onChecked(String categoryId, boolean eventFire) {
        if (eventFire) {
            // call EavAttribute Value
            eavRadioAdapter = null;
            eavMultiAdpter = null;
            category_selected_id = categoryId;
            getCategoryEavAttribute(categoryId, false);
        }
    }


    public void getPriceRangeSelected(ArrayList<String> cheArrayList) {
        if (previousMap.containsKey("min_price") || previousMap.containsKey("max_price")) {
            int selectedmin = 101;
            int selectedmax = 10000;
            if (previousMap.containsKey("min_price")) {
                int min = Integer.parseInt(previousMap.get("min_price"));
                if (min != 0) {
                    if (min <= 101) {
                        cheArrayList.add("1");
                    } else if (min <= 251) {
                        cheArrayList.add("2");
                    } else if (min <= 501) {
                        cheArrayList.add("3");
                    } else if (min <= 751) {
                        cheArrayList.add("4");
                    } else if (min <= 1001) {
                        cheArrayList.add("5");
                    } else if (min <= 1501) {
                        cheArrayList.add("6");
                    } else if (min <= 2001) {
                        cheArrayList.add("7");
                    } else if (min <= 2501) {
                        cheArrayList.add("8");
                    } else if (min <= 5000) {
                        cheArrayList.add("9");
                    }
                }

            }

            try {
                selectedmin = Integer.parseInt(cheArrayList.get(0));
            } catch (Exception e) {
                selectedmin = 0;
                e.printStackTrace();
            }

            if (previousMap.containsKey("max_price")) {
                int max = Integer.parseInt(previousMap.get("max_price"));
                if (max != 0) {
                    if (max > 100) {
                        selectedmax = 1;
                        //cheArrayList.add("1");
                    }
                    if (max > 250) {
                        selectedmax = 2;
                        //cheArrayList.add("2");
                    }
                    if (max > 500) {
                        selectedmax = 3;
                        // cheArrayList.add("3");
                    }
                    if (max > 750) {
                        selectedmax = 4;
                        //cheArrayList.add("4");
                    }
                    if (max > 1000) {
                        selectedmax = 5;
                        //cheArrayList.add("5");
                    }
                    if (max > 1500) {
                        selectedmax = 6;
                        //cheArrayList.add("6");
                    }
                    if (max > 2000) {
                        selectedmax = 7;
                        // cheArrayList.add("7");
                    }
                    if (max > 2500) {
                        selectedmax = 8;
                        //  cheArrayList.add("8");
                    }
                    if (max > 50000) {
                        selectedmax = 9;
                        //  cheArrayList.add("9");
                    }
                }
            } else {
                selectedmax = 9;
            }

            for (int i = selectedmin; i <= selectedmax; i++) {
                if (!cheArrayList.contains(String.valueOf(i))) {
                    cheArrayList.add(String.valueOf(i));
                }

            }
        }
    }

    @Override
    public void onFabricChecked() {
        // for update count
        if (relative_category.isSelected()) {
            if (categoryAdapter != null) {
                category_count.setText("" + categoryAdapter.getSelectedItem().size());
            }
        } else if (relative_brand.isSelected()) {
            if (brandAdaper != null) {
                brand_count.setText("" + brandAdaper.getSelectedItem().size());
            }
        } else if (relative_fabric.isSelected()) {
            if (fabricAdapter != null) {
                fabric_count.setText("" + fabricAdapter.getSelectedItem().size());
            }
        } else if (relative_work.isSelected()) {
            if (workAdapter != null) {
                work_count.setText("" + workAdapter.getSelectedItem().size());
            }
        } else if (relative_style.isSelected()) {
            if (styleAdapter != null) {
                state_count.setText("" + styleAdapter.getSelectedItem().size());
            }
        } else if (relative_price.isSelected()) {
            try {
                if (priceAdapter != null) {
                    if (previousMap != null && (previousMap.containsKey("min_price") || previousMap.containsKey("max_price"))) {
                        price_count.setText("" + (priceAdapter.getSelectedItem().size()));
                    } else {
                        price_count.setText("" + priceAdapter.getSelectedItem().size());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                price_count.setText("" + priceAdapter.getSelectedItem().size());
            }

        } else if (relative_state.isSelected()) {
            if (stateAdapter != null) {
                state_count.setText("" + stateAdapter.getSelectedItem().size());
            }
        } else if (relative_strich.isSelected()) {
            if (eavMultiAdpter != null) {
                if (previousMap != null) {
                    if (previousMap.containsKey("size")) {
                        String sizes = previousMap.get("size");
                        if (sizes != null) {
                            ArrayList<String> checkedArray = new ArrayList<String>(Arrays.asList(sizes.split(",")));
                            if(eavMultiAdpter.getSelectedItem().size() != checkedArray.size()) {
                                String temp = StaticFunctions.ArrayListToString(eavMultiAdpter.getSelectedItem(),",");
                                previousMap.put("size",temp);
                            }
                        }
                    }
                }
                strich_count.setText("" + eavMultiAdpter.getSelectedItem().size());
            } else if (eavRadioAdapter != null) {
                strich_count.setText("" + eavRadioAdapter.getSelectedItem().size());
            }
        }
    }

    public void firstTimeChecked() {
        if (previousMap != null) {
            if (previousMap.containsKey("category")) {
                category_count.setText("1");
            }

            if (previousMap.containsKey("brand")) {
                String brand = previousMap.get("brand");
                ArrayList<String> chekedArray = new ArrayList<String>(Arrays.asList(brand.split(",")));
                brand_count.setText("" + chekedArray.size());
            }

            if (previousMap.containsKey("fabric")) {
                String fabric = previousMap.get("fabric");
                ArrayList<String> chekedArray = new ArrayList<String>(Arrays.asList(fabric.split(",")));
                fabric_count.setText("" + chekedArray.size());
            }

            if (previousMap.containsKey("work")) {
                String work = previousMap.get("work");
                ArrayList<String> chekedArray = new ArrayList<String>(Arrays.asList(work.split(",")));
                work_count.setText("" + chekedArray.size());
            }

            if (previousMap.containsKey("style")) {
                String style = previousMap.get("style");
                ArrayList<String> chekedArray = new ArrayList<String>(Arrays.asList(style.split(",")));
                style_count.setText("" + chekedArray.size());
            }

            if (previousMap.containsKey("supplier_state")) {
                String style = previousMap.get("supplier_state");
                ArrayList<String> chekedArray = new ArrayList<String>(Arrays.asList(style.split(",")));
                state_count.setText("" + chekedArray.size());
            }

            if (previousMap.containsKey("min_price") || previousMap.containsKey("max_price")) {
                ArrayList<String> cheArrayList = new ArrayList<>();
                getPriceRangeSelected(cheArrayList);
                price_count.setText("" + (cheArrayList.size()));
            }

            int count = setCatalogTabFill();
            if (count != 0) {
                catalog_count.setText("" + count);
            }

            if (previousMap.containsKey("size")) {
                String sizes = previousMap.get("size");
                if (sizes != null) {
                    ArrayList<String> checkedArray = new ArrayList<String>(Arrays.asList(sizes.split(",")));
                    strich_count.setText("" + checkedArray.size());
                }
            }

            if (previousMap.containsKey("stitching_type")) {
                String category = previousMap.get("stitching_type");
                strich_count.setText("" + 1);
            }
        }
    }

    private void showSaveFilterDialog() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_save_filter, true)
                .autoDismiss(false)
                .positiveText("Save")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        EditText edit_filter_name = (EditText) dialog.findViewById(R.id.edit_filter_name);
                        if (edit_filter_name != null && !edit_filter_name.getText().toString().trim().isEmpty()) {
                            StringBuilder commaSepValueBuilder = new StringBuilder();
                            HashMap<String, String> param = generateParams(true);
                            for (String key : param.keySet())
                                commaSepValueBuilder.append(param.get(key) + ",");
                            String json = Application_Singleton.gson.toJson(generateParams(false));
                            postSaveFilter(edit_filter_name.getText().toString().trim(), commaSepValueBuilder.toString(), json);
                        } else {
                            edit_filter_name.setError("Please enter filter name");
                        }
                    }
                })
                .negativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .contentGravity(GravityEnum.END).cancelable(false).
                        show();

    }


    private void postSaveFilter(String title, String sub_text, String hashmap) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityFilter.this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("title", title);
        params.put("sub_text", sub_text);
        params.put("params", hashmap);
        if (isNonCatalog) {
            params.put("filter_type", Constants.CATALOG_TYPE_CAT);
        } else {
            params.put("filter_type", Constants.CATALOG_TYPE_CAT);
        }

        HttpManager.getInstance(ActivityFilter.this).methodPost(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.USER_SAVE_FILER, params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                ResponseSavedFilters responseSavedFilters = Application_Singleton.gson.fromJson(response, ResponseSavedFilters.class);
                Toast.makeText(ActivityFilter.this, "Successfully saved filter", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent()
                        .putExtra("parameters", generateParams(false))
                        .putExtra("selected_saved_filter", responseSavedFilters);
                setResult(Activity.RESULT_OK, intent);
                finish();

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });

    }


    private void patchSaveFilter(String title, String sub_text, String hashmap, String id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(ActivityFilter.this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("title", title);
        params.put("sub_text", sub_text);
        params.put("params", hashmap);
        HttpManager.getInstance(ActivityFilter.this).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.USER_SAVE_FILER + id + "/", Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(params), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                ResponseSavedFilters responseSavedFilters = Application_Singleton.gson.fromJson(response, ResponseSavedFilters.class);
                Toast.makeText(ActivityFilter.this, "Successfully updated filter", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent()
                        .putExtra("parameters", generateParams(false))
                        .putExtra("selected_saved_filter", responseSavedFilters);
                ;
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);

            }
        });
    }


    public void callDeleteSavedFilter(String deleteid) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.DELETEWITHPROGRESS, URLConstants.USER_SAVE_FILER + deleteid + "/", null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Intent intent = new Intent();
                setResult(9000, intent);
                finish();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


}
