package com.wishbook.catalog.commonmodels.responses;

public class OrderStep {

    public static final int STEP_UNDO = -1;
    public static final int STEP_CURRENT = 2;
    public static final int STEP_COMPLETED = 1;
    public static final int STEP_REMAIN = 0;

    private String name;
    private int state;


    public OrderStep(String name, int state) {
        this.name = name;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
