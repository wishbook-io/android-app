package com.wishbook.catalog.home.more.adapters.treeview;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.home.more.adapters.treeview.bean.FileBean;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;


/**
 * Created by ID_MARR on 2015/4/26.
 */
public class FileTreeViewHolder extends RecyclerView.ViewHolder {
    public ImageView iconIv;
    public TextView nameTv;

    public FileTreeViewHolder(final View itemView, View.OnLongClickListener onLongClickListener) {
        super(itemView);
        iconIv = (ImageView) itemView.findViewById(R.id.icon_iv);
        nameTv = (TextView) itemView.findViewById(R.id.name_tv);
        itemView.setOnLongClickListener(onLongClickListener);

    }

    public void bindData(final FileBean file, boolean isLeaf, boolean isExpend,int level) {
        itemView.setTag(file);
        // icon
        if (isLeaf ) {
            iconIv.setVisibility(View.INVISIBLE);
        } else {
            iconIv.setVisibility(View.VISIBLE);
            if ( isExpend) {
                iconIv.setSelected(true);
            } else {
                iconIv.setSelected(false);
            }
        }
        // indent
        itemView.setPadding(level * 40, 0, 0, 0);
        // name
        nameTv.setText(file.fileName);
        nameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application_Singleton.CONTAINER_TITLE = "Catalogs";
                Bundle bundle = new Bundle();
                bundle.putString("filtertype", "category");
                bundle.putString("filtervalue",""+file.fileId);
                Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs();
                fragmentCatalogs.setArguments(bundle);

                Application_Singleton.CONTAINERFRAG = fragmentCatalogs;
                Intent intent=new Intent(Activity_Home.context,OpenContainer.class);
                Activity_Home.context.startActivity(intent);
            }
        });
    }


}
