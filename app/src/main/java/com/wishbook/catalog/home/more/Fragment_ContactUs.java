package com.wishbook.catalog.home.more;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.wishbook.catalog.GATrackedFragment;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wishbook.catalog.R;

public class Fragment_ContactUs extends GATrackedFragment {

    private View v;
    private LinearLayout call_phone;
    private LinearLayout call_web;
    private LinearLayout call_location;
    private LinearLayout call_mail;
    private LinearLayout call_land;
    private ImageView call_fb;
    private ImageView call_linkedin;

    public Fragment_ContactUs() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_contact_us, container, false);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.appbar);
        call_phone = (LinearLayout) v.findViewById(R.id.call_phone);
        call_web = (LinearLayout) v.findViewById(R.id.call_web);
        call_location = (LinearLayout) v.findViewById(R.id.call_location);
        call_mail = (LinearLayout) v.findViewById(R.id.call_mail);
        call_land = (LinearLayout) v.findViewById(R.id.call_land);
        call_fb = (ImageView) v.findViewById(R.id.call_fb);
        call_linkedin = (ImageView) v.findViewById(R.id.call_linkedin);
        call_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.facebook.com/wishbook2";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        call_linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.linkedin.com/company/wishbook-infoservices";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        call_land.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "tel:+912616718989";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        call_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "mailto:support@wishbook.io";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        call_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "http://maps.google.com/?q= ring road,surat,gujarat,india";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
        call_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.wishbook.io";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        call_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "tel:+918469218980";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });
        toolbar.setVisibility(View.GONE);
        return v;
    }

}
