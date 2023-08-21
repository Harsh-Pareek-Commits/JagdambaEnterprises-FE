package com.example.jagdambaenterprises;

import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import com.example.jagdambaenterprises.R;
import com.example.jagdambaenterprises.constants.Category;
import com.example.jagdambaenterprises.domains.Product;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class PrepareOrderActivity extends AppCompatActivity {

    private List<Product> productList;
    private LinearLayout cardContainer;

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
            } else {
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
        } else {
            generatePDF();
        }
    }

    private void addProductRow(Product product, LinearLayout cardContainer) {
        String s = "";

        // ... Your sizeText calculation ...

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
        nameBrandSize.setText("Name: " + product.getName() + "\n" + "Brand: " + product.getBrand() + "\n" + "Size " + s);
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

    private void generatePDF() {

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = new PdfWriter(stream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);

            //Add company logo image to the PDF
            Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
            ByteArrayOutputStream logoStream = new ByteArrayOutputStream();
            logoBitmap.compress(Bitmap.CompressFormat.PNG, 0 /* Ignored for PNG */, logoStream);
            ImageData imageData = ImageDataFactory.create(logoStream.toByteArray());
            Image image = new Image(imageData);
            document.add(image);

            // Add a header to the PDF
            Paragraph header = new Paragraph("Order Details")
                    .setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER)
                    .setFontSize(20);
            document.add(header);

            // Add order details
            for (Product product : productList) {
                if (product.getRequestedQunatity() > 0) {
                    Paragraph orderDetails = new Paragraph(product.getName() + " " + product.getBrand() + " " + product.getSize() + " " + product.getRequestedQunatity())
                            .setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER)
                            .setFontSize(16);
                    document.add(orderDetails);
                }
            }


            document.close();
            File pdfFile = new File(getExternalFilesDir(null), "order.pdf");
            FileOutputStream fos = new FileOutputStream(pdfFile);
            byte[] pdfData = stream.toByteArray();
            fos.write(pdfData);
            fos.close();

            // Initiate download using Android Download Manager
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri pdfUri = FileProvider.getUriForFile(this, "com.example.jagdambaenterprises.fileprovider", pdfFile);

            DownloadManager.Request request = new DownloadManager.Request(pdfUri);
            request.setMimeType("application/pdf");
            request.setTitle("Order PDF");
            request.setDescription("Downloading your order PDF");
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "order.pdf");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setAllowedOverMetered(true);
            request.setAllowedOverRoaming(true);

            // Enqueue the download request
            downloadManager.enqueue(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
}
}
