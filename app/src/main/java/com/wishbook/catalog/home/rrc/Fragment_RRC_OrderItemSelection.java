package com.wishbook.catalog.home.rrc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.KeyboardUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.multipleimageselect.activities.AlbumSelectActivity;
import com.wishbook.catalog.Utils.multipleimageselect.models.Image;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.RRCRequest;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.commonmodels.responses.Response_sellingoder_catalog;
import com.wishbook.catalog.commonmodels.responses.Rrc_images;
import com.wishbook.catalog.commonmodels.responses.Rrc_items;
import com.wishbook.catalog.home.rrc.adapter.RRCOrderItemAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_RRC_OrderItemSelection extends GATrackedFragment implements SwipeRefreshLayout.OnRefreshListener, OpenContainer.OnBackPressedListener {

    protected View view;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipe_container;

    @BindView(R.id.linear_empty)
    LinearLayout linear_empty;

    @BindView(R.id.nested_scroll_step_two_images)
    NestedScrollView linear_step_two_images;

    @BindView(R.id.flexbox_image)
    FlexboxLayout flexbox_image;

    @BindView(R.id.attach_button)
    AppCompatButton attach_button;

    @BindView(R.id.btn_continue)
    AppCompatButton btn_continue;


    Context mContext;

    Handler handler;
    Runnable runnable;
    boolean isAllowCache = false;


    RRCOrderItemAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    String order_id;

    RRCRequest edit_rrc_request;
    Response_buyingorder response_buyingorder;

    RRCHandler.RRCREQUESTTYPE rrcrequesttype;

    int step_counter = 1;

    ArrayList<Image> rrcImageArrayList;

    public Fragment_RRC_OrderItemSelection() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_rrc_order_item_selection, ga_container, true);
        ButterKnife.bind(this, view);
        initView();
        initSwipeRefresh(view);
        step_counter = 1;
        updateUI(false);
        return view;
    }

    public void initView() {
        if (getActivity() instanceof OpenContainer) {
            ((OpenContainer) getActivity()).setOnBackPressedListener(this);
            Drawable icon = getResources().getDrawable(R.drawable.ic_arrow_back_24dp);
            icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            ((OpenContainer) getActivity()).toolbar.setNavigationIcon(icon);
            ((OpenContainer) getActivity()).toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doBack();
                }
            });
        }

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setHasFixedSize(true);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerview.getContext(),
                linearLayoutManager.getOrientation());
        recyclerview.addItemDecoration(dividerItemDecoration);


        attach_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
               // intent.putExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, limit);
                Fragment_RRC_OrderItemSelection.this.startActivityForResult(intent, Application_Singleton.MULTIIMAGE_PRODUCT_REQUEST_CODE);
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (step_counter == 2) {
                    if (edit_rrc_request == null) {
                        if (adapter.getAllSelectedItems() != null && adapter.getAllSelectedItems().size() > 0 && rrcImageArrayList!=null && rrcImageArrayList.size() > 0) {
                            showRRCReasonDialog(mContext, adapter.getAllSelectedItems());
                        } else {
                            if(rrcImageArrayList == null) {
                                Toast.makeText(getActivity(),"Minimum one image is required",Toast.LENGTH_LONG).show();
                            } else if(rrcImageArrayList.size() == 0) {
                                Toast.makeText(getActivity(),"Minimum one image is required",Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        int check_rrc_image_count = 0;
                        if(edit_rrc_request.getRrc_images()!=null){
                            check_rrc_image_count += edit_rrc_request.getRrc_images().size();
                        }
                        if(rrcImageArrayList!=null) {
                            check_rrc_image_count+= rrcImageArrayList.size();
                        }
                        if (adapter.getAllSelectedItems() != null && check_rrc_image_count > 0) {
                            onSubmitClick(adapter.getAllSelectedItems(), edit_rrc_request.getRequest_reason_text());
                        } else {
                            if(edit_rrc_request.getRrc_images().size() == 0 || rrcImageArrayList.size() == 0) {
                                Toast.makeText(getActivity(),"Minimum one image is required",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } else {
                    if (edit_rrc_request == null) {
                        if (adapter!=null && adapter.getAllSelectedItems() != null && adapter.getAllSelectedItems().size() > 0) {
                            step_counter = step_counter + 1;
                            updateUI(false);
                        }
                    } else {
                        if (adapter!=null && adapter.getAllSelectedItems() != null) {
                            step_counter = step_counter + 1;
                            updateUI(false);
                        }
                    }
                }
            }
        });
        initCall();
    }

    public void initSwipeRefresh(View view) {
        swipe_container = view.findViewById(R.id.swipe_container);
        swipe_container.setOnRefreshListener(this);
        swipe_container.setColorScheme(android.R.color.holo_blue_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }


    public void initCall() {
        if (getArguments() != null && getArguments().getString("order_id") != null) {
            order_id = getArguments().getString("order_id");
            callOrderCatalogWise();
        }

        if (getArguments() != null && getArguments().getSerializable("request_type") != null) {
            rrcrequesttype = (RRCHandler.RRCREQUESTTYPE) getArguments().getSerializable("request_type");
        }

        if (getArguments().getSerializable("data") != null) {
            edit_rrc_request = (RRCRequest) getArguments().getSerializable("data");
            if (edit_rrc_request.getRequest_type() != null) {
                if (edit_rrc_request.getRequest_type().equalsIgnoreCase("replacement"))
                    rrcrequesttype = RRCHandler.RRCREQUESTTYPE.REPLACEMENT;
                else if (edit_rrc_request.getRequest_type().equalsIgnoreCase("return"))
                    rrcrequesttype = RRCHandler.RRCREQUESTTYPE.RETURN;
                else if (edit_rrc_request.getRequest_type().equalsIgnoreCase("cancellation"))
                    rrcrequesttype = RRCHandler.RRCREQUESTTYPE.CANCELLATION;
            }

            btn_continue.setText("Continue");
        }
    }


    public void showRRCReasonDialog(Context context, HashMap<String, String> param) {
        Dialog dialog = new Dialog(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.dialog_rrc_reason, null);
        dialog.setContentView(dialogLayout);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        initDialogLayout(dialogLayout, dialog, rrcrequesttype, param);
        dialog.show();
    }


    public void initDialogLayout(final View dialogview, final Dialog dialog, Enum type, final HashMap<String, String> param) {
        TextView btn_negative = dialogview.findViewById(R.id.btn_negative);
        TextView btn_positive = dialogview.findViewById(R.id.btn_positive);
        TextView txt_dialog_title = dialogview.findViewById(R.id.txt_dialog_title);

        final EditText edit_other_reason = dialogview.findViewById(R.id.edit_other_reason);
        TextInputLayout txt_input_other_reason = dialogview.findViewById(R.id.txt_input_other_reason);
        final Spinner spinner_reason = dialogview.findViewById(R.id.spinner_reason);
        final String[] reasons_array = getResources().getStringArray(R.array.replacement_reasons);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                getActivity(), R.layout.order_spinner_item, reasons_array
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinneritem_new);
        spinner_reason.setAdapter(spinnerArrayAdapter);
        if(type!=null) {
            if (type == RRCHandler.RRCREQUESTTYPE.REPLACEMENT)
                txt_dialog_title.setText(String.format(getResources().getString(R.string.select_rrc_reason_title), "replacement"));
            else if (type == RRCHandler.RRCREQUESTTYPE.CANCELLATION)
                txt_dialog_title.setText(String.format(getResources().getString(R.string.select_rrc_reason_title), "cancellation"));
            else if(type == RRCHandler.RRCREQUESTTYPE.RETURN)
                txt_dialog_title.setText(String.format(getResources().getString(R.string.select_rrc_reason_title), "return"));
            else
                txt_dialog_title.setText(String.format(getResources().getString(R.string.select_rrc_reason_title), ""));
        } else {
            txt_dialog_title.setText(String.format(getResources().getString(R.string.select_rrc_reason_title), ""));
        }


        spinner_reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (reasons_array[i].equalsIgnoreCase("Others")) {
                    edit_other_reason.setVisibility(View.VISIBLE);
                } else {
                    edit_other_reason.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reason = spinner_reason.getSelectedItem().toString();
                if (spinner_reason.getSelectedItem().toString().equalsIgnoreCase("Others")) {
                    reason = "Others: " + edit_other_reason.getText().toString();
                }
                dialog.dismiss();
                onSubmitClick(param, reason);


            }
        });

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void setStepOne() {
        recyclerview.setVisibility(View.VISIBLE);
        linear_step_two_images.setVisibility(View.GONE);
        swipe_container.setEnabled(true);

        if (getActivity() instanceof OpenContainer) {
            String type_tile = "";
            if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.REPLACEMENT) {
                type_tile = "replacement";
            } else if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.CANCELLATION) {
                type_tile = "cancellation";
            } else if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.RETURN) {
                type_tile = "return";
            }
            if (edit_rrc_request == null) {
                ((OpenContainer) getActivity()).toolbar.setTitle("Select items to " + type_tile);
            } else {
                ((OpenContainer) getActivity()).toolbar.setTitle("Edit items of " + type_tile);
            }


            btn_continue.setText("Continue");
        }

        loadRRCImages();
    }

    public void setStepTwo() {
        recyclerview.setVisibility(View.GONE);
        linear_step_two_images.setVisibility(View.VISIBLE);
        swipe_container.setEnabled(false);
        KeyboardUtils.hideKeyboard(getActivity());

        if (getActivity() instanceof OpenContainer) {
            if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.REPLACEMENT) {
                ((OpenContainer) getActivity()).toolbar.setTitle("Attach images for replacement");
            } else if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.RETURN) {
                ((OpenContainer) getActivity()).toolbar.setTitle("Attach images for return");
            } else if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.CANCELLATION) {
                ((OpenContainer) getActivity()).toolbar.setTitle("Attach images for cancellation");
            }
        }

        btn_continue.setText("Submit");
    }


    @Override
    public void doBack() {
        if (step_counter == 1) {
            if (getActivity() != null)
                getActivity().finish();
        } else {
            step_counter = step_counter - 1;
            btn_continue.setText("Continue");
            updateUI(true);
        }
    }

    public void updateUI(boolean isFromBack) {
        if (step_counter == 1) {
            setStepOne();
        } else if (step_counter == 2) {
            setStepTwo();
        }
    }


    public void onSubmitClick(HashMap<String, String> param, String reason) {
        RRCHandler rrcHandler = new RRCHandler((Activity) mContext);
        rrcHandler.setRrcHandlerListner(new RRCHandler.RRCHandlerListner() {
            @Override
            public void onSuccessRequest() {
                if (edit_rrc_request != null) {
                    Toast.makeText(mContext, "Successfully request updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Successfully request created", Toast.LENGTH_SHORT).show();
                }

                if (getActivity() != null)
                    getActivity().finish();

            }

            @Override
            public void onSuccessCancel() {

            }

        });

        ArrayList<Rrc_items> rrc_items = new ArrayList<>();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            Rrc_items rrc_items1 = new Rrc_items();
            if (edit_rrc_request != null && edit_rrc_request.getRrc_items() != null) {
                rrc_items1.setQty(Integer.parseInt(entry.getValue()));
                rrc_items1.setOrder_item(entry.getKey());
                rrc_items1.setRrc(edit_rrc_request.getId());
                for (int j = 0; j < edit_rrc_request.getRrc_items().size(); j++) {
                    if (entry.getKey().equalsIgnoreCase(edit_rrc_request.getRrc_items().get(j).getOrder_item())) {
                        rrc_items1 = edit_rrc_request.getRrc_items().get(j);
                        rrc_items1.setQty(Integer.parseInt(entry.getValue()));
                    }
                }
            } else {
                rrc_items1.setQty(Integer.parseInt(entry.getValue()));
                rrc_items1.setOrder_item(entry.getKey());
            }
            rrc_items.add(rrc_items1);
        }


        if (edit_rrc_request == null) {
            // Post Mode
            if (response_buyingorder.getInvoice() != null && response_buyingorder.getInvoice().size() > 0) {
                rrcHandler.callRequest(rrcrequesttype, order_id,
                        reason, response_buyingorder.getInvoice().get(0).getId(),
                        rrc_items, false, null, rrcImageArrayList);
            } else {
                Toast.makeText(mContext, "Something went wrong (Invoice not found)", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Edit Mode
            rrcHandler.callRequest(rrcrequesttype, order_id,
                    edit_rrc_request.getRequest_reason_text(), edit_rrc_request.getInvoice_id(),
                    rrc_items, true, edit_rrc_request.getId(), rrcImageArrayList);
        }
    }

    @Override
    public void onRefresh() {
        runnable = new Runnable() {

            @Override
            public void run() {
                isAllowCache = false;
                swipe_container.setRefreshing(false);
                initCall();
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 500);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("TAG", "onActivityResult Additional Images: Request Code" + requestCode + "\n Result Code=>" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Application_Singleton.MULTIIMAGE_PRODUCT_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<Image> temp = data.getParcelableArrayListExtra(com.wishbook.catalog.Utils.multipleimageselect.helpers.Constants.INTENT_EXTRA_IMAGES);
            if (temp.size() > 0) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (rrcImageArrayList != null && rrcImageArrayList.size() > 0) {
                    rrcImageArrayList.addAll(temp);
                } else {
                    rrcImageArrayList = new ArrayList<>();
                    rrcImageArrayList.addAll(temp);
                }

                loadRRCImages();
            }
        }
    }

    public void loadRRCImages() {
        flexbox_image.removeAllViews();
        final ArrayList<Image> images = new ArrayList<>();
        // if there is no image in photos than show one product original image is first image
        if (edit_rrc_request != null && edit_rrc_request.getRrc_images() != null) {
            for (Rrc_images photo :
                    edit_rrc_request.getRrc_images()) {
                Image image = new Image();
                image.setPhoto_id(photo.getId());
                image.setPath(photo.getImage().getThumbnail_small());
                image.setName(photo.getId());
                if (images != null)
                    images.add(image);
            }
        }


        if (rrcImageArrayList != null)
            images.addAll(rrcImageArrayList);
        if (images != null && images.size() > 0) {
            for (int i = 0; i < images.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.rrc_item_image, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(params);
                if (images.get(i).getPhoto_id() != null) {
                    // Load from web
                    if (images.get(i).getPath() != null) {
                        StaticFunctions.loadFresco(getActivity(), images.get(i).getPath(), (SimpleDraweeView) view.findViewById(R.id.img_set));
                    }
                } else {
                    // Load from local
                    if (images.get(i).getPath() != null) {
                        StaticFunctions.loadFresco(getActivity(), "file://" + images.get(i).getPath(), (SimpleDraweeView) view.findViewById(R.id.img_set));
                    }
                }

                flexbox_image.addView(view);
                ImageView img_remove_set = view.findViewById(R.id.img_remove_set);
                if (images.get(i).isDefaultCatalogImage)
                    img_remove_set.setVisibility(View.GONE);
                else
                    img_remove_set.setVisibility(View.VISIBLE);

                final int finalI = i;
                final ArrayList<Image> finalImages = images;
                img_remove_set.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                .title("Delete Images")
                                .content("Are you sure you want to delete this images?")
                                .positiveText("OK")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        dialog.dismiss();
                                        if (finalImages.get(finalI).getPhoto_id() != null) {
                                            // Call Server for Delete product-photos
                                            callDeleteRRCImage(finalImages.get(finalI).getPhoto_id(), finalI, finalImages);
                                        } else {
                                            // Local Image Delete
                                            rrcImageArrayList.remove(images.get(finalI));
                                            loadRRCImages();
                                        }
                                    }
                                })
                                .negativeText("Cancel")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();

                    }
                });
                attach_button.setVisibility(View.VISIBLE);
            }

        }

        flexbox_image.addView(attach_button);
    }


    public void callDeleteRRCImage(final String deleteid, final int position, ArrayList<Image> images) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());

        String url = URLConstants.companyUrl(getActivity(), "rrc-requests-images", "") + deleteid + "/";
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.DELETEWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    ArrayList<Rrc_images> temp = new ArrayList<>();
                    temp.addAll(edit_rrc_request.getRrc_images());
                    for (Rrc_images photo :
                            temp) {
                        if (photo.getId().equals(deleteid)) {
                            edit_rrc_request.getRrc_images().remove(photo);
                        }
                    }
                    temp = null;
                    loadRRCImages();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    public void callOrderCatalogWise() {
        try {
            showProgress();
            String url = "purchaseorders_catalogwise";
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
            HttpManager.getInstance((Activity) mContext).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(mContext, url, order_id), null, headers, isAllowCache, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    if (isAdded() && !isDetached()) {
                        response_buyingorder = Application_Singleton.gson.fromJson(response, new TypeToken<Response_buyingorder>() {
                        }.getType());
                        if (response_buyingorder != null) {
                            if (response_buyingorder.getCatalogs().size() > 0) {
                                recyclerview.setVisibility(View.VISIBLE);
                                linear_empty.setVisibility(View.GONE);
                                ArrayList<Response_sellingoder_catalog> catalogArrayList = new ArrayList<>();

                                for (int i = 0; i < response_buyingorder.getCatalogs().size(); i++) {
                                    Response_sellingoder_catalog catalog = response_buyingorder.getCatalogs().get(i);
                                    if (catalog.getProducts() != null
                                            && catalog.getProducts().size() > 0) {
                                        int RWQ = catalog.getProducts().get(0).getDelivered_qty();
                                        for (int j = 0; j < response_buyingorder.getRrces().size(); j++) {
                                            for (Rrc_items item :
                                                    response_buyingorder.getRrces().get(j).getRrc_items()) {
                                                if (catalog.getProducts().get(0).getId().equals(item.getOrder_item())) {
                                                    if (edit_rrc_request != null) {
                                                        if (!edit_rrc_request.getRrc_items().contains(item)) {
                                                            if (response_buyingorder.getRrces().get(j).getRequest_status().equalsIgnoreCase("requested") || response_buyingorder.getRrces().get(j).getRequest_status().equalsIgnoreCase("approved")) {
                                                                RWQ -= item.getQty();
                                                            }
                                                        }
                                                    } else {
                                                        if(response_buyingorder.getRrces().get(j).getRequest_type()!=null
                                                                && response_buyingorder.getRrces().get(j).getRequest_type().equalsIgnoreCase("return")
                                                                || response_buyingorder.getRrces().get(j).getRequest_type().equalsIgnoreCase("replacement")) {
                                                            if (response_buyingorder.getRrces().get(j).getRequest_status().equalsIgnoreCase("requested") || response_buyingorder.getRrces().get(j).getRequest_status().equalsIgnoreCase("approved")) {
                                                                RWQ -= item.getQty();
                                                            }
                                                        }

                                                    }

                                                }
                                            }
                                        }


                                        if (RWQ > 0) {
                                            response_buyingorder.getCatalogs().get(i).getProducts().get(0).setRWQ_qty(RWQ);
                                            catalogArrayList.add(response_buyingorder.getCatalogs().get(i));
                                        }
                                    }
                                }

                                // RWQ = Delivered Quantity - (Pending Qty To Be Replaced/Returned + Qty of Items already returned)

                                if (edit_rrc_request != null)
                                    adapter = new RRCOrderItemAdapter(mContext, catalogArrayList, edit_rrc_request.getRrc_items());
                                else
                                    adapter = new RRCOrderItemAdapter(mContext, catalogArrayList, null);

                                if (catalogArrayList.size() == 0) {
                                    Toast.makeText(mContext, "No delivered products found", Toast.LENGTH_SHORT).show();
                                    disableSubmitButtons();
                                } else {
                                    enableSubmitButtons();
                                }
                                recyclerview.setAdapter(adapter);
                            } else {
                                linear_empty.setVisibility(View.VISIBLE);
                                recyclerview.setVisibility(View.GONE);
                            }
                        }
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                }
            });


        } catch (Exception e) {
            hideProgress();
            e.printStackTrace();
        }
    }

    private void disableSubmitButtons() {
        btn_continue.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.purchase_light_gray));
        btn_continue.setEnabled(false);

    }

    private void enableSubmitButtons() {
        btn_continue.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_primary));
        btn_continue.setEnabled(true);
    }
}

