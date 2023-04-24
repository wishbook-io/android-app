package com.wishbook.catalog.commonmodels;

public class ProductQunatityRate {
        String product;
        int quantity;
        float rate;
        String packing_type;

        public ProductQunatityRate(String product, int quantity, float rate, String packing_type) {
            this.product = product;
            this.quantity = quantity;
            this.rate = rate;
            this.packing_type = packing_type;
        }

        public ProductQunatityRate(String product, int quantity, float rate) {
            this.product = product;
            this.quantity = quantity;
            this.rate = rate;
            this.packing_type = packing_type;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public float getRate() {
            return rate;
        }

        public void setRate(float rate) {
            this.rate = rate;
        }

        public String getPacking_type() {return packing_type;}

        public void setPacking_type(String packing_type) {this.packing_type = packing_type;}
}
