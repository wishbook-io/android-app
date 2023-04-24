package com.wishbook.catalog.home.contacts;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.commonadapters.BackOrderAdapter;
import com.wishbook.catalog.commonmodels.responses.Response_sellingorder;
import com.wishbook.catalog.home.orders.add.Fragment_PurchaseBackOrder;

import java.util.ArrayList;

/**
 * Created by root on 6/2/17.
 */
public class Fragment_BackOrder extends GATrackedFragment {

    private BackOrderAdapter salesOrderAdapter;
    private RecyclerViewEmptySupport mRecyclerView;
    private Button orderBut;
    private ArrayList<String> selectedList = new ArrayList<>();
    ArrayList<Response_sellingorder> list = new ArrayList<>();
    public Fragment_BackOrder() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.backorder,container,false);
        mRecyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);

        orderBut = (Button) v.findViewById(R.id.orderBut);

        mRecyclerView.setEmptyView(v.findViewById(R.id.list_empty1));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        final Bundle bundle = getArguments();

        if(bundle!=null) {
             list = (ArrayList<Response_sellingorder>) bundle.getSerializable("ARRAYLIST");
        }
        for(int i=0;i<list.size();i++){
            list.get(i).setSelected(false);
        }


        salesOrderAdapter = new BackOrderAdapter(getActivity(), list);
        mRecyclerView.setAdapter(salesOrderAdapter);

        orderBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (salesOrderAdapter.getAllSelected().size() > 0) {
                    String listOfIds = "";
                    for (int i = 0; i < salesOrderAdapter.getAllSelected().size(); i++) {
                        if (i == salesOrderAdapter.getAllSelected().size() - 1) {
                            listOfIds = listOfIds + salesOrderAdapter.getAllSelected().get(i);
                        } else{
                            listOfIds = listOfIds + salesOrderAdapter.getAllSelected().get(i) + ",";
                    }
                    }
                    Fragment_PurchaseBackOrder order = new Fragment_PurchaseBackOrder();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("fromSales",listOfIds);
                    order.setArguments(bundle1);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main, order).addToBackStack(null).commit();

                }else
                {
                    Toast.makeText(getActivity(), "Please select atleast 1 order", Toast.LENGTH_LONG).show();
                }
              }
        });


        return  v;

    }
}
