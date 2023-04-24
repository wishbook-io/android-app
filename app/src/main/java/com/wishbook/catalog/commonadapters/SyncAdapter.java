package com.wishbook.catalog.commonadapters;

import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.MyContacts;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.commonmodels.responses.CatalogMinified;
import com.wishbook.catalog.commonmodels.responses.Response_Buyer;
import com.wishbook.catalog.commonmodels.responses.Response_BuyerFull;
import com.wishbook.catalog.commonmodels.responses.Response_Catagories;
import com.wishbook.catalog.commonmodels.responses.Response_Product;
import com.wishbook.catalog.commonmodels.responses.Response_Selection_Detail;
import com.wishbook.catalog.commonmodels.responses.Response_SellerFull;
import com.wishbook.catalog.commonmodels.responses.Response_Suppliers;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.commonmodels.responses.Response_sellingoder_catalog;
import com.wishbook.catalog.commonmodels.responses.Response_sellingorder;
import com.wishbook.catalog.commonmodels.responses.Response_sellingorder_new;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.commonmodels.SyncModel;
import com.wishbook.catalog.commonmodels.UploadContactsModel;
import com.wishbook.catalog.home.models.Response_Brands;
import com.wishbook.catalog.commonmodels.responses.Response_Selection;
import com.wishbook.catalog.commonmodels.responses.Response_ShareStatus;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.home.more.Fragment_Sync;
import com.wishbook.catalog.login.models.Response_States;

/**
 * Created by vignesh_streamoid on 14/05/16.
 */
public class SyncAdapter extends  RecyclerView.Adapter<SyncAdapter.ViewHolder>{

    private List<MyContacts> contactList = new ArrayList<>();
    private final AppCompatActivity context;
    private final ArrayList<SyncModel> itemsInSection;
    private String RECEIVEDCATALOGS ="Received Catalogs";
    private String RECEIVEDSELECTIONPRODUCTS ="Received Selections Products";
    private String MYSINGLECATALOG ="Catalogs";
    private String BRANDSCATALOG ="Brands Catalog";
    private String CATEGORIESCATALOG ="Categories Catalog";
    private String MYSELECTIONPRODUCTS ="Selection Products";
    private String BUYERS ="Buyers";
    private String SUPPLIERS ="Suppliers";
    private String SALES ="Sales";
    private String PURCHASE ="Purchase";
    private String CITIES ="Cities";




    String Progress="true";
    ArrayList<String> imageUrls=new ArrayList<>();
    MainSyncFunction obj;
    int countersuccess=0;
    int sucesscounter=0;
    public SyncAdapter(AppCompatActivity context, ArrayList<SyncModel> itemsInSection) {
        this.context=context;
this.itemsInSection=itemsInSection;

    }

    public void sync(String progress){
        imageUrls.clear();
         countersuccess=0;
         sucesscounter=0;
        Progress=progress;
        obj = new MainSyncFunction();
      obj.execute();
       /* for(SyncModel sm:itemsInSection){
            callHttp(sm);
            if(sm.getSyncTitle().equals("Contacts")){
               new GetAllContacts().execute();
           }


        }*/
    }

    private void callHttp(final SyncModel sm) {
        if(!sm.getSyncTitle().equals("Download Images")&!sm.getSyncTitle().equals("Contacts")){
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
            HttpManager.getInstance(context).request(HttpManager.METHOD.GET, sm.getSyncUrl(), null, headers, true, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    Log.v("cached response", response);
                     onServerResponse(response, false);

                    //notifyDataSetChanged();
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {

                    Log.v("sync response", response);
                   completedDirectly(itemsInSection,sm);

                    //RECEIVED CATALOG AND SELECTIONS
                    if(sm.getSyncTitle().equals(Fragment_Sync.SHAREDWITHME))
                    {
                        CatalogMinified[] response_catalogs = Application_Singleton.gson.fromJson(response, CatalogMinified[].class);
                        for(CatalogMinified res:response_catalogs)
                        {
                            imageUrls.add(res.getBrand_image());
                            imageUrls.add(res.getThumbnail().getThumbnail_medium());
                            if(res.getType().equals("catalog")) {
                                getReceivedCatalogdetails(res.getId());
                            }
                            else if(res.getType().equals("selection"))
                            {
                                getReceivedSelectionProducts(res.getId());
                            }
                        }
                        completedIndirectly(Fragment_Sync.SHAREDWITHME,itemsInSection);
                    }
                  /*  if(sm.getSyncTitle().equals(RECEIVEDCATALOGS))
                    {
                        Response_catalogapp[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalogapp[].class);
                        for (Response_catalogapp resb : response_catalog) {
                            imageUrls.add(resb.getThumbnail().getThumbnail_small());
                            imageUrls.add(resb.getThumbnail().getThumbnail_medium());
                            getMyCatalogdetails(resb.getId());
                            Log.v("imagesurl", "" + imageUrls.size());
                        }

                    }*/


                    if(sm.getSyncTitle().equals(RECEIVEDSELECTIONPRODUCTS))
                    {
                        Gson gson = new Gson();
                        Response_Selection_Detail response_productss = gson.fromJson(response, Response_Selection_Detail.class);
                        final ProductObj[] productObjs = response_productss.getProducts();
                        for(ProductObj prod:productObjs)
                        {
                            imageUrls.add(prod.getImage().getThumbnail_small());
                            imageUrls.add(prod.getImage().getThumbnail_medium());
                        }


                    }


                    //MY SELECTION PRODUCTS
                    if(sm.getSyncTitle().equals(MYSELECTIONPRODUCTS))
                    {
                        ProductObj[] productObjs = Application_Singleton.gson.fromJson(response, ProductObj[].class);
                        for(ProductObj prod:productObjs)
                        {
                            imageUrls.add(prod.getImage().getThumbnail_small());
                            imageUrls.add(prod.getImage().getThumbnail_medium());
                        }

                    }

                    //CATEGORIES CATALOGS
                    if(sm.getSyncTitle().equals(Fragment_Sync.CATEGORIES))
                    {
                        Response_Catagories[] response_catagories= Application_Singleton.gson.fromJson(response, Response_Catagories[].class);
                        for(Response_Catagories res:response_catagories)
                        {
                            imageUrls.add(res.getImage());
                            getCategorywiseCatalog(res.getId());
                        }
                        completedIndirectly(Fragment_Sync.CATEGORIES,itemsInSection);
                    }
                  /*  if(sm.getSyncTitle().equals(CATEGORIESCATALOG))
                    {
                        completedIndirectly(Fragment_Sync.CATEGORIES,itemsInSection);
                    }*/
                    //STATES
                    if(sm.getSyncTitle().equals(Fragment_Sync.STATES))
                    {
                        Response_States[] response_states = Application_Singleton.gson.fromJson(response, Response_States[].class);
                    for(Response_States states:response_states)
                    {
                        getCities(states.getId());
                    }
                        completedIndirectly(Fragment_Sync.STATES,itemsInSection);
                    }


                    //BUYERS APPROVED
                    if(sm.getSyncTitle().equals(Fragment_Sync.APPROVEDBUYERS))
                    {
                        Response_Buyer[] response_buyer = Application_Singleton.gson.fromJson(response, Response_Buyer[].class);
                        for (Response_Buyer resb : response_buyer) {
                           getBuyerDetails(resb.getId());
                        }
                        completedIndirectly(Fragment_Sync.APPROVEDBUYERS,itemsInSection);
                    }

                    if(sm.getSyncTitle().equals(BUYERS))
                    {
                        Response_BuyerFull response_buyerfull = new Gson().fromJson(response, Response_BuyerFull.class);
                            imageUrls.add(response_buyerfull.getBuying_company().getThumbnail());

                    }

                    //SUPPLIER APPROVED
                    if(sm.getSyncTitle().equals(Fragment_Sync.APPROVEDSUPPLIERS))
                    {
                        Response_Suppliers[] response_supplier = Application_Singleton.gson.fromJson(response, Response_Suppliers[].class);
                        for (Response_Suppliers resb : response_supplier) {
                            getSupplierDetails(resb.getId());
                        }
                        completedIndirectly(Fragment_Sync.APPROVEDSUPPLIERS,itemsInSection);
                    }

                    if(sm.getSyncTitle().equals(SUPPLIERS))
                    {
                        Response_SellerFull response_sellerfull = new Gson().fromJson(response, Response_SellerFull.class);
                        imageUrls.add(response_sellerfull.getBuying_company().getThumbnail());
                    }


                    //SALES ORDER
                    if(sm.getSyncTitle().equals(Fragment_Sync.SALESORDERS))
                    {
                        Response_sellingorder[] response_salesorder = new Gson().fromJson(response, Response_sellingorder[].class);
                        for(Response_sellingorder resb:response_salesorder)
                        {
                            getCatalogOrderwise(resb.getId());
                        }
                    }

                    if(sm.getSyncTitle().equals(SALES))
                    {
                        Response_sellingorder_new response_salesorder = new Gson().fromJson(response, Response_sellingorder_new.class);
                        for(Response_sellingoder_catalog resb:response_salesorder.getCatalogs())
                        {
                            for(Response_Product prod:resb.getProducts())
                            {
                                imageUrls.add(prod.getProduct_image());
                            }
                        }
                        completedIndirectly(Fragment_Sync.SALESORDERS,itemsInSection);


                    }

                    //PURCHASE ORDER
                    if(sm.getSyncTitle().equals(Fragment_Sync.PURCHASEORDERS))
                    {
                        Response_buyingorder[] response_sbuyingorder = new Gson().fromJson(response, Response_buyingorder[].class);
                        for(Response_buyingorder resb:response_sbuyingorder)
                        {
                            getCatalogOrderwisePurchase(resb.getId());
                        }
                    }

                    if(sm.getSyncTitle().equals(PURCHASE))
                    {
                        Response_buyingorder response_buyorder = new Gson().fromJson(response, Response_buyingorder.class);
                        for(Response_sellingoder_catalog resb:response_buyorder.getCatalogs())
                        {
                            for(Response_Product prod:resb.getProducts())
                            {
                                imageUrls.add(prod.getProduct_image());
                            }
                        }
                        completedIndirectly(Fragment_Sync.PURCHASEORDERS,itemsInSection);

                    }

                    //BRANDS
                    if(sm.getSyncTitle().equals(Fragment_Sync.BRANDS)){
                        Response_Brands[] response_brands = Application_Singleton.gson.fromJson(response, Response_Brands[].class);
                        for (Response_Brands resb : response_brands) {
                            imageUrls.add(resb.getImage().getThumbnail_small());
                            //todo catalogs
                            getBrandwiseCatalog(resb.getId());
                            Log.v("imagesurl", "" + imageUrls.size());
                        }
                        completedIndirectly(Fragment_Sync.BRANDS,itemsInSection);
                    }
                    /*if(sm.getSyncTitle().equals(BRANDSCATALOG))
                    {
                        completedIndirectly(Fragment_Sync.BRANDS,itemsInSection);
                    }*/

                    //MY CATALOGS
                    if(sm.getSyncTitle().equals(Fragment_Sync.MYCATALOGS)) {
                        Response_catalogMini[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                        for (Response_catalogMini resb : response_catalog) {
                            imageUrls.add(resb.getThumbnail().getThumbnail_small());
                            imageUrls.add(resb.getThumbnail().getThumbnail_medium());
                            getMyCatalogdetails(resb.getId());
                            Log.v("imagesurl", "" + imageUrls.size());

                        }
                        completedIndirectly(Fragment_Sync.MYCATALOGS,itemsInSection);
                    }

                    //EACH CATALOG OF MY CATALOG
                    if(sm.getSyncTitle().equals(MYSINGLECATALOG)) {
                        try {
                            final Response_catalog response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                            for (ProductObj resb : response_catalog.getProducts()) {
                                imageUrls.add(resb.getImage().getThumbnail_small());
                                imageUrls.add(resb.getImage().getThumbnail_medium());
                                Log.v("imagesurl", "" + imageUrls.size());
                            }
                        }
                        catch (Exception e){
                            final Response_catalog[] response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog[].class);
                            for (Response_catalog resb : response_catalog) {
                                for (ProductObj respro : resb.getProducts()) {
                                    imageUrls.add(respro.getImage().getThumbnail_small());
                                    imageUrls.add(respro.getImage().getThumbnail_medium());
                                    Log.v("imagesurl", "" + imageUrls.size());
                                }
                            }


                        }

                    }

                    //MY SELECTIONS
                    if(sm.getSyncTitle().equals(Fragment_Sync.MYSELECTIONS)) {
                        Response_Selection[] response_selections = Application_Singleton.gson.fromJson(response, Response_Selection[].class);
                        for (Response_Selection resb : response_selections) {
                            imageUrls.add(resb.getImage());
                            getSelectionProducts(resb.getId());
                            Log.v("imagesurl", "" + imageUrls.size());

                        }
                        completedIndirectly(Fragment_Sync.MYSELECTIONS,itemsInSection);
                    }

                    //CATALOG SENT BY ME
                    if(sm.getSyncTitle().equals(Fragment_Sync.SHAREDBYME)) {
                        Response_ShareStatus[] response_shareStatuses = Application_Singleton.gson.fromJson(response, Response_ShareStatus[].class);
                        for (Response_ShareStatus resb : response_shareStatuses) {
                            imageUrls.add(resb.getImage());
                            Log.v("imagesurl", "" + imageUrls.size());
                        }
                        completedIndirectly(Fragment_Sync.SHAREDBYME,itemsInSection);
                    }




                }

                @Override
                public void onResponseFailed(ErrorString error) {

                }
            });
        }


    }

    private void completedIndirectly(String type, ArrayList<SyncModel> itemsInSection) {
        for(int i=0;i<itemsInSection.size();i++)
        {
            if(itemsInSection.get(i).getSyncTitle().equals(type))
            {
                itemsInSection.get(i).setSyncstatus(true);
                sucesscounter++;
                obj.doProgress();
            }
        }
    }

    private void completedDirectly(ArrayList<SyncModel> itemsInSection, SyncModel sm) {
        switch (sm.getSyncTitle())
        {
            case Fragment_Sync.DASHBOARD:
                setCompleted(itemsInSection,sm.getSyncTitle());
                break;
            case Fragment_Sync.MYBRANDS:
                setCompleted(itemsInSection,sm.getSyncTitle());
                break;
            case Fragment_Sync.GROUPTYPES:
                setCompleted(itemsInSection,sm.getSyncTitle());
                break;
            case Fragment_Sync.PENDINGBUYERS:
                setCompleted(itemsInSection,sm.getSyncTitle());
                break;
            case Fragment_Sync.REJECTBUYERS:
                setCompleted(itemsInSection,sm.getSyncTitle());
                break;
            case Fragment_Sync.PENDINGSUPPLIERS:
                setCompleted(itemsInSection,sm.getSyncTitle());
                break;
            case Fragment_Sync.REJECTSUPPLIERS:
                setCompleted(itemsInSection,sm.getSyncTitle());
                break;
            case Fragment_Sync.BUYERGROUPS:
                setCompleted(itemsInSection,sm.getSyncTitle());
                break;
            case Fragment_Sync.MEETINGS:
                setCompleted(itemsInSection,sm.getSyncTitle());
                break;
            case Fragment_Sync.MEETINGREPORTS:
                setCompleted(itemsInSection,sm.getSyncTitle());
                break;
        }
    }

    private void setCompleted(ArrayList<SyncModel> itemsInSection, String syncTitle) {
        for(int i=0;i<itemsInSection.size();i++){
            if(itemsInSection.get(i).getSyncTitle().equals(syncTitle)){
                itemsInSection.get(i).setSyncstatus(true);
                sucesscounter++;
                obj.doProgress();
            }
        }
    }

    private void getCategorywiseCatalog(String id) {
        callHttp(new SyncModel(URLConstants.companyUrl(context,"catalogs","")+"?category="+id, false, CATEGORIESCATALOG));
    }
    private void getBrandwiseCatalog(String id) {
        callHttp(new SyncModel(URLConstants.companyUrl(context,"catalogs","")+"?brand="+id, false, BRANDSCATALOG));
    }

    private void getReceivedSelectionProducts(String id) {
        callHttp(new SyncModel(URLConstants.companyUrl(context,"selections_expand_false","")+id+"/?expand=true"  , false, RECEIVEDSELECTIONPRODUCTS));
    }
    private void getSelectionProducts(String id) {
        callHttp(new SyncModel(URLConstants.companyUrl(context,"selections_expand_false","")+id+"/?expand=true&& type=my", false,MYSELECTIONPRODUCTS));
    }

    private void getCities(String id) {
        callHttp(new SyncModel(URLConstants.companyUrl(context,"city",id), false, CITIES));
    }
    private void getMyCatalogdetails(String id) {
        callHttp(new SyncModel(URLConstants.companyUrl(context,"catalogs_expand_true_id",id), false, MYSINGLECATALOG));
    }
    private void getReceivedCatalogdetails(String id) {
        callHttp(new SyncModel(URLConstants.companyUrl(context,"catalogs","") + id + "/", false, RECEIVEDCATALOGS));
    }

    private void getBuyerDetails(String id) {
        callHttp(new SyncModel(URLConstants.companyUrl(context,"buyers","")+ id + "/?expand=true", false, BUYERS));
    }

    private void getSupplierDetails(String id) {
        callHttp(new SyncModel(URLConstants.companyUrl(context,"sellers","") + id + "/?expand=true", false, SUPPLIERS));
    }
    private void getCatalogOrderwise(String id) {
        callHttp(new SyncModel(URLConstants.companyUrl(context,"salesorders_catalogwise",id), false, SALES));
    }
    private void getCatalogOrderwisePurchase(String id) {
        callHttp(new SyncModel(URLConstants.companyUrl(context,"purchaseorders_catalogwise",id), false, PURCHASE));

    }

    @Override
    public SyncAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sync_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SyncAdapter.ViewHolder holder, final int position) {

        holder.synctext.setText(itemsInSection.get(position).getSyncTitle());

        if(itemsInSection.get(position).isSyncstatus()){
            holder.synctick.setVisibility(View.VISIBLE);
            holder.progress.setVisibility(View.INVISIBLE);
            int successNos=getSuccesNos(itemsInSection);
            Log.v("totalsucess",itemsInSection.get(position).getSyncTitle()+successNos);


        }else {

            holder.synctick.setVisibility(View.INVISIBLE);
            if(Progress.equals("false"))
            {
                holder.progress.setVisibility(View.VISIBLE);
            }
        }
    }

    private int getSuccesNos(ArrayList<SyncModel> itemsInSection) {
        int i=0;
        for (SyncModel syn:itemsInSection) {
            if(syn.isSyncstatus()){
                i++;
            }
        }
        return i;
    }

    @Override
    public int getItemCount() {
        return itemsInSection.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView synctick;
        private final TextView synctext;
        public ProgressBar progress;

        public ViewHolder(View itemView) {
            super(itemView);
            synctick=(ImageView)itemView.findViewById(R.id.synctick);
            synctext=(TextView)itemView.findViewById(R.id.synctext);
            progress=(ProgressBar) itemView.findViewById(R.id.progress);


        }
    }
    private void rtt() {
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ","").trim();
            contactList.add(new MyContacts(phoneNumber,null,name,name,"`"));
        }
        phones.close();
    }



private class DownloadImage extends AsyncTask<ArrayList<String>,Void,Void> {

    @Override
    protected Void doInBackground(ArrayList<String>... params) {
        for (String image : imageUrls) {
            if(image!=null) {
                if (!image.equals("")) {
                    Log.v("counter", String.valueOf(countersuccess));
                    Log.d("imgaesize", String.valueOf(imageUrls.size()));
                    Log.v("downloading imges", image);

                    DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                            .cacheOnDisc(true).build();
                    ImageLoader.getInstance().loadImageSync(image, options);
                   /* ImageLoader.getInstance().loadImage(image, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            countersuccess++;
                            Log.v("imagedown", "" + "" + countersuccess);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            super.onLoadingFailed(imageUri, view, failReason);
                            Log.v("imagedown", "" + "failed");
                            countersuccess++;
                        }
                    });*/
                    countersuccess++;

                } else {
                    countersuccess++;
                }
            }
            else
            {
                countersuccess++;

            }


        }


        return null;

    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        notifyDataSetChanged();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        for (int i = 0; i < itemsInSection.size(); i++) {
            if (itemsInSection.get(i).getSyncTitle().equals("Download Images")) {
                itemsInSection.get(i).setSyncstatus(true);

            }
        }
    }
}

    private class MainSyncFunction extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... params) {

            for(SyncModel sm:itemsInSection) {
                callHttp(sm);
            }
            return null;
        }

        public void doProgress()
        {
            publishProgress();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void strings) {
            super.onPostExecute(strings);
                Set<String> hs = new HashSet<>();
                hs.addAll(imageUrls);
                imageUrls.clear();
                imageUrls.addAll(hs);
                new DownloadImage().execute(imageUrls);

        }
    }





    private class GetAllContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            rtt();
            Collections.sort(contactList, new Comparator<MyContacts>() {
                @Override
                public int compare(MyContacts lhs, MyContacts rhs) {
                    return lhs.getCompany_name().compareToIgnoreCase(rhs.getName());
                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(context);
            ArrayList<NameValues> cons=new ArrayList<>();
            UploadContactsModel uploadContactsModel=new UploadContactsModel(cons);

            for(MyContacts contact:contactList){
                cons.add(new NameValues(contact.getName(),contact.getPhone()));
            }
            uploadContactsModel.setContacts(cons);
            HttpManager.getInstance(context).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(context,"contacts_onwishbook",""), new Gson().fromJson(new Gson().toJson(uploadContactsModel), JsonObject.class), headers, true, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    Log.v("cached response", response);
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    Log.v("sync response", response);

                    MyContacts[] myContacts = Application_Singleton.gson.fromJson(response, MyContacts[].class);
                    for (MyContacts myContacts1 : myContacts) {
                      imageUrls.add(myContacts1.getCompany_image());
                    }

                    for(int i=0;i<itemsInSection.size();i++){
                        if(itemsInSection.get(i).getSyncTitle().equals("Contacts")){
                            itemsInSection.get(i).setSyncstatus(true);
                            notifyDataSetChanged();

                        }

                    }
                }


                @Override
                public void onResponseFailed(ErrorString error) {

                }
            });
        }
    }

}