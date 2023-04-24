package com.wishbook.catalog.commonadapters;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MyContactAdapter1 extends RecyclerView.Adapter<MyContactAdapter1.MyViewHolder> implements SectionTitleProvider {

    private List<Contact> contactList;
    private Activity mActivity;
    private FragmentManager manager;
    private Boolean showOnNetworkTutorial = true;
    private Boolean showNotOnNetworkTutorial = true;
    private String TypeOfContacts = "";
    private SelectedListener selectedListener;
    private ArrayList<Contact> arraylist;

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
        private final AppCompatButton invite_button;
        public TextView contactName, contactNumber, contact_status, contact_company_name;
        public CheckBox contactCheckedCb;
        public LinearLayout main_container;


        public MyViewHolder(View view) {
            super(view);
            contactName = (TextView) view.findViewById(R.id.contact_name);
            contactNumber = (TextView) view.findViewById(R.id.contact_number);
            contact_status = (TextView) view.findViewById(R.id.contact_status);
            contact_company_name = view.findViewById(R.id.contact_company_name);
            contactCheckedCb = (CheckBox) view.findViewById(R.id.contact_cb);
            textInt = (ImageView) view.findViewById(R.id.textInt);
            invite_button = (AppCompatButton) view.findViewById(R.id.invite_button);
            main_container = (LinearLayout) view.findViewById(R.id.main_container);
        }
    }


    public MyContactAdapter1(Activity mActivity, List<Contact> contactList, FragmentManager supportFragmentManager, String type) {
        this.mActivity = mActivity;
        this.contactList = contactList;
        this.manager = supportFragmentManager;
        this.TypeOfContacts = type;
        this.arraylist = new ArrayList<Contact>();
        this.arraylist.addAll(contactList);
        StaticFunctions.selectedContacts.clear();
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
        holder.invite_button.setVisibility(View.GONE);
        holder.contact_status.setVisibility(View.GONE);

        //new thing added
        if (contact.isWishbookContact) {
            holder.contact_status.setText("Status: Already on Wishbook");
            if (contact.getCompanyName() != null) {
                holder.contact_company_name.setVisibility(View.VISIBLE);
                holder.contact_company_name.setText("(" + contact.getCompanyName() + ")");
            } else {
                holder.contact_company_name.setVisibility(View.GONE);
            }
        } else {
            holder.contact_status.setText("Status: Not on Wishbook");
            holder.contact_company_name.setVisibility(View.GONE);

            if (showNotOnNetworkTutorial && position > 6) {
                showNotOnNetworkTutorial = false;
                if (TypeOfContacts.equals("buyer")) {
                    //StaticFunctions.checkAndShowTutorialNoTouch(mActivity, "buyer_invite_tutorial", holder.invite_button, mActivity.getString(R.string.buyer_invite_tutorial), "bottom",463);
                }
                if (TypeOfContacts.equals("supplier")) {
                    //StaticFunctions.checkAndShowTutorialNoTouch(mActivity, "supplier_invite_tutorial", holder.invite_button, mActivity.getString(R.string.supplier_invite_tutorial), "bottom",888);
                }
            }

        }

        final TextDrawable drawable;
        if(contact.getContactName()!=null){
            holder.contactName.setText(StringUtils.capitalize(contact.getContactName().toLowerCase().trim()));
           drawable  = TextDrawable.builder()
                    .buildRound(contact.getContactName().substring(0, 1), ColorGenerator.MATERIAL.getRandomColor());
        } else {
            holder.contactName.setText("");
            drawable  = TextDrawable.builder()
                    .buildRound("", ColorGenerator.MATERIAL.getRandomColor());
        }

        if(contact.getContactNumber()!=null) {
            holder.contactNumber.setText(StringUtils.capitalize(contact.getContactNumber().toLowerCase().trim()));
        } else {
            holder.contactNumber.setText("");
        }

        holder.contactCheckedCb.setOnCheckedChangeListener(null);


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
                    if (selectedListener != null) {
                        Log.i("TAG", "onClick: Not Null");
                        selectedListener.getTotalSelected(StaticFunctions.selectedContacts.size());
                    } else {
                        Log.i("TAG", "onClick: Null");
                    }


                } else {
                    contactList.get(holder.getAdapterPosition()).setIsContactChecked(true);
                    holder.contactCheckedCb.setChecked(true);
                    holder.main_container.setBackgroundColor(Color.parseColor("#e9f6fe"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        holder.textInt.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.contactselection));
                    }
                    StaticFunctions.selectedContacts.add(new NameValues(contactList.get(holder.getAdapterPosition()).getContactName(), contactList.get(holder.getAdapterPosition()).getContactNumber()));
                    if (selectedListener != null) {
                        Log.i("TAG", "onClick: Not Null");
                        selectedListener.getTotalSelected(StaticFunctions.selectedContacts.size());
                    } else {
                        Log.i("TAG", "onClick: Null");
                    }
                }
            }
        });

      /*  holder.contactNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.getContactNumber().toLowerCase().trim()));
                mActivity.startActivity(intent);
            }
        });*/


    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        contactList.clear();
        if (charText.length() == 0) {
            contactList.addAll(arraylist);
        } else {
            for (Contact wp : arraylist) {
                String person_name = wp.getContactName() != null ? wp.getContactNumber() : "";
                if (person_name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    contactList.add(wp);
                }
            }

        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void setSelectedListener(SelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }

    public interface SelectedListener {
        void getTotalSelected(int count);
    }

}
