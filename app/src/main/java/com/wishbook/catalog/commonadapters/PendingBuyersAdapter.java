package com.wishbook.catalog.commonadapters;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.postpatchmodels.ResendBuyerID;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by prane on 21-03-2016.
 */
public class PendingBuyersAdapter extends RecyclerView.Adapter<PendingBuyersAdapter.MyViewHolder> {

    private AppCompatActivity mActivity;
    private ArrayList<Response_Buyer> mPendingBuyersList;


    public PendingBuyersAdapter(AppCompatActivity mActivity, ArrayList<Response_Buyer> mPendingBuyersList) {
        this.mActivity = mActivity;
        this.mPendingBuyersList = mPendingBuyersList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView buyerName, buyerNumber, buyerStatus, Resend,Approve;

        public MyViewHolder(View view) {
            super(view);
            buyerName = (TextView) view.findViewById(R.id.buyer_name);
            buyerNumber = (TextView) view.findViewById(R.id.buyer_number);
            buyerStatus = (TextView) view.findViewById(R.id.buyer_status);
            Resend = (TextView) view.findViewById(R.id.resend);
            Approve = (TextView) view.findViewById(R.id.approve);


        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_buyers_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Response_Buyer bg = mPendingBuyersList.get(position);
        if(bg.getStatus()!=null) {
            if (bg.getStatus().equals("supplier_pending")) {
                holder.Approve.setVisibility(View.VISIBLE);
                holder.Resend.setVisibility(View.GONE);
            } else {
                holder.Approve.setVisibility(View.GONE);
                holder.Resend.setVisibility(View.VISIBLE);
            }
        }
        if (bg.getBuying_company()!= null) {
            final String companyName = bg.getBuying_company_name();
            final String phoneNumber = bg.getBuying_company_phone_number();


            holder.buyerName.setText(StringUtils.capitalize(companyName.toLowerCase().trim()));
            holder.buyerNumber.setText(StringUtils.capitalize(phoneNumber.toLowerCase().trim()));
            if(bg.getStatus().equals("buyer_registrationpending")) {
                holder.buyerStatus.setText(StringUtils.capitalize("Registration Pending"));
            }
            else if(bg.getStatus().equals("buyer_pending"))
            {
                holder.buyerStatus.setText(StringUtils.capitalize("Pending"));
            }
            else if(bg.getStatus().equals("supplier_pending"))
            {
                holder.buyerStatus.setText(StringUtils.capitalize("Supplier Pending"));
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

            holder.buyerName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Application_Singleton.navselectedBuyer=mPendingBuyersList.get(holder.getAdapterPosition());
                   // mActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new BuyerDetailFrag()).addToBackStack("buyerdet").commit();

                }
            });

        } else {
            holder.buyerName.setText("N/A");
            holder.buyerNumber.setText("N/A");
            if(bg.getStatus().equals("buyer_registrationpending")) {
                holder.buyerStatus.setText(StringUtils.capitalize("Registration Pending"));
            }
            else if(bg.getStatus().equals("buyer_pending"))
            {
                holder.buyerStatus.setText(StringUtils.capitalize("Pending"));
            }
            else if(bg.getStatus().equals("supplier_pending"))
            {
                holder.buyerStatus.setText(StringUtils.capitalize("Supplier Pending"));
            }


          if(bg.getInvitee()!=null){
              holder.buyerName.setText(StringUtils.capitalize(bg.getInvitee_name().toLowerCase().trim()));
              holder.buyerNumber.setText(StringUtils.capitalize(bg.getInvitee_phone_number().toLowerCase().trim()));

              holder.buyerNumber.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + bg.getInvitee_phone_number().toLowerCase().trim()));
                      mActivity.startActivity(intent);
                  }
              });


             // holder.buyerStatus.setText(StringUtils.capitalize(bg.getInvitee().getInvite_type().toLowerCase().trim()));

          }

        }

        holder.Approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(mActivity);
                Gson gson = new Gson();
                approveBuyer approveBuyer = new approveBuyer(mPendingBuyersList.get(holder.getAdapterPosition()).getId(),"approved");
                HttpManager.getInstance(mActivity).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mActivity,"buyers","")+ mPendingBuyersList.get(holder.getAdapterPosition()).getId() + '/', gson.fromJson(gson.toJson(approveBuyer), JsonObject.class), headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        mPendingBuyersList.remove(holder.getAdapterPosition());
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
             /*   HashMap<String, String> headers = StaticFunctions.getAuthHeader(mActivity);
                HashMap<String, String> params=new HashMap<String, String>();
                params.put("buyers",bg.getId());
                HttpManager.getInstance(mActivity).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.companyUrl(getActivity(),"buyers",""), params, headers, true, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        Log.v("cached response", response);
                        //  onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Log.v("sync response", response);
                        StaticFunctions.showToast(mActivity, "request sent");
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        new MaterialDialog.Builder(mActivity)
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
                });*/
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(mActivity);
                ArrayList<String> list=new ArrayList<String>();
                list.add(bg.getId());
                ResendBuyerID resendBuyer = new ResendBuyerID(list);
                Gson gson1 = new Gson();
                HttpManager.getInstance(mActivity).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(mActivity,"buyers",""), (gson1.fromJson(gson1.toJson(resendBuyer), JsonObject.class)), headers,false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Log.d("Succefully","Added");
                        StaticFunctions.showToast(mActivity, "Buyer reinvited succefully");
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPendingBuyersList.size();
    }


  /*  private class ResendBuyerID{
        ArrayList<String> ids;

        public ResendBuyerID(ArrayList<String> id) {
            this.ids = id;
        }

        public ArrayList<String> getIds() {
            return ids;
        }

        public void setIds(ArrayList<String> id) {
            this.ids = id;
        }
    }*/


    class approveBuyer {
        String id;
        String status;

        public approveBuyer(String id,String status) {
            this.id = id;
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

}