package br.com.gertec.easylayer.app;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.gertec.easylayer.printer.Alignment;
import br.com.gertec.easylayer.printer.BarcodeFormat;
import br.com.gertec.easylayer.printer.BarcodeType;
import br.com.gertec.easylayer.printer.PrintConfig;
import br.com.gertec.easylayer.printer.Printer;
import br.com.gertec.easylayer.printer.PrinterError;
import br.com.gertec.easylayer.printer.PrinterErrorCode;
import br.com.gertec.easylayer.printer.PrinterException;
import br.com.gertec.easylayer.printer.TextFormat;
import br.com.gertec.easylayer.zxing.google.zxingcore.WriterException;


public class PrinterActivity extends AppCompatActivity implements Printer.Listener {
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
    private RadioGroup codeChoiceRadioGroup;
    AlertDialog alertDialog = null;
    private RadioButton smallCodeBtn, halfPaperCodeBtn, fullPaperCodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);
        outputView = findViewById(R.id.outputView);
        codeChoiceRadioGroup = findViewById(R.id.printCodeRadioGroup);
        smallCodeBtn = findViewById(R.id.smallCodeBtn);
        halfPaperCodeBtn = findViewById(R.id.halfPaperCodeBtn);
        fullPaperCodeBtn = findViewById(R.id.fullPaperCodeBtn);

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
        outputView.setText(message);
    }

    public void onPrintText(View view) throws PrinterException {
        Printer printer = Printer.getInstance(this, this);
        TextFormat textFormat = new TextFormat();
        textFormat.setFontSize(20);
        textFormat.setBold(true);
        textFormat.setAlignment(Alignment.CENTER);

        printer.printText(textFormat, "Exemplo de texto formatado");
        scrollpaper();
    }

    public void onScrollPaper(View view) {
        scrollpaper();
    }

    public void onPrintImage(View view) throws PrinterException {
        Printer printer = Printer.getInstance(this, this);
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.gertec);
        PrintConfig printConfig = new PrintConfig(400, 100, Alignment.CENTER);
        printer.printImage(printConfig, image);
        scrollpaper();
    }

    public void onPrintImageAutoResize(View view) throws PrinterException {
        Printer printer = Printer.getInstance(this, this);
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.invoice);
        printer.printImageAutoResize(image);
        scrollpaper();
    }

    public void onStatus(View view) {
        Printer printer = Printer.getInstance(this, this);
        try {
            showMessage(printer.getStatus().toString());
        } catch (PrinterException e) {
            showError(e.getMessage());
            e.printStackTrace();
        }
    }

    public void onPrintHtml(View view) throws PrinterException {
        Printer printer = Printer.getInstance(this, this);
        printer.printHtml(this, HTML_CODE_EXAMPLE);
        scrollpaper();
    }

    public void onPrintTable(View view) throws PrinterException {
        Printer printer = Printer.getInstance(this, this);

        List<String> header = Arrays.asList("QTD", "PRODUTO", "UNIT(R$)", "TOTAL(R$)");

        List<List<String>> rows = new ArrayList<>();
        rows.add(Arrays.asList("01", "ARROZ INTEGRAL", "2.00", "4.00"));
        rows.add(Arrays.asList("02", "LEITE CONDENSADO", "4,50", "9,00"));
        rows.add(Arrays.asList("03", "CAFE DO NORTE", "5,00", "15,00"));
        rows.add(Arrays.asList("01", "FARINHA UARINI", "9,60", "9,60"));
        rows.add(Arrays.asList("05", "AÇUCAR MASCAVO", "2,50", "12,50"));
        rows.add(Arrays.asList("01", "FEIJÃO PRETO", "10,00", "10,00"));
        rows.add(Arrays.asList("10", "MACARRÃO DO NORTE", "3,00", "30,00"));

        printer.printTable(this, header, rows);
        scrollpaper();
    }

    public void onPrintQrCode(View view) throws PrinterException, WriterException {
        Printer printer = Printer.getInstance(this, this);
        BarcodeFormat barcodeFormat = new BarcodeFormat(BarcodeType.QR_CODE,
                getBarcodeSize());
        printer.printBarcode(barcodeFormat, "https://www.gertec.com.br/");
        scrollpaper();
    }

    public void onPrintAztecCode(View view) throws PrinterException, WriterException {
        Printer printer = Printer.getInstance(this, this);
        BarcodeFormat barcodeFormat = new BarcodeFormat(BarcodeType.AZTEC,
                getBarcodeSize());
        printer.printBarcode(barcodeFormat, "https://www.gertec.com.br/");
        scrollpaper();
    }

    public void onPrintPdf417Code(View view) throws PrinterException, WriterException {
        Printer printer = Printer.getInstance(this, this);
        BarcodeFormat.Size size = getBarcodeSize();
        if (size.equals(BarcodeFormat.Size.SMALL)) {
            showAlertDialog("Tamanho inválido (SMALL) para o BarCode selecionado.");
        } else {
            BarcodeFormat barcodeFormat = new BarcodeFormat(BarcodeType.PDF_417, size);
            printer.printBarcode(barcodeFormat, "https://www.gertec.com.br/");
            scrollpaper();
        }
    }

    private BarcodeFormat.Size getBarcodeSize() {
        BarcodeFormat.Size size;

        switch (codeChoiceRadioGroup.getCheckedRadioButtonId()) {
            case R.id.smallCodeBtn:
                size = BarcodeFormat.Size.SMALL;
                break;
            case R.id.fullPaperCodeBtn:
                size = BarcodeFormat.Size.FULL_PAPER;
                break;
            default:
                size = BarcodeFormat.Size.HALF_PAPER;
                break;
        }
        return size;
    }

    public void onPaperUsage(View view) {
        Printer printer = Printer.getInstance(this, this);
        int paperUsage = 0;
        try {
            paperUsage = printer.getPaperUsage();
            showMessage("Paper usage: " + paperUsage);
        } catch (PrinterException e) {
            showError(e.getMessage());
            e.printStackTrace();
        }
    }

    public void onResetPaperUsage(View view) {
        Printer printer = Printer.getInstance(this, this);
        try {
            printer.resetPaperUsage();
        } catch (PrinterException e) {
            showError(e.getMessage());
            e.printStackTrace();
        }
    }

    public void onPrintQueue(View view) throws PrinterException, WriterException {
        Printer printer = Printer.getInstance(this, this);
        TextFormat textFormat = new TextFormat();
        textFormat.setFontSize(40);

        printer.printText(0, textFormat, "###############");
        printer.printHtml(this, HTML_CODE_EXAMPLE);
        printer.printText(0, textFormat, "###############");
        printer.printBarcode(new BarcodeFormat(BarcodeType.QR_CODE, getBarcodeSize()),
                "Teste");
        printer.printText(0, textFormat, "###############");
        scrollpaper();
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
                        }
                    });
            alertDialog.show();
        }
    }

    private void scrollpaper() {
        Printer printer = Printer.getInstance(this, this);

        int lineValue = 0;

        if (Build.MODEL.contains("790")) { // Validação para colocar o valor ideal para o scrollpaper de cada terminal
            lineValue = 180;
        } else if (Build.MODEL.contains("760")) {
            lineValue = 100;
        }

        printer.scrollPaper(lineValue);
    }
}
