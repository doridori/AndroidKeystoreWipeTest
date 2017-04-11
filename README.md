# AndroidKeystoreWipeTest
Simple app to test android keystore wiping

Accompanies [a blog post I have written](http://systemdotrun.blogspot.co.uk/2015/02/android-security-forgetful-keystore.html)


## Steps to Test keystore behaviour

1. Set device lock to none
2. Install this app
3. Generate Key
4. Verify key can be read (should show green bg)
5. Change System lock type - Note which lock type going from and to
6. Open app and record findings i.e T = the key can be read (green bg) or **F** if the key cannot be loaded (red bg).


Repeat steps 3 to 6 switching between different device lock types. Record them in using the markdown template below.

Send a PR or create issue with details on https://github.com/doridori/doridori.github.io so that the test results can displayed on the blog post.


## Blank table

| to ↓        from > | NONE | PIN | PASS | PATTERN |
|--------------------|------|-----|------|---------|
| NONE               |      |     |      |         |
| PIN                |      |     |      |         |
| PASS               |      |     |      |         |
| PATTERN            |      |     |      |         |



## Testing with `setEncryptionRequired()`.

* Requires the device have a pin/password/pattern to be set. If you attempt to save a key with none as device lock an `llegalStateException` will be thrown. Therefore the NONE column will be N/A when testing with `setEncryptionRequired()`..   
* Recommend uninstalling/resinstalling the app when switching between testing with/without `setEncryptionRequired()`. 