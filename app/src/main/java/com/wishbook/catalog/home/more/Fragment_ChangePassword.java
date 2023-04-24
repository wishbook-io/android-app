package com.wishbook.catalog.home.more;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.networking.NetworkManager;
import com.wishbook.catalog.commonmodels.UserInfo;

import java.util.HashMap;

public class Fragment_ChangePassword extends GATrackedFragment {

    private View v;
    private EditText newPswdEt, cnfrmPswdEt;
    private AppCompatButton changePswdBttn;
    private String newPswd1, newPswd2, currentPswd;
    private SharedPreferences pref;
    private Toolbar toolbar;

    public Fragment_ChangePassword() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_change_password, container, false);
        toolbar=(Toolbar)v.findViewById(R.id.appbar);


        toolbar.setNavigationIcon(getActivity().getResources().getDrawable(R.drawable.ic_toolbar_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        pref = getActivity().getSharedPreferences(Constants.WISHBOOK_PREFS, getActivity().MODE_PRIVATE);


      //  oldPswdEt = (EditText) v.findViewById(R.id.old_pswd);
        newPswdEt = (EditText) v.findViewById(R.id.new_pswd);
        cnfrmPswdEt = (EditText) v.findViewById(R.id.cnfrm_pswd);
        TextInputLayout txt_confirm_input = v.findViewById(R.id.txt_confirm_input);

        changePswdBttn = (AppCompatButton) v.findViewById(R.id.btn_change_pswd);
        if(UserInfo.getInstance(getActivity()).isPasswordSet()) {
            toolbar.setTitle("Change Password");
        } else {
            toolbar.setTitle("Set Password");
            cnfrmPswdEt.setHint("");
            txt_confirm_input.setHint("Confirm Password");
            changePswdBttn.setText("Set Password");
        }
        changePswdBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  oldPswd = oldPswdEt.getText().toString();
                newPswd1 = newPswdEt.getText().toString();
                newPswd2 = cnfrmPswdEt.getText().toString();
                changePswd(newPswd1, newPswd2);
            }
        });
        toolbar.setVisibility(View.GONE);
        return v;
    }

    private void changePswd(final String newPswd1, final String newPswd2) {
        if (validationsCheck(newPswd1, newPswd2)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("new_password1", newPswd1);
            params.put("new_password2", newPswd2);
            NetworkManager.getInstance().HttpRequestwithHeader(getActivity(), NetworkManager.POST, URLConstants.CHANGE_PASSWORD, params, new NetworkManager.customCallBack() {
                @Override
                public void onCompleted(int status, String response) {
                    if (status == NetworkManager.RESPONSESUCCESS) {
                        pref.edit().putString("password", newPswd1).apply();
                        Snackbar.make(v, "Password changed successfully", Snackbar.LENGTH_LONG).show();
                        if(toolbar.getTitle().equals("Set Password")) {
                            UserInfo.getInstance(getActivity()).setPasswordset(true);
                        }
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    } else {
                        Snackbar.make(v, "Request failed ! ", Snackbar.LENGTH_LONG).show();
                    }
                }
            });


        }
    }

    private boolean validationsCheck(String newPswd1, String newPswd2) {

        if (newPswd1.equals("")) {
            newPswdEt.setError(getActivity().getResources().getString(R.string.field_empty));
            return false;
        }else if(newPswd1.length()<6){
            newPswdEt.setError(getActivity().getResources().getString(R.string.password_less_than_6));
            return false;
        }

        if (newPswd2.equals("")) {
            cnfrmPswdEt.setError(getActivity().getResources().getString(R.string.field_empty));
            return false;
        }

        currentPswd = pref.getString("password", "");
        Log.v("CurrentPswd", "" + currentPswd);
       /* if (oldPswd.equals(currentPswd)) {
            oldPswdEt.setError(null);
            if (newPswd1.equals(newPswd2)) {
                cnfrmPswdEt.setError(null);


            } else {
                cnfrmPswdEt.setError(getActivity().getResources().getString(R.string.pswds_nt_matched));
                return false;
            }
        } else {
            oldPswdEt.setError(getActivity().getResources().getString(R.string.current_pswd_not_matched));
            return false;
        }*/
       if(newPswd1.equals(newPswd2))
            return true;
       else{
           Toast.makeText(getActivity(),"Password does not match",Toast.LENGTH_LONG).show();
           return false;
       }

    }
}
