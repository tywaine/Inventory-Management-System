package com.inventorymanagementsystem.Models;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Product {
    public final int ID;
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> unitPrice = new SimpleObjectProperty<>();
    private final IntegerProperty stockCount = new SimpleIntegerProperty();
    private final IntegerProperty lowStockAmount = new SimpleIntegerProperty();
    private static final Map<Integer, Product> products = new HashMap<>();
    private static final ObservableList<Product> productList = FXCollections.observableArrayList();
    private static final ObservableList<String> productNameList = FXCollections.observableArrayList();
    private static final ObservableList<String> categoryList = FXCollections.observableArrayList();

    public Product(int productId, String name, String category, BigDecimal unitPrice, int lowStockAmount) {
        ID = productId;
        this.id.set(productId);
        this.name.set(name);
        this.category.set(category);
        this.unitPrice.set(unitPrice);
        this.lowStockAmount.set(lowStockAmount);
        add(this);
        stockCount.set(calculateStockCount());
    }

    // Getters and setters
    public IntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice.get();
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice.set(unitPrice);
    }

    public ObjectProperty<BigDecimal> unitPriceProperty() {
        return unitPrice;
    }

    public int getLowStockAmount(){
        return lowStockAmount.get();
    }

    public void setLowStockAmount(int lowStockAmount){
        this.lowStockAmount.set(lowStockAmount);
    }

    public IntegerProperty lowStockAmountProperty(){
        return lowStockAmount;
    }

    public int getStockCount(){
        return stockCount.get();
    }

    public IntegerProperty stockCountProperty(){
        return stockCount;
    }

    public void setStockCount(int stockCount){
        this.stockCount.set(stockCount);
    }

    public int calculateStockCount(){
        int count = 0;

        for(Batch batch: Batch.getList()){
            if(ID == batch.getProductId()){
                count += batch.getCurrentStock();
            }
        }

        setStockCount(count);
        return count;
    }

    public ObservableList<Batch> getBatchList(){
        ObservableList<Batch> batchList = FXCollections.observableArrayList();

        for(Batch batch: Batch.getList()){
            if(ID == batch.getProductId()){
                batchList.add(batch);
            }
        }

        return batchList;
    }

    public static boolean isValidUnitPrice(String number){
        try{
            float num = Float.parseFloat(number);
            return num >= 0;
        } catch(NumberFormatException _){
            return false;
        }
    }

    public static void add(Product product) {
        if(product != null && !contains(product.ID)){
            products.put(product.ID, product);
            productList.add(product);
            productNameList.add(product.getName());

            for (String s : categoryList) {
                if (s.equalsIgnoreCase(product.getCategory())) {
                    return;
                }
            }

            categoryList.add(product.getCategory());
        }
        else if(product == null){
            System.out.println("Product is null. Was not added to Map and List");
        }
        else{
            System.out.println("Product is already present!");
        }
    }

    public static void update(Product product, String name, String category, BigDecimal unitPrice, int lowStockAmount){
        if(product != null){
            product.setName(name);
            product.setCategory(category);
            product.setUnitPrice(unitPrice);
            product.setLowStockAmount(lowStockAmount);

            productNameList.clear();

            for(Product p: productList){
                productNameList.add(p.getName());
            }
        }
        else{
            System.out.println("Product value is null");
        }
    }

    public static void remove(Product product) {
        if(valid(product)){
            productList.remove(product);
            products.remove(product.ID);
            productNameList.remove(product.getName());

        }
        else{
            System.out.println("Product ID not found");
        }
    }

    public static Product get(int productID) {
        if(contains(productID)){
            return products.get(productID);
        }
        else{
            System.out.println("Product ID not found. Null was returned");
            return null;
        }
    }

    public static int getLastAddedProductID() {
        if (!productList.isEmpty()) {
            return productList.getLast().ID;
        }
        else {
            System.out.println("Product list is empty.");
            return -1;
        }
    }

    public static boolean valid(Product product){
        return product != null && contains(product.ID);
    }

    public static boolean contains(int productID) {
        return products.containsKey(productID);
    }

    public static boolean containsName(String productName){
        for(String name: productNameList){
            if(productName.equalsIgnoreCase(name)){
                return true;
            }
        }

        return false;
    }

    public static ObservableList<Product> getList(){
        return productList;
    }

    public static ObservableList<String> getNameList(){
        return productNameList;
    }

    public static ObservableList<Product> getLowStockProducts(){
        ObservableList<Product> lowStockProducts = FXCollections.observableArrayList();

        for(Product product: productList){
            if(product.getStockCount() <= product.getLowStockAmount()){
                lowStockProducts.add(product);
            }
        }

        return lowStockProducts;
    }

    public static ObservableList<String> getCategoryList(){
        return categoryList;
    }

    public static void empty() {
        products.clear();
        productList.clear();
        productNameList.clear();
        categoryList.clear();
    }
}

