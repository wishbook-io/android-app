package com.wishbook.catalog.home.catalog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.CatalogsAdapter;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.catalog.details.Fragment_Manage_Images;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Activity_QualityCatalog extends AppCompatActivity implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.search_toolbar)
    Toolbar search_toolbar;

    @BindView(R.id.search_view)
    SearchView search_view;

    @BindView(R.id.img_searchclose)
    ImageView img_searchclose;

    @BindView(R.id.btn_add)
    AppCompatButton btn_add;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.linear_empty_view)
    LinearLayout linear_empty_view;

    private SwipeRefreshLayout swipe_container;

    private Context context;
    private RecyclerView.LayoutManager mLayoutManager;
    CatalogsAdapter mAdapter;

    private String catalog_id;

    HashMap<String, String> paramsClone = null;

    public static final int ADD_EDIT_SCREEN_SET = 1602;

    boolean isFilterPopupshow;
    PopupMenu popupMenu;

    boolean isEnableFilter;

    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;
    boolean isFirsttime = false;

    private static String TAG = Activity_QualityCatalog.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = Activity_QualityCatalog.this;
        StaticFunctions.initializeAppsee();
        setContentView(R.layout.activity_catalog_quality);
        ButterKnife.bind(this);
        isEnableFilter = true;
        setToolbar();
        initView();
        initSearchBar();
        initSwipeRefresh();

    }


    private void setToolbar() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        toolbar.setTitleTextColor(Color.WHITE);
        if (getIntent().getStringExtra("catalog_title") != null) {
            toolbar.setTitle(getIntent().getStringExtra("catalog_title"));
        }
        setSupportActionBar(toolbar);

        search_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        img_searchclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.setVisibility(View.VISIBLE);
                search_toolbar.setVisibility(View.GONE);
            }
        });


    }

    private void initSearchBar() {
        final SearchView.SearchAutoComplete searchTextView = (SearchView.SearchAutoComplete) search_view.findViewById(R.id.search_src_text);
        searchTextView.setHintTextColor(getResources().getColor(R.color.purchase_light_gray));
        search_view.setIconifiedByDefault(false);
        search_view.setOnQueryTextListener(this);
        search_view.setVisibility(View.VISIBLE);
        search_view.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View view, boolean hasFocus) {
                if (hasFocus) {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(view.findFocus(), 0);
                        }
                    }, 200);
                }
            }
        });

        img_searchclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.setVisibility(View.VISIBLE);
                search_toolbar.setVisibility(View.GONE);
                search_view.clearFocus();
                KeyboardUtils.hideKeyboard((Activity) context);
            }
        });
    }

    private void initView() {
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        initCall();
    }

    private void initCall() {
        if (getIntent().getStringExtra("catalog_id") != null) {
            catalog_id = getIntent().getStringExtra("catalog_id");
            if(!isFirsttime) {
                String supplier_approval_status = UserInfo.getInstance(context).getSupplierApprovalStatus();
                if (supplier_approval_status != null) {
                    if (supplier_approval_status.equals(Constants.SELLER_APPROVAL_PENDING)
                            || supplier_approval_status.equals(Constants.SELLER_APPROVAL_REJECTED)
                            || supplier_approval_status.equals(Constants.SELLER_APPROVAL_FIRSTTIME_UPLOAD)) {
                        isEnableFilter = false;
                        invalidateOptionsMenu();
                    }
                }
            }

            isFirsttime = true;
            getCatalogsQuality(getIntent().getStringExtra("catalog_id"), null);
        }
    }

    public void initSwipeRefresh() {
        swipe_container = findViewById(R.id.swipe_container);
        swipe_container.setOnRefreshListener(this);
        swipe_container.setColorScheme(android.R.color.holo_blue_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isEnableFilter) {
            menu.findItem(R.id.action_filter).setIcon(R.drawable.ic_filter_enable).setTitle("Enable");
        } else {
            menu.findItem(R.id.action_filter).setIcon(R.drawable.ic_filter_disable).setTitle("Disabled");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_quality_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        } else if (item.getItemId() == R.id.action_search) {
            toolbar.setVisibility(View.GONE);
            search_toolbar.setVisibility(View.VISIBLE);
        } else if (item.getItemId() == R.id.action_filter) {
            openMyProductFilterPopup(toolbar);
        }
        return super.onOptionsItemSelected(item);
    }



    private void getCatalogsQuality(String catalog_id, HashMap<String, String> params) {
        StaticFunctions.showProgressbar(Activity_QualityCatalog.this);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(Activity_QualityCatalog.this);
        String url = null;
        url = URLConstants.companyUrl(context, "catalogs", "") + "?catalog_id=" + catalog_id;

        if (params == null) {
            params = new HashMap<>();
            if(isEnableFilter){
                params.remove("buyer_disabled");
            } else {
                params.put("buyer_disabled", "true");
            }

        }

        params.put("view_type","mycatalogs");



        HttpManager.getInstance(Activity_QualityCatalog.this).request(HttpManager.METHOD.GETWITHPROGRESS, url, params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                StaticFunctions.hideProgressbar(Activity_QualityCatalog.this);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    StaticFunctions.hideProgressbar(Activity_QualityCatalog.this);
                    final Response_catalogMini[] response_catalogMinis = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                    if (response_catalogMinis.length > 0) {
                        linear_empty_view.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        final ArrayList<Response_catalogMini> catalogMinis = new ArrayList<Response_catalogMini>(Arrays.asList(response_catalogMinis));
                        mAdapter = new CatalogsAdapter((AppCompatActivity) context, catalogMinis, null, true, 1);
                        mRecyclerView.setAdapter(mAdapter);
                    } else {
                        linear_empty_view.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.hideProgressbar(Activity_QualityCatalog.this);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    private void openMyProductFilterPopup(View view) {
        if (isFilterPopupshow && popupMenu != null) {
            popupMenu.dismiss();
            isFilterPopupshow = false;
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            popupMenu = new PopupMenu(context, view, Gravity.RIGHT);
        } else {
            popupMenu = new PopupMenu(context, view);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_enable) {
                    if (paramsClone != null) {
                        if (paramsClone.containsKey("buyer_disabled"))
                            paramsClone.remove("buyer_disabled");

                    } else {
                        paramsClone = new HashMap<>();
                        paramsClone.remove("buyer_disabled");
                    }
                    getCatalogsQuality(catalog_id, paramsClone);
                    isEnableFilter = true;


                } else if (item.getItemId() == R.id.action_disable) {
                    if (paramsClone != null) {
                        paramsClone.put("buyer_disabled", "true");
                    } else {
                        paramsClone = new HashMap<>();
                        paramsClone.put("buyer_disabled", "true");
                    }
                    isEnableFilter = false;
                    getCatalogsQuality(catalog_id, paramsClone);
                }

                isFilterPopupshow = false;
                invalidateOptionsMenu();
                return true;
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener()

        {
            @Override
            public void onDismiss(PopupMenu popupMenu) {
                isFilterPopupshow = false;
            }
        });

        popupMenu.inflate(R.menu.menu_my_catalog_popup);
        if (!isFilterPopupshow)

        {
            popupMenu.show();
            isFilterPopupshow = true;
        }

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        HashMap<String, String> params = new HashMap<>();
        params.put("search", newText);
        if (paramsClone != null) {
            paramsClone.clear();
        }
        params.put("view_type", "mycatalogs");
        params.put("product_type", Constants.PRODUCT_TYPE_SCREEN);
        paramsClone = params;
        getCatalogsQuality(catalog_id, params);
        return false;
    }


    @OnClick(R.id.btn_add)
    public void addCatalog() {
        Bundle bundle = new Bundle();
        bundle.putString("product_id", null);
        bundle.putString("catalog_id", catalog_id);
        if (getIntent().getStringExtra("set_type") != null) {
            bundle.putString("set_type", getIntent().getStringExtra("set_type"));
        } else {
            bundle.putString("set_type", "color_set");
        }
        bundle.putBoolean("isEdit", false);
        Fragment_Manage_Images fragment_manage_images = new Fragment_Manage_Images();
        fragment_manage_images.setArguments(bundle);
        Application_Singleton.CONTAINER_TITLE = "Add set";
        Application_Singleton.CONTAINERFRAG = fragment_manage_images;
        Intent shared_by_me_intent = new Intent(context, OpenContainer.class);
        startActivityForResult(shared_by_me_intent, ADD_EDIT_SCREEN_SET);
    }

    @Override
    public void onRefresh() {
        runnable = new Runnable() {
            @Override
            public void run() {
                isAllowCache = false;
                swipe_container.setRefreshing(false);
                toolbar.setVisibility(View.VISIBLE);
                search_toolbar.setVisibility(View.GONE);
                search_view.clearFocus();
                KeyboardUtils.hideKeyboard((Activity) context);
                initCall();
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
