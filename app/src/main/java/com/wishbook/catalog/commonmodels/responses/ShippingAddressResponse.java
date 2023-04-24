package com.wishbook.catalog.commonmodels.responses;

public class ShippingAddressResponse {

    private String pincode;

    private String id;

    private String name;

    private State state;

    private String street_address;

    private String user;

    private Country country;

    private City city;

    private boolean isChecked;

    private boolean isBuyerAddress;

    private boolean is_default;

    private String phone_number;

    public ShippingAddressResponse() {
    }

    public ShippingAddressResponse(String pincode, String id, String name, String street_address, boolean isChecked) {
        this.pincode = pincode;
        this.id = id;
        this.name = name;
        this.street_address = street_address;
        this.isChecked = isChecked;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isBuyerAddress() {
        return isBuyerAddress;
    }

    public void setBuyerAddress(boolean buyerAddress) {
        isBuyerAddress = buyerAddress;
    }

    public boolean is_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public class City
    {
        private String id;

        private String city_name;

        private String state;

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getCity_name ()
        {
            return city_name;
        }

        public void setCity_name (String city_name)
        {
            this.city_name = city_name;
        }

        public String getState ()
        {
            return state;
        }

        public void setState (String state)
        {
            this.state = state;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [id = "+id+", city_name = "+city_name+", state = "+state+"]";
        }
    }

    public class State
    {
        private String id;

        private String state_type;

        private String state_name;

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getState_type ()
        {
            return state_type;
        }

        public void setState_type (String state_type)
        {
            this.state_type = state_type;
        }

        public String getState_name ()
        {
            return state_name;
        }

        public void setState_name (String state_name)
        {
            this.state_name = state_name;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [id = "+id+", state_type = "+state_type+", state_name = "+state_name+"]";
        }
    }

    public class Country
    {
        private String id;

        private String name;

        private String phone_code;

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getName ()
        {
            return name;
        }

        public void setName (String name)
        {
            this.name = name;
        }

        public String getPhone_code ()
        {
            return phone_code;
        }

        public void setPhone_code (String phone_code)
        {
            this.phone_code = phone_code;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [id = "+id+", name = "+name+", phone_code = "+phone_code+"]";
        }
    }
}
