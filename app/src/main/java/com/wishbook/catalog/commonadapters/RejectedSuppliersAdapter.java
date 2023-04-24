package com.wishbook.catalog.commonadapters;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_Suppliers;
import com.wishbook.catalog.home.contacts.details.ActionLogApi;
import com.wishbook.catalog.home.contacts.details.Fragment_SupplierDetailsNew2;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by nani on 17-04-2016.
 */
public class RejectedSuppliersAdapter extends RecyclerView.Adapter<RejectedSuppliersAdapter.MyViewHolder> {

    private AppCompatActivity mActivity;
    private Response_Suppliers[] mRejectedSuppliersList;

    public RejectedSuppliersAdapter(AppCompatActivity mActivity, Response_Suppliers[] mRejectedSuppliersList) {
        this.mActivity = mActivity;
        this.mRejectedSuppliersList = mRejectedSuppliersList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout itemcontainer;
        public TextView sellerName, sellerNumber, sellerType;
        AppCompatButton chat_user,view_catalog, btn_call;

        public MyViewHolder(View view) {
            super(view);
            sellerName = (TextView) view.findViewById(R.id.seller_name);
            sellerNumber = (TextView) view.findViewById(R.id.seller_number);
            sellerType = (TextView) view.findViewById(R.id.buyer_type);
            itemcontainer = (LinearLayout) view.findViewById(R.id.itemcontainer);
            chat_user = (AppCompatButton) view.findViewById(R.id.chat_user);
            btn_call = view.findViewById(R.id.btn_call);
            view_catalog = (AppCompatButton) view.findViewById(R.id.view_catalog);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.approved_suppliers_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Response_Suppliers bg = mRejectedSuppliersList[position];
        holder.view_catalog.setVisibility(View.INVISIBLE);
        holder.chat_user.setVisibility(View.INVISIBLE);
        if (bg.getSelling_company() != null) {
            final String companyName = bg.getSelling_company_name();
            final String phoneNumber = bg.getSelling_company_phone_number();
            //final String emailID = bg.getSelling_company().getEmail();

            if (bg.getSupplier_person_name() != null) {
                holder.sellerName.setVisibility(View.VISIBLE);
                holder.sellerName.setText(StringUtils.capitalize(bg.getSupplier_person_name().toLowerCase().trim()));
            } else {
                if (bg.getSelling_company_name() != null) {
                    holder.sellerName.setVisibility(View.VISIBLE);
                    holder.sellerName.setText(StringUtils.capitalize(companyName.toLowerCase().trim()));
                } else {
                    holder.sellerName.setVisibility(View.GONE);
                }
            }
            holder.sellerNumber.setText(StringUtils.capitalize(phoneNumber.toLowerCase().trim()));

            if(bg.getBuyer_type().equals("Enquiry")){
                holder.sellerType.setText("Enquiry " + StringUtils.capitalize(bg.getStatus()));
            }else{
                holder.sellerType.setText(StringUtils.capitalize(bg.getStatus()));
            }

            //holder.sellerEmailTv.setText(emailID);

            holder.itemcontainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle =new Bundle();
                    bundle.putString("sellerid",mRejectedSuppliersList[position].getId());
                    Fragment_SupplierDetailsNew2 supplier=new Fragment_SupplierDetailsNew2();
                    supplier.setArguments(bundle);
                    Application_Singleton.CONTAINER_TITLE="Supplier Details";
                    Application_Singleton.CONTAINERFRAG=supplier;
                    //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                    StaticFunctions.switchActivity(mActivity, OpenContainer.class);

                }
            });

            holder.btn_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(bg.getSelling_company_phone_number()!=null){
                        new ActionLogApi(mActivity, ActionLogApi.RELATION_TYPE_SUPPLIER, ActionLogApi.ACTION_TYPE_CALL, String.valueOf(bg.getSelling_company()));
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + bg.getSelling_company_phone_number()));
                        mActivity.startActivity(intent);
                    } else {
                        Toast.makeText(mActivity,"contact number is not exist",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {

            holder.sellerName.setText("N/A");
            holder.sellerNumber.setText("N/A");
            holder.sellerType.setText("N/A");
           // holder.sellerEmailTv.setText("N/A");
        }
    }

    @Override
    public int getItemCount() {
        return mRejectedSuppliersList.length;
    }
}

