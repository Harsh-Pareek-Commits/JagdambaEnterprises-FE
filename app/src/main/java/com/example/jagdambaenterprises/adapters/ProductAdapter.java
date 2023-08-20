package com.example.jagdambaenterprises.adapters;

import static android.provider.Settings.System.getString;

import android.content.Context;
import android.util.Log;

import com.example.jagdambaenterprises.R;
import com.example.jagdambaenterprises.domains.Product;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class ProductAdapter {
    private String apiBaseUrl;

    public ProductAdapter(Context context) {
        apiBaseUrl = context.getString(R.string.api_url);
    }

    public void addProductToApi(Product product, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        Gson gson = new Gson();
        String jsonProduct = gson.toJson(product);
        Log.w("Payload", jsonProduct);
        RequestBody body = RequestBody.create(mediaType,jsonProduct);
        Request request = new Request.Builder()
                .url(apiBaseUrl+"/product/create")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

}

