package com.wishbook.catalog.home.more.adapters;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.MultiSelectModel;

import java.util.ArrayList;

/**
 * Created by Abubakker on 13/4/17.
 */
public class MultiSelectDialog extends AppCompatDialogFragment implements SearchView.OnQueryTextListener, View.OnClickListener {

    private RecyclerView mrecyclerView;
    private MutliSelectAdapter mutliSelectAdapter;
    private ArrayList<String> callBackListOfIds = new ArrayList<String>();
    private SearchView searchView;
    private String title = "";
    private Boolean validate = false;
    private TextView dialogTitle;

    public static ArrayList<String> selectedIdsList = new ArrayList<>();
    public ArrayList<MultiSelectModel> listOfAdapter = new ArrayList<>();
    private ArrayList<String> listOfEntriesToBeSelected = new ArrayList<>();

    private Listener listener;

    private Listener2 listener2;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog.setContentView(R.layout.custom_multi_select);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mrecyclerView = (RecyclerView) dialog.findViewById(R.id.recycler_view);
        searchView = (SearchView) dialog.findViewById(R.id.search_view);
        dialogTitle = (TextView) dialog.findViewById(R.id.title);

        dialogTitle.setText(title);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mrecyclerView.setLayoutManager(layoutManager);

         TextView done = (TextView) dialog.findViewById(R.id.done);
         TextView cancel = (TextView) dialog.findViewById(R.id.cancel);

        done.setOnClickListener(this);
        cancel.setOnClickListener(this);

        listOfAdapter = setCheckedIDS(listOfAdapter, listOfEntriesToBeSelected);

        mutliSelectAdapter = new MutliSelectAdapter(listOfAdapter, getActivity());

        mrecyclerView.setAdapter(mutliSelectAdapter);

        searchView.setOnQueryTextListener(this);
        searchView.onActionViewExpanded();
        searchView.clearFocus();


        return dialog;
    }

    private ArrayList<MultiSelectModel> setCheckedIDS(ArrayList<MultiSelectModel> multiselectdata, ArrayList<String> listOfIdsSelected) {
        for (int i = 0; i < multiselectdata.size(); i++) {
            multiselectdata.get(i).setSelected(false);
            for (int j = 0; j < listOfIdsSelected.size(); j++) {
                if (listOfIdsSelected.get(j).equals(multiselectdata.get(i).getId())) {
                    multiselectdata.get(i).setSelected(true);
                }
            }
        }
        return multiselectdata;
    }


    private ArrayList<MultiSelectModel> filter(ArrayList<MultiSelectModel> models, String query) {
        query = query.toLowerCase();
        final ArrayList<MultiSelectModel> filteredModelList = new ArrayList<>();
        if (query.equals("") | query.isEmpty()) {
            filteredModelList.addAll(models);
            return filteredModelList;
        }

        for (MultiSelectModel model : models) {
            final String name = model.getName().toLowerCase();
            if (name.contains(query)) {
                filteredModelList.add(model);
            }
        }


        return filteredModelList;
    }

    public MultiSelectDialog validate(Boolean validate) {
        this.validate = validate;
        return this;
    }

    public MultiSelectDialog title(String title) {
        this.title = title;
        return this;
    }


    public MultiSelectDialog preSelectIDsList(ArrayList<String> list) {
        this.listOfEntriesToBeSelected = list;
        return this;
    }

    public MultiSelectDialog multiSelectList(ArrayList<MultiSelectModel> list) {
        this.listOfAdapter = list;
        return this;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(mutliSelectAdapter!=null) {
            mutliSelectAdapter.filter(newText);
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.done:
                if(validate){
                    callBackListOfIds.clear();
                    if (mutliSelectAdapter.getAllSelected().size() > 0) {
                        String data = StaticFunctions.ArrayListToString(mutliSelectAdapter.getAllSelected(),StaticFunctions.COMMASEPRATEDSPACE);
                        callBackListOfIds.addAll(mutliSelectAdapter.getAllSelected());
                        if(listener!=null){
                            listener.onDismiss(callBackListOfIds,data,mutliSelectAdapter.getNameList());
                        }
                        if(listener2!=null){
                            listener2.onDismiss2(callBackListOfIds,StaticFunctions.ArrayListToString(mutliSelectAdapter.getAllSelectedValue(),StaticFunctions.COMMASEPRATEDSPACE),mutliSelectAdapter.getNameList());
                        }

                        dismiss();
                    } else {
                          Toast.makeText(getActivity(), "Please select atleast one option", Toast.LENGTH_LONG).show();
                    }
                }else {
                    callBackListOfIds.clear();
                    String data = StaticFunctions.ArrayListToString(mutliSelectAdapter.getAllSelected(),StaticFunctions.COMMASEPRATEDSPACE);
                    callBackListOfIds.addAll(mutliSelectAdapter.getAllSelected());
                    if(listener!=null){
                        listener.onDismiss(callBackListOfIds,data,mutliSelectAdapter.getNameList());
                    }
                    if(listener2!=null){
                        listener2.onDismiss2(callBackListOfIds,StaticFunctions.ArrayListToString(mutliSelectAdapter.getAllSelectedValue(),StaticFunctions.COMMASEPRATEDSPACE),mutliSelectAdapter.getNameList());
                    }
                    dismiss();
                }
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    public interface Listener {
        void onDismiss(ArrayList<String> ids,String data,String[] arrayNames);

    }

    public interface Listener2 {
        void onDismiss2(ArrayList<String> ids,String data,String[] arrayNames); // for return String value in data
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        selectedIdsList.clear();
    }

    public void setCallbackListener(Listener listener) {
        this.listener = listener;
    }

    public void setCallbackListener2(Listener2 listener) {
        this.listener2 = listener;
    }

}
