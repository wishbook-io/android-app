package com.wishbook.catalog.home.more;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.widget.MultiSpinnerCity;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_City;
import com.wishbook.catalog.commonmodels.MultiSelectModel;
import com.wishbook.catalog.commonmodels.postpatchmodels.BuyerSegmentation;
import com.wishbook.catalog.commonmodels.postpatchmodels.GroupIDs;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;
import com.wishbook.catalog.commonmodels.responses.Response_Catagories;
import com.wishbook.catalog.home.more.adapters.MultiSelectDialog;
import com.wishbook.catalog.login.models.Response_Cities;
import com.wishbook.catalog.login.models.Response_States;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import lib.kingja.switchbutton.SwitchMultiButton;

public class Fragment_AddBuyerGroup_Version2 extends GATrackedFragment {

    @BindView(R.id.switch_type)
    SwitchMultiButton switch_type;

    @BindView(R.id.txt_input_group_name)
    TextInputLayout txt_input_group_name;
    @BindView(R.id.edit_group_name)
    EditText edit_group_name;

    @BindView(R.id.txt_input_buyertype)
    TextInputLayout txt_input_buyertype;
    @BindView(R.id.edit_buyer_type)
    EditText edit_buyer_type;

    @BindView(R.id.txt_input_state)
    TextInputLayout txt_input_state;
    @BindView(R.id.edit_state)
    EditText edit_state;

    @BindView(R.id.txt_input_city)
    TextInputLayout txt_input_city;
    @BindView(R.id.edit_city)
    EditText edit_city;

    @BindView(R.id.txt_input_category)
    TextInputLayout txt_input_category;
    @BindView(R.id.edit_category)
    EditText edit_category;


    @BindView(R.id.txt_input_buyer)
    TextInputLayout txt_input_buyer;
    @BindView(R.id.edit_buyer)
    EditText edit_buyer;

    @BindView(R.id.btn_submit)
    AppCompatButton btn_submit;

    //buyerType
    ArrayList<GroupIDs> BuyerGroupModel;
    ArrayList<String> buyerTypeSelectedIds;
    ArrayList<MultiSelectModel> buyerTypeMultiSelectModel;

    //stateType
    private Response_States[] allstates = null;
    ArrayList<String> stateSelectedIds;
    ArrayList<MultiSelectModel> stateMultiSelectModel;

    //cityType
    private Response_Cities[] allcities = null;
    ArrayList<String> citySelectedIds;
    ArrayList<MultiSelectModel> cityMultiSelectModel;

    //categoryType
    ArrayList<String> categorySelectedIds;
    ArrayList<MultiSelectModel> categoryMultiSelectModel;

    //Buyer
    ArrayList<String> buyerSelectedIds;
    ArrayList<MultiSelectModel> buyerMultiSelectModel;


    private ArrayList<String> ids = new ArrayList<>();
    private SpinAdapter_City spinAdapter_city;
    private String id = "";


    private View view;
    private Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_add_buyer_group_version2, ga_container, true);
        mContext = getActivity();
        ButterKnife.bind(this, view);
        initView();
        getStates();
        getbuyergroups();
        getCategories();
        return view;
    }


    private void initView() {

        initArrays();
        switch_type.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String s) {
                if (position == 0) {
                    showLocationTab();
                } else {
                    showCustomTab();
                    getBuyers();
                }
            }
        });


        edit_buyer_type.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    MultiSelectDialog multiSelectDialog = new MultiSelectDialog().title("Buyer Type").preSelectIDsList(buyerTypeSelectedIds).multiSelectList(buyerTypeMultiSelectModel);
                    multiSelectDialog.setCallbackListener2(new MultiSelectDialog.Listener2() {
                        @Override
                        public void onDismiss2(ArrayList<String> ids, String data, String[] arrayNames) {
                            buyerTypeSelectedIds.clear();
                            if (ids.size() > 0) {
                                buyerTypeSelectedIds.addAll(ids);
                                edit_buyer_type.setText(data);
                            } else {
                                edit_buyer_type.setText("All Type");
                            }
                        }
                    });
                    multiSelectDialog.show(getActivity().getSupportFragmentManager(), "share");
                }
                return false;
            }
        });


        edit_state.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                MultiSelectDialog multiSelectDialog = new MultiSelectDialog().title("Select State").preSelectIDsList(stateSelectedIds).multiSelectList(stateMultiSelectModel);
                multiSelectDialog.setCallbackListener2(new MultiSelectDialog.Listener2() {
                    @Override
                    public void onDismiss2(ArrayList<String> ids, String data, String[] arrayNames) {
                        stateSelectedIds.clear();
                        citySelectedIds.clear();
                        cityMultiSelectModel.clear();
                        edit_state.setText("All Cities");
                        if (ids.size() > 0) {
                            stateSelectedIds.addAll(ids);
                            edit_state.setText(data);
                        } else {
                            edit_state.setText("All States");
                        }

                        String states_ids = "";
                        for (int i = 0; i < ids.size(); i++) {
                            states_ids = states_ids + "," + ids.get(i).toString();
                        }
                        //removing first ,
                        if (states_ids.length() > 0) {
                            states_ids = states_ids.substring(1);
                            getCities(states_ids);
                        } else {
                            edit_city.setClickable(false);
                            edit_city.setEnabled(false);
                        }
                    }
                });
                multiSelectDialog.show(getActivity().getSupportFragmentManager(), "share");
                }
                return false;
            }

        });

        edit_city.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    MultiSelectDialog multiSelectDialog = new MultiSelectDialog().title("Select City").preSelectIDsList(citySelectedIds).multiSelectList(cityMultiSelectModel);
                    multiSelectDialog.setCallbackListener2(new MultiSelectDialog.Listener2() {
                        @Override
                        public void onDismiss2(ArrayList<String> ids, String data, String[] arrayNames) {
                            citySelectedIds.clear();
                            if (ids.size() > 0) {
                                citySelectedIds.addAll(ids);
                                edit_city.setText(data);
                            } else {
                                edit_city.setText("All Cities");
                            }
                        }
                    });
                    multiSelectDialog.show(getActivity().getSupportFragmentManager(), "share");
                }
                return false;
            }
        });

        edit_category.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    MultiSelectDialog multiSelectDialog = new MultiSelectDialog().title("Select Categories").preSelectIDsList(categorySelectedIds).multiSelectList(categoryMultiSelectModel);
                    multiSelectDialog.setCallbackListener2(new MultiSelectDialog.Listener2() {
                        @Override
                        public void onDismiss2(ArrayList<String> ids, String data, String[] arrayNames) {
                            //will return list of selected IDS
                            //will return list of selected IDS
                            categorySelectedIds.clear();
                            if (ids.size() > 0) {
                                categorySelectedIds.addAll(ids);
                                edit_category.setText(data);
                            } else {
                                edit_category.setText("All Categories");
                            }
                        }
                    });
                    multiSelectDialog.show(getActivity().getSupportFragmentManager(), "share");
                }
                return false;
            }
        });

        edit_buyer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    MultiSelectDialog multiSelectDialog = new MultiSelectDialog().validate(true).title("Select Buyer").preSelectIDsList(buyerSelectedIds).multiSelectList(buyerMultiSelectModel);
                    multiSelectDialog.setCallbackListener2(new MultiSelectDialog.Listener2() {
                        @Override
                        public void onDismiss2(ArrayList<String> ids, String data, String[] arrayNames) {
                            //will return list of selected IDS
                            //will return list of selected IDS
                            buyerSelectedIds.clear();
                            buyerSelectedIds.addAll(ids);
                            edit_buyer.setText(data);
                        }
                    });
                    multiSelectDialog.show(getActivity().getSupportFragmentManager(), "share");
                }
                return false;
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //return if not group name specified
                if (edit_group_name.getText().toString().equals("")) {
                    edit_group_name.setError("Please enter group name");
                    Toast.makeText(getContext(), "Please enter group name", Toast.LENGTH_SHORT).show();
                    return;
                }

                BuyerSegmentation buyerSegmentation = new BuyerSegmentation();
                buyerSegmentation.setSegmentation_name(edit_group_name.getText().toString());
                if (switch_type.getSelectedTab() == 1) {
                    //check if any buyer selected
                    if (buyerSelectedIds.size() > 0) {
                        buyerSegmentation.setBuyer_grouping_type("Custom");
                        buyerSegmentation.setBuyers(buyerSelectedIds);
                    } else {
                        Toast.makeText(getContext(), "Please select atleast one buyer", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    buyerSegmentation.setBuyer_grouping_type("Location Wise");
                    buyerSegmentation.setGroup_type(buyerTypeSelectedIds);
                    buyerSegmentation.setState(stateSelectedIds);
                    buyerSegmentation.setCity(citySelectedIds);
                    buyerSegmentation.setCategory(categorySelectedIds);
                }
                String json = new Gson().toJson(buyerSegmentation);
                postData(json);
            }

        });
        switch_type.setSelectedTab(0);
    }


    private void showLocationTab() {
        txt_input_buyertype.setVisibility(View.VISIBLE);
        txt_input_state.setVisibility(View.VISIBLE);
        txt_input_city.setVisibility(View.VISIBLE);
        txt_input_category.setVisibility(View.VISIBLE);

        txt_input_buyer.setVisibility(View.GONE);

    }

    private void initArrays() {
        //clearing everythinh
        buyerTypeSelectedIds = new ArrayList<>();
        BuyerGroupModel = new ArrayList<>();
        buyerTypeMultiSelectModel = new ArrayList<>();
        //clearing everythinh
        stateSelectedIds = new ArrayList<>();
        stateMultiSelectModel = new ArrayList<>();

        //clearing everythinh
        citySelectedIds = new ArrayList<>();
        cityMultiSelectModel = new ArrayList<>();

        //clearing everythinh
        categorySelectedIds = new ArrayList<>();
        categoryMultiSelectModel = new ArrayList<>();

        //clearing everythinh
        buyerSelectedIds = new ArrayList<>();
        buyerMultiSelectModel = new ArrayList<>();
    }

    private void showCustomTab() {
        txt_input_buyertype.setVisibility(View.GONE);
        txt_input_state.setVisibility(View.GONE);
        txt_input_city.setVisibility(View.GONE);
        txt_input_category.setVisibility(View.GONE);

        txt_input_buyer.setVisibility(View.VISIBLE);

    }

    private void getbuyergroups() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "grouptype", ""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                Log.v("sync response", response);
                Type listOfProductObj = new TypeToken<ArrayList<GroupIDs>>() {
                }.getType();
                BuyerGroupModel = Application_Singleton.gson.fromJson(response, listOfProductObj);
                for (GroupIDs groupIDs : BuyerGroupModel) {
                    MultiSelectModel model = new MultiSelectModel(groupIDs.getId(), groupIDs.getName());
                    model.setSelected(false);
                    buyerTypeMultiSelectModel.add(model);
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });


    }

    private void getCategories() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "category", "") + "?parent=10", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                if (isAdded() && !isDetached()) {
                    Response_Catagories[] response_catagories = Application_Singleton.gson.fromJson(response, Response_Catagories[].class);
                    if (response_catagories.length > 0) {
                        if (response_catagories[0].getId() != null) {
                            for (Response_Catagories response_catagories1 : response_catagories) {
                                MultiSelectModel model = new MultiSelectModel(response_catagories1.getId(), response_catagories1.getCategory_name());
                                model.setSelected(false);
                                categoryMultiSelectModel.add(model);
                            }
                        }
                    }
                }
            }


            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }

    private void getCities(String data) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "city",data), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                if (isAdded() && !isDetached()) {
                    cityMultiSelectModel.clear();
                    allcities = Application_Singleton.gson.fromJson(response, Response_Cities[].class);
                    for (Response_Cities cities : allcities) {
                        MultiSelectModel model = new MultiSelectModel(cities.getId(), cities.getCity_name());
                        model.setSelected(false);
                        cityMultiSelectModel.add(model);
                    }
                    if (allcities.length > 0) {
                        edit_city.setClickable(true);
                        edit_city.setEnabled(true);
                    }
                }
            }


            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }

    private void postData(String json) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "buyergroups", ""), new Gson().fromJson(json, JsonObject.class), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                HttpManager.getInstance(getActivity()).removeCacheParams(URLConstants.companyUrl(getActivity(), "buyergroups", ""), null);
                Toast.makeText(getActivity(), "Buyer group added successfully", Toast.LENGTH_SHORT).show();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void getStates() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GET, URLConstants.companyUrl(getActivity(), "state", "") + "?parent=10", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                if (isAdded() && !isDetached()) {
                    allstates = Application_Singleton.gson.fromJson(response, Response_States[].class);
                    for (Response_States states1 : allstates) {
                        MultiSelectModel model = new MultiSelectModel(states1.getId(), states1.getState_name());
                        model.setSelected(false);
                        stateMultiSelectModel.add(model);
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }

    private void getBuyers() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "buyers_approved", ""), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                if (isAdded()) {
                    Log.v("sync response", response);
                    Response_Buyer[] response_buyer = Application_Singleton.gson.fromJson(response, Response_Buyer[].class);
                    if (response_buyer.length > 0) {
                        for (Response_Buyer buyer : response_buyer) {
                            MultiSelectModel model = new MultiSelectModel(buyer.getBuying_company(), buyer.getBuying_company_name());
                            model.setSelected(false);
                            buyerMultiSelectModel.add(model);
                        }
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }

    private MultiSpinnerCity.MultiSpinnerListener onSelectedListener = new MultiSpinnerCity.MultiSpinnerListener() {

        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items

            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    ids.add(spinAdapter_city.getItem(i).getId().toString());
                }
            }
            Log.d("Ids", ids.toString());

            if (selected[0]) {
                id = "23511";
            }

        }
    };
}
