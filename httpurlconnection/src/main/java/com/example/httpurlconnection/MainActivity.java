package com.example.httpurlconnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button visitWebBtn = null;
    Button downImgBtn = null;
    TextView showTextView = null;
    ImageView showImageView = null;
    String resultStr = "";
    ProgressBar progressBar = null;
    ViewGroup viewGroup = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    private void initUI() {
        showTextView = (TextView)findViewById(R.id.textview);
        showImageView = (ImageView)findViewById(R.id.imagview);
        downImgBtn = (Button)findViewById(R.id.btn_download);
        visitWebBtn = (Button)findViewById(R.id.btn_visit_web);
        visitWebBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageView.setVisibility(View.GONE);
                showTextView.setVisibility(View.VISIBLE);
                Thread visiBaiduThread = new Thread(new VisiWebRunnable());
                visiBaiduThread.start();
                try {
                    visiBaiduThread.join();
                    if(!resultStr.equals("")){
                        showTextView.setText(resultStr);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        downImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageView.setVisibility(View.VISIBLE);
                showTextView.setVisibility(View.GONE);
                String imgUrl = "http://imgsrc.baidu.com/forum/pic/item/78310a55b319ebc420d41f368b26cffc1f1716f4.jpg";
                new DownImgAsyncTask().execute(imgUrl);
            }
        });
    }

    private class VisiWebRunnable implements Runnable {
        @Override
        public void run() {
            String data = getURlResponse("http://www.baidu.com");
            resultStr = data;
        }
    }

    private String getURlResponse(String urlString) {
        HttpURLConnection conn = null;
        InputStream is = null;
        String resultData = "";
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String inputLine = "";
            while ((inputLine = bufferedReader.readLine())!= null){
                resultData += inputLine +"\n";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(conn != null){
                conn.disconnect();
            }
        }
        return resultData;
    }

    private class DownImgAsyncTask extends AsyncTask<String,Void,Bitmap>{
        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap b = getImageBitmap(strings[0]);
            return b;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showImageView.setImageBitmap(null);
            showProgressBar();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if(result!=null){
                dismissProgressBar();
                showImageView.setImageBitmap(result);
            }
        }
    }

    private void dismissProgressBar() {
        if(progressBar!=null){
            progressBar.setVisibility(View.GONE);
            viewGroup.removeView(progressBar);
            progressBar = null;
        }
    }

    private void showProgressBar() {
        progressBar = new ProgressBar(this,null,android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        progressBar.setVisibility(View.VISIBLE);
        Context context = getApplicationContext();
        viewGroup = (ViewGroup) findViewById(R.id.parent_view);
        viewGroup.addView(progressBar,params);
    }

    private Bitmap getImageBitmap(String url) {
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return bitmap;
    }
}
