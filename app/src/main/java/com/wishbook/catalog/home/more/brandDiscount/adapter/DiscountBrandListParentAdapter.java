package com.wishbook.catalog.home.more.brandDiscount.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.commonmodels.responses.Brand;
import com.wishbook.catalog.commonmodels.responses.NameValueParent;
import com.wishbook.catalog.home.more.myviewers_2.ViewersAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DiscountBrandListParentAdapter extends RecyclerView.Adapter<DiscountBrandListParentAdapter.CustomViewHolder> implements DiscountBrandChildListAdapter.CallParentListener {


    private Context context;
    private ArrayList<NameValueParent> arraylist;
    private List<NameValueParent> list;
    public static String TAG = DiscountBrandListParentAdapter.class.getSimpleName();


    public DiscountBrandListParentAdapter(Context context, ArrayList<NameValueParent> list) {
        this.context = context;
        this.list = list;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(list);
    }

    @Override
    public DiscountBrandListParentAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discount_parent_item, parent, false);
        return new DiscountBrandListParentAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, final int position) {
        holder.checkbox.setOnCheckedChangeListener(null);

        if (list.get(position).getChildItemList() != null && list.get(position).getChildItemList().size() > 0) {
            // To Check if All Child is checked than parent will be checked
            boolean allChecked = true;
            for (int i = 0; i < list.get(position).getChildItemList().size(); i++) {
                if (list.get(position).getChildItemList().get(0) instanceof Brand) {
                    if (!((Brand) list.get(position).getChildItemList().get(i)).isChecked()) {
                        allChecked = false;
                        break;
                    }
                }
            }
            if (allChecked) {
                ((NameValueParent) list.get(position)).setChecked(true);
            } else {
                ((NameValueParent) list.get(position)).setChecked(false);
            }

            boolean allDisable = true;
            for (int i = 0; i < list.get(position).getChildItemList().size(); i++) {
                if (list.get(position).getChildItemList().get(0) instanceof Brand) {
                    if (!((Brand) list.get(position).getChildItemList().get(i)).isDisable()) {
                        allDisable = false;
                        break;
                    }
                }
            }

            if (allDisable) {
                ((NameValueParent) list.get(position)).setDisable(true);
            } else {
                ((NameValueParent) list.get(position)).setDisable(false);
            }
        }

        if (((NameValueParent) list.get(position)).isChecked()) {
            holder.checkbox.setChecked(true);
        } else {
            holder.checkbox.setChecked(false);
        }


        if(list.get(position).isDisable()){
            holder.checkbox.setEnabled(false);
            holder.checkbox.setChecked(true);
        } else {
            holder.checkbox.setEnabled(true);
        }
        holder.checkbox.setText(list.get(position).getName());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        holder.child_recyclerview.setLayoutManager(mLayoutManager);
        if (list.get(position).getChildItemList().size() > 0) {
            ((DiscountBrandChildListAdapter) list.get(position).getAdpter()).setcallParentListener(this);
            holder.child_recyclerview.setAdapter((DiscountBrandChildListAdapter) list.get(position).getAdpter());
        }
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (isCheck) {
                    if (list.get(position).getId().equals("-1")) {
                        allCheck(isCheck);
                        notifyDataSetChanged();
                    } else {
                        ((NameValueParent) list.get(position)).setChecked(true);
                        if (list.get(position).getChildItemList().size() > 0) {
                            if (list.get(position).getChildItemList().get(0) instanceof Brand) {
                                List<Brand> brands = (List<Brand>) list.get(position).getChildItemList();
                                for (int i = 0; i < brands.size(); i++) {
                                    brands.get(i).setChecked(true);
                                    ((DiscountBrandChildListAdapter) list.get(position).getAdpter()).notifyDataSetChanged();
                                }
                            }
                        }
                    }
                } else {
                    if (list.get(position).getId().equals("-1")) {
                        allCheck(isCheck);
                        notifyDataSetChanged();
                    } else {
                        ((NameValueParent) list.get(position)).setChecked(false);
                        if (list.get(position).getChildItemList().size() > 0) {
                            if (list.get(position).getChildItemList().get(0) instanceof Brand) {
                                List<Brand> brands = (List<Brand>) list.get(position).getChildItemList();
                                ((NameValueParent) list.get(position)).setChecked(true);
                                for (int i = 0; i < brands.size(); i++) {
                                    brands.get(i).setChecked(false);
                                    ((DiscountBrandChildListAdapter) list.get(position).getAdpter()).notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    // Filter Class
    public void filter(String charText) {
        list.clear();
        if (charText.length() == 0) {
            for (int i=0;i<arraylist.size();i++) {
                if(arraylist.get(i).getAdpter()!=null){
                    ((DiscountBrandChildListAdapter) arraylist.get(i).getAdpter()).filter("");
                }
            }

            list.addAll(arraylist);
        } else {
            for (int i = 0; i < arraylist.size(); i++) {
                list.add(arraylist.get(i));
                if (list.get(i).getAdpter() != null && list.get(i).getChildItemList().size() > 0) {
                    ArrayList<Brand> temp = new ArrayList<Brand>();
                    if(arraylist.get(i).getAdpter()!=null){
                        ((DiscountBrandChildListAdapter) arraylist.get(i).getAdpter()).filter("");
                    }
                    temp.addAll(arraylist.get(i).getChildItemList());
                    DiscountBrandChildListAdapter childListAdapter =
                            new DiscountBrandChildListAdapter(context, ((DiscountBrandChildListAdapter) arraylist.get(i).getAdpter()).filter(charText), temp,i);
                    list.get(i).setAdpter(childListAdapter);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void allCheck(boolean isChecked) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setChecked(isChecked);
            List<Brand> brands = (List<Brand>) list.get(i).getChildItemList();
            for (int j = 0; j < brands.size(); j++) {
                brands.get(j).setChecked(isChecked);
            }
        }
    }

    @Override
    public void changeChecked(int position) {
        if (list.get(position).isChecked()) {
            list.get(position).setChecked(false);
            notifyItemChanged(position);
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.checkbox)
        CheckBox checkbox;

        @BindView(R.id.child_recyclerview)
        RecyclerView child_recyclerview;

        private ViewersAdapter.ItemClickListener clickListener;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

    public ArrayList<NameValues> getAllCheckedFilter() {
        ArrayList<NameValues> chekedItem = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getId().equals("-1")){
               if(list.get(i).isChecked()) {
                   chekedItem.add(new NameValues("All brands","-1"));
                   break;
               }
            } else {
                if (list.get(i).getChildItemList().size() > 0) {
                    if (list.get(i).getChildItemList().get(0) instanceof Brand) {
                        NameValueParent temp = new NameValueParent();
                        List<Brand> tempBrands = new ArrayList<>();
                        temp.setName(temp.getName());
                        temp.setId(temp.getId());
                        for (Object b : list.get(i).getChildItemList()) {
                            if (((Brand) b).isChecked()) {
                                chekedItem.add(new NameValues(((Brand) b).getName(), ((Brand) b).getId()));
                            }
                        }
                    }
                }
            }

        }
        return chekedItem;
    }
}
