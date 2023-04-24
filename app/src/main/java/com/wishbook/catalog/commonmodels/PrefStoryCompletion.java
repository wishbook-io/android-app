package com.wishbook.catalog.commonmodels;

import java.util.ArrayList;

public class PrefStoryCompletion {

    String story_id;

    int completion_count;

    ArrayList<String> category_id;


    public String getStory_id() {
        return story_id;
    }

    public void setStory_id(String story_id) {
        this.story_id = story_id;
    }

    public int getCompletion_count() {
        return completion_count;
    }

    public void setCompletion_count(int completion_count) {
        this.completion_count = completion_count;
    }

    public ArrayList<String> getCategory_id() {
        return category_id;
    }

    public void setCategory_id(ArrayList<String> category_id) {
        this.category_id = category_id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof PrefStoryCompletion)) {
            return false;
        }
        PrefStoryCompletion prefStoryCompletion = (PrefStoryCompletion) obj;
        return story_id.equals(((PrefStoryCompletion) obj).story_id);
    }
}
