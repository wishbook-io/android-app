package com.wishbook.catalog.home.orders.adapters;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapterSupplier_MySelections;
import com.wishbook.catalog.commonmodels.SellingCompany;
import com.wishbook.catalog.commonmodels.responses.ProductsSelection;
import com.wishbook.catalog.home.inventory.BrandsModel;
import com.wishbook.catalog.home.inventory.CatalogsModel;
import com.wishbook.catalog.home.orders.add.Fragment_PurchaseBackOrder;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by root on 30/1/17.
 */
public class ProductsMySelectionAdapter extends RecyclerView.Adapter<ProductsMySelectionAdapter.MyViewHolder> {


    private Context mActivity;
    private ArrayList<BrandsModel> brandsModels = new ArrayList<>();
    private ArrayList<CatalogsModel> catalogsModels = new ArrayList<>();
    private ProductsSelection[] products =new ProductsSelection[]{};
    private BrandAdapterMySelection.ProductChangeListener productChangeListener;

    public ProductsMySelectionAdapter(Context mActivity, ProductsSelection[] products, ArrayList<CatalogsModel> catalogsModels, ArrayList<BrandsModel> brandsModels, BrandAdapterMySelection.ProductChangeListener productChangeListener) {
        this.mActivity = mActivity;
        this.products = products;
        this.brandsModels=brandsModels;
        this.catalogsModels=catalogsModels;
        this.productChangeListener=productChangeListener;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView product_title,price;
        EditText quantity;
        ImageView delete;
        SimpleDraweeView image;
        Spinner suppliers;

        public MyViewHolder(View view) {
            super(view);
            product_title = (TextView) view.findViewById(R.id.product_title);
            price =  (TextView) view.findViewById(R.id.price);
            quantity = (EditText) view.findViewById(R.id.quantity);
            image = (SimpleDraweeView) view.findViewById(R.id.image);
            delete = (ImageView) view.findViewById(R.id.delete);
            suppliers = (Spinner) view.findViewById(R.id.suppliers);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.products_myselection_brandwise, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,  int position) {


        SpinAdapterSupplier_MySelections spinAdapter_suppliers = new SpinAdapterSupplier_MySelections((Activity) mActivity, R.layout.spinneritem, products[position].getSelling_company());
        holder.suppliers.setAdapter(spinAdapter_suppliers);

        if(products[position].getQuantity()!=null){
            holder.quantity.setText(products[position].getQuantity());
        }

        if(products[position].getSelling_company_id()>=0){

            for(SellingCompany sellingCompany : products[position].getSelling_company()){

                if(products[position].getSelling_company_id() == sellingCompany.getId()) {
                    holder.suppliers.setSelection(spinAdapter_suppliers.getPosition(sellingCompany));
                }

            }

        }


        holder.suppliers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                holder.price.setText(products[holder.getAdapterPosition()].getSelling_company()[i].getPrice());
                for(ProductsSelection selection : Fragment_PurchaseBackOrder.order.getProducts())
                {
                    if(selection.getId().equals(products[holder.getAdapterPosition()].getId()))
                    {
                        selection.setPrice(products[holder.getAdapterPosition()].getSelling_company()[i].getPrice());
                        selection.setSelling_company_id(products[holder.getAdapterPosition()].getSelling_company()[i].getId());
                        productChangeListener.OnChange();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        holder.product_title.setText(products[position].getSku().toString());
        if(products[position].getSelling_company().length>0) {
            holder.price.setText(products[position].getSelling_company()[0].getPrice());
        }

        if (products[position].getImage() != null) {
           // StaticFunctions.loadImage(mActivity,products[position].getImage().getThumbnail_small(),holder.image,R.drawable.uploadempty);
            StaticFunctions.loadFresco(mActivity,products[position].getImage().getThumbnail_small(),holder.image);
         }



                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mActivity, "Product Deleted", Toast.LENGTH_SHORT).show();
                        ProductsSelection selectiontoremove = null;
                        for(ProductsSelection selection : Fragment_PurchaseBackOrder.order.getProducts())
                        {
                            if(selection.getId().equals(products[holder.getAdapterPosition()].getId()))
                            {
                                selectiontoremove = selection;
                             //   Fragment_PurchaseBackOrder.order.getProducts().remove(selection);
                            }
                        }
                        if(selectiontoremove!=null) {
                            Fragment_PurchaseBackOrder.order.getProducts().remove(selectiontoremove);
                            productChangeListener.OnChange();
                        }
                        Boolean done=false;
                        for (int i = 0; i < brandsModels.size(); i++) {
                            for (int j = 0; j < brandsModels.get(i).getCatalogs().size(); j++) {
                                if (done == false) {
                                    for (int k = 0; k < brandsModels.get(i).getCatalogs().get(j).getProductsSelections().length; k++) {
                                        if (brandsModels.get(i).getCatalogs().get(j).getProductsSelections()[k].getId().equals(products[holder.getAdapterPosition()].getId())) {
                                            if (brandsModels.get(i).getCatalogs().get(j).getProductsSelections().length > 1) {
                                                removeProduct(i,j,k,brandsModels);
                                                done = true;
                                                productChangeListener.OnChange();
                                                CatalogsAdapterMySelection.productAdapter.notifyDataSetChanged();
                                                break;
                                            } else {
                                               // brandsModels.get(i).getCatalogs().get(j).getProducts().remove(k);
                                                brandsModels.get(i).getCatalogs().remove(j);
                                                brandsModels.get(i).setExpanded(false);
                                                done = true;
                                                productChangeListener.OnChange();

                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            if (brandsModels.get(i).getCatalogs().size() > 0) {

                            } else {
                                brandsModels.remove(i);
                                productChangeListener.OnChange();

                                break;
                            }
                        }


                    }
                });

        holder.quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                products[holder.getAdapterPosition()].setQuantity(editable.toString());
                if(!editable.toString().equals("") && !editable.toString().equals("0") ) {
                   for(ProductsSelection selection : Fragment_PurchaseBackOrder.order.getProducts())
                   {
                       if(selection.getId().equals(products[holder.getAdapterPosition()].getId()))
                       {
                           selection.setQuantity(editable.toString());
                           productChangeListener.OnChange();
                       }

                   }
                }

                //products[position].setQuantity(editable.toString());
            }
        });


    }

    private ArrayList<BrandsModel> removeProduct(int i, int j, int k, ArrayList<BrandsModel> brandsModels) {
        ArrayList<ProductsSelection> products = new ArrayList<>(Arrays.asList( brandsModels.get(i).getCatalogs().get(j).getProductsSelections()));
        products.remove(k);
        ProductsSelection[] productsSelection = products.toArray(new ProductsSelection[products.size()]);
        brandsModels.get(i).getCatalogs().get(j).setProductsSelections(productsSelection);

        return  brandsModels;
    }


    @Override
    public int getItemCount() {
        return products.length;
    }





}
