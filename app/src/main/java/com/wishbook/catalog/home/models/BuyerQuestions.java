package com.wishbook.catalog.home.models;

import java.util.ArrayList;

/**
 * Created by yarolegovich on 07.03.2017.
 */

public class BuyerQuestions {


    private final String questions,question_summary;

    private ArrayList<String> options;
    private int ans;

    public BuyerQuestions(String  questions,String question_summary,ArrayList<String> options,int ans) {
        this.questions = questions;
        this.question_summary = question_summary;
        this.options = options;
        this.ans = ans;
    }



    public String getQuestions() {
        return questions;
    }
    public String getQuestion_summary() {
        return question_summary;
    }

    public String getOptions(int position){
        switch (position){
            case 0:return options.get(0);
            case 1:return options.get(1);
            case 2:return options.get(2);
            case 3:return options.get(3);
        }
        return "";
    }

    public int getAns(){
        return ans;
    }


    public void setAns(int ans){
        this.ans=ans;
    }


}