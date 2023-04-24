package com.wishbook.catalog.home.cart;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.CartCatalogModel;
import com.wishbook.catalog.commonmodels.CartProductModel;
import com.wishbook.catalog.home.adapters.ResalePriceChangeAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ResaleAmtBottomSheet extends BottomSheetDialogFragment {


    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.recycler_item)
    RecyclerView recycler_item;

    @BindView(R.id.btn_done)
    AppCompatButton btn_done;

    @BindView(R.id.txt_resale_order_amount)
    TextView txt_resale_order_amount;

    @BindView(R.id.txt_total_resale_amount)
    TextView txt_total_resale_amount;


    private double total_resale_amount;


    LinearLayoutManager mLayoutManager;

    ResalePriceChangeAdapter adapter;

    public static String TAG = ResaleAmtBottomSheet.class.getSimpleName();


    ResaleAmtBottomSheet.ResaleDoneSelectListener resaleDoneSelectListener;

    View view;

    public static ResaleAmtBottomSheet newInstance(Bundle bundle) {
        ResaleAmtBottomSheet f = new ResaleAmtBottomSheet();
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
        view = inflater.inflate(R.layout.resale_amt_bottomsheet, container, false);
        ButterKnife.bind(this, view);
        total_resale_amount = 0;
        Application_Singleton singleton = new Application_Singleton();
        singleton.trackScreenView(getClass().getSimpleName(), getActivity());
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        initView();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
                final BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
                WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                bottomSheetBehavior.setPeekHeight(StaticFunctions.dpToPx(getActivity(), size.y));
                bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            dismiss();
                        } else {

                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });
                parent.setBackgroundColor(Color.TRANSPARENT);
            }
        });

    }


    public void initView() {
        if (getArguments().getSerializable("data") != null) {
            adapter = new ResalePriceChangeAdapter(getActivity(),
                    (CartCatalogModel) getArguments().getSerializable("data"));
            adapter.setChangeMaginListner(new ResalePriceChangeAdapter.changeMarginListener() {
                @Override
                public void onChange() {
                   Double temp_resale_amt =  adapter.calculateTotalResaleAmt();
                   txt_total_resale_amount.setText("\u20B9 " + temp_resale_amt);
                }
            });
            final CartCatalogModel cart = (CartCatalogModel) getArguments().getSerializable("data");
            txt_resale_order_amount.setText("\u20B9 " + cart.getTotal_amount());
            txt_total_resale_amount.setText("\u20B9 " + total_resale_amount);
            mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recycler_item.setLayoutManager(mLayoutManager);
            recycler_item.setNestedScrollingEnabled(false);
            recycler_item.setHasFixedSize(true);
            recycler_item.setItemAnimator(new DefaultItemAnimator());
            recycler_item.setAdapter(adapter);
            btn_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (adapter.getAllResaleAmt() != null) {
                        Double total_resale_amt = 0.0;
                        HashMap<String, String> temp = adapter.getAllResaleAmt();
                        ArrayList<CartProductModel.Items> items = new ArrayList<>();
                        for (int i = 0; i < temp.size(); i++) {
                            total_resale_amt += Double.parseDouble(temp.get("item_price_" + i));
                            ArrayList<CartProductModel.Items> temp_items = (ArrayList<CartProductModel.Items>) cartItemCreate(i, Double.parseDouble(temp.get("item_price_" + i)));
                            if (temp_items != null) {
                                items.addAll(temp_items);
                            }
                        }
                        if (total_resale_amt > cart.getTotal_amount()) {
                            CartProductModel cartProductModel = new CartProductModel(items);
                            cartProductModel.setReseller_order(true);
                            patchDisplayAmt(getActivity(), cartProductModel, total_resale_amt,temp);
                        } else {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.resell_amount_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }
    }


    public List<CartProductModel.Items> cartItemCreate(int position, double displayAmt) {
        if (getArguments().getSerializable("data") != null) {
            CartCatalogModel cart = (CartCatalogModel) getArguments().getSerializable("data");
            List<CartProductModel.Items> items = new ArrayList<>();
            CartProductModel.Items cartItem = null;
            for (CartCatalogModel.Products product : cart.getCatalogs().get(position).getProducts()) {
                if (cartItem != null && cartItem.getProduct().equals(product.getProduct())) {
                    items.clear();
                }
                cartItem = new CartProductModel.Items(product.getRate(), String.valueOf(product.getQuantity()), product.getProduct(), product.getIs_full_catalog(), product.getNote());
                if (product.getNo_of_pcs() > 0) {
                    cartItem.setDisplay_amount(displayAmt);
                } else {
                    cartItem.setDisplay_amount(displayAmt);
                }
                items.add(cartItem);
            }
            return items;
        }
        return null;
    }

    public void patchDisplayAmt(final @NonNull Context context, CartProductModel cartProductModel, final Double total_resale_amt, final HashMap<String,String> param) {
        final SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
        try {
            String url = URLConstants.companyUrl(context, "cart", "") + preferences.getString("cartId", "") + "/";
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            Log.d("PATCHDATA", "" + (new Gson().toJson(cartProductModel)));
            final MaterialDialog progressDialog = StaticFunctions.showProgress(getActivity());
            progressDialog.show();
            HttpManager.getInstance((Activity) context).requestwithObject(HttpManager.METHOD.PATCHJSONOBJECTWITHPROGRESS, url, new Gson().fromJson(new Gson().toJson(cartProductModel), JsonObject.class), headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    try {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }



                        if (resaleDoneSelectListener != null) {
                            dismiss();
                            resaleDoneSelectListener.onDone(total_resale_amt,param);
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
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public interface ResaleDoneSelectListener {
        void onDone(Double totalPrice,HashMap<String,String> hashMap);
    }

    public void setResaleDoneSelectListener(ResaleAmtBottomSheet.ResaleDoneSelectListener resaleDoneSelectListener) {
        this.resaleDoneSelectListener = resaleDoneSelectListener;
    }
}

