package com.keyforge3d.app;

import android.content.ContentValues;
import android.content.Intent;
import android.provider.MediaStore;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class Chooser extends WebChromeClient {
    private final MainActivity act;
    public Chooser(MainActivity a) { act = a; }

    @Override
    public boolean onShowFileChooser(WebView v, ValueCallback<Uri[]> cb, WebChromeClient.FileChooserParams p) {
        if (act.chooserCb != null) act.chooserCb.onReceiveValue(null);
        act.chooserCb = cb;
        Intent i;
        act.captureUri = null;
        if (p.isCaptureEnabled()) {
            ContentValues cv = new ContentValues();
            cv.put("_display_name", "key_" + System.currentTimeMillis() + ".jpg");
            cv.put("mime_type", "image/jpeg");
            act.captureUri = act.getContentResolver().insert(Uri.parse("content://media/external/images/media"), cv);
            i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT, act.captureUri);
        } else {
            i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            i = Intent.createChooser(i, "Select key photo");
        }
        try {
            act.startActivityForResult(i, MainActivity.REQ_PICK);
        } catch (Exception e) {
            act.chooserCb = null;
            return false;
        }
        return true;
    }
}
