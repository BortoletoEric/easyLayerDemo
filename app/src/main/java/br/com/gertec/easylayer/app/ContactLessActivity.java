package br.com.gertec.easylayer.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import br.com.gertec.easylayer.contactless.AuthenticationException;
import br.com.gertec.easylayer.contactless.ConnectionException;
import br.com.gertec.easylayer.contactless.GenericContactLess;
import br.com.gertec.easylayer.contactless.MifareClassic;

public class ContactLessActivity extends AppCompatActivity {
    private TextView outputView, authenticationKeyText, readBlocksText, writeDataText, writeBlock;
    private Switch switchAuthentication;
    private MifareClassic mifare;
    private int DEFAULT_BLOCKS_TO_WRITE = 64;
    private int DEFAULT_DATA_VALUE_INDEX = 5;
    private int DEFAULT_RESTORE_INDEX = 6;
    private int BLOCKS_IN_SECTOR = 4;
    AlertDialog alertDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_less);

        outputView = findViewById(R.id.outputView);
        authenticationKeyText = findViewById(R.id.authenticationKeyText);
        readBlocksText = findViewById(R.id.readBlocksText);
        writeDataText = findViewById(R.id.writeDataText);
        writeBlock = findViewById(R.id.writeBlock);
        switchAuthentication = (Switch) findViewById(R.id.switchAuthentication);
    }

    public void onReadBlocks(View view) {
        if (readBlocksText.getText().equals("")) {
            Toast.makeText(ContactLessActivity.this,"Bloco inválido!",Toast.LENGTH_LONG).show();

        } else {
            int blocksToRead = Integer.parseInt(readBlocksText.getText().toString());
            blocksToRead = Math.min(blocksToRead, 64);

            byte[] authenticationKey = Utils.hexStringToByteArray(authenticationKeyText.getText().toString());
            readBlocks(blocksToRead, authenticationKey);
        }
    }


    public void onWriteBlocks(View view) {
        byte[] authenticationKey, data;
        authenticationKey = Utils.hexStringToByteArray(authenticationKeyText.getText().toString());
        String stringData = "22222222222222222222222222222222";
        data = Utils.hexStringToByteArray(stringData);

        boolean status = writeDataBlocks(DEFAULT_BLOCKS_TO_WRITE, data, authenticationKey);
        if (status == true)
            showMessage("Write '22' into all data blocks finished!");
    }

    public void onIncrementValue(View view) {
        //TODO uncomment code below only on  mifare.increment() is supported by API
//        mifare = new MifareClassic();
//        byte[] keyValue;
//        int blockIndex, valueToIncrement;
//
//        keyValue = Utils.hexStringToByteArray(authenticationKeyText.getText().toString());
//        blockIndex = DEFAULT_DATA_VALUE_INDEX;
//        valueToIncrement = 2;
//
//        try {
//            mifare.connect(2000);
//            mifare.authenticateSectorWithKeyA(mifare.blockToSector(blockIndex), keyValue);
//            mifare.increment(blockIndex, valueToIncrement);
//
//            showBlockContent("Increment finished!", blockIndex, mifare.readBlock(blockIndex));
//            mifare.close();
//        } catch (ConnectionException e) {
//            showError(e.getMessage()); e.printStackTrace();
//        } catch (AuthenticationException e) {
//            showError(e.getMessage()); e.printStackTrace();
//        } catch (IOException e) {
//            showError(e.getMessage()); e.printStackTrace();
//        }
    }

    public void onDecrementValue(View view) {
        //TODO uncomment code below only on  mifare.decrement() is supported by API
//        mifare = new MifareClassic();
//        byte[] keyValue;
//        int blockIndex, valueToDecrement;
//
//        keyValue = Utils.hexStringToByteArray(authenticationKeyText.getText().toString());
//        blockIndex = DEFAULT_DATA_VALUE_INDEX;
//        valueToDecrement = 2;
//
//        try {
//            mifare.connect(2000);
//            mifare.authenticateSectorWithKeyA(mifare.blockToSector(blockIndex), keyValue);
//            mifare.decrement(blockIndex, valueToDecrement);
//
//            showBlockContent("Decrement finished!", blockIndex, mifare.readBlock(blockIndex));
//            mifare.close();
//        } catch (ConnectionException e) {
//            showError(e.getMessage()); e.printStackTrace();
//        } catch (AuthenticationException e) {
//            showError(e.getMessage()); e.printStackTrace();
//        } catch (IOException e) {
//            showError(e.getMessage()); e.printStackTrace();
//        }
    }

    public void onDataValue(View view) {
        byte[] authenticationKey = Utils.hexStringToByteArray(authenticationKeyText.getText().toString());
        writeDataValue(DEFAULT_DATA_VALUE_INDEX, authenticationKey);
    }

    public void onRestore(View view) {
        //TODO uncomment code below only on  mifare.restore() is supported by API
//        mifare = new MifareClassic();
//        byte[] keyValue;
//        int sourceBlockIndex, targetBlockIndex;
//
//        keyValue = Utils.hexStringToByteArray(authenticationKeyText.getText().toString());
//        sourceBlockIndex = DEFAULT_DATA_VALUE_INDEX;
//        targetBlockIndex = DEFAULT_RESTORE_INDEX;
//
//        try {
//            mifare.connect(2000);
//            mifare.authenticateSectorWithKeyA(mifare.blockToSector(sourceBlockIndex), keyValue);
//            mifare.restore(sourceBlockIndex, targetBlockIndex);
//
//            showBlockContent("Restore finished!", targetBlockIndex, mifare.readBlock(targetBlockIndex));
//            mifare.close();
//        } catch (ConnectionException e) {
//            showError(e.getMessage()); e.printStackTrace();
//        } catch (AuthenticationException e) {
//            showError(e.getMessage()); e.printStackTrace();
//        } catch (IOException e) {
//            showError(e.getMessage()); e.printStackTrace();
//        }
    }

    public void onEraseDataBlocks(View view) {
        byte[] authenticationKey, data;

        authenticationKey = Utils.hexStringToByteArray(authenticationKeyText.getText().toString());
        String stringData = "00000000000000000000000000000000";
        data = Utils.hexStringToByteArray(stringData);

        boolean status = writeDataBlocks(DEFAULT_BLOCKS_TO_WRITE, data, authenticationKey);
        if (status == true)
            showMessage("Write '00' into all data blocks finished!");
    }

    public void buttonWriteBlock(View view) {
        try{
            byte[] authenticationKey, data;

            authenticationKey = Utils.hexStringToByteArray(authenticationKeyText.getText().toString());
            String stringData = writeDataText.getText().toString();
            data = Utils.hexStringToByteArray(stringData);
            int block = Integer.parseInt(writeBlock.getText().toString());

            boolean status = writeDataBlock(block, data, authenticationKey);
            if (status == true)
                showMessage("Write data block finished!");
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error write data block!");
        }
    }

    private boolean writeDataBlocks(int blocks, byte[] data, byte[] authenticationKey) {
        mifare = new MifareClassic();

        if(switchAuthentication.isChecked()) {
            try {
                mifare.connect(2000);
                mifare.authenticateSectorWithKeyA(0, authenticationKey);
                for (int blockIndex = 1; blockIndex < blocks; blockIndex++) {
                    if (blockIndex % BLOCKS_IN_SECTOR == 0) {
                        int sector = mifare.blockToSector(blockIndex);
                        mifare.authenticateSectorWithKeyA(sector, authenticationKey);
                    }
                    if ((blockIndex + 1) % BLOCKS_IN_SECTOR != 0) { // it prevents writing into last block from each sector
                        mifare.writeBlock(blockIndex, data);
                    }
                }

                mifare.close();
                return true;
            } catch (ConnectionException e) {
                showError(e.getMessage());
                e.printStackTrace();
            } catch (AuthenticationException e) {
                showError(e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                showError(e.getMessage());
                e.printStackTrace();
            } catch (NullPointerException e) {
                showError(e.getMessage());
                e.printStackTrace();
            }
        } else {
            try {
                mifare.connect(2000);
                for (int blockIndex = 1; blockIndex < blocks; blockIndex++) {
                    if (blockIndex % BLOCKS_IN_SECTOR == 0) {
                        int sector = mifare.blockToSector(blockIndex);
                    }
                    if ((blockIndex + 1) % BLOCKS_IN_SECTOR != 0) { // it prevents writing into last block from each sector
                        mifare.writeBlock(blockIndex, data);
                    }
                }

                mifare.close();
                return true;
            } catch (ConnectionException e) {
                showError(e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                showError(e.getMessage());
                e.printStackTrace();
            } catch (NullPointerException e) {
                showError(e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean writeDataBlock(int block, byte[] data, byte[] authenticationKey) {
        mifare = new MifareClassic();

        if(switchAuthentication.isChecked()) {
            try {
                mifare.connect(2000);
                mifare.authenticateSectorWithKeyA(0, authenticationKey);
                if (block % BLOCKS_IN_SECTOR == 0) {
                    int sector = mifare.blockToSector(block);
                    mifare.authenticateSectorWithKeyA(sector, authenticationKey);
                }
                if ((block + 1) % BLOCKS_IN_SECTOR != 0) { // it prevents writing into last block from each sector
                    mifare.writeBlock(block, data);
                }

                mifare.close();
                return true;
            } catch (ConnectionException e) {
                showError(e.getMessage());
                e.printStackTrace();
            } catch (AuthenticationException e) {
                showError(e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                showError(e.getMessage());
                e.printStackTrace();
            } catch (NullPointerException e) {
                showError(e.getMessage());
                e.printStackTrace();
            }
        } else {
            try {
                mifare.connect(2000);
                if (block % BLOCKS_IN_SECTOR == 0) {
                    int sector = mifare.blockToSector(block);
                }
                if ((block + 1) % BLOCKS_IN_SECTOR != 0) { // it prevents writing into last block from each sector
                    mifare.writeBlock(block, data);
                }

                mifare.close();
                return true;
            } catch (ConnectionException e) {
                showError(e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                showError(e.getMessage());
                e.printStackTrace();
            } catch (NullPointerException e) {
                showError(e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    private void readBlocks(int blocksToRead, byte[] authenticationKey) {
        mifare = new MifareClassic();
        if ((mifare.getSectorCount()*4) < blocksToRead) {
            Toast.makeText(ContactLessActivity.this,"Quantidade de blocos não suportada",Toast.LENGTH_SHORT
            ).show();
            return;
        }
        byte[] cardId;
        byte[][] cardData = new byte[blocksToRead][MifareClassic.BLOCK_SIZE];

        if(switchAuthentication.isChecked()) {
            try {
                cardId = mifare.connect(3000);
                for (int blockIndex = 0; blockIndex < blocksToRead; blockIndex++) {
                    if (blockIndex % BLOCKS_IN_SECTOR == 0) {
                        int sector = mifare.blockToSector(blockIndex);
                        mifare.authenticateSectorWithKeyA(sector, authenticationKey);
                    }
                    cardData[blockIndex] = mifare.readBlock(blockIndex);
                }
                mifare.close();
                showCardData(cardId, cardData);
            } catch (ConnectionException e) {
                showError(e.getMessage());
                e.printStackTrace();
            }
            catch (AuthenticationException e) {
                showError(e.getMessage());
                e.printStackTrace();
            }
            catch (IOException e) {
                showError(e.getMessage());
                e.printStackTrace();
            } catch (NullPointerException e) {
                showError(e.getMessage());
                e.printStackTrace();
            }
        } else {
            try {
                cardId = mifare.connect(3000);
                for (int blockIndex = 0; blockIndex < blocksToRead; blockIndex++) {
                    if (blockIndex % BLOCKS_IN_SECTOR == 0) {
                        int sector = mifare.blockToSector(blockIndex);
                    }
                    cardData[blockIndex] = mifare.readBlock(blockIndex);
                }
                mifare.close();
                showCardData(cardId, cardData);
            } catch (ConnectionException e) {
                showError(e.getMessage());
                e.printStackTrace();
            }
            catch (IOException e) {
                showError(e.getMessage());
                e.printStackTrace();
            } catch (NullPointerException e) {
                showError(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void writeDataValue(int blockIndex, byte[] authenticationKey) {
        mifare = new MifareClassic();
        /*
         * Mifare classic cards require that memory blocks are configured as
         * 0    1    2    3    4    5    6    7    8    9    10   11   12   13    14    15    16
         * b0   b1   b2   b3   /b0  /b1  /b2  /b3  b0   b1   b2   b3   addr /addr addr  /addr addr
         *
         * b0-b3 represent the data to be saved at the block; addr stands for a memory address, general
         * purpose use; / stands for the complementary data of that given byte block.
         */
        byte[] dataValue = Utils.hexStringToByteArray("55555555AAAAAAAA5555555501FE01FE");

        try {
            mifare.connect(500);
            mifare.authenticateSectorWithKeyA(mifare.blockToSector(blockIndex), authenticationKey);
            mifare.writeBlock(blockIndex, dataValue);

            showBlockContent("Write data value finished!", blockIndex, mifare.readBlock(blockIndex));
            mifare.close();
        } catch (ConnectionException | AuthenticationException | IOException e) {
            showError(e.getMessage());
        }
    }

    private void showCardData(byte[] cardId, byte[][] cardData) {
        outputView.setText("Card id: " + Utils.byteArrayToHexString(cardId) + "\n");
        for (int blockIndex = 0; blockIndex < cardData.length; blockIndex++)
            outputView.append("Block [" + blockIndex + "] : " + Utils.byteArrayToHexString(cardData[blockIndex]) + "\n");

    }

    private void showBlockContent(int blockIndex, byte[] block) {
        outputView.setText("Block [" + blockIndex + "] : " + Utils.byteArrayToHexString(block));
    }

    private void showBlockContent(String message, int blockIndex, byte[] block) {
        outputView.setText(message + "\n");
        outputView.append("Block [" + blockIndex + "] : " + Utils.byteArrayToHexString(block));
    }

    private void showError(String message) {
        outputView.setText("Error : " + message);
    }

    private void showMessage(String message) {
        outputView.setText(message);
    }

    public void onSendCommand(View view) {

        GenericContactLess cardInterface = new GenericContactLess();

        // This command has been copied from Gedi_Cl test case (commandSendTest) from GEDI lib
        byte[] cmdData = {0x00, (byte) 0xa4, 0x04, 0x00, 0x0e, 0x32, 0x50, 0x41, 0x59, 0x2e, 0x53, 0x59, 0x53, 0x2e, 0x44, 0x44, 0x46, 0x30, 0x31, 0x00};
        byte[] cmdResp = null;

        try {
            cardInterface.connect(2000);

            //TODO validate expected 'cmdResp' response
            cmdResp = cardInterface.sendCommand(cmdData);
            showMessage("Send APDU successfully executed");

            cardInterface.close();
        } catch (ConnectionException | IOException e) {
            showError(e.getMessage());
            showAlertDialog("Comando APDU executado para verificar aplicações AID do " +
                    "diretório 2PAY.SYS.DDF01 de cartão EMV. Não se aplica para cartões MiFare.");
        }
    }

    public void onResetEMV(View view) {
        GenericContactLess cardInterface = new GenericContactLess();

        try {
            cardInterface.connect(2000);

            byte[] abATR;
            abATR = cardInterface.resetEMV();
            showMessage("ResetEMV\nabATR: " + Utils.byteArrayToHexString(abATR));

            cardInterface.close();
        } catch (ConnectionException | IOException e) {
            showError(e.getMessage());
        }
    }

    private void showAlertDialog(String message) {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(this).create();
        }

        if (alertDialog.isShowing() == false) { // Verifica se o dialog não está sendo exibido
            alertDialog.setTitle("SendAPDU Error");
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
}
