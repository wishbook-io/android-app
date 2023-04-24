package com.wishbook.catalog.commonadapters;


import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.TextDrawable;
import com.wishbook.catalog.Utils.util.ColorGenerator;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;
import com.wishbook.catalog.home.contacts.details.ActivityBuyerGroupList;
import com.wishbook.catalog.home.contacts.details.Fragment_BuyerDetails;

import java.util.ArrayList;
import java.util.Locale;

public class BuyerGroupListAdapter extends RecyclerView.Adapter<BuyerGroupListAdapter.CustomViewHolder> {


    private ArrayList<Response_Buyer> items = new ArrayList<>();
    private ArrayList<Response_Buyer> itemsAll;
    private ArrayList<Response_Buyer> arraylist;
    private Context context;
    private boolean isCustomGroup;
    private String segmentationTile;
    private String segmentationId;


    public BuyerGroupListAdapter(Context context, ArrayList<Response_Buyer> items,boolean isCustomGroup,String segmentationId, String segmentationTile) {
        this.context = context;
        this.items = items;
        this.arraylist = new ArrayList<Response_Buyer>();
        this.arraylist.addAll(items);
        this.isCustomGroup = isCustomGroup;
        this.segmentationId = segmentationId;
        this.segmentationTile = segmentationTile;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyer_item, parent, false);
        return new BuyerGroupListAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final TextDrawable drawable;
        if (items.get(position).getBuying_person_name() != null && !items.get(position).getBuying_person_name().isEmpty()) {
            drawable = TextDrawable.builder()
                    .buildRound(items.get(position).getBuying_person_name().substring(0, 1), ColorGenerator.MATERIAL.getRandomColor());
            holder.textInt.setImageDrawable(drawable);
        } else {
            if (items.get(position).getBuying_company_name() != null && !items.get(position).getBuying_company_name().isEmpty()) {
                drawable = TextDrawable.builder()
                        .buildRound(items.get(position).getBuying_company_name().substring(0, 1), ColorGenerator.MATERIAL.getRandomColor());
                holder.textInt.setImageDrawable(drawable);
            } else {
                drawable = TextDrawable.builder()
                        .buildRound("", ColorGenerator.MATERIAL.getRandomColor());
                holder.textInt.setImageDrawable(drawable);
            }

        }

        holder.txt_buyer_name.setText(items.get(position).getBuying_person_name());
        holder.txt_buyer_comapny.setText(items.get(position).getBuying_company_name());

        if(isCustomGroup){
            holder.img_delete.setVisibility(View.VISIBLE);
        } else {
            holder.img_delete.setVisibility(View.GONE);
        }

        // holder.textInt.setImageDrawable(drawable);
        final ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(context, R.animator.flipping);
        anim.setTarget(holder.textInt);
        anim.setDuration(300);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Application_Singleton.CONTAINER_TITLE = "Buyer Details";
                Fragment_BuyerDetails buyerapproved = new Fragment_BuyerDetails();
                Application_Singleton.TOOLBARSTYLE = "WHITE";
                bundle.putString("buyerid", items.get(holder.getAdapterPosition()).getId());
                buyerapproved.setArguments(bundle);
                Application_Singleton.CONTAINERFRAG = buyerapproved;
                StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
            }
        });

        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Application_Singleton.canUseCurrentAcitivity()){
                    new MaterialDialog.Builder(context)
                            .content("Are you sure you want to remove this buyer ?")
                            .negativeText("No")
                            .positiveText("Yes")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    items.remove(holder.getAdapterPosition());
                                    notifyDataSetChanged();
                                    ((ActivityBuyerGroupList) context).deleteBuyers(segmentationId,items,segmentationTile);
                                }
                            })
                            .show();
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
            for (Response_Buyer wp : arraylist) {
                String buying_person = wp.getBuying_person_name() != null ? wp.getBuying_person_name() : "";
                String company = wp.getBuying_company_name() != null ? wp.getBuying_company_name() : "";
                if (buying_person.toLowerCase(Locale.getDefault()).contains(charText)) {
                    items.add(wp);
                } else if (company.toLowerCase((Locale.getDefault())).contains(charText)) {
                    items.add(wp);
                }
            }

        }
        notifyDataSetChanged();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_buyer_name, txt_buyer_comapny, txt_contact_number, txt_brokerage;
        private ImageView textInt;
        private CheckBox contact_cb;
        private LinearLayout linear_brokerage;
        private LinearLayout main_container;
        private ImageView img_delete;

        public CustomViewHolder(View view) {
            super(view);
            txt_buyer_name = (TextView) view.findViewById(R.id.txt_buyer_name);
            txt_buyer_comapny = (TextView) view.findViewById(R.id.txt_buyer_comapny);
            txt_contact_number = (TextView) view.findViewById(R.id.txt_contact_number);
            textInt = (ImageView) view.findViewById(R.id.textInt);
            contact_cb = (CheckBox) view.findViewById(R.id.contact_cb);
            main_container = (LinearLayout) view.findViewById(R.id.main_container);
            linear_brokerage = (LinearLayout) view.findViewById(R.id.linear_brokerage);
            txt_brokerage = (TextView) view.findViewById(R.id.txt_brokerage);
            img_delete = view.findViewById(R.id.img_delete);

        }
    }


}
