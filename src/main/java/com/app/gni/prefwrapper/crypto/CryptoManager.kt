package com.app.gni.prefwrapper.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.NonNull
import android.util.Base64
import android.util.Log
import java.io.IOException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.SignatureException
import java.security.UnrecoverableEntryException
import java.security.cert.CertificateException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Created by rajinikanthm on 9/9/18.
 */

class CryptoManager(keyStoreAlias: Alias?) {

    private val TRANSFORMATION = "AES/GCM/NoPadding"
    private var encryption: ByteArray? = null
    private var iv: ByteArray? = null
    private var keyStore: KeyStore? = null
    private var keyStoreAlias: Alias? = null

    init {
        loadKeyStore()
        this.keyStoreAlias = keyStoreAlias
    }

    @Throws(KeyStoreException::class, CertificateException::class, NoSuchAlgorithmException::class, IOException::class)
    fun loadKeyStore() {
        when (keyStore) {
            null -> {
                keyStore = KeyStore.getInstance(CryptoUtils.KEYSTORE_PROVIDER)
                keyStore!!.load(null)
            }
        }
    }

    fun base64Encode(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.NO_WRAP)
    }

    fun base64Decode(text: String): ByteArray {
        return Base64.decode(text, Base64.NO_WRAP)
    }

    @Throws(UnrecoverableEntryException::class, NoSuchAlgorithmException::class, KeyStoreException::class, NoSuchProviderException::class, NoSuchPaddingException::class, InvalidKeyException::class, IOException::class, InvalidAlgorithmParameterException::class, SignatureException::class, BadPaddingException::class, IllegalBlockSizeException::class)
    fun encryptText(textToEncrypt: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        iv = cipher.iv
        encryption = cipher.doFinal(textToEncrypt.toByteArray())
        return base64Encode(encryption!!)
    }

    @Throws(UnrecoverableEntryException::class, NoSuchAlgorithmException::class, KeyStoreException::class, NoSuchProviderException::class, NoSuchPaddingException::class, InvalidKeyException::class, IOException::class, BadPaddingException::class, IllegalBlockSizeException::class, InvalidAlgorithmParameterException::class)
    fun decryptData(encryptedData: String, encryptionIv: ByteArray): String {

        val byteArray = base64Decode(encryptedData)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(128, encryptionIv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
        val a = kotlin.text.String(cipher.doFinal(byteArray))
        Log.d("TAG", "value $a")
        return a
    }


    @NonNull
    @Throws(NoSuchAlgorithmException::class, NoSuchProviderException::class, InvalidAlgorithmParameterException::class, KeyStoreException::class, UnrecoverableEntryException::class)
    private fun getSecretKey(): SecretKey {
        val alias = keyStoreAlias!!.name
        val key = when {
            keyStore!!.containsAlias(alias) && keyStore!!.entryInstanceOf(alias, KeyStore.SecretKeyEntry::class.java) -> {
                val entry = keyStore!!.getEntry(alias, null) as KeyStore.SecretKeyEntry
                entry.secretKey
            }
            else -> {
                val keyGenerator = KeyGenerator
                        .getInstance(KeyProperties.KEY_ALGORITHM_AES, CryptoUtils.KEYSTORE_PROVIDER)
                keyGenerator.init(KeyGenParameterSpec.Builder(alias,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build())
                keyGenerator.generateKey()
            }
        }
        return key
    }

    fun getIv(): ByteArray {
        return iv!!
    }

    data class Alias(val name: String)

}