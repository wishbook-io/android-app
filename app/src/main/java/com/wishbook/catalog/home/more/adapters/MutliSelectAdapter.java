package com.wishbook.catalog.home.more.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.MultiSelectModel;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by root on 13/4/17.
 */
public class MutliSelectAdapter extends RecyclerView.Adapter<MutliSelectAdapter.MyViewHolder> {

    ArrayList<MultiSelectModel> dataSet = new ArrayList<MultiSelectModel>();
    Context context;
    String query="";
    private ArrayList<MultiSelectModel> arraylist;

    public MutliSelectAdapter(ArrayList<MultiSelectModel> dataSet,Context context) {
        this.dataSet = dataSet;
        this. arraylist = new ArrayList<>();
        arraylist.addAll(dataSet);
        this.context = context;
    }

    @Override
    public MutliSelectAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.multi_select_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if(!query.equals("") && query.length()>1) {
            String name = dataSet.get(position).getName();
            SpannableString str = new SpannableString(name);
            int endLength = name.toLowerCase().indexOf(query)+query.length();
            ColorStateList blueColor = new ColorStateList(new int[][] { new int[] {}}, new int[] { Color.parseColor("#0d80c1") });
            TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(null, Typeface.NORMAL, -1, blueColor, null);
            str.setSpan(textAppearanceSpan,name.toLowerCase().indexOf(query),endLength,  Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.name.setText(str);

        }else{

            holder.name.setText(dataSet.get(position).getName());
        }

       /* if(dataSet.get(position).getSelected()){
            if(!MultiSelectDialog.selectedIdsList.contains(dataSet.get(position).getId())) {
                MultiSelectDialog.selectedIdsList.add(dataSet.get(position).getId());
            }
        }*/

        holder.checkbox.setOnCheckedChangeListener(null);
        if(dataSet.get(position).getSelected()){
            holder.checkbox.setChecked(true);
        } else {
            holder.checkbox.setChecked(false);
        }


      /*  if(checkForSelection(dataSet.get(position).getId())){
            holder.checkbox.setChecked(true);
        }else{
            holder.checkbox.setChecked(false);
        }*/



       holder.whole_container.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(!holder.checkbox.isChecked()){
                   MultiSelectDialog.selectedIdsList.add(dataSet.get(holder.getAdapterPosition()).getId());
                   holder.checkbox.setChecked(true);
                   dataSet.get(holder.getAdapterPosition()).setSelected(true);
                   notifyItemChanged(holder.getAdapterPosition());
               }else{
                   removeFromSelection(dataSet.get(holder.getAdapterPosition()).getId());
                   holder.checkbox.setChecked(false);
                   dataSet.get(holder.getAdapterPosition()).setSelected(false);
                   notifyItemChanged(holder.getAdapterPosition());
               }
           }
       });
    }

    private void removeFromSelection(String id) {
        for(int i = 0; i<MultiSelectDialog.selectedIdsList.size(); i++){
            if(id.equals(MultiSelectDialog.selectedIdsList.get(i))){
                MultiSelectDialog.selectedIdsList.remove(i);
            }
        }
    }


    private boolean checkForSelection(String id) {
        for(int i = 0; i<MultiSelectDialog.selectedIdsList.size(); i++){
                if(id.equals(MultiSelectDialog.selectedIdsList.get(i))){
                    return true;
                }
        }
        return false;
    }

    public String getDataString(){
        String data="";
        for(int i=0;i<dataSet.size();i++){
            if(checkForSelection(dataSet.get(i).getId())){
                data =data+", "+dataSet.get(i).getName();
            }
        }
        if(data.length()>0) {
            return data.substring(1);
        }else{
            return "";
        }
    }

    public String[] getNameList(){
        ArrayList<String> names = new ArrayList<>();
        for(int i=0;i<dataSet.size();i++){
            if(checkForSelection(dataSet.get(i).getId())){
                names.add(dataSet.get(i).getName());
            }
        }
        return names.toArray(new String[names.size()]);
    }




    public ArrayList<String> getAllSelected() {
       ArrayList<String> checkedArraylist = new ArrayList<>();
        filter("");
        for(int i=0;i<dataSet.size();i++) {
            if(dataSet.get(i).getSelected())
                checkedArraylist.add(dataSet.get(i).getId());
        }

        return checkedArraylist;
    }

    public ArrayList<String> getAllSelectedValue() {
        ArrayList<String> checkedArraylist = new ArrayList<>();
        filter("");
        for(int i=0;i<dataSet.size();i++) {
            if(dataSet.get(i).getSelected())
                checkedArraylist.add(dataSet.get(i).getName());
        }
        return checkedArraylist;
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void setData(ArrayList<MultiSelectModel> data,String query) {
        this.dataSet = data;
        this.query = query;
        notifyDataSetChanged();
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        dataSet.clear();
        if (charText.length() == 0) {
            dataSet.addAll(arraylist);
        } else {
            for (MultiSelectModel wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    dataSet.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private  TextView name;
        private CheckBox checkbox;
        private LinearLayout whole_container;
        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            checkbox = (CheckBox) view.findViewById(R.id.checkbox);
            whole_container = (LinearLayout) view.findViewById(R.id.whole_container);
        }
    }
}
