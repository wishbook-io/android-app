package com.wishbook.catalog.login;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wishbook.catalog.R;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CompanyTypeBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @BindView(R.id.txt_ok)
    TextView txt_ok;

    @BindView(R.id.manufacturer)
    CheckBox manufacturer;

    @BindView(R.id.onlineretailer_reseller)
    CheckBox onlineretailer_reseller;

    @BindView(R.id.retailer)
    CheckBox retailer;

    @BindView(R.id.wholesaler_distributor)
    CheckBox wholesaler_distributor;

    @BindView(R.id.broker)
    CheckBox broker;

    @BindView(R.id.img_close)
    ImageView img_close;

    JSONObject object;

    CompanyTypeSelectListener companyTypeSelectListener;
    JSONObject companyJsonobject;


    static CompanyTypeBottomSheetDialogFragment newInstance(String string) {
        CompanyTypeBottomSheetDialogFragment f = new CompanyTypeBottomSheetDialogFragment();
        if(string!=null) {
            Bundle args = new Bundle();
            args.putString("previousSelected",string);
            f.setArguments(args);
        }
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.company_type_bottom_sheet_modal, container, false);
        ButterKnife.bind(this, v);
        object = new JSONObject();

        if(getArguments()!=null) {
            if(getArguments().getString("previousSelected")!=null) {
                try {
                    Log.i("TAG", "onCreateView: not null");
                    companyJsonobject = new JSONObject(getArguments().getString("previousSelected"));
                    if(companyJsonobject.getBoolean("manufacturer")){
                        manufacturer.setChecked(true);
                    }
                    if(companyJsonobject.getBoolean("wholesaler_distributor")){
                        wholesaler_distributor.setChecked(true);
                    }
                    if(companyJsonobject.getBoolean("retailer")){
                        retailer.setChecked(true);
                    }
                    if(companyJsonobject.getBoolean("online_retailer_reseller")){
                        onlineretailer_reseller.setChecked(true);
                    }
                    if(companyJsonobject.getBoolean("broker")){
                        broker.setChecked(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }


        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("TAG", "onClick: Ok Click");
                if (companyTypeSelectListener != null
                        && (manufacturer.isChecked() || onlineretailer_reseller.isChecked()
                        || retailer.isChecked() || wholesaler_distributor.isChecked()
                        || broker.isChecked())) {
                    try {
                        if (manufacturer.isChecked()) {
                            object.put("manufacturer", true);
                        } else {
                            object.put("manufacturer", false);
                        }
                        if (onlineretailer_reseller.isChecked()) {
                            object.put("online_retailer_reseller", true);
                        } else {
                            object.put("online_retailer_reseller", false);
                        }
                        if (retailer.isChecked()) {
                            object.put("retailer", true);
                        } else {
                            object.put("retailer", false);
                        }
                        if (wholesaler_distributor.isChecked()) {
                            object.put("wholesaler_distributor", true);
                        } else {
                            object.put("wholesaler_distributor", false);
                        }

                        if (broker.isChecked()) {
                            object.put("broker", true);
                        } else {
                            object.put("broker", false);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dismiss();
                    companyTypeSelectListener.onCheck(object.toString());
                } else {
                    Toast.makeText(getActivity(),"Please Select Company",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    public interface CompanyTypeSelectListener {
        void onCheck(String check);
    }

    public void setCompanyTypeSelectListener(CompanyTypeSelectListener companyTypeSelectListener){
        this.companyTypeSelectListener = companyTypeSelectListener;
    }
}

