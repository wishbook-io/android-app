package com.wishbook.catalog.commonadapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.commonmodels.responses.Response_Attendance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by root on 5/12/16.
 */
public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.MyViewHolder> {

    private AppCompatActivity mActivity;
    private Response_Attendance[] mlist;



    public AttendanceAdapter(AppCompatActivity mActivity,Response_Attendance[] mBuyerGroups) {
        this.mActivity = mActivity;
        this.mlist = mBuyerGroups;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView datetime,action;

        public MyViewHolder(View view) {
            super(view);
            datetime = (TextView) view.findViewById(R.id.datetime);
            action = (TextView) view.findViewById(R.id.action);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.datetime.setText(DateUtils.currentIST(mlist[position].getDate_time()) );
        holder.action.setText(mlist[position].getAction());
    }

    private String getformatedTime(String dat) {

        String format = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String toformat = "dd-MM-yyyy hh:mm aa";
        SimpleDateFormat tosdf = new SimpleDateFormat(toformat, Locale.US);


        try {
            Date date = sdf.parse(dat);
            return tosdf.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

    }

    @Override
    public int getItemCount() {
        return mlist.length;
    }



}
