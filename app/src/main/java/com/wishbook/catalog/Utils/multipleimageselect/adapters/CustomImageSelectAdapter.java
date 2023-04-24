package com.wishbook.catalog.Utils.multipleimageselect.adapters;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.multipleimageselect.models.Image;

/**
 * Created by Darshan on 4/18/2015.
 */
public class CustomImageSelectAdapter extends CustomGenericAdapter<Image> {
    ImageSelectListener imageSelectListener;

    public CustomImageSelectAdapter(Context context, ArrayList<Image> images) {
        super(context, images);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_view_item_image_select, null);

            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view_image_select);
            viewHolder.view = convertView.findViewById(R.id.view_alpha);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.getLayoutParams().width = size;
        viewHolder.imageView.getLayoutParams().height = size;

        viewHolder.view.getLayoutParams().width = size;
        viewHolder.view.getLayoutParams().height = size;

        if (arrayList.get(position).isSelected) {
            viewHolder.view.setAlpha(0.5f);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                convertView.setForeground(context.getResources().getDrawable(R.drawable.ic_done_white));
            }

        } else {
            viewHolder.view.setAlpha(0.0f);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                convertView.setForeground(null);
            }
        }

        File file=new File(arrayList.get(position).path);
        ImageLoader.getInstance().displayImage("file://"+file.getAbsolutePath(), viewHolder.imageView);


        return convertView;
    }

    public interface ImageSelectListener {
        void onSelect(int position);
    }

    public ImageSelectListener getImageSelectListener() {
        return imageSelectListener;
    }

    public void setImageSelectListener(ImageSelectListener imageSelectListener) {
        this.imageSelectListener = imageSelectListener;
    }

    private static class ViewHolder {
        public ImageView imageView;
        public View view;
    }
}
