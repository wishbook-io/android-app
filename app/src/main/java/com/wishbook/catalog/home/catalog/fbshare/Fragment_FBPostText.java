package com.wishbook.catalog.home.catalog.fbshare;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.ImageUtils;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.ToastUtil;
import com.wishbook.catalog.commonmodels.responses.FbPageModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_FBPostText extends GATrackedFragment {


    private View view;

    @BindView(R.id.txt_share_text_preview)
    TextView txt_share_text_preview;

    @BindView(R.id.edit_suggested_text)
    EditText edit_suggested_text;

    @BindView(R.id.btn_paste)
    AppCompatButton btn_paste;

    @BindView(R.id.linear_stage_one)
    LinearLayout linear_stage_one;

    @BindView(R.id.linear_copy_text)
    LinearLayout linear_copy_text;

    @BindView(R.id.linear_post_text)
    LinearLayout linear_post_text;

    @BindView(R.id.btn_fb_post)
    AppCompatButton btn_fb_post;

    @BindView(R.id.img_page)
    SimpleDraweeView img_page;

    @BindView(R.id.txt_page_name)
    TextView txt_page_name;

    @BindView(R.id.txt_page_publish_date)
    TextView txt_page_publish_date;

    @BindView(R.id.img_one)
    SimpleDraweeView img_one;

    @BindView(R.id.linear_column_two)
    LinearLayout linear_column_two;

    @BindView(R.id.img_two)
    SimpleDraweeView img_two;

    @BindView(R.id.img_three)
    SimpleDraweeView img_three;

    @BindView(R.id.img_four)
    SimpleDraweeView img_four;

    @BindView(R.id.btn_copy)
    AppCompatButton btn_copy;

    @BindView(R.id.scroll_view)
    ScrollView scroll_view;

    @BindView(R.id.txt_logout_facebook)
    TextView txt_logout_facebook;

    @BindView(R.id.txt_login_facebook)
    TextView txt_login_facebook;

    @BindView(R.id.linear_bottom)
    FrameLayout linear_bottom;

    @BindView(R.id.txt_logout_facebook_1)
    TextView txt_logout_facebook_1;

    @BindView(R.id.txt_fb_status)
    TextView txt_fb_status;

    Animation shake;


    private CallbackManager callbackManager;
    private CallbackManager pageCallbackManager;
    private ArrayList<FbPageModel> user_manage_pages;
    private ArrayList<String> photos_url;
    MaterialDialog progressDialog;
    int success_count;
    public FbPageModel selected_fb_page = null;
    String catalog_name;
    private static String TAG = Fragment_FBPostText.class.getSimpleName();


    public Fragment_FBPostText() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_fb_suggested_text, ga_container, true);
        ButterKnife.bind(this, view);
        initView();
        initListener();
        showStageOneUI();
        loginWithFacebook();

        // ####### Set up menu and Toolbar Start ########

        setHasOptionsMenu(true);

        return view;
    }


    private void initView() {
        if (getArguments().getString("data") != null) {
            String temp = getArguments().getString("data");
            temp = temp.replace("*", "");
            txt_share_text_preview.setText(temp);
        }

        txt_logout_facebook.setPaintFlags(txt_logout_facebook.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txt_logout_facebook_1.setPaintFlags(txt_logout_facebook_1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if (getArguments().getStringArrayList("images") != null) {
            photos_url = getArguments().getStringArrayList("images");
            if (photos_url.size() >= 2) {
                linear_column_two.setVisibility(View.VISIBLE);
            } else {
                linear_column_two.setVisibility(View.GONE);
            }
            for (int i = 0; i < photos_url.size(); i++) {
                if (i == 0) {
                    img_one.setVisibility(View.VISIBLE);
                    ImageUtils.loadImage(getActivity(), photos_url.get(i), img_one);
                } else if (i == 1) {
                    img_two.setVisibility(View.VISIBLE);
                    ImageUtils.loadImage(getActivity(), photos_url.get(i), img_two);
                } else if (i == 2) {
                    img_three.setVisibility(View.VISIBLE);
                    ImageUtils.loadImage(getActivity(), photos_url.get(i), img_three);
                } else if (i == 3) {
                    img_four.setVisibility(View.VISIBLE);
                    ImageUtils.loadImage(getActivity(), photos_url.get(i), img_four);
                }
            }
        }
    }


    private void initListener() {
        btn_fb_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected_fb_page != null) {
                    if (edit_suggested_text.getText().toString().isEmpty()) {
                        ToastUtil.showLong("Please enter suggested text");
                        edit_suggested_text.setError("Please enter suggested text");
                        edit_suggested_text.requestFocus();
                        return;
                    }
                    createAlbum(getArguments().getString("name"), edit_suggested_text.getText().toString(), selected_fb_page.getAccess_token(), selected_fb_page.getId());
                } else {
                    ToastUtil.showLong("Please select any Facebook Page");
                }
            }
        });


        btn_paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_suggested_text.setText(txt_share_text_preview.getText().toString());
            }
        });

        btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    shake = AnimationUtils.loadAnimation(getActivity(), R.anim.swing);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                scroll_view.smoothScrollBy(0, 250);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btn_paste.startAnimation(shake);
                    }
                }, 1500);
            }
        });

        txt_logout_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                PrefDatabaseUtils.setPrefFacebookPageAccessToken(getActivity(), null);
                PrefDatabaseUtils.setPrefFacebookLogin(getActivity(), false);
                showStageOneUI();
            }
        });

        txt_login_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWithFacebook();
            }
        });

        txt_logout_facebook_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                PrefDatabaseUtils.setPrefFacebookPageAccessToken(getActivity(), null);
                PrefDatabaseUtils.setPrefFacebookLogin(getActivity(), false);
                loginWithFacebook();
            }
        });

    }

    public void showStageOneUI() {
        linear_stage_one.setVisibility(View.VISIBLE);
        linear_copy_text.setVisibility(View.GONE);
        linear_post_text.setVisibility(View.GONE);
        linear_bottom.setVisibility(View.GONE);
    }

    public void showStageTwoUI(FbPageModel fbPageModel) {
        linear_stage_one.setVisibility(View.GONE);

        linear_copy_text.setVisibility(View.VISIBLE);
        linear_post_text.setVisibility(View.VISIBLE);
        linear_bottom.setVisibility(View.VISIBLE);

        txt_page_name.setText(fbPageModel.getName());
        SimpleDateFormat sdf = new SimpleDateFormat(StaticFunctions.CLIENT_DISPLAY_FORMAT1);
        txt_page_publish_date.setText(sdf.format(new Date()));
        fetchPageProfile(fbPageModel.getId());
    }


    private void showFbPageDialog(ArrayList<FbPageModel> fbPageModels) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View fb_page_select_dialogview = factory.inflate(R.layout.dialog_select_facebook_page, null);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setView(fb_page_select_dialogview);
        Spinner spinner = fb_page_select_dialogview.findViewById(R.id.spinner_fb_page);
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < fbPageModels.size(); i++) {
            arrayList.add(fbPageModels.get(i).getName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        fb_page_select_dialogview.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selected_fb_page = fbPageModels.get(spinner.getSelectedItemPosition());
                showStageTwoUI(fbPageModels.get(spinner.getSelectedItemPosition()));
            }
        });
        fb_page_select_dialogview.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_fb_status.setText("No Page Selected!!!");
                txt_logout_facebook_1.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }


    public void loginWithFacebook() {
        try {
            FacebookSdk.sdkInitialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_RAW_RESPONSES);
        callbackManager = CallbackManager.Factory.create();
        List<String> permissionNeeds = Arrays.asList("user_photos", "email", "user_birthday", "user_friends", "pages_show_list");
        callbackManager = CallbackManager.Factory.create();
        if (!PrefDatabaseUtils.getPrefFacebookLogin(getActivity())) {
            txt_logout_facebook_1.setVisibility(View.GONE);
            Log.e(TAG, "Login with Facebook with general permission");
            LoginManager.getInstance().logInWithReadPermissions(
                    this,
                    permissionNeeds);
            LoginManager.getInstance().registerCallback(callbackManager,
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
                                            Log.v(TAG, "Login Response===>" + response.toString());
                                            callbackManager = null;
                                            PrefDatabaseUtils.setPrefFacebookLogin(getActivity(), true);
                                            accessManagePermission();
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email,gender, birthday");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }

                        @Override
                        public void onCancel() {
                            txt_fb_status.setText("Login Failed,Try Again");
                            ToastUtil.showLong("Login: Cancel");
                            Log.e(TAG, "facebook login canceled");
                        }

                        @Override
                        public void onError(FacebookException e) {
                            if(e.getMessage()!=null && e.getMessage().contains("another user")) {
                                LoginManager.getInstance().logOut();
                                PrefDatabaseUtils.setPrefFacebookLogin(getActivity(), false);
                                PrefDatabaseUtils.setPrefFacebookPageAccessToken(getActivity(), null);
                            }
                            e.printStackTrace();
                            ToastUtil.showLong("Login: Something went wrong");
                            Log.e(TAG, "facebook login failed error");
                        }
                    });
        } else {
            txt_logout_facebook_1.setVisibility(View.VISIBLE);
            if (PrefDatabaseUtils.getPrefFacebookPageAccessToken(getActivity()) != null) {
                fetchPageDetails(AccessToken.getCurrentAccessToken());
            } else {
                if (AccessToken.getCurrentAccessToken().getPermissions().contains("manage_pages")
                        && AccessToken.getCurrentAccessToken().getPermissions().contains("publish_pages")) {
                    Log.e(TAG, "accessManagePermission: Permission Already contains 1");
                    PrefDatabaseUtils.setPrefFacebookPageAccessToken(getActivity(), "done");
                    fetchPageDetails(AccessToken.getCurrentAccessToken());
                } else {
                    accessManagePermission();
                }
            }
        }
    }


    public void accessManagePermission() {
        Log.e(TAG, "accessManagePermission: Call" + AccessToken.getCurrentAccessToken().getPermissions().toString());
        if (AccessToken.getCurrentAccessToken().getPermissions().contains("manage_pages")
                && AccessToken.getCurrentAccessToken().getPermissions().contains("publish_pages")) {
            Log.e(TAG, "accessManagePermission: Permission Already contains");
            PrefDatabaseUtils.setPrefFacebookPageAccessToken(getActivity(), "done");
            fetchPageDetails(AccessToken.getCurrentAccessToken());
        } else {
            List<String> permissionNeeds = Arrays.asList("manage_pages", "publish_pages");
            pageCallbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().logInWithPublishPermissions(
                    this,
                    permissionNeeds);
            LoginManager.getInstance().registerCallback(pageCallbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResults) {
                            PrefDatabaseUtils.setPrefFacebookPageAccessToken(getActivity(), "done");
                            fetchPageDetails(loginResults.getAccessToken());
                        }

                        @Override
                        public void onCancel() {
                            txt_fb_status.setText("Page Not Found!!!");
                            Log.e(TAG, "facebook page login canceled");
                        }


                        @Override
                        public void onError(FacebookException e) {
                            ToastUtil.showLong("Page: Something went wrong");
                            PrefDatabaseUtils.setPrefFacebookLogin(getActivity(), false);
                            PrefDatabaseUtils.setPrefFacebookPageAccessToken(getActivity(), null);
                            Log.e(TAG, "facebook page login failed error");
                        }
                    });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager != null)
            callbackManager.onActivityResult(requestCode, resultCode, data);

        if (pageCallbackManager != null)
            pageCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    public boolean isLoggedIn() {
        AccessToken token;
        token = AccessToken.getCurrentAccessToken();
        if (token == null) {
            //Means user is not logged in
            return false;
        } else {
            return true;
        }
    }


    public void fetchPageDetails(AccessToken accessToken) {
        user_manage_pages = null;
        new GraphRequest(accessToken,
                "/me/accounts",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        if (response.getError() == null) {
                            try {
                                if (response.getJSONObject().getJSONArray("data") != null) {
                                    JSONArray jsonArray = response.getJSONObject().getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject page_object = (JSONObject) response.getJSONObject().getJSONArray("data").get(i);
                                        FbPageModel fbPageModel = Application_Singleton.gson.fromJson(page_object.toString(), FbPageModel.class);
                                        if (user_manage_pages == null)
                                            user_manage_pages = new ArrayList<>();
                                        user_manage_pages.add(fbPageModel);
                                    }
                                    showFbPageDialog(user_manage_pages);
                                } else {
                                    Toast.makeText(getActivity(), "Sorry, No Pages Found", Toast.LENGTH_LONG).show();
                                    getActivity().finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(getActivity(), response.getError().getErrorMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ).executeAsync();
    }

    public void fetchPageProfile(String pageid) {
        Bundle params = new Bundle();
        params.putString("redirect", "0");
        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + pageid + "/picture",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.e(TAG, "fetchPageProfile: " + response.toString());
                        if (response.getJSONObject().has("data")) {
                            try {
                                JSONObject page_object = (JSONObject) response.getJSONObject().getJSONObject("data");
                                String urls = page_object.getString("url");
                                ImageUtils.loadImage(getActivity(), urls, img_page);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
        ).executeAsync();
    }


    public void postPhotos(String album_id, String page_access_token, String page_id) {
        Log.e(TAG, "postPhotos: Function Start ======>");
        String aid = album_id;
        if (album_id != null)
            aid = album_id;

        AccessToken pageToken = new AccessToken(page_access_token, getActivity().getString(R.string.facebook_app_id), page_id,
                null,
                null,
                null,
                null, null, null, null);
        for (int i = 0; i < photos_url.size(); i++) {
            if (i == 0) {
                progressDialog = StaticFunctions.showProgress(getActivity());
                progressDialog.show();
            }
            Bundle params = new Bundle();
            params.putString("url", photos_url.get(i));
            String graph_path_url = "/" + aid + "/photos";
            new GraphRequest(
                    pageToken,
                    graph_path_url,
                    params,
                    HttpMethod.POST,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            /* handle the result */
                            success_count++;
                            Log.e(TAG, "onCompleted: Photo Upload" + response.toString());
                            observableCallAPI(success_count);
                        }
                    }
            ).executeAsync();
        }
    }

    public void createAlbum(String album_name, String post_message, String page_acces_token, String page_id) {
        progressDialog = StaticFunctions.showProgress(getActivity());
        progressDialog.show();
        AccessToken pageToken = new AccessToken(page_acces_token, getActivity().getString(R.string.facebook_app_id), page_id,
                null,
                null,
                null,
                null, null, null, null);
        Bundle params2 = new Bundle();
        params2.putString("message", post_message);
        params2.putString("name", album_name);
        String graph_path_url = "/" + page_id + "/albums";
        new GraphRequest(
                pageToken,
                graph_path_url,
                params2,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        progressDialog.dismiss();
                        Log.e(TAG, "onCompleted: Album Created" + response.toString());
                        try {
                            FacebookRequestError error = response.getError();
                            if (error == null) {
                                if (response.getJSONObject().has("id")) {
                                    Log.e(TAG, "onCompleted: Get Response and GraphObject ID ========>" + response.getJSONObject().get("id"));
                                    String id = response.getJSONObject().get("id").toString();
                                    postPhotos(id, page_acces_token, page_id);
                                }
                            } else {
                                Log.e(TAG, "onCompleted: Error ===>" + response.getError().toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    public void observableCallAPI(int count) {
        if (count >= photos_url.size()) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Toast.makeText(getActivity(), "Successfully shared", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
