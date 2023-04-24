package com.wishbook.catalog.commonadapters;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.ResponseCodes;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.autocomplete.CustomAutoCompleteTextView;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_grouptypes;
import com.wishbook.catalog.commonmodels.postpatchmodels.ApprovedPatchBuyerStatus;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchBuyerStatus;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;
import com.wishbook.catalog.commonmodels.responses.Response_BuyerGroupType;
import com.wishbook.catalog.home.contacts.Fragment_BuyersEnquiry;
import com.wishbook.catalog.home.contacts.details.Fragment_BuyerEnquiryDetails;
import com.wishbook.catalog.home.models.BuyersList;
import com.wishbook.catalog.home.orderNew.add.Fragment_CreateSaleOrder_Version2;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by prane on 21-03-2016.
 */
public class EnquiryBuyerAdapter extends RecyclerView.Adapter<EnquiryBuyerAdapter.MyViewHolder> {

    private AppCompatActivity mActivity;
    private ArrayList<Response_Buyer> mApprovedBuyersList;
    private CustomAutoCompleteTextView buyer_select;
    AutoCompleteCommonAdapter adapter;
    private BuyersList buyer = null;
    Response_BuyerGroupType[] responseBuyerGroupTypes;
    private Boolean showTutorial = true;
    MaterialDialog transferDialog;

    private ArrayList<BuyersList> buyerslist = new ArrayList<>();

    Fragment fragment;

    public EnquiryBuyerAdapter(AppCompatActivity mActivity, ArrayList<Response_Buyer> mApprovedBuyersList, Response_BuyerGroupType[] responseBuyerGroupTypes, Fragment fragment_buyersEnquiry) {
        this.mActivity = mActivity;
        this.mApprovedBuyersList = mApprovedBuyersList;
        this.responseBuyerGroupTypes = responseBuyerGroupTypes;
        fragment = fragment_buyersEnquiry;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout itemcontainer;
        public TextView buyerName, buyerNumber,buyerStatus;
        AppCompatButton btn_reject, btn_approve;
        private AppCompatButton chat_user,create_order;
        private ImageView menu;

        public MyViewHolder(View view) {
            super(view);
            buyerName = (TextView) view.findViewById(R.id.buyer_name);
            //  transfer_button = (AppCompatButton) view.findViewById(R.id.transfer_button);
            //ask_references = (AppCompatButton) view.findViewById(R.id.ask_references);
            btn_reject = (AppCompatButton) view.findViewById(R.id.reject);
            btn_approve = (AppCompatButton) view.findViewById(R.id.approve);
            buyerNumber = (TextView) view.findViewById(R.id.buyer_number);
            buyerStatus = (TextView) view.findViewById(R.id.buyer_status);
            chat_user = (AppCompatButton) view.findViewById(R.id.chat_user);
            create_order = (AppCompatButton) view.findViewById(R.id.create_order);
            itemcontainer = (LinearLayout) view.findViewById(R.id.itemcontainer);
            menu = (ImageView) view.findViewById(R.id.menu);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.enquiry_buyers_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Response_Buyer bg = mApprovedBuyersList.get(position);
        if (bg.getBuying_company() != null) {
            final String companyName = bg.getBuying_company_name();
            final String buyingPersonName = bg.getBuying_person_name();
            final String phoneNumber = bg.getBuying_company_phone_number();
            final String companyType = bg.getGroup_type_name();



            // Changes According to Design Revision
            /*holder.chat_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, ConversationActivity.class);
                    intent.putExtra(ConversationUIService.USER_ID, bg.getBuying_company_chat_user());
                    intent.putExtra(ConversationUIService.DISPLAY_NAME, companyName); //put it for displaying the title.
                    mActivity.startActivity(intent);
                }
            });*/

            holder.itemcontainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Application_Singleton.CONTAINER_TITLE = "Buyer Enquiry";
                    Fragment_BuyerEnquiryDetails fragment_buyerEnquiryDetails = new Fragment_BuyerEnquiryDetails();
                    Bundle bundle = new Bundle();
                    bundle.putString("buyerid", bg.getId());
                    fragment_buyerEnquiryDetails.setArguments(bundle);
                    Application_Singleton.CONTAINERFRAG = fragment_buyerEnquiryDetails;
                    Intent intent = new Intent(mActivity,OpenContainer.class);
                    fragment.startActivityForResult(intent, ResponseCodes.BuyerEnquiry);
                    //todo buyer detail
                }
            });



            //change for bug #WB-850
            //holder.buyerName.setText(StringUtils.capitalize(companyName.toLowerCase().trim()));
            if(companyName != null){
                holder.buyerName.setText(StringUtils.capitalize(companyName.toLowerCase().trim()));
                holder.buyerName.setVisibility(View.VISIBLE);
            } else {
                holder.buyerName.setVisibility(View.GONE);
            }

           // holder.buyerNumber.setText(StringUtils.capitalize(phoneNumber.toLowerCase().trim()));

            holder.buyerNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Application_Singleton.CONTAINER_TITLE = "Buyer Enquiry";
                    Fragment_BuyerEnquiryDetails fragment_buyerEnquiryDetails = new Fragment_BuyerEnquiryDetails();
                    Bundle bundle = new    Bundle();
                    bundle.putString("buyerid", bg.getId());
                    fragment_buyerEnquiryDetails.setArguments(bundle);
                    Application_Singleton.CONTAINERFRAG = fragment_buyerEnquiryDetails;
                    StaticFunctions.switchActivity(mActivity, OpenContainer.class);
                    //todo buyer detail
                }
            });

            //Showing 3 dot menu for each item
            final PopupMenu popupMenu = new PopupMenu(mActivity, holder.menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.references:
                            HashMap<String, String> headers = StaticFunctions.getAuthHeader(mActivity);
                            PatchBuyerStatus rejectBuyer = new PatchBuyerStatus(bg.getId(), "Pending References");
                            Gson gson = new Gson();
                            HttpManager.getInstance(mActivity).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mActivity, "buyers", "") + bg.getId() + '/', gson.fromJson(gson.toJson(rejectBuyer), JsonObject.class), headers, new HttpManager.customCallBack() {
                                @Override
                                public void onCacheResponse(String response) {
                                }

                                @Override
                                public void onServerResponse(String response, boolean dataUpdated) {
                                    // progressDialog.dismiss();'
                                    bg.setStatus("Pending References");
                                    //holder.ask_references.setVisibility(View.GONE);
                                    popupMenu.getMenu().findItem(R.id.references).setVisible(false);
                                    notifyDataSetChanged();
                                    HttpManager.getInstance(mActivity).removeCacheParams(URLConstants.companyUrl(mActivity, "buyers_enquiry", "") + "&&limit=" + Fragment_BuyersEnquiry.LIMIT + "&&offset=" + 0 + "&&search=" + "", null);

                                }

                                @Override
                                public void onResponseFailed(ErrorString error) {
                                    StaticFunctions.showResponseFailedDialog(error);
                                }
                            });
                            break;
                        case R.id.transferred:
                            buyer = null;
                            transferDialog = new MaterialDialog.Builder(mActivity).title("Select Buyer").positiveText("Done").negativeText("Close").onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    buyer = null;
                                    dialog.dismiss();
                                }
                            }).customView(R.layout.buyer_select_view, true).onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (buyer != null) {
                                        PostIdToServer(buyer.getCompany_id(), bg.getId(), mActivity, popupMenu,holder,bg);
                                    } else {
                                        Toast.makeText(mActivity, "Please select buyer from suggested list", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).build();

                            TextInputLayout input_buyername = (TextInputLayout) transferDialog.getCustomView().findViewById(R.id.input_buyername);
                            TextView edit_buyername = (TextView) transferDialog.getCustomView().findViewById(R.id.edit_buyername);
                            LinearLayout buyer_container = (LinearLayout) transferDialog.getCustomView().findViewById(R.id.buyer_container);
                            buyer_container.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    KeyboardUtils.hideKeyboard(mActivity);
                                    fragment.startActivityForResult(new Intent(mActivity, Activity_BuyerSearch.class),Application_Singleton.BUYER_SEARCH_REQUEST_CODE);
                                }
                            });

                            /*buyer_select = (CustomAutoCompleteTextView) dialog.getCustomView().findViewById(R.id.buyer_select);

                            buyer_select.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    buyer = null;
                                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                }

                                @Override
                                public void afterTextChanged(Editable editable) {

                                }
                            });

                            buyer_select.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Log.v("onitemselected", "" + position);
                                    buyer = (BuyersList) parent.getItemAtPosition(position);
                                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                }
                            });*/

                            transferDialog.show();

                            /*adapter = new AutoCompleteCommonAdapter(mActivity, R.layout.spinneritem, buyerslist, "buyerlist");
                            buyer_select.setAdapter(adapter);
                            buyer_select.setThreshold(1);
                            buyer_select.setLoadingIndicator(
                                    (android.widget.ProgressBar) dialog.getCustomView().findViewById(R.id.progress_bar));*/

                            break;
                    }
                    return true;
                }
            });

            popupMenu.inflate(R.menu.menu_enquiry);
            holder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupMenu.show();
                }
            });


                    holder.create_order.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Fragment_CreateOrder createOrderFrag = new Fragment_CreateOrder();
                            //Fragment_CreateOrderNew createOrderFrag = new Fragment_CreateOrderNew();
                            Fragment_CreateSaleOrder_Version2 createOrderFrag = new Fragment_CreateSaleOrder_Version2();
                            Bundle bundle = new Bundle();
                            bundle.putString("buyerselected", bg.getBuying_company_name());
                            bundle.putString("buyer_selected_broker_id",bg.getBroker_company());
                            bundle.putString("buyer_selected_company_id",bg.getBuying_company());

                            BuyersList buyersList = new BuyersList(bg.getBuying_company(),bg.getBuying_company_name(),bg.getBroker_company());
                            bundle.putSerializable("buyer",buyersList);
                            createOrderFrag.setArguments(bundle);

                            Application_Singleton.CONTAINER_TITLE=mActivity.getResources().getString(R.string.new_sales_order);
                            Application_Singleton.CONTAINERFRAG=createOrderFrag;
                            //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                            StaticFunctions.switchActivity(mActivity, OpenContainer.class);
                        }
                    });



            //Setting Status

            //for Pending References
            if (bg.getStatus().equals("Pending References") || bg.getStatus().equals("References Filled")) {
                // holder.ask_references.setVisibility(View.GONE);
                popupMenu.getMenu().findItem(R.id.references).setVisible(false);

            } else {
                //  holder.ask_references.setVisibility(View.VISIBLE);
                popupMenu.getMenu().findItem(R.id.references).setVisible(true);
            }


            //for transferred
            if (bg.getStatus().equals("Transferred")) {
                //  holder.transfer_button.setVisibility(View.GONE);
                popupMenu.getMenu().findItem(R.id.transferred).setVisible(false);
            } else {
                if (!bg.getStatus().equals("Pending References") && !bg.getStatus().equals("References Filled")) {
                    //  holder.ask_references.setVisibility(View.VISIBLE);
                    popupMenu.getMenu().findItem(R.id.references).setVisible(true);
                }
            }


            //for approved
            if(bg.getStatus().toLowerCase().equals("approved")){
                hideViewsApproved(holder);
            }

            //for rejected
            if(bg.getStatus().toLowerCase().equals("rejected")){
                hideViewsRejected(holder);
            }

            String append ="Status: ";
            if (bg.getStatus().equals("Pending References")) {
                holder.buyerStatus.setText(append+bg.getStatus());
            } else if (bg.getStatus().equals("References Filled")) {
                holder.buyerStatus.setText(append+bg.getStatus());
            }else if(bg.getStatus().toLowerCase().equals("approved")){
                holder.buyerStatus.setText(append+"Approved");
            }
            else if(bg.getStatus().toLowerCase().equals("rejected")){
                holder.buyerStatus.setText(append+"Rejected");
            }
            else if(bg.getStatus().toLowerCase().equals("transferred")){
                holder.buyerStatus.setText(append+"Transferred");
            }
            else {
                holder.buyerStatus.setText(append+"Pending");
                showViews(holder);
                if(showTutorial){
                    showTutorial = false;
                    //StaticFunctions.checkAndShowTutorial(mActivity,"enquiry_received_approve_button_tutorial",holder.btn_approve,mActivity.getString(R.string.enquiry_received_approve_button_tutorial),"bottom");
                }
            }

            holder.btn_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(mActivity);
                    PatchBuyerStatus rejectBuyer = new PatchBuyerStatus(bg.getId(), "rejected");
                    Gson gson = new Gson();
                    HttpManager.getInstance(mActivity).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mActivity, "buyers", "") + bg.getId() + '/', gson.fromJson(gson.toJson(rejectBuyer), JsonObject.class), headers, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {

                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                          //  mApprovedBuyersList.remove(position);
                         //   notifyDataSetChanged();
                            hideViewsRejected(holder);
                            bg.setStatus("rejected");
                            notifyItemChanged(holder.getAdapterPosition());
                            // progressDialog.dismiss();
                        }

                        @Override
                        public void onResponseFailed(ErrorString error) {
                         StaticFunctions.showResponseFailedDialog(error);
                            //  progressDialog.dismiss();
                        }
                    });
                }
            });
            holder.btn_approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final MaterialDialog dialog = new MaterialDialog.Builder(mActivity).title("Select Group Type").positiveText("Done").negativeText("Close").onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            buyer = null;
                            dialog.dismiss();
                        }
                    }).customView(R.layout.group_type_select_view, true).build();

                    final Spinner spinner = (Spinner) dialog.getCustomView().findViewById(R.id.group_type);

                    if (responseBuyerGroupTypes != null) {
                        try {

                            SpinAdapter_grouptypes spinAdapter_grouptypes = new SpinAdapter_grouptypes(mActivity, R.layout.spinneritem, responseBuyerGroupTypes);
                            spinner.setAdapter(spinAdapter_grouptypes);

                        } catch (Exception e) {

                        }

                        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                patchApproveBuyer(mActivity, bg.getId(), "approved", ((Response_BuyerGroupType) spinner.getSelectedItem()).getId(), holder.getAdapterPosition(), dialog,holder,bg);
                            }
                        });

                        dialog.show();
                    }
                }
            });

        } else {

            holder.buyerName.setText("N/A");
            holder.buyerNumber.setText("N/A");

            //holder.buyerEmailTv.setText("N/A");
        }
    }

    private void showViews(MyViewHolder holder) {
        // change according to design revision suggested broker
        holder.menu.setVisibility(View.GONE);

        holder.btn_approve.setVisibility(View.INVISIBLE);
        holder.btn_reject.setVisibility(View.INVISIBLE);

        holder.create_order.setVisibility(View.INVISIBLE);


    }

    private void hideViewsRejected(MyViewHolder holder) {
        // change according to design revision suggested vroker
        holder.menu.setVisibility(View.GONE);
        holder.btn_approve.setVisibility(View.INVISIBLE);
        holder.btn_reject.setVisibility(View.INVISIBLE);
        holder.buyerStatus.setText("Status: Rejected");
        holder.create_order.setVisibility(View.INVISIBLE);
    }

    private void hideViewsApproved(MyViewHolder holder) {

        // change according to design revision suggested vroker
        holder.menu.setVisibility(View.GONE);
        holder.btn_approve.setVisibility(View.INVISIBLE);
        holder.btn_reject.setVisibility(View.INVISIBLE);
        holder.create_order.setVisibility(View.INVISIBLE);
        holder.buyerStatus.setText("Status: Approved");
    }

    private void patchApproveBuyer(final AppCompatActivity mActivity, String id, String approved, String group_type, final int position, final MaterialDialog dialog, final MyViewHolder holder, final Response_Buyer bg) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(mActivity);
        ApprovedPatchBuyerStatus approveBuyer = new ApprovedPatchBuyerStatus(id, "approved", group_type);
        Gson gson = new Gson();
        HttpManager.getInstance(mActivity).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(mActivity, "buyers", "") + id + '/', gson.fromJson(gson.toJson(approveBuyer), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                dialog.dismiss();
                Toast.makeText(mActivity, "Buyer Approved successfully", Toast.LENGTH_LONG).show();
              //  mApprovedBuyersList.remove(position);
                //notifyDataSetChanged();
                hideViewsApproved(holder);
                bg.setStatus("approved");
                notifyItemChanged(position);

            }

            @Override
            public void onResponseFailed(ErrorString error) {
               StaticFunctions.showResponseFailedDialog(error);
                //  progressDialog.dismiss();
            }
        });

    }

   public void PostIdToServer(String company_id, String buyer_id, final AppCompatActivity mActivity, final PopupMenu popupMenu, final MyViewHolder holder, final Response_Buyer bg) {
        String url = URLConstants.companyUrl(mActivity, "buyer_transfer", buyer_id);
        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(mActivity);
        params.put("selling_company", company_id);
        HttpManager.getInstance(mActivity).request(HttpManager.METHOD.POSTWITHPROGRESS, url, params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                // transfer_button.setVisibility(View.GONE);
                popupMenu.getMenu().findItem(R.id.transferred).setVisible(false);
                holder.buyerStatus.setText("Status: Transferred");
                bg.setStatus("Transferred");
                notifyDataSetChanged();
                Toast.makeText(mActivity, "Buyer Transferred Successfully", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponseFailed(ErrorString error) {

              StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public BuyersList getBuyer() {
        return buyer;
    }

    public MaterialDialog getTransferDialog() {
        return transferDialog;
    }

    public void  setBuyer(BuyersList buyer){
        this.buyer = buyer;
    }
    @Override
    public int getItemCount() {
        return mApprovedBuyersList.size();
    }

}
