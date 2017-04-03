package keystoretest.com.keystoretest;


import android.content.Context;
import android.security.KeyPairGeneratorSpec;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

/**
 * Wraps {@link javax.crypto.SecretKey} instances using a public/private key pair stored in
 * the platform {@link java.security.KeyStore}. This allows us to protect symmetric keys with
 * hardware-backed crypto, if provided by the device.
 * <p>
 * See <a href="http://en.wikipedia.org/wiki/Key_Wrap">key wrapping</a> for more
 * details.
 * <p>
 * Not inherently thread safe.
 */
public class SecretKeyWrapper {

    private final KeyPair mPair;
    private final Cipher mCipher;

    /**
     * Create a wrapper using the public/private key pair with the given alias.
     * If no pair with that alias exists, it will be generated.
     */
    public SecretKeyWrapper(Context context, String alias, boolean encryptionRequired) throws GeneralSecurityException, IOException {
        mCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        final KeyStore keyStore = getKeystore();

        if (!keyStore.containsAlias(alias)) {
            generateKeyPair(context, alias, encryptionRequired);
        }

        // Even if we just generated the key, always read it back to ensure we can read it successfully.
        final KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
        mPair = new KeyPair(entry.getCertificate().getPublicKey(), entry.getPrivateKey());
    }

    /**
     * Quick check method to see if the alias still exists in the keystore
     *
     * @return
     */
    public static boolean isAliasInKeystore(String alias) throws GeneralSecurityException {
        final KeyStore keyStore = getKeystore();
        return keyStore.containsAlias(alias);
    }

    public static void deleteAlias(String alias) throws GeneralSecurityException {
        final KeyStore keyStore = getKeystore();
        boolean aliasExists = keyStore.containsAlias(alias);
        keyStore.deleteEntry(alias);
        boolean aliasExitsAfterDelete = keyStore.containsAlias(alias);
    }

    private static KeyStore getKeystore() throws GeneralSecurityException {
        try {
            final KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            return keyStore;
        } catch (KeyStoreException e) {
            e.printStackTrace();
            throw new GeneralSecurityException(e);
        } catch (CertificateException e) {
            e.printStackTrace();
            throw new GeneralSecurityException(e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new GeneralSecurityException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new GeneralSecurityException(e);
        }
    }

    /**
     * @param context
     * @param alias
     * @param encryptionRequired
     * @throws java.security.GeneralSecurityException
     */
    private static void generateKeyPair(Context context, String alias, boolean encryptionRequired) throws GeneralSecurityException {
        final Calendar start = new GregorianCalendar();
        final Calendar end = new GregorianCalendar();
        end.add(Calendar.YEAR, 100);


        KeyPairGeneratorSpec.Builder kpBuilder = new KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias)
                .setSubject(new X500Principal("CN=" + alias))
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(start.getTime())
                .setEndDate(end.getTime());

        if (encryptionRequired) {
            kpBuilder.setEncryptionRequired();
        }

        final KeyPairGeneratorSpec spec = kpBuilder.build();

        final KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
        gen.initialize(spec);
        gen.generateKeyPair();

    }

    /**
     * Wrap a {@link javax.crypto.SecretKey} using the public key assigned to this wrapper.
     * Use {@link #unwrap(byte[])} to later recover the original
     * {@link javax.crypto.SecretKey}.
     *
     * @return a wrapped version of the given {@link javax.crypto.SecretKey} that can be
     * safely stored on untrusted storage.
     */
    public byte[] wrap(SecretKey key) throws GeneralSecurityException {
        mCipher.init(Cipher.WRAP_MODE, mPair.getPublic());
        return mCipher.wrap(key);
    }

    /**
     * Unwrap a {@link javax.crypto.SecretKey} using the private key assigned to this
     * wrapper.
     *
     * @param blob a wrapped {@link javax.crypto.SecretKey} as previously returned by
     *             {@link #wrap(javax.crypto.SecretKey)}.
     */
    public SecretKey unwrap(byte[] blob) throws GeneralSecurityException {
        mCipher.init(Cipher.UNWRAP_MODE, mPair.getPrivate());
        return (SecretKey) mCipher.unwrap(blob, "AES", Cipher.SECRET_KEY);
    }

}