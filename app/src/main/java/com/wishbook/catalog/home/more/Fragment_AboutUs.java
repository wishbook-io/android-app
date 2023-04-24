package com.wishbook.catalog.home.more;

import android.os.Bundle;
import com.wishbook.catalog.GATrackedFragment;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wishbook.catalog.R;

public class Fragment_AboutUs extends GATrackedFragment {

    private View v;

    public Fragment_AboutUs() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_about_us, container, false);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.appbar);
         toolbar.setVisibility(View.GONE);
        return v;
    }
}
