package com.wishbook.catalog.home.contacts.details;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.wishbook.catalog.GATrackedFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.responses.Response_BuyingCompany;

public class Fragment_Details_Address extends GATrackedFragment {

    private View v;

    public Fragment_Details_Address() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.bdetadress, container, false);
        if (getArguments() != null) {
            String company = getArguments().getString("company", "");
            Response_BuyingCompany response_buyingCompany = new Gson().fromJson(company, Response_BuyingCompany.class);
            if(response_buyingCompany!=null) {
                if (response_buyingCompany.getBranches() != null) {
                    if (response_buyingCompany.getBranches().size() > 0) {
                        ((TextView) v.findViewById(R.id.c_name)).setText("" + response_buyingCompany.getBranches().get(0).getName());
                        ((TextView) v.findViewById(R.id.c_mob)).setText("" + response_buyingCompany.getBranches().get(0).getPhoneNumber());
                        String street = response_buyingCompany.getBranches().get(0).getStreetAddress();
                        if (street == null || street.equals("null")) {
                            street = "";
                        } else {
                            street = street + ",";
                        }
                        String city = response_buyingCompany.getBranches().get(0).getCity().getCityName();
                        if (city == null || city.equals("null")) {
                            city = "";
                        } else {
                            city = city + ",";
                        }
                        String state = response_buyingCompany.getBranches().get(0).getState().getStateName();
                        if (state == null || state.equals("null")) {
                            state = "";
                        }
                        ((TextView) v.findViewById(R.id.c_loc)).setText(

                                street + city + state
                        );


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            v.findViewById(R.id.c_loc).setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        }

                        v.findViewById(R.id.c_mob).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + ((TextView) v.findViewById(R.id.c_mob)).getText()));
                                getActivity().startActivity(intent);
                            }
                        });

                    }
                }
            }
        }
        return v;
    }
}
