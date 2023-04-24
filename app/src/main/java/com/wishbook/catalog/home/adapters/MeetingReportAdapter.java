package com.wishbook.catalog.home.adapters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.home.models.Response_meeting_report;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Vignesh on 21-03-2016.
 */
public class MeetingReportAdapter extends RecyclerView.Adapter<MeetingReportAdapter.MyViewHolder> {

    private AppCompatActivity mActivity;
    private ArrayList<Response_meeting_report> response_meeting_reports;

    public MeetingReportAdapter(AppCompatActivity mActivity, ArrayList<Response_meeting_report>  response_meeting_reports) {
        this.mActivity = mActivity;
        this.response_meeting_reports = response_meeting_reports;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView total_duration_tex;
        private final TextView total_meeting_tex;
        private final TextView total_items_tex;
        private final TextView day_tex;

        public MyViewHolder(View view) {
            super(view);
            day_tex = (TextView) view.findViewById(R.id.day_tex);
            total_items_tex = (TextView) view.findViewById(R.id.total_items_tex);
            total_meeting_tex = (TextView) view.findViewById(R.id.total_meeting_tex);
            total_duration_tex = (TextView) view.findViewById(R.id.total_duration_tex);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meeting_rep_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Response_meeting_report response_meeting_report = response_meeting_reports.get(position);
       holder.day_tex.setText(response_meeting_report.getDay());
       holder.total_duration_tex.setText("Meeting Duration : "+ getformatedduration(response_meeting_report.getTotal_duration()));
       holder.total_items_tex.setText("No.of pcs sold : "+response_meeting_report.getTotal_items());
       holder.total_meeting_tex.setText("No.of Meetings : "+response_meeting_report.getTotal_meeting());
    }

    @Override
    public int getItemCount() {
        return response_meeting_reports.size();
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


}
