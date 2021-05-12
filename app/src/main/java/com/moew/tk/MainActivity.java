package com.moew.tk;
 
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity { 

EditText et;
Button bt;
WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		et=findViewById(R.id.activitymainEditText1);
		bt=findViewById(R.id.activitymainButton1);
		wv=findViewById(R.id.activitymainWebView1);
		WebSettings settings = this.wv.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setTextSize(WebSettings.TextSize.NORMAL);
        settings.setSupportZoom(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setAllowFileAccess(true);
        settings.setNeedInitialFocus(true);
        settings.setBuiltInZoomControls(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        settings.setLoadsImagesAutomatically(true);
        settings.setDisplayZoomControls(false);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
		if (Build.VERSION.SDK_INT >= 21)
		{
            try
			{
                settings.setMixedContentMode(0);
            }
			catch (Exception e)
			{
            }
        }
		else
		{
            Class<WebSettings> cls = WebSettings.class;
            try
			{
                Method method = cls.getMethod("setMixedContentMode", new Class[]{Integer.TYPE});
                if (method != null)
				{
                    method.invoke(settings, new Object[]{0});
                }
            }
			catch (Exception e2)
			{
				e2.printStackTrace();
            }
        }
		final MyWebViewActivity webClient = new MyWebViewActivity();
		this.wv.setWebViewClient(webClient);
		
		bt.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					String url=et.getText().toString();
					if(url==null||url.isEmpty()){
						Toast.makeText(MainActivity.this,"不可以为空",1).show();
						return;
					}
					wv.loadUrl(url);
				}
		});
    }
	
	
	
	
	class MyWebViewActivity extends WebViewClient
	{

	


		public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest)
		{

			try
			{
				String str = URLDecoder.decode(webResourceRequest.getUrl().toString(), "UTF-8");
				if (str.startsWith("jsbridge://CAPTCHA/onVerifyCAPTCHA"))
				{
					onVerifyCAPTCHA(str.replaceAll("^jsbridge://CAPTCHA/onVerifyCAPTCHA\\?p=", "").replaceAll("#2$", ""));
					return true;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			if (super.shouldOverrideUrlLoading(webView, webResourceRequest))
			{
				return true;
			}
			if (webResourceRequest.getUrl().toString().startsWith("http") || webResourceRequest.getUrl().toString().startsWith("https"))
			{
				return super.shouldOverrideUrlLoading(webView, webResourceRequest);
			}

			try
			{
				Intent intent = new Intent("android.intent.action.VIEW", webResourceRequest.getUrl());
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				return true;
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
				Toast.makeText(MainActivity.this, "您未安装请求打开的App...", Toast.LENGTH_LONG).show();
				return true;
			}






		}

		public void onVerifyCAPTCHA(String json)
		{
			try
			{
				JSONObject g = new JSONObject(json);
				int result=g.getInt("result");
				if (result == 0)
				{
					AlertDialog a=new AlertDialog.Builder(MainActivity.this).create();
					TextView tv=new TextView(MainActivity.this);
					tv.setText(g.getString("ticket"));
					tv.setTextIsSelectable(true);
					a.setView(tv);
					a.show();
				}

			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}


	}
	
} 
