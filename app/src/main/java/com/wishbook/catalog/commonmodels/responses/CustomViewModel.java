package com.wishbook.catalog.commonmodels.responses;


import java.io.Serializable;
import java.util.ArrayList;

public class CustomViewModel <T> implements Serializable{

    private int position;

    private int id;

    private ArrayList<T> objects;

    private String field1;

    private String field2;

    private String field3;

    private Object object;

    private Object field4;

    private boolean field5;

    public CustomViewModel() {

    }

    public CustomViewModel(int id , ArrayList<T> objects) {
        this.id = id;
        this.objects = objects;
    }


    public ArrayList<T> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<T> objects) {
        this.objects = objects;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Object getField4() {
        return field4;
    }

    public void setField4(Object field4) {
        this.field4 = field4;
    }

    public boolean isField5() {
        return field5;
    }

    public void setField5(boolean field5) {
        this.field5 = field5;
    }
}
