package com.keyforge3d.app;

import android.content.Intent;
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
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        try {
            act.startActivityForResult(Intent.createChooser(i, "Select key photo"), MainActivity.REQ_PICK);
        } catch (Exception e) {
            act.chooserCb = null;
            return false;
        }
        return true;
    }
}
