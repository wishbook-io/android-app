package com.wishbook.catalog.home.orders;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;
import com.wishbook.catalog.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BottomOrderDateRange extends BottomSheetDialogFragment {


    @BindView(R.id.txt_all_order)
    TextView txt_all_order;

    @BindView(R.id.txt_today)
    TextView txt_today;

    @BindView(R.id.txt_yesterday)
    TextView txt_yesterday;

    @BindView(R.id.txt_this_week)
    TextView txt_this_week;

    @BindView(R.id.txt_this_month)
    TextView txt_this_month;

    @BindView(R.id.txt_custom)
    TextView txt_custom;

    @BindView(R.id.img_close)
    ImageView img_close;

    String default_from_date, default_to_date;

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    BottomOrderDateRange.DismissListener dismissListener;


    public static BottomOrderDateRange newInstance(Bundle bundle) {
        BottomOrderDateRange bottomOrderDateRange = new BottomOrderDateRange();
        if (bundle != null) {
            bottomOrderDateRange.setArguments(bundle);
        }
        return bottomOrderDateRange;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.order_select_date_range_botomsheet, container, false);
        ButterKnife.bind(this, view);
        initListener();
        return view;
    }

    public void initListener() {
        if(getArguments()!=null && getArguments().getString("all_label_text")!=null) {
            txt_all_order.setText(getArguments().getString("all_label_text"));
        }
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        txt_all_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passData("all");
            }
        });

        txt_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passData("today");
            }
        });

        txt_yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passData("yesterday");
            }
        });

        txt_this_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passData("week");
            }
        });

        txt_this_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passData("month");
            }
        });

        txt_custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmoothDateRangePickerFragment smoothDateRangePickerFragment = SmoothDateRangePickerFragment.newInstance(
                        new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                            @Override
                            public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                                       int yearStart, int monthStart,
                                                       int dayStart, int yearEnd,
                                                       int monthEnd, int dayEnd) {
                                Log.e("TAG", "onDateRangeSet: ====>");
                                default_to_date = dayEnd + "/" + (monthEnd + 1) + "/" + yearEnd;
                                default_from_date = dayStart + "/" + (monthStart + 1) + "/" + yearStart;
                                passData("custom");
                            }
                        });

                smoothDateRangePickerFragment.setMaxDate(Calendar.getInstance());
                smoothDateRangePickerFragment.show(getActivity().getFragmentManager(), "smoothDateRangePicker");
            }
        });
    }

    public void passData(String type) {
        Date today = new Date();
        switch (type) {
            case "all":
                if (dismissListener != null) {
                    dismissListener.onDismiss(null,null, "All Order");
                    dismiss();
                }
                break;
            case "today":
                if (dismissListener != null) {
                    default_from_date = formatter.format(today);
                    default_to_date = formatter.format(today);
                    String query_para = "from_date=" + default_from_date + "&to_date=" + default_to_date;
                    dismissListener.onDismiss(default_from_date,default_to_date, "Today");
                    dismiss();
                }
                break;
            case "yesterday":
                if (dismissListener != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(today);
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    Date from_date = cal.getTime();
                    default_from_date = formatter.format(from_date);
                    default_to_date = formatter.format(from_date);
                    String query_para = "from_date=" + default_from_date + "&to_date=" + default_to_date;
                    dismissListener.onDismiss(default_from_date,default_to_date, "Yesterday");
                    dismiss();
                }
                break;
            case "week":
                if (dismissListener != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(today);
                    cal.add(Calendar.DAY_OF_MONTH, -7);
                    Date from_date = cal.getTime();
                    default_from_date = formatter.format(from_date);
                    default_to_date = formatter.format(today);
                    String query_para = "from_date=" + default_from_date + "&to_date=" + default_to_date;
                    dismissListener.onDismiss(default_from_date,default_to_date, "Last 7 Days");
                    dismiss();
                }
                break;
            case "month":
                if (dismissListener != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    Date from_date = cal.getTime();
                    default_from_date = formatter.format(from_date);
                    default_to_date = formatter.format(today);
                    String query_para = "from_date=" + default_from_date + "&to_date=" + default_to_date;
                    Log.e("TAG", "passData: ==>"+default_from_date );
                    dismissListener.onDismiss(default_from_date,default_to_date, "This Month");
                    dismiss();
                }
                break;
            case "custom":
                if (dismissListener != null) {
                    String query_para = default_from_date + ";" + default_to_date;
                    dismissListener.onDismiss(default_from_date,default_to_date, query_para);
                    dismiss();
                }
                break;
        }
    }

    public interface DismissListener {
        void onDismiss(String from_date,String to_date, String displayText);
    }


    public void setDismissListener(BottomOrderDateRange.DismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }


}