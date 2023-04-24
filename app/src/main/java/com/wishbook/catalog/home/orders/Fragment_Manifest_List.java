package com.wishbook.catalog.home.orders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.paginate.Paginate;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.imagecropper.PhotoTaker;
import com.wishbook.catalog.Utils.imagecropper.PhotoTakerUtils;
import com.wishbook.catalog.Utils.multipleimageselect.activities.AlbumSelectActivity;
import com.wishbook.catalog.Utils.multipleimageselect.models.Image;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.ResponseManifestList;
import com.wishbook.catalog.commonmodels.responses.SellerInvoice_images;
import com.wishbook.catalog.home.sellerhub.SellerManifestAPIHandler;
import com.wishbook.catalog.home.sellerhub.adapter.ManifestListAdapter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Fragment_Manifest_List extends GATrackedFragment implements
        Paginate.Callbacks, ManifestListAdapter.SellerManifestItemChangeListener,
        SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    /// ################# Variable Initialize ################################


    private String filter;
    private View view;
    private RecyclerViewEmptySupport mRecyclerView;
    private SearchView searchView;
    private String status = "";
    RelativeLayout relativeProgress;

    private SwipeRefreshLayout swipe_container;
    Handler handler;
    Runnable runnable;
    boolean isAllowCache = true;

    Spinner spinner;
    ImageView img_searchclose;
    LinearLayout linear_searchview;
    RelativeLayout relative_top_bar;
    boolean isFromPlaceOrder;
    LinearLayout linear_select_date_range, linear_order_date_container;
    TextView txt_selected_date,status_label;
    String default_from_date, default_to_date;
    String order_date_range_type;

    ManifestListAdapter manifestListAdapter;
    ArrayList<ResponseManifestList> responseManifestLists;

    //Pagination Variable
    HashMap<String, String> paramsClone = null;
    private static int LIMIT = 10;
    private static int INITIAL_OFFSET = 0;
    public boolean Loading = false;
    public boolean hasLoadedAllItems = false;
    Paginate paginate;
    int page;

    boolean isCamera = false;
    int islimit = Constants.UPLOAD_SELLER_INVOICE_LIMIT;

    public Fragment_Manifest_List() {

    }

    @SuppressLint("ValidFragment")
    public Fragment_Manifest_List(String filter, boolean isFromPlaceOrder) {
        // Required empty public constructor
        this.filter = filter;
        this.isFromPlaceOrder = isFromPlaceOrder;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manifest_list, container, false);
        if (isFromPlaceOrder) {
            isAllowCache = false;
        }
        if (getActivity() instanceof ActivityOrderHolder) {
            setHasOptionsMenu(true);
        }
        responseManifestLists = new ArrayList<>();
        initTopBar();

        relativeProgress = (RelativeLayout) view.findViewById(R.id.relative_progress);
        mRecyclerView = (RecyclerViewEmptySupport) view.findViewById(R.id.recycler_view);
        mRecyclerView.setEmptyView(view.findViewById(R.id.list_empty1));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        manifestListAdapter = new ManifestListAdapter(getActivity(), responseManifestLists, Fragment_Manifest_List.this);
        mRecyclerView.setAdapter(manifestListAdapter);
        manifestListAdapter.setSellerManifestItemChangeListener(this);
        bindAgainPaginate();

        searchView = (SearchView) view.findViewById(R.id.search_view);
        final SearchView.SearchAutoComplete searchTextView = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchTextView.setHintTextColor(getResources().getColor(R.color.purchase_light_gray));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setVisibility(View.VISIBLE);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View view, boolean hasFocus) {
                if (hasFocus) {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(view.findFocus(), 0);
                        }
                    }, 200);
                }
            }
        });

        initSwipeRefresh(view);

        return view;
    }

    public void initTopBar() {
        spinner = view.findViewById(R.id.spinner);
        img_searchclose = view.findViewById(R.id.img_searchclose);
        linear_searchview = view.findViewById(R.id.linear_searchview);
        relative_top_bar = view.findViewById(R.id.relative_top_bar);
        linear_select_date_range = view.findViewById(R.id.linear_select_date_range);
        linear_order_date_container = view.findViewById(R.id.linear_order_date_container);
        txt_selected_date = view.findViewById(R.id.txt_selected_date);
        status_label = view.findViewById(R.id.status_label);
        status_label.setText("Manifest Status");
        setDateRange();
        // Initializing a String Array
        String[] order_status = new String[]{
                "Created",
                "Closed"
        };

        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                getActivity(), R.layout.order_spinner_item, order_status
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinneritem_new);
        spinner.setAdapter(spinnerArrayAdapter);

        img_searchclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ActivityOrderHolder)
                    //  ((ActivityOrderHolder) getActivity()).toolbar.setVisibility(View.VISIBLE);
                    relative_top_bar.setVisibility(View.VISIBLE);
                linear_order_date_container.setVisibility(View.VISIBLE);
                linear_searchview.setVisibility(View.GONE);
                relative_top_bar.setVisibility(View.VISIBLE);
                searchView.setQuery("", false);
                searchView.clearFocus();
                spinner.setSelection(0);
                KeyboardUtils.hideKeyboard(getActivity());
            }
        });

        if (filter != null) {
            if (filter.equals("CREATED")) {
                spinner.setSelection(0);
            } else if (filter.equals("CLOSED")) {
                spinner.setSelection(1);
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    filter = "CREATED";
                    paramsClone = null;
                    initCall(filter);
                } else if (i == 1) {
                    filter = "CLOSED";
                    paramsClone = null;
                    initCall(filter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        linear_select_date_range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BottomOrderDateRange bottomOrderDateRange = BottomOrderDateRange.newInstance(null);
                bottomOrderDateRange.setDismissListener(new BottomOrderDateRange.DismissListener() {
                    @Override
                    public void onDismiss(String from_date, String to_date, String displayText) {
                        default_from_date = from_date;
                        default_to_date = to_date;
                        if (displayText.contains(";")) {
                            String[] split = displayText.split(";");
                            if (split.length == 2) {
                                default_from_date = split[0];
                                default_to_date = split[1];
                                txt_selected_date.setText("Start " + split[0] + " - " + "End " + split[1]);
                            }
                        } else {
                            if (displayText.equalsIgnoreCase("All Order")) {
                                displayText = "All";
                            }
                            txt_selected_date.setText(displayText);
                        }

                        paramsClone = null;
                        initCall(filter);
                    }
                });
                bottomOrderDateRange.show(getFragmentManager(), "SelectRange");
            }
        });
    }

    public void initCall(String status) {
        if (paramsClone == null) {
            paramsClone = new HashMap<>();
            paramsClone.put("limit", String.valueOf(LIMIT));
            paramsClone.put("offset", String.valueOf(INITIAL_OFFSET));
            if (status != null && !status.isEmpty()) {
                paramsClone.put("status", status);
            }
        }
        getManifestList(paramsClone);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 900) {
            if (grantResults.length > 0) {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openPhotoUploadWidget(isCamera,islimit);
                    isCamera = false;
                    islimit = 0;
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Camera/Storage Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }


    public  void openTaker(Context context,int limit) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(context);
        ArrayList<String> array = new ArrayList<String>();
        array.add("Camera");
        array.add("Gallery");
        array.add("Cancel");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item, array);
        dlg.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = null;
                switch (which) {
                    case 0:
                        openPhotoUploadWidget(true,limit);
                        break;
                    case 1:
                        openPhotoUploadWidget(false,limit);
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                }
            }
        });
        dlg.setCancelable(true);
        dlg.show();
    }

    private void openPhotoUploadWidget(boolean isCamera, int limit) {
        this.isCamera = isCamera;
        this.islimit = limit;
        String[] permissions = {
                "android.permission.CAMERA",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, 900);
        } else {
            if(isCamera) {
                Intent i = new Intent(getActivity(), PhotoTaker.class);
                i.putExtra("mode", "camera");
                i.putExtra("filename", "manifestpreview.jpg");
                i.putExtra("isCoverImage",false);
                i.putExtra("withoutcrop",true);
                Fragment_Manifest_List.this.startActivityForResult(i,Application_Singleton.CAMERA_IMAGE_REQUEST_CODE);
            } else {
                Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
                intent.putExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, limit);
                Fragment_Manifest_List.this.startActivityForResult(intent, Application_Singleton.MULTIIMAGE_PRODUCT_REQUEST_CODE);
            }
        }
    }



    public void bindAgainPaginate() {
        if (paginate != null) {
            paginate.unbind();
        }
        paginate = Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(4)
                .addLoadingListItem(true)
                .build();
    }


    @Override
    public void onLoadMore() {
        Loading = true;
        if (page > 0) {
            if (paramsClone == null) {
                HashMap<String, String> params = new HashMap<>();
                params.put("limit", String.valueOf(LIMIT));
                params.put("offset", String.valueOf(page * LIMIT));
                params.put("status", filter);
                getManifestList(params);
            } else {
                paramsClone.put("limit", String.valueOf(LIMIT));
                paramsClone.put("offset", String.valueOf(page * LIMIT));
                paramsClone.put("status", filter);
                getManifestList(paramsClone);
            }
        }

    }

    @Override
    public boolean isLoading() {
        return Loading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return hasLoadedAllItems;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
       /* if (manifestListAdapter != null) {
            getManifestList("CREATED");
        }*/
        return true;
    }


    public void showProgressList() {
        if (relativeProgress != null) {
            relativeProgress.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressList() {
        if (relativeProgress != null) {
            relativeProgress.setVisibility(View.GONE);
        }
    }


    public void initSwipeRefresh(View view) {
        swipe_container = view.findViewById(R.id.swipe_container);
        swipe_container.setOnRefreshListener(this);
        swipe_container.setColorScheme(android.R.color.holo_blue_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onRefresh() {
        runnable = new Runnable() {
            @Override
            public void run() {
                isAllowCache = false;
                searchView.setQuery("", false);
                searchView.clearFocus();
                linear_searchview.setVisibility(View.GONE);
                relative_top_bar.setVisibility(View.VISIBLE);
                paramsClone = null;
                initCall(filter);
                swipe_container.setRefreshing(false);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);

    }


    public void setDateRange() {
        if (order_date_range_type != null) {
            Date today = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            switch (order_date_range_type.toLowerCase()) {
                case "all orders":
                    txt_selected_date.setText("All");
                    default_from_date = null;
                    default_to_date = null;
                    break;
                case "today":
                    txt_selected_date.setText("Today");
                    default_from_date = formatter.format(today);
                    default_to_date = formatter.format(today);
                    break;
                case "yesterday": {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(today);
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    Date from_date = cal.getTime();
                    default_from_date = formatter.format(from_date);
                    default_to_date = formatter.format(from_date);
                    txt_selected_date.setText("Yesterday");
                }
                break;
                case "last 7 days": {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(today);
                    cal.add(Calendar.DAY_OF_MONTH, -7);
                    Date from_date = cal.getTime();
                    default_from_date = formatter.format(from_date);
                    default_to_date = formatter.format(today);
                    txt_selected_date.setText("Last 7 Days");
                }
                break;
                case "this month": {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(today);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    Date from_date = cal.getTime();
                    default_from_date = formatter.format(from_date);
                    default_to_date = formatter.format(today);
                    txt_selected_date.setText("This Month");
                }
                break;
                default:
                    if (order_date_range_type.contains(";")) {
                        String[] split = order_date_range_type.split(";");
                        if (split.length == 2) {
                            default_from_date = split[0];
                            default_to_date = split[1];
                            txt_selected_date.setText("Start " + split[0] + " - " + "End " + split[1]);
                        }
                    }
                    break;

            }
        } else {
            txt_selected_date.setText("All");
        }
    }


    @Override
    public void onUpdate(String manifest_id, int position) {
        getUpdateSingleManifest(manifest_id, position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == Application_Singleton.MULTIIMAGE_PRODUCT_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            clearFocus();
            ArrayList<Image> temp = data.getParcelableArrayListExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_IMAGES);
            if (temp.size() > 0 && responseManifestLists != null) {
                ResponseManifestList invoiceResponse = responseManifestLists.get(ManifestListAdapter.ADD_MORE_IMAGE_POSITION);
                ArrayList<SellerInvoice_images> temp1;
                if (responseManifestLists.get(ManifestListAdapter.ADD_MORE_IMAGE_POSITION).getImages() == null) {
                    temp1 = new ArrayList<>();
                } else {
                    temp1 = responseManifestLists.get(ManifestListAdapter.ADD_MORE_IMAGE_POSITION).getImages();
                }
                for (int i = 0; i < temp.size(); i++) {
                    SellerInvoice_images sellerInvoice_images = new SellerInvoice_images();
                    sellerInvoice_images.setPath(temp.get(i).getPath());
                    temp1.add(sellerInvoice_images);
                }
                invoiceResponse.setEditMode(true);
                invoiceResponse.setImages(temp1);
                if (manifestListAdapter != null) {
                    manifestListAdapter.notifyItemChanged(ManifestListAdapter.ADD_MORE_IMAGE_POSITION);
                }
            }
        } else {
             if(resultCode == Application_Singleton.CAMERA_IMAGE_RESPONSE_CODE && data != null) {
                 Log.e("TAG", "onActivityResult: =====> Camara Request code" );
                 clearFocus();
                 if (data.getData()!=null && responseManifestLists != null) {
                     Log.e("TAG", "onActivityResult: =====> URI not null" );
                     Uri mSaveUri = data.getData();
                     ResponseManifestList invoiceResponse = responseManifestLists.get(ManifestListAdapter.ADD_MORE_IMAGE_POSITION);
                     ArrayList<SellerInvoice_images> temp1;
                     if (responseManifestLists.get(ManifestListAdapter.ADD_MORE_IMAGE_POSITION).getImages() == null) {
                         temp1 = new ArrayList<>();
                     } else {
                         temp1 = responseManifestLists.get(ManifestListAdapter.ADD_MORE_IMAGE_POSITION).getImages();
                     }
                     SellerInvoice_images sellerInvoice_images = new SellerInvoice_images();
                     Log.e("TAG", "onActivityResult: "+data.getStringExtra("real_path"));
                     Uri uri;
                     File mDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/wishbooks/");
                     File output = new File(mDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
                     try {
                         File src = new File(data.getStringExtra("real_path"));
                         StaticFunctions.copy(src,output);
                         src.delete();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                     if (output.exists()) {
                         Log.v("selected image exits", output.getAbsolutePath());
                     }
                     Log.e("TAG", "onActivityResult: 1 "+output.getPath());
                     sellerInvoice_images.setPath(output.getPath());
                     temp1.add(sellerInvoice_images);
                     invoiceResponse.setEditMode(true);
                     invoiceResponse.setImages(temp1);
                     if (manifestListAdapter != null) {
                         manifestListAdapter.notifyItemChanged(ManifestListAdapter.ADD_MORE_IMAGE_POSITION);
                     }
                 }
             }
        }
    }

    public void clearFocus() {
        KeyboardUtils.hideKeyboard(getActivity());
        View currentFocus = (getActivity()).getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }

        if (SellerManifestAPIHandler.apiHandlerListener != null) {
            SellerManifestAPIHandler.apiHandlerListener.clear();
            SellerManifestAPIHandler.apiHandlerListener = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /// ##################### API CALLING ##############################################//

    private void getManifestList(HashMap<String, String> params) {
        if (params.containsKey("offset")) {
            if (params.get("offset").equals("0")) {
                page = 0;
                responseManifestLists.clear();
                if (manifestListAdapter != null) {
                    manifestListAdapter.notifyDataSetChanged();
                }
                hasLoadedAllItems = false;
            }
        } else {
            params.put("offset", "0");
            page = 0;
            responseManifestLists.clear();
            if (manifestListAdapter != null) {
                manifestListAdapter.notifyDataSetChanged();
            }
            hasLoadedAllItems = false;
        }
        if (default_from_date != null && default_to_date != null) {
            String from_date_string = DateUtils.changeDateFormat(StaticFunctions.CLIENT_DISPLAY_FORMAT3, StaticFunctions.SERVER_POST_FORMAT, default_from_date);
            String to_date_string = DateUtils.changeDateFormat(StaticFunctions.CLIENT_DISPLAY_FORMAT3, StaticFunctions.SERVER_POST_FORMAT, default_to_date);
            params.put("from_date", from_date_string);
            params.put("to_date", to_date_string);
        }
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        if (page == 0) {
            showProgressList();
        }
        String url = URLConstants.companyUrl(getActivity(), "manifests", "");
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, params, headers, isAllowCache, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgressList();
                if (isAdded() && !isDetached()) {
                    Loading = false;
                    final int offset = Integer.parseInt(params.get("offset"));
                    Type type = new TypeToken<ArrayList<ResponseManifestList>>() {
                    }.getType();
                    final ArrayList<ResponseManifestList> tempArrayList = Application_Singleton.gson.fromJson(response, type);
                    if (tempArrayList.size() > 0) {
                        responseManifestLists.addAll(tempArrayList);
                        tempArrayList.clear();
                        page = (int) Math.ceil((double) responseManifestLists.size() / LIMIT);
                        if (tempArrayList.size() % LIMIT != 0) {
                            hasLoadedAllItems = true;
                        }
                        if (responseManifestLists.size() <= LIMIT) {
                            manifestListAdapter.notifyDataSetChanged();
                        } else {
                            try {
                                mRecyclerView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        manifestListAdapter.notifyItemRangeChanged(offset, responseManifestLists.size());
                                    }
                                });
                            } catch (Exception e) {
                                manifestListAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        hasLoadedAllItems = true;
                        manifestListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgressList();
            }
        });
    }

    private void getUpdateSingleManifest(String manifest_id, int position) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgressList();
        String url = URLConstants.companyUrl(getActivity(), "manifests", "") + manifest_id + "/";
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgressList();
                if (isAdded() && !isDetached()) {
                    Type type = new TypeToken<ResponseManifestList>() {
                    }.getType();
                    try {
                        if (responseManifestLists != null && responseManifestLists.size() > 0) {
                            ResponseManifestList responseManifestList = Application_Singleton.gson.fromJson(response, type);
                            responseManifestLists.set(position, responseManifestList);
                            manifestListAdapter.notifyItemChanged(position);
                        }
                    } catch (Exception e) {
                        // Exception generate due to filter change/page refresh
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgressList();
            }
        });
    }

}