package com.wishbook.catalog.Utils.networking.async.parser;

import com.wishbook.catalog.Utils.networking.async.ByteBufferList;
import com.wishbook.catalog.Utils.networking.async.DataEmitter;
import com.wishbook.catalog.Utils.networking.async.DataSink;
import com.wishbook.catalog.Utils.networking.async.callback.CompletedCallback;
import com.wishbook.catalog.Utils.networking.async.future.Future;
import com.wishbook.catalog.Utils.networking.async.future.TransformFuture;
import com.wishbook.catalog.Utils.networking.async.http.body.DocumentBody;
import com.wishbook.catalog.Utils.networking.async.stream.ByteBufferListInputStream;

import org.w3c.dom.Document;

import java.lang.reflect.Type;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by vignesh on 8/3/13.
 */
public class DocumentParser implements AsyncParser<Document> {
    @Override
    public Future<Document> parse(DataEmitter emitter) {
        return new ByteBufferListParser().parse(emitter)
        .then(new TransformFuture<Document, ByteBufferList>() {
            @Override
            protected void transform(ByteBufferList result) throws Exception {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                setComplete(db.parse(new ByteBufferListInputStream(result)));
            }
        });
    }

    @Override
    public void write(DataSink sink, Document value, CompletedCallback completed) {
        new DocumentBody(value).write(null, sink, completed);
    }

    @Override
    public Type getType() {
        return Document.class;
    }
}
