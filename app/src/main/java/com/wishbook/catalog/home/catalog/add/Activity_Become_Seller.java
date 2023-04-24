package com.wishbook.catalog.home.catalog.add;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.widget.SimpleTextWatcher;
import com.wishbook.catalog.commonmodels.responses.ResponseBrandDiscount;
import com.wishbook.catalog.commonmodels.responses.ResponseCategoryEvp;
import com.wishbook.catalog.home.catalog.StartStopHandler;
import com.wishbook.catalog.home.catalog.adapter.BecomeSinglePcAdapter;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.more.brandDiscount.FragmentAddBrandDiscountVersion2;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Activity_Become_Seller extends AppCompatActivity {


    private Context mContext;

    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.radio_Full_catalog)
    RadioButton radio_Full_catalog;

    @BindView(R.id.radio_single_piece)
    RadioButton radio_single_piece;

    @BindView(R.id.radio_price)
    RadioButton radio_price;

    @BindView(R.id.radio_per)
    RadioButton radio_per;

    @BindView(R.id.edit_price)
    EditText edit_price;

    @BindView(R.id.edit_per)
    EditText edit_per;

    @BindView(R.id.txt_current_price)
    TextView txt_current_price;

    @BindView(R.id.edit_enable_duration)
    EditText edit_enable_duration;

    @BindView(R.id.btn_done)
    AppCompatButton btn_done;

    @BindView(R.id.radio_group_isFull)
    RadioGroup radio_group_isFull;

    @BindView(R.id.single_piece_container)
    LinearLayout single_piece_container;

    @BindView(R.id.linear_per_container)
    LinearLayout linear_per_container;

    @BindView(R.id.linear_price_container)
    LinearLayout linear_price_container;

    @BindView(R.id.linear_single_pc_per)
    LinearLayout linear_single_pc_per;

    @BindView(R.id.linear_single_pc_price)
    LinearLayout linear_single_pc_price;

    @BindView(R.id.linear_catalog_select_size_container)
    LinearLayout linear_catalog_select_size_container;

    @BindView(R.id.flex_catalog_select_size)
    FlexboxLayout flex_catalog_select_size;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;


    // Same price
    @BindView(R.id.linear_set_single_price_container)
    LinearLayout linear_set_single_price_container;

    @BindView(R.id.rg_add_margin)
    RadioGroup rg_add_margin;

    @BindView(R.id.radio_single_piece_per)
    RadioButton radio_single_piece_per;

    @BindView(R.id.radio_single_piece_price)
    RadioButton radio_single_piece_price;

    @BindView(R.id.edit_add_margin)
    EditText edit_add_margin;

    @BindView(R.id.txt_single_pc_product_price)
    TextView txt_single_pc_product_price;

    @BindView(R.id.txt_common_margin_note1)
    TextView txt_common_margin_note1;

    @BindView(R.id.txt_common_margin_note2)
    TextView txt_common_margin_note2;

    @BindView(R.id.txt_price_per_design)
    TextView txt_price_per_design;

    @BindView(R.id.txt_selected_margin)
    TextView txt_selected_margin;

    @BindView(R.id.txt_brand_discount_rule)
            TextView txt_brand_discount_rule;

    String catalog_price;
    String catalog_id;
    String product_id;
    String brand_id;
    boolean isAskSinglePiecePrice;

    String new_price;

    CharSequence[] chars;
    LinearLayoutManager mLayoutManager;
    boolean isCatalogWithSize;
    boolean isSizeMandatory;
    Enum seller_type;

    ArrayList<String> select_full_level_size_eav;
    ProductObj[] productObjs;
    ArrayList<String> system_sizes = new ArrayList<>();
    String public_price_range;
    boolean isDifferentPrice;
    ArrayList<ProductObj> productObjArrayList;
    HashMap<String, Boolean> product_price_changed = new HashMap<>();
    BecomeSinglePcAdapter becomeSinglePcAdapter;

    ResponseBrandDiscount discountRule;

    MaterialDialog discountNoRuleDialog;



    private static String TAG = Activity_Become_Seller.class.getSimpleName();
    private int ADD_DISCOUNT_REQUEST = 1501;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = Activity_Become_Seller.this;
        StaticFunctions.initializeAppsee();
        setContentView(R.layout.activity_become_seller);
        ButterKnife.bind(this);
        initListener();
    }

    private void initListener() {
        if (getIntent().getStringExtra("catalog_price") != null) {
            catalog_price = getIntent().getStringExtra("catalog_price");
            if (catalog_price != null && !catalog_price.isEmpty()) {
                if (catalog_price.contains("-")) {
                    String[] priceRangeMultiple = catalog_price.split("-");
                    txt_current_price.setText("Current Price " + " \u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1] + "/Pc.");
                    isDifferentPrice = true;

                    float max_margin_allowed = Math.max(Float.parseFloat(priceRangeMultiple[0]) * 10 / 100, 60);
                    double max_percentage_allowed = (max_margin_allowed) / Float.parseFloat(priceRangeMultiple[0]) * 100;
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    txt_common_margin_note1.setText(String.format(getResources().getString(R.string.max_margin_note_1), String.valueOf(df.format(max_percentage_allowed)) + "%", priceRangeMultiple[0], "60", String.valueOf(max_margin_allowed), "60", String.valueOf(Math.max(max_margin_allowed, Float.parseFloat("60")))));
                    txt_common_margin_note2.setText(String.format(getResources().getString(R.string.max_margin_note_2), "60", priceRangeMultiple[0], "100", String.valueOf(df.format(max_percentage_allowed))));
                    txt_price_per_design.setText("Price per design = " + "\u20B9 " + catalog_price);
                } else {
                    String priceRangeSingle = catalog_price;
                    isDifferentPrice = false;
                    float max_margin_allowed = Math.max(Float.parseFloat(catalog_price) * 10 / 100, 60);
                    double max_percentage_allowed = (max_margin_allowed) / Float.parseFloat(catalog_price) * 100;
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    txt_common_margin_note1.setText(String.format(getResources().getString(R.string.max_margin_note_1), String.valueOf(df.format(max_percentage_allowed)) + "%", catalog_price, "60", String.valueOf(max_margin_allowed), "60", String.valueOf(Math.max(max_margin_allowed, Float.parseFloat("60")))));
                    txt_common_margin_note2.setText(String.format(getResources().getString(R.string.max_margin_note_2), "60", catalog_price, "100", String.valueOf(df.format(max_percentage_allowed))));
                    txt_price_per_design.setText("Price per design = " + "\u20B9 " + catalog_price);
                }
            }
        }

        if (getIntent().getStringExtra("brand_id") != null) {
            brand_id = getIntent().getStringExtra("brand_id");
            getDiscountList(brand_id);
        }

        if (getIntent().getStringExtra("catalog_id") != null) {
            catalog_id = getIntent().getStringExtra("catalog_id");
        }

        if (getIntent().getStringExtra("product_id") != null) {
            product_id = getIntent().getStringExtra("product_id");
        }
        if (getIntent().getBooleanExtra("isAskAllowSinglePrice", false)) {
            isAskSinglePiecePrice = getIntent().getBooleanExtra("isAskAllowSinglePrice", false);
        }

        isAskSinglePiecePrice = true;

        if (getIntent().getStringExtra("category_id") != null) {
            getSizeEav(getIntent().getStringExtra("category_id"));
        }

        try {
            if (getIntent().getSerializableExtra("products") != null) {
                productObjs = (ProductObj[]) getIntent().getSerializableExtra("products");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            finish();
        }


        samePriceSingleListner();


        /**
         * Hide Single Pc. Price Asking
         */

        single_piece_container.setVisibility(View.GONE);


        edit_per.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                edit_price.setText("");
                radio_per.setChecked(true);
                radio_price.setChecked(false);
                return false;
            }
        });

        edit_price.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                edit_per.setText("");
                radio_price.setChecked(true);
                radio_per.setChecked(false);
                return false;

            }
        });


        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });


        linear_single_pc_per.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radio_per.isChecked()) {
                    radio_per.setChecked(false);
                } else {
                    radio_per.setChecked(true);
                }
            }
        });

        linear_single_pc_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radio_price.isChecked()) {
                    radio_price.setChecked(false);
                } else {
                    radio_price.setChecked(true);
                }
            }
        });


        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edit_enable_duration.getText().toString().isEmpty()) {

                    int days = Integer.parseInt(edit_enable_duration.getText().toString().trim());
                    if (days < 10) {
                        edit_enable_duration.setError("Minimum enable duration should be 10");
                        return;
                    }

                    if (days > 90) {
                        edit_enable_duration.setError("Maximum enable duration should be 90");
                        return;
                    }
                    if (isAskSinglePiecePrice && radio_single_piece.isChecked()) {
                        // Single Pc Checked
                        if (radio_single_piece_price.isChecked() || radio_single_piece_per.isChecked()) {
                            if (edit_add_margin.getText().toString().isEmpty()) {
                                edit_add_margin.setError("Please set margin for single pcs.");
                                edit_add_margin.requestFocus();
                                return;
                            } else {
                                float validation_price = 0;
                                if (catalog_price != null) {
                                    String min_price = catalog_price;
                                    if (catalog_price.contains("-")) {
                                        String[] priceRangeMultiple = catalog_price.split("-");
                                        min_price = priceRangeMultiple[0];
                                    }
                                    if (!min_price.contains("-")) {
                                        validation_price = ((Float.parseFloat(min_price) / 100) * 10);
                                        if (validation_price < 60) {
                                            validation_price = 60;
                                        }
                                        float max_margin_allowed = Math.max(Float.parseFloat(min_price) * 10 / 100, 60);
                                        float max_percentage_allowed = (max_margin_allowed) / Float.parseFloat(min_price) * 100;
                                        DecimalFormat decimalFormat;
                                        decimalFormat = new DecimalFormat("#.##");
                                        if (radio_single_piece_per.isChecked() && Float.parseFloat(edit_add_margin.getText().toString()) > max_percentage_allowed) {

                                            edit_add_margin.setError("Margin percentage must be <= " + decimalFormat.format(max_percentage_allowed) + "%");
                                            edit_add_margin.requestFocus();
                                            return;
                                        } else if (radio_single_piece_price.isChecked() && Float.parseFloat(edit_add_margin.getText().toString()) > max_margin_allowed) {
                                            edit_add_margin.setError("Margin amount must be <= " + decimalFormat.format(max_margin_allowed));
                                            edit_add_margin.requestFocus();
                                            return;
                                        }
                                    }
                                }

                            }
                        }
                    }
                    int disable_days = 30;
                    Date todayDate = new Date();
                    Calendar objectCalendar = Calendar.getInstance();
                    objectCalendar.setTime(todayDate);
                    if (!edit_enable_duration.getText().toString().isEmpty()) {
                        disable_days = Integer.parseInt(edit_enable_duration.getText().toString().trim());
                    }
                    objectCalendar.add(Calendar.DATE, disable_days);
                    Date expire_days = new Date(objectCalendar.getTimeInMillis());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    String expireDateString = sdf.format(expire_days);

                    if (seller_type == StartStopHandler.STARTSTOP.FULL_WITH_SIZE && isSizeMandatory) {
                        if (select_full_level_size_eav == null || select_full_level_size_eav.size() == 0) {
                            Toast.makeText(mContext, "Please select any one sizes", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else if (seller_type == StartStopHandler.STARTSTOP.SINGLE_WITH_SIZE) {
                        if (becomeSinglePcAdapter != null && recyclerview.getVisibility() == View.VISIBLE && radio_single_piece.isChecked()) {
                            List<ProductObj> old_product = becomeSinglePcAdapter.getProductObjs();
                            if (!validateSelectedSize(old_product)) {
                                return;
                            }
                        }
                    }
                    addCatalogSeller(radio_Full_catalog.isChecked(), expireDateString, true, Activity_Become_Seller.this, catalog_id, brand_id);
                } else {
                    Toast.makeText(mContext, "Please Enter enable duration", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void calculateCurrentPrice(int type, String value) {
        if (type == 0) {
            // For Percentage
            if (catalog_price.contains("-")) {
                String[] priceRangeMultiple = catalog_price.split("-");
                Float minValue = ((Float.parseFloat(priceRangeMultiple[0]) / 100) * Float.parseFloat(value)) + Float.parseFloat(priceRangeMultiple[0]);
                Float maxValue = ((Float.parseFloat(priceRangeMultiple[1]) / 100) * Float.parseFloat(value)) + Float.parseFloat(priceRangeMultiple[1]);
                new_price = minValue.toString() + "-" + maxValue.toString();
                txt_current_price.setText("Current Price " + "\u20B9" + minValue.toString() + " - " + "\u20B9" + maxValue + "/Pc.");
            } else {
                Float price = ((Float.parseFloat(catalog_price) / 100) * Float.parseFloat(value)) + Float.parseFloat(catalog_price);
                new_price = price.toString();
                txt_current_price.setText("Current  Price " + "\u20B9" + price + "/Pc.");
            }
        } else {
            // For Fixed Price
            if (catalog_price.contains("-")) {
                String[] priceRangeMultiple = catalog_price.split("-");
                Float minValue = Float.parseFloat(value) + Float.parseFloat(priceRangeMultiple[0]);
                Float maxValue = Float.parseFloat(value) + Float.parseFloat(priceRangeMultiple[1]);
                new_price = minValue.toString() + "-" + maxValue.toString();
                txt_current_price.setText("Current Price " + "\u20B9" + minValue.toString() + " - " + "\u20B9" + maxValue + "/Pc.");
            } else {
                Float price = (Float.parseFloat(value)) + Float.parseFloat(catalog_price);
                new_price = price.toString();
                txt_current_price.setText("Current  Price " + "\u20B9" + price + "/Pc.");
            }
        }
    }

    private void hideSizeFullCatalog() {
        if (productObjs != null && productObjs.length > 0) {
            seller_type = StartStopHandler.STARTSTOP.SINGLE_WITH_SIZE;
            linear_catalog_select_size_container.setVisibility(View.GONE);
            setRecyclerview(true);
        }
    }

    private void showSizeFullCatalog() {
        if (productObjs != null && productObjs.length > 0 && system_sizes != null && system_sizes.size() > 0) {
            seller_type = StartStopHandler.STARTSTOP.FULL_WITH_SIZE;
            addSizeCatalogLevel(system_sizes, flex_catalog_select_size, linear_catalog_select_size_container);
            linear_catalog_select_size_container.setVisibility(View.VISIBLE);
        }
        setRecyclerview(true);
    }

    private void addCatalogSeller(boolean isFullCatalog, String expiryDate, final boolean isVisible, final Activity context,
                                  final String catalogID, String brandID) {

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
        HashMap<String, String> params = new HashMap<>();
        params.put("add_brand", brandID);
        if (isFullCatalog) {
            params.put("sell_full_catalog", "true");
        } else {
            if (radio_single_piece_price.isChecked() && !edit_add_margin.getText().toString().isEmpty()) {
                params.put("single_piece_price_fix", edit_add_margin.getText().toString());
            } else if (radio_single_piece_per.isChecked() && !edit_add_margin.getText().toString().isEmpty()) {
                params.put("single_piece_price_percentage", edit_add_margin.getText().toString());
            }
            params.put("sell_full_catalog", "false");
        }

        if (expiryDate != null) {
            params.put("expiry_date", expiryDate);
        }

        params.put("catalog", catalogID);

        HttpManager.getInstance(context).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.companyUrl(context, "catalog-seller", ""), params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isCatalogWithSize || radio_single_piece.isChecked()) {
                    // post available sizes
                    StartStopHandler startStopHandler = new StartStopHandler(context);
                    if (seller_type == StartStopHandler.STARTSTOP.FULL_WITH_SIZE) {
                        // Patch Bundle id
                        if (product_id != null) {
                            Log.e(TAG, "onServerResponse: product id ====>" + product_id);
                            startStopHandler.becomeASellerHandler(seller_type, null, product_id, select_full_level_size_eav, true);
                        }
                    } else if (seller_type == StartStopHandler.STARTSTOP.SINGLE_WITH_SIZE) {
                        startStopHandler.becomeASellerHandler(seller_type, becomeSinglePcAdapter.getProductObjs(), null, null, true);
                    } else {
                        Intent backintent = new Intent();
                        backintent.putExtra("isVisible", isVisible);
                        setResult(Activity.RESULT_OK, backintent);
                        finish();
                    }

                    startStopHandler.setStartStopDoneListener(new StartStopHandler.StartStopDoneListener() {
                        @Override
                        public void onSuccessStart() {
                            Intent backintent = new Intent();
                            backintent.putExtra("isVisible", isVisible);
                            setResult(Activity.RESULT_OK, backintent);
                            finish();
                        }

                        @Override
                        public void onSuccessStop() {
                            // Not Required Always Start Selling
                        }

                        @Override
                        public void onError() {

                        }
                    });


                } else {
                    //
                    Intent backintent = new Intent();
                    backintent.putExtra("isVisible", isVisible);
                    setResult(Activity.RESULT_OK, backintent);
                    finish();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);

            }
        });
    }


    private void addSizeCatalogLevel(final ArrayList<String> sizes1, FlexboxLayout root, LinearLayout linear_select_size_container) {
        if (sizes1 != null && sizes1.size() > 0) {
            final ArrayList<String> string_size = sizes1;
            if (string_size != null && string_size.size() > 0) {
                linear_select_size_container.setVisibility(View.VISIBLE);
                root.removeAllViews();
                for (int j = 0; j < string_size.size(); j++) {
                    final CheckBox checkBox = new CheckBox(mContext);
                    checkBox.setId(j);
                    FlexboxLayout.LayoutParams check_lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
                    check_lp.setMargins(0, 0, 32, 12);
                    setCheckBoxColor(checkBox, R.color.color_primary, R.color.purchase_medium_gray);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        checkBox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    }
                    checkBox.setText(string_size.get(j));
                    checkBox.setTextColor(ContextCompat.getColor(mContext, R.color.purchase_dark_gray));
                    checkBox.setPadding(0, 0, 20, 0);
                    root.addView(checkBox, check_lp);
                    final int finalJ = j;
                    checkBox.setLayoutParams(check_lp);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            if (isChecked) {
                                if (select_full_level_size_eav == null) {
                                    select_full_level_size_eav = new ArrayList<>();
                                }
                                if (!select_full_level_size_eav.contains(string_size.get(finalJ)))
                                    select_full_level_size_eav.add(string_size.get(finalJ));
                            } else {
                                if (select_full_level_size_eav != null) {
                                    if (select_full_level_size_eav.contains(string_size.get(finalJ)))
                                        select_full_level_size_eav.remove(string_size.get(finalJ));
                                }
                            }
                        }
                    });
                }
            }
        } else {
            linear_select_size_container.setVisibility(View.GONE);
            root.removeAllViews();
        }
    }

    public void setCheckBoxColor(CheckBox checkBox, int checkedColor, int uncheckedColor) {
        checkBox.setButtonDrawable(getResources().getDrawable(R.drawable.checkbox_selector));
    }

    private void setRecyclerview(boolean isShow) {
        if (getIntent().getSerializableExtra("products") != null) {
            ProductObj[] productObjs = (ProductObj[]) getIntent().getSerializableExtra("products");
            if (productObjs != null && productObjs.length > 0) {
                if (isShow) {
                    if(!radio_Full_catalog.isChecked()) {
                        seller_type = StartStopHandler.STARTSTOP.SINGLE_WITH_SIZE;
                    }
                    recyclerview.setVisibility(View.VISIBLE);
                    becomeSinglePcAdapter = new BecomeSinglePcAdapter(mContext, Arrays.asList(productObjs), system_sizes,radio_Full_catalog.isChecked());
                    if (becomeSinglePcAdapter != null) {
                        becomeSinglePcAdapter.setDiscountRule(discountRule);
                    }
                    recyclerview.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                    recyclerview.setNestedScrollingEnabled(false);
                    recyclerview.setHasFixedSize(true);
                    recyclerview.setAdapter(becomeSinglePcAdapter);
                } else {
                    recyclerview.setVisibility(View.GONE);
                }

            }
        }

    }


    private boolean validateSelectedSize(List<ProductObj> productObjs) {
        boolean isAnyOneSizeSelected = false;
        for (ProductObj p : productObjs) {
            if (p.isIs_enable()) {
                isAnyOneSizeSelected = true;
                if (isSizeMandatory && (p.getAvailable_sizes() == null || p.getAvailable_sizes().size() == 0)) {
                    Toast.makeText(mContext, "Please select size in every design", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        if (!isAnyOneSizeSelected) {
            Toast.makeText(mContext, "Please select any one product to sell", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getSizeEav(String category_id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
        String url = URLConstants.CATEGORY_EVP_V2 + "?category=" + category_id + "&attribute_slug=" + "size";
        final MaterialDialog progressdialog = StaticFunctions.showProgress(mContext);
        progressdialog.show();
        HttpManager.getInstance((Activity) mContext).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (progressdialog != null) {
                    progressdialog.dismiss();
                }
                final ResponseCategoryEvp[] evpAttribute = Application_Singleton.gson.fromJson(response, ResponseCategoryEvp[].class);
                isCatalogWithSize = false;
                if (evpAttribute != null) {
                    if (evpAttribute.length > 0) {
                        isSizeMandatory = evpAttribute[0].getIs_required();
                        ArrayList<ResponseCategoryEvp.Attribute_values> enumGroupResponses1 = evpAttribute[0].getAttribute_values();
                        for (int i = 0; i < enumGroupResponses1.size(); i++) {
                            system_sizes.add(enumGroupResponses1.get(i).getValue());
                        }
                        isCatalogWithSize = true;
                    }
                } else {
                    isCatalogWithSize = false;
                }


                radio_group_isFull.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int id) {
                        //  if (isCatalogWithSize) {
                        if (radio_Full_catalog.isChecked()) {
                            showSizeFullCatalog();
                        } else {
                            hideSizeFullCatalog();
                        }
                        //  }

                        if (id == R.id.radio_single_piece) {
                            if (isAskSinglePiecePrice) {
                                single_piece_container.setVisibility(View.VISIBLE);
                                linear_set_single_price_container.setVisibility(View.VISIBLE);
                                /*if (!isDifferentPrice) {
                                    linear_set_single_price_container.setVisibility(View.VISIBLE);
                                    if (!edit_add_margin.getText().toString().isEmpty()) {
                                        updatesinglePrice(catalog_price);
                                    }
                                    if(becomeSellerSinglePcAdapter!=null) {
                                        becomeSellerSinglePcAdapter.isProductLevelMargin = false;
                                        becomeSellerSinglePcAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    linear_set_single_price_container.setVisibility(View.GONE);
                                    if(becomeSellerSinglePcAdapter!=null) {
                                        becomeSellerSinglePcAdapter.isProductLevelMargin = true;
                                        becomeSellerSinglePcAdapter.notifyDataSetChanged();
                                    }
                                }*/
                            }
                        } else {
                            single_piece_container.setVisibility(View.GONE);
                            linear_set_single_price_container.setVisibility(View.GONE);
                        }

                    }
                });
                radio_Full_catalog.setChecked(true);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (progressdialog != null) {
                    progressdialog.dismiss();
                }
            }
        });
    }

    private void samePriceSingleListner() {
        edit_add_margin.addTextChangedListener(new SimpleTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = edit_add_margin.getText().toString();
                if (edit_add_margin.getText().toString().trim().isEmpty()) {
                    value = "0";
                }
                if (becomeSinglePcAdapter != null) {
                    if (radio_single_piece_per.isChecked()) {
                        becomeSinglePcAdapter.singlePCAddPer = value;
                    } else {
                        becomeSinglePcAdapter.singlePcAddPrice = value;
                    }
                    becomeSinglePcAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);

            }
        });

        radio_single_piece_per.setChecked(true);

        rg_add_margin.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (radio_single_piece_per.isChecked()) {
                    txt_selected_margin.setText("%");
                    if (becomeSinglePcAdapter != null) {
                        becomeSinglePcAdapter.singlePcAddPrice = null;
                        if (!edit_add_margin.getText().toString().isEmpty()) {
                            becomeSinglePcAdapter.singlePCAddPer = edit_add_margin.getText().toString();
                        }
                    }
                } else {
                    txt_selected_margin.setText("\u20B9");
                    if (becomeSinglePcAdapter != null) {
                        becomeSinglePcAdapter.singlePCAddPer = null;
                        if (!edit_add_margin.getText().toString().isEmpty()) {
                            becomeSinglePcAdapter.singlePcAddPrice = edit_add_margin.getText().toString();
                        }
                    }
                }

                if(becomeSinglePcAdapter!=null)
                    becomeSinglePcAdapter.notifyDataSetChanged();
            }
        });

    }

    public void updatesinglePrice(String charSequence) {
        try {
            if (charSequence.toString().isEmpty()) {
                txt_single_pc_product_price.setText("Price for single Pc.: " + "\u20B9" + Math.round(0));
            } else {
                if (!radio_Full_catalog.isChecked()) {
                    if (radio_single_piece_per.isChecked()) {
                        float edit_text_price = 0;
                        if (!charSequence.toString().isEmpty()) {
                            edit_text_price = Float.parseFloat(charSequence.toString());
                        }
                        Float single_price = (edit_text_price / 100) * Float.parseFloat(edit_add_margin.getText().toString()) + edit_text_price;
                        txt_single_pc_product_price.setText("Price for single Pc.: " + "\u20B9" + Math.round(single_price));
                    } else {
                        float edit_text_price = 0;
                        if (!charSequence.toString().isEmpty()) {
                            edit_text_price = Float.parseFloat(charSequence.toString());
                        }
                        Float single_price = (Float.parseFloat(edit_add_margin.getText().toString()) + edit_text_price);
                        txt_single_pc_product_price.setText("Price for single Pc.: " + "\u20B9" + Math.round(single_price));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void getDiscountList(String brandID) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(this);
        MaterialDialog progressDialog = StaticFunctions.showProgress(mContext);
        progressDialog.show();
        HttpManager.getInstance(this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.DISCOUNT_RULE + "?is_allow_to_upload_catalog_for_brand=" + brandID, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    try {
                        ArrayList<ResponseBrandDiscount> responseData = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<ResponseBrandDiscount>>() {
                        }.getType());
                        ArrayList<ResponseBrandDiscount> tempArrayList = new ArrayList<>();
                        ResponseBrandDiscount allBrandDiscountRule = null;
                        if (responseData.size() > 0) {
                            for (ResponseBrandDiscount discountRule :
                                    responseData) {
                                if (discountRule.isAll_brands()) {
                                    tempArrayList.add(discountRule);
                                    allBrandDiscountRule = discountRule;
                                } else {
                                    if (discountRule.getBrands().contains(brandID)) {
                                        tempArrayList.add(discountRule);
                                    }
                                }
                            }
                            if (tempArrayList.size() > 1 && allBrandDiscountRule != null) {
                                tempArrayList.remove(allBrandDiscountRule);
                            }
                            discountRule = tempArrayList.get(0);

                            if (becomeSinglePcAdapter != null) {
                                becomeSinglePcAdapter.setDiscountRule(discountRule);
                                becomeSinglePcAdapter.notifyDataSetChanged();
                            }
                            txt_brand_discount_rule.setVisibility(View.VISIBLE);
                            txt_brand_discount_rule.setText("Your discount to Wishbook for this brand: "+discountRule.getCash_discount()+"%(Full)"+ " "+ discountRule.getSingle_pcs_discount()+"%(Single pc) ");
                        } else {
                            txt_brand_discount_rule.setVisibility(View.GONE);
                            txt_brand_discount_rule.setText("-");
                           discountNoRuleDialog =  new MaterialDialog.Builder(Activity_Become_Seller.this)
                                    .title("You have not set any discount for this brand")
                                    .content("If you want to continue to upload a catalog under this brand, need to set discount first")
                                    .positiveText("OK")
                                    .cancelable(false)
                                   .autoDismiss(false)
                                    .negativeText("CANCEL")
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    })
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            Application_Singleton.CONTAINER_TITLE = "Set Discount";
                                            Application_Singleton.CONTAINERFRAG = new FragmentAddBrandDiscountVersion2();
                                            startActivityForResult(new Intent(Activity_Become_Seller.this, OpenContainer.class), ADD_DISCOUNT_REQUEST);
                                        }
                                    })
                                    .build();

                           discountNoRuleDialog.show();
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_DISCOUNT_REQUEST && resultCode == Activity.RESULT_OK) {
            if(discountNoRuleDialog!=null) {
                discountNoRuleDialog.dismiss();
            }
            getDiscountList(brand_id);
        }
    }
}
