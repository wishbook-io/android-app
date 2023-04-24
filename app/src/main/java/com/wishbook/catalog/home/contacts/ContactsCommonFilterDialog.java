package com.wishbook.catalog.home.contacts;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonadapters.CommonFilterAdapter;
import com.wishbook.catalog.commonmodels.postpatchmodels.CommmonFilterOptionModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 20/5/17.
 */

public class ContactsCommonFilterDialog extends DialogFragment {

    private RecyclerView recycler_view_filter_list;
    CommonFilterAdapter adapter;
/*

    @BindView(R.id.toolbar)
    Toolbar toolbar;
*/

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.reset_fltr_btn)
    AppCompatButton reset_fltr_btn;

    @BindView(R.id.submit_fltr_btn)
    AppCompatButton submit_fltr_btn;
    private String selectedFilter;

    private ArrayList<CommmonFilterOptionModel> itemlist = new ArrayList<>();
    private ArrayList<Integer> selectedIDs = new ArrayList<>();

    public void setList( ArrayList<CommmonFilterOptionModel> itemlist){
        this.itemlist=itemlist;
    }

    public interface FilterDismissListener{
        void selectedIDs(ArrayList<Integer> ids);
    }

    FilterDismissListener listener;
    public void setListener(FilterDismissListener listener){
        this.listener=listener;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog.setContentView(R.layout.filter_main_layout);
        recycler_view_filter_list = (RecyclerView) dialog.findViewById(R.id.recycler_view_filter_list);

        recycler_view_filter_list.setHasFixedSize(true);
        recycler_view_filter_list
                .setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        adapter = new CommonFilterAdapter(itemlist);


        //for selecting IDs previously
        selectedIDs.clear();
        for(int i=0;i<itemlist.size();i++){
            if(itemlist.get(i).getSelected()){
                selectedIDs.add(itemlist.get(i).getId());
            }
        }
        if(selectedIDs.size()>0){
            adapter.setSelected(selectedIDs);
        }



        if(itemlist.size() == 0 )
        {
            adapter = new CommonFilterAdapter(itemlist);
        }



        recycler_view_filter_list.setAdapter(adapter);// set adapter on recyclerview
        adapter.notifyDataSetChanged();

        ButterKnife.bind(this,dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);



        submit_fltr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                listener.selectedIDs(adapter.getSelected());
            }
        });

        reset_fltr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                listener.selectedIDs(null);
            }
        });


        return  dialog;
    }


}
