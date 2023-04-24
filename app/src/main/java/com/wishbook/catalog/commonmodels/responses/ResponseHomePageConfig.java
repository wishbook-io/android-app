package com.wishbook.catalog.commonmodels.responses;

import java.util.ArrayList;

public class ResponseHomePageConfig {

    private ArrayList<Home_banner> home_banner;

    public ArrayList<Home_banner> getHome_banner() {
        return home_banner;
    }

    public void setHome_banner(ArrayList<Home_banner> home_banner) {
        this.home_banner = home_banner;
    }

    // Start Nested Class
    public class Home_banner {
        private String banner_img;

        private int banner_position;

        private String banner_deep_link;

        public String getBanner_img() {
            return banner_img;
        }

        public void setBanner_img(String banner_img) {
            this.banner_img = banner_img;
        }

        public int getBanner_position() {
            return banner_position;
        }

        public void setBanner_position(int banner_position) {
            this.banner_position = banner_position;
        }

        public String getBanner_deep_link() {
            return banner_deep_link;
        }

        public void setBanner_deep_link(String banner_deep_link) {
            this.banner_deep_link = banner_deep_link;
        }

        @Override
        public String toString() {
            return "ClassPojo [banner_img = " + banner_img + ", banner_position = " + banner_position + ", banner_deep_link = " + banner_deep_link + "]";
        }
    }

}
