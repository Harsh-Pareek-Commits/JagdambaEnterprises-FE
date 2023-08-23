package com.example.jagdambaenterprises.domains;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockOrder {

    private Long id;
    private String orderDate;
    private Map<String, Integer> productQuantityMap = new HashMap<>();
    private User user;
    private String status="pending";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public Map<String, Integer> getProductQuantityMap() {
        return productQuantityMap;
    }

    public void setProductQuantityMap(List<Product> productList) {
        for (Product product : productList) {
            productQuantityMap.put(product.getId(), product.getRequestedQunatity());
        }
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
