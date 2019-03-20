package com.yunpeng.alipay;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by m2mbob on 16/5/6.
 */
public class AlipayModule extends ReactContextBaseJavaModule{

    private static final int SDK_PAY_FLAG = 1;
    private static final String TAG = "AlipayModule";

    @SuppressLint("HandlerLeak")
   

    public AlipayModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void pay(final String payInfo,
                    final Promise promise) {

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    PayTask alipay = new PayTask(getCurrentActivity());
                    String result = alipay.pay(payInfo, true);
                    PayResult payResult = new PayResult(result);
                    String resultInfo = payResult.getMemo();
                    String resultStatus = payResult.getResultStatus();
                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = resultStatus;
                   
                    if(Integer.valueOf(resultStatus) >= 8000){
                        promise.resolve(result);
                    }else{
                        promise.reject(resultInfo, new RuntimeException(resultStatus+":"+resultInfo));
                    }
                } catch (Exception e) {
                    promise.reject(e.getLocalizedMessage(), e);
                }
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @Override
    public String getName() {
        return "AlipayModule";
    }

}
