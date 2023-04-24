package com.wishbook.catalog.Utils.networking;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;

public class Md5FileNameGeneratorExt implements FileNameGenerator {

    public Md5FileNameGeneratorExt() {
    }

    public String generate(String imageUri) {
        return String.valueOf(imageUri.hashCode()) + ".png";
    }

}
