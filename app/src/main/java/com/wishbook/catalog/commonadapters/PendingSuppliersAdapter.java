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
import com.wishbook.catalog.commonmodels.postpatchmodels.ResendSupplierID;
import com.wishbook.catalog.commonmodels.postpatchmodels.approveSupplier;
import com.wishbook.catalog.commonmodels.responses.Response_Suppliers;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nani on 17-04-2016.
 */
public class PendingSuppliersAdapter extends RecyclerView.Adapter<PendingSuppliersAdapter.MyViewHolder> {

    private AppCompatActivity mActivity;
    private ArrayList<Response_Suppliers> mPendingSuppliersList;

    public PendingSuppliersAdapter(AppCompatActivity mActivity, ArrayList<Response_Suppliers> mPendingSuppliersList) {
        this.mActivity = mActivity;
        this.mPendingSuppliersList = mPendingSuppliersList;
        // this.buyerCompanyNames = buyerCompanyNames;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView sellerName, sellerNumber,sellerStatus,Resend,Approve;;
        public MyViewHolder(View view) {
            super(view);
            sellerName = (TextView) view.findViewById(R.id.seller_name);
            sellerNumber = (TextView) view.findViewById(R.id.seller_number);
            sellerStatus = (TextView) view.findViewById(R.id.seller_status);
            Resend = (TextView) view.findViewById(R.id.resend);
            Approve = (TextView) view.findViewById(R.id.approve);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.approved_suppliers_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Response_Suppliers bg = mPendingSuppliersList.get(position);
        if(bg.getStatus()!=null) {
            if (bg.getStatus().equals("buyer_pending")) {
                holder.Approve.setVisibility(View.VISIBLE);
                holder.Resend.setVisibility(View.GONE);
            } else {
                holder.Approve.setVisibility(View.GONE);
                holder.Resend.setVisibility(View.VISIBLE);
            }
        }
        String mobile;
        if (bg.getSelling_company() != null) {
            final String companyName = bg.getSelling_company_name();
            final String phoneNumber = bg.getSelling_company_phone_number();


            holder.sellerName.setText(StringUtils.capitalize(companyName.toLowerCase().trim()));
            holder.sellerNumber.setText(phoneNumber);
            if(bg.getStatus().equals("supplier_registrationpending")) {
                holder.sellerStatus.setText(StringUtils.capitalize("Registration Pending"));
            }
            else if(bg.getStatus().equals("supplier_pending"))
            {
                holder.sellerStatus.setText(StringUtils.capitalize("Pending"));
            }
            else if(bg.getStatus().equals("buyer_pending"))
            {
                holder.sellerStatus.setText(StringUtils.capitalize("Buyer Pending"));
            }
           /* if (bg.getStatus().contains("pending")) {

                holder.sellerEmailTv.setText("Registration Pending");
            } else {
                holder.sellerEmailTv.setText(StaticFunctions.capitalizeFirstLetter(bg.getStatus()));
            }*/

            holder.sellerNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                    mActivity.startActivity(intent);
                }
            });

            holder.Approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(mActivity);
                    Gson gson = new Gson();
                    approveSupplier approvesupplier = new approveSupplier(mPendingSuppliersList.get(holder.getAdapterPosition()).getId(),"approved");
                    HttpManager.getInstance(mActivity).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mActivity,"sellers","") + mPendingSuppliersList.get(holder.getAdapterPosition()).getId() + '/', gson.fromJson(gson.toJson(approvesupplier), JsonObject.class), headers, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {

                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            mPendingSuppliersList.remove(holder.getAdapterPosition());
                            notifyDataSetChanged();
                            StaticFunctions.showToast(mActivity, "Supplier approved successfully");
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
                    ArrayList<String> list=new ArrayList<String>();
                    list.add(bg.getId());
                    ResendSupplierID resendSupplier = new ResendSupplierID(list);
                    Gson gson1 = new Gson();
                    HttpManager.getInstance(mActivity).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(mActivity,"sellers",""), (gson1.fromJson(gson1.toJson(resendSupplier), JsonObject.class)), headers,false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {

                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            Log.d("Succefully","Added");
                            StaticFunctions.showToast(mActivity, "Supplier reinvited successfully");
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {
                        }
                    });
                }
            });

        } else {

            holder.sellerName.setText(StringUtils.capitalize(bg.getInvitee_name().toLowerCase().trim()));
            holder.sellerNumber.setText(StringUtils.capitalize(bg.getInvitee_phone_number().toLowerCase().trim()));
            if(bg.getStatus().equals("supplier_registrationpending")) {
                holder.sellerStatus.setText(StringUtils.capitalize("Registration Pending"));
            }
            else if(bg.getStatus().equals("supplier_pending"))
            {
                holder.sellerStatus.setText(StringUtils.capitalize("Pending"));
            }
            else if(bg.getStatus().equals("buyer_pending"))
            {
                holder.sellerStatus.setText(StringUtils.capitalize("Buyer Pending"));
            }
            // holder.sellerEmailTv.setText(bg.getInvitee().getInvitee_company());


            holder.sellerNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + bg.getInvitee_phone_number().toLowerCase().trim()));
                    mActivity.startActivity(intent);
                }
            });

            holder.Approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(mActivity);
                    Gson gson = new Gson();
                    approveSupplier approvesupplier = new approveSupplier(mPendingSuppliersList.get(holder.getAdapterPosition()).getId(),"approved");
                    HttpManager.getInstance(mActivity).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mActivity,"sellers","") + mPendingSuppliersList.get(holder.getAdapterPosition()).getId() + '/', gson.fromJson(gson.toJson(approvesupplier), JsonObject.class), headers, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {

                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            mPendingSuppliersList.remove(holder.getAdapterPosition());
                            notifyDataSetChanged();
                            StaticFunctions.showToast(mActivity, "Supplier approved successfully");
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
                    ArrayList<String> list=new ArrayList<String>();
                    list.add(bg.getId());
                    ResendSupplierID resendSupplier = new ResendSupplierID(list);
                    Gson gson1 = new Gson();
                    HttpManager.getInstance(mActivity).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(mActivity,"sellers",""), (gson1.fromJson(gson1.toJson(resendSupplier), JsonObject.class)), headers,false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {

                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            Log.d("Succefully","Added");
                            StaticFunctions.showToast(mActivity, "Supplier reinvited successfully");
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {
                        }
                    });
                }
            });

        }


        holder.sellerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application_Singleton.selectedSupplier = mPendingSuppliersList.get(holder.getAdapterPosition());
              //  mActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new BuyerDetailFrag()).addToBackStack("buyerdet").commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mPendingSuppliersList.size();
    }


}

