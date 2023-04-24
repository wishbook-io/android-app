package com.wishbook.catalog.commonadapters;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.TextDrawable;
import com.wishbook.catalog.Utils.util.ColorGenerator;
import com.wishbook.catalog.commonmodels.Contact;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.home.contacts.Fragment_MyContacts;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prane on 20-03-2016.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> implements SectionTitleProvider {

    private List<Contact> contactList;
    private Activity mActivity;

    public void setData(List<Contact> filteredModelList) {
        this.contactList = filteredModelList;
        notifyDataSetChanged();
    }

    @Override
    public String getSectionTitle(int position) {
        return contactList.get(position).getContactName().substring(0, 1);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView textInt;
        public TextView contactName, contactNumber;
        public CheckBox contactCheckedCb;
        public LinearLayout main_container;

        public MyViewHolder(View view) {
            super(view);
            contactName = (TextView) view.findViewById(R.id.contact_name);
            contactNumber = (TextView) view.findViewById(R.id.contact_number);
            contactCheckedCb = (CheckBox) view.findViewById(R.id.contact_cb);
            textInt = (ImageView) view.findViewById(R.id.textInt);
            main_container = (LinearLayout) view.findViewById(R.id.main_container);
        }
    }


    public ContactsAdapter(Activity mActivity, List<Contact> contactList) {
        this.mActivity = mActivity;
        this.contactList = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_row, parent, false);

        return new MyViewHolder(itemView);
    }

    public List<NameValues> getAllSelectedContacts() {
        List<NameValues> contactsselected = new ArrayList<>();
        for (int i = contactList.size() - 1; i >= 0; i--) {
            final Contact model = contactList.get(i);
            if (model.isContactChecked()) {
                contactsselected.add(new NameValues(model.getContactName(), model.getContactNumber()));
            }
        }
        return contactsselected;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Contact contact = contactList.get(position);
        holder.contactName.setText(StringUtils.capitalize(contact.getContactName().toLowerCase().trim()));
        holder.contactNumber.setText(StringUtils.capitalize(contact.getContactNumber().toLowerCase().trim()));
        holder.contactCheckedCb.setOnCheckedChangeListener(null);
        // holder.contactCheckedCb.setChecked(contactList.get(position).isContactChecked());
        /*holder.contactCheckedCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                contactList.get(position).setIsContactChecked(isChecked);
            }
        });
*/
        final TextDrawable drawable = TextDrawable.builder()
                .buildRound(contact.getContactName().substring(0, 1), ColorGenerator.MATERIAL.getRandomColor());
        // holder.textInt.setImageDrawable(drawable);
        final ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(mActivity, R.animator.flipping);
        anim.setTarget(holder.textInt);
        anim.setDuration(300);

        if (contactList.get(position).isContactChecked()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.textInt.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.contactselection));
            }
            holder.contactCheckedCb.setChecked(true);
            holder.main_container.setBackgroundColor(Color.parseColor("#e9f6fe"));
        } else {
            holder.textInt.setImageDrawable(drawable);
            holder.contactCheckedCb.setChecked(false);
            holder.main_container.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        holder.main_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim.start();
                if (contactList.get(holder.getAdapterPosition()).isContactChecked()) {
                    contactList.get(holder.getAdapterPosition()).setIsContactChecked(false);
                    holder.contactCheckedCb.setChecked(false);
                    holder.main_container.setBackgroundColor(Color.parseColor("#ffffff"));
                    holder.textInt.setImageDrawable(drawable);

                    for (int i = 0; i < StaticFunctions.selectedContacts.size(); i++) {
                        if (StaticFunctions.selectedContacts.get(i).getPhone().equals(contactList.get(holder.getAdapterPosition()).getContactNumber())) {
                            StaticFunctions.selectedContacts.remove(i);
                            break;
                        }
                    }


                    //showing and hiding select all contacts
                    if(StaticFunctions.selectedContacts.size()>0){
                        Fragment_MyContacts.select_all_container.setVisibility(View.VISIBLE);
                    }else{
                        Fragment_MyContacts.select_all_container.setVisibility(View.GONE);
                    }
                    //  StaticFunctions.selectedContacts.remove(new NameValues(contactList.get(position).getContactName(),contactList.get(position).getContactNumber()));
                    //   notifyDataSetChanged();
                } else {
                    contactList.get(holder.getAdapterPosition()).setIsContactChecked(true);
                    holder.contactCheckedCb.setChecked(true);
                    holder.main_container.setBackgroundColor(Color.parseColor("#e9f6fe"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        holder.textInt.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.contactselection));
                    }
                    StaticFunctions.selectedContacts.add(new NameValues(contactList.get(holder.getAdapterPosition()).getContactName(), contactList.get(holder.getAdapterPosition()).getContactNumber()));

                    //  notifyDataSetChanged();

                    //showing and hiding select all contacts
                    if(StaticFunctions.selectedContacts.size()>0){
                        Fragment_MyContacts.select_all_container.setVisibility(View.VISIBLE);
                    }else{
                        Fragment_MyContacts.select_all_container.setVisibility(View.GONE);
                    }
                }
            }
        });


    }

    public void selectAllContacts() {
        StaticFunctions.selectedContacts.clear();
        for (int i = 0; i < contactList.size(); i++) {
            contactList.get(i).setIsContactChecked(true);
            StaticFunctions.selectedContacts.add(new NameValues(contactList.get(i).getContactName(), contactList.get(i).getContactNumber()));
            notifyDataSetChanged();
        }
    }

    public void removeSelectAllContacts() {
        StaticFunctions.selectedContacts.clear();
        for (int i = 0; i < contactList.size(); i++) {
            contactList.get(i).setIsContactChecked(false);
            notifyDataSetChanged();
        }
    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void animateTo(List<Contact> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<Contact> newModels) {
        for (int i = contactList.size() - 1; i >= 0; i--) {
            final Contact model = contactList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Contact> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Contact model = newModels.get(i);
            if (!contactList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Contact> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Contact model = newModels.get(toPosition);
            final int fromPosition = contactList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public void removeItem(int position) {
        final Contact model = contactList.remove(position);
        notifyItemRemoved(position);

    }

    public void addItem(int position, Contact model) {
        contactList.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Contact model = contactList.remove(fromPosition);
        contactList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

}
