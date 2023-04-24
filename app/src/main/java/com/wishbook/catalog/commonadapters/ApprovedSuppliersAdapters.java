package com.wishbook.catalog.commonadapters;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.ResponseCodes;
import com.wishbook.catalog.commonmodels.responses.Response_Suppliers;
import com.wishbook.catalog.home.contacts.Fragment_SuppliersApproved;
import com.wishbook.catalog.home.contacts.details.ActionLogApi;
import com.wishbook.catalog.home.contacts.details.Fragment_Details_Catalog;
import com.wishbook.catalog.home.contacts.details.Fragment_SupplierDetailsNew2;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by nani on 17-04-2016.
 */
public class ApprovedSuppliersAdapters extends RecyclerView.Adapter<ApprovedSuppliersAdapters.MyViewHolder> {

    private Fragment_SuppliersApproved mActivity;
    private ArrayList<Response_Suppliers> mApprovedSuppliersList;
    //List<String> buyerCompanyNames;


    public ApprovedSuppliersAdapters(Fragment_SuppliersApproved mActivity, ArrayList<Response_Suppliers> mApprovedSuppliersList) {
        this.mActivity = mActivity;
        this.mApprovedSuppliersList = mApprovedSuppliersList;
        // this.buyerCompanyNames = buyerCompanyNames;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout itemcontainer;
        public TextView sellerName, sellerNumber, seller_company_name;
        private AppCompatButton chat_user, view_catalog, btn_call;

        public MyViewHolder(View view) {
            super(view);
            sellerName = (TextView) view.findViewById(R.id.seller_name);
            sellerNumber = (TextView) view.findViewById(R.id.seller_number);
            seller_company_name = view.findViewById(R.id.seller_company_name);
            chat_user = (AppCompatButton) view.findViewById(R.id.chat_user);
            view_catalog = (AppCompatButton) view.findViewById(R.id.view_catalog);
            itemcontainer = (LinearLayout) view.findViewById(R.id.itemcontainer);
            btn_call = view.findViewById(R.id.btn_call);

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
        final Response_Suppliers bg = mApprovedSuppliersList.get(position);


        //change According Jira(/WB-1157)

        // change for bug #WB-850


        if (bg.getSelling_company() != null) {
            final String companyName = bg.getSelling_company_name();
            final String phoneNumber = bg.getSelling_company_phone_number();

            /*  final String emailID = bg.getSelling_company().getEmail();*/
            holder.chat_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ActionLogApi(mActivity.getActivity(), ActionLogApi.RELATION_TYPE_SUPPLIER, ActionLogApi.ACTION_TYPE_CHAT, bg.getSelling_company());
                    Intent intent = new Intent(mActivity.getActivity(), ConversationActivity.class);
                    intent.putExtra(ConversationUIService.USER_ID, bg.getSelling_company_chat_user());
                    intent.putExtra(ConversationUIService.DISPLAY_NAME, companyName); //put it for displaying the title.
                    intent.putExtra(ConversationUIService.TAKE_ORDER, true); //Skip chat list for showing on back press
                    mActivity.startActivity(intent);
                }
            });


            // change for bug #WB-1200
            if (bg.getSupplier_person_name() != null) {
                holder.sellerName.setVisibility(View.VISIBLE);
                holder.sellerName.setText(StringUtils.capitalize(bg.getSupplier_person_name().toLowerCase().trim()));
                if (bg.getSelling_company() != null) {
                    holder.seller_company_name.setVisibility(View.VISIBLE);
                    holder.seller_company_name.setText("(" + StringUtils.capitalize(bg.getSelling_company_name().toLowerCase().trim() + ")"));
                }
            } else {
                if (bg.getSelling_company_name() != null) {
                    holder.sellerName.setVisibility(View.VISIBLE);
                    holder.seller_company_name.setVisibility(View.VISIBLE);
                    holder.sellerName.setText(StringUtils.capitalize(companyName.toLowerCase().trim()));
                    holder.seller_company_name.setText("(" + StringUtils.capitalize(bg.getSelling_company_name().toLowerCase().trim() + ")"));
                } else {
                    holder.sellerName.setVisibility(View.GONE);
                    holder.seller_company_name.setVisibility(View.GONE);
                }
            }


            // holder.sellerNumber.setText(StringUtils.capitalize(phoneNumber.toLowerCase().trim()));
            /*if(bg.getGroup_type_name()!=null) {
                holder.sellerType.setText(StringUtils.capitalize(bg.getGroup_type_name().toLowerCase().trim()));
            }*/
            // holder.sellerEmailTv.setText(emailID);
            holder.itemcontainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Application_Singleton.selectedSupplier = mApprovedSuppliersList[position];

                    Bundle bundle = new Bundle();
                    bundle.putString("sellerid", mApprovedSuppliersList.get(holder.getAdapterPosition()).getId());
                    bundle.putString("sellerCompanyid", mApprovedSuppliersList.get(holder.getAdapterPosition()).getSelling_company());
                    Application_Singleton.CONTAINER_TITLE = "Supplier Details";
                    Application_Singleton.TOOLBARSTYLE = "WHITE";
                    //Fragment_SupplierDetails supplier=new Fragment_SupplierDetails();
                    Fragment_SupplierDetailsNew2 supplier = new Fragment_SupplierDetailsNew2();
                    supplier.setArguments(bundle);

                    Application_Singleton.CONTAINERFRAG = supplier;
                    //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                    Intent intent = new Intent(mActivity.getActivity(), OpenContainer.class);
                    mActivity.startActivityForResult(intent, ResponseCodes.Supplier_Approved);
                }
            });
            holder.btn_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ActionLogApi(mActivity.getActivity(), ActionLogApi.RELATION_TYPE_SUPPLIER, ActionLogApi.ACTION_TYPE_CALL, bg.getSelling_company());
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                    mActivity.startActivity(intent);
                }
            });
           /* holder.sellerEmailTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + emailID));
                    mActivity.startActivity(intent);
                }
            });*/

            holder.view_catalog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment_Details_Catalog fragment_details_catalog = new Fragment_Details_Catalog();
                    Bundle bundle = new Bundle();
                    bundle.putString("sellerCompanyid", bg.getSelling_company());
                    bundle.putString("isLinear","true");
                    fragment_details_catalog.setArguments(bundle);
                    Application_Singleton.CONTAINER_TITLE = "Products";
                    Application_Singleton.CONTAINERFRAG = fragment_details_catalog;
                    Intent intent = new Intent(mActivity.getActivity(), OpenContainer.class);
                    intent.putExtra("havingTabs", true);
                    Application_Singleton.trackEvent("Approved Supplier", "Click", "View Catalog");
                    mActivity.startActivity(intent);
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
