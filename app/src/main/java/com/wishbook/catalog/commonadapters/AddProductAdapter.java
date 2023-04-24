package com.wishbook.catalog.commonadapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.multipleimageselect.activities.AlbumSelectActivity;
import com.wishbook.catalog.Utils.multipleimageselect.models.Image;
import com.wishbook.catalog.home.catalog.add.Fragment_AddProduct;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by vignesh_streamoid on 14/05/16.
 */
public class AddProductAdapter extends RecyclerView.Adapter<AddProductAdapter.ViewHolder> {

//todo users: on tap of whole item
//buyer approved- supplier approved
//load wishbook contacts in background
// save state

    private final int height;
    private final int width;
    private final AppCompatActivity context;
    private ArrayList<Image> itemsInSection;
    public final boolean fullcatalog;
    private boolean onBind = true;
    boolean productsWS = false;
    boolean productsWP = false;
    String singlePCAddPer, singlePcAddPrice;
    ArrayList<String> system_sizes;
    public static int ADD_MORE_IMAGE_POSITION = 0;

    Fragment fragment;

    public AddProductAdapter(AppCompatActivity context, ArrayList<Image> itemsInSection, boolean fullcatalog, Boolean productsWS, Boolean productsWP, String singlePCAddPer, String singlePcAddPrice) {
        this.context = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
        this.itemsInSection = itemsInSection;
        this.fullcatalog = fullcatalog;
        this.productsWS = productsWS;
        this.productsWP = productsWP;
        this.singlePCAddPer = singlePCAddPer;
        this.singlePcAddPrice = singlePcAddPrice;
        system_sizes = new ArrayList<>();
    }


    @Override
    public AddProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.addpro, parent, false);
        ViewHolder vh = new ViewHolder(v, new NameEditTextListener(), new PriceEditTextListener(), new PublicPriceEditTextListener());
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //Do not show if productsWithoutPrice
        holder.flexbox_image.removeAllViews();

        if (productsWP) {
            holder.input_price.setVisibility(View.GONE);
            holder.public_price_container.setVisibility(View.GONE);
        } else {
            holder.input_price.setVisibility(View.VISIBLE);

            if (Fragment_AddProduct.view_permission.equals("public")) {
                holder.public_price_container.setVisibility(View.GONE);
            } else {
                holder.public_price_container.setVisibility(View.GONE);
            }
        }

        //Do not show if productsWithoutSKU
        if (productsWS) {
            holder.input_sku.setVisibility(View.GONE);
            holder.txt_input_sku.setVisibility(View.GONE);
        } else {
            holder.input_sku.setVisibility(View.VISIBLE);
            holder.txt_input_sku.setVisibility(View.VISIBLE);
        }


        if (!Fragment_AddProduct.catalog_price.equals("0") && !Fragment_AddProduct.catalog_price.equals("0.00") && !Fragment_AddProduct.catalog_price.equals("") && itemsInSection.get(position).price.toString().equals("0")) {
            itemsInSection.get(position).price = Fragment_AddProduct.catalog_price.toString();
        }


        if (!Fragment_AddProduct.public_price.equals("0") && !Fragment_AddProduct.public_price.equals("0.00") && !Fragment_AddProduct.public_price.equals("") && itemsInSection.get(position).public_price.toString().equals("0")) {
            itemsInSection.get(position).public_price = Fragment_AddProduct.public_price.toString();
        }


        // update nameEditTextListener every time we bind a new item
        // so that it knows what item in itemsInSection to update
        holder.nameEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.input_sku.setText(itemsInSection.get(holder.getAdapterPosition()).name);

        holder.priceEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.priceEditTextListener.updateHolder(holder);
        holder.input_price.setText(itemsInSection.get(holder.getAdapterPosition()).price);

        if (Fragment_AddProduct.view_permission.equals("public")) {
            holder.publicpriceEditTextListener.updatePosition(holder.getAdapterPosition());
            holder.public_price.setText(itemsInSection.get(holder.getAdapterPosition()).public_price);
        }
        ImageLoader.getInstance().displayImage("file://" + itemsInSection.get(position).path, holder.All_item_img);
        holder.pod_rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemsInSection.remove(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
        if(system_sizes.size()>0) {
            updateSizeUI(system_sizes,holder.flex_select_size,itemsInSection.get(position),holder.linear_select_size_container);
        }

        if (itemsInSection.get(position).getMore_images() != null && itemsInSection.get(position).getMore_images().size() > 0) {
            holder.flexbox_image.addView(holder.attach_button);
            for (int i = 0; i < itemsInSection.get(position).getMore_images().size(); i++) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.item_image, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(params);
                if (itemsInSection.get(position).getMore_images().get(i).getPath() != null) {
                    StaticFunctions.loadFresco(context, "file://" + itemsInSection.get(position).getMore_images().get(i).getPath(), (SimpleDraweeView) view.findViewById(R.id.img_set));
                } else if (itemsInSection.get(position).getMore_images().get(i).getThumbnail_small() != null) {
                    StaticFunctions.loadFresco(context, itemsInSection.get(position).getMore_images().get(i).getThumbnail_small(), (SimpleDraweeView) view.findViewById(R.id.img_set));
                }

                ImageView img_remove_set = view.findViewById(R.id.img_remove_set);
                final int finalI = i;
                img_remove_set.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                .title("Delete Image")
                                .content("Are you sure you want to delete this image?")
                                .positiveText("OK")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        dialog.dismiss();
                                        ArrayList<Image> temp = itemsInSection.get(holder.getAdapterPosition()).getMore_images();
                                        temp.remove(finalI);
                                        itemsInSection.get(holder.getAdapterPosition()).setMore_images(temp);
                                        notifyItemChanged(holder.getAdapterPosition());
                                    }
                                })
                                .negativeText("Cancel")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();

                    }
                });
                holder.flexbox_image.addView(view);
            }
        } else {
            holder.flexbox_image.addView(holder.btn_first_additional_image);
        }

        holder.attach_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFocus();
                ADD_MORE_IMAGE_POSITION = holder.getAdapterPosition();
                Intent intent = new Intent(context, AlbumSelectActivity.class);
                int limit = Constants.ADDITIONAL_IMAGE_LIMIT;
                if (itemsInSection.get(position).getMore_images() != null) {
                    limit = Constants.ADDITIONAL_IMAGE_LIMIT - itemsInSection.get(position).getMore_images().size();
                }
                intent.putExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, limit);

                if(limit == 0) {
                    Toast.makeText(context,"Only "+Constants.ADDITIONAL_IMAGE_LIMIT+ " additional images allowed",Toast.LENGTH_SHORT).show();
                } else {
                    if(fragment!=null)
                        fragment.startActivityForResult(intent, Application_Singleton.MULTIIMAGE_PRODUCT_REQUEST_CODE);
                }

            }
        });

        holder.btn_first_additional_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFocus();
                ADD_MORE_IMAGE_POSITION = holder.getAdapterPosition();
                Intent intent = new Intent(context, AlbumSelectActivity.class);
                intent.putExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, Constants.ADDITIONAL_IMAGE_LIMIT);
                if(fragment!=null)
                    fragment.startActivityForResult(intent, Application_Singleton.MULTIIMAGE_PRODUCT_REQUEST_CODE);
            }
        });
    }

    public ArrayList<Image> getProducts() {

       /* for (int i = 0; i < itemsInSection.size(); i++) {
            Log.d("ADDPRODUCT_NAME", itemsInSection.get(i).getName());
            Log.d("ADDPRODUCT_PRICE", itemsInSection.get(i).getPrice());
        }
*/
        return itemsInSection;

    }

    public void clearFocus() {
        KeyboardUtils.hideKeyboard((Activity) context);
        View currentFocus = ((Activity) context).getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setSizes(ArrayList<String> sizes) {
        system_sizes = sizes;
    }


    @Override
    public int getItemCount() {
        return itemsInSection.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView All_item_img;
        private final ImageView pod_rem;
        // each data item is just a string in this case
        public EditText input_sku;
        public EditText input_price;
        public EditText public_price;
        public LinearLayout public_price_container;
        public NameEditTextListener nameEditTextListener;
        public PriceEditTextListener priceEditTextListener;
        public PublicPriceEditTextListener publicpriceEditTextListener;
        public LinearLayout linear_single_price_container;
        public TextView txt_single_pc_price;
        public TextInputLayout txt_input_sku;
        LinearLayout linear_select_size_container;
        FlexboxLayout flex_select_size;

        FlexboxLayout flexbox_image;

        AppCompatButton attach_button;

        TextView btn_first_additional_image;



        public ViewHolder(View v, NameEditTextListener nameEditTextListener, PriceEditTextListener priceEditTextListener, PublicPriceEditTextListener publicpriceEditTextListener) {
            super(v);


            this.All_item_img = (ImageView) itemView.findViewById(R.id.prod_img);
            this.pod_rem = (ImageView) itemView.findViewById(R.id.pod_rem);
            this.input_sku = (EditText) v.findViewById(R.id.input_sku);
            this.txt_input_sku = v.findViewById(R.id.sku);
            this.input_price = (EditText) v.findViewById(R.id.input_price);
            this.public_price = (EditText) v.findViewById(R.id.public_price);
            this.linear_single_price_container = v.findViewById(R.id.linear_single_price_container);
            this.txt_single_pc_price = v.findViewById(R.id.txt_single_pc_price);
            this.linear_select_size_container = v.findViewById(R.id.linear_select_size_container);
            this.flex_select_size = v.findViewById(R.id.flex_select_size);

            this.flexbox_image = v.findViewById(R.id.flexbox_image);
            this.attach_button = v.findViewById(R.id.attach_button);
            this.btn_first_additional_image = v.findViewById(R.id.btn_first_additional_image);

            this.public_price_container = (LinearLayout) v.findViewById(R.id.public_price_container);
            this.nameEditTextListener = nameEditTextListener;
            this.input_sku.addTextChangedListener(nameEditTextListener);

            this.priceEditTextListener = priceEditTextListener;
            this.input_price.addTextChangedListener(priceEditTextListener);


            this.publicpriceEditTextListener = publicpriceEditTextListener;
            this.public_price.addTextChangedListener(publicpriceEditTextListener);


        }
    }

    public void setProducts(ArrayList<Image> prods) {
        this.itemsInSection = prods;
    }


    public void removeAll() {
        itemsInSection.clear();
        notifyItemRangeRemoved(0, itemsInSection.size());
        notifyDataSetChanged();
    }


    // we make TextWatcher to be aware of the position it currently works with
    // this way, once a new item is attached in onBindViewHolder, it will
    // update current position MyCustomEditTextListener, reference to which is kept by ViewHolder
    private class NameEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            itemsInSection.get(position).name = charSequence.toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

    // we make TextWatcher to be aware of the position it currently works with
    // this way, once a new item is attached in onBindViewHolder, it will
    // update current position MyCustomEditTextListener, reference to which is kept by ViewHolder
    private class PriceEditTextListener implements TextWatcher {
        private int position;
        private ViewHolder viewHolder;

        public PriceEditTextListener() {
        }

        public PriceEditTextListener(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        public void updatePosition(int position) {
            this.position = position;
        }

        public void updateHolder(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            itemsInSection.get(position).setPrice(charSequence.toString());
            Log.d("ADDPRODUCT_PRICECHANE+", "" + position + "-" + itemsInSection.get(position).getPrice());

            /**
             * Hide Single Pc. (Managed by server changes Dec-27 2018)
             */

            this.viewHolder.txt_single_pc_price.setVisibility(View.GONE);
           /* if(charSequence.toString().isEmpty()){
                this.viewHolder.txt_single_pc_price.setText("\u20B9" + 0 + "/Pc.");
            } else {
                if (!fullcatalog) {
                    this.viewHolder.linear_single_price_container.setVisibility(View.VISIBLE);
                    if (singlePCAddPer != null) {
                        Float single_price = ((Float.parseFloat(charSequence.toString()) / 100) * Float.parseFloat(singlePCAddPer)) + Float.parseFloat(charSequence.toString());
                        this.viewHolder.txt_single_pc_price.setText("\u20B9" + Math.round(single_price) + "/Pc.");
                    } else if (singlePcAddPrice != null) {
                        Float single_price = (Float.parseFloat(singlePcAddPrice) + Float.parseFloat(charSequence.toString()));
                        this.viewHolder.txt_single_pc_price.setText("\u20B9" + Math.round(single_price) + "/Pc.");
                    }
                }
            }*/

        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

    private class PublicPriceEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            itemsInSection.get(position).public_price = charSequence.toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

    public void showDesignNumber(boolean productsWS) {
        this.productsWS = productsWS;
        notifyDataSetChanged();
    }

    private void updateSizeUI(final ArrayList<String> string_size, FlexboxLayout root, final Image image, LinearLayout linear_select_size_container) {
        if (string_size != null && string_size.size() > 0) {
            linear_select_size_container.setVisibility(View.VISIBLE);
            root.removeAllViews();
            for (int j = 0; j < string_size.size(); j++) {
                final CheckBox checkBox = new CheckBox(context);
                checkBox.setId(j);
                FlexboxLayout.LayoutParams check_lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
                check_lp.setMargins(0, 0, 32, 12);
                setCheckBoxColor(checkBox, R.color.color_primary, R.color.purchase_medium_gray);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    checkBox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                }
                checkBox.setText(string_size.get(j));
                checkBox.setTextColor(ContextCompat.getColor(context, R.color.purchase_dark_gray));
                checkBox.setPadding(0, 0, 20, 0);
                root.addView(checkBox, check_lp);
                final int finalJ = j;

                if (image.getAvailable_sizes() != null) {
                    ArrayList<String> temp_size;
                    temp_size = image.getAvailable_sizes();
                    if (temp_size.contains(string_size.get(finalJ))) {
                        checkBox.setChecked(true);
                    }
                }
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked) {
                            ArrayList<String> temp_size;
                            temp_size = image.getAvailable_sizes();
                            if (temp_size == null) {
                                temp_size = new ArrayList<>();
                            }
                            temp_size.add(string_size.get(finalJ));
                            image.setAvailable_sizes(temp_size);
                        } else {
                            ArrayList<String> temp_size;
                            temp_size = image.getAvailable_sizes();
                            if (temp_size != null) {
                                temp_size.remove(string_size.get(finalJ));
                                image.setAvailable_sizes(temp_size);
                            }
                        }
                    }
                });

                checkBox.setLayoutParams(check_lp);
            }
        } else {
            linear_select_size_container.setVisibility(View.GONE);
            root.removeAllViews();
            image.setAvailable_sizes(null);
        }


    }

    public void setCheckBoxColor(CheckBox checkBox, int checkedColor, int uncheckedColor) {
        checkBox.setButtonDrawable(ContextCompat.getDrawable(context, R.drawable.checkbox_selector));
    }


}