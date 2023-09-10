package com.example.jagdambaenterprises;

import static com.itextpdf.kernel.colors.DeviceRgb.BLACK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.jagdambaenterprises.adapters.AddOrderAdapter;
import com.twilio.rest.api.v2010.account.Message;

import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.example.jagdambaenterprises.constants.Category;
import com.example.jagdambaenterprises.constants.PaymentMode;
import com.example.jagdambaenterprises.domains.Customer;
import com.example.jagdambaenterprises.domains.Order;
import com.example.jagdambaenterprises.domains.Product;
import com.example.jagdambaenterprises.domains.User;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.twilio.Twilio;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.*;
import okhttp3.Response;

@SuppressLint("MissingInflatedId")

public class ConfirmOrderActivity extends AppCompatActivity {


    private List<Product> productList;
    private LinearLayout cardContainer;
    private static final int SAVE_PDF_REQUEST_CODE = 123;
    private boolean viewedPDF= false;
    private Button button;
    Float total = 0.0f;
    private  TextView totalTextView,discounted;
    private Order  orderDetails = new Order();
    private EditText custName,custContact,custAddress,gstNumber,discount;

    private Spinner orderTypeSpinner,employeeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        custName = findViewById(R.id.custName);
        custContact = findViewById(R.id.custNumber);
        custAddress = findViewById(R.id.address);

        setContentView(R.layout.activity_confirm_order);
        orderTypeSpinner = findViewById(R.id.orderType);
        ArrayAdapter<CharSequence> orderTypeAdapter = ArrayAdapter.createFromResource(
                this, R.array.orderType, android.R.layout.simple_spinner_item);
        orderTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderTypeSpinner.setAdapter(orderTypeAdapter);
        gstNumber= findViewById(R.id.gst);
        button = findViewById(R.id.btnConfirmOrder);
        discount = findViewById(R.id.discount);
        employeeSpinner = findViewById(R.id.emp);
        productList = (List<Product>) getIntent().getSerializableExtra("productList");
        cardContainer = findViewById(R.id.cardContainer);

        for (Product product : productList) {
            boolean islastRow = false;
            if(productList.indexOf(product)==productList.size()-1){
                islastRow=true;
            }
            addProductRow(product, cardContainer,islastRow);
        }
        totalTextView=findViewById(R.id.total);
        totalTextView.setText("Rs"+total);
        orderTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                updateTotal();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle when nothing is selected
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrder();
            }
        });
        discount.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(discount.getText().toString().isEmpty()){
                    totalTextView.setText("Rs"+total);
                }
                else{
                    totalTextView.setText("Rs"+(total-Float.parseFloat(discount.getText().toString())));
                }
                return false;
            }
        });

    }
    private boolean validatePhoneNumber() {
        String phoneNumber = custContact.getText().toString().trim();

        if (phoneNumber.length() < 10 || phoneNumber.length() > 10) {
            Toast.makeText(this, "Please enter a 10-digit phone number", Toast.LENGTH_SHORT).show();
            custContact.requestFocus(); // Focus back on the EditText
            return true;
        }
        return false;
    }
    private boolean validateCustName() {
        String phoneNumber = custContact.getText().toString().trim();

        if (phoneNumber.length() < 10 || phoneNumber.length() > 10) {
            Toast.makeText(this, "Please enter a valid Name", Toast.LENGTH_SHORT).show();
            custName.requestFocus(); // Focus back on the EditTex;
            return true;
        }
        return false;
    }
    private boolean validateCustAddress() {
        String custAdd = custAddress.getText().toString().trim();

        if (custAdd.length() <=0) {
            Toast.makeText(this, "Please enter a valid Address", Toast.LENGTH_SHORT).show();
            custAddress.requestFocus(); // Focus back on the EditText
            return true;
        }
        return false;
    }
    private void confirmOrder() {
        if (validateCustName()|| validatePhoneNumber()|| validateCustAddress())
            return;

        boolean isQuantityEmpty = false;
        custName = findViewById(R.id.custName);
        custContact = findViewById(R.id.custNumber);
        custAddress = findViewById(R.id.address);
        long currentTimeMillis = System.currentTimeMillis();
        long nanoTime = System.nanoTime();
        long randomValue = new Random().nextLong();
        long oId = currentTimeMillis * 1000000 + nanoTime + randomValue;

        String orderId = "JG" + String.format("%04d", oId % 10000);

            orderDetails.setOrderId(orderId);
            orderDetails.setTotalAmount(total);
            orderDetails.setDiscountAmount(0);
            orderDetails.setPaymentMode(PaymentMode.CASH);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                orderDetails.setOrderDate(LocalDate.now().toString());
            }

            orderDetails.setPaymentStatus(true);
            orderDetails.setAmountPaid(0);
            orderDetails.setCustomer(null);
            orderDetails.setUser(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                orderDetails.setOrderDate(LocalDate.now().toString());
            }
            Customer customer = new Customer();
            customer.setName(custName.getText().toString());
            customer.setAddress(custAddress.getText().toString());
            customer.setPhoneNumber(Long.parseLong(custContact.getText().toString()));
            Map<Product, Integer> productQuantityMap = new HashMap<>();
            for (Product product : productList) {
                if (product.getRequestedQunatity() > 0) {
                    Integer qnt=product.getRequestedQunatity();
                    productQuantityMap.put(product, qnt);
                }else{
                    productList.remove(product);
                }
            }
            orderDetails.setProductQuantityMap(productQuantityMap);
            User user = new User();
            user.setId(1);
            orderDetails.setCustomer(customer);
            orderDetails.setUser(user);

            saveAndDownloadInvoicePDF2();
        String hindi = "नमस्ते "+custName.getText().toString()+",\n" +
                "आपका आदेश आईडी: "+orderDetails.getOrderId()+"\n" +
                "कुल: "+(orderDetails.getTotalAmount() - Float.parseFloat(discount.getText().toString()))+".\n" +
                "हमारी सेवा का धन्यवाद!\n" +
                "सादर,\n" +
                "जगदंबा एंटरप्राइजेस";

        sendSms(hindi);


           new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    AddOrderAdapter apiClient = new AddOrderAdapter(ConfirmOrderActivity.this);
                    apiClient.addOrderAPI(orderDetails, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            // Handle failure
                            Log.e("Prepare Order:", "Failed to add product", e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Update UI or show a Toast indicating the failure
                                    Toast.makeText(ConfirmOrderActivity.this, "Failed to create stock order: "+ e.getMessage(), Toast.LENGTH_LONG).show();
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
                                        Toast.makeText(ConfirmOrderActivity.this, "Stock Order created", Toast.LENGTH_SHORT).show();
                                        // Finish the current activity to restart it

                                        // Intent intent = new Intent(PrepareOrderActivity.this, AddProductActivity.class);

                                        saveAndDownloadInvoicePDF();

                                        // startActivity(intent);

                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Update UI or show a Toast indicating the failure
                                        Toast.makeText(ConfirmOrderActivity.this, "Failed to create stock orer. Please try again later.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                    return null;
                }
            }.execute();


        }


 private void addProductRow(Product product, LinearLayout cardContainer, boolean lastRow) {
        String sizeInfo;

        if (product.getCategory().equals(Category.Tyre.toString())) {
            sizeInfo = product.getSize().getAspectRatio() != null && !product.getSize().getAspectRatio().isEmpty()
                    ? "Size: " + product.getSize().getAspectRatio() + "-" + product.getSize().getRim()
                    : "Size: " + product.getSize().getWidth() + "-" + product.getSize().getRim();
        } else {
            sizeInfo = "Size: " + product.getSize().getProductSize() + "-" + product.getSize().getSizeUnit();
        }

        // Create a CardView
        CardView cardView = new CardView(this);
        cardView.setLayoutParams(new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT
        ));
        cardView.setRadius(16);
        cardView.setCardElevation(8);

        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        gridLayout.setColumnCount(4);
        gridLayout.setPadding(16, 16, 16, 16);
        cardView.addView(gridLayout);

        // Name, Brand, and Size TextView
        LinearLayout.LayoutParams nameBrandSizeLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        nameBrandSizeLayoutParams.setMargins(0, 0, 16, 0);

        TextView nameBrandSize = new TextView(this);
        String categorySpecificInfo = product.getCategory().equals(Category.Tyre.toString())
                ? (product.isIstubeless() ? "Tubeless" : "Tube Tyre")
                : "";
        String fullInfo = String.format("Name: %s\nBrand: %s\n%s \n(%s)", product.getName(), product.getBrand(), sizeInfo, categorySpecificInfo);
        nameBrandSize.setText(fullInfo);
        nameBrandSize.setTextSize(18);
        nameBrandSize.setLayoutParams(nameBrandSizeLayoutParams);
        gridLayout.addView(nameBrandSize);

        // Quantity Spinner
        Spinner quantitySpinner = new Spinner(this);
        ArrayList<String> quantityOptions = new ArrayList<>();
        for (int i = 0; i <= product.getQuantity(); i++) {
            quantityOptions.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, quantityOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantitySpinner.setAdapter(adapter);
        quantitySpinner.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        quantitySpinner.setBackgroundResource(R.drawable.edit_text_border);
        quantitySpinner.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        quantitySpinner.setPadding(16, 10, 16, 10);
        int selectedIndex = quantityOptions.indexOf(String.valueOf(product.getRequestedQunatity())); // Corrected typo here
        if (selectedIndex >= 0) {
            quantitySpinner.setSelection(selectedIndex);
        }
        GridLayout.LayoutParams quantitySpinnerLayoutParams = new GridLayout.LayoutParams();
        quantitySpinnerLayoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
        quantitySpinnerLayoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        quantitySpinnerLayoutParams.columnSpec = GridLayout.spec(1);
        quantitySpinnerLayoutParams.setMargins(16, 0, 16, 0);
        quantitySpinner.setLayoutParams(quantitySpinnerLayoutParams);
        gridLayout.addView(quantitySpinner);

        // Price TextView
        TextView priceTextView = new TextView(this);
        priceTextView.setText("Rs " + product.getSellingPrice()); // Adjust the formatting as needed
        priceTextView.setTextSize(18);
        GridLayout.LayoutParams priceTextViewLayoutParams = new GridLayout.LayoutParams();
        priceTextViewLayoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
        priceTextViewLayoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        priceTextViewLayoutParams.columnSpec = GridLayout.spec(2);
        priceTextViewLayoutParams.setMargins(30, 0, 0, 0);
        priceTextView.setLayoutParams(priceTextViewLayoutParams);
        gridLayout.addView(priceTextView);

        // Product ID TextView
        TextView prodId = new TextView(this);
        prodId.setVisibility(View.INVISIBLE);
        prodId.setText(product.getId().toString());
        LinearLayout.LayoutParams prodIdLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        prodIdLayout.setMargins(0, 0, 16, 0);
        prodId.setLayoutParams(prodIdLayout);
        gridLayout.addView(prodId);

        cardContainer.addView(cardView);

        float price = product.getSellingPrice() * product.getRequestedQunatity(); // Corrected typo here
        total += price;

        quantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                product.setRequestedQunatity(Integer.parseInt((String) adapterView.getItemAtPosition(i)));
                updateTotal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle when nothing is selected
            }
        });
    }

    private void sendSms(String message) {
        OkHttpClient client = new OkHttpClient();
        final String ACCOUNT_SID = "ACc82a9be2c71697b7dd42833606112d31";
        final String AUTH_TOKEN = "16338164fafbed54dd0bc9073ab19389";

        String url = "https://api.twilio.com/2010-04-01/Accounts/" + ACCOUNT_SID + "/Messages.json";

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        formBodyBuilder
                .add("To", "+91" +orderDetails.getCustomer().getPhoneNumber()) // Update with the desired phone number
                .add("From", "+18159823385") // Your Twilio phone number
                .add("Body", message);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Basic " + Base64.encodeToString((ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP))
                .post(formBodyBuilder.build())
                .build();

        SendTwilioMessageTask sendTwilioMessageTask = new SendTwilioMessageTask();
        sendTwilioMessageTask.execute(request);
    }

    private class SendTwilioMessageTask extends AsyncTask<Request, Void, String> {

        @Override
        protected String doInBackground(Request... requests) {
            OkHttpClient client = new OkHttpClient();

            try {
                Response response = client.newCall(requests[0]).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    return "Request failed with code: " + response.code();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Handle the result here, e.g., update UI or show a toast
            Toast.makeText(ConfirmOrderActivity.this,"SMS sent successfully to Customer", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAndDownloadInvoicePDF() {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = new PdfWriter(stream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            pdfDocument.setDefaultPageSize(PageSize.A4);

            Document document = new Document(pdfDocument);
            document.setStrokeColor(BLACK);
            document.setStrokeWidth(5);
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
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(16)
                    .setBold();
            document.add(Name);
            Paragraph contact = new Paragraph("      9435338217")
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(16);
            document.add(contact);

            Paragraph date = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                date = new Paragraph("Date: " + LocalDate.now().toString())
                        .setTextAlignment(TextAlignment.LEFT)
                        .setFontSize(16);
            }
            document.add(date);
            // Add bill header
            Paragraph billHeader = new Paragraph("Request for Stock")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14)
                    .setBold();
            document.add(billHeader);

            // Create a table for product details
            Table table = new Table(new float[]{1,2, 2,2,2, 1}); // 3 columns with relative widths
            table.setWidth(UnitValue.createPercentValue(100)); // Set width to 100% of the page width
            // Add the cell to the table
            // Add table header row
            table.addHeaderCell("S.No.").setBold().setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell("NAME").setBold().setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell("HSN Code").setBold().setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell("Qty").setBold().setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell("Price").setBold().setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell("Amount").setBold().setTextAlignment(TextAlignment.CENTER);

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
                    table.addCell(name+"-"+product.getBrand()+"\n"+s);
                    table.addCell("123");
                    table.addCell(String.valueOf(product.getRequestedQunatity()));
                    table.addCell(String.valueOf(product.getSellingPrice()));
                    table.addCell(String.valueOf(product.getSellingPrice()*product.getRequestedQunatity()* product.getSellingPrice()));
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

    private void saveAndDownloadInvoicePDF2() {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = new PdfWriter(stream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            pdfDocument.setDefaultPageSize(PageSize.A4);

            Document document = new Document(pdfDocument);
            document.setStrokeColor(BLACK);
            document.setStrokeWidth(5);
            Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.jelogo);

// Resize the logoBitmap to a smaller size
            int desiredWidth = 550; // Adjust the desired width
            int desiredHeight = 200; // Adjust the desired height
            Bitmap resizedLogoBitmap = Bitmap.createScaledBitmap(logoBitmap, desiredWidth, desiredHeight, true);

            ByteArrayOutputStream logoStream = new ByteArrayOutputStream();
            resizedLogoBitmap.compress(Bitmap.CompressFormat.PNG, 100, logoStream);
            ImageData imageData = ImageDataFactory.create(logoStream.toByteArray());
            Image image = new Image(imageData);

// Set image alignment to center
            image.setHorizontalAlignment(HorizontalAlignment.CENTER);
            image.setMargins(0, 0, 0, 0)   ;
            //      image.setVerticalAlignment(VerticalAlignment.MIDDLE);
            Table main = new Table(new float[]{1});
            main.setWidth(UnitValue.createPercentValue(100));
            main.addCell(image);
            Table secondRow = new Table(new float[]{1, 1});
            secondRow.setWidth(UnitValue.createPercentValue(100));
            Table customerDetails = new Table(new float[]{1, 1});
            customerDetails.setWidth(UnitValue.createPercentValue(100));

            customerDetails.addCell("Customer Name");
            customerDetails.addCell(orderDetails.getCustomer().getName());
            customerDetails.addCell("Contact");
           // customerDetails.addCell(orderDetails.getCustomer().getPhoneNumber().toString());
            customerDetails.addCell("Address");
            customerDetails.addCell(orderDetails.getCustomer().getAddress());
            secondRow.addCell(customerDetails);
            Table shipment = new Table(new float[]{1, 1});
            shipment.setWidth(UnitValue.createPercentValue(100));
            shipment.addCell("Shipment Date");
            shipment.addCell(orderDetails.getOrderDate());
            shipment.addCell("Shipment Address");
            shipment.addCell(orderDetails.getCustomer().getAddress());
            secondRow.addCell(shipment);
            main.addCell(secondRow);
            Table table = new Table(new float[]{1, 2, 2, 2, 2, 1}); // 3 columns with relative widths
            table.setWidth(UnitValue.createPercentValue(100)); // Set width to 100% of the page width


            table.addHeaderCell("S.No.").setBold().setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell("NAME").setBold().setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell("HSN Code").setBold().setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell("Qty").setBold().setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell("Price").setBold().setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell("Amount").setBold().setTextAlignment(TextAlignment.CENTER);

            for (Product product : productList) {
                int x = 1;
                String s = "";
                String name = "";
                if (product.getCategory().equals(Category.Tyre.toString())) {
                    if (product.getSize().getAspectRatio() != "" && product.getSize().getAspectRatio() != null) {
                        s = "Size: " + product.getSize().getAspectRatio() +
                                "-" + product.getSize().getRim();
                    } else {
                        s = "Size: " + product.getSize().getWidth() +
                                "-" + product.getSize().getRim();
                    }
                    if (product.isIstubeless()) {
                        name = product.getName() + " (Tubeless)";
                    } else {
                        name = product.getName() + " (Tube Tyre)";
                    }
                } else {
                    s = "Size: " + product.getSize().getProductSize() +
                            "-" + product.getSize().getSizeUnit();
                    name = product.getName();
                }

                if (product.getRequestedQunatity() > 0) {
                    table.addCell(String.valueOf(x));
                    table.addCell(name + "-" + product.getBrand() + "\n" + s);
                    table.addCell(product.getHSNCode() + ""
                    );
                    table.addCell(String.valueOf(product.getRequestedQunatity()));
                    if (orderTypeSpinner.getSelectedItem().toString().equals("Retail")) {
                        table.addCell(String.valueOf(product.getSellingPrice()));
                        table.addCell(String.valueOf(product.getSellingPrice() * product.getRequestedQunatity() ));
                    } else {
                        table.addCell(String.valueOf(product.getWholesalePrice()));
                        table.addCell(String.valueOf(product.getWholesalePrice() * product.getRequestedQunatity()));
                    }
                }

                x = x + 1;
            }
            main.addCell(table);
            Table totalTable = new Table(new float[]{1, 1});
            totalTable.setWidth(UnitValue.createPercentValue(100));
            Table bankDetails = new Table(new float[]{1});
            bankDetails.setWidth(UnitValue.createPercentValue(100));
            bankDetails.addCell("Bank Details");
            Table  totalAmount = new Table(new float[]{1, 1});
            totalAmount.setWidth(UnitValue.createPercentValue(100));
            totalAmount.addCell("Total Amount");
            totalAmount.addCell("Rs"+total);
            totalAmount.addCell("CGST");
            totalAmount.addCell("14%");
            totalAmount.addCell("SGST");
            totalAmount.addCell("14%");
            if(discount.getText().toString().isEmpty()){
                totalAmount.addCell("Discount");
                totalAmount.addCell("Rs "+discount.getText().toString());
            }
            totalAmount.addCell("Total Amount");
            totalAmount.addCell("Rs"+(total - Float.parseFloat(discount.getText().toString())));
            totalTable.addCell(bankDetails);
            totalTable.addCell(totalAmount);
            secondRow.setStrokeWidth(0) ;
            totalTable.setStrokeWidth(0) ;


            main.addCell(totalTable);
            document.add(main);
            document.close();

            // Save the PDF file
            File pdfFile = new File(getExternalFilesDir(null), orderDetails.getOrderId()+".pdf");
            FileOutputStream fos = new FileOutputStream(pdfFile);
            byte[] pdfData = stream.toByteArray();
            fos.write(pdfData);
            fos.close();

            // Share the PDF using an Intent
            saveAndSharePDF(pdfFile);
        } catch (Exception e) {
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
            Intent intent = new Intent(ConfirmOrderActivity.this, HomeActivity.class);

            startActivity(intent);
        }
    }

    private void updateTotal() {
        total = 0f; // Initialize total here

        for (int j = 0; j < cardContainer.getChildCount(); j++) {
            CardView cardView = (CardView) cardContainer.getChildAt(j);
            GridLayout gridLayout = (GridLayout) cardView.getChildAt(0);
            Spinner quantitySpinner = (Spinner) gridLayout.getChildAt(1);
            TextView priceTextView = (TextView) gridLayout.getChildAt(2);
            TextView prodId = (TextView) gridLayout.getChildAt(3);

            for (Product product : productList) {
                if (product.getId().toString().equals(prodId.getText().toString())) {
                    if (orderTypeSpinner.getSelectedItem().toString().equals("Retail")) {
                        priceTextView.setText("Rs " + product.getSellingPrice());
                        total += (product.getSellingPrice() * Integer.parseInt((String) quantitySpinner.getSelectedItem()))-Float.parseFloat(discount.getText().toString());

                    } else {
                        priceTextView.setText("Rs " + product.getWholesalePrice());
                        total += (product.getWholesalePrice() * Integer.parseInt((String) quantitySpinner.getSelectedItem()))-Float.parseFloat(discount.getText().toString());
                    }
                    break;
                }
            }
        }

        totalTextView.setText("Rs. " + total); // Update the totalTextView here
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SAVE_PDF_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               // saveAndDownloadPDF();
            } else {
                Toast.makeText(this, "Permission denied. Cannot save PDF.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
