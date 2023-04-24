package com.wishbook.catalog.Utils.networking.async.future;

public interface DependentCancellable extends Cancellable {
    void setParent(Cancellable parent);
}
