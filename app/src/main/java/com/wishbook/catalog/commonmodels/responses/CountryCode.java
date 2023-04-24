package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by root on 15/12/16.
 */
public class CountryCode {
    private Countrycodes[] countrycodes;

    public Countrycodes[] getCountrycodes ()
    {
        return countrycodes;
    }

    public void setCountrycodes (Countrycodes[] countrycodes)
    {
        this.countrycodes = countrycodes;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [countrycodes = "+countrycodes+"]";
    }
    public class Countrycodes
    {
        private String code;

        private String country;

        public String getCode ()
        {
            return code;
        }

        public void setCode (String code)
        {
            this.code = code;
        }

        public String getCountry ()
        {
            return country;
        }

        public void setCountry (String country)
        {
            this.country = country;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [code = "+code+", country = "+country+"]";
        }
    }


}
