package com.wishbook.catalog.home.orderNew.adapters;


import android.content.Context;
import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.ProductObjectQuantity;
import com.wishbook.catalog.commonmodels.ProductQunatityRate;
import com.wishbook.catalog.commonmodels.responses.CustomViewModel;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.orderNew.add.Fragment_CreatePurchaseOrderVersion2;
import com.wishbook.catalog.home.orderNew.add.Fragment_CreateSaleOrder_Version2;
import com.wishbook.catalog.home.orderNew.expandableRecyclerview.CatalogListItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoreCatalogViewAdapterVersion2 extends RecyclerView.Adapter<MoreCatalogViewAdapterVersion2.CustomViewHolder> {

    private Context mContext;
    private Fragment fragment;
    private MoreCatalogViewAdapterVersion2.ViewChangeListener viewChangeListener;
    private boolean isDefaultAvailable = true;
    private ArrayList<Response_catalogMini> response_catalogMinis;
    private ArrayList<CustomViewModel> customViewModels;
    private ProductObj[] productObjs = null;
    ArrayList<com.wishbook.catalog.home.orderNew.expandableRecyclerview.CatalogListItem> list;
    private static String TAG = MoreCatalogViewAdapterVersion2.class.getSimpleName();
    SubProductAdapter subProductAdapter;

    public MoreCatalogViewAdapterVersion2(Context mContext, Fragment fragment, ArrayList<Response_catalogMini> response_catalogMinis) {
        this.mContext = mContext;
        this.fragment = fragment;
        this.response_catalogMinis = response_catalogMinis;
        list = new ArrayList<>();
        subProductAdapter = null;
    }

    @Override
    public MoreCatalogViewAdapterVersion2.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_more_catalog_item_version2, parent, false);
        return new MoreCatalogViewAdapterVersion2.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final Response_catalogMini catalogMini = response_catalogMinis.get(position);
        if (position == 0) {
            holder.delete.setVisibility(View.GONE);
        } else {
            holder.delete.setVisibility(View.VISIBLE);
        }
        if (catalogMini.getTitle() != null) {
            holder.catalog_name.setText(catalogMini.getTitle());

            holder.catalog_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.liner_expand.performClick();
                }
            });
        }
        if (catalogMini.getThumbnail().getThumbnail_small() != null & !catalogMini.getThumbnail().getThumbnail_small().isEmpty()) {
            StaticFunctions.loadFresco(mContext, catalogMini.getThumbnail().getThumbnail_small(), holder.simpleDraweeView);
        }

        if (catalogMini.getFull_catalog_orders_only() != null && catalogMini.getFull_catalog_orders_only().equals("true")) {
            holder.full_catalog_note.setVisibility(View.VISIBLE);
        } else {
            holder.full_catalog_note.setVisibility(View.GONE);
        }


        holder.simpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.liner_expand.performClick();
            }
        });
        holder.liner_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(catalogMini.getCustomViewModel()!=null) {
                    if (catalogMini.getCustomViewModel().isField5()) {
                        // collapse
                        setExpanded(false, holder);
                        catalogMini.getCustomViewModel().setField5(false);
                    } else {
                        // expand
                        setExpanded(true, holder);
                        catalogMini.getCustomViewModel().setField5(true);
                    }
                }
            }
        });
        if (catalogMini.getCustomViewModel() != null) {
            if (catalogMini.getCustomViewModel().getField1() != null) {
                holder.edit_qty.setText(catalogMini.getCustomViewModel().getField1());
            } else {
                holder.edit_qty.setText("1");
            }
            setProducts(((Response_catalog) catalogMini.getCustomViewModel().getObject()), Integer.parseInt(catalogMini.getCustomViewModel().getField1()), holder.recycler_view_subitem, position, holder);
            holder.radiogroup_pkg_type_more.setOnCheckedChangeListener(null);
            holder.radiogroup_pkg_type_more.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    if (i == R.id.radio_default_boxed) {
                        viewChangeListener.onEditPkgType(mContext.getResources().getString(R.string.package_type_box), holder.getAdapterPosition());

                    } else {
                        viewChangeListener.onEditPkgType(mContext.getResources().getString(R.string.package_type_naked), holder.getAdapterPosition());
                    }
                }
            });
            ((RadioButton) holder.radiogroup_pkg_type_more.getChildAt(1)).setChecked(true);

            if (catalogMini.getCustomViewModel().isField5()) {
                setExpanded(true, holder);
            } else {
                setExpanded(false, holder);
            }
        }


        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int oldQuantity = Integer.parseInt(holder.edit_qty.getText().toString());
                if (oldQuantity > 0) {
                    int newQuantity = oldQuantity - 1;
                    holder.edit_qty.setText(String.valueOf(newQuantity));
                    catalogMini.getCustomViewModel().setField1(String.valueOf(newQuantity));
                    viewChangeListener.onEditQuantity(holder.getAdapterPosition(), newQuantity);
                    holder.txt_sub_set.setText(newQuantity + " set");
                    ArrayList<CatalogListItem> listnew = (ArrayList<CatalogListItem>) response_catalogMinis.get(holder.getAdapterPosition()).getCustomViewModel().getObjects();
                    if (listnew != null) {
                        holder.txt_sub_designs.setText(String.valueOf(((SubProductAdapter2) response_catalogMinis.get(holder.getAdapterPosition()).getCustomViewModel().getField4()).getTotalCatalogItem(listnew.get(0)) + " Pcs."));
                    }
                }
            }
        });

        holder.btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int oldQuantity = Integer.parseInt(holder.edit_qty.getText().toString());
                int newQuantity = oldQuantity + 1;
                holder.edit_qty.setText(String.valueOf(newQuantity));
                catalogMini.getCustomViewModel().setField1(String.valueOf(newQuantity));
                viewChangeListener.onEditQuantity(holder.getAdapterPosition(), newQuantity);
                holder.txt_sub_set.setText(newQuantity + " set");
                ArrayList<CatalogListItem> listnew = (ArrayList<CatalogListItem>) response_catalogMinis.get(holder.getAdapterPosition()).getCustomViewModel().getObjects();
                if (listnew != null) {
                    holder.txt_sub_designs.setText(String.valueOf(((SubProductAdapter2) response_catalogMinis.get(holder.getAdapterPosition()).getCustomViewModel().getField4()).getTotalCatalogItem(listnew.get(0)) + " Pcs."));
                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(mContext)
                        .content("Do you want to remove this catalog ?")
                        .negativeText("No")
                        .positiveText("Yes")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                response_catalogMinis.remove(holder.getAdapterPosition());
                                viewChangeListener.onRemoveView(holder.getAdapterPosition());
                                dialog.dismiss();

                            }
                        })
                        .show();
            }
        });


    }


    public ArrayList<ProductQunatityRate> getAllProducts() {
        ArrayList<ProductQunatityRate> productQunatityRates = new ArrayList<>();
        if(response_catalogMinis != null) {
            for (int i = 0; i < response_catalogMinis.size(); i++) {
                if (response_catalogMinis != null)
                    try {
                        productQunatityRates.addAll(((SubProductAdapter2) response_catalogMinis.get(i).getCustomViewModel().getField4()).getAllProducts());
                    }catch (Exception e){e.printStackTrace();}
            }
        }
        return productQunatityRates;
    }

    public float getCurrentTotal() {
        float total = 0;
        for (int i = 0; i < response_catalogMinis.size(); i++) {
            if (response_catalogMinis.get(i).getCustomViewModel() != null) {
                if ((SubProductAdapter2) response_catalogMinis.get(i).getCustomViewModel().getField4() != null) {
                    total = total + ((SubProductAdapter2) response_catalogMinis.get(i).getCustomViewModel().getField4()).getCurrentTotal();
                } else {

                }
            } else {

            }

        }
        return total;
    }

    @Override
    public int getItemCount() {
        return response_catalogMinis.size();
    }

    private void setExpanded(boolean expanded, final CustomViewHolder holder) {
        if (expanded) {
            holder.arrow_img.setRotation(180);
            holder.recycler_view_subitem.setVisibility(View.VISIBLE);
            holder.txt_expand_txt.setText("Hide Catalog Order Details");
        } else {
            holder.arrow_img.setRotation(0);
            holder.recycler_view_subitem.setVisibility(View.GONE);
            holder.txt_expand_txt.setText("View Catalog Order Details");
        }
    }

    public interface ViewChangeListener {
        void onRemoveView(int position);

        void onEditQuantity(int position, int quantity);

        void onEditPkgType(String pkgtype, int position);
    }

    public void setViewChangeListener(ViewChangeListener viewChangeListener) {
        this.viewChangeListener = viewChangeListener;
    }

    private void setProducts(Response_catalog response_catalog, int quantity, final RecyclerView recyclerView, final int position, final CustomViewHolder holder) {
        productObjs = response_catalog.getProducts().toArray(new ProductObj[response_catalog.getProducts().size()]);
        final boolean isFullCatalog = (response_catalog.getFull_catalog_orders_only().equals("true")) ? true : false;
        final CustomViewModel customViewModel = response_catalogMinis.get(position).getCustomViewModel();
        final ArrayList<ProductObjectQuantity> list1 = new ArrayList<ProductObjectQuantity>();
        boolean isKurtiSet = false;
        int number_design_per_set = 1;
        if (isKurtiSet) {
            // for not kurti set
            for (ProductObj productObj : productObjs) {
                int set_quantity = quantity * number_design_per_set;
                ProductObjectQuantity productObjectQuantity = new ProductObjectQuantity(productObj, set_quantity, Float.parseFloat(productObj.getFinal_price()), isFullCatalog, mContext.getResources().getString(R.string.package_type_naked));
                productObjectQuantity.setSetDesign(number_design_per_set);
                list1.add(productObjectQuantity);
            }
        } else {
            // kurti set
            for (ProductObj productObj : productObjs) {
                list1.add(new ProductObjectQuantity(productObj, quantity, Float.parseFloat(productObj.getFinal_price()), isFullCatalog, mContext.getResources().getString(R.string.package_type_naked)));
            }
        }

        CatalogListItem item = new CatalogListItem(response_catalog.getTitle(), response_catalog.getBrand().getName(), response_catalog.getTotal_products(), list1);
        if (list != null) {
            list = new ArrayList<com.wishbook.catalog.home.orderNew.expandableRecyclerview.CatalogListItem>();
            list.add(0, item);
            customViewModel.setObjects(list);
        }

        SubProductAdapter2 subProductAdapter = null;
        subProductAdapter = new SubProductAdapter2(mContext, list, null, isFullCatalog, position, response_catalogMinis.get(position), new SubProductAdapter2.ProductChangeListener() {
            @Override
            public void onChange(int position) {
                Log.d(TAG, "onChange: position" + position);
                ArrayList<CatalogListItem> listnew = (ArrayList<CatalogListItem>) response_catalogMinis.get(position).getCustomViewModel().getObjects();
                if (listnew != null) {
                    holder.txt_sub_designs.setText(String.valueOf(((SubProductAdapter2) response_catalogMinis.get(position).getCustomViewModel().getField4()).getTotalCatalogItem(listnew.get(0)) + " Pcs."));
                    if (fragment instanceof Fragment_CreatePurchaseOrderVersion2) {
                        ((Fragment_CreatePurchaseOrderVersion2) fragment).changePrice(position);
                    } else if (fragment instanceof Fragment_CreateSaleOrder_Version2) {
                        ((Fragment_CreateSaleOrder_Version2) fragment).changePrice(position);
                    }

                }
            }
        });

        customViewModel.setField4(subProductAdapter);
        Log.e(TAG, "setProducts:  Field Called" + position);
        recyclerView.setAdapter(subProductAdapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        holder.txt_sub_designs.setText(String.valueOf(subProductAdapter.getTotalCatalogItem(list.get(0))) + " Pcs.");
        response_catalogMinis.get(position).setCustomViewModel(customViewModel);


    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.catalog_img)
        SimpleDraweeView simpleDraweeView;

        @BindView(R.id.catalog_name)
        TextView catalog_name;

        @BindView(R.id.full_catalog_note)
        TextView full_catalog_note;

        @BindView(R.id.btn_minus)
        TextView btn_minus;

        @BindView(R.id.edit_qty)
        EditText edit_qty;

        @BindView(R.id.btn_plus)
        TextView btn_plus;

        @BindView(R.id.txt_sub_set)
        TextView txt_sub_set;

        @BindView(R.id.txt_sub_designs)
        TextView txt_sub_designs;

        @BindView(R.id.recycler_view_subitem)
        RecyclerView recycler_view_subitem;


        @BindView(R.id.radiogroup_pkg_type_more)
        RadioGroup radiogroup_pkg_type_more;

        @BindView(R.id.radio_default_boxed)
        RadioButton radio_default_boxed;

        @BindView(R.id.radio_default_naked)
        RadioButton radio_default_naked;

        @BindView(R.id.delete)
        ImageView delete;

        @BindView(R.id.liner_expand)
        LinearLayout liner_expand;

        @BindView(R.id.txt_expand_txt)
        TextView txt_expand_txt;

        @BindView(R.id.arrow_img)
        ImageView arrow_img;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(mContext);
            recycler_view_subitem.setLayoutManager(mLayoutManager1);
            recycler_view_subitem.setHasFixedSize(true);
            recycler_view_subitem.setNestedScrollingEnabled(false);
        }
    }


}
