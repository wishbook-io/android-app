package com.wishbook.catalog.home.more.referral;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.ConfigResponse;
import com.wishbook.catalog.commonmodels.responses.Profile;
import com.wishbook.catalog.commonmodels.responses.UserStats;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BottomReferandEarn extends GATrackedFragment {


    private View view;

    @BindView(R.id.txt_invite_link)
    TextView txt_invite_link;

    @BindView(R.id.txt_reward_history)
    TextView txt_reward_history;


    @BindView(R.id.txt_reward_point)
    TextView txt_reward_point;

    @BindView(R.id.btn_share_facebook)
    AppCompatButton btn_share_facebook;

    @BindView(R.id.btn_share_whatsapp)
    AppCompatButton btn_share_whatsapp;

    @BindView(R.id.btn_share_other)
    AppCompatButton btn_share_other;

    @BindView(R.id.btn_share_phonebook)
    AppCompatButton btn_share_phonebook;

    @BindView(R.id.txt_referral_promotion_text)
    TextView txt_referral_promotion_text;

    String from = "Home Page";

    public enum SHARETYPE {
        WHATSAPP, FACEBOOK, OTHER
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_refer_program, container, false);
        ButterKnife.bind(this, view);
        this.view = view;
        initView();
        initListner();
        sendScreenEvent();
        return view;

    }

    public void initView() {

        if (UserInfo.getInstance(getActivity()).getFirstName().isEmpty()) {
            showProfileNamePopup();
        }


        if (UserInfo.getInstance(getActivity()).getBranchRefLink() != null) {
            txt_invite_link.setText(UserInfo.getInstance(getActivity()).getBranchRefLink());
            txt_invite_link.setPaintFlags(txt_invite_link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        txt_reward_history.setPaintFlags(txt_reward_history.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if (PrefDatabaseUtils.getConfig(getActivity()) != null) {
            ArrayList<ConfigResponse> data = new Gson().fromJson(PrefDatabaseUtils.getConfig(getActivity()), new TypeToken<ArrayList<ConfigResponse>>() {
            }.getType());
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getKey().equals("REFERRAL_EARN_PROMOTIONAL_TEXT")) {
                    txt_referral_promotion_text.setVisibility(View.VISIBLE);
                    txt_referral_promotion_text.setText(Html.fromHtml(data.get(i).getDisplay_text()), TextView.BufferType.SPANNABLE);
                    break;
                }
            }

        }

        if (getArguments() != null && getArguments().getString("from") != null) {
            from = getArguments().getString("from");
        }


    }


    public void initListner() {
        if (UserInfo.getInstance(getActivity()).getUserStats() != null) {
            UserStats userStats = Application_Singleton.gson.fromJson(UserInfo.getInstance(getActivity()).getUserStats(), UserStats.class);
            if(userStats!=null && userStats.getWb_point_balance()!=null) {
                txt_reward_point.setText(userStats.getWb_point_balance());
            } else {
                txt_reward_point.setText("0");
            }
        }
        txt_reward_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> param = new HashMap<>();
                param.put("type", "tab");
                param.put("page", "myearning/rewardpoint");
                new DeepLinkFunction(param, getActivity());
            }
        });
        btn_share_phonebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendClickEvent("Phonebook");
                Fragment_InviteFriend inviteFriend = new Fragment_InviteFriend();
                Bundle allBuyers = new Bundle();
                allBuyers.putString("from", "allbuyers");
                allBuyers.putBoolean("isReferEarn", true);
                inviteFriend.setArguments(allBuyers);
                Application_Singleton.CONTAINER_TITLE = "Invite Friends and Relatives";
                Application_Singleton.CONTAINERFRAG = inviteFriend;
                startActivity(new Intent(getActivity(), OpenContainer.class));
            }
        });

        btn_share_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendClickEvent("Facebook");
                shareFriends(SHARETYPE.FACEBOOK);
            }
        });


        btn_share_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendClickEvent("Whatsapp");
                shareFriends(SHARETYPE.WHATSAPP);
            }
        });

        btn_share_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendClickEvent("Other");
                shareFriends(SHARETYPE.OTHER);
            }
        });


    }


    public void shareFriends(SHARETYPE type) {

        if (type.equals(SHARETYPE.FACEBOOK)) {
            boolean installed = appInstalledOrNot("com.facebook.katana", getActivity());
            if (!installed) {
                Toast.makeText(getActivity(), "Facebook is not installed on your phone", Toast.LENGTH_SHORT).show();
            } else {
                shareFacebook();
                return;
            }
        }
        try {

            String shareText = getString(R.string.share_app_msg) + Uri.parse(UserInfo.getInstance(getActivity()).getBranchRefLink());
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Download Wishbook APP");
            intent.putExtra(Intent.EXTRA_TEXT, shareText);
            if (type.equals(SHARETYPE.WHATSAPP)) {
                intent.setPackage("com.whatsapp");
            } else if (type.equals(SHARETYPE.FACEBOOK)) {
                intent.setPackage("com.facebook.katana");
            }

            intent.setType("text/plain");
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            if (type.equals(SHARETYPE.WHATSAPP))
                Toast.makeText(getActivity(), "Whatsapp have not been installed", Toast.LENGTH_SHORT).show();
            if (type.equals(SHARETYPE.FACEBOOK))
                Toast.makeText(getActivity(), "Facebook is not installed on your phone", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareFacebook() {
        Log.e("TAG", "Link=====>" + Uri.parse(UserInfo.getInstance(getActivity()).getBranchRefLink()));
        ShareDialog shareDialog = new ShareDialog(getActivity());
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            Log.e("TAG", "Can Show True shareFacebook: ");
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(UserInfo.getInstance(getActivity()).getBranchRefLink()))
                    .setQuote(getString(R.string.share_app_msg) + Uri.parse(UserInfo.getInstance(getActivity()).getBranchRefLink()))
                    .build();
            shareDialog.show(content);
        } else {
            Log.e("TAG", "Can Show False shareFacebook: ");
        }
    }

    private static boolean appInstalledOrNot(String uri, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private void sendScreenEvent() {
        Log.e("TAG", "sendScreenEvent: Refrerral_Screen");
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Referral_Screen");
        HashMap<String, String> param = new HashMap<>();
        param.put("visited", "true");
        param.put("from", from);
        wishbookEvent.setEvent_properties(param);
        new WishbookTracker(getActivity(), wishbookEvent);
    }

    private void sendClickEvent(String type) {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Referral_Invite");
        HashMap<String, String> param = new HashMap<>();
        param.put("type", type);
        wishbookEvent.setEvent_properties(param);
        new WishbookTracker(getActivity(), wishbookEvent);
    }

    private void showProfileNamePopup() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(getContext())
                .title("Please Enter name")
                .inputRangeRes(2, 60, R.color.color_primary)
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .input("Enter name", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                    }
                })
                .positiveText("Save")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        EditText editText = dialog.getInputEditText();
                        String[] splited = editText.getText().toString().trim().split(" ");
                        String firstname_split = "", lastname_split = "";
                        if (splited.length == 1) {
                            firstname_split = splited[0];
                        } else if (splited.length == 2) {
                            firstname_split = splited[0];
                            lastname_split = splited[1];
                        } else if (splited.length > 2) {
                            firstname_split = (splited[0]);
                            ArrayList<String> temp = new ArrayList<>();
                            String lastName = "";
                            for (int i = 1; i < splited.length; i++) {
                                temp.add(splited[i]);
                            }
                            lastName = StaticFunctions.ArrayListToString(temp, " ");
                            lastname_split = lastName;
                        }
                        callPatchProfileName(firstname_split,lastname_split);
                    }
                })
                .cancelable(false).show();

    }

    public void callPatchProfileName(final String firstname_split, final String lastname_split) {
        Profile profile = new Profile();
        profile.setFirst_name(firstname_split);
        if (lastname_split != null && !lastname_split.isEmpty()) {
            profile.setLast_name(lastname_split);
        }
        JsonObject jsonObject = new Gson().fromJson(new Gson().toJson(profile), JsonObject.class);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        final MaterialDialog progressDialog = StaticFunctions.showProgress(getContext());
        progressDialog.show();
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.userUrl(getActivity(), "", ""), jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                SharedPreferences preferencesUtils = getActivity().getSharedPreferences(Constants.WISHBOOK_PREFS, getActivity().MODE_PRIVATE);
                preferencesUtils.edit().putString("firstName", firstname_split).apply();
                preferencesUtils.edit().putString("lastName", lastname_split).apply();
                Toast.makeText(getActivity(), "Profile updated successfully!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                StaticFunctions.logger(error.getErrormessage());
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
    }

}
