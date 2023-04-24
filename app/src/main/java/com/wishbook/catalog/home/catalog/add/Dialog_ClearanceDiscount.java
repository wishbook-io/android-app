package com.wishbook.catalog.home.catalog.add;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.widget.SimpleTextWatcher;
import com.wishbook.catalog.commonmodels.ProductMyDetail;
import com.wishbook.catalog.home.catalog.adapter.ClearanceDiscountAdapter;
import com.wishbook.catalog.home.models.ProductObj;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Dialog_ClearanceDiscount extends DialogFragment {

    @BindView(R.id.recycler_view_product)
    RecyclerView recycler_view_product;

    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.btn_submit)
    AppCompatButton btn_submit;

    @BindView(R.id.btn_delete)
    AppCompatButton btn_delete;

    @BindView(R.id.nested_scroll_view)
    NestedScrollView nested_scroll_view;

    @BindView(R.id.edit_per)
    EditText edit_per;

    String catalog_seller_id;

    ClearanceDiscountChangeListener clearanceDiscountChangeListener;

    boolean isShowDeleteOption;

    ClearanceDiscountAdapter adapter;

    ProductMyDetail productMyDetail;

    String isAlreadyDiscountedPer;

    public Dialog_ClearanceDiscount() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_clearance_discount);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
        ButterKnife.bind(this, dialog);
        initView();
        initListener();
        setData();
        return dialog;
    }


    public void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view_product.setLayoutManager(linearLayoutManager);
        recycler_view_product.setHasFixedSize(true);
        recycler_view_product.setNestedScrollingEnabled(false);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(200);
        recycler_view_product.setFocusable(false);
        recycler_view_product.setItemAnimator(itemAnimator);



        if (getArguments().getString("catalog_seller_id") != null) {
            catalog_seller_id = getArguments().getString("catalog_seller_id");
        }

    }

    public void initListener() {
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm() && catalog_seller_id != null) {
                    callPatchClearanceDiscount(catalog_seller_id, false);
                }
            }
        });



        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                        .title("Delete Clearance Discount")
                        .content("Are you sure you want to delete discount?")
                        .positiveText("YES")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                                callPatchClearanceDiscount(catalog_seller_id, true);
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
            }
        });
    }


    public void setData() {
        if (getArguments().getSerializable("product_my_detail") != null) {
            productMyDetail = (ProductMyDetail) getArguments().getSerializable("product_my_detail");
        }

        if (getArguments().getSerializable("products") != null) {
            ArrayList<ProductObj> arrayList = (ArrayList<ProductObj>) getArguments().getSerializable("products");
            adapter = new ClearanceDiscountAdapter(getActivity(), arrayList, productMyDetail);
            recycler_view_product.setAdapter(adapter);
            ViewCompat.setNestedScrollingEnabled(recycler_view_product, false);
        }


        edit_per.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (adapter != null) {
                        if (!edit_per.getText().toString().trim().isEmpty()) {
                            adapter.setDiscount_per(Double.parseDouble(edit_per.getText().toString().trim()));
                            adapter.notifyDataSetChanged();
                        } else {
                            adapter.setDiscount_per(0);
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (getArguments().getDouble("my_clearance_discount_percentage") > 0) {
            if(adapter!=null) {
                Log.e("TAG", "setData: Already Discount" );
                adapter.setIsAlreadyDiscountedPer(String.valueOf(getArguments().getDouble("my_clearance_discount_percentage")));
            }
            edit_per.setText(String.valueOf(getArguments().getDouble("my_clearance_discount_percentage")));
            isAlreadyDiscountedPer = edit_per.getText().toString();
            isShowDeleteOption = true;
        }


        if (isShowDeleteOption) {
            btn_delete.setVisibility(View.VISIBLE);
        } else {
            btn_delete.setVisibility(View.GONE);
        }

        nested_scroll_view.postDelayed(new Runnable() {
            @Override
            public void run() {
                nested_scroll_view.scrollTo(0, 0);
            }
        }, 1000);


    }

    public boolean validateForm() {
        if (edit_per.getText().toString().trim().isEmpty()) {
            edit_per.setError("Please enter discount percentage");
            edit_per.requestFocus();
            return false;
        }

        try {
            if (Double.parseDouble(edit_per.getText().toString().trim()) > 80) {
                edit_per.setError("Clearance discount can't be greater 80%");
                edit_per.requestFocus();
                return false;
            }
        } catch (Exception e) {
            edit_per.setError("Please enter valid discount");
            edit_per.requestFocus();
            return false;
        }

        return true;
    }


    public void callPatchClearanceDiscount(String catalogSellerId, final boolean isDeleteDiscount) {
        HashMap<String, Object> param = new HashMap<>();
        if (isDeleteDiscount) {
            param.put("clearance_discount_percentage", "0");
        } else {
            param.put("clearance_discount_percentage", edit_per.getText().toString());
        }
        JsonObject jsonObject = new Gson().fromJson(new Gson().toJson(param), JsonObject.class);
        String url = URLConstants.companyUrl(getActivity(), "catalog-seller", "") + catalogSellerId + "/";
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, url, jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    dismiss();
                    if (isDeleteDiscount) {
                        Toast.makeText(getActivity(), "Successfully deleted discount", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Successfully updated discount", Toast.LENGTH_LONG).show();
                    }

                    if (clearanceDiscountChangeListener != null) {
                        clearanceDiscountChangeListener.clearanceDiscountChange();
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public interface ClearanceDiscountChangeListener {
        void clearanceDiscountChange();
    }


    public ClearanceDiscountChangeListener getClearanceDiscountChangeListener() {
        return clearanceDiscountChangeListener;
    }

    public void setClearanceDiscountChangeListener(ClearanceDiscountChangeListener clearanceDiscountChangeListener) {
        this.clearanceDiscountChangeListener = clearanceDiscountChangeListener;
    }
}
