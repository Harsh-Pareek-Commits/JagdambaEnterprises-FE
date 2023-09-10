package com.example.jagdambaenterprises.adapters;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jagdambaenterprises.R;
import com.example.jagdambaenterprises.constants.Category;
import com.example.jagdambaenterprises.domains.Order;
import com.example.jagdambaenterprises.domains.Product;
import com.example.jagdambaenterprises.domains.StockOrder;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OrderStockAdapter extends RecyclerView.Adapter<OrderStockAdapter.ViewHolder> {
    private String apiBaseUrl;
    private List<Product> productList;
    private OnCardClickListener cardClickListener;
    private SparseBooleanArray selectedItems;
private LinearLayout quantityLayout;
    public interface OnCardClickListener {
        void onCardClick(int position);
    }

    public OrderStockAdapter(List<Product> productList) {
        this.productList = productList;
        selectedItems = new SparseBooleanArray();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock_order, parent, false);
        return new ViewHolder(view);
    }
    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        // Set data to the views
        holder.textName.setText(product.getName());
        holder.textBrand.setText(product.getBrand());

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
        if (product.getQuantity() <= 5) {
            holder.textStock.setTextColor(Color.parseColor("#cc0000"));
            holder.itemView.setBackgroundColor(Color.parseColor("#ffcccc"));
        }
        if (product.getQuantity() > 5) {
            holder.textStock.setTextColor(Color.parseColor("#000000"));
            holder.itemView.setBackgroundColor(Color.parseColor("#e6fff3"));
        }
        if (holder.checkBox.isChecked()) {
            product.setSelected(true);

            holder.itemView.setBackgroundColor(
                    Color.parseColor("#9fbfdf")

            );

        } else {
            product.setSelected(false);
            if (product.getQuantity() <= 10) {
                holder.itemView.setBackgroundColor(Color.parseColor("#ffcccc"));
            }
            if (product.getQuantity() > 10) {
                holder.itemView.setBackgroundColor(Color.parseColor("#e6fff3"));
            }

        }

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private CardView cardView;
        private View itemView;
        TextView textName, textBrand, textSize, textStock; // Declare TextViews

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

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleSelection(getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleSelection(getAdapterPosition());
                }
            });
        }
    }
    public OrderStockAdapter(Context context) {
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
    public void addOrderAPI(Order order, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        Gson gson = new Gson();
        String jsonProduct = gson.toJson(order);
        Log.w("Payload", jsonProduct);
        RequestBody body = RequestBody.create(mediaType,jsonProduct);
        Request request = new Request.Builder()
                .url(apiBaseUrl+"/order/create")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

}
