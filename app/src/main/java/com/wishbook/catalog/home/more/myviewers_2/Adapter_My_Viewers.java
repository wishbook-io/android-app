package com.wishbook.catalog.home.more.myviewers_2;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Model_My_Viewers;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Adapter_My_Viewers extends RecyclerView.Adapter<Adapter_My_Viewers.CustomViewHolder> {

    private Context context;
    private ArrayList<Model_My_Viewers> data;

    public Adapter_My_Viewers(Context context, ArrayList<Model_My_Viewers> viewers) {
        this.context = context;
        this.data = viewers;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_myviewers_item, parent, false);
        return new Adapter_My_Viewers.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final Model_My_Viewers viewer = data.get(position);
        if(viewer.getCompany_name()!=null && viewer.getCompany_name().length() > 1) {
            holder.buyer_name.setText(viewer.getCompany_name().substring(0, 1));
        } else {
            holder.buyer_name.setText("");
        }

        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(context, Activity_MyViewersDetail.class);
                intent.putExtra("catalog_id", "" + viewer.getCatalog_id());
                intent.putExtra("product_id",viewer.getProduct_id());
                intent.putExtra("catalog_name", "" + viewer.getCatalog_title());
                context.startActivity(intent);
            }
        });
        if (position == 0) {
            String title = "<font color='#3a3a3a' size='14'>" + viewer.getCompany_name() + "</font><font color='#777777' size='14'> recently viewed your </font><font color='#3a3a3a' size='14'>" + viewer.getCatalog_title() + " \u2022 </font><font color='#777777' size='14'>" + DateUtils.getTimeAgo(viewer.getCreated_at(), context) + "</font>";
            holder.viewer_txt.setText(Html.fromHtml(title), TextView.BufferType.SPANNABLE);
            //  holder.viewer_txt2.setText( " recently ");
            //   holder.viewer_txt3.setText(" "+viewer.getCatalog_title()+" \u2022 " + DateUtils.getTimeAgo(viewer.getCreated_at(), context));

        } else {
            String title = "<font color='#3a3a3a' size='14'>" + viewer.getCompany_name() + "</font><font color='#777777' size='14'> viewed your </font><font color='#3a3a3a' size='14'>" + viewer.getCatalog_title() + " \u2022 </font><font color='#777777' size='14'>" + DateUtils.getTimeAgo(viewer.getCreated_at(), context) + "</font>";
            holder.viewer_txt.setText(Html.fromHtml(title), TextView.BufferType.SPANNABLE);
            ;

            //    holder.viewer_txt3.setText(" "+viewer.getCatalog_title()+" \u2022 " + DateUtils.getTimeAgo(viewer.getCreated_at(), context));
        }
        StaticFunctions.loadFresco(context, viewer.getCatalog_image(), holder.viewer_catalog_image);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.viewer_txt_image)
        ImageView viewer_txt_image;

        @BindView(R.id.viewer_txt)
        TextView viewer_txt;

        @BindView(R.id.buyer_name)
        TextView buyer_name;

        @BindView(R.id.viewer_catalog_image)
        SimpleDraweeView viewer_catalog_image;

        private Adapter_My_Viewers.ItemClickListener clickListener;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(Adapter_My_Viewers.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }


}
