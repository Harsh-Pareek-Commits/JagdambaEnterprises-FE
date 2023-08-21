package com.example.jagdambaenterprises;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jagdambaenterprises.R;

import com.example.jagdambaenterprises.adapters.OrderStockAdapter;
import com.example.jagdambaenterprises.domains.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StockOrderActivity extends AppCompatActivity  {

    private RecyclerView recyclerView;
    private OrderStockAdapter orderStockAdapter;
    private List<Product> productListOriginal;
    private List<Product> selectedProducts = new ArrayList<>();
    private SearchView searchView;
    private SparseBooleanArray selectedItems; // To store selected items' positions
    private Button prepareOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_order);
        fetchProductDetails();
        prepareOrder=findViewById(R.id.prepareOrder);
        searchView = findViewById(R.id.searchView1);
        searchView.setIconifiedByDefault(true);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderStockAdapter = new OrderStockAdapter(new ArrayList<>());
      //  orderStockAdapter.setOnCardClickListener(this); // Set the card click listener
        recyclerView.setAdapter(orderStockAdapter);

        selectedItems = new SparseBooleanArray();

        // Fetch data and set adapter...

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProductList(newText);
                return true;
            }
        });
        prepareOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareOrder();
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    onSearchExpanded();
                } else {
                    onSearchCollapsed();
                }
            }
        });
        prepareOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to PrepareOrderActivity
                for (int i = 0; i < productListOriginal.size(); i++) {
                    if (productListOriginal.get(i).isSelected()) {
                        selectedProducts.add(productListOriginal.get(i));
                    }
                }
                if (selectedProducts.isEmpty()) {
                    Toast.makeText(StockOrderActivity.this, "Please select at least one product", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(StockOrderActivity.this, PrepareOrderActivity.class);
                intent.putExtra("productList", (Serializable) selectedProducts);

                startActivity(intent);
            }
        });
    }

    // ... (Other methods)
    private void prepareOrder() {
        // Loop through the selected products and add them to the list
        List<Product> selectedProductsList = new ArrayList<>();
        for (int i = 0; i < productListOriginal.size(); i++) {
            if (productListOriginal.get(i).isSelected()) {
                selectedProductsList.add(productListOriginal.get(i));
            }
        }}

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
                    productListOriginal=productList;
                    orderStockAdapter.setProductList(productList); // Use setProductList method
                } else {
                    Toast.makeText(StockOrderActivity.this, "Failed to fetch product details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Toast.makeText(StockOrderActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("ViewStockActivity", errorMessage);
            }
        });

    }
    @Override
    public void onBackPressed() {
        // Create an intent to navigate to the previous screen
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity to prevent creating a new instance
    }
    private void filterProductList(String query) {
        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : productListOriginal) {
            if (containsPartialMatch(product.getName(), query) ||
                    containsPartialMatch(product.getSize().getAspectRatio(), query) ||
                    containsPartialMatch(product.getBrand(), query) ||
                    containsPartialMatch(product.getTyreType(), query)) {
                filteredProducts.add(product);
            }
        }
        orderStockAdapter.setProductList(filteredProducts); // Update the adapter with filtered list
    }

    private boolean containsPartialMatch(String original, String query) {
        if (original == null || query == null) {
            return false;
        }
        return original.toLowerCase().contains(query.toLowerCase());
    }
    private void onSearchExpanded() {
        prepareOrder.setVisibility(View.GONE);
        setSearchWeight(1);
    }

    private void onSearchCollapsed() {
        prepareOrder.setVisibility(View.VISIBLE);
        setSearchWeight(0);
    }
    private void setSearchWeight(int weight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) searchView.getLayoutParams();
        params.weight = weight;
        searchView.setLayoutParams(params);
    }
}
