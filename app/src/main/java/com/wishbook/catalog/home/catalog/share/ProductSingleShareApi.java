package com.wishbook.catalog.home.catalog.share;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.ShareImageDownloadUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.home.models.ProductObj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;

public class ProductSingleShareApi {

    Context context;
    String product_id;
    Response_catalog response_catalog;
    StaticFunctions.SHARETYPE sharetype;

    public ProductSingleShareApi(Context context, String product_id, StaticFunctions.SHARETYPE sharetype, Response_catalog response_catalog) {
        this.context = context;
        this.product_id = product_id;
        this.response_catalog = response_catalog;
        this.sharetype = sharetype;

        if (response_catalog == null) {
            getCatalogData(context, product_id);
        } else {
            ProductObj[] productObjs = new ProductObj[1];
            productObjs[0] = new ProductObj(response_catalog.getId(), response_catalog.getSingle_piece_price_range());
            productObjs[0].setImage(response_catalog.getImage());
            downloadAndCopyDetails(context, productObjs, sharetype, response_catalog.getCatalog_title());
        }
    }


    public void getCatalogData(final Context context, String product_id) {
        try {
            final MaterialDialog progress_dialog = StaticFunctions.showProgress(context);
            progress_dialog.show();
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context, "catalogs_expand_true_id", product_id), null, headers, true, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override

                public void onServerResponse(String response, boolean dataUpdated) {
                    if (progress_dialog != null) {
                        progress_dialog.dismiss();
                    }
                    try {
                        response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                        ProductObj[] productObjs = new ProductObj[1];
                        productObjs[0] = new ProductObj(response_catalog.getId(), response_catalog.getSingle_piece_price_range());
                        productObjs[0].setImage(response_catalog.getImage());
                        downloadAndCopyDetails(context, productObjs, sharetype, response_catalog.getCatalog_title());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    if (progress_dialog != null) {
                        progress_dialog.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void downloadAndCopyDetails(Context context, ProductObj[] productObjs, StaticFunctions.SHARETYPE sharetype, String catalogname) {
        try {
            if (productObjs != null && productObjs.length > 0) {
                try {
                    ShareImageDownloadUtils.shareProducts((AppCompatActivity) context, null,productObjs,
                            sharetype, "", catalogname, false,false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (sharetype != null && sharetype != StaticFunctions.SHARETYPE.GALLERY)
                    copyProductDetail(context, true);
            } else {
                Toast.makeText(context, "No product to share", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String copyProductDetail(Context context, boolean isCopy) {
        StringBuffer copy_details = new StringBuffer();
        String brand_name = "";
        if (response_catalog.getBrand() != null && response_catalog.getBrand().getName() != null)
            brand_name = response_catalog.getBrand().getName();

        copy_details.append("\u26A1" + "Single piece " + response_catalog.getCategory_name() + " from " + brand_name +  " "+ response_catalog.getCatalog_title() + " Collection" + "\u26A1" + "\n\n");

        ////////
        String id = "";
        id = "*Product ID*: " + (response_catalog.getId()) + "\n";

        copy_details.append(id + "\n");

        /////////////
        String size = "";
        if (response_catalog.getAvailable_sizes() != null && !response_catalog.getAvailable_sizes().isEmpty()) {
            size = "*Available Sizes*: " + (response_catalog.getAvailable_sizes()) + "\n";
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
        if(response_catalog.getEavdata()!=null && response_catalog.getEavdata().getDupatta_length()!=null) {
            length.add("Dupatta Length: " + response_catalog.getEavdata().getDupatta_length());
        }
        if(response_catalog.getEavdata()!=null && response_catalog.getEavdata().getDupatta_width()!=null) {
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
        if (isCopy) {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(copy_details.toString());
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Product Details", copy_details.toString());
                clipboard.setPrimaryClip(clip);
            }
            Toast.makeText(context, "Product description copied to clipboard", Toast.LENGTH_SHORT).show();
        }
        return copy_details.toString();

    }

}
