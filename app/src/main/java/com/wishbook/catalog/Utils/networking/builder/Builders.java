package com.wishbook.catalog.Utils.networking.builder;

import android.graphics.Bitmap;

import com.wishbook.catalog.Utils.networking.bitmap.BitmapInfo;

/**
 * Created by vignesh on 6/10/13.
 */
public interface Builders {

    interface IV {
        interface F<A extends F<?>> extends ImageViewBuilder<A>, BitmapBuilder<A>, LoadImageViewFutureBuilder {
            BitmapInfo getBitmapInfo();
            Bitmap getBitmap();
        }
    }

    interface Any {
        // restrict to image view builder
        interface IF<A extends IF<?>> extends ImageViewBuilder<A>, ImageViewFutureBuilder {
        }

        // restrict to bitmap future builder
        interface BF<A extends BF<?>> extends BitmapBuilder<A>, BitmapFutureBuilder, IF<A> {
        }

        // restrict to future builder
        interface F extends FutureBuilder, ImageViewFutureBuilder {
        }

        // restrict to multipart builder
        interface M extends MultipartBodyBuilder<M>, F {
        }

        // restrict to url encoded builder builder
        interface U extends UrlEncodedBuilder<U>, F {
        }

        // top level builder
        interface B extends RequestBuilder<F, B, M, U>, F {
        }
    }
}
