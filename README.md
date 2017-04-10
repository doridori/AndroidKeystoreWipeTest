# AndroidKeystoreWipeTest
Simple app to test android keystore wiping

Accompanies [a blog post I have written](http://systemdotrun.blogspot.co.uk/2015/02/android-security-forgetful-keystore.html)


#Steps to Test keystore behaviour

Note to switch lock type back to none, you'll have to remove device encryption, any user installed certificates i.e Charles Proxy root cert, and remove any device admins. Also once a keypair has been generated the system will not let you revert to NONE (regardless of if the KeyStore is encrypted or not) unless the app has been deleted / the pair removed.

1. Set device lock to none
2. Install this app
3. Generate Key
4. Verify key can be read (should show green bg)
5. Change System lock type - Note which lock type going from and to
6. Open app and record findings i.e T = the key can be read (green bg) or **F** if the key cannot be loaded (red bg).


Repeat steps 3 to 6 for switching between different device lock types.

Record them in using this markdown template, then you could send a PR or create issue with details on https://github.com/doridori/doridori.github.io
Some tests will be N/A for example you cannot create a keystore entry with `setEncryptionRequired()` if device lock is **None**. You can try but you IllegalStateException.


Blank table

| to ↓        from > | NONE | PIN | PASS | PATTERN |
|--------------------|------|-----|------|---------|
| NONE               |      |     |      |         |
| PIN                |      |     |      |         |
| PASS               |      |     |      |         |
| PATTERN            |      |     |      |         |


Note: Testing with `setEncryptionRequired()` requires the device have a pin/password therefore the None column will be N/A.   
