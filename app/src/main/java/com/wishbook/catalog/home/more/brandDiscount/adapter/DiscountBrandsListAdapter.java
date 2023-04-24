package com.wishbook.catalog.home.more.brandDiscount.adapter;

import android.content.Context;
import android.graphics.Typeface;
import com.google.android.material.textfield.TextInputLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.Model.ParentWrapper;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.BuyerSeg;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.commonmodels.responses.Brand;
import com.wishbook.catalog.commonmodels.responses.NameValueParent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class DiscountBrandsListAdapter extends ExpandableRecyclerAdapter<DiscountBrandsListAdapter.MyParentViewHolder, DiscountBrandsListAdapter.MyChildViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<NameValueParent> arraylist;
    private List<NameValueParent> list;

    public DiscountBrandsListAdapter(Context context, List<NameValueParent> itemList) {
        super(itemList);
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.list = itemList;
        this.arraylist = new ArrayList<NameValueParent>();
        arraylist.addAll(itemList);
    }

    @Override
    public DiscountBrandsListAdapter.MyParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.fabric_select_item, viewGroup, false);
        return new DiscountBrandsListAdapter.MyParentViewHolder(view);
    }

    @Override
    public DiscountBrandsListAdapter.MyChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.fabric_select_item, viewGroup, false);
        return new DiscountBrandsListAdapter.MyChildViewHolder(view);
    }


    @Override
    public void onBindParentViewHolder(final DiscountBrandsListAdapter.MyParentViewHolder parentViewHolder, final int position, final ParentListItem parentListItem) {
        parentViewHolder.checkbox.setOnCheckedChangeListener(null);
        if (parentListItem.getChildItemList() != null && parentListItem.getChildItemList().size() > 0) {
            boolean allChecked = true;
            for (int i = 0; i < parentListItem.getChildItemList().size(); i++) {
                if(parentListItem.getChildItemList().get(0) instanceof  Brand){
                    if(!((Brand)parentListItem.getChildItemList().get(i)).isChecked()){
                        allChecked = false;
                        break;
                    }
                }
            }
            if(allChecked){
                ((NameValueParent) parentListItem).setChecked(true);
            } else {
                ((NameValueParent) parentListItem).setChecked(false);
            }

        }

        if (((NameValueParent) parentListItem).isChecked()) {
            parentViewHolder.checkbox.setChecked(true);
        } else {
            parentViewHolder.checkbox.setChecked(false);
        }
        parentViewHolder.checkbox.setText(((NameValueParent) parentListItem).getName());
        parentViewHolder.checkbox.setTypeface(null, Typeface.BOLD);
        parentViewHolder.checkbox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        parentViewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (isCheck) {
                    ((NameValueParent) parentListItem).setChecked(true);
                    if (parentListItem.getChildItemList().size() > 0) {
                        if (parentListItem.getChildItemList().get(0) instanceof Brand) {
                            List<Brand> brands = (List<Brand>) parentListItem.getChildItemList();
                            for (int i = 0; i < brands.size(); i++) {
                                brands.get(i).setChecked(true);
                                notifyChildItemChanged(Integer.parseInt(((NameValueParent) parentListItem).getId()), i);
                            }
                        } else if (parentListItem.getChildItemList().get(0) instanceof BuyerSeg) {
                            List<BuyerSeg> buyerSegs = (List<BuyerSeg>) parentListItem.getChildItemList();
                            ((NameValueParent) parentListItem).setChecked(true);
                            for (int i = 0; i < buyerSegs.size(); i++) {
                                buyerSegs.get(i).setChecked(true);
                                notifyChildItemChanged(Integer.parseInt(((NameValueParent) parentListItem).getId()), i);
                            }
                        }
                    }
                } else {
                    ((NameValueParent) parentListItem).setChecked(false);
                    if (parentListItem.getChildItemList().size() > 0) {
                        if (parentListItem.getChildItemList().get(0) instanceof Brand) {
                            List<Brand> brands = (List<Brand>) parentListItem.getChildItemList();
                            ((NameValueParent) parentListItem).setChecked(true);
                            for (int i = 0; i < brands.size(); i++) {
                                brands.get(i).setChecked(false);
                                notifyChildItemChanged(Integer.parseInt(((NameValueParent) parentListItem).getId()), i);
                            }
                        } else if (parentListItem.getChildItemList().get(0) instanceof BuyerSeg) {
                            List<BuyerSeg> buyerSegs = (List<BuyerSeg>) parentListItem.getChildItemList();
                            ((NameValueParent) parentListItem).setChecked(true);
                            for (int i = 0; i < buyerSegs.size(); i++) {
                                buyerSegs.get(i).setChecked(false);
                                notifyChildItemChanged(Integer.parseInt(((NameValueParent) parentListItem).getId()), i);
                            }
                        }
                    }
                }
            }
        });
    }


    @Override
    public void onBindChildViewHolder(DiscountBrandsListAdapter.MyChildViewHolder childViewHolder, final int position, final Object childListItem) {
        if (childListItem instanceof Brand) {

            final Brand subBrands = (Brand) childListItem;
            childViewHolder.checkbox.setOnCheckedChangeListener(null);
            if (subBrands.isChecked()) {
                childViewHolder.checkbox.setChecked(true);
            } else {
                childViewHolder.checkbox.setChecked(false);
            }
            childViewHolder.checkbox.setText(subBrands.getName());
            childViewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        subBrands.setChecked(true);
                    } else {
                        if (((NameValueParent) getParentItemList().get(getParentPositionOfPosition(position))).isChecked()) {
                            ((NameValueParent) getParentItemList().get(getParentPositionOfPosition(position))).setChecked(false);
                            notifyParentItemChanged(getParentPositionOfPosition(position));
                        }

                        subBrands.setChecked(false);
                    }

                }
            });
        } else if (childListItem instanceof BuyerSeg) {
            final BuyerSeg subSeg = (BuyerSeg) childListItem;
            childViewHolder.checkbox.setOnCheckedChangeListener(null);
            if (subSeg.isChecked()) {
                childViewHolder.checkbox.setChecked(true);
            } else {
                childViewHolder.checkbox.setChecked(false);
            }
            childViewHolder.checkbox.setText(subSeg.getSegmentation_name());
            childViewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    subSeg.setChecked(true);
                }
            });
        }


    }

    public int getParentPositionOfPosition(int pos) {
        int pCount = -1;    //Die aktuelle Parentposition
        for (int i = 0; i <= pos; i++) {
            Object listItem = getListItem(i);
            if (listItem instanceof ParentWrapper) {
                pCount++;
            }
        }
        return pCount;
    }

    public int getChildPositionOfPosition(int pos) {
        int cCount = -1;
        for (int i = 0; i <= pos; i++) {
            Object listItem = getListItem(i);
            if (listItem instanceof ParentWrapper) {
                cCount = -1;
            } else {
                cCount++;
            }
        }
        return cCount;
    }


    public class MyParentViewHolder extends ParentViewHolder {

        private CheckBox checkbox;
        private TextInputLayout txt_other_input_value;

        public MyParentViewHolder(View itemView) {
            super(itemView);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            txt_other_input_value = (TextInputLayout) itemView.findViewById(R.id.txt_other_input_value);
        }
    }


    public class MyChildViewHolder extends ChildViewHolder {

        private CheckBox checkbox;

        public MyChildViewHolder(View itemView) {
            super(itemView);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }


    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        list.clear();
        if (charText.length() == 0) {
            list.addAll(arraylist);
        } else {
            for (NameValueParent wp : arraylist) {
                if (wp.getChildItemList().size() > 0) {
                    List<Brand> brands = (List<Brand>) wp.getChildItemList();
                    List<Brand> filterbrands = new ArrayList<>();
                    for (Brand b : brands) {
                        if (b.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                            filterbrands.add(b);
                        }
                    }
                    wp.setChildItemList(filterbrands);
                    list.add(wp);
                }
            }
        }
        for (int i=0;i<list.size();i++){
            Log.e("TAG", "Parent filter: " );
            for (int j=0;j<list.get(i).getChildItemList().size();j++){
                Brand b = (Brand) list.get(i).getChildItemList().get(j);
                Log.e("TAG", "Child filter: "+(b.getName()));
            }
        }
        for (int i=0;i<list.size();i++){
            notifyParentItemChanged(i);
        }

    }

    public ArrayList<NameValues> getAllCheckedFilter() {
        ArrayList<NameValues> chekedItem = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
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
                } else if (list.get(i).getChildItemList().get(0) instanceof BuyerSeg) {
                    NameValueParent temp = new NameValueParent();
                    List<BuyerSeg> tempGroup = new ArrayList<>();
                    temp.setName(temp.getName());
                    temp.setId(temp.getId());
                    for (Object b : list.get(i).getChildItemList()) {
                        if (((BuyerSeg) b).isChecked()) {
                            if (((BuyerSeg) b).getId() == "0" && ((BuyerSeg) b).getSegmentation_name().equals("All groups"))
                                break;
                            else
                                chekedItem.add(new NameValues(((BuyerSeg) b).getSegmentation_name(), ((BuyerSeg) b).getId()));
                        }
                    }
                }
            }

        }
        return chekedItem;
    }
}
