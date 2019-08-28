package il.co.wwo.mapapplication;

import androidx.appcompat.app.AppCompatActivity;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import android.widget.ProgressBar;
import android.widget.Toast;

public class BrowserActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getIntent().getStringExtra("url");
        setContentView(R.layout.activity_browser);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (NullPointerException e){
            Toast.makeText(this, "Action bar title error",Toast.LENGTH_SHORT).show();
        }
        getSupportActionBar().setTitle(R.string.app_name);
        webView =  findViewById(R.id.webView);
        progressBar =  findViewById(R.id.progressBar);
        WebSettings webSetting = webView.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        if(url != null)
            webView.loadUrl(url);
    }

    public class WebViewClient extends android.webkit.WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {

            // TODO Auto-generated method stub

            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }
}
