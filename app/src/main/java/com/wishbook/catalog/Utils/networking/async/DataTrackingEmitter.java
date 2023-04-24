package com.wishbook.catalog.Utils.networking.async;

/**
 * Created by vignesh on 5/28/13.
 */
public interface DataTrackingEmitter extends DataEmitter {
    interface DataTracker {
        void onData(int totalBytesRead);
    }
    void setDataTracker(DataTracker tracker);
    DataTracker getDataTracker();
    int getBytesRead();
    void setDataEmitter(DataEmitter emitter);
}
