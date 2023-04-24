package com.wishbook.catalog.home.contacts.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchSeller;
import com.wishbook.catalog.commonmodels.postpatchmodels.RejectSeller;
import com.wishbook.catalog.commonmodels.responses.Response_SellerFull;
import com.wishbook.catalog.home.orders.add.Fragment_CreatePurchaseOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fragment_SupplierDetails extends GATrackedFragment {

    private View v;
    private TabLayout tabLayout;
    private RelativeLayout editcontainer;
    private Button updatepricebut;
    private Button but_cancel;
    private Button btn_reject;
    private Button btn_approve;
    private ViewPager viewPager;
    private Button but_save,but_create_order;
    private SimpleDraweeView profpic;
    private EditText edit_fix_amount;
    private EditText edit_percentage_amount;
    private LinearLayout cont_createorder;
    private LinearLayout cont_pricerule;
    private LinearLayout tabscont;
    private String supplierID;
    private RadioGroup radiogroupprice;

    public Fragment_SupplierDetails() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.detailssupplier, ga_container, true);
        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        profpic = (SimpleDraweeView) v.findViewById(R.id.profpic);

        cont_pricerule = (LinearLayout) v.findViewById(R.id.cont_pricerule);
        cont_createorder = (LinearLayout) v.findViewById(R.id.cont_createorder);
        tabscont = (LinearLayout) v.findViewById(R.id.tabscont);
        cont_pricerule.setVisibility(View.GONE);
        cont_createorder.setVisibility(View.GONE);
        tabscont.setVisibility(View.GONE);

        updatepricebut = (Button) v.findViewById(R.id.updatepricebut);
        editcontainer = (RelativeLayout) v.findViewById(R.id.pricecontainer);
        edit_fix_amount = (EditText) v.findViewById(R.id.input_price);
        edit_percentage_amount = (EditText) v.findViewById(R.id.input_percentage);
        but_cancel = (Button) v.findViewById(R.id.but_cancel);
        but_save = (Button) v.findViewById(R.id.but_save);
        radiogroupprice=(RadioGroup)v.findViewById(R.id.radiogroupprice);


        btn_approve = (Button) v.findViewById(R.id.btn_approve);

        btn_reject = (Button) v.findViewById(R.id.btn_reject);

        but_create_order = (Button) v.findViewById(R.id.createorder);
        editcontainer.setVisibility(View.GONE);

        viewPager = (ViewPager) v.findViewById(R.id.view_pager);
        if (viewPager != null) {
            setupViewPager(viewPager, null);
        }
        updatepricebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editcontainer.getVisibility() == View.VISIBLE) {
                    editcontainer.setVisibility(View.GONE);
                } else {
                    editcontainer.setVisibility(View.VISIBLE);
                }

            }
        });
        but_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editcontainer.setVisibility(View.GONE);
            }
        });
        if (getArguments() != null) {
            if (!getArguments().getString("sellerid", "").equals("")) {
                supplierID = getArguments().getString("sellerid", "");
                getSeller(getArguments().getString("sellerid", ""));
            }
        }

        but_create_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*getActivity().getSupportFragmentManager().beginTransaction().replace(R.la)*/

                Application_Singleton.CONTAINER_TITLE = getResources().getString(R.string.new_purchase_order);
                Fragment_CreatePurchaseOrder purchase = new Fragment_CreatePurchaseOrder();
                Bundle bundle = new Bundle();
                bundle.putString("sellerid",supplierID);
                purchase.setArguments(bundle);
                Application_Singleton.CONTAINERFRAG = purchase;
                Intent intent = new Intent(getActivity(), OpenContainer.class);
                startActivity(intent);
            }
        });


        radiogroupprice.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if(checkedId != R.id.check_add){
                            edit_fix_amount.setEnabled(true);
                            edit_fix_amount.requestFocus();
                            edit_fix_amount.setSelection(1);
                            edit_percentage_amount.setEnabled(false);
                            edit_percentage_amount.setText("0");
                            edit_percentage_amount.setError(null);
                            InputMethodManager imm = (InputMethodManager)   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(edit_fix_amount, InputMethodManager.SHOW_IMPLICIT);


                        }
                        else {

                            edit_fix_amount.setEnabled(false);
                            edit_fix_amount.setText("0");
                            edit_fix_amount.setError(null);
                            edit_percentage_amount.setEnabled(true);
                            edit_percentage_amount.requestFocus();
                            edit_percentage_amount.setSelection(1);


                            InputMethodManager imm = (InputMethodManager)   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(edit_percentage_amount, InputMethodManager.SHOW_IMPLICIT);

                        }


                    }
                }
        );
        radiogroupprice.check(R.id.check_add);







        // getSeller(getArguments().getString("sellerid",""));
        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                RejectSeller rejectBuyer = new RejectSeller(getArguments().getString("sellerid",""), "rejected");
                Gson gson = new Gson();
                HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(),"sellers","") + getArguments().getString("sellerid","") + '/', gson.fromJson(gson.toJson(rejectBuyer), JsonObject.class), headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                        // progressDialog.dismiss();
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        StaticFunctions.showResponseFailedDialog(error);
                    }
                });
            }
        });
        btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                RejectSeller rejectBuyer = new RejectSeller(getArguments().getString("sellerid",""), "approved");
                Gson gson = new Gson();
                HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(),"sellers","") + getArguments().getString("sellerid","") + '/', gson.fromJson(gson.toJson(rejectBuyer), JsonObject.class), headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                        // progressDialog.dismiss();
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        new MaterialDialog.Builder(getActivity())
                                .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                                .content(error.getErrormessage())
                                .positiveText("OK")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        //  progressDialog.dismiss();
                    }
                });
            }
        });

        but_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                Gson gson = new Gson();
                PatchSeller patchSeller = new PatchSeller();
                patchSeller.setId(getArguments().getString("sellerid",""));
/*                patchSeller.setFix_amount(edit_fix_amount.getText().toString());
                patchSeller.setPercentage_amount(edit_percentage_amount.getText().toString());*/


                if (radiogroupprice.getCheckedRadioButtonId() != R.id.check_add) {
                    Log.i("TAG", "onClick: Fixed Rate");
                    patchSeller.setFix_amount(edit_fix_amount.getText().toString());
                    patchSeller.setPercentage_amount("0");
                }
                else {
                    Log.i("TAG", "onClick: percentage Rate");
                    patchSeller.setFix_amount("0");
                    patchSeller.setPercentage_amount(edit_percentage_amount.getText().toString());

                }


                HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(),"sellers","") + getArguments().getString("sellerid","") + '/', gson.fromJson(gson.toJson(patchSeller), JsonObject.class), headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        editcontainer.setVisibility(View.GONE);
                        // progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Supplier details updated", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        StaticFunctions.showResponseFailedDialog(error);
                    }
                });
            }

        });
//        ((TextView) v.findViewById(R.id.det_name)).setText(Application_Singleton.selectedSupplier.getSelling_company().getName());
//        ((TextView) v.findViewById(R.id.det_email)).setText("Email: " + Application_Singleton.selectedSupplier.getSelling_company().getEmail());
//        ((TextView) v.findViewById(R.id.det_num)).setText("Phone: " + Application_Singleton.selectedSupplier.getSelling_company().getPhone_number());
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);


        return v;
    }

    private void getSeller(String id) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(),"sellers","") + id + "/?expand=true", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                Log.v("sync response", response);
                final Response_SellerFull response_seller = new Gson().fromJson(response, Response_SellerFull.class);
                if (viewPager != null) {
                    setupViewPager(viewPager, response_seller);
                    tabLayout.setTabMode(TabLayout.MODE_FIXED);
                    tabLayout.setupWithViewPager(viewPager);

                }
                ((TextView) v.findViewById(R.id.det_name)).setText(response_seller.getSelling_company().getName());
                ((TextView) v.findViewById(R.id.det_email)).setText(response_seller.getSelling_company().getEmail());
                ((TextView) v.findViewById(R.id.det_num)).setText(response_seller.getSelling_company().getPhone_number());
                edit_fix_amount.setText(response_seller.getFix_amount());
                edit_percentage_amount.setText(response_seller.getPercentage_amount());
                if (response_seller.getSelling_company().getThumbnail() != null && !response_seller.getSelling_company().getThumbnail().equals("")) {
                    //StaticFunctions.loadImage(getActivity(),response_seller.getSelling_company().getThumbnail(),profpic,R.drawable.uploadempty);
                    StaticFunctions.loadFresco(getActivity(),response_seller.getSelling_company().getThumbnail(),profpic);
                   // Picasso.with(getActivity()).load(response_seller.getSelling_company().getThumbnail()).into(profpic);
                }

                if (response_seller.getStatus().equals("rejected")) {
                    btn_reject.setVisibility(View.GONE);
                    btn_approve.setVisibility(View.VISIBLE);
                    cont_pricerule.setVisibility(View.GONE);
                    cont_createorder.setVisibility(View.GONE);
                    tabscont.setVisibility(View.GONE);
                } else {
                    cont_pricerule.setVisibility(View.VISIBLE);
                    cont_createorder.setVisibility(View.VISIBLE);
                    btn_reject.setVisibility(View.VISIBLE);
                    btn_approve.setVisibility(View.GONE);
                    tabscont.setVisibility(View.VISIBLE);

                }
                v.findViewById(R.id.det_num).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + response_seller.getSelling_company().getPhone_number()));
                        getActivity().startActivity(intent);
                    }
                });
                v.findViewById(R.id.det_email).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + response_seller.getSelling_company().getEmail()));
                        getActivity().startActivity(intent);
                    }
                });
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                Log.v("error response", error.getErrormessage());
            }
        });
    }

    private void setupViewPager(ViewPager viewPager, final Response_SellerFull response_sellerFull) {

        Adapter adapter = new Adapter(getChildFragmentManager());
        Fragment_Details_Orders bDet_orders = new Fragment_Details_Orders();
        if (response_sellerFull != null && response_sellerFull.getSelling_company() != null && response_sellerFull.getSelling_company().getSelling_order() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("orders", new Gson().toJson(response_sellerFull.getBuying_company().getSelling_order()));
            bundle.putString("type", "supplier");
            bDet_orders.setArguments(bundle);
        }
        adapter.addFragment(bDet_orders, "Orders");
        Fragment_Details_Address bDet_address = new Fragment_Details_Address();
        if (response_sellerFull != null && response_sellerFull.getSelling_company() != null) {
            Bundle bundle1 = new Bundle();
            bundle1.putString("company", new Gson().toJson(response_sellerFull.getSelling_company()));
            bDet_address.setArguments(bundle1);
        }
        adapter.addFragment(bDet_address, "Address");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentStatePagerAdapter {
        private final List<GATrackedFragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(GATrackedFragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public GATrackedFragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

}
