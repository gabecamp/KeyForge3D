package com.keyforge3d.app;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import java.io.OutputStream;

    public class JsBridge {
    private final Activity act;
    public JsBridge(Activity a) { act = a; }
        @JavascriptInterface
        public void saveStl(String name, String base64) {
            try {
                byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
                ContentValues v = new ContentValues();
                v.put("_display_name", name);
                v.put("mime_type", "model/stl");
                Uri u = act.getContentResolver().insert(Uri.parse("content://media/external/downloads"), v);
                OutputStream o = act.getContentResolver().openOutputStream(u);
                o.write(bytes);
                o.close();
                toast("Saved to Downloads/" + name);
            } catch (Exception e) {
                toast("Save failed: " + e.getMessage());
            }
        }
        private void toast(final String m) {
            act.runOnUiThread(() -> Toast.makeText(act, m, Toast.LENGTH_LONG).show());
        }
    }
