package com.example.jagdambaenterprises;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jagdambaenterprises.adapters.StockAdapter;
import com.example.jagdambaenterprises.domains.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


    public class ViewStockActivity extends AppCompatActivity {

        private RecyclerView recyclerView;
        private StockAdapter stockAdapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_stock_view);

            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            stockAdapter = new StockAdapter(new ArrayList<>()); // Initialize the adapter with an empty list
            recyclerView.setAdapter(stockAdapter);
            fetchProductDetails();
        }

        private void fetchProductDetails() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.api_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ProductService productService = retrofit.create(ProductService.class);
            Call<List<Product>> call = productService.getAllProducts("JSESSIONID=E4CAEF499E17B96593D74D385EACDEFD");

            call.enqueue(new Callback<List<Product>>() {
                @Override
                public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                    if (response.isSuccessful()) {
                        List<Product> productList = response.body();
                        stockAdapter.setProductList(productList); // Use setProductList method
                    } else {
                        Toast.makeText(ViewStockActivity.this, "Failed to fetch product details", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Product>> call, Throwable t) {
                    String errorMessage = "Network error: " + t.getMessage();
                    Toast.makeText(ViewStockActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("ViewStockActivity", errorMessage);
                }
            });

        }
    }

