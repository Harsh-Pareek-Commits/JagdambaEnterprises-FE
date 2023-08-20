package com.example.jagdambaenterprises;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jagdambaenterprises.adapters.ProductSearchAdapter;
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
        private SearchView searchView;
        private ProductSearchAdapter productSearchAdapter;
        private List<Product> productListOriginal; // Store the original product list


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            setContentView(R.layout.activity_stock_view);
            searchView = findViewById(R.id.searchView);

            searchView.setIconifiedByDefault(false);

            searchView.setFocusable(false);
            searchView.setImeOptions(EditorInfo.IME_ACTION_NONE);



            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            stockAdapter = new StockAdapter(new ArrayList<>()); // Initialize the adapter with an empty list
            recyclerView.setAdapter(stockAdapter);
            fetchProductDetails();
            productSearchAdapter = new ProductSearchAdapter(stockAdapter.getProductList());
            // Set up the search functionality
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterProductList(newText); // Filter the product list based on the search query
                    return true;
                }
            });

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
                        productListOriginal=productList;
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
            stockAdapter.setProductList(filteredProducts); // Update the adapter with filtered list
        }

        private boolean containsPartialMatch(String original, String query) {
            if (original == null || query == null) {
                return false;
            }
            return original.toLowerCase().contains(query.toLowerCase());
        }
    }

