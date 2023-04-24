package com.wishbook.catalog.home.catalog.add;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.flexbox.AlignContent;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.wishbook.catalog.Activity_AddCatalog;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.BuildConfig;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.autocomplete.CustomAutoCompleteTextView;
import com.wishbook.catalog.Utils.imagecropper.PhotoTakerUtils;
import com.wishbook.catalog.Utils.multipleimageselect.activities.AlbumSelectActivity;
import com.wishbook.catalog.Utils.multipleimageselect.models.Image;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.widget.SimpleTextWatcher;
import com.wishbook.catalog.commonadapters.AutoCompleteCatalogAdapter;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_Categories;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_brands;
import com.wishbook.catalog.commonmodels.CategoryTree;
import com.wishbook.catalog.commonmodels.ProductMyDetail;
import com.wishbook.catalog.commonmodels.ResponseHomeCategories;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.postpatchmodels.CatalogUploadOption;
import com.wishbook.catalog.commonmodels.responses.RequestEav;
import com.wishbook.catalog.commonmodels.responses.ResponseAddProduct;
import com.wishbook.catalog.commonmodels.responses.ResponseBrandDiscountExpand;
import com.wishbook.catalog.commonmodels.responses.ResponseCategoryEvp;
import com.wishbook.catalog.commonmodels.responses.ResponseProductUpload;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.commonmodels.responses.ScreenSetModel;
import com.wishbook.catalog.home.adapters.AddProductAdapter2;
import com.wishbook.catalog.home.catalog.CatalogHolder;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;
import com.wishbook.catalog.home.catalog.StartStopHandler;
import com.wishbook.catalog.home.catalog.adapter.AddScreenSetAdapter;
import com.wishbook.catalog.home.models.Add_Catalog_Response;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.models.Response_Brands;
import com.wishbook.catalog.home.more.Fragment_Mybrands;
import com.wishbook.catalog.home.more.adapters.treeview.bean.FileBean;
import com.wishbook.catalog.home.more.brandDiscount.ActivityBrandwiseDiscountList;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import fisk.chipcloud.ChipDeletedListener;

public class Fragment_AddCatalog_Version2 extends GATrackedFragment implements Activity_AddCatalog.OnBackPressedListener, AddProductAdapter2.ChangeProductListener {


    private static final String TAG = Fragment_AddCatalog_Version2.class.getSimpleName();
    @BindView(R.id.nested_step_one)
    LinearLayout nested_step_one;

    @BindView(R.id.nested_step_two)
    LinearLayout nested_step_two;

    @BindView(R.id.nested_step_three)
    LinearLayout nested_step_three;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.hint_brand)
    TextView hint_brand;

    @BindView(R.id.spinner_catalog_type)
    Spinner spinner_catalog_type;

    @BindView(R.id.spinner_category)
    Spinner spinner_category;

    @BindView(R.id.spinner_brand)
    Spinner spinner_brand;

    @BindView(R.id.auto_text_catalog)
    CustomAutoCompleteTextView auto_text_catalog;


    @BindView(R.id.btn_add_work_first)
    TextView btn_add_work_first;

    @BindView(R.id.btn_add_fabric_first)
    TextView btn_add_fabric_first;

    @BindView(R.id.btn_add_style_first)
    TextView btn_add_style_first;


    @BindView(R.id.work_fabric_section)
    LinearLayout work_fabric_section;


    @BindView(R.id.fabric_container)
    LinearLayout fabric_container;
    @BindView(R.id.flexbox_fabric)
    FlexboxLayout flexbox_fabric;

    @BindView(R.id.work_container)
    LinearLayout work_container;
    @BindView(R.id.flexbox_work)
    FlexboxLayout flexbox_work;

    @BindView(R.id.style_container)
    LinearLayout style_container;
    @BindView(R.id.flexbox_style)
    FlexboxLayout flexbox_style;


    @BindView(R.id.btn_continue)
    AppCompatButton btn_continue;


    @BindView(R.id.linear_eav)
    LinearLayout linear_eav;

    @BindView(R.id.linear_length_width_eav)
    LinearLayout linear_length_width_eav;


    @BindView(R.id.text_dispatch)
    TextView text_dispatch;

    @BindView(R.id.edit_dispatch_label)
    TextView edit_dispatch_label;

    @BindView(R.id.linear_dispatch_date)
    LinearLayout linear_dispatch_date;

    @BindView(R.id.edit_other_eav)
    EditText edit_other_eav;

    @BindView(R.id.edit_enable_duration)
    EditText edit_enable_duration;

    @BindView(R.id.rg_product_type)
    RadioGroup rg_product_type;

    @BindView(R.id.radio_noncatalog)
    RadioButton radio_noncatalog;

    @BindView(R.id.radio_catalog)
    RadioButton radio_catalog;

    @BindView(R.id.radio_setmatching)
    RadioButton radio_setmatching;

    @BindView(R.id.linear_enable_duration_container)
    LinearLayout linear_enable_duration_container;


    // ### StepTwo #### //
    @BindView(R.id.edit_product_selection)
    EditText edit_no_of_design;

    @BindView(R.id.check_same_price)
    CheckBox check_same_price;

    @BindView(R.id.img_cover)
    ImageView img_cover;

    @BindView(R.id.btn_upload_cover)
    TextView btn_upload_cover;

    @BindView(R.id.radio_full_catalog)
    RadioButton radio_full_catalog;

    @BindView(R.id.radio_single_piece_catalog)
    RadioButton radio_single_piece_catalog;

    @BindView(R.id.recycler_products)
    RecyclerView recycler_products;

    @BindView(R.id.btn_add_product)
    TextView btn_add_product;

    @BindView(R.id.radio_full_single)
    RadioGroup radio_full_single;

    @BindView(R.id.spinner_top_category)
    Spinner spinner_top_category;

    // ### Screen Type Catalog Step One ### //
    @BindView(R.id.linear_screen_step_one)
    LinearLayout linear_screen_step_one;


    @BindView(R.id.linear_catalog_brand)
    LinearLayout linear_catalog_brand;

    @BindView(R.id.txt_input_screen_quality_name)
    TextInputLayout txt_input_screen_quality_name;

    @BindView(R.id.edit_screen_quality_name)
    EditText edit_screen_quality_name;

    @BindView(R.id.linear_catalog_availability)
    LinearLayout linear_catalog_availability;

    @BindView(R.id.recyclerview_screen_set)
    RecyclerView recyclerview_screen_set;

    @BindView(R.id.btn_add_set)
    AppCompatButton btn_add_set;

    @BindView(R.id.edit_screen_price_pc)
    EditText edit_screen_price_pc;

    @BindView(R.id.edit_screen_pc_set)
    EditText edit_screen_pc_set;

    @BindView(R.id.text_inputlayout_catalog)
    TextInputLayout text_inputlayout_catalog;

    @BindView(R.id.text_inputlayout_photoshoot)
    TextInputLayout text_inputlayout_photoshoot;

    @BindView(R.id.spinner_photoshoot)
    Spinner spinner_photoshoot;


    @BindView(R.id.linear_length_width_eav_root_container)
    LinearLayout linear_length_width_eav_root_container;

    // ### Single Pc Same Price ### //


    @BindView(R.id.edit_common_add_margin)
    EditText edit_common_add_margin;

    @BindView(R.id.txt_common_margin_note1)
    TextView txt_common_margin_note1;

    @BindView(R.id.txt_common_margin_note2)
    TextView txt_common_margin_note2;

    @BindView(R.id.rg_common_price)
    RadioGroup rg_common_price;

    @BindView(R.id.radio_common_per)
    RadioButton radio_common_per;

    @BindView(R.id.radio_common_price)
    RadioButton radio_common_price;

    @BindView(R.id.txt_common_single_pc_price)
    TextView txt_common_single_pc_price;

    @BindView(R.id.linear_common_margin)
    LinearLayout linear_common_margin;

    @BindView(R.id.linear_same_price)
    LinearLayout linear_same_price;

    @BindView(R.id.catalog_minimum_note)
    TextView catalog_minimum_note;



    @BindView(R.id.linear_catalog_select_size_container)
    LinearLayout linear_catalog_select_size_container;

    @BindView(R.id.flex_catalog_select_size)
    FlexboxLayout flex_catalog_select_size;

    ArrayList<String> catalog_level_size_eav;


    View view;

    // #### Variable Initialize Start ###

    HashMap<String, String> catalogNameparams = new HashMap<>();
    ArrayList<String> selectedFabric = new ArrayList<>();
    ArrayList<String> selectedWork = new ArrayList<>();
    ArrayList<String> selectedStyle = new ArrayList<>();


    ArrayList<RadioGroup> enumRadioGroup = new ArrayList<>();
    ArrayList<FlexboxLayout> multiFlexLayout = new ArrayList<>();
    HashMap<String, ArrayList<String>> multiCheck = new HashMap<>();
    HashMap<String, String> eav_text = new HashMap<>();


    HashMap<String, Boolean> eavValidation = new HashMap<>();

    List<FileBean> mDatas = new ArrayList<>();
    List<FileBean> mScreenCategory = new ArrayList<>();
    List<FileBean> mTopCategory = new ArrayList<>();
    AutoCompleteCatalogAdapter autoCompleteCatalogAdapter;
    ArrayList<Response_catalogMini> response_catalogMinis;
    Response_catalogMini selectedCatalog;
    boolean isChangeCategory;
    final Calendar myCalendar = Calendar.getInstance();
    int add_step;
    ArrayList<Response_Brands> response_brandsArrayList;
    Response_Brands[] response_brands;
    ArrayList<ResponseCategoryEvp> eav_init;
    ArrayList<ResponseCategoryEvp> eav_with_edit_text;

    // ### Variable Step 2 Initialize  Start ####
    private Bitmap bitmapImage = null;
    private ArrayList<Image> product_image;
    private ArrayList<Image> product_image_clone;
    private AddProductAdapter2 addProductAdapter;
    private AddScreenSetAdapter addScreenSetAdapter;
    private boolean require_work = false, require_fabric = false, require_style = false;
    int textViewDefineCount = 0;
    int recyclerViewCount = 0;
    int counter = 0;
    int productCount = 1;
    public static String view_permission = "public";

    public String category_id = "1";


    // ## Variable Step 3 Initialize Start ####
    private ArrayList<ScreenSetModel> screenSetModels;

    private Add_Catalog_Response old_catalog_response;
    private CatalogUploadOption old_catalog_option;

    // ### Variable Upload Dialog ### ///
    MaterialDialog dialogCatalogUpload;
    Runnable runnablePostData;
    long BeforeTime, TotalTxBeforeTest;
    Handler handlerPostData;
    HashMap<Integer, Integer> screen_successcount;
    HashMap<Integer, Integer> product_successcount;

    boolean isSizeAvailable;
    boolean isSizeMandatory;
    ArrayList<ResponseCategoryEvp.Attribute_values> size_values;
    int photo_counter = 0;


    boolean isEditMode = false, isEditModeChangeCover = false;
    String edit_catalog_id;
    String edit_product_id;
    Response_catalog edit_catalog;
    ProductMyDetail productMyDetail;

    boolean isCallEndPointCalled;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.activity_add_catalog_step_1, ga_container, true);
        ButterKnife.bind(this, v);
        setupToolbar();
        ((Activity_AddCatalog) getActivity()).setOnBackPressedListener(this);
        getCategory("10");
        //getTopCategory();
        add_step = 1;
        updateUI(false);

        initView();
        initListener(v);
        initFabricWorkListener();
        initDispatchListener();

        bindProductRecyclerView();

        if (getArguments() != null && getArguments().getBoolean("isEditMode", false)
                && getArguments().getString("catalog_id") != null
                && getArguments().getString("product_id")!=null) {
            isEditMode = true;
            edit_catalog_id = getArguments().getString("catalog_id");
            edit_product_id = getArguments().getString("product_id");
            getCatalogDataBeforeEdit(getActivity(), edit_product_id);
            Log.d(TAG, "onCreateView: ======> Edit Catalog ID" + edit_catalog_id);
            toolbar.setTitle("Edit Products");
        }

        if (isEditMode) {
            btn_continue.setText("Continue");
        } else {
            btn_continue.setText("Save & Continue");
        }
        view = v;
        return v;
    }


    protected void setupToolbar() {
        toolbar.setBackgroundColor(getResources().getColor(R.color.color_primary));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setTitle(Application_Singleton.CONTAINER_TITLE);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Add Products");
        Drawable icon = getResources().getDrawable(R.drawable.ic_arrow_back_24dp);
        icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doBack();
            }
        });
    }

    private void initView() {
        check_same_price.setChecked(true);

        List<String> photoshoot_array = Arrays.asList(getResources().getStringArray(R.array.photo_shoot_type));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                getActivity(), R.layout.spinneritem, photoshoot_array
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        spinner_photoshoot.setAdapter(spinnerArrayAdapter);


    }

    public void setHintBrand() {
        //SPANNABLE STRING
        SpannableString ss = null;
        if (radio_catalog.isChecked()) {
            ss = new SpannableString(getString(R.string.hint_brands_1));
        } else {
            ss = new SpannableString(getString(R.string.hint_brands));
        }

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Application_Singleton.CONTAINER_TITLE = "Brands";
                Application_Singleton.CONTAINERFRAG = new Fragment_Mybrands();
                Intent intent = new Intent(getActivity(), OpenContainer.class);
                startActivityForResult(intent, 100);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };

        if (radio_catalog.isChecked()) {
            if (UserInfo.getInstance(getActivity()).getLanguage().equals("en")) {
                ss.setSpan(clickableSpan, 62, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                ss.setSpan(clickableSpan, 60, ss.length() - 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {
            if (UserInfo.getInstance(getActivity()).getLanguage().equals("en")) {
                try {
                    ss.setSpan(clickableSpan, getString(R.string.hint_brands).indexOf("register your brand on Wishbook."), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (Exception e) {
                    e.printStackTrace();
                    ss.setSpan(clickableSpan, ss.length() - 10, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

            } else {
                try {
                    ss.setSpan(clickableSpan, getString(R.string.hint_brands).indexOf("अपना ब्रांड रजिस्टर करें।"), ss.length() - 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (Exception e) {
                    e.printStackTrace();
                    ss.setSpan(clickableSpan, ss.length() - 10, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

            }
        }


        hint_brand.setText(ss);
        hint_brand.setMovementMethod(LinkMovementMethod.getInstance());
        hint_brand.setClickable(true);
    }


    private void initListener(View view) {
        response_catalogMinis = new ArrayList<>();
        auto_text_catalog.setThreshold(3);
        auto_text_catalog.setLoadingIndicator(
                (android.widget.ProgressBar) view.findViewById(R.id.progess_catalog));
        spinner_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                catalogNameparams.put("brand", spinner_brand.getSelectedItem() == null ? "" : ((Response_Brands) (spinner_brand.getSelectedItem())).getId());
                catalogNameparams.put("category", spinner_category.getSelectedItem() == null ? "" : String.valueOf(((FileBean) (spinner_category.getSelectedItem())).fileId));
                catalogNameparams.put("view_type", "public");
                autoCompleteCatalogAdapter = new AutoCompleteCatalogAdapter(getActivity(), R.layout.spinneritem, response_catalogMinis, catalogNameparams);
                auto_text_catalog.setAdapter(autoCompleteCatalogAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!isEditMode) {
                    catalogNameparams.put("brand", spinner_brand.getSelectedItem() == null ? "" : ((Response_Brands) (spinner_brand.getSelectedItem())).getId());
                    catalogNameparams.put("category", spinner_category.getSelectedItem() == null ? "" : String.valueOf(((FileBean) (spinner_category.getSelectedItem())).fileId));
                    autoCompleteCatalogAdapter = new AutoCompleteCatalogAdapter(getActivity(), R.layout.spinneritem, response_catalogMinis, catalogNameparams);
                    auto_text_catalog.setAdapter(autoCompleteCatalogAdapter);
                    isChangeCategory = true;
                    if (spinner_category.getSelectedItem() != null)
                        category_id = String.valueOf(((FileBean) (spinner_category.getSelectedItem())).fileId);
                    resetFabricWorkStyle();

                    getCategoryEavAttribute(String.valueOf(((FileBean) (spinner_category.getSelectedItem())).fileId));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


      /*  spinner_top_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String parent_selected_id = String.valueOf(((FileBean) (spinner_top_category.getSelectedItem())).fileId);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/


        auto_text_catalog.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                selectedCatalog = null;
                if (charSequence.length() > 50) {
                    auto_text_catalog.setError(getResources().getString(R.string.validate_name));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        auto_text_catalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!spinner_catalog_type.getSelectedItem().equals("Non-Catalog")) {
                    selectedCatalog = (Response_catalogMini) parent.getItemAtPosition(position);
                    CatalogBottomSheetDialogFragment myBottomSheet = CatalogBottomSheetDialogFragment.newInstance(selectedCatalog.getId());
                    myBottomSheet.show(getFragmentManager(), myBottomSheet.getTag());
                }
            }
        });


        spinner_catalog_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner_catalog_type.getSelectedItem().equals("Screens")) {

                } else {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rg_product_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (radio_setmatching.isChecked()) {
                    txt_input_screen_quality_name.setVisibility(View.VISIBLE);
                    text_inputlayout_photoshoot.setVisibility(View.VISIBLE);
                    spinner_photoshoot.setVisibility(View.VISIBLE);
                    spinner_photoshoot.setSelection(2);
                    linear_screen_step_one.setVisibility(View.VISIBLE);
                    linear_catalog_brand.setVisibility(View.VISIBLE);
                    linear_catalog_availability.setVisibility(View.GONE);
                    text_inputlayout_catalog.setVisibility(View.GONE);
                    auto_text_catalog.setVisibility(View.GONE);

                    setBrandAdapter(response_brands);
                    SpinAdapter_Categories spinAdapter_categories;
                    spinAdapter_categories = new SpinAdapter_Categories(getActivity(), R.layout.spinneritem, mScreenCategory);
                    spinner_category.setAdapter(spinAdapter_categories);
                    spinAdapter_categories.notifyDataSetChanged();
                    setHintBrand();
                } else {
                    txt_input_screen_quality_name.setVisibility(View.GONE);
                    linear_screen_step_one.setVisibility(View.GONE);
                    linear_catalog_brand.setVisibility(View.VISIBLE);
                    linear_catalog_availability.setVisibility(View.VISIBLE);
                    text_inputlayout_catalog.setVisibility(View.VISIBLE);
                    auto_text_catalog.setVisibility(View.VISIBLE);
                    setBrandAdapter(response_brands);
                    SpinAdapter_Categories spinAdapter_categories;

                    if (radio_noncatalog.isChecked()) {
                        text_inputlayout_photoshoot.setVisibility(View.VISIBLE);
                        spinner_photoshoot.setVisibility(View.VISIBLE);
                        spinner_photoshoot.setSelection(1);

                    } else {
                        text_inputlayout_photoshoot.setVisibility(View.GONE);
                        spinner_photoshoot.setVisibility(View.GONE);
                        spinner_photoshoot.setSelection(0);
                    }
                    if (addProductAdapter != null) {
                        addProductAdapter.isCatalog = radio_catalog.isChecked();
                    }
                    spinAdapter_categories = new SpinAdapter_Categories(getActivity(), R.layout.spinneritem, mDatas);
                    spinner_category.setAdapter(spinAdapter_categories);
                    spinAdapter_categories.notifyDataSetChanged();
                    setHintBrand();
                }
            }
        });

        if (getArguments() != null && getArguments().getString("catalog_type") != null) {
            String catalog_type = getArguments().getString("catalog_type");
            if (catalog_type.equals("catalog")) {
                radio_catalog.setChecked(true);
            } else if (catalog_type.equalsIgnoreCase("noncatalog")) {
                radio_noncatalog.setChecked(true);
            } else if (catalog_type.equalsIgnoreCase("screen")) {
                radio_setmatching.setChecked(true);
            }
        } else {
            radio_catalog.setChecked(true);
        }


        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (add_step == 2) {
                    if (radio_setmatching.isChecked()) {
                        if (validateScreen()) {
                            postDataNonCatalogPhase2();
                        }
                    } else {
                        boolean isWithFile = false;
                        if (isEditMode && isEditModeChangeCover) {
                            isWithFile = true;
                            if (!validateCoverImage()) {
                                return;
                            }
                        } else if (!isEditMode) {
                            isWithFile = true;
                            if (!validateCoverImage()) {
                                return;
                            }
                        }
                        if (!validationProductsRecyclerView()) {
                            return;
                        }

                        postDataCatalogWithEditSupport(isWithFile);
                        btn_continue.setEnabled(false);
                    }
                } else {
                    if (radio_setmatching.isChecked()) {
                        // Not Validate
                        if (showStepOneScreenValidationError() && validateEav()) {
                            checkScreenNameValidate(edit_screen_quality_name.getText().toString());
                        }
                    } else {
                        if (edit_enable_duration.getText().toString().trim().isEmpty()) {
                            edit_enable_duration.setError("Duration can't be empty");
                            return;
                        } else {
                            int days = Integer.parseInt(edit_enable_duration.getText().toString().trim());
                            if (days < 10) {
                                edit_enable_duration.setError("Minimum enable duration should be 10");
                                return;
                            }

                            if (days > 90) {
                                edit_enable_duration.setError("Maximum enable duration should be 90");
                                return;
                            }
                        }

                        if (require_fabric) {
                            if (!validateFabricWork()) {
                                return;
                            }
                        }

                        if (require_work) {
                            if (!validateFabricWork()) {
                                return;
                            }
                        }

                        if (isEditMode) {
                            if (!auto_text_catalog.getText().toString().equals("")
                                    && spinner_brand != null && spinner_brand.getChildCount() > 0
                                    && validateEav()) {
                                getBrandsDiscountRule(((Response_Brands) (spinner_brand.getSelectedItem())).getId());
                            } else {
                                showStepOneValidationError();
                            }
                        } else {
                            if (!auto_text_catalog.getText().toString().equals("")
                                    && spinner_brand != null && spinner_brand.getChildCount() > 0
                                    && spinner_category != null && spinner_category.getChildCount() > 0
                                    && !edit_enable_duration.getText().toString().isEmpty()
                                    && validateEav()) {
                                if (radio_catalog.isChecked() && !isEditMode) {
                                    checkCatalogNameValidate(auto_text_catalog.getText().toString(), true);
                                } else {
                                    add_step = add_step + 1;
                                    setStepTwo();
                                    btn_continue.setText("Submit");
                                }
                            } else {
                                showStepOneValidationError();
                            }
                        }

                    }
                }
            }
        });


        btn_upload_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhotoUploadWidget();
            }
        });
        img_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPhotoUploadWidget();
            }
        });
    }

    private void updateWorkFarbicSection() {

        if (require_work) {
            work_container.setVisibility(View.VISIBLE);
        } else {
            work_container.setVisibility(View.GONE);
        }

        if (require_fabric) {
            fabric_container.setVisibility(View.VISIBLE);
        } else {
            fabric_container.setVisibility(View.GONE);
        }

        if (require_style) {
            style_container.setVisibility(View.VISIBLE);
        } else {
            style_container.setVisibility(View.GONE);
        }

        if (!require_work && !require_fabric && !require_style) {
            work_fabric_section.setVisibility(View.GONE);
        } else {
            work_fabric_section.setVisibility(View.VISIBLE);
        }


    }


    private void initFabricWorkListener() {
        btn_add_fabric_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideKeyboard(getActivity());
                clearFocus();
                startActivityForResult(new Intent(getActivity(), ActivityFabricSelect.class).putExtra("selectedfabric", selectedFabric).putExtra("type", "fabric").putExtra("category_id", category_id), Application_Singleton.FABRIC_SEARCH_REQUEST_CODE);
            }
        });


        btn_add_work_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideKeyboard(getActivity());
                clearFocus();
                startActivityForResult(new Intent(getActivity(), ActivityFabricSelect.class).putExtra("selectedwork", selectedWork).putExtra("type", "work").putExtra("category_id", category_id), Application_Singleton.WORK_SEARCH_REQUEST_CODE);
            }
        });


        // Style Listener Start
        btn_add_style_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideKeyboard(getActivity());
                clearFocus();
                startActivityForResult(new Intent(getActivity(), ActivityFabricSelect.class).putExtra("selectedstyle", selectedStyle).putExtra("type", "style").putExtra("category_id", category_id), Application_Singleton.STYLE_SEARCH_REQUEST_CODE);
            }
        });
    }


    public void showStepOneValidationError() {
        if (auto_text_catalog.getText().toString().isEmpty()) {
            auto_text_catalog.setError("Product name cannot be empty");
            auto_text_catalog.requestFocus();
            return;
        }

        if (spinner_brand.getChildCount() < 1) {
            Toast.makeText(getActivity(), "Please select the brand", Toast.LENGTH_LONG).show();
            return;
        }

        if (spinner_category.getChildCount() < 1) {
            Toast.makeText(getActivity(), "Please select the category", Toast.LENGTH_LONG).show();
            return;
        }

        if (!validateFabricWork()) {
            return;
        }


        // Check eav data of radio
        for (int i = 0; i < enumRadioGroup.size(); i++) {
            try {
                if (enumRadioGroup.get(i).getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getActivity(), "Please select any " + enumRadioGroup.get(i).getTag(), Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        for (int i = 0; i < multiFlexLayout.size(); i++) {
            try {
                if (multiCheck.get(multiFlexLayout.get(i).getTag()).size() == 0) {
                    Toast.makeText(getActivity(), "Please select any " + multiFlexLayout.get(i).getTag(), Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
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
    }

    public boolean validateFabricWork() {
        if (selectedFabric != null && require_fabric) {
            if (selectedFabric.size() == 0) {
                Toast.makeText(getActivity(), "Please Select any fabric", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            if (require_fabric) {
                Toast.makeText(getActivity(), "Please Select any fabric", Toast.LENGTH_SHORT).show();
                return false;
            }

        }


        if (selectedWork != null && require_work) {
            if (selectedWork.size() == 0) {
                Toast.makeText(getActivity(), "Please Select any work", Toast.LENGTH_SHORT).show();
                return false;
            }

        } else {
            if (require_work) {
                Toast.makeText(getActivity(), "Please Select any work", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public boolean validateCoverImage() {
        File mDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/wishbooks/");
        final File output = new File(mDirectory, "catalogpreview.jpg");

        if (bitmapImage != null && output.exists()) {
            return true;
        } else {
            if (bitmapImage == null) {
                new MaterialDialog.Builder(getActivity())
                        .content("Cover Image can't be empty")
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            } else {
                Toast.makeText(getActivity(), "Cover Image not Found", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }

    public boolean showStepOneScreenValidationError() {


        if (edit_screen_quality_name.getText().toString().isEmpty()) {
            edit_screen_quality_name.setError("Please enter quality name");
            edit_screen_quality_name.requestFocus();
            return false;
        }

        if (spinner_brand.getChildCount() < 1) {
            Toast.makeText(getActivity(), "Please select the brand", Toast.LENGTH_LONG).show();
            return false;
        }

        if (spinner_category.getChildCount() < 1) {
            Toast.makeText(getActivity(), "Please select the category", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!validateFabricWork()) {
            return false;
        }

        // Check eav data of radio
        for (int i = 0; i < enumRadioGroup.size(); i++) {
            try {
                if (enumRadioGroup.get(i).getCheckedRadioButtonId() == -1 && eavValidation.get(multiFlexLayout.get(i).getTag())) {
                    Toast.makeText(getActivity(), "Please select any " + enumRadioGroup.get(i).getTag(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < multiFlexLayout.size(); i++) {
            try {
                if (multiCheck.get(multiFlexLayout.get(i).getTag()).size() == 0 && eavValidation.get(multiFlexLayout.get(i).getTag())) {
                    Toast.makeText(getActivity(), "Please select any " + multiFlexLayout.get(i).getTag(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (edit_screen_pc_set.getText().toString().isEmpty()) {
            edit_screen_pc_set.requestFocus();
            edit_screen_pc_set.setError("Please enter No.of Pcs./Set");
            return false;
        } else {
            if (Integer.parseInt(edit_screen_pc_set.getText().toString()) == 0) {
                edit_screen_pc_set.requestFocus();
                edit_screen_pc_set.setError("Please enter valid No.of Pcs./Set");
                return false;
            }

            String no_of_size_selected = null;
            if (multiFlexLayout != null && multiFlexLayout.size() > 0) {
                for (int i = 0; i < multiFlexLayout.size(); i++) {
                    try {
                        if (multiCheck.containsKey("size")) {
                            no_of_size_selected = String.valueOf(multiCheck.get("size").size());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (no_of_size_selected != null) {
                if (Integer.parseInt(edit_screen_pc_set.getText().toString()) != Integer.parseInt(no_of_size_selected)) {
                    edit_screen_pc_set.requestFocus();
                    edit_screen_pc_set.setError(String.format(getResources().getString(R.string.error_no_of_size_set), edit_screen_pc_set.getText().toString(), no_of_size_selected));
                    return false;
                }
            }

        }

        if (edit_screen_price_pc.getText().toString().isEmpty()) {
            edit_screen_price_pc.requestFocus();
            edit_screen_price_pc.setError("Please enter Price/Pc.");
            return false;
        }


        if (Double.parseDouble(edit_screen_price_pc.getText().toString()) <= 70) {
            edit_screen_price_pc.requestFocus();
            edit_screen_price_pc.setError("Price should be greater than 70");
            return false;
        }

        if (Double.parseDouble(edit_screen_price_pc.getText().toString()) > 50000) {
            edit_screen_price_pc.requestFocus();
            edit_screen_price_pc.setError("Price must be less than or equal to 50K");
            return false;
        }
        return true;
    }

    private boolean validateEav() {
        if (enumRadioGroup != null && enumRadioGroup.size() > 0) {
            for (int i = 0; i < enumRadioGroup.size(); i++) {
                try {
                    if (enumRadioGroup.get(i).getCheckedRadioButtonId() == -1 && eavValidation.get(enumRadioGroup.get(i).getTag())) {
                        Toast.makeText(getActivity(), "Please select any " + StaticFunctions.formatErrorTitle(enumRadioGroup.get(i).getTag().toString()), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        if (multiFlexLayout != null && multiFlexLayout.size() > 0) {
            for (int i = 0; i < multiFlexLayout.size(); i++) {
                try {
                    if (multiCheck.get(multiFlexLayout.get(i).getTag()) == null && eavValidation.get(multiFlexLayout.get(i).getTag())) {
                        Toast.makeText(getActivity(), "Please select any " + multiFlexLayout.get(i).getTag(), Toast.LENGTH_SHORT).show();
                        return false;
                    } else {
                        Log.e("TAG", "validateEav: ====>" + multiCheck.get(multiFlexLayout.get(i).getTag()).size());
                        if (multiCheck.get(multiFlexLayout.get(i).getTag()).size() == 0 && eavValidation.get(multiFlexLayout.get(i).getTag())) {
                            Toast.makeText(getActivity(), "Please select any " + multiFlexLayout.get(i).getTag(), Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (eav_text != null && eav_text.size() > 0) {
            for (Map.Entry<String, String> entry : eav_text.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (eav_text.get(key).isEmpty() && eavValidation.get(key)) {
                    Toast.makeText(getActivity(), "Please enter " + StaticFunctions.formatErrorTitle(key), Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    if (!eav_text.get(key).isEmpty()) {
                        ResponseCategoryEvp eav = findEavFromSlug(key);
                        if(eav!=null) {
                            if(eav.getMin_value()!=null) {
                                if (Double.parseDouble(eav_text.get(key)) < Double.parseDouble(eav.getMin_value())) {
                                    String error_msg = StaticFunctions.formatErrorTitle(eav.getAttribute_name()) +" can't be less than"+" "+eav.getMin_value()+" "+eav.getUnit();
                                    Toast.makeText(getActivity(), error_msg, Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                            }

                            if(eav.getMax_value()!=null) {
                                if (Double.parseDouble(eav_text.get(key)) > Double.parseDouble(eav.getMax_value())) {
                                    String error_msg = StaticFunctions.formatErrorTitle(eav.getAttribute_name()) +" can't be greater than"+" "+eav.getMax_value()+" "+eav.getUnit();
                                    Toast.makeText(getActivity(), error_msg, Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                            }

                        }

                    }

                }
            }
        }

        return true;
    }

    public ResponseCategoryEvp findEavFromSlug(String slug_name) {
        for (ResponseCategoryEvp evp:
             eav_with_edit_text) {
            if(evp.getAttribute_slug().equalsIgnoreCase(slug_name)) {
                return evp;
            }
        }
        return null;
    }

    private void getCategory(String parentid) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "category", "") + "?parent=" + parentid, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                CategoryTree[] ct = Application_Singleton.gson.fromJson(response, CategoryTree[].class);
                if (isAdded() && !isDetached()) {
                    if (ct != null) {
                        if (ct.length > 0) {
                            mDatas = new ArrayList<>();
                            mScreenCategory = new ArrayList<>();
                            if (ct[0].getId() != null) {
                                for (int i = 0; i < ct.length; i++) {
                                    CategoryTree ctitem = ct[i];
                                    FileBean fileBean, fileBeanForScreen;
                                    if (ctitem.getparent_category() == null) {
                                        fileBean = new FileBean(ctitem.getId(), -1, ctitem.getcategory_name());
                                        fileBeanForScreen = new FileBean(ctitem.getId(), -1, ctitem.getcategory_name());
                                        mDatas.add(fileBean);
                                        mScreenCategory.add(fileBeanForScreen);
                                    } else {
                                        fileBean = new FileBean(ctitem.getId(), ctitem.getparent_category(), ctitem.getcategory_name());
                                        fileBeanForScreen = new FileBean(ctitem.getId(), ctitem.getparent_category(), ctitem.getcategory_name());
                                        mDatas.add(fileBean);
                                        mScreenCategory.add(fileBeanForScreen);
                                    }
                                }
                            }
                        } else {
                            mDatas = new ArrayList<>();
                            mScreenCategory = new ArrayList<>();
                            category_id = "1";
                        }
                    }
                    getCategoryEavAttribute("");
                    getBrands();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    private ArrayList<FileBean> setColorSizeSet(ArrayList<FileBean> fileBeans, ArrayList<ResponseCategoryEvp> responseCategoryEvps) {
        if (fileBeans != null && responseCategoryEvps != null) {
            for (int i = 0; i < fileBeans.size(); i++) {
                String set_type = " (color set)";
                for (int j = 0; j < responseCategoryEvps.size(); j++) {
                    if (responseCategoryEvps.get(j).getAttribute_slug().equalsIgnoreCase("size")
                            && responseCategoryEvps.get(j).getCategory().equalsIgnoreCase(String.valueOf(fileBeans.get(i).getFileId()))) {
                        set_type = " (size set)";
                    }
                }
                FileBean temp = fileBeans.get(i);
                temp.setFileName(temp.getFileName() + set_type);
                fileBeans.set(i, temp);
            }
        }

        return fileBeans;
    }

    /**
     * API Call get User Brand's Brand I Own and Brand I Sell and
     * set DropDown Adapter
     */
    private void getBrands() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "brands", "") + "?type=my", null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    response_brands = Application_Singleton.gson.fromJson(response, Response_Brands[].class);
                    setBrandAdapter(response_brands);
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    private void setBrandAdapter(Response_Brands[] response_brands) {
        response_brandsArrayList = new ArrayList<>();
        if (radio_setmatching.isChecked() || radio_noncatalog.isChecked()) {
            Response_Brands no_brand = new Response_Brands("-1", "No brand");
            response_brandsArrayList.add(no_brand);
        }

        if (response_brands != null)
            Collections.addAll(response_brandsArrayList, response_brands);

        SpinAdapter_brands spinAdapter_brands = new SpinAdapter_brands(getActivity(), R.layout.spinneritem, response_brandsArrayList.toArray(new Response_Brands[response_brandsArrayList.size()]));
        spinner_brand.setAdapter(spinAdapter_brands);

        if (edit_catalog != null) {
            for (int i = 0; i < response_brandsArrayList.size(); i++) {
                if (edit_catalog.getBrand() != null && response_brandsArrayList.get(i).getId().equalsIgnoreCase(edit_catalog.getBrand().getId())) {
                    spinner_brand.setSelection(i);
                    break;
                }
            }
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
                                    getBrandsDiscountRule(((Response_Brands) (spinner_brand.getSelectedItem())).getId());
                                }
                            }

                        } else {
                            if (isMoveNext) {
                                getBrandsDiscountRule(((Response_Brands) (spinner_brand.getSelectedItem())).getId());
                            }

                        }
                    }
                } catch (JsonSyntaxException e) {
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


    public void checkScreenNameValidate(final String catalogname) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) getContext());
        HashMap<String, String> params = new HashMap<>();
        params.put("title", catalogname);
        HttpManager.getInstance((Activity) getContext()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getContext(), "dropdownvalidate", ""), params, headers, false, new HttpManager.customCallBack() {
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
                                edit_screen_quality_name.setError("Quality name already exist");
                                edit_screen_quality_name.requestFocus();
                                Toast.makeText(getActivity(), "Quality name already exist", Toast.LENGTH_LONG).show();
                            } else {
                                add_step = add_step + 1;
                                setStepTwo();
                                btn_continue.setText("Upload");
                            }

                        } else {
                            add_step = add_step + 1;
                            setStepTwo();
                            btn_continue.setText("Upload");
                        }
                    }
                } catch (JsonSyntaxException e) {
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
        linear_dispatch_date.setOnClickListener(new View.OnClickListener() {

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

    }


    private void updateDispatch() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        text_dispatch.setText(sdf.format(myCalendar.getTime()));
        edit_dispatch_label.setVisibility(View.VISIBLE);
    }

    public void getCategoryEavAttribute(final String categoryId) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.CATEGORY_EVP_V2 + "?category=" + categoryId, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                if (isAdded() && !isDetached()) {
                    if (categoryId.equalsIgnoreCase("")) {
                        final ResponseCategoryEvp[] evpAttribute = Application_Singleton.gson.fromJson(response, ResponseCategoryEvp[].class);
                        eav_with_edit_text = new ArrayList<>();
                        eav_init = new ArrayList<ResponseCategoryEvp>(Arrays.asList(evpAttribute));
                        setColorSizeSet((ArrayList<FileBean>) mScreenCategory, eav_init);
                        if (isCatalogTypeScreen()) {
                            Log.e("TAG", "onItemSelected: Spinner Catalog Set CategoryEav Fetch");
                            SpinAdapter_Categories spinAdapter_categories = new SpinAdapter_Categories(getActivity(), R.layout.spinneritem, mScreenCategory);
                            spinner_category.setAdapter(spinAdapter_categories);
                        } else {
                            SpinAdapter_Categories spinAdapter_categories = new SpinAdapter_Categories(getActivity(), R.layout.spinneritem, mDatas);
                            spinner_category.setAdapter(spinAdapter_categories);
                        }
                    } else {
                        if (isAdded() && !isDetached()) {
                            require_fabric = false;
                            require_work = false;
                            require_style = false;
                            linear_eav.removeAllViews();
                            linear_length_width_eav.removeAllViews();
                            enumRadioGroup.clear();
                            multiFlexLayout.clear();
                            multiCheck.clear();
                            eav_text.clear();
                            eav_with_edit_text = new ArrayList<>();

                            // linear_custom_text.removeAllViews();
                            linear_length_width_eav_root_container.setVisibility(View.GONE);

                            final ResponseCategoryEvp[] evpAttribute = Application_Singleton.gson.fromJson(response, ResponseCategoryEvp[].class);
                            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(0, StaticFunctions.dpToPx(getActivity(), 16), 0, 0);

                            isSizeAvailable = false;
                            if (addProductAdapter != null) {
                                addProductAdapter.setSizes(null);
                                addProductAdapter.notifyDataSetChanged();
                            }

                            if (evpAttribute.length > 0) {
                                for (int i = 0; i < evpAttribute.length; i++) {


                                    if (evpAttribute[i].getAttribute_slug() != null) {

                                        eavValidation.put(evpAttribute[i].getAttribute_slug(), evpAttribute[i].getIs_required());

                                        if (evpAttribute[i].getAttribute_slug().equalsIgnoreCase("fabric")) {
                                            if (edit_catalog != null) {
                                                String temp_edit_value = editCatalogEavValue(edit_catalog, "fabric");
                                                setFabric(StaticFunctions.stringToArray(temp_edit_value, StaticFunctions.COMMASEPRATEDSPACE));
                                            }

                                            require_fabric = true;
                                        }
                                        if (evpAttribute[i].getAttribute_slug().equalsIgnoreCase("work")) {
                                            require_work = true;
                                            if (edit_catalog != null) {
                                                String temp_edit_value = editCatalogEavValue(edit_catalog, "work");
                                                setWork(StaticFunctions.stringToArray(temp_edit_value, StaticFunctions.COMMASEPRATEDSPACE));
                                            }
                                        }
                                        if (evpAttribute[i].getAttribute_slug().equalsIgnoreCase("style")) {
                                            require_style = true;
                                            if (edit_catalog != null) {
                                                String temp_edit_value = editCatalogEavValue(edit_catalog, "style");
                                                setStyle(StaticFunctions.stringToArray(temp_edit_value, StaticFunctions.COMMASEPRATEDSPACE));
                                            }
                                        }

                                    }

                                    if (evpAttribute[i].getAttribute_slug().equalsIgnoreCase("fabric")
                                            || evpAttribute[i].getAttribute_slug().equalsIgnoreCase("work")
                                            || evpAttribute[i].getAttribute_slug().equalsIgnoreCase("style")
                                            || evpAttribute[i].getAttribute_slug().equalsIgnoreCase("gender")) {
                                        continue;
                                    }

                                    if (evpAttribute[i].getAttribute_datatype().equalsIgnoreCase("enum")) {
                                        RadioGroup radioGroup = new RadioGroup(getActivity());
                                        radioGroup.setTag(evpAttribute[i].getAttribute_slug());
                                        String temp_edit_value = null;
                                        if (edit_catalog != null) {
                                            temp_edit_value= editCatalogEavValue(edit_catalog, StaticFunctions.formatErrorTitle(evpAttribute[i].getAttribute_name()));
                                        }
                                        enumRadioGroup.add(radioGroup);
                                        for (int j = 0; j < evpAttribute[i].getAttribute_values().size(); j++) {
                                            RadioButton radioButtonView = new RadioButton(getActivity());
                                            radioButtonView.setPadding(20, 0, 0, 0);
                                            radioButtonView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            radioButtonView.setId((i * 100) + j);
                                            radioButtonView.setText(evpAttribute[i].getAttribute_values().get(j).getValue());
                                            radioGroup.setLayoutParams(lp);
                                            radioGroup.addView(radioButtonView);

                                            try {
                                                if (temp_edit_value != null && temp_edit_value.equalsIgnoreCase(evpAttribute[i].getAttribute_values().get(j).getValue())) {
                                                    radioButtonView.setChecked(true);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        TextView textView = new TextView(getActivity());
                                        textView.setText(StaticFunctions.formatErrorTitle(evpAttribute[i].getAttribute_name()));
                                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                        textView.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
                                        linear_eav.addView(textView);
                                        linear_eav.addView(radioGroup);


                                    }
                                    if (evpAttribute[i].getAttribute_datatype().equalsIgnoreCase("multi")) {
                                        if (!isCatalogTypeScreen() && evpAttribute[i].getAttribute_slug().equalsIgnoreCase("size")) {
                                            isSizeAvailable = true;
                                            size_values = evpAttribute[i].getAttribute_values();
                                            if (addProductAdapter != null) {
                                                addProductAdapter.setSizes(size_values);
                                                addProductAdapter.notifyDataSetChanged();
                                            }

                                            continue;
                                        }

                                        final FlexboxLayout multi_layout = new FlexboxLayout(getActivity());
                                        ViewGroup.LayoutParams lp1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        multi_layout.setLayoutParams(lp1);
                                        multi_layout.setPadding(StaticFunctions.dpToPx(getActivity(), 16), 0, 0, 0);
                                        multi_layout.setFlexWrap(FlexWrap.WRAP);
                                        multi_layout.setAlignContent(AlignContent.SPACE_AROUND);
                                        multi_layout.setTag(evpAttribute[i].getAttribute_slug());


                                        multiFlexLayout.add(multi_layout);
                                        ArrayList<String> temp_edit_value = null;
                                        if (edit_catalog != null) {
                                            temp_edit_value = StaticFunctions.stringToArray(editCatalogEavValue(edit_catalog, StaticFunctions.formatErrorTitle(evpAttribute[i].getAttribute_name())), StaticFunctions.COMMASEPRATEDSPACE);
                                        }
                                        for (int j = 0; j < evpAttribute[i].getAttribute_values().size(); j++) {
                                            final CheckBox checkBox = new CheckBox(getActivity());
                                            checkBox.setId(j);
                                            FlexboxLayout.LayoutParams check_lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
                                            check_lp.setMargins(0, 0, 32, 12);
                                            setCheckBoxColor(checkBox, R.color.color_primary, R.color.purchase_medium_gray);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                                checkBox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                                            }
                                            checkBox.setText(evpAttribute[i].getAttribute_values().get(j).getValue());
                                            checkBox.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
                                            checkBox.setPadding(0, 0, 20, 0);
                                            multi_layout.addView(checkBox, check_lp);


                                            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                                    if (isChecked) {
                                                        if (multiCheck.containsKey(multi_layout.getTag())) {
                                                            ArrayList<String> checked = multiCheck.get(multi_layout.getTag());
                                                            if (!checked.contains(checkBox.getText().toString())) {
                                                                checked.add(checkBox.getText().toString());
                                                            }
                                                            multiCheck.put(multi_layout.getTag().toString(), checked);

                                                        } else {
                                                            ArrayList<String> checked = new ArrayList<>();
                                                            checked.add(checkBox.getText().toString());
                                                            multiCheck.put(multi_layout.getTag().toString(), checked);
                                                        }
                                                    } else {
                                                        if (multiCheck.containsKey(multi_layout.getTag())) {
                                                            ArrayList<String> checked = multiCheck.get(multi_layout.getTag());
                                                            if (checked.contains(checkBox.getText().toString())) {
                                                                checked.remove(checkBox.getText().toString());
                                                            }
                                                            multiCheck.put(multi_layout.getTag().toString(), checked);

                                                        }
                                                    }
                                                }
                                            });

                                            checkBox.setLayoutParams(check_lp);

                                            try {
                                                if (temp_edit_value != null && temp_edit_value.contains(evpAttribute[i].getAttribute_values().get(j).getValue())) {
                                                    checkBox.setChecked(true);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        TextView textView = new TextView(getActivity());
                                        textView.setText(StaticFunctions.formatErrorTitle(evpAttribute[i].getAttribute_name()));
                                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                        textView.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
                                        linear_eav.addView(textView);
                                        linear_eav.addView(multi_layout);
                                    }

                                    if (evpAttribute[i].getAttribute_datatype().equalsIgnoreCase("text")
                                            || evpAttribute[i].getAttribute_datatype().equalsIgnoreCase("int")
                                            || evpAttribute[i].getAttribute_datatype().equalsIgnoreCase("float")) {

                                        eav_with_edit_text.add(evpAttribute[i]);
                                        String temp_edit_value = null;
                                        if (edit_catalog != null) {
                                            temp_edit_value = editCatalogEavValue(edit_catalog, StaticFunctions.formatErrorTitle(evpAttribute[i].getAttribute_name()));
                                            if(temp_edit_value!=null && evpAttribute[i].getUnit()!=null)
                                                temp_edit_value = temp_edit_value.replace(evpAttribute[i].getUnit(), "");
                                        }

                                        /*if (evpAttribute[i].getAttribute_slug().equalsIgnoreCase("top_length")) {

                                            eav_text.put(evpAttribute[i].getAttribute_slug(), "");
                                            linear_top_eav.setVisibility(View.VISIBLE);
                                            txt_top_unit.setText(evpAttribute[i].getAttribute_type());
                                            final int finalI1 = i;
                                            edit_top.addTextChangedListener(new SimpleTextWatcher() {
                                                final int finalI = finalI1;

                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    eav_text.put(evpAttribute[finalI].getAttribute_slug(), edit_top.getText().toString());
                                                }
                                            });
                                            if (temp_edit_value != null)
                                                edit_top.setText(temp_edit_value);

                                        } else if (evpAttribute[i].getAttribute_slug().equalsIgnoreCase("bottom_length")) {
                                            final int finalI = i;
                                            eav_text.put(evpAttribute[i].getAttribute_slug(), "");
                                            txt_bottom_unit.setText(evpAttribute[i].getUnit());
                                            linear_bottom_eav.setVisibility(View.VISIBLE);
                                            edit_bottom.addTextChangedListener(new SimpleTextWatcher() {
                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    eav_text.put(evpAttribute[finalI].getAttribute_slug(), edit_bottom.getText().toString());
                                                }
                                            });
                                            if (temp_edit_value != null)
                                                edit_bottom.setText(temp_edit_value);
                                        } else if (evpAttribute[i].getAttribute_slug().equalsIgnoreCase("dupatta_length")) {
                                            final int finalI = i;
                                            eav_text.put(evpAttribute[i].getAttribute_slug(), "");
                                            linear_dupatta_eav.setVisibility(View.VISIBLE);
                                            txt_dupatta_unit.setText(evpAttribute[i].getUnit());
                                            edit_dupatta.addTextChangedListener(new SimpleTextWatcher() {
                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    eav_text.put(evpAttribute[finalI].getAttribute_slug(), edit_dupatta.getText().toString());
                                                }
                                            });
                                            if (temp_edit_value != null)
                                                edit_dupatta.setText(temp_edit_value);
                                        } else if (evpAttribute[i].getAttribute_slug().equalsIgnoreCase("dupatta_width")) {
                                            final int finalI = i;
                                            eav_text.put(evpAttribute[i].getAttribute_slug(), "");
                                            linear_dupatta_width_eav.setVisibility(View.VISIBLE);
                                            txt_dupatta_width_label.setText(evpAttribute[i].getAttribute_name());
                                            txt_dupatta_width_unit.setText(evpAttribute[i].getUnit());
                                            edit_dupatta_width.addTextChangedListener(new SimpleTextWatcher() {
                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    eav_text.put(evpAttribute[finalI].getAttribute_slug(), edit_dupatta_width.getText().toString());
                                                }
                                            });
                                            if (temp_edit_value != null)
                                                edit_dupatta_width.setText(temp_edit_value);
                                        } */


                                        eav_text.put(evpAttribute[i].getAttribute_slug(), "");
                                        LayoutInflater la = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View v = la.inflate(R.layout.eav_text_item, null);
                                        TextView txt_eav_text_title = v.findViewById(R.id.txt_eav_text_title);
                                        final EditText editText = v.findViewById(R.id.edit_eav_text_value);
                                        if (evpAttribute[i].getAttribute_datatype().equalsIgnoreCase("int")) {
                                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                        }
                                        if (evpAttribute[i].getAttribute_datatype().equalsIgnoreCase("float")) {
                                            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                        }
                                        TextView txt_eav_unit = v.findViewById(R.id.txt_eav_text_type);
                                        txt_eav_text_title.setText(StaticFunctions.formatErrorTitle(evpAttribute[i].getAttribute_name()));
                                        if (evpAttribute[i].getUnit() != null && !evpAttribute[i].getUnit().isEmpty()) {
                                            txt_eav_unit.setText(evpAttribute[i].getUnit());
                                        } else {
                                            txt_eav_unit.setText("");
                                        }


                                        final int finalI = i;
                                        editText.addTextChangedListener(new SimpleTextWatcher() {
                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                eav_text.put(evpAttribute[finalI].getAttribute_slug(), editText.getText().toString());
                                            }
                                        });

                                        if (temp_edit_value != null)
                                            editText.setText(temp_edit_value);

                                        if(evpAttribute[i].getAttribute_slug().contains("length") || evpAttribute[i].getAttribute_slug().contains("width")) {
                                            linear_length_width_eav_root_container.setVisibility(View.VISIBLE);
                                            linear_length_width_eav.addView(v);
                                        } else {
                                            linear_eav.addView(v);
                                        }

                                    }
                                }
                            }

                            updateWorkFarbicSection();
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
                    img_cover.setImageBitmap(bitmap);
                    if (bitmap != null) {
                        String decoded = scanQRImage(bitmap);
                        if (decoded != null) {
                            Toast.makeText(getActivity(), decoded, Toast.LENGTH_LONG).show();
                        }
                    }

                    bitmapImage = bitmap;
                    if (isEditMode) {
                        isEditModeChangeCover = true;
                    }

                    File mDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/wishbooks/");
                    File output = new File(mDirectory, "catalogpreview.jpg");
                    Log.v("selected image", output.getAbsolutePath());
                    if (output.exists()) {
                        Log.v("selected image exits", output.getAbsolutePath());
                    }
                    btn_upload_cover.setText("Change Cover Photo");
                }
            });
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TAG", "onActivityResult: Add Catalog request code" + requestCode + "\n Result code" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Application_Singleton.FABRIC_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            auto_text_catalog.clearFocus();
            edit_enable_duration.clearFocus();
            KeyboardUtils.hideKeyboard(getActivity());

            setFabric(data.getStringArrayListExtra("fabric"));
        } else if (requestCode == Application_Singleton.WORK_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            auto_text_catalog.clearFocus();
            edit_enable_duration.clearFocus();
            KeyboardUtils.hideKeyboard(getActivity());
            setWork(data.getStringArrayListExtra("work"));
        } else if (requestCode == Application_Singleton.STYLE_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            auto_text_catalog.clearFocus();
            edit_enable_duration.clearFocus();
            KeyboardUtils.hideKeyboard(getActivity());

            setStyle(data.getStringArrayListExtra("style"));
        } else if (requestCode == 100) {
            getBrands();
        } else if (requestCode == 500 && resultCode == Activity.RESULT_OK) {

        } else if (requestCode == Application_Singleton.MULTIIMAGE_SELECT_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            clearFocus();
            ArrayList<Image> temp = data.getParcelableArrayListExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_IMAGES);
            boolean isInsertMode = false;
            if (addProductAdapter != null && addProductAdapter.getProducts().size() == 0) {
                // Set Adapter
                isInsertMode = false;
            } else {
                // Insert Mode
                isInsertMode = true;
            }
            for (Image image : temp) {
                if (image != null && image.getName() != null) {
                    image.setName(image.getName().replaceAll(".jpg", "").replaceAll(".png", "").replaceAll(".jpeg", ""));
                } else {
                    image.setName("");
                }
                product_image.add(image);
                if (isInsertMode) {
                    if (addProductAdapter != null)
                        addProductAdapter.notifyItemInserted(product_image.size() - 1);
                }
            }


            if (!isInsertMode) {
                addProductAdapter = new AddProductAdapter2(getActivity(), product_image,
                        radio_full_catalog.isChecked(),
                        Fragment_AddCatalog_Version2.this,
                        check_same_price.isChecked(), recycler_products, radio_catalog.isChecked());
                addProductAdapter.setChangeProductListener(this);
                if (!radio_full_catalog.isChecked()) {
                    if (!edit_common_add_margin.getText().toString().isEmpty()) {
                        if (radio_common_per.isChecked()) {
                            addProductAdapter.singlePCAddPer = edit_common_add_margin.getText().toString();
                        } else {
                            addProductAdapter.singlePcAddPrice = edit_common_add_margin.getText().toString();
                        }
                    }
                }
                recycler_products.setAdapter(addProductAdapter);
                Log.e("TAG", "onActivityResult: Set Adapter====>");
            } else {
                if (addProductAdapter != null) {
                    Log.e("TAG", "onActivityResult: Notify====>");
                    Log.e("TAG", "onActivityResult: " + product_image.get(0).price);
                    addProductAdapter.notifyItemRangeChanged(0, product_image.size());
                }

            }

            if (addProductAdapter != null) {
                if (isSizeAvailable && size_values != null) {
                    addProductAdapter.setSizes(size_values);
                }

            }

        } else if (requestCode == AddProductAdapter2.SINGLE_IMAGE_CHANGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            clearFocus();
            ArrayList<Image> temp = data.getParcelableArrayListExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_IMAGES);
            if (temp.size() > 0 && product_image != null && product_image.size() > 0) {
                Image pre_image = product_image.get(AddProductAdapter2.EDIT_POSITION);
                if (temp.get(0) != null && temp.get(0).getName() != null) {
                    temp.get(0).setName(temp.get(0).getName().replaceAll(".jpg", "").replaceAll(".png", "").replaceAll(".jpeg", ""));
                } else {
                    temp.get(0).setName("");
                }
                pre_image.setName(temp.get(0).getName());
                pre_image.setPath(temp.get(0).getPath());
                product_image.set(AddProductAdapter2.EDIT_POSITION, pre_image);
                if (addProductAdapter != null) {
                    addProductAdapter.notifyItemChanged(AddProductAdapter2.EDIT_POSITION);
                }
            }
        } else if (requestCode == Application_Singleton.MULTIIMAGE_SCREEN_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            clearFocus();
            ArrayList<Image> temp = data.getParcelableArrayListExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_IMAGES);
            boolean isInsertMode = false;
            if (temp.size() > 0 && screenSetModels != null && screenSetModels.size() > 0) {
                ScreenSetModel set_model = screenSetModels.get(AddScreenSetAdapter.EDIT_POSITION);
                ArrayList<Image> temp1;
                if (screenSetModels.get(AddScreenSetAdapter.EDIT_POSITION).getImages() == null) {
                    temp1 = new ArrayList<>();
                } else {
                    temp1 = screenSetModels.get(AddScreenSetAdapter.EDIT_POSITION).getImages();
                }
                temp1.addAll(temp);
                set_model.setImages(temp1);
                screenSetModels.set(AddScreenSetAdapter.EDIT_POSITION, set_model);
            }

            if (addScreenSetAdapter != null)
                if (addScreenSetAdapter != null) {
                    addScreenSetAdapter.notifyItemChanged(AddScreenSetAdapter.EDIT_POSITION);
                }
        } else if (requestCode == Application_Singleton.MULTIIMAGE_PRODUCT_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            clearFocus();
            ArrayList<Image> temp = data.getParcelableArrayListExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_IMAGES);
            if (temp.size() > 0 && product_image != null && product_image.size() > 0) {
                Image pre_image = product_image.get(AddProductAdapter2.ADD_MORE_IMAGE_POSITION);
                ArrayList<Image> temp1;
                if (product_image.get(AddProductAdapter2.ADD_MORE_IMAGE_POSITION).getMore_images() == null) {
                    temp1 = new ArrayList<>();
                } else {
                    temp1 = product_image.get(AddProductAdapter2.ADD_MORE_IMAGE_POSITION).getMore_images();
                }
                temp1.addAll(temp);
                pre_image.setMore_images(temp1);
                product_image.set(AddProductAdapter2.ADD_MORE_IMAGE_POSITION, pre_image);
                if (addProductAdapter != null) {
                    addProductAdapter.notifyItemChanged(AddProductAdapter2.ADD_MORE_IMAGE_POSITION);
                }
            }
        }
    }


    public void resetFabricWorkStyle() {
        selectedFabric.clear();
        selectedWork.clear();
        selectedStyle.clear();

        if (selectedStyle.size() > 0) {
            btn_add_style_first.setVisibility(View.GONE);
        }
        flexbox_style.removeAllViews();
        if (selectedStyle.size() == 0) {
            btn_add_style_first.setVisibility(View.VISIBLE);
        }


        if (selectedFabric.size() > 0) {
            btn_add_fabric_first.setVisibility(View.GONE);
        }
        flexbox_fabric.removeAllViews();
        flexbox_fabric.addView(btn_add_fabric_first);

        if (selectedWork.size() > 0) {
            btn_add_work_first.setVisibility(View.GONE);
        }
        flexbox_work.removeAllViews();
        flexbox_work.addView(btn_add_work_first);
    }

    public void setFabric(ArrayList<String> valueString) {
        selectedFabric = valueString;
        if (selectedFabric!=null && selectedFabric.size() > 0) {
            btn_add_fabric_first.setVisibility(View.VISIBLE);
        }
        flexbox_fabric.removeAllViews();
        ChipCloudConfig deleteableConfig = new ChipCloudConfig()
                .selectMode(ChipCloud.SelectMode.mandatory)
                .checkedChipColor(Color.parseColor("#ddaa00"))
                .checkedTextColor(Color.parseColor("#ffffff"))
                .uncheckedChipColor(Color.parseColor("#e0e0e0"))
                .showClose(Color.parseColor("#a6a6a6"))
                .useInsetPadding(true)
                .uncheckedTextColor(Color.parseColor("#000000"));

        ChipCloud deleteableCloud = new ChipCloud(getActivity(), flexbox_fabric, deleteableConfig);
        if (selectedFabric != null) {
            for (String s : selectedFabric) {
                deleteableCloud.addChip(s);
            }
            flexbox_fabric.addView(btn_add_fabric_first);

            deleteableCloud.setDeleteListener(new ChipDeletedListener() {
                @Override
                public void chipDeleted(int index, String label) {
                    selectedFabric.remove(label);
                    if (selectedFabric.size() == 0) {
                        //btn_add_fabric.setVisibility(View.GONE);
                        btn_add_fabric_first.setVisibility(View.VISIBLE);
                    }
                    Log.d("TAG", String.format("chipDeleted at index: %d label: %s", index, label));
                }
            });
        }

    }

    public void setWork(ArrayList<String> valeStringArrayList) {
        selectedWork = valeStringArrayList;
        if (selectedWork != null && selectedWork.size() > 0) {
            btn_add_work_first.setVisibility(View.VISIBLE);
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

        if (selectedWork != null) {
            for (String s : selectedWork) {
                deleteableCloud.addChip(s);
            }
            flexbox_work.addView(btn_add_work_first);
            deleteableCloud.setDeleteListener(new ChipDeletedListener() {
                @Override
                public void chipDeleted(int index, String label) {
                    selectedWork.remove(label);
                    if (selectedWork.size() == 0) {
                        btn_add_work_first.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public void setStyle(ArrayList<String> valueStringArrayList) {
        selectedStyle = valueStringArrayList;
        if (selectedStyle != null && selectedStyle.size() > 0) {
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
        if (selectedStyle != null) {
            for (String s : selectedStyle) {
                deleteableCloud.addChip(s);
            }

            deleteableCloud.setDeleteListener(new ChipDeletedListener() {
                @Override
                public void chipDeleted(int index, String label) {
                    selectedStyle.remove(label);
                    if (selectedStyle.size() == 0) {
                        btn_add_style_first.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

    }


    public void setStepOne() {
        nested_step_one.setVisibility(View.VISIBLE);
        nested_step_two.setVisibility(View.GONE);
        nested_step_three.setVisibility(View.GONE);
        nested_step_one.scrollTo(0, 0);
    }

    public void setStepTwo() {
        sendCatalogStepOneAnalytics();
        if (isCatalogTypeScreen()) {

            nested_step_one.setVisibility(View.GONE);
            nested_step_two.setVisibility(View.GONE);
            nested_step_three.setVisibility(View.VISIBLE);
            nested_step_three.scrollTo(0, 0);
            if (screenSetModels.size() == 0) {
                ScreenSetModel screenSetModel = new ScreenSetModel();
                if (((FileBean) spinner_category.getSelectedItem()).getFileName().contains(Constants.SCREEN_SIZE_SET)) {
                    screenSetModel.setColor_set_type(false);
                } else {
                    screenSetModel.setColor_set_type(true);
                }
                screenSetModels.add(screenSetModel);
            } else {
                if (screenSetModels != null) {
                    for (int i = 0; i < screenSetModels.size(); i++) {
                        if (((FileBean) spinner_category.getSelectedItem()).getFileName().contains(Constants.SCREEN_SIZE_SET)) {
                            screenSetModels.get(i).setColor_set_type(false);
                        } else {
                            screenSetModels.get(i).setColor_set_type(true);
                        }
                    }
                    if (addScreenSetAdapter != null)
                        addScreenSetAdapter.notifyItemRangeChanged(0, screenSetModels.size());
                }
            }

            btn_add_set.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validateScreen()) {
                        if (screenSetModels != null) {
                            ScreenSetModel screenSetModel = new ScreenSetModel();
                            if (((FileBean) spinner_category.getSelectedItem()).getFileName().contains(Constants.SCREEN_SIZE_SET)) {
                                screenSetModel.setColor_set_type(false);
                            } else {
                                screenSetModel.setColor_set_type(true);
                            }
                            screenSetModels.add(screenSetModel);
                            addScreenSetAdapter.notifyItemInserted(screenSetModels.size() - 1);
                        }
                    }
                }
            });
        } else {
            nested_step_one.setVisibility(View.GONE);
            nested_step_three.setVisibility(View.GONE);
            nested_step_two.setVisibility(View.VISIBLE);
            nested_step_two.scrollTo(0, 0);
            recycler_products.setFocusable(false);

            if (radio_noncatalog.isChecked() && edit_no_of_design.getError() != null) {
                edit_no_of_design.setError(null);
            }

            if (radio_catalog.isChecked()) {
                catalog_minimum_note.setVisibility(View.VISIBLE);
            } else {
                catalog_minimum_note.setVisibility(View.GONE);
            }


            btn_add_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (edit_no_of_design.getText().toString().equals("")) {
                        edit_no_of_design.setError("Please enter no. of design");
                    } else {
                        if (radio_catalog.isChecked()) {
                            if (Integer.parseInt(edit_no_of_design.getText().toString()) < 3) {
                                Toast.makeText(getActivity(), "minimum 3 design required", Toast.LENGTH_SHORT).show();
                                edit_no_of_design.setError("please change the total no of design to 3");
                                edit_no_of_design.requestFocus();
                                return;
                            }
                        }
                        textViewDefineCount = Integer.parseInt(edit_no_of_design.getText().toString());
                        if (addProductAdapter != null) {
                            recyclerViewCount = addProductAdapter.getItemCount();
                        }
                        if (textViewDefineCount - recyclerViewCount > 0) {
                            clearFocus();
                            Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
                            intent.putExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, textViewDefineCount - recyclerViewCount);
                            startActivityForResult(intent, Application_Singleton.MULTIIMAGE_SELECT_REQUEST_CODE);
                        } else {
                            if (textViewDefineCount > recyclerViewCount) {
                                //Remaining message
                                Toast.makeText(getActivity(), String.format(getResources().getString(R.string.add_design_subtext_limit), textViewDefineCount - recyclerViewCount), Toast.LENGTH_LONG).show();
                            } else {
                                // upload message
                                Toast.makeText(getActivity(), String.format(getResources().getString(R.string.add_design_subtext_limit1), textViewDefineCount), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            });


            if (radio_full_catalog.isChecked()) {
                showSize(true);
            }

            setStepTwoListener();


            if (addProductAdapter != null && check_same_price.isChecked() && rg_common_price.getCheckedRadioButtonId() == -1) {
                radio_common_per.setChecked(true);
            }
        }
    }

    public void checkSamePriceRemove() {
        check_same_price.setChecked(false);
    }

    public void setStepTwoListener() {
        radio_full_single.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (radio_full_catalog.isChecked()) {
                    addProductAdapter.changeFullCatalog(radio_full_catalog.isChecked());
                    showSize(true);
                    addProductAdapter.notifyDataSetChanged();
                    linear_common_margin.setVisibility(View.GONE);
                } else {
                    addProductAdapter.changeFullCatalog(radio_full_catalog.isChecked());
                    showSize(false);
                    addProductAdapter.notifyDataSetChanged();
                    linear_common_margin.setVisibility(View.VISIBLE);
                }
            }
        });
        check_same_price.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    addProductAdapter.isSamePrice(b);
                    for (int i = 0; i < addProductAdapter.getProducts().size(); i++) {
                        Log.e("TAG", "onCheckedChanged: " + product_image.get(i).getPrice());
                    }
                    addProductAdapter.notifyItemRangeChanged(0, addProductAdapter.getItemCount());
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            recycler_products.scrollToPosition(0);
                        }
                    });
                } else {
                    addProductAdapter.isSamePrice(b);
                    for (int i = 0; i < addProductAdapter.getProducts().size(); i++) {
                        Log.e("TAG", "onCheckedChanged: " + product_image.get(i).getPrice());
                    }
                    //addProductAdapter.notifyItemRangeChanged(0,addProductAdapter.getItemCount());
                }
            }
        });

        rg_common_price.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (!radio_full_catalog.isChecked()) {
                    if (radio_common_per.isChecked()) {
                        if (addProductAdapter != null) {
                            addProductAdapter.singlePcAddPrice = null;
                            if (!edit_common_add_margin.getText().toString().isEmpty()) {
                                addProductAdapter.singlePCAddPer = edit_common_add_margin.getText().toString();
                            }
                        }
                    } else {
                        if (addProductAdapter != null) {
                            addProductAdapter.singlePCAddPer = null;
                            if (!edit_common_add_margin.getText().toString().isEmpty()) {
                                addProductAdapter.singlePcAddPrice = edit_common_add_margin.getText().toString();
                            }
                        }
                    }
                    addProductAdapter.notifyDataSetChanged();
                } else {
                    if (addProductAdapter != null) {
                        addProductAdapter.singlePCAddPer = null;
                        addProductAdapter.singlePcAddPrice = null;
                    }
                    addProductAdapter.notifyDataSetChanged();
                }
            }
        });


        edit_common_add_margin.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (addProductAdapter != null) {
                    if (radio_common_per.isChecked()) {
                        addProductAdapter.singlePCAddPer = edit_common_add_margin.getText().toString();
                    } else {
                        addProductAdapter.singlePcAddPrice = edit_common_add_margin.getText().toString();
                    }
                    addProductAdapter.notifyDataSetChanged();
                }
                //updatesingleCommonPrice(edit_common_price.getText().toString());
            }
        });
    }


    public void bindProductRecyclerView() {
        product_image = new ArrayList<>();
        addProductAdapter = new AddProductAdapter2(getActivity(), product_image,
                radio_full_catalog.isChecked(),
                Fragment_AddCatalog_Version2.this,
                check_same_price.isChecked(), recycler_products, radio_catalog.isChecked());
        addProductAdapter.setChangeProductListener(this);
        recycler_products.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_products.setAdapter(addProductAdapter);
        //recycler_products.setHasFixedSize(true);
        recycler_products.setNestedScrollingEnabled(false);

        screenSetModels = new ArrayList<>();
        addScreenSetAdapter = new AddScreenSetAdapter(getActivity(), screenSetModels, recyclerview_screen_set, Fragment_AddCatalog_Version2.this);
        recyclerview_screen_set.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerview_screen_set.setAdapter(addScreenSetAdapter);
        recyclerview_screen_set.setNestedScrollingEnabled(false);
    }

    public void updateUI(boolean isFromBack) {
        if (add_step == 1) {
            setStepOne();
        } else if (add_step == 2) {
            setStepTwo();
        }
    }

    public String scanQRImage(Bitmap bMap) {
        String contents = null;
        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
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
    public void doBack() {
        if (add_step == 1) {
            if (getActivity() != null)
                getActivity().finish();
        } else {
            add_step = add_step - 1;
            if (isEditMode) {
                btn_continue.setText("Continue");
            } else {
                btn_continue.setText("Save & Continue");
            }
            updateUI(true);
        }
    }


    //// ###### Call API  #### ///


    private void postDataCatalogWithEditSupport(boolean isWithFile) {
        if (!auto_text_catalog.getText().toString().equals("") && spinner_brand != null && spinner_brand.getChildCount() > 0) {
            File mDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/wishbooks/");
            File outputrenamed = new File(mDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            try {
                final File output = new File(mDirectory, "catalogpreview.jpg");
                if (isWithFile) {
                    StaticFunctions.copy(output, outputrenamed);
                }

                final HashMap params = new HashMap();
                params.put("title", auto_text_catalog.getText().toString());
                params.put("brand", spinner_brand.getSelectedItem() == null ? "" : ((Response_Brands) (spinner_brand.getSelectedItem())).getId());
                if (params.get("brand").equals(""))
                    params.remove("brand");
                if (params.get("brand").equals("-1"))
                    params.remove("brand");
                if (!isEditMode) {
                    params.put("category", "" + spinner_category.getSelectedItem() == null ? "" : ((FileBean) (spinner_category.getSelectedItem())).fileId);
                }

                if (radio_noncatalog.isChecked()) {
                    params.put("catalog_type", Constants.CATALOG_TYPE_NON);
                    params.put("photoshoot_type", spinner_photoshoot.getSelectedItem().toString());
                } else {
                    // in catalog set default value
                    params.put("photoshoot_type", "Live Model Photoshoot");
                }

                if (!radio_full_catalog.isChecked()) {
                    if (radio_common_per.isChecked()) {
                        params.put("single_piece_price_percentage", edit_common_add_margin.getText().toString());
                    } else {
                        params.put("single_piece_price", edit_common_add_margin.getText().toString());
                    }
                }


                if (!isEditMode) {
                    //public
                    params.put("public_price", "" + addProductAdapter.getProducts().get(0).getPrice());
                    params.put("view_permission", "public");
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
                }


                RequestEav requestEav = new RequestEav();
                HashMap<String, Object> eav = new HashMap<>();
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


                // send dynamic eav data of radio
                for (int i = 0; i < enumRadioGroup.size(); i++) {
                    try {
                        RadioButton rb = (RadioButton) view.findViewById(enumRadioGroup.get(i).getCheckedRadioButtonId());
                        eav.put(enumRadioGroup.get(i).getTag().toString(), rb.getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                for (int i = 0; i < multiFlexLayout.size(); i++) {
                    try {
                        eav.put(multiFlexLayout.get(i).getTag().toString(), multiCheck.get(multiFlexLayout.get(i).getTag().toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                if (eav_text != null && eav_text.size() > 0) {
                    for (Map.Entry<String, String> entry : eav_text.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if (value != null && !value.isEmpty()) {
                            eav.put(key, value);
                        }
                    }
                }


                if (!edit_other_eav.getText().toString().trim().isEmpty()) {
                    requestEav.setOther(edit_other_eav.getText().toString().trim());
                    eav.put("other", edit_other_eav.getText().toString().trim());
                }

                if (isWithFile)
                    params.put("eav", Application_Singleton.gson.toJson(eav));
                else
                    params.put("eav",eav);

                if (!text_dispatch.getText().toString().isEmpty() && !text_dispatch.getText().toString().equalsIgnoreCase(getResources().getString(R.string.select_dispatch_date))) {
                    String dispatch_server = DateUtils.changeDateFormat(StaticFunctions.CLIENT_DISPLAY_FORMAT, StaticFunctions.SERVER_POST_FORMAT, text_dispatch.getText().toString());
                    if (!dispatch_server.isEmpty() && !dispatch_server.equals("")) {
                        params.put("dispatch_date", dispatch_server);
                    }
                }

                if (radio_full_catalog.isChecked()) {
                    params.put("sell_full_catalog", true);
                } else {
                    params.put("sell_full_catalog", false);
                }


                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

                String url = URLConstants.companyUrl(getActivity(), "upload_catalogs", "");
                HttpManager.METHOD method = HttpManager.METHOD.FILEUPLOAD;

                if (isEditMode) {
                    if (!isWithFile) {
                        outputrenamed = null;
                    }
                    method = HttpManager.METHOD.FILEUPLOADPATCHWITHPROGRESS;
                    url = URLConstants.companyUrl(getActivity(), "upload_catalogs", "") + edit_catalog_id + "/";
                } else {
                    method = HttpManager.METHOD.FILEUPLOAD;
                    url = URLConstants.companyUrl(getActivity(), "upload_catalogs", "");
                }

                if (!StaticFunctions.isOnline(getActivity())) {
                    HttpManager.showNetworkAlert(getActivity());
                    return;
                }
                showUploadDialog();
                Log.e(TAG, "Create Request : Catalog Upload======>");
                Log.e(TAG, "postDataCatalogWithEditSupport: ===>"+Application_Singleton.gson.toJson(params) );
                HttpManager.getInstance(getActivity()).requestwithFile(method, url, params, headers, "thumbnail", "image/jpg", outputrenamed, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Log.e(TAG, "onServerResponse: Catalog Upload======>");
                        if (isAdded() && !isDetached()) {
                            try {
                                hideUploadDialog();
                                old_catalog_response = new Gson().fromJson(response, Add_Catalog_Response.class);
                                sendCatalogItemAnalytics(old_catalog_response.getId(), params);
                                try {
                                    if (output != null && output.exists()) {
                                        output.delete();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                // Response_catalogAdd response_catalogAdd = new Gson().fromJson(response, Response_catalogAdd.class);
                                Fragment_AddProduct addProduct = new Fragment_AddProduct();
                                Bundle bundle = new Bundle();
                                bundle.putString("catalog_category", new Gson().toJson(old_catalog_response.getCategory()));
                                bundle.putString("catalog_brand", old_catalog_response.getBrand());
                                bundle.putString("catalog_id", old_catalog_response.getId());
                                addProduct.setArguments(bundle);
                                Application_Singleton.CONTAINER_TITLE = "Add Designs";
                                Application_Singleton.CONTAINERFRAG = addProduct;


                                //posting additional Options to Server


                                final CatalogUploadOption option = new CatalogUploadOption();
                                if (check_same_price.isChecked()) {
                                    option.setPublic_single_price(addProductAdapter.getProducts().get(0).getPrice());
                                    option.setPrivate_single_price(addProductAdapter.getProducts().get(0).getPrice());
                                }
                                option.setWithout_price(false);
                                option.setCatalog(old_catalog_response.getId());

                                postCatalogOptions(option);
                                if (isEditMode && validationProductsRecyclerView()) {
                                    stageUpdateExistingProduct();
                                } else {
                                    if (validationProductsRecyclerView()) {
                                        counter = 0;
                                        stageProductUpload(false);
                                    }
                                }
                            } catch (Exception e) {
                                btn_continue.setEnabled(true);
                                hideUploadDialog();
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        btn_continue.setEnabled(true);
                        hideUploadDialog();
                        StaticFunctions.showResponseFailedDialog(error);
                        Toast.makeText(getActivity(), error.getErrormessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            } catch (IOException e) {
                btn_continue.setEnabled(true);
                hideUploadDialog();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


        } else {
            btn_continue.setEnabled(true);
            if (auto_text_catalog.getText().toString().equals("")) {
                auto_text_catalog.setError("Catalog name cannot be empty");
                auto_text_catalog.requestFocus();
                Toast.makeText(getActivity(), "Catalog name cannot be empty", Toast.LENGTH_LONG).show();
            } else if (bitmapImage == null) {
                new MaterialDialog.Builder(getActivity())
                        .content("Please select cover image")
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            } else if (spinner_brand.getChildCount() < 1) {
                Toast.makeText(getActivity(), "Please select the brand", Toast.LENGTH_LONG).show();
            }
        }
    }

    //// ###### Call Non-catalog API  #### ///
    private void postDataNonCatalogPhase2() {
        //if (edit_screen_quality_name.getText().toString().equals("") && spinner_brand != null && spinner_brand.getChildCount() > 0) {
        try {
            final HashMap params = new HashMap();
            params.put("title", edit_screen_quality_name.getText().toString());
            params.put("brand", spinner_brand.getSelectedItem() == null ? "" : ((Response_Brands) (spinner_brand.getSelectedItem())).getId());
            if (params.get("brand").equals(""))
                params.remove("brand");
            if (params.get("brand").equals("-1"))
                params.remove("brand");
            params.put("category", "" + spinner_category.getSelectedItem() == null ? "" : ((FileBean) (spinner_category.getSelectedItem())).fileId);
            params.put("catalog_type", Constants.CATALOG_TYPE_NON);
            params.put("set_type", "multi_set");

            if (((FileBean) (spinner_category.getSelectedItem())).getFileName().contains(Constants.SCREEN_SIZE_SET)) {
                params.put("multi_set_type", "size_set");
            } else {
                params.put("multi_set_type", "color_set");
            }

            params.put("no_of_pcs_per_design", edit_screen_pc_set.getText().toString());
            params.put("price_per_design", edit_screen_price_pc.getText().toString());
            params.put("view_permission", "public");

            params.put("photoshoot_type", spinner_photoshoot.getSelectedItem().toString());

            RequestEav requestEav = new RequestEav();
            HashMap<String, Object> eav = new HashMap<>();
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


            // send dynamic eav data of radio
            for (int i = 0; i < enumRadioGroup.size(); i++) {
                try {
                    RadioButton rb = (RadioButton) view.findViewById(enumRadioGroup.get(i).getCheckedRadioButtonId());
                    eav.put(enumRadioGroup.get(i).getTag().toString(), rb.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            for (int i = 0; i < multiFlexLayout.size(); i++) {
                try {
                    eav.put(multiFlexLayout.get(i).getTag().toString(), multiCheck.get(multiFlexLayout.get(i).getTag().toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (eav_text != null && eav_text.size() > 0) {
                for (Map.Entry<String, String> entry : eav_text.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (value != null && !value.isEmpty()) {
                        eav.put(key, value);
                    }
                }
            }


            if (!edit_other_eav.getText().toString().trim().isEmpty()) {
                requestEav.setOther(edit_other_eav.getText().toString().trim());
                eav.put("other", edit_other_eav.getText().toString().trim());
            }

            params.put("eav", new Gson().fromJson(new Gson().toJson(eav), JsonObject.class));


            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            String url = URLConstants.companyUrl(getActivity(), "catalogs", "");
            HttpManager.METHOD method = HttpManager.METHOD.FILEUPLOAD;
            method = HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS;
            url = URLConstants.companyUrl(getActivity(), "upload_catalogs", "");

            if (!StaticFunctions.isOnline(getActivity())) {
                HttpManager.showNetworkAlert(getActivity());
                return;
            }

            HttpManager.getInstance(getActivity()).requestwithObject(method, url, new Gson().fromJson(new Gson().toJson(params), JsonObject.class), headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if (isAdded() && !isDetached()) {
                        try {
                            old_catalog_response = new Gson().fromJson(response, Add_Catalog_Response.class);
                            sendCatalogItemAnalytics(old_catalog_response.getId(), params);
                            screen_successcount = new HashMap<>();
                            for (int i = 0; i < screenSetModels.size(); i++) {
                                Log.e("TAG", "onServerResponse: Screen Name====>" + screenSetModels.get(i).getScreen_name());
                                uploadScreenProduct(i);
                            }
                        } catch (Exception e) {
                            btn_continue.setEnabled(true);
                            hideUploadDialog();
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    btn_continue.setEnabled(true);
                    StaticFunctions.showResponseFailedDialog(error);
                    Toast.makeText(getActivity(), error.getErrormessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            btn_continue.setEnabled(true);
            hideUploadDialog();
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    private void postCatalogOptions(CatalogUploadOption option) {
        Log.e(TAG, "Create Request Post Catalog Upload Option======>");
        Gson gson1 = new Gson();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.METHOD method;
        method = HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS;
        String url = URLConstants.companyUrl(getContext(), "catalogs_upload_option", "");


        HttpManager.getInstance((Activity) getContext()).requestwithObject(method, url, (gson1.fromJson(gson1.toJson(option), JsonObject.class)), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.e(TAG, "Response Catalog Upload Option======>");
                if (isAdded() && !isDetached()) {
                    try {
                        if (getContext() != null) {
                            old_catalog_option = Application_Singleton.gson.fromJson(response, CatalogUploadOption.class);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        btn_continue.setEnabled(true);
                        hideUploadDialog();
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                Log.e(TAG, "Post Catalog Upload Option FAILED======>");
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
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
            handlerPostData = new Handler();
            runnablePostData = new Runnable() {
                public void run() {
                    if (handlerPostData != null) {
                        long AfterTime = System.currentTimeMillis();
                        long TotalRxAfterTest = TrafficStats.getTotalTxBytes();
                        double rxDiff = TotalRxAfterTest - TotalTxBeforeTest;
                        if ((rxDiff != 0)) {
                            double rxBPS = (rxDiff / 1024); // total rx bytes per second.
                            dialogCatalogUpload.setContent("Uploading speed : " + (int) rxBPS + " KB/s");
                            TotalTxBeforeTest = TotalRxAfterTest;
                        }

                        handlerPostData.postDelayed(this, 1000);
                    }
                }
            };
            handlerPostData.postDelayed(runnablePostData, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideUploadDialog() {
        if (dialogCatalogUpload != null) {
            dialogCatalogUpload.dismiss();
        }
    }

    public boolean validationProductsRecyclerView() {
        boolean havePrice = true;
        boolean havePublicPrice = true;
        if (product_image.size() > 0) {
            if (radio_catalog.isChecked()) {
                if (product_image.size() < 3) {
                    Toast.makeText(getActivity(), "minimum 3 designs required !", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            if (edit_no_of_design.getText().toString().isEmpty()) {
                edit_no_of_design.requestFocus();
                edit_no_of_design.setError("Please enter design");
                return false;
            }
            textViewDefineCount = Integer.parseInt(edit_no_of_design.getText().toString());
            /*if (check_same_price.isChecked()) {
                if (edit_common_price.getText().toString().isEmpty()) {
                    edit_common_price.setError("Price cannot be empty");
                    edit_common_price.requestFocus();
                    return false;
                } else {
                    if (!checkPriceValidation(edit_common_price)) {
                        return false;
                    }
                }
            }*/
            if (addProductAdapter != null) {
                recyclerViewCount = addProductAdapter.getItemCount();
            } else {
                Toast.makeText(getActivity(), "No products selected !", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (textViewDefineCount - recyclerViewCount == 0) {
                for (int i = 0; i < product_image.size(); i++) {
                    if (!checkPriceValidation(product_image.get(i).price)) {
                        havePrice = false;
                        if (product_image.get(i).price.equals("") || product_image.get(i).price.equals("0")) {
                            Log.e("TAG", "validationProductsRecyclerView: i==>" + i + " \n Price" + product_image.get(i).price);
                            Toast.makeText(getActivity(), getResources().getString(R.string.error_null_price), Toast.LENGTH_SHORT).show();
                            return false;
                        } else {
                            int validate_value = 100;
                            if (radio_noncatalog.isChecked()) {
                                validate_value = 70;
                            }
                            if ((int) Double.parseDouble(product_image.get(i).price) <= validate_value) {
                                Toast.makeText(getActivity(), "Price should be greater than " + validate_value, Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        }
                        break;
                    }
                    if (view_permission.equals("public")) {
                        if (!checkPriceValidation(product_image.get(i).price)) {
                            havePublicPrice = false;
                            if (product_image.get(i).price.equals("") || product_image.get(i).price.equals("0")) {
                                Log.e("TAG", "validationProductsRecyclerView: 2");
                                Toast.makeText(getActivity(), getResources().getString(R.string.error_null_price), Toast.LENGTH_SHORT).show();
                            } else {
                                int validate_value = 100;
                                if (radio_noncatalog.isChecked()) {
                                    validate_value = 70;
                                }
                                if (Integer.parseInt(product_image.get(i).price) <= validate_value) {
                                    Toast.makeText(getActivity(), "Price should be greater than " + validate_value, Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                            }
                            break;
                        } else {
                            product_image.get(i).setPublic_price(product_image.get(i).getPrice());
                        }
                    }
                }
            } else {
                int remaining_count = textViewDefineCount;
                try {
                    remaining_count = textViewDefineCount - recyclerViewCount;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (textViewDefineCount > recyclerViewCount) {
                    //Remaining message
                    Toast.makeText(getActivity(), String.format(getResources().getString(R.string.add_design_subtext_limit), textViewDefineCount - recyclerViewCount), Toast.LENGTH_LONG).show();
                } else {
                    // upload message
                    Toast.makeText(getActivity(), String.format(getResources().getString(R.string.add_design_subtext_limit1), textViewDefineCount), Toast.LENGTH_LONG).show();
                }
                //Toast.makeText(getActivity(), String.format(getResources().getString(R.string.add_design_subtext_limit), remaining_count), Toast.LENGTH_LONG).show();
                return false;
            }
            if (!radio_full_catalog.isChecked()) {
                if (edit_common_add_margin.getText().toString().isEmpty()) {
                    edit_common_add_margin.setError("Please set margin for single pcs.");
                    edit_common_add_margin.requestFocus();
                    return false;
                } else {
                    float max_margin_allowed = Math.max(Float.parseFloat(addProductAdapter.getProducts().get(0).getPublic_price()) * 10 / 100, 60);
                    double max_percentage_allowed = (max_margin_allowed) / Float.parseFloat(addProductAdapter.getProducts().get(0).getPublic_price()) * 100;
                    DecimalFormat decimalFormat;
                    decimalFormat = new DecimalFormat("#.##");
                    if (radio_common_per.isChecked() && Double.parseDouble(edit_common_add_margin.getText().toString()) > max_percentage_allowed) {
                        edit_common_add_margin.setError("Margin percentage must be <= " + decimalFormat.format(max_percentage_allowed) + "%");
                        edit_common_add_margin.requestFocus();
                        Toast.makeText(getActivity(), "Margin percentage must be <= " + decimalFormat.format(max_percentage_allowed) + "%", Toast.LENGTH_SHORT).show();
                        return false;
                    } else if (radio_common_price.isChecked() && Double.parseDouble(edit_common_add_margin.getText().toString()) > max_margin_allowed) {
                        edit_common_add_margin.setError("Margin amount must be <= " + max_margin_allowed);
                        edit_common_add_margin.requestFocus();
                        Toast.makeText(getActivity(), "Margin amount must be <= " + max_margin_allowed, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
            if (isSizeAvailable) {
                if (radio_full_catalog.isChecked()) {
                    if (eavValidation.get("size")) {
                        if (catalog_level_size_eav == null || catalog_level_size_eav.size() == 0) {
                            Toast.makeText(getActivity(), "Please Select any size", Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }
                } else {
                    for (int i = 0; i < product_image.size(); i++) {
                        if (eavValidation.get("size")) {
                            if (product_image.get(i).getAvailable_sizes() == null || product_image.get(i).getAvailable_sizes().size() == 0) {
                                if(!isEditMode) {
                                    Toast.makeText(getActivity(), "Please select size for each product", Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                            }
                        }
                    }
                }

            }

        } else {
            Toast.makeText(getActivity(), "No products selected !", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void stageProductUpload(boolean isBulkProductUploadCalled) {
        Log.e(TAG, "Create Request Upload Products======>");
        //initializing loader
        dialogCatalogUpload = new MaterialDialog.Builder(getContext()).title("Uploading Images ..").build();
        dialogCatalogUpload.setCancelable(false);
        dialogCatalogUpload.show();
        BeforeTime = System.currentTimeMillis();
        TotalTxBeforeTest = TrafficStats.getTotalTxBytes();
        handlerPostData = new Handler();
        product_successcount = new HashMap<>();

        runnablePostData = new Runnable() {
            public void run() {
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
                // workaround solution
                if((AfterTime - BeforeTime) > 280000) {
                    Log.e(TAG,"After 4 Minute");
                    ObservableProductUpload();
                }
                handlerPostData.postDelayed(this, 1000);
            }
        };

        handlerPostData.postDelayed(runnablePostData, 1000);
        if (isEditMode) {
            ArrayList<Image> temp = new ArrayList<>();
            temp.addAll(product_image);
            for (Image p :
                    temp) {
                if (p.getPhoto_id() != null) {
                    product_image.remove(p);
                }
            }
        }

        product_image_clone = new ArrayList<>();
        product_image_clone.addAll(product_image);
        dialogCatalogUpload.setTitle("Uploading Image : " + counter + " / " + product_image_clone.size());
        addProductAdapter.removeAll();

        if (isEditMode && product_image_clone.size() == 0) {
            hideUploadDialog();
            navigateFromUploadPage();

           // callUploadEndPoint(edit_product_id);
        }

        for (int i = 0; i < product_image_clone.size(); i++) {
            try {
                Thread.sleep(300);
            } catch (Exception e){
                e.printStackTrace();
            }
            final Image image = product_image_clone.get(i);
            product_image.remove(image);
            File outputrenamed = new File(image.path);
            Log.v("selected image", outputrenamed.getAbsolutePath());
            if (outputrenamed.exists()) {
                try {
                    if (StaticFunctions.isOnline(getActivity())) {
                        HashMap params = new HashMap();
                        params.put("title", image.name);
                        params.put("sku", "" + image.name);
                        String catalog_id = null;
                        if (isEditMode) {
                            catalog_id = edit_catalog_id;
                        } else {
                            catalog_id = old_catalog_response.getId();
                        }
                        params.put("catalog", catalog_id);
                        params.put("price", image.price);
                        params.put("public_price", image.price);
                        if (isSizeAvailable) {
                            if (radio_full_catalog.isChecked()) {
                                if (catalog_level_size_eav != null && catalog_level_size_eav.size() > 0) {
                                    params.put("available_sizes", StaticFunctions.ArrayListToString(catalog_level_size_eav, StaticFunctions.COMMASEPRATED));
                                }
                            } else {
                                if (image.getAvailable_sizes() != null && !image.getAvailable_sizes().isEmpty()) {
                                    params.put("available_sizes", StaticFunctions.ArrayListToString(image.getAvailable_sizes(), StaticFunctions.COMMASEPRATED));
                                }
                            }
                        }

                        if (isEditMode) {
                            params.put("sort_order", (productCount + edit_catalog.getProduct().length));
                            productCount++;
                        } else {
                            if (image.sort_order < 1) {
                                image.setSort_order(productCount);
                                params.put("sort_order", productCount);
                            } else {
                                params.put("sort_order", image.sort_order);
                            }
                            productCount++;
                        }


                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                        final int finalI = i;


                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "stageProductUpload: =====>" + Application_Singleton.gson.toJson(params));
                        }
                        HttpManager.getInstance(getActivity()).requestwithFile(HttpManager.METHOD.FILEUPLOAD, URLConstants.companyUrl(getActivity(), "productsonly", catalog_id), params, headers, "image", "image/jpg", outputrenamed, false, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {
                                onServerResponse(response, false);
                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                try {
                                    Log.e(TAG, "Response Post Products======>" + counter);
                                    counter++;
                                    product_successcount.put(finalI, 0);
                                    ResponseAddProduct responseAddProduct = new Gson().fromJson(response, ResponseAddProduct.class);
                                    sendProductAddItemAnalytics(responseAddProduct);
                                    dialogCatalogUpload.setTitle("Uploading Image : " + counter + " / " + product_image_clone.size());
                                    edit_no_of_design.setText("" + ((Integer.parseInt(edit_no_of_design.getText().toString())) - 1));
                                    try {
                                        uploadProductMorePhotos(responseAddProduct.getId(), finalI);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onResponseFailed(final ErrorString error) {
                                Log.e(TAG, "Response Post Products FAILED======>" + counter);
                                if (isAdded() && !isDetached()) {
                                    dialogCatalogUpload.dismiss();
                                    hideUploadDialog();
                                    new MaterialDialog.Builder(getActivity())
                                            .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                                            .content(error.getErrormessage())
                                            .positiveText("OK")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                                    dialog.dismiss();
                                                    if (error.getErrormessage().equals("sku should be unique")) {
                                                        product_image.add(image);
                                                    }
                                                    if (product_image.size() > 0) {
                                                        addProductAdapter = new AddProductAdapter2(getActivity(), product_image,
                                                                radio_full_catalog.isChecked(),
                                                                Fragment_AddCatalog_Version2.this,
                                                                check_same_price.isChecked(), recycler_products, radio_catalog.isChecked());
                                                        recycler_products.setAdapter(addProductAdapter);
                                                    }
                                                }

                                            })
                                            .show();
                                }
                            }
                        });

                    } else {
                        StaticFunctions.showNetworkAlert(getActivity());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

    }


    private void uploadScreenProduct(final int position) {
        if (dialogCatalogUpload == null) {
            dialogCatalogUpload = new MaterialDialog.Builder(getContext()).title("Uploading Products ..").build();
            dialogCatalogUpload.setCancelable(false);
        }

        if (!dialogCatalogUpload.isShowing()) {
            dialogCatalogUpload.show();
        }
        setUploadTimer();
        HashMap params = new HashMap();
        params.put("title", screenSetModels.get(position).getScreen_name());
        params.put("sku", screenSetModels.get(position).getScreen_name());
        params.put("catalog", old_catalog_response.getId());
        params.put("sort_order", String.valueOf(position));
        params.put("product_type", "set");
        if (screenSetModels.get(position).getColor_name() != null && !screenSetModels.get(position).getColor_name().isEmpty())
            params.put("set_type_details", screenSetModels.get(position).getColor_name());

        int disable_days = 30;
        Date todayDate = new Date();
        Calendar objectCalendar = Calendar.getInstance();
        objectCalendar.setTime(todayDate);
        if (!screenSetModels.get(position).getExpiry_date().isEmpty()) {
            disable_days = Integer.parseInt(screenSetModels.get(position).getExpiry_date().toString().trim());
        }
        objectCalendar.add(Calendar.DATE, disable_days);
        Date expire_days = new Date(objectCalendar.getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String expireDateString = sdf.format(expire_days);
        params.put("expiry_date", expireDateString);


        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        File outputrenamed = new File(addScreenSetAdapter.getScreenProductImage(position).get(0).path);
        HttpManager.getInstance(getActivity()).requestwithFile(HttpManager.METHOD.FILEUPLOAD, URLConstants.companyUrl(getActivity(), "productsonly", old_catalog_response.getId()), params, headers, "image", "image/jpg", outputrenamed, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                screen_successcount.put(position, 0);
                //checkISSuccess();
                try {
                    ResponseProductUpload productUpload = Application_Singleton.gson.fromJson(response, ResponseProductUpload.class);
                    uploadProductPhotos(productUpload.getId(), position);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(final ErrorString error) {
                if (isAdded() && !isDetached()) {
                    hideUploadDialog();
                    new MaterialDialog.Builder(getActivity())
                            .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                            .content(error.getErrormessage())
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


    }


    private void uploadProductPhotos(String product_id, final int position) {
        HashMap params = new HashMap();
        final int[] counter = {0};
        for (int screen_photo_index = 0; screen_photo_index < addScreenSetAdapter.getScreenProductImage(position).size(); screen_photo_index++) {
            if (screen_photo_index == 0) {
                params.put("set_default", String.valueOf(true));
            } else {
                params.put("set_default", String.valueOf(false));
            }
            params.put("product", product_id);
            params.put("sort_order", String.valueOf((screen_photo_index + 1)));

            File outputrenamed = new File(addScreenSetAdapter.getScreenProductImage(position).get(screen_photo_index).path);
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            int finalScreen_photo_index = screen_photo_index;
            HttpManager.getInstance(getActivity()).requestwithFile(HttpManager.METHOD.FILEUPLOAD, URLConstants.companyUrl(getActivity(), "products_photos", product_id), params, headers, "image", "image/jpg", outputrenamed, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    counter[0]++;
                    screen_successcount.put(position, counter[0]);
                    checkISSuccess();
                }

                @Override
                public void onResponseFailed(final ErrorString error) {
                    if (isAdded() && !isDetached()) {
                        hideUploadDialog();
                        new MaterialDialog.Builder(getActivity())
                                .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                                .content(error.getErrormessage())
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
        }
    }

    /**
     * Send Product image with all other selected images
     *
     * @param product_id
     * @param position
     */
    private void uploadProductMorePhotos(String product_id, final int position) {
        HashMap params = new HashMap();
        final int[] photo_counter = {0};
        Log.e(TAG, "Request Post More Photos ======>" + photo_counter[0]);
        if (product_image_clone.get(position).getMore_images() == null || product_image_clone.get(position).getMore_images().size() == 0) {
            Log.e(TAG, "Response Post More Photos NO IMAGES======>" + photo_counter[0]);
            photo_counter[0]++;
            product_successcount.put(position, photo_counter[0]);
            ObservableProductUpload();
        } else {
            for (int photo_index = 0; photo_index < product_image_clone.get(position).getMore_images().size(); photo_index++) {
                params.put("set_default", String.valueOf(false));
                params.put("product", product_id);
                params.put("sort_order", String.valueOf((photo_index + 1)));
                File outputrenamed = new File(product_image_clone.get(position).getMore_images().get(photo_index).path);
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                int finalPhoto_index = photo_index;
                HttpManager.getInstance(getActivity()).requestwithFile(HttpManager.METHOD.FILEUPLOAD, URLConstants.companyUrl(getActivity(), "products_photos", product_id), params, headers, "image", "image/jpg", outputrenamed, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Log.e(TAG, "Response Post More Photos SUCCESS======>" + photo_counter[0]);
                        photo_counter[0]++;
                        product_successcount.put(position, photo_counter[0]);
                        ObservableProductUpload();
                    }

                    @Override
                    public void onResponseFailed(final ErrorString error) {
                        if (isAdded() && !isDetached()) {
                            hideUploadDialog();
                            new MaterialDialog.Builder(getActivity())
                                    .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                                    .content(error.getErrormessage())
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
            }
        }
    }

    private void setUploadTimer() {
        if (handlerPostData == null) {
            BeforeTime = System.currentTimeMillis();
            TotalTxBeforeTest = TrafficStats.getTotalTxBytes();
            handlerPostData = new Handler();
            runnablePostData = new Runnable() {
                public void run() {
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
                    handlerPostData.postDelayed(this, 1000);
                }
            };

            handlerPostData.postDelayed(runnablePostData, 1000);
        }

    }

    public void setCheckBoxColor(CheckBox checkBox, int checkedColor, int uncheckedColor) {
        checkBox.setButtonDrawable(getResources().getDrawable(R.drawable.checkbox_selector));

    }

    public void callUploadEndPoint(String bundle_id) {
        if(!isCallEndPointCalled) {
            isCallEndPointCalled = true;
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) getActivity());
            String url = URLConstants.companyUrl(getActivity(), "upload-endpoint", bundle_id) ;
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }
                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    navigateFromUploadPage();
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });
        }
    }


    public void checkISSuccess() {
        boolean isSucess = false;
        if (screen_successcount != null && screenSetModels != null) {
            for (int i = 0; i < screenSetModels.size(); i++) {
                if (screen_successcount.containsKey(i)) {
                    if (screen_successcount.get(i).intValue() >= (screenSetModels.get(i).getImages().size())) {
                        Log.e(TAG, "Screen Observable: IsSucess true ---" + screen_successcount.get(i).intValue() + " \nSize ===>" + (screenSetModels.get(i).getImages().size()));
                        isSucess = true;
                    } else {
                        isSucess = false;
                        break;
                    }
                } else {
                    isSucess = false;
                    break;
                }
            }
            if (isSucess) {
                Log.e(TAG, "Screen Observable: Success Called" );
                Toast.makeText(getActivity(), "Screen Uploaded Successfully", Toast.LENGTH_SHORT).show();
                if (handlerPostData != null) {
                    handlerPostData.removeCallbacks(runnablePostData);
                }
                hideUploadDialog();
                String bundle_id;
                if (isEditMode) {
                    bundle_id = edit_product_id;
                } else {
                    bundle_id = old_catalog_response.getProduct_id();
                }
                callUploadEndPoint(bundle_id);
            }
        }
    }

    public void ObservableProductUpload() {
        boolean isSuccess = false;
        if (product_successcount != null && product_image_clone != null) {
            Log.e(TAG, "=======ObservableProductUpload=======");
            for (int i = 0; i < product_image_clone.size(); i++) {
                if (product_successcount.containsKey(i)) {
                    int extra_image_size = 0;
                    if (product_image_clone.get(i).getMore_images() != null) {
                        extra_image_size = product_image_clone.get(i).getMore_images().size();
                    }
                    if (product_successcount.get(i).intValue() >= extra_image_size) {
                        Log.e(TAG, "ObservableProductUpload: Is Success true ---" + product_successcount.get(i).intValue() + " \nSize ===>" + extra_image_size);
                        isSuccess = true;
                    } else {
                        isSuccess = false;
                        break;
                    }
                } else {
                    isSuccess = false;
                }
            }
            if (isSuccess) {
                Log.e(TAG, "ObservableProductUpload: Success Called" );
                dialogCatalogUpload.dismiss();
                if (handlerPostData != null) {
                    handlerPostData.removeCallbacks(runnablePostData);
                }

                String bundle_id;
                if (isEditMode) {
                    bundle_id = edit_product_id;
                } else {
                    bundle_id = old_catalog_response.getProduct_id();
                }
                callUploadEndPoint(bundle_id);
            }
        }
    }

    public void navigateToMyCatalog() {
        Bundle bundle = new Bundle();
        Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs(CatalogHolder.MYCATALOGS);
        fragmentCatalogs.setArguments(bundle);
        Application_Singleton.CONTAINER_TITLE = "My Products";
        Application_Singleton.CONTAINERFRAG = fragmentCatalogs;
        Intent intent2 = new Intent(getActivity(), OpenContainer.class);
        intent2.putExtra("toolbarCategory", OpenContainer.ADD_CATALOG);
        String supplier_approval_status = UserInfo.getInstance(getActivity()).getSupplierApprovalStatus();
        if (supplier_approval_status == null) {
            UserInfo.getInstance(getActivity()).setSupplierApprovalStatus(Constants.SELLER_APPROVAL_FIRSTTIME_UPLOAD);
        }
        startActivity(intent2);
    }

    public void navigateFromUploadPage() {
        try {
            if(dialogCatalogUpload!=null) {
                dialogCatalogUpload.dismiss();
                if (handlerPostData != null) {
                    handlerPostData.removeCallbacks(runnablePostData);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }


        if (!isEditMode) {
            if (radio_noncatalog.isChecked()) {
                Toast.makeText(getActivity(), "Non-Catalog Uploaded Successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Catalog Uploaded Successfully", Toast.LENGTH_LONG).show();
            }
        }
        getActivity().setResult(Activity.RESULT_OK);
        if (!isEditMode) {
            String approval_status = UserInfo.getInstance(getActivity()).getSupplierApprovalStatus();
            if (approval_status == null) {
                navigateToMyCatalog();
            } else {
                if (!approval_status.equalsIgnoreCase(Constants.SELLER_APPROVAL_APPROVED)) {
                    navigateToMyCatalog();
                }
            }
        }
        getActivity().finish();
    }

    public boolean isCatalogTypeScreen() {
        if (radio_setmatching.isChecked()) {
            return true;
        }
        return false;
    }


    public boolean checkPriceValidation(String price) {
        if (price.toString().trim().isEmpty()) {
            return false;
        } else {
            int validate_value = 100;
            if (radio_noncatalog.isChecked()) {
                validate_value = 70;
            }
            double productPrice = 0;
            try {
                productPrice = Double.parseDouble(price);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            if (productPrice > validate_value)
                return true;
            else
                return false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handlerPostData != null) {
            handlerPostData.removeCallbacks(runnablePostData);
            // handlerPostData = null;
        }

        if (bitmapImage != null && !bitmapImage.isRecycled()) {
            bitmapImage.recycle();
        }
    }

    public boolean validateScreen() {
        ArrayList<String> temp_validation = new ArrayList<>();
        for (int i = 0; i < screenSetModels.size(); i++) {
            if (screenSetModels.get(i).getScreen_name() == null || screenSetModels.get(i).getScreen_name().isEmpty()) {
                Toast.makeText(getActivity(), "Please enter set name/number", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                if (temp_validation.contains(screenSetModels.get(i).getScreen_name())) {
                    Toast.makeText(getActivity(), "Set name/number can't be same", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    temp_validation.add(screenSetModels.get(i).getScreen_name());
                }
            }

            if (screenSetModels.get(i).isColor_set_type() && (screenSetModels.get(i).getColor_name() == null || screenSetModels.get(i).getColor_name().isEmpty())) {
                if (screenSetModels.get(i).isColor_set_type()) {
                    Toast.makeText(getActivity(), "Please enter color name", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
            if (screenSetModels.get(i).getImages() == null || screenSetModels.get(i).getImages().size() == 0) {
                Toast.makeText(getActivity(), "Please add atleast one product", Toast.LENGTH_SHORT).show();
                return false;
            }


            if (screenSetModels.get(i).getExpiry_date() == null || screenSetModels.get(i).getExpiry_date().trim().isEmpty()) {
                Toast.makeText(getActivity(), "Please enter enable duration", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                int days = Integer.parseInt(screenSetModels.get(i).getExpiry_date().trim());
                if (days < 10) {
                    Toast.makeText(getActivity(), "Minimum enable duration should be 10", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (days > 90) {
                    Toast.makeText(getActivity(), "Maximum enable duration should be 90", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }


        return true;
    }

    public void clearFocus() {
        KeyboardUtils.hideKeyboard(getActivity());
        View currentFocus = (getActivity()).getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
    }


    public void getTopCategory() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "category", "") + "?parent=1", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    try {
                        ResponseHomeCategories[] response_catagories = Application_Singleton.gson.fromJson(response, ResponseHomeCategories[].class);
                        int default_selection_index = 0;
                        if (response_catagories.length > 0) {
                            Log.e(TAG, "onServerResponse: ==>" + response_catagories.length);
                            for (int i = 0; i < response_catagories.length; i++) {
                                ResponseHomeCategories ctitem = response_catagories[i];
                                FileBean fileBean;
                                fileBean = new FileBean(Integer.parseInt(ctitem.getId()), -1, ctitem.getCategory_name());
                                mTopCategory.add(fileBean);
                                if (ctitem.getId().equals("4")) {
                                    // set womenswear default selected
                                    default_selection_index = i;
                                }

                            }
                            Log.e(TAG, "onServerResponse: Spinner ==>" + mTopCategory.size());
                            SpinAdapter_Categories spinAdapter_categories = new SpinAdapter_Categories(getActivity(), R.layout.spinneritem, mTopCategory);
                            spinner_top_category.setAdapter(spinAdapter_categories);
                            spinner_top_category.setSelection(default_selection_index);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    public void showSize(boolean isFullCatalog) {
        if (isFullCatalog) {
            if (isSizeAvailable && size_values.size() > 0) {
                addSizeCatalogLevel(size_values, flex_catalog_select_size, linear_catalog_select_size_container);
            } else {
                linear_catalog_select_size_container.setVisibility(View.GONE);
            }
        } else {
            linear_catalog_select_size_container.setVisibility(View.GONE);
        }
    }

    private void addSizeCatalogLevel(final ArrayList<ResponseCategoryEvp.Attribute_values> sizes1, FlexboxLayout root, LinearLayout linear_select_size_container) {
        if (sizes1 != null && sizes1.size() > 0) {
            final ArrayList<String> string_size = new ArrayList<>();
            for (int i = 0; i < sizes1.size(); i++) {
                string_size.add(sizes1.get(i).getValue());
            }
            if (string_size != null && string_size.size() > 0) {
                linear_select_size_container.setVisibility(View.VISIBLE);
                root.removeAllViews();
                for (int j = 0; j < string_size.size(); j++) {
                    final CheckBox checkBox = new CheckBox(getActivity());
                    checkBox.setId(j);
                    FlexboxLayout.LayoutParams check_lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
                    check_lp.setMargins(0, 0, 32, 12);
                    setCheckBoxColor(checkBox, R.color.color_primary, R.color.purchase_medium_gray);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        checkBox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    }
                    checkBox.setText(string_size.get(j));
                    checkBox.setTextColor(ContextCompat.getColor(getActivity(), R.color.purchase_dark_gray));
                    checkBox.setPadding(0, 0, 20, 0);
                    root.addView(checkBox, check_lp);
                    final int finalJ = j;
                    checkBox.setLayoutParams(check_lp);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            if (isChecked) {
                                if (catalog_level_size_eav == null) {
                                    catalog_level_size_eav = new ArrayList<>();
                                }
                                if (!catalog_level_size_eav.contains(string_size.get(finalJ)))
                                    catalog_level_size_eav.add(string_size.get(finalJ));
                            } else {
                                if (catalog_level_size_eav != null) {
                                    if (catalog_level_size_eav.contains(string_size.get(finalJ)))
                                        catalog_level_size_eav.remove(string_size.get(finalJ));
                                }
                            }
                        }
                    });

                    if (catalog_level_size_eav != null && catalog_level_size_eav.contains(string_size.get(j))) {
                        checkBox.setChecked(true);
                    }
                }
            }
        } else {
            linear_select_size_container.setVisibility(View.GONE);
            root.removeAllViews();
        }
    }

    // ######## Send Analytics Event Section Start ############ //

    public void sendCatalogItemAnalytics(String catalog_id, HashMap<String, String> hashMap) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.SELLER_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("CatalogItem_Add");
        HashMap<String, String> prop = new HashMap<>();
        prop.put("catalog_item_id", catalog_id);
        prop.put("product_type", hashMap.get("catalog_type"));
        prop.put("brand", spinner_brand.getSelectedItem() == null ? "" : ((Response_Brands) (spinner_brand.getSelectedItem())).getId());
        prop.put("product_category", ((FileBean) (spinner_category.getSelectedItem())).fileName);
        prop.put("full_catalog", hashMap.get("sell_full_catalog"));

        if (hashMap.containsKey("multi_set_type")) {
            prop.put("set_type", hashMap.get("multi_set_type"));
        }

        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(getActivity(), wishbookEvent);

        sendProductStateChangesAnalytics("Create", prop);
    }

    public void sendProductStateChangesAnalytics(String type, HashMap<String, String> hashMap) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.SELLER_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Product_StateChange");
        HashMap<String, String> prop = new HashMap<>();
        prop.put("status", type);
        prop.putAll(prop);
        wishbookEvent.setEvent_properties(hashMap);
        new WishbookTracker(getActivity(), wishbookEvent);
    }

    public void sendCatalogStepOneAnalytics() {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.SELLER_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("CatalogItem_Add");
        HashMap<String, String> prop = new HashMap<>();
        prop.put("brand", spinner_brand.getSelectedItem() == null ? "" : ((Response_Brands) (spinner_brand.getSelectedItem())).getId());
        prop.put("product_category", spinner_category.getSelectedItem() == null ? "" : ((FileBean) (spinner_category.getSelectedItem())).fileName);
        if (radio_catalog.isChecked()) {
            prop.put("product_type", "catalog");
        } else if (radio_noncatalog.isChecked()) {
            prop.put("product_type", "non-catalog");
        } else if (radio_setmatching.isChecked()) {
            prop.put("product_type", "set-matching");
        }
        if (radio_setmatching.isChecked()) {
            prop.put("catalog_item_name", edit_screen_quality_name.getText().toString());
            if (((FileBean) (spinner_category.getSelectedItem())).getFileName().contains(Constants.SCREEN_SIZE_SET)) {
                prop.put("set_type", "size_set");
            } else {
                prop.put("set_type", "color_set");
            }
        } else {
            prop.put("catalog_item_name", auto_text_catalog.getText().toString());
        }


        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(getActivity(), wishbookEvent);
        sendCatalogStepTwoAnalytics();
    }


    public void sendCatalogStepTwoAnalytics() {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.SELLER_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Product_Add_screen");
        HashMap<String, String> prop = new HashMap<>();
        prop.put("source", "Product Add Page");
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(getActivity(), wishbookEvent);
    }

    public void sendProductAddItemAnalytics(ResponseAddProduct responseAddProduct) {
        try {
            WishbookEvent wishbookEvent = new WishbookEvent();
            wishbookEvent.setEvent_category(WishbookEvent.SELLER_EVENTS_CATEGORY);
            wishbookEvent.setEvent_names("Product_Add");
            HashMap<String, String> prop = new HashMap<>();
            prop.put("product_id", responseAddProduct.getCatalog_id());
            prop.put("product_name", responseAddProduct.getCatalog_type());
            prop.put("brand", spinner_brand.getSelectedItem() == null ? "" : ((Response_Brands) (spinner_brand.getSelectedItem())).getId());
            prop.put("product_category", ((FileBean) (spinner_category.getSelectedItem())).fileName);
            prop.put("full_catalog", String.valueOf(responseAddProduct.getFull_catalog_orders_only()));
            prop.put("set_type", responseAddProduct.getMulti_set_type());
            prop.put("price", responseAddProduct.getPrice());


            wishbookEvent.setEvent_properties(prop);
            new WishbookTracker(getActivity(), wishbookEvent);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // ######## Send Analytics Event Section End ############ //


    // ######## Edit Catalog Section Start ############ //
    public void getCatalogDataBeforeEdit(final Context context, final String catalog_id) {
        final MaterialDialog progressDialog = StaticFunctions.showProgressDialog(context, "Loading", "Please wait...", true);
        progressDialog.show();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        String url = URLConstants.companyUrl(getActivity(), "catalogs_expand_true_id", catalog_id);
        HashMap<String, String> param = new HashMap<>();
        param.put("view_type", "mycatalogs");
        param.put("show_all_products", "true");
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, url, param, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    try {
                        edit_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                        getMyDetails(getActivity(), edit_catalog.getId(), edit_catalog);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    public void getMyDetails(Context context, String product_id, final Response_catalog response_catalog) {
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            final MaterialDialog progressDialog = StaticFunctions.showProgressDialog(context, "Loading", "Please wait...", true);
            progressDialog.show();
            HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context, "mydetails", product_id), null, headers, false, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    try {
                        if (isAdded() && !isDetached()) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            productMyDetail = Application_Singleton.gson.fromJson(response, ProductMyDetail.class);

                            if (productMyDetail.isIs_owner()) {
                                response_catalog.setIs_owner(true);
                            }

                            if (productMyDetail.isI_am_selling_this()) {
                                response_catalog.setIs_seller(true);
                            }

                            if (productMyDetail.isCurrently_selling()) {
                                response_catalog.setIs_currently_selling(true);
                            }

                            if (productMyDetail.isI_am_selling_sell_full_catalog()) {
                                response_catalog.setI_am_selling_sell_full_catalog(true);
                            }

                            fillData(edit_catalog);

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
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fillData(Response_catalog response_catalog) {
        // Fill Step One Data
        rg_product_type.setEnabled(false);
        radio_catalog.setEnabled(false);
        radio_noncatalog.setEnabled(false);
        radio_setmatching.setEnabled(false);
        spinner_category.setEnabled(false);
        view.findViewById(R.id.text_inputlayout_category).setVisibility(View.GONE);
        linear_enable_duration_container.setVisibility(View.GONE);


        if(response_brands!=null && response_brands.length > 0) {
            setBrandAdapter(response_brands);
        }

        if (response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_CAT)) {
            radio_catalog.setChecked(true);
        } else if (response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_NON)) {
            radio_noncatalog.setChecked(true);
            List<String> photoshoot_array = Arrays.asList(getResources().getStringArray(R.array.photo_shoot_type));
            if (response_catalog.getPhotoshoot_type() != null) {
                int selection_index = photoshoot_array.indexOf(response_catalog.getPhotoshoot_type());
                if (selection_index != -1)
                    spinner_photoshoot.setSelection(selection_index);
            }
        } else if (response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            radio_setmatching.setChecked(true);
        }
        category_id = response_catalog.getCategory();
        auto_text_catalog.setText(response_catalog.getCatalog_title());
        getCategoryEavAttribute(category_id);
        String temp_text = editCatalogEavValue(edit_catalog, "other");
        if (temp_text != null && !temp_text.isEmpty()) {
            edit_other_eav.setText(temp_text);
        }

        if (response_catalog.getDispatch_date() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            myCalendar.setTime(DateUtils.stringToDate(response_catalog.getDispatch_date(), sdf));
            updateDispatch();
        }


        // Fill Step-Two Data
        setStepTwoListener();
        StaticFunctions.loadImage(getActivity(), response_catalog.getImage().getThumbnail_small(), img_cover, R.drawable.uploadempty);
        if (response_catalog.isI_am_selling_sell_full_catalog()) {
            radio_full_catalog.setChecked(true);
            radio_single_piece_catalog.setChecked(false);
        } else {
            radio_single_piece_catalog.setChecked(true);
            radio_full_catalog.setChecked(false);

            if (productMyDetail != null) {
                if (productMyDetail.getSingle_piece_price_fix() > 0) {
                    radio_common_price.setChecked(true);
                    edit_common_add_margin.setText("" + productMyDetail.getSingle_piece_price_fix());
                } else {
                    radio_common_per.setChecked(true);
                    edit_common_add_margin.setText("" + productMyDetail.getSingle_piece_price_percentage());
                }
            }
        }


        // set catalog level sizes
        if (response_catalog.getAvailable_sizes() != null && !response_catalog.getAvailable_sizes().isEmpty()) {
            if (catalog_level_size_eav == null) {
                catalog_level_size_eav = new ArrayList<>();
            }
            catalog_level_size_eav.addAll(StaticFunctions.stringToArray(response_catalog.getAvailable_sizes(), StaticFunctions.COMMASEPRATED));
        }

        edit_no_of_design.setText("" + response_catalog.getProduct().length);


        if (response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_CAT)
                || response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_NON)) {
            boolean temp_is_same_price = true;
            ArrayList<String> temp_price = new ArrayList<>();
            for (int index = 0; index < response_catalog.getProduct().length; index++) {

                if (product_image == null) {
                    product_image = new ArrayList<>();
                }
                Image image = new Image();
                image.setPath(response_catalog.getProduct()[index].getImage().getThumbnail_small());
                image.setName(response_catalog.getProduct()[index].getSku());
                if (response_catalog.getProduct()[index].getMwp_price() > 0) {
                    image.setPublic_price("" + response_catalog.getProduct()[index].getMwp_price());
                    image.setPrice(String.valueOf(response_catalog.getProduct()[index].getMwp_price()));
                    if(index > 0) {
                        if (temp_is_same_price && !temp_price.contains(String.valueOf(response_catalog.getProduct()[index].getMwp_price()))) {
                            temp_is_same_price = false;
                        }
                    }

                    temp_price.add(String.valueOf(response_catalog.getProduct()[index].getMwp_price()));

                } else {
                    image.setPublic_price(response_catalog.getProduct()[index].getFinal_price());
                    image.setPrice(response_catalog.getProduct()[index].getFinal_price());

                    if(index > 0)
                    if (temp_is_same_price && !temp_price.contains(response_catalog.getProduct()[index].getFinal_price())) {
                        temp_is_same_price = false;
                    }
                    temp_price.add(String.valueOf(response_catalog.getProduct()[index].getFinal_price()));
                }

                if(productMyDetail!=null) {
                    if(!productMyDetail.isI_am_selling_sell_full_catalog()) {
                        image.setDisabled(!productMyDetail.getProducts().get(index).isCurrently_selling());
                    }
                }

                image.setPhoto_id(response_catalog.getProduct()[index].getId());
                if (response_catalog.getProduct()[index].getAvailable_size_string() != null && !response_catalog.getProduct()[index].getAvailable_size_string().isEmpty())
                    image.setAvailable_sizes(StaticFunctions.stringToArray(response_catalog.getProduct()[index].getAvailable_size_string(), StaticFunctions.COMMASEPRATED));




                product_image.add(image);

            }

            if (!temp_is_same_price)
                check_same_price.setChecked(false);
            else
                check_same_price.setChecked(true);

            addProductAdapter = new AddProductAdapter2(getActivity(), product_image,
                    radio_full_catalog.isChecked(),
                    Fragment_AddCatalog_Version2.this,
                    check_same_price.isChecked(), recycler_products, radio_catalog.isChecked());
            addProductAdapter.setChangeProductListener(this);
            if (!radio_full_catalog.isChecked()) {
                if (!edit_common_add_margin.getText().toString().isEmpty()) {
                    if (radio_common_per.isChecked()) {
                        addProductAdapter.singlePCAddPer = edit_common_add_margin.getText().toString();
                    } else {
                        addProductAdapter.singlePcAddPrice = edit_common_add_margin.getText().toString();
                    }
                }
            }
            recycler_products.setAdapter(addProductAdapter);
        }

    }

    public String editCatalogEavValue(final Response_catalog response_catalog, String eav_key) {
        ArrayList<String> lengths = new ArrayList<>();
        if (edit_catalog != null && response_catalog.getEavdatajson() != null) {
            for (Iterator iterator = response_catalog.getEavdatajson().keySet().iterator(); iterator.hasNext(); ) {
                String key = (String) iterator.next();
                String valueString = null;
                if (response_catalog.getEavdatajson().get(key) instanceof JsonArray) {
                    valueString = StaticFunctions.JsonArrayToString((JsonArray) response_catalog.getEavdatajson().get(key), StaticFunctions.COMMASEPRATEDSPACE);
                } else {
                    valueString = response_catalog.getEavdatajson().get(key).toString().replaceAll("\"", "");
                }
                if (key != null) {
                    key = StaticFunctions.formatErrorTitle(key);
                }

                if (key.equalsIgnoreCase(eav_key)) {
                    Log.e(TAG, "editCatalogEavValue: Key====>" + key + "\n=======> Value String For" + valueString);
                    return valueString;
                }
            }
        }
        return null;
    }

    private void callSoftDelete(final int adapterPosition, String product_id) {
        String url = URLConstants.companyUrl(getActivity(), "productsonly", "") + product_id + "/";
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

        HashMap<String, String> params = new HashMap<>();
        params.put("deleted", "true");


        if (!StaticFunctions.isOnline(getActivity())) {
            HttpManager.showNetworkAlert(getActivity());
            return;
        }

        final MaterialDialog progressDialog = StaticFunctions.showProgress(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.show();
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, url, Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(params), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }

                if (addProductAdapter != null) {
                    addProductAdapter.getProducts().remove(adapterPosition);
                    addProductAdapter.notifyDataSetChanged();

                    if (!edit_no_of_design.getText().toString().trim().isEmpty()) {
                        edit_no_of_design.setText(String.valueOf((Integer.parseInt(edit_no_of_design.getText().toString()) - 1)));
                    }
                }

                Toast.makeText(getActivity(), "Product Successfully deleted", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                btn_continue.setEnabled(true);
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    @Override
    public void softDelete(int position, Image oldImage) {
        callSoftDelete(position, oldImage.getPhoto_id());
    }

    private void stageUpdateExistingProduct() {
        Log.e(TAG, "stageUpdateExistingProduct ======>");
        dialogCatalogUpload = new MaterialDialog.Builder(getContext()).build();
        dialogCatalogUpload.setTitle("2. Updating Products.....");
        dialogCatalogUpload.setCancelable(false);
        dialogCatalogUpload.show();
        BeforeTime = System.currentTimeMillis();
        TotalTxBeforeTest = TrafficStats.getTotalTxBytes();
        HashMap<String, String> update_product_successcount = new HashMap<>();
        ArrayList<Image> updateProductArraylist = new ArrayList<>();
        ArrayList<ResponseAddProduct> oldPoductsArraylist = new ArrayList<>();
        handlerPostData = new Handler();

        runnablePostData = new Runnable() {
            public void run() {
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
                handlerPostData.postDelayed(this, 1000);
            }
        };
        boolean isTwoSizeSame = true;
        handlerPostData.postDelayed(runnablePostData, 1000);
        if (isEditMode) {
            ArrayList<Image> temp = new ArrayList<>();
            temp.addAll(product_image);
            for (int index = 0; index < temp.size(); index++) {
                if (temp.get(index).getPhoto_id() != null) {
                    if (radio_full_catalog.isChecked()) {
                        if (edit_catalog.getAvailable_sizes() != null && catalog_level_size_eav!=null) {
                            isTwoSizeSame = StaticFunctions.compareTwoStringArrayList(StaticFunctions.stringToArray(edit_catalog.getAvailable_sizes(), StaticFunctions.COMMASEPRATED), catalog_level_size_eav);
                            Log.e(TAG, "AnyChangesSize Catalog Level=====> " + "\n Value===>" + isTwoSizeSame);
                        }
                    } else {
                        for (ProductObj product :
                                edit_catalog.getProduct()) {
                            if (product.getAvailable_size_string() != null && !product.getAvailable_size_string().isEmpty()) {
                                isTwoSizeSame = StaticFunctions.compareTwoStringArrayList(StaticFunctions.stringToArray(product.getAvailable_size_string(), StaticFunctions.COMMASEPRATED), temp.get(index).getAvailable_sizes());
                                Log.e(TAG, "AnyChangesSize=====> " + product.getId() + "\n Value===>" + isTwoSizeSame);
                                if (!isTwoSizeSame) {
                                    break;
                                }
                            }
                        }
                    }
                    if(edit_catalog.getProduct()!=null) {
                        if (!isTwoSizeSame || !edit_catalog.getProduct()[index].getSku().equalsIgnoreCase(temp.get(index).getName()) || !String.valueOf(edit_catalog.getProduct()[index].getMwp_price()).equalsIgnoreCase(temp.get(index).getPrice()))
                            updateProductArraylist.add(temp.get(index));
                    }
                }
            }
        }

        if (isEditMode && updateProductArraylist.size() == 0) {
            Log.e(TAG, "stageUpdateExistingProduct: Size is 0 (No Update)====>");
            hideUploadDialog();
            stageProductUpload(false);
        } else {

            StartStopHandler.STARTSTOP enum_type  = StartStopHandler.STARTSTOP.SINGLE_WITHOUT_SIZE;
            if(isSizeAvailable) {
                enum_type = StartStopHandler.STARTSTOP.SINGLE_WITH_SIZE;
            }
            StartStopHandler startStopHandler = new StartStopHandler(getActivity());
            startStopHandler.setStartStopDoneListener(new StartStopHandler.StartStopDoneListener() {
                @Override
                public void onSuccessStart() {
                    stageProductUpload(true);
                }

                @Override
                public void onSuccessStop() {

                }

                @Override
                public void onError() {
                    hideUploadDialog();
                }
            });



            List<ProductObj> productObjs = new ArrayList<>();
            for (int index = 0; index < updateProductArraylist.size(); index++) {
                final Image image = updateProductArraylist.get(index);
                ProductObj productObj = new ProductObj();
                productObj.setId(image.getPhoto_id());
                productObj.setSku(image.name);
                productObj.setMwp_price(Double.parseDouble(image.price));
                productObj.setAvailable_sizes(image.getAvailable_sizes());
                if(isSizeAvailable) {
                    if(radio_full_catalog.isChecked()) {
                        productObj.setAvailable_sizes(catalog_level_size_eav);
                    }
                }
                if(!isSizeAvailable && !image.isDisabled)
                    productObj.setIs_enable(true);

                productObjs.add(productObj);
            }
            startStopHandler.updateProduct(enum_type,productObjs,true);
        }


    }

    private boolean observalUpdateProductsSuccess(HashMap<String, String> update_product_successcount, ArrayList<Image> updateProductObj) {
        boolean isSuccess = false;
        if (update_product_successcount != null && updateProductObj != null) {
            for (int i = 0; i < updateProductObj.size(); i++) {
                if (update_product_successcount.containsKey(updateProductObj.get(i).getPhoto_id())) {
                    isSuccess = true;
                } else {
                    isSuccess = false;
                    return isSuccess;
                }
            }
            return isSuccess;
        }
        return isSuccess;
    }

    private void getBrandsDiscountRule(String brandID) {
        if (brandID != null && !brandID.equalsIgnoreCase("-1")) {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            showProgress();
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.DISCOUNT_RULE + "?is_allow_to_upload_catalog_for_brand=" + brandID + "&expand=true", null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    if (isAdded() && !isDetached()) {
                        boolean isDiscoutAvailable;
                        try {
                            ArrayList<ActivityBrandwiseDiscountList> responseData = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<ResponseBrandDiscountExpand>>() {
                            }.getType());
                            if (responseData.size() > 0) {
                                add_step = add_step + 1;
                                setStepTwo();
                                btn_continue.setText("Submit");
                                if (isEditMode) {
                                    btn_continue.setText("Update Product");
                                }
                            } else {
                                new MaterialDialog.Builder(getActivity())
                                        .title("You have not set any discount for this brand")
                                        .content("If you want to continue to upload a catalog under this brand, need to set discount first")
                                        .positiveText("OK")
                                        .negativeText("CANCEL")
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                                dialog.dismiss();
                                                StaticFunctions.switchActivity(getActivity(), ActivityBrandwiseDiscountList.class);
                                            }
                                        })
                                        .show();
                            }

                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });
        }
    }
}
