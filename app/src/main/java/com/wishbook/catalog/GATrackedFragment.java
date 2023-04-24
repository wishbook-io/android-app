package com.wishbook.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Base Class for Fragment in app
 * Used to common functionality such as Common progress bar and GA Analytics
 */
public class GATrackedFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    View v;
    RelativeLayout relativeProgress;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.common_progress_dialog, container, false);
        relativeProgress = (RelativeLayout) v.findViewById(R.id.relative_progress);
        return v;
    }




    @Override
    public void onResume() {
        super.onResume();
        Application_Singleton singleton = new Application_Singleton();
        singleton.trackScreenView(getClass().getSimpleName(), getActivity());
    }

    public void showProgress() {
        if(relativeProgress!=null){
            relativeProgress.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress() {
        if(relativeProgress !=null) {
            relativeProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
