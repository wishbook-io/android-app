package com.wishbook.catalog.commonadapters;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
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
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.home.contacts.Fragment_MyContacts;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prane on 20-03-2016.
 */
public class MyContactAdapter extends RecyclerView.Adapter<MyContactAdapter.MyViewHolder> implements SectionTitleProvider {

    private List<Contact> contactList;
    private Activity mActivity;
    private FragmentManager manager;
    private Boolean showOnNetworkTutorial = true;
    private Boolean showNotOnNetworkTutorial = true;
    private String TypeOfContacts = "";

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


    public MyContactAdapter(Activity mActivity, List<Contact> contactList, FragmentManager supportFragmentManager, String type) {
        this.mActivity = mActivity;
        this.contactList = contactList;
        this.manager = supportFragmentManager;
        this.TypeOfContacts = type;
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
        holder.invite_button.setEnabled(false);
        holder.invite_button.setClickable(false);

        //new thing added
        if (contact.isWishbookContact) {
            holder.invite_button.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.button_edge_less_padding_green));
            holder.invite_button.setText("Add to network");
            holder.invite_button.setTextColor(ContextCompat.getColor(mActivity, R.color.approved));
            holder.contact_status.setText("Status: Already on Wishbook");
            if(contact.getCompanyName()!=null) {
                holder.contact_company_name.setVisibility(View.VISIBLE);
                holder.contact_company_name.setText("("+contact.getCompanyName()+")");
            } else {
                holder.contact_company_name.setVisibility(View.GONE);
            }



            if (showOnNetworkTutorial&& position>6) {
                showOnNetworkTutorial = false;
                if (TypeOfContacts.equals("buyer")) {
                   // StaticFunctions.checkAndShowTutorialNoTouch(mActivity, "buyer_all_add_network_tutorial", holder.invite_button, mActivity.getString(R.string.buyer_all_add_network_tutorial), "left",623);
                }

                if (TypeOfContacts.equals("supplier")) {
                  //  StaticFunctions.checkAndShowTutorialNoTouch(mActivity, "supplier_all_add_network_tutorial", holder.invite_button, mActivity.getString(R.string.supplier_all_add_network_tutorial), "bottom",2345);
                }
            }


        } else {
            holder.invite_button.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.button_edge_less_padding_blue));
            holder.invite_button.setText("Send Invite");
            holder.invite_button.setTextColor(ContextCompat.getColor(mActivity, R.color.color_primary));
            holder.contact_status.setText("Status: Not on Wishbook");
            holder.contact_company_name.setVisibility(View.GONE);

            if (showNotOnNetworkTutorial&& position>6) {
                showNotOnNetworkTutorial = false;
                if (TypeOfContacts.equals("buyer")) {
                    //StaticFunctions.checkAndShowTutorialNoTouch(mActivity, "buyer_invite_tutorial", holder.invite_button, mActivity.getString(R.string.buyer_invite_tutorial), "bottom",463);
                }
                if (TypeOfContacts.equals("supplier")) {
                    //StaticFunctions.checkAndShowTutorialNoTouch(mActivity, "supplier_invite_tutorial", holder.invite_button, mActivity.getString(R.string.supplier_invite_tutorial), "bottom",888);
                }
            }

        }

        holder.contactName.setText(StringUtils.capitalize(contact.getContactName().toLowerCase().trim()));
        holder.contactNumber.setText(StringUtils.capitalize(contact.getContactNumber().toLowerCase().trim()));
        holder.contactCheckedCb.setOnCheckedChangeListener(null);

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
            holder.invite_button.setVisibility(View.INVISIBLE);

        } else {
            holder.invite_button.setVisibility(View.VISIBLE);
            holder.textInt.setImageDrawable(drawable);
            holder.contactCheckedCb.setChecked(false);
            holder.main_container.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        holder.main_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(UserInfo.getInstance(mActivity).isGuest()) {
                    StaticFunctions.ShowRegisterDialog(mActivity,"");
                    return;
                }
                anim.start();
                if (contactList.get(holder.getAdapterPosition()).isContactChecked()) {
                    holder.invite_button.setVisibility(View.VISIBLE);
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
                    if (StaticFunctions.selectedContacts.size() > 0) {
                        Fragment_MyContacts.select_all_container.setVisibility(View.VISIBLE);
                    } else {
                        Fragment_MyContacts.select_all_container.setVisibility(View.GONE);
                    }
                    //  StaticFunctions.selectedContacts.remove(new NameValues(contactList.get(position).getContactName(),contactList.get(position).getContactNumber()));
                    //   notifyDataSetChanged();
                } else {
                    holder.invite_button.setVisibility(View.INVISIBLE);
                    contactList.get(holder.getAdapterPosition()).setIsContactChecked(true);
                    holder.contactCheckedCb.setChecked(true);
                    holder.main_container.setBackgroundColor(Color.parseColor("#e9f6fe"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        holder.textInt.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.contactselection));
                    }
                    StaticFunctions.selectedContacts.add(new NameValues(contactList.get(holder.getAdapterPosition()).getContactName(), contactList.get(holder.getAdapterPosition()).getContactNumber()));

                    //  notifyDataSetChanged();

                    //showing and hiding select all contacts
                    if (StaticFunctions.selectedContacts.size() > 0) {
                        Fragment_MyContacts.select_all_container.setVisibility(View.VISIBLE);
                    } else {
                        Fragment_MyContacts.select_all_container.setVisibility(View.GONE);
                    }
                }
            }
        });

        holder.contactNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.getContactNumber().toLowerCase().trim()));
                mActivity.startActivity(intent);
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

    public void removeContactsAlreadyInvited() {
        for (int i = 0; i < contactList.size(); i++) {
            if(contactList.get(i).isContactChecked()){
                contactList.remove(i);
            }
        }
        notifyDataSetChanged();
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
