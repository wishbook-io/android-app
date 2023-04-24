package com.wishbook.catalog.home.cart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.widget.SimpleTextWatcher;
import com.wishbook.catalog.commonmodels.Advancedcompanyprofile;
import com.wishbook.catalog.commonmodels.CartCatalogModel;
import com.wishbook.catalog.commonmodels.CartProductModel;
import com.wishbook.catalog.commonmodels.CompanyProfile;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.home.adapters.ResalePriceChangeAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Activity_ResaleAmt extends AppCompatActivity {

    private Context mContext;

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

    @BindView(R.id.resell_bottom_note)
    TextView resell_bottom_note;

    @BindView(R.id.edit_default_margin)
    EditText edit_default_margin;


    @BindView(R.id.txt_save_default_margin)
    TextView txt_save_default_margin;


    private double total_resale_amount;


    LinearLayoutManager mLayoutManager;

    ResalePriceChangeAdapter adapter;

    boolean isEditModeDefaultMargin;

    public static String TAG = ResaleAmtBottomSheet.class.getSimpleName();


    ResaleAmtBottomSheet.ResaleDoneSelectListener resaleDoneSelectListener;

    DecimalFormat decimalFormat;

    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = Activity_ResaleAmt.this;
        StaticFunctions.initializeAppsee();
        setContentView(R.layout.resale_amt_bottomsheet);
        ButterKnife.bind(this);
        decimalFormat = new DecimalFormat("#.##");
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initView(false);
    }

    public void initView(boolean isRefershData) {
        if (getIntent().getSerializableExtra("data") != null) {
            //setResaleDefaultMargin();
            getCartData(mContext);

            Log.e(TAG, "initView: called ===>");
            final CartCatalogModel cart = (CartCatalogModel) getIntent().getSerializableExtra("data");
            if (isRefershData) {
                for (int i = 0; i < cart.getCatalogs().size(); i++) {
                    cart.getCatalogs().get(i).setCatalog_display_amount(0);
                }
            }

            btn_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (adapter!=null && adapter.getAllResaleAmt() != null) {
                        Double total_resale_amt = 0.0;
                        HashMap<String, String> temp = adapter.getAllResaleAmt();
                        ArrayList<CartProductModel.Items> items = new ArrayList<>();
                        for (int i = 0; i < temp.size(); i++) {
                            total_resale_amt += Double.parseDouble(temp.get("item_price_" + i));
                            ArrayList<CartProductModel.Items> temp_items = (ArrayList<CartProductModel.Items>) cartItemCreate(i, Double.parseDouble(temp.get("item_price_" + i)), cart.getCatalogs().get(i).getProducts().get(0).getQuantity());
                            if (temp_items != null) {
                                items.addAll(temp_items);
                            }
                        }
                        if (total_resale_amt >= cart.getTotal_amount()) {
                            CartProductModel cartProductModel = new CartProductModel(items);
                            cartProductModel.setReseller_order(true);
                            patchDisplayAmt(mContext, cartProductModel, total_resale_amt, temp);
                        } else {
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.resell_amount_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }
    }

    public void setResaleDefaultMargin() {
        if (UserInfo.getInstance(mContext).getResaleDefaultMargin() != null) {
            edit_default_margin.setText(UserInfo.getInstance(mContext).getResaleDefaultMargin());
        } else {
            edit_default_margin.setText("");
        }

        disableSaveButton();


        edit_default_margin.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edit_default_margin.getText().toString().isEmpty()) {
                    enableSaveButton();
                } else {
                    disableSaveButton();
                }
            }
        });

        txt_save_default_margin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit_default_margin.getText().toString().isEmpty()) {
                    Toast.makeText(mContext, "Please enter Resale default margin", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (Double.parseDouble(edit_default_margin.getText().toString()) < 999) {
                        patchDefaultMargin(edit_default_margin.getText().toString());
                        edit_default_margin.clearFocus();
                        KeyboardUtils.hideKeyboard((Activity) mContext);
                        disableSaveButton();
                    } else {
                        Toast.makeText(mContext, "Resale default margin can't be greater 999%", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void disableSaveButton() {
        txt_save_default_margin.setEnabled(false);
        txt_save_default_margin.setBackgroundColor(ContextCompat.getColor(mContext, R.color.purchase_light_gray));
    }

    public void enableSaveButton() {
        txt_save_default_margin.setEnabled(true);
        txt_save_default_margin.setBackgroundColor(getResources().getColor(R.color.color_primary));
    }



    public List<CartProductModel.Items> cartItemCreate(int position, double displayAmt, int quantity) {

        if (getIntent().getSerializableExtra("data") != null) {
            CartCatalogModel cart_temp = (CartCatalogModel) getIntent().getSerializableExtra("data");
            List<CartProductModel.Items> items = new ArrayList<>();
            CartProductModel.Items cartItem = null;

            double perProductDisplayAmt = displayAmt / cart_temp.getCatalogs().get(position).getProducts().size();

            for (CartCatalogModel.Products product : cart_temp.getCatalogs().get(position).getProducts()) {
                if (cartItem != null && cartItem.getProduct().equals(product.getProduct())) {
                    //same price case
                    items.clear();
                    perProductDisplayAmt = displayAmt;
                }
                cartItem = new CartProductModel.Items(product.getRate(), String.valueOf(quantity), product.getProduct(), product.getIs_full_catalog(), product.getNote());
                cartItem.setDisplay_amount(Double.parseDouble(decimalFormat.format(perProductDisplayAmt)));

                if (product.getNo_of_pcs() > 0) {
                    cartItem.setQuantity(String.valueOf(quantity * product.getNo_of_pcs()));
                } else {
                    cartItem.setQuantity(String.valueOf(quantity));
                }
                items.add(cartItem);
            }
            return items;
        }
        return null;
    }

    public void patchDisplayAmt(final @NonNull Context context, CartProductModel cartProductModel, final Double total_resale_amt, final HashMap<String, String> param) {
        final SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
        try {
            String url = URLConstants.companyUrl(context, "cart", "") + preferences.getString("cartId", "") + "/";
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            Log.d("PATCHDATA", "" + (new Gson().toJson(cartProductModel)));
            final MaterialDialog progressDialog = StaticFunctions.showProgress(mContext);
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


                        Intent intent = new Intent();
                        intent.putExtra("total_resale_amt", total_resale_amt);
                        intent.putExtra("param", param);
                        boolean isPaymentRedirted = false;
                        if (getIntent().getBooleanExtra("payment_redirected", false)) {
                            isPaymentRedirted = true;
                        }
                        intent.putExtra("payment_redirected", isPaymentRedirted);
                        setResult(Activity.RESULT_OK, intent);
                        finish();

                        if (resaleDoneSelectListener != null) {

                            //resaleDoneSelectListener.onDone(total_resale_amt, param);
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
                    //Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public interface ResaleDoneSelectListener {
        void onDone(Double totalPrice, HashMap<String, String> hashMap);
    }

    public void setResaleDoneSelectListener(ResaleAmtBottomSheet.ResaleDoneSelectListener resaleDoneSelectListener) {
        this.resaleDoneSelectListener = resaleDoneSelectListener;
    }

    private void patchDefaultMargin(final String margin) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
        JsonObject jsonObject;
        CompanyProfile companyProfile = new CompanyProfile();
        Advancedcompanyprofile advancedcompanyprofile = new Advancedcompanyprofile();
        advancedcompanyprofile.setResale_default_margin(margin);
        companyProfile.setAdvancedcompanyprofile(advancedcompanyprofile);
        jsonObject = new Gson().fromJson(new Gson().toJson(companyProfile), JsonObject.class);
        HttpManager.getInstance((Activity) mContext).requestPatch(HttpManager.METHOD.PATCH, URLConstants.companyUrl(mContext, "", ""), jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                UserInfo.getInstance(mContext).setResaleDefaultMargin(margin);
                initView(true);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.logger(error.getErrormessage());
            }
        });


    }

    public void getCartData(final @NonNull Context context) {
        try {
            final SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
            String url = URLConstants.companyUrl(context, "cart", "") + preferences.getString("cartId", "") + "/catalogwise/";
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            final MaterialDialog progress_dialog = StaticFunctions.showProgress(mContext);
            progress_dialog.show();
            HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    try {
                        if(progress_dialog!=null) {
                            progress_dialog.dismiss();
                        }
                        CartCatalogModel cart_response = Application_Singleton.gson.fromJson(response, CartCatalogModel.class);
                        adapter = new ResalePriceChangeAdapter(mContext,
                                cart_response);
                        adapter.setChangeMaginListner(new ResalePriceChangeAdapter.changeMarginListener() {
                            @Override
                            public void onChange() {
                                Double temp_resale_amt = adapter.calculateTotalResaleAmt();
                                txt_total_resale_amount.setText("\u20B9 " + decimalFormat.format(temp_resale_amt));
                            }
                        });



                        if (getIntent().getStringExtra("selected_payment") != null) {
                            if (getIntent().getStringExtra("selected_payment").equalsIgnoreCase("COD")) {
                                resell_bottom_note.setText(getResources().getString(R.string.resell_bottom_sheet_cod_note));
                            } else {
                                resell_bottom_note.setText(getResources().getString(R.string.resell_bottom_sheet_other_note));
                            }
                        }
                        txt_resale_order_amount.setText("\u20B9 " + cart_response.getTotal_amount());
                        txt_total_resale_amount.setText("\u20B9 " + total_resale_amount);
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recycler_item.setLayoutManager(mLayoutManager);
                        recycler_item.setNestedScrollingEnabled(false);
                        recycler_item.setHasFixedSize(true);
                        recycler_item.setItemAnimator(new DefaultItemAnimator());
                        recycler_item.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    if(progress_dialog!=null) {
                        progress_dialog.dismiss();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
