package com.wishbook.catalog.home.more;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.dropdownadapters.CategoriesAdapter;
import com.wishbook.catalog.commonmodels.ResponseHomeCategories;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.home.more.adapters.RowItem;
import com.wishbook.catalog.home.more.adapters.treeview.FileTreeAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_Categories extends GATrackedFragment {
    private View v;
    private ViewGroup containerparent;
    private FileTreeAdapter mTreeAdapter;
    private ArrayList<RowItem> rowItems = new ArrayList<>();

    public Fragment_Categories() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_categories, container, false);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.appbar);

        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.LOGIN_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("CategoriesList_screen");
        HashMap<String, String> prop = new HashMap<>();
        prop.put("visited","true");
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(getActivity(), wishbookEvent);

        final RecyclerView rv = (RecyclerView) v.findViewById(R.id.rv);
        int[] IMAGES = {
//                R.drawable.sarees,
//                R.drawable.kurti,
//                R.drawable.lehengas,
//                R.drawable.suits,
//                R.drawable.noprev,
//                R.drawable.blouse,
//                R.drawable.ghagra
                    R.drawable.placeholder_image,
                R.drawable.placeholder_image,
                R.drawable.placeholder_image,
                R.drawable.placeholder_image,
                R.drawable.placeholder_image,
                R.drawable.placeholder_image,
                R.drawable.placeholder_image

        };
        rowItems = new ArrayList<RowItem>();
        rowItems.add(new RowItem(IMAGES[0]));
        rowItems.add(new RowItem(IMAGES[1]));
        rowItems.add(new RowItem(IMAGES[2]));
        rowItems.add(new RowItem(IMAGES[3]));
        rowItems.add(new RowItem(IMAGES[4]));
        rowItems.add(new RowItem(IMAGES[5]));
        toolbar.setVisibility(View.GONE);
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "category", "") + "?parent=10", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    ResponseHomeCategories[] response_catagories = Application_Singleton.gson.fromJson(response, ResponseHomeCategories[].class);
                    if (response_catagories.length > 0) {
                        if (response_catagories[0].getId() != null) {
                            CategoriesAdapter categoriesAdapter = new CategoriesAdapter((AppCompatActivity) getActivity(), response_catagories, rowItems);
                            rv.setAdapter(categoriesAdapter);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });

        return v;
    }
}
