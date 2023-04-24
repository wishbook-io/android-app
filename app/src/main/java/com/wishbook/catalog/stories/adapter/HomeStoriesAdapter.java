package com.wishbook.catalog.stories.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.ResponseStoryModel;
import com.wishbook.catalog.stories.StoryActivity;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import at.grabner.circleprogress.CircleProgressView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeStoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<?> items;
    private int type;
    private Fragment fragment;

    public static int STORY = 1;

    public HomeStoriesAdapter(Context context, ArrayList<?> items, int type, Fragment fragment) {
        this.context = context;
        this.items = items;
        this.type = type;
        this.fragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == STORY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_item, parent, false);
            return new StoryViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StoryViewHolder) {
            if (type == STORY) {
                if (items.get(0) instanceof ResponseStoryModel) {
                    final ResponseStoryModel story = (ResponseStoryModel) items.get(position);
                    StaticFunctions.loadFresco(context,story.getImage(),((StoryViewHolder) holder).img_story);
                    ((StoryViewHolder) holder).txt_story_title.setText(story.getName());
                    ((StoryViewHolder) holder).outer_story_progress.setBlockCount(story.getCatalogs().size());
                    ((StoryViewHolder) holder).outer_story_progress.setMaxValue(story.getCatalogs().size());
                    ((StoryViewHolder) holder).outer_story_progress.setValue(story.getCompletion_count());

                    ((StoryViewHolder) holder).setClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Application_Singleton.StoryClickPostion = position;
                            Intent storyIntent = new Intent(context,StoryActivity.class);
                            storyIntent.putExtra("story_id", story.getId());


                            Log.d("StoryAdapter ", "comp_count:"+ story.getCompletion_count()+ " cat_size:"+story.getCatalogs().size());
                            int pos = story.getCompletion_count() == story.getCatalogs().size() ? 0 : story.getCompletion_count();
                            Log.d("StoryAdapter ", "start_pos:"+ pos);
                            storyIntent.putExtra("story_start_position", pos);



                            if(fragment!=null){
                                Application_Singleton.trackEvent("Home","Story Click" , story.getName());
                                fragment.startActivityForResult(storyIntent, Application_Singleton.STORY_VIEW_REQUEST_CODE);
                                ((Activity)context).overridePendingTransition(0,0);
                            }
                        }
                    });
                }
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


    public class StoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.img_brand_logo)
        SimpleDraweeView img_story;

        @BindView(R.id.outer_story_progress)
        CircleProgressView outer_story_progress;

        @BindView(R.id.txt_story_title)
        TextView txt_story_title;

        private HomeStoriesAdapter.ItemClickListener clickListener;

        public StoryViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(HomeStoriesAdapter.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }
}
