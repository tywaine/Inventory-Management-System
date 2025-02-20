package com.inventorymanagementsystem.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

public class Supplier {
    public final int ID;
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty contactEmail = new SimpleStringProperty();
    private final StringProperty phoneNumber = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();
    private static final Map<Integer, Supplier> suppliers = new HashMap<>();
    private static final ObservableList<Supplier> supplierList = FXCollections.observableArrayList();

    public Supplier(int supplierId, String name, String contactEmail, String phoneNumber, String address) {
        ID = supplierId;
        this.id.set(supplierId);
        this.name.set(name);
        this.contactEmail.set(contactEmail);
        this.phoneNumber.set(phoneNumber);
        this.address.set(address);
        add(this);
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

    public String getFirstName(){
        return getName().split(" ")[0];
    }

    public String getLastName(){
        return getName().split(" ")[1];
    }

    public String getContactEmail() {
        return contactEmail.get();
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail.set(contactEmail);
    }

    public StringProperty contactEmailProperty() {
        return contactEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public StringProperty addressProperty() {
        return address;
    }

    public static boolean isValidName(String name) {
        return name.matches("^[A-Za-z]+$");
    }

    public static boolean isValidEmail(String email) {
        return User.isValidEmail(email);
    }

    public static boolean isValidPhoneNumber1(String phoneNumber) {
        // Check if the phone number consists only of numbers and its length is between 7 and 11 digits
        return phoneNumber.matches("\\d{7,11}");
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        // Check if the phone number is in the format ###-####
        return phoneNumber != null && phoneNumber.matches("\\d{3}-\\d{4}");
    }

    public static void add(Supplier supplier) {
        if(supplier != null && !contains(supplier.ID)){
            suppliers.put(supplier.ID, supplier);
            supplierList.add(supplier);
        }
        else if(supplier == null){
            System.out.println("Supplier is null. Was not added to Map and List");
        }
        else{
            System.out.println("Supplier is already present!");
        }
    }

    public static void update(Supplier supplier, String name, String contactEmail, String phoneNumber, String address){
        if(supplier != null){
            supplier.setName(name);
            supplier.setContactEmail(contactEmail);
            supplier.setPhoneNumber(phoneNumber);
            supplier.setAddress(address);
        }
        else{
            System.out.println("Supplier value is null");
        }
    }

    public static void remove(Supplier supplier) {
        if(valid(supplier)){
            supplierList.remove(supplier);
            suppliers.remove(supplier.ID);
        }
        else{
            System.out.println("Supplier ID not found");
        }
    }

    public static Supplier get(int supplierID) {
        if(contains(supplierID)){
            return suppliers.get(supplierID);
        }
        else{
            System.out.println("Supplier ID not found. Null was returned");
            return null;
        }
    }

    public static boolean valid(Supplier supplier){
        return supplier != null && contains(supplier.ID);
    }

    public static int getSupplierCount() {
        return suppliers.size();
    }

    public static boolean contains(int supplierID) {
        return suppliers.containsKey(supplierID);
    }

    public static ObservableList<Supplier> getList(){
        return supplierList;
    }

    public static void empty() {
        suppliers.clear();
        supplierList.clear();
    }
}