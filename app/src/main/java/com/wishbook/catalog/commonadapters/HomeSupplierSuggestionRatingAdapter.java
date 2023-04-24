package com.wishbook.catalog.commonadapters;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.MyContacts;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.ResponseCreditReference;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeSupplierSuggestionRatingAdapter extends RecyclerView.Adapter<HomeSupplierSuggestionRatingAdapter.CustomViewHolder> {


    private Context context;
    private ArrayList<MyContacts> myContacts;
    private String filter;
    private Fragment fragment;
    Type type = new TypeToken<ArrayList<MyContacts>>() {
    }.getType();

    public HomeSupplierSuggestionRatingAdapter(Context context, ArrayList<MyContacts> myContacts, Fragment fragment) {
        this.context = context;
        this.myContacts = myContacts;
        this.fragment = fragment;
    }

    @Override
    public HomeSupplierSuggestionRatingAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_suggestion_supplier_credit_item, parent, false);
        return new HomeSupplierSuggestionRatingAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeSupplierSuggestionRatingAdapter.CustomViewHolder holder, int position) {
        final MyContacts contacts = myContacts.get(position);
        if (contacts.getCompany_name() != null && !contacts.getCompany_name().isEmpty()) {
            holder.txt_supplier_name.setText(contacts.getCompany_name());
            holder.txt_first_word_init.setText(contacts.getCompany_name().substring(0, 1));
        }

        holder.txt_supplier_location.setText(contacts.getState_name() + "," + contacts.getCity_name());
        holder.btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserInfo.getInstance(context).isGuest()) {
                    StaticFunctions.ShowRegisterDialog(context,"Home Page");
                    return;
                }

                if (contacts.getCompany_id() != null) {
                    callRequestFeedback(context, contacts.getCompany_id(), contacts.getCompany_name(), holder.getAdapterPosition());
                }
            }
        });


        holder.img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MyContacts> localList = UserInfo.getInstance(context).getwishSuggestioncontacts();
                for(int i=0;i<localList.size();i++){
                    try {
                        if (localList.get(i).getPhone().equals(myContacts.get(holder.getAdapterPosition()).getPhone())) {
                            localList.get(i).setIs_visible(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                UserInfo.getInstance( context).setwishSuggestionContacts(new Gson().toJson(localList));
                myContacts.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(),getItemCount());
            }
        });


        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return myContacts.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.img_close)
        ImageView img_close;

        @BindView(R.id.txt_first_word_init)
        TextView txt_first_word_init;

        @BindView(R.id.txt_supplier_name)
        TextView txt_supplier_name;

        @BindView(R.id.txt_supplier_location)
        TextView txt_supplier_location;

        @BindView(R.id.btn_request)
        AppCompatButton btn_request;


        private HomeSupplierSuggestionRatingAdapter.ItemClickListener clickListener;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(HomeSupplierSuggestionRatingAdapter.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null)
                clickListener.onClick(view, getPosition(), false);
        }
    }


    public void callRequestFeedback(final Context context, String sellerCompanyid, String sellerCompanyName, final int position) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        HashMap<String, String> params = new HashMap<>();
        params.put("buying_company", UserInfo.getInstance(context).getCompany_id());
        params.put("selling_company", sellerCompanyid);
        params.put("selling_company_name", sellerCompanyName);
        params.put("buyer_requested", "true");
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.companyUrl(context, "add-credit-reference", ""), params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                final ResponseCreditReference creditReference = new Gson().fromJson(response, ResponseCreditReference.class);
                Toast.makeText(context, "Successfully requested for feedback", Toast.LENGTH_SHORT).show();
               /* ArrayList<MyContacts> contactList1 = UserInfo.getInstance(context).getwishSuggestioncontacts();
                if (contactList1.contains(myContacts.get(position))) {
                    int local_position = contactList1.indexOf(myContacts.get(position));
                    MyContacts temp = myContacts.get(position);
                    temp.setCredit_reference_id(creditReference.getId());
                    ArrayList<MyContacts> tempArray = contactList1;
                    tempArray.remove(local_position);
                    tempArray.add(local_position,temp);
                    UserInfo.getInstance(context).setwishSuggestionContacts(new Gson().toJson(tempArray));
                }*/

                ArrayList<MyContacts> localList = UserInfo.getInstance(context).getwishSuggestioncontacts();
                for(int i=0;i<localList.size();i++){
                    if(localList.get(i).getPhone().equals(myContacts.get(position).getPhone())){
                        MyContacts temp = localList.get(i);
                        temp.setCredit_reference_id(creditReference.getId());
                        localList.set(i,temp);
                    }
                }
                UserInfo.getInstance( context).setwishSuggestionContacts(new Gson().toJson(localList));
                myContacts.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,getItemCount());

            }

            @Override
            public void onResponseFailed(ErrorString error) {
             StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }
}
