package com.wishbook.catalog.home.catalog.add;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.multipleimageselect.models.Image;
import com.wishbook.catalog.home.adapters.AddProductAdapter2;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_AddProduct_Version2 extends GATrackedFragment {


    @BindView(R.id.radio_full_catalog)
    RadioButton radio_full_catalog;

    @BindView(R.id.radio_single_piece_catalog)
    RadioButton radio_single_piece_catalog;

    @BindView(R.id.recycler_products)
    RecyclerView recycler_products;

    @BindView(R.id.btn_add_product)
    TextView btn_add_product;


    private Bitmap bitmapImage = null;
    private ArrayList<Image> product_image;
    private ArrayList<Image> product_image_clone;
    private AddProductAdapter2 addProductAdapter;
    int textViewDefineCount = 0;
    int recyclerViewCount = 0;
    int counter = 0;
    int productCount = 1;


    private String catalog_id = "";
    private boolean catalog_fullproduct;
    private String catalog_type;

    private String singlePCAddPer, singlePcAddPrice;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_add_product_version2, ga_container, true);
        ButterKnife.bind(this, v);


        if (getArguments() != null) {
            catalog_id = getArguments().getString("catalog_id");
            catalog_fullproduct = getArguments().getBoolean("catalog_fullproduct", false);
            catalog_type = getArguments().getString("catalog_type");
        }
        bindProductRecyclerView();
        return v;
    }

    public void bindProductRecyclerView() {
        boolean isCatalog = true;
        if (catalog_type != null && catalog_type.equalsIgnoreCase(Constants.PRODUCT_TYPE_NON)) {
            isCatalog = false;
        }
        product_image = new ArrayList<>();
        addProductAdapter = new AddProductAdapter2(getActivity(), product_image,
                radio_full_catalog.isChecked(),
                Fragment_AddProduct_Version2.this,
                true, recycler_products, isCatalog);
        recycler_products.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_products.setAdapter(addProductAdapter);
        recycler_products.setNestedScrollingEnabled(false);
    }

}
