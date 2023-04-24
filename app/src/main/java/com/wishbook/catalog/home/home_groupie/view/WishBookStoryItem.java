package com.wishbook.catalog.home.home_groupie.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.ResponseStoryModel;
import com.wishbook.catalog.stories.StoryActivity;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import at.grabner.circleprogress.CircleProgressView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WishBookStoryItem extends Item<WishBookStoryItem.StoryViewHolder> {

    Fragment fragment;
    Context mContext;
    private ResponseStoryModel data;

    public WishBookStoryItem(ResponseStoryModel data, Context context, Fragment fragment) {
        this.data = data;
        this.mContext = context;
        this.fragment = fragment;
    }

    @Override
    public void bind(@NonNull WishBookStoryItem.StoryViewHolder holder, int position) {
        Log.e("TAG", "Story bind: ======>" + position);
        final ResponseStoryModel story = data;
        holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        StaticFunctions.loadFresco(mContext, story.getImage(), holder.img_story);
        holder.txt_story_title.setText(story.getName());
        holder.outer_story_progress.setBlockCount(story.getCatalogs().size());
        holder.outer_story_progress.setMaxValue(story.getCatalogs().size());
        holder.outer_story_progress.setValue(story.getCompletion_count());

        holder.setClickListener(new WishBookStoryItem.ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Application_Singleton.StoryClickPostion = position;
                Intent storyIntent = new Intent(mContext, StoryActivity.class);
                storyIntent.putExtra("story_id", story.getId());
                Log.d("StoryAdapter ", "comp_count:" + story.getCompletion_count() + " cat_size:" + story.getCatalogs().size());
                int pos = story.getCompletion_count() == story.getCatalogs().size() ? 0 : story.getCompletion_count();
                Log.d("StoryAdapter ", "start_pos:" + pos);
                storyIntent.putExtra("story_start_position", pos);
                if (fragment != null) {
                    Application_Singleton.trackEvent("Home", "Story Click", story.getName());
                    fragment.startActivityForResult(storyIntent, Application_Singleton.STORY_VIEW_REQUEST_CODE);
                    ((Activity) mContext).overridePendingTransition(0, 0);
                }
            }
        });
    }


    @Override
    public int getLayout() {
        return R.layout.story_item;
    }


    @NonNull
    @Override
    public WishBookStoryItem.StoryViewHolder createViewHolder(@NonNull View itemView) {
        return new WishBookStoryItem.StoryViewHolder(itemView);
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }


    public class StoryViewHolder extends ViewHolder implements View.OnClickListener {

        @BindView(R.id.img_brand_logo)
        SimpleDraweeView img_story;

        @BindView(R.id.outer_story_progress)
        CircleProgressView outer_story_progress;

        @BindView(R.id.txt_story_title)
        TextView txt_story_title;

        private WishBookStoryItem.ItemClickListener clickListener;

        public StoryViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(WishBookStoryItem.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }

}