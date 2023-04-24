package com.wishbook.catalog.commonadapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Activity_AddCatalog;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.Summary_Sub;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.CatalogInfo;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommonAnalyticsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private ArrayList<?> items;
    private int type;
    public static final int LEFTANALYTICS = 0;
    public static final int LEFTANALYTICS1 = 1;
    public static final int RIGHTANALYTICS = 2;
    public static final int CATALOGANALYTICS = 3;
    public static final int LATESTANALYTICS = 4;
    public static final int DEEPLINKPRICE = 5;

    public static final String MYFOLLOWERS = "myfollowers";
    public static final String TOPCATALOG = "topCatalogs";



    public CommonAnalyticsAdapter(Context context, ArrayList<?> items, int type) {
        this.context = context;
        this.items = items;
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == LEFTANALYTICS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_left_analytics, parent, false);
            return new DashboardLeftAnalyticsViewHolder(view);
        } else if (viewType == RIGHTANALYTICS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_right_analytics, parent, false);
            return new DashboardRightAnalyticsViewHolder(view);
        } else if (viewType == LEFTANALYTICS1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_left_analytics_version2, parent, false);
            return new DashboardLeftAnalyticsVersion2ViewHolder(view);
        } else if (viewType == CATALOGANALYTICS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manufacturer_summary_list_item, parent, false);
            return new DashboardCatalogViewHolder(view);
        } else if (viewType == LATESTANALYTICS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_one_catalog, parent, false);
            return new DashboardLatestAnalyticsViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DashboardLeftAnalyticsViewHolder) {
            if (items.get(0) instanceof Summary_Sub) {
                Summary_Sub summary_sub = (Summary_Sub) items.get(0);
                String value = summary_sub.getAnalytics_value();
                if(summary_sub.getAnalytics_value().length()==1) {
                    value = "0"+ summary_sub.getAnalytics_value();
                }
                ((DashboardLeftAnalyticsViewHolder) holder).txt_analytics_value.setText(value);
                ((DashboardLeftAnalyticsViewHolder) holder).txt_analytics_label.setText(summary_sub.getAnalytics_label());


                ((DashboardLeftAnalyticsViewHolder) holder).setClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                       /* Bundle bundle = new Bundle();
                        Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs();
                        fragmentCatalogs.setArguments(bundle);
                        Application_Singleton.CONTAINER_TITLE="My Catalog";
                        Application_Singleton.CONTAINERFRAG= fragmentCatalogs;
                        StaticFunctions.switchActivity((Activity) context, OpenContainer.class);*/


                        HashMap<String,String> param = new HashMap<String, String>();
                        param.put("ctype","mycatalog");
                        param.put("type","catalog");
                        new DeepLinkFunction(param,context);
                    }
                });
            }
        } else if (holder instanceof DashboardRightAnalyticsViewHolder) {
            if (items.get(0) instanceof Summary_Sub) {
                final Summary_Sub summary_sub = (Summary_Sub) items.get(0);
                String value = summary_sub.getAnalytics_value();
                if(summary_sub.getAnalytics_value().length()==1) {
                    value = "0"+ summary_sub.getAnalytics_value();
                }
                ((DashboardRightAnalyticsViewHolder) holder).txt_analytics_value.setText(value);
                ((DashboardRightAnalyticsViewHolder) holder).txt_analytics_label.setText(summary_sub.getAnalytics_label());

                ((DashboardRightAnalyticsViewHolder) holder).setClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Log.i("TAG", "onClick: Right Analytics Click");
                        if(summary_sub.getRedirectorText().equals(MYFOLLOWERS)) {
                            Log.i("TAG", "onClick: Right Analytics Click1");
                            Intent intent1 = new Intent(context, Activity_BuyerSearch.class);
                            intent1.putExtra("toolbarTitle", "Search Followers");
                            intent1.putExtra("type", "myfollowers");
                            context.startActivity(intent1);
                        }
                    }
                });

            }
        } else if (holder instanceof DashboardLeftAnalyticsVersion2ViewHolder) {
            if (items.get(0) instanceof Summary_Sub) {
                final Summary_Sub summary_sub = (Summary_Sub) items.get(0);
                String value = summary_sub.getAnalytics_value();
                if(summary_sub.getAnalytics_value().length()==1) {
                    value = "0"+ summary_sub.getAnalytics_value();
                }
                ((DashboardLeftAnalyticsVersion2ViewHolder) holder).txt_analytics_value.setText(value);
                ((DashboardLeftAnalyticsVersion2ViewHolder) holder).txt_analytics_label.setText(summary_sub.getAnalytics_label());

                ((DashboardLeftAnalyticsVersion2ViewHolder) holder).setClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if(summary_sub.getRedirectorText().equals(TOPCATALOG)) {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("view_type", "mycatalogs");
                            params.put("limitdisplay", summary_sub.getAnalytics_value());
                            params.put("offset", String.valueOf(Application_Singleton.CATALOG_INITIAL_OFFSET));
                            params.put("most_viewed","true");
                            Application_Singleton.deep_link_filter = params;
                            Bundle bundle = new Bundle();
                            Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs();
                            bundle.putString("filtertype","topCatalogs");
                            bundle.putString("filtervalue","true");
                            fragmentCatalogs.setArguments(bundle);
                            Application_Singleton.CONTAINER_TITLE="Catalogs";
                            Application_Singleton.CONTAINERFRAG= fragmentCatalogs;
                            StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                        }

                    }
                });
            }
        } else if (holder instanceof DashboardCatalogViewHolder) {
            if (items.get(0) instanceof Summary_Sub) {
                Summary_Sub summary_sub = (Summary_Sub) items.get(0);
                String number_catalog = summary_sub.getAnalytics_value();
                if(summary_sub.getAnalytics_value()!=null) {
                    if(summary_sub.getAnalytics_value().length()==1) {
                        number_catalog = "0"+ summary_sub.getAnalytics_value();
                    }
                    ((DashboardCatalogViewHolder) holder).txt_number_catalog.setText(number_catalog);
                }


                ((DashboardCatalogViewHolder) holder).relative_active_catalog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       /* Bundle bundle = new Bundle();
                        Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs();
                        fragmentCatalogs.setArguments(bundle);
                        Application_Singleton.CONTAINER_TITLE="Catalogs";
                        Application_Singleton.CONTAINERFRAG= fragmentCatalogs;
                        StaticFunctions.switchActivity((Activity) context, OpenContainer.class);*/

                        HashMap<String,String> param = new HashMap<String, String>();
                        param.put("ctype","mycatalog");
                        param.put("type","catalog");
                        new DeepLinkFunction(param,context);
                    }
                });
                ((DashboardCatalogViewHolder) holder).relative_add_new.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!UserInfo.getInstance(context).isCompanyProfileSet()
                                && UserInfo.getInstance(context).getCompanyname().isEmpty()) {
                            HashMap<String, String> param = new HashMap<String, String>();
                            param.put("type", "profile_update");
                            new DeepLinkFunction(param, context);
                            return;
                        }
                        /*Application_Singleton.CONTAINER_TITLE = "Add Catalog";
                        Application_Singleton.CONTAINERFRAG = new Fragment_AddCatalog();
                        ((Activity)context).startActivityForResult(new Intent(context,OpenContainer.class),Application_Singleton.ADD_CATALOG_REQUEST);*/

                        StaticFunctions.switchActivity((Activity) context, Activity_AddCatalog.class);
                       // StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                    }
                });

            }
        } else if (holder instanceof DashboardLatestAnalyticsViewHolder) {
            if (items.get(0) instanceof CatalogInfo.Lastest_catalog) {
                CatalogInfo.Lastest_catalog lastest_catalog = (CatalogInfo.Lastest_catalog) items.get(0);
                String number_viewes = lastest_catalog.getViews();
                if(lastest_catalog.getViews()!=null) {
                    if(lastest_catalog.getViews().length()==1) {
                        number_viewes = "0"+ lastest_catalog.getViews();
                    }
                    ((DashboardLatestAnalyticsViewHolder) holder).txt_count_view.setText(number_viewes);
                }


                if (lastest_catalog.getImage() != null) {
                    StaticFunctions.loadFresco(context, lastest_catalog.getImage(), ((DashboardLatestAnalyticsViewHolder) holder).item_img);
                }

                ((DashboardLatestAnalyticsViewHolder) holder).setClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                       /* Bundle bundle = new Bundle();
                        Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs();
                        fragmentCatalogs.setArguments(bundle);
                        Application_Singleton.CONTAINER_TITLE="My Catalog";
                        Application_Singleton.CONTAINERFRAG= fragmentCatalogs;
                        StaticFunctions.switchActivity((Activity) context, OpenContainer.class);*/

                        HashMap<String,String> param = new HashMap<String, String>();
                        param.put("ctype","mycatalog");
                        param.put("type","catalog");
                        new DeepLinkFunction(param,context);
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

    public class DashboardLeftAnalyticsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.txt_analytics_value)
        TextView txt_analytics_value;

        @BindView(R.id.txt_analytics_label)
        TextView txt_analytics_label;

        private ItemClickListener clickListener;

        public DashboardLeftAnalyticsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

    public class DashboardLeftAnalyticsVersion2ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.txt_analytics_value)
        TextView txt_analytics_value;

        @BindView(R.id.txt_analytics_label)
        TextView txt_analytics_label;

        private ItemClickListener clickListener;

        public DashboardLeftAnalyticsVersion2ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }


        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }

    public class DashboardRightAnalyticsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.txt_analytics_value)
        TextView txt_analytics_value;

        @BindView(R.id.txt_analytics_label)
        TextView txt_analytics_label;

        private ItemClickListener clickListener;

        public DashboardRightAnalyticsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }

    public class DashboardCatalogViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_number_catalog)
        TextView txt_number_catalog;

        @BindView(R.id.txt_add_new_catalog)
        TextView txt_add_new_catalog;

        @BindView(R.id.relative_active_catalog)
        RelativeLayout relative_active_catalog;

        @BindView(R.id.relative_add_new)
        RelativeLayout relative_add_new;


        public DashboardCatalogViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class DashboardLatestAnalyticsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.txt_count_view)
        TextView txt_count_view;

        @BindView(R.id.item_img)
        SimpleDraweeView item_img;

        private ItemClickListener clickListener;

        public DashboardLatestAnalyticsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }

    public class DashboardMostViewedCatalogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.txt_title)
        TextView txt_title;

        @BindView(R.id.summary_list_item)
        LinearLayout summary_list_item;

        @BindView(R.id.suggestion_recyclerview)
        RecyclerView suggestion_recyclerview;

        private ItemClickListener clickListener;

        public DashboardMostViewedCatalogViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }


}
