package com.wishbook.catalog.home.cart;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_City;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_State;
import com.wishbook.catalog.commonmodels.CartCatalogModel;
import com.wishbook.catalog.commonmodels.postpatchmodels.SalesOrderCreate;
import com.wishbook.catalog.commonmodels.responses.AddressResponse;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.commonmodels.responses.ShippingAddressResponse;
import com.wishbook.catalog.home.orderNew.details.Fragment_CashPayment;
import com.wishbook.catalog.login.models.Response_Cities;
import com.wishbook.catalog.login.models.Response_States;

import java.util.ArrayList;
import java.util.HashMap;


public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private Context context;
    private ArrayList<ShippingAddressResponse> addressResponses;
    private ChangeListener changeListener;
    private RecyclerView recyclerView;

    private Response_States[] allstates;
    private Response_Cities[] allcities;
    private String State = "";
    private String City = "";
    private String stateId = "";
    private String cityId = "";
    private Fragment fragment_cashPayment;
    private boolean isBrokerageOrder = false;
    private String defaultSelect;
    private boolean isDefaultChange = false;
    private CartCatalogModel cart;

    public AddressAdapter(Context context, ArrayList<ShippingAddressResponse> addressResponses, ChangeListener changeListener, RecyclerView recyclerView, Fragment_CashPayment fragment_cashPayment) {
        this.context = context;
        this.addressResponses = addressResponses;
        this.changeListener = changeListener;
        this.recyclerView = recyclerView;
        this.fragment_cashPayment = fragment_cashPayment;
    }

    public AddressAdapter(Context context, ArrayList<ShippingAddressResponse> addressResponses, ChangeListener changeListener, RecyclerView recyclerView, Fragment fragment_cashPayment, boolean isBrokerageOrder, String defaultSelect, CartCatalogModel cart) {
        this.context = context;
        this.addressResponses = addressResponses;
        this.changeListener = changeListener;
        this.recyclerView = recyclerView;
        this.fragment_cashPayment = fragment_cashPayment;
        this.isBrokerageOrder = isBrokerageOrder;
        this.defaultSelect = defaultSelect;
        this.cart = cart;
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shipping_address_item, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AddressViewHolder holder, int position) {
        String name = "", phone = "";
        if (addressResponses.get(position).getName() != null) {
            name = addressResponses.get(position).getName();
        }
        if (addressResponses.get(position).getPhone_number() != null) {
            phone = addressResponses.get(position).getPhone_number();
        }
        String address = addressResponses.get(position).getStreet_address() + ", " +
                addressResponses.get(position).getState().getState_name() + ", " +
                addressResponses.get(position).getCity().getCity_name() + "- " +
                addressResponses.get(position).getPincode();

        holder.linearEditAddress.setVisibility(View.GONE);
        String radio_text = "";
        if (!name.isEmpty()) {
            radio_text += name + "\n";
        }
        if (!phone.isEmpty()) {
            radio_text += phone + "\n";
        }
        if (!address.isEmpty()) {
            radio_text += address;
        }
        holder.checkAddress.setText(radio_text);
        holder.checkAddress.setOnCheckedChangeListener(null);
        getstates(holder.spinner_state, holder.spinner_city);
        if (addressResponses.get(position).isChecked()) {
            ShippingAddressResponse address1 = addressResponses.get(holder.getAdapterPosition());
            if (isValidAddress(address1)) {
                holder.checkAddress.setChecked(true);
            }
        } else {
            holder.checkAddress.setChecked(false);
        }

        if (defaultSelect != null && !isDefaultChange) {
            if (addressResponses.get(position).getId().equals(defaultSelect)) {
                if (isValidAddress(addressResponses.get(position))) {
                    Log.i("TAG", "onBindViewHolder: Equal Condition");
                    holder.checkAddress.setChecked(true);
                    addressResponses.get(position).setChecked(true);
                    setLogistick1(addressResponses.get(position).getId());
                /*if (isBrokerageOrder) {
                    setLogistick2(addressResponses.get(position).getId());
                } else {
                    setLogistick1(addressResponses.get(position).getId());
                }*/
                }
            }
        }
        if (addressResponses.get(position).isBuyerAddress()) {
            holder.txtEditAddress.setVisibility(View.GONE);
            holder.txtDeleteAddress.setVisibility(View.GONE);
        } else {
            if (addressResponses.get(position).is_default()) {
                //holder.txtEditAddress.setVisibility(View.GONE);
                holder.txtDeleteAddress.setVisibility(View.GONE);
            } else {
                holder.txtEditAddress.setVisibility(View.VISIBLE);
                holder.txtDeleteAddress.setVisibility(View.VISIBLE);
            }
        }
        holder.txtEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.linearEditAddress.setVisibility(View.VISIBLE);
                holder.linearEditAddress.requestFocus();
                setEditAddress(holder, addressResponses.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                // changeListener.onEditAddress(position, addressResponses.get(position));
            }
        });


        holder.txtDeleteAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (addressResponses.size() > 1) {
                    if (Application_Singleton.canUseCurrentAcitivity()) {
                        new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                .title("Delete Address")
                                .content("Are you sure you want to delete address?")
                                .positiveText("OK")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        dialog.dismiss();
                                        deleteAddress(addressResponses.get(holder.getAdapterPosition()).getId(), holder.getAdapterPosition());
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

                } else {
                    if (Application_Singleton.canUseCurrentAcitivity()) {
                        new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                .title("Delete Address")
                                .content("You can't delete all addresses")
                                .positiveText("OK")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }

                }


            }
        });
        holder.btnDiscardAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.linearEditAddress.setVisibility(View.GONE);
            }
        });

        holder.checkAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                isDefaultChange = true;
                if (b) {
                    ShippingAddressResponse address = addressResponses.get(holder.getAdapterPosition());
                    if (isValidAddress(address)) {
                        for (int i = 0; i < addressResponses.size(); i++) {
                            if (i != holder.getAdapterPosition()) {
                                addressResponses.get(i).setChecked(false);
                            } else {
                                addressResponses.get(i).setChecked(true);
                                setLogistick1(addressResponses.get(i).getId());
                            }
                        }
                        notifyDataSetChanged();
                    } else {
                        if (Application_Singleton.canUseCurrentAcitivity()) {
                            new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                    .content("Please Select Valid Address")
                                    .positiveText("OK")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            dialog.dismiss();
                                            holder.linearEditAddress.setVisibility(View.VISIBLE);
                                            holder.linearEditAddress.requestFocus();
                                            setEditAddress(holder, addressResponses.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                                        }
                                    })
                                    .show();
                        }
                    }

                } else {
                    holder.checkAddress.setChecked(false);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressResponses.size();
    }

    private void getstates(final Spinner spinner_state, final Spinner spinner_city) {
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GET, URLConstants.companyUrl(context, "state", ""), null, null, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (!((Activity) context).isFinishing()) {
                    allstates = Application_Singleton.gson.fromJson(response, Response_States[].class);
                    if (allstates != null) {
                        SpinAdapter_State spinAdapter_state = new SpinAdapter_State((Activity) context, R.layout.spinneritem, R.id.spintext, allstates);
                        spinner_state.setAdapter(spinAdapter_state);
                    }
                    if (allstates != null) {
                        if (State != null) {
                            for (int i = 0; i < allstates.length; i++) {
                                if (State.equals(allstates[i].getState_name())) {
                                    spinner_state.setSelection(i);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });

        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (allstates != null) {
                    stateId = allstates[position].getId();
                    Log.i("TAG", "onItemSelected:  State" + stateId);

                    HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GET, URLConstants.companyUrl(context, "city", stateId), null, null, false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {
                            onServerResponse(response, false);
                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            allcities = Application_Singleton.gson.fromJson(response, Response_Cities[].class);
                            if (allcities != null) {
                                SpinAdapter_City spinAdapter_city = new SpinAdapter_City((Activity) context, R.layout.spinneritem, allcities);
                                spinner_city.setAdapter(spinAdapter_city);
                                if (State != null && State != "") {
                                    if (City != null) {
                                        for (int i = 0; i < allcities.length; i++) {
                                            if (City.equals(allcities[i].getCity_name())) {
                                                spinner_city.setSelection(i);
                                                break;
                                            }

                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityId = allcities[position].getId();
                Log.i("TAG", "onItemSelected:  City" + cityId);
                HashMap<String, String> params = new HashMap<>();
                params.put("city", cityId);
                params.put("state", stateId);
                HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GET, URLConstants.companyUrl(context, "companylist", ""), params, null, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {

                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void setEditAddress(final AddressViewHolder viewHolder, final ShippingAddressResponse response, final int position) {
        if(response.getStreet_address()!=null)
            viewHolder.editAddline1.setText(response.getStreet_address());
        if(response.getPincode()!=null)
            viewHolder.editPincode.setText(response.getPincode());
        if(response.getName()!=null)
            viewHolder.edit_name.setText(response.getName());
        if(response.getPhone_number()!=null)
            viewHolder.edit_mobile_number.setText(response.getPhone_number());
        if (allstates != null) {
            if (response.getState().getState_name() != null) {
                for (int i = 0; i < allstates.length; i++) {
                    if (response.getState().getState_name().equals(allstates[i].getState_name())) {
                        viewHolder.spinner_state.setSelection(i);
                        break;
                    }
                }
            }
        }
        if (allcities != null) {
            if (response.getCity().getCity_name() != null) {
                for (int i = 0; i < allcities.length; i++) {
                    if (response.getCity().getCity_name().equals(allcities[i].getCity_name())) {
                        viewHolder.spinner_city.setSelection(i);
                        break;
                    }
                }
            }
        }

        viewHolder.btnSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isEmptyValidation(viewHolder.edit_name)) {
                    viewHolder.edit_name.requestFocus();
                    viewHolder.edit_name.setError("Please Enter Name");
                    return;
                }

                if (isEmptyValidation(viewHolder.edit_mobile_number)) {
                    viewHolder.edit_mobile_number.requestFocus();
                    viewHolder.edit_mobile_number.setError("Please Enter Phone number");
                    return;
                }

                if(!viewHolder.edit_mobile_number.getText().toString().isEmpty()) {
                    if(!isValidMobile(viewHolder.edit_mobile_number.getText().toString())){
                        viewHolder.edit_mobile_number.requestFocus();
                        viewHolder.edit_mobile_number.setError("Please Enter Valid Phone number");
                    }
                }

                if (isEmptyValidation(viewHolder.editAddline1)) {
                    viewHolder.editAddline1.requestFocus();
                    viewHolder.editAddline1.setError("Please Enter Address");
                    return;
                }

                if (stateId == "") {
                    viewHolder.spinner_state.requestFocus();
                    Toast.makeText(context, "Please select State", Toast.LENGTH_SHORT).show();
                    return;
                }


                try {
                    if (((Response_States) viewHolder.spinner_state.getSelectedItem()).getState_name().equalsIgnoreCase("-")) {
                        viewHolder.spinner_state.requestFocus();
                        Toast.makeText(context, "Please select State", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (cityId == "") {
                    viewHolder.spinner_city.requestFocus();
                    Toast.makeText(context, "Please select City", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    if (((Response_Cities) viewHolder.spinner_city.getSelectedItem()).getState_name().equalsIgnoreCase("-")) {
                        viewHolder.spinner_city.requestFocus();
                        Toast.makeText(context, "Please select City", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (isEmptyValidation(viewHolder.editPincode)) {
                    viewHolder.editPincode.requestFocus();
                    viewHolder.editPincode.setError("Please enter valid pincode");
                    return;
                }

                if (viewHolder.editPincode.getText().toString().length() != 6) {
                    viewHolder.editPincode.requestFocus();
                    viewHolder.editPincode.setError("Please enter valid pincode");
                    return;
                }
                updateAddress(viewHolder.editAddline1.getText().toString().trim(),
                        viewHolder.editAddline2.getText().toString().trim(),
                        viewHolder.editPincode.getText().toString().trim(),
                        response.getId(), viewHolder.edit_name.getText().toString(), viewHolder.edit_mobile_number.getText().toString(), position);
            }
        });


    }

    public void updateAddress(String add1, String add2, String pincode, String addressid, String name, String phone_number, final int position) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        AddressResponse address = new AddressResponse();
        address.setCity(cityId);
        address.setState(stateId);
        address.setCountry("1");
        if (!name.isEmpty()) {
            address.setName(name);
        } else {
            address.setName("Work");
        }
        address.setPhone_number(phone_number);
        address.setPincode(pincode);
        if (!add2.isEmpty()) {
            address.setStreet_address(add1 + " , " + add2);
        } else {
            address.setStreet_address(add1);
        }
        HttpManager.getInstance((Activity) context).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl((Activity) context, "address", "") + addressid + "/", Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(address), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    AddressResponse newAddress = Application_Singleton.gson.fromJson(response, AddressResponse.class);
                    ShippingAddressResponse shippingAddressResponse = new ShippingAddressResponse(newAddress.getPincode(), newAddress.getId(), newAddress.getName(), newAddress.getStreet_address(), false);
                    ShippingAddressResponse.City city = shippingAddressResponse.new City();
                    city.setId(newAddress.getCity());
                    city.setCity_name(getCityName(newAddress.getCity()));

                    ShippingAddressResponse.State state = shippingAddressResponse.new State();
                    state.setId(newAddress.getState());
                    state.setState_name(getStateName(newAddress.getState()));
                    shippingAddressResponse.setCity(city);
                    shippingAddressResponse.setState(state);


                    if (newAddress.getPhone_number() != null) {
                        shippingAddressResponse.setPhone_number(newAddress.getPhone_number());
                    }

                    if (newAddress.is_default()) {
                        shippingAddressResponse.setIs_default(true);
                    }

                    addressResponses.set(position, shippingAddressResponse);
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

    private void deleteAddress(String addressid, final int position) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.DELETEWITHPROGRESS, URLConstants.companyUrl(context, "address", "") + addressid + "/", null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                //onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                addressResponses.remove(position);
                notifyDataSetChanged();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                Log.i("TAG", "onResponseFailed: Error" + error);
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public String getCityName(String id) {
        if (allcities != null) {
            for (int i = 0; i < allcities.length; i++) {
                if (id.equals(allcities[i].getId())) {
                    return allcities[i].getCity_name();
                }
            }
        }
        return null;
    }

    public String getStateName(String id) {
        if (allstates != null) {
            for (int i = 0; i < allstates.length; i++) {
                if (id.equals(allstates[i].getId())) {
                    return allstates[i].getState_name();
                }
            }
        }
        return null;
    }

    public String getSelected() {
        for (ShippingAddressResponse address : addressResponses) {
            if (address.isChecked()) {
                return address.getId();
            }
        }
        return null;
    }

    private boolean isEmptyValidation(EditText editText) {
        if (editText.getText().toString().trim().isEmpty())
            return true;
        else
            return false;
    }

    private boolean isValidMobile(String editText) {
        if (editText.toString().trim().length() != 10)
            return false;
        else {
            if (!editText.isEmpty()) {
                if (editText.startsWith("6") ||
                        editText.startsWith("7") ||
                        editText.startsWith("8") ||
                        editText.startsWith("9")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

    }

    public void setLogistick1(String addressid) {
        SalesOrderCreate order = new SalesOrderCreate();
        order.setShip_to(addressid);
        String orderjson = new Gson().toJson(order);
        JsonObject jsonObject = new Gson().fromJson(orderjson, JsonObject.class);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        final MaterialDialog progress_dialog = StaticFunctions.showProgress((Activity) context);
        progress_dialog.show();
        HttpManager.getInstance((Activity) context).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(context, "cart", "") + cart.getId() + "/", jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if(progress_dialog!=null) {
                    progress_dialog.dismiss();
                }
                try {
                    if (fragment_cashPayment instanceof Fragment_CashPayment_2) {
                        CartCatalogModel temp = new CartCatalogModel();
                        temp = Application_Singleton.gson.fromJson(response, CartCatalogModel.class);
                        temp.setCatalogs(cart.getCatalogs());
                        ((Fragment_CashPayment_2) fragment_cashPayment).cart = temp;
                        ((Fragment_CashPayment_2) fragment_cashPayment).updateCartInvoiceUI();
                        ((Fragment_CashPayment_2) fragment_cashPayment).getShippingCharges(cart.getId());
                        ((Fragment_CashPayment_2) fragment_cashPayment).fetchPayment(cart.getId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if(progress_dialog!=null) {
                    progress_dialog.dismiss();
                }
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public boolean isValidAddress(ShippingAddressResponse address1) {
        if (address1.getPincode() != null && !address1.getPincode().isEmpty() && !address1.getPincode().equalsIgnoreCase("null") && address1.getPincode().length() == 6
                && address1.getStreet_address() != null && !address1.getStreet_address().isEmpty() && !address1.getStreet_address().equalsIgnoreCase("null")
                && address1.getState().getId() != null && !address1.getState().getState_name().isEmpty() && !address1.getState().getState_name().equalsIgnoreCase("-")
                && address1.getCity().getId() != null && !address1.getCity().getCity_name().isEmpty() && !address1.getCity().getCity_name().equalsIgnoreCase("-")) {
            return true;
        }
        return false;
    }

    public void setLogistick2(String addressid) {
        SalesOrderCreate order = new SalesOrderCreate();
        order.setShip_to(addressid);
        String orderjson = new Gson().toJson(order);
        JsonObject jsonObject = new Gson().fromJson(orderjson, JsonObject.class);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        HttpManager.getInstance((Activity) context).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(context, "brokerageorder", "") + cart.getId() + "/", jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Gson gson = new Gson();
                final Response_buyingorder selectedOrder = gson.fromJson(response, Response_buyingorder.class);
                if (isBrokerageOrder) {
                    selectedOrder.setBrokerage(true);
                }
                ((Fragment_CashPayment_2) fragment_cashPayment).getShippingCharges(selectedOrder.getId());
                ((Fragment_CashPayment_2) fragment_cashPayment).fetchPayment(selectedOrder.getId());
                ((Fragment_CashPayment_2) fragment_cashPayment).updateCartInvoiceUI();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public interface ChangeListener {
        void onEditAddress(int position, ShippingAddressResponse response);

    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {
        RadioButton checkAddress;
        TextView txtEditAddress;
        TextView txtDeleteAddress;
        AppCompatButton btnSaveAddress, btnDiscardAddress;
        EditText editAddline1, editAddline2, editPincode;
        EditText edit_name;
        EditText edit_mobile_number;
        LinearLayout linearEditAddress;
        Spinner spinner_state, spinner_city;

        public AddressViewHolder(View view) {
            super(view);
            checkAddress = view.findViewById(R.id.check_address);
            txtEditAddress = (TextView) view.findViewById(R.id.txtEditAddress);
            txtDeleteAddress = (TextView) view.findViewById(R.id.txtDeleteAddress);

            editAddline1 = (EditText) view.findViewById(R.id.edit_addline1);
            editAddline2 = (EditText) view.findViewById(R.id.edit_addline2);
            editPincode = (EditText) view.findViewById(R.id.edit_pincode);
            edit_name = view.findViewById(R.id.edit_name);
            edit_mobile_number = view.findViewById(R.id.edit_mobile_number);
            spinner_state = (Spinner) view.findViewById(R.id.spinner_state);
            spinner_city = (Spinner) view.findViewById(R.id.spinner_city);
            linearEditAddress = (LinearLayout) view.findViewById(R.id.linear_edit_address);
            btnSaveAddress = (AppCompatButton) view.findViewById(R.id.btn_save_address);
            btnDiscardAddress = (AppCompatButton) view.findViewById(R.id.btn_discard_address);
        }
    }

}
