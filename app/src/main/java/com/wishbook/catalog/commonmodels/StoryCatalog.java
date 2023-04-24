package com.wishbook.catalog.commonmodels;

public class StoryCatalog {
    String id;
    int last_position;
    boolean isComplete;

    public StoryCatalog(String id, int last_position) {
        this.id = id;
        this.last_position = last_position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLast_position() {
        return last_position;
    }

    public void setLast_position(int last_position) {
        this.last_position = last_position;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoryCatalog that = (StoryCatalog) o;
        return that.id.equals(id);
    }

}
