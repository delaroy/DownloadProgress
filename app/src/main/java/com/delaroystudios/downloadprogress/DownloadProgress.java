package com.delaroystudios.downloadprogress;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadProgress extends AppCompatActivity {

    Button btnShowProgress;
    private ProgressDialog pDialog;

    ImageView my_image;

    public static final int progress_bar_type = 0;

    private static String file_url = "http://idsp.ak.gov.ng/images/logo.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btnShowProgress = (Button) findViewById(R.id.btnProgressBar);

        my_image = (ImageView) findViewById(R.id.my_image);

        btnShowProgress.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){

                new DownloadFileFromURL().execute(file_url);
            }

        });
    }

    @Override
    protected Dialog onCreateDialog(int id){
        switch (id){
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

   class DownloadFileFromURL extends AsyncTask<String, String, String>{

       @Override
       protected void onPreExecute(){
           super.onPreExecute();
           showDialog(progress_bar_type);
       }

       @Override
       protected  String doInBackground(String... f_url){
           int count;
           try{
               URL url = new URL(f_url[0]);
               URLConnection connection = url.openConnection();
               connection.connect();

               int lenghtOfFile = connection.getContentLength();

               InputStream input = new BufferedInputStream(url.openStream(), 8192);

               String storageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
               String fileName = "/downloadedfile.jpg";
               File imageFile = new File(storageDir+fileName);
               OutputStream output = new FileOutputStream(imageFile);

               byte data[] = new byte[1024];
               long total = 0;

               while((count = input.read(data)) != -1){
                   total += count;

                   publishProgress(""+(int)((total*100)/lenghtOfFile));

                   output.write(data, 0, count);
               }
               output.flush();

               output.close();
               input.close();
           }catch (Exception e){
               Log.e("Error: ", e.getMessage());
           }

           return null;

       }

       protected void onProgressUpdate(String... progress){
           pDialog.setProgress(Integer.parseInt(progress[0]));
       }

       @Override
       protected void onPostExecute(String file_url){
           dismissDialog(progress_bar_type);

           String imagePath = Environment.getExternalStorageDirectory() + "/downloadedfile.jpg";
           my_image.setImageDrawable(Drawable.createFromPath(imagePath));
       }
   }
}
