package com.wishbook.catalog.commonadapters;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.Enquiry_Details;
import com.wishbook.catalog.commonmodels.responses.Response_Suppliers;
import com.wishbook.catalog.home.contacts.Fragment_SuppliersEnquiry;
import com.wishbook.catalog.home.orders.add.Fragment_CreatePurchaseOrder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nani on 17-04-2016.
 */
public class EnquirySuppliersAdapters extends RecyclerView.Adapter<EnquirySuppliersAdapters.MyViewHolder> {

    private AppCompatActivity mActivity;
    private ArrayList<Response_Suppliers> mApprovedSuppliersList;
    //List<String> buyerCompanyNames;


    public EnquirySuppliersAdapters(AppCompatActivity mActivity, ArrayList<Response_Suppliers> mApprovedSuppliersList) {
        this.mActivity = mActivity;
        this.mApprovedSuppliersList = mApprovedSuppliersList;
        // this.buyerCompanyNames = buyerCompanyNames;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView sellerName, sellerNumber, sellerType;
        private AppCompatButton chat_user;
        private AppCompatButton respond_references,create_order;

        public MyViewHolder(View view) {
            super(view);
            sellerName = (TextView) view.findViewById(R.id.seller_name);
            sellerNumber = (TextView) view.findViewById(R.id.seller_number);
            sellerType = (TextView) view.findViewById(R.id.seller_status);
            chat_user = (AppCompatButton) view.findViewById(R.id.chat_user);
            respond_references = (AppCompatButton) view.findViewById(R.id.respond_references);
            create_order = (AppCompatButton) view.findViewById(R.id.create_order);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.approved_enquiry_suppliers_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Response_Suppliers bg = mApprovedSuppliersList.get(position);

        if (bg.getSelling_company() != null) {
            final String companyName = bg.getSelling_company_name();
            final String phoneNumber = bg.getSelling_company_phone_number();

            if(bg.getStatus().equals("Pending References")){
                holder.respond_references.setVisibility(View.VISIBLE);
            }else
            {
                holder.respond_references.setVisibility(View.GONE);
            }

            String append = "Status: ";
            if(bg.getStatus().equals("Pending References")){
                holder.sellerType.setText(append+bg.getStatus());
                holder.create_order.setVisibility(View.INVISIBLE);

            }else if(bg.getStatus().equals("References Filled")) {
                holder.sellerType.setText(append+"References Filled");
                holder.create_order.setVisibility(View.INVISIBLE);
            }
            else if(bg.getStatus().toLowerCase().equals("approved")){
                holder.sellerType.setText(append+"Approved");
                holder.create_order.setVisibility(View.VISIBLE);
            }
            else if(bg.getStatus().toLowerCase().equals("rejected")){
                holder.sellerType.setText(append+"Rejected");
                holder.create_order.setVisibility(View.INVISIBLE);
            }
            else {
                holder.sellerType.setText(append+"Pending");
                holder.create_order.setVisibility(View.INVISIBLE);
            }

            holder.create_order.setVisibility(View.INVISIBLE);
            holder.create_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Application_Singleton.CONTAINER_TITLE = mActivity.getResources().getString(R.string.new_purchase_order);
                    Fragment_CreatePurchaseOrder purchase = new Fragment_CreatePurchaseOrder();
                    Bundle bundle = new Bundle();
                    bundle.putString("sellerid",bg.getId());
                    purchase.setArguments(bundle);
                    Application_Singleton.CONTAINERFRAG = purchase;
                    Intent intent = new Intent(mActivity, OpenContainer.class);
                    mActivity.startActivity(intent);
                }
            });



          /*  final String emailID = bg.getSelling_company().getEmail();*/
            holder.chat_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, ConversationActivity.class);
                    intent.putExtra(ConversationUIService.USER_ID, bg.getSelling_company_chat_user());
                    intent.putExtra(ConversationUIService.DISPLAY_NAME, companyName); //put it for displaying the title.
                    mActivity.startActivity(intent);
                }
            });
            holder.sellerName.setText(StringUtils.capitalize(companyName.toLowerCase().trim()));
            holder.sellerNumber.setText(StringUtils.capitalize(phoneNumber.toLowerCase().trim()));
           /* if(bg.getGroup_type_name()!=null) {
                holder.sellerType.setText(StringUtils.capitalize(bg.getGroup_type_name().toLowerCase().trim()));
            }*/

            holder.sellerNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                    mActivity.startActivity(intent);
                }
            });

            holder.respond_references.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(mActivity).title("Fill Reference").inputType(InputType.TYPE_CLASS_TEXT).positiveText("Ok").input(null, null, false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            HashMap<String, String> headers = StaticFunctions.getAuthHeader(mActivity);
                            Enquiry_Details details = Application_Singleton.gson.fromJson(bg.getDetails(), Enquiry_Details.class);
                            details.setCatalog(details.getCatalog().replace("u","").replace("'",""));
                            details.setRefrences(input.toString());
                            Gson gson1 = new Gson();

                            String detail = Application_Singleton.gson.toJson(details);
                            PatchDetails patch = new PatchDetails(detail,"References Filled");

                            HttpManager.getInstance(mActivity).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mActivity,"sellers","")+ bg.getId() + '/', (gson1.fromJson(gson1.toJson(patch), JsonObject.class)), headers, new HttpManager.customCallBack() {
                                @Override
                                public void onCacheResponse(String response) {

                                }

                                @Override
                                public void onServerResponse(String response, boolean dataUpdated) {
                                    bg.setStatus("References Filled");
                                    holder.respond_references.setVisibility(View.INVISIBLE);
                                    notifyDataSetChanged();
                                    HttpManager.getInstance(mActivity).removeCacheParams(URLConstants.companyUrl(mActivity,"sellers_enquiry","")+"&&limit="+ Fragment_SuppliersEnquiry.LIMIT+"&&offset="+0+"&&search="+"",null);
                                    StaticFunctions.showToast(mActivity, "Responded Successfully");

                                }

                                @Override
                                public void onResponseFailed(ErrorString error) {
                                }
                            });
                        }
                    }).show();
                }
            });

        } else {

            holder.sellerName.setText("N/A");
            holder.sellerNumber.setText("N/A");
            // holder.sellerType.setText("N/A");
            //holder.sellerEmailTv.setText("N/A");
        }
    }

    @Override
    public int getItemCount() {
        return mApprovedSuppliersList.size();
    }
}
