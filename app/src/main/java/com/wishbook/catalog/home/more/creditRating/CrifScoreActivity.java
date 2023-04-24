package com.wishbook.catalog.home.more.creditRating;

import androidx.appcompat.app.AppCompatActivity;

public class CrifScoreActivity extends AppCompatActivity {

    private String orderId = "";
    private String accessCode = "";
    private String appId = "X@Wqxuwz#nElygI3!";
    private String mercchantid = "DTC0000017";
    private String encRequest = "";
    private String customerId = "DTC0000017";
    private String userId = "chm_bbc_uat@wishbook.io";
    private String password = "0DC8E5D8BDA6DC81C181AF658C01D5E2363D93ED";
    private String productCode = "BBC_CONSUMER_SCORE#85#2.0";
    private String encKey = "782B62D78F950A61E30447965B8B45450A9ABA5E";


    private String TAG = "CrifScoreActivity";




/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crif_score);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            callStage1();
            //callStage2("");
            //callStage3();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void callStage1() throws IOException {


        Random rand = new Random();

        int  n = rand.nextInt(500) + 1;

        orderId = "WB"+n;



        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String timestamp = s.format(new Date());
        String accessCodeString = userId+"|"+customerId+"|"+productCode+"|"+password+"|"+timestamp;


        accessCode = getEncryptedString(accessCodeString,encKey, "E");
        Log.d(TAG,"Stage1: accessCode:"+accessCodeString);

        //case 1
        //String requestString="Nitin||Jain||10-10-1986|||02228673678|||"+"jayp@wishbook.io"+"||AHJPT6190N||||||||NA|||307, BHARAT CHAMBER 52 BARODA STREET P.D MELLO ROAD, CARNAC BUNDER|Mumbai|Mumbai|Maharashtra|400009|india|||||||"+ customerId+"|"+productCode+"|Y|";

        //case 2
        //String requestString="Shashikiran|Goud|Jalla|Male||45||123456789|||"+"jayp@wishbook.io"+"||AFUPJ7365N||LHR/1631969||000538/060257|UID900|||PRAKASH|||GANGA ELECTRICALS,  SHOP NO 3 JAIN MANDIR|SAHARANPUR|SAHARANPUR|UP|247001|India|||||||"+ customerId+"|"+productCode+"|Y|";

        //case 3
        //String requestString="Naresh||Pruthi|Male||45||123456789|||"+"jayp@wishbook.io"+"||AFUPJ7365N||LHR/1631969||000538/060257|UID900|||PRAKASH|||GANGA ELECTRICALS,  SHOP NO 3 JAIN MANDIR|SAHARANPUR|SAHARANPUR|UP|247001|India|||||||"+ customerId+"|"+productCode+"|Y|";

        //case 4
        String requestString="Shailesh||Borkar|Male||45||123456789|||"+"jayp@wishbook.io"+"||AFUPJ7365N||LHR/1631969||000538/060257|UID900|||PRAKASH|||GANGA ELECTRICALS,  SHOP NO 3 JAIN MANDIR|SAHARANPUR|SAHARANPUR|UP|247001|India|||||||"+ customerId+"|"+productCode+"|Y|";


        encRequest =  getEncryptedString(requestString,accessCode, "E");
        Log.d(TAG,"Stage1: requestString:"+requestString);




        String responseX="";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost getRequest = new HttpPost("https://test.crifhighmark.com/Inquiry/do.getSecureService/service");
        // Add additional header to getRequest which accepts application/xml data
        getRequest.addHeader("accept", "application/xml");
        //set request parameter in header
        getRequest.setHeader("orderId",orderId);//pass order ID
        getRequest.setHeader("accessCode", accessCode); // pass accessCode
        getRequest.setHeader("appID",appId);//pass Application ID(android or ios)
        getRequest.setHeader("encRequest",encRequest); //pass encrypted inquiry request
        getRequest.setHeader("merchantID",mercchantid ); //pass merchant id
        // Execute your request and catch response HttpResponse response =
        HttpResponse response = httpClient.execute(getRequest);

        // Check for HTTP response code: 200 = success
        if (response.getStatusLine().getStatusCode() == 200) {
            Log.d(TAG,"Stage1: success");
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();


                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(instream));
                    // do something useful with the response
                    responseX = reader.readLine();

                    Log.d(TAG, "Stage1Response: " + responseX);

                    String decrypted = getEncryptedString(responseX, encKey, "D");

                    Log.d(TAG, "Stage1Response decrypted: " + decrypted);
                    callStage2(decrypted, "Null");
                    httpClient.getConnectionManager().shutdown();
                } catch (IOException ex) {
                    // In case of an IOException the connection will be released
                    // back to the connection manager automatically
                    throw ex;
                } catch (RuntimeException ex) {
                    // In case of an unexpected exception you may want to abort
                    // the HTTP request in order to shut down the underlying
                    // connection and release it back to the connection manager.
                    getRequest.abort();
                    throw ex;
                } finally {
                    // Closing the input stream will trigger connection release
                    instream.close();
                }
                // When HttpClient instance is no longer needed,
                // shut down the connection manager to ensure
                // immediate deallocation of all system resources

            }
        }
        else {
            Log.d(TAG,"Stage1: not 200 " + response.getStatusLine().getStatusCode());

        }






    }

    public void callStage2(String stage1Response, String userAns) throws IOException {

       // stage1Response = "wb5|CCR180605CR41314846|S06|https://cir.crifhighmark.com/Inquiry/B2B/secureService.action";
        String[] stringArray = stage1Response.split("\\|");

        orderId = stringArray[0];
        Log.d(TAG,"Stage2: orderId:"+orderId.toString());
        String reportId = stringArray[1];
        Log.d(TAG,"Stage2: reportId:"+reportId.toString());


        String redirectURL = "https://cir.crifhighmark.com/Inquiry/B2B/secureService.action";

        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String timestamp = s.format(new Date());
        String accessCodeString = userId+"|"+customerId+"|"+productCode+"|"+password+"|"+timestamp;

        accessCode = getEncryptedString(accessCodeString, encKey, "E");
        Log.d(TAG,"Stage2: accessCode:"+accessCodeString);

        String requestString = orderId+"|"+reportId+"|"+accessCode+"|"+redirectURL+"|N|N|Y|";
        String encRequest2 =  getEncryptedString(requestString, encKey, "E");
        Log.d(TAG,"Stage2: requestString:"+requestString);



        String responseX="";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost getRequest = new HttpPost("https://test.crifhighmark.com/Inquiry/do.getSecureService/response");
        // Add additional header to getRequest which accepts application/xml data
        getRequest.addHeader("accept", "application/xml");
        //set request parameter in header
        getRequest.setHeader("orderId",orderId);//pass order ID
        getRequest.setHeader("reportId",reportId);//pass order ID
        getRequest.setHeader("userAns",userAns);
        getRequest.setHeader("accessCode", accessCode); // pass accessCode
        getRequest.setHeader("appID",appId);//pass Application ID(android or ios)
        getRequest.setHeader("encRequest",encRequest2); //pass encrypted inquiry request
        getRequest.setHeader("merchantID",mercchantid ); //pass merchant id
        getRequest.addHeader("requestType", "Authorization");

        // Execute your request and catch response HttpResponse response =
        HttpResponse response = httpClient.execute(getRequest);

        // Check for HTTP response code: 200 = success
        if (response.getStatusLine().getStatusCode() == 200) {
            Log.d(TAG,"Stage2: success");
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();


                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(instream));
                    // do something useful with the response
                    responseX = reader.readLine();

                    Log.d(TAG, "Stage2Response: " + responseX);

                    String decrypted = getEncryptedString(responseX, encKey, "D");

                    Log.d(TAG, "Stage2Response decrypted: " + decrypted);

                    String[] resArray = decrypted.split("\\|");
                    String status = resArray[2];
                    Log.d(TAG, "Stage2Response Status: " + status);

                    if(status.equals("S11")){

                        Log.d(TAG, "Stage2 : Need to answer ques" + status);

                        if(resArray.length > 3){
                            String ques = resArray[3];
                            Log.d(TAG, "Stage2 Ques: " + ques);

                            if(ques == null || ques.equals("null")){
                                Log.d(TAG, "Stage2 No Ques Available: " + ques);
                            }
                        }
                        if(resArray.length > 4){
                            String options = resArray[4];
                            if(resArray.length > 5) {
                                String[] optionsArray = resArray[5].split(" ~ ");
                                Log.d(TAG, "Stage2 Answers: " + Arrays.toString(optionsArray));
                            }
                        }

                        callStage2(stage1Response, "859566");

                    }

                    else if(status.equals("S01")){
                        Log.d(TAG, "Stage2 : can go to stage3 " + status);
                        callStage3(decrypted);
                    }
                    else if(status.equals("S02")){
                        Log.d(TAG, "Stage2 : can't go to stage3 all anwser wrong " + status);
                    }
                    else if(status.equals("S03")){
                        Log.d(TAG, "Stage2 : User Cancelled the Transaction " + status);
                    }
                    else if(status.equals("S05")){
                        Log.d(TAG, "Stage2 : User Validations Failure " + status);
                    }
                    else if(status.equals("S08")){
                        Log.d(TAG, "Stage2 : Technical Error " + status);
                    }
                    else if(status.equals("S09")){
                        Log.d(TAG, "Stage2 : No Hit " + status);
                    }

                    else if(status.equals("S10")){
                        Log.d(TAG, "Stage2 : can go to stage3 " + status);
                        callStage3(decrypted);
                    }




                    httpClient.getConnectionManager().shutdown();
                } catch (IOException ex) {
                    // In case of an IOException the connection will be released
                    // back to the connection manager automatically
                    throw ex;
                } catch (RuntimeException ex) {
                    // In case of an unexpected exception you may want to abort
                    // the HTTP request in order to shut down the underlying
                    // connection and release it back to the connection manager.
                    getRequest.abort();
                    throw ex;
                } finally {
                    // Closing the input stream will trigger connection release
                    instream.close();
                }
                // When HttpClient instance is no longer needed,
                // shut down the connection manager to ensure
                // immediate deallocation of all system resources

            }
        }
        else {
            Log.d(TAG,"Stage2: not 200 " + response.getStatusLine().getStatusCode());

        }


    }

    private void callStage3(String stage2Response) throws IOException {

        String[] stringArray = stage2Response.split("\\|");
        orderId = stringArray[0];
        Log.d(TAG,"Stage2: orderId:"+orderId.toString());
        String reportId = stringArray[1];
        Log.d(TAG,"Stage2: reportId:"+reportId.toString());

        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String timestamp = s.format(new Date());
        String accessCodeString = userId+"|"+customerId+"|"+productCode+"|"+password+"|"+timestamp;

        String accessCode2 = getEncryptedString(accessCodeString, encKey, "E");
        Log.d(TAG,"Stage3: accessCode:"+accessCodeString);


        String output;
        String responseX="";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost getRequest = new HttpPost("https://test.crifhighmark.com/Inquiry/do.getSecureService/response");
        // Add additional header to getRequest which accepts application/xml data
        getRequest.addHeader("accept", "application/xml");
        getRequest.addHeader("Content-Type ", " text/plain");
        //set request parameter in header
        getRequest.setHeader("orderId",orderId);//pass order ID
        getRequest.setHeader("reportId",reportId);//pass order ID
        getRequest.setHeader("accessCode", accessCode2); // pass accessCode
        getRequest.setHeader("appID",appId);//pass Application ID(android or ios)//pass encrypted inquiry request
        getRequest.setHeader("merchantID",mercchantid ); //pass merchant id

        // Execute your request and catch response HttpResponse response =
        HttpResponse response = httpClient.execute(getRequest);

        // Check for HTTP response code: 200 = success
        if (response.getStatusLine().getStatusCode() == 200) {
            Log.d(TAG,"Stage3: success");
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();


                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(instream));
                    // do something useful with the response
                    while ((output = reader.readLine()) != null) {
                        responseX+=output;
                    }

                    Log.d(TAG, "Stage3Response: " + responseX);



                    httpClient.getConnectionManager().shutdown();
                } catch (IOException ex) {
                    // In case of an IOException the connection will be released
                    // back to the connection manager automatically
                    throw ex;
                } catch (RuntimeException ex) {
                    // In case of an unexpected exception you may want to abort
                    // the HTTP request in order to shut down the underlying
                    // connection and release it back to the connection manager.
                    getRequest.abort();
                    throw ex;
                }  finally {
                    // Closing the input stream will trigger connection release
                    instream.close();
                }
                // When HttpClient instance is no longer needed,
                // shut down the connection manager to ensure
                // immediate deallocation of all system resources

            }
        }
        else {
            Log.d(TAG,"Stage3: not 200 " + response.getStatusLine().getStatusCode());

        }



    }


    public  String getEncryptedString(String s, String encKeyString, String type) throws IOException {

        String encryptedString = "";
        // Log.d(TAG,"Enc encKeyString: "+encKeyString);
        // Log.d(TAG,"Enc: "+s);

        String url="https://test.crifhighmark.com/Inquiry/do.getSecureService/encrypt";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost encRequest = new HttpPost(url);
        encRequest.addHeader("accept", "text/plain");
        encRequest.setHeader("key", encKeyString);
        encRequest.setHeader("request", s);
        encRequest.setHeader("type", type);
        encRequest.setHeader("userId", userId);
        encRequest.setHeader("password", password);
        HttpResponse encresponse = httpClient.execute(encRequest);


        // Get hold of the response entity
        HttpEntity entity = encresponse.getEntity();

        // If the response does not enclose an entity, there is no need
        // to worry about connection release
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(instream));
                // do something useful with the response
                encryptedString = reader.readLine();

                //   Log.d(TAG,"EncResponse: "+encryptedString);
                httpClient.getConnectionManager().shutdown();
                return encryptedString;


            } catch (IOException ex) {

                // In case of an IOException the connection will be released
                // back to the connection manager automatically
                throw ex;

            } catch (RuntimeException ex) {

                // In case of an unexpected exception you may want to abort
                // the HTTP request in order to shut down the underlying
                // connection and release it back to the connection manager.
                encRequest.abort();
                throw ex;

            } finally {
                // Closing the input stream will trigger connection release
                instream.close();

            }
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
        }
        else {

            return encryptedString;
        }

    }


*/

}
