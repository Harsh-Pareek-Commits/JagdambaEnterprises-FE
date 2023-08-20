package com.example.jagdambaenterprises;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jagdambaenterprises.adapters.ProductAdapter;
import com.example.jagdambaenterprises.constants.SizeUnit;
import com.example.jagdambaenterprises.domains.Product;
import com.example.jagdambaenterprises.domains.Size;
import com.example.jagdambaenterprises.domains.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;

public class AddProductActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private FrameLayout cameraPreview;
    private Button captureButton;

    private EditText nameEditText, brandEditText, costPriceEditText, sellingPriceEditText,
            wholesalePriceEditText,sizeEditText, quantityEditText, aspectRatioEditText, widthEditText, rimEditText, productSizeEditText, aspectRation,width,rim;
    private Spinner TyreTypeSpinner, productSizeUnitSpinner, categorySpinner,vehicleType;
    private Button addButton;
    private Switch istoggleSwtich;
    private LinearLayout TyreDetailsLayout, productDetailsLayout;

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
        TyreTypeSpinner = findViewById(R.id.spinnerTyreType);
        sizeEditText = findViewById(R.id.editProductSize);
        productSizeUnitSpinner = findViewById(R.id.spinnerProductSizeUnit);
        categorySpinner = findViewById(R.id.spinnerCategory);
        addButton = findViewById(R.id.buttonAddProduct);
        TyreDetailsLayout = findViewById(R.id.TyreDetailsLayout);
        productDetailsLayout = findViewById(R.id.OtherProdDetailsLayout);
        aspectRation=findViewById(R.id.editAspectRatio);
        width=findViewById(R.id.editWidth);
        rim=findViewById(R.id.editRim);
        vehicleType=findViewById(R.id.spinnerVehicalType);
        istoggleSwtich=findViewById(R.id.isTubeless);
        cameraPreview = findViewById(R.id.cameraPreview);
        captureButton = findViewById(R.id.captureButton);
        ArrayAdapter<CharSequence> TyreTypeAdapter = ArrayAdapter.createFromResource(
                this, R.array.Tyre_types, android.R.layout.simple_spinner_item);
        TyreTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        TyreTypeSpinner.setAdapter(TyreTypeAdapter);


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
        aspectRatioEditText.setFilters(new InputFilter[] { new NumericAndSlashInputFilter() });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                String brand = brandEditText.getText().toString();
                Float costPrice =Float.parseFloat( costPriceEditText.getText().toString());
                Float sellingPrice = Float.parseFloat( sellingPriceEditText.getText().toString());
                Float wholesalePrice =Float.parseFloat(  wholesalePriceEditText.getText().toString());
                Integer quantity = Integer.parseInt(quantityEditText.getText().toString());
                String aspectRatio =aspectRatioEditText.getText().toString();

                if(widthEditText.getText().toString().equals("")){
                    widthEditText.setText("0");
                }
                Float width = Float.parseFloat(widthEditText.getText().toString());
                Integer rim =Integer.parseInt( rimEditText.getText().toString());
                if(productSizeEditText.getText().toString().equals("")){
                    productSizeEditText.setText("0");
                }
                Integer productSize = Integer.parseInt(productSizeEditText.getText().toString());
                String selectedTyreType = TyreTypeSpinner.getSelectedItem().toString();
                SizeUnit selectedSizeUnit = SizeUnit.valueOf(productSizeUnitSpinner.getSelectedItem().toString());
                String selectedCategory = categorySpinner.getSelectedItem().toString();
                String selectedVehicleType = vehicleType.getSelectedItem().toString();
                User user=new User();
                user.setId(1);
                Product product = new Product();
                product.setUser(user);
                product.setName(name);
                product.setBrand(brand);
                product.setCostPrice(costPrice);
                product.setSellingPrice(sellingPrice);
                product.setWholesalePrice(wholesalePrice);
                product.setQuantity(quantity);
                product.setIstubeless(istoggleSwtich.isChecked());
                product.setTyreType(selectedTyreType);

                product.setCategory(selectedCategory);
                product.setVehicleType(selectedVehicleType);
                Size size= new Size();
                size.setAspectRatio(aspectRatio);
                size.setWidth(width);
                size.setRim(rim);
                size.setProductSize(productSize);
                size.setSizeUnit(selectedSizeUnit);
                product.setSize(size);
                product.setStatus(true);



                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        ProductAdapter apiClient = new ProductAdapter(AddProductActivity.this);
                        apiClient.addProductToApi(product, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                // Handle failure
                                Log.e("AddProductActivity", "Failed to add product", e);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Update UI or show a Toast indicating the failure
                                        Toast.makeText(AddProductActivity.this, "Failed to add product: "+ e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    // Product added successfully
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Update UI or show a Toast indicating success
                                            Toast.makeText(AddProductActivity.this, "Product added successfully.", Toast.LENGTH_SHORT).show();
                                            // Finish the current activity to restart it
                                            finish();

// Start the same activity again
                                            Intent intent = new Intent(AddProductActivity.this, AddProductActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Update UI or show a Toast indicating the failure
                                            Toast.makeText(AddProductActivity.this, "Failed to add product. Please try again later.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                        return null;
                    }
                }.execute();

            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedCategory = adapterView.getItemAtPosition(position).toString();

                if ("Tyre".equals(selectedCategory)) {
                    TyreDetailsLayout.setVisibility(View.VISIBLE);
                    productDetailsLayout.setVisibility(View.GONE);
                } else if ("Other".equals(selectedCategory)) {
                    TyreDetailsLayout.setVisibility(View.GONE);
                    productDetailsLayout.setVisibility(View.VISIBLE);
                } else {
                    TyreDetailsLayout.setVisibility(View.GONE);
                    productDetailsLayout.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
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
    private class NumericAndSlashInputFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                char character = source.charAt(i);
                if (!Character.isDigit(character) && character != '/') {
                    return "";
                }
            }
            return null;
        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // ...

    protected byte[]  getImageByte(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            return byteArray;
        }
        return null;
    }

}
