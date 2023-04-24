package com.wishbook.catalog.home.more.myviewers_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.ResponseCatalogViewers;
import com.wishbook.catalog.home.contacts.add.Fragment_Invite;
import com.wishbook.catalog.home.contacts.details.Fragment_BuyerDetails;
import com.wishbook.catalog.home.contacts.details.Fragment_SupplierDetailsNew2;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewersAdapter extends RecyclerView.Adapter<ViewersAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<ResponseCatalogViewers> viewers;

    public ViewersAdapter(Context context, ArrayList<ResponseCatalogViewers> viewers) {
        this.context = context;
        this.viewers = viewers;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewers_item, parent, false);
        return new ViewersAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        if (position == 0) {
            holder.divider_bottom.setVisibility(View.VISIBLE);
        }
        final ResponseCatalogViewers viewer = viewers.get(position);
        if(viewer.getCompany_name()!=null && viewer.getCompany_name().length() > 1){
            holder.buyer_name.setText(viewer.getCompany_name().substring(0, 1));
            holder.txt_buyer_company.setText(viewer.getCompany_name());
        }

        holder.btn_active.setVisibility(View.GONE);
        holder.btn_inactive.setVisibility(View.VISIBLE);
        holder.txt_location.setVisibility(View.VISIBLE);
        holder.txt_location.setText(viewer.getCity_name() + ", " + viewer.getState_name());
        holder.txt_time.setText(getCategory(DateUtils.getTimeAgo(viewer.created_at, context)));

        if (viewer.getRelationship_id() != null) {
            holder.btn_active.setVisibility(View.GONE);
            holder.btn_inactive.setVisibility(View.VISIBLE);
        } else {

            if (viewer.getCompany_name().equals("Guest User")) {
                holder.btn_active.setVisibility(View.GONE);
                holder.txt_location.setVisibility(View.GONE);
            } else {
                holder.btn_active.setVisibility(View.VISIBLE);
            }

            holder.btn_inactive.setVisibility(View.GONE);
        }


        holder.btn_active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddBuyerDialog(viewer.getCompany_name(), viewer.getPhone_number(), holder);
                WishbookEvent wishbookEvent = new WishbookEvent();
                wishbookEvent.setEvent_category(WishbookEvent.SELLER_EVENTS_CATEGORY);
                wishbookEvent.setEvent_names("MyViewer_AddAsBuyer");
                HashMap<String,String> prop = new HashMap<>();
                prop.put("company_id",UserInfo.getInstance(context).getCompany_id());
                wishbookEvent.setEvent_properties(prop);
                new WishbookTracker(context,wishbookEvent);
                Application_Singleton.trackEvent("MyViewers","Add" , "Add Buyer");
            }
        });

        holder.setClickListener(new ViewersAdapter.ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (viewer.getCompany_id() != null) {
                    if (viewer.getConnected_as().toLowerCase().equals("buyer")) {
                        Bundle bundle = new Bundle();
                        Application_Singleton.CONTAINER_TITLE = "Buyer Details";
                        Fragment_BuyerDetails buyerapproved = new Fragment_BuyerDetails();
                        Application_Singleton.TOOLBARSTYLE = "WHITE";
                        bundle.putString("buyerid", viewer.getRelationship_id());
                        buyerapproved.setArguments(bundle);
                        Application_Singleton.CONTAINERFRAG = buyerapproved;
                        StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                    } else if (viewer.getConnected_as().toLowerCase().equals("supplier")) {
                        Bundle bundle = new Bundle();
                        if (viewer.getRelationship_id() != null && viewer.getCompany_id() != null) {
                            bundle.putString("sellerid", viewer.getRelationship_id());
                            bundle.putString("sellerCompanyid", viewer.getCompany_id());
                            Application_Singleton.CONTAINER_TITLE = "Supplier Details";
                            Application_Singleton.TOOLBARSTYLE = "WHITE";
                            Fragment_SupplierDetailsNew2 supplier = new Fragment_SupplierDetailsNew2();
                            supplier.setArguments(bundle);
                            Application_Singleton.CONTAINERFRAG = supplier;
                            Intent intent = new Intent(context, OpenContainer.class);
                            context.startActivity(intent);
                        } else if (viewer.getCompany_id() != null) {
                            // for public details
                            bundle.putString("sellerid", viewer.getCompany_id());
                            bundle.putBoolean("isHideAll", true);
                            Application_Singleton.CONTAINER_TITLE = "Supplier Details";
                            Application_Singleton.TOOLBARSTYLE = "WHITE";
                            Fragment_SupplierDetailsNew2 supplier = new Fragment_SupplierDetailsNew2();
                            supplier.setArguments(bundle);
                            Application_Singleton.CONTAINERFRAG = supplier;
                            Intent intent = new Intent(context, OpenContainer.class);
                            context.startActivity(intent);
                        }
                    }

                }
            }
        });
        if (viewer.getIs_manufacturer() != null && viewer.getIs_manufacturer().equals("true")) {
            holder.btn_active.setVisibility(View.GONE);
            holder.btn_inactive.setVisibility(View.INVISIBLE);
        }

        if(UserInfo.getInstance(context).getCompany_type().equals("manufacturer")){
            holder.btn_active.setVisibility(View.GONE);
            holder.btn_inactive.setVisibility(View.INVISIBLE);
        }

    }

    private void showAddBuyerDialog(String contactname, String contactNumber, final CustomViewHolder holder) {


        StaticFunctions.selectedContacts = new ArrayList<>();
        StaticFunctions.selectedContacts.add(new NameValues(contactname, contactNumber));

        Fragment_Invite invite = new Fragment_Invite();
        Bundle bundle = new Bundle();
        bundle.putString("type", "buyer");
        invite.setArguments(bundle);
        invite.setListener(new Fragment_Invite.SuccessListener() {
            @Override
            public void OnSuccess() {
                StaticFunctions.selectedContacts.clear();
                holder.btn_active.setVisibility(View.GONE);
                holder.btn_inactive.setVisibility(View.VISIBLE);
            }

            @Override
            public void OnCancel() {

            }
        });
        invite.show(((AppCompatActivity) context).getSupportFragmentManager(), "invite");

    }

    @Override
    public int getItemCount() {
        return viewers.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

    public String getCategory(String value) {
        try {
            if (value.contains("Yesterday")) {
                return "Yesterday";
            }
            if (value.contains("days")) {
                String numberOnly = value.replaceAll("[^0-9]", "");
                try {
                    int days = Integer.parseInt(numberOnly);
                    if (days <= 7) {
                        return "Last Week";
                    } else if (days <= 30) {
                        return "Last Month";
                    } else {
                        return "Last Year";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.textInt)
        ImageView textInt;

        @BindView(R.id.txt_location)
        TextView txt_location;

        @BindView(R.id.txt_buyer_company)
        TextView txt_buyer_company;

        @BindView(R.id.btn_active)
        AppCompatButton btn_active;

        @BindView(R.id.btn_inactive)
        AppCompatButton btn_inactive;

        @BindView(R.id.txt_time)
        TextView txt_time;

        @BindView(R.id.buyer_name)
        TextView buyer_name;

        @BindView(R.id.divider_bottom)
        View divider_bottom;

        private ViewersAdapter.ItemClickListener clickListener;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(ViewersAdapter.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }


}
