package com.wishbook.catalog.home.orderNew.adapters;


import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonadapters.ReceivedProductsAdapterNew;
import com.wishbook.catalog.commonmodels.ProductObjectQuantity;
import com.wishbook.catalog.commonmodels.ProductQunatityRate;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.orderNew.expandableRecyclerview.CatalogListItem;

import java.util.ArrayList;

public class SubProductAdapter2 extends RecyclerView.Adapter<SubProductAdapter2.MyChildViewHolder> {


    private LayoutInflater mInflater;
    private Context context;
    private ArrayList<CatalogListItem> feedItemList;
    private String catId = null;
    private boolean fullproduct = false;
    private boolean isRemoveHeader = false;
    private int catalogPosition;
    private Response_catalogMini response_catalog;


    public SubProductAdapter2(Context context, @NonNull ArrayList<CatalogListItem> feedItemList, String catId, boolean fullproduct, int catalogPosition, Response_catalogMini response_catalog, ProductChangeListener productChangeListener) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.feedItemList = feedItemList;
        this.context = context;
        this.productChangeListener = productChangeListener;
        this.fullproduct = fullproduct;
        this.response_catalog = response_catalog;
        this.catalogPosition = catalogPosition;
    }

    @Override
    public MyChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.purchase_order_item, parent, false);
        return new SubProductAdapter2.MyChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyChildViewHolder childViewHolder, int position) {
        final ProductObjectQuantity productObjectQuantity = (ProductObjectQuantity) feedItemList.get(0).getFeedItemList().get(position);
        if (productObjectQuantity.isFullCatalog()) {
            childViewHolder.minusbut.setVisibility(View.GONE);
            childViewHolder.plusbut.setVisibility(View.GONE);
            childViewHolder.delete.setVisibility(View.GONE);
            childViewHolder.qntytext.setEnabled(false);
        } else {
            childViewHolder.minusbut.setVisibility(View.VISIBLE);
            childViewHolder.plusbut.setVisibility(View.VISIBLE);
            childViewHolder.delete.setVisibility(View.VISIBLE);
            childViewHolder.qntytext.setEnabled(false);
        }

        if (productObjectQuantity.getFeedItemList().getSku() != null && !productObjectQuantity.getFeedItemList().getSku().equals("null") && !productObjectQuantity.getFeedItemList().getSku().equals("")) {
            childViewHolder.prod_name.setVisibility(View.VISIBLE);
            childViewHolder.prod_name.setText(productObjectQuantity.getFeedItemList().getSku());
        } else {
            childViewHolder.prod_name.setVisibility(View.GONE);
        }

        childViewHolder.prod_price.setText("\u20B9" + productObjectQuantity.getPrice());
        String image = productObjectQuantity.getFeedItemList().getImage().getThumbnail_small();
        try {
            if (image != null && !image.equals("")) {
                // StaticFunctions.loadImage(context, image, childViewHolder.prod_img, R.drawable.uploadempty);
                StaticFunctions.loadFresco(context, image, childViewHolder.prod_img);
            }
        }catch (Exception e){e.printStackTrace();}
        childViewHolder.prod_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage(productObjectQuantity.getFeedItemList().getImage().getThumbnail_medium());
            }
        });


        childViewHolder.qntytext.setText("" + productObjectQuantity.getQuantity());
        /*childViewHolder.qntytext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!productObjectQuantity.isFullCatalog()) {
                    if (childViewHolder.qntytext.getText().equals("0")) {
                        productObjectQuantity.setQuantity(1);
                        // childViewHolder.qntytext.setText("" + productObjectQuantity.getQuantity());
                        productChangeListener.onChange(catalogPosition);
                    } else {
                        try {
                            productObjectQuantity.setQuantity(Integer.parseInt(childViewHolder.qntytext.getText().toString()));
                            notifyDataSetChanged();
                            // childViewHolder.qntytext.setText("" + productObjectQuantity.getQuantity());
                            productChangeListener.onChange(catalogPosition);
                        } catch (IllegalStateException e) {

                        } catch (Exception e) {
                        }

                    }
                }
            }
        });*/


        if (!productObjectQuantity.isFullCatalog()) {
            childViewHolder.minusbut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (productObjectQuantity.getQuantity() > 0) {
                        productObjectQuantity.setQuantity(productObjectQuantity.getQuantity() - 1);
                        notifyDataSetChanged();
                        childViewHolder.qntytext.setText("" + productObjectQuantity.getQuantity());
                        productChangeListener.onChange(catalogPosition);
                    }
                }
            });
            childViewHolder.plusbut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    productObjectQuantity.setQuantity(productObjectQuantity.getQuantity() + 1);
                    notifyDataSetChanged();
                    productChangeListener.onChange(catalogPosition);
                }
            });
            childViewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(context)
                            .title("Delete")
                            .content("Do you want to delete this Product ?")
                            .negativeText("No")
                            .positiveText("Yes")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    productObjectQuantity.setQuantity(0);
                                    notifyDataSetChanged();
                                    productChangeListener.onChange(catalogPosition);
                                    dialog.dismiss();

                                }
                            })
                            .show();
                }
            });
        } else {
            childViewHolder.minusbut.setVisibility(View.GONE);
            childViewHolder.plusbut.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return feedItemList.get(0).getFeedItemList().size();
    }

    public ArrayList<ProductQunatityRate> getAllProducts() {
        ArrayList<ProductQunatityRate> productQunatityRates = new ArrayList<>();
        if (feedItemList != null) {
            for (CatalogListItem catalog : feedItemList) {
                for (ProductObjectQuantity obj : catalog.getFeedItemList()) {
                    if (obj.getQuantity() > 0) {
                        ProductQunatityRate productQunatityRate = new ProductQunatityRate(obj.getFeedItemList().getId(), obj.getQuantity(), obj.getPrice(), obj.getPacking_type());
                        productQunatityRates.add(productQunatityRate);
                    }
                }
            }

        }
        return productQunatityRates;
    }

    public void remove(ProductObjectQuantity product, int position, ReceivedProductsAdapterNew.MyChildViewHolder childViewHolder) {
        if (feedItemList != null) {
            for (int i = 0; i < feedItemList.size(); i++) {
                for (int j = 0; j < feedItemList.get(i).getFeedItemList().size(); j++) {
                    if (feedItemList.get(i).getFeedItemList().get(j).equals(product)) {
                        Log.i("TAG", "remove: sucess" + position);
                        feedItemList.get(i).getFeedItemList().remove(j);
                        break;
                    }
                }
            }
        }
    }

    public ArrayList<ProductObjectQuantity> getAllProducts(int quantity) {
        ArrayList<ProductObjectQuantity> productQunatityRates = new ArrayList<>();
        if (feedItemList != null) {
            for (CatalogListItem catalog : feedItemList) {
                for (ProductObjectQuantity obj : catalog.getFeedItemList()) {
                    if (obj.getQuantity() > 0) {
                        ProductObjectQuantity productQunatityRate = new ProductObjectQuantity(obj.getFeedItemList(), quantity, obj.getPrice());
                        productQunatityRates.add(productQunatityRate);
                    }
                }
            }
        }
        return productQunatityRates;
    }

    public ArrayList<ProductObjectQuantity> setAllProductsQty(int quantity, int position) {
        ArrayList<ProductObjectQuantity> productQunatityRates = new ArrayList<>();
        if (feedItemList != null) {
            for (ProductObjectQuantity obj : feedItemList.get(position).getFeedItemList()) {
               // if (obj.getQuantity() > 0) {
                    ProductObjectQuantity productQunatityRate = new ProductObjectQuantity(obj.getFeedItemList(), quantity, obj.getPrice(), obj.isFullCatalog(), obj.getPacking_type());
                    if (obj.getSetDesign() != 0) {
                        productQunatityRate.setSetDesign(obj.getSetDesign());
                    }
                    productQunatityRates.add(productQunatityRate);
               // }
            }
        }
        return productQunatityRates;
    }

    public ArrayList<ProductObjectQuantity> setAllProductsPkgType(String pkgtype, int position) {
        ArrayList<ProductObjectQuantity> productQunatityRates = new ArrayList<>();
        if (feedItemList != null) {
            for (ProductObjectQuantity obj : feedItemList.get(position).getFeedItemList()) {
                if (obj.getQuantity() > 0) {
                    ProductObjectQuantity productQunatityRate = new ProductObjectQuantity(obj.getFeedItemList(), obj.getQuantity(), obj.getPrice(), obj.isFullCatalog(), pkgtype);
                    if (obj.getSetDesign() != 0) {
                        productQunatityRate.setSetDesign(obj.getSetDesign());
                    }
                    productQunatityRates.add(productQunatityRate);
                }
            }
        }
        return productQunatityRates;
    }

    public int getTotalCatalogItem(CatalogListItem listItem) {
        int totalItem = 0;
        try {
            if (feedItemList != null) {
                for (ProductObjectQuantity obj : listItem.getFeedItemList()) {
                    totalItem += obj.getQuantity();
                }
            }
        } catch (Exception e) {
        }
        return totalItem;
    }

    public float getCurrentTotal() {
        float total = 0;
        for (CatalogListItem catalog : feedItemList) {
            for (ProductObjectQuantity productObj : catalog.getFeedItemList()) {
                try {
                    Log.d("TAG", "SubProduct getCurrentTotal: " + productObj.getQuantity());
                    total = total + (Float.parseFloat("" + productObj.getPrice()) * productObj.getQuantity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return total;
    }


    public void showImage(String imagepath) {
        Dialog mSplashDialog = new Dialog(context, R.style.AppTheme_Dark_Dialog);
        mSplashDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSplashDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSplashDialog.setContentView(R.layout.imagedialog);
        SimpleDraweeView image = (SimpleDraweeView) mSplashDialog.findViewById(R.id.myimg);
        if (imagepath != null & !imagepath.equals("")) {
            // StaticFunctions.loadImage(context, imagepath, image, R.drawable.uploadempty);
            StaticFunctions.loadFresco(context, imagepath, image);
            //Picasso.with(mContext).load(imagepath).into(image);
        }
        mSplashDialog.setCancelable(true);
        mSplashDialog.show();
    }

    public String getCatId() {
        return catId;
    }

    private ProductChangeListener productChangeListener;


    public interface ProductChangeListener {
        void onChange(int position);
    }

    public ArrayList<CatalogListItem> getFeedItemList() {
        return feedItemList;
    }

    public class MyChildViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView prod_img;
        private TextView prod_name;
        private TextView prod_seller;
        private TextView prod_price;
        private EditText qntytext;
        private TextView minusbut;
        private TextView plusbut;
        private ImageView delete;


        public MyChildViewHolder(View itemView) {
            super(itemView);
            prod_img = (SimpleDraweeView) itemView.findViewById(R.id.prod_img);
            prod_name = (TextView) itemView.findViewById(R.id.prod_name);
            prod_seller = (TextView) itemView.findViewById(R.id.prod_seller);
            prod_price = (TextView) itemView.findViewById(R.id.prod_price);
            qntytext = (EditText) itemView.findViewById(R.id.edit_qty);
            minusbut = (TextView) itemView.findViewById(R.id.btn_minus);
            plusbut = (TextView) itemView.findViewById(R.id.btn_plus);
            delete = (ImageView) itemView.findViewById(R.id.delete);
        }
    }
}
