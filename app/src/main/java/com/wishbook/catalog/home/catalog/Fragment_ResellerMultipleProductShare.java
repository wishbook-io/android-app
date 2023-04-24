package com.wishbook.catalog.home.catalog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.ShareImageDownloadUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.widget.SimpleTextWatcher;
import com.wishbook.catalog.commonmodels.RequestProductBatchUpdate;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.ResponseProductShare;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.catalog.adapter.ResellerMultipleProductShareAdapter;
import com.wishbook.catalog.home.catalog.fbshare.Fragment_FBPostText;
import com.wishbook.catalog.home.catalog.share.Fragment_WhatsAppSelection;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.models.ThumbnailObj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_ResellerMultipleProductShare extends GATrackedFragment implements ShareImageDownloadUtils.DownloadImageTaskCompleteListener {

    @BindView(R.id.linear_toolbar)
    LinearLayout linear_toolbar;

    @BindView(R.id.linear_common_margin)
    LinearLayout linear_common_margin;

    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.recyclerview_share_items)
    RecyclerView mRecyclerView;

    @BindView(R.id.relative_progress)
    RelativeLayout relativeProgress;

    @BindView(R.id.relative_empty)
    RelativeLayout relative_empty;

    @BindView(R.id.rg_common_price)
    RadioGroup rg_common_price;

    @BindView(R.id.radio_common_price)
    RadioButton radio_common_price;

    @BindView(R.id.radio_common_per)
    RadioButton radio_common_per;

    @BindView(R.id.edit_common_add_margin)
    EditText edit_common_add_margin;

    @BindView(R.id.btn_share)
    TextView btn_share;

    View view;


    public static String TAG = Fragment_ResellerMultipleProductShare.class.getSimpleName();

    ResellerMultipleProductShareAdapter resellerMultipleProductShareAdapter;

    ArrayList<Response_catalogMini> selected_response_catalogmini;

    private Context mContext;

    StringBuffer multiple_product;
    int multiple_product_fetch_counter = 0;

    StaticFunctions.SHARETYPE sharetype;

    CallbackListener callbackListener;

    MaterialDialog progressDialog;

    ArrayList<String> product_b2c_urls;

    CountDownTimer countDownTimerExpand;

    boolean isAllDownloadComplete = false;

    String from = "Product List Multi Share";


    public Fragment_ResellerMultipleProductShare() {

    }

    public static Fragment_ResellerMultipleProductShare newInstance(Bundle bundle) {
        Fragment_ResellerMultipleProductShare f = new Fragment_ResellerMultipleProductShare();
        if (bundle != null) {
            f.setArguments(bundle);
        }
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.reseller_multiple_product_share_bottom_sheet, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, v);
        initView();
        initListener();
        ShareImageDownloadUtils shareImageDownloadUtils = new ShareImageDownloadUtils(mContext);
        shareImageDownloadUtils.setDownloadImageTaskCompleteListener(this);
        Application_Singleton.getInstance().trackScreenView("Fragment_ResellerMultipleProductShare",getActivity());
        return v;
    }


    public void initView() {
        if (mContext instanceof OpenContainer) {
            ((OpenContainer) mContext).toolbar.setVisibility(View.GONE);
        }
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        if (getArguments() != null && getArguments().getSerializable("data") != null) {
            selected_response_catalogmini = new ArrayList<>();
            selected_response_catalogmini.addAll((ArrayList<Response_catalogMini>) getArguments().getSerializable("data"));

            resellerMultipleProductShareAdapter = new ResellerMultipleProductShareAdapter(getActivity(), selected_response_catalogmini, true);
            mRecyclerView.setAdapter(resellerMultipleProductShareAdapter);
        }

        // changes given WB-4985
        if (selected_response_catalogmini != null && selected_response_catalogmini.size() > 1) {
            linear_common_margin.setVisibility(View.VISIBLE);
        } else {
            linear_common_margin.setVisibility(View.GONE);
        }

        if (getArguments() != null && getArguments().getSerializable("share_type") != null) {
            sharetype = (StaticFunctions.SHARETYPE) getArguments().getSerializable("share_type");
            if (sharetype == StaticFunctions.SHARETYPE.WHATSAPP) {
                btn_share.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.whatsapp_color_code));
            } else {
                btn_share.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_primary));
            }
        }
    }

    private void initListener() {
        radio_common_per.setChecked(true);
        rg_common_price.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (radio_common_per.isChecked()) {
                    if (resellerMultipleProductShareAdapter != null) {
                        resellerMultipleProductShareAdapter.updateResaleAmount("per", edit_common_add_margin.getText().toString());
                    }
                } else {
                    if (resellerMultipleProductShareAdapter != null) {
                        resellerMultipleProductShareAdapter.updateResaleAmount("price", edit_common_add_margin.getText().toString());
                    }
                }
            }
        });
        edit_common_add_margin.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (radio_common_per.isChecked()) {
                    if (resellerMultipleProductShareAdapter != null) {
                        resellerMultipleProductShareAdapter.updateResaleAmount("per", edit_common_add_margin.getText().toString());
                    }
                } else {
                    if (resellerMultipleProductShareAdapter != null) {
                        resellerMultipleProductShareAdapter.updateResaleAmount("price", edit_common_add_margin.getText().toString());
                    }
                }
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharetype == StaticFunctions.SHARETYPE.WHATSAPP) {
                    if (!StaticFunctions.isWhatsappInstalled(getActivity())) {
                        Toast.makeText(mContext, "Whatsapp is not installed on your device", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if (checkIsResalePriceAvailable())
                    copyMultipleProductDetails();

            }
        });

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }

    private boolean checkIsResalePriceAvailable() {
        if (selected_response_catalogmini != null) {
            for (int i = 0; i < selected_response_catalogmini.size(); i++) {
                if (selected_response_catalogmini.get(i).getResale_price() == null || selected_response_catalogmini.get(i).getResale_price().isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter resale price ", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    if (Double.parseDouble(selected_response_catalogmini.get(i).getResale_price()) <= (selected_response_catalogmini.get(i).getPrice_per_design_with_gst() + selected_response_catalogmini.get(i).getShipping_charges())) {
                        Toast.makeText(getActivity(), "Resale price should be greater than product price", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    if (Double.parseDouble(selected_response_catalogmini.get(i).getResale_price()) > ((selected_response_catalogmini.get(i).getPrice_per_design_with_gst() + selected_response_catalogmini.get(i).getShipping_charges()) * 3)) {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.resell_amount_error_3x_item), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }


    private void postShare(Context context, ArrayList<ProductObj> productObjs, StaticFunctions.SHARETYPE mode, String resellprice, String actualPrice) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        RequestProductBatchUpdate requestProductBatchUpdate = new RequestProductBatchUpdate();
        ArrayList<ProductObj> shaProductObjArrayList = new ArrayList<>();
        for (ProductObj p :
                productObjs) {
            ProductObj productObj = new ProductObj();
            productObj.setProduct_id(p.getId());
            productObj.setResell_price(resellprice);
            productObj.setShared_on(mode.name());
            productObj.setSell_full_catalog(false);
            productObj.setActual_price(actualPrice);
            shaProductObjArrayList.add(productObj);
        }
        requestProductBatchUpdate.setProducts(shaProductObjArrayList);

        HttpManager.getInstance((Activity) context).requestwithObject(HttpManager.METHOD.POSTJSONOBJECT, URLConstants.companyUrl(context, "product-share/start-sharing", ""), Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(requestProductBatchUpdate), JsonObject.class), headers, false, new HttpManager.customCallBack() {
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


    public void copyMultipleProductDetails() {
        multiple_product = new StringBuffer();
        if (selected_response_catalogmini != null && selected_response_catalogmini.size() > 0) {
            for (int i = 0; i < selected_response_catalogmini.size(); i++) {
                double actual_price = selected_response_catalogmini.get(i).getPrice_per_design_with_gst() + selected_response_catalogmini.get(i).getShipping_charges();
                callProductsExpandDetails(mContext, selected_response_catalogmini.get(i).getId(), selected_response_catalogmini.get(i).getResale_price());
                ArrayList<ProductObj> list = new ArrayList<>();
                ThumbnailObj temp = new ThumbnailObj(selected_response_catalogmini.get(i).getImage().getFull_size(),
                        selected_response_catalogmini.get(i).getImage().getThumbnail_medium(),
                        selected_response_catalogmini.get(i).getImage().getThumbnail_small());
                ProductObj productObj = new ProductObj(selected_response_catalogmini.get(i).getId(),
                        selected_response_catalogmini.get(i).getTitle(),
                        selected_response_catalogmini.get(i).getTitle(),
                        null,
                        null, null, temp,
                        String.valueOf(actual_price),
                        selected_response_catalogmini.get(i).getPrice_range(), selected_response_catalogmini.get(i).getPrice_range(), null, null, null);
                productObj.setChecked(true);
                list.add(productObj);

                postShare(mContext, list,
                        sharetype, selected_response_catalogmini.get(i).getResale_price(),
                        String.valueOf(actual_price));

            }
            if (sharetype == StaticFunctions.SHARETYPE.FBPAGE) {
                isAllDownloadComplete = true;
            } else {
                shareType(sharetype);
            }
        }
    }

    public void shareType(StaticFunctions.SHARETYPE sharetype) {
        if (selected_response_catalogmini != null && selected_response_catalogmini.size() > 0) {
            ProductObj[] productObjs = new ProductObj[selected_response_catalogmini.size()];
            for (int i = 0; i < selected_response_catalogmini.size(); i++) {
                ThumbnailObj thumbnailObj = new ThumbnailObj(selected_response_catalogmini.get(i).getImage().getThumbnail_medium(), selected_response_catalogmini.get(i).getImage().getThumbnail_medium(), selected_response_catalogmini.get(i).getImage().getThumbnail_medium());
                productObjs[i] = new ProductObj(selected_response_catalogmini.get(i).getId(), selected_response_catalogmini.get(i).getSingle_piece_price_range());
                productObjs[i].setImage(thumbnailObj);
            }
            downloadShareImage(mContext, productObjs, sharetype, "Single Catalog");
        }

    }

    void downloadShareImage(Context context, ProductObj[] productObjs, StaticFunctions.SHARETYPE sharetype, String catalogname) {
        try {
            if (productObjs != null && productObjs.length > 0) {
                if (sharetype == StaticFunctions.SHARETYPE.WHATSAPP && productObjs.length > 30) {
                    Fragment_WhatsAppSelection frag = new Fragment_WhatsAppSelection();
                    Application_Singleton.CONTAINER_TITLE = "WhatsApp (Pick 30 images to share)";
                    Bundle bundle = new Bundle();
                    bundle.putString("sharetext", "");
                    bundle.putString("type", "whatsapp");
                    frag.setArguments(bundle);
                    Application_Singleton.CONTAINERFRAG = frag;
                    Intent intent = new Intent(context, OpenContainer.class);
                    intent.getIntExtra("toolbarCategory", OpenContainer.CATALOGSHARE);
                    context.startActivity(intent);
                    return;
                }
                try {
                    ShareImageDownloadUtils.shareProducts((AppCompatActivity) context, Fragment_ResellerMultipleProductShare.this,
                            productObjs, sharetype, "", catalogname, true, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "No product to share", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void callProductsExpandDetails(final Context context, String product_id, final String resalePrice) {
        final HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        String url;
        url = URLConstants.companyUrl(context, "catalogs_expand_true_id", product_id);
        HashMap<String, String> param = new HashMap<>();
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog = StaticFunctions.showProgress(context);
            progressDialog.setContent("Fetching content..");
            progressDialog.show();
        }
        HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, url, param, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                multiple_product_fetch_counter++;
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Response_catalog response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                try {
                    try {
                        sendProductShareAnalysis(context, response_catalog, resalePrice, sharetype.name(), false);
                        multiple_product.append(createStringForCopyDetails(context, false, response_catalog, resalePrice) + "\n\n-------------\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (multiple_product_fetch_counter >= selected_response_catalogmini.size()) {
                        try {
                            int sdk = android.os.Build.VERSION.SDK_INT;
                            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                clipboard.setText(multiple_product.toString());
                            } else {
                                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                android.content.ClipData clip = android.content.ClipData.newPlainText("Product Details", multiple_product.toString());
                                clipboard.setPrimaryClip(clip);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (multiple_product_fetch_counter >= selected_response_catalogmini.size() && isAllDownloadComplete) {
                            finishActivity();
                        }

                        Toast.makeText(context, "Products description copied to clipboard", Toast.LENGTH_LONG).show();
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                multiple_product_fetch_counter++;
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    public String createStringForCopyDetails(Context context, boolean isCopy, Response_catalog response_catalog, String resaleprice) {
        StringBuffer copy_details = new StringBuffer();
        String brand_name = "";
        if (response_catalog.getBrand() != null && response_catalog.getBrand().getName() != null)
            brand_name = response_catalog.getBrand().getName();

        copy_details.append("\u26A1" + "Single piece " + response_catalog.getCategory_name() + " from " + brand_name + response_catalog.getCatalog_title() + " Collection" + "\u26A1" + "\n\n");

        ////////
        String id = "";
        id = "*Product ID*: " + (response_catalog.getId()) + "\n";

        copy_details.append(id + "\n");

        String price = "";
        price = "*Price*: " + resaleprice + "/Pc.";
        if (!price.isEmpty()) {
            copy_details.append(price + "\n");
        }

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
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(copy_details.toString());
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Product Details", copy_details.toString());
                clipboard.setPrimaryClip(clip);
            }
        }


        return copy_details.toString();
    }


    public interface CallbackListener {
        void onSuccess();

        void onFailure();
    }


    public void setCallbackListener(CallbackListener callbackListener) {
        this.callbackListener = callbackListener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        try {
            if (countDownTimerExpand != null) {
                countDownTimerExpand.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: Request Code====>" + requestCode + "\n Result Code==>" + resultCode);
        try {
            if (requestCode == 5001) {
                if (ShareImageDownloadUtils.last_share_type != null && ShareImageDownloadUtils.last_share_type == StaticFunctions.SHARETYPE.WHATSAPP_BUSINESS ||
                        ShareImageDownloadUtils.last_share_type == StaticFunctions.SHARETYPE.WHATSAPP) {
                    ShareImageDownloadUtils.setStepTwoCounter(getActivity(), multiple_product.toString());
                    int count_to = 2000;
                    CountDownTimer countDownTimer = new CountDownTimer(count_to, 10) {
                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {
                            if (getActivity() != null) {
                                if (callbackListener != null) {
                                    callbackListener.onSuccess();
                                    getActivity().finish();
                                }
                            }
                        }
                    }.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDownloadImageSuccess() {
        isAllDownloadComplete = true;
        if (multiple_product_fetch_counter >= selected_response_catalogmini.size() && isAllDownloadComplete) {
            finishActivity();
        }
    }

    @Override
    public void onDownloadImageFailure() {

    }

    public void finishActivity() {
        if (sharetype != StaticFunctions.SHARETYPE.WHATSAPP && sharetype != StaticFunctions.SHARETYPE.WHATSAPP_BUSINESS) {
            try {
                int count_to = 1000;
                countDownTimerExpand = new CountDownTimer(count_to, 10) {
                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        if (getActivity() != null) {
                            if (callbackListener != null) {
                                callbackListener.onSuccess();
                                observableCallAPI(sharetype);
                                getActivity().finish();
                            }
                        }
                    }
                }.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // #################### Send Wishbook Analysis Data #####################################//

    public void sendProductShareAnalysis(Context context, Response_catalog response_catalog,
                                         String resale_price, String share_type, boolean isFullCatalogShare) {
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
        prop.put("share_price", resale_price);
        prop.put("share_type", share_type);
        prop.put("is_full_catalog_share", String.valueOf(isFullCatalogShare));
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(getActivity(), wishbookEvent);
    }

    public void observableCallAPI(StaticFunctions.SHARETYPE sharetype) {


        if (progressDialog != null) {
            progressDialog.dismiss();
        }


        if (sharetype == StaticFunctions.SHARETYPE.FBPAGE) {
            ArrayList<String> photos = new ArrayList<>();
            if (selected_response_catalogmini != null && selected_response_catalogmini.size() > 0) {
                ProductObj[] productObjs = new ProductObj[selected_response_catalogmini.size()];
                for (int i = 0; i < selected_response_catalogmini.size(); i++) {
                    ThumbnailObj thumbnailObj = new ThumbnailObj(selected_response_catalogmini.get(i).getImage().getThumbnail_medium(), selected_response_catalogmini.get(i).getImage().getThumbnail_medium(), selected_response_catalogmini.get(i).getImage().getThumbnail_medium());
                    productObjs[i] = new ProductObj(selected_response_catalogmini.get(i).getId(), selected_response_catalogmini.get(i).getSingle_piece_price_range());
                    productObjs[i].setImage(thumbnailObj);
                    photos.add(thumbnailObj.getThumbnail_medium());
                }
            }
            Bundle bundle = new Bundle();
            bundle.putString("data", multiple_product.toString());
            bundle.putStringArrayList("images", photos);
            bundle.putString("name", "Single piece " + selected_response_catalogmini.get(0).getCategory_name() + " from " + selected_response_catalogmini.get(0).getCatalog_title() + " Collection");
            Fragment_FBPostText fragment_fbPostText = new Fragment_FBPostText();
            fragment_fbPostText.setArguments(bundle);
            Application_Singleton.CONTAINER_TITLE = "Post on Facebook";
            Application_Singleton.CONTAINERFRAG = fragment_fbPostText;
            StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
        }

    }


}
