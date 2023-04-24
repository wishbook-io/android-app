package com.wishbook.catalog.home.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.home.notifications.adapters.NotificationsAdapter;
import com.wishbook.catalog.home.notifications.models.NotificationModel;
import com.wishbook.catalog.services.LocalService;

import java.util.ArrayList;
import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import rx.Subscriber;

public class Fragment_Notifications extends GATrackedFragment {

    private View v;
    private Toolbar toolbar;
    private RecyclerViewEmptySupport recyclerView;
    private ArrayList<NotificationModel> data;
    private NotificationsAdapter notificationsAdapter;
    private AppCompatButton but_delall;
    private Subscriber<Integer> subscriber;

    public Fragment_Notifications() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }



    public void getData() {
        if (PrefDatabaseUtils.getGCMNotificationData(getActivity()) != null) {
            data = new Gson().fromJson(PrefDatabaseUtils.getGCMNotificationData(getActivity()), new TypeToken<ArrayList<NotificationModel>>() {
            }.getType());

            for (int i=0;i<data.size();i++){
                data.get(i).setRead(true);
            }


            if(data.size() > Constants.STORE_NOTIFICATION){
                int removeLength = data.size() - Constants.STORE_NOTIFICATION;
                for(int i=0;i<removeLength;i++){
                    try {
                        data.remove(0);
                        PrefDatabaseUtils.setGCMNotificationData(getActivity(),new Gson().toJson(data));
                    }
                    catch (Exception e) {
                        Log.e("RealmDelete", e.getMessage());
                    }
                }
            }
            PrefDatabaseUtils.setGCMNotificationData(getActivity(),new Gson().toJson(data));
        } else {
            data = new ArrayList<>();
        }
        if (data != null) {
            LocalService.notificationCounter.onNext(data.size());
        }

        Collections.reverse(data);
        notificationsAdapter = new NotificationsAdapter((AppCompatActivity) getActivity(), data);
        recyclerView.setAdapter(notificationsAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_notifications, container, false);
        but_delall = (AppCompatButton) v.findViewById(R.id.but_delall);
        but_delall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Realm.getDefaultInstance().delete(NotificationModel.class);

                    }
                });
                LocalService.notificationCounter.onNext(0);
                data = new ArrayList<>();
                RealmResults<NotificationModel> dataf = Realm.getDefaultInstance().where(NotificationModel.class).findAll();
                for (NotificationModel notificationModel : dataf) {
                    data.add(notificationModel);
                }
                Collections.reverse(data);
                notificationsAdapter = new NotificationsAdapter((AppCompatActivity) getActivity(), data);
                recyclerView.setAdapter(notificationsAdapter);*/

            }
        });
        recyclerView = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setEmptyView(v.findViewById(R.id.list_empty1));

        return v;
    }

}
