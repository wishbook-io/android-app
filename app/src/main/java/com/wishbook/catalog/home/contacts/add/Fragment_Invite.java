package com.wishbook.catalog.home.contacts.add;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_suppliertypes;
import com.wishbook.catalog.commonmodels.InviteContactsModel;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.postpatchmodels.GroupIDs;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vigneshkarnika on 10/05/16.
 */
public class Fragment_Invite extends DialogFragment {
    //todo sorted order
    private EditText input_buyername;
    private EditText input_phone;
    private Spinner spinner_grouptype;
    private AppCompatButton btn_discard;
    private AppCompatButton btn_add;
    SpinAdapter_suppliertypes spinAdapter_buyergroups;
    private TextView countrycodes;

    public static String countrysel="+91";
    private Spinner spinner_requesttype;



    public interface SuccessListener{
        public void OnSuccess();
        public void OnCancel();
    }

    public SuccessListener successListener;

    public void setListener(Fragment_Invite.SuccessListener listener) {
        this.successListener=listener;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if(successListener!=null){
            successListener.OnCancel();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog.setContentView(R.layout.invitesenddialog);
        spinner_grouptype=(Spinner)dialog.findViewById(R.id.spinner_grouptype);
        spinner_requesttype=(Spinner)dialog.findViewById(R.id.spinner_requesttype);
        getbuyergroups();

        btn_discard=(AppCompatButton)dialog.findViewById(R.id.btn_cancel);
        btn_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btn_add=(AppCompatButton)dialog.findViewById(R.id.btn_submit);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invite();
            }
        });
        List<String> list = new ArrayList<String>();

        if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller") ) {
            list.add("Buyer");
        }
        else if(UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")){
            list.add("Supplier");
        }
        else{
            list.add("Buyer");
            list.add("Supplier");
        }




        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_requesttype.setAdapter(dataAdapter);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));



        /*try {
            if(getArguments()!=null){
                if(getArguments().getString("type")!=null){
                    Log.i("TAG", "onCreateDialog: Type Not Null");
                    if(getArguments().getString("type").equals("supplier")){
                        spinner_requesttype.setSelection(1);
                    }
                }
            }
            else{
                spinner_requesttype.setSelection(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        return dialog;
    }


    private void invite() {

        //CRASHING HERE FIND SOME POINT
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

        InviteContactsModel inviteContactsModel=new InviteContactsModel();
        if(spinAdapter_buyergroups!=null && spinAdapter_buyergroups.getCount()>0) {
            if (((GroupIDs) spinner_grouptype.getSelectedItem()) != null) {
                inviteContactsModel.setGroup_type(((GroupIDs) spinner_grouptype.getSelectedItem()).getId());
            }
        }else{
           Toast.makeText(getActivity(),"Please select buyer group",Toast.LENGTH_LONG).show();
           return;
        }
        inviteContactsModel.setRequest_type(spinner_requesttype.getSelectedItem().toString());
        ArrayList<ArrayList<String>> invitees=new ArrayList<>();
        for (NameValues selectedContact : StaticFunctions.selectedContacts) {
            ArrayList<String> namevalue=new ArrayList<>();
            namevalue.add(selectedContact.getName());
            String phoneNumber1 = selectedContact.getPhone().replaceAll("\\D+", "");
            if (phoneNumber1.length() >= 10) {
               phoneNumber1 = phoneNumber1.substring(phoneNumber1.length() - 10, phoneNumber1.length());
            }
            namevalue.add(phoneNumber1);
            invitees.add(namevalue);
        }
        inviteContactsModel.setInvitee(invitees);
        HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(getActivity(),"contactsinvites",""), new Gson().fromJson(new Gson().toJson(inviteContactsModel), JsonObject.class), headers,false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try{
                    if(!isDetached() && isAdded()) {
                        if (successListener != null) {
                            successListener.OnSuccess();
                        }
                        Toast.makeText(getActivity(), "Added to your Network", Toast.LENGTH_SHORT).show();
                        dismiss();
                        // progressDialog.dismiss();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

            }


            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
//        HashMap<String, List<String>> paramlist=new HashMap<String, List<String>>();
//        paramlist.put("invitee", StaticFunctions.selectedContacts);
//        HashMap<String, String> params=new HashMap<>();
//        params.put("request_type", spinner_requesttype.getSelectedItem().toString());
//        params.put("group_type", ((Fragment_AddSupplier.GroupIDs) spinner_grouptype.getSelectedItem()).getId());
//        HttpManager.getInstance(getActivity()).requestwithArrayParams(HttpManager.METHOD.POSTARRAYPARAMSWITHPROGRESS, URLConstants.companyUrl(getActivity(),"contactsinvites",""), paramlist,params, headers, false, new HttpManager.customCallBack() {
//            @Override
//            public void onCacheResponse(String response) {
//                Log.v("cached response", response);
//                //  onServerResponse(response, false);
//            }
//
//            @Override
//            public void onServerResponse(String response, boolean dataUpdated) {
//                Log.v("sync response", response);
//                Toast.makeText(getActivity(), "invitation sent !", Toast.LENGTH_SHORT).show();
//                dismiss();
//            }
//
//            @Override
//            public void onResponseFailed(ErrorString error) {
//                Toast.makeText(getActivity(), error.getErrormessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

//                HashMap<String, List<String>> params=new HashMap<String, List<String>>();
//                params.put("invitee",mAdapter.getAllSelectedContacts());
//                NetworkManager.getInstance().HttpRequestArraywithHeader(getActivity(), URLConstants.INVITE, params, new NetworkManager.customCallBack() {
//                    @Override
//                    public void onCompleted(int status, String response) {
//                        if(status==NetworkManager.RESPONSESUCCESS){
//                            Toast.makeText(getActivity(),"invitation sent !",Toast.LENGTH_SHORT).show();
//                        }
//                        else{
//                            Toast.makeText(getActivity(),"request failed !",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
    }

    private void getbuyergroups() {


        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"grouptype",""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (isAdded() && !isDetached()) {
                        Type listOfProductObj = new TypeToken<ArrayList<GroupIDs>>() {
                        }.getType();
                        ArrayList<GroupIDs> buyergroups = Application_Singleton.gson.fromJson(response, listOfProductObj);
                        spinAdapter_buyergroups = new SpinAdapter_suppliertypes(getActivity(), R.layout.spinneritem, buyergroups);
                        spinner_grouptype.setAdapter(spinAdapter_buyergroups);
                        try {
                            if (buyergroups.size() > 0) {
                                for (int i = 0; i < buyergroups.size(); i++) {
                                    if (buyergroups.get(i).getName().equals("Retailer")) {
                                        spinner_grouptype.setSelection(i);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                Log.v("sync response", error.getErrormessage());
            }
        });



    }
}
