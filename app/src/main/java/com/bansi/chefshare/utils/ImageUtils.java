package com.bansi.chefshare.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageUtils {

    public static String bitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) return "";

        // Professional Max-Speed Optimization
        // 360px is the perfect size for mobile lists and detail views.
        // Compressed to 18% for lightning-fast Firestore uploads without sacrificing clarity.
        int maxSize = 360;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Super-Fast Compression - 18% quality for peak performance
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 18, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        
        // Clean memory
        if (resizedBitmap != bitmap) {
            resizedBitmap.recycle();
        }
        
        // Use Base64.NO_WRAP to avoid unnecessary newlines, saving more space
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    public static Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
}
