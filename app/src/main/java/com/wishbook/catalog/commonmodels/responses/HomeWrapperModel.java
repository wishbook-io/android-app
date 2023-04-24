package com.wishbook.catalog.commonmodels.responses;

import com.google.gson.JsonArray;

public class HomeWrapperModel<T> {

    private String background_image;

    private String content_type;

    private String content_title;

    private String see_more_deep_link;

    private int page;

    private Background_color background_color;

    private JsonArray data;

    private String span_grid;

    public String getBackground_image() {
        return background_image;
    }

    public void setBackground_image(String background_image) {
        this.background_image = background_image;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getContent_title() {
        return content_title;
    }

    public void setContent_title(String content_title) {
        this.content_title = content_title;
    }

    public Background_color getBackground_color() {
        return background_color;
    }

    public void setBackground_color(Background_color background_color) {
        this.background_color = background_color;
    }

    public JsonArray getData() {
        return data;
    }

    public void setData(JsonArray data) {
        this.data = data;
    }

    public String getSpan_grid() {
        return span_grid;
    }

    public void setSpan_grid(String span_grid) {
        this.span_grid = span_grid;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getSee_more_deep_link() {
        return see_more_deep_link;
    }

    public void setSee_more_deep_link(String see_more_deep_link) {
        this.see_more_deep_link = see_more_deep_link;
    }
}
