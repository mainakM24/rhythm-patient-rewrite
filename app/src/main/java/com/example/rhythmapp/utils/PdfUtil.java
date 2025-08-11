package com.example.rhythmapp.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

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

        savePdf(pdfDocument);

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
}