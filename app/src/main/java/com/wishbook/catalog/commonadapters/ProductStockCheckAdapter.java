package com.wishbook.catalog.commonadapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductStockCheckAdapter extends RecyclerView.Adapter<ProductStockCheckAdapter.ViewHolder> {


    DialogFragment fragment;
    Context context;
    private List<Response_catalogMini> responseCatalogMinis;


    public ProductStockCheckAdapter(List<Response_catalogMini> data, Context context, DialogFragment fragment) {
        this.responseCatalogMinis = data;
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public ProductStockCheckAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.dialog_product_stock_item, parent, false);
        return new ProductStockCheckAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Response_catalogMini data = responseCatalogMinis.get(position);
        if (position == 0) {
            holder.txt_header.setVisibility(View.VISIBLE);
        } else {
            holder.txt_header.setVisibility(View.GONE);
        }

        holder.txt_catalog_name.setText(data.getTitle());
        holder.txt_brand_name.setText(data.getBrand_name());
        if (data.getSell_full_catalog().equalsIgnoreCase("true")) {
            holder.txt_only_full_catalog.setText("Full catalog only");
        } else {
            holder.txt_only_full_catalog.setText("Single piece available");
        }


        StaticFunctions.loadFresco(context, data.getImage().getThumbnail_small(), holder.item_img);


        holder.btn_keep_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> keep_data = null;
                if (PrefDatabaseUtils.getPrefKeepSellingStock(context) != null) {
                    keep_data = new Gson().fromJson(PrefDatabaseUtils.getPrefKeepSellingStock(context), new TypeToken<HashMap<String, String>>() {
                    }.getType());
                } else {
                    keep_data = new HashMap<>();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                keep_data.put(data.getId(), sdf.format(new Date()));
                PrefDatabaseUtils.setPrefKeepSellingStock(context, new Gson().toJson(keep_data));
                try {
                    responseCatalogMinis.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(position, getItemCount());
                    if (responseCatalogMinis.size() == 0) {
                        fragment.dismiss();
                    }
                } catch (Exception e) {
                    fragment.dismiss();
                    e.printStackTrace();
                }


            }
        });

        holder.btn_stop_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    HashMap<String, String> keep_data = null;
                    if (PrefDatabaseUtils.getPrefKeepSellingStock(context) != null) {
                        keep_data = new Gson().fromJson(PrefDatabaseUtils.getPrefKeepSellingStock(context), new TypeToken<HashMap<String, String>>() {
                        }.getType());
                    } else {
                        keep_data = new HashMap<>();
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    keep_data.put(data.getId(), sdf.format(new Date()));
                    PrefDatabaseUtils.setPrefKeepSellingStock(context, new Gson().toJson(keep_data));
                    stopSelling(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return responseCatalogMinis.size();
    }

    public void stopSelling(final int position) {
        Fragment_CatalogsGallery fragment_catalogsGallery = new Fragment_CatalogsGallery();
        final Response_catalog response_catalog = new Response_catalog();
        response_catalog.setId(responseCatalogMinis.get(position).getId());
        String label = "";
        label = Fragment_CatalogsGallery.DISABLE_LABEL;
        fragment_catalogsGallery.setUpdateListEnableDisable(new Fragment_CatalogsGallery.updateListEnableDisable() {
            @Override
            public void successEnableDisable(boolean isEnable) {
                try {
                    responseCatalogMinis.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                    if (responseCatalogMinis.size() == 0) {
                        fragment.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        fragment_catalogsGallery.getCatalogDataBeforeEnableDisable(false, (Activity) context, response_catalog, label, Fragment_Catalogs.class.getSimpleName());
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_header)
        TextView txt_header;

        @BindView(R.id.item_img)
        SimpleDraweeView item_img;

        @BindView(R.id.txt_catalog_name)
        TextView txt_catalog_name;

        @BindView(R.id.txt_brand_name)
        TextView txt_brand_name;

        @BindView(R.id.txt_only_full_catalog)
        TextView txt_only_full_catalog;

        @BindView(R.id.btn_keep_sell)
        TextView btn_keep_sell;

        @BindView(R.id.btn_stop_sell)
        TextView btn_stop_sell;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}


