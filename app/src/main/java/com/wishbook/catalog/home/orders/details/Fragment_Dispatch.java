package com.wishbook.catalog.home.orders.details;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatButton;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wishbook.catalog.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Vigneshkarnika on 10/05/16.
 */
public class Fragment_Dispatch extends DialogFragment {


    private EditText input_buyername;
    private EditText input_phone;
    private AppCompatButton btn_discard;
    private AppCompatButton btn_add;
    private Listener listener;
    private EditText input_date,input_mode,input_transporter;
    private EditText input_paymentDet;
    private String datePicked = "0000-00-00";
    private LinearLayout input_date_cont;

    public interface Listener {
        void onDismiss(String date, String mode, String transporter , String details);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog.setContentView(R.layout.dialog_dispatch);
        input_date = (EditText) dialog.findViewById(R.id.input_date);
        input_mode = (EditText) dialog.findViewById(R.id.input_mode);
        input_transporter = (EditText) dialog.findViewById(R.id.input_transporter);
        //input_date.setEnabled(false);
        input_date_cont = (LinearLayout) dialog.findViewById(R.id.input_date_cont);
        input_paymentDet = (EditText) dialog.findViewById(R.id.input_paymentDet);
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(myCalendar);
            }

        };

        if(input_date.getText().toString().equals("")){
            updateLabel(myCalendar);
        }

        input_date.setFocusable(false);
        input_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DialogFragment newFragment = new DatePickerFragment();
                //newFragment.show(getChildFragmentManager(), "datePicker");
                DatePickerDialog dialog= new DatePickerDialog(getActivity(),R.style.LightDialogTheme,date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.show();
            }
        });
        btn_discard = (AppCompatButton) dialog.findViewById(R.id.btn_discard);
        btn_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btn_add = (AppCompatButton) dialog.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!input_date.getText().toString().equals("")) {
                    listener.onDismiss(input_date.getText().toString(), input_mode.getText().toString(),input_transporter.getText().toString(),input_paymentDet.getText().toString());
                }
                else
                {
                    Toast.makeText(getActivity(),"Fill All the Details",Toast.LENGTH_LONG).show();
                }

                dismiss();
            }
        });
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }


    public void setListener(Listener listener) {
        this.listener = listener;
    }


    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dp = new DatePickerDialog(getActivity(),R.style.LightDialogTheme, this, year, month, day);
            dp.getDatePicker().setMaxDate(System.currentTimeMillis());
            return dp;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            String years = String.valueOf(year);
            String months = String.valueOf(month+1);
            String days = String.valueOf(day);
            if (months.length() == 1) {
                months = "0" + months;
            }
            if (days.length() == 1) {
                days = "0" + days;
            }
            datePicked = years + "-" + months + "-" + days;
            input_date.setText(days + "-" + months + "-" + years);


            // Do something with the date chosen by the user
        }
    }

    private void updateLabel(Calendar myCalendar) {
        Date today=null;
        Date setDate=null;
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
        String formattedDate = df.format(c.getTime());
        try {
            today = df.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        String format = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String form = sdf.format(myCalendar.getTime());

        try {
            setDate = sdf.parse(form);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(!setDate.after(today)) {
            input_date.setText(form);
        }
    }

}
