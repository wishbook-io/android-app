package com.wishbook.catalog.home.more.creditRating.crief;

public class CRIFKYC {
    private String firstName="";
    private String middleName="";
    private String lastName="";
    private String gender="";
    private String dob="";
    private String ageAsOnToday="";
    private String maritalStatus="";
    private String phone1="";
    private String phone2="";
    private String phone3="";
    private String email1="";
    private String email2="";
    private String panCardNumber="";
    private String dlNumber="";
    private String voterId="";
    private String passportNumber="";
    private String rationCardNumber="";
    private String UID="";
    private String otherId1="";
    private String otherId2="";
    private String fatherName="";
    private String spouseName="";
    private String motherName="";
    private String address1="";
    private String village1="";
    private String city1="";
    private String state1="";
    private String pin1="";
    private String country1="";
    private String address2="";
    private String village2="";
    private String city2="";
    private String state2="";
    private String pin2="";
    private String country2="";
    private String consent="";

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setAgeAsOnToday(String ageAsOnToday) {
        this.ageAsOnToday = ageAsOnToday;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public void setPhone3(String phone3) {
        this.phone3 = phone3;
    }

    public void setEmail1(String email1) {
        this.email1 = email1;
    }

    public void setEmail2(String email2) {
        this.email2 = email2;
    }

    public void setPanCardNumber(String panCardNumber) {
        this.panCardNumber = panCardNumber;
    }

    public void setDlNumber(String dlNumber) {
        this.dlNumber = dlNumber;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public void setRationCardNumber(String rationCardNumber) {
        this.rationCardNumber = rationCardNumber;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public void setOtherId1(String otherId1) {
        this.otherId1 = otherId1;
    }

    public void setOtherId2(String otherId2) {
        this.otherId2 = otherId2;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public void setVillage1(String village1) {
        this.village1 = village1;
    }

    public void setCity1(String city1) {
        this.city1 = city1;
    }

    public void setState1(String state1) {
        this.state1 = state1;
    }

    public void setPin1(String pin1) {
        this.pin1 = pin1;
    }

    public void setCountry1(String country1) {
        this.country1 = country1;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public void setVillage2(String village2) {
        this.village2 = village2;
    }

    public void setCity2(String city2) {
        this.city2 = city2;
    }

    public void setState2(String state2) {
        this.state2 = state2;
    }

    public void setPin2(String pin2) {
        this.pin2 = pin2;
    }

    public void setCountry2(String country2) {
        this.country2 = country2;
    }

    public void setConsent(boolean consent) {
        this.consent = consent?"Y":"N";
    }

    @Override
    public String toString() {
        CRIFCreds creds = CRIFApi.getCreds();
        String str = "";
        if(creds!=null) {
            String customerId=creds.getCustomerId();
            String productId=creds.getProductId();
            str = firstName + "|" + middleName + "|" + lastName + "|" + gender + "|" + dob + "|" + ageAsOnToday + "|" + maritalStatus + "|" + phone1 + "|" + phone2 + "|" + phone3 + "|" + email1 + "|" + email2 + "|" + panCardNumber + "|" + dlNumber + "|" + voterId + "|" + passportNumber + "|" + rationCardNumber + "|" + UID + "|" + otherId1 + "|" + otherId2 + "|" + fatherName + "|" + motherName + "|" + spouseName + "|" + address1 + "|" + village1 + "|" + city1 + "|" + state1 + "|" + pin1 + "|" + country1 + "|" + address2 + "|" + village2 + "|" + city2 + "|" + state2 + "|" + pin2 + "|" + country2 + "|" + customerId + "|" + productId + "|" + consent + "|";
        }
        return str;
    }
    public static CRIFKYC getSampleKYC1(){
        CRIFKYC crifkyc = new CRIFKYC();
        crifkyc.setFirstName("Nitin");
        crifkyc.setLastName("Jain");
        crifkyc.setDob("10-10-1986");
        crifkyc.setPhone1("02228673678");
        crifkyc.setEmail1("shubhanga@wishbook.io");
        crifkyc.setPanCardNumber("AHJPT6190N");
        crifkyc.setFatherName("NA");
        crifkyc.setAddress1("307, BHARAT CHAMBER 52 BARODA STREET P.D MELLO ROAD, CARNAC BUNDER");
        crifkyc.setVillage1("Mumbai");
        crifkyc.setCity1("Mumbai");
        crifkyc.setState1("Maharashtra");
        crifkyc.setPin1("400009");
        crifkyc.setCountry1("india");
        crifkyc.setConsent(true);
        return crifkyc;
    }
}
