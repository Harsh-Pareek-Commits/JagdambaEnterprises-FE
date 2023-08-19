package com.example.jagdambaenterprises;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private ImageView iconAddStock, iconViewStock, iconPlaceOrder, iconViewOrder,iconViewProduct,iconAddProduct, iconViewBike,iconAddBike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        iconAddStock = findViewById(R.id.iconAddStock);
        iconViewStock = findViewById(R.id.iconViewStock);
        iconPlaceOrder = findViewById(R.id.iconPlaceOrder);
        iconViewOrder = findViewById(R.id.iconViewOrder);
        iconViewProduct=findViewById(R.id.iconViewProduct);
        iconAddProduct=findViewById(R.id.iconAddProduct);
        iconViewBike=findViewById(R.id.iconViewBike);
        iconViewBike=findViewById(R.id.iconAddBike);
        iconAddStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToAddStockScreen();
            }
        });

        iconViewStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToViewStockScreen();// Handle clicking on the "View Stock" icon
                // Navigate to the ViewStockActivity or perform necessary action
            }
        });

        iconPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle clicking on the "Place Order" icon
                // Navigate to the PlaceOrderActivity or perform necessary action
            }
        });

        iconViewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Navigate to the ViewOrderActivity or perform necessary action
            }
        });
        iconAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToAddProductScreen();                // Navigate to the ViewOrderActivity or perform necessary action
            }
        });
    }

    private void navigateToAddStockScreen() {
        Intent intent = new Intent(this, AddStockActivity.class);
        startActivity(intent);
        finish(); // Close the login screen so that back button doesn't bring you back to it
    }
    private void navigateToViewStockScreen() {
        Intent intent = new Intent(this, ViewStockActivity.class);
        startActivity(intent);
        finish(); // Close the login screen so that back button doesn't bring you back to it
    }
    private void navigateToAddProductScreen() {
        Intent intent = new Intent(this, AddProductActivity.class);
        startActivity(intent);
        finish(); // Close the login screen so that back button doesn't bring you back to it
    }
}
