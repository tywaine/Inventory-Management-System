package com.inventorymanagementsystem.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class User {
    public final int ID;
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty role = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final StringProperty createdAtFormatted = new SimpleStringProperty();
    private static final Map<Integer, User> staffs = new HashMap<>();
    private static final ObservableList<User> staffList = FXCollections.observableArrayList();

    public User(int userId, String name, String role, String email, LocalDateTime createdAt) {
        ID = userId;
        this.id.set(userId);
        this.name.set(name);
        this.role.set(role);
        this.email.set(email);
        this.createdAt.set(createdAt);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.createdAtFormatted.set(createdAt.format(formatter));

        addStaff(this);
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

    public String getRole() {
        return role.get();
    }

    public StringProperty roleProperty() {
        return role;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    public String getCreatedAtFormatted() {
        return createdAtFormatted.get();
    }

    public StringProperty createdAtFormattedProperty(){
        return createdAtFormatted;
    }

    public boolean isStaff(){
        return getRole().equals("STAFF");
    }

    public boolean isAdmin(){
        return getRole().equals("ADMIN");
    }

    public static boolean isValidName(String name) {
        String[] names = name.trim().split(" ");

        if(names.length == 1){
            return names[0].matches("^[A-Za-z]+$");
        }

        if(names.length == 2){
            return names[0].matches("^[A-Za-z]+$") &&
                    names[1].matches("^[A-Za-z]+$");
        }

        return false;
    }

    public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public static boolean isValidPasswordLength(String password){
        return password.length() >= 8 && password.length() <= 50;
    }

    public static void addStaff(User staff) {
        if(staff == null){
            System.out.println("User is null. Was not added to Map and List");
            return;
        }
        if(staff.getRole().equals("STAFF")){
            if(!containsStaff(staff.ID)){
                staffs.put(staff.ID, staff);
                staffList.add(staff);
            }
            else{
                System.out.println("Staff is already present!");
            }
        }
        else{
            System.out.println("User's role is not STAFF");
        }
    }

    public static void update(User user, String name, String email){
        if(user != null){
            user.setName(name);
            user.setEmail(email);
        }
        else{
            System.out.println("User value is null");
        }
    }

    public static void removeStaff(User staff) {
        if(validStaff(staff)){
            staffList.remove(staff);
            staffs.remove(staff.ID);
        }
        else{
            System.out.println("Staff ID not found");
        }
    }

    public static User getStaff(int staffId) {
        if(containsStaff(staffId)){
            return staffs.get(staffId);
        }
        else{
            System.out.println("Staff ID not found. Null was returned");
            return null;
        }
    }

    public static boolean validStaff(User staff){
        return staff != null && containsStaff(staff.ID);
    }

    public static boolean containsStaff(int staffID) {
        return staffs.containsKey(staffID);
    }

    public static ObservableList<User> getStaffList(){
        return staffList;
    }

    public static void emptyStaff() {
        staffs.clear();
        staffList.clear();
    }
}

