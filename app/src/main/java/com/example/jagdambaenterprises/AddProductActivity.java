package com.example.jagdambaenterprises;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.jagdambaenterprises.domains.Product;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;

public class AddProductActivity extends AppCompatActivity {

    private EditText nameEditText, brandEditText, costPriceEditText, sellingPriceEditText,
            wholesalePriceEditText,sizeEditText, quantityEditText, aspectRatioEditText, widthEditText, rimEditText, productSizeEditText, aspectRation,width,rim;
    private Spinner tireTypeSpinner, productSizeUnitSpinner, categorySpinner,vehicleType;
    private Button addButton;
    private LinearLayout tyerDetailsLayout, productDetailsLayout;

    private OkHttpClient client;
    private Gson gson;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        client = new OkHttpClient();
        gson = new GsonBuilder().create();

        nameEditText = findViewById(R.id.editProductName);
        brandEditText = findViewById(R.id.editProductBrand);
        costPriceEditText = findViewById(R.id.editCostPrice);
        sellingPriceEditText = findViewById(R.id.editSellingPrice);
        wholesalePriceEditText = findViewById(R.id.editWholesalePrice);
        quantityEditText = findViewById(R.id.editQuantity);
        aspectRatioEditText = findViewById(R.id.editAspectRatio);
        widthEditText = findViewById(R.id.editWidth);
        rimEditText = findViewById(R.id.editRim);
        productSizeEditText = findViewById(R.id.editProductSize);
        tireTypeSpinner = findViewById(R.id.spinnerTireType);
        sizeEditText = findViewById(R.id.editProductSize);
        productSizeUnitSpinner = findViewById(R.id.spinnerProductSizeUnit);
        categorySpinner = findViewById(R.id.spinnerCategory);
        addButton = findViewById(R.id.buttonAddProduct);
        tyerDetailsLayout = findViewById(R.id.tyerDetailsLayout);
        productDetailsLayout = findViewById(R.id.OtherProdDetailsLayout);
      aspectRation=findViewById(R.id.editAspectRatio);
      width=findViewById(R.id.editWidth);
      rim=findViewById(R.id.editRim);
      vehicleType=findViewById(R.id.spinnerVehicalType);

        ArrayAdapter<CharSequence> tireTypeAdapter = ArrayAdapter.createFromResource(
                this, R.array.tire_types, android.R.layout.simple_spinner_item);
        tireTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tireTypeSpinner.setAdapter(tireTypeAdapter);


        ArrayAdapter<CharSequence> productSizeUnitAdapter = ArrayAdapter.createFromResource(
                this, R.array.sizeUnit, android.R.layout.simple_spinner_item);
        productSizeUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSizeUnitSpinner.setAdapter(productSizeUnitAdapter);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.category, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> vehicleTypeAdapter = ArrayAdapter.createFromResource(
                this, R.array.VehicalType, android.R.layout.simple_spinner_item);
        vehicleTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleType.setAdapter(vehicleTypeAdapter);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                String brand = brandEditText.getText().toString();
                // Get other values from the EditText fields
                // Get selected spinner values for tireType, size, and category

                // Create a Product object with the values
                Product product = new Product();
                product.setName(name);
                product.setBrand(brand);
                // Set other values for the product object

                // Call the API to add the product
                try {
                    addProductToServer(product);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedCategory = adapterView.getItemAtPosition(position).toString();

                if ("Tyer".equals(selectedCategory)) {
                    tyerDetailsLayout.setVisibility(View.VISIBLE);
                    productDetailsLayout.setVisibility(View.GONE);
                } else if ("Other".equals(selectedCategory)) {
                    tyerDetailsLayout.setVisibility(View.GONE);
                    productDetailsLayout.setVisibility(View.VISIBLE);
                } else {
                    tyerDetailsLayout.setVisibility(View.GONE);
                    productDetailsLayout.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
    }

    private void addProductToServer(Product product) throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String productJson = gson.toJson(product);

        RequestBody requestBody = RequestBody.create(JSON, productJson);
        Request request = new Request.Builder()
                .url(getString(R.string.api_url))
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            // Handle success response
        } else {
            // Handle error response
        }
    }
}
