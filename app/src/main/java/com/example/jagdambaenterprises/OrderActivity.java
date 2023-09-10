package com.example.jagdambaenterprises;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jagdambaenterprises.adapters.AddOrderAdapter;
import com.example.jagdambaenterprises.service.ProductService;
import com.example.jagdambaenterprises.domains.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderActivity extends AppCompatActivity  {

    private RecyclerView recyclerView;
    private AddOrderAdapter addOrderAdapter;
    private List<Product> productListOriginal;
    private List<Product> selectedProducts = new ArrayList<>();
    private SearchView searchView;
    private SparseBooleanArray selectedItems; // To store selected items' positions
    private Button prepareOrder;
    private CardView innerCardView,productDetails;
    private LinearLayout messageLayout;
    private TextView message;
    private ImageView imageView;
private TableLayout seletectedProductTableLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);
        fetchProductDetails();
        prepareOrder=findViewById(R.id.prepareOrder);
        searchView = findViewById(R.id.searchView1);
        searchView.setIconifiedByDefault(true);
        innerCardView=findViewById(R.id.selectedProductCardView);
        messageLayout=findViewById(R.id.dropdownCardView);
        recyclerView = findViewById(R.id.recyclerView);
        View parentView = findViewById(android.R.id.content); // Get the parent view of the activity
        addOrderAdapter = new AddOrderAdapter(new ArrayList<>(), parentView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(addOrderAdapter);
        message=findViewById(R.id.selectedProductTextView);
        imageView=findViewById(R.id.idropdownicon);


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

        messageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (innerCardView.getVisibility() == View.GONE) {
                    innerCardView.setVisibility(View.VISIBLE);
                    message.setText("Tap to collapse ");
                    imageView.setImageResource(R.drawable.up);
                } else { // Corrected this line
                    innerCardView.setVisibility(View.GONE);
                    message.setText("Tap to expand");
                    imageView.setImageResource(R.drawable.down);
                }
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
                // Clear the selected products list before populating
                selectedProducts.clear();

                // Populate selected products from productListOriginal
                for (int i = 0; i < productListOriginal.size(); i++) {
                    if (productListOriginal.get(i).isSelected()) {
                        selectedProducts.add(productListOriginal.get(i));
                    }
                }

                if (selectedProducts.isEmpty()) {
                    Toast.makeText(OrderActivity.this, "Please select at least one product", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(OrderActivity.this, ConfirmOrderActivity.class);
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
        }
        Intent intent = new Intent(OrderActivity.this, ConfirmOrderActivity.class); // Replace YourCurrentActivity with the name of your current activity class
        startActivity(intent);
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
                    addOrderAdapter.setProductList(productList); // Use setProductList method
                } else {
                    Toast.makeText(OrderActivity.this, "Failed to fetch product details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Toast.makeText(OrderActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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
        addOrderAdapter.setProductList(filteredProducts); // Update the adapter with filtered list
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
