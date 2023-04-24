package com.wishbook.catalog.reseller;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.ValidationUtils;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.Advancedcompanyprofile;
import com.wishbook.catalog.commonmodels.CompanyProfile;
import com.wishbook.catalog.commonmodels.KYC_model;
import com.wishbook.catalog.commonmodels.UserInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Reseller_BankAccount extends GATrackedFragment {

    private View view;

    @BindView(R.id.kyc_bank_name)
    EditText kyc_bank_name;

    @BindView(R.id.kyc_bank_accno)
    EditText kyc_bank_accno;

    @BindView(R.id.kyc_bank_ifsc)
    EditText kyc_bank_ifsc;

    @BindView(R.id.kyc_bank_accname)
    EditText kyc_bank_accname;

    @BindView(R.id.kyc_bank_acctype)
    Spinner kyc_bank_acctype;

    @BindView(R.id.kyc_submit)
    LinearLayout kyc_submit;

    @BindView(R.id.input_default_margin)
    TextInputLayout input_default_margin;

    @BindView(R.id.edit_default_margin)
    EditText edit_default_margin;

    boolean isBankDataPresent;

    ArrayList<KYC_model> kyc_modelArrayList;

    private static String TAG = Fragment_Reseller_BankAccount.class.getSimpleName();

    String bankId;

    public Fragment_Reseller_BankAccount() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_reseller_bank_account, ga_container, true);
        ButterKnife.bind(this, view);

        getBankDetails();


        kyc_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**
                 * Wb-4425 Remove Default Margin
                 * Changes Done by Bhavik Gandhi
                 */
                /*if (edit_default_margin.getText().length() > 0) {
                    if (Double.parseDouble(edit_default_margin.getText().toString()) > 100) {
                        edit_default_margin.setError("Margin can't be more than 100%");
                        return;
                    }
                }*/


                if (kyc_bank_name.getText().length() > 0 && kyc_bank_name.getText().length() <= 1) {
                    kyc_bank_name.setError("Enter valid Bank name");
                    return;
                }

                if (kyc_bank_accno.getText().toString().isEmpty()) {
                    kyc_bank_accno.setError("Account no. can't be empty");
                    return;
                }

                if (!kyc_bank_accno.getText().toString().isEmpty() && kyc_bank_accno.getText().toString().length() < 9) {
                    kyc_bank_accno.setError("Enter valid Account no.");
                    return;
                }

                if (kyc_bank_ifsc.getText().length() > 0 && kyc_bank_ifsc.getText().length() <= 1) {
                    kyc_bank_ifsc.setError("Enter valid IFSC code");
                    return;
                }

                if (kyc_bank_ifsc.getText().length() != 11) {
                    kyc_bank_ifsc.setError("Enter valid IFSC code");
                    return;
                }

                if(!ValidationUtils.isIFSCFifthCharacterZero(kyc_bank_ifsc)) {
                    kyc_bank_ifsc.setError(getActivity().getResources().getString(R.string.ifsc_fith_should_zero_error));
                    return;
                }

                if (kyc_bank_accname.getText().length() > 0 && kyc_bank_accname.getText().length() <= 1) {
                    kyc_bank_accname.setError("Enter valid Account name");
                    return;
                }

                if (kyc_bank_acctype.getSelectedItem().toString().equalsIgnoreCase("Account type")) {
                    Toast.makeText(getActivity(), "Please Select Account type", Toast.LENGTH_SHORT).show();
                    return;
                }

                /*try {
                    if(Double.parseDouble(edit_default_margin.getText().toString()) < 999) {
                    } else {
                        Toast.makeText(getActivity(),"Resale default margin can't be greater 999%",Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                    return;
                }*/

                patchBankDetails();
            }
        });
        return view;
    }

    private void getBankDetails() {
        try {
            showProgress();
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "bank_details", ""), null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    hideProgress();
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    if (isAdded() && !isDetached()) {
                        try {
                            hideProgress();
                            Type type = new TypeToken<ArrayList<KYC_model>>() {
                            }.getType();
                            kyc_modelArrayList = Application_Singleton.gson.fromJson(response, type);
                            if (kyc_modelArrayList != null && kyc_modelArrayList.size() > 0) {
                                UserInfo.getInstance(getActivity()).setBankDetails(Application_Singleton.gson.toJson(kyc_modelArrayList));
                            }

                            /*if(UserInfo.getInstance(getActivity()).getResaleDefaultMargin()!=null) {
                                edit_default_margin.setText(UserInfo.getInstance(getActivity()).getResaleDefaultMargin());
                            } else {
                                edit_default_margin.setText("0");
                            }*/


                            bankId = kyc_modelArrayList.get(0).getId();
                            if (kyc_modelArrayList != null && kyc_modelArrayList.get(0) != null && kyc_modelArrayList.get(0).getId() != null) {
                                isBankDataPresent = true;
                                if (kyc_modelArrayList.get(0).getAccount_name() != null) {
                                    kyc_bank_accname.setText(kyc_modelArrayList.get(0).getAccount_name());
                                }
                                if (kyc_modelArrayList.get(0).getAccount_number() != null) {
                                    kyc_bank_accno.setText(kyc_modelArrayList.get(0).getAccount_number());
                                }
                                if (kyc_modelArrayList.get(0).getBank_name() != null) {
                                    kyc_bank_name.setText(kyc_modelArrayList.get(0).getBank_name());
                                }
                                if (kyc_modelArrayList.get(0).getIfsc_code() != null) {
                                    kyc_bank_ifsc.setText(kyc_modelArrayList.get(0).getIfsc_code().toUpperCase());
                                }
                                if (kyc_modelArrayList.get(0).getAccount_type() != null) {
                                    if (kyc_modelArrayList.get(0).getAccount_type().equalsIgnoreCase("Savings")) {
                                        kyc_bank_acctype.setSelection(1);
                                    }
                                    if (kyc_modelArrayList.get(0).getAccount_type().equalsIgnoreCase("Current")) {
                                        kyc_bank_acctype.setSelection(2);
                                    }
                                }

                            } else {
                                Log.e(TAG, "onServerResponse: Else ");
                                kyc_modelArrayList = new ArrayList<>();
                                KYC_model kyc_model = new KYC_model();
                                kyc_modelArrayList.add(kyc_model);
                                isBankDataPresent = false;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "onServerResponse: Catch Exception ");
                            e.printStackTrace();
                            kyc_modelArrayList = new ArrayList<>();
                            KYC_model kyc_model = new KYC_model();
                            kyc_modelArrayList.add(kyc_model);
                            isBankDataPresent = false;
                        }
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void patchBankDetails() {
        // WB-4425 Remove Default Margin
        /*if (edit_default_margin.getText().toString().length() > 0) {
            patchDefaultMargin(edit_default_margin.getText().toString());
        } else {
            patchDefaultMargin("0");
        }*/

        try {
            showProgress();
            HttpManager.METHOD method;
            String url;
            if (isBankDataPresent) {
                method = HttpManager.METHOD.PATCHJSONOBJECTWITHPROGRESS;
                url = URLConstants.companyUrl(getActivity(), "bank_details", "") + bankId + "/";
            } else {
                method = HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS;
                url = URLConstants.companyUrl(getActivity(), "bank_details", "");
            }
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            kyc_modelArrayList.get(0).setBank_name(kyc_bank_name.getText().toString());
            kyc_modelArrayList.get(0).setAccount_number(kyc_bank_accno.getText().toString());
            kyc_modelArrayList.get(0).setIfsc_code(kyc_bank_ifsc.getText().toString().toUpperCase());
            kyc_modelArrayList.get(0).setAccount_name(kyc_bank_accname.getText().toString());
            if (kyc_bank_acctype.getSelectedItemPosition() != 0) {
                kyc_modelArrayList.get(0).setAccount_type(kyc_bank_acctype.getSelectedItem().toString());
            }
            HttpManager.getInstance(getActivity()).requestwithObject(method, url, new Gson().fromJson(new Gson().toJson(kyc_modelArrayList.get(0)), JsonObject.class), headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    Log.e(TAG, "patchBankDetails: Response");
                    hideProgress();
                    try {
                        Type type = new TypeToken<KYC_model>() {
                        }.getType();
                        KYC_model kyc_models = Application_Singleton.gson.fromJson(response, type);
                        if (kyc_models != null) {
                            UserInfo.getInstance(getActivity()).setBankDetails(Application_Singleton.gson.toJson(kyc_models));
                            Toast.makeText(getActivity(), "Bank Detail Saved Successfully", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void patchDefaultMargin(final String margin) {

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        JsonObject jsonObject;
        CompanyProfile companyProfile = new CompanyProfile();
        Advancedcompanyprofile advancedcompanyprofile = new Advancedcompanyprofile();
        advancedcompanyprofile.setResale_default_margin(margin);
        companyProfile.setAdvancedcompanyprofile(advancedcompanyprofile);
        jsonObject = new Gson().fromJson(new Gson().toJson(companyProfile), JsonObject.class);
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCH, URLConstants.companyUrl(getActivity(), "", ""), jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                UserInfo.getInstance(getActivity().getApplicationContext()).setResaleDefaultMargin(margin);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.logger(error.getErrormessage());

            }
        });


    }
}

