package com.wishbook.catalog.home.catalog.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.multipleimageselect.activities.AlbumSelectActivity;
import com.wishbook.catalog.Utils.multipleimageselect.models.Image;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.ScreenSetModel;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddScreenSetAdapter extends RecyclerView.Adapter<AddScreenSetAdapter.CustomViewHolder> {

    public static int EDIT_POSITION = 0;

    // ### Variable Init Start ## //
    Context context;
    ArrayList<ScreenSetModel> screenSetModels;
    Fragment fragment;
    RecyclerView recyclerView;


    private static String TAG = AddScreenSetAdapter.class.getSimpleName();


    public AddScreenSetAdapter(Context context, ArrayList<ScreenSetModel> screenSetModels, RecyclerView recyclerView, Fragment fragment) {
        this.context = context;
        this.screenSetModels = screenSetModels;
        this.fragment = fragment;
        this.recyclerView = recyclerView;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_screen_set, parent, false);
        return new AddScreenSetAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder,  int position) {
        final ScreenSetModel setModel = screenSetModels.get(position);
        holder.txt_set_number.setText("Set " + (position + 1) + " Detail");
        holder.flexbox_image.removeAllViews();
        holder.edit_sku.setTag(holder.getAdapterPosition());
        holder.edit_colours.setTag(holder.getAdapterPosition());
        holder.edit_product_expire_days.setTag(holder.getAdapterPosition());

        if (setModel.getScreen_name() != null && !setModel.getScreen_name().isEmpty()) {
            holder.edit_sku.setText(setModel.getScreen_name());
            setModel.setScreen_name(setModel.getScreen_name());
        } else {
            holder.edit_sku.setText("");
        }

        if (setModel.getColor_name() != null && !setModel.getColor_name().isEmpty()) {
            holder.edit_colours.setText(setModel.getColor_name());
            setModel.setColor_name(setModel.getColor_name());
        } else {
            holder.edit_colours.setText("");
        }


        if(setModel.getExpiry_date()!=null && !setModel.getExpiry_date().isEmpty()) {
            holder.edit_product_expire_days.setText(setModel.getExpiry_date());
            setModel.setExpiry_date(setModel.getExpiry_date());
        } else {
            holder.edit_product_expire_days.setText("30");
        }


        if (setModel.isColor_set_type()) {
            holder.txt_input_colours.setVisibility(View.VISIBLE);
            holder.edit_colours.setVisibility(View.VISIBLE);
            holder.txt_input_colours.setHint("Enter Color");
        } else {
            holder.txt_input_colours.setVisibility(View.GONE);
            holder.edit_colours.setVisibility(View.GONE);
            holder.txt_input_colours.setHint("Enter Size");
        }



        if(holder.getAdapterPosition() > 0) {
            holder.img_delete.setVisibility(View.VISIBLE);
            holder.img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                            .title("Delete Product")
                            .content("Are you sure you want to delete this product?")
                            .positiveText("OK")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                    screenSetModels.remove(holder.getAdapterPosition());
                                    notifyItemRemoved(holder.getAdapterPosition());
                                    notifyItemRangeChanged(holder.getAdapterPosition(),getItemCount());
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
        } else {
            holder.img_delete.setVisibility(View.GONE);
        }

        if (setModel.getImages() != null && setModel.getImages().size() > 0) {
            holder.flexbox_image.addView(holder.attach_button);
            for (int i = 0; i < setModel.getImages().size(); i++) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.item_image, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(params);
                if (setModel.getImages().get(i).getPath() != null) {
                    StaticFunctions.loadFresco(context, "file://" + setModel.getImages().get(i).getPath(), (SimpleDraweeView) view.findViewById(R.id.img_set));
                } else if (setModel.getImages().get(i).getThumbnail_small() != null) {
                    StaticFunctions.loadFresco(context, setModel.getImages().get(i).getThumbnail_small(), (SimpleDraweeView) view.findViewById(R.id.img_set));
                }

                ImageView img_remove_set = view.findViewById(R.id.img_remove_set);
                final int finalI = i;
                img_remove_set.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (setModel.getImages().get(finalI).getPhoto_id() != null) {
                            new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                    .title("Delete Product")
                                    .content("Are you sure you want to delete this product?")
                                    .positiveText("OK")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            dialog.dismiss();
                                            callDeleteProductsPhotos(setModel.getImages().get(finalI).getPhoto_id(), holder.getAdapterPosition(), setModel);
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

                        } else {
                            new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                    .title("Delete Product")
                                    .content("Are you sure you want to delete this product?")
                                    .positiveText("OK")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            dialog.dismiss();
                                            ArrayList<Image> temp = setModel.getImages();
                                            temp.remove(finalI);
                                            setModel.setImages(temp);
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

                    }
                });
                holder.flexbox_image.addView(view);
            }
        } else {
            holder.flexbox_image.addView(holder.attach_button);
        }

        holder.attach_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFocus();
                EDIT_POSITION = holder.getAdapterPosition();
                Intent intent = new Intent(context, AlbumSelectActivity.class);
                fragment.startActivityForResult(intent, Application_Singleton.MULTIIMAGE_SCREEN_REQUEST_CODE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return screenSetModels.size();
    }

    public ArrayList<Image> getScreenProductImage(int position) {
        return screenSetModels.get(position).getImages();
    }

    public void callDeleteProductsPhotos(String deleteid, final int position, final ScreenSetModel setModel) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        String url = URLConstants.companyUrl(context, "products_photos", "") + deleteid + "/";
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.DELETEWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                ArrayList<Image> temp = setModel.getImages();
                temp.remove(position);
                setModel.setImages(temp);
                notifyItemChanged(position);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void clearFocus() {
        KeyboardUtils.hideKeyboard((Activity) context);
        View currentFocus = ((Activity) context).getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
    }


    public class MyTextWatcher implements TextWatcher {
        private EditText editText;
        private String type;

        public MyTextWatcher(EditText editText, String type) {
            this.editText = editText;
            this.type = type;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(type!=null){
                int position = (int) editText.getTag();
                if (!s.toString().isEmpty()) {
                    if(type.equalsIgnoreCase("screen")){
                        screenSetModels.get(position).setScreen_name(s.toString());
                    } else if(type.equalsIgnoreCase("color")){
                        screenSetModels.get(position).setColor_name(s.toString());
                    } else if (type.equalsIgnoreCase("expiry")){
                        screenSetModels.get(position).setExpiry_date(s.toString());
                    }
                }
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.edit_colours)
        EditText edit_colours;

        @BindView(R.id.flexbox_image)
        FlexboxLayout flexbox_image;

        @BindView(R.id.attach_button)
        AppCompatButton attach_button;

        @BindView(R.id.txt_set_number)
        TextView txt_set_number;

        @BindView(R.id.edit_sku)
        EditText edit_sku;

        @BindView(R.id.txt_input_colours)
        TextInputLayout txt_input_colours;

        @BindView(R.id.product_expire_days)
        LinearLayout product_expire_days;

        @BindView(R.id.edit_product_expire_days)
        EditText edit_product_expire_days;

        @BindView(R.id.img_delete)
        ImageView img_delete;



        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            MyTextWatcher textWatcher = new MyTextWatcher(edit_sku,"screen");
            edit_sku.addTextChangedListener(textWatcher);

            MyTextWatcher textWatcher1 = new MyTextWatcher(edit_colours,"color");
            edit_colours.addTextChangedListener(textWatcher1);

            MyTextWatcher textWatcher2 = new MyTextWatcher(edit_product_expire_days,"expiry");
            edit_product_expire_days.addTextChangedListener(textWatcher2);
        }
    }
}

