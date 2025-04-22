package br.com.gertec.easylayer.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import br.com.gertec.easylayer.printer.Alignment;
import br.com.gertec.easylayer.printer.Printer;
import br.com.gertec.easylayer.printer.PrinterError;
import br.com.gertec.easylayer.printer.PrinterErrorCode;
import br.com.gertec.easylayer.printer.PrinterException;
import br.com.gertec.easylayer.printer.PrinterUtils;
import br.com.gertec.easylayer.printer.TextFormat;

public class IntensityLevelActivity extends AppCompatActivity implements Printer.Listener {

    final String TAG = IntensityLevelActivity.class.getSimpleName();
    List imgTitle = Arrays.asList("40% Monochromatic", "Intensity Level 2", "Intensity Level 3");
    ImageView imgEntry;
    ImageView imgResult1, imgResult2, imgResult3;
    TextView txtResult1, txtResult2, txtResult3;
    Bitmap bitmap;
    Printer printer;
    int qtdPrinter=0;
    private AlertDialog alertDialog;
    private boolean onPrinterError = false;
    public Bitmap getBitmapFromAsset(Context context, String filePath) {
//        AssetManager assetManager = getResources().getAssets().open(filePath);

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = getResources().getAssets().open(filePath);
//            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            e.printStackTrace();
            // handle exception
        }

        return bitmap;
    }
    Bitmap  originalbt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intensity_level);

        imgEntry = findViewById(R.id.imgEntry);
        imgResult1 = findViewById(R.id.imgResult1);
        imgResult2 = findViewById(R.id.imgResult2);
        imgResult3 = findViewById(R.id.imgResult3);

        txtResult1 = findViewById(R.id.txtResult1);
        txtResult2 = findViewById(R.id.txtResult2);
        txtResult3 = findViewById(R.id.txtResult3);

        //resourceImages[0] = R.drawable.invoice;

        txtResult1.setText(imgTitle.get(0).toString());
        txtResult2.setText(imgTitle.get(1).toString());
        txtResult3.setText(imgTitle.get(2).toString());

        originalbt = getBitmapFromAsset(getApplicationContext(),"invoice.png");
    }

    @Override
    protected void onStart() {
        super.onStart();
        onConvertToBlackAndWhite();

    }

    public void onConvertToBlackAndWhite() {
        printer = Printer.getInstance(this, this);
        PrinterUtils printerUtils = printer.getPrinterUtils();

        double darknessFactor = 0.40; // 40%
//        bitmap = printerUtils.toMonochromatic(convertToBitmap(imgEntry), darknessFactor);
        bitmap = printerUtils.toMonochromatic(originalbt, darknessFactor);


        imgResult1.setImageBitmap(bitmap);
        imgResult1.setVisibility(View.VISIBLE);

        imgResult2.setImageBitmap(printerUtils.applyIntensityLevel(bitmap, PrinterUtils.INTENSITY_LEVEL_2));
        imgResult2.setVisibility(View.VISIBLE);

        imgResult3.setImageBitmap(printerUtils.applyIntensityLevel(bitmap, PrinterUtils.INTENSITY_LEVEL_3));
        imgResult3.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.btnPrintAll) {
            try {
                onPrintAll();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPrintAll() throws PrinterException {
        printer = Printer.getInstance(this, this);
        PrinterUtils printerUtils = printer.getPrinterUtils();

        double darknessFactor = 0.40; // 40%
        qtdPrinter=8;
        //printImage(((BitmapDrawable) imgEntry.getDrawable()).getBitmap(), "Original");
        printImage(originalbt, "Original");

        bitmap = printerUtils.toMonochromatic(originalbt, darknessFactor);
        printImage(bitmap, imgTitle.get(0).toString());
       // printImage(((BitmapDrawable) imgResult1.getDrawable()).getBitmap(), imgTitle.get(0).toString());
        printImage(((BitmapDrawable) imgResult2.getDrawable()).getBitmap(), imgTitle.get(1).toString());
        printImage(((BitmapDrawable) imgResult3.getDrawable()).getBitmap(), imgTitle.get(2).toString());
    }

    private void printImage(Bitmap bitmap, String label) throws PrinterException {
        TextFormat textFormat = new TextFormat();
        textFormat.setFontSize(30);
        textFormat.setAlignment(Alignment.CENTER);

        printer.printText(textFormat, label);
        printer.printImageAutoResize(bitmap);
        printer.scrollPaper(140);
    }

    private Bitmap convertToBitmap(ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        return drawable.getBitmap();
    }

    @Override
    public void onPrinterError(PrinterError printerError) {
        if (onPrinterError == false) {
            Log.i(TAG, "Cause: " + printerError.getCause() + "\nError code: " +
                    printerError.getCode() + "\nRequest id:" + printerError.getRequestId());
            if (printerError.getCode() == PrinterErrorCode.PRINTER_OUT_OF_PAPER.getCode()) {
                showDialogOnUiThread(PrinterErrorCode.PRINTER_OUT_OF_PAPER.getDescription());
            }
            onPrinterError = true;
        }
    }

    private void showDialogOnUiThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showAlertDialog(message);
            }
        });
    }

    private void showAlertDialog(String message) {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(this).create();
        }

        if (alertDialog.isShowing() == false) { // Verifica se o dialog não está sendo exibido
            alertDialog.setTitle("Printer Error");
            alertDialog.setMessage(message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            onPrinterError = false;
                        }
                    });
            alertDialog.show();
        }

    }

    @Override
    public void onPrinterSuccessful(int printerRequestId) {
        Log.i("GERTEC_PRINTER", "Requisição a impressora id " + printerRequestId +
                " executada com sucesso");
    }


    public void onPrintImageEntry(View view) throws PrinterException {
        qtdPrinter=1;
//        printImage(((BitmapDrawable) imgEntry.getDrawable()).getBitmap(), "Original");
        printImage(originalbt, "Original");
    }

    public void onPrintIntensityLevelImage1(View view) throws PrinterException {
        qtdPrinter=1;
//        printImage(((BitmapDrawable) imgResult1.getDrawable()).getBitmap(), txtResult1.getText().toString());
        printImage(((BitmapDrawable) imgResult1.getDrawable()).getBitmap(), txtResult1.getText().toString());
    }

    public void onPrintIntensityLevelImage2(View view) throws PrinterException {
        qtdPrinter=1;
        printImage(((BitmapDrawable) imgResult2.getDrawable()).getBitmap(), txtResult2.getText().toString());
    }

    public void onPrintIntensityLevelImage3(View view) throws PrinterException {
        qtdPrinter=1;
        printImage(((BitmapDrawable) imgResult3.getDrawable()).getBitmap(), txtResult3.getText().toString());
    }
}
