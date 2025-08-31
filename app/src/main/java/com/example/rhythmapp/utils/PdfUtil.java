package com.example.rhythmapp.utils;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class PdfUtil {
    public static void createPdfFromCurrentScreen(View view) {
        Bitmap screenBitmap = captureScreen(view);

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                (int) (screenBitmap.getWidth() * 0.7F),
                (int) (screenBitmap.getHeight() * 0.7F),
                1
        ).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint background = new Paint();
        background.setColor(Color.parseColor("#1b1b1b")); // Light gray
        canvas.drawRect(0, 0, screenBitmap.getWidth(), screenBitmap.getHeight(), background);
        canvas.drawBitmap(screenBitmap, 0, 0, null);
        pdfDocument.finishPage(page);

        savePdf(view.getContext(), pdfDocument);

        pdfDocument.close();
        screenBitmap.recycle();
    }

    public static Bitmap captureScreen(View view) {

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(
                view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(0.7F, 0.7F);
        view.draw(canvas);
        return bitmap;
    }

    public static void savePdf(PdfDocument pdfDocument) {
        try {
            File file = new File(
                     Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "screen_capture" + System.currentTimeMillis() + ".pdf"
            );
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            fos.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void savePdf(Context context, PdfDocument pdfDocument) {
        String filename = "Rhythm_Report" + System.currentTimeMillis() + ".pdf";

        Uri collection;
        collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        // collection = MediaStore.Files.getContentUri("external"); //for older api

        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, filename);
        values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
        values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Rhythm");

        Uri fileUri = context.getContentResolver().insert(collection, values);

        assert fileUri != null;
        try (OutputStream out = context.getContentResolver().openOutputStream(fileUri)) {
            pdfDocument.writeTo(out);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            pdfDocument.close();
        }

        openPdf(context, fileUri);
    }

    private static void openPdf(Context context, Uri fileUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No PDF viewer installed", Toast.LENGTH_SHORT).show();
        }
    }
}