package com.wishbook.catalog.home.more;

import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.ValidationUtils;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.CompanyProfile;
import com.wishbook.catalog.commonmodels.KYC_model;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.postpatchmodels.PostKycGst;
import com.wishbook.catalog.home.Activity_Home;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_GST_2 extends GATrackedFragment {


    @BindView(R.id.kyc_pan)
    EditText kyc_pan;

    @BindView(R.id.kyc_gst)
    EditText kyc_gst;

    @BindView(R.id.kyc_paytm)
    EditText kyc_paytm;

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

    @BindView(R.id.txt_input_paytm)
    TextInputLayout txt_input_paytm;

    @BindView(R.id.pan_layout)
    TextInputLayout pan_layout;

    boolean isGSTDataPresent;
    boolean isBankDataPresent;
    boolean isPaytmDataPresent;
    ArrayList<KYC_model> res;
    String gstId, bankId;
    private SharedPreferences pref;

    public static String TAG = "KYCDETAIL";

    public Fragment_GST_2() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.mybusiness_kyc_form, ga_container, true);
        ButterKnife.bind(this, v);

        /**
         * Remove Paytm Task Given by Gaurav sir
         * changes done by Bhavik Gandhi Feb-7-2019
         */
        txt_input_paytm.setVisibility(View.GONE);
        kyc_paytm.setVisibility(View.GONE);

        if (UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
            pan_layout.setVisibility(View.VISIBLE);
        }

        pref = getActivity().getSharedPreferences(Constants.WISHBOOK_PREFS, getActivity().MODE_PRIVATE);
      /*  kyc_paytm.setText(pref.getString("paytm_phone_number", ""));
        if (pref.getString("paytm_phone_number", "").length() > 1) {
            isPaytmDataPresent = true;
        } else {
            isPaytmDataPresent = false;
        }*/
        res = new ArrayList<>();
        KYC_model kyc_model = new KYC_model();
        res.add(kyc_model);
        isBankDataPresent = false;
        isGSTDataPresent = false;

        initListener();
        getGST();
        getBankDetails();
        return v;
    }

    public void initListener() {
        kyc_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (kyc_pan.getText().length() > 0 && kyc_pan.getText().length() != 10) {
                    kyc_pan.setError("Enter valid PAN no.");
                    return;
                }
                if (kyc_gst.getText().length() > 0 && kyc_gst.getText().length() != 15) {
                    kyc_gst.setError("Enter valid GST no.");
                    return;
                }
             /*   if (kyc_paytm.getText().length() > 0 && kyc_paytm.getText().length() != 10) {
                    kyc_paytm.setError("Enter valid Paytm no.");
                    return;
                }*/
                if (kyc_bank_name.getText().length() > 0 && kyc_bank_name.getText().length() <= 1) {
                    kyc_bank_name.setError("Enter valid Bank name");
                    return;
                }
                if (kyc_bank_accno.getText().length() > 0 && kyc_bank_accno.getText().length() <= 1) {
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
                patchBankDetails();

            }
        });
    }

    private void getGST() {
        try {
            showProgress();
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "company_kyc", ""), null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    hideProgress();
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    PostKycGst[] resPostKycGst = Application_Singleton.gson.fromJson(response, PostKycGst[].class);
                    if (resPostKycGst.length > 0 && resPostKycGst[0] != null) {
                        isGSTDataPresent = true;
                        kyc_gst.setText(resPostKycGst[0].getGstin());
                        kyc_pan.setText(resPostKycGst[0].getPan());
                        gstId = resPostKycGst[0].getId();
                    } else {
                        isGSTDataPresent = false;
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

    private void patchGST() {
        Log.e(TAG, "patchGST:Request ===" );
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.METHOD method;
            String url;
            if (isGSTDataPresent) {
                method = HttpManager.METHOD.PATCHWITHPROGRESS;
                url = URLConstants.companyUrl(getActivity(), "company_kyc", "") + gstId + "/";

                PostKycGst postKycGst = new PostKycGst();
                postKycGst.setPan(kyc_pan.getText().toString());
                postKycGst.setGstin(kyc_gst.getText().toString());
                HttpManager.getInstance(getActivity()).requestPatch(method, url, new Gson().fromJson(new Gson().toJson(postKycGst), JsonObject.class), headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Log.e(TAG, "patchGST:Response ===" );
                        PostKycGst resPostKycGst = Application_Singleton.gson.fromJson(response, PostKycGst.class);
                        if (resPostKycGst != null) {
                            UserInfo.getInstance(getActivity()).setKyc(Application_Singleton.gson.toJson(resPostKycGst));
                        }
                        try {
                            //patchPaytm();
                            if (Activity_Home.pref != null) {
                                Activity_Home.pref.edit().putString("entered_gst", null).commit();
                            } else {
                                Activity_Home.pref = StaticFunctions.getAppSharedPreferences(getActivity());
                                Activity_Home.pref.edit().putString("entered_gst", null).commit();
                            }
                            Toast.makeText(getActivity(), "Data saved successfully", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        } catch (Exception e) {
                            hideProgress();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        patchPaytm();
                    }
                });
            } else {
                HashMap<String, String> params = new HashMap<>();
                params.put("gstin", kyc_gst.getText().toString());
                params.put("pan", kyc_pan.getText().toString());
                method = HttpManager.METHOD.POSTJSONWITHPROGRESS;
                url = URLConstants.companyUrl(getActivity(), "company_kyc", "");
                HttpManager.getInstance(getActivity()).request(method, url, params, headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Log.e(TAG, "patchGST:Request Else===" );
                        Toast.makeText(getActivity(), "Data saved successfully", Toast.LENGTH_SHORT).show();
                        hideProgress();
                        getActivity().finish();
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        StaticFunctions.showResponseFailedDialog(error);
                        Toast.makeText(getActivity(), "Data saved successfully", Toast.LENGTH_SHORT).show();
                        hideProgress();
                        getActivity().finish();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


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
                    if(isAdded() && !isDetached()) {
                        try {
                            hideProgress();
                            Type type = new TypeToken<ArrayList<KYC_model>>() {
                            }.getType();
                            res = Application_Singleton.gson.fromJson(response, type);
                            if(res!=null && res.size() > 0){
                                UserInfo.getInstance(getActivity()).setBankDetails(Application_Singleton.gson.toJson(res));
                            }
                            bankId = res.get(0).getId();
                            if (res != null && res.get(0) != null && res.get(0).getId() != null) {
                                isBankDataPresent = true;
                                if (res.get(0).getAccount_name() != null) {
                                    kyc_bank_accname.setText(res.get(0).getAccount_name());
                                }
                                if (res.get(0).getAccount_number() != null) {
                                    kyc_bank_accno.setText(res.get(0).getAccount_number());
                                }
                                if (res.get(0).getBank_name() != null) {
                                    kyc_bank_name.setText(res.get(0).getBank_name());
                                }
                                if (res.get(0).getIfsc_code() != null) {
                                    kyc_bank_ifsc.setText(res.get(0).getIfsc_code().toUpperCase());
                                }
                                if (res.get(0).getAccount_type() != null) {
                                    if (res.get(0).getAccount_type().equalsIgnoreCase("Savings")) {
                                        kyc_bank_acctype.setSelection(1);
                                    }
                                    if (res.get(0).getAccount_type().equalsIgnoreCase("Current")) {
                                        kyc_bank_acctype.setSelection(2);
                                    }
                                }

                            } else {
                                res = new ArrayList<>();
                                KYC_model kyc_model = new KYC_model();
                                res.add(kyc_model);
                                isBankDataPresent = false;
                            }
                        } catch (Exception e) {
                            res = new ArrayList<>();
                            KYC_model kyc_model = new KYC_model();
                            res.add(kyc_model);
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
        Log.e(TAG, "patchBankDetails: Request" );
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
            res.get(0).setBank_name(kyc_bank_name.getText().toString());
            res.get(0).setAccount_number(kyc_bank_accno.getText().toString());
            res.get(0).setIfsc_code(kyc_bank_ifsc.getText().toString().toUpperCase());
            res.get(0).setAccount_name(kyc_bank_accname.getText().toString());
            if (kyc_bank_acctype.getSelectedItemPosition() != 0) {
                res.get(0).setAccount_type(kyc_bank_acctype.getSelectedItem().toString());
            }
            HttpManager.getInstance(getActivity()).requestwithObject(method, url, new Gson().fromJson(new Gson().toJson(res.get(0)), JsonObject.class), headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    Log.e(TAG, "patchBankDetails: Response" );
                    hideProgress();
                    patchGST();
                    try {
                        Type type = new TypeToken<KYC_model>() {
                        }.getType();
                        KYC_model kyc_models = Application_Singleton.gson.fromJson(response, type);
                        if (kyc_models!=null) {
                            UserInfo.getInstance(getActivity()).setBankDetails(Application_Singleton.gson.toJson(kyc_models));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    StaticFunctions.showResponseFailedDialog(error);
                    patchGST();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void patchPaytm() {
        Log.e(TAG, "patchPaytm: Request" );
        try {
            HttpManager.METHOD method;
            String url;
            method = HttpManager.METHOD.PATCHJSONOBJECTWITHPROGRESS;
            url = URLConstants.COMPANY_URL + UserInfo.getInstance(getContext()).getCompany_id() + "/";
            CompanyProfile companyProfile = new CompanyProfile();
            companyProfile.setPaytm_phone_number(kyc_paytm.getText().toString());
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HttpManager.getInstance(getActivity()).requestwithObject(method, url, new Gson().fromJson(new Gson().toJson(companyProfile), JsonObject.class), headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    hideProgress();
                    getActivity().finish();
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    Log.e(TAG, "patchPaytm: Response" );
                    pref.edit().putString("paytm_phone_number", kyc_paytm.getText().toString()).commit();
                    Toast.makeText(getActivity(), "Data saved successfully", Toast.LENGTH_SHORT).show();
                    hideProgress();
                    getActivity().finish();
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    pref.edit().putString("paytm_phone_number", kyc_paytm.getText().toString()).commit();
                    Toast.makeText(getContext(), "Request Failed!", Toast.LENGTH_LONG).show();
                    hideProgress();
                    getActivity().finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
