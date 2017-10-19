package keystoretest.com.keystoretest;

import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class MainActivity extends AppCompatActivity {
    public static final String ALIAS = "TestKeyAlias";

    private TextView status;
    private CheckBox encryptionRequiredCheckBox;
    boolean encryptionRequired;
    private Prefs prefs;
    private SecretKeyWrapper secretKeyWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = new Prefs(getApplicationContext());
        initViews();
        loadEncryptionRequired();
    }

    private void loadEncryptionRequired() {
        encryptionRequired = prefs.isEncryptionRequired();
        encryptionRequiredCheckBox.setChecked(encryptionRequired);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateStatus("start", true);
        checkIfAliasExists();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        updateStatus("stop", false);
        System.exit(0);
    }

    private void checkIfAliasExists() {
        try {
            boolean aliasInKeyStore = SecretKeyWrapper.isAliasInKeystore(ALIAS);
            updateStatus("Alias in keystore:" + aliasInKeyStore, aliasInKeyStore);

            if (aliasInKeyStore) {
                //test key can be read
                new SecretKeyWrapper(this, ALIAS, encryptionRequiredCheckBox.isChecked());
                updateStatus("Key can be read EncryptionReq:" + encryptionRequiredCheckBox.isChecked(), true);
            }
        } catch (GeneralSecurityException e) {
            updateStatus(e.getMessage(), false);
            e.printStackTrace();
        } catch (IOException e) {
            updateStatus(e.getMessage(), false);
            e.printStackTrace();
        }
    }

    public void generateClicked(View view) {
        try {
            secretKeyWrapper = new SecretKeyWrapper(this, ALIAS, encryptionRequiredCheckBox.isChecked());
            updateStatus("Pair generated with alias! EncryptionReq:" + encryptionRequiredCheckBox.isChecked(), true);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            updateStatus(e.getMessage(), false);
        } catch (IOException e) {
            e.printStackTrace();
            updateStatus(e.getMessage(), false);
        }
    }

    public void deleteKeyClicked(View view) {
        try {
            if (!secretKeyWrapper.deleteAlias(ALIAS)) {
                updateStatus("Alias deleted", false);
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            updateStatus(e.getMessage(), false);
        }
    }

    private void updateStatus(String string, boolean good) {
        status.setText(status.getText() + "\n" + string);
        setStatusColor(good ? Color.GREEN : Color.RED);
    }

    private void setStatusColor(int color) {
        status.setBackgroundColor(color);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_settings == item.getItemId()) {
            openSettings();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void printDeviceDetails() {
        TextView deviceInfo = (TextView) findViewById(R.id.deviceInfo);

        StringBuilder b = new StringBuilder();
        b.append("DEVICE: ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL).append("\n");
        b.append("OS: ").append(Build.VERSION.RELEASE).append("-").append(Build.VERSION.SDK_INT);

        deviceInfo.setText(b.toString());
    }

    private void initViews() {
        status = (TextView) findViewById(R.id.status);
        encryptionRequiredCheckBox = (CheckBox) findViewById(R.id.generateEncryptedChk);
        encryptionRequiredCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.setEncryptionRequired(isChecked);
            }
        });
        printDeviceDetails();
    }


    public void openSettings() {
        Intent intent =
                new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
        startActivity(intent);
    }

}
