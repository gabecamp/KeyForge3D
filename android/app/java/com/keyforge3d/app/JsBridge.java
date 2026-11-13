package com.keyforge3d.app;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import java.io.OutputStream;

public class JsBridge {
    private final Activity act;
    public JsBridge(Activity a) { act = a; }

    private Uri write(String name, String mime, String base64) throws Exception {
        byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        ContentValues v = new ContentValues();
        v.put("_display_name", name);
        v.put("mime_type", mime);
        Uri u = act.getContentResolver().insert(Uri.parse("content://media/external/downloads"), v);
        OutputStream o = act.getContentResolver().openOutputStream(u);
        o.write(bytes);
        o.close();
        return u;
    }

    @JavascriptInterface
    public void saveFile(String name, String mime, String base64) {
        try { write(name, mime, base64); toast("Saved to Downloads/" + name); }
        catch (Exception e) { toast("Save failed: " + e.getMessage()); }
    }

    @JavascriptInterface
    public void shareFile(String name, String mime, String base64) {
        try {
            Uri u = write(name, mime, base64);
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType(mime.equals("model/stl") ? "application/octet-stream" : mime);
            i.putExtra(Intent.EXTRA_STREAM, u);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            act.startActivity(Intent.createChooser(i, "Share " + name));
        } catch (Exception e) { toast("Share failed: " + e.getMessage()); }
    }

    private void toast(final String m) {
        act.runOnUiThread(() -> Toast.makeText(act, m, Toast.LENGTH_LONG).show());
    }
}
