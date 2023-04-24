package com.wishbook.catalog.Utils.networking.async.http;

import com.wishbook.catalog.Utils.networking.async.AsyncServer;
import com.wishbook.catalog.Utils.networking.async.AsyncSocket;
import com.wishbook.catalog.Utils.networking.async.ByteBufferList;
import com.wishbook.catalog.Utils.networking.async.DataEmitter;
import com.wishbook.catalog.Utils.networking.async.DataSink;
import com.wishbook.catalog.Utils.networking.async.FilteredDataEmitter;
import com.wishbook.catalog.Utils.networking.async.callback.CompletedCallback;
import com.wishbook.catalog.Utils.networking.async.callback.WritableCallback;
import com.wishbook.catalog.Utils.networking.async.http.body.AsyncHttpRequestBody;

import java.nio.charset.Charset;

abstract class AsyncHttpResponseImpl extends FilteredDataEmitter implements AsyncSocket, AsyncHttpResponse, AsyncHttpClientMiddleware.ResponseHead {
    public AsyncSocket socket() {
        return mSocket;
    }

    @Override
    public AsyncHttpRequest getRequest() {
        return mRequest;
    }

    void setSocket(AsyncSocket exchange) {
        mSocket = exchange;
        if (mSocket == null)
            return;

        mSocket.setEndCallback(mReporter);
    }

    protected void onHeadersSent() {
        AsyncHttpRequestBody requestBody = mRequest.getBody();
        if (requestBody != null) {
            requestBody.write(mRequest, AsyncHttpResponseImpl.this, new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    onRequestCompleted(ex);
                }
            });
        } else {
            onRequestCompleted(null);
        }
    }

    protected void onRequestCompleted(Exception ex) {
    }
    
    private CompletedCallback mReporter = new CompletedCallback() {
        @Override
        public void onCompleted(Exception error) {
            if (error != null && !mCompleted) {
                report(new ConnectionClosedException("Internet Connection Time-out.", error));
            }
            else {
                report(error);
            }
        }
    };

    protected void onHeadersReceived() {
    }


    @Override
    public DataEmitter emitter() {
        return getDataEmitter();
    }

    @Override
    public void emitter(DataEmitter emitter) {
        setDataEmitter(emitter);
    }

    private void terminate() {
        // DISCONNECT. EVERYTHING.
        // should not get any data after this point...
        // if so, eat it and disconnect.
        mSocket.setDataCallback(new NullDataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                super.onDataAvailable(emitter, bb);
                mSocket.close();
            }
        });
    }

    @Override
    protected void report(Exception e) {
        super.report(e);

        terminate();
        mSocket.setWriteableCallback(null);
        mSocket.setClosedCallback(null);
        mSocket.setEndCallback(null);
        mCompleted = true;
    }

    @Override
    public void close() {
        super.close();
        terminate();
    }

    private AsyncHttpRequest mRequest;
    private AsyncSocket mSocket;
    protected Headers mHeaders;
    public AsyncHttpResponseImpl(AsyncHttpRequest request) {
        mRequest = request;
    }

    boolean mCompleted = false;

    @Override
    public Headers headers() {
        return mHeaders;
    }

    @Override
    public void headers(Headers headers) {
        mHeaders = headers;
    }

    int code;
    @Override
    public int code() {
        return code;
    }

    @Override
    public void code(int code) {
        this.code = code;
    }

    @Override
    public void protocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public void message(String message) {
        this.message = message;
    }

    String protocol;
    @Override
    public String protocol() {
        return protocol;
    }

    String message;
    @Override
    public String message() {
        return message;
    }

    @Override
    public String toString() {
        if (mHeaders == null)
            return super.toString();
        return mHeaders.toPrefixString(protocol + " " + code + " " + message);
    }

    private boolean mFirstWrite = true;
    private void assertContent() {
        if (!mFirstWrite)
            return;
        mFirstWrite = false;
        assert null != mRequest.getHeaders().get("Content-Type");
        assert mRequest.getHeaders().get("Transfer-Encoding") != null || HttpUtil.contentLength(mRequest.getHeaders()) != -1;
    }

    DataSink mSink;

    @Override
    public DataSink sink() {
        return mSink;
    }

    @Override
    public void sink(DataSink sink) {
        mSink = sink;
    }

    @Override
    public void write(ByteBufferList bb) {
        assertContent();
        mSink.write(bb);
    }

    @Override
    public void end() {
        throw new AssertionError("end called?");
    }

    @Override
    public void setWriteableCallback(WritableCallback handler) {
        mSink.setWriteableCallback(handler);
    }

    @Override
    public WritableCallback getWriteableCallback() {
        return mSink.getWriteableCallback();
    }


    @Override
    public boolean isOpen() {
        return mSink.isOpen();
    }

    @Override
    public void setClosedCallback(CompletedCallback handler) {
        mSink.setClosedCallback(handler);
    }

    @Override
    public CompletedCallback getClosedCallback() {
        return mSink.getClosedCallback();
    }
    
    @Override
    public AsyncServer getServer() {
        return mSocket.getServer();
    }

    @Override
    public String charset() {
        Multimap mm = Multimap.parseSemicolonDelimited(headers().get("Content-Type"));
        String cs;
        if (mm != null && null != (cs = mm.getString("charset")) && Charset.isSupported(cs)) {
            return cs;
        }
        return null;
    }
}
