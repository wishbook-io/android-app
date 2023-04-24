package com.wishbook.catalog.Utils.networking;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import com.wishbook.catalog.Utils.networking.async.AsyncServer;
import com.wishbook.catalog.Utils.networking.async.ByteBufferList;
import com.wishbook.catalog.Utils.networking.async.future.Future;
import com.wishbook.catalog.Utils.networking.async.future.FutureCallback;
import com.wishbook.catalog.Utils.networking.async.http.AsyncHttpRequest;
import com.wishbook.catalog.Utils.networking.async.parser.ByteBufferListParser;
import com.wishbook.catalog.Utils.networking.async.util.FileCache;
import com.wishbook.catalog.Utils.networking.bitmap.BitmapInfo;
import com.wishbook.catalog.Utils.networking.bitmap.PostProcess;
import com.wishbook.catalog.Utils.networking.bitmap.Transform;
import com.wishbook.catalog.Utils.networking.loader.MediaFile;

class BitmapFetcher implements NetworkProcessorRequestBuilder.LoadRequestCallback {
    String decodeKey;
    String bitmapKey;
    BitmapInfo info;
    boolean hasTransforms;
    ArrayList<Transform> transforms;
    NetworkProcessorRequestBuilder builder;
    int sampleWidth;
    int sampleHeight;
    boolean animateGif;
    boolean deepZoom;
    ArrayList<PostProcess> postProcess;

    public void recomputeDecodeKey() {
        decodeKey = NetworkProcessorBitmapRequestBuilder.computeDecodeKey(builder, sampleWidth, sampleHeight,
        animateGif, deepZoom);
        bitmapKey = NetworkProcessorBitmapRequestBuilder.computeBitmapKey(decodeKey, transforms);
    }

    private boolean fastLoad(String uri) {
        NetworkProcessor ion = builder.ion;
        if (deepZoom) {
            if (uri == null || !uri.startsWith("file:/"))
                return false;
            File file = new File(URI.create(uri));
            if (!file.exists())
                return false;
            MediaFile.MediaFileType type = MediaFile.getFileType(file.getAbsolutePath());
            if (type == null || !MediaFile.isVideoFileType(type.fileType)) {
                LoadDeepZoom loadDeepZoom = new LoadDeepZoom(ion, decodeKey, animateGif, null);
                loadDeepZoom.onCompleted(null, new Response<File>(null, ResponseServedFrom.LOADED_FROM_CACHE, null, null, file));
//                System.out.println("fastloading deepZoom");
                return true;
            }
            // fall through to allow some other loader to open this, cause this is a video file
        }

        boolean put = !hasTransforms;

        for (Loader loader: ion.configure().getLoaders()) {
            Future<BitmapInfo> future = loader.loadBitmap(builder.contextReference.getContext(), ion, decodeKey, uri, sampleWidth, sampleHeight, animateGif);
            if (future != null) {
                final BitmapCallback callback = new LoadBitmapBase(ion, decodeKey, put);
                future.setCallback(new FutureCallback<BitmapInfo>() {
                    @Override
                    public void onCompleted(Exception e, BitmapInfo result) {
                        callback.report(e, result);
                    }
                });
//                System.out.println("fastloading");
                return true;
            }
        }
        return false;
    }

    public static final int MAX_IMAGEVIEW_LOAD = 5;

    public static boolean shouldDeferImageView(NetworkProcessor ion) {
        if (ion.bitmapsPending.keySet().size() <= MAX_IMAGEVIEW_LOAD)
            return false;
        int loadCount = 0;
        for (String key: ion.bitmapsPending.keySet()) {
            Object owner = ion.bitmapsPending.tag(key);
            if (owner instanceof LoadBitmapBase) {
                loadCount++;
                if (loadCount > MAX_IMAGEVIEW_LOAD)
                    return true;
            }
        }
        return false;
    }

    public void defer() {
        DeferredLoadBitmap ret = new DeferredLoadBitmap(builder.ion, decodeKey, this);
        executeTransforms(builder.ion);

    }

    private void executeTransforms(NetworkProcessor ion) {
        // if there's a transform, do it
        if (!hasTransforms)
            return;

        // verify this transform isn't already pending
        // make sure that the parent download isn't cancelled (empty list)
        // and also make sure there are waiters for this transformed bitmap
        if (ion.bitmapsPending.tag(bitmapKey) == null) {
            ion.bitmapsPending.add(decodeKey, new TransformBitmap(ion, bitmapKey, decodeKey, transforms, postProcess));
        }
    }

    @Override
    public boolean loadRequest(AsyncHttpRequest request) {
        return !fastLoad(request.getUri().toString());
    }

    public void execute() {
        final NetworkProcessor ion = builder.ion;

        // bitmaps that were transformed are put into the FileCache to prevent
        // subsequent retransformation. See if we can retrieve the bitmap from the disk cache.
        // See TransformBitmap for where the cache is populated.
        FileCache fileCache = ion.responseCache.getFileCache();
        if (!builder.noCache && fileCache.exists(bitmapKey) && !deepZoom) {
            BitmapCallback.getBitmapSnapshot(ion, bitmapKey, postProcess);
            return;
        }

        // Perform a download as necessary.
        if (ion.bitmapsPending.tag(decodeKey) == null && !fastLoad(builder.uri)) {
            builder.setHandler(null);
            builder.loadRequestCallback = this;

            if (!deepZoom) {
                Future<Response<ByteBufferList>> emitterTransform = builder.execute(new ByteBufferListParser(), new Runnable() {
                    @Override
                    public void run() {
                        AsyncServer.post(NetworkProcessor.mainHandler, new Runnable() {
                            @Override
                            public void run() {
                                ion.bitmapsPending.remove(decodeKey);
                            }
                        });
                    }
                })
                .withResponse();
                emitterTransform.setCallback(new LoadBitmap(ion, decodeKey, !hasTransforms, sampleWidth, sampleHeight, animateGif));
            }
            else {
//                System.out.println("downloading file for deepZoom");
                File file = fileCache.getTempFile();
                Future<Response<File>> emitterTransform = builder.write(file).withResponse();
                LoadDeepZoom loadDeepZoom = new LoadDeepZoom(ion, decodeKey, animateGif, fileCache);
                emitterTransform.setCallback(loadDeepZoom);
            }
        }

        executeTransforms(ion);
    }
}
