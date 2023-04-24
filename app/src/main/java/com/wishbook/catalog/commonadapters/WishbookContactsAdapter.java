package com.wishbook.catalog.commonadapters;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.MyContacts;
import com.wishbook.catalog.commonmodels.NameValues;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prane on 20-03-2016.
 */
public class WishbookContactsAdapter extends RecyclerView.Adapter<WishbookContactsAdapter.MyViewHolder> implements SectionTitleProvider {

    private List<MyContacts> contactList;
    private Activity mActivity;
    public void setData(List<MyContacts> filteredModelList) {
        this.contactList=filteredModelList;
        notifyDataSetChanged();
    }

    @Override
    public String getSectionTitle(int position) {
        return contactList.get(position).getName().substring(0, 1);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView contact_img;
        //private final TextView contact_company;
        private final TextView contacttype;
        public TextView contactName, contactNumber;
        public CheckBox contactCheckedCb;
        private ImageView chat;
        public LinearLayout main_container;

        public MyViewHolder(View view) {
            super(view);
            contactCheckedCb = (CheckBox) view.findViewById(R.id.contact_cb);
            contactName = (TextView) view.findViewById(R.id.contact_name);
            //contact_company = (TextView) view.findViewById(R.id.contact_company);
            contactNumber = (TextView) view.findViewById(R.id.contact_number);
            contact_img = (ImageView) view.findViewById(R.id.textInt);
            contacttype = (TextView) view.findViewById(R.id.contacttype);
            chat = (ImageView) view.findViewById(R.id.chat);
            main_container = (LinearLayout) view.findViewById(R.id.main_container);

        }
    }


    public WishbookContactsAdapter(Activity mActivity, List<MyContacts> contactList) {
        this.mActivity = mActivity;
        this.contactList = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_row, parent, false);

        return new MyViewHolder(itemView);
    }

    public List<NameValues> getAllSelectedContacts(){
        List<NameValues> contactsselected=new ArrayList<>();
        for (int i = contactList.size() - 1; i >= 0; i--) {
            MyContacts model = contactList.get(i);
            if (model.isContactChecked()) {
                contactsselected.add(new NameValues(model.getName(),model.getPhone()));
            }
        }
        return contactsselected;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            final RoundedBitmapDrawable[] dr = new RoundedBitmapDrawable[1];
            final MyContacts contact = contactList.get(position);
            holder.contactName.setText(StringUtils.capitalize(contact.getName().toLowerCase().trim()));
            holder.contactNumber.setText(contact.getPhone());
            holder.main_container.setEnabled(false);


            if(contact.getCompany_name().toLowerCase().trim().equals("invited"))
            {
              //  holder.contact_company.setText(StringUtils.capitalize(contact.getCompany_name().toLowerCase().trim()));
              //  holder.contact_company.setTypeface(null, Typeface.ITALIC);
                holder.chat.setVisibility(View.INVISIBLE);
            }
            else
            {
              //  holder.contact_company.setText(StringUtils.capitalize(contact.getCompany_name().toLowerCase().trim()));
             //   holder.contact_company.setTypeface(null, Typeface.NORMAL);
                holder.chat.setVisibility(View.VISIBLE);
            }

            holder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, ConversationActivity.class);
                    intent.putExtra(ConversationUIService.USER_ID, contact.getChat_user().toString());
                    intent.putExtra(ConversationUIService.DISPLAY_NAME, contact.getName().toString()); //put it for displaying the title.
                    mActivity.startActivity(intent);
                }
            });
           // holder.textInt.setImageDrawable(drawable);
            final ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(mActivity, R.animator.flipping);
            anim.setTarget(holder.contact_img);
            anim.setDuration(500);



            ImageLoader.getInstance().displayImage(contact.getCompany_image(),holder.contact_img, Application_Singleton.options,new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap src) {
                    super.onLoadingComplete(imageUri, view, src);
                    dr[0] =
                            RoundedBitmapDrawableFactory.create(mActivity.getResources(), src);
                    dr[0].setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 2.0f);
                    if(contactList.get(position).isContactChecked())
                    {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            holder.contact_img.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.contactselection));
                        }
                    }
                    else
                    {
                        holder.contact_img.setImageDrawable(dr[0]);
                    }

                }
            });

            if(contactList.get(position).isContactChecked())
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.contact_img.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.contactselection));
                }
                holder.contactCheckedCb.setChecked(true);
                holder.main_container.setBackgroundColor(Color.parseColor("#e9f6fe"));
            }
            else
            {
                holder.contact_img.setImageDrawable(dr[0]);
                holder.contactCheckedCb.setChecked(false);
                holder.main_container.setBackgroundColor(Color.parseColor("#ffffff"));
            }
            holder.main_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        anim.start();
                        if (contactList.get(position).isContactChecked()) {
                            contactList.get(position).setIsContactChecked(false);
                            holder.contactCheckedCb.setChecked(false);
                            holder.main_container.setBackgroundColor(Color.parseColor("#ffffff"));
                            holder.contact_img.setImageDrawable(dr[0]);
                            //   notifyDataSetChanged();
                        } else {
                            contactList.get(position).setIsContactChecked(true);
                            holder.contactCheckedCb.setChecked(true);
                            holder.main_container.setBackgroundColor(Color.parseColor("#e9f6fe"));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                holder.contact_img.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.contactselection));
                            }
                            //  notifyDataSetChanged();
                        }
                    }
            });
            if(!contactList.get(position).getConnected_as().equals("")){
                holder.contacttype.setVisibility(View.VISIBLE);
                holder.contacttype.setText(StringUtils.capitalize(contactList.get(position).getConnected_as()));
            }
            else
            {
                holder.main_container.setEnabled(true);
                holder.contacttype.setVisibility(View.GONE);
            }



//            Picasso.with(mActivity).load(contact.getCompany_image()).resize(200, 200)
//                    .transform(new CircleTransform()).into(holder.contact_img);
        }
        catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }





}
