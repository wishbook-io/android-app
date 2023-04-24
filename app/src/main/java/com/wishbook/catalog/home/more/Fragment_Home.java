package com.wishbook.catalog.home.more;

import android.os.Bundle;
import com.wishbook.catalog.GATrackedFragment;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import com.wishbook.catalog.R;
import com.wishbook.catalog.home.more.adapters.CustomListviewAdapter;
import com.wishbook.catalog.home.more.adapters.RowItem;

public class Fragment_Home extends GATrackedFragment {

    private View v;
    private AppCompatButton browseCatalogsBttn, manageSelectionsBttn, addVisitsBttn, placeOrdersBttn;
    private ArrayList<RowItem> rowItems = new ArrayList<>();
    public Fragment_Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_help, container, false);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.appbar);
       /* browseCatalogsBttn = (AppCompatButton) v.findViewById(R.id.btn_browse_catalogs);
        manageSelectionsBttn = (AppCompatButton) v.findViewById(R.id.btn_manage_selections);
        addVisitsBttn = (AppCompatButton) v.findViewById(R.id.btn_add_visits);
        placeOrdersBttn = (AppCompatButton) v.findViewById(R.id.btn_place_orders);*/
        // toolbar.setVisibility(View.GONE);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.rv);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        //listView.setAdapter(new ArrayAdapter<Integer>());
       /* int[] IMAGES = {
                R.drawable.stepregistration,
                R.drawable.summary,
                R.drawable.step2invitecontacts,
                R.drawable.step3_upload_catalogs,
                R.drawable.step4_addproducts,
                R.drawable.step5_sharecatalogs,
                R.drawable.step6create_order
        };*/
        rowItems = new ArrayList<RowItem>();
       /* rowItems.add(new RowItem(IMAGES[0]));
        rowItems.add(new RowItem(IMAGES[1]));
        rowItems.add(new RowItem(IMAGES[2]));
        rowItems.add(new RowItem(IMAGES[3]));
        rowItems.add(new RowItem(IMAGES[4]));
        rowItems.add(new RowItem(IMAGES[5]));
        rowItems.add(new RowItem(IMAGES[6]));
*/



        CustomListviewAdapter Adapter = new CustomListviewAdapter(getActivity(),rowItems);

        recyclerView.setAdapter(Adapter);

       /* browseCatalogsBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browseCatalogIntent = new Intent(getActivity(), Activity_BrowseCatalogs.class);
                startActivity(browseCatalogIntent);
            }
        });

        manageSelectionsBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent managingSelectionsIntent = new Intent(getActivity(), Activity_ManagingSelections.class);
                startActivity(managingSelectionsIntent);
            }
        });

        addVisitsBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addingVisitsIntent = new Intent(getActivity(), Activity_AddingVisits.class);
                startActivity(addingVisitsIntent);
            }
        });

        placeOrdersBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent placingOrdersIntent = new Intent(getActivity(), Activity_PlacingOrders.class);
                startActivity(placingOrdersIntent);
            }
        });*/

        return v;
    }

}
