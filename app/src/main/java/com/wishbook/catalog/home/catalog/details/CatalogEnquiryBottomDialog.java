package com.wishbook.catalog.home.catalog.details;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CatalogEnquiryBottomDialog extends BottomSheetDialogFragment {



    @BindView(R.id.btn_send_enquiry)
    AppCompatButton btn_send_enquiry;

    @BindView(R.id.check_price)
    AppCompatCheckBox check_price;

    @BindView(R.id.check_fabric)
    AppCompatCheckBox check_fabric;

    @BindView(R.id.check_dispatch)
    AppCompatCheckBox check_dispatch;

    @BindView(R.id.img_close)
    ImageView img_close;

    CatalogEnquiryBottomDialog.CatalogEnquirySelectListener catalogEnquirySelectListener;


    public static CatalogEnquiryBottomDialog newInstance(String string) {
        CatalogEnquiryBottomDialog f = new CatalogEnquiryBottomDialog();
        if (string != null) {
            Bundle args = new Bundle();
            args.putString("previousSelected", string);
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
        View v = inflater.inflate(R.layout.catalog_enquiry_bottom_sheet_modal, container, false);
        ButterKnife.bind(this, v);
        Application_Singleton singleton = new Application_Singleton();
        singleton.trackScreenView(getClass().getSimpleName(), getActivity());
        check_price.setChecked(false);
        check_fabric.setChecked(false);
        check_dispatch.setChecked(false);
        initListener();
        return v;
    }

    public void initListener() {
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btn_send_enquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check_price.isChecked() || check_fabric.isChecked() || check_dispatch.isChecked()) {
                    if (catalogEnquirySelectListener != null) {
                        ArrayList<String> temp = new ArrayList<>();
                        if (check_price.isChecked())
                            temp.add(check_price.getText().toString());
                        if (check_fabric.isChecked())
                            temp.add(check_fabric.getText().toString());
                        if (check_dispatch.isChecked())
                            temp.add(check_dispatch.getText().toString());
                        catalogEnquirySelectListener.onSendEnquiry(StaticFunctions.ArrayListToString(temp, StaticFunctions.COMMASEPRATEDSPACE));
                        dismiss();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Select any one option", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public interface CatalogEnquirySelectListener {
        void onSendEnquiry(String enquiryabout);
    }

    public void setCatalogEnquirySelectListener(CatalogEnquiryBottomDialog.CatalogEnquirySelectListener catalogEnquirySelectListener) {
        this.catalogEnquirySelectListener = catalogEnquirySelectListener;
    }
}
