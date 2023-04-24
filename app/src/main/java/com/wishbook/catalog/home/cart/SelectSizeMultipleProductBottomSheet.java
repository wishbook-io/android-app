package com.wishbook.catalog.home.cart;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.CartProductModel;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.adapters.MultipleSizeSelectionAdapter;
import com.wishbook.catalog.home.models.ProductObj;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectSizeMultipleProductBottomSheet extends BottomSheetDialogFragment {

    Context mContext;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    @BindView(R.id.btn_add_to_cart)
    TextView btn_add_to_cart;

    @BindView(R.id.img_close)
    ImageView img_close;

    ArrayList<Response_catalogMini> response_catalogMiniArrayList;

    MultipleSizeSelectionAdapter adapter;

    MultipleSizeAddToCartListener multipleSizeAddToCartListener;



    public static SelectSizeMultipleProductBottomSheet newInstance(Bundle bundle) {
        SelectSizeMultipleProductBottomSheet f = new SelectSizeMultipleProductBottomSheet();
        if (bundle != null)
            f.setArguments(bundle);
        return f;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.select_size_multiple_product_bottom_sheet, container, false);
        ButterKnife.bind(this, view);
        Application_Singleton singleton = new Application_Singleton();
        singleton.trackScreenView(getClass().getSimpleName(), getActivity());
        mContext = getActivity();
        initView();
        initRecyclerView();
        return view;
    }

    public void initView() {
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void onStart() {

        try {
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
                    final CoordinatorLayout.Behavior behavior = params.getBehavior();
                    final BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;

                    WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    bottomSheetBehavior.setPeekHeight(display.getHeight());
                    parent.setBackgroundColor(Color.TRANSPARENT);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initRecyclerView() {
        if (getArguments().getSerializable("data") != null) {
            response_catalogMiniArrayList = (ArrayList<Response_catalogMini>) getArguments().getSerializable("data");
            recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerview.setNestedScrollingEnabled(false);
            adapter = new MultipleSizeSelectionAdapter(mContext, response_catalogMiniArrayList);
            recyclerview.setAdapter(adapter);
        }
    }


    @OnClick(R.id.btn_add_to_cart)
    public void addToCart() {
        if(adapter!=null && adapter.validateSizeSelected()) {
            CartHandler cartHandler = new CartHandler((AppCompatActivity) mContext);
            ArrayList<ProductObj> productObjArrayList = new ArrayList<>();
            for (int i = 0; i < response_catalogMiniArrayList.size(); i++) {
                for (int j = 0;j<response_catalogMiniArrayList.get(i).getAvailable_sizes_array().size();j++) {
                    if(!response_catalogMiniArrayList.get(i).getAvailable_sizes_array().get(j).isEmpty()) {
                        ProductObj p_temp = new ProductObj(response_catalogMiniArrayList.get(i).getId(),
                                response_catalogMiniArrayList.get(i).getSingle_piece_price(), "Size : " +response_catalogMiniArrayList.get(i).getAvailable_sizes_array().get(j));
                        p_temp.setCatalog_id(response_catalogMiniArrayList.get(i).getCatalog_id());
                        productObjArrayList.add(p_temp);
                    }
                }
            }
            cartHandler.addMultipleProductToCartSinglePrice(productObjArrayList);
            cartHandler.setAddToCartCallbackListener(new CartHandler.AddToCartCallbackListener() {
                @Override
                public void onSuccess(CartProductModel response) {
                    if(multipleSizeAddToCartListener!=null) {
                        multipleSizeAddToCartListener.onSuccess(response);
                    }
                }

                @Override
                public void onFailure() {
                    if(multipleSizeAddToCartListener!=null) {
                        multipleSizeAddToCartListener.onFailure();
                    }
                }
            });
            dismiss();
        }
    }

    public interface MultipleSizeAddToCartListener {
        void onSuccess (CartProductModel response);

        void onFailure() ;
    }



    public void setMultipleSizeAddToCartListener(MultipleSizeAddToCartListener multipleSizeAddToCartListener) {
        this.multipleSizeAddToCartListener = multipleSizeAddToCartListener;
    }
}
