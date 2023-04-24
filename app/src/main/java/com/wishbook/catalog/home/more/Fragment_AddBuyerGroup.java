package com.wishbook.catalog.home.more;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
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
import com.wishbook.catalog.Utils.networking.NetworkManager;
import com.wishbook.catalog.Utils.widget.MultiSpinnerCity;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_City;
import com.wishbook.catalog.commonmodels.MultiSelectModel;
import com.wishbook.catalog.commonmodels.postpatchmodels.BuyerSegmentation;
import com.wishbook.catalog.commonmodels.postpatchmodels.GroupIDs;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;
import com.wishbook.catalog.commonmodels.responses.Response_Catagories;
import com.wishbook.catalog.home.more.adapters.MultiSelectDialog;
import com.wishbook.catalog.home.more.adapters.treeview.FileTreeDialogAdapter;
import com.wishbook.catalog.home.more.adapters.treeview.bean.FileBean;
import com.wishbook.catalog.login.models.Response_Cities;
import com.wishbook.catalog.login.models.Response_States;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Vigneshkarnika on 10/05/16.
 */
public class Fragment_AddBuyerGroup extends GATrackedFragment {

    private FileTreeDialogAdapter mTreeAdapter;

    private EditText input_groupname;
    private EditText spinner_buyertype;
    private AppCompatButton btn_discard;
    private AppCompatButton btn_add;
    private SpinAdapter_City spinAdapter_city;
    private TextView countrycodes;


    public static String countrysel = "+91";
    private ArrayList<FileBean> mDatas;
    private ArrayList<String> ids = new ArrayList<>();
    private String id = "";

    @BindView(R.id.buyer_type_container)
    TextInputLayout buyer_type_container;

    @BindView(R.id.state_container)
    TextInputLayout state_container;

    @BindView(R.id.city_container)
    TextInputLayout city_container;

    @BindView(R.id.buyer_container)
    TextInputLayout buyer_container;

    @BindView(R.id.category_container)
    TextInputLayout category_container;

    @BindView(R.id.text_location_wise)
    TextView text_location_wise;

    @BindView(R.id.text_custom)
    TextView text_custom;

    //buyerType
    ArrayList<GroupIDs> BuyerGroupModel;
    ArrayList<String> buyerTypeSelectedIds;
    ArrayList<MultiSelectModel> buyerTypeMultiSelectModel;

    //stateType
    EditText spinner_states;
    private Response_States[] allstates = null;
    ArrayList<String> stateSelectedIds;
    ArrayList<MultiSelectModel> stateMultiSelectModel;

    //cityType
    EditText spinner_city;
    private Response_Cities[] allcities = null;
    ArrayList<String> citySelectedIds;
    ArrayList<MultiSelectModel> cityMultiSelectModel;

    //categoryType
    EditText spinner_category;
    ArrayList<String> categorySelectedIds;
    ArrayList<MultiSelectModel> categoryMultiSelectModel;

    //Buyer
    EditText spinner_buyer;
    ArrayList<String> buyerSelectedIds;
    ArrayList<MultiSelectModel> buyerMultiSelectModel;

    SwitchCompat create_group_indicator;

    public interface DismissListener{
        public void onDismiss();
    }

    DismissListener dismissListener;

    public void setListener(DismissListener dismissListener){
        this.dismissListener = dismissListener;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.addbuyergroupdialog, ga_container, true);
        spinner_buyertype = (EditText) view.findViewById(R.id.spinner_buyertype);
        input_groupname = (EditText) view.findViewById(R.id.input_groupname);
        spinner_states = (EditText) view.findViewById(R.id.spinner_state);
        countrycodes = (TextView) view.findViewById(R.id.countrycodes);
        spinner_city = (EditText) view.findViewById(R.id.spinner_city);
        spinner_category = (EditText) view.findViewById(R.id.spinner_category);
        spinner_buyer = (EditText) view.findViewById(R.id.spinner_buyer);
        create_group_indicator = (SwitchCompat) view.findViewById(R.id.create_group_indicator);

        ButterKnife.bind(this, view);
        //CLEAR and Assign things
        clearEverything();

        //getting Buyer Groups
        getbuyergroups();

        //getting Buyer Groups
        getCategories();


        create_group_indicator.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //buyerTypeSelectedIds.clear();
                    buyerMultiSelectModel.clear();

                    text_custom.setTextColor(ContextCompat.getColor(getContext(), R.color.color_primary));
                    text_location_wise.setTextColor(ContextCompat.getColor(getContext(), R.color.second_grey));

                    buyer_container.setVisibility(View.VISIBLE);
                    buyer_type_container.setVisibility(View.GONE);
                    state_container.setVisibility(View.GONE);
                    city_container.setVisibility(View.GONE);
                    category_container.setVisibility(View.GONE);


                    getBuyers();
                } else {

                    text_custom.setTextColor(ContextCompat.getColor(getContext(), R.color.second_grey));
                    text_location_wise.setTextColor(ContextCompat.getColor(getContext(), R.color.color_primary));


                   /* categorySelectedIds.clear();
                    buyerTypeSelectedIds.clear();
                    citySelectedIds.clear();
                    buyerTypeSelectedIds.clear();*/

                    buyer_container.setVisibility(View.GONE);
                    buyer_type_container.setVisibility(View.VISIBLE);
                    state_container.setVisibility(View.VISIBLE);
                    city_container.setVisibility(View.VISIBLE);
                    category_container.setVisibility(View.VISIBLE);
                }
            }
        });

        spinner_buyertype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiSelectDialog multiSelectDialog = new MultiSelectDialog().title("Buyer Type").preSelectIDsList(buyerTypeSelectedIds).multiSelectList(buyerTypeMultiSelectModel);
                multiSelectDialog.setCallbackListener(new MultiSelectDialog.Listener() {
                    @Override
                    public void onDismiss(ArrayList<String> ids, String data, String[] arrayNames) {
                        //will return list of selected IDS
                        //will return list of selected IDS
                        buyerTypeSelectedIds.clear();
                        if (ids.size() > 0) {
                            buyerTypeSelectedIds.addAll(ids);
                            spinner_buyertype.setText(data);
                        } else {
                            spinner_buyertype.setText("All Type");
                        }
                    }
                });
                multiSelectDialog.show(getActivity().getSupportFragmentManager(), "share");
            }
        });

        spinner_states.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiSelectDialog multiSelectDialog = new MultiSelectDialog().title("Select State").preSelectIDsList(stateSelectedIds).multiSelectList(stateMultiSelectModel);
                multiSelectDialog.setCallbackListener(new MultiSelectDialog.Listener() {
                    @Override
                    public void onDismiss(ArrayList<String> ids, String data, String[] arrayNames) {
                        //will return list of selected IDS
                        //will return list of selected IDS
                        stateSelectedIds.clear();


                        //clear previous selected cities on state changed
                        citySelectedIds.clear();
                        cityMultiSelectModel.clear();
                        spinner_city.setText("All Cities");

                        if (ids.size() > 0) {
                            stateSelectedIds.addAll(ids);
                            spinner_states.setText(data);
                        } else {
                            spinner_states.setText("All States");
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
                            spinner_city.setClickable(false);
                            spinner_city.setEnabled(false);
                        }
                    }
                });
                multiSelectDialog.show(getActivity().getSupportFragmentManager(), "share");
            }
        });

        spinner_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiSelectDialog multiSelectDialog = new MultiSelectDialog().title("Select City").preSelectIDsList(citySelectedIds).multiSelectList(cityMultiSelectModel);
                multiSelectDialog.setCallbackListener(new MultiSelectDialog.Listener() {
                    @Override
                    public void onDismiss(ArrayList<String> ids, String data, String[] arrayNames) {
                        //will return list of selected IDS
                        //will return list of selected IDS
                        citySelectedIds.clear();
                        if (ids.size() > 0) {
                            citySelectedIds.addAll(ids);
                            spinner_city.setText(data);
                        } else {
                            spinner_city.setText("All Cities");
                        }
                    }
                });
                multiSelectDialog.show(getActivity().getSupportFragmentManager(), "share");
            }
        });

        spinner_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiSelectDialog multiSelectDialog = new MultiSelectDialog().title("Select Categories").preSelectIDsList(categorySelectedIds).multiSelectList(categoryMultiSelectModel);
                multiSelectDialog.setCallbackListener(new MultiSelectDialog.Listener() {
                    @Override
                    public void onDismiss(ArrayList<String> ids, String data, String[] arrayNames) {
                        //will return list of selected IDS
                        //will return list of selected IDS
                        categorySelectedIds.clear();
                        if (ids.size() > 0) {
                            categorySelectedIds.addAll(ids);
                            spinner_category.setText(data);
                        } else {
                            spinner_category.setText("All Categories");
                        }
                    }
                });
                multiSelectDialog.show(getActivity().getSupportFragmentManager(), "share");
            }
        });

        spinner_buyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiSelectDialog multiSelectDialog = new MultiSelectDialog().validate(true).title("Select Buyer").preSelectIDsList(buyerSelectedIds).multiSelectList(buyerMultiSelectModel);
                multiSelectDialog.setCallbackListener(new MultiSelectDialog.Listener() {
                    @Override
                    public void onDismiss(ArrayList<String> ids, String data, String[] arrayNames) {
                        //will return list of selected IDS
                        //will return list of selected IDS
                        buyerSelectedIds.clear();
                        buyerSelectedIds.addAll(ids);
                        spinner_buyer.setText(data);
                    }
                });
                multiSelectDialog.show(getActivity().getSupportFragmentManager(), "share");
            }
        });
        NetworkManager.getInstance().HttpRequestwithHeader(getActivity(), NetworkManager.GET, URLConstants.companyUrl(getActivity(), "state", ""), null, new NetworkManager.customCallBack() {
            @Override
            public void onCompleted(int status, String response) {
                if (status == NetworkManager.RESPONSESUCCESS) {
                    allstates = Application_Singleton.gson.fromJson(response, Response_States[].class);
                    for (Response_States states1 : allstates) {
                        MultiSelectModel model = new MultiSelectModel(states1.getId(), states1.getState_name());
                        model.setSelected(false);
                        stateMultiSelectModel.add(model);
                    }
                }
            }
        });


        btn_discard = (AppCompatButton) view.findViewById(R.id.btn_discard);
        btn_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getActivity().finish();
            }
        });
        btn_add = (AppCompatButton) view.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //return if not group name specified
                if (input_groupname.getText().toString().equals("")) {
                    input_groupname.setError("Please enter group name");
                    Toast.makeText(getContext(), "Please enter group name", Toast.LENGTH_SHORT).show();
                    return;
                }

                BuyerSegmentation buyerSegmentation = new BuyerSegmentation();
                buyerSegmentation.setSegmentation_name(input_groupname.getText().toString());
                if (create_group_indicator.isChecked()) {
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
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "buyergroups", ""), new Gson().fromJson(json, JsonObject.class), headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        Log.v("cached response", response);

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Log.v("sync response", response);


                        clearEverything();

                        HttpManager.getInstance(getActivity()).removeCacheParams(URLConstants.companyUrl(getActivity(), "buyergroups", ""), null);
                        Toast.makeText(getActivity(), "Buyer group added successfully", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        StaticFunctions.showResponseFailedDialog(error);
                    }
                });
            }
        });
        return view;
    }

   /* @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog.setContentView(R.layout.addbuyergroupdialog);

        ButterKnife.bind(this, dialog);
        spinner_buyertype = (EditText) dialog.findViewById(R.id.spinner_buyertype);
        input_groupname = (EditText) dialog.findViewById(R.id.input_groupname);
        spinner_states = (EditText) dialog.findViewById(R.id.spinner_state);
        countrycodes = (TextView) dialog.findViewById(R.id.countrycodes);
        spinner_city = (EditText) dialog.findViewById(R.id.spinner_city);
        spinner_category = (EditText) dialog.findViewById(R.id.spinner_category);
        spinner_buyer = (EditText) dialog.findViewById(R.id.spinner_buyer);
        create_group_indicator = (SwitchCompat) dialog.findViewById(R.id.create_group_indicator);

        //CLEAR and Assign things
        clearEverything();

        //getting Buyer Groups
        getbuyergroups();

        //getting Buyer Groups
        getCategories();


        create_group_indicator.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //buyerTypeSelectedIds.clear();
                    buyerMultiSelectModel.clear();

                    text_custom.setTextColor(ContextCompat.getColor(getContext(), R.color.color_primary));
                    text_location_wise.setTextColor(ContextCompat.getColor(getContext(), R.color.second_grey));

                    buyer_container.setVisibility(View.VISIBLE);
                    buyer_type_container.setVisibility(View.GONE);
                    state_container.setVisibility(View.GONE);
                    city_container.setVisibility(View.GONE);
                    category_container.setVisibility(View.GONE);


                    getBuyers();
                } else {

                    text_custom.setTextColor(ContextCompat.getColor(getContext(), R.color.second_grey));
                    text_location_wise.setTextColor(ContextCompat.getColor(getContext(), R.color.color_primary));


                   *//* categorySelectedIds.clear();
                    buyerTypeSelectedIds.clear();
                    citySelectedIds.clear();
                    buyerTypeSelectedIds.clear();*//*

                    buyer_container.setVisibility(View.GONE);
                    buyer_type_container.setVisibility(View.VISIBLE);
                    state_container.setVisibility(View.VISIBLE);
                    city_container.setVisibility(View.VISIBLE);
                    category_container.setVisibility(View.VISIBLE);
                }
            }
        });

        spinner_buyertype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiSelectDialog multiSelectDialog = new MultiSelectDialog().title("Buyer Type").preSelectIDsList(buyerTypeSelectedIds).multiSelectList(buyerTypeMultiSelectModel);
                multiSelectDialog.setCallbackListener(new MultiSelectDialog.Listener() {
                    @Override
                    public void onDismiss(ArrayList<String> ids, String data, String[] arrayNames) {
                        //will return list of selected IDS
                        //will return list of selected IDS
                        buyerTypeSelectedIds.clear();
                        if (ids.size() > 0) {
                            buyerTypeSelectedIds.addAll(ids);
                            spinner_buyertype.setText(data);
                        } else {
                            spinner_buyertype.setText("All Type");
                        }
                    }
                });
                multiSelectDialog.show(getActivity().getSupportFragmentManager(), "share");
            }
        });

        spinner_states.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiSelectDialog multiSelectDialog = new MultiSelectDialog().title("Select State").preSelectIDsList(stateSelectedIds).multiSelectList(stateMultiSelectModel);
                multiSelectDialog.setCallbackListener(new MultiSelectDialog.Listener() {
                    @Override
                    public void onDismiss(ArrayList<String> ids, String data, String[] arrayNames) {
                        //will return list of selected IDS
                        //will return list of selected IDS
                        stateSelectedIds.clear();


                        //clear previous selected cities on state changed
                        citySelectedIds.clear();
                        cityMultiSelectModel.clear();
                        spinner_city.setText("All Cities");

                        if (ids.size() > 0) {
                            stateSelectedIds.addAll(ids);
                            spinner_states.setText(data);
                        } else {
                            spinner_states.setText("All States");
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
                            spinner_city.setClickable(false);
                            spinner_city.setEnabled(false);
                        }
                    }
                });
                multiSelectDialog.show(getActivity().getSupportFragmentManager(), "share");
            }
        });

        spinner_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiSelectDialog multiSelectDialog = new MultiSelectDialog().title("Select City").preSelectIDsList(citySelectedIds).multiSelectList(cityMultiSelectModel);
                multiSelectDialog.setCallbackListener(new MultiSelectDialog.Listener() {
                    @Override
                    public void onDismiss(ArrayList<String> ids, String data, String[] arrayNames) {
                        //will return list of selected IDS
                        //will return list of selected IDS
                        citySelectedIds.clear();
                        if (ids.size() > 0) {
                            citySelectedIds.addAll(ids);
                            spinner_city.setText(data);
                        } else {
                            spinner_city.setText("All Cities");
                        }
                    }
                });
                multiSelectDialog.show(getActivity().getSupportFragmentManager(), "share");
            }
        });

        spinner_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiSelectDialog multiSelectDialog = new MultiSelectDialog().title("Select Categories").preSelectIDsList(categorySelectedIds).multiSelectList(categoryMultiSelectModel);
                multiSelectDialog.setCallbackListener(new MultiSelectDialog.Listener() {
                    @Override
                    public void onDismiss(ArrayList<String> ids, String data, String[] arrayNames) {
                        //will return list of selected IDS
                        //will return list of selected IDS
                        categorySelectedIds.clear();
                        if (ids.size() > 0) {
                            categorySelectedIds.addAll(ids);
                            spinner_category.setText(data);
                        } else {
                            spinner_category.setText("All Categories");
                        }
                    }
                });
                multiSelectDialog.show(getActivity().getSupportFragmentManager(), "share");
            }
        });

        spinner_buyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiSelectDialog multiSelectDialog = new MultiSelectDialog().validate(true).title("Select Buyer").preSelectIDsList(buyerSelectedIds).multiSelectList(buyerMultiSelectModel);
                multiSelectDialog.setCallbackListener(new MultiSelectDialog.Listener() {
                    @Override
                    public void onDismiss(ArrayList<String> ids, String data, String[] arrayNames) {
                        //will return list of selected IDS
                        //will return list of selected IDS
                        buyerSelectedIds.clear();
                        buyerSelectedIds.addAll(ids);
                        spinner_buyer.setText(data);
                    }
                });
                multiSelectDialog.show(getActivity().getSupportFragmentManager(), "share");
            }
        });
        NetworkManager.getInstance().HttpRequestwithHeader(getActivity(), NetworkManager.GET, URLConstants.companyUrl(getActivity(), "state", ""), null, new NetworkManager.customCallBack() {
            @Override
            public void onCompleted(int status, String response) {
                if (status == NetworkManager.RESPONSESUCCESS) {
                    allstates = Application_Singleton.gson.fromJson(response, Response_States[].class);
                    for (Response_States states1 : allstates) {
                        MultiSelectModel model = new MultiSelectModel(states1.getId(), states1.getState_name());
                        model.setSelected(false);
                        stateMultiSelectModel.add(model);
                    }
                }
            }
        });


        btn_discard = (AppCompatButton) dialog.findViewById(R.id.btn_discard);
        btn_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btn_add = (AppCompatButton) dialog.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //return if not group name specified
                if (input_groupname.getText().toString().equals("")) {
                    input_groupname.setError("Please enter group name");
                    Toast.makeText(getContext(), "Please enter group name", Toast.LENGTH_SHORT).show();
                    return;
                }

                BuyerSegmentation buyerSegmentation = new BuyerSegmentation();
                buyerSegmentation.setSegmentation_name(input_groupname.getText().toString());
                if (create_group_indicator.isChecked()) {
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
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "buyergroups", ""), new Gson().fromJson(json, JsonObject.class), headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        Log.v("cached response", response);

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Log.v("sync response", response);


                        clearEverything();

                        HttpManager.getInstance(getActivity()).removeCacheParams(URLConstants.companyUrl(getActivity(), "buyergroups", ""), null);
                        Toast.makeText(getActivity(), "Buyer group added successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        Log.v("sync response", error.getErrormessage());
                    }
                });
            }
        });


        dialog.getWindow().

                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.getWindow().

                setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }*/

    private void clearEverything() {
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

    private void getCategories() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "category", "") + "?parent=10", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
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


            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }

    private void getCities(String data) {
        NetworkManager.getInstance().HttpRequestwithHeader(getActivity(), NetworkManager.GET, URLConstants.companyUrl(getActivity(), "city", data), null, new NetworkManager.customCallBack() {
            @Override
            public void onCompleted(int status, String response) {
                if (status == NetworkManager.RESPONSESUCCESS) {
                    cityMultiSelectModel.clear();
                    allcities = Application_Singleton.gson.fromJson(response, Response_Cities[].class);
                    for (Response_Cities cities : allcities) {
                        MultiSelectModel model = new MultiSelectModel(cities.getId(), cities.getCity_name());
                        model.setSelected(false);
                        cityMultiSelectModel.add(model);
                    }
                    if (allcities.length > 0) {
                        spinner_city.setClickable(true);
                        spinner_city.setEnabled(true);
                    }
                }
            }
        });

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
                Log.v("sync response", error.getErrormessage());
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

    /*  MultiSelectDialog multiSelectDialog = new MultiSelectDialog().title("Brand I Sell").preSelectIDsList(alreadySelected).multiSelectList(model);
                            multiSelectDialog.setCallbackListener(new MultiSelectDialog.Listener()

    {
        @Override
        public void onDismiss (ArrayList < String > ids) {
        //will return list of selected IDS
        //will return list of selected IDS
        if (responseSell.length > 0) {
            PatchDistibuter(responseSell[0].getId(), ids);
        } else {
            AddDistibuter(ids);
        }
    }
    });
                            multiSelectDialog.show(getActivity().getSupportFragmentManager(), "share");
*/
}
