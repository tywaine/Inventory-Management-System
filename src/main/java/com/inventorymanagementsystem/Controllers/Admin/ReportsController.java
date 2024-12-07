package com.inventorymanagementsystem.Controllers.Admin;

import com.inventorymanagementsystem.Config.DataBaseManager;
import com.inventorymanagementsystem.Models.Model;
import com.inventorymanagementsystem.Models.Sale;
import com.inventorymanagementsystem.Utils.MyAlert;
import com.lowagie.text.pdf.PdfPCell;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import javafx.util.Callback;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class ReportsController implements Initializable {
    public TableView<Sale> tableViewSales;
    public TableColumn<Sale, Integer> columnSaleId;
    public TableColumn<Sale, Integer> columnProductId;
    public TableColumn<Sale, String> columnProductName;
    public TableColumn<Sale, LocalDate> columnSaleDate;
    public TableColumn<Sale, Integer> columnQuantitySold;
    public TableColumn<Sale, BigDecimal> columnSalePrice;
    public TableColumn<Sale, Void> columnDelete;
    public DatePicker datePickerStart, datePickerEnd;
    public Button btnGeneratePDF;
    public Label lblStartDateError, lblEndDateError;

    private final ObservableList<Sale> saleList = Sale.getList();
    private FilteredList<Sale> filteredSaleList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        datePickerStart.getEditor().setOnKeyReleased(this::handleStartDateKeyReleased);
        datePickerEnd.getEditor().setOnKeyReleased(this::handleEndDateKeyReleased);

        datePickerStart.getEditor().textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        datePickerEnd.getEditor().textProperty().addListener((observable, oldValue, newValue) -> validateFields());

        columnSaleId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        columnProductId.setCellValueFactory(cellData -> cellData.getValue().productIdProperty().asObject());
        columnProductName.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
        columnSaleDate.setCellValueFactory(cellData -> cellData.getValue().saleDateProperty());
        columnQuantitySold.setCellValueFactory(cellData -> cellData.getValue().quantitySoldProperty().asObject());
        columnSalePrice.setCellValueFactory(cellData -> cellData.getValue().salePriceProperty());
        setupDeleteColumn();
        filteredSaleList = new FilteredList<>(saleList, p -> true);
        tableViewSales.setItems(filteredSaleList);

        datePickerStart.valueProperty().addListener((observable, oldValue, newValue) -> filterSalesByDate());
        datePickerEnd.valueProperty().addListener((observable, oldValue, newValue) -> filterSalesByDate());
    }

    private void filterSalesByDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate startDate = datePickerStart.getValue();
        LocalDate endDate = datePickerEnd.getValue();

        filteredSaleList.setPredicate(sale -> {
            if (startDate == null && endDate == null) {
                return true;
            }

            if (startDate != null && endDate == null) {
                return !sale.getSaleDate().isBefore(startDate);
            }

            if (startDate == null) {
                return !sale.getSaleDate().isAfter(endDate);
            }

            return !sale.getSaleDate().isBefore(startDate) && !sale.getSaleDate().isAfter(endDate);
        });
    }

    private void setupDeleteColumn() {
        columnDelete.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Sale, Void> call(final TableColumn<Sale, Void> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button();

                    {
                        FontIcon deleteIcon = new FontIcon(FontAwesomeSolid.TRASH);
                        deleteIcon.setIconSize(16);
                        deleteButton.setGraphic(deleteIcon);

                        deleteButton.setOnAction(event -> {
                            Sale selectedSale = getTableView().getItems().get(getIndex());
                            deleteSale(selectedSale);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                };
            }
        });
    }

    private void deleteSale(Sale sale) {
        if (MyAlert.confirmationDialogAlertIsYes("Delete Sale?",
                "Are you sure you want to delete this Sale?\nSale ID: " + sale.ID)) {
            DataBaseManager.deleteSale(sale);
            MyAlert.showAlert(Alert.AlertType.INFORMATION, "Deleted Sale",
                    "Sale with ID: " +sale.ID + " has been deleted!");
        }
    }

    private void handleStartDateKeyReleased(KeyEvent keyEvent) {
        handleDateError(datePickerStart, lblStartDateError);
    }

    private void handleEndDateKeyReleased(KeyEvent keyEvent) {
        handleDateError(datePickerEnd, lblEndDateError);
    }

    private void handleDateError(DatePicker datePicker, Label lblDateError) {
        String dateStr = datePicker.getEditor().getText();

        if(dateStr.isEmpty()){
            lblDateError.setText("");
            datePicker.setValue(null);
            validateFields();
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            LocalDate date = LocalDate.parse(dateStr, formatter);
            datePicker.setValue(date);
            lblDateError.setText("");
        } catch (DateTimeException e) {
            System.err.println("Error: " + e.getMessage());
            lblDateError.setText("Not a valid date.");
        }

        validateFields();
    }

    private void validateFields() {
        if(!lblStartDateError.getText().isEmpty() || !lblEndDateError.getText().isEmpty()){
            btnGeneratePDF.setDisable(true);
        }
        else{
            if(datePickerStart.getEditor().getText().isEmpty() || datePickerEnd.getEditor().getText().isEmpty()){
                btnGeneratePDF.setDisable(false);
            }
        }

        filterSalesByDate();
    }

    public void generatePDF() {
        if (tableViewSales.getItems().isEmpty()) {
            MyAlert.showAlert(Alert.AlertType.ERROR, "TableView is Empty.",
                    "The TableView is Empty. Therefore, there is no sales report to generate.");
            return;
        }

        LocalDate startDate = datePickerStart.getValue();
        LocalDate endDate = datePickerEnd.getValue();

        if (startDate != null && endDate != null && startDate.isAfter(endDate) && !startDate.isEqual(endDate)) {
            MyAlert.showAlert(Alert.AlertType.ERROR, "Start Date is After End Date.",
                    "The Start Date cannot be after the End Date.");
            return;
        }

        if (startDate != null && endDate != null && endDate.isBefore(startDate) && !startDate.isEqual(endDate)) {
            MyAlert.showAlert(Alert.AlertType.ERROR, "End Date is Before Start Date.",
                    "The End Date cannot be before the Start Date.");
            return;
        }

        if (MyAlert.confirmationDialogAlertIsYes("Confirmation",
                "Are you sure you want to generate a PDF for the sales information?")) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            fileChooser.setInitialFileName("Sales_Report_" + LocalDate.now() + ".pdf");
            File file = fileChooser.showSaveDialog(btnGeneratePDF.getScene().getWindow());

            if (file != null) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    Document document = new Document();
                    PdfWriter.getInstance(document, fileOutputStream);
                    document.open();

                    Paragraph title = new Paragraph("Sales Report",
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
                    title.setAlignment(Element.ALIGN_CENTER);
                    document.add(title);

                    String dateTime = LocalDateTime.now().format(formatter);
                    Paragraph date = new Paragraph("Date and Time: " + dateTime,
                            FontFactory.getFont(FontFactory.HELVETICA, 12));
                    date.setSpacingAfter(10);
                    document.add(date);

                    Paragraph sales = new Paragraph("\tSales:");
                    sales.setSpacingAfter(10);
                    document.add(sales);
                    PdfPTable salesTable = getSalesTable();
                    document.add(salesTable);

                    Paragraph products = new Paragraph("\n\tProducts:");
                    products.setSpacingAfter(10);
                    document.add(products);
                    PdfPTable summaryTable = getProductSummaryTable();
                    document.add(summaryTable);
                    Paragraph totalRevenue = new Paragraph("Total Revenue: $" + getTotalRevenue());
                    document.add(totalRevenue);

                    Paragraph topSellingProducts = new Paragraph("\n\tTop Selling Product/s:");
                    topSellingProducts.setSpacingAfter(10);
                    document.add(topSellingProducts);
                    PdfPTable topSellingProductTable = getTopSellingProductTable();
                    document.add(topSellingProductTable);

                    document.close();
                    MyAlert.showAlert(Alert.AlertType.INFORMATION, "Successfully Generated PDF",
                            "The PDF report has been saved successfully.");
                } catch (Exception e) {
                    e.printStackTrace();
                    MyAlert.showAlert(Alert.AlertType.ERROR, "Error Generating PDF",
                            "An error occurred while generating the PDF.");
                }
            }
        }
    }

    private PdfPTable getSalesTable() {
        float[] columnWidths = {2, 2, 2, 2, 2, 2};
        PdfPTable salesTable = new PdfPTable(columnWidths);

        salesTable.addCell(new PdfPCell(new Phrase("Sale ID", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
        salesTable.addCell(new PdfPCell(new Phrase("Product ID", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
        salesTable.addCell(new PdfPCell(new Phrase("Product Name", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
        salesTable.addCell(new PdfPCell(new Phrase("Sale Date", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
        salesTable.addCell(new PdfPCell(new Phrase("Quantity Sold", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
        salesTable.addCell(new PdfPCell(new Phrase("Sale Price", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));

        for (Sale sale : tableViewSales.getItems()) {
            salesTable.addCell(String.valueOf(sale.ID));
            salesTable.addCell(String.valueOf(sale.getProductId()));
            salesTable.addCell(sale.getProductName());
            salesTable.addCell(sale.getSaleDate().toString());
            salesTable.addCell(String.valueOf(sale.getQuantitySold()));
            salesTable.addCell("$" + sale.getSalePrice().toString());
        }

        return salesTable;
    }

    private PdfPTable getProductSummaryTable() {
        float[] columnWidths = {2, 4, 2, 2};
        PdfPTable summaryTable = new PdfPTable(columnWidths);

        summaryTable.addCell(new PdfPCell(new Phrase("Product ID", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
        summaryTable.addCell(new PdfPCell(new Phrase("Product Name", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
        summaryTable.addCell(new PdfPCell(new Phrase("Total Quantity Sold", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
        summaryTable.addCell(new PdfPCell(new Phrase("Total Sales", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));

        Set<Integer> processedProductIds = new HashSet<>();

        for (Sale sale : tableViewSales.getItems()) {
            int productId = sale.getProductId();

            if (processedProductIds.add(productId)) {
                int totalQuantitySold = getTotalQuantitySold(productId);
                BigDecimal totalSales = getTotalSales(productId);

                summaryTable.addCell(String.valueOf(productId));
                summaryTable.addCell(sale.getProductName());
                summaryTable.addCell(String.valueOf(totalQuantitySold));
                summaryTable.addCell("$" + totalSales.toString());
            }
        }

        return summaryTable;
    }

    public BigDecimal getTotalSales(int productId) {
        BigDecimal totalSales = BigDecimal.ZERO;

        for (Sale sale : tableViewSales.getItems()) {
            if (sale.getProductId() == productId) {
                BigDecimal saleAmount = sale.getSalePrice().multiply(BigDecimal.valueOf(sale.getQuantitySold()));
                totalSales = totalSales.add(saleAmount);
            }
        }
        return totalSales;
    }

    public int getTotalQuantitySold(int productId) {
        int totalQuantitySold = 0;

        for (Sale sale : tableViewSales.getItems()) {
            if (sale.getProductId() == productId) {
                totalQuantitySold += sale.getQuantitySold();
            }
        }
        return totalQuantitySold;
    }

    public BigDecimal getTotalRevenue(){
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (Sale sale : tableViewSales.getItems()) {
            BigDecimal saleAmount = sale.getSalePrice().multiply(BigDecimal.valueOf(sale.getQuantitySold()));
            totalRevenue = totalRevenue.add(saleAmount);
        }

        return totalRevenue;
    }

    public PdfPTable getTopSellingProductTable(){
        List<Sale> sales = new ArrayList<>();
        Set<Integer> productIds = new HashSet<>();
        Sale saleResult = null;
        int saleResult_totalQuantitySold = -1;

        for(Sale currentSale: tableViewSales.getItems()){
            if(sales.isEmpty()){
                sales.add(currentSale);
                productIds.add(currentSale.getProductId());
                saleResult = currentSale;
                saleResult_totalQuantitySold = getTotalQuantitySold(currentSale.getProductId());
            }
            else{
                if(productIds.add(currentSale.getProductId())){
                    int currentSale_totalQuantitySold = getTotalQuantitySold(currentSale.getProductId());

                    if(currentSale_totalQuantitySold > saleResult_totalQuantitySold){
                        sales.clear();
                        sales.add(currentSale);
                        saleResult = currentSale;
                        saleResult_totalQuantitySold = getTotalQuantitySold(currentSale.getProductId());
                    }
                    else{
                        if(currentSale.getQuantitySold() == saleResult.getQuantitySold()){
                            sales.add(currentSale);
                        }
                    }
                }
            }
        }

        float[] columnWidths = {2, 4, 2, 2};
        PdfPTable topSellingProducts = new PdfPTable(columnWidths);

        topSellingProducts.addCell(new PdfPCell(new Phrase("Product ID", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
        topSellingProducts.addCell(new PdfPCell(new Phrase("Product Name", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
        topSellingProducts.addCell(new PdfPCell(new Phrase("Total Quantity Sold", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
        topSellingProducts.addCell(new PdfPCell(new Phrase("Total Sales", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));

        Set<Integer> processedProductIds = new HashSet<>();

        for (Sale sale : sales) {
            int productId = sale.getProductId();

            if (processedProductIds.add(productId)) {
                int totalQuantitySold = getTotalQuantitySold(productId);
                BigDecimal totalSales = getTotalSales(productId);

                topSellingProducts.addCell(String.valueOf(productId));
                topSellingProducts.addCell(sale.getProductName());
                topSellingProducts.addCell(String.valueOf(totalQuantitySold));
                topSellingProducts.addCell("$" + totalSales.toString());
            }
        }

        return topSellingProducts;
    }

}
