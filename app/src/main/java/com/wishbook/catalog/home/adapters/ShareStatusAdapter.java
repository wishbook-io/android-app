package com.wishbook.catalog.home.adapters;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;


import org.apache.commons.lang3.StringUtils;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_ShareStatus;
import com.wishbook.catalog.home.catalog.details.Fragment_ShareStatus_Details;

import java.util.ArrayList;

public class ShareStatusAdapter extends RecyclerView.Adapter<ShareStatusAdapter.CustomViewHolder> {

    private ArrayList<Response_ShareStatus> feedItemList;
    private AppCompatActivity mContext;

    public ShareStatusAdapter(AppCompatActivity context,ArrayList<Response_ShareStatus> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;



    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private final TextView cat_name;
        private final TextView npeople;
        private final TextView nprods;
        private final TextView nopened;
        private final SimpleDraweeView cat_img;
        private final TextView status_tex;
        private final TextView productsviewed;
        private final CardView push_details;

        public CustomViewHolder(View view) {
            super(view);
             cat_name = (TextView) view.findViewById(R.id.cat_name);
             npeople = (TextView) view.findViewById(R.id.npeople);
             nprods = (TextView) view.findViewById(R.id.nprods);
             nopened = (TextView) view.findViewById(R.id.nopened);
            productsviewed = (TextView) view.findViewById(R.id.productsviewed);
            cat_img= (SimpleDraweeView) view.findViewById(R.id.cat_img);
            status_tex=(TextView)view.findViewById(R.id.status_tex);
            push_details = (CardView) view.findViewById(R.id.push_details);


        }
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sharestatus_item, viewGroup,false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, final int i) {
        customViewHolder.cat_name.setText(StringUtils.capitalize(feedItemList.get(i).getTitle().toLowerCase().trim()));
        customViewHolder.npeople.setText(StringUtils.capitalize(feedItemList.get(i).getTotal_users().toLowerCase().trim()));
        customViewHolder.nprods.setText(StringUtils.capitalize(feedItemList.get(i).getTotal_products().toLowerCase().trim()));
        customViewHolder.nopened.setText(StringUtils.capitalize(feedItemList.get(i).getTotal_viewed().toLowerCase().trim()));
        customViewHolder.productsviewed.setText(StringUtils.capitalize(feedItemList.get(i).getTotal_products_viewed().toLowerCase().trim()));
        customViewHolder.status_tex.setText(StringUtils.capitalize(feedItemList.get(i).getStatus().toLowerCase().trim()));
        String image = feedItemList.get(i).getImage();
        if (image != null & !image.equals("")) {
            //StaticFunctions.loadImage(mContext,image,customViewHolder.cat_img,R.drawable.uploadempty);
            //Picasso.with(mContext).load(image).into(customViewHolder.cat_img);
            StaticFunctions.loadFresco(mContext,image,customViewHolder.cat_img);
        }

        customViewHolder.push_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Bundle bundle = new Bundle();
                bundle.putString("catalog",feedItemList.get(i).getTitle());
                bundle.putString("id",feedItemList.get(i).getId());
                Fragment_ShareStatus_Details fragment_shareStatus_details = new Fragment_ShareStatus_Details();
                fragment_shareStatus_details.setArguments(bundle);
                mContext.getSupportFragmentManager().beginTransaction().add(R.id.content_main, fragment_shareStatus_details).commit();
*/
                Application_Singleton.response_shareStatus = feedItemList.get(i) ;
                Application_Singleton.CONTAINER_TITLE = "Shared Details";
                Application_Singleton.CONTAINERFRAG = new Fragment_ShareStatus_Details();
                Intent intent=new Intent(mContext,OpenContainer.class);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedItemList.size();
    }
}