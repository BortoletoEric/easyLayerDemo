package br.com.gertec.easylayer.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

import br.com.gertec.easylayer.codescanner.CodeScanner;
import br.com.gertec.easylayer.codescanner.ScanConfig;
import br.com.gertec.easylayer.zxing.google.zxing.integration.android.IntentIntegrator;
import br.com.gertec.easylayer.zxing.google.zxing.integration.android.IntentResult;

public class CodeScannerActivity extends AppCompatActivity {

    private TextView outputView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scancode);
        outputView = findViewById(R.id.outputView);

        Button btnAll = findViewById(R.id.scan1);
        btnAll.setOnClickListener(view -> onScanAll());
    }

    public void onScanAll() {
        CodeScanner codeScanner = CodeScanner.getInstance();
        codeScanner.scanCode(this);
    }

    public void onScanQrCode(View view) {
        CodeScanner codeScanner = CodeScanner.getInstance();
        codeScanner.scanCode(this, CodeScanner.QR_CODE);
    }

    public void onScanITF(View view) {
        CodeScanner codeScanner = CodeScanner.getInstance();
        ScanConfig scanConfig = new ScanConfig();
        scanConfig.setBeepEnabled(true);
        scanConfig.setLandscapeEnabled(true);
        scanConfig.setTimeout(20000);
        scanConfig.setLabel("Meu leitor ITF");
        codeScanner.scanCode(this, scanConfig, CodeScanner.ITF);
    }

    public void onScan1D(View view) {
        CodeScanner codeScanner = CodeScanner.getInstance();
        codeScanner.scanCode(this, CodeScanner.SCAN_1D);
    }

    public void onScan2D(View view) {
        CodeScanner codeScanner = CodeScanner.getInstance();
        codeScanner.scanCode(this, CodeScanner.SCAN_2D);
    }

    public void onScanCustom(View view) {
        CodeScanner codeScanner = CodeScanner.getInstance();
        List<String> formats = Arrays.asList(CodeScanner.QR_CODE, CodeScanner.CODABAR);
        codeScanner.scanCode(this, formats);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String content = result.getContents();
            String codeType = result.getFormatName();
            if (content != null && codeType != null) {
                outputView.setText("Tipo de Código: " + codeType + "\n Conteúdo: " + content);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
