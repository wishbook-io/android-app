package com.wishbook.catalog.commonmodels.responses;

public class AWBShipmentTrackingResponse {

    private Tracking_data tracking_data;

    public Tracking_data getTracking_data() {
        return tracking_data;
    }

    public void setTracking_data(Tracking_data tracking_data) {
        this.tracking_data = tracking_data;
    }

    /**
     * start Subclass for tracking data
     */
    public class Tracking_data {
        private String track_status;

        private Shipment_track_activities[] shipment_track_activities;

        private String track_url;

        private Shipment_track[] shipment_track;

        private String shipment_status;

        public String getTrack_status() {
            return track_status;
        }

        public void setTrack_status(String track_status) {
            this.track_status = track_status;
        }

        public Shipment_track_activities[] getShipment_track_activities() {
            return shipment_track_activities;
        }

        public void setShipment_track_activities(Shipment_track_activities[] shipment_track_activities) {
            this.shipment_track_activities = shipment_track_activities;
        }

        public String getTrack_url() {
            return track_url;
        }

        public void setTrack_url(String track_url) {
            this.track_url = track_url;
        }

        public Shipment_track[] getShipment_track() {
            return shipment_track;
        }

        public void setShipment_track(Shipment_track[] shipment_track) {
            this.shipment_track = shipment_track;
        }

        public String getShipment_status() {
            return shipment_status;
        }

        public void setShipment_status(String shipment_status) {
            this.shipment_status = shipment_status;
        }

    }


    public class Shipment_track {
        private String delivered_date;

        private String pickup_date;

        private String awb_code;

        private String weight;

        private String packages;

        private String origin;

        private String order_id;

        private String current_status;

        private String destination;

        private String id;

        private String consignee_name;

        private String delivered_to;

        private String courier_company_id;

        private String shipment_id;

        public String getDelivered_date() {
            return delivered_date;
        }

        public void setDelivered_date(String delivered_date) {
            this.delivered_date = delivered_date;
        }

        public String getPickup_date() {
            return pickup_date;
        }

        public void setPickup_date(String pickup_date) {
            this.pickup_date = pickup_date;
        }

        public String getAwb_code() {
            return awb_code;
        }

        public void setAwb_code(String awb_code) {
            this.awb_code = awb_code;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getPackages() {
            return packages;
        }

        public void setPackages(String packages) {
            this.packages = packages;
        }

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public String getCurrent_status() {
            return current_status;
        }

        public void setCurrent_status(String current_status) {
            this.current_status = current_status;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getConsignee_name() {
            return consignee_name;
        }

        public void setConsignee_name(String consignee_name) {
            this.consignee_name = consignee_name;
        }

        public String getDelivered_to() {
            return delivered_to;
        }

        public void setDelivered_to(String delivered_to) {
            this.delivered_to = delivered_to;
        }

        public String getCourier_company_id() {
            return courier_company_id;
        }

        public void setCourier_company_id(String courier_company_id) {
            this.courier_company_id = courier_company_id;
        }

        public String getShipment_id() {
            return shipment_id;
        }

        public void setShipment_id(String shipment_id) {
            this.shipment_id = shipment_id;
        }

        @Override
        public String toString() {
            return "ClassPojo [delivered_date = " + delivered_date + ", pickup_date = " + pickup_date + ", awb_code = " + awb_code + ", weight = " + weight + ", packages = " + packages + ", origin = " + origin + ", order_id = " + order_id + ", current_status = " + current_status + ", destination = " + destination + ", id = " + id + ", consignee_name = " + consignee_name + ", delivered_to = " + delivered_to + ", courier_company_id = " + courier_company_id + ", shipment_id = " + shipment_id + "]";
        }
    }


    public class Shipment_track_activities {
        private String location;

        private String date;

        private String activity;

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getActivity() {
            return activity;
        }

        public void setActivity(String activity) {
            this.activity = activity;
        }
    }

}
