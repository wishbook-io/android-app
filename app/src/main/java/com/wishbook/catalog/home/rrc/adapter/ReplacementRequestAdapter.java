package com.wishbook.catalog.home.rrc.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Photos;
import com.wishbook.catalog.commonmodels.responses.RRCRequest;
import com.wishbook.catalog.commonmodels.responses.Rrc_images;
import com.wishbook.catalog.home.catalog.details.Activity_ProductPhotos;
import com.wishbook.catalog.home.rrc.Fragment_RRC_OrderItemSelection;
import com.wishbook.catalog.home.rrc.RRCActionBottomSheet;
import com.wishbook.catalog.home.rrc.RRCHandler;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReplacementRequestAdapter extends RecyclerView.Adapter<ReplacementRequestAdapter.RRCRequestViewHolder> {


    ArrayList<RRCRequest> itemsArrayList;
    Context context;
    RRCHandler.RRCREQUESTTYPE rrcrequesttype;

    public ReplacementRequestAdapter(Context context, ArrayList<RRCRequest> itemsArrayList, RRCHandler.RRCREQUESTTYPE rrcrequesttype) {
        this.itemsArrayList = itemsArrayList;
        this.context = context;
        this.rrcrequesttype = rrcrequesttype;
    }

    @NonNull
    @Override
    public RRCRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.replacement_request_item, viewGroup, false);
        return new RRCRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RRCRequestViewHolder rrcRequestViewHolder, int i) {
        final RRCRequest requestItem = itemsArrayList.get(i);
        if (requestItem.getImages() != null && requestItem.getImages().size() > 0 && !requestItem.getImages().get(0).isEmpty())
            StaticFunctions.loadFresco(context, requestItem.getImages().get(0), rrcRequestViewHolder.prod_img);

        rrcRequestViewHolder.txt_order_value.setText("\u20B9 " + requestItem.getTotal_rate());
        rrcRequestViewHolder.txt_order_no.setText("Request #" + requestItem.getId());
        rrcRequestViewHolder.txt_order_number.setText(requestItem.getOrder());
        String t1 = DateUtils.changeDateFormat(StaticFunctions.SERVER_POST_FORMAT1, StaticFunctions.CLIENT_DISPLAY_FORMAT1, requestItem.getCreated());
        rrcRequestViewHolder.txt_request_date.setText(t1);
        rrcRequestViewHolder.txt_request_status.setText(StringUtils.capitalize(requestItem.getRequest_status()));
        if (requestItem.getRequest_status().equalsIgnoreCase("approved") ||
                requestItem.getRequest_status().equalsIgnoreCase("rejected") || requestItem.getRequest_status().equalsIgnoreCase("cancel")) {
            rrcRequestViewHolder.txt_update_request.setVisibility(View.GONE);
        } else {
            rrcRequestViewHolder.txt_update_request.setVisibility(View.VISIBLE);
        }

        if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.REPLACEMENT) {
            rrcRequestViewHolder.txt_request_label.setText("Replacement Status");
        } else if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.CANCELLATION) {
            rrcRequestViewHolder.txt_request_label.setText("Cancellation Status");
        } else if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.RETURN) {
            rrcRequestViewHolder.txt_request_label.setText("Return Status");
        }

        if (requestItem.getRequest_reason_text() != null && !requestItem.getRequest_reason_text().isEmpty()) {
            rrcRequestViewHolder.linear_request_reason.setVisibility(View.VISIBLE);
            rrcRequestViewHolder.txt_request_reason_value.setText(requestItem.getRequest_reason_text());
        } else {
            rrcRequestViewHolder.linear_request_reason.setVisibility(View.GONE);
        }

        if (requestItem.getRequest_status().equalsIgnoreCase("rejected") && requestItem.getReject_reason() != null) {
            rrcRequestViewHolder.linear_rejected_reason.setVisibility(View.VISIBLE);
            rrcRequestViewHolder.txt_rejected_reason_value.setText(requestItem.getReject_reason());
        } else {
            rrcRequestViewHolder.linear_rejected_reason.setVisibility(View.GONE);
        }
        if (requestItem.getRrc_images() != null && requestItem.getRrc_images().size() > 0) {
            rrcRequestViewHolder.txt_view_image.setVisibility(View.VISIBLE);
            rrcRequestViewHolder.txt_view_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<Photos> photos  = new ArrayList<>();
                    for (Rrc_images images:
                            requestItem.getRrc_images()) {
                        photos.add(new Photos(images.getId(),images.getImage()));
                    }
                    Intent photos_intent = new Intent(context, Activity_ProductPhotos.class);
                    photos_intent.putExtra("photos", photos);
                    photos_intent.putExtra("position", 0);
                    context.startActivity(photos_intent);
                }
            });
        } else {
            rrcRequestViewHolder.txt_view_image.setVisibility(View.GONE);
        }

        rrcRequestViewHolder.txt_update_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RRCActionBottomSheet rrcActionBottomSheet = RRCActionBottomSheet.newInstance(null);
                rrcActionBottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), rrcActionBottomSheet.getTag());
                rrcActionBottomSheet.setDismissListener(new RRCActionBottomSheet.DismissListener() {
                    @Override
                    public void onDismiss(String type) {
                        if (type.equalsIgnoreCase("CANCEL")) {
                            RRCHandler rrcHandler = new RRCHandler((Activity) context);
                            rrcHandler.setRrcHandlerListner(new RRCHandler.RRCHandlerListner() {
                                @Override
                                public void onSuccessRequest() {

                                }

                                @Override
                                public void onSuccessCancel() {
                                    Toast.makeText(context, "Successfully cancelled request", Toast.LENGTH_SHORT).show();
                                }
                            });
                            rrcHandler.callRRCCancel(requestItem);
                        } else if (type.equalsIgnoreCase("UPDATE")) {
                            Bundle bundle = new Bundle();
                            bundle.putString("order_id", requestItem.getOrder());
                            bundle.putSerializable("data", requestItem);
                            Fragment_RRC_OrderItemSelection fragment_rrc_orderItemSelection = new Fragment_RRC_OrderItemSelection();
                            fragment_rrc_orderItemSelection.setArguments(bundle);
                            if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.REPLACEMENT) {
                                Application_Singleton.CONTAINER_TITLE = "Edit items of replacement";
                            } else if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.CANCELLATION) {
                                Application_Singleton.CONTAINER_TITLE = "Edit items of cancellation";
                            } else if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.RETURN) {
                                Application_Singleton.CONTAINER_TITLE = "Edit items of return";
                            }
                            Application_Singleton.CONTAINERFRAG = fragment_rrc_orderItemSelection;
                            context.startActivity(new Intent(context, OpenContainer.class));
                        }
                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    public class RRCRequestViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_request)
        CardView card_request;

        @BindView(R.id.txt_order_no)
        TextView txt_order_no;

        @BindView(R.id.txt_request_date)
        TextView txt_request_date;

        @BindView(R.id.prod_img)
        SimpleDraweeView prod_img;

        @BindView(R.id.txt_order_value)
        TextView txt_order_value;

        @BindView(R.id.txt_request_status)
        TextView txt_request_status;

        @BindView(R.id.txt_update_request)
        TextView txt_update_request;

        @BindView(R.id.txt_order_number)
        TextView txt_order_number;

        @BindView(R.id.txt_request_label)
        TextView txt_request_label;

        @BindView(R.id.linear_request_reason)
        LinearLayout linear_request_reason;

        @BindView(R.id.txt_request_reason_value)
        TextView txt_request_reason_value;

        @BindView(R.id.linear_rejected_reason)
        LinearLayout linear_rejected_reason;

        @BindView(R.id.txt_rejected_reason_value)
        TextView txt_rejected_reason_value;

        @BindView(R.id.txt_view_image)
        TextView txt_view_image;


        public RRCRequestViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
