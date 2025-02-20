package com.inventorymanagementsystem.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Batch {
    public final int ID;
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty productId = new SimpleIntegerProperty();
    private final IntegerProperty currentStock = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> expirationDate = new SimpleObjectProperty<>();
    private static final Map<Integer, Batch> batches = new HashMap<>();
    private static final ObservableList<Batch> batchList = FXCollections.observableArrayList();

    public Batch(int batchId, int productId, int currentStock, LocalDate expirationDate) {
        ID = batchId;
        this.id.set(batchId);
        this.productId.set(productId);
        this.currentStock.set(currentStock);
        this.expirationDate.set(expirationDate);
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

    public int getCurrentStock() {
        return currentStock.get();
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock.set(currentStock);
    }

    public IntegerProperty currentStockProperty() {
        return currentStock;
    }

    public LocalDate getExpirationDate() {
        return expirationDate.get();
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate.set(expirationDate);
    }

    public ObjectProperty<LocalDate> expirationDateProperty() {
        return expirationDate;
    }

    public static void add(Batch batch) {
        if(batch != null && !contains(batch.ID)){
            batches.put(batch.ID, batch);
            batchList.add(batch);
        }
        else if(batch == null){
            System.out.println("Batch is null. Was not added to Map and List");
        }
        else{
            System.out.println("Batch is already present!");
        }
    }

    public static void update(Batch batch, int productId, int currentStock, LocalDate expirationDate){
        if(batch != null){
            batch.setProductId(productId);
            batch.setCurrentStock(currentStock);
            batch.setExpirationDate(expirationDate);
        }
        else{
            System.out.println("Batch value is null");
        }
    }

    public static void remove(Batch batch) {
        if(valid(batch)){
            batchList.remove(batch);
            batches.remove(batch.ID);
        }
        else{
            System.out.println("Batch ID not found");
        }
    }

    public static Batch get(int batchID) {
        if(contains(batchID)){
            return batches.get(batchID);
        }
        else{
            System.out.println("Batch ID not found. Null was returned");
            return null;
        }
    }

    public static int getLastAddedBatchID() {
        if (!batchList.isEmpty()) {
            return batchList.getLast().ID;
        }
        else {
            System.out.println("Batch list is empty.");
            return -1;
        }
    }

    public static boolean valid(Batch batch){
        return batch != null && contains(batch.ID);
    }

    public static boolean contains(int batchID) {
        return batches.containsKey(batchID);
    }

    public static ObservableList<Batch> getList(){
        return batchList;
    }

    public static void empty() {
        batches.clear();
        batchList.clear();
    }
}

