package com.wishbook.catalog.commonmodels.responses;

public class ResponseNotificationPreference {

    private String created;

    private String modified;

    private String id;

    private String user;

    private boolean whatsapp_notifications;

    private boolean whatsapp_promotions;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isWhatsapp_notifications() {
        return whatsapp_notifications;
    }

    public void setWhatsapp_notifications(boolean whatsapp_notifications) {
        this.whatsapp_notifications = whatsapp_notifications;
    }

    public boolean isWhatsapp_promotions() {
        return whatsapp_promotions;
    }

    public void setWhatsapp_promotions(boolean whatsapp_promotions) {
        this.whatsapp_promotions = whatsapp_promotions;
    }
}
