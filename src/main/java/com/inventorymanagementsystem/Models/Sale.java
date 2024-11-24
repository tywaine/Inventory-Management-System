package com.inventorymanagementsystem.Models;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Sale {
    public final int ID;
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty productId = new SimpleIntegerProperty();
    private final StringProperty productName = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> saleDate = new SimpleObjectProperty<>();
    private final IntegerProperty quantitySold = new SimpleIntegerProperty();
    private final ObjectProperty<BigDecimal> salePrice = new SimpleObjectProperty<>();
    private static final Map<Integer, Sale> sales = new HashMap<>();
    private static final ObservableList<Sale> saleList = FXCollections.observableArrayList();

    public Sale(int saleId, int productId, String productName, LocalDate saleDate, int quantitySold, BigDecimal salePrice) {
        ID = saleId;
        this.id.set(saleId);
        this.productId.set(productId);
        this.productName.set(productName);
        this.saleDate.set(saleDate);
        this.quantitySold.set(quantitySold);
        this.salePrice.set(salePrice);
        add(this);
    }

    // Getters and setters
    public IntegerProperty idProperty() {
        return id;
    }

    public int getProductId() {
        return productId.get();
    }

    public void setProductId(int productId) {
        this.productId.set(productId);
    }

    public IntegerProperty productIdProperty() {
        return productId;
    }

    public String getProductName() {
        return productName.get();
    }

    public void setProductName(String productName) {
        this.productName.set(productName);
    }

    public StringProperty productNameProperty() {
        return productName;
    }

    public LocalDate getSaleDate() {
        return saleDate.get();
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate.set(saleDate);
    }

    public ObjectProperty<LocalDate> saleDateProperty() {
        return saleDate;
    }

    public int getQuantitySold() {
        return quantitySold.get();
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold.set(quantitySold);
    }

    public IntegerProperty quantitySoldProperty() {
        return quantitySold;
    }

    public BigDecimal getSalePrice() {
        return salePrice.get();
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice.set(salePrice);
    }

    public ObjectProperty<BigDecimal> salePriceProperty() {
        return salePrice;
    }

    public static void add(Sale sale) {
        if(sale != null && !contains(sale.ID)){
            sales.put(sale.ID, sale);
            saleList.add(sale);
        }
        else if(sale == null){
            System.out.println("Sale is null. Was not added to Map and List");
        }
        else{
            System.out.println("Sale is already present!");
        }
    }

    public static void update(Sale sale, int productId, String productName, LocalDate saleDate, int quantitySold, BigDecimal salePrice){
        if(sale != null){
            sale.setProductId(productId);
            sale.setProductName(productName);
            sale.setSaleDate(saleDate);
            sale.setQuantitySold(quantitySold);
            sale.setSalePrice(salePrice);
        }
        else{
            System.out.println("Sale value is null");
        }
    }

    public static void remove(Sale sale) {
        if(valid(sale)){
            saleList.remove(sale);
            sales.remove(sale.ID);
        }
        else{
            System.out.println("Sale ID not found");
        }
    }

    public static Sale get(int saleID) {
        if(contains(saleID)){
            return sales.get(saleID);
        }
        else{
            System.out.println("Sale ID not found. Null was returned");
            return null;
        }
    }

    public static boolean valid(Sale sale){
        return sale != null && contains(sale.ID);
    }

    public static int getSaleCount() {
        return sales.size();
    }

    public static boolean contains(int saleID) {
        return sales.containsKey(saleID);
    }

    public static ObservableList<Sale> getList(){
        return saleList;
    }

    public static void empty() {
        sales.clear();
        saleList.clear();
    }
}
