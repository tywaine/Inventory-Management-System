package com.inventorymanagementsystem.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PurchaseOrder {
    public final int ID;
    public final IntegerProperty id = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> orderDate = new SimpleObjectProperty<>();
    private final IntegerProperty supplierId = new SimpleIntegerProperty();
    private final StringProperty supplierName = new SimpleStringProperty();
    private final StringProperty productName = new SimpleStringProperty();
    private final IntegerProperty quantity = new SimpleIntegerProperty();
    private final ObjectProperty<BigDecimal> totalAmount = new SimpleObjectProperty<>();

    private static final Map<Integer, PurchaseOrder> purchaseOrders = new HashMap<>();
    private static final ObservableList<PurchaseOrder> purchaseOrderList = FXCollections.observableArrayList();

    public PurchaseOrder(int orderId, LocalDate orderDate, int supplierId,
                         String supplierName, String productName, int quantity, BigDecimal totalAmount) {
        ID = orderId;
        this.id.set(orderId);
        this.orderDate.set(orderDate);
        this.supplierId.set(supplierId);
        this.supplierName.set(supplierName);
        this.productName.set(productName);
        this.quantity.set(quantity);
        this.totalAmount.set(totalAmount);
        add(this);
    }

    public IntegerProperty idProperty(){
        return id;
    }

    // Getters and setters
    public LocalDate getOrderDate() {
        return orderDate.get();
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate.set(orderDate);
    }

    public ObjectProperty<LocalDate> orderDateProperty() {
        return orderDate;
    }

    public int getSupplierId() {
        return supplierId.get();
    }

    public void setSupplierId(int supplierId) {
        this.supplierId.set(supplierId);
    }

    public IntegerProperty supplierIdProperty() {
        return supplierId;
    }

    public String getSupplierName(){
        return supplierName.get();
    }

    public void setSupplierName(String supplierName){
        this.supplierName.set(supplierName);
    }

    public StringProperty supplierNameProperty(){
        return supplierName;
    }

    public String getProductName(){
        return productName.get();
    }

    public void setProductName(String productName){
        this.productName.set(productName);
    }

    public StringProperty productNameProperty(){
        return productName;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount.get();
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount.set(totalAmount);
    }

    public ObjectProperty<BigDecimal> totalAmountProperty() {
        return totalAmount;
    }

    public static boolean isValidUnitPrice(String number){
        try{
            float num = Float.parseFloat(number);
            return num >= 0;
        } catch(NumberFormatException _){
            return false;
        }
    }

    public static void add(PurchaseOrder purchaseOrder) {
        if(purchaseOrder != null && !contains(purchaseOrder.ID)){
            purchaseOrders.put(purchaseOrder.ID, purchaseOrder);
            purchaseOrderList.add(purchaseOrder);
        }
        else if(purchaseOrder == null){
            System.out.println("Purchase Order is null. Was not added to Map and List");
        }
        else{
            System.out.println("Purchase Order is already present!");
        }
    }

    public static void update(PurchaseOrder purchaseOrder, LocalDate orderDate, int supplierId,
                              String supplierName, String productName, int quantity, BigDecimal totalAmount){
        if(purchaseOrder != null){
            purchaseOrder.setOrderDate(orderDate);
            purchaseOrder.setSupplierId(supplierId);
            purchaseOrder.setSupplierName(supplierName);
            purchaseOrder.setProductName(productName);
            purchaseOrder.setQuantity(quantity);
            purchaseOrder.setTotalAmount(totalAmount);
        }
        else{
            System.out.println("Purchase Order value is null");
        }
    }

    public static void remove(PurchaseOrder purchaseOrder) {
        if(valid(purchaseOrder)){
            purchaseOrderList.remove(purchaseOrder);
            purchaseOrders.remove(purchaseOrder.ID);
        }
        else{
            System.out.println("Purchase Order ID not found");
        }
    }

    public static PurchaseOrder get(int purchaseOrderID) {
        if(contains(purchaseOrderID)){
            return purchaseOrders.get(purchaseOrderID);
        }
        else{
            System.out.println("Purchase Order ID not found. Null was returned");
            return null;
        }
    }

    public static boolean valid(PurchaseOrder purchaseOrder){
        return purchaseOrder != null && contains(purchaseOrder.ID);
    }

    public static int getPurchaseOrderCount() {
        return purchaseOrders.size();
    }

    public static boolean contains(int purchaseOrderID) {
        return purchaseOrders.containsKey(purchaseOrderID);
    }

    public static ObservableList<PurchaseOrder> getList(){
        return purchaseOrderList;
    }

    public static void empty() {
        purchaseOrders.clear();
        purchaseOrderList.clear();
    }
}

