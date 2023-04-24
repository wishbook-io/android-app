package com.wishbook.catalog.home.catalog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatButton;

import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.wishbook.catalog.R;


import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vigneshkarnika on 10/05/16.
 */
public class Fragment_SortBy extends DialogFragment {

    @BindView(R.id.sort_by)
    RadioGroup sort_by;

    @BindView(R.id.btn_discard)
    AppCompatButton reset;

    @BindView(R.id.btn_apply)
    AppCompatButton sort_btn;

    HashMap<String, String> params;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.sort_by_dialog);
        ButterKnife.bind(this, dialog);



        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                //clearing local things
                Intent i = new Intent();
                saveFiltersLocally(null);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                dismiss();
            }
        });

        sort_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent()
                        .putExtra("parameters", params);
                saveFiltersLocally(params);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                dismiss();

            }
        });

        sort_by.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
              params = new HashMap<String, String>();
                switch (i){
                    case R.id.number_of_views_selected:
                        break;
                    case R.id.price_high_to_low_selected:
                        break;
                    case R.id.price_low_to_high_selected:
                        break;
                    case R.id.latest_selected:
                        params.put("ordering","id");
                        break;
                    case R.id.a_to_z_selected:
                        params.put("ordering","title");
                        break;
                    case R.id.z_to_a_selected:
                        params.put("ordering","-title");
                        break;
                    case R.id.user_rating_selected:
                        break;
                }
            }
        });

        return dialog;
    }

    private void saveFiltersLocally(HashMap<String, String> params) {
        SharedPreferences db = PreferenceManager.getDefaultSharedPreferences(getContext());

        SharedPreferences.Editor collection = db.edit();
        Gson gson = new Gson();
        String arrayList1 = gson.toJson(params);

        collection.putString("order_by", arrayList1);
        collection.commit();
    }


}
