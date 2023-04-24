package com.wishbook.catalog.reseller.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.ShippingAddressResponse;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResellerAddressAdapter extends RecyclerView.Adapter<ResellerAddressAdapter.CustomViewHolder> {

    Context context;
    ArrayList<ShippingAddressResponse> shippingAddressResponseArrayList;
    RecyclerView recyclerView;

    public ResellerAddressAdapter(Context context, ArrayList<ShippingAddressResponse> shippingAddressResponseArrayList, RecyclerView recyclerView) {
        this.context = context;
        this.shippingAddressResponseArrayList = shippingAddressResponseArrayList;
        this.recyclerView = recyclerView;
    }

    @Override
    public ResellerAddressAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View list = layoutInflater.inflate(R.layout.reseller_address_item, viewGroup, false);
        return new ResellerAddressAdapter.CustomViewHolder(list);
    }

    @Override
    public void onBindViewHolder(@NonNull final ResellerAddressAdapter.CustomViewHolder holder, int position) {
        ShippingAddressResponse address = shippingAddressResponseArrayList.get(position);

        String address_txt = address.getStreet_address() + ", " +
                address.getState().getState_name() + ", " +
                address.getCity().getCity_name() + "- " +
                address.getPincode();


        holder.txt_full_address.setText(address_txt);
        if (address.getName() != null) {
            holder.txt_customer_name.setText(address.getName());
        }

        if (address.getPhone_number() != null) {
            holder.txt_phone_number.setVisibility(View.VISIBLE);
            holder.txt_phone_number.setText(address.getPhone_number());
        } else {
            holder.txt_phone_number.setVisibility(View.GONE);
        }

        // Don't show delete option for default address
        if(address.is_default()) {
            holder.img_delete.setVisibility(View.GONE);
        } else {
            holder.img_delete.setVisibility(View.VISIBLE);
        }
        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                        .title("Delete Address")
                        .content("Are you sure you want to delete address?")
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                                deleteAddress(shippingAddressResponseArrayList.get(holder.getAdapterPosition()).getId(), holder.getAdapterPosition());
                            }
                        })
                        .negativeText("Cancel")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }

        });

        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return shippingAddressResponseArrayList.size();
    }


    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }


    private void deleteAddress(String addressid, final int position) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.DELETEWITHPROGRESS, URLConstants.companyUrl(context, "address", "") + addressid + "/", null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                shippingAddressResponseArrayList.remove(position);
                notifyDataSetChanged();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.txt_customer_name)
        TextView txt_customer_name;

        @BindView(R.id.img_delete)
        ImageView img_delete;

        @BindView(R.id.txt_phone_number)
        TextView txt_phone_number;

        @BindView(R.id.txt_full_address)
        TextView txt_full_address;

        @BindView(R.id.view_bottom_line)
        View view_bottom_line;


        private ResellerAddressAdapter.ItemClickListener clickListener;


        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);

        }

        public void setClickListener(ResellerAddressAdapter.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }
}

