package com.inventorymanagementsystem.Models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class InventoryAdjustment {
    public final int ID;
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty user_id = new SimpleIntegerProperty();
    private final StringProperty user_role = new SimpleStringProperty();
    private final IntegerProperty productId = new SimpleIntegerProperty();
    private final StringProperty productName = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> adjustmentDatetime = new SimpleObjectProperty<>();
    private final StringProperty adjustmentDatetimeFormatted = new SimpleStringProperty();
    private final IntegerProperty batchId = new SimpleIntegerProperty();
    private final StringProperty adjustmentType = new SimpleStringProperty();
    private final IntegerProperty previous_stock = new SimpleIntegerProperty();
    private final IntegerProperty adjusted_stock = new SimpleIntegerProperty();
    private static final Map<Integer, InventoryAdjustment> inventoryAdjustments = new HashMap<>();
    private static final ObservableList<InventoryAdjustment> inventoryAdjustmentList = FXCollections.observableArrayList();

    public InventoryAdjustment(int adjustmentId, int user_id, String user_role, int product_id, String productName, int batchId,
                               LocalDateTime adjustmentDatetime, String adjustmentType, int previous_stock, int adjusted_stock) {
        ID = adjustmentId;
        this.id.set(adjustmentId);
        this.user_id.set(user_id);
        this.user_role.set(user_role);
        this.productId.set(product_id);
        this.productName.set(productName);
        this.batchId.set(batchId);
        this.adjustmentDatetime.set(adjustmentDatetime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.adjustmentDatetimeFormatted.set(adjustmentDatetime.format(formatter));
        this.adjustmentType.set(adjustmentType);
        this.previous_stock.set(previous_stock);
        this.adjusted_stock.set(adjusted_stock);
        add(this);
    }

    // Getters and setters
    public IntegerProperty idProperty() {
        return id;
    }

    public int getBatchId() {
        return batchId.get();
    }

    public StringProperty batchIdStringProperty(){
        StringProperty batch_id = new SimpleStringProperty();

        if(getBatchId() == -1){
            batch_id.set("Not Used");
        }
        else{
            batch_id.set(String.valueOf(getBatchId()));
        }

        return batch_id;
    }

    public String getBatchIdString(){
        if(getBatchId() == -1){
            return "Not Used";
        }
        else{
            return String.valueOf(getBatchId());
        }
    }

    public IntegerProperty userIdProperty(){
        return user_id;
    }

    public int getUserId(){
        return user_id.get();
    }

    public StringProperty userRoleProperty(){
        return user_role;
    }

    public String getUserRole(){
        return user_role.get();
    }

    public IntegerProperty batchIdProperty() {
        return batchId;
    }

    public int getProductId(){
        return productId.get();
    }

    public IntegerProperty productIdProperty(){
        return productId;
    }

    public String getProductName(){
        return productName.get();
    }

    public StringProperty productNameProperty(){
        return productName;
    }

    public LocalDateTime getAdjustmentDatetime() {
        return adjustmentDatetime.get();
    }

    public ObjectProperty<LocalDateTime> adjustmentDatetimeProperty() {
        return adjustmentDatetime;
    }

    public String getAdjustmentDatetimeFormatted() {
        return adjustmentDatetimeFormatted.get();
    }

    public StringProperty adjustmentDatetimeFormattedProperty(){
        return adjustmentDatetimeFormatted;
    }

    public String getAdjustmentType() {
        return adjustmentType.get();
    }

    public StringProperty adjustmentTypeProperty() {
        return adjustmentType;
    }

    public int getPrevious_stock() {
        return previous_stock.get();
    }

    public IntegerProperty previous_stockProperty() {
        return previous_stock;
    }

    public StringProperty previous_stockStringProperty(){
        StringProperty previousStock = new SimpleStringProperty();

        if(getPrevious_stock() == -1){
            previousStock.set("0");
        }
        else{
            previousStock.set(String.valueOf(getPrevious_stock()));
        }

        return previousStock;
    }

    public String getPrevious_stockString(){
        if(getPrevious_stock() == -1){
            return "0";
        }
        else{
            return String.valueOf(getPrevious_stock());
        }
    }

    public int getAdjusted_stock() {
        return adjusted_stock.get();
    }

    public IntegerProperty adjusted_stockProperty() {
        return adjusted_stock;
    }

    public StringProperty adjusted_stockStringProperty(){
        StringProperty adjustedStock = new SimpleStringProperty();

        if(getAdjusted_stock() == -1){
            adjustedStock.set("0");
        }
        else{
            adjustedStock.set(String.valueOf(getAdjusted_stock()));
        }

        return adjustedStock;
    }

    public String getAdjusted_stockString(){
        if(getAdjusted_stock() == -1){
            return "0";
        }
        else{
            return String.valueOf(getAdjusted_stock());
        }
    }

    public static void add(InventoryAdjustment inventoryAdjustment) {
        if(inventoryAdjustment != null && !contains(inventoryAdjustment.ID)){
            inventoryAdjustments.put(inventoryAdjustment.ID, inventoryAdjustment);
            inventoryAdjustmentList.add(inventoryAdjustment);
        }
        else if(inventoryAdjustment == null){
            System.out.println("Inventory Adjustment is null. Was not added to Map and List");
        }
        else{
            System.out.println("Inventory Adjustment is already present!");
        }
    }

    public static void remove(InventoryAdjustment inventoryAdjustment) {
        if(valid(inventoryAdjustment)){
            inventoryAdjustmentList.remove(inventoryAdjustment);
            inventoryAdjustments.remove(inventoryAdjustment.ID);
        }
        else{
            System.out.println("Inventory Adjustment ID not found");
        }
    }

    public static InventoryAdjustment getInventoryAdjustment(int inventoryAdjustmentID) {
        if(contains(inventoryAdjustmentID)){
            return inventoryAdjustments.get(inventoryAdjustmentID);
        }
        else{
            System.out.println("Inventory Adjustment ID not found. Null was returned");
            return null;
        }
    }

    public static boolean valid(InventoryAdjustment inventoryAdjustment){
        return inventoryAdjustment != null && contains(inventoryAdjustment.ID);
    }

    public static int getInventoryAdjustmentCount() {
        return inventoryAdjustments.size();
    }

    public static boolean contains(int inventoryAdjustmentID) {
        return inventoryAdjustments.containsKey(inventoryAdjustmentID);
    }

    public static ObservableList<InventoryAdjustment> getList(){
        return inventoryAdjustmentList;
    }

    public static void empty() {
        inventoryAdjustments.clear();
        inventoryAdjustmentList.clear();
    }
}

