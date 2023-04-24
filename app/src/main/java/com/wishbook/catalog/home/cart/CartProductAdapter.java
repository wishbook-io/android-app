package com.wishbook.catalog.home.cart;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.CartCatalogModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.CartProductHolder> {
    private AppCompatActivity context;
    private ArrayList<CartCatalogModel.Products> productList;
    private String catalog_type;
    private ProductChangeListener productChangeListener;

    CartProductAdapter(@NonNull ArrayList<CartCatalogModel.Products> data, @NonNull AppCompatActivity context, int position, @NonNull String catalog_type) {
        this.productList = data;
        this.catalog_type = catalog_type;
        this.context = context;
    }

    @NonNull
    @Override
    public CartProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mycart_product, parent, false);
        return new CartProductHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartProductHolder holder, int position) {
        try {
            if (catalog_type.contains("Full")) {
                holder.btn_minus.setVisibility(View.GONE);
                holder.btn_plus.setVisibility(View.GONE);
            }
            StaticFunctions.loadFresco(context, productList.get(position).getProduct_image(), holder.product_img);
            if (productList.get(position).getProduct_sku() != null && !productList.get(position).getProduct_sku().equals("null") && productList.get(position).getProduct_sku().length() > 10) {
                String sku = productList.get(position).getProduct_sku();
                sku = sku.substring(0, 3) + "..." + sku.substring(sku.length() - 4);
                holder.cart_product_sku.setText(sku);
            } else if (productList.get(position).getProduct_sku() != null && !productList.get(position).getProduct_sku().equals("null") && productList.get(position).getProduct_sku().length() > 0) {
                holder.cart_product_sku.setText(productList.get(position).getProduct_sku());
            } else {
                holder.cart_product_sku.setText("-");
            }
            holder.cart_product_priceperpc.setText("\u20B9 " + productList.get(position).getRate() + " x " + productList.get(position).getCount() + " Pc.");
            double price = Double.parseDouble(productList.get(position).getRate()) * productList.get(position).getCount();
            holder.cart_product_price.setText("\u20B9 " + price);
            holder.edit_qty.setText(String.valueOf(productList.get(position).getCount()));

            initListener(holder, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void initListener(final @NonNull CartProductHolder holder, final int position) {
        holder.btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(holder.edit_qty.getText().toString());
                holder.edit_qty.setText("" + (++qty));
                productList.get(position).setCount(qty);
                double price = Double.parseDouble(productList.get(position).getRate()) * productList.get(position).getCount();
                holder.cart_product_price.setText("\u20B9 " + price);
                // cart_adapter.changeSubPrice(cart_position);
                if (productChangeListener != null) {
                    productChangeListener.onChange(position);
                }
            }
        });
        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(holder.edit_qty.getText().toString());
                if (qty != 0) {
                    productList.get(position).setCount(qty);
                    holder.edit_qty.setText("" + (--qty));
                    productList.get(position).setCount(qty);
                    double price = Double.parseDouble(productList.get(position).getRate()) * productList.get(position).getCount();
                    holder.cart_product_price.setText("\u20B9 " + price);
                    // cart_adapter.changeSubPrice(cart_position);
                    if (productChangeListener != null) {
                        productChangeListener.onChange(position);
                    }
                }
            }
        });

        holder.product_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity) context).startActivity(new Intent((AppCompatActivity) context, CartProductView.class)
                        .putExtra("id", productList.get(position).getProduct_id()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    void setProductQTY(int qty) {
        for (int i = 0; i < productList.size(); i++) {
            productList.get(i).setCount(qty);
            productList.get(i).setQuantity(qty);
        }
        // getTotalPrice();
        notifyDataSetChanged();
    }

    double getTotalPrice() {
        double total = 0;
        for (int i = 0; i < productList.size(); i++) {
            try {
                total = total + Double.parseDouble(productList.get(i).getRate()) * productList.get(i).getCount();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return total;
    }



    double getTotalPrice2() {
        double total = 0;
        for (int i = 0; i < productList.size(); i++) {
            try {

                total = total + Double.parseDouble(productList.get(i).getRate());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return total;

    }

    void setProductChangeListener(@NonNull ProductChangeListener productChangeListener) {
        this.productChangeListener = productChangeListener;
    }

    public interface ProductChangeListener {
        void onChange(int position);
    }

    class CartProductHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cart_product_sku)
        TextView cart_product_sku;

        @BindView(R.id.cart_product_priceperpc)
        TextView cart_product_priceperpc;

        @BindView(R.id.cart_product_price)
        TextView cart_product_price;

        @BindView(R.id.btn_minus)
        TextView btn_minus;

        @BindView(R.id.btn_plus)
        TextView btn_plus;

        @BindView(R.id.edit_qty)
        EditText edit_qty;

        @BindView(R.id.product_img)
        SimpleDraweeView product_img;

        private CartProductHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }


}


