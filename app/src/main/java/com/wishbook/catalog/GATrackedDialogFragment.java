package com.wishbook.catalog;

import androidx.fragment.app.DialogFragment;

/**
 * Base class for DialogFragment
 * Used for GA analytics tracking commom
 */
public class GATrackedDialogFragment extends DialogFragment {
    @Override
    public void onResume() {
        super.onResume();
        Application_Singleton singleton = new Application_Singleton();
        singleton.trackScreenView(getClass().getSimpleName(),getActivity());
    }
}
