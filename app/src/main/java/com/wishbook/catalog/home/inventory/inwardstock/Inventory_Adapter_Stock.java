package com.wishbook.catalog.home.inventory.inwardstock;

import android.app.Activity;
import android.content.Context;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.postpatchmodels.InventoryAddStock;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.inventory.BrandsModel;
import com.wishbook.catalog.home.inventory.CatalogsModel;
import com.wishbook.catalog.home.models.ProductObj;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 21/11/16.
 */
public class Inventory_Adapter_Stock extends RecyclerView.Adapter<Inventory_Adapter_Stock.MyViewHolder> {

    private Context mActivity;
    private ArrayList<BrandsModel> brandsModels = new ArrayList<>();
    private ArrayList<CatalogsModel> catalogsModels = new ArrayList<>();
    private ArrayList<InventoryAddStock> inventoryAdds;
    private InventoryAddStock[] inventoryAdd_array;
    private Boolean AllInventory = false;
    private Fragment_Inward_Stock fragment_inward_stock;


    public Inventory_Adapter_Stock(Context mActivity, ArrayList<InventoryAddStock> inventoryAdds, ArrayList<CatalogsModel> catalogsModels, ArrayList<BrandsModel> brandsModels, Boolean allInventory, Fragment_Inward_Stock fragment_inward_stock) {
        this.mActivity = mActivity;
        this.inventoryAdds = inventoryAdds;
        this.brandsModels = brandsModels;
        this.catalogsModels = catalogsModels;
        this.AllInventory = allInventory;
        this.fragment_inward_stock = fragment_inward_stock;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView product_title,in_stock;
        EditText quantity;
        ImageView delete;
        AppCompatButton add;

        public MyViewHolder(View view) {
            super(view);
            product_title = (TextView) view.findViewById(R.id.product_title);
            in_stock = (TextView) view.findViewById(R.id.in_stock);
            quantity = (EditText) view.findViewById(R.id.quantity);
            delete = (ImageView) view.findViewById(R.id.delete);
            add = (AppCompatButton) view.findViewById(R.id.add);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inventory_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        if (AllInventory) {
           holder.delete.setVisibility(View.GONE);
            holder.add.setVisibility(View.VISIBLE);
            holder.in_stock.setVisibility(View.VISIBLE);
            holder.in_stock.setText("In Stock : " + inventoryAdds.get(position).getQuantity());
            holder.quantity.setText(1+"");
        } else {
            holder.quantity.setText(inventoryAdds.get(position).getQuantity()+"");
        }


        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAddProduct(inventoryAdds.get(holder.getAdapterPosition()).getProduct(), mActivity, fragment_inward_stock, fragment_inward_stock.getBrandsModels(), Integer.parseInt(holder.quantity.getText().toString()),Fragment_Inward_Stock.INVENTORY_TYPE,catalogsModels);
            }
        });

        holder.product_title.setText(inventoryAdds.get(position).getProduct_title());
                /*Type type = new TypeToken<ArrayList<InventoryAdd>>(){}.getType();
                Gson gson = new Gson();
                String json = Activity_Home.pref.getString("inventoryAddStock", "na");
                inventoryAdds= gson.fromJson(json, type);*/
        holder.quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals("") && !editable.toString().equals("0")) {
                    for (int i = 0; i < brandsModels.size(); i++) {
                        for (int j = 0; j < brandsModels.get(i).getCatalogs().size(); j++) {
                            for (int k = 0; k < brandsModels.get(i).getCatalogs().get(j).getProducts().size(); k++) {
                                if (brandsModels.get(i).getCatalogs().get(j).getProducts().get(k).getProduct().equals(inventoryAdds.get(holder.getAdapterPosition()).getProduct())) {
                                    brandsModels.get(i).getCatalogs().get(j).getProducts().get(k).setQuantity(Integer.parseInt(holder.quantity.getText().toString()));
                                    Fragment_Inward_Stock.addToLocal(brandsModels);
                                    Toast.makeText(mActivity, "Product Quantity Changed", Toast.LENGTH_SHORT).show();

                                    break;
                                }
                            }
                        }
                    }
                }


                            /*inventoryAdds.get(position).setQuantity(holder.quantity.getText().toString());
                            inventoryAdd_array = inventoryAdds.toArray(new InventoryAddStock[inventoryAdds.size()]);
                            Gson gson = new Gson();
                            String json = gson.toJson(inventoryAdd_array);
                            Activity_Home.pref.edit().putString("inventoryAddStock", json).commit();*/
            }
        });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                       /* if(inventoryAdds.size()>1)
                        {
                            inventoryAdds.remove(position);
                            inventoryAdd_array = inventoryAdds.toArray(new InventoryAddStock[inventoryAdds.size()]);
                            Gson gson = new Gson();
                            String json = gson.toJson(inventoryAdd_array);
                            Activity_Home.pref.edit().putString("inventoryAddStock", json).commit();
                            notifyDataSetChanged();
                        }
                        else
                        {
                            inventoryAdds.remove(position);
                            inventoryAdd_array = inventoryAdds.toArray(new InventoryAddStock[inventoryAdds.size()]);
                            Gson gson = new Gson();
                            String json = gson.toJson(inventoryAdd_array);
                            Activity_Home.pref.edit().putString("inventoryAddStock", "na").commit();
                            notifyDataSetChanged();
                            Fragment_Inward_Stock.bottom_container.setVisibility(View.GONE);
                            Fragment_Inward_Stock.middle_container.setVisibility(View.VISIBLE);
                        }*/
                Toast.makeText(mActivity, "Product Deleted", Toast.LENGTH_SHORT).show();


                Boolean done = false;
                for (int i = 0; i < brandsModels.size(); i++) {

                    for (int j = 0; j < brandsModels.get(i).getCatalogs().size(); j++) {

                        if (done == false) {
                            for (int k = 0; k < brandsModels.get(i).getCatalogs().get(j).getProducts().size(); k++) {


                                if (brandsModels.get(i).getCatalogs().get(j).getProducts().get(k).getProduct().equals(inventoryAdds.get(holder.getAdapterPosition()).getProduct())) {
                                    if (brandsModels.get(i).getCatalogs().get(j).getProducts().size() > 1) {
                                        brandsModels.get(i).getCatalogs().get(j).getProducts().remove(k);
                                        Fragment_Inward_Stock.addToLocal(brandsModels);
                                        done = true;
                                        notifyDataSetChanged();
                                        break;
                                    } else {
                                        brandsModels.get(i).getCatalogs().get(j).getProducts().remove(k);
                                        brandsModels.get(i).getCatalogs().remove(j);
                                        brandsModels.get(i).setExpanded(false);
                                               /* if (brandsModels.get(i).getCatalogs().size() > 0) {
                                                }
                                                else
                                                {
                                                    brandsModels.remove(i);
                                                    notifyDataSetChanged();
                                                    Fragment_Inward_Stock.addToLocal(brandsModels);
                                                }*/
                                        Fragment_Inward_Stock.addToLocal(brandsModels);
                                        done = true;
                                        notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (brandsModels.get(i).getCatalogs().size() > 0) {

                    } else {
                        brandsModels.remove(i);
                        Fragment_Inward_Stock.addToLocal(brandsModels);
                        notifyDataSetChanged();
                        if (brandsModels.size() < 1) {
                            Activity_Home.pref.edit().putString(Fragment_Inward_Stock.LOCAL_INVENTORY_JSON, "na").commit();
                            Fragment_Inward_Stock.bottom_container.setVisibility(View.GONE);
                            Fragment_Inward_Stock.btn_save.setVisibility(View.GONE);
                        }
                        break;

                    }


                }


            }
        });

    }

    private void getAddProduct(final String product, Context context, final Fragment_Inward_Stock fragment_inward_stock, final ArrayList<BrandsModel> brandsModels, final int quantity, final String inventoryType, final ArrayList<CatalogsModel> catalogsModels) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context, "products_details", product), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    Log.v("sync response ", response);
                    ProductObj resProductObj = Application_Singleton.gson.fromJson(response, ProductObj.class);
                    fragment_inward_stock.processResultForExpandableView(brandsModels,resProductObj,quantity);
                    fragment_inward_stock.notifyBrandsChange();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return inventoryAdds.size();
    }


}
