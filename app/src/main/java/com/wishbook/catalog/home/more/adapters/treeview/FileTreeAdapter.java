package com.wishbook.catalog.home.more.adapters.treeview;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.wishbook.catalog.R;
import com.wishbook.catalog.home.more.adapters.treeview.bean.FileBean;
import com.wishbook.catalog.home.more.adapters.treeview.treerecyclerview.TreeAdapter;


public class FileTreeAdapter extends TreeAdapter<FileBean> {

    private LayoutInflater mLayoutInflater ;

    private View.OnLongClickListener mOnLongClickListener ;

    public FileTreeAdapter(Context context, List<FileBean> datas, int defaultExpandLevel,View.OnLongClickListener onItemLongClickListener) throws IllegalAccessException, IllegalArgumentException {
        super(context, datas, defaultExpandLevel);
        mLayoutInflater = LayoutInflater.from(context) ;
        mOnLongClickListener = onItemLongClickListener ;
    }

    @Override
    public FileTreeViewHolder onCreateTreeViewHolder(ViewGroup viewGroup) {
        return new FileTreeViewHolder(mLayoutInflater.inflate(R.layout.item ,viewGroup,false), mOnLongClickListener);
    }

    @Override
    public void onBindTreeViewHolder(RecyclerView.ViewHolder viewHolder, FileBean obj, boolean isLeaf, boolean isExpend, int level) {
        ((FileTreeViewHolder)viewHolder).bindData(obj,isLeaf,isExpend,level) ;
    }


}
