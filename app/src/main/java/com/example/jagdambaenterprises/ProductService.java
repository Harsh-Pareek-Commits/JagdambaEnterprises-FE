package com.example.jagdambaenterprises;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import com.example.jagdambaenterprises.domains.Product;

import java.util.List;

public interface ProductService {
    @GET("product/view")
    Call<List<Product>> getAllProducts(@Header("Cookie") String cookie);
}
