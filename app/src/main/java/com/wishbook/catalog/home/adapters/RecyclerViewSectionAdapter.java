package com.wishbook.catalog.home.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.style.ReplacementSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wishbook.catalog.Activity_AddCatalog;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.commonmodels.SummaryModel;
import com.wishbook.catalog.commonmodels.Summary_Sub;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.home.Fragment_ProductSelections;
import com.wishbook.catalog.home.more.Fragment_AddBrand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RecyclerViewSectionAdapter extends RecyclerView.Adapter<RecyclerViewSectionAdapter.ItemViewHolder> {

    private final AppCompatActivity appCompatActivity;
    private List<SummaryModel> allData;

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        try {
            Log.i("TAG", "onBindViewHolder: ==>"+allData.get(position).getHeaderTitle()+"Size===>"+allData.size());
            final ArrayList<Summary_Sub> itemsInSection = allData.get(position).getSummary_subs();

            final Summary_Sub itemName = itemsInSection.get(0);

            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            if(allData.get(position).getHeaderTitle().equals("Analytics")) {
                itemViewHolder.txt_title.setVisibility(View.GONE);
                itemViewHolder.container.setBackgroundColor(appCompatActivity.getResources().getColor(R.color.transparent));
            } else {
                itemViewHolder.txt_title.setVisibility(View.VISIBLE);
                itemViewHolder.txt_title.setText(allData.get(position).getHeaderTitle());
                itemViewHolder.container.setBackgroundColor(appCompatActivity.getResources().getColor(R.color.white));
            }



            String headerTitle = allData.get(position).getHeaderTitle();

            if(itemName.getSuggestionContactsAdapter()!=null)
            {
                itemViewHolder.summary_list_item.setVisibility(View.GONE);
                itemViewHolder.recyclerView.setVisibility(View.VISIBLE);
                itemViewHolder.image.setVisibility(View.GONE);
                LinearLayoutManager layoutManager
                        = new LinearLayoutManager(appCompatActivity, LinearLayoutManager.HORIZONTAL, false);
                itemViewHolder.recyclerView.setLayoutManager(layoutManager);
                itemViewHolder.recyclerView.setAdapter(itemName.getSuggestionContactsAdapter());
                RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
                itemAnimator.setAddDuration(500);
                itemAnimator.setRemoveDuration(500);
                itemViewHolder.recyclerView.setItemAnimator(itemAnimator);
            }else if(itemName.getCommonSummaryAdapter()!=null){
                itemViewHolder.summary_list_item.setVisibility(View.GONE);
                itemViewHolder.recyclerView.setVisibility(View.VISIBLE);
                itemViewHolder.image.setVisibility(View.GONE);
                GridLayoutManager layoutManager = new GridLayoutManager(appCompatActivity,1);
                itemViewHolder.recyclerView.setLayoutManager(layoutManager);
                itemViewHolder.recyclerView.setAdapter(itemName.getCommonSummaryAdapter());
                itemViewHolder.recyclerView.setNestedScrollingEnabled(false);
           /* RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(500);
            itemAnimator.setRemoveDuration(500);
            itemViewHolder.recyclerView.setItemAnimator(itemAnimator);*/
            }else if(itemName.getShareStatusAdapter()!=null){
                itemViewHolder.summary_list_item.setVisibility(View.GONE);
                itemViewHolder.recyclerView.setVisibility(View.VISIBLE);
                itemViewHolder.image.setVisibility(View.GONE);
                LinearLayoutManager layoutManager
                        = new LinearLayoutManager(appCompatActivity, LinearLayoutManager.HORIZONTAL, false);
                itemViewHolder.recyclerView.setLayoutManager(layoutManager);
                itemViewHolder.recyclerView.setAdapter(itemName.getShareStatusAdapter());
                RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
                itemAnimator.setAddDuration(500);
                itemAnimator.setRemoveDuration(500);
                itemViewHolder.recyclerView.setItemAnimator(itemAnimator);
            } else if(itemName.getCommonAnalyticsAdapter() !=null) {
                itemViewHolder.summary_list_item.setVisibility(View.GONE);
                itemViewHolder.recyclerView.setVisibility(View.VISIBLE);
                itemViewHolder.image.setVisibility(View.GONE);
                GridLayoutManager layoutManager = new GridLayoutManager(appCompatActivity,1);
                itemViewHolder.recyclerView.setLayoutManager(layoutManager);
                itemViewHolder.recyclerView.setAdapter(itemName.getCommonAnalyticsAdapter());
                RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
                itemAnimator.setAddDuration(500);
                itemAnimator.setRemoveDuration(500);
                itemViewHolder.recyclerView.setItemAnimator(itemAnimator);
                itemViewHolder.recyclerView.setNestedScrollingEnabled(false);
            } else if(itemName.getAllSectionAdapter() != null) {
                itemViewHolder.summary_list_item.setVisibility(View.GONE);
                itemViewHolder.recyclerView.setVisibility(View.VISIBLE);
                itemViewHolder.image.setVisibility(View.GONE);
                GridLayoutManager layoutManager = new GridLayoutManager(appCompatActivity,1);
                itemViewHolder.recyclerView.setLayoutManager(layoutManager);
                itemViewHolder.recyclerView.setAdapter(itemName.getAllSectionAdapter());
                RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
                itemAnimator.setAddDuration(500);
                itemAnimator.setRemoveDuration(500);
                itemViewHolder.recyclerView.setItemAnimator(itemAnimator);
                itemViewHolder.recyclerView.setNestedScrollingEnabled(false);
            } else if(itemName.getHomeSupplierSuggestionRatingAdapter()!=null){
                itemViewHolder.summary_list_item.setVisibility(View.GONE);
                itemViewHolder.recyclerView.setVisibility(View.VISIBLE);
                itemViewHolder.image.setVisibility(View.GONE);
                LinearLayoutManager layoutManager
                        = new LinearLayoutManager(appCompatActivity, LinearLayoutManager.HORIZONTAL, false);
                itemViewHolder.recyclerView.setLayoutManager(layoutManager);
                itemViewHolder.recyclerView.setAdapter(itemName.getHomeSupplierSuggestionRatingAdapter());
                RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
                itemAnimator.setAddDuration(500);
                itemAnimator.setRemoveDuration(500);
                itemViewHolder.recyclerView.setItemAnimator(itemAnimator);
            }
            else {
                itemViewHolder.image.setVisibility(View.GONE);
                itemViewHolder.recyclerView.setVisibility(View.GONE);
                itemViewHolder.summary_list_item.setVisibility(View.VISIBLE);
                Log.i("TAG", "onBindViewHolder: "+itemName.getText());
                itemViewHolder.catalog_text.setText(itemName.getText());
                itemViewHolder.catalog_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (itemName.getUniqueID().equals("Add Catalog")) {
                            HashMap<String,String> param = new HashMap<String, String>();
                            param.put("ctype","mycatalog");
                            param.put("type","catalog");
                            new DeepLinkFunction(param,appCompatActivity);
                        }
                    }
                });

                switch (itemName.getUniqueID()) {
                    case "Add Brand":
                        ((ItemViewHolder) holder).upload_more.setText("Upload");
                        ((ItemViewHolder) holder).upload_text.setText("Brand");
                        ((ItemViewHolder) holder).upload.setText("Uploaded");
                        break;
                    case "Add Product":
                        ((ItemViewHolder) holder).upload_more.setText("Add new");
                        ((ItemViewHolder) holder).upload_text.setText("Product");
                        ((ItemViewHolder) holder).upload.setText("Product");
                        break;
                    case "Selected Products":
                        ((ItemViewHolder) holder).upload_more.setText("View");
                        ((ItemViewHolder) holder).upload_text.setText("Products");
                        ((ItemViewHolder) holder).upload.setText("in Selection");
                }

                itemViewHolder.catalog_upload_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (itemName.getUniqueID()) {
                            case "Add Brand":
                                Application_Singleton.CONTAINER_TITLE = "Add Brand";
                                Application_Singleton.CONTAINERFRAG = new Fragment_AddBrand();
                                //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                                Application_Singleton.trackEvent("Home", "Click", "Add Brand");
                                StaticFunctions.switchActivity(appCompatActivity, OpenContainer.class);
                                break;
                            case "Add Catalog":
                                if(!UserInfo.getInstance(appCompatActivity).isCompanyProfileSet()
                                        && UserInfo.getInstance(appCompatActivity).getCompanyname().isEmpty()) {
                                    HashMap<String,String> param =  new HashMap<String,String>();
                                    param.put("type","profile_update");
                                    new DeepLinkFunction(param,appCompatActivity);
                                    return;
                                }
                              /*  Application_Singleton.CONTAINER_TITLE = "Add Catalog";
                                Application_Singleton.CONTAINERFRAG = new Fragment_AddCatalog();*/
                                //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                                Application_Singleton.trackEvent("Home", "Click", "Add Catalog");
                                appCompatActivity.startActivityForResult(new Intent(appCompatActivity, Activity_AddCatalog.class),Application_Singleton.ADD_CATALOG_REQUEST);
                                WishbookTracker.sendScreenEvents(appCompatActivity,WishbookEvent.SELLER_EVENTS_CATEGORY,"CatalogItem_Add_screen","home page",null);


                                break;
                            case "Selected Products":
                                Application_Singleton.CONTAINER_TITLE = "Add Selection";
                                Application_Singleton.CONTAINERFRAG = new Fragment_ProductSelections();
                                //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                                Application_Singleton.trackEvent("Home", "Click", "Add Selection");
                                StaticFunctions.switchActivity(appCompatActivity, OpenContainer.class);

                                break;

                        }

                    }
                });
            }
        }catch (NullPointerException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return allData.size();
    }


    public interface OnItemTapListener {
        void onItemSelect(String itemname);
    }

    public RecyclerViewSectionAdapter(AppCompatActivity appCompatActivity,List<SummaryModel> data) {
        this.allData = data;
        this.appCompatActivity=appCompatActivity;

    }


 /*   @Override
    public int getSectionCount() {
        return allData.size();
    }

    @Override
    public int getItemCount(int section) {
        return allData.get(section).getSummary_subs().size();
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int section) {
        String sectionName = allData.get(section).getHeaderTitle();
        SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
        sectionViewHolder.sectionTitle.setText(sectionName);
    }*/

   /* @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int section, final int relativePosition, int absolutePosition) {

        final ArrayList<Summary_Sub> itemsInSection = allData.get(section).getSummary_subs();

        final Summary_Sub itemName = itemsInSection.get(relativePosition);

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

        if(itemName.getSuggestionContactsAdapter()!=null)
        {
            itemViewHolder.summary_list_item.setVisibility(View.GONE);
            itemViewHolder.recyclerView.setVisibility(View.VISIBLE);
            itemViewHolder.image.setVisibility(View.GONE);
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(appCompatActivity, LinearLayoutManager.HORIZONTAL, false);
            itemViewHolder.recyclerView.setLayoutManager(layoutManager);
             itemViewHolder.recyclerView.setAdapter(itemName.getSuggestionContactsAdapter());
            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(500);
            itemAnimator.setRemoveDuration(500);
            itemViewHolder.recyclerView.setItemAnimator(itemAnimator);
        }else if(itemName.getCommonSummaryAdapter()!=null){
            itemViewHolder.summary_list_item.setVisibility(View.GONE);
            itemViewHolder.recyclerView.setVisibility(View.VISIBLE);
            itemViewHolder.image.setVisibility(View.GONE);
            GridLayoutManager layoutManager = new GridLayoutManager(appCompatActivity,1);
            itemViewHolder.recyclerView.setLayoutManager(layoutManager);
            itemViewHolder.recyclerView.setAdapter(itemName.getCommonSummaryAdapter());
            itemViewHolder.recyclerView.setNestedScrollingEnabled(false);
           *//* RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(500);
            itemAnimator.setRemoveDuration(500);
            itemViewHolder.recyclerView.setItemAnimator(itemAnimator);*//*
        }else if(itemName.getShareStatusAdapter()!=null){
            itemViewHolder.summary_list_item.setVisibility(View.GONE);
            itemViewHolder.recyclerView.setVisibility(View.VISIBLE);
            itemViewHolder.image.setVisibility(View.GONE);
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(appCompatActivity, LinearLayoutManager.HORIZONTAL, false);
            itemViewHolder.recyclerView.setLayoutManager(layoutManager);
            itemViewHolder.recyclerView.setAdapter(itemName.getShareStatusAdapter());
            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(500);
            itemAnimator.setRemoveDuration(500);
            itemViewHolder.recyclerView.setItemAnimator(itemAnimator);
        }
        else {
            itemViewHolder.image.setVisibility(View.GONE);
            itemViewHolder.recyclerView.setVisibility(View.GONE);
            itemViewHolder.summary_list_item.setVisibility(View.VISIBLE);
            itemViewHolder.catalog_text.setText(itemName.getText());
            itemViewHolder.catalog_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(itemName.getUniqueID().equals("Add Catalog")){
                        Application_Singleton.CONTAINER_TITLE="My Catalog";
                        Application_Singleton.CONTAINERFRAG=new Fragment_Catalogs();
                        //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                        StaticFunctions.switchActivity(appCompatActivity, OpenContainer.class);
                    }
                }
            });

            switch (itemName.getUniqueID()){
                case "Add Brand":
                    ((ItemViewHolder) holder).upload_more.setText("Upload");
                    ((ItemViewHolder) holder).upload_text.setText("Brand");
                    ((ItemViewHolder) holder).upload.setText("Uploaded");
                    break;
                case "Add Catalog":
                    ((ItemViewHolder) holder).upload_more.setText("Upload more");
                    ((ItemViewHolder) holder).upload_text.setText("Catalogs");
                    ((ItemViewHolder) holder).upload.setText("Uploaded");
                    break;
                case "Selected Products":
                    ((ItemViewHolder) holder).upload_more.setText("View");
                    ((ItemViewHolder) holder).upload_text.setText("Products");
                    ((ItemViewHolder) holder).upload.setText("in Selection");
            }

            itemViewHolder.catalog_upload_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (itemName.getUniqueID()){
                        case "Add Brand":
                            Application_Singleton.CONTAINER_TITLE="Add Brand";
                            Application_Singleton.CONTAINERFRAG=new Fragment_AddBrand();
                            //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                            StaticFunctions.switchActivity(appCompatActivity, OpenContainer.class);
                            break;
                        case "Add Catalog":
                            Application_Singleton.CONTAINER_TITLE="Add Catalog";
                            Application_Singleton.CONTAINERFRAG=new Fragment_AddCatalog();
                            //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                            StaticFunctions.switchActivity(appCompatActivity, OpenContainer.class);
                            break;
                        case "Selected Products":
                            Application_Singleton.CONTAINER_TITLE="Add Selection";
                            Application_Singleton.CONTAINERFRAG=new Fragment_ProductSelections();
                            //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                            StaticFunctions.switchActivity(appCompatActivity, OpenContainer.class);

                            break;

                    }

                }
            });

//        String dec=itemName.getText()
//                +" <a href='#'><u>"+itemName.getRedirectorText()+"</u></a>";


//        CharSequence sequence = Html.fromHtml(dec);
//        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
//        UnderlineSpan[] underlines = strBuilder.getSpans(0, 10, UnderlineSpan.class);
//        for(UnderlineSpan span : underlines) {
//            int start = strBuilder.getSpanStart(span);
//            int end = strBuilder.getSpanEnd(span);
//            int flags = strBuilder.getSpanFlags(span);
////            ClickableSpan myActivityLauncher = new ClickableSpan() {
////                public void onClick(View view) {
////                    Log.v("test","clicked");
////                    appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main,itemsInSection.get(relativePosition).getRedirector()).commit();
////                }
////            };
//         //   strBuilder.setSpan(myActivityLauncher, start, end, flags);
//        }
           // itemViewHolder.itemTitle.setText(itemName.getText());
        *//*itemViewHolder.itemTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        Application_Singleton.CONTAINER_TITLE=itemName.getRedirectorText1();
                        Application_Singleton.CONTAINERFRAG=itemsInSection.get(relativePosition).getRedirector1();
                        //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                        StaticFunctions.switchActivity(appCompatActivity, OpenContainer.class);
            }
        });*//*
       *//* itemViewHolder.btn_click.setText(itemName.getRedirectorText());
        itemViewHolder.btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("test", "clicked");
                if ( itemName.getRedirectorText().equals("View all Share status")) {
                    Fragment_HomeHolder.viewPager.setCurrentItem(1);
                } else {
                    Application_Singleton.CONTAINER_TITLE=itemName.getUniqueID();

                    Application_Singleton.CONTAINERFRAG=itemsInSection.get(relativePosition).getRedirector();
                    StaticFunctions.switchActivity(appCompatActivity, OpenContainer.class);
                    //appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector()).addToBackStack(null).commit();
                }
            }
        });

        if(itemName.getRedirectorText1()!=null&&itemName.getRedirector1()!=null){
           itemViewHolder.btn_click1.setText(itemName.getRedirectorText1());
            itemViewHolder.btn_click1.setVisibility(View.VISIBLE);
            itemViewHolder.btn_click1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Application_Singleton.CONTAINER_TITLE=itemName.getRedirectorText1();
                    Application_Singleton.CONTAINERFRAG=itemsInSection.get(relativePosition).getRedirector1();
           //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                    StaticFunctions.switchActivity(appCompatActivity, OpenContainer.class);

               }
            });
        }
        else {
            itemViewHolder.btn_click1.setVisibility(View.GONE);
        }*//*

//        itemViewHolder.itemTitle.setLinksClickable(true);
//        itemViewHolder.itemTitle.setMovementMethod(new BaseMovementMethod());

            // itemViewHolder.itemImage.setBackgroundColor(Color.parseColor("#01579b"));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, boolean header) {
        View v = null;
        if (header) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_section, parent, false);
            return new SectionViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            return new ItemViewHolder(v);
        }
    }*/


    public class RoundedBackgroundSpan extends ReplacementSpan {

        private  int CORNER_RADIUS = 8;
        private int backgroundColor = 0;
        private int textColor = 0;

        public RoundedBackgroundSpan(Context context) {
            super();
            backgroundColor = context.getResources().getColor(R.color.color_primary);
            textColor = context.getResources().getColor(R.color.white);
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            RectF rect = new RectF(x, top, x + measureText(paint, text, start, end), bottom);
            paint.setColor(backgroundColor);
            canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, paint);
            paint.setColor(textColor);
            canvas.drawText(text, start, end, x, y, paint);
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            return Math.round(paint.measureText(text, start, end));
        }

        private float measureText(Paint paint, CharSequence text, int start, int end) {
            return paint.measureText(text, start, end);
        }
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {


        final TextView sectionTitle;

        public SectionViewHolder(View itemView) {
            super(itemView);

            sectionTitle = (TextView) itemView.findViewById(R.id.sectionTitle);


        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        final TextView catalog_text,upload_text,upload_more,upload,txt_title;
        private LinearLayout catalog_container;
        private LinearLayout catalog_upload_container;
        private final LinearLayout summary_list_item;
        private final RecyclerView recyclerView;
        private ImageView image;
        private RelativeLayout container;



        public ItemViewHolder(View itemView) {
            super(itemView);
            container = (RelativeLayout) itemView.findViewById(R.id.container);
            txt_title = (TextView) itemView.findViewById(R.id.txt_title);
             summary_list_item = (LinearLayout) itemView.findViewById(R.id.summary_list_item);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.suggestion_recyclerview);
            image = (ImageView) itemView.findViewById(R.id.image);
            catalog_text = (TextView) itemView.findViewById(R.id.catalog_text);
            upload_text = (TextView) itemView.findViewById(R.id.upload_text);
            upload_more = (TextView) itemView.findViewById(R.id.upload_more);
            upload = (TextView) itemView.findViewById(R.id.upload);
            catalog_container = (LinearLayout) itemView.findViewById(R.id.catalog_container);
            catalog_upload_container = (LinearLayout) itemView.findViewById(R.id.catalog_upload_container);


        }
    }



}