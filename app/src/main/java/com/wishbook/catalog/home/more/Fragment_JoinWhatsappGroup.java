package com.wishbook.catalog.home.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_JoinWhatsappGroup extends GATrackedFragment {

    private View v;

    @BindView(R.id.txt_singlepc_whatsapp_group)
    TextView txt_singlepc_whatsapp_group;

    @BindView(R.id.txt_full_set_whatsapp_group)
    TextView txt_full_set_whatsapp_group;

    @BindView(R.id.txt_fashion_whatsapp_group)
    TextView txt_fashion_whatsapp_group;

    public Fragment_JoinWhatsappGroup() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_join_whatsapp_group, container, false);
        ButterKnife.bind(this, v);
        if(PrefDatabaseUtils.getPrefSinglePcWhatsappGroup(getActivity())!=null) {
            txt_singlepc_whatsapp_group.setText(PrefDatabaseUtils.getPrefSinglePcWhatsappGroup(getActivity()));
        }

        if(PrefDatabaseUtils.getPrefFullSetWhatsappGroup(getActivity())!=null) {
            txt_full_set_whatsapp_group.setText(PrefDatabaseUtils.getPrefFullSetWhatsappGroup(getActivity()));
        }

        if(PrefDatabaseUtils.getPrefFashionTrendWhatsappGroup(getActivity())!=null) {
            txt_fashion_whatsapp_group.setText(PrefDatabaseUtils.getPrefFashionTrendWhatsappGroup(getActivity()));
        }

        return v;
    }


}
