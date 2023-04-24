package com.wishbook.catalog.home.catalog.add;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.autocomplete.CustomAutoCompleteTextView;
import com.wishbook.catalog.Utils.imagecropper.PhotoTakerUtils;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.AutoCompleteCatalogAdapter;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_Categories;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_brands;
import com.wishbook.catalog.commonmodels.CategoryTree;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.postpatchmodels.CatalogUploadOption;
import com.wishbook.catalog.commonmodels.responses.RequestEav;
import com.wishbook.catalog.commonmodels.responses.ResponseCategoryEvp;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.models.Add_Catalog_Response;
import com.wishbook.catalog.home.models.Response_Brands;
import com.wishbook.catalog.home.more.Fragment_Mybrands;
import com.wishbook.catalog.home.more.adapters.treeview.bean.FileBean;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import fisk.chipcloud.ChipDeletedListener;
import fisk.chipcloud.ChipListener;


public class Fragment_AddCatalog extends GATrackedFragment implements OpenContainer.OnBackPressedListener {

    private SharedPreferences pref;
    boolean priceTypeSingle = false;
    boolean public_priceTypeSingle = false;
    boolean typeOfCatalog = false;
    boolean fullcatalog = false;
    private Bitmap bitmapImage = null;

    @BindView(R.id.hint_brands)
    TextView hint_brands;
    @BindView(R.id.hint_fabric_work)
    TextView hint_fabric_work;
    @BindView(R.id.hint_individual_price)
    TextView hint_individual_price;

    @BindView(R.id.hint_public_individual_price)
    TextView hint_public_individual_price;

    @BindView(R.id.hint_private_catalog_text)
    TextView hint_private_catalog_text;
    @BindView(R.id.hint_product_no_price_text)
    TextView hint_product_no_price_text;
    @BindView(R.id.hint_product_no_sku)
    TextView hint_product_no_sku;
    @BindView(R.id.hint_public_catalog_text)
    TextView hint_public_catalog_text;
    @BindView(R.id.hint_select_brands)
    TextView hint_select_brands;
    @BindView(R.id.hint_sell_full_catalog)
    TextView hint_sell_full_catalog;

    @BindView(R.id.hint_public_single_price)
    TextView hint_public_single_price;

    @BindView(R.id.hint_single_price)
    TextView hint_single_price;
    @BindView(R.id.hint_switch)
    SwitchCompat hint_switch;

    @BindView(R.id.spinner_brands)
    Spinner spinner_brands;

    @BindView(R.id.btn_upload)
    AppCompatButton btnUpload;

    @BindView(R.id.btn_add_fabric)
    AppCompatButton btn_add_fabric;
    @BindView(R.id.btn_clear_all_fabric)
    AppCompatButton btn_clear_all_fabric;
    @BindView(R.id.fabric_container)
    LinearLayout fabric_container;
    @BindView(R.id.flexbox_deleteable)
    FlexboxLayout flexbox_deleteable;

    @BindView(R.id.work_container)
    LinearLayout work_container;
    @BindView(R.id.flexbox_work)
    FlexboxLayout flexbox_work;
    @BindView(R.id.btn_add_work)
    AppCompatButton btn_add_work;
    @BindView(R.id.btn_clear_all_work)
    AppCompatButton btn_clear_all_work;

    @BindView(R.id.style_container)
    LinearLayout style_container;
    @BindView(R.id.flexbox_style)
    FlexboxLayout flexbox_style;
    /*@BindView(R.id.btn_add_style)
    AppCompatButton btn_add_style;
    @BindView(R.id.btn_clear_all_style)
    AppCompatButton btn_clear_all_style;*/


    @BindView(R.id.edit_enable_duration)
    EditText edit_enable_duration;
    @BindView(R.id.hint_disable)
    TextView hint_disable;


    @BindView(R.id.btn_continue1)
    AppCompatButton btn_continue1;

    @BindView(R.id.linear_step_three)
    LinearLayout linear_step_three;
    @BindView(R.id.linear_step_one)
    LinearLayout linear_step_one;
    @BindView(R.id.linear_step_two)
    LinearLayout linear_step_two;

    @BindView(R.id.btn_add_work_first)
    TextView btn_add_work_first;

    @BindView(R.id.btn_add_fabric_first)
    TextView btn_add_fabric_first;

    @BindView(R.id.btn_add_style_first)
    TextView btn_add_style_first;

    @BindView(R.id.txt_edit_dispatch_date)
    TextView txt_edit_dispatch_date;

    @BindView(R.id.spinner_category)
    Spinner spinner_cat;
    EditText input_fabric, input_work;
    TextInputLayout input_fabric_container, input_work_container;

    @BindView(R.id.txt_public_price_label)
    TextView txt_public_price_label;

    @BindView(R.id.radio_strich_type)
    RadioGroup radio_strich_type;

    @BindView(R.id.flex_available_sizes)
    FlexboxLayout flex_available_sizes;
    @BindView(R.id.linear_multi_design)
    LinearLayout linear_multi_design;
    @BindView(R.id.text_enum)
    TextView text_enum;

    @BindView(R.id.btn_minus)
    TextView btn_minus;
    @BindView(R.id.btn_plus)
    TextView btn_plus;
    @BindView(R.id.edit_qty)
    EditText edit_qty;

    @BindView(R.id.edit_other_detail)
    EditText edit_other_detail;
    @BindView(R.id.other_detail_title)
    TextView other_detail_title;

    @BindView(R.id.linear_enter_piece)
    LinearLayout linear_enter_piece;

    @BindView(R.id.edit_dispatch_date)
    EditText edit_dispatch_date;

    @BindView(R.id.radio_group_isFull)
    RadioGroup radio_group_isFull;

    @BindView(R.id.radio_Full_catalog)
    RadioButton radio_Full_catalog;


    @BindView(R.id.radio_price)
    RadioButton radio_price;

    @BindView(R.id.radio_per)
    RadioButton radio_per;

    @BindView(R.id.edit_price)
    EditText edit_price;

    @BindView(R.id.edit_per)
    EditText edit_per;

    @BindView(R.id.single_piece_container)
    LinearLayout single_piece_container;

    @BindView(R.id.linear_per_container)
    LinearLayout linear_per_container;

    @BindView(R.id.linear_price_container)
    LinearLayout linear_price_container;


    ImageView imageView;

    @BindView(R.id.catalog_autocomp)
    CustomAutoCompleteTextView catalog_autocomp;

    @BindView(R.id.linear_single_pc_price)
            LinearLayout linear_single_pc_price;
    @BindView(R.id.linear_single_pc_per)
            LinearLayout linear_single_pc_per;

    EditText input_price, public_input_price;
    CheckBox checkBox, productsSKU, productsPrice, addFabricWork;

    private ArrayList<String> selectedFabric = new ArrayList<>();
    private ArrayList<String> selectedWork = new ArrayList<>();
    private ArrayList<String> selectedStyle = new ArrayList<>();
    final List<FileBean> mDatas = new ArrayList<>();
    ArrayList<String> selectedsize = null;
    AutoCompleteCatalogAdapter autoCompleteCatalogAdapter;
    ArrayList<Response_catalogMini> response_catalogMinis;
    Response_catalogMini selectedCatalog;
    HashMap<String, String> catalogNameparams = new HashMap<>();

    private int add_step;

    RadioGroup public_radprice;
    Handler handler;
    long TotalTxBeforeTest;
    long TotalRxAfterTest;
    Runnable runnable;
    long BeforeTime;
    MaterialDialog dialogCatalogUpload;
    RadioGroup radprice;
    private boolean isEditMode;
    private Add_Catalog_Response old_catalog_response;
    private CatalogUploadOption old_catalog_option;
    RadioGroup type_catalog;
    boolean isChangeCategory;

    // toolbar dependent in openContainer Activity

    final Calendar myCalendar = Calendar.getInstance();

    public Fragment_AddCatalog() {
        // Required empty public constructor
    }


    public static Fragment_AddCatalog newInstance(String param1, String param2) {
        Fragment_AddCatalog fragment = new Fragment_AddCatalog();
        return fragment;
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_add_catalog_step, ga_container, true);
        ButterKnife.bind(this, v);
        pref = getActivity().getSharedPreferences("wishbookprefs", getActivity().MODE_PRIVATE);
        add_step = 1;
        ((OpenContainer) getActivity()).setOnBackPressedListener(Fragment_AddCatalog.this);
        ((OpenContainer) getActivity()).toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doBack();
            }
        });


        //Toolbar toolbar = (Toolbar) v.findViewById(R.id.appbar);
        imageView = (ImageView) v.findViewById(R.id.imageView2);
        checkBox = (CheckBox) v.findViewById(R.id.fulcatalog);
     //   productsSKU = (CheckBox) v.findViewById(R.id.productsWS);
        productsPrice = (CheckBox) v.findViewById(R.id.productsWP);
        addFabricWork = (CheckBox) v.findViewById(R.id.fabric_work);
        radprice = (RadioGroup) v.findViewById(R.id.radprice);
        public_radprice = (RadioGroup) v.findViewById(R.id.public_radprice);
        type_catalog = (RadioGroup) v.findViewById(R.id.type_catalog);
        input_price = (EditText) v.findViewById(R.id.input_price);
        public_input_price = (EditText) v.findViewById(R.id.public_input_price);
        // input_catname = (EditText) v.findViewById(R.id.input_catname);
        input_fabric = (EditText) v.findViewById(R.id.input_fabric);
        input_work = (EditText) v.findViewById(R.id.input_work);
        input_fabric_container = (TextInputLayout) v.findViewById(R.id.input_fabric_container);
        input_work_container = (TextInputLayout) v.findViewById(R.id.input_work_container);
        final LinearLayout public_price_container = (LinearLayout) v.findViewById(R.id.public_price_container);
        final LinearLayout private_price_container = (LinearLayout) v.findViewById(R.id.private_price_container);

        response_catalogMinis = new ArrayList<>();

        catalog_autocomp.setThreshold(3);
        catalog_autocomp.setLoadingIndicator(
                (android.widget.ProgressBar) v.findViewById(R.id.progress_bar));

        linear_enter_piece.setVisibility(View.GONE);

        catalog_autocomp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                selectedCatalog = null;
                if (charSequence.length() > 50) {
                    catalog_autocomp.setError(getResources().getString(R.string.validate_name));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        catalog_autocomp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("onitemselected", "" + position);
                selectedCatalog = (Response_catalogMini) parent.getItemAtPosition(position);
                final CatalogBottomSheetDialogFragment myBottomSheet = CatalogBottomSheetDialogFragment.newInstance(selectedCatalog.getId());
                myBottomSheet.show(getFragmentManager(), myBottomSheet.getTag());
            }
        });

        hint_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    setViews(View.VISIBLE);
                    Application_Singleton.tutorial_pref.edit().putBoolean("add_catalog_tutorial_shown", true).apply();
                } else {
                    setViews(View.GONE);
                    Application_Singleton.tutorial_pref.edit().putBoolean("add_catalog_tutorial_shown", false).apply();
                }
            }
        });

        if (Application_Singleton.tutorial_pref.getBoolean("add_catalog_tutorial_shown", true)) {
            hint_switch.setChecked(true);
        } else {
            hint_switch.setChecked(false);
        }

        //SPANNABLE STRING
        SpannableString ss = new SpannableString(getString(R.string.hint_brands));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Application_Singleton.CONTAINER_TITLE = "Brands";
                Application_Singleton.CONTAINERFRAG = new Fragment_Mybrands();
                Intent intent = new Intent(getActivity(), OpenContainer.class);
                startActivityForResult(intent, 100);
                // StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        if (UserInfo.getInstance(getActivity()).getLanguage().equals("en")) {
            ss.setSpan(clickableSpan, 62, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            ss.setSpan(clickableSpan, 60, ss.length() - 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        hint_brands.setText(ss);
        hint_brands.setMovementMethod(LinkMovementMethod.getInstance());
        hint_brands.setClickable(true);


        radprice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.individual_price) {
                    input_price.setVisibility(View.GONE);
                    priceTypeSingle = false;
                } else {
                    priceTypeSingle = true;
                    input_price.setVisibility(View.VISIBLE);
                }

            }
        });

        radprice.check(R.id.private_single_price); //set single price checked default
        input_price.setVisibility(View.VISIBLE);
        priceTypeSingle = true;

        if (type_catalog.getCheckedRadioButtonId() == R.id.public_catalog) {
            typeOfCatalog = true;
            public_price_container.setVisibility(View.VISIBLE);
            txt_public_price_label.setVisibility(View.VISIBLE);
            // private_price_container.setVisibility(View.VISIBLE);
        } else {
            typeOfCatalog = false;
            public_price_container.setVisibility(View.GONE);
            txt_public_price_label.setVisibility(View.GONE);
            // private_price_container.setVisibility(View.VISIBLE);
        }
        type_catalog.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (!productsPrice.isChecked()) {
                    if (checkedId == R.id.public_catalog) {
                        typeOfCatalog = true;
                        public_priceTypeSingle = true;
                        public_price_container.setVisibility(View.VISIBLE);
                        txt_public_price_label.setVisibility(View.VISIBLE);
                        // private_price_container.setVisibility(View.VISIBLE);
                    } else if (checkedId == R.id.private_catalog) {
                        typeOfCatalog = false;
                        public_priceTypeSingle = false;
                        //public_price_container.setVisibility(View.GONE);
                        //txt_public_price_label.setVisibility(View.GONE);
                        //  private_price_container.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        addFabricWork.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    fabric_container.setVisibility(View.VISIBLE);
                    work_container.setVisibility(View.VISIBLE);
                    //input_fabric_container.setVisibility(View.VISIBLE);
                    //input_work_container.setVisibility(View.VISIBLE);
                } else {
                    fabric_container.setVisibility(View.GONE);
                    work_container.setVisibility(View.GONE);

                    flexbox_work.removeAllViews();
                    selectedWork.clear();
                    btn_clear_all_work.setVisibility(View.GONE);

                    flexbox_deleteable.removeAllViews();
                    selectedFabric.clear();
                    btn_clear_all_fabric.setVisibility(View.GONE);
                    input_fabric_container.setVisibility(View.GONE);
                    input_work_container.setVisibility(View.GONE);
                }
            }
        });
        addFabricWork.setChecked(true);


        public_radprice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!productsPrice.isChecked()) {
                    if (checkedId == R.id.public_individual_price) {
                        public_input_price.setVisibility(View.GONE);
                        public_input_price.setText("");
                        public_priceTypeSingle = false;

                    } else {
                        public_priceTypeSingle = true;
                        public_input_price.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        public_radprice.check(R.id.public_single_price); // set single price checked default
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fullcatalog = isChecked;
            }
        });
        checkBox.setChecked(true);
        v.findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!catalog_autocomp.getText().toString().isEmpty()) {
                    if(isEditMode){
                        openPhotoUploadWidget();
                    } else {
                        checkCatalogNameValidate(catalog_autocomp.getText().toString(), false);
                    }
                } else {
                    new MaterialDialog.Builder(getActivity())
                            .content("Please Enter Catalog name")
                            .positiveText("OK")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });

        v.findViewById(R.id.imageView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!catalog_autocomp.getText().toString().isEmpty()) {
                    if(isEditMode){
                        openPhotoUploadWidget();
                    } else {
                        checkCatalogNameValidate(catalog_autocomp.getText().toString(), false);
                    }
                } else {
                    new MaterialDialog.Builder(getActivity())
                            .content("Please Enter Catalog name")
                            .positiveText("OK")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });

        //toolbar.setVisibility(View.GONE);


        productsPrice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //  private_price_container.setVisibility(View.GONE);
                    public_price_container.setVisibility(View.GONE);
                } else {
                    //  private_price_container.setVisibility(View.VISIBLE);
                    if (typeOfCatalog) {
                        public_price_container.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        btn_add_fabric_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), ActivityFabricSelect.class).putExtra("selectedfabric", selectedFabric).putExtra("type", "fabric"), Application_Singleton.FABRIC_SEARCH_REQUEST_CODE);
            }
        });
        btn_add_fabric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), ActivityFabricSelect.class).putExtra("selectedfabric", selectedFabric).putExtra("type", "fabric"), Application_Singleton.FABRIC_SEARCH_REQUEST_CODE);
            }
        });
        btn_clear_all_fabric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flexbox_deleteable.removeAllViews();
                selectedFabric.clear();
                input_fabric_container.setVisibility(View.GONE);
                btn_clear_all_fabric.setVisibility(View.GONE);
                btn_add_work_first.setVisibility(View.VISIBLE);
            }
        });

        btn_add_work_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), ActivityFabricSelect.class).putExtra("selectedwork", selectedWork).putExtra("type", "work"), Application_Singleton.WORK_SEARCH_REQUEST_CODE);
            }
        });
        btn_add_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), ActivityFabricSelect.class).putExtra("selectedwork", selectedWork).putExtra("type", "work"), Application_Singleton.WORK_SEARCH_REQUEST_CODE);
            }
        });

        btn_clear_all_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flexbox_work.removeAllViews();
                input_work_container.setVisibility(View.GONE);
                selectedWork.clear();
                btn_clear_all_work.setVisibility(View.GONE);
                btn_add_work_first.setVisibility(View.VISIBLE);

            }
        });


        // Style Listener Start
        btn_add_style_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), ActivityFabricSelect.class).putExtra("selectedstyle", selectedStyle).putExtra("type", "style"), Application_Singleton.STYLE_SEARCH_REQUEST_CODE);
            }
        });

        getCategory();


        v.findViewById(R.id.btn_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }


        });

        updateUI(false);
        spinner_brands.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                catalogNameparams.put("brand", spinner_brands.getSelectedItem() == null ? "" : ((Response_Brands) (spinner_brands.getSelectedItem())).getId());
                catalogNameparams.put("category", spinner_cat.getSelectedItem() == null ? "" : String.valueOf(((FileBean) (spinner_cat.getSelectedItem())).fileId));
                catalogNameparams.put("view_type", "public");
                autoCompleteCatalogAdapter = new AutoCompleteCatalogAdapter(getActivity(), R.layout.spinneritem, response_catalogMinis, catalogNameparams);
                catalog_autocomp.setAdapter(autoCompleteCatalogAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                catalogNameparams.put("brand", spinner_brands.getSelectedItem() == null ? "" : ((Response_Brands) (spinner_brands.getSelectedItem())).getId());
                catalogNameparams.put("category", spinner_cat.getSelectedItem() == null ? "" : String.valueOf(((FileBean) (spinner_cat.getSelectedItem())).fileId));
                autoCompleteCatalogAdapter = new AutoCompleteCatalogAdapter(getActivity(), R.layout.spinneritem, response_catalogMinis, catalogNameparams);
                catalog_autocomp.setAdapter(autoCompleteCatalogAdapter);
                isChangeCategory = true;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        initDispatchListener();

        return v;


    }

    private void initDispatchListener() {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDispatch();
            }

        };
        edit_dispatch_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.purchase_medium_gray));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.color_primary));

            }
        });

        txt_edit_dispatch_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.purchase_medium_gray));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.color_primary));
            }
        });
    }




    private void openPhotoUploadWidget() {
        String[] permissions = {
                "android.permission.CAMERA",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, 900);
        } else {
            PhotoTakerUtils.openTaker(getActivity(), "catalogpreview.jpg", true, new PhotoTakerUtils.OnCropFinishListener() {
                @Override
                public void OnCropFinsh(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                    if (bitmap != null) {
                        String decoded = scanQRImage(bitmap);
                        if (decoded != null) {
                            Toast.makeText(getActivity(), decoded, Toast.LENGTH_LONG).show();
                        }
                    }

                    bitmapImage = bitmap;

                    File mDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/wishbooks/");
                    File output = new File(mDirectory, "catalogpreview.jpg");
                    Log.v("selected image", output.getAbsolutePath());
                    if (output.exists()) {
                        Log.v("selected image exits", output.getAbsolutePath());
                    }
                    btnUpload.setText("Change Cover Photo");
                }
            });
        }

    }

    private void postDataCatalog() {


        if (public_priceTypeSingle) {
            if (!checkPriceValidation(public_input_price, true)) {
                return;
            }
        }
        File mDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/wishbooks/");
        final File output = new File(mDirectory, "catalogpreview.jpg");
        Log.v("selected image", output.getAbsolutePath());
        if (bitmapImage != null && !catalog_autocomp.getText().toString().equals("") && spinner_brands != null && spinner_brands.getChildCount() > 0) {
            if (output.exists()) {
                File outputrenamed = new File(mDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
                try {
                    StaticFunctions.copy(output, outputrenamed);
                    HashMap params = new HashMap();
                    params.put("title", catalog_autocomp.getText().toString());
                    params.put("brand", spinner_brands.getSelectedItem() == null ? "" : ((Response_Brands) (spinner_brands.getSelectedItem())).getId());
                    params.put("category", "" + spinner_cat.getSelectedItem() == null ? "" : ((FileBean) (spinner_cat.getSelectedItem())).fileId);
                    if (priceTypeSingle == true) {
                        params.put("price", "" + input_price.getText().toString());
                    }

                    if (public_priceTypeSingle == true) {
                        //public_price
                        //params.put("price", "" + public_input_price.getText().toString());
                        params.put("public_price", "" + public_input_price.getText().toString());
                    }

                    if (typeOfCatalog == true) {
                        //public
                        params.put("view_permission", "public");
                    } else {
                        //private
                        params.put("view_permission", "push");
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
                    params.put("expiry_date", expireDateString);

                    RequestEav requestEav = new RequestEav();
                    HashMap<String, Object> eav = new HashMap<>();
                    if (addFabricWork.isChecked()) {
                        if (selectedFabric != null && selectedFabric.size() > 0) {
                            requestEav.setFabric(selectedFabric);
                            eav.put("fabric", selectedFabric);
                        }

                        if (selectedWork != null && selectedWork.size() > 0) {
                            requestEav.setWork(selectedWork);
                            eav.put("work", selectedWork);
                        }

                        if (selectedStyle != null && selectedStyle.size() > 0) {
                            eav.put("style", selectedStyle);
                        }
                    }

                    if (selectedsize != null && selectedsize.size() > 0) {
                        requestEav.setSize(selectedsize);
                        eav.put(flex_available_sizes.getTag().toString(), selectedsize);
                        // requestEav.setNumber_pcs_design_per_set(Integer.parseInt(edit_qty.getText().toString().trim()));
                    }
                    if (radio_strich_type.getCheckedRadioButtonId() != -1) {
                        String strich_type = ((RadioButton) radio_strich_type.findViewById(radio_strich_type.getCheckedRadioButtonId())).getText().toString();
                        requestEav.setStitching_type(strich_type);
                        eav.put(radio_strich_type.getTag().toString(), strich_type);
                    }

                    if (!edit_other_detail.getText().toString().trim().isEmpty()) {
                        requestEav.setOther(edit_other_detail.getText().toString().trim());
                        eav.put("other", edit_other_detail.getText().toString().trim());
                    }

                    params.put("eav", Application_Singleton.gson.toJson(eav));

                    if(!edit_dispatch_date.getText().toString().isEmpty()){
                        String dispatch_server = DateUtils.changeDateFormat(StaticFunctions.CLIENT_DISPLAY_FORMAT, StaticFunctions.SERVER_POST_FORMAT, edit_dispatch_date.getText().toString());
                        if (!dispatch_server.isEmpty() && !dispatch_server.equals("")) {
                            params.put("dispatch_date",dispatch_server);
                        }
                    }

                    if(radio_Full_catalog.isChecked()){
                        params.put("sell_full_catalog",true);
                    } else {
                        params.put("sell_full_catalog",false);
                        if(radio_price.isChecked()){
                            if (edit_price.getText().toString().isEmpty()) {
                                params.put("single_piece_price", "0");
                            } else {
                                params.put("single_piece_price", edit_price.getText().toString());
                            }
                        } else if(radio_per.isChecked()){
                            if (edit_per.getText().toString().isEmpty()) {
                                params.put("single_piece_price_percentage", "0");
                            } else {
                                params.put("single_piece_price_percentage", edit_per.getText().toString());
                            }

                        } else {
                            params.put("single_piece_price_percentage", "0");
                        }
                    }



                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

                    String url = URLConstants.companyUrl(getActivity(), "catalogs", "");
                    HttpManager.METHOD method = HttpManager.METHOD.FILEUPLOAD;

                    if (isEditMode) {
                        method = HttpManager.METHOD.FILEUPLOADPUTWITHPROGRESS;
                        url = URLConstants.companyUrl(getActivity(), "catalogs", "") + old_catalog_response.getId() + "/";
                    } else {
                        method = HttpManager.METHOD.FILEUPLOAD;
                        url = URLConstants.companyUrl(getActivity(), "catalogs", "");
                    }
                    if(!StaticFunctions.isOnline(getActivity())){
                        HttpManager.showNetworkAlert(getActivity());
                        return;
                    }
                    showUploadDialog();
                    HttpManager.getInstance(getActivity()).requestwithFile(method, url, params, headers, "thumbnail", "image/jpg", outputrenamed, false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {

                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            hideUploadDialog();
                            //output.delete();
                            Toast.makeText(getActivity(), "Catalog added successfully", Toast.LENGTH_SHORT).show();
                            old_catalog_response = new Gson().fromJson(response, Add_Catalog_Response.class);


                            // Response_catalogAdd response_catalogAdd = new Gson().fromJson(response, Response_catalogAdd.class);
                            Fragment_AddProduct addProduct = new Fragment_AddProduct();
                            Bundle bundle = new Bundle();
                            bundle.putString("catalog_category", new Gson().toJson(old_catalog_response.getCategory()));
                            bundle.putString("catalog_brand", old_catalog_response.getBrand());
                            bundle.putString("catalog_id", old_catalog_response.getId());
                            if (priceTypeSingle == true) {
                                bundle.putString("catalog_price", public_input_price.getText().toString());
                            }
                            if (public_priceTypeSingle == true) {
                                bundle.putString("public_price", "" + public_input_price.getText().toString());
                            }

                            if (addFabricWork.isChecked()) {
                                StringBuilder fabricString = new StringBuilder();
                                if (input_fabric_container.getVisibility() == View.VISIBLE) {
                                    if (selectedFabric != null) {
                                        if (!input_fabric.getText().toString().isEmpty()) {
                                            selectedFabric.add(input_fabric.getText().toString().trim());
                                        }
                                    }
                                }
                                if (input_work_container.getVisibility() == View.VISIBLE) {
                                    if (selectedWork != null) {
                                        if (!input_work.getText().toString().isEmpty()) {
                                            selectedWork.add(input_work.getText().toString().trim());
                                        }

                                    }
                                }
                                if (selectedFabric != null && selectedFabric.size() > 0) {
                                    for (int i = 0; i < selectedFabric.size(); i++) {
                                        if (i == selectedFabric.size() - 1) {
                                            fabricString.append(selectedFabric.get(i));
                                        } else {
                                            fabricString.append(selectedFabric.get(i)).append(", ");
                                        }
                                    }
                                }

                                StringBuilder workString = new StringBuilder();
                                if (selectedWork != null && selectedWork.size() > 0) {
                                    for (int i = 0; i < selectedWork.size(); i++) {
                                        if (i == selectedWork.size() - 1) {
                                            workString.append(selectedWork.get(i));
                                        } else {
                                            workString.append(selectedWork.get(i)).append(", ");
                                        }
                                    }
                                }

                                Log.i("TAG", "onServerResponse: Fabric Set==>" + Application_Singleton.gson.toJson(selectedFabric));
                                Log.i("TAG", "onServerResponse: Work Set==>" + Application_Singleton.gson.toJson(selectedWork));
                                bundle.putString("fabric", Application_Singleton.gson.toJson(selectedFabric));
                                bundle.putString("work", Application_Singleton.gson.toJson(selectedWork));
                            }

                            bundle.putString("view_permission", old_catalog_response.getView_permission());
                            bundle.putString("catalog_fullproduct", old_catalog_response.getSell_full_catalog());
                           // bundle.putBoolean("productsWithoutSKU", productsSKU.isChecked());
                            bundle.putBoolean("productsWithoutPrice", productsPrice.isChecked());
                            if(!radio_Full_catalog.isChecked()){
                                bundle.putBoolean("catalog_fullproduct",false);
                                if(!edit_per.getText().toString().isEmpty())
                                    bundle.putString("singlePcAddPer",edit_per.getText().toString());
                                if(!edit_price.getText().toString().isEmpty())
                                    bundle.putString("singlePcAddPrice",edit_price.getText().toString());
                            } else {
                                bundle.putBoolean("catalog_fullproduct",true);
                            }


                            addProduct.setArguments(bundle);
                            Application_Singleton.CONTAINER_TITLE = "Add Designs";
                            Application_Singleton.CONTAINERFRAG = addProduct;


                            //posting additional Options to Server
                            final CatalogUploadOption option = new CatalogUploadOption();
                            if (type_catalog.getCheckedRadioButtonId() == R.id.private_catalog) {
                                if (public_priceTypeSingle) {
                                    if (!public_input_price.getText().toString().isEmpty()
                                            && !public_input_price.getText().toString().equals("0")
                                            && !public_input_price.getText().toString().equals("")
                                            && !public_input_price.getText().toString().equals("0.0"))
                                        option.setPrivate_single_price(public_input_price.getText().toString());
                                    option.setPublic_single_price("0");
                                }
                            }
                            if (type_catalog.getCheckedRadioButtonId() == R.id.public_catalog) {
                                if (public_priceTypeSingle) {
                                    if (!public_input_price.getText().toString().equals("0")
                                            && !public_input_price.getText().toString().equals("")
                                            && !public_input_price.getText().toString().equals("0.0")) {
                                        option.setPublic_single_price(public_input_price.getText().toString());
                                        option.setPrivate_single_price("0");
                                    }
                                }
                            }


                            option.setWithout_price(productsPrice.isChecked());
                         //   option.setWithout_sku(productsSKU.isChecked());
                            option.setCatalog(old_catalog_response.getId());


                            postCatalogOptions(option);


                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {
                            hideUploadDialog();
                            StaticFunctions.showResponseFailedDialog(error);
                            Toast.makeText(getActivity(), error.getErrormessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

                } catch (IOException e) {
                    hideUploadDialog();
                    Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }


            }

        } else {
            if (catalog_autocomp.getText().toString().equals("")) {
                catalog_autocomp.setError("Catalog name cannot be empty");
                catalog_autocomp.requestFocus();
                Toast.makeText(getActivity(), "Catalog name cannot be empty", Toast.LENGTH_LONG).show();

            } else if (bitmapImage == null) {
                new MaterialDialog.Builder(getActivity())
                        .title("Image Error")
                        .content("Select Image")
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            } else if (spinner_brands.getChildCount() < 1) {
                Toast.makeText(getActivity(), "Please select the brand", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateDispatch() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        edit_dispatch_date.setText(sdf.format(myCalendar.getTime()));
        txt_edit_dispatch_date.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 900) {
            if (grantResults.length > 0) {
                // Fill with results
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openPhotoUploadWidget();
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    private void getCategory() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "category", "") + "?parent=10", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                CategoryTree[] ct = Application_Singleton.gson.fromJson(response, CategoryTree[].class);
                if (isAdded() && !isDetached()) {
                    if (ct != null) {
                        if (ct.length > 0) {
                            if (ct[0].getId() != null) {
                                for (int i = 0; i < ct.length; i++) {
                                    CategoryTree ctitem = ct[i];
                                    FileBean fileBean;
                                    if (ctitem.getparent_category() == null) {
                                        fileBean = new FileBean(ctitem.getId(), -1, ctitem.getcategory_name());
                                        mDatas.add(fileBean);
                                    } else {
                                        fileBean = new FileBean(ctitem.getId(), ctitem.getparent_category(), ctitem.getcategory_name());
                                        mDatas.add(fileBean);
                                    }
                                }
                            }
                        }
                    }
                    SpinAdapter_Categories spinAdapter_categories = new SpinAdapter_Categories(getActivity(), R.layout.spinneritem, mDatas);
                    spinner_cat.setAdapter(spinAdapter_categories);
                    getBrands();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    private void postCatalogOptions(CatalogUploadOption option) {
        Gson gson1 = new Gson();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.METHOD method = HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS;
        String url = URLConstants.companyUrl(getContext(), "catalogs_upload_option", "");
        if (isEditMode) {
            method = HttpManager.METHOD.PUTWITHPROGRESS;
            url = URLConstants.companyUrl(getContext(), "catalogs_upload_option", "") + old_catalog_option.getId() + "/";
        } else {
            method = HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS;
            url = URLConstants.companyUrl(getContext(), "catalogs_upload_option", "");
        }
        HttpManager.getInstance((Activity) getContext()).requestwithObject(method, url, (gson1.fromJson(gson1.toJson(option), JsonObject.class)), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    if (getContext() != null) {
                        if(handler!=null){
                            handler.removeCallbacks(runnable);
                            handler = null;
                        }
                        old_catalog_option = Application_Singleton.gson.fromJson(response, CatalogUploadOption.class);
                        Intent intent = new Intent(getActivity(), OpenContainer.class);
                        Fragment_AddCatalog.this.startActivityForResult(intent, 500);
                        // StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                        isEditMode = true;
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (getContext() != null) {
                    //StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                    // isEditMode = true;
                    // getActivity().finish();
                }
            }
        });
    }

    private void getBrands() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "brands", "") + "?type=my", null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
                if (isAdded() && !isDetached()) {
                    Response_Brands[] response_brands = Application_Singleton.gson.fromJson(response, Response_Brands[].class);
                    if (response_brands.length > 0) {
                        if (response_brands[0].getId() != null) {
                            SpinAdapter_brands spinAdapter_brands = new SpinAdapter_brands(getActivity(), R.layout.spinneritem, response_brands);
                            spinner_brands.setAdapter(spinAdapter_brands);
                        }
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    private void setViews(int visible) {
        hint_brands.setVisibility(visible);
        hint_fabric_work.setVisibility(visible);
        hint_individual_price.setVisibility(visible);
        hint_public_individual_price.setVisibility(visible);
        hint_public_single_price.setVisibility(visible);
        hint_private_catalog_text.setVisibility(visible);
        hint_public_catalog_text.setVisibility(visible);
        hint_product_no_price_text.setVisibility(View.GONE);
        hint_product_no_sku.setVisibility(View.GONE);
        hint_select_brands.setVisibility(View.GONE);
        hint_sell_full_catalog.setVisibility(visible);
        hint_single_price.setVisibility(visible);
        hint_disable.setVisibility(visible);
    }

    public static String scanQRImage(Bitmap bMap) {
        String contents = null;

        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        try {
            Result result = reader.decode(bitmap);
            contents = result.getText();
        } catch (Exception e) {
            Log.e("QrTest", "Error decoding barcode", e);
        }
        return contents;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((OpenContainer) getActivity()).toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doBack();
            }
        });
    }

    public boolean checkPriceValidation(EditText editText, boolean isPublic) {
        if (editText.getText().toString().isEmpty()) {
            editText.requestFocus();
            editText.setError(getResources().getString(R.string.error_empty_price));
            //Toast.makeText(getActivity(), getResources().getString(R.string.error_empty_price), Toast.LENGTH_LONG).show();
            return false;
        } else {
            float productPrice = Float.parseFloat(editText.getText().toString());
            if (productPrice > 100) {
                return true;
            } else if (productPrice == 0.0) {
                editText.setError(getResources().getString(R.string.error_null_price));
                return false;
            } else {
                editText.requestFocus();
                if (isPublic) {
                    editText.setError(getResources().getString(R.string.error_min_price_public));
                } else {
                    editText.setError(getResources().getString(R.string.error_min_price_private));
                }
                return false;
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TAG", "onActivityResult: Add Catalog request code" + requestCode + "\n Result code" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Application_Singleton.FABRIC_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedFabric = data.getStringArrayListExtra("fabric");
            if (selectedFabric.size() > 0) {
                btn_add_fabric.setVisibility(View.VISIBLE);
                btn_clear_all_fabric.setVisibility(View.VISIBLE);
                btn_add_fabric_first.setVisibility(View.GONE);
            }
            flexbox_deleteable.removeAllViews();
            ChipCloudConfig deleteableConfig = new ChipCloudConfig()
                    .selectMode(ChipCloud.SelectMode.mandatory)
                    .checkedChipColor(Color.parseColor("#ddaa00"))
                    .checkedTextColor(Color.parseColor("#ffffff"))
                    .uncheckedChipColor(Color.parseColor("#e0e0e0"))
                    .showClose(Color.parseColor("#a6a6a6"))
                    .useInsetPadding(true)
                    .uncheckedTextColor(Color.parseColor("#000000"));

            ChipCloud deleteableCloud = new ChipCloud(getActivity(), flexbox_deleteable, deleteableConfig);
            for (String s : selectedFabric) {
                Log.i("TAG", "Add chip" + s);
                if (s.equalsIgnoreCase("other")) {
                    input_fabric_container.setVisibility(View.VISIBLE);
                }
                deleteableCloud.addChip(s);
            }

            deleteableCloud.setDeleteListener(new ChipDeletedListener() {
                @Override
                public void chipDeleted(int index, String label) {
                    if (label.equalsIgnoreCase("other")) {
                        input_fabric_container.setVisibility(View.GONE);
                    }
                    selectedFabric.remove(label);
                    if (selectedFabric.size() == 0) {
                        btn_clear_all_fabric.setVisibility(View.GONE);
                        btn_add_fabric.setVisibility(View.GONE);
                        btn_add_fabric_first.setVisibility(View.VISIBLE);
                    }
                    Log.d("TAG", String.format("chipDeleted at index: %d label: %s", index, label));
                }
            });
        } else if (requestCode == Application_Singleton.WORK_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedWork = data.getStringArrayListExtra("work");
            if (selectedWork.size() > 0) {
                btn_add_work.setVisibility(View.VISIBLE);
                btn_clear_all_work.setVisibility(View.VISIBLE);
                btn_add_work_first.setVisibility(View.GONE);
            }
            flexbox_work.removeAllViews();
            ChipCloudConfig deleteableConfig = new ChipCloudConfig()
                    .selectMode(ChipCloud.SelectMode.multi)
                    .checkedChipColor(Color.parseColor("#ddaa00"))
                    .checkedTextColor(Color.parseColor("#ffffff"))
                    .uncheckedChipColor(Color.parseColor("#e0e0e0"))
                    .showClose(Color.parseColor("#a6a6a6"))
                    .useInsetPadding(true)
                    .uncheckedTextColor(Color.parseColor("#000000"));

            ChipCloud deleteableCloud = new ChipCloud(getActivity(), flexbox_work, deleteableConfig);
            for (String s : selectedWork) {
                Log.i("TAG", "Add chip" + s);
                if (s.equalsIgnoreCase("other")) {
                    input_work_container.setVisibility(View.VISIBLE);
                }
                deleteableCloud.addChip(s);
            }

            deleteableCloud.setDeleteListener(new ChipDeletedListener() {
                @Override
                public void chipDeleted(int index, String label) {
                    if (label.equals("other")) {
                        input_work_container.setVisibility(View.GONE);
                    }
                    selectedWork.remove(label);
                    if (selectedWork.size() == 0) {
                        btn_add_work_first.setVisibility(View.VISIBLE);
                        btn_add_work.setVisibility(View.GONE);
                        btn_clear_all_work.setVisibility(View.GONE);
                    }
                }
            });

        } else if (requestCode == Application_Singleton.STYLE_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedStyle = data.getStringArrayListExtra("style");
            if (selectedStyle.size() > 0) {
                // btn_add_style.setVisibility(View.VISIBLE);
                // btn_clear_all_style.setVisibility(View.VISIBLE);
                btn_add_style_first.setVisibility(View.GONE);
            }
            flexbox_style.removeAllViews();
            ChipCloudConfig deleteableConfig = new ChipCloudConfig()
                    .selectMode(ChipCloud.SelectMode.multi)
                    .checkedChipColor(Color.parseColor("#ddaa00"))
                    .checkedTextColor(Color.parseColor("#ffffff"))
                    .uncheckedChipColor(Color.parseColor("#e0e0e0"))
                    .showClose(Color.parseColor("#a6a6a6"))
                    .useInsetPadding(true)
                    .uncheckedTextColor(Color.parseColor("#000000"));

            ChipCloud deleteableCloud = new ChipCloud(getActivity(), flexbox_style, deleteableConfig);
            for (String s : selectedStyle) {
                deleteableCloud.addChip(s);
            }

            deleteableCloud.setDeleteListener(new ChipDeletedListener() {
                @Override
                public void chipDeleted(int index, String label) {
                    selectedStyle.remove(label);
                    if (selectedStyle.size() == 0) {
                        btn_add_style_first.setVisibility(View.VISIBLE);
                        //  btn_add_style.setVisibility(View.GONE);
                        //  btn_clear_all_style.setVisibility(View.GONE);
                    }
                }
            });
        } else if (requestCode == 100) {
            getBrands();
        } else if (requestCode == 500 && resultCode == Activity.RESULT_OK) {
            if (handler != null) {
                handler.removeCallbacks(runnable);
                handler = null;
            }
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }
    }


    public void stepOne() {
        btn_continue1.setText("Continue");
        linear_step_one.setVisibility(View.VISIBLE);
        linear_step_two.setVisibility(View.GONE);
        linear_step_three.setVisibility(View.GONE);

        if(isEditMode){
            catalog_autocomp.setEnabled(false);
        }
        isChangeCategory = false;

        btn_continue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmapImage != null && !catalog_autocomp.getText().toString().equals("") && spinner_brands != null && spinner_brands.getChildCount() > 0) {
                    if (!catalog_autocomp.getText().toString().isEmpty()) {
                        if (catalog_autocomp.getText().toString().length() > 50) {
                            catalog_autocomp.setError(getResources().getString(R.string.validate_name));
                        } else {
                            if(isEditMode){
                                // in Edit mode name not check
                                add_step = add_step + 1;
                                stepTwo(false);
                            } else {
                                checkCatalogNameValidate(catalog_autocomp.getText().toString(), true);
                            }
                        }
                    }
                } else {
                    if (catalog_autocomp.getText().toString().equals("")) {
                        catalog_autocomp.setError("Catalog name cannot be empty");
                        catalog_autocomp.requestFocus();
                        Toast.makeText(getActivity(), "Catalog name cannot be empty", Toast.LENGTH_LONG).show();
                    } else if (spinner_brands.getChildCount() < 1) {
                        Toast.makeText(getActivity(), "Please select the brand", Toast.LENGTH_LONG).show();
                    } else if (bitmapImage == null) {
                        new MaterialDialog.Builder(getActivity())
                                .title("Image Error")
                                .content("Select Image")
                                .positiveText("OK")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                }
            }
        });
        ((OpenContainer) getActivity()).toolbar.setTitle("Add Catalog");
        ((OpenContainer) getActivity()).toolbar.setBackgroundColor(getResources().getColor(R.color.color_primary));
        ((OpenContainer) getActivity()).toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        Drawable icon = getResources().getDrawable(R.drawable.ic_arrow_back_24dp);
        icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        ((OpenContainer) getActivity()).toolbar.setNavigationIcon(icon);
    }

    public void stepTwo(boolean isFromBack) {
        btn_continue1.setText("Enter Price Details");
        linear_step_one.setVisibility(View.GONE);
        linear_step_two.setVisibility(View.VISIBLE);
        linear_step_three.setVisibility(View.GONE);
        btn_continue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedFabric != null) {
                    if (selectedFabric.size() == 0) {
                        Toast.makeText(getActivity(), "Please Select any fabric", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Select any fabric", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedWork != null) {
                    if (selectedWork.size() == 0) {
                        Toast.makeText(getActivity(), "Please Select any work", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } else {
                    Toast.makeText(getActivity(), "Please Select any work", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (linear_multi_design.getVisibility() == View.VISIBLE) {
                  /*  if (edit_qty.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.minimum_quantity), Toast.LENGTH_SHORT).show();
                        return;
                    }*/

                    if (selectedsize != null && selectedsize.size() > 0) {
                        // size is selected
                    } else {
                        Toast.makeText(getActivity(), "Select Size", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (radio_strich_type.getVisibility() == View.VISIBLE) {
                    if (radio_strich_type.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(getActivity(), "Please Select Strich Type", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (edit_enable_duration.getText().toString().trim().isEmpty()) {
                    edit_enable_duration.setError("Duration can't be empty");
                    return;
                } else {
                    int days = Integer.parseInt(edit_enable_duration.getText().toString().trim());
                    if (days < 10) {
                        edit_enable_duration.setError("Minimum enable duration should be 10");
                        return;
                    }
                }

                add_step = add_step + 1;
                stepThree(false);
            }
        });
        ((OpenContainer) getActivity()).toolbar.setTitle("Product Details");
        ((OpenContainer) getActivity()).toolbar.setBackgroundColor(getResources().getColor(R.color.white));
        ((OpenContainer) getActivity()).toolbar.setTitleTextColor(getResources().getColor(R.color.color_primary));
        Drawable icon = getResources().getDrawable(R.drawable.ic_arrow_back_24dp);
        icon.setColorFilter(getResources().getColor(R.color.color_primary), PorterDuff.Mode.SRC_IN);
        ((OpenContainer) getActivity()).toolbar.setNavigationIcon(icon);
        String categoryId = String.valueOf(((FileBean) spinner_cat.getSelectedItem()).fileId);
        if (!isFromBack) {
            if(isChangeCategory){
                radio_strich_type.setVisibility(View.GONE);
                radio_strich_type.removeAllViews();
                linear_multi_design.setVisibility(View.GONE);
                flex_available_sizes.removeAllViews();
                selectedsize = null;
                edit_other_detail.setText("");
                other_detail_title.setText("Other Details");
                getCategoryEavAttribute(categoryId);
            }

        }


    }

    public void stepThree(boolean isFromBack) {
        btn_continue1.setText("Add Your Designs");
        linear_step_one.setVisibility(View.GONE);
        linear_step_two.setVisibility(View.GONE);
        linear_step_three.setVisibility(View.VISIBLE);

        ((OpenContainer) getActivity()).toolbar.setTitle("Price Details");
        ((OpenContainer) getActivity()).toolbar.setBackgroundColor(getResources().getColor(R.color.white));
        ((OpenContainer) getActivity()).toolbar.setTitleTextColor(getResources().getColor(R.color.color_primary));
        Drawable icon = getResources().getDrawable(R.drawable.ic_arrow_back_24dp);
        icon.setColorFilter(getResources().getColor(R.color.color_primary), PorterDuff.Mode.SRC_IN);
        ((OpenContainer) getActivity()).toolbar.setNavigationIcon(icon);
        if (!isFromBack) {
            initSinglePcListener();
        }

        btn_continue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!radio_Full_catalog.isChecked()){
                    // Single Pc Checked
                    if(radio_price.isChecked()){
                        if(edit_price.getText().toString().isEmpty()){
                            //edit_price.setError("Please enter price");
                            //return;
                        } else {
                           float price = Float.parseFloat(edit_price.getText().toString());
                            if (price > 1000) {
                                edit_price.setError("You can not add margin more than Rs. 1000");
                               return;
                           }
                        }
                    } else if (radio_per.isChecked()){
                        if(edit_per.getText().toString().isEmpty()){
                            //edit_per.setError("Please enter percentage");
                            //return;
                        } else {
                            float price = Float.parseFloat(edit_per.getText().toString());
                            if(price > 20){
                                edit_per.setError("You can not add margin more than 20 %");
                                return;
                            }
                        }
                    }
                }
                add_step = add_step + 1;
                postDataCatalog();
            }
        });
    }

    private void initSinglePcListener(){
        radio_group_isFull.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if (id == R.id.radio_single_piece) {
                    single_piece_container.setVisibility(View.VISIBLE);
                } else {
                    single_piece_container.setVisibility(View.GONE);
                }
            }
        });

        // Default Condition
        radio_Full_catalog.setChecked(true);

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

        radio_per.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    edit_price.setText("");
                    radio_price.setChecked(false);

                }

            }
        });

        radio_price.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    edit_per.setText("");
                    radio_per.setChecked(false);

                }
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
                if(radio_price.isChecked()){
                    radio_price.setChecked(false);
                } else {
                    radio_price.setChecked(true);
                }
            }
        });
    }

    public void updateUI(boolean isFromBack) {
        if (add_step == 1) {
            stepOne();
        } else if (add_step == 2) {
            stepTwo(isFromBack);
        } else if (add_step == 3) {
            stepThree(true);
        }
    }

    @Override
    public void doBack() {
        if (add_step == 1) {
            if (getActivity() != null)
                getActivity().finish();
        } else {
            add_step = add_step - 1;
            updateUI(true);
        }
    }

    public void checkCatalogNameValidate(final String catalogname, final boolean isMoveNext) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) getContext());
        HashMap<String, String> params = new HashMap<>();
        params.putAll(catalogNameparams);
        params.put("title", catalogname);
        HttpManager.getInstance((Activity) getContext()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getContext(), "catalog_dropdown", ""), params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    hideProgress();
                    if (isAdded() && !isDetached()) {
                        Response_catalogMini[] catalogMinis = new Gson().fromJson(response, Response_catalogMini[].class);
                        boolean isAvailable = false;
                        if (catalogMinis.length > 0) {
                            for (int i = 0; i < catalogMinis.length; i++) {
                                if (catalogMinis[i].getTitle().equalsIgnoreCase(catalogname)) {
                                    isAvailable = true;
                                }
                            }
                            if (isAvailable) {
                                final CatalogBottomSheetDialogFragment myBottomSheet = CatalogBottomSheetDialogFragment.newInstance(catalogMinis[0].getId());
                                myBottomSheet.show(getFragmentManager(), myBottomSheet.getTag());
                            } else {
                                if (isMoveNext) {
                                    add_step = add_step + 1;
                                    stepTwo(false);
                                } else {
                                    openPhotoUploadWidget();
                                }
                            }

                        } else {
                            if (isMoveNext) {
                                add_step = add_step + 1;
                                stepTwo(false);
                            } else {
                                openPhotoUploadWidget();
                            }

                        }
                    }
                } catch (JsonSyntaxException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void getCategoryEavAttribute(String categoryId) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.CATEGORY_EVP + "?category=" + categoryId, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                if (isAdded() && !isDetached()) {
                    final ResponseCategoryEvp[] evpAttribute = Application_Singleton.gson.fromJson(response, ResponseCategoryEvp[].class);
                    if (evpAttribute.length > 0) {
                        for (int i = 0; i < evpAttribute.length; i++) {
                            if (evpAttribute[i].getAttribute_datatype().equalsIgnoreCase("enum")) {
                                radio_strich_type.setVisibility(View.VISIBLE);
                                text_enum.setVisibility(View.VISIBLE);
                                text_enum.setText(StaticFunctions.formatErrorTitle(evpAttribute[i].getAttribute_name()));
                                radio_strich_type.setTag(evpAttribute[i].getAttribute_name());
                                radio_strich_type.removeAllViews();
                                //other_detail_title.setText("Stitching Details");
                                for (int j = 0; j < evpAttribute[i].getAttribute_values().size(); j++) {
                                    RadioButton radioButtonView = new RadioButton(getActivity());
                                    radioButtonView.setPadding(40, 0, 0, 0);
                                    radioButtonView.setId(j);
                                    radioButtonView.setText(evpAttribute[i].getAttribute_values().get(j).getValue());
                                    radio_strich_type.addView(radioButtonView);
                                }
                            } else {
                                text_enum.setVisibility(View.GONE);
                            }
                            if (evpAttribute[i].getAttribute_datatype().equalsIgnoreCase("multi")) {
                                linear_multi_design.setVisibility(View.VISIBLE);
                                flex_available_sizes.removeAllViews();
                                //other_detail_title.setText("Design & Sizes");
                                btn_plus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!edit_qty.getText().toString().trim().isEmpty()) {
                                            int oldqty = Integer.parseInt(edit_qty.getText().toString());
                                            edit_qty.setText(String.valueOf(oldqty + 1));
                                        } else {
                                            edit_qty.setText("1");
                                        }
                                    }
                                });

                                btn_minus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!edit_qty.getText().toString().trim().isEmpty()) {
                                            int oldqty = Integer.parseInt(edit_qty.getText().toString());
                                            if (oldqty > 1) {
                                                edit_qty.setText(String.valueOf(oldqty - 1));
                                            }
                                        } else {
                                            edit_qty.setText("1");
                                        }
                                    }
                                });
                                final ArrayList<ResponseCategoryEvp.Attribute_values> values = evpAttribute[i].getAttribute_values();
                                ChipCloudConfig selectableConfig = new ChipCloudConfig()
                                        .selectMode(ChipCloud.SelectMode.multi)
                                        .checkedChipColor(Color.parseColor("#ddaa00"))
                                        .checkedTextColor(Color.parseColor("#ffffff"))
                                        .uncheckedChipColor(Color.parseColor("#e0e0e0"))
                                        .uncheckedTextColor(Color.parseColor("#000000"))
                                        .useInsetPadding(true);

                                ChipCloud deleteableCloud = new ChipCloud(getActivity(), flex_available_sizes, selectableConfig);
                                selectedsize = new ArrayList<String>();
                                flex_available_sizes.setTag(evpAttribute[i].getAttribute_name());
                                deleteableCloud.setListener(new ChipListener() {
                                    @Override
                                    public void chipCheckedChange(int index, boolean checked, boolean userClick) {
                                        if (userClick) {
                                            if (checked) {
                                                selectedsize.add(values.get(index).getValue());
                                            } else {
                                                selectedsize.remove(values.get(index).getValue());
                                            }

                                        }
                                    }
                                });
                                for (ResponseCategoryEvp.Attribute_values s : evpAttribute[i].getAttribute_values()) {
                                    deleteableCloud.addChip(s.getValue());
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }

    public void showUploadDialog() {
        try {
            //initializing loader
            dialogCatalogUpload = new MaterialDialog.Builder(getContext()).title("Uploading ..").build();
            dialogCatalogUpload.setCancelable(false);
            dialogCatalogUpload.show();

            BeforeTime = System.currentTimeMillis();

            TotalTxBeforeTest = TrafficStats.getTotalTxBytes();


            handler = new Handler();

            runnable = new Runnable() {
                public void run() {
                    if(handler!=null) {
                        long AfterTime = System.currentTimeMillis();

                        long TotalRxAfterTest = TrafficStats.getTotalTxBytes();


                        double rxDiff = TotalRxAfterTest - TotalTxBeforeTest;
                        if ((rxDiff != 0)) {
                            double rxBPS = (rxDiff / 1024); // total rx bytes per second.

                            if (getContext() != null) {
                                Log.d("Internet speed", String.valueOf(rxBPS) + " KB/s. Total rx = ");
                            }
                            dialogCatalogUpload.setContent("Uploading speed : " + (int) rxBPS + " KB/s");
                            TotalTxBeforeTest = TotalRxAfterTest;
                        }

                        handler.postDelayed(this, 1000);
                    }
                }
            };

            handler.postDelayed(runnable, 1000);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void hideUploadDialog() {
        if (dialogCatalogUpload != null && dialogCatalogUpload.isShowing()) {
            dialogCatalogUpload.dismiss();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }

        if(bitmapImage!=null && !bitmapImage.isRecycled()){
            bitmapImage.recycle();
        }
    }
}
