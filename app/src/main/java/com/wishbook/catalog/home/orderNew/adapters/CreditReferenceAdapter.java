package com.wishbook.catalog.home.orderNew.adapters;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.TextDrawable;
import com.wishbook.catalog.Utils.util.ColorGenerator;
import com.wishbook.catalog.commonmodels.responses.ResponseCreditReference;
import com.wishbook.catalog.home.contacts.details.Fragment_SupplierDetailsNew2;

import java.util.ArrayList;

public class CreditReferenceAdapter extends RecyclerView.Adapter<CreditReferenceAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<ResponseCreditReference> responseCreditReferences;

    public CreditReferenceAdapter(Context context, ArrayList<ResponseCreditReference> responseCreditReferences) {
        this.context = context;
        this.responseCreditReferences = responseCreditReferences;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_reference_item, parent, false);
        return new CreditReferenceAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final ResponseCreditReference creditReference = responseCreditReferences.get(position);
        final TextDrawable drawable;
        if (creditReference.getSelling_company_name() != null && !creditReference.getSelling_company_name().isEmpty()) {
            drawable = TextDrawable.builder()
                    .buildRound(creditReference.getSelling_company_name().substring(0, 1), ColorGenerator.MATERIAL.getRandomColor());
            holder.textInt.setImageDrawable(drawable);
        } else {
            drawable = TextDrawable.builder()
                    .buildRound("", ColorGenerator.MATERIAL.getRandomColor());
            holder.textInt.setImageDrawable(drawable);
        }

        holder.txt_supplier_name.setText(creditReference.getSelling_company_name());
        holder.txt_supplier_comapny.setVisibility(View.GONE);
        holder.txt_contact_number.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Fragment_SupplierDetailsNew2 fragment_supplier = new Fragment_SupplierDetailsNew2();
                Application_Singleton.CONTAINER_TITLE = "Supplier Details";
                Application_Singleton.TOOLBARSTYLE = "WHITE";
                if (creditReference.getSelling_company() != null && creditReference.getRelation_id() != null) {
                    bundle.putString("sellerid", creditReference.getRelation_id());
                    bundle.putString("sellerCompanyid", creditReference.getSelling_company());
                } else if (creditReference.getSelling_company() != null) {
                    // for public details
                    bundle.putString("sellerid", creditReference.getSelling_company());
                    bundle.putBoolean("isHideAll", true);
                }
                fragment_supplier.setArguments(bundle);
                Application_Singleton.CONTAINERFRAG = fragment_supplier;
                Intent intent = new Intent(context, OpenContainer.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return responseCreditReferences.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_supplier_name, txt_supplier_comapny, txt_contact_number, txt_brokerage;
        private ImageView textInt;
        private CheckBox contact_cb;
        private LinearLayout main_container;

        public CustomViewHolder(View view) {
            super(view);
            txt_supplier_name = (TextView) view.findViewById(R.id.txt_supplier_name);
            txt_supplier_comapny = (TextView) view.findViewById(R.id.txt_supplier_comapny);
            txt_contact_number = (TextView) view.findViewById(R.id.txt_contact_number);
            textInt = (ImageView) view.findViewById(R.id.img_first_letter);
            contact_cb = (CheckBox) view.findViewById(R.id.contact_cb);
            main_container = (LinearLayout) view.findViewById(R.id.main_container);
        }
    }


}
