package com.wishbook.catalog.commonadapters;

/**
 * Created by Vigneshkarnika on 22/03/16.
 */

import android.content.Context;
import android.graphics.Point;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.InviteContactsModel;
import com.wishbook.catalog.commonmodels.MyContacts;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Response_BuyerGroupType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SuggestionContactsAdapter extends RecyclerView.Adapter<SuggestionContactsAdapter.ViewHolder> {

    private final AppCompatActivity context;
    private List<MyContacts> mDataset;
    private int height;
    private int width;
    String inviteAs;
    private Response_BuyerGroupType[] response_buyerGroupType;

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected SimpleDraweeView imageView;
        protected TextView company_name;
        protected TextView buyer_invite;
        protected TextView supplier_invite;
        protected Spinner spinner_grouptype;
        protected ImageView remove_image;
        LinearLayout itemcontainer;
        RelativeLayout main_layout;

        public ViewHolder(View view) {
            super(view);
            this.imageView = (SimpleDraweeView) view.findViewById(R.id.img);
            this.company_name = (TextView) view.findViewById(R.id.company_name);
            this.buyer_invite = (TextView) view.findViewById(R.id.buyer_invite);
            this.supplier_invite = (TextView) view.findViewById(R.id.supplier_invite);
            this.spinner_grouptype = (Spinner) view.findViewById(R.id.group_type);
            this.remove_image = (ImageView) view.findViewById(R.id.close_image);
            itemcontainer = (LinearLayout) itemView.findViewById(R.id.itemcontainer);
            main_layout = (RelativeLayout) itemView.findViewById(R.id.main_container);
            // LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Math.round(width/2.5f),Math.round(height/3.2f));
            //itemcontainer.setLayoutParams(lp);
        }
    }

    public SuggestionContactsAdapter(AppCompatActivity context, List<MyContacts> myDataset, Response_BuyerGroupType[] response_buyerGroupType) {
        mDataset = myDataset;
        this.context = context;
        this.response_buyerGroupType = response_buyerGroupType;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.suggestion_contacts_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,  int position) {
        final MyContacts contacts = mDataset.get(position);
           holder.main_layout.setVisibility(View.VISIBLE);
            if (UserInfo.getInstance(context).getCompanyType().equals("seller") ) {
                holder.buyer_invite.setVisibility(View.VISIBLE);
                holder.supplier_invite.setVisibility(View.GONE);
            }else if (UserInfo.getInstance(context).getCompanyType().equals("buyer")) {
                holder.buyer_invite.setVisibility(View.GONE);
                holder.supplier_invite.setVisibility(View.VISIBLE);
                holder.spinner_grouptype.setVisibility(View.INVISIBLE);
            }
            else {
                if(contacts.getType()!=null) {
                    if(contacts.getType().equals("supplier")) {
                        holder.buyer_invite.setVisibility(View.GONE);
                        holder.supplier_invite.setVisibility(View.VISIBLE);
                    } else if(contacts.getType().equals("buyer")) {
                        holder.buyer_invite.setVisibility(View.VISIBLE);
                        holder.supplier_invite.setVisibility(View.GONE);
                    } else {
                        holder.buyer_invite.setVisibility(View.VISIBLE);
                        holder.supplier_invite.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.buyer_invite.setVisibility(View.VISIBLE);
                    holder.supplier_invite.setVisibility(View.VISIBLE);
                }
            }

            //removed because of supplier feedback request
            holder.supplier_invite.setVisibility(View.GONE);






            if(mDataset.get(position).getCompany_name().length()>=12){
                holder.company_name.setText(mDataset.get(position).getCompany_name().substring(0,12)+"..");
            }else {
                holder.company_name.setText(mDataset.get(position).getCompany_name());
            }
            if (mDataset.get(position).getCompany_image() != null & !mDataset.get(position).getCompany_image().equals("")) {
                //StaticFunctions.loadImage(context, mDataset.get(position).getCompany_image(), holder.imageView, R.drawable.uploadempty);
                StaticFunctions.loadFresco(context, mDataset.get(position).getCompany_image(), holder.imageView);
            }

            if (response_buyerGroupType != null) {
                try {

                    SpinnerGroupAdapterNew spinAdapter_grouptypes = new SpinnerGroupAdapterNew(context, R.layout.spinneritem, response_buyerGroupType);
                    holder.spinner_grouptype.setAdapter(spinAdapter_grouptypes);

                    if (UserInfo.getInstance(context).getCompanyType().equals("buyer")) {
                        holder.spinner_grouptype.setSelection(5);
                    }

                    if(contacts.getGroup_type()!=null) {
                        for (int i=0;i<response_buyerGroupType.length;i++) {
                            if(response_buyerGroupType[i].getId().equals(contacts.getGroup_type().get(0))){
                                holder.spinner_grouptype.setSelection(i);
                            }
                        }
                    }


                } catch (Exception e) {

                }
            }

        holder.remove_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ArrayList<MyContacts> localList = UserInfo.getInstance(context).getwishSuggestioncontacts();
                    for (int i = 0; i < localList.size(); i++) {
                        if (localList.get(i).getPhone().equals(mDataset.get(holder.getAdapterPosition()).getPhone())) {
                            localList.get(i).setIs_visible(false);
                        }
                        UserInfo.getInstance( context).setwishSuggestionContacts(new Gson().toJson(localList));
                        mDataset.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyItemRangeChanged(holder.getAdapterPosition(),getItemCount());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

            holder.buyer_invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        inviteAs = "Buyer";
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
                        InviteContactsModel inviteContactsModel = new InviteContactsModel();
                        if (((Response_BuyerGroupType) holder.spinner_grouptype.getSelectedItem()) != null && ((Response_BuyerGroupType) holder.spinner_grouptype.getSelectedItem()).getId() != null) {
                            inviteContactsModel.setGroup_type(((Response_BuyerGroupType) holder.spinner_grouptype.getSelectedItem()).getId());
                            inviteContactsModel.setRequest_type(inviteAs);
                            ArrayList<ArrayList<String>> invitees = new ArrayList<>();

                            ArrayList<String> namevalue = new ArrayList<>();
                            namevalue.add(mDataset.get(holder.getAdapterPosition()).getName());
                            namevalue.add(mDataset.get(holder.getAdapterPosition()).getPhone());
                            invitees.add(namevalue);

                            inviteContactsModel.setInvitee(invitees);

                            inviteContact(inviteContactsModel, headers, context,holder.getAdapterPosition(),mDataset);
                        } else {
                            Toast.makeText(context, "Please select group type", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            holder.supplier_invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inviteAs = "Supplier";
                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
                    InviteContactsModel inviteContactsModel = new InviteContactsModel();
                    if (((Response_BuyerGroupType) holder.spinner_grouptype.getSelectedItem()) != null && ((Response_BuyerGroupType) holder.spinner_grouptype.getSelectedItem()).getId() != null) {
                        inviteContactsModel.setGroup_type(((Response_BuyerGroupType) holder.spinner_grouptype.getSelectedItem()).getId());
                        inviteContactsModel.setRequest_type(inviteAs);
                        ArrayList<ArrayList<String>> invitees = new ArrayList<>();

                        ArrayList<String> namevalue = new ArrayList<>();
                        namevalue.add(mDataset.get(holder.getAdapterPosition()).getName());
                        namevalue.add(mDataset.get(holder.getAdapterPosition()).getPhone());
                        invitees.add(namevalue);

                        inviteContactsModel.setInvitee(invitees);

                        inviteContact(inviteContactsModel, headers, context, holder.getAdapterPosition(), mDataset);
                    } else {
                        Toast.makeText(context, "Please select group type", Toast.LENGTH_LONG).show();
                    }

                }
            });
    }

    private void inviteContact(final InviteContactsModel inviteContactsModel, HashMap<String, String> headers, final AppCompatActivity context, final int position, final List<MyContacts> mDataset) {
        HttpManager.getInstance(context).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(context, "contactsinvites", ""), new Gson().fromJson(new Gson().toJson(inviteContactsModel), JsonObject.class), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Toast.makeText(context, "Invitations sent!", Toast.LENGTH_SHORT).show();
              /*  mDataset.get(position).setConnected_as(inviteContactsModel.getRequest_type().toLowerCase());
                UserInfo.getInstance(context).setwishSuggestionContacts(new Gson().toJson(mDataset));
                notifyDataSetChanged();*/
              String phone = mDataset.get(position).getPhone();
                ArrayList<MyContacts> localList = UserInfo.getInstance(context).getwishSuggestioncontacts();
                for(int i=0;i<localList.size();i++){
                    if(localList.get(i).getPhone().equals(mDataset.get(position).getPhone())){
                        localList.get(i).setConnected_as(inviteContactsModel.getRequest_type().toLowerCase());
                    }
                }
                UserInfo.getInstance(context).setwishSuggestionContacts(new Gson().toJson(localList));
                mDataset.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,getItemCount());
                removeDuplicate(mDataset,phone);
                // progressDialog.dismiss();
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
                //  progressDialog.dismiss();
            }
        });
    }

    private void removeDuplicate(List<MyContacts> mDataset, String phone) {
        try {
            for (int i=0;i<mDataset.size();i++){
                if(mDataset.get(i).getPhone().equals(phone)) {
                    mDataset.remove(mDataset.get(i));
                    notifyItemRemoved(i);
                    notifyItemRangeChanged(i,getItemCount());
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}