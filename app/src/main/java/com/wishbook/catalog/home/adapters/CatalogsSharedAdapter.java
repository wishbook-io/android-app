package com.wishbook.catalog.home.adapters;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.ResponseCodes;
import com.wishbook.catalog.Utils.DataPasser;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.CatalogMinified;
import com.wishbook.catalog.home.catalog.CatalogHolder;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;


public class CatalogsSharedAdapter extends RecyclerView.Adapter<CatalogsSharedAdapter.CustomViewHolder> {
    private ArrayList<CatalogMinified> feedItemList;
    private AppCompatActivity mContext;
    private Fragment recievedCatalogs;
    private String Order;
    private UserInfo userinfo;
    private View view;

    public CatalogsSharedAdapter(AppCompatActivity context, ArrayList<CatalogMinified> feedItemList, String order, Fragment fragment_recievedCatalogs) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.Order = order;
        this.recievedCatalogs = fragment_recievedCatalogs;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private final SimpleDraweeView cat_img, brand_img;
        private final TextView cattitle;
        private final RelativeLayout disable_container;
        private final TextView disable_text, disable_text1;
        private final TextView price_range;
        private final RelativeLayout mainContainer;


        public CustomViewHolder(View view) {
            super(view);
            cattitle = (TextView) view.findViewById(R.id.cattitle);
            cat_img = (SimpleDraweeView) view.findViewById(R.id.cat_img);
            brand_img = (SimpleDraweeView) view.findViewById(R.id.brand_img);
            price_range = (TextView) view.findViewById(R.id.price_range);
            mainContainer = (RelativeLayout) view.findViewById(R.id.MainContainer);
            disable_container = (RelativeLayout) view.findViewById(R.id.disable_container);
            disable_text = (TextView) view.findViewById(R.id.disable_text);
            disable_text1 = (TextView) view.findViewById(R.id.disable_text1);

            userinfo = UserInfo.getInstance(mContext);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.browse_catalog_item_version2, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {

        final CatalogMinified responseCatalogMinified = feedItemList.get(i);

        String catimage = responseCatalogMinified.getThumbnail().getThumbnail_medium();


        //price_range
        String price_range = responseCatalogMinified.getPrice_range();
        if (checkPriceRange(responseCatalogMinified)) {
            customViewHolder.price_range.setVisibility(View.VISIBLE);
            if (price_range.contains("-")) {
                String[] priceRangeMultiple = price_range.split("-");
                customViewHolder.price_range.setText("\u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1] + "/Pc.");
            } else {
                String priceRangeSingle = price_range;
                customViewHolder.price_range.setText("\u20B9" + priceRangeSingle + "/Pc.");
            }
        } else {
            if (responseCatalogMinified.getTotal_products() != null) {
                customViewHolder.price_range.setVisibility(View.VISIBLE);
                //customViewHolder.price_range.setText("( " + responseCatalogMinified.getTotal_products() + " Designs" + " )");
            } else {
                customViewHolder.price_range.setVisibility(View.GONE);
            }
            //customViewHolder.price_range.setVisibility(View.GONE);
        }

        if (responseCatalogMinified.getSupplier_disabled() || responseCatalogMinified.getBuyer_disabled()) {
            if (responseCatalogMinified.getBuyer_disabled()) {
                customViewHolder.disable_container.setAlpha(0.5f);
                customViewHolder.disable_text.setVisibility(View.VISIBLE);
                customViewHolder.disable_text.setText(mContext.getResources().getString(R.string.buyer_disable));
            } else {
                customViewHolder.disable_text.setVisibility(View.GONE);
            }
            if (responseCatalogMinified.getSupplier_disabled()) {
                customViewHolder.disable_container.setAlpha(0.5f);
                customViewHolder.disable_text1.setVisibility(View.VISIBLE);
                customViewHolder.disable_text1.setText(mContext.getResources().getString(R.string.supplier_disable));
            } else {
                customViewHolder.disable_text1.setVisibility(View.GONE);
            }
        } else {
            customViewHolder.disable_container.setAlpha(1);
            customViewHolder.disable_text1.setVisibility(View.INVISIBLE);
            customViewHolder.disable_text.setVisibility(View.INVISIBLE);
        }


        if (catimage != null & !catimage.equals("")) {
            StaticFunctions.loadFresco(mContext, catimage, customViewHolder.cat_img);
        }

        String brand_image = responseCatalogMinified.getBrand_image();
        if (brand_image != null & !brand_image.equals("")) {
            StaticFunctions.loadFresco(mContext, brand_image, customViewHolder.brand_img);
        }

        //hyperlinking
        customViewHolder.brand_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticFunctions.hyperLinking1("brand", responseCatalogMinified.getBrand(), mContext, responseCatalogMinified.getBrand_name());
            }
        });


        customViewHolder.mainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CatalogMinified selectedCatalog = new CatalogMinified(responseCatalogMinified.getId(), responseCatalogMinified.getType(), responseCatalogMinified.getFull_catalog_orders_only(), responseCatalogMinified.getBuyer_disabled(), responseCatalogMinified.getTitle());
                selectedCatalog.setBrand_name(responseCatalogMinified.getBrand_name());
                selectedCatalog.setType("catalog");
                selectedCatalog.setSupplier_chat_user(responseCatalogMinified.getSupplier_chat_user());
                selectedCatalog.setSupplier_name(responseCatalogMinified.getSupplier_name());
                selectedCatalog.setSupplier(responseCatalogMinified.getSupplier());
                selectedCatalog.setPrice_range(responseCatalogMinified.getPrice_range());
                selectedCatalog.setBuyer_disabled(responseCatalogMinified.getBuyer_disabled());
                selectedCatalog.setSupplier_disabled(responseCatalogMinified.getSupplier_disabled());
                selectedCatalog.setSupplier_details(responseCatalogMinified.getSupplier_details());
                selectedCatalog.setFromReceived(true);
                selectedCatalog.setIs_addedto_wishlist(responseCatalogMinified.getIs_addedto_wishlist());
                Application_Singleton.selectedshareCatalog = selectedCatalog;

                if (selectedCatalog.getType().matches("selection")) {
                    DataPasser.selectedID = selectedCatalog.getId();
                    Application_Singleton.CONTAINER_TITLE = responseCatalogMinified.getTitle();
                    Application_Singleton.CONTAINERFRAG = new Fragment_CatalogsGallery();
                    Intent intent = new Intent(mContext, OpenContainer.class);
                    intent.putExtra("toolbarCategory", OpenContainer.SELECTIONSHARE);
                    intent.putExtra("Ordertype", Order);
                    recievedCatalogs.startActivityForResult(intent, ResponseCodes.Received_Catalogs);
                } else {
                    Application_Singleton.CONTAINER_TITLE = responseCatalogMinified.getTitle();
                    Application_Singleton.CONTAINERFRAG = new Fragment_CatalogsGallery();
                    Application_Singleton.isFromGallery = CatalogHolder.MYRECEIVED;
                    Intent intent = new Intent(mContext, OpenContainer.class);
                    intent.putExtra("toolbarCategory", OpenContainer.CATALOGSHARE);
                    intent.putExtra("Ordertype", Order);
                    recievedCatalogs.startActivityForResult(intent, ResponseCodes.Received_Catalogs);
                }

            }
        });
        customViewHolder.cattitle.setText(StringUtils.capitalize(responseCatalogMinified.getTitle().toLowerCase().trim()));

       /* customViewHolder.linear_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CatalogMinified selectedCatalog = new CatalogMinified(responseCatalogMinified.getId(), responseCatalogMinified.getType(), responseCatalogMinified.getFull_catalog_orders_only(), responseCatalogMinified.getBuyer_disabled(), responseCatalogMinified.getIs_viewed(), responseCatalogMinified.getBrand_name(), responseCatalogMinified.getExp_desp_date());
                selectedCatalog.setPrice_range(responseCatalogMinified.getPrice_range());

                Application_Singleton.shareCatalogHolder = selectedCatalog;
                Fragment_CatalogsGallery fragment_catalogsGallery = new Fragment_CatalogsGallery();
                fragment_catalogsGallery.showShareBottomSheet(mContext, responseCatalogMinified.getProduct_type(), responseCatalogMinified.getId());
                Application_Singleton.trackEvent("Catalog Cover", "Share", "Received");
            }
        });*/

    }



    private boolean checkPriceRange(CatalogMinified responseCatalogMinified) {
        if (responseCatalogMinified.getPrice_range() != null && !responseCatalogMinified.getPrice_range().equals("0.00") && !responseCatalogMinified.getPrice_range().equals("0.0") && !responseCatalogMinified.getPrice_range().equals("0")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return feedItemList.size();
    }
}