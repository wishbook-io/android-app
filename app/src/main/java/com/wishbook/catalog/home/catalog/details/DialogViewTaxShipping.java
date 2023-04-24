package com.wishbook.catalog.home.catalog.details;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.ShareImageDownloadUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.CompanyGroupFlag;
import com.wishbook.catalog.commonmodels.RequestProductBatchUpdate;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.ResponseProductShare;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.home.catalog.Fragment_BrowseCatalogs;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.models.ThumbnailObj;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.facebook.FacebookSdk.getApplicationContext;

public class DialogViewTaxShipping extends DialogFragment {


    @BindView(R.id.txt_full_catalog_label)
    TextView txt_full_catalog_label;

    @BindView(R.id.txt_single_piece_catalog_label)
    TextView txt_single_piece_catalog_label;

    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.gst_label)
    TextView gst_label;

    @BindView(R.id.txt_catalog_name_label)
    TextView txt_catalog_name_label;

    @BindView(R.id.txt_catalog_name_value)
    TextView txt_catalog_name_value;

    @BindView(R.id.txt_single_price)
    TextView txt_single_price;

    @BindView(R.id.txt_full_price)
    TextView txt_full_price;

    @BindView(R.id.txt_single_tax)
    TextView txt_single_tax;

    @BindView(R.id.txt_full_tax)
    TextView txt_full_tax;

    @BindView(R.id.txt_single_shipping)
    TextView txt_single_shipping;

    @BindView(R.id.txt_full_shipping)
    TextView txt_full_shipping;

    @BindView(R.id.txt_full_total)
    TextView txt_full_total;

    @BindView(R.id.txt_single_total)
    TextView txt_single_total;

    @BindView(R.id.txt_resell_price)
    TextView txt_resell_price;

    @BindView(R.id.txt_resell_price_label)
    TextView txt_resell_price_label;

    @BindView(R.id.txt_margin)
    TextView txt_margin;

    @BindView(R.id.relative_progress)
    RelativeLayout relativeProgress;

    @BindView(R.id.linear_whatsapp_share)
    LinearLayout linear_whatsapp_share;

    @BindView(R.id.txt_suggested_note)
    TextView txt_suggested_note;

    @BindView(R.id.linear_suggested_price)
    LinearLayout linear_suggested_price;

    @BindView(R.id.linear_margin)
    LinearLayout linear_margin;

    @BindView(R.id.txt_cod_note)
    TextView txt_cod_note;


    Response_catalog response_catalog;
    double price;
    boolean isSinglePcAvailable = false;
    double suggested_resell_price = 0;
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    ArrayList<String> product_b2c_urls;
    boolean isReseller;
    String from = "Tax Shipping Dialog";


    public DialogViewTaxShipping() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_view_tax_shipping_charges);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
        ButterKnife.bind(this, dialog);

        initListener();
        setData();
        ShareImageDownloadUtils.last_share_type = null;
        ShareImageDownloadUtils.whatsapp_count = 0;
        CompanyGroupFlag companyGroupFlag = Application_Singleton.gson.fromJson(UserInfo.getInstance(getActivity()).getCompanyGroupFlag(), CompanyGroupFlag.class);

        if (companyGroupFlag != null && (companyGroupFlag.getOnline_retailer_reseller())) {
            isReseller = true;
            showResellerView();
        } else {
            hideResellerView();
        }

        return dialog;

    }


    public void initListener() {

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        linear_whatsapp_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (response_catalog != null) {
                        boolean whatsappInstalled = StaticFunctions.isWhatsappInstalled(getActivity());
                        boolean whatsappBusinessInstalled = StaticFunctions.isWhatsappBusinessInstalled(getActivity());
                        if (whatsappInstalled || whatsappBusinessInstalled) {
                            StaticFunctions.SHARETYPE sharetype = StaticFunctions.SHARETYPE.OTHER;
                            if (whatsappInstalled) {
                                sharetype = StaticFunctions.SHARETYPE.WHATSAPP;
                            } else if (whatsappBusinessInstalled) {
                                sharetype = StaticFunctions.SHARETYPE.WHATSAPP_BUSINESS;
                            }
                            double single_resell_price = suggested_resell_price;
                            if (isReseller) {
                                ProductObj[] shareProductObj;
                                if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                                    ArrayList<ProductObj> list = new ArrayList<>();
                                    for (int i = 0; i < response_catalog.getPhotos().size(); i++) {
                                        ThumbnailObj temp = new ThumbnailObj(response_catalog.getPhotos().get(i).getImage().getFull_size(), response_catalog.getPhotos().get(i).getImage().getThumbnail_medium(), response_catalog.getPhotos().get(i).getImage().getThumbnail_small());
                                        ProductObj productObj = new ProductObj(response_catalog.getId(),
                                                response_catalog.getTitle(),
                                                response_catalog.getTitle(),
                                                null,
                                                null, null, temp, response_catalog.getPrice_range(), response_catalog.getPrice_range(), response_catalog.getPrice_range(), null, null, null);
                                        productObj.setChecked(true);
                                        list.add(productObj);
                                    }
                                    shareProductObj = list.toArray(new ProductObj[list.size()]);
                                    single_resell_price = suggested_resell_price / list.size();
                                } else if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SINGLE)) {
                                    ArrayList<ProductObj> list = new ArrayList<>();
                                    ThumbnailObj temp = new ThumbnailObj(response_catalog.getImage().getFull_size(), response_catalog.getImage().getThumbnail_medium(), response_catalog.getImage().getThumbnail_small());
                                    ProductObj productObj = new ProductObj(response_catalog.getId(),
                                            response_catalog.getTitle(),
                                            response_catalog.getTitle(),
                                            null,
                                            null, null, temp, response_catalog.getPrice_range(), response_catalog.getPrice_range(), response_catalog.getPrice_range(), null, null, null);
                                    productObj.setChecked(true);
                                    list.add(productObj);
                                    shareProductObj = list.toArray(new ProductObj[list.size()]);
                                } else {
                                    shareProductObj = response_catalog.getProduct();
                                    if (response_catalog.full_catalog_orders_only.equalsIgnoreCase("true")) {
                                        single_resell_price = suggested_resell_price / response_catalog.getProduct().length;
                                    } else {
                                        single_resell_price = suggested_resell_price;
                                    }
                                }
                                if (isSinglePcAvailable) {
                                    postShare(getActivity(), shareProductObj, sharetype, single_resell_price);
                                } else {
                                    postScreenShare(getActivity(), response_catalog, sharetype, single_resell_price);
                                }
                            } else {
                                startSharing(sharetype);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Whatsapp is not installed on your phone", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }


    public void setData() {
        getCatalogData(getActivity(), getArguments().getString("product_id"));
    }


    public void getCatalogData(Context context, String responseCatalogID) {
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context, "catalogs_expand_true_id", responseCatalogID), null, headers, true, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override

                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    try {
                        if (isAdded() && !isDetached()) {
                            try {
                                response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                                txt_catalog_name_value.setText(response_catalog.getCatalog_title());

                                if (response_catalog != null) {
                                    if (response_catalog.full_catalog_orders_only != null && response_catalog.full_catalog_orders_only.equals("true")) {
                                        isSinglePcAvailable = false;
                                    } else {
                                        isSinglePcAvailable = true;
                                    }
                                    if (isSinglePcAvailable) {
                                        txt_single_piece_catalog_label.setText("Single Piece\n(1 Design)");
                                    }

                                    if (response_catalog.getProduct_type() != null && !response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN) && !response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SINGLE)) {
                                        // For Catalog and Non-Catalog
                                        txt_full_catalog_label.setText("Full Catalog\n" + "(" + response_catalog.getProduct().length + " Designs)");
                                        setPrice(response_catalog);
                                    } else if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SINGLE)) {
                                        ArrayList<ProductObj> list = new ArrayList<>();
                                        ThumbnailObj temp = new ThumbnailObj(response_catalog.getImage().getFull_size(), response_catalog.getImage().getThumbnail_medium(), response_catalog.getImage().getThumbnail_small());
                                        ProductObj productObj = new ProductObj(response_catalog.getId(),
                                                response_catalog.getTitle(),
                                                response_catalog.getTitle(),
                                                null,
                                                null, null, temp, response_catalog.getPrice_range(), response_catalog.getPrice_range(), response_catalog.getPrice_range(), null, null, null);
                                        productObj.setChecked(true);
                                        list.add(productObj);
                                        setPrice(response_catalog);
                                    } else {
                                        // For Set-matching
                                        txt_catalog_name_label.setText("Set Name: ");
                                        ArrayList<ProductObj> list = new ArrayList<>();
                                        String set = "";
                                        if (response_catalog.getCatalog_multi_set_type() != null && response_catalog.getCatalog_multi_set_type().equalsIgnoreCase("color_set")) {
                                            set = "Color";
                                            if (Integer.parseInt(response_catalog.getNo_of_pcs_per_design()) == 1) {
                                                set = "Color";
                                            } else {
                                                set = "Colors";
                                            }
                                        } else if (response_catalog.getCatalog_multi_set_type() != null && response_catalog.getCatalog_multi_set_type().equalsIgnoreCase("size_set")) {
                                            if (Integer.parseInt(response_catalog.getNo_of_pcs_per_design()) == 1) {
                                                set = "Size";
                                            } else {
                                                set = "Sizes";
                                            }
                                        }
                                        txt_full_catalog_label.setText("Full Set\n" + "(Set of " + response_catalog.getNo_of_pcs_per_design() + " " + set + ")");
                                        if (response_catalog.getPhotos() != null && response_catalog.getPhotos().size() > 0) {
                                            for (int i = 0; i < response_catalog.getPhotos().size(); i++) {
                                                ThumbnailObj temp = new ThumbnailObj(response_catalog.getPhotos().get(i).getImage().getFull_size(), response_catalog.getPhotos().get(i).getImage().getThumbnail_medium(), response_catalog.getPhotos().get(i).getImage().getThumbnail_small());
                                                ProductObj productObj = new ProductObj(response_catalog.getId(),
                                                        response_catalog.getTitle(),
                                                        response_catalog.getTitle(),
                                                        null,
                                                        null, null, temp, response_catalog.getPrice_range(), response_catalog.getPrice_range(), response_catalog.getPrice_range(), null, null, null);
                                                productObj.setChecked(true);
                                                list.add(productObj);
                                            }
                                            setPrice(response_catalog);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showProgress() {
        if (relativeProgress != null) {
            relativeProgress.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress() {
        if (relativeProgress != null) {
            relativeProgress.setVisibility(View.GONE);
        }
    }

    public void showResellerView() {
        linear_margin.setVisibility(View.VISIBLE);
        linear_suggested_price.setVisibility(View.VISIBLE);
        linear_whatsapp_share.setVisibility(View.VISIBLE);
        txt_suggested_note.setVisibility(View.VISIBLE);
        txt_cod_note.setText(getActivity().getResources().getString(R.string.cod_note2));
    }

    public void hideResellerView() {

        linear_margin.setVisibility(View.GONE);
        linear_suggested_price.setVisibility(View.GONE);
        linear_whatsapp_share.setVisibility(View.GONE);
        txt_suggested_note.setVisibility(View.GONE);
        txt_cod_note.setText(getActivity().getResources().getString(R.string.cod_note2_retailer));
    }

    public void setPrice(Response_catalog response_catalog) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        if (response_catalog.getProduct_type() != null && !response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN) && !response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SINGLE)) {
            if (response_catalog.getFull_catalog_orders_only() != null && response_catalog.getFull_catalog_orders_only().equalsIgnoreCase("true")) {
                double full_catalog_price = 0.0;
                for (int i = 0; i < response_catalog.getProduct().length; i++) {
                    full_catalog_price += Double.parseDouble(response_catalog.getProduct()[i].getPublic_price_with_gst());
                }

                full_catalog_price += Double.parseDouble(response_catalog.getShipping_charges());
                double avg_price = 0.0;
                avg_price = full_catalog_price / response_catalog.getProduct().length;
                price = avg_price;
                double avg_tax = 0.0;
                double public_catalog_price = 0.0;
                double public_catalog_price_gst = 0.0;
                for (int i = 0; i < response_catalog.getProduct().length; i++) {
                    public_catalog_price += Double.parseDouble(response_catalog.getProduct()[i].getFinal_price());
                    public_catalog_price_gst += Double.parseDouble(response_catalog.getProduct()[i].getPublic_price_with_gst());
                }
                avg_tax = (public_catalog_price_gst / response_catalog.getProduct().length) - (public_catalog_price / response_catalog.getProduct().length);


                txt_single_price.setText("N/A");
                txt_single_shipping.setText("N/A");
                txt_single_tax.setText("N/A");
                txt_single_total.setText("N/A");

                txt_full_price.setText("\u20B9" + decimalFormat.format(public_catalog_price));
                txt_full_tax.setText("\u20B9" + (decimalFormat.format(public_catalog_price_gst - public_catalog_price)));
                txt_full_shipping.setText("\u20B9" + response_catalog.getSurface_shipping_charges());

                txt_full_total.setText("\u20B9" + decimalFormat.format((public_catalog_price_gst + response_catalog.getSurface_shipping_charges())));

                double gst_per = (100 * ((public_catalog_price_gst - public_catalog_price) / public_catalog_price));
                gst_label.setText("GST" + "(" + (int) Math.round(gst_per) + "%)");

                setSuggestedResellPrice(public_catalog_price_gst + response_catalog.getSurface_shipping_charges());
            } else {
                // Avg price calculate
                int selected_product = response_catalog.getProduct().length;

                // start price breakup logic
                double avg_tax = 0.0;
                double single_catalog_price = 0.0;
                double single_catalog_price_with_gst = 0.0;
                double avg_shipping_charge = 0.0;
                double public_full_catalog_price = 0.0;
                double public_full_catalog_price_with_gst = 0.0;
                for (int i = 0; i < response_catalog.getProduct().length; i++) {
                    public_full_catalog_price += Double.parseDouble(response_catalog.getProduct()[i].getPublic_price());
                    public_full_catalog_price_with_gst += Double.parseDouble(response_catalog.getProduct()[i].getPublic_price_with_gst());

                    single_catalog_price += Double.parseDouble(response_catalog.getProduct()[i].getSingle_piece_price());
                    single_catalog_price_with_gst += Double.parseDouble(response_catalog.getProduct()[i].getSingle_piece_price_with_gst());

                    avg_shipping_charge += Double.parseDouble(response_catalog.getProduct()[i].getShipping_charges());
                }
                avg_tax = (single_catalog_price_with_gst / selected_product) - (single_catalog_price / selected_product);
                avg_shipping_charge = avg_shipping_charge / selected_product;

                txt_single_price.setText("\u20B9" + decimalFormat.format((single_catalog_price / selected_product)));
                txt_single_shipping.setText("\u20B9" + decimalFormat.format(avg_shipping_charge));
                txt_single_tax.setText("\u20B9" + decimalFormat.format(avg_tax));
                txt_single_total.setText("\u20B9" + decimalFormat.format(((single_catalog_price / selected_product) + avg_shipping_charge + avg_tax)));

                txt_full_price.setText("\u20B9" + decimalFormat.format(public_full_catalog_price));
                txt_full_tax.setText("\u20B9" + decimalFormat.format((public_full_catalog_price_with_gst - public_full_catalog_price)));
                txt_full_shipping.setText("\u20B9" + response_catalog.getSurface_shipping_charges());
                txt_full_total.setText("\u20B9" + decimalFormat.format((public_full_catalog_price_with_gst + response_catalog.getSurface_shipping_charges())));

                price = (single_catalog_price_with_gst / selected_product) + avg_shipping_charge;
                double gst_per = (100 * ((public_full_catalog_price_with_gst - public_full_catalog_price) / public_full_catalog_price));
                gst_label.setText("GST" + "(" + (int) Math.round(gst_per) + "%)");
                setSuggestedResellPrice((single_catalog_price / selected_product) + avg_shipping_charge + avg_tax);
            }
        } else if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SINGLE)) {
            if (response_catalog.getPrice_per_design_with_gst() != null) {
                double full_set_price = 0.0;
                full_set_price = Double.parseDouble(response_catalog.getPrice_per_design_with_gst()) + Double.parseDouble(response_catalog.getShipping_charges());
                price = full_set_price;
            }

            // start price breakup logic
            double tax = 0.0;
            double single_pc_price = 0.0;
            if (response_catalog.getSingle_piece_price_range().contains("-")) {
                single_pc_price = Double.parseDouble(response_catalog.getSingle_piece_price());
            } else {
                single_pc_price = Double.parseDouble(response_catalog.getSingle_piece_price_range());
            }
            double single_pc_price_gst = Double.parseDouble(response_catalog.getPrice_per_design_with_gst());
            double shipping_charge = Double.parseDouble(response_catalog.getShipping_charges());
            tax = single_pc_price_gst - single_pc_price;

            txt_single_price.setText("\u20B9" + single_pc_price);
            txt_single_shipping.setText("\u20B9" + shipping_charge);
            txt_single_tax.setText("\u20B9" + decimalFormat.format(tax));
            txt_single_total.setText("\u20B9" + decimalFormat.format((single_pc_price + shipping_charge + tax)));

            txt_full_price.setText("N/A");
            txt_full_tax.setText("N/A");
            txt_full_shipping.setText("N/A");
            txt_full_total.setText("N/A");

            double gst_per = (100 * (tax / single_pc_price));
            gst_label.setText("GST" + "(" + (int) Math.round(gst_per) + "%)");
            setSuggestedResellPrice(single_pc_price + shipping_charge + tax);

        } else {
            // Set-matching price set
            if (response_catalog.getPrice_per_design_with_gst() != null) {
                double full_set_price = 0.0;
                full_set_price = (Double.parseDouble(response_catalog.getPrice_per_design_with_gst()) * Integer.parseInt(response_catalog.getNo_of_pcs_per_design()))
                        + Double.parseDouble(response_catalog.getShipping_charges());
                double avg_price = 0.0;
                avg_price = full_set_price / Integer.parseInt(response_catalog.getNo_of_pcs_per_design());
                price = avg_price;
            }

            // start price breakup logic
            double tax = 0.0;
            double price_per_design = Double.parseDouble(response_catalog.getPrice_per_design());
            double price_per_design_gst = Double.parseDouble(response_catalog.getPrice_per_design_with_gst());
            tax = price_per_design_gst - price_per_design;


            txt_single_price.setText("N/A");
            txt_single_shipping.setText("N/A");
            txt_single_tax.setText("N/A");
            txt_single_total.setText("N/A");

            txt_full_price.setText("\u20B9" + decimalFormat.format(price_per_design));
            txt_full_tax.setText("\u20B9" + decimalFormat.format((price_per_design_gst - price_per_design)));
            txt_full_shipping.setText("\u20B9" + response_catalog.getSurface_shipping_charges());
            txt_full_total.setText("\u20B9" + decimalFormat.format(((price_per_design_gst * (Integer.parseInt(response_catalog.getNo_of_pcs_per_design())) + response_catalog.getSurface_shipping_charges()))));
            gst_label.setText("GST");
            setSuggestedResellPrice(price_per_design_gst * (Integer.parseInt(response_catalog.getNo_of_pcs_per_design())) + response_catalog.getSurface_shipping_charges());
        }

    }

    public void setSuggestedResellPrice(double price) {
        try {
            int margin_per = 0;
            if (price <= 500) {
                suggested_resell_price = price + 100;
                margin_per = (int) (100 * (100 / price));
            } else if (price > 500 && price <= 1200) {
                margin_per = 25;
                suggested_resell_price = ((price / 100) * 25) + price;
            } else if (price > 1200 && price <= 2000) {
                margin_per = 20;
                suggested_resell_price = ((price / 100) * 20) + price;
            } else {
                margin_per = 15;
                suggested_resell_price = ((price / 100) * 15) + price;
            }

            if (isSinglePcAvailable) {
                txt_resell_price_label.setText(getActivity().getResources().getString(R.string.suggested_resell_price_pc));
            } else {
                txt_resell_price_label.setText(getActivity().getResources().getString(R.string.suggested_resell_price));
            }
            suggested_resell_price = Math.round(suggested_resell_price);
            double margin_rs = Math.round((suggested_resell_price - price));
            txt_resell_price.setText("\u20B9 " + decimalFormat.format(suggested_resell_price));
            txt_margin.setText("\u20B9 " + decimalFormat.format(margin_rs) + " " + "(" + margin_per + "%)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String copyProductDetail(boolean isCopy, int shareProductLength, String resaleprice) {
        StringBuffer copy_details = new StringBuffer();
        int products = 0;
        if (response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            products = Integer.parseInt(response_catalog.getNo_of_pcs_per_design());
        } else {
            products = response_catalog.getProduct().length;
        }
        if (products > 1) {
            if (response_catalog.getBrand() != null && response_catalog.getBrand().getName() != null) {
                if (!response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                    if (response_catalog.getCatalog_type().equalsIgnoreCase("catalog")) {
                        copy_details.append("\u26A1" + response_catalog.getBrand().getName() + " - " + response_catalog.getTitle() + " " + response_catalog.getCategory_name() + " Collection" + "\u26A1" + "\n\n");
                        copy_details.append("*No of Designs*: " + shareProductLength + "\n");

                    } else if (response_catalog.getCatalog_type().equalsIgnoreCase("noncatalog")) {
                        copy_details.append("*" + response_catalog.getTitle() + " Collection of " + shareProductLength + " " + response_catalog.getCategory_name() + "*" + "\n\n");
                    }
                }

            } else {
                if (response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                    copy_details.append("*" + response_catalog.getTitle() + " Collection of " + shareProductLength + " " + response_catalog.getCategory_name() + "*" + "\n\n");
                }
            }
        } else {
            String categoryname = response_catalog.getCategory_name();
            if (response_catalog.getCategory_name().equalsIgnoreCase("Sarees")
                    || response_catalog.getCategory_name().equalsIgnoreCase("Kurtis")
                    || response_catalog.getCategory_name().equalsIgnoreCase("Dress Materials")) {
                categoryname = categoryname.substring(0, response_catalog.getCategory_name().length() - 1);
            }
            copy_details.append(getResources().getString(R.string.unicode_fire) + " *" + "Hot selling " + categoryname + "* " + getResources().getString(R.string.unicode_fire) + "\n\n");
        }

        ////////
        String id = "";
        if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            id = "*Set ID*: " + (response_catalog.getId()) + "\n";
        } else if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SINGLE)) {
            id = "*Product ID*: " + (response_catalog.getId()) + "\n";
        } else {
            id = "*Catalog ID*: " + (response_catalog.getId()) + "\n";
        }
        copy_details.append(id + "\n");

        String price = "";
        if (response_catalog.getFull_catalog_orders_only().equalsIgnoreCase("true")) {
            price = "*Full Catalog Price*: " + resaleprice;
        } else {
            price = "*Price*: " + resaleprice + "/Pc.";
        }

        if (!price.isEmpty()) {
            copy_details.append(price + "\n");
        }

        /////////////
        String size = "";
        if (response_catalog.full_catalog_orders_only.equals("false")) {
            if (!StaticFunctions.ArrayListToString(getSelectedSizesDistinctEav(response_catalog), StaticFunctions.COMMASEPRATED).isEmpty()) {
                size = "*Available Sizes*: " + (StaticFunctions.ArrayListToString(getSelectedSizesDistinctEav(response_catalog), StaticFunctions.COMMASEPRATED)) + "\n";
            }
        } else {
            if (response_catalog.getAvailable_sizes() != null && !response_catalog.getAvailable_sizes().isEmpty()) {
                size = "*Available Sizes*: " + response_catalog.getAvailable_sizes() + "\n";
            }
        }


        ////////////
        String striching = "";
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getStitching_type() != null) {
            striching = "*Stitching Details*: " + response_catalog.getEavdata().getStitching_type();
        }
        if (!size.isEmpty()) {
            copy_details.append(size + "\n");
        }

        if (!striching.isEmpty()) {
            copy_details.append(striching + "\n");
        }

        String fabric = (StaticFunctions.ArrayListToString(response_catalog.getEavdata().getFabric(), StaticFunctions.COMMASEPRATEDSPACE));
        String work = (StaticFunctions.ArrayListToString(response_catalog.getEavdata().getWork(), StaticFunctions.COMMASEPRATEDSPACE));
        copy_details.append("*Fabric*: " + fabric + "\n");
        copy_details.append("*Work*: " + work + "\n");

        ArrayList<String> length = new ArrayList<>();
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getTop() != null) {
            length.add("Top: " + response_catalog.getEavdata().getTop());
        }
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getBottom() != null) {
            length.add("Bottom: " + response_catalog.getEavdata().getBottom());
        }
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getDupatta() != null) {
            length.add("Dupatta: " + response_catalog.getEavdata().getDupatta());
        }
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getDupatta_length() != null) {
            length.add("Dupatta Length: " + response_catalog.getEavdata().getDupatta_length());
        }
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getDupatta_width() != null) {
            length.add("Dupatta Width: " + response_catalog.getEavdata().getDupatta_width());
        }
        if (length != null && length.size() > 0) {
            copy_details.append("*Lengths*: " + StaticFunctions.ArrayListToString(length, StaticFunctions.COMMASEPRATEDSPACE) + "\n");
        }


        String other_details = "";
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getOther() != null && response_catalog.getEavdata().getOther().length() > 0) {
            other_details = "*Other Details*: " + response_catalog.getEavdata().getOther();
        }
        if (!other_details.isEmpty()) {
            copy_details.append(other_details + "\n");
        }

        copy_details.append("\n\n");
        copy_details.append("*COD Available*" + "\n");
        if (response_catalog.full_catalog_orders_only.equals("false")) {
            copy_details.append("*Single Piece Available*" + "\n");
        }

        if (product_b2c_urls != null && product_b2c_urls.size() > 0) {
            copy_details.append("\n" + "More information visit:\n" + StaticFunctions.ArrayListToString(product_b2c_urls, "\n\n"));
        }

        if (isCopy) {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(copy_details.toString());
                Toast.makeText(getActivity(), "Products description copied to clipboard", Toast.LENGTH_LONG).show();
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Product Details", copy_details.toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), "Products description copied to clipboard", Toast.LENGTH_LONG).show();
            }
        }

        return copy_details.toString();

    }

    public ArrayList<String> getSelectedSizesDistinctEav(Response_catalog response_catalog) {
        if (response_catalog != null) {
            ArrayList<String> catalog_sizes = new ArrayList<>();
            for (int i = 0; i < response_catalog.getProduct().length; i++) {
                if (response_catalog.getProduct()[i].getAvailable_size_string() != null && !response_catalog.getProduct()[i].getAvailable_size_string().isEmpty())
                    //String[] ss = new String{response_catalog.getProduct()[i].getAvailable_size_string().split(",");};
                    catalog_sizes.addAll(Arrays.asList(response_catalog.getProduct()[i].getAvailable_size_string().split(",")));
            }
            ArrayList<String> listFromSet = new ArrayList<String>(new HashSet<String>(catalog_sizes));
            return listFromSet;
        } else {
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5001) {
            if (ShareImageDownloadUtils.last_share_type == StaticFunctions.SHARETYPE.WHATSAPP_BUSINESS ||
                    ShareImageDownloadUtils.last_share_type == StaticFunctions.SHARETYPE.WHATSAPP) {
                int no_of_design = 0;
                if (response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                    no_of_design = response_catalog.getNo_of_pcs();
                } else {
                    no_of_design = response_catalog.getProduct().length;
                }
                ShareImageDownloadUtils.setStepTwoCounter(getActivity(), copyProductDetail(true, no_of_design, String.valueOf(suggested_resell_price)));
            }
        }
    }

    private void startSharing(StaticFunctions.SHARETYPE sharetype) throws IOException {

        if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            ArrayList<ProductObj> list = new ArrayList<>();
            for (int i = 0; i < response_catalog.getPhotos().size(); i++) {
                ThumbnailObj temp = new ThumbnailObj(response_catalog.getPhotos().get(i).getImage().getFull_size(), response_catalog.getPhotos().get(i).getImage().getThumbnail_medium(), response_catalog.getPhotos().get(i).getImage().getThumbnail_small());
                ProductObj productObj = new ProductObj(response_catalog.getId(),
                        response_catalog.getTitle(),
                        response_catalog.getTitle(),
                        null,
                        null, null, temp, response_catalog.getPrice_range(), response_catalog.getPrice_range(), response_catalog.getPrice_range(), null, null, null);
                productObj.setChecked(true);
                list.add(productObj);
            }
            ShareImageDownloadUtils.shareProducts((AppCompatActivity) getActivity(), DialogViewTaxShipping.this,
                    list.toArray(new ProductObj[list.size()]), sharetype, "", response_catalog.getCatalog_title(), false, true);
        } else if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SINGLE)) {
            ArrayList<ProductObj> list = new ArrayList<>();
            ThumbnailObj temp = new ThumbnailObj(response_catalog.getImage().getFull_size(), response_catalog.getImage().getThumbnail_medium(), response_catalog.getImage().getThumbnail_small());
            ProductObj productObj = new ProductObj(response_catalog.getId(),
                    response_catalog.getTitle(),
                    response_catalog.getTitle(),
                    null,
                    null, null, temp, response_catalog.getPrice_range(), response_catalog.getPrice_range(), response_catalog.getPrice_range(), null, null, null);
            productObj.setChecked(true);
            list.add(productObj);
            ShareImageDownloadUtils.shareProducts((AppCompatActivity) getActivity(), DialogViewTaxShipping.this,
                    list.toArray(new ProductObj[list.size()]), sharetype, "", response_catalog.getCatalog_title(), false, true);
        } else {
            ShareImageDownloadUtils.shareProducts((AppCompatActivity) getActivity(), DialogViewTaxShipping.this,
                    response_catalog.getProduct(), sharetype, "", response_catalog.getCatalog_title(), false, true);
        }
    }

    private void postShare(Context context, ProductObj[] productObjs, final StaticFunctions.SHARETYPE mode, double suggested_resell_price) {
        sendProductShareAnalysis(context,response_catalog, String.valueOf(suggested_resell_price),mode.name(),!isSinglePcAvailable);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        RequestProductBatchUpdate requestProductBatchUpdate = new RequestProductBatchUpdate();
        ArrayList<ProductObj> shaProductObjArrayList = new ArrayList<>();
        for (ProductObj p :
                productObjs) {
            ProductObj productObj = new ProductObj();
            productObj.setProduct_id(p.getId());
            productObj.setResell_price(String.valueOf(suggested_resell_price));
            productObj.setShared_on(mode.name());
            productObj.setSell_full_catalog(false);
            productObj.setActual_price(String.valueOf(price));
            shaProductObjArrayList.add(productObj);
        }
        requestProductBatchUpdate.setProducts(shaProductObjArrayList);
        Log.e("TAG", "postShare: =====>" + Application_Singleton.gson.toJson(requestProductBatchUpdate));
        HttpManager.getInstance((Activity) context).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(context, "product-share/start-sharing", ""), Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(requestProductBatchUpdate), JsonObject.class), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    ResponseProductShare productShares = Application_Singleton.gson.fromJson(response, ResponseProductShare.class);
                    if (productShares.getB2c_product_url_list().size() > 0) {
                        product_b2c_urls = productShares.getB2c_product_url_list();
                    }
                    startSharing(mode);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void postScreenShare(Context context, Response_catalog response_catalog, final StaticFunctions.SHARETYPE mode, double suggested_resell_price) {
        sendProductShareAnalysis(context,response_catalog, String.valueOf(suggested_resell_price),mode.name(),!isSinglePcAvailable);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        RequestProductBatchUpdate requestProductBatchUpdate = new RequestProductBatchUpdate();
        ArrayList<ProductObj> shaProductObjArrayList = new ArrayList<>();
        ProductObj productObj = new ProductObj();
        productObj.setProduct_id(response_catalog.getId());
        productObj.setResell_price(String.valueOf(suggested_resell_price));
        productObj.setShared_on(mode.name());
        productObj.setActual_price(String.valueOf(price));
        productObj.setSell_full_catalog(true);
        shaProductObjArrayList.add(productObj);
        requestProductBatchUpdate.setProducts(shaProductObjArrayList);
        Log.e("TAG", "postShare: =====>" + Application_Singleton.gson.toJson(requestProductBatchUpdate));
        HttpManager.getInstance((Activity) context).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(context, "product-share/start-sharing", ""), Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(requestProductBatchUpdate), JsonObject.class), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    ResponseProductShare productShares = Application_Singleton.gson.fromJson(response, ResponseProductShare.class);
                    if (productShares.getB2c_product_url_list().size() > 0) {
                        product_b2c_urls = productShares.getB2c_product_url_list();
                    }
                    startSharing(mode);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }



    // #################### Send Wishbook Analysis Data #####################################//

    public void sendProductShareAnalysis(Context context, Response_catalog response_catalog,
                                         String resale_price, String share_type , boolean isFullCatalogShare) {
        UserInfo userInfo = UserInfo.getInstance(context);
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.PRODUCT_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Product_Share");
        HashMap<String, String> prop = new HashMap<>();
        if (from != null) {
            if (from.equalsIgnoreCase(Fragment_BrowseCatalogs.class.getSimpleName()))
                prop.put("source", "Product Detail");
            else
                prop.put("source", from);
        }

        prop.put("product_type", response_catalog.getProduct_type());
        prop.put("full_catalog", response_catalog.full_catalog_orders_only);
        if (response_catalog.getBrand() != null)
            prop.put("brand", response_catalog.getBrand().getName());
        else
            prop.put("brand", "No Brand");

        if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            prop.put("num_items", String.valueOf(response_catalog.getNo_of_pcs_per_design()));
        } else {
            if (response_catalog.getProduct() != null)
                prop.put("num_items", String.valueOf(response_catalog.getProduct().length));
        }
        prop.put("product_id", response_catalog.getId());
        prop.put("product_name", response_catalog.getTitle());
        if (response_catalog.getImage() != null && response_catalog.getImage().getThumbnail_small() != null)
            prop.put("product_cover_image", response_catalog.getImage().getThumbnail_small());
        if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN))
            prop.put("set_type", response_catalog.getCatalog_multi_set_type());

        if (response_catalog.getPrice_range() != null) {
            prop.put("full_set_price", response_catalog.getPrice_range());
        }


        if (response_catalog.getSingle_piece_price_range() != null)
            prop.put("single_pc_price", response_catalog.getSingle_piece_price());

        if (response_catalog.getCategory_name() != null) {
            prop.put("category", response_catalog.getCategory_name());
        }

        if (response_catalog.getPrice_range() != null) {
            if (response_catalog.getPrice_range().contains("-")) {
                String[] priceRangeMultiple = response_catalog.getPrice_range().split("-");
                prop.put("lowest_price", priceRangeMultiple[0]);
            } else {
                prop.put("lowest_price", response_catalog.getPrice_range());
            }
        }

        prop.put("product_view_count", String.valueOf(StaticFunctions.getProductViewCount(getActivity(), response_catalog.getId())));
        prop.put("share_price",resale_price);
        prop.put("share_type",share_type);
        prop.put("is_full_catalog_share", String.valueOf(isFullCatalogShare));
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(getActivity(), wishbookEvent);
    }
}
