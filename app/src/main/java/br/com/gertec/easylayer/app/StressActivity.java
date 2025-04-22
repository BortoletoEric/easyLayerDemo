package br.com.gertec.easylayer.app;

import android.content.DialogInterface;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import br.com.gertec.easylayer.printer.BarcodeFormat;
import br.com.gertec.easylayer.printer.BarcodeType;
import br.com.gertec.easylayer.printer.Printer;
import br.com.gertec.easylayer.printer.PrinterError;
import br.com.gertec.easylayer.printer.PrinterErrorCode;
import br.com.gertec.easylayer.printer.PrinterException;
import br.com.gertec.easylayer.printer.TextFormat;
import br.com.gertec.easylayer.zxing.google.zxingcore.WriterException;

public class StressActivity extends AppCompatActivity implements Printer.Listener {

    String HTML_CODE_EXAMPLE =
            "<!DOCTYPE html>" +
                    "<html>" +
                    "   <head>" +
                    "      <meta charset='UTF-8'>" +
                    "      <style type='text/css'>" +
                    "         h6 { font-size: 50%; }" +
                    "         h5 { font-size: 80%; }" +
                    "         h4 { font-size: 100%; }" +
                    "         h3 { font-size: 150%; }" +
                    "         h2 { font-size: 200%; }" +
                    "         h1 { font-size: 250%; }" +
                    "         right { float:right; }" +
                    "         left { float:left; }" +
                    "         hr { border-top: 2px solid black; }" +
                    "         table { width: 384px; }" +
                    "         body { font-size: 11px; font-family: sans serif}" +
                    "      </style>" +
                    "   </head>" +
                    "   <body>" +
                    "      <b>NOME DO ESTABELECIMENTO</b>" +
                    "      <br><b>Endereço, 101 - Bairro</br></b>" +
                    "      <b>São Paulo - SP - CEP 13.001-000</b></br>" +
                    "      <b>CNPJ: 11.222.333/0001-44</b></br>" +
                    "      <b>IE: 000.000.000.000</b></br>" +
                    "      <b>M: 0.000.000-0</b></br>" +
                    "      <hr>" +
                    "      </hr>" +
                    "      <center><b>CUPOM FISCAL ELETRONICO - SAT</b></center>" +
                    "      </br>" +
                    "      <table>" +
                    "         <tr>" +
                    "            <th>ITEM</th>" +
                    "            <th>CÓDIGO</th>" +
                    "            <th>DESCRIÇÃO</th>" +
                    "         </tr>" +
                    "         <tr>" +
                    "            <th>QTD</th>" +
                    "            <th>UN.</th>" +
                    "            <th>VL. UNIT (R$) ST</th>" +
                    "            <th>VL. ITEM(R$)</th>" +
                    "         </tr>" +
                    "      </table>" +
                    "      </br>" +
                    "      <table>" +
                    "         <tr>" +
                    "            <td><b>001</b></td>" +
                    "            <td><b>1011213</b></td>" +
                    "            <td><b>NOME DO PRODUTO</b></td>" +
                    "         </tr>" +
                    "         <tr>" +
                    "            <td><b>001</b></td>" +
                    "            <td><b>000UN</b></td>" +
                    "            <td><b>12,00 F1 A</b></td>" +
                    "            <td><b>25,00</b></td>" +
                    "         </tr>" +
                    "      </table>" +
                    "      </br>" +
                    "      <b>" +
                    "         <hr>" +
                    "         </hr>" +
                    "      </b>" +
                    "      <b>" +
                    "         TOTAL" +
                    "         <right>R$25,00</right>" +
                    "      </b>" +
                    "      </br>" +
                    "      <hr>" +
                    "      </hr>" +
                    "      <table>" +
                    "         <tr>" +
                    "            <td><b>N. 0000000139</b></td>" +
                    "            <td><b>Série 1</b></td>" +
                    "            <td><b>01/01/2016 15:00:00</b></td>" +
                    "         </tr>" +
                    "      </table>" +
                    "      </br>" +
                    "      <center><b>Consulte pela chave de acesso em</b></br>" +
                    "         <b>http://www.nfb.fazenda.sp.gov.br</br>CHAVE DE ACESSO</b>" +
                    "      </center>" +
                    "      </br>" +
                    "   </body>" +
                    "</html>";

    private TextView outputView;
    boolean rodaStresse = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stress);
        outputView = findViewById(R.id.outputView);

        print_Stress();
    }

    @Override
    public void onPrinterError(PrinterError printerError) {
        showMessageOnUiThread("Cause: " + printerError.getCause() + "\nError code: " +
                printerError.getCode() + "\nRequest id:" + printerError.getRequestId());

        if (printerError.getCode() == PrinterErrorCode.PRINTER_OUT_OF_PAPER.getCode()) {
            showDialogOnUiThread(PrinterErrorCode.PRINTER_OUT_OF_PAPER.getDescription());
        }
    }

    @Override
    public void onPrinterSuccessful(int printerRequestId) {
        Log.i("GERTEC_PRINTER", "Requisição a impressora id " + printerRequestId +
                " executada com sucesso");
    }

    private void showError(String message) {
        outputView.setText("Error : " + message);
    }

    private void showMessage(String message) {
        //outputView.setText(message);
        Toast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_LONG).show();
    }


    public void scrollPaper(View view) {
        Printer printer = Printer.getInstance(this, this);
        printer.scrollPaper(110);
    }


    public void onPrintHtml() throws PrinterException {
        Printer printer = Printer.getInstance(this, this);
        printer.printHtml(this, HTML_CODE_EXAMPLE);
        printer.scrollPaper(110);
    }

    public void onPrintQueue(View view) throws PrinterException, WriterException {
        Printer printer = Printer.getInstance(this, this);
        TextFormat textFormat = new TextFormat();
        textFormat.setFontSize(40);

        printer.printText(0, textFormat, "###############");
        printer.printHtml(this, HTML_CODE_EXAMPLE);
        printer.printText(0, textFormat, "###############");
        printer.printBarcode(new BarcodeFormat(BarcodeType.QR_CODE, BarcodeFormat.Size.SMALL),
                "Teste");
        printer.printText(0, textFormat, "###############");
        printer.scrollPaper(110);

    }

    private void showMessageOnUiThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showMessage(message);
            }
        });
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
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle("Printer Error");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void print_Stress() {
        new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        try{
                            while (rodaStresse){
                                onPrintHtml();
                                Thread.sleep(5000);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        rodaStresse = true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        rodaStresse = true;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        rodaStresse = false;
    }

}