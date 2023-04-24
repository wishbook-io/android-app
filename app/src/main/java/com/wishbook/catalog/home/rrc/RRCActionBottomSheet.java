package com.wishbook.catalog.home.rrc;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wishbook.catalog.R;

import butterknife.BindView;
import butterknife.ButterKnife;


@SuppressLint("ValidFragment")
public class RRCActionBottomSheet extends BottomSheetDialogFragment {


    View view;

    @BindView(R.id.relative_action_cancel)
    RelativeLayout relative_action_cancel;

    @BindView(R.id.relative_action_edit)
    RelativeLayout relative_action_edit;


    DismissListener dismissListener;


    public static RRCActionBottomSheet newInstance(Bundle bundle) {
        RRCActionBottomSheet bottomShareDialog = new RRCActionBottomSheet();
        if (bundle != null)
            bottomShareDialog.setArguments(bundle);
        return bottomShareDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.rrc_action_bottomsheet, container, false);
        ButterKnife.bind(this, view);
        initListener();
        return view;
    }



    public void initListener() {
        relative_action_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dismissListener!=null) {
                    dismissListener.onDismiss("UPDATE");
                    dismiss();
                }
            }
        });

        relative_action_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getActivity())
                        .title("Cancel request")
                        .content("Are you sure you want to cancel request?")
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                                dismiss();
                                if(dismissListener!=null) {
                                    dismissListener.onDismiss("CANCEL");

                                }

                            }
                        })
                        .negativeText("Cancel")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    public interface DismissListener {
        void onDismiss(String type);
    }


    public void setDismissListener(DismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }





}