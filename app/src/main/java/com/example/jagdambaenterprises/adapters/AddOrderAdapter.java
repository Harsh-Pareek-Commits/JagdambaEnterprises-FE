package com.example.jagdambaenterprises.adapters;

import android.content.Context;

import android.util.Log;

import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jagdambaenterprises.R;
import com.example.jagdambaenterprises.domains.Order;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AddOrderAdapter extends RecyclerView.Adapter<AddOrderAdapter.ViewHolder> {
    private String apiBaseUrl;

    public AddOrderAdapter(Context context) {
        apiBaseUrl = context.getString(R.string.api_url);
    }

    public AddOrderAdapter(ArrayList<Object> objects, View parentView) {
    }

    public void addOrder(Order order, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        Gson gson = new Gson();
        String jsonProduct = gson.toJson(order);
        Log.w("Payload", jsonProduct);
        RequestBody body = RequestBody.create(mediaType,jsonProduct);
        Request request = new Request.Builder()
                .url(apiBaseUrl+"/order/create/stock-order")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    @NonNull
    @Override
    public AddOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AddOrderAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
