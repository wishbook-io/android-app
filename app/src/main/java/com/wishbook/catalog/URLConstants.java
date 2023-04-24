package com.wishbook.catalog;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wishbook.catalog.Utils.LogoutCommonUtils;
import com.wishbook.catalog.commonmodels.UserInfo;

/**
 * Created by prane on 19-03-2016.
 */
public class URLConstants {



  //  private static final boolean ISPRODUCTION = false;

    public static final String BARCODE_APP_URL = "https://app.wishbook.io";

    // production
    // public static final  String APP_URL ="https://app.wishbook.io";
    public static final  String APP_URL = Application_Singleton.getAPIURL();

    public static final  String CHECKOUT_URL = Application_Singleton.getCheckoutApi();
    //development
   // public static final String APP_URL = "http://b2b.wishbook.io";

    public  static final String API_VERSION = "/api/v1/";


    //public static final String APP_URL = "http://192.168.0.11";
    // public static final String APP_URL ="http://local.wishbook.io";


    //  public static final String LOGIN_URL = APP_URL + "/api/rest-auth/login/";
    //  public static final String USER_URL = APP_URL + "/api/v1/auth/user/";
    //  public static final String COMPANY_URL = APP_URL + "/api/companies/";
    //   public static final String CHECK_PHONE = APP_URL + "/api/checkphonenoexist/";
    //   public static final String REGISTER_USER = APP_URL + "/api/rest-auth/registration/";

    public static final String NEW_REGISTER_USER = APP_URL + "/api/senduserdetail/";

    public static final String CHECKOTP_URL = APP_URL + "/api/checkotpanduser/";
    public static final String MOBILE_CHECKOTP_URL = APP_URL + "/api/checkotpandmobile/";
    public static final String PROFILE_CHECKOTP_URL = APP_URL + "/api/registrationopt/";
    public static final String CHECKOTP_URL_CHANGEPASSWORD = APP_URL + "/api/checkotpandchangepassword/";


    public static final String RESENDOTP_URL = APP_URL + "/api/resendotp/";
    // public static final String CHECKOTP_URL = APP_URL + "/api/checkotpandmobile/";
    //public static final String RESENDOTP_URL = APP_URL + "/api/registrationopt/";
    public static final String RESET_PASSWORD_URL = APP_URL + "/api/rest-auth/password/reset/";
    public static final String GET_MYSENT_CAT_URL = APP_URL + "/api/pushes/?view_type=mysent";
    // public static final String GET_MYBRANDS_URL = APP_URL + "/api/brands/";
    public static final String GET_BRANDS_URL = APP_URL + "/api/brands/";
    public static final String GET_CATEGORIES_URL = APP_URL + "/api/category/";
    public static final String GET_SALES_ORDERS = APP_URL + "/api/salesorders/";
    public static final String GET_STATES = APP_URL + "/api/state/";
    public static final String GET_CITIES = APP_URL + "/api/city/?state=";
    // public static final String USER_EXISTENCE_CHECK = APP_URL + "/api/checkuserexist/";
    public static final String REGISTRATION_OTP = APP_URL + "/api/registrationopt/?format=json";
    // public static final String LOGOUT_URL = APP_URL + "/api/rest-auth/logout/";
    public static final String PRODUCT_LIKE = APP_URL + "/api/productlike/";
    public static final String GET_SELECTIONS_URL = APP_URL + "/api/selections/";
    public static final String GET_SELECTIONPRODUCTS_URL = APP_URL + "/api/products/?selection=";
    public static final String ADDCATALOG = APP_URL + "/api/catalogs/";
    public static final String ADDPRODUCT = APP_URL + "/api/products/";
    public static final String GET_BUYERGROUPS = APP_URL + "/api/buyersegmentation/";
    public static final String GET_GROUPTYPES = APP_URL + "/api/buyersegmentation/";
    public static final String GET_GROUPS = APP_URL + "/api/grouptype/";
    public static final String SHARECATALOG_URL = APP_URL + "/api/pushes/";
    public static final String BUYERS = APP_URL + "/api/buyers/";
    public static final String BUYERSONLY = APP_URL + "/api/buyersonly/";
    public static final String PENDING_BUYERS = APP_URL + "/api/buyersonly/?status=pending";
    public static final String APPROVED_BUYERS = APP_URL + "/api/buyersonly/?status=approved";
    public static final String REJECTED_BUYERS = APP_URL + "/api/buyersonly/?status=rejected";
    public static final String BRANDADD = APP_URL + "/api/brandapp/";
    public static final String INVITE = APP_URL + "/api/invites/";
    public static final String INVITEARRAY = APP_URL + "/api/importarrayinvitee/";
    //public static final String PROFILE = APP_URL + "/api/rest-auth/user/";
    public static final String MEETINGS = APP_URL + "/api/meeting/";
    public static final String GETPRODUCTSBYCAT = APP_URL + "/api/products/?catalog=";
    public static final String MEETINGREPORT = APP_URL + "/api/apptable/";
    // public static final String RECENTSHAREDCAT = APP_URL + "/api/catalogapp/?view_type=shared";
    // public static final String RECIEVED_CAT = APP_URL + "/api/catalogapp/?view_type=myreceived";
    public static final String RECEIVED_SELECTIONS = APP_URL + "/api/selections/?type=push";
    public static final String SYNC_ACTIVITYLOG = APP_URL + "/api/syncactivitylog";
    public static final String DISABLE_CATALOG = APP_URL + "/api/disableitem/";
    public static final String ENABLE_CATALOG = APP_URL + "/api/enableitem/";
    public static final String MYCATALOGS = APP_URL + "/api/catalogapponly/?view_type=mycatalogs";


    public static final String SUPPLIERS_APPROVED = APP_URL + "/api/sellersonly/?status=approved";
    public static final String SUPPLIERS_PENDING = APP_URL + "/api/sellersonly/?status=pending";
    public static final String SUPPLIERS_REJECTED = APP_URL + "/api/sellersonly/?status=rejected";
    public static final String GET_INVOICE = APP_URL + "/api/invoice";

    public static final String CREATEORDER = APP_URL + "/api/salesorders/";
    public static final String ADDBUYER = APP_URL + "/api/addbuyerusingnumber/";
    public static final String GETUSERNAME = APP_URL + "/api/getusernamefromno/";
    public static final String GET_WISHBOOK_CONTACTS = APP_URL + "/api/onwishbook/";
    public static final String GCM = APP_URL + "/api/device/gcm/";
    public static final String GET_CATALOGSAPP_URL = APP_URL + "/api/catalogapponly/";
    public static final String GET_CATALOGSAPPFULL_URL = APP_URL + "/api/catalogapp/";
    public static final String GET_DASHBOARD_URL = APP_URL + "/api/dashboard/";
    public static final String COMPANYTYPE = APP_URL + "/api/companytype/";
    public static final String RECIEVED_CAT_APP = APP_URL + "/api/v1/catalogs/?view_type=myreceived";
    public static final String RECENTSHAREDCATAPP = APP_URL + "/api/sharedbyme/";
    public static final String ADDSELLER = APP_URL + "/api/addsupplierusingnumber/";
    public static final String RESEND_SELLER = APP_URL + "/api/resendsupplier/";
    public static final String RESEND_BUYER = APP_URL + "/api/resendbuyer/";
    public static final String BUYER_LIST = APP_URL + "/api/buyerlist/";
    public static final String GET_COMPANYLIST = APP_URL + "/api/companylist/";
    public static final String PUSH = APP_URL + "/api/pushes/";
    public static final String GET_CATALOGSUPPLIER = APP_URL + "/api/catalogsupplier/";
    public static final String GET_BROKERS = APP_URL + "/api/brokerlist/";
    public static final String PURCHASE = APP_URL + "/api/purchseorders/";
    public static final String GET_SELECTIONSUPPLIER = APP_URL + "/api/selectionsupplier/";
    public static final String GET_BUYER = APP_URL + "/api/buyers/";
    public static final String GET_SELLER = APP_URL + "/api/sellers/";
    public static final String GET_BRANDDISTRIBUTOR = APP_URL + "/api/branddistributor/";
    public static final String TERMSCONDITION = APP_URL + "/api/tnc/";

    public static final String GET_WAREHOUSE = APP_URL + "/api/warehouse/";
    public static final String BARCODE_SAVE = APP_URL + "/api/v1/barcode/";
    public static final String SAVE_INVENTORY = APP_URL + "/api/v1/inventoryadjustment/";
    public static final String SAVE_OPENING_STOCK = APP_URL + "/api/v1/openingstock/";


    public static final String GET_ATTENDANCE = APP_URL + "/api/attendance/";


    // NEW API VERSION 1:
    public static final String USERS = APP_URL + "/api/v1/users/";
    public static final String REGISTER_USER = APP_URL + "/api/v1/auth/registration/";
    //  public static final String NEW_REGISTER_USER = APP_URL + "/api/senduserdetail/";
    public static final String PROFILE = APP_URL + "/api/v1/auth/user/";
    public static final String LOGIN_URL = APP_URL + "/api/v1/auth/login/";
    public static final String PASSWORD_RESET = APP_URL + "/api/v1/auth/password-reset/";
    public static final String CHANGE_PASSWORD = APP_URL + "/api/v1/auth/password/change/";
    public static final String LOGOUT_URL = APP_URL + "/api/v1/auth/logout/";
    public static final String NOTIFY = APP_URL + "/api/v1/notify/";
    public static final String INVENTORY = APP_URL + "/api/v1/inventory/";
    public static final String INVENTORY_DASHBOARD = APP_URL + "/api/v1/inventory/dashboard/";


    public static final String PAYTM_RESPONSE = APP_URL + "/api/v1/paytm/response/";
    public static final String GENERATE_CHECKSUM = APP_URL + "/api/v1/generate-checksum/";
    public static final String GENERATE_CHECKSUM_V2 = APP_URL + "/api/v1/generate-checksum-v2/";
    // public static final String VERIFY_CHECKSUM = APP_URL + "/api/v1/verify-checksum/";
    public static final String PYTM_CALLBACK_URL = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

    //Companies Will Include Following URL
    //Brands,Catalogs,


    public static final String COMPANY_URL = APP_URL + "/api/v1/companies/";
    public static final String COUNTRIES = APP_URL + "/api/v1/country/";
    public static String BANNER_URL = APP_URL + "/api/v1/promotions/";

    public static final String PAYMENT_METHOD_URL = CHECKOUT_URL + "/api/v2/payment-method/";
    public static final String BUYER_DISCOUNT_URL = APP_URL + "/api/v1/buyer-discount/";
    public static final String CONFIG_URL = APP_URL + "/api/v1/config/";
    public static final String SHIPPING_CHARGE = CHECKOUT_URL + "/api/v2/shipping-charges/";
    public static final String ENUM_GRPUP = APP_URL + "/api/v1/enumgroup/";
    public static final String LANGUAGE = APP_URL + "/api/v1/languages/";
    public static final String SHORT_URL = APP_URL + "/api/v1/deeplink/";
    public static final String CATEGORY_EVP = APP_URL + "/api/v1/category-eav-attribute/";
    public static final String CATEGORY_EVP_V2 = APP_URL + "/api/v2/category-eav-attribute/";
    public static final String FEEDBACK_URL = APP_URL + "/api/v2/user-reviews/";
    public static final String PROMOTIONAL_TAG = APP_URL + "/api/v1/promotional-tags/?status=Enable";
    public static final String PRODUCT_TAB_PROMOTION = APP_URL + "/api/v2/promotional-tags/";

    public static final String ACTIONLOG = APP_URL + "/api/v1/action-log/";

    public static final String CAMPAIGNLOG = APP_URL + "/api/v1/user-campaign-click/";

    public static final String AUTHENTICATION = APP_URL + "/api/v1/user-authentication/";

    public static final String CHANGECOMPANY = APP_URL + "/api/v1/companies/change_company/";

    public static final String USER_SAVE_FILER = APP_URL + "/api/v1/user-saved-filter/";

    public static final String APP_VERSION_URL = APP_URL + "/api/v1/app-version/";

    public static final String PREDEFINED_FILTER_URL = APP_URL + "/api/v1/predefined-filters/categories/";

    public static final String PREDEFINED_SUB_FILTER_URL = APP_URL + "/api/v1/predefined-filters/";

    public static final String RESET_GUEST_USER = APP_URL + "/api/v1/reset-to-guest-user/";

    public static final String DISCOUNT_RULE = APP_URL + "/api/v2/discount-rule/";

    public static final String STORIES = APP_URL + "/api/v1/stories/";

    public static final String CASHFREE_CHECKSUM = CHECKOUT_URL + "/api/v1/cashfree/checksum/";

    public static final String CASHFREE_RETURN = CHECKOUT_URL + "/api/v1/cashfree/response/";

    public static final String CASHFREE_TOKEN = CHECKOUT_URL + "/api/v1/cashfree/token/";

    public static final String  PUBLIC_CATALOG_LIST =  APP_URL + "/api/v2/products/";
    public static final String  PUBLIC_BRANDS_LIST =  APP_URL + "/api/v1/brands/";

    public static final String SCREEN_LIST = APP_URL + "/api/v2/products/";

    public static final String DELIVERY_INFO = APP_URL + "/api/v2/delivery-info";

    public static final String PINCODE_ZONE = APP_URL + "/api/v2/pincode-zone/";

    public static final String USER_FEED_LIST = APP_URL + "/api/v2/user-feed/";



    static MaterialDialog materialDialog;

    static String company_id = "1";

    /**
     * Define all company nested urls
     * @param context
     * @param type
     * @param id
     * @return  String - company nested urls
     */
    //<editor-region desc="Nested Company URL">
    public static final String companyUrl(final Context context, String type, String id) {
        String COMPANY_URL;

        switch (type) {


            //TNC
            case "tnc":
                //TERMSCONDITION
                COMPANY_URL = APP_URL + "/api/v1/tnc/";
                return COMPANY_URL;
            //COUNTRY , STATE , CITY

            case "country":

                COMPANY_URL = APP_URL + "/api/v1/country/";
                return COMPANY_URL;

            case "state":
                //GET_STATES
                COMPANY_URL = APP_URL + "/api/v1/state/";
                return COMPANY_URL;


            case "city":
                //GET_CITIES
                COMPANY_URL = APP_URL + "/api/v1/city/?state=" + id;
                return COMPANY_URL;


            case "grouptype":
                //GET_GROUPS
                COMPANY_URL = APP_URL + "/api/v1/group-types/";
                return COMPANY_URL;

            //CATEGORY
            case "category":
                //GET_CATEGORIES_URL
                COMPANY_URL = APP_URL + "/api/v2/category/";
                return COMPANY_URL;

            //COMPANYLIST
            case "companylist":
                //GET_COMPANYLIST
                COMPANY_URL = APP_URL + "/api/v1/companies/dropdown/";
                return COMPANY_URL;



        }


        if (UserInfo.getInstance(context).getCompany_id() != null
                && !UserInfo.getInstance(context).getCompany_id().isEmpty()) {
            company_id = UserInfo.getInstance(context).getCompany_id();
        }

        switch (type) {
            //  ############ Image Search API Start ###########//
            case "imgsearch":
                COMPANY_URL = APP_URL + "/api/v2/imagesearch/";
                return COMPANY_URL;


            //    ##### v2 API Start  #########  //

            case "enumvalues" :
                COMPANY_URL = APP_URL + "/api/v2/category-eav-attribute/enumvalues/";
                return COMPANY_URL;

            case "catalogs_expand_false":
                //GET_CATALOGSAPP_URL
                COMPANY_URL = APP_URL + "/api/v2/products/?" + "expand=false";
                return COMPANY_URL;
            case "catalogs_expand_true":
                //companyUrl(getActivity(),"catalogs_expand_true","")
                COMPANY_URL = APP_URL + "/api/v2/products/?" + "expand=true";
                return COMPANY_URL;
            case "catalogs_expand_true_id":
                //companyUrl(getActivity(),"catalogs_expand_true","")
                COMPANY_URL = APP_URL + "/api/v2/products/" + id + "/?expand=true";
                return COMPANY_URL;

            case "catalogs_disable":
                //DISABLE_CATALOG
                COMPANY_URL = APP_URL + "/api/v2/products/" + id + "/disable/";
                return COMPANY_URL;

            case "catalogs_enable":
                //  ENABLE_CATALOG
                COMPANY_URL = APP_URL + "/api/v2/products/" + id + "/enable/";
                return COMPANY_URL;

            case "catalogs":
                //ADDCATALOG
                COMPANY_URL = APP_URL + "/api/v2/products/";
                return COMPANY_URL;

            case "productsonly":
                //ADDPRODUCT or GETPRODUCTSBYCAT
                COMPANY_URL = APP_URL + "/api/v2/products/";
                return COMPANY_URL;

            case "upload-endpoint":
                COMPANY_URL = APP_URL + "/api/v2/products/" + id + "/upload-endpoint/";
                return COMPANY_URL;

            case "products_photos":
                COMPANY_URL = APP_URL + "/api/v2/products-photos/";
                return COMPANY_URL;

            case "productsonlywithoutcatalog":
                //ADDPRODUCT or GETPRODUCTSBYCAT
                COMPANY_URL = APP_URL + "/api/v2/products/";
                return COMPANY_URL;

            case "purchaseorders_catalogwise":
                COMPANY_URL = APP_URL + "/api/v2/companies/" + company_id + "/purchase-orders/" + id + "/catalogwise/?expand=true";
                return COMPANY_URL;

            case "salesorders_catalogwise":
                //GET_SALES_ORDERS Catalogwise
                COMPANY_URL = APP_URL + "/api/v2/companies/" + company_id + "/sales-orders/" + id + "/catalogwise/?expand=true";
                return COMPANY_URL;

            case "statistics":
                COMPANY_URL = APP_URL + "/api/v2/companies/" + company_id + "/statistics/";
                return COMPANY_URL;



            case "product-recommendation":
                COMPANY_URL = APP_URL + "/api/v2/products/" + id +"/recommendation/";
                return COMPANY_URL;

            case "reseller-settlement":
                COMPANY_URL = APP_URL + "/api/v2/companies/" + company_id + "/reseller-settlement/";
                return COMPANY_URL;

            case "reseller-settlement-dashboard":
                COMPANY_URL = APP_URL + "/api/v2/companies/" + company_id + "/reseller-settlement/dashboard/";
                return COMPANY_URL;

            case "bulk-product-update":
                COMPANY_URL = APP_URL + "/api/v2/products/bulk-update/";
                return COMPANY_URL;

            case "wbpoint-log" :
                COMPANY_URL = APP_URL + "/api/v2/wbpoint-log/";
                return COMPANY_URL;

            case "wbpoint-dashboard" :
                COMPANY_URL = APP_URL + "/api/v2/wbpoint-log/dashboard/";
                return COMPANY_URL;

            case "invited-users":
                COMPANY_URL = APP_URL + "/api/v2/companies/" + company_id + "/invited-users/bulk-upload/";
                return COMPANY_URL;

            case "incentive-history":
                COMPANY_URL = APP_URL + "/api/v2/companies/" + company_id + "/incentives/";
                return COMPANY_URL;

            case "incentive-dashboard":
                COMPANY_URL = APP_URL + "/api/v2/companies/" + company_id + "/incentives/dashboard/";
                return COMPANY_URL;

            case "bulk-update-product-seller":
                COMPANY_URL = APP_URL + "/api/v2/products/bulk-update-product-seller/";
                return COMPANY_URL;

            case "product-share/start-sharing":
                COMPANY_URL = APP_URL + "/api/v2/product-share/start-sharing/";
                return COMPANY_URL;

            case "product-share":
                COMPANY_URL = APP_URL + "/api/v2/product-share/";
                return COMPANY_URL;

            case "incentive-tiers":
                COMPANY_URL = APP_URL + "/api/v2/incentive-tiers/";
                return COMPANY_URL;

            case "mydetails":
                COMPANY_URL = APP_URL + "/api/v2/products/"+id+"/mydetails/";
                return COMPANY_URL;

            case "pending-purchase-product-status":
                COMPANY_URL = APP_URL + "/api/v2/companies/" + company_id + "/pending-purchase-product-status/";
                return COMPANY_URL;

            case "pending-order-item-action":
                COMPANY_URL = APP_URL + "/api/v2/companies/"+company_id+"/pending-order-item-action/";
                return COMPANY_URL;

            case "product-ratings":
                COMPANY_URL = APP_URL + "/api/v2/product-ratings/";
                return COMPANY_URL;

            case "review-images":
                COMPANY_URL = APP_URL + "/api/v2/review-images/";
                return COMPANY_URL;

            case "buyer-order-item-crm":
                COMPANY_URL = APP_URL + "/api/v2/companies/"+company_id+"/buyer-order-item-crm/";
                return COMPANY_URL;



                /// ##### Checkout v2 Api Start ######

            case "cart":
                COMPANY_URL = CHECKOUT_URL + "/api/v2/companies/" + company_id + "/carts/";
                return COMPANY_URL;

            case "cart-payment":
                COMPANY_URL = CHECKOUT_URL + "/api/v2/companies/" + company_id + "/carts/"+id+"/payment/";
                return COMPANY_URL;

            case "cart-delete":
                COMPANY_URL = CHECKOUT_URL + "/api/v2/companies/" + company_id + "/cart-items/";
                return COMPANY_URL;

            case "credit-approved-line":
                COMPANY_URL = CHECKOUT_URL + "/api/v1/companies/" + company_id + "/credits-approved-line/";
                return COMPANY_URL;

            case "coupons":
                COMPANY_URL = CHECKOUT_URL + "/api/v2/coupons/";
                return COMPANY_URL;

            case "coupon-apply":
                COMPANY_URL = CHECKOUT_URL + "/api/v2/companies/" + company_id + "/carts/"+id+"/apply-coupon/";
                return COMPANY_URL;

            //// ##### RRC v2 api start ######### //

            case "rrc-requests":
                COMPANY_URL = CHECKOUT_URL + "/api/v2/companies/" + company_id + "/rrc-requests/";
                return COMPANY_URL;

            case "rrc-eligible-order-list":
                //PURCHASE
                COMPANY_URL = APP_URL + "/api/v2/companies/" + company_id + "/purchase-orders/";
                return COMPANY_URL;

            case "rrc-requests-images":
                COMPANY_URL = APP_URL + "/api/v1/rrc-requests-images/";
                return COMPANY_URL;


                // ##### Seller-Invoice,Manifest API start ######### //

            case "seller-invoice":
                COMPANY_URL = APP_URL + "/api/v2/seller-invoice/";
                return COMPANY_URL;

            case "seller-invoice-images":
                COMPANY_URL = APP_URL + "/api/v2/seller-invoice-images/";
                return COMPANY_URL;

            case "manifests":
                COMPANY_URL = APP_URL + "/api/v2/manifests/";
                return COMPANY_URL;

            case "manifest-images":
                COMPANY_URL = APP_URL + "/api/v2/manifest-images/";
                return COMPANY_URL;

            /// ####### API v1 start ################  //

            //DASHBOARD

            case "seller-dashboard":
                COMPANY_URL = APP_URL + "/api/v1/seller-dashboard/"+"?company_id="+company_id;
                return COMPANY_URL;
            case "dashboard":
                //GET_DASHBOARD_URL
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/dashboard/";
                return COMPANY_URL;

            case "buyer_dashboard":
                //Buyer_Dahsboard URL
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/dashboard/buyer/";
                return COMPANY_URL;

            case "seller_dashboard":
                //Seller_Dahsboard URL (Manufacturer)
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/dashboard/seller/";
                return COMPANY_URL;

            case "companytype":
                //COMPANYTYPE
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/types/";
                if(id != null && !id.isEmpty() && !id.equals("")){
                    COMPANY_URL = COMPANY_URL  + id + "/";
                }
                return COMPANY_URL;


            //BRANDS
            case "brands":
                //GET_MYBRANDS_URL
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/brands/";
                return COMPANY_URL;

            case "brandswithparams":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/brands/";
                if(id != null && !id.isEmpty() && !id.equals("")){
                    COMPANY_URL = COMPANY_URL  + id + "/";
                }
                return COMPANY_URL;

            case "brands_expand_false":
                //GET_BRANDS_URL or BRANDADD
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/brands?" + "expand=false";
                return COMPANY_URL;

         /*   case "brands_expand_falsewithparams":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id+"/";
                return COMPANY_URL;
*/
            case "brands_distributor":
                //GET_BRANDDISTRIBUTOR
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/brand-distributor/";
                return COMPANY_URL;

            case "brands_dropdown":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/brands/dropdown/";
                return COMPANY_URL;

            case "brands_i_sell_dropdown":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/brands/dropdown/?type=brandisell";
                return COMPANY_URL;


          /*  case "brands_expand_truewithparams":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id;
                return COMPANY_URL;
*/

            //CATALOG

            case "brands-follow":
                //POST_BRAND_FOLLOW_URL
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/brand-follow/";
                if(id != null && !id.isEmpty() && !id.equals("")){
                    COMPANY_URL = COMPANY_URL  + id + "/";
                }
                return COMPANY_URL;

           /* case "brands-unfollow":
                //GET_BRAND_UNFOLLOW_URL
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/brand-follow/" +id;
                return COMPANY_URL;
*/
            case "catalog_view_count":
                //ADDCATALOG
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/company-catalog-view/";
                return COMPANY_URL;

            case "upload_catalogs":
                //ADDCATALOG
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/catalogs/";
                return COMPANY_URL;





         /*   case "catalogs_suggestion":
                //ADDCATALOG
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/catalogs/suggestion/";
                return COMPANY_URL;*/

            case "screen-catalog-grouping":
                COMPANY_URL = APP_URL + "/api/v1/companies/"+company_id+ "/catalogs/?expand=true";
                return COMPANY_URL;

            case "catalogs_suggestion":
                //ADDCATALOG
                COMPANY_URL = APP_URL + "/api/v2/products/suggestion/";
                return COMPANY_URL;

            case "catalogs_upload_option":
                //ADDCATALOG
                COMPANY_URL = APP_URL + "/api/v1/catalog-upload-options/";
                return COMPANY_URL;

            case "catalogs_upload_option_with_id":
                //ADDCATALOG
                COMPANY_URL = APP_URL + "/api/v1/catalog-upload-options/?catalog=" + id;
                return COMPANY_URL;

            case "catalogs_received":
                //ADDCATALOG
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/catalogs/?view_type=myreceived";
                return COMPANY_URL;




            case "catalogs_supplier":
                //GET_CATALOGSUPPLIER
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/catalogs/" + id + "/suppliers/";
                return COMPANY_URL;





         /*   case "catalogs_expand_true_id":
                //companyUrl(getActivity(),"catalogs_expand_true","")
            COMPANY_URL = APP_URL + "/api/v2/companies/" + company_id + "/products/" + id + "/?expand=true";
            return COMPANY_URL;*/



            case "catalog-seller":
                // add seller for particular catalog
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/catalog-seller/";
                return COMPANY_URL;


            case "mycatalogs":
                //MYCATALOGS
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/catalogs/" + "?view_type=mycatalogs/";
                return COMPANY_URL;

            case "catalog_dropdown":
                //catalog_dropdown (id == search_keyword)
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/catalogs/dropdown/";
                return COMPANY_URL;

            case "dropdownvalidate":
                //catalog_dropdown (id == search_keyword)
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/catalogs/dropdownvalidate/";
                return COMPANY_URL;
            //SELECTIONS
            case "selections":
                //GET_SELECTIONS_URL
                COMPANY_URL = APP_URL + "/api/v1/selections/" + "?expand=true";
                return COMPANY_URL;

            case "selections_expand_false":
                //GET_SELECTIONS_URL
                COMPANY_URL = APP_URL + "/api/v1/selections/";
                return COMPANY_URL;

            case "selections_supplier":
                //GET_SELECTIONSUPPLIER
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/selections/" + id + "/suppliers/";
                return COMPANY_URL;

            case "selections_disable":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/selections/" + id + "/disable/";
                return COMPANY_URL;

            case "selections_enable":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/selections/" + id + "/enable/";
                return COMPANY_URL;


            //INVOICE

            case "invoice":
                //GET_INVOICE
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/invoice/";
                return COMPANY_URL;


            case "buyergroups":
                //GET_BUYERGROUPS
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/buyer-groups/";
                return COMPANY_URL;


            //WAREHOUSE
            case "warehouse":
                //GET_WAREHOUSE
                COMPANY_URL = APP_URL + "/api/v1/warehouse/";
                return COMPANY_URL;

            //SHARE
            case "pushes_with_id":
                //SHARECATALOG_URL
                COMPANY_URL = APP_URL + "/api/v1/shares/";
                if(id != null && !id.isEmpty() && !id.equals("")){
                    COMPANY_URL = COMPANY_URL  + id + "/";
                }
                return COMPANY_URL;

            case "pushes":
                //SHARECATALOG_URL
                COMPANY_URL = APP_URL + "/api/v1/shares/";
                return COMPANY_URL;

            case "mysent_catalog":
                //GET_MYSENT_CAT_URL
                COMPANY_URL = APP_URL + "/api/v1/shares/?view_type=mysent";
                return COMPANY_URL;

            //PRODUCTS

            case "products":
                //ADDPRODUCT or GETPRODUCTSBYCAT
                COMPANY_URL = APP_URL + "/api/v1/catalogs/" + id + "/products/" + "?expand=true";
                return COMPANY_URL;

            case "products_details":
                //ADDPRODUCT or GETPRODUCTSBYCAT
                COMPANY_URL = APP_URL + "/api/v1/products/" + id + "/?expand=true";
                return COMPANY_URL;






            case "single_product":
                //ADDPRODUCT or GETPRODUCTSBYCAT
                COMPANY_URL = APP_URL + "/api/v1/products/" + id + "?expand=true";
                return COMPANY_URL;

            case "products_selection":
                //GET_SELECTIONPRODUCTS_URL
                COMPANY_URL = APP_URL + "/api/v1/products/?selection=" + id;
                return COMPANY_URL;

            case "products_disable":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/products/" + id + "/disable/";
                return COMPANY_URL;

            case "products_enable":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/products/" + id + "/enable/";
                return COMPANY_URL;


            case "productlike":
                //PRODUCT_LIKE
                COMPANY_URL = APP_URL + "/api/v1/products/" + id + "/likes/";
                return COMPANY_URL;

            //Contacts

            case "contactsinvites":
                //INVITEARRAY
                COMPANY_URL = APP_URL + "/api/v1/contacts/invites/";
                return COMPANY_URL;

            case "contacts_onwishbook":
                //GET_WISHBOOK_CONTACTS
                COMPANY_URL = APP_URL + "/api/v1/contacts/onwishbook/";
                return COMPANY_URL;


            //BUYERS

            case "buyers_expand_true":
                //GET_BUYER
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/buyers/?" + "expand=true";
                return COMPANY_URL;

            //URLConstants.companyUrl(getActivity(),"sellers_expand_true","")
            case "buyers_rejected":
                // REJECTED_BUYERS
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/buyers/?" + "status=rejected";
                return COMPANY_URL;

            case "buyers_enquiry":
                //APPROVED_BUYERS
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/buyers/?" + "status=enquiry";
                return COMPANY_URL;

            case "buyers_approved":
                //APPROVED_BUYERS
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/buyers/?" + "status=approved";
                return COMPANY_URL;

            case "buyers_approved_chat":
                //APPROVED_BUYERS
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/buyers/?" + "status=approved&fields=buying_company_name,buying_company_chat_user";
                return COMPANY_URL;

            case "buyers_pending":
                //PENDING_BUYERS
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/buyers/?" + "status=pending";
                return COMPANY_URL;


            case "buyers_expand_false_statewise":
                //BUYERSONLY  STATEWISE
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/buyers/statewise/?" + "expand=false";
                return COMPANY_URL;


            case "buyers":
                //ADDBUYER or RESEND_BUYER
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/buyers/";
                return COMPANY_URL;

            case "buyer_transfer":
                //ADDBUYER or RESEND_BUYER
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/buyers/" + id + "/transfer/";
                return COMPANY_URL;

            case "buyerlist":
                //BUYER_LIST
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/buyers/dropdown/";
                return COMPANY_URL;

            case "buyers_group_list":
                //ADDBUYER or RESEND_BUYER
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/buyers/?buyer_segmentation=" + id;
                return COMPANY_URL;

            case "buyerlist_no_deputed":
                //BUYER_LIST
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/buyers/dropdown/?without_deputed=true";
                return COMPANY_URL;

            case "brokerlist":
                //GET_BROKERS
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/brokers/";
                return COMPANY_URL;

            //SUPPLIER

            case "sellers_rejected":
                //SUPPLIERS_REJECTED
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/suppliers/?" + "status=rejected";
                return COMPANY_URL;

            case "sellers_enquiry":
                //SUPPLIERS_APPROVED
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/suppliers/?" + "status=enquiry";
                return COMPANY_URL;

            case "sellers_approved":
                //SUPPLIERS_APPROVED
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/suppliers/?" + "status=approved";
                return COMPANY_URL;

            case "sellers_approved_chat":
                //SUPPLIERS_APPROVED
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/suppliers/?" + "status=approved&fields=selling_company_chat_user,selling_company_name";
                return COMPANY_URL;

            case "sellers_pending":
                //SUPPLIERS_PENDING
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/suppliers/?" + "status=pending";
                return COMPANY_URL;

            case "sellers":
                //ADDSELLER or RESEND_SELLER
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/suppliers/";
                return COMPANY_URL;

            case "public_supplier_detail":
                //public supplier details
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/suppliers/company_details/?company=" + id;
                return COMPANY_URL;

            case "suppliers_list":
                //SUPPLIER_LIST
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/suppliers/dropdown/";
                return COMPANY_URL;

            case "seller-policy":
                // GET SUPPLIER POLICY
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/seller-policy/?company=" + id;
                return COMPANY_URL;


            //ORDERS

            case "multiorder":
                //CREATEORDER or GET_SALES_ORDERS
                COMPANY_URL = APP_URL + "/api/v1/multiorder/";
                return COMPANY_URL;

            case "salesorder":
                //CREATEORDER or GET_SALES_ORDERS
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/sales-orders/";
                if(id != null && !id.isEmpty() && !id.equals("")){
                    COMPANY_URL = COMPANY_URL  + id + "/";
                }
                return COMPANY_URL;

            case "brokerage_order":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/brokerage-orders/";
                return COMPANY_URL;



            case "salesorders_transfer":
                //GET_SALES_ORDERS Catalogwise
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/sales-orders/" + id + "/transfer/";
                return COMPANY_URL;

            case "purchaseorder":
                //PURCHASE
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/purchase-orders/";
                if(id != null && !id.isEmpty() && !id.equals("")){
                    COMPANY_URL = COMPANY_URL  + id + "/";
                }
                return COMPANY_URL;

            case "payment":
                //PURCHASE
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/purchase-orders/" + id + "/payment/";
                return COMPANY_URL;

            case "create_invoice":
                //PURCHASE
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/order-invoice/";
                return COMPANY_URL;


            case "invoice_payment":
                //PURCHASE
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/order-invoice/" + id + "/payment/";
                return COMPANY_URL;

            case "invoice_dispatch":
                //PURCHASE
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/order-invoice/" + id + "/dispatched/";
                return COMPANY_URL;



            case "shipping-charges":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/shipping-charges/?order=" + id ;
                return COMPANY_URL;

            case "add_order_rating":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/order-rating/";
                return COMPANY_URL;

            case "order_rating":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/order-rating/?order=" + id;
                return COMPANY_URL;

            case "shipment":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/shipments/";
                return COMPANY_URL;

            //Broker
            case "add_buyers":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/suppliers/" + id + "/add_buyers/";
                return COMPANY_URL;

            case "add_suppliers":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/buyers/" + id + "/add_suppliers/";
                return COMPANY_URL;

            case "get_connected_buyers_broker":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/buyers/dropdown/?selling_company=" + id;
                return COMPANY_URL;

            case "get_connected_suppliers_broker":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/suppliers/dropdown/?buying_company=" + id;
                return COMPANY_URL;

            case "remove_connected_buyers":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/suppliers/" + id + "/remove_buyers/";
                Log.i("TAG", "companyUrl: ==>" + COMPANY_URL);
                return COMPANY_URL;

            case "remove_connected_suppliers":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/buyers/" + id + "/remove_suppliers/";
                return COMPANY_URL;

            case "purchase_brokerage_orders_catalogwise":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/brokerage-orders/" + id + "/catalogwise/?expand=true";
                return COMPANY_URL;

            case "brokerageorder":
                //PURCHASE
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/brokerage-orders/";
                return COMPANY_URL;


            //SYNCACTIVITYLOG

            case "syncactivitylog":
                //PRODUCT_LIKE
                COMPANY_URL = APP_URL + "/api/v1/syncactivitylog/";
                return COMPANY_URL;

            case "company_otp_url":
                //otp register and validate for company profile
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/phone_number/";
                return COMPANY_URL;

            case "company_types":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/types/";
                return COMPANY_URL;

            case "company_kyc":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/kyc/";
                return COMPANY_URL;

            case "bank_details":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/bank-details/";
                return COMPANY_URL;

            case "address":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/address/";
                return COMPANY_URL;

            case "my_followers":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/brands/followers/";
                return COMPANY_URL;

            case "brands_permission":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/brands/has_permission/";
                return COMPANY_URL;

            case "catalog_all_seller":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/catalogs/" + id + "/all_suppliers/";
                return COMPANY_URL;

            case "suggested_broker":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/buyers/" + id + "/suggested_brokers/";
                return COMPANY_URL;



            case "credit-rating":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/credit-rating/?company=" + id;
                return COMPANY_URL;

            case "credit-reference":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/credit-reference/?buying_company=" + id;
                return COMPANY_URL;


            case "my-viewers":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/catalogs/my-viewers/";
                return COMPANY_URL;

            case "my-viewers-live":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/catalogs/my-viewers-live/";
                return COMPANY_URL;

            case "catalog-viewers":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/catalogs/" + id + "/catalog-viewers";
                return COMPANY_URL;

            case "solo-propreitorship-kyc":
                //ADDCATALOG
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/solo-propreitorship-kyc/";
                return COMPANY_URL;

            case "catalog-enquiries" :
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/catalog-enquiries/";
                return COMPANY_URL;

            case "buyer-leads" :
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/catalog-enquiries/buyer-leads/";
                return COMPANY_URL;
            case "add-credit-reference":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/credit-reference/";
                return COMPANY_URL;



            case "user-credit-submissions":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/user-credit-submissions/";
                return COMPANY_URL;


            case "wbmoney-log":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/wbmoney-log/";
                return COMPANY_URL;

            case "wbmoney-log-dashboard":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/wbmoney-log/dashboard/";
                return COMPANY_URL;


            case "placetoship":
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/sales-orders/"+ id +"/placetoship/";
                return COMPANY_URL;





            default:
                COMPANY_URL = APP_URL + "/api/v1/companies/" + company_id + "/";
                return COMPANY_URL;
        }



    }
    //</editor-region>


    /**
     * Define all user nested urls
     * @param context
     * @param type
     * @param id
     * @return - user nested urls
     */
    public static final String userUrl(final Context context, String type, String id) {
        String USER_URL;
        switch (type) {
            case "new_registration":
                //NEW_REGISTER_USER
                USER_URL = APP_URL + "/api/v1/users/" + id;
                return USER_URL;

            case "user_platform":
                //STORE USER DEVICE INFO
                USER_URL = APP_URL + "/api/v1/users/" + id + "/platform/";
                return USER_URL;
        }
        if (UserInfo.getInstance(context).getUserId() != null
                && !UserInfo.getInstance(context).getUserId().isEmpty()) {
            switch (type) {
                case "attendance":
                    //GET_ATTENDANCE
                    USER_URL = APP_URL + "/api/v1/users/" + UserInfo.getInstance(context).getUserId() + "/attendance/";
                    return USER_URL;
                case "meetings":
                    //MEETINGS
                    USER_URL = APP_URL + "/api/v1/users/" + UserInfo.getInstance(context).getUserId() + "/meetings/";
                    return USER_URL;

                case "meetings_with_id":
                    //MEETINGS
                    USER_URL = APP_URL + "/api/v1/users/" + UserInfo.getInstance(context).getUserId() + "/meetings/" + id + "/";

                    return USER_URL;

                case "meetingsreport":
                    //MEETINGREPORT
                    USER_URL = APP_URL + "/api/v1/users/" + UserInfo.getInstance(context).getUserId() + "/meetings/report/";
                    return USER_URL;

                case "profile_number_verify":
                    //MEETINGREPORT
                    USER_URL = APP_URL + "/api/v1/users/" + UserInfo.getInstance(context).getUserId() + "/phone_number/";
                    return USER_URL;
                case "wishlist-catalog":
                    //Show Wishlist Catalog
                    USER_URL = APP_URL + "/api/v2/wishlist/";
                    return USER_URL;

                case "wishlist-delete-product":
                    USER_URL = APP_URL + "/api/v2/wishlist/delete-product/";
                    return USER_URL;

                case "wishlist-product":
                    //Show Wishlist Catalog
                    USER_URL = APP_URL + "/api/v2/wishlist/products/";
                    return USER_URL;

                case "recent-catalog":
                    //Show Wishlist Catalog
                    USER_URL = APP_URL + "/api/v2/products/recently-viewed/";
                    return USER_URL;

                case "stats":
                    // For Send Analytics
                    USER_URL = APP_URL + "/api/v1/users/" + UserInfo.getInstance(context).getUserId() + "/stats/";
                    return USER_URL;

                case "user-recommendation" :
                    USER_URL = APP_URL + "/api/v2/users/" + UserInfo.getInstance(context).getUserId() + "/recommendation/";
                    return USER_URL;

                case "notification-preference":
                    USER_URL = APP_URL + "/api/v2/users/" + UserInfo.getInstance(context).getUserId() + "/notification-preference/";
                    return USER_URL;


                default:
                    USER_URL = APP_URL + "/api/v1/users/" + UserInfo.getInstance(context).getUserId() + "/";
                    return USER_URL;
            }
        } else {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                    .title("Sorry! Your cache memory may be full!")
                    .content("Please Logout and then Login once to clear cache")
                    .positiveText("Yes")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            try {
                                LogoutCommonUtils.logout((Activity) context, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
            if (materialDialog != null && !materialDialog.isShowing()) {
                materialDialog = builder.build();
                materialDialog.show();
            }
            return "";
        }


    }
}