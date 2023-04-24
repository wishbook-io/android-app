package com.wishbook.catalog.home.orderNew.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.responses.Rrc_items;
import com.wishbook.catalog.commonmodels.responses.Rrces;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RRCAdapter extends RecyclerView.Adapter<RRCAdapter.RRCViewHolder> {

    private Context context;
    private ArrayList<Rrces> rrcesArrayList;

    public RRCAdapter(Context context, ArrayList<Rrces> rrcesArrayList) {
        this.context = context;
        this.rrcesArrayList = rrcesArrayList;
    }

    @NonNull
    @Override
    public RRCViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.purchase_rrc_item, viewGroup, false);
        return new RRCAdapter.RRCViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RRCViewHolder rrcViewHolder, int position) {
        Rrces rrces = rrcesArrayList.get(position);
        StringBuffer rrc_title = new StringBuffer();
        rrc_title.append(StringUtils.capitalize(rrces.getRequest_type()));
        if (rrces.getInvoice_id() != null && !rrces.getInvoice_id().isEmpty())
            rrc_title.append(" invoice #" + rrces.getInvoice_id());

        rrcViewHolder.txt_rrc_title.setText(rrc_title);

        if(rrces.getRefund()!=null) {
            rrcViewHolder.relative_refund.setVisibility(View.VISIBLE);
            StringBuffer refund_value = new StringBuffer();
            refund_value.append("Refunded: "+rrces.getRefund().getRefund_date().getInitiated_date());

            refund_value.append("\n"+"Refund Mode: "+rrces.getRefund().getRefund_mode());
            if(rrces.getRefund().getTransaction_id()!=null)
                refund_value.append("\n"+"Transaction ID: "+rrces.getRefund().getTransaction_id());
            if(rrces.getRefund().getRefund_status()!=null)
                refund_value.append("\n Refund Status: "+rrces.getRefund().getRefund_status());
            rrcViewHolder.txt_refund_value.setText(refund_value);

            rrcViewHolder.relative_breakup.setVisibility(View.VISIBLE);
            StringBuffer breakup_value = new StringBuffer();
            breakup_value.append("Payment Gateway: "+"\u20B9 "+rrces.getRefund().getOther_refunded()+"\n");
            breakup_value.append("WB Rewards: "+"\u20B9 "+rrces.getRefund().getReward_refunded()+"\n");
            breakup_value.append("WB Money: "+"\u20B9 "+rrces.getRefund().getWb_refunded());

            rrcViewHolder.txt_breakup_value.setText(breakup_value);
        } else {
            rrcViewHolder.relative_refund.setVisibility(View.GONE);
            rrcViewHolder.relative_breakup.setVisibility(View.GONE);
        }

        if (rrces.getRequest_status().equalsIgnoreCase("rejected") && rrces.getReject_reason() != null) {
            rrcViewHolder.linear_rejected_reason.setVisibility(View.VISIBLE);
            rrcViewHolder.txt_rejected_reason_value.setText(rrces.getReject_reason());
        } else {
            rrcViewHolder.linear_rejected_reason.setVisibility(View.GONE);
        }
        addItemList(rrces.getRrc_items(),rrcViewHolder.linear_rrc_items);

    }

    private void addItemList(ArrayList<Rrc_items> rrc_items, LinearLayout root) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root.removeAllViews();
        for (int i = 0; i < rrc_items.size(); i++) {
            View v = vi.inflate(R.layout.catalog_row, null);
            TextView itemName = (TextView) v.findViewById(R.id.invoice_prod_name);
            TextView itemValue = (TextView) v.findViewById(R.id.invoice_prod_value);

            RelativeLayout relative_shipping = v.findViewById(R.id.relative_pkg_type);

            TextView shipping_label = v.findViewById(R.id.txt_label_pkg_type);
            TextView shipping_value = v.findViewById(R.id.txt_pkg_type);


            itemName.setText("" + (i + 1) + "." + rrc_items.get(i).getCatalog_title() + "("+(rrc_items.get(i).getQty()) +"pcs.)");
            itemValue.setText("\u20B9 " + rrc_items.get(i).getRate());


            relative_shipping.setVisibility(View.GONE);
         /*   if (rrc_items.get(i).getShipping() != null && !rrc_items.get(i).getShipping().isEmpty() && Double.parseDouble(rrc_items.get(i).getShipping()) > 0 ) {
                relative_shipping.setVisibility(View.VISIBLE);
                shipping_label.setText("Shipping");
                shipping_value.setText("\u20B9 " + rrc_items.get(i).getShipping());
            } else {
                relative_shipping.setVisibility(View.GONE);
            }*/

            root.addView(v);
        }

    }

    @Override
    public int getItemCount() {
        return rrcesArrayList.size();
    }

    public class RRCViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_rrc_title)
        TextView txt_rrc_title;

        @BindView(R.id.relative_refund)
        RelativeLayout relative_refund;

        @BindView(R.id.txt_refund_value)
        TextView txt_refund_value;

        @BindView(R.id.linear_rrc_items)
        LinearLayout linear_rrc_items;

        @BindView(R.id.relative_breakup)
        RelativeLayout relative_breakup;

        @BindView(R.id.txt_breakup_value)
        TextView txt_breakup_value;

        @BindView(R.id.linear_rejected_reason)
        LinearLayout linear_rejected_reason;

        @BindView(R.id.txt_rejected_reason_value)
        TextView txt_rejected_reason_value;


        public RRCViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
