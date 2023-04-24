package com.wishbook.catalog.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.home.cart.Fragment_MyCart;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;
import com.wishbook.catalog.home.catalog.details.Fragment_SingleDetail;
import com.wishbook.catalog.login.Activity_Register;
import com.wishbook.catalog.login.Activity_Register_Version2;

public class NavigationUtils {

    public void navigateDetailPage(Context context, Bundle bundle) {
        Fragment_CatalogsGallery gallery = new Fragment_CatalogsGallery();
        gallery.setArguments(bundle);
        Application_Singleton.CONTAINERFRAG = gallery;
        if(bundle.getString("product_id")!=null) {
            Application_Singleton.CONTAINER_TITLE = "Product ID: "+bundle.getString("product_id");
        } else {
            Application_Singleton.CONTAINER_TITLE = "";
        }
        Intent intent = new Intent(context, OpenContainer.class);
        intent.putExtra("toolbarCategory", OpenContainer.BROWSECATALOG);
        context.startActivity(intent);
    }

    public void navigateSingleProductDetailPage(Context context,Bundle bundle) {
        Fragment_SingleDetail singleDetail = new Fragment_SingleDetail();
        singleDetail.setArguments(bundle);
        Application_Singleton.CONTAINERFRAG = singleDetail;
        if(bundle.getString("product_id")!=null) {
            Application_Singleton.CONTAINER_TITLE = "Product ID: "+bundle.getString("product_id");
        } else {
            Application_Singleton.CONTAINER_TITLE = "";
        }
        Intent intent = new Intent(context, OpenContainer.class);
        intent.putExtra("toolbarCategory", OpenContainer.BROWSECATALOG);
        context.startActivity(intent);
    }

    public void navigateMyCart(Context context) {
        Application_Singleton.CONTAINER_TITLE = "My Cart";
        Application_Singleton.CONTAINERFRAG = new Fragment_MyCart();
        context.startActivity(new Intent(context, OpenContainer.class));
    }

    public void navigateRegisterPage(Context context, Bundle bundle) {
        Intent registerIntent = new Intent(context, Activity_Register_Version2.class);
        registerIntent.putExtras(bundle);
        context.startActivity(registerIntent);
    }
}
