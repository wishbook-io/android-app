package com.wishbook.catalog.home.more.adapters.treeview;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.wishbook.catalog.R;
import com.wishbook.catalog.home.more.adapters.treeview.bean.FileBean;
import com.wishbook.catalog.home.more.adapters.treeview.treerecyclerview.TreeAdapterDialog;


public class FileTreeDialogAdapter extends TreeAdapterDialog<FileBean> {

    private LayoutInflater mLayoutInflater ;

    private View.OnLongClickListener mOnLongClickListener ;

    public FileTreeDialogAdapter(Context context, List<FileBean> datas, int defaultExpandLevel, View.OnLongClickListener onItemLongClickListener) throws IllegalAccessException, IllegalArgumentException {
        super(context, datas, defaultExpandLevel);
        mLayoutInflater = LayoutInflater.from(context) ;
        mOnLongClickListener = onItemLongClickListener ;

    }

    @Override
    public FileTreeDialogViewHolder onCreateTreeViewHolder(ViewGroup viewGroup) {
        return new FileTreeDialogViewHolder(mLayoutInflater.inflate(R.layout.itemdialog,viewGroup,false), mOnLongClickListener);
    }

    @Override
    public void onBindTreeViewHolder(RecyclerView.ViewHolder viewHolder, FileBean obj, boolean isLeaf, boolean isExpend, int level) {
        ((FileTreeDialogViewHolder)viewHolder).bindData(obj,isLeaf,isExpend,level) ;
    }

   public ArrayList<String> getAllselectedData(){
       ArrayList<String> arrayList=new ArrayList<>();
       for(FileBean fileBean:mDatas){
           if(fileBean.isChecked){
               arrayList.add(""+fileBean.fileId);
           }
       }
       return arrayList;
   }



    public class FileTreeDialogViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox check;
        public ImageView iconIv;
        public TextView nameTv;

        public FileTreeDialogViewHolder(final View itemView, View.OnLongClickListener onLongClickListener) {
            super(itemView);
            iconIv = (ImageView) itemView.findViewById(R.id.icon_iv);
            nameTv = (TextView) itemView.findViewById(R.id.name_tv);
            check = (CheckBox) itemView.findViewById(R.id.check);
            itemView.setOnLongClickListener(onLongClickListener);

        }

        public void bindData(final FileBean file, boolean isLeaf, boolean isExpend, int level) {
            itemView.setTag(file);
            // icon
            if (isLeaf) {
                iconIv.setVisibility(View.INVISIBLE);
            } else {
                iconIv.setVisibility(View.VISIBLE);
                if (isExpend) {
                    iconIv.setSelected(true);
                } else {
                    iconIv.setSelected(false);
                }
            }
            // indent
            itemView.setPadding(level * 40, 0, 0, 0);
            // name
            nameTv.setText(file.fileName);

            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    file.setIsChecked(isChecked);
                    // mDatas.get(i)
                    for(int i=0;i<mDatas.size();i++){
                        if(mDatas.get(i).getFileId()==file.getFileId()) {
                            mDatas.get(i).setIsChecked(file.isChecked());
                        }
                    }
                }
            });
            nameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    file.setIsChecked(!check.isChecked());
                   // mDatas.get(i)
                    for(int i=0;i<mDatas.size();i++){
                        if(mDatas.get(i).getFileId()==file.getFileId()) {
                            mDatas.get(i).setIsChecked(file.isChecked());
                        }
                    }
                }
            });
        }
    }
}
