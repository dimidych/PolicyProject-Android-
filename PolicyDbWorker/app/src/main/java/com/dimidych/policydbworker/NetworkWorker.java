package com.dimidych.policydbworker;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkWorker {
    private static final String LOG_TAG = "NetworkWorker";

    public static boolean ping(String ipAddress) {
        try {
            try {
                URL url = new URL("http://" + ipAddress);
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("User-Agent", "Android Application:");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000 * 3);
                urlc.connect();

                if (urlc.getResponseCode() == 200) {
                    Log.d(LOG_TAG, "Ping success.");
                    return true;
                }
            } catch (MalformedURLException e1) {
                Log.e(LOG_TAG, "Удаленный узел не отвечает. " + e1.getMessage());
            } catch (IOException e) {
                Log.e(LOG_TAG, "Удаленный узел не отвечает. " + e.getMessage());
            } finally {
                Thread.sleep(2000);
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Удаленный узел не отвечает. " + ex.getMessage());
        }

        return false;
    }

    public static <TParam> String restServiceCall(String ipAddress, String port,
                                                  String serviceName,
                                                  TParam param) {
        try {
            if (TextUtils.isEmpty(ipAddress.trim()))
                throw new Exception("IP адрес сервера не задан");

            if (TextUtils.isEmpty(port.trim()))
                throw new Exception("Порт сервера не задан");

            if (TextUtils.isEmpty(serviceName.trim()))
                throw new Exception("Имя сервиса не задано");

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://" + ipAddress + ":" + port + "/" + serviceName);
            httppost.setHeader("Content-Type", "application/json");
            Gson gson = new Gson();
            String paramJson = gson.toJson(param, param.getClass());
            StringEntity stringEntity = new StringEntity(paramJson);
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, System.getProperty("http.agent"));
            httppost.setEntity(stringEntity);
            HttpResponse response = httpClient.execute(httppost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String result = gson.fromJson(reader, String.class);
                inputStream.close();
                return result;
            } else
                throw new Exception("Запрос завершился ошибкой " + statusCode);
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }

        return "";
    }

    public static <TParam, TResult> Result<TResult[]> commonServiceCall(String ipAddress,
                                                                        String port,
                                                                        String serviceName,
                                                                        TParam param) {
        Result<TResult[]> result = new Result<>();

        try {
            if (TextUtils.isEmpty(ipAddress.trim()))
                throw new Exception("IP адрес сервера не задан");

            if (TextUtils.isEmpty(port.trim()))
                throw new Exception("Порт сервера не задан");

            if (TextUtils.isEmpty(serviceName.trim()))
                throw new Exception("Имя сервиса не задано");

            String serviceResult = restServiceCall(ipAddress, port, serviceName, param);

            if (TextUtils.isEmpty(serviceResult))
                throw new Exception("Ошибка выполнения запроса к службе");

            Gson gson = new Gson();
            result = gson.fromJson(serviceResult, new TypeToken<Result<TResult>>() {
            }.getType());

            if (!result.BoolRes)
                Log.e(LOG_TAG, "Ошибка вызова веб-службы. " + result.ErrorRes);

            result.ErrorRes = serviceResult;
            return result;
        } catch (Exception ex) {
            result.ErrorRes = ex.getMessage();
            Log.e(LOG_TAG, result.ErrorRes);
            result.BoolRes = false;
        }

        return result;
    }
}
