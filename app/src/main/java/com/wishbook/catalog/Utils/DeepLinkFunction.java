package com.wishbook.catalog.Utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.freshchat.consumer.sdk.ConversationOptions;
import com.freshchat.consumer.sdk.FaqOptions;
import com.freshchat.consumer.sdk.Freshchat;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.util.LocaleHelper;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.ResponseCatalogEnquiry;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.commonmodels.responses.Response_sellingorder;
import com.wishbook.catalog.home.Activity_Home;
import com.wishbook.catalog.home.banner.Fragment_BannerWebView;
import com.wishbook.catalog.home.catalog.CatalogHolder;
import com.wishbook.catalog.home.catalog.Fragment_BrandFollowedCatalogs;
import com.wishbook.catalog.home.catalog.Fragment_Brands;
import com.wishbook.catalog.home.catalog.Fragment_BrowseProduct;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;
import com.wishbook.catalog.home.catalog.Fragment_WishList;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;
import com.wishbook.catalog.home.catalog.details.Fragment_ProductsInSelection;
import com.wishbook.catalog.home.contacts.Fragment_ContactsHolder;
import com.wishbook.catalog.home.contacts.add.Fragment_Buyer_Rating;
import com.wishbook.catalog.home.contacts.details.Fragment_BuyerDetails;
import com.wishbook.catalog.home.contacts.details.Fragment_BuyerEnquiryDetails;
import com.wishbook.catalog.home.contacts.details.Fragment_SupplierDetailsNew2;
import com.wishbook.catalog.home.more.Fragment_DiscountSetting;
import com.wishbook.catalog.home.more.Fragment_GST_2;
import com.wishbook.catalog.home.more.Fragment_JoinWhatsappGroup;
import com.wishbook.catalog.home.more.Fragment_Profile;
import com.wishbook.catalog.home.more.creditRating.CreditRatingIntroActivity;
import com.wishbook.catalog.home.more.creditRating.FragmentCreditRating;
import com.wishbook.catalog.home.more.myearning.Fragment_MyEarningHolder;
import com.wishbook.catalog.home.more.referral.BottomReferandEarn;
import com.wishbook.catalog.home.myBusiness.Fragment_MyBusiness;
import com.wishbook.catalog.home.myBusiness.Fragment_SellerHub;
import com.wishbook.catalog.home.myBusiness.Fragment_SellerPayout;
import com.wishbook.catalog.home.notifications.Fragment_Notifications;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;
import com.wishbook.catalog.home.orderNew.details.Activity_OrderDetailsNew;
import com.wishbook.catalog.home.orders.ActivityEnquiryHolder;
import com.wishbook.catalog.home.orders.ActivityOrderHolder;
import com.wishbook.catalog.home.rrc.Fragment_RRCList;
import com.wishbook.catalog.home.rrc.RRCHandler;
import com.wishbook.catalog.home.sellerhub.Fragment_Seller_Pending_Order_item;
import com.wishbook.catalog.reseller.Fragment_ResellerHolder;
import com.wishbook.catalog.rest.CampaignLogApi;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class DeepLinkFunction {

    private Bundle bundle;
    HashMap<String, String> param;
    Context context;
    String from = "Notification";
    private static String TAG = DeepLinkFunction.class.getSimpleName();


    public DeepLinkFunction(HashMap<String, String> param, Context context) {
        this.param = param;
        this.context = context;

        if (param != null && param.containsKey("type")) {
            String type = param.get("type");
            String page = "";
            if (param.containsKey("page")) {
                page = param.get("page");
            }

            if (param.containsKey("from")) {
                from = param.get("from");
            }
            Log.e(TAG, "DeepLinkFunction:===> " + type);
            switch (type) {
                case "register":
                    if (UserInfo.getInstance(context).isGuest()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("from", from);
                        new NavigationUtils().navigateRegisterPage(context, bundle);
                    } else {
                        Toast.makeText(context, "You are already registered user", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case "product":
                case "catalog":
                    getCatalogDeepLink1(this.context, param);
                    break;

                /**
                 * Now we are not creating the enquiry all enquriy create using AppLozic
                 */
              /*  case "received_enquiries":
                    getEnquiryDeepLink(this.context, param);
                    break;

                case "sent_enquiries":
                    getEnquiryDeepLink(this.context, param);
                    break;*/

                case "sales":
                    getOrderDeepLink(context, param);
                    break;

                case "purchase":
                    getOrderDeepLink(context, param);
                    break;

                case "buyer":
                    getContactsDeepLink(context, param);
                    break;

                case "supplier":
                    getContactsDeepLink(context, param);
                    break;

                case "brands":
                    getBrandsDeepLink(context, param);
                    break;

                case "tab":
                    getTabDeeplink(context, param);
                    break;

                case "credit_reference_buyer_requested":
                    getCreditRefBuyerRequested(context, param.get("id"));
                    break;

                case "catalogwise-enquiry-supplier":
                    getCatalogEnquiry(context, param);
                    break;

                case "promotional":
                    String link = extractLinks(page);
                    if (link != null) {
                        try {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(link));
                            context.startActivity(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (context != null) {
                            if (context instanceof OpenContainer && Application_Singleton.CONTAINERFRAG instanceof Fragment_Notifications) {

                                break;
                            }
                        }
                        Fragment_Notifications fragment_notifications = new Fragment_Notifications();
                        Application_Singleton.CONTAINER_TITLE = "Notifications";
                        Application_Singleton.CONTAINERFRAG = fragment_notifications;
                        if (context instanceof Activity)
                            StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                    }
                    break;

                case "update":
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.wishbook.catalog"));
                    context.startActivity(i);
                    break;

                case "selection":
                    DataPasser.selectedID = param.get("id");
                    bundle = new Bundle();
                    bundle.putString("fromNotification", "yes");
                    Fragment_ProductsInSelection productsInSelection = new Fragment_ProductsInSelection();
                    productsInSelection.setArguments(bundle);
                    Application_Singleton.CONTAINER_TITLE = "Selection";
                    Application_Singleton.CONTAINERFRAG = productsInSelection;
                    StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                    break;


                case "html":
                    List<String> tags = new ArrayList<>();
                    tags.add(page.trim());

                    FaqOptions faqOptions = new FaqOptions()
                            .filterByTags(tags, page.trim(), FaqOptions.FilterType.ARTICLE);//tags, filtered screen title, type

                    Freshchat.showFAQs(context, faqOptions);
                    break;

                case "faq":
                    List<String> tags_faq = new ArrayList<>();
                    tags_faq.add(page.trim());

                    FaqOptions faqOptions_faq = new FaqOptions()
                            .filterByTags(tags_faq, page.trim(), FaqOptions.FilterType.ARTICLE);//tags, filtered screen title, type

                    Freshchat.showFAQs(context, faqOptions_faq);
                    break;

                case "webview":
                    Fragment_BannerWebView bannerWebView = new Fragment_BannerWebView();
                    bundle = new Bundle();
                    bundle.putString("banner_url", page.trim());
                    bannerWebView.setArguments(bundle);
                    if (!page.isEmpty()) {
                        if (page.contains("resell-and-earn")) {
                            Application_Singleton.CONTAINER_TITLE = "Resell and Earn";
                        } else {
                            Application_Singleton.CONTAINER_TITLE = "Wishbook";
                        }
                    } else {
                        Application_Singleton.CONTAINER_TITLE = "Wishbook";
                    }
                    Application_Singleton.CONTAINERFRAG = bannerWebView;
                    //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                    StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                    break;


                case "support_chat":

                    if (page != null && !page.isEmpty() && !page.equals("support_chat")) {
                        List<String> freshchat_tags = new ArrayList<>();
                        freshchat_tags.add(page);

                        ConversationOptions convOptions = new ConversationOptions()
                                .filterByTags(freshchat_tags, "Wishbook Support");

                        Freshchat.showConversations(context, convOptions);
                    } else {
                        Freshchat.showConversations(context);
                    }

                    break;


                case "support_call":
                    String number = context.getResources().getString(R.string.call_wb_support_value_notifation);
                    if (param.containsKey("number")) {
                        number = param.get("number");
                    }
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
                    context.startActivity(intent);
                    break;

                case "credit_rating":
                    callApplyCreditRating(param);
                    break;

                case "refer_earn":
                    openReferEarnBottomSheet(context);
                    break;

                case "resell_earn":
                    navigateResellEarnWebView();
                    break;
                case "join_whatsapp_group":
                    navigateJoinWhatsappGroup();
                    break;
                case "profile_update":
                    openProfilePage();
                    break;
                case "product_landing":
                    try {
                        Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.CATALOGS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case "followed_by_me":
                    Application_Singleton.CONTAINER_TITLE = context.getResources().getString(R.string.from_brands_you_follow);
                    Application_Singleton.CONTAINERFRAG = new Fragment_BrandFollowedCatalogs();
                    if (context instanceof Activity)
                        StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                    break;

                case "replacement":
                    navigateRRC(RRCHandler.RRCREQUESTTYPE.REPLACEMENT);
                    break;

                case "return":
                    navigateRRC(RRCHandler.RRCREQUESTTYPE.RETURN);
                    break;
                default:
                    Fragment_Notifications fragment_notifications = new Fragment_Notifications();
                    Application_Singleton.CONTAINER_TITLE = "Notifications";
                    Application_Singleton.CONTAINERFRAG = fragment_notifications;
                    if (context instanceof Activity)
                        StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                    break;

            }


        } else if (param != null && param.containsKey("t")) {
            getPageDeepLink();
        }
    }

    public static String extractLinks(String text) {
        String link = null;
        Matcher m = Patterns.WEB_URL.matcher(text);
        while (m.find()) {
            link = m.group();

        }

        return link;
    }


    public void getCatalogDeepLink1(Context context, HashMap<String, String> paramsclone) {

        if (paramsclone.containsKey("id")) {
            if (paramsclone.containsKey("push_id")) {
                String table_id = paramsclone.get("id");
                String push_id = paramsclone.get("push_id");
                if (table_id != null && !table_id.isEmpty() && push_id != null && !push_id.isEmpty()) {
                    // if (!table_id.equals(push_id))
                    // Application_Singleton.isFromGallery = CatalogHolder.MYRECEIVED;
                }
            }



            getCatalogsData(paramsclone.get("id"));
        } else if (paramsclone.containsKey("from_notification")) {
            if (paramsclone.get(context.getResources().getString(R.string.catalog_type)).equals("received")) {
                getCatalogsData(paramsclone.get("id"));
            }
        } else if(paramsclone.containsKey("pid")) {
            Bundle bundle = new Bundle();
            bundle.putString("product_id", paramsclone.get("pid"));
            bundle.putString("from", from);
            new NavigationUtils().navigateSingleProductDetailPage(context, bundle);
        } else {

            Fragment fragment = null;
            HashMap<String, String> params = new HashMap<>();
            params.putAll(paramsclone);
            params.put("limit", String.valueOf(Application_Singleton.CATALOG_LIMIT));
            params.put("offset", String.valueOf(Application_Singleton.CATALOG_INITIAL_OFFSET));


            // Apply Filter Para
            if (params.containsKey(context.getResources().getString(R.string.deep_fabric))) {
                params.put("fabric", params.get(context.getResources().getString(R.string.deep_fabric)));
            }

            if (params.containsKey(context.getResources().getString(R.string.deep_work))) {
                params.put("work", params.get(context.getResources().getString(R.string.deep_work)));
            }

            if (params.containsKey(context.getResources().getString(R.string.deep_trusted_seller))) {
                params.put("trusted_seller", params.get(context.getResources().getString(R.string.deep_trusted_seller)));
                params.remove(context.getResources().getString(R.string.deep_trusted_seller));
            }

            if (params.containsKey(context.getResources().getString(R.string.deep_supplier_approved))) {
                params.put("is_supplier_approved", params.get(context.getResources().getString(R.string.deep_supplier_approved)));
                params.remove(context.getResources().getString(R.string.deep_supplier_approved));
            }

            if (params.containsKey(context.getResources().getString(R.string.deep_category))) {
                params.put("category", params.get(context.getResources().getString(R.string.deep_category)));
            }

            if (params.containsKey(context.getResources().getString(R.string.deep_from_price))) {
                params.put("min_price", params.get(context.getResources().getString(R.string.deep_from_price)));
                params.remove(context.getResources().getString(R.string.deep_from_price));
            }

            if (params.containsKey(context.getResources().getString(R.string.deep_to_price))) {
                params.put("max_price", params.get(context.getResources().getString(R.string.deep_to_price)));
                params.remove(context.getResources().getString(R.string.deep_to_price));
            }

            if (params.containsKey(context.getResources().getString(R.string.deep_brand))) {
                params.put("brand", params.get(context.getResources().getString(R.string.deep_brand)));
            }

            params.put("from", from);
            if (params.containsKey(context.getResources().getString(R.string.catalog_type))) {
                if (params.get(context.getResources().getString(R.string.catalog_type)).equals("public")) {
                    params.put("view_type", "public");
                    /**
                     * Changes done April -3
                     * Given task by Jay Patel
                     */
                    if(params.containsKey("category") && (params.get("category").equalsIgnoreCase("46")
                            || params.get("category").equalsIgnoreCase("66")
                            || params.get("category").equalsIgnoreCase("67")
                            || params.get("category").equalsIgnoreCase("68"))) {
                        params.put("product_type",Constants.CATALOG_TYPE_NON+","+Constants.CATALOG_TYPE_SCREEN);
                    }

                    if (params.containsKey("catalog_type") || params.containsKey("product_type")) {


                        String catalog_type = Constants.PRODUCT_TYPE_CAT;
                        if (params.containsKey("catalog_type")) {
                            catalog_type = params.get("catalog_type");
                        }
                        if (params.containsKey("product_type")) {
                            catalog_type = params.get("product_type");
                        }
                        if (catalog_type != null) {
                            Log.e(TAG, "getCatalogDeepLink1: ===>"+catalog_type);
                            if (catalog_type.contains(Constants.CATALOG_TYPE_NON)) {
                                Application_Singleton.deep_link_filter_non_catalog = params;
                            } else {
                                Application_Singleton.deep_link_filter = params;
                            }
                        } else {
                            Application_Singleton.deep_link_filter = params;
                        }
                    } else {
                        Application_Singleton.deep_link_filter = params;
                    }


                    if (params.containsKey("focus_position")) {
                        try {
                            Application_Singleton.Non_CATALOG_POSITION = Integer.parseInt(params.get("focus_position"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // Handle for Bottombar not set
                    try {

                        Fragment_BrowseProduct fragment_browseProduct = new Fragment_BrowseProduct();
                        Application_Singleton.CONTAINER_TITLE = "Products";
                        Application_Singleton.CONTAINERFRAG = fragment_browseProduct;
                        Intent intent2 = new Intent(context, OpenContainer.class);
                        context.startActivity(intent2);
                        //Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.CATALOGS);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if(context!=null) {
                            Fragment_BrowseProduct fragment_browseProduct = new Fragment_BrowseProduct();
                            Application_Singleton.CONTAINER_TITLE = "Products";
                            Application_Singleton.CONTAINERFRAG = fragment_browseProduct;
                            Intent intent2 = new Intent(context, OpenContainer.class);
                            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent2);
                        }
                    }

                } else if (params.get(context.getResources().getString(R.string.catalog_type)).equals("received") || params.get(context.getResources().getString(R.string.catalog_type)).equals("myreceived")) {
                    params.put("view_type", "public");

                    // Now Move to Public Catalog

                   /* Fragment_RecievedCatalogs fragment_recievedCatalogs = new Fragment_RecievedCatalogs();
                    fragment_recievedCatalogs.setArguments(bundle);
                    Application_Singleton.CONTAINER_TITLE = "Received Catalogs";
                    Application_Singleton.CONTAINERFRAG = fragment_recievedCatalogs;
                    Application_Singleton.deep_link_filter = params;
                    Intent intent2 = new Intent(context, OpenContainer.class);
                    context.startActivity(intent2);*/
                    // Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.CATALOGS);



                    Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.CATALOGS);
                } else if (params.get(context.getResources().getString(R.string.catalog_type)).equals("mycatalog")) {
                    Bundle bundle = new Bundle();
                    Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs(CatalogHolder.MYCATALOGS);
                    fragmentCatalogs.setArguments(bundle);
                    Application_Singleton.CONTAINER_TITLE = "My Products";
                    Application_Singleton.CONTAINERFRAG = fragmentCatalogs;
                    Intent intent2 = new Intent(context, OpenContainer.class);
                    intent2.putExtra("toolbarCategory", OpenContainer.ADD_CATALOG);
                    context.startActivity(intent2);
                }
            }
        }

    }

    public void getEnquiryDeepLink(Context context, HashMap<String, String> paramsclone) {
        if (paramsclone.containsKey("type")) {
            if (paramsclone.get("type").equals("received_enquiries")) {
                // Bure received enquiry
                if (paramsclone.containsKey("id")) {
                    Fragment_BuyerEnquiryDetails fragment_buyerEnquiryDetails = new Fragment_BuyerEnquiryDetails();
                    Bundle bundle = new Bundle();
                    bundle.putString("buyerid", paramsclone.get("id"));
                    fragment_buyerEnquiryDetails.setArguments(bundle);
                    Application_Singleton.CONTAINERFRAG = fragment_buyerEnquiryDetails;
                    Intent intent = new Intent(context, OpenContainer.class);
                    context.startActivity(intent);
                } else {
                    //Activity_Home.tabs.getTabAt(Application_Singleton.USERS).select();
                    context.startActivity(new Intent(context, ActivityEnquiryHolder.class).putExtra("type", "enquiry").putExtra("position", 0));
                /*    Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.USERS);
                    UserInfo.getInstance(context).settabselected(0);
                    UserInfo.getInstance(context).selastsubtabselected(0);*/
                }
            } else if (paramsclone.get("type").equals("sent_enquiries")) {
                // suppliers set enquiry
                if (paramsclone.containsKey("id")) {

                } else {
                    context.startActivity(new Intent(context, ActivityEnquiryHolder.class).putExtra("type", "leads").putExtra("position", 0));
                    //Activity_Home.tabs.getTabAt(Application_Singleton.USERS).select();
                   /* Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.USERS);
                    UserInfo.getInstance(context).settabselected(0);
                    UserInfo.getInstance(context).selastsubtabselected(1);*/
                }
            }

        }
    }

    public void getOrderDeepLink(Context context, HashMap<String, String> paramsclone) {

        if (paramsclone.get("type").equals("sales")) {

            if (paramsclone.containsKey("id")) {
                // Intent intent =new Intent(SplashScreen.this,OpenContainer.class);
                Response_sellingorder order = new Response_sellingorder(paramsclone.get("id"));

                Application_Singleton.selectedOrder = order;
                Intent intent = new Intent(context, Activity_OrderDetailsNew.class)
                        .putExtra("isAfterPayment", true);
                if(paramsclone.containsKey("focus_on")) {
                    intent.putExtra("focus_on",paramsclone.get("focus_on"));
                }
                context.startActivity(intent);
            } else {

            }

        } else if (paramsclone.get("type").equals("purchase")) {

            if (paramsclone.containsKey("id")) {
                Response_buyingorder order = new Response_buyingorder(paramsclone.get("id"));

                Application_Singleton.selectedOrder = order;
                Intent intent = new Intent(context, Activity_OrderDetailsNew.class)
                        .putExtra("isAfterPayment", true);
                if(paramsclone.containsKey("focus_on")) {
                    intent.putExtra("focus_on",paramsclone.get("focus_on"));
                }
                context.startActivity(intent);
            } else {

            }

        }
    }

    public void getContactsDeepLink(Context context, HashMap<String, String> paramsclone) {

        if (paramsclone.get("type").equals("buyer")) {
            if (paramsclone.containsKey("id")) {
                // Intent intent =new Intent(SplashScreen.this,OpenContainer.class);
                Application_Singleton.CONTAINER_TITLE = "Buyer Details";
                Application_Singleton.TOOLBARSTYLE = "WHITE";
                Fragment_BuyerDetails fragmentBuyerDetails = new Fragment_BuyerDetails();
                Bundle bundle = new Bundle();
                bundle.putString("buyerid", paramsclone.get("id"));
                fragmentBuyerDetails.setArguments(bundle);
                Application_Singleton.CONTAINERFRAG = fragmentBuyerDetails;
                StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
            } else {

            }

        } else if (paramsclone.get("type").equals("supplier")) {

            if (paramsclone.containsKey("id")) {
                // Intent intent =new Intent(SplashScreen.this,OpenContainer.class);
                Bundle bundle = new Bundle();
                bundle.putString("sellerid", paramsclone.get("id"));
                Application_Singleton.CONTAINER_TITLE = "Supplier Details";
                Fragment_SupplierDetailsNew2 supplier = new Fragment_SupplierDetailsNew2();
                supplier.setArguments(bundle);
                Application_Singleton.CONTAINERFRAG = supplier;
                //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
            } else {
                if (paramsclone.containsKey("companyid")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("sellerid", paramsclone.get("companyid"));
                    bundle.putBoolean("isHideAll", true);
                    Application_Singleton.CONTAINER_TITLE = "Supplier Details";
                    Fragment_SupplierDetailsNew2 supplier = new Fragment_SupplierDetailsNew2();
                    supplier.setArguments(bundle);
                    Application_Singleton.CONTAINERFRAG = supplier;
                    //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
                    StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                }
            }
        }
    }

    public void getBrandsDeepLink(Context context, HashMap<String, String> paramsclone) {
        if (paramsclone.containsKey("id")) {
            Bundle bundle = new Bundle();
            bundle.putString("filtertype", "brand");
            bundle.putString("filtervalue", paramsclone.get("id"));
            Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs();
            fragmentCatalogs.setArguments(bundle);
            Application_Singleton.CONTAINER_TITLE = "Catalogs";
            Application_Singleton.CONTAINERFRAG = fragmentCatalogs;
            //         appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, itemsInSection.get(relativePosition).getRedirector1()).addToBackStack(null).commit();
            Intent intent = new Intent(context, OpenContainer.class);
            context.startActivity(intent);
        }
    }


    public void getTabDeeplink(Context context, HashMap<String, String> paramsclone) {

        if (paramsclone.containsKey("page")) {
            String page = paramsclone.get("page");
            switch (page.toLowerCase()) {


                //catalogs tab
                case "catalogs":
                    //Activity_Home.tabs.getTabAt(Application_Singleton.CATALOGS).select();
                    Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.CATALOGS);
                    //  Application_Singleton.selectedCatalogInnerSubTab = 0;
                    break;
                case "catalogs/public":
                    //Activity_Home.tabs.getTabAt(Application_Singleton.CATALOGS).select();
                    Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.CATALOGS);
                    //  Application_Singleton.selectedCatalogInnerSubTab = 0;
                    break;
                case "catalogs/received":
                    /**
                     * Now Move to Public Catalog
                     */
                    Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.CATALOGS);
                   /* Fragment_RecievedCatalogs fragment_recievedCatalogs = new Fragment_RecievedCatalogs();
                    fragment_recievedCatalogs.setArguments(bundle);
                    Application_Singleton.CONTAINER_TITLE = "Received Catalogs";
                    Application_Singleton.CONTAINERFRAG = fragment_recievedCatalogs;
                    context.startActivity(new Intent(context,OpenContainer.class));*/
                    break;
                case "catalogs/mycatalog":
                    Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs(CatalogHolder.MYCATALOGS);
                    fragmentCatalogs.setArguments(bundle);
                    Application_Singleton.CONTAINER_TITLE = "My Catalog";
                    Application_Singleton.CONTAINERFRAG = fragmentCatalogs;
                    context.startActivity(new Intent(context, OpenContainer.class).putExtra("toolbarCategory", OpenContainer.ADD_CATALOG));
                    break;
                case "catalogs/brands":
                    Fragment_Brands fragment_brands = new Fragment_Brands();
                    Bundle bundle = new Bundle();
                    if (paramsclone.containsKey("focus_position"))
                        bundle.putString("focus_position", paramsclone.get("focus_position"));
                    fragment_brands.setArguments(bundle);
                    Application_Singleton.CONTAINER_TITLE = "Brands";
                    Application_Singleton.CONTAINERFRAG = fragment_brands;
                    context.startActivity(new Intent(context, OpenContainer.class));
                    break;

                //contacts tab
                                        /*case "contacts/mycontacts":
                                            Activity_Home.tabs.getTabAt(Application_Singleton.USERS).select();
                                            UserInfo.getInstance(context).settabselected(1);
                                            UserInfo.getInstance(context).selastsubtabselected(0);
                                            break;
                                        case "contacts/onwishbook":
                                            Activity_Home.tabs.getTabAt(Application_Singleton.USERS).select();
                                            UserInfo.getInstance(context).settabselected(0);
                                            UserInfo.getInstance(context).selastsubtabselected(1);
                                            break;*/
                case "buyers":
                    //Activity_Home.tabs.getTabAt(Application_Singleton.USERS).select();
                    // Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.USERS);
                    UserInfo.getInstance(context).settabselected(0);


                    Application_Singleton.CONTAINER_TITLE = "My Network";
                    Application_Singleton.CONTAINERFRAG = new Fragment_ContactsHolder();
                    StaticFunctions.switchActivity((Activity) context, OpenContainer.class);

                    break;
                case "suppliers":
                    // Activity_Home.tabs.getTabAt(Application_Singleton.USERS).select();
                    // Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.USERS);
                    UserInfo.getInstance(context).settabselected(1);
                    Application_Singleton.CONTAINER_TITLE = "My Network";
                    Application_Singleton.CONTAINERFRAG = new Fragment_ContactsHolder();
                    StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                    break;

                //users/old
                case "buyers/approved":
                    // Activity_Home.tabs.getTabAt(Application_Singleton.USERS).select();
                    // Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.USERS);
                    UserInfo.getInstance(context).settabselected(0);
                    UserInfo.getInstance(context).selastsubtabselected(1);

                    Application_Singleton.CONTAINER_TITLE = "My Network";
                    Application_Singleton.CONTAINERFRAG = new Fragment_ContactsHolder();
                    StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                    break;
                case "suppliers/approved":
                    // Activity_Home.tabs.getTabAt(Application_Singleton.USERS).select();
                    //Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.USERS);
                    UserInfo.getInstance(context).settabselected(1);
                    UserInfo.getInstance(context).selastsubtabselected(1);

                    Application_Singleton.CONTAINER_TITLE = "My Network";
                    Application_Singleton.CONTAINERFRAG = new Fragment_ContactsHolder();
                    StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                    break;
                case "buyers/pending":
                    // Activity_Home.tabs.getTabAt(Application_Singleton.USERS).select();
                    Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.USERS);
                    UserInfo.getInstance(context).settabselected(1);
                    UserInfo.getInstance(context).selastsubtabselected(0);
                    break;
                case "suppliers/pending":
                    // Activity_Home.tabs.getTabAt(Application_Singleton.USERS).select();
                    Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.USERS);
                    UserInfo.getInstance(context).settabselected(2);
                    UserInfo.getInstance(context).selastsubtabselected(0);
                    break;
                case "buyers/enquiry":
                    // Activity_Home.tabs.getTabAt(Application_Singleton.USERS).select();
                    Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.USERS);
                    UserInfo.getInstance(context).settabselected(0);
                    UserInfo.getInstance(context).selastsubtabselected(0);
                    break;
                case "suppliers/enquiry":
                    // Activity_Home.tabs.getTabAt(Application_Singleton.USERS).select();
                    Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.USERS);
                    UserInfo.getInstance(context).settabselected(0);
                    UserInfo.getInstance(context).selastsubtabselected(1);
                    break;

                //users/new
                case "buyers/on_wishbook":
                    // Activity_Home.tabs.getTabAt(Application_Singleton.USERS).select();
                    Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.USERS);
                    UserInfo.getInstance(context).settabselected(1);
                    UserInfo.getInstance(context).selastsubtabselected(1);
                    break;
                case "suppliers/on_wishbook":
                    // Activity_Home.tabs.getTabAt(Application_Singleton.USERS).select();
                    Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.USERS);
                    UserInfo.getInstance(context).settabselected(2);
                    UserInfo.getInstance(context).selastsubtabselected(1);
                    break;
                case "buyers/all":
                    // Activity_Home.tabs.getTabAt(Application_Singleton.USERS).select();
                    //Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.USERS);
                    UserInfo.getInstance(context).settabselected(0);
                    UserInfo.getInstance(context).selastsubtabselected(0);

                    Application_Singleton.CONTAINER_TITLE = "My Network";
                    Application_Singleton.CONTAINERFRAG = new Fragment_ContactsHolder();
                    StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                    break;
                case "suppliers/all":
                    // Activity_Home.tabs.getTabAt(Application_Singleton.USERS).select();
                    //Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.USERS);
                    UserInfo.getInstance(context).settabselected(1);
                    UserInfo.getInstance(context).selastsubtabselected(0);

                    Application_Singleton.CONTAINER_TITLE = "My Network";
                    Application_Singleton.CONTAINERFRAG = new Fragment_ContactsHolder();
                    StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                    break;
                case "enquiry/sent":
                    // Activity_Home.tabs.getTabAt(Application_Singleton.USERS).select();
                    Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.USERS);
                    UserInfo.getInstance(context).settabselected(0);
                    UserInfo.getInstance(context).selastsubtabselected(0);

                    break;
                case "enquiry/received":
                    //  Activity_Home.tabs.getTabAt(Application_Singleton.USERS).select();
                    Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.USERS);
                    UserInfo.getInstance(context).settabselected(0);
                    UserInfo.getInstance(context).selastsubtabselected(1);
                    break;


                //orders tab
                case "order/salesorder/pending":
                    // Activity_Home.tabs.getTabAt(Application_Singleton.ORDERS).select();
                    Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.ORDERS);
                    Application_Singleton.selectedInnerTabOrders = 1;
                    Application_Singleton.selectedInnerSubTabOrders = 0;
                    break;
                case "order/salesorder/dispatched":
                    // Activity_Home.tabs.getTabAt(Application_Singleton.ORDERS).select();
                    Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.ORDERS);
                    Application_Singleton.selectedInnerTabOrders = 1;
                    Application_Singleton.selectedInnerSubTabOrders = 1;
                    break;
                case "order/salesorder/cancelled":
                    // Activity_Home.tabs.getTabAt(Application_Singleton.ORDERS).select();
                    Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.ORDERS);
                    Application_Singleton.selectedInnerTabOrders = 1;
                    Application_Singleton.selectedInnerSubTabOrders = 2;
                    break;
                case "order/purchaseorder/total":
                    context.startActivity(new Intent(context, ActivityOrderHolder.class).putExtra("type", "purchase").putExtra("position", 0));
                    break;
                case "order/purchaseorder/pending":
                    //  Activity_Home.tabs.getTabAt(Application_Singleton.ORDERS).select();
                  /*  Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.ORDERS);
                    Application_Singleton.selectedInnerTabOrders = 0;
                    Application_Singleton.selectedInnerSubTabOrders = 0;*/

                    context.startActivity(new Intent(context, ActivityOrderHolder.class).putExtra("type", "purchase").putExtra("position", 1));
                    break;
                case "order/purchaseorder/dispatched":
                    //  Activity_Home.tabs.getTabAt(Application_Singleton.ORDERS).select();
                    Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.ORDERS);
                    Application_Singleton.selectedInnerTabOrders = 0;
                    Application_Singleton.selectedInnerSubTabOrders = 1;
                    break;
                case "order/purchaseorder/cancelled":
                    //  Activity_Home.tabs.getTabAt(Application_Singleton.ORDERS).select();
                    Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.ORDERS);
                    Application_Singleton.selectedInnerTabOrders = 0;
                    Application_Singleton.selectedInnerSubTabOrders = 2;
                    break;


                case "gst":
                    // showGSTPopup();
                    Application_Singleton.CONTAINER_TITLE = "KYC and Bank Details";
                    Application_Singleton.CONTAINERFRAG = new Fragment_GST_2();
                    context.startActivity(new Intent(context, OpenContainer.class));
                    break;


                case "setttings/discount":
                    // showGSTPopup();
                    Application_Singleton.CONTAINER_TITLE = "Discount settings";
                    Application_Singleton.CONTAINERFRAG = new Fragment_DiscountSetting();
                    StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
                    break;

                case "profile":
                    Application_Singleton.CONTAINER_TITLE = "Profile";
                    Application_Singleton.CONTAINERFRAG = new Fragment_Profile();
                    context.startActivity(new Intent(context, OpenContainer.class));
                    break;

                case "myfollowers":
                    Intent intent1 = new Intent(context, Activity_BuyerSearch.class);
                    intent1.putExtra("toolbarTitle", "Search Followers");
                    intent1.putExtra("type", "myfollowers");
                    context.startActivity(intent1);
                    break;

                case "mycart":
                    new NavigationUtils().navigateMyCart(context);
                    break;

                case "wbmoney":
                    navigateMyEarning("wbmoney");
                    //context.startActivity(new Intent(context, Activity_Wb_Money.class));
                    break;

                case "mywishlist":
                    Application_Singleton.CONTAINER_TITLE = "My Wishlist";
                    Application_Singleton.CONTAINERFRAG = new Fragment_WishList();
                    context.startActivity(new Intent(context, OpenContainer.class));
                    break;

                case "myearning/rewardpoint":
                    navigateMyEarning("reward");
                    break;

                case "myearning/wbmoney":
                    navigateMyEarning("wbmoney");
                    break;

                case "myearning/incentive":
                    navigateMyEarning("incentive");
                    break;

                case "reseller/payout" :
                    navigateReseller("payout");
                    break;

                case "reseller/preference":
                    navigateReseller("bankaccount");
                    break;

                case "reseller/address":
                    navigateReseller("address");
                    break;

                case "reseller/sharedbyme":
                    navigateReseller("sharedbyme");
                    break;

                case "sellerhub":
                    navigateSellerHub();
                    break;

                case "sellerpayout":
                    navigateSellerPayout();
                    break;

                case "sellerpendingitem":
                    navigateSellerPendingOrderItem();
                    break;

              /*  case "weekly_incentive_target":
                    openIncentiveTargetBottomSheet(context);
                    break;*/
            }

        }


    }


    public void getPageDeepLink() {
        // Sent Through SMS
        // Now Move to Public
       /* Application_Singleton.CONTAINER_TITLE = "Received Catalogs";
        Fragment_RecievedCatalogs fragment = new Fragment_RecievedCatalogs();
        Application_Singleton.CONTAINERFRAG = fragment;
        Bundle bundle = new Bundle();
        bundle.putInt(Application_Singleton.adapterFocusPosition, 0);
        fragment.setArguments(bundle);
        StaticFunctions.switchActivity((Activity) context, OpenContainer.class);*/

        Activity_Home.bottomBar.selectTabAtPosition(Application_Singleton.CATALOGS);
    }

    private void getCatalogsData(final String id) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("from", from);
            bundle.putString("product_id", id);
            Fragment_CatalogsGallery gallery = new Fragment_CatalogsGallery();
            gallery.setArguments(bundle);
            Application_Singleton.CONTAINER_TITLE = "";
            Application_Singleton.CONTAINERFRAG = gallery;
            if (context != null && context instanceof Activity) {
                Intent intent = new Intent(context, OpenContainer.class);
                intent.putExtra("toolbarCategory", OpenContainer.BROWSECATALOG);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, OpenContainer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("toolbarCategory", OpenContainer.BROWSECATALOG);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callApplyCreditRating(HashMap<String, String> param) {
        if (!UserInfo.getInstance(context).isGuest() && !StaticFunctions.isOnlyReseller(context)) {
            new CampaignLogApi((Activity) context, "credit_rating");
            if (param.containsKey("isEdit")) {
                if (param.get("isEdit").equals("true")) {
                    // Open Edit Mode
                    Bundle data = new Bundle();
                    data.putBoolean("isEdit", true);
                    data.putString("from", from);
                    FragmentCreditRating fragment_CreditRating = new FragmentCreditRating();
                    fragment_CreditRating.setArguments(data);
                    Application_Singleton.CONTAINERFRAG = fragment_CreditRating;
                    context.startActivity(new Intent(context, OpenContainer.class));
                } else {
                    // Open Add Mode
                    Intent intent = new Intent(context, CreditRatingIntroActivity.class);
                    intent.putExtra("from", from);
                    context.startActivity(intent);
                }
            } else {
                // Assume open to Add Mode
                try {
                    if (UserInfo.getInstance(context).isCreditRatingApply()) {
                        if (UserInfo.getInstance(context).getCreditScore().equals("null")) {
                            Bundle data = new Bundle();
                            data.putBoolean("isEdit", true);
                            data.putString("from", from);
                            FragmentCreditRating fragment_CreditRating = new FragmentCreditRating();
                            fragment_CreditRating.setArguments(data);
                            Application_Singleton.CONTAINERFRAG = fragment_CreditRating;
                            context.startActivity(new Intent(context, OpenContainer.class));
                        } else {
                            Toast.makeText(context, "You have already applied for credit rating", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Intent intent = new Intent(context, CreditRatingIntroActivity.class);
                        intent.putExtra("from", from);
                        context.startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if(UserInfo.getInstance(context).isGuest()) {
                StaticFunctions.ShowRegisterDialog(context, from);
            } else {
                // disable credit rating for reseller
                Toast.makeText(context, "You cannot apply for credit rating", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void getCreditRefBuyerRequested(Context context, String id) {
        try {
            Bundle data = new Bundle();
            data.putString("id", id);
            data.putInt("mode", Constants.CREDT_RATING_EDIT_MODE);
            data.putString("buyer_name", "");
            Application_Singleton.TOOLBARSTYLE = "WHITE";
            Fragment_Buyer_Rating fragment_buyer_rating = new Fragment_Buyer_Rating();
            fragment_buyer_rating.setArguments(data);
            Application_Singleton.CONTAINERFRAG = fragment_buyer_rating;
            context.startActivity(new Intent(context, OpenContainer.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getCatalogEnquiry(final Context context, HashMap<String, String> param) {

        if (context != null && !((Activity) context).isFinishing()) {
            final MaterialDialog progressdialog = StaticFunctions.showProgress(context);
            progressdialog.show();

            if (param.containsKey("id")) {
                String applozicID = param.get("id");
                HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
                String url = URLConstants.companyUrl(context, "catalog-enquiries", "") + "?applogic_conversation_id=" + applozicID;
                HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        progressdialog.dismiss();
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {

                        progressdialog.dismiss();
                        if (context != null) {
                            try {
                                Type type = new TypeToken<ArrayList<ResponseCatalogEnquiry>>() {
                                }.getType();
                                ArrayList<ResponseCatalogEnquiry> temp = Application_Singleton.gson.fromJson(response, type);
                                if (temp.size() > 0) {
                                    ResponseCatalogEnquiry responseCatalogEnquiry = temp.get(0);
                                    if (UserInfo.getInstance(context) != null && UserInfo.getInstance(context).getCompany_id() != null) {
                                        if (UserInfo.getInstance(context).getCompany_id().equals(responseCatalogEnquiry.getSelling_company())) {
                                            new OpenContextBasedApplogicChat(context, responseCatalogEnquiry, OpenContextBasedApplogicChat.SUPPLIERTOBUYER, null, null);
                                        } else {
                                            new OpenContextBasedApplogicChat(context, responseCatalogEnquiry, OpenContextBasedApplogicChat.BUYERTOSUPPLIER, null, null);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        progressdialog.dismiss();
                    }
                });
            }

        }
    }


    public void openReferEarnBottomSheet(Context context) {
        if(UserInfo.getInstance(context).isGuest()) {
            StaticFunctions.ShowRegisterDialog(context,from);
            return;
        }
        BottomReferandEarn bottomReferandEarn = new BottomReferandEarn();
        Application_Singleton.CONTAINER_TITLE = "Refer & Earn Program";
        Application_Singleton.CONTAINERFRAG = bottomReferandEarn;
        context.startActivity(new Intent(context, OpenContainer.class));
    }


    public void openIncentiveTargetBottomSheet(Context context) {
        BottomReferandEarn bottomReferandEarn = new BottomReferandEarn();
        Bundle bundle = new Bundle();
        bundle.putString("from",from);
        bottomReferandEarn.setArguments(bundle);
        Application_Singleton.CONTAINER_TITLE = "Refer & Earn Program";
        Application_Singleton.CONTAINERFRAG = bottomReferandEarn;
        context.startActivity(new Intent(context, OpenContainer.class));
    }

    private void navigateMyEarning(String position) {
        if(UserInfo.getInstance(context).isGuest()) {
            StaticFunctions.ShowRegisterDialog(context,from);
            return;
        }
        Bundle reseller_bundle = new Bundle();
        Fragment_MyEarningHolder fragment_myEarningHolder = new Fragment_MyEarningHolder();
        reseller_bundle.putString("from", from);
        reseller_bundle.putString("position", position);
        fragment_myEarningHolder.setArguments(reseller_bundle);
        Application_Singleton.CONTAINER_TITLE = "My Earnings";
        Application_Singleton.CONTAINERFRAG = fragment_myEarningHolder;
        context.startActivity(new Intent(context, OpenContainer.class));
    }

    public void navigateReseller(String position) {
        Bundle reseller_bundle = new Bundle();
        Fragment_ResellerHolder fragment_resellerHolder = new Fragment_ResellerHolder();
        reseller_bundle.putString("from", Fragment_MyBusiness.class.getSimpleName());
        reseller_bundle.putString("position", position);
        fragment_resellerHolder.setArguments(reseller_bundle);
        Application_Singleton.CONTAINER_TITLE = "Reseller Hub";
        Application_Singleton.CONTAINERFRAG = fragment_resellerHolder;
        context.startActivity(new Intent(context, OpenContainer.class));
    }

    public void navigateSellerHub() {
        Bundle bundle = new Bundle();
        Fragment_SellerHub fragment_sellerHub = new Fragment_SellerHub();
        bundle.putString("from", from);
        fragment_sellerHub.setArguments(bundle);
        Application_Singleton.CONTAINER_TITLE = "Seller Hub";
        Application_Singleton.CONTAINERFRAG = fragment_sellerHub;
        context.startActivity(new Intent(context, OpenContainer.class));
    }

    public void navigateSellerPayout() {
        Bundle bundle = new Bundle();
        Fragment_SellerPayout fragment_sellerPayout = new Fragment_SellerPayout();
        bundle.putString("from", from);
        fragment_sellerPayout.setArguments(bundle);
        Application_Singleton.CONTAINER_TITLE = "Seller payouts";
        Application_Singleton.CONTAINERFRAG = fragment_sellerPayout;
        context.startActivity(new Intent(context, OpenContainer.class));
    }

    public void navigateSellerPendingOrderItem() {
        Bundle bundle = new Bundle();
        Fragment_Seller_Pending_Order_item fragment = new Fragment_Seller_Pending_Order_item();
        bundle.putString("from", from);
        fragment.setArguments(bundle);
        Application_Singleton.CONTAINER_TITLE = "Pending order items";
        Application_Singleton.CONTAINERFRAG = fragment;
        context.startActivity(new Intent(context, OpenContainer.class));
    }

    private void navigateJoinWhatsappGroup() {
        Bundle reseller_bundle = new Bundle();
        Fragment_JoinWhatsappGroup fragment_joinWhatsappGroup = new Fragment_JoinWhatsappGroup();
        fragment_joinWhatsappGroup.setArguments(reseller_bundle);
        Application_Singleton.CONTAINER_TITLE = "Join our WhatsApp Groups";
        Application_Singleton.CONTAINERFRAG = fragment_joinWhatsappGroup;
        context.startActivity(new Intent(context, OpenContainer.class));
    }

    private void navigateRRC(RRCHandler.RRCREQUESTTYPE request_type) {
        Bundle bundle = new Bundle();
        Fragment_RRCList fragment_replacement = new Fragment_RRCList();
        if (request_type == RRCHandler.RRCREQUESTTYPE.REPLACEMENT) {
            Application_Singleton.CONTAINER_TITLE = "Replacement requests";
        } else if (request_type == RRCHandler.RRCREQUESTTYPE.CANCELLATION) {
            Application_Singleton.CONTAINER_TITLE = "Cancellation requests";
        } else if (request_type == RRCHandler.RRCREQUESTTYPE.RETURN) {
            Application_Singleton.CONTAINER_TITLE = "Return requests";
        }
        bundle.putSerializable("RRC_TYPE",request_type);
        fragment_replacement.setArguments(bundle);
        Application_Singleton.CONTAINERFRAG = fragment_replacement;
        context.startActivity(new Intent(context, OpenContainer.class));
    }

    private void navigateResellEarnWebView() {
        HashMap<String, String> param = new HashMap<>();
        String url;
        if (LocaleHelper.getLanguage(context) != null) {
            if (LocaleHelper.getLanguage(context).equals("en")) {
                url = "https://www.wishbook.io/resell-and-earn.html";
            } else {
                url = "https://www.wishbook.io/resell-and-earn-hi.html";
            }
        } else {
            url = "https://www.wishbook.io/resell-and-earn.html";
        }
        Fragment_BannerWebView bannerWebView = new Fragment_BannerWebView();
        bundle = new Bundle();
        bundle.putString("banner_url", url);
        if(from!=null && !from.isEmpty()) {
            bundle.putString("from",from);
        }
        bannerWebView.setArguments(bundle);
        Application_Singleton.CONTAINER_TITLE = "Resell and Earn";
        Application_Singleton.CONTAINERFRAG = bannerWebView;
        StaticFunctions.switchActivity((Activity) context, OpenContainer.class);
    }

    public void openProfilePage() {
        Toast.makeText(context,"Please complete your profile",Toast.LENGTH_SHORT).show();
        Fragment_Profile fragment_profile = new Fragment_Profile();
        Bundle bundle = new Bundle();
        bundle.putBoolean("showAsDialog", true);
        fragment_profile.setArguments(bundle);
        fragment_profile.show(((AppCompatActivity)context).getSupportFragmentManager(),"PROFILE");
    }
}
