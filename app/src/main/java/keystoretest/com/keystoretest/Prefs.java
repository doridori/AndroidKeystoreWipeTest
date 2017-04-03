package keystoretest.com.keystoretest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    private final SharedPreferences sharedPreferences;
    private static final String KEY_ENC_REQUIRED = "KEY_ENC_REQUIRED";

    public Prefs(Context context) {
        sharedPreferences = context.getSharedPreferences("KEYSTORE_TEST.Prefs", Context.MODE_PRIVATE);
    }

    @SuppressLint("CommitPrefEdits")
    void setEncryptionRequired(boolean encryptionRequired) {
        sharedPreferences.edit().putBoolean(KEY_ENC_REQUIRED, encryptionRequired).commit();
    }

    boolean isEncryptionRequired() {
        return sharedPreferences.getBoolean(KEY_ENC_REQUIRED, false);
    }

}
