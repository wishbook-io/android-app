package com.wishbook.catalog.commonadapters;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;
import com.wishbook.catalog.home.contacts.details.Fragment_BuyerDetails;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by prane on 21-03-2016.
 */
public class RejectedBuyersAdapter extends RecyclerView.Adapter<RejectedBuyersAdapter.MyViewHolder> {

    private AppCompatActivity mActivity;
    private Response_Buyer[] mRejectedBuyersList;


    public RejectedBuyersAdapter(AppCompatActivity mActivity, Response_Buyer[] mRejectedBuyersList) {
        this.mActivity = mActivity;
        this.mRejectedBuyersList = mRejectedBuyersList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout containerrej;
        public TextView buyerName, buyerNumber, buyerType, buyerEmailTv;

        public MyViewHolder(View view) {
            super(view);
            buyerName = (TextView) view.findViewById(R.id.buyer_name);
            buyerNumber = (TextView) view.findViewById(R.id.buyer_number);
            buyerType = (TextView) view.findViewById(R.id.buyer_type);
            containerrej = (RelativeLayout) view.findViewById(R.id.containerrej);
            buyerEmailTv = (TextView) view.findViewById(R.id.buyer_email_tv);


        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rejected_buyers_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {


        Response_Buyer bg = mRejectedBuyersList[position];
        if (bg.getBuying_company() != null) {
            final String companyName = bg.getBuying_company_name();
            final String phoneNumber = bg.getBuying_company_phone_number();

            if(companyName!=null){
                holder.buyerName.setText(StringUtils.capitalize(companyName.toLowerCase().trim()));
            } else {
                holder.buyerName.setText("");
            }

            holder.buyerNumber.setText(StringUtils.capitalize(phoneNumber.toLowerCase().trim()));

            holder.buyerEmailTv.setVisibility(View.GONE);

            if(bg.getBuyer_type().equals("Enquiry")){
                holder.buyerType.setText("Enquiry " + StringUtils.capitalize(bg.getStatus()));
            }else{
                holder.buyerType.setText(StringUtils.capitalize(bg.getStatus()));
            }

            holder.containerrej.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle =new Bundle();
                    bundle.putString("buyerid",mRejectedBuyersList[position].getId());
                    bundle.putString("isBuyerRejected","yes");
                    Application_Singleton.CONTAINER_TITLE = "Buyer Details";
                    Application_Singleton.TOOLBARSTYLE = "WHITE";
                    Fragment_BuyerDetails buyerDetailsApprovedFrag=new Fragment_BuyerDetails();
                    buyerDetailsApprovedFrag.setArguments(bundle);
                    Application_Singleton.CONTAINER_TITLE="Buyer Details";
                    Application_Singleton.CONTAINERFRAG=buyerDetailsApprovedFrag;
                    //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                    StaticFunctions.switchActivity(mActivity, OpenContainer.class);

                //    mActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new BuyerDetailFrag()).addToBackStack("buyerdet").commit();

                }
            });

        } else {

            holder.buyerName.setText("N/A");
            holder.buyerNumber.setText("N/A");
            holder.buyerType.setText("N/A");
            holder.buyerEmailTv.setText("N/A");
        }
    }

    @Override
    public int getItemCount() {
        return mRejectedBuyersList.length;
    }

}
