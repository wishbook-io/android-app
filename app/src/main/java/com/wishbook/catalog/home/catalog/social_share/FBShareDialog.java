package com.wishbook.catalog.home.catalog.social_share;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.wishbook.catalog.GATrackedDialogFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.ToastUtil;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FBShareDialog extends GATrackedDialogFragment {

  /*  private View view;

    @BindView(R.id.linear_fb_wall)
    LinearLayout linear_fb_wall;

    @BindView(R.id.linear_fb_page)
    LinearLayout linear_fb_page;

    @BindView(R.id.linear_fb_market_place)
    LinearLayout linear_fb_market_place;

    private CallbackManager pageCallbackManager;

    public static String TAG = FBShareDialog.class.getSimpleName();


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fb_bottom_select);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = View.inflate(getContext(), R.layout.fb_bottom_select, null);
        ButterKnife.bind(this, view);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.gravity = Gravity.BOTTOM;
            dialog.getWindow().setAttributes(lp);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_profile, container, true);
        view = View.inflate(getContext(), R.layout.fb_bottom_select, null);
        ButterKnife.bind(this, view);
        return view;
    }

    public void initListener() {
        linear_fb_wall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        linear_fb_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askManageFbPagePermission();
            }
        });

        linear_fb_market_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void askManageFbPagePermission() {
        List<String> permissionNeeds = Arrays.asList("manage_pages", "publish_pages", "publish_actions");
        pageCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithPublishPermissions(
                this,
                permissionNeeds);
        LoginManager.getInstance().registerCallback(pageCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResults) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResults.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        Log.v(TAG, "Page Login Respone===>" + response.toString());
                                    }
                                });
                        Bundle parameters = new Bundle();
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Log.e(TAG, "facebook page login canceled");
                        ToastUtil.showGravityShort("Cancelled the Permission",Gravity.BOTTOM);
                    }


                    @Override
                    public void onError(FacebookException e) {
                        Log.e(TAG, "facebook page login failed error");
                        ToastUtil.showGravityShort("Something went wrong",Gravity.BOTTOM);

                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(pageCallbackManager!=null)
            pageCallbackManager.onActivityResult(requestCode,resultCode,data);
    }


    public void showFbWallHint() {

    }
*/

}
