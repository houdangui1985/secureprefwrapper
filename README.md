# secureprefwrapper
Android shared preferences secure wrapper with AES-256 encryption algorithm

Encryption
While delaing with sensitive data the only secured way is to use encryption to protect the data. 
And then we can decrypt the stored data to get the actual data whenever we need it.
AES-256 can be a moderately good choice of an encryption algorithm.
But In order to save the encryption key we need safe and strong place so that the key is more secure.

About Android KeyStore
The Android Keystore system lets you store cryptographic keys in a container to make it more difficult to extract from the device. Once keys are in the keystore, they can be used for cryptographic operations with the key material remaining non-exportable. Moreover, it offers facilities to restrict when and how keys can be used, such as requiring user authentication for key use or restricting keys to be used only in certain cryptographic modes. See Security Features section for more information.

To support low-power StrongBox implementations, a subset of algorithms and key sizes are supported:
RSA 2048
AES 128 and 256
ECDSA P-256
HMAC-SHA256 (supports key sizes between 8 bytes and 64 bytes, inclusive)
Triple DES 168

Usage:
Add this as a library for the project

Init Secureprefrences
@this - app context
@alias - name of the store in string format 
 ...............................................
 SecurePreferences.init(this,alias) 
 ...............................................
 
 Pref Editor
  
 val editor = SecurePreferences.edit()
 editor.putString("key","value")
 
 Retrive data
 val value =  SecurePreferences.getString(key,defaultvalue)
 
 
 
 
 
 
