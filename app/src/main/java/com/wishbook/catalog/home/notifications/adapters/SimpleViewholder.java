package com.wishbook.catalog.home.notifications.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wishbook.catalog.R;

/**
 * Created by root on 21/10/16.
 */
public class SimpleViewholder extends RecyclerView.ViewHolder {
   private TextView title,message;
    private LinearLayout container;
    ImageView badge_item;
    private LinearLayout linear_action_button;
    private LinearLayout linear_root;
    TextView txt_call_wb_support, txt_chat_wb_support;

    public SimpleViewholder(View itemView) {
        super(itemView);
        linear_root = itemView.findViewById(R.id.linear_root);
        title = (TextView) itemView.findViewById(R.id.title);
        message = (TextView) itemView.findViewById(R.id.message);
        container = (LinearLayout) itemView.findViewById(R.id.container);
        badge_item = (ImageView) itemView.findViewById(R.id.unread_view);
        linear_action_button = itemView.findViewById(R.id.linear_action_button);
        txt_call_wb_support = itemView.findViewById(R.id.txt_call_wb_support);
        txt_chat_wb_support = itemView.findViewById(R.id.txt_chat_wb_support);

    }

    public ImageView getBadge_item() {
        return badge_item;
    }

    public void setBadge_item(ImageView badge_item) {
        this.badge_item = badge_item;
    }

    public LinearLayout getContainer() {
        return container;
    }

    public void setContainer(LinearLayout container) {
        this.container = container;
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public TextView getMessage() {
        return message;
    }

    public void setMessage(TextView message) {
        this.message = message;
    }

    public LinearLayout getLinear_action_button() {
        return linear_action_button;
    }

    public void setLinear_action_button(LinearLayout linear_action_button) {
        this.linear_action_button = linear_action_button;
    }

    public TextView getTxt_call_wb_support() {
        return txt_call_wb_support;
    }

    public void setTxt_call_wb_support(TextView txt_call_wb_support) {
        this.txt_call_wb_support = txt_call_wb_support;
    }

    public TextView getTxt_chat_wb_support() {
        return txt_chat_wb_support;
    }

    public void setTxt_chat_wb_support(TextView txt_chat_wb_support) {
        this.txt_chat_wb_support = txt_chat_wb_support;
    }

    public LinearLayout getLinear_root() {
        return linear_root;
    }

    public void setLinear_root(LinearLayout linear_root) {
        this.linear_root = linear_root;
    }
}
