package com.wishbook.catalog.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.smscodereader.interfaces.OTPListener;
import com.wishbook.catalog.Utils.smscodereader.receivers.OtpReader;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Countries;
import com.wishbook.catalog.login.models.Response_Success;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Fragment_ForgotPassword extends GATrackedFragment implements OTPListener {

    private static final String POPUP_OTP_TITLE = "Reset Password";
    private static final String POPUP_OTP_TITLE_LOGIN = "Mobile Verification";
    private static final String POPUP_OTP_TEXT = "";
    private EditText inputotp;

    @BindView(R.id.input_mobile)
    EditText input_mobile;
    @BindView(R.id.btn_forgotpass)
    AppCompatButton btn_forgotpass;
    @BindView(R.id.appbar)
    Toolbar appbar;
    @BindView(R.id.countrycodes)
    TextView countrycodes;

    private Countries[] countries;
    private String CountryId = "1";
    private int type = 0 ;

    public Fragment_ForgotPassword() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OtpReader.bind(Fragment_ForgotPassword.this, Constants.SENDER_NUM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        ButterKnife.bind(this, view);
        setUpToolbar((AppCompatActivity) getActivity(), appbar);
        getCountries(getActivity());
        return view;
    }

    @OnClick(R.id.btn_forgotpass)
    public void requestPassword() {
        if (input_mobile.getText().toString().length() < 10) {
            input_mobile.setError("Invalid Mobile Number");
            return;
        }
        getOtpPasswordReset();
    }

    @OnClick(R.id.countrycodes)
    public void onCountryCodesClick() {
        final PopupMenu popup = new PopupMenu(getActivity(), countrycodes);
        if (countries != null && countries.length > 0) {

            for (int i = 0; i < countries.length; i++) {
                String country = countries[i].getName();
                popup.getMenu().add(0, i, 0, country);

            }
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    CountryId = countries[item.getItemId()].getId();
                    Log.v("country selected", "" + CountryId);
                    String country = countries[item.getItemId()].getName();
                    countrycodes.setText(country);
                    return true;
                }
            });
            popup.show();

        }
    }

    public void setUpToolbar(final AppCompatActivity context, Toolbar toolbar) {
        if(getArguments()!=null) {
            if(getArguments().getString("type") !=null) {
                if(getArguments().getString("type").equals("loginOtp")) {
                    type = 1;
                } else {
                    type = 0;
                }
            }
        }
        if(type == 1) {
            toolbar.setTitle("Login with OTP");
        } else {
            toolbar.setTitle("Forgot password");
        }
        toolbar.setNavigationIcon(context.getResources().getDrawable(R.drawable.ic_toolbar_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void getOtpPasswordReset() {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone_number", input_mobile.getText().toString());
        params.put("country", CountryId);
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.PASSWORD_RESET, params, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.logger(response);
                validateOTP(input_mobile.getText().toString());
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void validateOTP(final String mobile) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dark_Dialog);
        if(type == 1) {
            alert.setTitle(POPUP_OTP_TITLE_LOGIN);
        } else {
            alert.setTitle(POPUP_OTP_TITLE);
        }

        alert.setMessage(POPUP_OTP_TEXT);

        final View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.otp_changepass, (ViewGroup) getView(), false);
        inputotp = (EditText) viewInflated.findViewById(R.id.input_otp);

        final EditText input_password = (EditText) viewInflated.findViewById(R.id.input_password);
        final EditText input_confpassword = (EditText) viewInflated.findViewById(R.id.input_confpassword);

        if(type == 1) {
            // login with OTP
            input_password.setVisibility(View.GONE);
            input_confpassword.setVisibility(View.GONE);
        } else {
            // forgot password
            input_password.setVisibility(View.VISIBLE);
            input_confpassword.setVisibility(View.VISIBLE);
        }
        alert.setView(viewInflated);
        alert.setPositiveButton("Submit", null);
        alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("Resend", null);

        final AlertDialog alertModal = alert.create();
        alertModal.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

        alertModal.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button submit_but = alertModal.getButton(AlertDialog.BUTTON_POSITIVE);
                Button neg_but = alertModal.getButton(AlertDialog.BUTTON_NEGATIVE);
                neg_but.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        getOtpPasswordReset();
                        dialog.dismiss();

                    }
                });

                submit_but.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(final View v) {

                                                      if(type == 0) {
                                                          //change by abu
                                                          if (input_password.getText().toString().equals("")) {
                                                              input_password.setError("password cannot be empty");
                                                              return;
                                                          }else if(input_password.getText().toString().length()<6){
                                                              input_password.setError("password cannot be less than 6 digits");
                                                              return;
                                                          }
                                                          if (!input_password.getText().toString().equals(input_confpassword.getText().toString())) {
                                                              input_confpassword.setError("passwords do not match");
                                                              return;
                                                          }

                                                      }

                                                      if (inputotp.getText().toString().equals("")) {
                                                          inputotp.setError("otp cannot be empty");
                                                          return;
                                                      }

                                                      final HashMap<String, String> params = new HashMap<>();
                                                      params.put("phone_number", mobile);
                                                      params.put("otp", inputotp.getText().toString());
                                                      params.put("country", CountryId);
                                                      if(type == 0) {
                                                          params.put("password", input_password.getText().toString());
                                                      }
                                                      if(type == 0){
                                                          HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.PASSWORD_RESET, params, new HttpManager.customCallBack() {

                                                              @Override
                                                              public void onCacheResponse(String response) {

                                                              }

                                                              @Override
                                                              public void onServerResponse(String response, boolean dataUpdated) {
                                                                  Response_Success response_success = Application_Singleton.gson.fromJson(response, Response_Success.class);
                                                                  if (response_success.getSuccess() != null) {
                                                                      StaticFunctions.showToast(getActivity(), "Password Updated !");
                                                                      try {
                                                                          UserInfo userInfo;
                                                                          userInfo = UserInfo.getInstance(getActivity());
                                                                          final HashMap<String, String> param = new HashMap<>();
                                                                          param.put("phone_number", mobile);
                                                                          param.put("country", CountryId);
                                                                          param.put("password",input_password.getText().toString());
                                                                          param.put("whatsapp_notifications","true");
                                                                          param.put("whatsapp_promotions","false");
                                                                          Fragment_VerifyOtp loginObject = new Fragment_VerifyOtp();
                                                                          synchronized (loginObject) {

                                                                              loginObject.loginUser(param,getActivity(),"fromReg",userInfo,input_password.getText().toString());
                                                                              //getActivity().getSupportFragmentManager().popBackStack();
                                                                          }

                                                                      } catch (Exception e) {

                                                                      }
                                                                      dialog.dismiss();
                                                                  } else {
                                                                      StaticFunctions.showToast(getActivity(), "Request Failed !");
                                                                  }
                                                              }

                                                              @Override
                                                              public void onResponseFailed(ErrorString error) {
                                                                  new MaterialDialog.Builder(getActivity())
                                                                          .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                                                                          .content(error.getErrormessage())
                                                                          .positiveText("OK")
                                                                          .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                                              @Override
                                                                              public void onClick(MaterialDialog dialog, DialogAction which) {
                                                                                  dialog.dismiss();
                                                                              }
                                                                          })
                                                                          .show();
                                                              }
                                                          });
                                                      } else {
                                                          // login with OTP
                                                          try {
                                                              UserInfo userInfo;
                                                              userInfo = UserInfo.getInstance(getActivity());
                                                              final HashMap<String, String> param = new HashMap<>();
                                                              param.put("name_or_number", mobile);
                                                              param.put("country", CountryId);
                                                              param.put("otp",inputotp.getText().toString());
                                                              Fragment_login loginObject = new Fragment_login();
                                                              synchronized (loginObject) {
                                                                  loginObject.loginUser(param,getActivity(),"fromReg",userInfo,input_password.getText().toString());
                                                                  //getActivity().getSupportFragmentManager().popBackStack();
                                                              }

                                                          } catch (Exception e) {

                                                          }
                                                          dialog.dismiss();
                                                      }


                                                  }
                                              }

                );
            }
        });
        alertModal.setCancelable(false);
        alertModal.show();
    }


    @Override
    public void otpReceived(String messageText) {
        if (messageText.contains("OTP")) {
            Pattern pattern = Pattern.compile("[0-9]+");
            Matcher matcher = pattern.matcher(messageText);
            while (matcher.find()) {
                String match = matcher.group();
                inputotp.setText(match);
            }
        }
    }

    public void getCountries(final Context context) {
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GET, URLConstants.COUNTRIES, null, null, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                countries = Application_Singleton.gson.fromJson(response, Countries[].class);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });

    }
}
