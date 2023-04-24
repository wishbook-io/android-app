package com.wishbook.catalog.Utils.widget;

import android.util.Xml;

import com.wishbook.catalog.commonmodels.RequestCreditRating;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


public class AadhaarXMLParser {
    // We don't use namespaces
    private static final String ns = null;

    private RequestCreditRating aadhaarCard;
    String co;
    String house;
    String lm;
    String loc;
    String dist;
    String subdist;
    String vtc;
    String po;
    String gender;

    public RequestCreditRating parse(String xmlContent) throws XmlPullParserException, IOException {
        InputStream in = new ByteArrayInputStream(xmlContent.getBytes());
        aadhaarCard = new RequestCreditRating();
        aadhaarCard.originalXML = xmlContent;
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readFeed(parser);
        } finally {
            in.close();
        }
        return aadhaarCard;
    }

    private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {


        parser.require(XmlPullParser.START_TAG, ns, "PrintLetterBarcodeData");
        StringBuffer full_address = new StringBuffer();

        aadhaarCard.aadhar_card = parser.getAttributeValue(null, "uid");//
        aadhaarCard.full_name = "" + parser.getAttributeValue(null, "name");// F  L
        gender = parser.getAttributeValue(null, "gender"); // M F
        co = "" + parser.getAttributeValue(null, "co");
        house = "" + parser.getAttributeValue(null, "house"); //
        lm = parser.getAttributeValue(null, "lm");
        loc = parser.getAttributeValue(null, "loc");
        vtc = parser.getAttributeValue(null, "vtc"); //
        po = parser.getAttributeValue(null, "po");
        aadhaarCard.city = "" + parser.getAttributeValue(null, "dist"); //
        aadhaarCard.state = parser.getAttributeValue(null, "state"); //
        aadhaarCard.pincode = "" + parser.getAttributeValue(null, "pc"); //
        aadhaarCard.birth_date = "" + parser.getAttributeValue(null, "dob");

        if(house!=null &&   !house.isEmpty() && !house.equals("null")){
            full_address.append(house+", ") ;
        }
        if(lm!=null &&   !lm.isEmpty() && !lm.equals("null")){
            full_address.append(lm+", "+loc);
        }
        if(gender!=null && !gender.isEmpty()){
            if(gender.equalsIgnoreCase("M")){
                aadhaarCard.gender = "Male";
            } else if(gender.equalsIgnoreCase("F")){
                aadhaarCard.gender = "Female";
            } else {
                aadhaarCard.gender = "Transgender";
            }
        }
        aadhaarCard.address = full_address.toString();
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
