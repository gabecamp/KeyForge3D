package com.keyforge3d.app;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import java.io.OutputStream;

public class MainActivity extends Activity {
    private WebView web;
    ValueCallback<Uri[]> chooserCb;
    Uri captureUri;
    static final int REQ_PICK = 1;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        web = new WebView(this);
        web.setBackgroundColor(Color.parseColor("#0f141b"));
        setContentView(web);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        web.addJavascriptInterface(new JsBridge(this), "Android");
        web.setWebChromeClient(new Chooser(this));
        web.loadUrl("file:///android_asset/index.html");
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        if (req == REQ_PICK && chooserCb != null) {
            Uri[] r = null;
            if (res == RESULT_OK) {
                if (captureUri != null) r = new Uri[]{captureUri};
                else if (data != null && data.getData() != null) r = new Uri[]{data.getData()};
            } else if (captureUri != null) {
                try { getContentResolver().delete(captureUri, null, null); } catch (Exception e) { }
            }
            captureUri = null;
            chooserCb.onReceiveValue(r);
            chooserCb = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack(); else super.onBackPressed();
    }
}
