package com.example.jagdambaenterprises.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jagdambaenterprises.R;
import com.example.jagdambaenterprises.domains.Product;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {
    private List<Product> productList;

    public StockAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock, parent, false);
        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set data to the views
        holder.textName.setText(product.getName());
        holder.textBrand.setText(product.getBrand());
        holder.textCostPrice.setText("Cost Price: " + product.getCostPrice());
        holder.textSellingPrice.setText("Selling Price: " + product.getSellingPrice());
        holder.textWholesalePrice.setText("Wholesale Price: " + product.getWholesalePrice());

        // Check if the Size object is not null before accessing its methods
        if (product.getSize() != null) {
            if(product.getSize().getAspectRatio()>0) {
                holder.textSize.setText("Size: " +  product.getSize().getAspectRatio() +
                        "-" + product.getSize().getRimDiameter() + "mm");
            }
            else{
                holder.textSize.setText("Size: " +  product.getSize().getWidth() +
                        "-" + product.getSize().getRimDiameter() );
            }
            holder.textStock.setText("Stock: " + product.getQuantity());

        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    public class StockViewHolder extends RecyclerView.ViewHolder {
        CardView cardView; // Add CardView reference
        TextView textName, textBrand, textSize, textCostPrice, textSellingPrice, textWholesalePrice, textStock; // Declare TextViews

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView); // Initialize CardView
            textName = itemView.findViewById(R.id.textName); // Initialize textName
            textBrand = itemView.findViewById(R.id.textBrand); // Initialize textBrand
            textCostPrice = itemView.findViewById(R.id.textCostPrice); // Initialize textCostPrice
            textSellingPrice = itemView.findViewById(R.id.textSellingPrice); // Initialize textSellingPrice
            textWholesalePrice = itemView.findViewById(R.id.textWholesalePrice); // Initialize textWholesalePrice
             textSize = itemView.findViewById(R.id.textSize); // Initialize textAspectRatio
           textStock=itemView.findViewById(R.id.textStock);
            // Initialize other views...
        }
    }
}
