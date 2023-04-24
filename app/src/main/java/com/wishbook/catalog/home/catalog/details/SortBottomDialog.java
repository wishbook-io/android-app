package com.wishbook.catalog.home.catalog.details;


import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SortBottomDialog extends BottomSheetDialogFragment {


    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.latest_selected)
    RadioButton recently_uploaded;

    @BindView(R.id.number_of_views_selected)
    RadioButton number_of_views_selected;
    @BindView(R.id.price_low_to_high_selected)
    RadioButton price_low_to_high_selected;

    @BindView(R.id.price_high_to_low_selected)
    RadioButton price_high_to_low_selected;

    @BindView(R.id.sort_by_group)
    RadioGroup sort_by_group;


    SortBySelectListener sortBySelectListener;

    public static SortBottomDialog newInstance(String string) {
        SortBottomDialog f = new SortBottomDialog();
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
        View v = inflater.inflate(R.layout.sort_by_bottom_sheet_modal, container, false);
        ButterKnife.bind(this, v);
        Application_Singleton singleton = new Application_Singleton();
        singleton.trackScreenView(getClass().getSimpleName(), getActivity());
        if (getArguments() != null) {
            if (getArguments().getString("previousSelected") != null) {
                try {
                    // TO Do selected sort
                    String ordering = getArguments().getString("previousSelected");
                    if (ordering.equals("-id")) {
                        recently_uploaded.setChecked(true);
                        recently_uploaded.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_primary));
                    } else if (ordering.equals("-views")) {
                        number_of_views_selected.setChecked(true);
                        number_of_views_selected.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_primary));
                    } else if (ordering.equals("price")) {
                        price_low_to_high_selected.setChecked(true);
                        price_low_to_high_selected.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_primary));
                    } else if (ordering.equals("-price")) {
                        price_high_to_low_selected.setChecked(true);
                        price_high_to_low_selected.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_primary));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                recently_uploaded.setChecked(true);
                recently_uploaded.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_primary));
            }
        } else {
            recently_uploaded.setChecked(true);
            recently_uploaded.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_primary));
        }


        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        sort_by_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                String ordrby;
                if (number_of_views_selected.isChecked()) {
                    ordrby = "-views";
                    Application_Singleton.trackEvent("Sort Catalog", "Click", number_of_views_selected.getText().toString());
                } else if (price_low_to_high_selected.isChecked()) {
                    ordrby = "price";
                    Application_Singleton.trackEvent("Sort Catalog", "Click", price_low_to_high_selected.getText().toString());
                } else if (price_high_to_low_selected.isChecked()) {
                    ordrby = "-price";
                    Application_Singleton.trackEvent("Sort Catalog", "Click", price_high_to_low_selected.getText().toString());
                }
                else {
                    //case where by recently_uploaded as default
                    ordrby = "-id";
                    Application_Singleton.trackEvent("Sort Catalog", "Click", recently_uploaded.getText().toString());
                }

                if (sortBySelectListener != null) {
                    sortBySelectListener.onCheck(ordrby);
                    dismiss();
                } else {
                    dismiss();
                }
            }
        });

        return v;
    }


    public interface SortBySelectListener {
        void onCheck(String check);
    }

    public void setSortBySelectListener(SortBySelectListener sortBySelectListener) {
        this.sortBySelectListener = sortBySelectListener;
    }
}
