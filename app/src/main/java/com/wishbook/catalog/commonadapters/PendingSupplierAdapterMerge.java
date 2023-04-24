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
import com.wishbook.catalog.commonmodels.postpatchmodels.ResendSupplierID;
import com.wishbook.catalog.commonmodels.responses.Response_BuyerGroupType;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.contacts.viewholder.Contact_ViewHolder;
import com.wishbook.catalog.home.contacts.viewholder.Pending_Suppler_ViewHolder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by prane on 21-03-2016.
 */
public class PendingSupplierAdapterMerge extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AppCompatActivity mActivity;
    private ArrayList<MergeContacts> allItems;
    private int contactsCount;
    private int supplierCount;
    private  Response_BuyerGroupType[] response_buyerGroupType;
    private final int SUPPLIER = 0, CONTACT = 1,HEADER=2;

    public PendingSupplierAdapterMerge(AppCompatActivity mActivity, ArrayList<MergeContacts>  mPendingSupplierList,Response_BuyerGroupType[] response_buyerGroupType) {
        this.mActivity = mActivity;
        this.allItems = mPendingSupplierList;
        this.response_buyerGroupType=response_buyerGroupType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case SUPPLIER:
                View v1 = inflater.inflate(R.layout.approved_suppliers_row, parent, false);
                viewHolder = new Pending_Suppler_ViewHolder(v1);
                break;
            case CONTACT:
                View v2 = inflater.inflate(R.layout.contacts_row_buyer, parent, false);
                viewHolder = new Contact_ViewHolder(v2);
                break;
          /*  case HEADER:
                View v3 = inflater.inflate(R.layout.header_row, parent, false);
                viewHolder = new Header_VIewHolder(v3);*//*
                break;*/
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case SUPPLIER:
                Pending_Suppler_ViewHolder view1 = (Pending_Suppler_ViewHolder) holder;
                configureSupplierViewholder(view1, position);
                break;
            case CONTACT:
                Contact_ViewHolder view2 = (Contact_ViewHolder) holder;
                configureContactViewholder(view2, position);
                break;
         /*   case HEADER:
                Header_VIewHolder view3 = (Header_VIewHolder) holder;
                configureHeaderViewholder(view3, position);
                break;*/
        }

    }

    /*private void configureHeaderViewholder(Header_VIewHolder holder, int position) {
        holder.title.setText(allItems.get(position).getName());
    }*/

    private void configureSupplierViewholder(Pending_Suppler_ViewHolder holder, final int position) {
        final MergeContacts bg = allItems.get(position);
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
        if (bg.getName()!= null) {
            final String companyName = bg.getName();
            final String phoneNumber = bg.getPhone();

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
            }  else if(bg.getStatus().equals("Pending References")){
                holder.sellerStatus.setText(StringUtils.capitalize("Pending References"));
            }else if(bg.getStatus().equals("References Filled")) {
                holder.sellerStatus.setText(StringUtils.capitalize("References Filled"));
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
                    approveSupplier approvesupplier = new approveSupplier(bg.getId(),"approved");
                    HttpManager.getInstance(mActivity).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mActivity,"sellers","") + bg.getId() + '/', gson.fromJson(gson.toJson(approvesupplier), JsonObject.class), headers, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {

                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            allItems.remove(position);
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

            holder.sellerName.setText(StringUtils.capitalize(bg.getName().toLowerCase().trim()));
            holder.sellerNumber.setText(StringUtils.capitalize(bg.getPhone().toLowerCase().trim()));
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
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + bg.getPhone().toLowerCase().trim()));
                    mActivity.startActivity(intent);
                }
            });

            holder.Approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(mActivity);
                    Gson gson = new Gson();
                    approveSupplier approvesupplier = new approveSupplier(bg.getId(),"approved");
                    HttpManager.getInstance(mActivity).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mActivity,"sellers","") + bg.getId() + '/', gson.fromJson(gson.toJson(approvesupplier), JsonObject.class), headers, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {

                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            allItems.remove(position);
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
                    HttpManager.getInstance(mActivity).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(mActivity,"sellers","")+"resend/", (gson1.fromJson(gson1.toJson(resendSupplier), JsonObject.class)), headers,false, new HttpManager.customCallBack() {
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


       /* holder.sellerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application_Singleton.selectedSupplier = mPendingSuppliersList.get(position);
                //  mActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new BuyerDetailFrag()).addToBackStack("buyerdet").commit();

            }
        });*/

    }

    private void configureContactViewholder(final Contact_ViewHolder holder, int position) {
        final MergeContacts contact = allItems.get(position);
        holder.contactName.setText(StringUtils.capitalize(contact.getName().toLowerCase().trim()));
        holder.contactNumber.setText(StringUtils.capitalize(contact.getPhone().toLowerCase().trim()));

        if(holder.spinner_grouptype.getChildCount()>0)
        {
            holder.spinner_grouptype.setSelection(Integer.parseInt(Activity_Home.pref.getString("position_buyer_group","0")));
        }

        holder.contactNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.getPhone().toLowerCase().trim()));
                mActivity.startActivity(intent);
            }
        });

        SpinAdapter_grouptypes spinAdapter_grouptypes = new SpinAdapter_grouptypes(mActivity, R.layout.spinneritem, response_buyerGroupType);
        holder.spinner_grouptype.setAdapter(spinAdapter_grouptypes);
        holder.spinner_grouptype.setSelection(Integer.parseInt(Activity_Home.pref.getString("position_buyer_group","0")));


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
                    params.put("supplier_name", contact.getName().toLowerCase().trim());
                    params.put("country","1");
                    params.put("supplier_number", contact.getPhone().toLowerCase().trim());
                    params.put("group_type", ((Response_BuyerGroupType)holder.spinner_grouptype.getSelectedItem()).getId());
                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(mActivity);
                    HttpManager.getInstance(mActivity).request(HttpManager.METHOD.POSTJSONWITHPROGRESS, URLConstants.companyUrl(mActivity,"sellers",""), params, headers, false, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {

                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            Toast.makeText(mActivity, "Supplier invited successfully", Toast.LENGTH_SHORT).show();

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
                return SUPPLIER;
            } else {
                return CONTACT;
        }
    }

    @Override
    public int getItemCount() {
        return allItems.size();
    }


    class approveSupplier {
        String id;
        String status;

        public approveSupplier(String id,String status) {
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