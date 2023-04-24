package com.wishbook.catalog.commonadapters;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.NavigationUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecommendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private ArrayList<?> items;
    private int type;
    private Fragment fragment;
    private int numberofGridSize;
    private final int height;
    private final int width;

    public static int SIMILAR = 1;


    public RecommendListAdapter(Context context, ArrayList<?> items, int type, Fragment fragment, int numberofGridSize) {
        this.context = context;
        this.items = items;
        this.type = type;
        this.fragment = fragment;
        this.numberofGridSize = numberofGridSize;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SIMILAR) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommandation_list_item, parent, false);
            if (numberofGridSize == 3) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Math.round(width / 3.1f), Math.round(height / 4.2f));
                view.setLayoutParams(lp);
            } else {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Math.round(width / 2.1f), Math.round(height / 3.2f));
                view.setLayoutParams(lp);
            }
            return new RecommendListAdapter.RecommendViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecommendViewHolder) {
            if (items.get(0) instanceof Response_catalogMini) {
                StaticFunctions.loadFresco(context, ((Response_catalogMini) items.get(position)).getImage().getThumbnail_small(), ((RecommendViewHolder) holder).item_img);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("from","Similar Product List");
                        bundle.putString("product_id",((Response_catalogMini) items.get(holder.getAdapterPosition())).getId());
                        new NavigationUtils().navigateDetailPage(context,bundle);
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    @Override
    public int getItemViewType(int position) {
        return type;
    }


    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }


    public class RecommendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.itemcontainer)
        RelativeLayout itemcontainer;

        @BindView(R.id.item_img)
        SimpleDraweeView item_img;



        private RecommendListAdapter.ItemClickListener clickListener;

        public RecommendViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(RecommendListAdapter.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }
}
