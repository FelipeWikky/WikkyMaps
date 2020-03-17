package com.thewikky.wikkymaps.Model;

import android.util.Base64;

public class Base64Custom {

    public static String encrypt(String text){
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT).replaceAll("\\n|\\r", "");
    }

    public static String decrypt(String textCodificado){
        return new String ( Base64.decode(textCodificado, Base64.DEFAULT) ) ;
    }

}
