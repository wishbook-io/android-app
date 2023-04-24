package com.wishbook.catalog.home.orders.details;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wishbook.catalog.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Vigneshkarnika on 10/05/16.
 */
public class Fragment_Pay extends DialogFragment {


    private EditText input_buyername;
    private EditText input_phone;
    private TextView btn_discard;
    private TextView btn_add;
    private Listener listener;
    private EditText input_date;
    private EditText input_amount;
    private EditText input_paymentDet;
    private String datePicked = "0000-00-00";
    private LinearLayout input_date_cont;
    private TextView txt_neft_wishbook_detail;

    private LinearLayout linear_neft_details;
    private EditText editNeft, editBankName, editChequeNumber;
    private LinearLayout linear_bank_details, linear_other_details;
    private String type;
    private TextInputLayout input_payment_details, txt_input_neft, txt_input_bankName, txt_input_checque_no;
    private TextView txt_copy, txt_share;
    private TextView txt_cheque_note, txt_other_note;

    LinearLayout linear_neft_option;


    public interface Listener {
        void onDismiss(String date, String details, String amount);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog.setContentView(R.layout.dialog_pay);
        input_date = (EditText) dialog.findViewById(R.id.input_date);
        //input_date.setEnabled(false);
        input_date_cont = (LinearLayout) dialog.findViewById(R.id.input_date_cont);
        input_paymentDet = (EditText) dialog.findViewById(R.id.input_paymentDet);
        input_paymentDet.addTextChangedListener(new MyTextWatcher(input_paymentDet));
        input_amount = (EditText) dialog.findViewById(R.id.input_amount);
        txt_neft_wishbook_detail = dialog.findViewById(R.id.txt_neft_wishbook_detail);
        linear_neft_option = dialog.findViewById(R.id.linear_neft_option);
        txt_share = dialog.findViewById(R.id.txt_share);
        txt_copy = dialog.findViewById(R.id.txt_copy);

        txt_cheque_note = dialog.findViewById(R.id.txt_cheque_note);
        txt_other_note = dialog.findViewById(R.id.txt_other_note);

        linear_neft_details = (LinearLayout) dialog.findViewById(R.id.linear_neft_details);
        editNeft = (EditText) dialog.findViewById(R.id.edit_neft);
        editNeft.addTextChangedListener(new MyTextWatcher(editNeft));

        linear_bank_details = (LinearLayout) dialog.findViewById(R.id.linear_bank_details);
        linear_other_details = (LinearLayout) dialog.findViewById(R.id.linear_other_details);
        editBankName = (EditText) dialog.findViewById(R.id.edit_bank_name);
        editBankName.addTextChangedListener(new MyTextWatcher(editBankName));
        editChequeNumber = (EditText) dialog.findViewById(R.id.edit_cheque_number);
        editChequeNumber.addTextChangedListener(new MyTextWatcher(editChequeNumber));

        input_payment_details = (TextInputLayout) dialog.findViewById(R.id.input_payment_details);
        txt_input_neft = (TextInputLayout) dialog.findViewById(R.id.txt_input_neft);
        txt_input_bankName = (TextInputLayout) dialog.findViewById(R.id.txt_input_bankName);
        txt_input_checque_no = (TextInputLayout) dialog.findViewById(R.id.txt_input_checque_no);
        /*input_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getChildFragmentManager(), "datePicker");
            }
        });*/
        if (getArguments() != null) {
            String amount = getArguments().getString("amount");
            if (amount != null) {
                input_amount.setText(amount);
            }

            type = getArguments().getString("type");
            if (type != null && type.equalsIgnoreCase("NEFT")) {
                linear_other_details.setVisibility(View.GONE);
                linear_bank_details.setVisibility(View.GONE);
                linear_neft_details.setVisibility(View.VISIBLE);
                txt_neft_wishbook_detail.setVisibility(View.VISIBLE);
                linear_neft_option.setVisibility(View.VISIBLE);
            } else if (type != null && type.equalsIgnoreCase("CHEQUE")) {
                linear_other_details.setVisibility(View.GONE);
                linear_bank_details.setVisibility(View.VISIBLE);
                linear_neft_details.setVisibility(View.GONE);
                txt_cheque_note.setVisibility(View.VISIBLE);
                txt_neft_wishbook_detail.setVisibility(View.VISIBLE);
                linear_neft_option.setVisibility(View.VISIBLE);
            } else {
                linear_other_details.setVisibility(View.VISIBLE);
                linear_bank_details.setVisibility(View.GONE);
                linear_neft_details.setVisibility(View.GONE);

                txt_other_note.setVisibility(View.VISIBLE);
            }
        }
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

        if (input_date.getText().toString().equals("")) {
            updateLabel(myCalendar);
        }

        input_date.setFocusable(false);
        input_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DialogFragment newFragment = new DatePickerFragment();
                //newFragment.show(getChildFragmentManager(), "datePicker");
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), R.style.LightDialogTheme, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.show();
            }
        });
        btn_discard = (TextView) dialog.findViewById(R.id.btn_discard);
        btn_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btn_add = (TextView) dialog.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String paymentDetail = null;
                if (type != null && type.equalsIgnoreCase("NEFT")) {
                    if(valideEmpty(editNeft)){
                        return;
                    }
                    paymentDetail = "Transaction ID=" + editNeft.getText().toString().trim();
                } else if (type != null && type.equalsIgnoreCase("CHEQUE")) {
                    if(valideEmpty(editBankName) || valideEmpty(editChequeNumber)){
                        return;
                    }
                    paymentDetail = "Bank Name=" + editBankName.getText().toString().trim() + "\nCheque No=" + editChequeNumber.getText().toString();
                } else {
                    if(valideEmpty(input_paymentDet)){
                        return;
                    }
                    paymentDetail = input_paymentDet.getText().toString();
                }
                if (!input_date.getText().toString().equals("") && paymentDetail != null && !input_amount.getText().toString().equals("")) {

                    if (listener != null) {
                        listener.onDismiss(input_date.getText().toString(), paymentDetail, input_amount.getText().toString());
                        dismiss();
                    } else {
                        dismiss();
                    }


                } else {
                    Toast.makeText(getActivity(), "Fill All the Details", Toast.LENGTH_LONG).show();
                }

            }
        });
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        initListener();
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
            DatePickerDialog dp = new DatePickerDialog(getActivity(), R.style.LightDialogTheme, this, year, month, day);
            dp.getDatePicker().setMaxDate(System.currentTimeMillis());
            return dp;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            String years = String.valueOf(year);
            String months = String.valueOf(month + 1);
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
        String format = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        input_date.setText(sdf.format(myCalendar.getTime()));
    }

    public void initListener() {
        txt_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(getResources().getString(R.string.payment_neft_details));
                    Toast.makeText(getActivity(),"copied to clipboard",Toast.LENGTH_SHORT).show();
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Wishbook Payment Detail", getResources().getString(R.string.payment_neft_details));
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getActivity(),"copied to clipboard",Toast.LENGTH_SHORT).show();
                }

            }
        });

        txt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareWishBookBankDetail();
            }
        });

    }

    public void shareWishBookBankDetail() {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Wishbook Payment Detail");
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.payment_neft_details));
            intent.setType("text/plain");
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }

    }

    private class MyTextWatcher implements TextWatcher {
        EditText type;

        public MyTextWatcher(EditText type) {
            this.type = type;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            valideEmpty(type);
        }
    }

    private boolean valideEmpty(EditText type) {
        if (type == editBankName) {
            if (TextUtils.isEmpty(editBankName.getText().toString())) {
                txt_input_bankName.setError(getString(R.string.empty_field));
                editBankName.requestFocus();
                return true;
            } else {
                txt_input_bankName.setError(null);
            }
        } else if (type == editChequeNumber) {
            if (TextUtils.isEmpty(editChequeNumber.getText().toString())) {
                txt_input_checque_no.setError(getString(R.string.empty_field));
                editChequeNumber.requestFocus();
                return true;
            } else {
                txt_input_checque_no.setError(null);
            }
        } else if (type == editNeft) {
            if (TextUtils.isEmpty(editNeft.getText().toString())) {
                txt_input_neft.setError(getString(R.string.empty_field));
                editNeft.requestFocus();
                return true;
            } else {
                txt_input_neft.setError(null);
            }
        } else if (type == input_paymentDet) {
            if (TextUtils.isEmpty(input_paymentDet.getText().toString())) {
                input_payment_details.setError(getString(R.string.empty_field));
                input_payment_details.requestFocus();
                return true;
            } else {
                input_payment_details.setError(null);
            }
        }
        return false;

    }

}
