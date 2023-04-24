package com.wishbook.catalog.commonadapters;

import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.CommonSummaryList;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.home.contacts.Fragment_ContactsHolder;
import com.wishbook.catalog.home.contacts.add.Fragment_AddBuyer;
import com.wishbook.catalog.home.contacts.add.Fragment_AddSupplier;
import com.wishbook.catalog.home.orders.ActivityOrderHolder;
import com.wishbook.catalog.home.orders.add.Fragment_CreateOrder;
import com.wishbook.catalog.home.orders.add.Fragment_CreatePurchaseOrder;

import java.util.ArrayList;

/**
 * Created by root on 15/4/17.
 */
public class CommonSummaryAdapter extends RecyclerView.Adapter<CommonSummaryAdapter.MyViewHolder> {
    private AppCompatActivity context;
    private ArrayList<CommonSummaryList> dataSet = new ArrayList<>();

    @Override
    public CommonSummaryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_summary_item, parent, false);
        return new MyViewHolder(view);
    }

    public CommonSummaryAdapter(AppCompatActivity context, ArrayList<CommonSummaryList> dataSet) {
        this.dataSet = dataSet;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(final CommonSummaryAdapter.MyViewHolder holder, int position) {

        if(dataSet.get(position).getHeader().equals("Sales Orders") || dataSet.get(position).getHeader().equals("Purchase Orders")) {
            holder.add.setVisibility(View.INVISIBLE);
        } else {
            holder.add.setVisibility(View.VISIBLE);
        }
        //holder.headerTextView.setText(dataSet.get(position).getHeader());
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (dataSet.get(holder.getAdapterPosition()).getHeader()) {
                    case "Buyers":
                        Application_Singleton.trackEvent("Home","BuyersDashboard","Add Buyers");
                        Fragment_AddBuyer addBuyerFragment = new Fragment_AddBuyer();
                        addBuyerFragment.show(context.getSupportFragmentManager(), "addbuyer");
                        break;
                    case "Suppliers":
                        Application_Singleton.trackEvent("Home","SupplierDashboard","Add Supplier");
                        Fragment_AddSupplier fragment_addSupplier = new Fragment_AddSupplier();
                        fragment_addSupplier.show(context.getSupportFragmentManager(), "addsupplier");
                        break;
                    case "Sales Orders":
                        Application_Singleton.CONTAINER_TITLE = context.getResources().getString(R.string.new_sales_order);
                        Fragment_CreateOrder sales = new Fragment_CreateOrder();
                        Application_Singleton.CONTAINERFRAG = sales;
                        Intent intent = new Intent(context, OpenContainer.class);
                        context.startActivity(intent);
                        break;
                    case "Purchase Orders":
                        Application_Singleton.CONTAINER_TITLE = context.getResources().getString(R.string.new_purchase_order);
                        Fragment_CreatePurchaseOrder purchase = new Fragment_CreatePurchaseOrder();
                        Application_Singleton.CONTAINERFRAG = purchase;
                        Intent intent1 = new Intent(context, OpenContainer.class);
                        context.startActivity(intent1);
                        break;
                }
            }
        });

       /* holder.main_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    switch (dataSet.get(position).getHeader()) {
                        case "Buyers":
                            Activity_Home.tabs.getTabAt(2).select();
                            Application_Singleton.selectedInnerTabContacts = 1;
                            break;
                        case "Suppliers":
                            Activity_Home.tabs.getTabAt(2).select();
                            Application_Singleton.selectedInnerTabContacts = 2;
                            break;
                        case "Sales Orders":
                            Activity_Home.tabs.getTabAt(3).select();
                            Application_Singleton.selectedInnerTabOrders = 1;
                            break;
                        case "Purchase Orders":
                            Activity_Home.tabs.getTabAt(3).select();
                            Application_Singleton.selectedInnerTabOrders = 0;
                            break;
                    }
                }catch (Exception e){

                }
            }
        });*/

        //enquiry buyer,supplier or pending orders
        holder.text1_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    switch (dataSet.get(holder.getAdapterPosition()).getHeader()) {
                        case "Buyers":
                            Application_Singleton.trackEvent("Home","BuyersDashboard","Enquiries");
                            Application_Singleton.CONTAINER_TITLE = "My Network";
                            Application_Singleton.CONTAINERFRAG = new Fragment_ContactsHolder();
                            StaticFunctions.switchActivity(context, OpenContainer.class);
                            /*Activity_Home.tabs.getTabAt(2).select();
                            Activity_Home.bottomBar.selectTabAtPosition(2);*/
                            UserInfo.getInstance(context).settabselected(0);
                            UserInfo.getInstance(context).selastsubtabselected(0);
                            break;
                        case "Suppliers":
                            Application_Singleton.trackEvent("Home","SuppliersDashboard","Enquiries");
                            Application_Singleton.CONTAINER_TITLE = "My Network";
                            Application_Singleton.CONTAINERFRAG = new Fragment_ContactsHolder();
                            StaticFunctions.switchActivity(context, OpenContainer.class);

                           /* Activity_Home.tabs.getTabAt(2).select();
                            Activity_Home.bottomBar.selectTabAtPosition(2);*/

                            UserInfo.getInstance(context).settabselected(0);
                            UserInfo.getInstance(context).selastsubtabselected(1);
                            break;
                        case "Sales Orders":
                            Application_Singleton.trackEvent("Home","SalesOrderDashboard","Pending");
                            context.startActivity(new Intent(context, ActivityOrderHolder.class).putExtra("type", "sale").putExtra("position", 1));
                           /* Activity_Home.tabs.getTabAt(3).select();
                            Activity_Home.bottomBar.selectTabAtPosition(3);
                            Application_Singleton.selectedInnerTabOrders = 1;
                            Application_Singleton.selectedInnerSubTabOrders = 0;*/
                            break;
                        case "Purchase Orders":
                            Application_Singleton.trackEvent("Home","PurchaseOrderDashboard","Pending");
                            context.startActivity(new Intent(context, ActivityOrderHolder.class).putExtra("type", "purchase").putExtra("position", 1));
                            /*Activity_Home.tabs.getTabAt(3).select();
                            Activity_Home.bottomBar.selectTabAtPosition(3);
                            Application_Singleton.selectedInnerTabOrders = 0;
                            Application_Singleton.selectedInnerSubTabOrders = 0;*/
                            break;
                    }
                } catch (Exception e) {

                }
            }
        });

        //pending buyer,supplier or dispatched orders
        holder.text2_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    switch (dataSet.get(holder.getAdapterPosition()).getHeader()) {
                        case "Buyers":
                           /* Activity_Home.tabs.getTabAt(2).select();
                            Activity_Home.bottomBar.selectTabAtPosition(2);*/
                            Application_Singleton.trackEvent("Home","BuyersDashboard","Pending");
                            Application_Singleton.CONTAINER_TITLE = "My Network";
                            Application_Singleton.CONTAINERFRAG = new Fragment_ContactsHolder();
                            StaticFunctions.switchActivity(context, OpenContainer.class);

                            UserInfo.getInstance(context).settabselected(1);
                            UserInfo.getInstance(context).selastsubtabselected(0);
                            break;
                        case "Suppliers":
                            Application_Singleton.trackEvent("Home","SuppliersDashboard","Pending");
                            Application_Singleton.CONTAINER_TITLE = "My Network";
                            Application_Singleton.CONTAINERFRAG = new Fragment_ContactsHolder();
                            StaticFunctions.switchActivity(context, OpenContainer.class);

                          /*  Activity_Home.tabs.getTabAt(2).select();
                            Activity_Home.bottomBar.selectTabAtPosition(2);*/
                            UserInfo.getInstance(context).settabselected(2);
                            UserInfo.getInstance(context).selastsubtabselected(0);
                            break;
                        case "Sales Orders":
                            Application_Singleton.trackEvent("Home","SalesOrderDashboard","Dispatched");
                            context.startActivity(new Intent(context, ActivityOrderHolder.class).putExtra("type", "sale").putExtra("position", 2));
                         /*   Activity_Home.tabs.getTabAt(3).select();
                            Activity_Home.bottomBar.selectTabAtPosition(3);
                            Application_Singleton.selectedInnerTabOrders = 1;
                            Application_Singleton.selectedInnerSubTabOrders = 1;*/
                            break;
                        case "Purchase Orders":
                            Application_Singleton.trackEvent("Home","PurchaseOrderDashboard","Dispatched");
                            context.startActivity(new Intent(context, ActivityOrderHolder.class).putExtra("type", "purchase").putExtra("position", 2));
                            /*Activity_Home.tabs.getTabAt(3).select();
                            Activity_Home.bottomBar.selectTabAtPosition(3);
                            Application_Singleton.selectedInnerTabOrders = 0;
                            Application_Singleton.selectedInnerSubTabOrders = 1;*/
                            break;
                    }
                } catch (Exception e) {

                }
            }
        });

        //approved buyer,supplier or cancelled orders
        holder.text3_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    switch (dataSet.get(holder.getAdapterPosition()).getHeader()) {
                        case "Buyers":
                            Application_Singleton.trackEvent("Home","BuyersDashboard","Approved");
                            Application_Singleton.CONTAINER_TITLE = "My Network";
                            Application_Singleton.CONTAINERFRAG = new Fragment_ContactsHolder();
                            StaticFunctions.switchActivity(context, OpenContainer.class);
                          /*  Activity_Home.tabs.getTabAt(2).select();
                            Activity_Home.bottomBar.selectTabAtPosition(2);*/
                            UserInfo.getInstance(context).settabselected(1);
                            UserInfo.getInstance(context).selastsubtabselected(2);
                            break;
                        case "Suppliers":
                            Application_Singleton.trackEvent("Home","SuppliersDashboard","Approved");
                            Application_Singleton.CONTAINER_TITLE = "My Network";
                            Application_Singleton.CONTAINERFRAG = new Fragment_ContactsHolder();
                            StaticFunctions.switchActivity(context, OpenContainer.class);
                           /* Activity_Home.tabs.getTabAt(2).select();
                            Activity_Home.bottomBar.selectTabAtPosition(2);*/
                            UserInfo.getInstance(context).settabselected(2);
                            UserInfo.getInstance(context).selastsubtabselected(2);
                            break;
                        case "Sales Orders":
                            Application_Singleton.trackEvent("Home","SalesOrderDashboard","Cancelled");
                            context.startActivity(new Intent(context, ActivityOrderHolder.class).putExtra("type", "sale").putExtra("position", 3));
                         /*   Activity_Home.tabs.getTabAt(3).select();
                            Activity_Home.bottomBar.selectTabAtPosition(3);
                            Application_Singleton.selectedInnerTabOrders = 1;
                            Application_Singleton.selectedInnerSubTabOrders = 2;*/
                            break;
                        case "Purchase Orders":
                            Application_Singleton.trackEvent("Home","PurchaseOrderDashboard","Cancelled");
                            context.startActivity(new Intent(context, ActivityOrderHolder.class).putExtra("type", "purchase").putExtra("position", 3));
                            /*Activity_Home.tabs.getTabAt(3).select();
                            Activity_Home.bottomBar.selectTabAtPosition(3);
                            Application_Singleton.selectedInnerTabOrders = 0;
                            Application_Singleton.selectedInnerSubTabOrders = 2;*/
                            break;
                    }
                } catch (Exception e) {

                }
            }
        });


        switch (dataSet.get(position).getHeader()) {
            case "Buyers":
                holder.text2.setBackground(context.getResources().getDrawable(R.drawable.dashboard_round_red));
                holder.text3.setBackground(context.getResources().getDrawable(R.drawable.dashboard_round_green));
                holder.text1.setText(""+dataSet.get(position).getEnquiries());
                holder.text2.setText(""+dataSet.get(position).getPendingRequest());
                holder.text3.setText(""+dataSet.get(position).getBuyerSupplier());

               // holder.text2.setTextColor(ContextCompat.getColor(context, R.color.pending));
                //holder.text3.setTextColor(ContextCompat.getColor(context, R.color.approved));

                holder.footer.setText("Add");
                holder.footer1.setText("Enquiries");
                holder.footer2.setText("Pending");
                holder.footer3.setText("Approved");
                holder.add_image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_home_contact));
                break;
            case "Suppliers":
                holder.text2.setBackground(context.getResources().getDrawable(R.drawable.dashboard_round_red));
                holder.text3.setBackground(context.getResources().getDrawable(R.drawable.dashboard_round_green));
                holder.text1.setText(""+dataSet.get(position).getEnquiries());
                holder.text2.setText(""+dataSet.get(position).getPendingRequest());
                holder.text3.setText(""+dataSet.get(position).getBuyerSupplier());



                //holder.text2.setTextColor(ContextCompat.getColor(context, R.color.pending));
               // holder.text3.setTextColor(ContextCompat.getColor(context, R.color.approved));

                holder.footer.setText("Add");
                holder.footer1.setText("Enquiries");
                holder.footer2.setText("Pending");
                holder.footer3.setText("Approved");
                holder.add_image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_home_contact));
                break;
            default:
                holder.text1.setText(dataSet.get(position).getOrdersPending());
                holder.text2.setText(dataSet.get(position).getOrdersDispatched());
                holder.text3.setText(dataSet.get(position).getOrdersCancelled());
                holder.footer.setText("Create");
                holder.footer1.setText("Pending");
                holder.footer2.setText("Dispatched");
                holder.footer3.setText("Cancelled");
                holder.add_image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add_shopping_cart_24dp));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView headerTextView;
        private TextView text1;
        private TextView text2;
        private TextView text3;
        private LinearLayout text1_container;
        private LinearLayout text2_container;
        private LinearLayout text3_container;
        private TextView footer1;
        private TextView footer2;
        private TextView footer3;
        private TextView footer;
        private LinearLayout add;
        private ImageView add_image;
        private RelativeLayout main_container;


        public MyViewHolder(View itemView) {
            super(itemView);
            //  headerTextView = (TextView) itemView.findViewById(R.id.header);
            text1 = (TextView) itemView.findViewById(R.id.text1);
            text2 = (TextView) itemView.findViewById(R.id.text2);
            text3 = (TextView) itemView.findViewById(R.id.text3);
            footer = (TextView) itemView.findViewById(R.id.footer);
            footer1 = (TextView) itemView.findViewById(R.id.footer1);
            footer2 = (TextView) itemView.findViewById(R.id.footer2);
            footer3 = (TextView) itemView.findViewById(R.id.footer3);
            add = (LinearLayout) itemView.findViewById(R.id.add);
            text1_container = (LinearLayout) itemView.findViewById(R.id.text1_container);
            text2_container = (LinearLayout) itemView.findViewById(R.id.text2_container);
            text3_container = (LinearLayout) itemView.findViewById(R.id.text3_container);
            add_image = (ImageView) itemView.findViewById(R.id.add_image);
            main_container = (RelativeLayout) itemView.findViewById(R.id.main_container);

        }
    }

}
