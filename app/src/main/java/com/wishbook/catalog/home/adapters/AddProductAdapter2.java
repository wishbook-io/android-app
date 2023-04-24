package com.wishbook.catalog.home.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.multipleimageselect.activities.AlbumSelectActivity;
import com.wishbook.catalog.Utils.multipleimageselect.models.Image;
import com.wishbook.catalog.commonmodels.responses.ResponseCategoryEvp;
import com.wishbook.catalog.home.catalog.add.Fragment_AddCatalog_Version2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddProductAdapter2 extends RecyclerView.Adapter<AddProductAdapter2.CustomViewHolder> {


    public static int SINGLE_IMAGE_CHANGE_REQUEST = 3300;
    public static int EDIT_POSITION = 0;
    public static int ADD_MORE_IMAGE_POSITION = 0;


    // ### Variable Init Start ## //
    private ArrayList<Image> itemsInSection;
    public boolean isFullCatalog, isCatalog;
    Context context;
    public String singlePCAddPer, singlePcAddPrice;
    boolean isAddmarginSinglePcPrice, isAddmarginSinglePcPer, isSamePrice;
    Fragment fragment;
    RecyclerView recyclerView;
    ArrayList<ResponseCategoryEvp.Attribute_values> catalog_sizes_selected;

    ChangeProductListener changeProductListener;


    private static String TAG = AddProductAdapter2.class.getSimpleName();


    public AddProductAdapter2(Context context, ArrayList<Image> itemsInSection,
                              boolean isFullCatalog, Fragment fragment,
                              boolean isSamePrice, RecyclerView recyclerView, boolean isCatalog) {
        this.context = context;
        this.itemsInSection = itemsInSection;
        this.isFullCatalog = isFullCatalog;
        this.fragment = fragment;
        this.isSamePrice = isSamePrice;
        this.recyclerView = recyclerView;
        this.isCatalog = isCatalog;
    }

    @Override
    public AddProductAdapter2.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_add_catalog_part8_recycler, parent, false);
        return new AddProductAdapter2.CustomViewHolder(view, new AddProductAdapter2.NameEditTextListener(), new PriceEditTextListener());
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {
        holder.flexbox_image.removeAllViews();
        if (holder.getAdapterPosition() == 0) {
            // Show Add Single price margin

            holder.linear_header.setVisibility(View.VISIBLE);

            initSinglePcListener(holder);

        } else {
            // Hide Add Single price margin
            holder.linear_header.setVisibility(View.GONE);
        }

        holder.nameEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.edit_design_sku.setText(itemsInSection.get(holder.getAdapterPosition()).name);

        holder.priceEditTextListener.updateHolder(holder);
        holder.priceEditTextListener.updatePosition(holder.getAdapterPosition());


        if (isSamePrice) {
            holder.edit_price.setText(itemsInSection.get(0).getPrice());
        } else {
            holder.edit_price.setText("" + itemsInSection.get(holder.getAdapterPosition()).getPrice());
        }

        holder.edit_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.edit_price.requestFocus();
            }
        });

        holder.priceEditTextListener.updatesinglePrice(holder.edit_price.getText().toString());
        if (!isFullCatalog) {
            holder.txt_single_pc_product_price.setVisibility(View.VISIBLE);
            updateSizeUI(catalog_sizes_selected, holder.flex_select_size, itemsInSection.get(position), holder.linear_select_size_container);
        } else {
            holder.txt_single_pc_product_price.setVisibility(View.GONE);
            holder.linear_select_size_container.setVisibility(View.GONE);
        }

        if(itemsInSection.get(position).getPhoto_id()!=null) {
            ImageLoader.getInstance().displayImage(itemsInSection.get(position).path, holder.product_img);
            holder.txt_product_edit_image_label.setVisibility(View.GONE);
            holder.flexbox_image.setVisibility(View.GONE);
        } else {
            ImageLoader.getInstance().displayImage("file://" + itemsInSection.get(position).path, holder.product_img);
            holder.txt_product_edit_image_label.setVisibility(View.VISIBLE);
            holder.flexbox_image.setVisibility(View.VISIBLE);
        }

        if(itemsInSection.get(position).isDisabled) {
            holder.txt_product_status.setVisibility(View.VISIBLE);
        } else {
            holder.txt_product_status.setVisibility(View.GONE);
        }


        holder.product_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemsInSection.get(position).getPhoto_id() == null) {
                    clearFocus();
                    EDIT_POSITION = holder.getAdapterPosition();
                    Intent intent = new Intent(context, AlbumSelectActivity.class);
                    intent.putExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, 1);
                    if (fragment != null) {
                        fragment.startActivityForResult(intent, SINGLE_IMAGE_CHANGE_REQUEST);
                    } else {
                        ((Activity) context).startActivityForResult(intent, SINGLE_IMAGE_CHANGE_REQUEST);
                    }
                }
            }
        });
        holder.img_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                        .content("Are you sure you want to remove?")
                        .positiveText("YES")
                        .negativeText("NO")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                                try {
                                    if(itemsInSection.get(holder.getAdapterPosition()).getPhoto_id()!=null) {
                                        // Call Server Delete
                                        if(changeProductListener!=null) {
                                            changeProductListener.softDelete(holder.getAdapterPosition(),itemsInSection.get(holder.getAdapterPosition()));
                                        }
                                    } else {
                                        // Just Locally Remove
                                        try {
                                            itemsInSection.remove(holder.getAdapterPosition());
                                            if (holder.getAdapterPosition() == 0) {

                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                notifyDataSetChanged();

                            }
                        })
                        .show();

            }
        });


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
                if(limit == 0) {
                    Toast.makeText(context,"Only "+Constants.ADDITIONAL_IMAGE_LIMIT+ " additional images allowed for same design",Toast.LENGTH_SHORT).show();
                } else {
                    intent.putExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, limit);
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
                fragment.startActivityForResult(intent, Application_Singleton.MULTIIMAGE_PRODUCT_REQUEST_CODE);
            }
        });


    }

    public void setSizes(ArrayList<ResponseCategoryEvp.Attribute_values> sizes) {
        catalog_sizes_selected = sizes;
    }

    private void updateSizeUI(final ArrayList<ResponseCategoryEvp.Attribute_values> sizes1, FlexboxLayout root, final Image image, LinearLayout linear_select_size_container) {
        if (sizes1 != null && sizes1.size() > 0) {
            final ArrayList<String> string_size = new ArrayList<>();
            for (int i = 0; i < sizes1.size(); i++) {
                string_size.add(sizes1.get(i).getValue());
            }
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

    @Override
    public int getItemCount() {
        return itemsInSection.size();
    }

    public void setProducts(ArrayList<Image> prods) {
        this.itemsInSection = prods;
    }

    public void changeFullCatalog(boolean isFullCatalog) {
        this.isFullCatalog = isFullCatalog;
    }

    public void isSamePrice(boolean isSamePrice) {
        this.isSamePrice = isSamePrice;
    }

    public interface ChangeProductListener {
        void softDelete(int position,Image oldImage);
    }


    public void setChangeProductListener(ChangeProductListener changeProductListener) {
        this.changeProductListener = changeProductListener;
    }

    public void removeAll() {
        itemsInSection.clear();
        notifyItemRangeRemoved(0, itemsInSection.size());
        notifyDataSetChanged();
    }


    public void notifySinglePc(final int position) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < itemsInSection.size(); i++) {
                    notifyItemChanged(i);
                }
            }
        });

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(position);
            }
        });

    }

    public ArrayList<Image> getProducts() {
        return itemsInSection;
    }


    public List<String> getSelectedSizesDistinctEav() {
        ArrayList<String> catalog_sizes = new ArrayList<>();
        for (int i = 0; i < itemsInSection.size(); i++) {
            if (itemsInSection.get(i).getAvailable_sizes() != null)
                catalog_sizes.addAll(itemsInSection.get(i).getAvailable_sizes());
        }
        Set uniqueValues = new HashSet(catalog_sizes);
        List<String> listFromSet = new ArrayList<String>(uniqueValues);
        return listFromSet;
    }


    private class NameEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            itemsInSection.get(position).name = charSequence.toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private class PriceEditTextListener implements TextWatcher {
        private int position;
        private AddProductAdapter2.CustomViewHolder viewHolder;

        public PriceEditTextListener() {
        }

        public PriceEditTextListener(AddProductAdapter2.CustomViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        public void updateHolder(AddProductAdapter2.CustomViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        public void updatePosition(int position) {
            this.position = position;
        }

        public void updatesinglePrice(String charSequence) {
            try {
                if (charSequence.toString().isEmpty()) {
                    this.viewHolder.txt_single_pc_product_price.setText("Price for single Pc.: " + "\u20B9" + Math.round(0));
                } else {
                    if (!isFullCatalog) {
                        Log.d(TAG, "" + position + "-" + itemsInSection.get(position).getPrice());
                        Log.d(TAG, "" + position + "Single Add Per-> " + singlePCAddPer + "\n Single Add Price" + singlePcAddPrice);
                        if (singlePCAddPer != null) {
                            Log.d(TAG, "Per Add+" + "" + position + "-" + singlePCAddPer);
                            float edit_text_price = 0;
                            if (!charSequence.toString().isEmpty()) {
                                edit_text_price = Float.parseFloat(charSequence.toString());
                            }
                            Float single_price = (edit_text_price / 100) * Float.parseFloat(singlePCAddPer) + edit_text_price;
                            this.viewHolder.txt_single_pc_product_price.setText("Price for single Pc.: " + "\u20B9" + Math.round(single_price));
                        } else if (singlePcAddPrice != null) {
                            Log.d(TAG, "Price Add+" + "" + position + "-" + singlePcAddPrice);
                            float edit_text_price = 0;
                            if (!charSequence.toString().isEmpty()) {
                                edit_text_price = Float.parseFloat(charSequence.toString());
                            }
                            Float single_price = (Float.parseFloat(singlePcAddPrice) + edit_text_price);
                            this.viewHolder.txt_single_pc_product_price.setText("Price for single Pc.: " + "\u20B9" + Math.round(single_price));
                        }
                    } else {
                        this.viewHolder.txt_single_pc_product_price.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            try {
                itemsInSection.get(position).setPrice(charSequence.toString());
                Log.d(TAG, "ADDPRODUCT_PRICECHANE+" + "" + position + "-" + itemsInSection.get(position).getPrice());
                if (position == 0) {
                    notifySinglePc(0);
                    updatesinglePrice(charSequence.toString());
                } else {
                    if (!itemsInSection.get(0).getPrice().equals(itemsInSection.get(position).getPrice())) {
                        if (fragment != null && fragment instanceof Fragment_AddCatalog_Version2) {
                            ((Fragment_AddCatalog_Version2) fragment).checkSamePriceRemove();
                        }
                    }
                    updatesinglePrice(charSequence.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private void initSinglePcListener(final CustomViewHolder viewHolder) {


    }

    public boolean isAddMarginPriceSelected() {
        return isAddmarginSinglePcPrice;
    }

    public boolean isAddMarginPerSelected() {
        return isAddmarginSinglePcPer;
    }


    public boolean checkPriceValidation(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            editText.requestFocus();
            editText.setError(Application_Singleton.getCurrentActivity().getString(R.string.error_empty_price));
            return false;
        } else {
            float productPrice = Float.parseFloat(editText.getText().toString());
            int validatePrice = 100;
            if (isCatalog) {
                Log.e(TAG, "checkPriceValidation: isCatalog");
                validatePrice = 100;
            } else {
                Log.e(TAG, "checkPriceValidation: isNonCatalog");
                validatePrice = 70;
            }
            if (productPrice > validatePrice) {
                Log.e(TAG, "checkPriceValidation: Greater Validate Price");
                return true;
            } else if (productPrice == 0.0) {
                editText.setError(Application_Singleton.getCurrentActivity().getResources().getString(R.string.error_null_price));
                return false;
            } else {
                editText.requestFocus();
                editText.setError("Minimum public price must be greater than " + validatePrice);
                return false;
            }
        }
    }

    public void clearFocus() {
        KeyboardUtils.hideKeyboard((Activity) context);
        View currentFocus = ((Activity) context).getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.product_img)
        ImageView product_img;

        @BindView(R.id.img_remove)
        ImageView img_remove;

        @BindView(R.id.edit_design_sku)
        EditText edit_design_sku;

        @BindView(R.id.edit_price)
        EditText edit_price;


        @BindView(R.id.txt_single_pc_product_price)
        TextView txt_single_pc_product_price;


        @BindView(R.id.linear_single_pc_price_container)
        LinearLayout linear_single_pc_price_container;



        @BindView(R.id.linear_header)
        LinearLayout linear_header;


        @BindView(R.id.linear_select_size_container)
        LinearLayout linear_select_size_container;

        @BindView(R.id.flex_select_size)
        FlexboxLayout flex_select_size;


        @BindView(R.id.flexbox_image)
        FlexboxLayout flexbox_image;

        @BindView(R.id.attach_button)
        AppCompatButton attach_button;

        @BindView(R.id.btn_first_additional_image)
        TextView btn_first_additional_image;

        @BindView(R.id.txt_product_edit_image_label)
        TextView txt_product_edit_image_label;

        @BindView(R.id.txt_product_status)
        TextView txt_product_status;


        public PriceEditTextListener priceEditTextListener;
        public NameEditTextListener nameEditTextListener;

        public CustomViewHolder(View view, NameEditTextListener nameEditTextListener, PriceEditTextListener priceEditTextListener) {
            super(view);
            ButterKnife.bind(this, view);

            this.nameEditTextListener = nameEditTextListener;
            edit_design_sku.addTextChangedListener(nameEditTextListener);


            this.priceEditTextListener = priceEditTextListener;
            edit_price.addTextChangedListener(priceEditTextListener);

            btn_first_additional_image.setPaintFlags(btn_first_additional_image.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
    }
}
