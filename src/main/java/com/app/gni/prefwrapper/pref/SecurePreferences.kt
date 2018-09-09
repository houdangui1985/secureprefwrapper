package com.app.gni.prefwrapper.pref

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.app.gni.prefwrapper.crypto.CryptoManager
import com.app.gni.prefwrapper.secureutils.MsgDigest
import java.io.IOException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.UnrecoverableEntryException
import java.security.cert.CertificateException
import javax.crypto.NoSuchPaddingException

/**
 * Created by rajinikanthm on 9/9/18.
 */
object SecurePreferences : SharedPreferences {

    private var mPrefs: SharedPreferences? = null
    private val DEFAULT_PREF_FILE_NAME = "Secure_Pref"
    private var cryptoManager: CryptoManager? = null

    private val defaultValue = -1

    @Throws(IOException::class, CertificateException::class, NoSuchAlgorithmException::class, KeyStoreException::class, UnrecoverableEntryException::class, InvalidAlgorithmParameterException::class, NoSuchPaddingException::class, InvalidKeyException::class, NoSuchProviderException::class)
    fun init(appContext: Context, name: String) {
        val key = MsgDigest.getHashString(name)
        val alias = CryptoManager.Alias(key)
        mPrefs = appContext.getSharedPreferences(DEFAULT_PREF_FILE_NAME, Context.MODE_PRIVATE)
        cryptoManager = CryptoManager(alias)
    }

    override fun contains(p0: String?): Boolean {
        val hashedKey = MsgDigest.getHashString(p0)
        return mPrefs!!.contains(hashedKey)
    }

    override fun getBoolean(p0: String?, p1: Boolean): Boolean {
        val value = getString(p0, null)
        if (value != null) {
            return java.lang.Boolean.parseBoolean(value)
        }
        return false
    }

    override fun unregisterOnSharedPreferenceChangeListener(p0: SharedPreferences.OnSharedPreferenceChangeListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerOnSharedPreferenceChangeListener(p0: SharedPreferences.OnSharedPreferenceChangeListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInt(p0: String?, p1: Int): Int {
        val value = getString(p0, "")
        if (value != "") {
            return Integer.parseInt(value)
        }
        return defaultValue
    }

    override fun getAll(): MutableMap<String, *> {
        val all = mPrefs!!.all
        val dAll = HashMap<String, Any>(all.size)

        if (all.isNotEmpty()) {
            for (key in all.keys) {
                try {
                    val value = all[key]
                    dAll.put(key, cryptoManager!!.decryptData(value as String, cryptoManager!!.getIv()))
                } catch (e: Exception) {
                    //TOdO:Logger
                }
            }
        }
        return dAll
    }

    override fun edit(): SharedPreferences.Editor {
        return SecureEditor(mPrefs, cryptoManager!!)
    }

    override fun getLong(p0: String?, p1: Long): Long {
        val value = getString(p0, "")
        if (value != "") {
            return java.lang.Long.parseLong(value)
        }
        return defaultValue.toLong()
    }

    override fun getFloat(p0: String?, p1: Float): Float {
        val value = getString(p0, "")
        if (value != "") {
            return java.lang.Float.parseFloat(value)
        }
        return defaultValue.toFloat()
    }

    override fun getStringSet(p0: String?, valueSet: MutableSet<String>?): MutableSet<String> {

        try {
            val hashedKey = MsgDigest.getHashString(p0)
            val eSet = mPrefs!!.getStringSet(hashedKey, null)
            if (eSet != null) {
                val dSet = HashSet<String>(eSet.size)
                eSet.mapTo(dSet) { cryptoManager!!.decryptData(it, cryptoManager!!.getIv()) }
                return dSet
            }
        } catch (e: Exception) {
            //TODO:TODO: Callback or Logger needs to add
        }
        return mutableSetOf()
    }


    override fun getString(p0: String?, p1: String?): String {
        try {
            val hashedKey = MsgDigest.getHashString(p0)
            val value = mPrefs!!.getString(hashedKey, p1)
            Log.d("TAG", "Encrypted data:: $value")
            if (value != null)
                return cryptoManager!!.decryptData(value, cryptoManager!!.getIv())
        } catch (e: Exception) {
            //return p1!! // TODO: Callback or Logger needs to add
        }
        return p1!!
    }
}