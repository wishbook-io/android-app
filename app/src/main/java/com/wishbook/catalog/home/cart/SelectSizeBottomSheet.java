package com.wishbook.catalog.home.cart;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.commonadapters.SizeSelectAdapter;
import com.wishbook.catalog.commonmodels.responses.Eavdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


@SuppressLint("ValidFragment")
public class SelectSizeBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    Eavdata data;
    String sizes;
    DismissListener dismissListener;
    CartHandler activity;
    String note;
    public String range, piece;
    SizeSelectAdapter sizeSelectAdapter;
    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.btn_done)
    AppCompatButton btn_done;

    @BindView(R.id.txt_title)
    TextView txt_title;

    @BindView(R.id.recycler_size)
    RecyclerView mRecyclerView;

    @BindView(R.id.txt_sub_title)
    TextView txt_sub_title;
    String type;

    private Context context;

    public SelectSizeBottomSheet() {
    }

    SelectSizeBottomSheet(String sizes, String range, String piece, String type) {
        this.sizes = sizes;
        this.range = range;
        this.piece = piece;
        this.type = type;
    }

    SelectSizeBottomSheet(Eavdata eav, String data) {
        this.data = eav;
        this.note = data;
    }

    public static SelectSizeBottomSheet getInstance(String sizes, String range, String piece, String type) {
        return new SelectSizeBottomSheet(sizes, range, piece, type);
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_add_cart_size_select, container, false);
        ButterKnife.bind(this,view);
        Application_Singleton singleton = new Application_Singleton();
        singleton.trackScreenView(getClass().getSimpleName(), getActivity());
        context = getActivity();
        initView();
        img_close = view.findViewById(R.id.img_close);
        btn_done.setOnClickListener(this);
        img_close.setOnClickListener(this);


        try {
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            view.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {

                            try {
                                dismissListener.onDismiss(new HashMap<String, Integer>());
                                dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    }
                    return false;

                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return view;
    }


    public void initView() {

        if(type!=null) {
            if (type.contains("product")) {
                txt_sub_title.setText("Single design only, \u20B9 " + range + "/Pc.");
            } else {

                txt_sub_title.setText("1 set = " + piece + " Pcs. , \u20B9 " + range + "/Pc.");
            }
        } else {
            txt_sub_title.setText("1 set = " + piece + " Pcs. , \u20B9 " + range + "/Pc.");
        }


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        if(sizes!=null) {
            ArrayList<String> tempSize = new ArrayList<>(Arrays.asList(sizes.split(",")));
            sizeSelectAdapter = new SizeSelectAdapter(context, tempSize, this, type);
            mRecyclerView.setAdapter(sizeSelectAdapter);
        }
    }

    @Override
    public void onStart() {
        final WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
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

                    Display display = wm.getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    bottomSheetBehavior.setPeekHeight(display.getHeight());

                    bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                        @Override
                        public void onStateChanged(@NonNull View bottomSheet, int newState) {
                            if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                                if(dismissListener!=null) {
                                    dismissListener.onDismiss(new HashMap<String, Integer>());
                                }
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


            if (note != null && note.length() > 1) {

                note = note.substring(note.indexOf(":") + 1).trim();
          /*  for (int i = 0; i < radioButtons.size(); i++) {
                if (note.equalsIgnoreCase(radioButtons.get(i).getText().toString())) {
                    radioButtons.get(i).performClick();
                }
            }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeTitle(int qty) {

        if (type.contains("product")) {
            txt_title.setText("Select Size & quantity (" + qty + " Pcs.)");
        } else {
            txt_title.setText("Select Size & quantity (" + qty + " sets)");
        }
    }



    @Override
    public void onClick(View v) {

        try {

            if (v.getId() == R.id.btn_done) {
                try {
                    boolean flag = false;
                    if (dismissListener != null) {
                        String sizeSelected = "";
                        List<String> l = new ArrayList<>(sizeSelectAdapter.getData().keySet());
                        for (int i = 0; i < sizeSelectAdapter.getData().size(); i++) {
                            if (sizeSelectAdapter.getData().get(l.get(i)) > 0) {
                                flag = true;
                            }
                        }
                        Log.d("SIZESELECTED", sizeSelected);
                        if (flag) {
                            if(dismissListener!=null) {
                                dismissListener.onDismiss(sizeSelectAdapter.getData());
                            }
                            getDialog().dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Select any size", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (v == img_close) {
                try {
                    if(dismissListener!=null) {
                        dismissListener.onDismiss(new HashMap<String, Integer>());
                    }
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setDismissListener(SelectSizeBottomSheet.DismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }


    public interface DismissListener {
        void onDismiss(HashMap<String, Integer> type);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commit();
        } catch (IllegalStateException e) {
            Log.d("CustomBottomSheet", "Exception", e);
        }
    }
}

