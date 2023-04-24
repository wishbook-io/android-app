package com.wishbook.catalog.commonadapters;

import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.networking.NetworkManager;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;
import com.wishbook.catalog.commonmodels.responses.Response_meeting;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.more.visits.Fragment_Visits;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;
import com.wishbook.catalog.login.Fragment_Register_New_Version2;
import com.wishbook.catalog.pendingTasks.BuyerDetailFrag;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MyCustomViewHolder> {
    private ArrayList<Response_meeting> feedItemList;
    private AppCompatActivity mContext;
    private Fragment_Visits fragment_visits;

    public MeetingAdapter(AppCompatActivity context, ArrayList<Response_meeting> feedItemList, Fragment_Visits fragment_visits) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.fragment_visits = fragment_visits;
    }

    public class MyCustomViewHolder extends RecyclerView.ViewHolder {


        private final TextView met_date;
        private final TextView met_name;
        private final TextView met_session;
        private final TextView met_status;
        private final EditText met_note;
        private final ImageView met_statusicon;
        private final Button current;
        private final LinearLayout container;
        private final ImageView edit_note;
        private final ImageView done_note;
        private TextView btn_add_note;
        private LinearLayout linear_note;
        private TextView txt_note, txt_new_buyer;


        public MyCustomViewHolder(View view) {
            super(view);
            met_date = (TextView) view.findViewById(R.id.met_date);
            met_name = (TextView) view.findViewById(R.id.met_name);
            met_session = (TextView) view.findViewById(R.id.met_session);
            met_status = (TextView) view.findViewById(R.id.met_status);
            met_note = (EditText) view.findViewById(R.id.note);
            met_statusicon = (ImageView) view.findViewById(R.id.met_statusicon);
            current = (Button) view.findViewById(R.id.current);
            container = (LinearLayout) view.findViewById(R.id.container);
            edit_note = (ImageView) view.findViewById(R.id.edit_note);
            done_note = (ImageView) view.findViewById(R.id.done_note);
            btn_add_note = (TextView) view.findViewById(R.id.btn_add_note);
            linear_note = (LinearLayout) view.findViewById(R.id.linear_note);
            txt_note = (TextView) view.findViewById(R.id.txt_note);
            txt_new_buyer = (TextView) view.findViewById(R.id.txt_new_buyer);
        }
    }

    @Override
    public MyCustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.meetingritem, viewGroup, false);

        return new MyCustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyCustomViewHolder customViewHolder,  int i) {
        try {
            final Response_meeting response_meeting_recyclerview = feedItemList.get(i);
            customViewHolder.met_date.setText(DateUtils.currentIST(response_meeting_recyclerview.getStart_datetime()));

            if (response_meeting_recyclerview.getBuyer_name_text() != null) {
                customViewHolder.txt_new_buyer.setVisibility(View.VISIBLE);
                customViewHolder.txt_new_buyer.setText(response_meeting_recyclerview.getBuyer_name_text());
            } else {
                customViewHolder.txt_new_buyer.setVisibility(View.GONE);
            }
            if (response_meeting_recyclerview.getNote() != null &&
                    !response_meeting_recyclerview.getNote().isEmpty()) {
                customViewHolder.linear_note.setVisibility(View.VISIBLE);
                customViewHolder.txt_note.setText(response_meeting_recyclerview.getNote());
                customViewHolder.btn_add_note.setText("Edit Note");
            } else {
                customViewHolder.linear_note.setVisibility(View.GONE);
                customViewHolder.btn_add_note.setText("Add Note");
            }
            if (response_meeting_recyclerview.getBuying_company_name() != null && !response_meeting_recyclerview.getBuying_company_name().equals("") && !response_meeting_recyclerview.getBuying_company_name().equals("null")) {
                customViewHolder.edit_note.setVisibility(View.GONE);
                customViewHolder.met_note.setVisibility(View.GONE);
                customViewHolder.done_note.setVisibility(View.GONE);
                customViewHolder.met_name.setVisibility(View.VISIBLE);
                customViewHolder.txt_new_buyer.setVisibility(View.GONE); // hide buyer_name when buyingcompany exist


                // customViewHolder.met_date.setText(getformatedDate(response_meeting_recyclerview.getStart_datetime()));
                customViewHolder.met_name.setText(response_meeting_recyclerview.getBuying_company_name());
                if (response_meeting_recyclerview.getDuration() == null) {
                    customViewHolder.met_session.setVisibility(View.GONE);
                } else {
                    customViewHolder.met_session.setVisibility(View.VISIBLE);
                    customViewHolder.met_session.setText("Duration : " + getformatedduration(response_meeting_recyclerview.getDuration()));
                }

                customViewHolder.met_status.setText(StringUtils.capitalize(response_meeting_recyclerview.getStatus()));
                //   customViewHolder.met_time.setText(getformatedTime(response_meeting_recyclerview.getStart_datetime()));
                if (Activity_Home.pref != null) {
                    if (!Activity_Home.pref.getString("currentmeet", "na").equals("na")) {
                        Response_meeting response_meeting = Application_Singleton.gson.fromJson(Activity_Home.pref.getString("currentmeet", "na"), Response_meeting.class);

                        try {
                            final Response_Buyer response_buyer = Application_Singleton.gson.fromJson(Activity_Home.pref.getString("currentmeetbuyer", "na"), Response_Buyer.class);
                            if (response_meeting.getId().equals(response_meeting_recyclerview.getId())) {
                                customViewHolder.current.setVisibility(View.GONE);
                                customViewHolder.met_session.setVisibility(View.INVISIBLE);
                                customViewHolder.met_status.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Application_Singleton.navselectedBuyer = response_buyer;
                                        mContext.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new BuyerDetailFrag()).addToBackStack("buyerdet").commit();
                                    }
                                });
                            } else {
                                customViewHolder.current.setVisibility(View.GONE);
                                customViewHolder.met_session.setVisibility(View.VISIBLE);
                            }


                            customViewHolder.current.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Application_Singleton.navselectedBuyer = response_buyer;
                 /*   Application_Singleton.CONTAINER_TITLE=response_meeting_recyclerview.getBuying_company_name();
                    BuyerDetailFrag buyerDetailFrag=new BuyerDetailFrag();
                    Application_Singleton.CONTAINERFRAG=buyerDetailFrag;
                    //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                    StaticFunctions.switchActivity(mContext, OpenContainer.class);*/

                                    mContext.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new BuyerDetailFrag()).addToBackStack("buyerdet").commit();

                                }
                            });
                        } catch (Exception e) {

                        }


                    }
                }

                if (customViewHolder.met_status.getText().equals("Pending") || customViewHolder.met_status.getText().equals("pending")) {
                    customViewHolder.met_statusicon.setImageResource(R.drawable.ic_pending);
                } else {
                    customViewHolder.met_statusicon.setImageResource(R.drawable.ic_right);
                }

                //hyperlinking
                customViewHolder.met_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StaticFunctions.hyperLinking("buyer", response_meeting_recyclerview.getBuyer_table_id(), mContext);
                    }
                });


                // changed by gaurav sir
            /*customViewHolder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment_Visits.descontent.setVisibility(View.VISIBLE);
                    Fragment_Visits.details_met_time.setText(StaticFunctions.currentISTTime(response_meeting_recyclerview.getStart_datetime()));
                    Fragment_Visits.details_met_status.setText(StringUtils.capitalize(response_meeting_recyclerview.getStatus()));
                    Fragment_Visits.details_met_date.setText(StaticFunctions.currentISTDate(response_meeting_recyclerview.getStart_datetime()));
                    Fragment_Visits.details_met_noOfpcs.setText(response_meeting_recyclerview.getTotal_products());
                    Fragment_Visits.details_met_buyername.setText(response_meeting_recyclerview.getBuying_company_name());
                    if (response_meeting_recyclerview.getSalesorder().size() > 0) {
                        Fragment_Visits.details_met_noOforder.setText(response_meeting_recyclerview.getSalesorder().size() + "");
                    } else {
                        Fragment_Visits.details_met_noOforder.setText("0");
                    }
                    if (response_meeting_recyclerview.getDuration() != null) {
                        Fragment_Visits.details_met_duration.setVisibility(View.VISIBLE);
                        Fragment_Visits.text_duration.setVisibility(View.VISIBLE);
                        Fragment_Visits.visit_duration_layout.setVisibility(View.VISIBLE);
                        Fragment_Visits.details_met_duration.setText(getformatedduration(response_meeting_recyclerview.getDuration()));
                    } else {
                        Fragment_Visits.text_duration.setVisibility(View.GONE);
                        Fragment_Visits.visit_duration_layout.setVisibility(View.GONE);
                        Fragment_Visits.details_met_duration.setVisibility(View.GONE);
                    }
                }
            });*/
                Fragment_Visits.descontent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Fragment_Visits.descontent.getVisibility() == View.VISIBLE) {
                            Fragment_Visits.descontent.setVisibility(View.GONE);
                        }
                    }
                });
            } else {
                customViewHolder.met_note.setVisibility(View.VISIBLE);
                customViewHolder.met_name.setVisibility(View.GONE);
                customViewHolder.edit_note.setVisibility(View.VISIBLE);

                if (response_meeting_recyclerview.getNote() != null && !response_meeting_recyclerview.getNote().equals("") && !response_meeting_recyclerview.getNote().equals("null")) {
                    customViewHolder.met_note.setText(response_meeting_recyclerview.getNote());
                } else {
                    customViewHolder.met_note.setText("");
                }

                customViewHolder.met_note.setEnabled(false);

                if (response_meeting_recyclerview.getDuration() != null && !response_meeting_recyclerview.getDuration().equals("") && !response_meeting_recyclerview.getDuration().equals("null")) {
                    customViewHolder.met_session.setText("Duration : " + getformatedduration(response_meeting_recyclerview.getDuration()));
                }

                customViewHolder.met_status.setText(StringUtils.capitalize(response_meeting_recyclerview.getStatus()));


                if (customViewHolder.met_status.getText().equals("Pending") || customViewHolder.met_status.getText().equals("pending")) {
                    customViewHolder.met_statusicon.setImageResource(R.drawable.ic_pending);
                } else {
                    customViewHolder.met_statusicon.setImageResource(R.drawable.ic_right);
                }

                if (customViewHolder.met_status.getText().equals("Pending") || customViewHolder.met_status.getText().equals("pending")) {
                    customViewHolder.met_note.setEnabled(true);
                    customViewHolder.current.setVisibility(View.GONE);
                    customViewHolder.met_session.setVisibility(View.INVISIBLE);
                    customViewHolder.edit_note.setVisibility(View.VISIBLE);

                } else {
                    customViewHolder.current.setVisibility(View.GONE);
                    customViewHolder.met_session.setVisibility(View.VISIBLE);
                    customViewHolder.met_note.setEnabled(false);
                    customViewHolder.edit_note.setVisibility(View.GONE);
                }
                customViewHolder.met_note.setEnabled(false);
                customViewHolder.edit_note.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customViewHolder.met_note.setEnabled(true);
                        customViewHolder.met_note.requestFocus();
                        customViewHolder.edit_note.setVisibility(View.INVISIBLE);
                        customViewHolder.done_note.setVisibility(View.VISIBLE);
                    }
                });

                customViewHolder.done_note.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("note", customViewHolder.met_note.getText().toString());
                        NetworkManager.getInstance().HttpRequestwithHeader(mContext, "PATCH", URLConstants.userUrl(mContext, "meetings_with_id", response_meeting_recyclerview.getId()), params, new NetworkManager.customCallBack() {
                            @Override
                            public void onCompleted(int status, String response) {
                                if (status == NetworkManager.RESPONSESUCCESS) {
                                    // Response_meeting response_meeting=(Response_meeting)Application_Singleton.gson.fromJson(response,Response_meeting.class);
                                    Toast.makeText(mContext, "Changes saved", Toast.LENGTH_LONG).show();
                                    customViewHolder.met_note.setEnabled(false);
                                    customViewHolder.edit_note.setVisibility(View.VISIBLE);
                                    customViewHolder.done_note.setVisibility(View.INVISIBLE);
                                } else {

                                }
                            }
                        });
                    }
                });

            }

            customViewHolder.txt_new_buyer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(mContext)
                            .content(R.string.meeting_new_buyer_popup)
                            .positiveText("New")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                    Application_Singleton.CONTAINER_TITLE = "New Registration";
                                    Application_Singleton.TOOLBARSTYLE = "WHITE";
                                    Application_Singleton.RegisterName = response_meeting_recyclerview.getBuyer_name_text();
                                    Application_Singleton.MEETING_ID = response_meeting_recyclerview.getId();
                                    Application_Singleton.CONTAINERFRAG = new Fragment_Register_New_Version2();
                                    Intent intent = new Intent(mContext, OpenContainer.class);
                                    fragment_visits.startActivityForResult(intent, Application_Singleton.ADD_NEW_BUYER_REQUEST_CODE);
                                }
                            })
                            .negativeText("Existing")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    KeyboardUtils.hideKeyboard(mContext);
                                    Application_Singleton.MEETING_ID = response_meeting_recyclerview.getId();
                                    fragment_visits.startActivityForResult(new Intent(mContext, Activity_BuyerSearch.class), Application_Singleton.EXISTING_BUYER_SEARCH_REQUEST_CODE);
                                }
                            })
                            .show();
                }
            });
            customViewHolder.btn_add_note.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final MaterialDialog dialog = new MaterialDialog.Builder(mContext).
                            title("Add Note")
                            .content("Enter Note")
                            .inputType(InputType.TYPE_CLASS_TEXT)
                            .input(null, null, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                    HashMap<String, String> params = new HashMap<String, String>();
                                    params.put("note", input.toString());
                                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(mContext);
                                    HttpManager.getInstance(mContext).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.userUrl(mContext, "meetings_with_id", response_meeting_recyclerview.getId()), Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(params), JsonObject.class), headers, new HttpManager.customCallBack() {

                                        @Override
                                        public void onCacheResponse(String response) {

                                        }

                                        @Override
                                        public void onServerResponse(String response, boolean dataUpdated) {
                                            Response_meeting response_meeting = (Response_meeting) Application_Singleton.gson.fromJson(response, Response_meeting.class);
                                            feedItemList.set(customViewHolder.getAdapterPosition(), response_meeting);
                                            notifyItemChanged(customViewHolder.getAdapterPosition());
                                        }


                                        @Override
                                        public void onResponseFailed(ErrorString error) {
                                          StaticFunctions.showResponseFailedDialog(error);
                                        }
                                    });
                                }
                            }).build();
                    if (customViewHolder.btn_add_note.getText().toString().equalsIgnoreCase("Edit Note")) {
                        dialog.getInputEditText().setText(response_meeting_recyclerview.getNote());
                    }

                    dialog.show();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private String getformatedduration(String duration) {
        try {
            StringTokenizer stringTokenizer = new StringTokenizer(duration, ".");
            if (stringTokenizer.hasMoreTokens()) {
                return stringTokenizer.nextToken();
            }
        } catch (Exception e) {

        }
        return "";
    }

    private String getformatedDate(String dat) {

        String format = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        DateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String toformat = "MMMM dd,yyyy";
        SimpleDateFormat tosdf = new SimpleDateFormat(toformat, Locale.US);

        try {
            Date date = sdf.parse(dat);
            return tosdf.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

    }

    private String getformatedTime(String dat) {

        String format = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        DateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String toformat = "hh:mm aa";
        SimpleDateFormat tosdf = new SimpleDateFormat(toformat, Locale.US);

        try {
            Date date = sdf.parse(dat);
            return tosdf.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

    }

    @Override
    public int getItemCount() {
        return feedItemList.size();
    }


}