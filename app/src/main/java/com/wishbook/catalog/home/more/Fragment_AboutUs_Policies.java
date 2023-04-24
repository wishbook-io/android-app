package com.wishbook.catalog.home.more;

import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;

public class Fragment_AboutUs_Policies extends GATrackedFragment implements View.OnClickListener{

    private View v;
    private CardView about_us;
    private CardView product_services;
    private CardView terms_of_use;
    private CardView privacy_policy;
    private CardView contact_us;
    private CardView user_manual;

    public Fragment_AboutUs_Policies() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_aboutus_policies, container, false);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.appbar);
        toolbar.setVisibility(View.GONE);

        about_us = (CardView) v.findViewById(R.id.about_us);
        product_services = (CardView) v.findViewById(R.id.product_services);
        privacy_policy = (CardView) v.findViewById(R.id.privacy_policy);
        terms_of_use = (CardView) v.findViewById(R.id.terms_of_use);
        user_manual = (CardView) v.findViewById(R.id.user_manual);
        contact_us = v.findViewById(R.id.contact_us);


        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getActivity(),Activity_AboutUs_WebView.class);
                intent.putExtra("show","about_us");
                getActivity().startActivity(intent);
            }
        });

        product_services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getActivity(),Activity_AboutUs_WebView.class);
                intent.putExtra("show","product_services");
                getActivity().startActivity(intent);
            }
        });

        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getActivity(),Activity_AboutUs_WebView.class);
                intent.putExtra("show","privacy_policy");
                getActivity().startActivity(intent);
            }
        });

        terms_of_use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getActivity(),Activity_AboutUs_WebView.class);
                intent.putExtra("show","terms_of_use");
                getActivity().startActivity(intent);
            }
        });

        contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application_Singleton.CONTAINER_TITLE = "Contact Us";
                Application_Singleton.CONTAINERFRAG = new Fragment_ContactUs();
                StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
            }
        });



        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.about_us:
                Intent intent =new Intent(getActivity(),Activity_AboutUs_WebView.class);
                intent.putExtra("show","about_us");
                getActivity().startActivity(intent);
                break;
            case R.id.product_services:

                break;
            case R.id.privacy_policy:

                break;
            case R.id.terms_of_use:

                break;
            case R.id.user_manual:

                break;

        }
    }
}
