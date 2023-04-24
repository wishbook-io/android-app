package com.wishbook.catalog.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DataPasser;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.postpatchmodels.SelectionNewAdd;
import com.wishbook.catalog.commonmodels.postpatchmodels.SelectionPatch;
import com.wishbook.catalog.commonmodels.responses.Response_Selection;
import com.wishbook.catalog.home.adapters.ProductselectionAdapter;
import com.wishbook.catalog.home.adapters.SpinAdapter_ProdSel;
import com.wishbook.catalog.home.catalog.details.Fragment_ProductsInSelection;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.models.SelectionsProducts;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


public class Fragment_ProductSelections extends GATrackedFragment {

    private View v;
    public static TextView prod_selcount;
    private RecyclerViewEmptySupport mRecyclerView;
    private RadioButton rad_addto;
    private RadioButton rad_createnew;
    private RadioGroup rad_group;
    private EditText input_title;
    private Spinner list_sel;
    private MaterialDialog progressDialog;
    private SharedPreferences pref;
    public static LinearLayout linearLayout;
    Response_Selection[] response_selections = new Response_Selection[]{};

    public Fragment_ProductSelections() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_productselection, container, false);
        ArrayList<ProductObj> preseletedprods=new ArrayList<>();
       // final Toolbar toolbar = (Toolbar) v.findViewById(R.id.appbar);
        linearLayout = (LinearLayout) v.findViewById(R.id.linearLayout2);
        rad_addto=(RadioButton)v.findViewById(R.id.rad_addto);
        rad_addto=(RadioButton)v.findViewById(R.id.rad_addto);
        rad_group=(RadioGroup)v.findViewById(R.id.rad_group);
        list_sel=(Spinner)v.findViewById(R.id.list_sel);
        input_title=(EditText)v.findViewById(R.id.input_title);
        input_title.setVisibility(View.GONE);
        rad_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int checkedRadioButtonId = group.getCheckedRadioButtonId();
                if(checkedId== R.id.rad_addto){
                    input_title.setVisibility(View.GONE);
                }
                if(checkedId== R.id.rad_createnew){
                    input_title.setVisibility(View.VISIBLE);
                }
            }
        });
        rad_createnew=(RadioButton)v.findViewById(R.id.rad_createnew);
        mRecyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
       // productSelState.setUpToolbar((AppCompatActivity) getActivity(), toolbar);
        prod_selcount = (TextView) v.findViewById(R.id.prod_selcount);
        Type listOfProductObj = new TypeToken<ArrayList<ProductObj>>() {
        }.getType();
        String previousProds = Activity_Home.pref.getString("selectedProds", null);
        if (previousProds != null) {
            preseletedprods = Application_Singleton.gson.fromJson(previousProds, listOfProductObj);
            prod_selcount.setText("SELECTED PRODUCTS : " + preseletedprods.size());
            if(preseletedprods.size()>0)
            {
                prod_selcount.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                prod_selcount.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);
            }
            ProductselectionAdapter productselectionAdapter = new ProductselectionAdapter((AppCompatActivity) getActivity(),preseletedprods);
            if(productselectionAdapter.equals(""))
            {
                prod_selcount.setText("SELECTED PRODUCTS : " + preseletedprods.size());
            }
            mRecyclerView.setAdapter(productselectionAdapter);
        }


        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"selections_expand_false","")+"?type=my", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String result, boolean dataUpdated) {
                try {
                    Log.v("sync response", result);
                    Response_Selection[] response_selections = Application_Singleton.gson.fromJson(result, Response_Selection[].class);
                    if (response_selections.length > 0) {
                        if (response_selections[0].getId() != null) {
                            SpinAdapter_ProdSel spinAdapter_prodSel= new SpinAdapter_ProdSel(getActivity(), R.layout.spinneritem,response_selections);
                            list_sel.setAdapter(spinAdapter_prodSel);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
     //   if (StaticFunctions.isOnline(getActivity())) {



//            progressDialog = StaticFunctions.showProgress(getActivity());
//            progressDialog.show();
//            NetworkProcessor.with(getActivity())
//                    .load( URLConstants.companyUrl(getActivity(),"selections","")).addHeader("Authorization", StaticFunctions.getAuthString(Activity_Home.pref.getString("key", "")))
//                    .asString().setCallback(new FutureCallback<String>() {
//                @Override
//                public void onCompleted(Exception e, String result) {
//                    Log.v("res", "" + result);
//                    Response_Selection[] response_selections = productSelState.processSelRes(e, result);
//                    if (response_selections.length > 0) {
//                        if (response_selections[0].getId() != null) {
//                            SpinAdapter_ProdSel spinAdapter_prodSel= new SpinAdapter_ProdSel(getActivity(),R.layout.spinneritem,response_selections);
//                            list_sel.setAdapter(spinAdapter_prodSel);
//                        }
//                    }
//                    progressDialog.dismiss();
//                }
//            });
     //   }
        final ArrayList<ProductObj> finalPreseletedprods = preseletedprods;
        v.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(getActivity());

                if (StaticFunctions.isOnline(getActivity())) {
                    pref = getActivity().getSharedPreferences("wishbookprefs", getActivity().MODE_PRIVATE);
                    progressDialog = StaticFunctions.showProgress(getActivity());
                    progressDialog.show();
                    final ArrayList<String> productslist = new ArrayList<String>();
                    for (ProductObj productObj : finalPreseletedprods) {
                        productslist.add(productObj.getId());
                    }
                    Map<String, List<String>> params = new HashMap<String, List<String>>();
                    params.put("products", productslist);
                    String nameofselection = "";
                    String selectionID = "";


                    int checkedRadioButtonId = rad_group.getCheckedRadioButtonId();
                    if (checkedRadioButtonId == R.id.rad_createnew) {

                        if(input_title.getText().toString().equals("")|| input_title.getText() == null ){
                            Toast.makeText(getActivity(),"Please give name to new selection",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            return ;
                        }
                        else {
                            nameofselection = input_title.getText().toString();


                            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                            SelectionNewAdd selectioAdd = new SelectionNewAdd(nameofselection, productslist);
                            Gson gson1 = new Gson();
                            HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(getActivity(), "selections_expand_false", ""), (gson1.fromJson(gson1.toJson(selectioAdd), JsonObject.class)), headers, false, new HttpManager.customCallBack() {
                                @Override
                                public void onCacheResponse(String response) {

                                }

                                @Override
                                public void onServerResponse(String response, boolean dataUpdated) {
                                    try {
                                        Log.d("Succefully", "Added");
                                        progressDialog.dismiss();
                                        Activity_Home.pref.edit().putString("selectedProds", null).apply();
                                        // getActivity().getSupportFragmentManager().popBackStackImmediate("selection", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                        // getActivity().finish();

                                        SelectionPatch patch = Application_Singleton.gson.fromJson(response, SelectionPatch.class);
                                        DataPasser.selectedID = patch.getId();
                                        Application_Singleton.CONTAINER_TITLE = StringUtils.capitalize(patch.getName());
                                        Application_Singleton.CONTAINERFRAG = new Fragment_ProductsInSelection();
                                        Intent intent = new Intent(getContext(), OpenContainer.class);
                                        intent.putExtra("ordertype", "selections");
                                        intent.putExtra("toolbarCategory", OpenContainer.MYSELECTION);
                                        startActivity(intent);
                                        getActivity().finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onResponseFailed(ErrorString error) {
                                    progressDialog.dismiss();
                                    StaticFunctions.showResponseFailedDialog(error);
                                }
                            });
                        }
                    }
                    else
                    {
                        if(list_sel.getSelectedItem()!=null) {
                            selectionID = ((Response_Selection) (list_sel.getSelectedItem())).getId();
                        }
                        else
                        {
                            Toast.makeText(getActivity(),"Please select selection",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            return ;
                        }
                        final HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                        final String finalSelectionID = selectionID;
                        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"selections_expand_false",""), null, headers, false, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {
                                Log.v("cached response", response);
                                onServerResponse(response, false);
                            }

                            @Override
                            public void onServerResponse(String result, boolean dataUpdated) {
                                try {
                                    Gson gson = new Gson();
                                    SelectionsProducts[] response_products = gson.fromJson(result, SelectionsProducts[].class);
                                    if (!response_products.equals("null")) {

                                        for (int i = 0; i < response_products.length; i++) {
                                            if (finalSelectionID.equals(response_products[i].getId())) {

                                                Log.d("check", "" + response_products[i].getProducts().length);

                                                for (int f = 0; f < response_products[i].getProducts().length; f++) {
                                                    Log.d("check2", response_products[i].getProducts()[f]);
                                                    productslist.add(response_products[i].getProducts()[f]);
                                                }
                                                break;
                                            }
                                        }
                                        Log.d("RESULT", "SELECTION ID = " + finalSelectionID + " PRODUCT IDS = " + productslist.toString());

                                        SelectionPatch selectionPatch = new SelectionPatch(finalSelectionID, productslist);
                                        Gson gson1 = new Gson();
                                        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "selections_expand_false", "") + finalSelectionID + "/", gson1.fromJson(gson1.toJson(selectionPatch), JsonObject.class), headers, new HttpManager.customCallBack() {
                                            @Override
                                            public void onCacheResponse(String response) {

                                            }

                                            @Override
                                            public void onServerResponse(String response, boolean dataUpdated) {
                                                try {
                                                    Log.d("Succefully", "Patched");
                                                    progressDialog.dismiss();
                                                    Activity_Home.pref.edit().putString("selectedProds", null).apply();
                                                    //getActivity().getSupportFragmentManager().popBackStackImmediate("selection", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                                    SelectionPatch patch = Application_Singleton.gson.fromJson(response, SelectionPatch.class);
                                                    DataPasser.selectedID = patch.getId();
                                                    Application_Singleton.CONTAINER_TITLE = StringUtils.capitalize(patch.getName());
                                                    Application_Singleton.CONTAINERFRAG = new Fragment_ProductsInSelection();
                                                    Intent intent = new Intent(getContext(), OpenContainer.class);
                                                    intent.putExtra("ordertype", "selections");
                                                    intent.putExtra("toolbarCategory", OpenContainer.MYSELECTION);
                                                    startActivity(intent);
                                                    getActivity().finish();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onResponseFailed(ErrorString error) {
                                                Log.d("SORRY NOT", "Patched");
                                                StaticFunctions.showResponseFailedDialog(error);
                                            }
                                        });


                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                // progressDialog.dismiss();
                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {

                                //progressDialog.dismiss();
                            }
                        });

                    }

                }
                else{
                    StaticFunctions.showNetworkAlert(getActivity());
                }
            }
        });
        return v;
    }

}
