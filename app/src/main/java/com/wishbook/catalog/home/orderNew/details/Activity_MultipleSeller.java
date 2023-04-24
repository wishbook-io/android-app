package com.wishbook.catalog.home.orderNew.details;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.CartCatalogModel;
import com.wishbook.catalog.home.cart.CartHandler;
import com.wishbook.catalog.home.orderNew.adapters.CartMultipleSellerAdapter;

import java.util.ArrayList;

public class Activity_MultipleSeller extends AppCompatActivity {

    RecyclerView seller_items;
    ArrayList<CartCatalogModel.Sellers> sellers;
    ArrayList<CartCatalogModel.Products> products;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_multiple_seller);
            seller_items = findViewById(R.id.seller_items);
            Context mContext = Activity_MultipleSeller.this;
            toolbar = findViewById(R.id.toolbar_top);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("Select Supplier");
            if (getIntent().getSerializableExtra("sellers") != null) {
                sellers = (ArrayList<CartCatalogModel.Sellers>) getIntent().getSerializableExtra("sellers");
                Log.d("MULTIPLE_SELLER", "TRUE" + sellers.size());
            }
            if (getIntent().getSerializableExtra("quantity") != null) {
                products = (ArrayList<CartCatalogModel.Products>) getIntent().getSerializableExtra("quantity");
                Log.d("MULTIPLE_SELLER", "TRUE" + sellers.size());
            }
            if (sellers != null) {
                Log.d("MULTIPLE_SELLER", "FALSE");
                CartMultipleSellerAdapter cartMultipleSellerAdapter = new CartMultipleSellerAdapter(sellers, mContext, seller_items,getIntent().getStringExtra("current_seller"));
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(Activity_MultipleSeller.this);
                seller_items.setLayoutManager(mLayoutManager);
                seller_items.setAdapter(cartMultipleSellerAdapter);
            }

            findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < sellers.size(); i++) {
                        if (sellers.get(i).isSelected()) {
                            backToCart(sellers.get(i).getCompany_id());
                        }
                    }
                }
            });
        }catch (Exception e){e.printStackTrace();}

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void backToCart(String sellerId) {
        CartHandler cartHandler = new CartHandler(Activity_MultipleSeller.this);
        int a[]=new int[products.size()];
        for(int i=0;i<products.size();i++){
            a[i]=products.get(i).getQuantity();
        }
        if (getIntent().getBooleanExtra("isFullCatalog", true)) {
            if(getIntent().getStringExtra("note").length()>0) {
                cartHandler. addCatalogToCart(getIntent().getStringExtra("catalogId"), sellerId, null, "multipleseller", Activity_MultipleSeller.this, null,getIntent().getStringExtra("note"));
            }else{
                cartHandler.addCatalogToCart(getIntent().getStringExtra("catalogId"), sellerId, null, "multipleseller", Activity_MultipleSeller.this, null,"Nan");
            }
        } else {

            // Disaable code for major changes
            if(getIntent().getStringExtra("note").length()>0) {
                //cartHandler.addProductToCart(null, getIntent().getStringExtra("productId"), getIntent().getStringExtra("price"), a, false, sellerId, null, "multipleseller", Activity_MultipleSeller.this, getIntent().getStringExtra("note"));
            }else{
               // cartHandler.addProductToCart(null, getIntent().getStringExtra("productId"), getIntent().getStringExtra("price"), a, false, sellerId, null, "multipleseller", Activity_MultipleSeller.this, "Nan");
            }
        }
    }
}
