package com.wishbook.catalog.home.contacts.add;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.ResponseCompanyRating;
import com.wishbook.catalog.home.adapters.BuyerRatingAdapter;
import com.wishbook.catalog.home.models.BuyerQuestions;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Buyer_Rating extends GATrackedFragment{


    @BindView(R.id.indicator1)
    LinearLayout indicator0;
    @BindView(R.id.indicator2)
    LinearLayout indicator1;
    @BindView(R.id.indicator3)
    LinearLayout indicator2;
    @BindView(R.id.indicator4)
    LinearLayout indicator3;
    @BindView(R.id.btn_submit)
    Button btn_submit;
    @BindView(R.id.frame)
    FrameLayout frame;
    static DiscreteScrollView scrollView;
    Boolean flag1,flag2,flag3,flag4;
    @BindView(R.id.supporting_text)
    TextView supportingText;
    BuyerRatingAdapter adapter;
    String company_id;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_buyer_rating, ga_container, true);
        ButterKnife.bind(this,v);
        scrollView = v.findViewById(R.id.item_picker);
        initView();
        initListener();
        return v;
    }

    public void initView() {

        supportingText.setText(String.format(getString(R.string.support_buyer_rating),getArguments().getCharSequence("buyer_name")));

        if(getArguments().getInt("mode")== Constants.CREDT_RATING_ADD_MODE) {
            //add mode
            adapter = new BuyerRatingAdapter(getData(), Fragment_Buyer_Rating.this);
            scrollView.setAdapter(adapter);
            flag1=false;flag2=false;flag3=false;flag4=false;
        }else{
            //edit mode
            requestPastRating(getActivity());
            flag1=true;flag2=true;flag3=true;flag4=true;
            indicator0.setBackgroundResource( R.color.color_primary);
            indicator1.setBackgroundResource( R.color.color_primary);
            indicator2.setBackgroundResource( R.color.color_primary);
            indicator3.setBackgroundResource( R.color.color_primary);
            frame.setVisibility(View.VISIBLE);
        }

        if(getArguments()!=null){
            if(getArguments().getString("buyer_company_id")!=null){
                company_id= getArguments().getString("buyer_company_id");
            }
        }

    }

    public void initListener() {
        scrollView.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
            @Override
            public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
                if (viewHolder != null) {
                    viewHolder.itemView.setAlpha(1);
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getArguments().getInt("mode")==Constants.CREDT_RATING_ADD_MODE) {
                    if (flag1 && flag2 && flag3 && flag4) {
                        sendBuyerRating(null,HttpManager.METHOD.POSTWITHPROGRESS);
                    }
                }else{
                    sendBuyerRating(getArguments().getString("id"),HttpManager.METHOD.PUTWITHPROGRESS);
                }
            }
        });
    }

    public void sendBuyerRating(String id,HttpManager.METHOD method){
        String url="";
        if(id==null) {
            url = URLConstants.companyUrl(getActivity(), "add-credit-reference", "");
        }else{
            url= URLConstants.companyUrl(getActivity(), "add-credit-reference", "")+id+"/";
        }
        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        params.put("buying_company", company_id);
        params.put("selling_company", UserInfo.getInstance(getActivity()).getCompany_id());
        params.put("selling_company_name", UserInfo.getInstance(getActivity()).getCompanyname());
        params.put("transaction_on_credit","true");
        if(adapter!=null ){
            params.put("duration_of_work",fetchOptions(adapter.getans(0),1));
            params.put("transaction_value",fetchOptions(adapter.getans(1),2));
            params.put("average_payment_duration",fetchOptions(adapter.getans(2),3));
            params.put("average_gr_rate",fetchOptions(adapter.getans(3),4));
        }
        HttpManager.getInstance(getActivity()).request(method, url, params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    Toast.makeText(getActivity(), "Buyer rating given", Toast.LENGTH_LONG).show();
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    public void onItemClicked(int i){

        if(i<3) {
            scrollView.smoothScrollToPosition(i + 1);
            changeProgress(i);
        }
        if(i==3){
            changeProgress(i);
        }


    }


    void changeProgress(int i){
        switch (i){
            case 0:indicator0.setBackgroundResource( R.color.color_primary);
                flag1=true;
                break;
            case 1:indicator1.setBackgroundResource( R.color.color_primary);
                flag2=true;
                break;
            case 2: indicator2.setBackgroundResource( R.color.color_primary);
                flag3=true;
                break;
            case 3:indicator3.setBackgroundResource( R.color.color_primary);
                flag4=true;
                break;

        }
        if(flag1 && flag2 && flag3 && flag4) {
            frame.setVisibility(View.VISIBLE);
        }
    }
    public List<BuyerQuestions> getData() {
        return Arrays.asList(
                new BuyerQuestions(getResources().getString(R.string.question1),getResources().getString(R.string.question1_title),
                        new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.question1_options))),0),
                new BuyerQuestions(getResources().getString(R.string.question2),getResources().getString(R.string.question2_title),
                        new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.question2_options))),0),
                new BuyerQuestions(getResources().getString(R.string.question3),getResources().getString(R.string.question3_title),
                        new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.question3_options))),0),
                new BuyerQuestions(getResources().getString(R.string.question4),getResources().getString(R.string.question4_title),
                        new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.question4_options))),0));
    }

    String fetchOptions(int op,int q){
        ArrayList<String> ans=null;
        String answer = "";
        if(q==1) {
            ans = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.question1_options)));
        }else if(q==2){
            ans = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.question2_options)));
        }else if(q==3){
            ans = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.question3_options)));
        }else if(q==4){
            ans = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.question4_options)));
        }
        try {
            answer = "" + ans.get(op - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return answer;
    }


    public void requestPastRating(final Context context) {
        showProgress();
        String url= URLConstants.companyUrl(context, "add-credit-reference", "")+"/"+getArguments().getString("id");
        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, url, params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    hideProgress();
                    if (isAdded() && !isDetached()) {
                        ResponseCompanyRating responseCompanyRating = Application_Singleton.gson.fromJson(response,ResponseCompanyRating.class);
                        company_id=responseCompanyRating.getBuying_company();
                        if(responseCompanyRating !=null){
                            ((OpenContainer) context).toolbar.setTitle(responseCompanyRating.getBuying_company_name());
                            adapter=new BuyerRatingAdapter(getPastData(responseCompanyRating),Fragment_Buyer_Rating.this);
                            scrollView.setAdapter(adapter);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                StaticFunctions.showResponseFailedDialog(error);

                Log.d("ERRROR", error.toString());
            }
        });
    }

    public List<BuyerQuestions> getPastData(ResponseCompanyRating responseCompanyRating){
        ArrayList<String> a1 = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.question1_options)));
        ArrayList<String> a2 = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.question2_options)));
        ArrayList<String> a3 = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.question3_options)));
        ArrayList<String> a4 = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.question4_options)));
        int ans1=0,ans2=0,ans3=0,ans4=0;
        for(int i=0;i<a1.size();i++){
            if(a1.get(i).equals(responseCompanyRating.getDuration_of_work())){
                ans1=i+1;
            }
            if(a2.get(i).equals(responseCompanyRating.getTransaction_value())){
                ans2=i+1;
            }
            if(a3.get(i).equals(responseCompanyRating.getAverage_payment_duration())){
                ans3=i+1;
            }
            if(a4.get(i).equals(responseCompanyRating.getAverage_gr_rate())){
                ans4=i+1;
            }
        }
        if (ans1 == 0) {
            indicator0.setBackgroundResource(R.color.purchase_light_gray);
            flag1 = false;
        }
        if (ans2 == 0) {
            indicator1.setBackgroundResource(R.color.purchase_light_gray);
            flag2 = false;
        }
        if (ans3 == 0) {
            indicator2.setBackgroundResource(R.color.purchase_light_gray);
            flag3 = false;
        }
        if (ans4 == 0) {
            indicator3.setBackgroundResource(R.color.purchase_light_gray);
            flag4 = false;
        }
        return Arrays.asList(
                new BuyerQuestions(getResources().getString(R.string.question1),getResources().getString(R.string.question1_title),
                        a1,ans1),
                new BuyerQuestions(getResources().getString(R.string.question2),getResources().getString(R.string.question2_title),
                        a2,ans2),
                new BuyerQuestions(getResources().getString(R.string.question3),getResources().getString(R.string.question3_title),
                        a3,ans3),
                new BuyerQuestions(getResources().getString(R.string.question4),getResources().getString(R.string.question4_title),
                        a4,ans4));
    }


}
