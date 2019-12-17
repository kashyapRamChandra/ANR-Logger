package com.example.anrlogger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final String ANR_PATH = "/data/anr/traces.txt";

    private static final String ANR_SAVED_PATH = "anr.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void scanForErrorFile(View view) {
        try {
            final File anrFile = new File(ANR_PATH);
            if (anrFile.exists()) {
                //need to Share.
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final File savedFile = saveToSDCard(anrFile);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    shareAnrFile(savedFile);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            } else {
                Toast.makeText(MainActivity.this, getString(R.string.no_error_log_found), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File saveToSDCard(File anrFile) throws IOException {
        File dst = new File(Environment.getExternalStorageDirectory(), ANR_SAVED_PATH);
        try (InputStream in = new FileInputStream(anrFile)) {


            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
        return dst;
    }

    private void shareAnrFile(File file) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "ANR FILE");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        if (!file.exists() || !file.canRead()) {
            return;
        }
        Uri uri = Uri.fromFile(file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

}
