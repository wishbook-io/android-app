package com.wishbook.catalog.home.contacts.details;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.autocomplete.CustomAutoCompleteTextView;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonadapters.AutoCompleteCommonAdapter;
import com.wishbook.catalog.commonadapters.dropdownadapters.SpinAdapter_grouptypes;
import com.wishbook.catalog.commonmodels.Enquiry_Details;
import com.wishbook.catalog.commonmodels.postpatchmodels.ApprovedPatchBuyerStatus;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchBuyerStatus;
import com.wishbook.catalog.commonmodels.responses.CatalogMinified;
import com.wishbook.catalog.commonmodels.responses.ResponseSuggestedBroker;
import com.wishbook.catalog.commonmodels.responses.Response_BuyerFull;
import com.wishbook.catalog.commonmodels.responses.Response_BuyerGroupType;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;
import com.wishbook.catalog.home.contacts.adapter.SuggestedBrokerAdapter;
import com.wishbook.catalog.home.models.BuyersList;
import com.wishbook.catalog.home.orderNew.add.Fragment_CreateSaleOrder_Version2;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


public class Fragment_BuyerEnquiryDetails extends GATrackedFragment {

    private View v;
    private Response_BuyerFull response_buyer;

    @BindView(R.id.buyer_name)
    TextView enquired_buyer_name;

    @BindView(R.id.buyer_number)
    TextView enquired_buyer_number;

    @BindView(R.id.location)
    TextView enquired_buyer_location;

    @BindView(R.id.catalog_name)
    TextView enquired_catalog_name;

    @BindView(R.id.catalog_image)
    SimpleDraweeView enquired_catalog_image;


    @BindView(R.id.status)
    TextView enquired_status;

    @BindView(R.id.comment)
    TextView enquired_comment;

    @BindView(R.id.comments_container)
    LinearLayout enquired_comments_container;


    @BindView(R.id.btn_reject)
    AppCompatButton btn_reject;

    @BindView(R.id.btn_approve)
    AppCompatButton btn_approve;

    @BindView(R.id.chat_user)
    AppCompatButton chat_user;

    @BindView(R.id.btn_call)
    AppCompatButton btn_call;

    @BindView(R.id.create_order)
    AppCompatButton create_order;

    @BindView(R.id.bottom_buttons_container)
    FrameLayout bottom_buttons_container;

    @BindView(R.id.menu)
    ImageView menu;

    @BindView(R.id.recycle_suggested_broker)
    RecyclerView recycle_suggested_broker;

    @BindView(R.id.suggested_broker_card)
    CardView suggested_broker_card;

    @BindView(R.id.txt_enquiry_date)
            TextView txt_enquiry_date;

    TextView edit_buyername;

    public Fragment_BuyerEnquiryDetails() {
        // Required empty public constructor
    }

    String buyer_id = "";

    private ArrayList<BuyersList> buyerslist = new ArrayList<>();

    private CustomAutoCompleteTextView buyer_select;
    AutoCompleteCommonAdapter adapter;
    private BuyersList buyer = null;
    Response_BuyerGroupType[] responseBuyerGroupTypes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.enquiry_details_buyer_layout, ga_container, true);
        ButterKnife.bind(this, v);


        if (getArguments() != null) {
            if (!getArguments().getString("buyerid", "").equals("")) {
                buyer_id = getArguments().getString("buyerid", "");
                getSuggestedBroker(buyer_id);
            }
        }


        getBuyerGroups();


        //Showing 3 dot menu for each item
        final PopupMenu popupMenu = new PopupMenu(getActivity(), menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.references:
                        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                        PatchBuyerStatus rejectBuyer = new PatchBuyerStatus(buyer_id, "Pending References");
                        Gson gson = new Gson();
                        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "buyers", "") + buyer_id + '/', gson.fromJson(gson.toJson(rejectBuyer), JsonObject.class), headers, new HttpManager.customCallBack() {
                            @Override
                            public void onCacheResponse(String response) {
                            }

                            @Override
                            public void onServerResponse(String response, boolean dataUpdated) {
                                // progressDialog.dismiss();'
                                //holder.ask_references.setVisibility(View.GONE);
                                popupMenu.getMenu().findItem(R.id.references).setVisible(false);
                                enquired_status.setText("Pending References");
                                getActivity().setResult(Activity.RESULT_OK);
                                //HttpManager.getInstance(getActivity()).removeCacheParams(URLConstants.companyUrl(getActivity(), "buyers_enquiry", "") + "&&limit=" + Fragment_BuyersEnquiry.LIMIT + "&&offset=" + 0 + "&&search=" + "", null);

                            }

                            @Override
                            public void onResponseFailed(ErrorString error) {
                                StaticFunctions.showResponseFailedDialog(error);
                            }
                        });
                        break;
                    case R.id.transferred:
                        buyer = null;
                        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).title("Select Buyer").positiveText("Done").negativeText("Close").onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                buyer = null;
                                dialog.dismiss();
                            }
                        }).customView(R.layout.buyer_select_view, true).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (buyer != null) {
                                    PostIdToServer(buyer.getCompany_id(), buyer_id, popupMenu);
                                } else {
                                    Toast.makeText(getActivity(), "Please select buyer from suggested list", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).build();

                        TextInputLayout input_buyername = (TextInputLayout) dialog.getCustomView().findViewById(R.id.input_buyername);
                        edit_buyername = (TextView) dialog.getCustomView().findViewById(R.id.edit_buyername);
                        LinearLayout buyer_container = (LinearLayout) dialog.getCustomView().findViewById(R.id.buyer_container);
                        buyer_container.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                KeyboardUtils.hideKeyboard(getActivity());
                                Fragment_BuyerEnquiryDetails.this.startActivityForResult(new Intent(getActivity(), Activity_BuyerSearch.class), Application_Singleton.BUYER_SEARCH_REQUEST_CODE);
                            }
                        });

                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        edit_buyername.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                if (charSequence != null && !charSequence.toString().isEmpty()) {
                                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

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

                        dialog.show();

                        /*adapter = new AutoCompleteCommonAdapter(getActivity(), R.layout.spinneritem, buyerslist, "buyerlist");
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
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });


        getBuyer(buyer_id, popupMenu);

        chat_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (response_buyer != null) {
                    new ActionLogApi(getActivity(), ActionLogApi.RELATION_TYPE_ENQUIRY, ActionLogApi.ACTION_TYPE_CHAT, String.valueOf(response_buyer.getBuying_company().getId()));
                    Intent intent = new Intent(getActivity(), ConversationActivity.class);
                    intent.putExtra(ConversationUIService.USER_ID, response_buyer.getBuying_company().getChat_user());
                    intent.putExtra(ConversationUIService.DISPLAY_NAME, response_buyer.getBuying_company().getName()); //put it for displaying the title.
                    startActivity(intent);
                }
            }
        });

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(response_buyer!=null){
                    new ActionLogApi(getActivity(), ActionLogApi.RELATION_TYPE_ENQUIRY, ActionLogApi.ACTION_TYPE_CALL, String.valueOf(response_buyer.getBuying_company().getId()));
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + response_buyer.getBuying_company().getPhone_number()));
                    startActivity(intent);
                }

            }
        });

        create_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (response_buyer != null) {
                    //Fragment_CreateOrder createOrderFrag = new Fragment_CreateOrder();
                    //Fragment_CreateOrderNew createOrderFrag = new Fragment_CreateOrderNew();
                    Fragment_CreateSaleOrder_Version2 createOrderFrag = new Fragment_CreateSaleOrder_Version2();
                    Bundle bundle = new Bundle();
                    bundle.putString("buyerselected", response_buyer.getBuying_company().getName());
                    bundle.putString("buyer_selected_broker_id", response_buyer.getBroker_company());
                    bundle.putString("buyer_selected_company_id", String.valueOf(response_buyer.getBuying_company().getId()));
                    BuyersList buyersList = new BuyersList(String.valueOf(response_buyer.getBuying_company().getId()), response_buyer.getBuying_company().getName(), response_buyer.getBroker_company());
                    bundle.putSerializable("buyer", buyersList);
                    createOrderFrag.setArguments(bundle);

                    Application_Singleton.CONTAINER_TITLE = getActivity().getResources().getString(R.string.new_sales_order);
                    Application_Singleton.CONTAINERFRAG = createOrderFrag;
                    //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                    StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                }
            }
        });

        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!buyer_id.equals("")) {
                    HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                    PatchBuyerStatus rejectBuyer = new PatchBuyerStatus(buyer_id, "rejected");
                    Gson gson = new Gson();
                    HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getContext(), "buyers", "") + buyer_id + '/', gson.fromJson(gson.toJson(rejectBuyer), JsonObject.class), headers, new HttpManager.customCallBack() {
                        @Override
                        public void onCacheResponse(String response) {

                        }

                        @Override
                        public void onServerResponse(String response, boolean dataUpdated) {
                            //  mApprovedBuyersList.remove(position);
                            //   notifyDataSetChanged();
                            hideViewsRejected();
                            getActivity().setResult(Activity.RESULT_OK);
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
            }
        });


        btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!buyer_id.equals("")) {
                    final MaterialDialog dialog = new MaterialDialog.Builder(getContext()).title("Select Group Type").positiveText("Done").negativeText("Close").onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            buyer = null;
                            dialog.dismiss();
                        }
                    }).customView(R.layout.group_type_select_view, true).build();

                    final Spinner spinner = (Spinner) dialog.getCustomView().findViewById(R.id.group_type);

                    if (responseBuyerGroupTypes != null) {
                        try {

                            SpinAdapter_grouptypes spinAdapter_grouptypes = new SpinAdapter_grouptypes((AppCompatActivity) getActivity(), R.layout.spinneritem, responseBuyerGroupTypes);
                            spinner.setAdapter(spinAdapter_grouptypes);

                        } catch (Exception e) {

                        }

                        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                patchApproveBuyer(buyer_id, "approved", ((Response_BuyerGroupType) spinner.getSelectedItem()).getId(), dialog);
                            }
                        });

                        dialog.show();
                    }
                }
            }
        });


        return v;
    }

    private void getBuyerGroups() {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "grouptype", ""), null, headers, true, new HttpManager.customCallBack() {
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
                responseBuyerGroupTypes = Application_Singleton.gson.fromJson(response, Response_BuyerGroupType[].class);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }

    private void getBuyer(String id, final PopupMenu popupMenu) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "buyers", "") + id + "/?expand=true", null, headers, true, new HttpManager.customCallBack() {
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
                response_buyer = new Gson().fromJson(response, Response_BuyerFull.class);
                if (response_buyer != null) {
                    enquired_buyer_name.setText(response_buyer.getBuying_company().getName());
                    enquired_buyer_number.setText("(" + response_buyer.getBuying_company().getPhone_number() + ")");
                    txt_enquiry_date.setText(DateUtils.getFormattedDate(StringUtils.capitalize(response_buyer.getCreated_at().toString().toLowerCase().trim())));
                    if (!response_buyer.getBuying_company().getBranches().get(0).getCity().getCityName().equals("-") && !response_buyer.getBuying_company().getBranches().get(0).getCity().getStateName().equals("-")) {
                        enquired_buyer_location.setText(response_buyer.getBuying_company().getBranches().get(0).getCity().getCityName() + " , " + response_buyer.getBuying_company().getBranches().get(0).getCity().getStateName());
                    } else {
                        enquired_buyer_location.setVisibility(View.GONE);
                    }

                    //Setting Status

                    //for Pending References
                    if (response_buyer.getStatus().equals("Pending References") || response_buyer.getStatus().equals("References Filled")) {
                        // holder.ask_references.setVisibility(View.GONE);
                        popupMenu.getMenu().findItem(R.id.references).setVisible(false);
                        showViews();

                    } else {
                        //  holder.ask_references.setVisibility(View.VISIBLE);
                        popupMenu.getMenu().findItem(R.id.references).setVisible(true);
                        showViews();
                    }


                    //for transferred
                    if (response_buyer.getStatus().equals("Transferred")) {
                        showViews();
                        //  holder.transfer_button.setVisibility(View.GONE);
                        popupMenu.getMenu().findItem(R.id.transferred).setVisible(false);
                    } else {
                        if (!response_buyer.getStatus().equals("Pending References") && !response_buyer.getStatus().equals("References Filled")) {
                            //  holder.ask_references.setVisibility(View.VISIBLE);
                            popupMenu.getMenu().findItem(R.id.references).setVisible(true);
                            showViews();
                        }
                    }


                    //for approved
                    if (response_buyer.getStatus().toLowerCase().equals("approved")) {
                        hideViewsApproved();
                    }

                    //for rejected
                    if (response_buyer.getStatus().toLowerCase().equals("rejected")) {
                        hideViewsRejected();
                    }


                    if (response_buyer.getStatus().equals("supplier_pending")) {
                        enquired_status.setText(StringUtils.capitalize("Supplier Pending"));
                        showViews();
                    } else {
                        enquired_status.setText(StringUtils.capitalize(response_buyer.getStatus().toString()));
                    }
                    String details = "";
                    Enquiry_Details enquiry_details = null;
                    if (response_buyer.getDetails() != null) {
                        details = response_buyer.getDetails().replace("'", "");
                        enquiry_details = new Gson().fromJson(details, Enquiry_Details.class);
                    }

                    if (enquiry_details != null) {
                        if (enquiry_details.getRefrences() != null) {
                            enquired_comments_container.setVisibility(View.VISIBLE);
                            enquired_comment.setText(enquiry_details.getRefrences());
                        } else {
                            enquired_comments_container.setVisibility(View.GONE);
                        }

                        String catalog_id = enquiry_details.getCatalog();
                        getCatalogs(catalog_id);
                    }
                }
            }


            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }

    private void getCatalogs(String catalog) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "catalogs_expand_true_id", catalog), null, headers, true, new HttpManager.customCallBack() {
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
                final Response_catalog response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                if (response_catalog != null) {
                    enquired_catalog_name.setText(response_catalog.getTitle());
                    if (response_catalog.getThumbnail().getThumbnail_medium() != null) {
                        //StaticFunctions.loadImage(getContext(), response_catalog.getThumbnail().getThumbnail_medium(), enquired_catalog_image, R.drawable.uploadempty);
                        StaticFunctions.loadFresco(getContext(), response_catalog.getThumbnail().getThumbnail_medium(), enquired_catalog_image);
                    }

                    enquired_catalog_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            goToCatalogDetailPage(response_catalog);
                        }
                    });

                    enquired_catalog_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            goToCatalogDetailPage(response_catalog);
                        }
                    });

                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });


    }

    private void showViews() {
        bottom_buttons_container.setVisibility(View.VISIBLE);
    }

    private void hideViewsRejected() {
        bottom_buttons_container.setVisibility(View.INVISIBLE);
        enquired_status.setText("Rejected");
    }

    private void hideViewsApproved() {
        bottom_buttons_container.setVisibility(View.VISIBLE);
        menu.setVisibility(View.GONE);
        btn_approve.setVisibility(View.INVISIBLE);
        btn_reject.setVisibility(View.INVISIBLE);
        create_order.setVisibility(View.VISIBLE);
        enquired_status.setText("Approved");
    }


    private void getSuggestedBroker(String relationID) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "suggested_broker", relationID), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                Log.v("Broker", response);
                ResponseSuggestedBroker[] brokers = Application_Singleton.gson.fromJson(response, ResponseSuggestedBroker[].class);
                if (brokers.length > 0) {
                    suggested_broker_card.setVisibility(View.VISIBLE);
                    recycle_suggested_broker.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recycle_suggested_broker.setHasFixedSize(true);
                    recycle_suggested_broker.setNestedScrollingEnabled(false);
                    recycle_suggested_broker.setItemAnimator(new DefaultItemAnimator());
                    ArrayList<ResponseSuggestedBroker> brokerArrayList = new ArrayList<ResponseSuggestedBroker>(Arrays.asList(brokers));
                    Log.i("TAG", "onServerResponse: "+brokerArrayList.size());
                    SuggestedBrokerAdapter suggestedBrokerAdapter = new SuggestedBrokerAdapter(getActivity(), brokerArrayList);
                    recycle_suggested_broker.setAdapter(suggestedBrokerAdapter);
                } else {
                    suggested_broker_card.setVisibility(View.GONE);
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });
    }

    private void patchApproveBuyer(String id, String approved, String group_type, final MaterialDialog dialog) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        ApprovedPatchBuyerStatus approveBuyer = new ApprovedPatchBuyerStatus(id, "approved", group_type);
        Gson gson = new Gson();
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "buyers", "") + id + '/', gson.fromJson(gson.toJson(approveBuyer), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Buyer Approved successfully", Toast.LENGTH_LONG).show();
                hideViewsApproved();
                getActivity().setResult(Activity.RESULT_OK);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void PostIdToServer(String company_id, String buyer_id, final PopupMenu popupMenu) {
        String url = URLConstants.companyUrl(getActivity(), "buyer_transfer", buyer_id);
        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        params.put("selling_company", company_id);
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POSTWITHPROGRESS, url, params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                // transfer_button.setVisibility(View.GONE);
                popupMenu.getMenu().findItem(R.id.transferred).setVisible(false);
                enquired_status.setText("Transferred");
                Toast.makeText(getActivity(), "Buyer Transferred Successfully", Toast.LENGTH_LONG).show();
                getActivity().setResult(Activity.RESULT_OK);
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

                Log.d("ERRROR", error.toString());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Application_Singleton.BUYER_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getSerializableExtra("buyer") != null) {
                buyer = (BuyersList) data.getSerializableExtra("buyer");
                if (edit_buyername != null) {
                    edit_buyername.setText(buyer.getCompany_name());
                }
            }
        }
    }


    public void goToCatalogDetailPage(Response_catalog response_catalog) {
        CatalogMinified response = new CatalogMinified(response_catalog.getId(), "catalog", response_catalog.isBuyer_disabled(), response_catalog.getTitle(), response_catalog.getBrand().getName(), response_catalog.getView_permission());
        response.setIs_supplier_approved(response_catalog.getIs_supplier_approved());
        response.setSupplier(response_catalog.getSupplier());
        response.setFull_catalog_orders_only(response_catalog.getFull_catalog_orders_only());
        response.setSupplier_name(response_catalog.getSupplier_name());
        response.setSupplier_chat_user(response_catalog.getSupplier_chat_user());
        response.setBuyer_disabled(response_catalog.isBuyer_disabled());
        // response.setSupplier_disabled(response_catalog.get());
        response.setPrice_range(response_catalog.getPrice_range());
        response.setSupplier_details(response_catalog.getSupplier_details());
        response.setIs_owner(response_catalog.is_owner());
        response.setIs_addedto_wishlist(response_catalog.getIs_addedto_wishlist());
        response.setFromPublic(true);
        if(response_catalog.getSupplier_details()!=null) {
            response.setNear_by_sellers(response_catalog.getSupplier_details().getNear_by_sellers());
        }
        Application_Singleton.selectedshareCatalog = response;
        Fragment_CatalogsGallery gallery = new Fragment_CatalogsGallery();
        Application_Singleton.CONTAINER_TITLE = response_catalog.getTitle();
        Application_Singleton.CONTAINERFRAG = gallery;
        Intent intent = new Intent(getActivity(), OpenContainer.class);
        intent.putExtra("pushId", response_catalog.getPush_user_id());
        intent.putExtra("toolbarCategory", OpenContainer.BROWSECATALOG);
        intent.putExtra("near_by_text",response_catalog.getSupplier_details().getNear_by_sellers());
        intent.putExtra("Ordertype", "catalog");
        getActivity().startActivity(intent);
    }
}
