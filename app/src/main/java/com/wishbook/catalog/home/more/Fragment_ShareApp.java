package com.wishbook.catalog.home.more;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;

public class Fragment_ShareApp extends GATrackedFragment {


    private View view;

    public Fragment_ShareApp() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_share_app, ga_container, true);
        //setUpToolbar(getActivity(), toolbar);
        return view;
    }


  /*  public void setUpToolbar(final Context context, Toolbar toolbar) {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
        toolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.intro_text_color));
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_arrow_back_24dp);
        icon.setColorFilter(ContextCompat.getColor(context, R.color.intro_text_color), PorterDuff.Mode.SRC_IN);
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }*/


}
