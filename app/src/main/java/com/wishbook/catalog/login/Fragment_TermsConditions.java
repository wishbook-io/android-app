package com.wishbook.catalog.login;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.wishbook.catalog.Application_Singleton;

import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.login.adapters.TermsConditionsAdapter;
import com.wishbook.catalog.login.models.TermsConditionModel;
import com.wishbook.catalog.login.models.TermsCondition_Response;

/**
 * Created by root on 31/8/16.
 */
public class Fragment_TermsConditions extends GATrackedFragment {



    List<TermsConditionModel> tncList = new ArrayList<>();
    TermsConditionsAdapter adapter;

    public Fragment_TermsConditions() {
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.terms_conditions,container,false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TermsConditionsAdapter(getActivity(),tncList);
        recyclerView.setAdapter(adapter);

        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"tnc",""), null, null, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.d("response",response.toString());
                Log.v("sync response", response);
                TermsCondition_Response responseObj= Application_Singleton.gson.fromJson(response,TermsCondition_Response.class);

                tncList.add(new TermsConditionModel("Main Description",Html.fromHtml(responseObj.getMain_description().toString()).toString()));
                for(int i=0;i<responseObj.getSub_description().length;i++)
                {
                    tncList.add(new TermsConditionModel(Html.fromHtml(responseObj.getSub_description()[i].getHeading().toString()).toString(),
                            Html.fromHtml(responseObj.getSub_description()[i].getDescription()).toString()));
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onResponseFailed(ErrorString error) {
            Log.d("Error",error.toString());
            }
        });

        return view;
    }
    public void setUpToolbar(final AppCompatActivity context, Toolbar toolbar) {
        toolbar.setTitle("Terms and Condition");
        toolbar.setNavigationIcon(context.getResources().getDrawable(R.drawable.ic_toolbar_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSupportFragmentManager().popBackStack();
            }
        });
    }
}
