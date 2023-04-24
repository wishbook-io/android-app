package com.wishbook.catalog.home.more.creditRating;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.widget.NestedScrollView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.BuildConfig;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.ValidationUtils;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.widget.AadhaarXMLParser;
import com.wishbook.catalog.Utils.widget.SimpleTextWatcher;
import com.wishbook.catalog.commonmodels.RequestBureau;
import com.wishbook.catalog.commonmodels.RequestCreditRating;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.ResponseUserCreditSubmission;
import com.wishbook.catalog.home.Fragment_Summary;
import com.wishbook.catalog.home.more.creditRating.crief.CRIFApi;
import com.wishbook.catalog.home.more.creditRating.crief.CRIFCreds;
import com.wishbook.catalog.home.myBusiness.Fragment_MyBusiness;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentCreditRating extends GATrackedFragment implements OpenContainer.OnBackPressedListener {

    final Calendar myCalendar = Calendar.getInstance();
    @BindView(R.id.linear_first_step)
    LinearLayout linear_first_step;
    @BindView(R.id.input_aadhar_number)
    TextInputLayout input_aadhar_number;
    @BindView(R.id.edit_aadhar_number)
    EditText edit_aadhar_number;
    @BindView(R.id.input_address)
    TextInputLayout input_address;
    @BindView(R.id.edit_address)
    EditText edit_address;
    @BindView(R.id.input_pincode)
    TextInputLayout input_pincode;
    @BindView(R.id.edit_pincode)
    EditText edit_pincode;
    @BindView(R.id.input_city)
    TextInputLayout input_city;
    @BindView(R.id.edit_city)
    EditText edit_city;
    @BindView(R.id.input_state)
    TextInputLayout input_state;
    @BindView(R.id.edit_state)
    EditText edit_state;
    @BindView(R.id.edit_dob)
    EditText edit_dob;
    @BindView(R.id.input_full_name)
    TextInputLayout input_full_name;
    @BindView(R.id.edit_full_name)
    EditText edit_full_name;

    @BindView(R.id.input_last_name)
    TextInputLayout input_last_name;
    @BindView(R.id.edit_last_name)
    EditText edit_last_name;

    @BindView(R.id.linear_second_step)
    LinearLayout linear_second_step;
    @BindView(R.id.input_pan_card_number)
    TextInputLayout input_pan_card_number;
    @BindView(R.id.edit_pan_card_number)
    EditText edit_pan_card_number;
    @BindView(R.id.input_father_name)
    TextInputLayout input_father_name;
    @BindView(R.id.edit_father_name)
    EditText edit_father_name;
    @BindView(R.id.input_spouce_name)
    TextInputLayout input_spouce_name;
    @BindView(R.id.edit_spouce_name)
    EditText edit_spouce_name;
    @BindView(R.id.input_mobile_number)
    TextInputLayout input_mobile_number;
    @BindView(R.id.edit_mobile_number)
    EditText edit_mobile_number;
    @BindView(R.id.input_email)
    TextInputLayout input_email;
    @BindView(R.id.input_dob)
    TextInputLayout input_dob;

    @BindView(R.id.edit_email)
    EditText edit_email;
    @BindView(R.id.radio_group)
    RadioGroup radio_group;
    @BindView(R.id.radio_male)
    RadioButton radio_male;
    @BindView(R.id.radio_female)
    RadioButton radio_female;
    @BindView(R.id.radio_other)
    RadioButton radio_other;
    @BindView(R.id.checkbox_terms)
    CheckBox checkbox_terms;
    @BindView(R.id.btn_submit)
    AppCompatButton btn_submit;
    RequestCreditRating scanAadharData;
    String gender;
    private View v;
    private Context mContext;
    private int add_step;

    private static String TAG = FragmentCreditRating.class.getSimpleName();

    private String bureau_order_id;
    private String bureau_report_id;
    private String bureau_status;
    private String bureau_score;

    private boolean isEditMode;
    RequestCreditRating oldKycData;
    ResponseUserCreditSubmission oldUserCreditSubmission;
    private String crif_api_error;

    @BindView(R.id.scroll_view)
    NestedScrollView scroll_view;

    private boolean isChangeKYC = false;

    private String from;



    public FragmentCreditRating() {

    }

    public final static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_credit_rating, container, false);
        ButterKnife.bind(this, v);
        mContext = getActivity();
        add_step = 1;
        initView();
        return v;
    }

    public void initView() {
        ((OpenContainer) getActivity()).setOnBackPressedListener(FragmentCreditRating.this);
        ((OpenContainer) getActivity()).toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doBack();
            }
        });


        if (getArguments() != null && getArguments().getString("content") != null) {
            try {
                AadhaarXMLParser aadhaarXMLParser = new AadhaarXMLParser();
                scanAadharData = aadhaarXMLParser.parse(getArguments().getString("content"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        updateUI();
        fillUpAadharData();

        if (getArguments() != null && getArguments().getBoolean("isEdit")) {
            isEditMode = getArguments().getBoolean("isEdit");
        }
        if (isEditMode) {
            getKYCData();
            getUserCreditSubmission1();
           // getUserCreditSubmission();
        }

        if(getArguments()!=null && getArguments().getString("from")!=null){
            from = getArguments().getString("from");
        }

        sendAnalytics();
    }

    public void fillUpAadharData() {
        if (UserInfo.getInstance(getActivity()).getCompanyCityName() != null) {
            edit_city.setText(UserInfo.getInstance(getActivity()).getCompanyCityName());
        }

        if (UserInfo.getInstance(getActivity()).getCompanyStateName() != null) {
            edit_state.setText(UserInfo.getInstance(getActivity()).getCompanyStateName());
        }


        if (scanAadharData != null) {
            edit_aadhar_number.setText(scanAadharData.getAadhar_card().toString());
            edit_address.setText(scanAadharData.getAddress());
            edit_pincode.setText(scanAadharData.getPincode());
            edit_city.setText(scanAadharData.getCity());
            edit_state.setText(scanAadharData.getState());


            if(scanAadharData.getFull_name()!=null){
                String[] splited = scanAadharData.getFull_name().split(" ");
                if(splited.length == 1){
                    edit_full_name.setText(splited[0]);
                } else if(splited.length == 2){
                    edit_full_name.setText(splited[0]);
                    edit_last_name.setText(splited[1]);
                } else if(splited.length > 2){
                    edit_full_name.setText(splited[0]);


                    ArrayList<String> temp = new ArrayList<>() ;
                    for (int i=1;i<splited.length;i++){
                        temp.add(splited[i]);
                    }
                   String lastName = StaticFunctions.ArrayListToString(temp," ");
                    edit_last_name.setText(lastName);
                }
            }
            edit_pan_card_number.setText(scanAadharData.getPan_card());
            edit_father_name.setText(scanAadharData.getFather_name());
           // edit_spouce_name.setText(scanAadharData.getSpouse_name());
            edit_mobile_number.setText(scanAadharData.getMobile_no());
            edit_email.setText(scanAadharData.getEmail());
            if(scanAadharData.gender!=null){
                if (scanAadharData.gender.equalsIgnoreCase("Male")) {
                    radio_male.setChecked(true);
                } else if (scanAadharData.gender.equalsIgnoreCase("Female")) {
                    radio_female.setChecked(true);
                } else {
                    radio_other.setChecked(true);
                }
            }


            if (scanAadharData.birth_date != null) {
                try {
                    String myFormat = "dd-MM-yyyy"; //In which you need put here
                    String aadharFormat = "dd/MM/yyyy";
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                    SimpleDateFormat serverFormatter = new SimpleDateFormat(aadharFormat,Locale.US);
                    edit_dob.setText(sdf.format(serverFormatter.parse(scanAadharData.birth_date)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (UserInfo.getInstance(getActivity()).getMobile() != null) {
            edit_mobile_number.setText(UserInfo.getInstance(getActivity()).getMobile());
        }

        if (!UserInfo.getInstance(getActivity()).getEmail().contains("@wishbook") || !UserInfo.getInstance(getActivity()).getEmail().contains("@wishbooks.io")) {
            edit_email.setText(UserInfo.getInstance(getActivity()).getEmail());
        }

    }

    public void stepOne() {
        scroll_view.scrollTo(0,0);
        btn_submit.setText("NEXT");
        linear_first_step.setVisibility(View.VISIBLE);
        linear_second_step.setVisibility(View.GONE);
        ((OpenContainer) getActivity()).toolbar.setTitle("Apply for credit");
        ((OpenContainer) getActivity()).toolbar.setBackgroundColor(getResources().getColor(R.color.color_primary));
        ((OpenContainer) getActivity()).toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        Drawable icon = getResources().getDrawable(R.drawable.ic_arrow_back_24dp);
        icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        ((OpenContainer) getActivity()).toolbar.setNavigationIcon(icon);

        edit_aadhar_number.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_aadhar_number.isErrorEnabled()) {
                    input_aadhar_number.setError(null);
                    input_aadhar_number.setErrorEnabled(false);
                    if(isEditMode){
                        if(oldKycData!=null && oldKycData.getAadhar_card()!=null && !oldKycData.getAadhar_card().equals(edit_aadhar_number.getText().toString())){
                            isChangeKYC = true;
                        }
                    }
                }
            }
        });

        edit_full_name.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_full_name.isErrorEnabled()) {
                    input_full_name.setError(null);
                    input_full_name.setErrorEnabled(false);
                }
            }
        });

        edit_last_name.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_last_name.isErrorEnabled()) {
                    input_last_name.setError(null);
                    input_last_name.setErrorEnabled(false);
                }
            }
        });


        edit_address.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_address.isErrorEnabled()) {
                    input_address.setError(null);
                    input_address.setErrorEnabled(false);
                }
            }
        });

        edit_pincode.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_pincode.isErrorEnabled()) {
                    input_pincode.setError(null);
                    input_pincode.setErrorEnabled(false);
                }
            }
        });


        edit_city.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_city.isErrorEnabled()) {
                    input_city.setError(null);
                    input_city.setErrorEnabled(false);
                }
            }
        });

        edit_state.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_state.isErrorEnabled()) {
                    input_state.setError(null);
                    input_state.setErrorEnabled(false);
                }
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        edit_dob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.color_primary));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.color_primary));

            }
        });

        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(i);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked) {

                    if (checkedRadioButton.getText().toString().equalsIgnoreCase("Other")) {
                        gender = "Transgender";
                    } else {
                        gender = checkedRadioButton.getText().toString();
                    }
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!edit_aadhar_number.getText().toString().isEmpty() && ValidationUtils.isValidAadaharNumber(edit_aadhar_number.getText().toString()) &&
                        !edit_full_name.getText().toString().isEmpty() &&
                        !edit_last_name.getText().toString().isEmpty() &&
                        !edit_address.getText().toString().isEmpty() &&
                        !edit_pincode.getText().toString().isEmpty() &&
                        !edit_city.getText().toString().isEmpty() &&
                        !edit_state.getText().toString().isEmpty() && gender != null) {
                    add_step = add_step + 1;
                    stepTwo();
                } else {
                    if (edit_aadhar_number.getText().toString().isEmpty()) {
                        edit_aadhar_number.requestFocus();
                        input_aadhar_number.setError("Aadhar number is required ");
                        return;
                    }

                    if(!ValidationUtils.isValidAadaharNumber(edit_aadhar_number.getText().toString())){
                        edit_aadhar_number.requestFocus();
                        input_aadhar_number.setError("Enter Valid Aadhar number");
                        return;
                    }

                    if (edit_full_name.getText().toString().isEmpty()) {
                        edit_full_name.requestFocus();
                        input_full_name.setError("First name is required ");
                        return;
                    }

                    if (edit_last_name.getText().toString().isEmpty()) {
                        edit_last_name.requestFocus();
                        input_last_name.setError("Last name is required ");
                        return;
                    }

                    if (edit_address.getText().toString().isEmpty()) {
                        edit_address.requestFocus();
                        input_address.setError("Address is required ");
                        return;
                    }

                    if (edit_pincode.getText().toString().isEmpty()) {
                        edit_pincode.requestFocus();
                        input_pincode.setError("Pincode is required ");
                        return;
                    }

                    if (edit_city.getText().toString().isEmpty()) {
                        edit_city.requestFocus();
                        input_city.setError("City is required ");
                        return;
                    }

                    if (edit_state.getText().toString().isEmpty()) {
                        edit_state.requestFocus();
                        input_state.setError("State is required ");
                        return;
                    }

                    if (gender == null) {
                        Toast.makeText(getActivity(), "Please Select Gender", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        edit_dob.setText(sdf.format(myCalendar.getTime()));
    }

    public void stepTwo() {
        scroll_view.scrollTo(0,0);
        btn_submit.setText("DONE");
        linear_first_step.setVisibility(View.GONE);
        linear_second_step.setVisibility(View.VISIBLE);
        ((OpenContainer) getActivity()).toolbar.setTitle("Apply for credit rating");
        ((OpenContainer) getActivity()).toolbar.setBackgroundColor(getResources().getColor(R.color.color_primary));
        ((OpenContainer) getActivity()).toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        Drawable icon = getResources().getDrawable(R.drawable.ic_arrow_back_24dp);
        icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        ((OpenContainer) getActivity()).toolbar.setNavigationIcon(icon);

        edit_pan_card_number.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_pan_card_number.isErrorEnabled()) {
                    input_pan_card_number.setError(null);
                    input_pan_card_number.setErrorEnabled(false);
                    if(isEditMode){
                        if(oldKycData!=null && oldKycData.getPan_card()!=null && !oldKycData.getPan_card().equals(edit_pan_card_number.getText().toString())){
                            Log.e(TAG, "onTextChanged: Pan Card Change");
                            isChangeKYC = true;
                        }
                    }
                }
            }
        });

        edit_dob.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_dob.isErrorEnabled()) {
                    input_dob.setError(null);
                    input_dob.setErrorEnabled(false);
                }
            }
        });

        edit_father_name.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_father_name.isErrorEnabled()) {
                    input_father_name.setError(null);
                    input_father_name.setErrorEnabled(false);
                }
            }
        });

        edit_spouce_name.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_spouce_name.isErrorEnabled()) {
                    input_spouce_name.setError(null);
                    input_spouce_name.setErrorEnabled(false);
                }
            }
        });

        edit_mobile_number.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (input_mobile_number.isErrorEnabled()) {
                    input_mobile_number.setError(null);
                    input_mobile_number.setErrorEnabled(false);
                    if(isEditMode){
                        if(oldKycData!=null && oldKycData.getMobile_no()!=null && !oldKycData.getMobile_no().equals(edit_mobile_number.getText().toString())){
                            isChangeKYC = true;
                        }
                    }
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFatherNameEmpty = false;
                boolean isSpouchnameEmpty = false;
                if (edit_father_name.getText().toString().isEmpty()) {
                    isFatherNameEmpty = true;
                }

                if(!edit_pan_card_number.getText().toString().isEmpty()){
                    // Validate Pan Card
                    Pattern pattern = Pattern.compile("[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}");
                    Matcher matcher = pattern.matcher(edit_pan_card_number.getText().toString());
                    if (!matcher.matches()) {
                        edit_pan_card_number.requestFocus();
                        input_pan_card_number.setError("Enter Valid PAN number");
                        return;
                    }

                }
             /*   if (edit_spouce_name.getText().toString().isEmpty()) {
                    isSpouchnameEmpty = true;
                }*/
                if (!isFatherNameEmpty
                        && !edit_pan_card_number.getText().toString().isEmpty()
                        && !edit_dob.getText().toString().isEmpty()
                        && !edit_mobile_number.getText().toString().isEmpty()) {
                    btn_submit.setEnabled(false);
                    btn_submit.setVisibility(View.GONE);
                    String full_name = edit_full_name.getText().toString()+ " "+edit_last_name.getText().toString();
                    postKycData(getActivity(), edit_aadhar_number.getText().toString(), edit_address.getText().toString(),
                            edit_pincode.getText().toString(), edit_city.getText().toString(), edit_state.getText().toString(),
                            full_name, gender, edit_dob.getText().toString(), edit_pan_card_number.getText().toString(), edit_father_name.getText().toString(), edit_spouce_name.getText().toString(), edit_mobile_number.getText().toString(), edit_email.getText().toString(), true);

                } else {
                    showMissingFieldDialog(edit_dob.getText().toString().isEmpty(),
                            edit_father_name.getText().toString().isEmpty(),
                            edit_mobile_number.getText().toString().isEmpty(),edit_pan_card_number.getText().toString().isEmpty());

                    if(edit_pan_card_number.getText().toString().isEmpty()){
                        edit_pan_card_number.requestFocus();
                        input_pan_card_number.setError("PAN number. required ");
                    }

                    if (edit_dob.getText().toString().isEmpty()) {
                        edit_dob.requestFocus();
                        input_dob.setError("Date of birth is required ");

                    }

                    if (isFatherNameEmpty) {
                        edit_father_name.requestFocus();
                        input_father_name.setError("Father's/Spouse's Name as per PAN card is required");

                       /* edit_spouce_name.requestFocus();
                        input_spouce_name.setError("Spouse's name is required");*/

                    }

                    if (edit_mobile_number.getText().toString().isEmpty()) {
                        edit_mobile_number.requestFocus();
                        input_mobile_number.setError("Mobile number is required ");

                    }


                }


            }
        });
    }

    public void updateUI() {
        if (add_step == 1) {
            stepOne();
        } else if (add_step == 2) {
            stepTwo();
        }
    }

    @Override
    public void doBack() {
        if (add_step == 1) {
            if(getActivity()!=null) {
                getActivity().finish();
            }
        } else {
            add_step = add_step - 1;
            updateUI();
        }
    }

    public void postKycData(Context context, String aadhar, String address,
                            String pincode, String city, String state, String fullname,
                            String gender, String dob, String pan, String father_name, String spoucename,
                            String mobile_number, String email_id, final boolean isProceedCrif) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        RequestCreditRating requestCreditRating = new RequestCreditRating();
        requestCreditRating.setAadhar_card(aadhar);
        requestCreditRating.setAddress(address);
        if (pincode != null && !pincode.isEmpty()) {
            requestCreditRating.setPincode(pincode);
        }

        requestCreditRating.setCity(city);
        requestCreditRating.setState(state);
        requestCreditRating.setFull_name(fullname);
        if (gender != null && !gender.isEmpty()) {
            requestCreditRating.setGender(gender);
        }

        String dob1 = DateUtils.changeDateFormat(StaticFunctions.CLIENT_DISPLAY_FORMAT, StaticFunctions.SERVER_POST_FORMAT, dob);
        Log.e("TAG", "postKycData: " + dob1);
        if (!dob1.isEmpty() && !dob.equals("")) {
            requestCreditRating.setBirth_date(dob1);
        }

        requestCreditRating.setPan_card(pan);
        requestCreditRating.setFather_name(father_name);
        requestCreditRating.setSpouse_name(spoucename);
        requestCreditRating.setMobile_no(mobile_number);
        if(email_id!=null){
            requestCreditRating.setEmail(email_id);
          /* // if(email_id.contains("@wishbook") || email_id.contains("@wishbooks.io")){
            if(email_id.contains("@wishbook")){
                requestCreditRating.setEmail("tech@wishbook.io");
            } else {
                requestCreditRating.setEmail(email_id);
            }*/
        }
        HttpManager.METHOD method = HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS;
        String url = URLConstants.companyUrl(context, "solo-propreitorship-kyc", "");
        if (isEditMode) {
            method = HttpManager.METHOD.PUTWITHPROGRESS;
            if(oldKycData!=null) {
                url = URLConstants.companyUrl(context, "solo-propreitorship-kyc", "") + oldKycData.getId() + "/";
            } else {
                Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show();
                return;
            }

        } else {
            method = HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS;
            url = URLConstants.companyUrl(context, "solo-propreitorship-kyc", "");
        }
        HttpManager.getInstance(getActivity()).requestwithObject(method, url, (Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(requestCreditRating), JsonObject.class)), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(final String response, boolean dataUpdated) {
                try{
                    oldKycData = new Gson().fromJson(response, RequestCreditRating.class);
                    UserInfo.getInstance(getActivity()).setCreditRating(true);
                    if(!isProceedCrif){
                        Toast.makeText(getActivity(), "Application has been submitted", Toast.LENGTH_SHORT).show();
                    }

                    //showStage1Error("<RESPONSE><STATUS>S05</STATUS><ORDERID>wb16057</ORDERID><ERRORS><ERROR>Either Date or Date Format is Invalid.</ERROR></ERRORS></RESPONSE>");
                    if (isProceedCrif) {
                        Log.d(TAG, "onServerResponse: isProcessCrif"+isChangeKYC);
                        if(oldUserCreditSubmission==null){
                            // Start with First Step
                            callCrifStage1();
                        }
                        else {
                            if(oldUserCreditSubmission!=null && oldUserCreditSubmission.getBureau_report_id()!=null && oldUserCreditSubmission.getBureau_order_id()!=null && !isChangeKYC){
                                // start with second step
                                callCrifStage2(oldUserCreditSubmission.getBureau_order_id()+"|"+oldUserCreditSubmission.getBureau_report_id(),"null");
                            } else {
                                callCrifStage1();
                            }
                        }
                    } else {
                        afterProceesAnyway();
                    }
                } catch (Exception e){
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);

            }
        });
    }

    private void getKYCData() {

        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "solo-propreitorship-kyc", ""), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    hideProgress();
                    if (isAdded() && !isDetached()) {
                        final RequestCreditRating[] creditRatings = new Gson().fromJson(response, RequestCreditRating[].class);
                        if (creditRatings.length > 0) {
                            UserInfo.getInstance(getActivity()).setCreditRating(true);
                            oldKycData = creditRatings[0];
                            if (oldKycData != null) {
                                edit_aadhar_number.setText(oldKycData.getAadhar_card().toString());
                                edit_address.setText(oldKycData.getAddress());
                                edit_pincode.setText(oldKycData.getPincode());
                                edit_city.setText(oldKycData.getCity());
                                edit_state.setText(oldKycData.getState());
                                edit_full_name.setText(oldKycData.getFull_name());
                                if(oldKycData.getFull_name()!=null){
                                    String[] splited = oldKycData.getFull_name().split(" ");
                                    if(splited.length == 1){
                                        edit_full_name.setText(splited[0]);
                                    } else if(splited.length == 2){
                                        edit_full_name.setText(splited[0]);
                                        edit_last_name.setText(splited[1]);
                                    } else if(splited.length>2){
                                        edit_full_name.setText(splited[0]);
                                        ArrayList<String> temp = new ArrayList<>() ;
                                        String lastName="";
                                        for (int i=1;i<splited.length;i++){
                                            temp.add(splited[i]);
                                        }
                                        lastName = StaticFunctions.ArrayListToString(temp," ");
                                        edit_last_name.setText(lastName);
                                    }
                                }
                                edit_pan_card_number.setText(oldKycData.getPan_card());
                                if(oldKycData.getFather_name()!=null && oldKycData.getSpouse_name()!=null){
                                    // Old Data Compatible(merge two field)
                                    edit_father_name.setText(oldKycData.getFather_name());
                                } else {
                                    if(oldKycData.getFather_name()!=null){
                                        edit_father_name.setText(oldKycData.getFather_name());
                                    } else if(oldKycData.getSpouse_name()!=null){
                                        edit_father_name.setText(oldKycData.getSpouse_name());
                                    }
                                }

                                edit_mobile_number.setText(oldKycData.getMobile_no());
                                edit_email.setText(oldKycData.getEmail());
                                if (oldKycData.gender != null) {
                                    if (oldKycData.gender.equalsIgnoreCase("Male")) {
                                        radio_male.setChecked(true);
                                    } else if (oldKycData.gender.equalsIgnoreCase("Female")) {
                                        radio_female.setChecked(true);
                                    } else {
                                        radio_other.setChecked(true);
                                    }
                                }
                                edit_city.setText(oldKycData.getCity());
                                edit_state.setText(oldKycData.getState());
                                edit_mobile_number.setText(oldKycData.getMobile_no());
                                edit_email.setText(oldKycData.getEmail());

                                if (oldKycData.getBirth_date() != null) {
                                    try {
                                        String myFormat = "dd-MM-yyyy"; //In which you need put here
                                        String serverFormat = "yyyy-MM-dd";
                                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                        SimpleDateFormat serverFormatter = new SimpleDateFormat(serverFormat, Locale.US);
                                        edit_dob.setText(sdf.format(serverFormatter.parse(oldKycData.getBirth_date())));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        } else {
                            UserInfo.getInstance(getActivity()).setCreditRating(false);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                // Failed get Kyc
                isEditMode = false;
                hideProgress();
                Log.v("error response", error.getErrormessage());
            }
        });
    }

    private void getUserCreditSubmission() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "user-credit-submissions", ""), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    if (isAdded() && !isDetached()) {
                        try {
                            Type type = new TypeToken<ArrayList<ResponseUserCreditSubmission>>() {
                            }.getType();
                            ArrayList<ResponseUserCreditSubmission> userCreditSubmissions = new Gson().fromJson(response, type);
                            if (userCreditSubmissions.size() > 0) {
                                oldUserCreditSubmission = userCreditSubmissions.get(0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                Log.v("error response", error.getErrormessage());
            }
        });
    }


    private void getUserCreditSubmission1() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "credit-rating", UserInfo.getInstance(getActivity()).getCompany_id()), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    try {
                        Type type = new TypeToken<ArrayList<ResponseUserCreditSubmission>>() {
                        }.getType();
                        ArrayList<ResponseUserCreditSubmission> userCreditSubmissions = new Gson().fromJson(response, type);
                        if (userCreditSubmissions.size() > 0) {
                            oldUserCreditSubmission = userCreditSubmissions.get(0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                Log.v("error response", error.getErrormessage());
            }
        });
    }

    public void callCrifStage1() {
        String progress_message="";
        if(BuildConfig.DEBUG){
            progress_message = "Call Stage 1..";
        } else {
            progress_message = "Loading..";
        }
        final MaterialDialog progressDialog = showProgress(getActivity(), progress_message, "");
        progressDialog.show();
        Log.d(TAG, "======================callCrifStage1:========================= ");
        CRIFApi crifApi = new CRIFApi();
        crifApi.init(generateCrifCredStepOne());
        Call<ResponseBody> call = crifApi.initRetrofit().postData(CRIFApi.SERVICE_URL);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        String stage1Res="";
                        try {
                            stage1Res = response.body().string();
                            Log.d(TAG, "Stage1: " + stage1Res);
                            if(stage1Res.contains("ERROR")){
                                crif_api_error = "Stage 1: Response"+stage1Res;
                                showStage1Error(stage1Res,1);
                            } else {
                                String responseStepOne = getCriefAccesscode(CRIFApi.KEY, stage1Res, "D");
                                if(responseStepOne!=null)
                                    Log.d(TAG, "Stage1 dec : " + responseStepOne);
                                bureau_status = CRIFApi.BUREAU_STATUS_STAGE1;
                                callCrifStage2(responseStepOne, "NULL");
                            }
                        } catch (Exception e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            crif_api_error = "Stage 1: Response"+stage1Res;
                            showStage1Error(stage1Res,1);
                            // calluserCreditSubmission(bureau_score, bureau_status, crif_api_error, bureau_order_id, bureau_report_id);
                        }

                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                try{
                    bureau_status = CRIFApi.BUREAU_STATUS_STAGE1;
                    progressDialog.dismiss();
                    Log.d(TAG, "Stage 1 onFailure: ");
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    public void callCrifStage2(final String responseStepOne, final String userAns) {
        String progress_message="";
        if(BuildConfig.DEBUG){
            progress_message = "Call Stage 2..";
        } else {
            progress_message = "Loading..";
        }
        final MaterialDialog progressDialog = showProgress(getActivity(), progress_message, "");
        progressDialog.show();
        Log.d(TAG, "======================callCrifStepTwo:========================= ");
        CRIFApi crifApi = new CRIFApi();
        crifApi.init(generateCrifCredStepTwo(responseStepOne, userAns));
        Log.d(TAG, "Stage2:init");
        Call<ResponseBody> call = crifApi.initRetrofit().postData(CRIFApi.RESPONSE_URL);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (isAdded() && !isDetached()) {
                    try {
                        progressDialog.dismiss();
                        String stage2Res = "";
                        Log.d(TAG, "Stage2:response " + response);
                        try {
                            if (response.isSuccessful()) {
                                stage2Res = response.body().string();
                                Log.d(TAG, "Stage2: " + stage2Res);
                                if (stage2Res.contains("ERROR")) {
                                    crif_api_error = "Stage 2: Response" + stage2Res;
                                    showStage1Error(stage2Res, 2);
                                } else {
                                    String responseStage2 = getCriefAccesscode(CRIFApi.KEY, stage2Res, "D");
                                    if (responseStage2 != null)
                                        Log.d(TAG, "Stage2Response decrypted: " + responseStage2);

                                    String[] resArray = responseStage2.split("\\|");
                                    String status = resArray[2];
                                    if (status.equals("S11")) {
                                        Log.d(TAG, "Stage2 : Need to answer ques" + status);
                                        String ques = null;
                                        if (resArray.length > 3) {
                                            ques = resArray[3];
                                            Log.d(TAG, "Stage2 Ques: " + ques);
                                            if (ques == null || ques.equals("null")) {
                                                Log.d(TAG, "Stage2 No Ques Available: " + ques);
                                                showBerauError("Sorry, Could not process further", 2);
                                            }
                                        }
                                        if (resArray.length > 4) {
                                            String options = resArray[4];
                                            if (resArray.length > 5) {
                                                String[] optionsArray = options.split(" ~ ");
                                                if (ques != null) {
                                                    showQuestionDialog(ques, optionsArray, responseStepOne);
                                                }
                                                Log.d(TAG, "Stage2 Answers: " + Arrays.toString(optionsArray));
                                            }
                                        }

                                    } else if (status.equals("S01")) {
                                        Log.d(TAG, "Stage2 : can go to stage3 " + status);
                                        callCrifStage3(responseStage2);
                                    } else if (status.equals("S02")) {
                                        Log.d(TAG, "Stage2 : can't go to stage3 all anwser wrong " + status);
                                        showBerauError("Can't go to stage3 all anwser wrong", 2);
                                    } else if (status.equals("S03")) {
                                        Log.d(TAG, "Stage2 : User Cancelled the Transaction " + status);
                                        showBerauError("User Cancelled the Transaction", 2);
                                    } else if (status.equals("S05")) {
                                        Log.d(TAG, "Stage2 : User Validations Failure " + status);
                                        showBerauError("User Validations Failure ", 2);
                                    } else if (status.equals("S08")) {
                                        Log.d(TAG, "Stage2 : Technical Error " + status);
                                        showBerauError("Technical Error ", 2);
                                    } else if (status.equals("S09")) {
                                        Log.d(TAG, "Stage2 : No Hit " + status);
                                        //todo what to be sent as bureau score
                                        //showBerauError("No Hit ", 2);
                                        bureau_status = "Verification But Miss";
                                        calluserCreditSubmission(bureau_score, bureau_status, null, bureau_order_id, bureau_report_id);
                                    } else if (status.equals("S10")) {
                                        Log.d(TAG, "Stage2 : can go to stage3 " + status);
                                        callCrifStage3(responseStage2);
                                    }
                                }
                            } else {
                                Log.d(TAG, "Stage2:unsucessfull " + response);
                            }
                        } catch (Exception e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            crif_api_error = "Stage 2: Response" + stage2Res;
                            calluserCreditSubmission(bureau_score, bureau_status, crif_api_error, bureau_order_id, bureau_report_id);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                try{
                    progressDialog.dismiss();
                } catch (Exception e){
                    e.printStackTrace();
                }
                Log.e(TAG, "Stage 2 onFailure: ");
            }
        });
    }

    public void callCrifStage3(final String responseStageTwo) {
        try {
            String progress_message = "";
            if (BuildConfig.DEBUG) {
                progress_message = "Call Stage 3..";
            } else {
                progress_message = "Loading..";
            }
            final MaterialDialog progressDialog = showProgress(getActivity(), progress_message, "");
            progressDialog.show();
            CRIFApi crifApi = new CRIFApi();
            crifApi.init(generateCrifCredStepThree(responseStageTwo));
            Call<ResponseBody> call = crifApi.initRetrofit().postData(CRIFApi.RESPONSE_URL);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (isAdded() && !isDetached()) {
                        String result = "";
                        try {
                            progressDialog.dismiss();
                            if (response.isSuccessful()) {
                                BufferedReader reader = null;
                                StringBuilder sb = new StringBuilder();
                                try {
                                    reader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                                    String line;
                                    try {
                                        while ((line = reader.readLine()) != null) {
                                            sb.append(line);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    result = sb.toString();
                                    Log.d(TAG, "Stage3: " + result);
                                    bureau_status = CRIFApi.BUREAU_STATUS_STAGE3;
                                    parseBureauGetScore(result); // GET bureau Score
                                    calluserCreditSubmission(bureau_score, bureau_status, result, bureau_order_id, bureau_report_id);
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    e.printStackTrace();
                                    crif_api_error = "Stage 3: Response" + result;
                                }
                            }
                        } catch (Exception e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            crif_api_error = "Stage 3: Response" + result;
                            calluserCreditSubmission(bureau_score, bureau_status, crif_api_error, bureau_order_id, bureau_report_id);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    try{
                        progressDialog.dismiss();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    Log.e(TAG, "Stage 3 onFailure: ");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Stage 3 onFailure: ");
        }
    }

    public String getCriefAccesscode(String encKey, String requestString, String type) {

        Log.d(TAG, "getCriefAccesscode: start " + requestString);

        String progress_message="";
        if(BuildConfig.DEBUG){
            progress_message = "Encrypting..";
        } else {
            progress_message = "Loading..";
        }
        final MaterialDialog progressDialog = showProgress(getActivity(), progress_message, "");
        progressDialog.show();
        CRIFApi crifApi = new CRIFApi();
        crifApi.init(generateCrifCredForEncDec(encKey, requestString, type));
        Call<ResponseBody> call = crifApi.initRetrofit().postData(CRIFApi.ENCRYPT_URL);
        try {
            if (isAdded() && !isDetached()) {
                Log.d(TAG, "getCriefAccesscode: call before " + requestString);
                progressDialog.dismiss();
            }
            return call.execute().body().string();
        } catch (Exception e) {
            Log.d(TAG, "getCriefAccesscode: error " + requestString);
            progressDialog.dismiss();
            e.printStackTrace();
        }
        return null;
    }

    public CRIFCreds generateCrifCredForEncDec(String encKey, String requestString, String type) {
        CRIFCreds crifCreds = new CRIFCreds();
        crifCreds.setAccept("text/plain");
        crifCreds.setUserId(CRIFApi.userId);
        crifCreds.setPassword(CRIFApi.password);
        crifCreds.setKey(encKey);
        crifCreds.setType(type);
        crifCreds.setEncryptedString(requestString);
        return crifCreds;
    }

    public CRIFCreds generateCrifCredStepOne() {
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String timestamp = s.format(new Date());
        String accessCodeString = CRIFApi.userId + "|" + CRIFApi.customerId + "|" + CRIFApi.productCode + "|" + CRIFApi.password + "|" + timestamp;
        String accessCode = getCriefAccesscode(CRIFApi.KEY, accessCodeString, "E");
        Random rand = new Random();
        int n = rand.nextInt(50000) + 1;
        String orderId = "WB" + n;


        CRIFCreds crifCreds = new CRIFCreds();
        crifCreds.setOrderId(orderId);
        crifCreds.setAccept("application/xml");
        crifCreds.setAccessCode(accessCode);
        crifCreds.setAppId(CRIFApi.APP_ID);

        //case 1
        //String requestString="Nitin||Jain||10-10-1986|||02228673678|||"+"jayp@wishbook.io"+"||AHJPT6190N||||||||NA|||307, BHARAT CHAMBER 52 BARODA STREET P.D MELLO ROAD, CARNAC BUNDER|Mumbai|Mumbai|Maharashtra|400009|india|||||||"+ CRIFApi.customerId+"|"+CRIFApi.productCode+"|Y|";

        //case 2
        //String requestString = "Shashikiran|Goud|Jalla|Male||45||123456789|||" + "jayp@wishbook.io" + "||AFUPJ7365N||LHR/1631969||000538/060257|UID900|||PRAKASH|||GANGA ELECTRICALS,  SHOP NO 3 JAIN MANDIR|SAHARANPUR|SAHARANPUR|UP|247001|India|||||||" + CRIFApi.customerId + "|" + CRIFApi.productCode + "|Y|";

        //case 3
        // String requestString="Naresh||Pruthi|Male||45||123456789|||"+"jayp@wishbook.io"+"||AFUPJ7365N||LHR/1631969||000538/060257|UID900|||PRAKASH|||GANGA ELECTRICALS,  SHOP NO 3 JAIN MANDIR|SAHARANPUR|SAHARANPUR|UP|247001|India|||||||"+ CRIFApi.customerId+"|"+CRIFApi.productCode+"|Y|";

        //case 4
        //String requestString = "Shailesh||Borkar|Male||45||123456789|||" + "jayp@wishbook.io" + "||AFUPJ7365N||LHR/1631969||000538/060257|UID900|||PRAKASH|||GANGA ELECTRICALS,  SHOP NO 3 JAIN MANDIR|SAHARANPUR|SAHARANPUR|UP|247001|India|||||||" + CRIFApi.customerId + "|" + CRIFApi.productCode + "|Y|";

        String requestString = requestStringForCRIF(oldKycData);
        requestString = requestString.replaceAll("\n","");

        crifCreds.setEncRequest(getCriefAccesscode(accessCode, requestString, "E"));
        crifCreds.setMerchantID(CRIFApi.MERCHANT_ID);
        return crifCreds;
    }

    public String requestStringForCRIF(RequestCreditRating requestCreditRating) {
        String customerId=CRIFApi.customerId;
        String productId=CRIFApi.productCode;
        String firstName="";
        String middleName="";
        String lastName="";
        if(requestCreditRating.getFull_name()!=null){
            String[] splited = requestCreditRating.getFull_name().split(" ");
            if(splited.length == 1){
                firstName = splited[0];
            } else if(splited.length == 2){
                firstName = splited[0];
                lastName = splited[1];
            } else if(splited.length > 2) {
                firstName = splited[0];
                middleName = splited[1];
                ArrayList<String> temp = new ArrayList<>() ;
                for (int i=2;i<splited.length;i++){
                    temp.add(splited[i]);
                }
                lastName = StaticFunctions.ArrayListToString(temp," ");
            }

        }
        requestCreditRating.setEmail("creditreports@wishbook.io");
        String dob= "";

        if (oldKycData.getBirth_date() != null) {
            try {
                String myFormat = "dd-MM-yyyy"; //In which you need put here
                String serverFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                SimpleDateFormat serverFormatter = new SimpleDateFormat(serverFormat,Locale.US);
                dob = sdf.format(serverFormatter.parse(oldKycData.getBirth_date()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String str = firstName + "|" + middleName + "|" + lastName + "|"
                + requestCreditRating.getGender() + "|" + dob + "|" + "" + "|" +
                "" + "|" + requestCreditRating.getMobile_no() + "|" + "" + "|" + "" + "|"
                + requestCreditRating.getEmail() + "|" + "" + "|" + requestCreditRating.getPan_card() +
                "|" + "" + "|" + "" + "|" + "" + "|" + "" + "|" + requestCreditRating.getAadhar_card() + "|"
                + "" + "|" + "" + "|" + requestCreditRating.getFather_name() + "|"
                + "" + "|" + requestCreditRating.getSpouse_name() + "|"
                + requestCreditRating.getAddress() + "|" + requestCreditRating.getCity() + "|" + requestCreditRating.getCity() + "|" + requestCreditRating.getState() + "|"
                + requestCreditRating.getPincode() + "|" + "India" + "|" + "" + "|" + "" + "|" + "" + "|" + "" + "|" + "" + "|" + "" + "|"
                + customerId + "|" + productId + "|" + "Y" + "|";

        Log.d(TAG, "requestStringForCRIF: "+str);
        return str;
    }

    public CRIFCreds generateCrifCredStepTwo(String responseStepOne, String userAns) {


        String[] stringArray = responseStepOne.split("\\|");
        Log.d(TAG, "generateCrifCredStepTwo " + Arrays.toString(stringArray));


        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String timestamp = s.format(new Date());
        String accessCodeString = CRIFApi.userId + "|" + CRIFApi.customerId + "|" + CRIFApi.productCode + "|" + CRIFApi.password + "|" + timestamp;
        String accessCode = getCriefAccesscode(CRIFApi.KEY, accessCodeString, "E");


        CRIFCreds crifCreds = new CRIFCreds();
        crifCreds.setAccept("application/xml");
        crifCreds.setOrderId(stringArray[0]);
        bureau_order_id = stringArray[0];
        bureau_report_id = stringArray[1];
        crifCreds.setReportId(stringArray[1]);
        crifCreds.setUserAns(userAns);
        crifCreds.setAccessCode(accessCode);
        crifCreds.setAppId(CRIFApi.APP_ID);
        String redirectURL = "https://cir.crifhighmark.com/Inquiry/B2B/secureService.action";

        String requestString = stringArray[0] + "|" + stringArray[1] + "|" + accessCode + "|" + redirectURL + "|N|N|Y|";

        crifCreds.setEncRequest(getCriefAccesscode(CRIFApi.KEY, requestString, "E"));
        crifCreds.setMerchantID(CRIFApi.MERCHANT_ID);
        crifCreds.setRequestType("Authorization");
        return crifCreds;
    }

    public CRIFCreds generateCrifCredStepThree(String responseStepTwo) {


        String[] stringArray = responseStepTwo.split("\\|");
        Log.d(TAG, "generateCrifCredStepTwo " + Arrays.toString(stringArray));


        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String timestamp = s.format(new Date());
        String accessCodeString = CRIFApi.userId + "|" + CRIFApi.customerId + "|" + CRIFApi.productCode + "|" + CRIFApi.password + "|" + timestamp;
        String accessCode = getCriefAccesscode(CRIFApi.KEY, accessCodeString, "E");


        CRIFCreds crifCreds = new CRIFCreds();
        crifCreds.setAccept("application/xml");
        crifCreds.setContent_type("text/plain");
        crifCreds.setOrderId(stringArray[0]);
        crifCreds.setReportId(stringArray[1]);
        crifCreds.setAccessCode(accessCode);
        crifCreds.setAppId(CRIFApi.APP_ID);
        crifCreds.setMerchantID(CRIFApi.MERCHANT_ID);

        return crifCreds;
    }

    public void showQuestionDialog(String question, final String[] options, final String responseStepOne) {
        new MaterialDialog.Builder(getActivity())
                .cancelable(false)
                .content(R.string.temp_1, question)
                .titleColor(getResources().getColor(R.color.purchase_dark_gray))
                .items(options)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        Log.e(TAG, "onSelection: " + which);
                        if (which != -1) {
                            callCrifStage2(responseStepOne, options[which]);
                        } else {
                            Toast.makeText(getActivity(), "Please select any one option", Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    }
                })
                .positiveText("OK")
                .show();

    }

    public static MaterialDialog showProgress(Context context, String title, String message) {
        return new MaterialDialog.Builder(context)
                .title(title)
                .cancelable(false)
                .content("Please wait..")
                .progress(true, 0).build();
    }


    public void parseBureauGetScore(String xmlContent) {
        InputStream in = new ByteArrayInputStream(xmlContent.getBytes());
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(in);
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("SCORES");
            System.out.println("Scroes  :" + nList.getLength());
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    NodeList scoreList = nNode.getChildNodes();
                    Element eElement = (Element) nNode;
                   /* Log.e(TAG, "parse Type: " + eElement.getElementsByTagName("SCORE-TYPE").item(0).getTextContent());
                    Log.e(TAG, "parse value: " + eElement.getElementsByTagName("SCORE-VALUE").item(0).getTextContent());
                    Log.e(TAG, "parse comments: " + eElement.getElementsByTagName("SCORE-COMMENTS").item(0).getTextContent());*/
                    bureau_score = eElement.getElementsByTagName("SCORE-VALUE").item(0).getTextContent();
                    System.out.println("Sc  :" + scoreList.getLength());
                } else {
                    System.out.println("NOT A NODE" + i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMissingFieldDialog(boolean isDobEmpty, boolean isFathernameEmpty, boolean isMobileEmpty, boolean isPanEmpty) {
        StringBuilder text = new StringBuilder();
        if(isPanEmpty){
            text.append("\u2022"+"PAN No." + "\n");
        }

        if (isDobEmpty) {
            text.append("\u2022"+"Date of birth" + "\n");
        }

        if (isFathernameEmpty) {
            text.append("\u2022"+"Father's/Spouse's Name as per PAN card\n");
        }

        if (isMobileEmpty) {
            text.append("\u2022"+"Mobile number\n");
        }
        String temp = String.format(getActivity().getResources().getString(R.string.field_missing_subtext_bureau), text);
        new MaterialDialog.Builder(getActivity())
                .title(R.string.field_missing_title_bureau)
                .titleColor(getResources().getColor(R.color.red))
                .cancelable(true)
                .content(temp)
                .positiveText("OK")
                .negativeText("Proceed anyway")
                .negativeColor(getResources().getColor(R.color.purchase_medium_gray))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String full_name = edit_full_name.getText().toString()+ " "+edit_last_name.getText().toString();
                        postKycData(getActivity(), edit_aadhar_number.getText().toString(), edit_address.getText().toString(),
                                edit_pincode.getText().toString(), edit_city.getText().toString(), edit_state.getText().toString(),
                               full_name, gender, edit_dob.getText().toString(), edit_pan_card_number.getText().toString(), edit_father_name.getText().toString(), edit_spouce_name.getText().toString(), edit_mobile_number.getText().toString(), edit_email.getText().toString(), false);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    public void afterProceesAnyway() {
        new MaterialDialog.Builder(getActivity())
                .cancelable(false)
                .content(Application_Singleton.getResourceString(R.string.credit_without_approve_dialog))
                .positiveText(Application_Singleton.getResourceString(R.string.okay))
                .negativeText(Application_Singleton.getResourceString(R.string.call_wb_support_label))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + PrefDatabaseUtils.getPrefWishbookSupportNumberFromConfig(getActivity())));
                        getActivity().startActivity(intent);
                        UserInfo.getInstance(getActivity()).setCreditScore("0");
                        Application_Singleton.isChangeCreditScrore = true;
                        getActivity().finish();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        Application_Singleton.isChangeCreditScrore = true;
                        UserInfo.getInstance(getActivity()).setCreditScore("0");
                        getActivity().finish();
                    }
                })
                .show();
    }

    private void calluserCreditSubmission(final String bureauScore, String bureauStatus, String bureauXml, String orderID, String reportID) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        RequestBureau requestBureau = new RequestBureau();
        requestBureau.setBureau_score(bureauScore);
        requestBureau.setBureau_type("Crif");
        requestBureau.setBureau_status(bureauStatus);
        requestBureau.setBureau_xml(bureauXml);
        requestBureau.setBureau_order_id(orderID);
        requestBureau.setBureau_report_id(reportID);
        requestBureau.setCompany(UserInfo.getInstance(getActivity()).getCompany_id());

        HttpManager.METHOD method = HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS;
        String url = URLConstants.companyUrl(getActivity(), "user-credit-submissions", "");

        if (isEditMode && oldUserCreditSubmission!=null && oldUserCreditSubmission.getId()!=null) {
           /* method = HttpManager.METHOD.PUTWITHPROGRESS;
            url = URLConstants.companyUrl(getActivity(), "user-credit-submissions", "") + oldUserCreditSubmission.getId() + "/";*/

            method = HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS;
            url = URLConstants.companyUrl(getActivity(), "user-credit-submissions", "");
        } else {
            method = HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS;
            url = URLConstants.companyUrl(getActivity(), "user-credit-submissions", "");
        }

        sendCreditApplicationSubmitAnalytics(bureauScore,bureauStatus);

        HttpManager.getInstance(getActivity()).requestwithObject(method, url, (Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(requestBureau), JsonObject.class)), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(final String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    if(bureauScore==null){
                        UserInfo.getInstance(getActivity()).setCreditScore("0");
                    } else {
                        UserInfo.getInstance(getActivity()).setCreditScore(bureauScore);
                    }

                    Application_Singleton.isChangeCreditScrore = true;
                    Toast.makeText(getActivity(), "Application has been submitted", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);

            }
        });
    }


    private void showBerauError(String error, final int stage) {
        new MaterialDialog.Builder(getActivity())
                .content(error)
                .cancelable(false)
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if(stage==1){
                            btn_submit.setVisibility(View.VISIBLE);
                            btn_submit.setEnabled(true);
                        } else {
                            calluserCreditSubmission(bureau_score, bureau_status, null, bureau_order_id, bureau_report_id);
                        }
                    }
                })
                .show();
        bureau_status = "Verification But Miss";
    }

    private void showStage1Error(String errorXML,int stage){

        if(errorXML.contains("&")){
            errorXML = errorXML.replace("&","");
        }

        String status = "";
        String error = "";
        InputStream in = new ByteArrayInputStream(errorXML.getBytes());
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(in);
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("RESPONSE");
            status = getNodeValue(nList,"STATUS");
            error = getNodeValue(nList,"ERROR");
            showBerauError(error,stage);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getNodeValue(NodeList nodeList,String tagname) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node nNode = nodeList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE ) {
                Element eElement = (Element) nNode;
                return eElement.getElementsByTagName(tagname).item(0).getTextContent();
            }
        }
        return null;
    }

    private void sendAnalytics() {
        WishbookEvent wishbookEvent= new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("CreditApplication_screen");
        HashMap<String,String> prop = new HashMap<>();
        if(from !=null) {
            if(from.equalsIgnoreCase(Fragment_Summary.class.getSimpleName())) {
                prop.put("source","Home page");
            } else if(from.equalsIgnoreCase(Fragment_MyBusiness.class.getSimpleName())) {
                prop.put("source","My Business Page");
            }
        }

        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(getActivity(),wishbookEvent);
    }


    private void sendCreditApplicationSubmitAnalytics(String crif_score, String crif_status) {
        WishbookEvent wishbookEvent= new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("CreditApplication_Submitted");
        HashMap<String,String> prop = new HashMap<>();
        prop.put("crif_status",crif_status);
        prop.put("crif_score",crif_score);

        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(getActivity(),wishbookEvent);
    }
}
