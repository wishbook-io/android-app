package com.wishbook.catalog.home.contacts.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.responses.ResponseSuggestedBroker;
import com.wishbook.catalog.home.contacts.details.ActionLogApi;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SuggestedBrokerAdapter extends RecyclerView.Adapter<SuggestedBrokerAdapter.CustomViewHolder> {

    Context context;
    ArrayList<ResponseSuggestedBroker> responseSuggestedBrokers;

    public SuggestedBrokerAdapter(Context context, ArrayList<ResponseSuggestedBroker> responseSuggestedBrokers) {
        this.context = context;
        this.responseSuggestedBrokers = responseSuggestedBrokers;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggested_broker_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final ResponseSuggestedBroker broker = responseSuggestedBrokers.get(position);
        Log.i("SuggestedBroker", "onBindViewHolder: "+position);
        if (broker.getBuying_company_name() != null) {
            holder.txt_buying_company_name.setText(broker.getBuying_company_name());
        }

        holder.txt_company_name.setText("("+broker.getCompany_name()+")");

       // holder.txt_contact_number.setText(broker.getCompany_phone_number());

        holder.btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(broker.getCompany_phone_number()!=null) {
                    new ActionLogApi((Activity) context, ActionLogApi.RELATION_TYPE_ENQUIRY, ActionLogApi.ACTION_TYPE_CALL, broker.getCompany());
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + broker.getCompany_phone_number()));
                    context.startActivity(intent);
                }
            }
        });

        holder.chat_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(broker.getCompany_chat_user()!=null) {
                    new ActionLogApi((Activity) context, ActionLogApi.RELATION_TYPE_ENQUIRY, ActionLogApi.ACTION_TYPE_CHAT, broker.getCompany());
                    Intent intent = new Intent(context, ConversationActivity.class);
                    intent.putExtra(ConversationUIService.USER_ID, broker.getCompany_chat_user());
                    intent.putExtra(ConversationUIService.DISPLAY_NAME, broker.getCompany_name()); //put it for displaying the title.
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return responseSuggestedBrokers.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_buying_company_name)
        TextView txt_buying_company_name;

        @BindView(R.id.txt_company_name)
        TextView txt_company_name;

        @BindView(R.id.txt_contact_number)
        TextView txt_contact_number;

        @BindView(R.id.chat_user)
        AppCompatButton chat_user;

        @BindView(R.id.btn_call)
        AppCompatButton btn_call;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
