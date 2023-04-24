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
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;
import com.wishbook.catalog.home.catalog.BuyerShareCatalogHolder;
import com.wishbook.catalog.home.contacts.Fragment_BuyersApproved;
import com.wishbook.catalog.home.contacts.details.ActionLogApi;
import com.wishbook.catalog.home.contacts.details.Fragment_BuyerDetails;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by prane on 21-03-2016.
 */
public class ApprovedBuyersAdapter extends RecyclerView.Adapter<ApprovedBuyersAdapter.MyViewHolder> {

    private Fragment_BuyersApproved mActivity;
    private ArrayList<Response_Buyer> mApprovedBuyersList;
    private Boolean showTutorial = true;
    UserInfo userInfo;


    public ApprovedBuyersAdapter(Fragment_BuyersApproved mActivity, ArrayList<Response_Buyer> mApprovedBuyersList) {
        this.mActivity = mActivity;
        this.mApprovedBuyersList = mApprovedBuyersList;
        userInfo = new UserInfo(mActivity.getContext());

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout itemcontainer;
        public TextView buyerName, buyerNumber, buyerType,buyer_company_name ;
        private AppCompatButton chat_user, share_catalog, btn_call;

        public MyViewHolder(View view) {
            super(view);
            buyerName = (TextView) view.findViewById(R.id.buyer_name);
            buyerNumber = (TextView) view.findViewById(R.id.buyer_number);
            buyer_company_name = view.findViewById(R.id.buyer_company_name);
             buyerType = (TextView) view.findViewById(R.id.buyer_type);
            chat_user = (AppCompatButton) view.findViewById(R.id.chat_user);
            share_catalog = (AppCompatButton) view.findViewById(R.id.share_catalog);
            btn_call = view.findViewById(R.id.btn_call);
            itemcontainer = (LinearLayout) view.findViewById(R.id.itemcontainer);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.approved_buyers_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Response_Buyer bg = mApprovedBuyersList.get(position);

        if (bg.getBuying_company() != null) {

            final String companyName = bg.getBuying_company_name();
            final String buyingPersonName = bg.getBuying_person_name();
            final String phoneNumber = bg.getBuying_company_phone_number();
            final String companyType = bg.getGroup_type_name();
            holder.chat_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ActionLogApi(mActivity.getActivity(), ActionLogApi.RELATION_TYPE_BUYER, ActionLogApi.ACTION_TYPE_CHAT, bg.getBuying_company());
                    Intent intent = new Intent(mActivity.getContext(), ConversationActivity.class);
                    intent.putExtra(ConversationUIService.USER_ID, bg.getBuying_company_chat_user());
                    intent.putExtra(ConversationUIService.DISPLAY_NAME, companyName); //put it for displaying the title.
                    intent.putExtra(ConversationUIService.TAKE_ORDER,true); //Skip chat list for showing on back press
                    mActivity.startActivity(intent);

                }
            });

            holder.buyerType.setText(bg.getGroup_type_name());

           /*// if(companyName != null){
                holder.buyerName.setText(StringUtils.capitalize(companyName.toLowerCase().trim()));

          //  }*/
            // change for bug #WB-850
         /*   if (buyingPersonName != null) {
                holder.buyerName.setVisibility(View.VISIBLE);
                holder.buyerName.setText(StringUtils.capitalize(buyingPersonName.toLowerCase().trim()));
            } else {
                if (companyName != null) {
                    holder.buyerName.setVisibility(View.VISIBLE);
                    holder.buyerName.setText(StringUtils.capitalize(companyName.toLowerCase().trim()));
                } else {
                    holder.buyerName.setVisibility(View.GONE);
                }
            }*/

            // change for bug #WB-1200

            if(buyingPersonName!=null) {
                holder.buyerName.setVisibility(View.VISIBLE);
                holder.buyerName.setText(StringUtils.capitalize(buyingPersonName.toLowerCase().trim()));
                if(companyName!=null) {
                    holder.buyer_company_name.setVisibility(View.VISIBLE);
                    holder.buyer_company_name.setText("("+StringUtils.capitalize(companyName.toLowerCase().trim()+")"));
                }
            } else {
                if (companyName != null) {
                    holder.buyerName.setVisibility(View.VISIBLE);
                    holder.buyer_company_name.setVisibility(View.VISIBLE);
                    holder.buyerName.setText(StringUtils.capitalize(companyName.toLowerCase().trim()));
                    holder.buyer_company_name.setText("("+StringUtils.capitalize(companyName.toLowerCase().trim()+")"));
                } else {
                    holder.buyerName.setVisibility(View.GONE);
                    holder.buyer_company_name.setVisibility(View.GONE);
                }
            }

            // change According Jira(WB-1157)
            // holder.buyerNumber.setText(StringUtils.capitalize(phoneNumber.toLowerCase().trim()));
           /* if(companyType!=null) {
                holder.buyerType.setText(StringUtils.capitalize(companyType.toLowerCase().trim()));
            }*/

            holder.btn_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ActionLogApi(mActivity.getActivity(), ActionLogApi.RELATION_TYPE_BUYER, ActionLogApi.ACTION_TYPE_CALL, bg.getBuying_company());
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                    mActivity.startActivity(intent);

                }
            });


            // change According Jira(WB-1157)
           /* holder.buyerNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                    mActivity.startActivity(intent);
                }
            });*/
            /*holder.buyerEmailTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + emailID));
                    mActivity.startActivity(intent);
                }
            });*/
            holder.itemcontainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //   mActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new BuyerDetailFrag()).addToBackStack("buyerdet").commit();
                    //Application_Singleton.navselectedBuyer=mApprovedBuyersList[position];
                    Application_Singleton.CONTAINER_TITLE = "Buyer Details";
                    Application_Singleton.TOOLBARSTYLE = "WHITE";
                    Fragment_BuyerDetails buyerapproved = new Fragment_BuyerDetails();
                    Bundle bundle = new Bundle();
                    bundle.putString("buyerid", mApprovedBuyersList.get(holder.getAdapterPosition()).getId());
                    buyerapproved.setArguments(bundle);
                    Application_Singleton.CONTAINERFRAG = buyerapproved;
                    //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                    Intent intent = new Intent(mActivity.getContext(), OpenContainer.class);
                    mActivity.startActivityForResult(intent, ResponseCodes.Buyers_Approved);
                }
            });

            holder.share_catalog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BuyerShareCatalogHolder fragmentCatalogs = new BuyerShareCatalogHolder();
                    Application_Singleton.CONTAINER_TITLE = "Catalogs";
                    Application_Singleton.CONTAINERFRAG = fragmentCatalogs;
                    //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                    Intent intent = new Intent(mActivity.getContext(), OpenContainer.class);
                    intent.putExtra("havingTabs", true);
                    mActivity.startActivity(intent);

                }
            });

            if (showTutorial && !userInfo.getGroupstatus().equals("2")) {
                showTutorial = false;
                //StaticFunctions.checkAndShowTutorial(mActivity.getContext(), "buyer_mynetwork_share_button_tutorial", holder.share_catalog, mActivity.getString(R.string.buyer_mynetwork_share_button_tutorial), "bottom");
            }

            if (userInfo.getGroupstatus().equals("2") && !userInfo.getBuyerSplitSalesperson()) {
                holder.share_catalog.setVisibility(View.GONE);
            } else {
                holder.share_catalog.setVisibility(View.GONE);
            }

            holder.share_catalog.setVisibility(View.GONE);

        } else {

            holder.buyerName.setText("N/A");
            holder.buyerNumber.setText("N/A");
            // holder.buyerType.setText("N/A");
            //holder.buyerEmailTv.setText("N/A");
        }

    }

    @Override
    public int getItemCount() {
        return mApprovedBuyersList.size();
    }

}
