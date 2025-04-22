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

public class MonochromaticActivity extends AppCompatActivity implements Printer.Listener {

    final String TAG = MonochromaticActivity.class.getSimpleName();
    int resourceImages[] = new int[6];
    List imgTitle = Arrays.asList("Default Printer", "40% Factor", "40% Inverted Factor", "60% Factor", "60% Inverted Factor", "40% Factor + Clean", "60% Factor + Clean");
    int selectedImageIndex = 0;
    ImageView imgEntry;
    ImageView imgResult1, imgResult2, imgResult3, imgResult4, imgResult5, imgResult6, imgResult7;
    TextView txtResult1, txtResult2, txtResult3, txtResult4, txtResult5, txtResult6, txtResult7;
    Bitmap bitmap;
    Printer printer;
    int qtdPrinter=0;
    private boolean onPrinterError = false;
    private double darknessFactor;
    private AlertDialog alertDialog;

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
        setContentView(R.layout.activity_monochromatic);

        imgEntry = findViewById(R.id.imgEntry);
        imgResult1 = findViewById(R.id.imgResult1);
        imgResult2 = findViewById(R.id.imgResult2);
        imgResult3 = findViewById(R.id.imgResult3);
        imgResult4 = findViewById(R.id.imgResult4);
        imgResult5 = findViewById(R.id.imgResult5);
        imgResult6 = findViewById(R.id.imgResult6);
        imgResult7 = findViewById(R.id.imgResult7);

        txtResult1 = findViewById(R.id.txtResult1);
        txtResult2 = findViewById(R.id.txtResult2);
        txtResult3 = findViewById(R.id.txtResult3);
        txtResult4 = findViewById(R.id.txtResult4);
        txtResult5 = findViewById(R.id.txtResult5);
        txtResult6 = findViewById(R.id.txtResult6);
        txtResult7 = findViewById(R.id.txtResult7);

        resourceImages[0] = R.drawable.placa_carro_azul;

        txtResult1.setText(imgTitle.get(0).toString());
        txtResult2.setText(imgTitle.get(1).toString());
        txtResult3.setText(imgTitle.get(2).toString());
        txtResult4.setText(imgTitle.get(3).toString());
        txtResult5.setText(imgTitle.get(4).toString());
        txtResult6.setText(imgTitle.get(5).toString());
        txtResult7.setText(imgTitle.get(6).toString());

        originalbt = getBitmapFromAsset(getApplicationContext(),"Placa_carro_azul.png");
    }

    @Override
    protected void onStart() {
        super.onStart();
        onConvertToBlackAndWhite();
    }

    public void onConvertToBlackAndWhite() {
        printer = Printer.getInstance(this, this);
        PrinterUtils printerUtils = printer.getPrinterUtils();


        bitmap = printerUtils.toMonochromatic(originalbt, darknessFactor);

        bitmap = convertToBitmap(imgEntry);
        Bitmap monochromaticBitmap = printerUtils.toMonochromatic(bitmap, 0.80);

        imgResult1.setImageBitmap(monochromaticBitmap);
        imgResult1.setVisibility(View.VISIBLE);

        imgResult2.setImageBitmap(printerUtils.toMonochromatic(bitmap, 0.40));
        imgResult2.setVisibility(View.VISIBLE);

        imgResult3.setImageBitmap(printerUtils.toInvertedMonochromatic(bitmap, 0.40));
        imgResult3.setVisibility(View.VISIBLE);

        imgResult4.setImageBitmap(printerUtils.toMonochromatic(bitmap, 0.60));
        imgResult4.setVisibility(View.VISIBLE);

        imgResult5.setImageBitmap(printerUtils.toInvertedMonochromatic(bitmap, 0.60));
        imgResult5.setVisibility(View.VISIBLE);

        imgResult6.setImageBitmap(printerUtils.toMonochromatic(bitmap, 0.40, true));
        imgResult6.setVisibility(View.VISIBLE);

        imgResult7.setImageBitmap(printerUtils.toMonochromatic(bitmap, 0.60, true));
        imgResult7.setVisibility(View.VISIBLE);


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
        qtdPrinter=8;
        printImage(((BitmapDrawable) imgEntry.getDrawable()).getBitmap(), "Original");
        printImage(((BitmapDrawable) imgResult1.getDrawable()).getBitmap(), imgTitle.get(0).toString());
        printImage(((BitmapDrawable) imgEntry.getDrawable()).getBitmap(), "Original");
        printImage(((BitmapDrawable) imgResult1.getDrawable()).getBitmap(), imgTitle.get(0).toString());
        printImage(((BitmapDrawable) imgResult2.getDrawable()).getBitmap(), imgTitle.get(1).toString());
        printImage(((BitmapDrawable) imgResult3.getDrawable()).getBitmap(), imgTitle.get(2).toString());
        printImage(((BitmapDrawable) imgResult4.getDrawable()).getBitmap(), imgTitle.get(3).toString());
        printImage(((BitmapDrawable) imgResult5.getDrawable()).getBitmap(), imgTitle.get(4).toString());
        printImage(((BitmapDrawable) imgResult6.getDrawable()).getBitmap(), imgTitle.get(5).toString());
        printImage(((BitmapDrawable) imgResult7.getDrawable()).getBitmap(), imgTitle.get(6).toString());
    }

    private void printImage(Bitmap bitmap, String label) throws PrinterException {

        Printer printer = Printer.getInstance(this, this);
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
        printImage(originalbt, "Original");
    }

    public void onPrintImage1(View view) throws PrinterException {
        qtdPrinter=1;
        printImage(((BitmapDrawable) imgResult1.getDrawable()).getBitmap(), txtResult1.getText().toString());
    }

    public void onPrintImage2(View view) throws PrinterException {
        qtdPrinter=1;
        printImage(((BitmapDrawable) imgResult2.getDrawable()).getBitmap(), txtResult2.getText().toString());
    }

    public void onPrintImage3(View view) throws PrinterException {
        qtdPrinter=1;
        printImage(((BitmapDrawable) imgResult3.getDrawable()).getBitmap(), txtResult3.getText().toString());
    }

    public void onPrintImage4(View view) throws PrinterException {
        qtdPrinter=1;
        printImage(((BitmapDrawable) imgResult4.getDrawable()).getBitmap(), txtResult4.getText().toString());
    }

    public void onPrintImage5(View view) throws PrinterException {
        qtdPrinter=1;
        printImage(((BitmapDrawable) imgResult5.getDrawable()).getBitmap(), txtResult5.getText().toString());
    }

    public void onPrintImage6(View view) throws PrinterException {
        qtdPrinter=1;
        printImage(((BitmapDrawable) imgResult6.getDrawable()).getBitmap(), txtResult6.getText().toString());
    }


    public void onPrintImage7(View view) throws PrinterException {
        qtdPrinter=1;
        printImage(((BitmapDrawable) imgResult7.getDrawable()).getBitmap(), txtResult7.getText().toString());
    }
}


