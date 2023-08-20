package com.example.jagdambaenterprises;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class AddStockActivity extends AppCompatActivity {

    private EditText editProductName, editCostPrice, editSellPrice, editDistributorPrice, editQuantity;
    private Spinner spinnerProductSize, spinnerDiameter;
    private Button btnAddStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock);

        editProductName = findViewById(R.id.editProductName);
        editCostPrice = findViewById(R.id.editCostPrice);
        editSellPrice = findViewById(R.id.editSellPrice);
        editDistributorPrice = findViewById(R.id.editDistributorPrice);
        editQuantity = findViewById(R.id.editQuantity);
        spinnerProductSize = findViewById(R.id.spinnerProductSize);
        spinnerDiameter = findViewById(R.id.spinnerDiameter);
        btnAddStock = findViewById(R.id.btnAddStock);

        // Populate spinners with values
        ArrayAdapter<String> productSizeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"2.75", "3.75", "100-90"});
        productSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProductSize.setAdapter(productSizeAdapter);

        ArrayAdapter<String> diameterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"-12", "17", "18"});
        diameterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDiameter.setAdapter(diameterAdapter);

        btnAddStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve values from the UI elements
                String productName = editProductName.getText().toString();
                String productSize = spinnerProductSize.getSelectedItem().toString();
                String diameter = spinnerDiameter.getSelectedItem().toString();
                int costPrice = Integer.parseInt(editCostPrice.getText().toString());
                int sellPrice = Integer.parseInt(editSellPrice.getText().toString());
                int distributorPrice = Integer.parseInt(editDistributorPrice.getText().toString());
                int quantity = Integer.parseInt(editQuantity.getText().toString());

                // TODO: Add stock to the database or perform necessary actions
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
}

