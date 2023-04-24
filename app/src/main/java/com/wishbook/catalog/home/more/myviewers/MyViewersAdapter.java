package com.wishbook.catalog.home.more.myviewers;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.ResponseMyViewers;
import com.wishbook.catalog.home.more.myviewers_2.Activity_MyViewersDetail;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyViewersAdapter extends RecyclerView.Adapter<MyViewersAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<ResponseMyViewers> responseMyViewersLists;


    public MyViewersAdapter(Context context, ArrayList<ResponseMyViewers> responseMyViewersLists) {
        this.context = context;
        this.responseMyViewersLists = responseMyViewersLists;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_viewers_list_item, parent, false);
        return new MyViewersAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final ResponseMyViewers viewers = responseMyViewersLists.get(position);
        if (viewers.getImage() != null
                && viewers.getImage().getThumbnail_small() != null
                && !viewers.getImage().getThumbnail_small().isEmpty()) {
            StaticFunctions.loadFresco(context, viewers.getImage().getThumbnail_small(), holder.catalog_img);
        }

        String title = "<font color='#3a3a3a' size='16'>"+viewers.getTitle()+"</font><font color='#777777' size='16'> by </font><font color='#3a3a3a' size='16'>"+viewers.getBrand_name()+"</font>";
        holder.txt_catalog_name.setText(Html.fromHtml(title), TextView.BufferType.SPANNABLE);
        String view_string = "<font color='#777777' size='16'>Viewed by </font><font color='#3a3a3a' size='16'>"+viewers.getViewers()+"</font>";
        holder.txt_viewed_by.setText(Html.fromHtml(view_string), TextView.BufferType.SPANNABLE);

        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(context, Activity_MyViewersDetail.class);
                intent.putExtra("catalog_id", viewers.getId());
                intent.putExtra("total_viewers", viewers.getTotal_viewes());
                intent.putExtra("brand_i_own", viewers.isBrand_i_own());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return responseMyViewersLists.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.catalog_img)
        SimpleDraweeView catalog_img;

        @BindView(R.id.txt_catalog_name)
        TextView txt_catalog_name;

        @BindView(R.id.txt_brand_name)
        TextView txt_brand_name;

        @BindView(R.id.txt_viewed_by)
        TextView txt_viewed_by;

        private MyViewersAdapter.ItemClickListener clickListener;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(MyViewersAdapter.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }
}
