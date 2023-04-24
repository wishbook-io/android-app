package com.wishbook.catalog.home.more;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_WishbookBankDetail extends GATrackedFragment {

    private View view;

    @BindView(R.id.txt_share)
    TextView txt_share;

    @BindView(R.id.txt_copy)
    TextView txt_copy;

    public Fragment_WishbookBankDetail() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_wishbook_bank_detail, ga_container, true);
        ButterKnife.bind(this, view);
        initListener();
        return view;
    }


    public void initListener() {
        txt_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(getResources().getString(R.string.payment_neft_details));
                    Toast.makeText(getActivity(),"copied to clipboard",Toast.LENGTH_SHORT).show();
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Wishbook Payment Detail", getResources().getString(R.string.payment_neft_details));
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getActivity(),"copied to clipboard",Toast.LENGTH_SHORT).show();
                }

            }
        });

        txt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareWishBookBankDetail();
            }
        });

    }

    public void shareWishBookBankDetail() {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Wishbook Payment Detail");
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.payment_neft_details));
            intent.setType("text/plain");
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }

    }
}
