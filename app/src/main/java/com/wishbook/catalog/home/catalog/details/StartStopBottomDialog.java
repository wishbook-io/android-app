package com.wishbook.catalog.home.catalog.details;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.ProductMyDetail;
import com.wishbook.catalog.commonmodels.responses.EnumGroupResponse;
import com.wishbook.catalog.home.adapters.StartStopSellingAdapter;
import com.wishbook.catalog.home.catalog.StartStopHandler;
import com.wishbook.catalog.home.models.ProductObj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.facebook.FacebookSdk.getApplicationContext;

public class StartStopBottomDialog extends BottomSheetDialogFragment {


    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.txt_title)
    TextView txt_title;

    @BindView(R.id.frame_submit)
    FrameLayout frame_submit;

    @BindView(R.id.linear_size_container)
    LinearLayout linear_size_container;

    @BindView(R.id.txt_note_for_size)
            TextView txt_note_for_size;


    /// ### Start init variable ######

    StartStopDoneListener startStopDoneListener;
    boolean isStopSelling;
    Context mContext;
    String catalogname;
    StartStopSellingAdapter startStopSellingAdapter;
    Enum type;
    ProductObj[] productObjs;
    ArrayList<String> selected_full_sizes;
    boolean isSellerWithSize;
    ArrayList<String> system_sizes = new ArrayList<>();
    String category_id = null;
    String id;


    public static StartStopBottomDialog newInstance(Bundle bundle) {
        StartStopBottomDialog f = new StartStopBottomDialog();
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
        View v = inflater.inflate(R.layout.start_stop_bottom_sheet_modal, container, false);
        ButterKnife.bind(this, v);
        mContext = getActivity();
        Application_Singleton singleton = new Application_Singleton();
        singleton.trackScreenView(getClass().getSimpleName(), getActivity());
        if (getArguments().getBoolean("isStopSelling")) {
            isStopSelling = true;
        }
        if (getArguments().getString("catalogname") != null) {
            catalogname = getArguments().getString("catalogname");
            if (isStopSelling) {
                txt_title.setText("Stop selling " + catalogname);
            } else {
                txt_title.setText("Start selling " + catalogname);
            }
        }
        if (getArguments().getString("category_id") != null) {
            category_id = getArguments().getString("category_id");
            id = getArguments().getString("id");
            getSizeEav(category_id);
        }

        initListner();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        final View view = getView();
        view.post(new Runnable() {
            @Override
            public void run() {
                View parent = (View) view.getParent();
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
                CoordinatorLayout.Behavior behavior = params.getBehavior();
                BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
                WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                final Point size = new Point();
                display.getSize(size);
                bottomSheetBehavior.setPeekHeight((int) (size.y));
                bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    }
                });
                parent.setBackgroundColor(Color.TRANSPARENT);
            }
        });

    }


    public void initListner() {
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        frame_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartStopHandler startStopHandler =   new StartStopHandler(getActivity());
                if (type == StartStopHandler.STARTSTOP.SINGLE_WITH_SIZE || type == StartStopHandler.STARTSTOP.SINGLE_WITHOUT_SIZE) {
                  startStopHandler.startStopHandler(type, startStopSellingAdapter.getProductObjs(), null, null,!isStopSelling);
                } else if (type == StartStopHandler.STARTSTOP.FULL_WITH_SIZE) {
                   startStopHandler.startStopHandler(type, null, id, selected_full_sizes,!isStopSelling);
                }
                startStopHandler.setStartStopDoneListener(new StartStopHandler.StartStopDoneListener() {
                    @Override
                    public void onSuccessStart() {
                        getDialog().dismiss();
                        if(startStopDoneListener!=null) {
                            startStopDoneListener.onSuccessStart();
                        }
                    }

                    @Override
                    public void onSuccessStop() {
                        getDialog().dismiss();
                        if(startStopDoneListener!=null) {
                            startStopDoneListener.onSuccessStop();
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });

            }
        });
    }

    public interface StartStopDoneListener {
        void onSuccessStart();

        void onSuccessStop();
    }

    public void setStartStopDoneListener(StartStopDoneListener startStopDoneListener) {
        this.startStopDoneListener = startStopDoneListener;
    }

    private void addSubShipment(final ArrayList<String> sizes, LinearLayout root, ProductMyDetail productMyDetail) {
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root.removeAllViews();
        root.addView(txt_note_for_size);
        for (int i = 0; i < sizes.size(); i++) {
            View v = vi.inflate(R.layout.size_select_item, null);
            CheckBox cb = v.findViewById(R.id.checkbox);
            cb.setText(sizes.get(i));
            final int finalI = i;

            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        if (selected_full_sizes == null) {
                            selected_full_sizes = new ArrayList<>();
                        }
                        if (!selected_full_sizes.contains(sizes.get(finalI))) {
                            selected_full_sizes.add(sizes.get(finalI));
                        }
                    } else {
                        if (selected_full_sizes != null) {
                            selected_full_sizes.remove(sizes.get(finalI));
                        }
                    }
                }
            });


            if (productMyDetail.getAvailable_sizes() != null) {
                String[] splited = productMyDetail.getAvailable_sizes().split(",");
                ArrayList<String> s1 = new ArrayList<>(Arrays.asList(splited));
                if (s1.contains(sizes.get(i))) {
                    cb.setChecked(true);
                }
            }

            root.addView(v);
        }

    }




    public void getMyDetails(Context context, String catalogID) {
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            final MaterialDialog progress_dialog = StaticFunctions.showProgress(context);
            progress_dialog.show();
            HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context, "mydetails", catalogID), null, headers, false, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if (progress_dialog != null) {
                        progress_dialog.dismiss();
                    }
                    try {
                        if (isAdded() && !isDetached()) {
                            ProductMyDetail productMyDetail = Application_Singleton.gson.fromJson(response, ProductMyDetail.class);
                            if (getArguments() != null && getArguments().getSerializable("products") != null) {
                                if (getArguments().getSerializable("type") == (StartStopHandler.STARTSTOP.SINGLE_WITH_SIZE)) {
                                    type = StartStopHandler.STARTSTOP.SINGLE_WITH_SIZE;
                                    if (getArguments().getSerializable("products") != null) {
                                        productObjs = (ProductObj[]) getArguments().getSerializable("products");
                                        startStopSellingAdapter = new StartStopSellingAdapter(mContext, Arrays.asList(productObjs), system_sizes, true, productMyDetail);
                                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                                        recyclerView.setLayoutManager(mLayoutManager);
                                        recyclerView.setNestedScrollingEnabled(false);
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                        recyclerView.setAdapter(startStopSellingAdapter);
                                    }
                                } else if (getArguments().getSerializable("type") == (StartStopHandler.STARTSTOP.SINGLE_WITHOUT_SIZE)) {
                                    type = StartStopHandler.STARTSTOP.SINGLE_WITHOUT_SIZE;
                                    productObjs = (ProductObj[]) getArguments().getSerializable("products");
                                    startStopSellingAdapter = new StartStopSellingAdapter(mContext, Arrays.asList(productObjs), system_sizes, false, productMyDetail);
                                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    recyclerView.setNestedScrollingEnabled(false);
                                    recyclerView.setHasFixedSize(true);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(startStopSellingAdapter);
                                } else {
                                    type = StartStopHandler.STARTSTOP.FULL_WITH_SIZE;
                                    productObjs = (ProductObj[]) getArguments().getSerializable("products");
                                    addSubShipment(system_sizes, linear_size_container, productMyDetail);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    if (progress_dialog != null) {
                        progress_dialog.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getSizeEav(String category_id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
        String url = URLConstants.companyUrl(mContext, "enumvalues", "") + "?category=" + category_id + "&attribute_slug=" + "size";
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
                EnumGroupResponse[] enumGroupResponses = Application_Singleton.gson.fromJson(response, EnumGroupResponse[].class);
                if (enumGroupResponses != null) {
                    if (enumGroupResponses.length > 0) {
                        ArrayList<EnumGroupResponse> enumGroupResponses1 = new ArrayList<EnumGroupResponse>(Arrays.asList(enumGroupResponses));
                        for (int i = 0; i < enumGroupResponses1.size(); i++) {
                            system_sizes.add(enumGroupResponses1.get(i).getValue());
                        }
                        isSellerWithSize = true;
                    }
                } else {
                    isSellerWithSize = false;
                }
                if(isSellerWithSize) {
                    txt_note_for_size.setVisibility(View.VISIBLE);
                } else {
                    txt_note_for_size.setVisibility(View.GONE);
                }

                getMyDetails(mContext, id);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (progressdialog != null) {
                    progressdialog.dismiss();
                }
            }
        });
    }
}