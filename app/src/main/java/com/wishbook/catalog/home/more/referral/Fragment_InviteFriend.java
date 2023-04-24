package com.wishbook.catalog.home.more.referral;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DividerItemDecoration;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.Contact;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.commonmodels.UploadContactsModel;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.home.more.referral.adapter.PhoneBookAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_InviteFriend extends GATrackedFragment {

    @BindView(R.id.linear_no_permission)
    LinearLayout linear_no_permission;

    @BindView(R.id.btn_enable_permission)
    TextView btn_enable_permission;

    @BindView(R.id.relative_container)
    RelativeLayout relative_container;


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.fab_invite)
    FloatingActionButton fab_invite;

    @BindView(R.id.linear_button)
    LinearLayout linear_button;

    @BindView(R.id.btn_select_all)
    AppCompatButton btn_select_all;

    @BindView(R.id.btn_select_none)
    AppCompatButton btn_select_none;

    @BindView(R.id.txt_progress)
    TextView txt_progress;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.linear_searchview)
    LinearLayout linear_searchview;

    @BindView(R.id.search_view)
    SearchView search_view;

    @BindView(R.id.img_searchclose)
    ImageView img_searchclose;


    private View view;
    private List<Contact> contactList = new ArrayList<>();
    private GetAllContacts gac;
    PhoneBookAdapter phoneBookAdapter;


    public Fragment_InviteFriend() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.activity_phone_invite, ga_container, true);
        ButterKnife.bind(this, view);
        initRecyclerView();

        if (getActivity() instanceof OpenContainer) {
            ((OpenContainer) getActivity()).toolbar.setVisibility(View.GONE);
            toolbar.setTitle("Invite Friends & Relatives");
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            toolbar.setSubtitle("*Sms charge will be applicable");
            toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
            toolbar.setSubtitleTextAppearance(getActivity(), R.style.ToolbarSubtitleAppearance);

            toolbar.inflateMenu(R.menu.menu_phone_invite);


            img_searchclose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(phoneBookAdapter!=null) {
                        search_view.setQuery("",false);
                        phoneBookAdapter.filter("");
                    }
                    toolbar.setVisibility(View.VISIBLE);
                    linear_searchview.setVisibility(View.GONE);
                    KeyboardUtils.hideKeyboard(getActivity());
                }
            });

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });

            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.action_search) {
                        toolbar.setVisibility(View.GONE);
                        linear_searchview.setVisibility(View.VISIBLE);
                        search_view.requestFocus();
                        KeyboardUtils.showKeyboard(search_view,getActivity());
                        search_view.requestFocusFromTouch();
                    }
                    return false;
                }
            });

            search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if(phoneBookAdapter!=null) {
                        phoneBookAdapter.filter(s.toString());
                    }
                    return false;
                }
            });
        }

        initListner();
        return view;
    }

    public void initRecyclerView() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            showPermissionView();
        } else {
            hidePermissionView();
            gac = new GetAllContacts(getContext());
            gac.execute();
        }
    }

    public void initListner() {
        btn_enable_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_CONTACTS)) {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_CONTACTS},
                            100);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(getView(), "Please Enable 'READ CONTACT' Permission from setting", Snackbar.LENGTH_LONG)
                            .setAction("SETTING", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    View snackBarView = snackbar.getView();
                    snackBarView.setAlpha(0.9f);
                    snackbar.addCallback(new Snackbar.Callback() {

                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {

                        }

                        @Override
                        public void onShown(Snackbar snackbar) {
                        }
                    });

                    snackbar.show();
                }
            }
        });


        btn_select_none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneBookAdapter != null) {
                    phoneBookAdapter.selectNoneContacts();
                }
            }
        });

        btn_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneBookAdapter != null) {
                    phoneBookAdapter.selectAllContacts();
                }
            }
        });

        fab_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReferralContacts();
                sendSms();
            }
        });

    }

    private class GetAllContacts extends AsyncTask<Void, Void, Void> {

        private Context mContext;

        public GetAllContacts(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            txt_progress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("Contact :", "Getting Contacts from Phone");
            //showProgress();
            rtt(mContext);
            if (contactList.size() > 0) {
                Collections.sort(contactList, new Comparator<Contact>() {
                    @Override
                    public int compare(Contact lhs, Contact rhs) {
                        return lhs.getContactName().compareToIgnoreCase(rhs.getContactName());
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            txt_progress.setVisibility(View.GONE);
            //hideProgress();
            hidePermissionView();
            bindAdapter();
        }
    }

    private void rtt(Context mContext) {
        contactList.clear();
        HashSet<String> nums = new HashSet<>();
        if (mContext != null) {
            Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (phones != null) {
                try {
                    while (phones.moveToNext()) {
                        String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "").trim();
                        if (phoneNumber.length() >= 10) {
                            phoneNumber = phoneNumber.substring(phoneNumber.length() - 10, phoneNumber.length());
                        }
                        Contact ct = new Contact(name, phoneNumber, false, false);

                        String phoneNumber1 = phoneNumber.replaceAll("\\D+", "");
                        if (phoneNumber1.length() >= 10) {
                            String phoneNumbertrim = phoneNumber1.substring(phoneNumber1.length() - 10, phoneNumber1.length());
                            if (!nums.contains(phoneNumbertrim)) {
                                contactList.add(ct);
                            }
                            nums.add(phoneNumbertrim);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                phones.close();
            }

        }
        if (contactList.size() > 0) {
            Collections.sort(contactList, new Comparator<Contact>() {

                @Override
                public int compare(Contact lhs, Contact rhs) {
                    return lhs.getContactName().compareToIgnoreCase(rhs.getContactName());
                }
            });
        }
        if (mContext != null) {
            UserInfo.getInstance(mContext).setContacts(new Gson().toJson(contactList));
            UserInfo.getInstance(mContext).setContactStatus("fetched");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hidePermissionView();
                    gac = new GetAllContacts(getContext());
                    gac.execute();
                }
            }
        }
    }


    public void bindAdapter() {
        txt_progress.setVisibility(View.GONE);
        try {
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        } catch (Exception e) {
            e.printStackTrace();
        }

        phoneBookAdapter = new PhoneBookAdapter(contactList, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(phoneBookAdapter);

    }

    public void showPermissionView() {
        linear_no_permission.setVisibility(View.VISIBLE);
        relative_container.setVisibility(View.GONE);
        linear_button.setVisibility(View.GONE);
        fab_invite.hide();
    }

    public void hidePermissionView() {
        linear_no_permission.setVisibility(View.GONE);
        relative_container.setVisibility(View.VISIBLE);
        linear_button.setVisibility(View.VISIBLE);
        fab_invite.show();
    }

    public void sendReferralContacts() {
        if (phoneBookAdapter != null) {
            if (phoneBookAdapter.getSelectedContacts().size() == 0) {
                Toast.makeText(getActivity(), "Please select any one contact", Toast.LENGTH_SHORT).show();
                return;
            }
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            ArrayList<NameValues> nameValuesArrayMap = new ArrayList<>();
            UploadContactsModel uploadContactsModel = new UploadContactsModel(nameValuesArrayMap);
            for (Contact selectedContact : phoneBookAdapter.getSelectedContacts()) {
                String phoneNumber1 = selectedContact.getContactNumber().replaceAll("\\D+", "");
                if (phoneNumber1.length() >= 10) {
                    phoneNumber1 = phoneNumber1.substring(phoneNumber1.length() - 10, phoneNumber1.length());
                }
                nameValuesArrayMap.add(new NameValues(selectedContact.getContactName(), phoneNumber1));
            }
            uploadContactsModel.setContacts(nameValuesArrayMap);
            HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECT, URLConstants.companyUrl(getActivity(), "invited-users", ""), new Gson().fromJson(new Gson().toJson(uploadContactsModel), JsonObject.class), headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    try {
                        if (!isDetached() && isAdded()) {
                            // Toast.makeText(getActivity(), "Successfully invited", Toast.LENGTH_SHORT).show();
                            //getActivity().finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    StaticFunctions.showResponseFailedDialog(error);
                }
            });
        }

    }


    public void sendSms() {
        if (phoneBookAdapter != null) {
            if (phoneBookAdapter.getSelectedContacts().size() == 0) {
                Toast.makeText(getActivity(), "Please select any one contact", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuffer phone_app = new StringBuffer();
            for (int i = 0; i < phoneBookAdapter.getSelectedContacts().size(); i++) {
                phone_app.append(phoneBookAdapter.getSelectedContacts().get(i).getContactNumber() + ";");
            }
            String shareText = getString(R.string.share_app_msg) + Uri.parse(UserInfo.getInstance(getActivity()).getBranchRefLink());
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone_app));
            smsIntent.putExtra("sms_body", shareText);
            startActivity(smsIntent);


            if(linear_searchview.getVisibility() == View.VISIBLE) {

                if(phoneBookAdapter!=null) {
                    phoneBookAdapter.selectNoneContacts();
                }

                if(phoneBookAdapter!=null) {
                    search_view.setQuery("",false);
                    phoneBookAdapter.filter("");
                }

                linear_searchview.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
            }

        }

    }

}
