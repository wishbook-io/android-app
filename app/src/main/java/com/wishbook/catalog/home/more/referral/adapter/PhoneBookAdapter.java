package com.wishbook.catalog.home.more.referral.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.TextDrawable;
import com.wishbook.catalog.Utils.util.ColorGenerator;
import com.wishbook.catalog.commonmodels.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PhoneBookAdapter extends RecyclerView.Adapter<PhoneBookAdapter.ContactViewHolder> {
    private List<Contact> contactVOList;
    private List<Contact> contactListClone;
    private Context mContext;


    public PhoneBookAdapter(List<Contact> contactVOList, Context mContext) {
        this.contactVOList = contactVOList;
        contactListClone = new ArrayList<>();
        contactListClone.addAll(contactVOList);
        this.mContext = mContext;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_contact_view, null);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        final Contact contact = contactVOList.get(position);
        if (contact != null && contact.getContactNumber() != null && !contact.getContactNumber().isEmpty()) {
            holder.contact_cb.setOnCheckedChangeListener(null);
            if (contact.getContactName() != null && contact.getContactName().length() > 1) {
                final TextDrawable drawable = TextDrawable.builder()
                        .buildRound(contact.getContactName().substring(0, 1), ColorGenerator.MATERIAL.getRandomColor());
                holder.textInt.setImageDrawable(drawable);
            } else {
                final TextDrawable drawable = TextDrawable.builder()
                        .buildRound("", ColorGenerator.MATERIAL.getRandomColor());
                holder.textInt.setImageDrawable(drawable);
            }


            holder.tvContactName.setText(contact.getContactName());
            holder.tvPhoneNumber.setText(contact.getContactNumber());
            if (contact.isContactChecked()) {
                holder.contact_cb.setChecked(true);
            } else {
                holder.contact_cb.setChecked(false);
            }

            holder.contact_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        contact.setContactChecked(true);
                    } else {
                        contact.setContactChecked(false);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return contactVOList.size();
    }

    public void selectAllContacts() {

        for (int i = 0; i < contactVOList.size(); i++) {
            contactVOList.get(i).setContactChecked(true);

        }
        notifyDataSetChanged();
    }


    public void selectNoneContacts() {
        for (Contact c :
                contactVOList) {
            c.setContactChecked(false);
        }
        notifyDataSetChanged();
    }

    public ArrayList<Contact> getSelectedContacts() {
        ArrayList<Contact> temp = new ArrayList<>();
        for (Contact c :
                contactVOList) {
            if (c.isContactChecked) {
                temp.add(c);
            }

        }
        return temp;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        contactVOList.clear();
        if (charText.length() == 0) {
            contactVOList.addAll(contactListClone);
        } else {
            for (Contact wp : contactListClone) {
                if (wp.getContactName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    contactVOList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        ImageView textInt;
        TextView tvContactName;
        TextView tvPhoneNumber;
        CheckBox contact_cb;

        public ContactViewHolder(View itemView) {
            super(itemView);
            textInt = (ImageView) itemView.findViewById(R.id.textInt);
            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);
            contact_cb = itemView.findViewById(R.id.contact_cb);
        }
    }
}
