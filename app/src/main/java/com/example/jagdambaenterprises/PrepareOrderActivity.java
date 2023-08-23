package com.example.jagdambaenterprises;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.example.jagdambaenterprises.adapters.OrderStockAdapter;
import com.example.jagdambaenterprises.adapters.ProductAdapter;
import com.example.jagdambaenterprises.constants.Category;
import com.example.jagdambaenterprises.domains.Product;
import com.example.jagdambaenterprises.domains.StockOrder;
import com.example.jagdambaenterprises.domains.User;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PrepareOrderActivity extends AppCompatActivity {


    private List<Product> productList;
    private LinearLayout cardContainer;
    private static final int SAVE_PDF_REQUEST_CODE = 123;
private boolean viewedPDF= false;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_order);

        button = findViewById(R.id.btnConfirmOrder);
        productList = (List<Product>) getIntent().getSerializableExtra("productList");
        cardContainer = findViewById(R.id.cardContainer);

        for (Product product : productList) {
            addProductRow(product, cardContainer);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrder();
            }
        });
    }

    private void confirmOrder() {
        boolean isQuantityEmpty = false;

        for (int i = 0; i < cardContainer.getChildCount(); i++) {
            CardView cardView = (CardView) cardContainer.getChildAt(i);
            LinearLayout cardContentLayout = (LinearLayout) cardView.getChildAt(0);
            TextView nameBrandSize = (TextView) ((LinearLayout) cardContentLayout.getChildAt(0)).getChildAt(0);
            EditText quantityEditText = (EditText) cardContentLayout.getChildAt(1);

            String quantityText = quantityEditText.getText().toString().trim();
            if (quantityText.isEmpty()) {
                isQuantityEmpty = true;
                break;
            }
            else {
                // Parse the quantityText to an integer
                int requestedQuantity = Integer.parseInt(quantityText);

                // Get the product name, brand, and size from the TextView
                String[] nameBrandSizeArray = nameBrandSize.getText().toString().split("\n");
                String productName = nameBrandSizeArray[0].replace("Name: ", "");
                String productBrand = nameBrandSizeArray[1].replace("Brand: ", "");
                String productSize = nameBrandSizeArray[2].replace("Size: ", "");

                // Find the matching product in the productList and update its requested quantity
                for (Product product : productList) {
                    if (product.getName().equals(productName) &&
                            product.getBrand().equals(productBrand)) {
                        product.setRequestedQunatity(requestedQuantity);
                        break; // No need to continue searching
                    }
                }
            }
        }

        if (isQuantityEmpty) {
            Toast.makeText(this, "Please fill in all quantities", Toast.LENGTH_SHORT).show();
        }
        else {
            StockOrder stockOrder = new StockOrder();
            stockOrder.setProductQuantityMap(productList);
            stockOrder.setStatus("Pending");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                stockOrder.setOrderDate(LocalDate.now().toString());
            }
            User user = new User();
            user.setId(1);
            stockOrder.setUser(user);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    OrderStockAdapter apiClient = new OrderStockAdapter(PrepareOrderActivity.this);
                    apiClient.addProductToApi(stockOrder, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            // Handle failure
                            Log.e("Prepare Order:", "Failed to add product", e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Update UI or show a Toast indicating the failure
                                    Toast.makeText(PrepareOrderActivity.this, "Failed to create stock order: "+ e.getMessage(), Toast.LENGTH_LONG).show();
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
                                        Toast.makeText(PrepareOrderActivity.this, "Stock Order created", Toast.LENGTH_SHORT).show();
                                        // Finish the current activity to restart it

                                       // Intent intent = new Intent(PrepareOrderActivity.this, AddProductActivity.class);

                                       saveAndDownloadPDF();

                                             // startActivity(intent);

                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Update UI or show a Toast indicating the failure
                                        Toast.makeText(PrepareOrderActivity.this, "Failed to create stock orer. Please try again later.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                    return null;
                }
            }.execute();


        }

    }


    private void addProductRow(Product product, LinearLayout cardContainer) {
        String s = "";
        if (product.getCategory().equals(Category.Tyre.toString())) {
            if (product.getSize().getAspectRatio() != "" && product.getSize().getAspectRatio() != null) {
                s = "Size: " + product.getSize().getAspectRatio() +
                        "-" + product.getSize().getRim();
            } else {
                s = "Size: " + product.getSize().getWidth() +
                        "-" + product.getSize().getRim();
            }
        } else {
            s = "Size: " + product.getSize().getProductSize() +
                    "-" + product.getSize().getSizeUnit();
        }


        // Create a CardView
        CardView cardView = new CardView(this);
        cardView.setLayoutParams(new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT
        ));
        cardView.setRadius(16);
        cardView.setCardElevation(8); // Set elevation to show shadow
        //cardView.setCardBackgroundColor(getResources().getColor(R.color.card_background)); // Set card background color

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 8, 16, 8);

        // Create a LinearLayout to hold the content within the CardView
        LinearLayout cardContentLayout = new LinearLayout(this);
        cardContentLayout.setLayoutParams(layoutParams);
        cardContentLayout.setOrientation(LinearLayout.HORIZONTAL);
        cardContentLayout.setPadding(16, 16, 16, 16);
        cardContentLayout.setGravity(Gravity.CENTER_VERTICAL); // Center the content vertically

        // Create a LinearLayout to hold the left-aligned content
        LinearLayout leftContentLayout = new LinearLayout(this);
        leftContentLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        leftContentLayout.setOrientation(LinearLayout.VERTICAL);
        cardContentLayout.addView(leftContentLayout);

        // Create TextView for Name, Brand, and Size
        TextView nameBrandSize = new TextView(this);
        if (product.getCategory().equals(Category.Tyre.toString())) {
            if (product.isIstubeless()) {
                nameBrandSize.setText("Name: " + product.getName() + "\n" + "Brand: " + product.getBrand() + "\n" + "Size " + s + "\n" + "Tubeless");
            } else {
                nameBrandSize.setText("Name: " + product.getName() + "\n" + "Brand: " + product.getBrand() + "\n" + "Size " + s + "\n" + "Tube Tyre");
            }
        } else{
            nameBrandSize.setText("Name: " + product.getName() + "\n" + "Brand: " + product.getBrand() + "\n" + "Size " + s);
    }
        nameBrandSize.setTextSize(18);
        leftContentLayout.addView(nameBrandSize);

        // Create EditText for Quantity
        EditText quantity = new EditText(this);
        quantity.setHint("Enter Quantity");
        quantity.setGravity(Gravity.END | Gravity.CENTER_VERTICAL); // Align to far-right and center vertically
        quantity.setInputType(InputType.TYPE_CLASS_NUMBER); // Accept numbers only
        quantity.setBackgroundResource(R.drawable.edit_text_border); // Customize the background
        quantity.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)); // Set width to wrap content
        quantity.setPadding(16, 10, 16, 10); // Add padding to the quantity EditText

        cardContentLayout.addView(quantity);

        // Add cardContentLayout to the CardView
        cardView.addView(cardContentLayout);

        // Add the CardView to the cardContainer (LinearLayout)
        cardContainer.addView(cardView);
    }

    private void saveAndDownloadPDF() {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = new PdfWriter(stream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);

            Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.jelogo);

// Resize the logoBitmap to a smaller size
            int desiredWidth = 800; // Adjust the desired width
            int desiredHeight = 300; // Adjust the desired height
            Bitmap resizedLogoBitmap = Bitmap.createScaledBitmap(logoBitmap, desiredWidth, desiredHeight, true);

            ByteArrayOutputStream logoStream = new ByteArrayOutputStream();
            resizedLogoBitmap.compress(Bitmap.CompressFormat.PNG, 100, logoStream);
            ImageData imageData = ImageDataFactory.create(logoStream.toByteArray());
            Image image = new Image(imageData);

// Set image alignment to center
            image.setHorizontalAlignment(HorizontalAlignment.CENTER);
            image.setMargins(0, 0, 0, 0)   ;
      //      image.setVerticalAlignment(VerticalAlignment.MIDDLE);

            document.add(image);
            Paragraph Name = new Paragraph("Brahmanand Pareek")
                    .setTextAlignment(com.itextpdf.layout.property.TextAlignment.LEFT)
                    .setFontSize(16)
                    .setBold();
            document.add(Name);
            Paragraph contact = new Paragraph("      9435338217")
                    .setTextAlignment(com.itextpdf.layout.property.TextAlignment.LEFT)
                    .setFontSize(16);
            document.add(contact);

            Paragraph date = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                date = new Paragraph("Date: " + java.time.LocalDate.now().toString())
                        .setTextAlignment(TextAlignment.LEFT)
                        .setFontSize(16);
            }
            document.add(date);
            // Add bill header
            Paragraph billHeader = new Paragraph("Request for Stock")
                    .setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER)
                    .setFontSize(14)
                    .setBold();
            document.add(billHeader);

            // Create a table for product details
            Table table = new Table(new float[]{1,2, 2,2,2, 1}); // 3 columns with relative widths
            table.setWidth(UnitValue.createPercentValue(100)); // Set width to 100% of the page width
            // Add the cell to the table
            // Add table header row
            table.addHeaderCell("S.No.").setBold().setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell("Name").setBold().setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell("Brand").setBold().setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell("Size").setBold().setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell("Category").setBold().setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell("Quantity").setBold().setTextAlignment(TextAlignment.CENTER);

            for (Product product : productList) {
                int x=1;
                String s= "";
                String name="";
                if (product.getCategory().equals(Category.Tyre.toString())) {
                if(product.getSize().getAspectRatio()!="" && product.getSize().getAspectRatio()!=null) {
                    s="Size: " +  product.getSize().getAspectRatio() +
                            "-" + product.getSize().getRim();
                }
                else{
                    s="Size: " +  product.getSize().getWidth() +
                            "-" + product.getSize().getRim() ;
                }
            if (product.isIstubeless()){
                name= product.getName() + " (Tubeless)";
                    }
            else{
                name= product.getName() + " (Tube Tyre)";
            }
                }
        else {
                s="Size: " +  product.getSize().getProductSize() +
                        "-" + product.getSize().getSizeUnit();
                name=product.getName();
            }

                if (product.getRequestedQunatity() > 0) {
                    table.addCell(String.valueOf(x));
                    table.addCell(name);
                    table.addCell(product.getBrand());
                    table.addCell(s);
                    table.addCell(product.getCategory());
                    table.addCell(String.valueOf(product.getRequestedQunatity()));
                }
                x=x+1;
            }

            document.add(table);
            document.close();

            // Save the PDF file
            File pdfFile = new File(getExternalFilesDir(null), "JagdambaEnterprisesOrder.pdf");
            FileOutputStream fos = new FileOutputStream(pdfFile);
            byte[] pdfData = stream.toByteArray();
            fos.write(pdfData);
            fos.close();

            // Share the PDF using an Intent
            saveAndSharePDF(pdfFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveAndSharePDF(File pdfFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri pdfUri = FileProvider.getUriForFile(this, "com.example.jagdambaenterprises.fileprovider", pdfFile);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Open PDF using..."));
        viewedPDF=true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (viewedPDF){
            Intent intent = new Intent(PrepareOrderActivity.this, HomeActivity.class);

            startActivity(intent);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SAVE_PDF_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveAndDownloadPDF();
            } else {
                Toast.makeText(this, "Permission denied. Cannot save PDF.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
