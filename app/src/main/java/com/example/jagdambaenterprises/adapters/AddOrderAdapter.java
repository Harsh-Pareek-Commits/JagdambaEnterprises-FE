package com.example.jagdambaenterprises.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jagdambaenterprises.R;
import com.example.jagdambaenterprises.constants.Category;
import com.example.jagdambaenterprises.domains.Product;
import com.example.jagdambaenterprises.domains.StockOrder;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AddOrderAdapter extends RecyclerView.Adapter<AddOrderAdapter.ViewHolder> {
    private String apiBaseUrl;
    private List<Product> productList;
    private OnCardClickListener cardClickListener;
    private SparseBooleanArray selectedItems;
    private TableLayout productQuantityTablePreview;
    private View parentView; // Add this field

private CardView productDetails;
    public interface OnCardClickListener {
        void onCardClick(int position);
    }

    public AddOrderAdapter(List<Product> productList) {
        this.productList = productList;
        selectedItems = new SparseBooleanArray();
    }
    public AddOrderAdapter(List<Product> productList, View parentView) {
        this.productList = productList;
        selectedItems = new SparseBooleanArray();
        this.parentView = parentView; // Initialize the parent view
    }
    public void setOnCardClickListener(OnCardClickListener listener) {
        cardClickListener = listener;
    }

    public void toggleSelection(int position) {
        selectedItems.put(position, !selectedItems.get(position, false));
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_add_order_item, parent, false);
        return new ViewHolder(view);
    }
    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.textName.setText(product.getName());
        holder.textBrand.setText(product.getBrand());
        holder.textSellingPrice.setText("Selling Price: " + product.getSellingPrice());
        // Set data to the views
        holder.textName.setText(product.getName());
        holder.textBrand.setText(product.getBrand());
        List<String> quantityOptions = new ArrayList<>();
        quantityOptions.add("Select Quantity");
        for (int i = 1; i <= product.getQuantity(); i++) {
            quantityOptions.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                holder.itemView.getContext(),
                android.R.layout.simple_spinner_item,
                quantityOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.quantitySpinner.setAdapter(adapter);


        // Check if the Size object is not null before accessing its methods
        if (product.getCategory().equals(Category.Tyre.toString())) {
            if (product.getSize().getAspectRatio() != "" && product.getSize().getAspectRatio() != null) {
                holder.textSize.setText("Size: " + product.getSize().getAspectRatio() +
                        "-" + product.getSize().getRim());
            } else {
                holder.textSize.setText("Size: " + product.getSize().getWidth() +
                        "-" + product.getSize().getRim());
            }
        } else {
            holder.textSize.setText("Size: " + product.getSize().getProductSize() +
                    "-" + product.getSize().getSizeUnit());
        }

        holder.checkBox.setChecked(selectedItems.get(position));
        holder.textStock.setText("Stock: " + product.getQuantity());

        if (holder.checkBox.isChecked()) {

            holder.cardView.setBackgroundColor(
                    Color.parseColor("#9fbfdf")

            );

        } else {
            product.setSelected(false);
            if (product.getQuantity() <= 5) {
                holder.itemView.setBackgroundColor(Color.parseColor("#ffcccc"));
            }
            if (product.getQuantity() > 5) {
                holder.itemView.setBackgroundColor(Color.parseColor("#e6fff3"));
            }

        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle visibility of the quantity layout
                boolean isExpanded = selectedItems.get(holder.getAdapterPosition(), false);
                selectedItems.put(holder.getAdapterPosition(), !isExpanded);
                if (holder.removeLayout.getVisibility() == View.GONE && holder.quantityLayout.getVisibility() == View.GONE) {
                    holder.quantityLayout.setVisibility(View.VISIBLE);
                } else {
                    holder.quantityLayout.setVisibility(View.GONE);
                }
            }
        });

        // Set the click listener for the "Add" button
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product.setRequestedQunatity(0);
                product.setSelected(false);
                holder.quantitySpinner.setSelection(0);
                holder.quantityLayout.setVisibility(View.GONE);
                holder.removeLayout.setVisibility(View.GONE);
                productQuantityTablePreview.removeAllViews();

                for (Product prod: productList) {
                    if (prod.isSelected()) {
                        TableRow newRow = new TableRow(holder.itemView.getContext());

                        // Create TextViews for product name and selected quantity
                        TextView productNameView = new TextView(holder.itemView.getContext());
                        TextView quantityView = new TextView(holder.itemView.getContext());

                        productNameView.setText(prod.getName());
                        String selectedQuantity = prod.getRequestedQunatity() + "";

                        // Configure the TextViews with appropriate data

                        // Add TextViews to the row
                        newRow.addView(productNameView);
                        newRow.addView(quantityView);

                        // Add the row to the table
                        productQuantityTablePreview.addView(newRow);
                        quantityView.setText(selectedQuantity);
                    }
                }
              if(productQuantityTablePreview.getChildCount()>0){
                    productDetails.setVisibility(View.VISIBLE);
              }else{
                    productDetails.setVisibility(View.GONE);
              }
            }
        });

        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Add" button click here
                // You can get the quantity from the EditText and perform actions
                String quantityString = (String) holder.quantitySpinner.getSelectedItem();
                if (!TextUtils.isEmpty(quantityString)) {
                    int quantity = Integer.parseInt(quantityString);
                    // Perform your desired actions with the selected product and quantity
                    // For example, add the updateProductQuantityPreview and quantity to a list or database
                    updateProductQuantityPreview();
                }
            }
            private void updateProductQuantityPreview() {
                // Clear previous rows if any
                product.setSelected(true);
                product.setRequestedQunatity( Integer.parseInt((String) holder.quantitySpinner.getSelectedItem()));

                productDetails=parentView.findViewById(R.id.pdCardView);
                productDetails.setVisibility(View.VISIBLE);
                productQuantityTablePreview = parentView.findViewById(R.id.productQuantityTablePreview);
                productQuantityTablePreview.removeAllViews(); // Clear existing rows

                // Check if the item is selected
                if (selectedItems.get(holder.getAdapterPosition())) {
                    // Create a new table row
                    for (Product prod: productList) {
                        if (prod.isSelected()) {
                    TableRow newRow = new TableRow(holder.itemView.getContext());

                    // Create TextViews for product name and selected quantity
                    TextView productNameView = new TextView(holder.itemView.getContext());
                    TextView quantityView = new TextView(holder.itemView.getContext());

                             productNameView.setText(prod.getName());
                             String selectedQuantity = prod.getRequestedQunatity()+"";

                    // Configure the TextViews with appropriate data


                    // Add TextViews to the row
                    newRow.addView(productNameView);
                    newRow.addView(quantityView);

                    // Add the row to the table
                    productQuantityTablePreview.addView(newRow);
                             quantityView.setText(selectedQuantity);
                         }
                     }

                    // Show the table preview
                    productQuantityTablePreview.setVisibility(View.VISIBLE);
                } else {
                    // Hide the table preview
                    productQuantityTablePreview.setVisibility(View.GONE);
                }
                holder.quantityLayout.setVisibility(View.GONE);
                holder.removeLayout.setVisibility(View.VISIBLE);
            }
        });


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private CardView cardView;
        private View itemView;


        private Spinner quantitySpinner;
        private Button addButton,removeButton;


        TextView textName, textBrand, textSize, textStock ,textSellingPrice; // Declare TextViews
private LinearLayout quantityLayout,removeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;  // Store the whole card view

            checkBox = itemView.findViewById(R.id.checkBox);
            cardView = itemView.findViewById(R.id.cardView);
            cardView = itemView.findViewById(R.id.cardView); // Initialize CardView
            textName = itemView.findViewById(R.id.textName); // Initialize textName
            textBrand = itemView.findViewById(R.id.textBrand); // Initialize textWholesalePrice
            textSize = itemView.findViewById(R.id.textSize);
            textStock=itemView.findViewById(R.id.textStock);
            textSellingPrice=itemView.findViewById(R.id.textSellingPrice);
            cardView = itemView.findViewById(R.id.cardView); // Initialize CardView
            textName = itemView.findViewById(R.id.textName); // Initialize textName
            textBrand = itemView.findViewById(R.id.textBrand); // Initialize textBrand
            textSize = itemView.findViewById(R.id.textSize); // Initialize textAspectRatio
            textStock=itemView.findViewById(R.id.textStock);
            quantitySpinner = itemView.findViewById(R.id.quantitySpinner);
            quantityLayout = itemView.findViewById(R.id.quantityLayout);  // Initialize quantityLayout
            addButton = itemView.findViewById(R.id.addButton);
            removeLayout=   itemView.findViewById(R.id.RemoveLayout);
            removeButton=itemView.findViewById(R.id.Remove);
            boolean isExpanded = selectedItems.get(getAdapterPosition(), false);
            productQuantityTablePreview = itemView.findViewById(R.id.productQuantityTablePreview);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleSelection(getAdapterPosition());
                   // updateProductQuantityPreview();

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleSelection(getAdapterPosition());
                //    updateProductQuantityPreview();

                }
            });

        }

    }
    public AddOrderAdapter(Context context) {
        apiBaseUrl = context.getString(R.string.api_url);
    }
    public void addProductToApi(StockOrder stockOrder, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        Gson gson = new Gson();
        String jsonProduct = gson.toJson(stockOrder);
        Log.w("Payload", jsonProduct);
        RequestBody body = RequestBody.create(mediaType,jsonProduct);
        Request request = new Request.Builder()
                .url(apiBaseUrl+"/order/create/stock-order")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

}
