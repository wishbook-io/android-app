package com.wishbook.catalog.commonadapters;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_grouptypes;
import com.wishbook.catalog.commonmodels.MergeContacts;
import com.wishbook.catalog.commonmodels.postpatchmodels.ResendBuyerID;
import com.wishbook.catalog.commonmodels.postpatchmodels.approveBuyer;
import com.wishbook.catalog.commonmodels.responses.Response_BuyerGroupType;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.contacts.viewholder.Contact_ViewHolder;
import com.wishbook.catalog.home.contacts.viewholder.Pending_Buyer_ViewHolder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by prane on 21-03-2016.
 */
public class PendingBuyersAdapterMerge extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AppCompatActivity mActivity;
    private ArrayList<MergeContacts> allItems;
    private final int BUYER = 0, CONTACT = 1;
    private Response_BuyerGroupType[] response_buyerGroupType;

    public PendingBuyersAdapterMerge(AppCompatActivity mActivity, ArrayList<MergeContacts> mPendingBuyersList, Response_BuyerGroupType[] response_buyerGroupType) {
        this.mActivity = mActivity;
        this.allItems = mPendingBuyersList;
        this.response_buyerGroupType = response_buyerGroupType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case BUYER:
                View v1 = inflater.inflate(R.layout.pending_buyers_row, parent, false);
                viewHolder = new Pending_Buyer_ViewHolder(v1);
                break;
            case CONTACT:
                View v2 = inflater.inflate(R.layout.contacts_row_buyer, parent, false);
                viewHolder = new Contact_ViewHolder(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case BUYER:
                Pending_Buyer_ViewHolder view1 = (Pending_Buyer_ViewHolder) holder;
                configureBuyerViewholder(view1, position);
                break;
            case CONTACT:
                Contact_ViewHolder view2 = (Contact_ViewHolder) holder;
                configureContactViewholder(view2, position);
                break;
        }

    }

    private void configureBuyerViewholder(Pending_Buyer_ViewHolder holder, final int position) {
        final MergeContacts bg = allItems.get(position);
        if (bg.getStatus() != null) {
            if (bg.getStatus().equals("supplier_pending")) {
                holder.Approve.setVisibility(View.VISIBLE);
                holder.Resend.setVisibility(View.GONE);
            } else {
                holder.Approve.setVisibility(View.GONE);
                holder.Resend.setVisibility(View.VISIBLE);
            }
        }


        if (bg.getName() != null) {
            final String companyName = bg.getName();
            final String phoneNumber = bg.getPhone();
            holder.buyerName.setText(StringUtils.capitalize(companyName.toLowerCase().trim()));
            holder.buyerNumber.setText(StringUtils.capitalize(phoneNumber.toLowerCase().trim()));
            if (bg.getStatus().equals("buyer_registrationpending")) {
                holder.buyerStatus.setText(StringUtils.capitalize("Registration Pending"));
            } else if (bg.getStatus().equals("buyer_pending")) {
                holder.buyerStatus.setText(StringUtils.capitalize("Pending"));
            } else if (bg.getStatus().equals("supplier_pending")) {
                holder.buyerStatus.setText(StringUtils.capitalize("Supplier Pending"));
            }  else if(bg.getStatus().equals("Pending References")){
                holder.buyerStatus.setText(StringUtils.capitalize("Pending References"));
            }else if(bg.getStatus().equals("References Filled")) {
                holder.buyerStatus.setText(StringUtils.capitalize("References Filled"));
            }

            holder.buyerNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                    mActivity.startActivity(intent);
                }
            });

           /* if(bg.getStatus().contains("pending")) {
                holder.buyerEmailStatus.setText("Registration Pending");
            } else {
                holder.buyerEmailStatus.setText(StaticFunctions.capitalizeFirstLetter(bg.getStatus()));
            }*/

           /* holder.buyerName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Application_Singleton.navselectedBuyer=mPendingBuyersList.get(position);
                    // mActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new BuyerDetailFrag()).addToBackStack("buyerdet").commit();

                }
            });
*/
        } else {
            holder.buyerName.setText("N/A");
            holder.buyerNumber.setText("N/A");
            if (bg.getStatus().equals("buyer_registrationpending")) {
                holder.buyerStatus.setText(StringUtils.capitalize("Registration Pending"));
            } else if (bg.getStatus().equals("buyer_pending")) {
                holder.buyerStatus.setText(StringUtils.capitalize("Pending"));
            } else if (bg.getStatus().equals("supplier_pending")) {
                holder.buyerStatus.setText(StringUtils.capitalize("Supplier Pending"));
            }

            holder.buyerName.setText(StringUtils.capitalize(bg.getName()));
            holder.buyerNumber.setText(StringUtils.capitalize(bg.getPhone()));

            holder.buyerNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + bg.getPhone().toLowerCase().trim()));
                    mActivity.startActivity(intent);
                }
            });


            // holder.buyerStatus.setText(StringUtils.capitalize(bg.getInvitee().getInvite_type().toLowerCase().trim()));


        }

        holder.Approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(mActivity);
                Gson gson = new Gson();
                approveBuyer approveBuyer = new approveBuyer(allItems.get(position).getId(), "approved");
                HttpManager.getInstance(mActivity).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mActivity, "buyers", "") + allItems.get(position).getId() + '/', gson.fromJson(gson.toJson(approveBuyer), JsonObject.class), headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        allItems.remove(position);
                        notifyDataSetChanged();
                        StaticFunctions.showToast(mActivity, "Buyer approved successfully");
                        // progressDialog.dismiss();

                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        //  progressDialog.dismiss();
                    }
                });
            }
        });


        holder.Resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(mActivity);
                ArrayList<String> list = new ArrayList<String>();
                list.add(bg.getId());
                ResendBuyerID resendBuyer = new ResendBuyerID(list);
                Gson gson1 = new Gson();
                HttpManager.getInstance(mActivity).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(mActivity, "buyers", "") + "resend/", (gson1.fromJson(gson1.toJson(resendBuyer), JsonObject.class)), headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Log.d("Succefully", "Added");
                        StaticFunctions.showToast(mActivity, "Buyer reinvited succefully");
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                    }
                });
            }
        });

    }

    private void configureContactViewholder(final Contact_ViewHolder holder, int position) {
        final MergeContacts contact = allItems.get(position);
        holder.contactName.setText(StringUtils.capitalize(contact.getName().toLowerCase().trim()));
        holder.contactNumber.setText(StringUtils.capitalize(contact.getPhone().toLowerCase().trim()));
        holder.contactNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.getPhone().toLowerCase().trim()));
                mActivity.startActivity(intent);
            }
        });
        if (holder.spinner_grouptype.getChildCount() > 0) {
            holder.spinner_grouptype.setSelection(Integer.parseInt(Activity_Home.pref.getString("position_buyer_group", "0")));
        }

        try {

            SpinAdapter_grouptypes spinAdapter_grouptypes = new SpinAdapter_grouptypes(mActivity, R.layout.spinneritem, response_buyerGroupType);
            holder.spinner_grouptype.setAdapter(spinAdapter_grouptypes);
            holder.spinner_grouptype.setSelection(Integer.parseInt(Activity_Home.pref.getString("position_buyer_group", "0")));
        } catch (Exception e) {

        }

        holder.spinner_grouptype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Activity_Home.pref.edit().putString("position_buyer_group", String.valueOf(position)).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        holder.invite_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("buyer_name", contact.getName().toLowerCase().trim());
                    params.put("buyer_number", contact.getPhone().toLowerCase().trim());
                    params.put("country", "1");
                    params.put("group_type", ((Response_BuyerGroupType) holder.spinner_grouptype.getSelectedItem()).getId());
                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(mActivity);
                    HttpManager.getInstance(mActivity).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.companyUrl(mActivity, "buyers", ""), params, headers, false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {

                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            Toast.makeText(mActivity, "Buyer invited successfully", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {
                            Toast.makeText(mActivity, error.getErrormessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }


    @Override
    public int getItemViewType(int position) {
        if (!allItems.get(position).getId().equals("")) {
            return BUYER;
        } else {
            return CONTACT;
        }
    }

    @Override
    public int getItemCount() {
        return allItems.size();
    }

}