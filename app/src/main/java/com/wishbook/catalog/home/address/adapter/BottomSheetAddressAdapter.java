package com.wishbook.catalog.home.address.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.ShippingAddressResponse;
import com.wishbook.catalog.home.address.DeliveryAddressAddDialog;
import com.wishbook.catalog.home.address.ManageDeliveryAddressBottomDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BottomSheetAddressAdapter extends RecyclerView.Adapter<BottomSheetAddressAdapter.CustomViewHolder> {


    Context context;
    ArrayList<ShippingAddressResponse> responseArrayList;
    Type type;
    Fragment fragment;
    String previous_selected_id = null;
    boolean isReadOnlyRadio;

    public BottomSheetAddressAdapter(Context context,
                                     ArrayList<ShippingAddressResponse> responseArrayList,
                                     Fragment fragment, String previous_selected_id ,boolean isReadOnlyRadio) {
        this.context = context;
        this.responseArrayList = responseArrayList;
        this.fragment = fragment;
        this.previous_selected_id = previous_selected_id;
        type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        this.isReadOnlyRadio = isReadOnlyRadio;

    }

    @Override
    public BottomSheetAddressAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_address_item, parent, false);
        return new BottomSheetAddressAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BottomSheetAddressAdapter.CustomViewHolder holder, final int position) {
        final ShippingAddressResponse addressResponse = responseArrayList.get(position);
        holder.radioButton.setText(addressResponse.getName());
        if(isReadOnlyRadio) {
            holder.radioButton.setButtonDrawable(null);
        }

        String address_txt = addressResponse.getStreet_address() + ", " +
                addressResponse.getState().getState_name() + ", " +
                addressResponse.getCity().getCity_name() + "- " +
                addressResponse.getPincode();

        holder.txt_subtext.setText(address_txt);
        if (previous_selected_id != null) {
            if (previous_selected_id.equals(addressResponse.getId())) {
                holder.radioButton.setChecked(true);
                holder.radioButton.setTextColor(ContextCompat.getColor(context, R.color.color_primary));
            }
        }

        holder.linear_saved_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidAddress(addressResponse)) {
                    holder.radioButton.setChecked(true);
                    if (fragment instanceof ManageDeliveryAddressBottomDialog) {
                        Intent intent = new Intent()
                                .putExtra("delivery_address_id", addressResponse.getId());
                        fragment.getTargetFragment().onActivityResult(fragment.getTargetRequestCode(), Activity.RESULT_OK, intent);
                        ((ManageDeliveryAddressBottomDialog) fragment).dismiss();
                    }

                } else {
                    Toast.makeText(context, "Please Select Valid Address", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Don't show delete option for default address
        if(addressResponse.is_default()) {
            holder.txtDeleteAddress.setVisibility(View.GONE);
        } else {
            holder.txtDeleteAddress.setVisibility(View.VISIBLE);
        }


        holder.txtEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isEditMode",true);
                bundle.putString("address_id",addressResponse.getId());
                DeliveryAddressAddDialog deliveryAddressAddDialog = DeliveryAddressAddDialog.newInstance(bundle);
                deliveryAddressAddDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "Add Address");
                deliveryAddressAddDialog.setDeliveryAddAddressListener(new DeliveryAddressAddDialog.DeliveryAddAddressListener() {
                    @Override
                    public void onAdd(String address_id) {
                        if (fragment instanceof ManageDeliveryAddressBottomDialog) {
                            ((ManageDeliveryAddressBottomDialog) fragment).getAllDeliveryAddress();
                        }
                    }
                });
            }
        });



        holder.txtDeleteAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(context)
                        .title("Delete Address")
                        .content("Are you sure you want to delete address?")
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                                callDeleteAddress(addressResponse.getId(), position);
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

    }

    @Override
    public int getItemCount() {
        if (responseArrayList.size() == 0) {
            if (fragment instanceof ManageDeliveryAddressBottomDialog) {
                ((ManageDeliveryAddressBottomDialog) fragment).showEmptyLinear();
            }
        }
        return responseArrayList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.radio_btn)
        RadioButton radioButton;

        @BindView(R.id.txt_subtext)
        TextView txt_subtext;

        @BindView(R.id.txtDeleteAddress)
        TextView txtDeleteAddress;

        @BindView(R.id.txtEditAddress)
        TextView txtEditAddress;


        @BindView(R.id.linear_saved_item)
        LinearLayout linear_saved_item;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    private void callDeleteAddress(String addressid, final int position) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.DELETEWITHPROGRESS, URLConstants.companyUrl(context, "address", "") + addressid + "/", null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    responseArrayList.remove(position);
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public boolean isValidAddress(ShippingAddressResponse address1) {
        if (address1.getPincode() != null && !address1.getPincode().isEmpty() && !address1.getPincode().equalsIgnoreCase("null") && address1.getPincode().length() == 6
                && address1.getStreet_address() != null && !address1.getStreet_address().isEmpty() && !address1.getStreet_address().equalsIgnoreCase("null")
                && address1.getPhone_number() != null && !address1.getPhone_number().isEmpty() && address1.getPhone_number().length() == 10
                && address1.getName() != null && !address1.getName().isEmpty() && !address1.getName().equalsIgnoreCase("null")
                && address1.getState().getId() != null && !address1.getState().getState_name().isEmpty() && !address1.getState().getState_name().equalsIgnoreCase("-")
                && address1.getCity().getId() != null && !address1.getCity().getCity_name().isEmpty() && !address1.getCity().getCity_name().equalsIgnoreCase("-")) {
            return true;
        }
        return false;
    }


}
