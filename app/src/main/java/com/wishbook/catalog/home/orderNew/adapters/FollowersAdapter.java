package com.wishbook.catalog.home.orderNew.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.TextDrawable;
import com.wishbook.catalog.Utils.util.ColorGenerator;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.commonmodels.responses.FollowUsers;
import com.wishbook.catalog.home.contacts.add.Fragment_Invite;
import com.wishbook.catalog.home.contacts.details.Fragment_BuyerDetails;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;

import java.util.ArrayList;
import java.util.Locale;


public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.CustomViewHolder> {


    Context context;
    private ArrayList<FollowUsers> items = new ArrayList<>();
    private ArrayList<FollowUsers> arraylist;

    public FollowersAdapter(Context context, ArrayList<FollowUsers> items) {
        this.context = context;
        this.items = items;
        this.arraylist = new ArrayList<FollowUsers>();
        this.arraylist.addAll(items);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.followers_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final TextDrawable drawable;
        if (items.get(position).getName() != null) {
            drawable = TextDrawable.builder()
                    .buildRound(items.get(position).getName().substring(0, 1), ColorGenerator.MATERIAL.getRandomColor());
            holder.textInt.setImageDrawable(drawable);
        } else {
            drawable = TextDrawable.builder()
                    .buildRound("", ColorGenerator.MATERIAL.getRandomColor());
            holder.textInt.setImageDrawable(drawable);
        }

        holder.txt_buyer_name.setText(items.get(position).getName());
        holder.txt_brand_name.setText(items.get(position).getFollowed_brand_names());
        if(items.get(position).getIs_invited()){
            holder.txt_contact_number.setVisibility(View.GONE);
            holder.btn_add_network.setVisibility(View.GONE);
            holder.txt_contact_number.setText(items.get(position).getPhone_number());
        } else {
            holder.txt_contact_number.setVisibility(View.GONE);
            holder.btn_add_network.setVisibility(View.VISIBLE);
        }

        holder.main_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(items.get(holder.getAdapterPosition()).getBuyer_id()!=null && !items.get(holder.getAdapterPosition()).getBuyer_id().isEmpty()) {
                    Application_Singleton.CONTAINER_TITLE = "Buyer Details";
                    Application_Singleton.TOOLBARSTYLE = "WHITE";
                    Fragment_BuyerDetails buyerapproved = new Fragment_BuyerDetails();
                    Bundle bundle = new Bundle();
                    bundle.putString("buyerid", items.get(holder.getAdapterPosition()).getBuyer_id());
                    buyerapproved.setArguments(bundle);
                    Application_Singleton.CONTAINERFRAG = buyerapproved;
                    Intent intent = new Intent(context, OpenContainer.class);
                    context.startActivity(intent);
                }
            }
        });
        holder.btn_add_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NameValues nameValues = new NameValues(items.get(holder.getAdapterPosition()).getName(), items.get(holder.getAdapterPosition()).getPhone_number());
                StaticFunctions.selectedContacts.add(nameValues);
                // StaticFunctions.selectedContacts = contactAdapter.getAllSelectedContacts();
                String typeBuyerSupplier="";
                if (StaticFunctions.selectedContacts.size() > 0) {
                    typeBuyerSupplier="buyer";
                    Fragment_Invite invite = new Fragment_Invite();
                    Bundle bundle = new Bundle();
                    bundle.putString("type",typeBuyerSupplier);
                    invite.setArguments(bundle);
                    invite.setListener(new Fragment_Invite.SuccessListener() {
                        @Override
                        public void OnSuccess() {
                            StaticFunctions.selectedContacts.clear();
                            holder.btn_add_network.setVisibility(View.GONE);
                        }

                        @Override
                        public void OnCancel() {

                        }
                    });
                    invite.show(((Activity_BuyerSearch)context).getSupportFragmentManager(), "invite");
                } else {
                    Toast.makeText(context, "Please select atleast one contact", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        items.clear();
        if (charText.length() == 0) {
            items.addAll(arraylist);
        } else {
            for (FollowUsers wp : arraylist) {
                if(wp.getName()!=null){
                    if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        items.add(wp);
                    }
                }
            }

        }
        notifyDataSetChanged();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_buyer_name, txt_brand_name, txt_contact_number,txt_brokerage;
        private ImageView textInt;
        private CheckBox contact_cb;
        private LinearLayout linear_brokerage;
        private LinearLayout main_container;
        private AppCompatButton btn_add_network;

        public CustomViewHolder(View view) {
            super(view);
            txt_buyer_name = (TextView) view.findViewById(R.id.txt_buyer_name);
            txt_brand_name = (TextView) view.findViewById(R.id.txt_brand_name);
            txt_contact_number = (TextView) view.findViewById(R.id.txt_contact_number);
            textInt = (ImageView) view.findViewById(R.id.textInt);
            contact_cb = (CheckBox) view.findViewById(R.id.contact_cb);
            main_container = (LinearLayout) view.findViewById(R.id.main_container);
            linear_brokerage = (LinearLayout) view.findViewById(R.id.linear_brokerage);
            txt_brokerage = (TextView) view.findViewById(R.id.txt_brokerage);
            btn_add_network = (AppCompatButton) view.findViewById(R.id.btn_add_network);

        }
    }
}
