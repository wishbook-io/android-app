package com.wishbook.catalog.home.sellerhub.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.RequestPendingOrderItems;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.ResponseSellerPendingOrderItems;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SellerPendingOrderAdapter extends RecyclerView.Adapter<SellerPendingOrderAdapter.SellerPendingOrderViewHolder> {


    ArrayList<ResponseSellerPendingOrderItems> itemsArrayList;
    Context context;

    PendingOrderItemChangeListener pendingOrderItemChangeListener;

    public SellerPendingOrderAdapter(Context context, ArrayList<ResponseSellerPendingOrderItems> itemsArrayList) {
        this.itemsArrayList = itemsArrayList;
        this.context = context;
    }

    @Override
    public SellerPendingOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_pending_order_item, parent, false);
        return new SellerPendingOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SellerPendingOrderViewHolder customViewHolder, final int position) {
        final ResponseSellerPendingOrderItems item = itemsArrayList.get(position);
        customViewHolder.txt_brand_value.setText(item.getProduct_brand());
        customViewHolder.txt_catalog_name_value.setText(item.getProduct_catalog_title());
        customViewHolder.txt_design_value.setText(item.getProduct_title());
        customViewHolder.txt_ready_qty_value.setText(item.getReady_to_ship_qty());
        customViewHolder.txt_pending_qty_value.setText(item.getPending_qty());
        customViewHolder.txt_product_code_value.setText(item.getProduct());

        if(item.getSize()!=null) {
            customViewHolder.linear_size.setVisibility(View.VISIBLE);
            customViewHolder.txt_size_value.setText(item.getSize());
        } else {
            customViewHolder.linear_size.setVisibility(View.GONE);
        }

        if (item.getExpected_date() != null) {
            customViewHolder.txt_expected_date_value.setText(item.getExpected_date());
        } else {
            customViewHolder.txt_expected_date_value.setText("-");
        }

        if(item.getExtra_json()!=null) {
            customViewHolder.txt_pending_since_value.setText(item.getExtra_json().getPending_since()+" Days");
        }

        if (item.getProduct_image() != null) {
            StaticFunctions.loadFresco(context, item.getProduct_image(), customViewHolder.product_img);
        }

        customViewHolder.btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSellerUpdatePendingQtyDialog(context,item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    public void showSellerUpdatePendingQtyDialog(Context context,ResponseSellerPendingOrderItems data) {
        Dialog dialog = new Dialog(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.dialog_seller_update_pending_qty, null);
        dialog.setContentView(dialogLayout);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        initDialogLayout(dialogLayout, dialog,data);
        dialog.show();
    }


    public void initDialogLayout(final View dialogview, final Dialog dialog, final ResponseSellerPendingOrderItems data) {
        final Calendar myCalendar = Calendar.getInstance();
        TextView btn_negative = dialogview.findViewById(R.id.btn_negative);
        TextView btn_positive = dialogview.findViewById(R.id.btn_positive);
        LinearLayout linear_dispatch_date = dialogview.findViewById(R.id.linear_dispatch_date);
        TextView txt_dialog_title = dialogview.findViewById(R.id.txt_dialog_title);
        final TextView text_dispatch = dialogview.findViewById(R.id.text_dispatch);
        final TextView edit_dispatch_label = dialogview.findViewById(R.id.edit_dispatch_label);
        final EditText edit_ready_qty = dialogview.findViewById(R.id.edit_ready_qty);
        final EditText edit_unavailable_qty = dialogview.findViewById(R.id.edit_unavailable_qty);
        if(data.getProduct_sku()!=null) {
            txt_dialog_title.setText(String.format(context.getResources().getString(R.string.update_availability_of),data.getProduct_sku()));
        } else {
            txt_dialog_title.setText(String.format(context.getResources().getString(R.string.update_availability_of),data.getProduct_catalog_title()));
        }

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                text_dispatch.setText(sdf.format(myCalendar.getTime()));
                edit_dispatch_label.setVisibility(View.VISIBLE);
            }

        };
        linear_dispatch_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.purchase_medium_gray));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.color_primary));

            }
        });

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ready_qty = 0 , unavailable_qty = 0;

                if(!edit_ready_qty.getText().toString().isEmpty()) {
                    ready_qty = Integer.parseInt(edit_ready_qty.getText().toString());
                }

                if(!edit_ready_qty.getText().toString().isEmpty()) {
                    if(ready_qty == 0) {
                        Toast.makeText(context,"Minimum ready for pickup quantity greater 0",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!edit_unavailable_qty.getText().toString().isEmpty()) {
                        unavailable_qty = Integer.parseInt(edit_unavailable_qty.getText().toString());
                    }
                    if(ready_qty+unavailable_qty > Integer.parseInt(data.getPending_qty())) {
                        Toast.makeText(context,"Sum of Ready to ship & Unavailable Quantity should be <= Order Qty - Sum of already marked as unavailable & ready for pickup",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                boolean isAllvaluenull = false;
                if(edit_ready_qty.getText().toString().isEmpty() &&  edit_unavailable_qty.getText().toString().isEmpty() && edit_dispatch_label.getVisibility() == View.GONE ) {
                    isAllvaluenull = true;
                }

                if(!isAllvaluenull) {
                    RequestPendingOrderItems requestPendingOrderItems = new RequestPendingOrderItems();
                    requestPendingOrderItems.setAction_type("bulk_update");
                    ArrayList<RequestPendingOrderItems.Items> items = new ArrayList<RequestPendingOrderItems.Items>();
                    RequestPendingOrderItems.Items item = requestPendingOrderItems.new Items();
                    item.setId(data.getId());
                    item.setProduct_id(data.getProduct());
                    item.setOrder_item_ids(data.getExtra_json().getOrder_item_ids());
                    item.setSeller_company_id(UserInfo.getInstance(context).getCompany_id());
                    if(!edit_ready_qty.getText().toString().isEmpty()){
                        item.setReady_to_ship_qty(Integer.parseInt(edit_ready_qty.getText().toString()));
                    }

                    if(!edit_unavailable_qty.getText().toString().isEmpty()) {
                        item.setUnavailable_qty(Integer.parseInt(edit_unavailable_qty.getText().toString()));
                    }

                    String myFormat = "yyyy-MM-dd";
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                    if(edit_dispatch_label.getVisibility() == View.VISIBLE) {
                        item.setExpected_date(sdf.format(myCalendar.getTime()));
                    }

                    items.add(item);
                    requestPendingOrderItems.setItems(items);
                    postUpdateQuantity(context,requestPendingOrderItems);
                    dialog.dismiss();
                } else {
                    Toast.makeText(context,"Please enter ready quantity ",Toast.LENGTH_SHORT).show();
                    return;
                }



            }
        });

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void postUpdateQuantity(final Context context, RequestPendingOrderItems requestPendingOrderItems) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        Log.e("TAG", "postUpdateQuantity: "+Application_Singleton.gson.toJson(requestPendingOrderItems ));
        HttpManager.getInstance((Activity) context).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(context, "pending-order-item-action", ""), Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(requestPendingOrderItems), JsonObject.class), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if(context!=null) {
                    if(pendingOrderItemChangeListener!=null) {
                        pendingOrderItemChangeListener.onUpdate();
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }



    public interface PendingOrderItemChangeListener {

        void onUpdate();
    }


    public void setPendingOrderItemChangeListener(PendingOrderItemChangeListener pendingOrderItemChangeListener) {
        this.pendingOrderItemChangeListener = pendingOrderItemChangeListener;
    }

    public class SellerPendingOrderViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.txt_brand_value)
        TextView txt_brand_value;

        @BindView(R.id.product_img)
        SimpleDraweeView product_img;

        @BindView(R.id.txt_catalog_name_value)
        TextView txt_catalog_name_value;

        @BindView(R.id.txt_expected_date_value)
        TextView txt_expected_date_value;

        @BindView(R.id.btn_update)
        TextView btn_update;

        @BindView(R.id.txt_ready_qty_value)
        TextView txt_ready_qty_value;

        @BindView(R.id.txt_pending_qty_value)
        TextView txt_pending_qty_value;

        @BindView(R.id.txt_pending_since_value)
        TextView txt_pending_since_value;

        @BindView(R.id.txt_product_code_value)
        TextView txt_product_code_value;

        @BindView(R.id.txt_design_value)
        TextView txt_design_value;

        @BindView(R.id.linear_size)
        LinearLayout linear_size;

        @BindView(R.id.txt_size_value)
        TextView txt_size_value;

        public SellerPendingOrderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
