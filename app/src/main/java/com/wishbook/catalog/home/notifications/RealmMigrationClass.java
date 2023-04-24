package com.wishbook.catalog.home.notifications;

import android.util.Log;


/**
 * Created by root on 27/7/17.
 */

public class RealmMigrationClass /*implements RealmMigration */{
    /*@Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        Log.e("log migration","before migrate if con "+oldVersion);
        if(oldVersion == 0) {
            try {
                RealmSchema sessionSchema = realm.getSchema();
                Log.e("log migration","before migrate");
                if(oldVersion == 0) {
                    RealmObjectSchema sessionObjSchema = sessionSchema.get("NotificationModel");
                    if(sessionObjSchema.getFieldNames().contains("read")){
                        if(sessionObjSchema.getFieldNames().contains("otherPara")){
                            sessionObjSchema.addField("otherPara", String.class)
                                    .transform(new RealmObjectSchema.Function() {
                                        @Override
                                        public void apply(DynamicRealmObject obj) {
                                            obj.setNull("otherPara");
                                        }
                                    });

                            sessionObjSchema.setNullable("otherPara",true);
                        }
                    } else {
                        sessionObjSchema.addField("read", boolean.class, FieldAttribute.REQUIRED)
                                .transform(new RealmObjectSchema.Function() {
                                    @Override
                                    public void apply(DynamicRealmObject obj) {
                                        obj.set("read", false);
                                    }
                                });

                        sessionObjSchema.setNullable("read",true);
                    }

                    oldVersion++;
                }

            } catch (Exception e) {

            }

        }
        if(oldVersion == 1) {
            try {
                RealmSchema sessionSchema = realm.getSchema();
                Log.e("log migration","before migrate");
                if(oldVersion == 1) {
                    RealmObjectSchema sessionObjSchema = sessionSchema.get("NotificationModel");
                    if(!sessionObjSchema.getFieldNames().contains("otherPara")){
                        sessionObjSchema.addField("otherPara", String.class)
                                .transform(new RealmObjectSchema.Function() {
                                    @Override
                                    public void apply(DynamicRealmObject obj) {
                                        obj.setNull("otherPara");
                                    }
                                });

                        sessionObjSchema.setNullable("otherPara",true);

                        oldVersion++;
                    }
                }
            } catch (Exception e) {

            }

        }



    }*/

}