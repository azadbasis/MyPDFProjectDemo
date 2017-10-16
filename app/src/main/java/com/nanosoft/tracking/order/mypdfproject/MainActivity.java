package com.nanosoft.tracking.order.mypdfproject;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String pdfFileName;
    double grandTotal = 0;
    String titleText = "";
    List<String> itemList;


    private int REQUEST_EXTERNAL_STORAGE = 1;
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        itemList = new ArrayList<>();
        itemList.add("Azhar");
        itemList.add("Anwar");
        itemList.add("Anis");
        itemList.add("Arif");
        itemList.add("SOJIB");
        itemList.add("KUTUB");
        String customerName = "CustomerName";
        String customerAddress = "CustomerAddress";
        String customerMobile = "CustomerMobile";
        String orderBy = "OrderBy";
        String type = "Type";
        createPDF(customerName, customerAddress, customerMobile, orderBy, type, itemList);

    }

    private void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {

        }
    }

    private void createPDF(String name, String address, String mobile, String orderBy, String type, List<String> ordersPdf) {

        Document doc = new Document();
        PdfWriter docWriter = null;
        DecimalFormat df = new DecimalFormat("0.00");
        Calendar c = Calendar.getInstance();
        String sDate = c.get(Calendar.YEAR) + "-"
                + c.get(Calendar.MONTH)
                + "-" + c.get(Calendar.DAY_OF_MONTH);
        try {

            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
            Font bf12 = new Font(Font.FontFamily.TIMES_ROMAN, 12);
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sale Report";

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();
            //pdfFileName = name+" "+sDate;
            pdfFileName = name;
            File file = new File(dir, pdfFileName + ".pdf");
            FileOutputStream fOut = new FileOutputStream(file);
            docWriter = PdfWriter.getInstance(doc, fOut);

            doc.addAuthor("betterThanZero");
            doc.addCreationDate();
            doc.addProducer();
            doc.addCreator("MySampleCode.com");
            doc.addTitle("Report with Column Headings");
            doc.setPageSize(PageSize.LETTER);

            doc.open();
            titleText = name + "\n" + "Address:" + address + "\n" + "Mobile:" + mobile + "\n" + "Order By:" + orderBy +
                    "\n" + "Type:" + type;


            Paragraph paragraph = new Paragraph("Invoice");
            paragraph.setAlignment(Paragraph.ALIGN_CENTER);
            float[] columnWidthHeading = {1f, 1f};
            PdfPTable tableHeading = new PdfPTable(columnWidthHeading);
            tableHeading.setWidthPercentage(100f);
            tableHeading.setSpacingAfter(35f);
            insertCellHeading(tableHeading, titleText, Element.ALIGN_CENTER, 1, bfBold12);
            insertCellHeading(tableHeading, titleText, Element.ALIGN_CENTER, 1, bfBold12);

            float[] columnWidths = {1f, 1f, 1f, 1f};
            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(100f);

            insertCell(table, "Product Name", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Unit Price", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Quantity", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Total", Element.ALIGN_CENTER, 1, bfBold12);
            table.setHeaderRows(1);


            for (int i = 0; i < ordersPdf.size(); i++) {
                insertCell(table, ordersPdf.get(i), Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, String.valueOf(ordersPdf.get(i)), Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, String.valueOf(ordersPdf.get(i)), Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, String.valueOf(ordersPdf.get(i)), Element.ALIGN_CENTER, 1, bf12);
                grandTotal += ordersPdf.get(i).length();

            }
            insertCell(table, "All Total", Element.ALIGN_RIGHT, 3, bfBold12);
            insertCell(table, df.format(grandTotal), Element.ALIGN_RIGHT, 1, bfBold12);

            paragraph.add(tableHeading);
            paragraph.add(table);
            doc.add(paragraph);

        } catch (DocumentException dex) {
            dex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (doc != null) {
                doc.close();
            }
            if (docWriter != null) {
                docWriter.close();
            }
        }

        viewPdf(pdfFileName + ".pdf", "Sale Report");

        //   addDataToRealm(name,address,mobile);
        //startActivity(new Intent(con,HistoryActivity.class));

    }

    private void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {

        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        cell.setMinimumHeight(20f);
        //set the cell alignment
        cell.setHorizontalAlignment(align);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        //in case there is no text and you wan to create an empty row
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(10f);
        }
        //add the call to the table
        table.addCell(cell);
    }

    private void insertCellHeading(PdfPTable table, String text, int align, int colspan, Font font) {

        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        //set the cell alignment
        cell.setHorizontalAlignment(align);
        cell.setBorderColor(BaseColor.WHITE);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        //in case there is no text and you wan to create an empty row
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(10f);
        }
        //add the call to the table
        table.addCell(cell);
    }

    private void viewPdf(String file, String directory) {

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        Uri path = Uri.fromFile(pdfFile);

        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        //pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }
}
