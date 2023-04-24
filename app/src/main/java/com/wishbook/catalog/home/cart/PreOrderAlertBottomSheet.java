package com.wishbook.catalog.home.cart;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.CartCatalogModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class PreOrderAlertBottomSheet extends BottomSheetDialogFragment {

    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.btn_done)
    Button btn_done;


    @BindView(R.id.linear_catalog)
    LinearLayout linear_catalog;

    CartCatalogModel cartCatalogModel;

    private Context context;

    PreOrderDoneListener preOrderDoneListener;


    @SuppressLint("ValidFragment")
    PreOrderAlertBottomSheet(CartCatalogModel cartCatalogModel) {
        this.cartCatalogModel = cartCatalogModel;

    }

    public static PreOrderAlertBottomSheet getInstance(CartCatalogModel catalogsModel) {
        return new PreOrderAlertBottomSheet(catalogsModel);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.pre_order_bootom_dialog, container, false);
        ButterKnife.bind(this, view);
        Application_Singleton singleton = new Application_Singleton();
        singleton.trackScreenView(getClass().getSimpleName(), getActivity());
        context = getActivity();
        initView();
        img_close = view.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(preOrderDoneListener!=null){
                    dismiss();
                    preOrderDoneListener.onDone();
                }
            }
        });
        initView();
        return view;
    }


    public void initView() {
        addCatalogList(cartCatalogModel.getCatalogs(), linear_catalog);
    }

    private void addCatalogList(ArrayList<CartCatalogModel.Catalogs> catalogs, LinearLayout root) {
        LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root.removeAllViews();
        for (int i = 0; i < catalogs.size(); i++) {
            if (!catalogs.get(i).isReady_to_ship()) {
                View v = vi.inflate(R.layout.catalog_availability_row, null);
                TextView catalogName = (TextView) v.findViewById(R.id.catalog_name);
                TextView catalogValue = (TextView) v.findViewById(R.id.catalog_dispatch);
                catalogName.setText((i+1) + ". " + catalogs.get(i).getCatalog_title());
                try{
                    String converted_date = DateUtils.changeDateFormat(StaticFunctions.SERVER_POST_FORMAT, StaticFunctions.CLIENT_DISPLAY_FORMAT1, catalogs.get(i).getDispatch_date());
                    catalogValue.setText(converted_date);
                } catch (Exception e){
                    catalogValue.setText(catalogs.get(i).getDispatch_date());
                    e.printStackTrace();
                }

                root.addView(v);
            }

        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();

        if (dialog != null) {
            View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        final View view = getView();
        view.post(new Runnable() {
            @Override
            public void run() {
                View parent = (View) view.getParent();
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
                CoordinatorLayout.Behavior behavior = params.getBehavior();
                final BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
                WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                bottomSheetBehavior.setPeekHeight(StaticFunctions.dpToPx(getActivity(), 500));
                bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            dismiss();
                        } else {
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });
                parent.setBackgroundColor(Color.TRANSPARENT);
            }
        });

    }


    public interface PreOrderDoneListener {
        void onDone();
    }

    public void setPreOrderDoneListener(PreOrderAlertBottomSheet.PreOrderDoneListener preOrderDoneListener) {
        this.preOrderDoneListener = preOrderDoneListener;
    }
}
