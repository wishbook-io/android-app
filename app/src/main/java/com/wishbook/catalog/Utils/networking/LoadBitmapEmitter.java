package com.wishbook.catalog.Utils.networking;

class LoadBitmapEmitter extends LoadBitmapBase {
    final boolean animateGif;

    public LoadBitmapEmitter(NetworkProcessor ion, String urlKey, boolean put, boolean animateGif) {
        super(ion, urlKey, put);
        this.animateGif = animateGif;
    }
}