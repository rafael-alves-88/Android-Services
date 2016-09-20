package com.example.rm40300.demoservices.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadIntentService extends IntentService {

    public static final int DOWNLOAD_SUCESSO    = 1;
    public static final int DOWNLOAD_ERRO       = 0;
    public static final String FILE_PATH        = "filePath";
    public static final String URL_DOWNLOAD     = "url";
    public static final String RESULT_RECEIVER  = "resultReceiver";

    public DownloadIntentService() {
        super(DownloadIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getStringExtra(URL_DOWNLOAD);
        final ResultReceiver receiver = intent.getParcelableExtra(RESULT_RECEIVER);

        File file = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath(),
                "imagemservice.png");

        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
            URL downloadURL = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) downloadURL.openConnection();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new Exception("Não foi possível baixar a imagem");
            }

            InputStream is = conn.getInputStream();
            FileOutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                out.write(buffer, 0, byteCount);
            }

            out.close();
            is.close();

            Bundle bundle = new Bundle();
            bundle.putString(FILE_PATH, file.getPath());
            receiver.send(DOWNLOAD_SUCESSO, bundle);
        } catch (Exception e) {
            receiver.send(DOWNLOAD_ERRO, Bundle.EMPTY);
            e.printStackTrace();
        }
    }
}
