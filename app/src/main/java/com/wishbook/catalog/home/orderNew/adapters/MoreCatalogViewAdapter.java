package com.wishbook.catalog.home.orderNew.adapters;

import android.content.Context;
import androidx.annotation.IdRes;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.responses.CustomViewModel;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.orderNew.add.Fragment_CreateOrderNew;
import com.wishbook.catalog.home.orderNew.add.Fragment_CreatePurchaseOrderNew;

import java.util.ArrayList;


public class MoreCatalogViewAdapter extends RecyclerView.Adapter<MoreCatalogViewAdapter.CustomViewHolder> {

    private Context mContext;
    private ArrayList<CustomViewModel> itemList;
    private Fragment fragment;
    private ViewChangeListener viewChangeListener;
    private boolean isDefaultAvailable = true;

    public MoreCatalogViewAdapter(Context context, ArrayList<CustomViewModel> itemList, Fragment fragment, ViewChangeListener viewChangeListener) {
        this.itemList = itemList;
        this.mContext = context;
        this.fragment = fragment;
        this.viewChangeListener = viewChangeListener;
    }

    public MoreCatalogViewAdapter(Context context, ArrayList<CustomViewModel> itemList, Fragment fragment, ViewChangeListener viewChangeListener, boolean isDefaultAvailable) {
        this.itemList = itemList;
        this.mContext = context;
        this.fragment = fragment;
        this.viewChangeListener = viewChangeListener;
        this.isDefaultAvailable = isDefaultAvailable;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_more_catalog_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        if (itemList.get(position).getObjects().get(0) instanceof Response_catalogMini) {
            final ArrayList<Response_catalogMini> response_catalogMinis = itemList.get(position).getObjects();
            //final SpinAdapter_CatalogsMini spinAdapter_catalogsMini = new SpinAdapter_CatalogsMini((Activity) mContext, R.layout.spinneritem, response_catalogMinis);
            holder.txt_full_catalog_note.setVisibility(View.INVISIBLE);
            holder.txt_catalog_name.setText(response_catalogMinis.get(0).getTitle());
           // holder.spinner_catalog.setAdapter(spinAdapter_catalogsMini);
            //holder.spinner_catalog.performClick();
            //holder.spinner_catalog.requestFocus();

          /*  holder.spinner_catalog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int spinnerposition, long id) {
                    if (spinnerposition > 0) {
                        if (holder.edit_catalog_quantity.getText().toString().isEmpty()) {
                            if (fragment instanceof Fragment_CreatePurchaseOrderNew) {
                                ((Fragment_CreatePurchaseOrderNew) fragment).getProductsByCatalog(response_catalogMinis.get(spinnerposition).getId(), response_catalogMinis.get(spinnerposition).getFull_catalog_orders_only(), false, position + 1, 1);
                            } else if (fragment instanceof Fragment_CreateOrderNew) {
                                if (isDefaultAvailable) {
                                    ((Fragment_CreateOrderNew) fragment).getProductsByCatalog(response_catalogMinis.get(spinnerposition).getId(), response_catalogMinis.get(spinnerposition).getFull_catalog_orders_only(), false, position + 1, 1);
                                } else {
                                    ((Fragment_CreateOrderNew) fragment).getProductsByCatalog(response_catalogMinis.get(spinnerposition).getId(), response_catalogMinis.get(spinnerposition).getFull_catalog_orders_only(), false, position, 1);
                                }
                            }

                        } else {
                            if (fragment instanceof Fragment_CreatePurchaseOrderNew) {
                                ((Fragment_CreatePurchaseOrderNew) fragment).getProductsByCatalog(response_catalogMinis.get(spinnerposition).getId(), response_catalogMinis.get(spinnerposition).getFull_catalog_orders_only(), false, position + 1, Integer.parseInt(holder.edit_catalog_quantity.getText().toString().trim()));
                            } else if (fragment instanceof Fragment_CreateOrderNew) {

                                if (isDefaultAvailable) {
                                    ((Fragment_CreateOrderNew) fragment).getProductsByCatalog(response_catalogMinis.get(spinnerposition).getId(), response_catalogMinis.get(spinnerposition).getFull_catalog_orders_only(), false, position + 1, Integer.parseInt(holder.edit_catalog_quantity.getText().toString().trim()));
                                } else {
                                    ((Fragment_CreateOrderNew) fragment).getProductsByCatalog(response_catalogMinis.get(spinnerposition).getId(), response_catalogMinis.get(spinnerposition).getFull_catalog_orders_only(), false, position, Integer.parseInt(holder.edit_catalog_quantity.getText().toString().trim()));
                                }

                            }

                        }
                        itemList.get(position).setField1(String.valueOf(spinnerposition));
                        itemList.get(position).setField3(response_catalogMinis.get(spinnerposition).getTitle());
                        boolean isFullCatalog = (response_catalogMinis.get(spinnerposition).getFull_catalog_orders_only().equals("true")) ? true : false;
                        if (isFullCatalog) {
                            holder.txt_full_catalog_note.setVisibility(View.VISIBLE);
                        } else {
                            holder.txt_full_catalog_note.setVisibility(View.INVISIBLE);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });*/


            if (fragment instanceof Fragment_CreatePurchaseOrderNew) {
                ((Fragment_CreatePurchaseOrderNew) fragment).getProductsByCatalog(response_catalogMinis.get(0).getId(), response_catalogMinis.get(0).getFull_catalog_orders_only(), false, position + 1, Integer.parseInt(holder.edit_catalog_quantity.getText().toString().trim()),holder.edit_catalog_quantity,holder.input_catalog_quantity);
            } else if (fragment instanceof Fragment_CreateOrderNew) {
                if (isDefaultAvailable) {
                    ((Fragment_CreateOrderNew) fragment).getProductsByCatalog(response_catalogMinis.get(0).getId(), response_catalogMinis.get(0).getFull_catalog_orders_only(), false, position + 1, Integer.parseInt(holder.edit_catalog_quantity.getText().toString().trim()),holder.edit_catalog_quantity,holder.input_catalog_quantity);
                } else {
                    ((Fragment_CreateOrderNew) fragment).getProductsByCatalog(response_catalogMinis.get(0).getId(), response_catalogMinis.get(0).getFull_catalog_orders_only(), false, position, Integer.parseInt(holder.edit_catalog_quantity.getText().toString().trim()),holder.edit_catalog_quantity,holder.input_catalog_quantity);
                }


        }
        itemList.get(position).setField1(String.valueOf(position+1));
        itemList.get(position).setField3(response_catalogMinis.get(0).getTitle());
        boolean isFullCatalog = (response_catalogMinis.get(0).getFull_catalog_orders_only().equals("true")) ? true : false;
        if (isFullCatalog) {
            holder.txt_full_catalog_note.setVisibility(View.VISIBLE);
        } else {
            holder.txt_full_catalog_note.setVisibility(View.GONE);
        }

            if (itemList.get(position).getField2() == null) {
                itemList.get(position).setField2("1");
            }

            holder.edit_catalog_quantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int qunatity = 1;
                    if (!holder.edit_catalog_quantity.getText().toString().trim().equals("0") && !holder.edit_catalog_quantity.getText().toString().trim().equals("")) {
                        qunatity = Integer.parseInt(holder.edit_catalog_quantity.getText().toString().trim());
                        if (isDefaultAvailable) {
                            viewChangeListener.onEditQuantity(holder.getAdapterPosition() + 1, qunatity);
                        } else {
                            viewChangeListener.onEditQuantity(holder.getAdapterPosition(), qunatity);
                        }

                    } else {
                        if (holder.edit_catalog_quantity.getText().toString().trim().equals("0")) {
                            holder.input_catalog_quantity.setError(mContext.getString(R.string.minimum_quantity));
                            holder.edit_catalog_quantity.requestFocus();
                        }
                        qunatity = 1;
                    }
                    itemList.get(holder.getAdapterPosition()).setField2(holder.edit_catalog_quantity.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                    validateQuantity(holder.edit_catalog_quantity, holder.input_catalog_quantity);
                }
            });
            holder.btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("TAG", "onClick: Array Size==>" + itemList.size() + "Position==>" + holder.getAdapterPosition());
                    itemList.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount());
                    if (isDefaultAvailable) {
                        viewChangeListener.onRemoveView(holder.getAdapterPosition() + 1);
                    } else {
                        viewChangeListener.onRemoveView(holder.getAdapterPosition());
                    }

                }
            });
            holder.radioGroupPkgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    if (i == R.id.radio_default_boxed) {
                        if (isDefaultAvailable) {
                            viewChangeListener.onEditPkgType(mContext.getResources().getString(R.string.package_type_box), holder.getAdapterPosition() + 1);
                        } else {
                            viewChangeListener.onEditPkgType(mContext.getResources().getString(R.string.package_type_box), holder.getAdapterPosition());
                        }

                    } else {
                        if (isDefaultAvailable) {
                            viewChangeListener.onEditPkgType(mContext.getResources().getString(R.string.package_type_naked), holder.getAdapterPosition() + 1);
                        } else {
                            viewChangeListener.onEditPkgType(mContext.getResources().getString(R.string.package_type_naked), holder.getAdapterPosition());
                        }

                    }
                }
            });
            ((RadioButton) holder.radioGroupPkgType.getChildAt(1)).setChecked(true);

        }

    }


    public interface ViewChangeListener {
        void onRemoveView(int position);

        void onEditQuantity(int position, int quantity);

        void onEditPkgType(String pkgtype, int position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public boolean validateItems() {
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getField1() != null) {
                if (itemList.get(i).getField1().equals("0")) {
                    Toast.makeText(mContext, "Please select catalog", Toast.LENGTH_LONG).show();
                    return false;
                }
                if (itemList.get(i).getField2() != null) {
                    if (itemList.get(i).getField2().equals("0") || itemList.get(i).getField2().equals("")) {
                        Toast.makeText(mContext, "Invalid Quantity", Toast.LENGTH_LONG).show();
                        return false;
                    }
                } else {
                    Toast.makeText(mContext, "Invalid Quantity", Toast.LENGTH_LONG).show();
                    return false;
                }
            } else {
                Toast.makeText(mContext, "Please select catalog", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private void validateQuantity(EditText s, TextInputLayout inputLayout) {
        if (!TextUtils.isEmpty(s.getText())) {
            inputLayout.setError(null);
            if (s.getText().toString().equals("0")) {
                inputLayout.setError(mContext.getString(R.string.minimum_quantity));
            }
        } else {
            inputLayout.setError(mContext.getString(R.string.empty_field));
        }
    }

    public ArrayList<String> getField1() {
        ArrayList<String> field1 = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getField3() != null) {
                field1.add(itemList.get(i).getField3());
            }
        }

        return field1;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private final Spinner spinner_catalog;
        private EditText edit_catalog_quantity;
        private ImageView btn_close;
        private TextView txt_full_catalog_note;
        private RadioGroup radioGroupPkgType;
        private TextInputLayout input_catalog_quantity;
        private TextView txt_catalog_name;

        public CustomViewHolder(View view) {
            super(view);
            spinner_catalog = (Spinner) view.findViewById(R.id.spinner_catalog);
            edit_catalog_quantity = (EditText) view.findViewById(R.id.edit_catalog_quantity);
            btn_close = (ImageView) view.findViewById(R.id.btn_close);
            txt_full_catalog_note = (TextView) view.findViewById(R.id.txt_full_catalog_note);
            radioGroupPkgType = (RadioGroup) view.findViewById(R.id.radiogroup_pkg_type_more);
            input_catalog_quantity = (TextInputLayout) view.findViewById(R.id.input_catalog_quantity);
            txt_catalog_name = (TextView) view.findViewById(R.id.txt_catalog_name);
        }
    }
}
