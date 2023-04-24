package com.wishbook.catalog.commonadapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.commonmodels.responses.Response_meeting;

import java.util.ArrayList;
import java.util.StringTokenizer;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BuyerMeetingAdapter extends RecyclerView.Adapter<BuyerMeetingAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<Response_meeting> response_meetings;

    public BuyerMeetingAdapter(Context context, ArrayList<Response_meeting> response_meetings) {
        this.context = context;
        this.response_meetings = response_meetings;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyer_meeting_item, parent, false);
        return new BuyerMeetingAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Response_meeting response_meeting = response_meetings.get(position);
        holder.txt_meeting_number.setText(String.valueOf(response_meetings.size() - (position)));
        if (response_meeting.getDuration() != null) {
            holder.txt_duration.setText(getformatedduration(response_meeting.getDuration()));
        }

        if (response_meeting.getNote() != null && !response_meeting.getNote().isEmpty()) {
            holder.linear_note.setVisibility(View.VISIBLE);
            holder.txt_note.setText(response_meeting.getNote());
        } else {
            holder.linear_note.setVisibility(View.GONE);
        }
        holder.txt_date_time.setText(DateUtils.currentIST(response_meeting.getStart_datetime()));
    }

    @Override
    public int getItemCount() {
        return response_meetings.size();
    }

    private String getformatedduration(String duration) {
        try {
            StringTokenizer stringTokenizer = new StringTokenizer(duration, ".");
            if (stringTokenizer.hasMoreTokens()) {
                return stringTokenizer.nextToken();
            }
        } catch (Exception e) {

        }
        return "";
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_date_time)
        TextView txt_date_time;

        @BindView(R.id.txt_duration)
        TextView txt_duration;

        @BindView(R.id.txt_note)
        TextView txt_note;

        @BindView(R.id.linear_note)
        LinearLayout linear_note;

        @BindView(R.id.txt_meeting_number)
        TextView txt_meeting_number;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
