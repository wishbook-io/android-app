package com.wishbook.catalog.home.cart;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchCancelOrder;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.commonmodels.responses.ShippingAddressResponse;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CodReconfirmDialogFragment extends DialogFragment implements View.OnClickListener {


    @BindView(R.id.btn_reconfirm_order)
    TextView btn_reconfirm_order;

    @BindView(R.id.btn_cancel_order)
    TextView btn_cancel_order;

    @BindView(R.id.txt_order_details)
    TextView txt_order_details;

    @BindView(R.id.txt_order_no)
    TextView txt_order_no;

    @BindView(R.id.txt_order_date)
    TextView txt_order_date;

    @BindView(R.id.txt_order_status)
    TextView txt_order_status;

    @BindView(R.id.txt_payment_detail)
    TextView txt_payment_detail;

    @BindView(R.id.txt_shipping_address)
    TextView txt_shipping_address;

    @BindView(R.id.linear_shipping_container)
    LinearLayout linear_shipping_container;

    @BindView(R.id.relative_payment_detail)
    RelativeLayout relative_payment_detail;

    @BindView(R.id.txt_margin_amt)
    TextView txt_margin_amt;

    @BindView(R.id.relative_margin_amount)
    RelativeLayout relative_margin_amount;

    @BindView(R.id.txt_pending_amt)
    TextView txt_pending_amt;

    @BindView(R.id.relative_progress)
    RelativeLayout relativeProgress;


    DisMissListener disMissListener;

    int count;
    Response_buyingorder response_buyingorder;

    Response_buyingorder selectOrder;

    ArrayList<Response_buyingorder> selectOrderList;

    ArrayList<Response_buyingorder> response_buyingorderArrayList;

    int pending_success_count;
    MaterialDialog progressDialog;
    boolean isSingleOrder;

    // Duplication code for single order reconfirm and bulk order reconfirm

    public CodReconfirmDialogFragment() {

    }

    public static CodReconfirmDialogFragment newInstance(Bundle bundle) {
        CodReconfirmDialogFragment f = new CodReconfirmDialogFragment();
        if (bundle != null) {
            f.setArguments(bundle);
        }
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.cod_verification_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = View.inflate(getContext(), R.layout.cod_verification_dialog, null);
        ButterKnife.bind(this, view);
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.cod_verification_dialog, null);
        ButterKnife.bind(this, view);
        btn_cancel_order.setOnClickListener(this);
        btn_reconfirm_order.setOnClickListener(this);
        initView();
        return view;
    }


    private void initView() {
        if (getArguments() != null && getArguments().getSerializable("data") != null) {
            count = getArguments().getInt("current_count");
            response_buyingorder = (Response_buyingorder) getArguments().getSerializable("data");
            callOrderExpand(response_buyingorder.getId());
            isSingleOrder = true;
        } else if (getArguments() != null && getArguments().getSerializable("data_array") != null) {
            Log.e("TAG", "initView: Data Array Not Null");
            count = getArguments().getInt("current_count");
            response_buyingorderArrayList = (ArrayList<Response_buyingorder>) getArguments().getSerializable("data_array");
            StringBuilder stringBuilder = new StringBuilder();
            // List of orders
            for (Response_buyingorder order :
                    response_buyingorderArrayList) {
                callOrderExpandBulkOrder(order.getId());
                stringBuilder.append(order.getId() + ", ");
            }
            Log.e("TAG", "initView: Multiple Orders : " + stringBuilder.toString());
        }

    }

    public void bindUI() {
        if (selectOrder != null) {
            if (selectOrder.getOrder_number() != null) {
                txt_order_no.setText(selectOrder.getOrder_number());
            } else {
                txt_order_no.setText(selectOrder.getId());
            }
            if (selectOrder.getCreated_at() != null) {
                String temp_date = DateUtils.changeDateFormat(StaticFunctions.SERVER_POST_FORMAT1, StaticFunctions.CLIENT_DISPLAY_FORMAT1, selectOrder.getCreated_at());
                txt_order_date.setText(temp_date);
            }

            txt_order_status.setText(StringUtils.capitalize(selectOrder.getProcessing_status().toLowerCase().trim()));

            if (selectOrder.getPayment_details() != null && !selectOrder.getPayment_details().isEmpty()) {
                relative_payment_detail.setVisibility(View.VISIBLE);
                txt_payment_detail.setText(selectOrder.getPayment_details());
            } else {
                relative_payment_detail.setVisibility(View.GONE);
            }


            if (selectOrder.getShip_to() != null) {
                linear_shipping_container.setVisibility(View.VISIBLE);
                try {
                    ShippingAddressResponse ship = new Gson().fromJson(new Gson().toJson(((LinkedTreeMap<String, Object>) selectOrder.getShip_to())), ShippingAddressResponse.class);
                    String pincode = ship.getPincode();
                    if (ship.getPincode() != null) {
                        try {
                            pincode = String.valueOf(Math.round(Double.parseDouble(ship.getPincode())));
                        } catch (Exception e) {
                            pincode = ship.getPincode();
                        }
                    }
                    String address = ship.getStreet_address() + ", " +
                            ship.getState().getState_name() + ", " +
                            ship.getCity().getCity_name() + " - " +
                            pincode;
                    txt_shipping_address.setText(address);
                } catch (Exception e) {
                    linear_shipping_container.setVisibility(View.GONE);
                }

            }

            if (selectOrder.isReseller_order()) {
                relative_margin_amount.setVisibility(View.VISIBLE);
                if (selectOrder.getInvoice().size() > 0) {
                    double margin_amount = selectOrder.getDisplay_amount() - Double.parseDouble(selectOrder.getInvoice().get(0).getTotal_amount());
                    txt_margin_amt.setText("\u20B9 " + new DecimalFormat("#.##").format(margin_amount));
                }
                if (selectOrder.getInvoice().size() > 0) {
                    txt_pending_amt.setText("\u20B9 " + selectOrder.getInvoice().get(0).getDisplay_amount());
                }
            } else {
                relative_margin_amount.setVisibility(View.GONE);
                if (selectOrder.getInvoice().size() > 0) {
                    txt_pending_amt.setText("\u20B9 " + selectOrder.getInvoice().get(0).getPending_amount());
                }
            }
        }
    }

    public void bindUIBulkOrder() {
        if (selectOrderList.get(0) != null) {
            double total_pending_amt = 0, total_display_amt = 0, total_total_amt = 0;
            ArrayList<String> order_number_list = new ArrayList<>();
            for (int i = 0; i < selectOrderList.size(); i++) {
                if (selectOrderList.get(i).getOrder_number() != null) {
                    order_number_list.add(selectOrderList.get(i).getOrder_number());
                } else {
                    order_number_list.add(selectOrderList.get(i).getId());
                }
                total_pending_amt += Double.parseDouble(selectOrderList.get(i).getInvoice().get(0).getPending_amount());
                if (selectOrderList.get(i).isReseller_order()) {
                    if (selectOrderList.get(i).getInvoice().size() > 0) {
                        total_display_amt += selectOrderList.get(i).getDisplay_amount();
                        total_total_amt += Double.parseDouble(selectOrderList.get(i).getInvoice().get(0).getTotal_amount());
                    }
                }
            }
            selectOrder = selectOrderList.get(0);
            if (selectOrderList.size() > 0) {
                txt_order_no.setText(StaticFunctions.ArrayListToString(order_number_list, StaticFunctions.COMMASEPRATED));
            }
            if (selectOrder.getCreated_at() != null) {
                String temp_date = DateUtils.changeDateFormat(StaticFunctions.SERVER_POST_FORMAT1, StaticFunctions.CLIENT_DISPLAY_FORMAT1, selectOrder.getCreated_at());
                txt_order_date.setText(temp_date);
            }

            txt_order_status.setText(StringUtils.capitalize(selectOrder.getProcessing_status().toLowerCase().trim()));

            if (selectOrder.getPayment_details() != null && !selectOrder.getPayment_details().isEmpty()) {
                relative_payment_detail.setVisibility(View.VISIBLE);
                try {
                    String[] multi_string = selectOrder.getPayment_details().split("\n");
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String s:
                            multi_string) {
                        if(!s.contains("Paid Amount") && !s.contains("Pending Amount")) {
                            stringBuilder.append(s+"\n");
                        }
                    }
                    txt_payment_detail.setText(stringBuilder.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    txt_payment_detail.setText(selectOrder.getPayment_details());
                }


            } else {
                relative_payment_detail.setVisibility(View.GONE);
            }


            if (selectOrder.getShip_to() != null) {
                linear_shipping_container.setVisibility(View.VISIBLE);
                try {
                    ShippingAddressResponse ship = new Gson().fromJson(new Gson().toJson(((LinkedTreeMap<String, Object>) selectOrder.getShip_to())), ShippingAddressResponse.class);
                    String pincode = ship.getPincode();
                    if (ship.getPincode() != null) {
                        try {
                            pincode = String.valueOf(Math.round(Double.parseDouble(ship.getPincode())));
                        } catch (Exception e) {
                            pincode = ship.getPincode();
                        }
                    }
                    String address = ship.getStreet_address() + ", " +
                            ship.getState().getState_name() + ", " +
                            ship.getCity().getCity_name() + " - " +
                            pincode;
                    txt_shipping_address.setText(address);
                } catch (Exception e) {
                    linear_shipping_container.setVisibility(View.GONE);
                }
            }

            if (selectOrder.isReseller_order()) {
                relative_margin_amount.setVisibility(View.VISIBLE);
                if (selectOrder.getInvoice().size() > 0) {
                    double margin_amount = total_display_amt - total_total_amt;
                    txt_margin_amt.setText("\u20B9 " + new DecimalFormat("#.##").format(margin_amount));
                }
                if (selectOrder.getInvoice().size() > 0) {
                    txt_pending_amt.setText("\u20B9 " + total_display_amt);
                }

            } else {
                relative_margin_amount.setVisibility(View.GONE);
                if (selectOrder.getInvoice().size() > 0) {
                    txt_pending_amt.setText("\u20B9 " + total_pending_amt);
                }
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel_order:
                if(isSingleOrder) {
                    if (response_buyingorder != null)
                        callOrderCancel(response_buyingorder.getId());
                } else {
                    if (response_buyingorderArrayList != null && response_buyingorderArrayList.size() > 0)
                        callBulkOrderCancel();
                }
                break;
            case R.id.btn_reconfirm_order:
                if(isSingleOrder) {
                    if (response_buyingorder != null)
                        callOrderPending(response_buyingorder.getId());
                } else {
                    if (response_buyingorderArrayList != null && response_buyingorderArrayList.size() > 0)
                        callBulkOrderPending();
                }
                break;
            default:
                break;
        }
    }

    public interface DisMissListener {
        void onCancelOrder(int count);

        void onReconfirmOrder(int count);
    }

    public void setDisMissListener(DisMissListener disMissListener) {
        this.disMissListener = disMissListener;
    }

    private void callOrderExpand(String id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = "";
        url = "purchaseorders_catalogwise";
        final MaterialDialog progress_dialog = StaticFunctions.showProgress(getActivity());
        progress_dialog.show();
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), url, id), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (progress_dialog != null) {
                        progress_dialog.dismiss();
                    }
                    hideProgress();
                    selectOrder = Application_Singleton.gson.fromJson(response, Response_buyingorder.class);
                    bindUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (progress_dialog != null) {
                    progress_dialog.dismiss();
                }
                hideProgress();
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
        });
    }

    private void callOrderExpandBulkOrder(String id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        String url = "";
        url = "purchaseorders_catalogwise";
        final MaterialDialog progress_dialog = StaticFunctions.showProgress(getActivity());
        progress_dialog.show();
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), url, id), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (progress_dialog != null) {
                        progress_dialog.dismiss();
                    }
                    hideProgress();
                    if (selectOrderList == null) {
                        selectOrderList = new ArrayList<>();
                    }
                    selectOrderList.add(Application_Singleton.gson.fromJson(response, Response_buyingorder.class));
                    bindUIBulkOrder();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (progress_dialog != null) {
                    progress_dialog.dismiss();
                }
                hideProgress();
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
        });
    }

    public void callOrderCancel(final String id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        PatchCancelOrder patchCancelOrder = new PatchCancelOrder(id, "Cancelled");
        patchCancelOrder.setBuyer_cancel(getActivity().getResources().getString(R.string.cod_cancelled_text));
        final MaterialDialog progressDialog = StaticFunctions.showProgress(getActivity());
        progressDialog.show();
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "purchaseorder", "") + id + '/', Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(patchCancelOrder), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getActivity(), "Order Cancelled Successfully", Toast.LENGTH_SHORT).show();
                    if (disMissListener != null) {
                        disMissListener.onCancelOrder(count);
                    }
                    dismiss();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
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
        });
    }

    public void callOrderPending(final String id) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        PatchCancelOrder patchCancelOrder = new PatchCancelOrder(id, "Pending");
        patchCancelOrder.setProcessing_note(getActivity().getResources().getString(R.string.cod_verifed_text));
        final MaterialDialog progressDialog = StaticFunctions.showProgress(getActivity());
        progressDialog.show();
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "purchaseorder", "") + id + '/', Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(patchCancelOrder), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getActivity(), "Order Confirmed Successfully", Toast.LENGTH_SHORT).show();
                    if (disMissListener != null) {
                        disMissListener.onReconfirmOrder(count);
                    }
                    dismiss();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
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
        });
    }

    public void callBulkOrderPending() {
        for (int i =0;i<response_buyingorderArrayList.size();i++) {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            PatchCancelOrder patchCancelOrder = new PatchCancelOrder(response_buyingorderArrayList.get(i).getId(), "Pending");
            patchCancelOrder.setProcessing_note(getActivity().getResources().getString(R.string.cod_verifed_text));
            if(i==0){
                progressDialog = StaticFunctions.showProgress(getActivity());
                progressDialog.show();
            }
            HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "purchaseorder", "") + response_buyingorderArrayList.get(i).getId() + '/', Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(patchCancelOrder), JsonObject.class), headers, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if (isAdded() && !isDetached()) {
                        pending_success_count++;
                        observableCallAPI(pending_success_count,true);
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
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
            });
        }

    }

    public void callBulkOrderCancel() {
        for (int i =0;i<response_buyingorderArrayList.size();i++) {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            PatchCancelOrder patchCancelOrder = new PatchCancelOrder(response_buyingorderArrayList.get(i).getId(), "Cancelled");
            patchCancelOrder.setBuyer_cancel(getActivity().getResources().getString(R.string.cod_cancelled_text));
            if(i==0) {
                progressDialog = StaticFunctions.showProgress(getActivity());
                progressDialog.show();
            }
            HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "purchaseorder", "") + response_buyingorderArrayList.get(i).getId() + '/', Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(patchCancelOrder), JsonObject.class), headers, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if (isAdded() && !isDetached()) {
                        pending_success_count++;
                        observableCallAPI(pending_success_count,false);
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
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
            });
        }

    }

    public void observableCallAPI(int count,boolean isPending) {
        if(count >= response_buyingorderArrayList.size()) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if(isPending) {
                Toast.makeText(getActivity(), "Orders Confirmed Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Orders Cancelled Successfully", Toast.LENGTH_SHORT).show();
            }
            if (disMissListener != null) {
                disMissListener.onReconfirmOrder(count);
            }
            dismiss();
        }

    }

    public void showProgress() {
        if (relativeProgress != null) {
            relativeProgress.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress() {
        if (relativeProgress != null) {
            relativeProgress.setVisibility(View.GONE);
        }
    }


}
