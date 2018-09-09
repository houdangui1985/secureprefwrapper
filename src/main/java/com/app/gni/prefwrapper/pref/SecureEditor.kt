package com.app.gni.prefwrapper.pref

import android.content.SharedPreferences
import com.app.gni.prefwrapper.crypto.CryptoManager
import com.app.gni.prefwrapper.secureutils.MsgDigest

/**
 * Created by rajinikanthm on 9/9/18.
 */
class SecureEditor(mPrefs: SharedPreferences?, cryptoManager: CryptoManager?) : SharedPreferences.Editor {

    private var mEditor: SharedPreferences.Editor? = null
    private var mPref: SharedPreferences? = null
    private var cryptoManager: CryptoManager? = null

    init {
        this.mPref = mPref
        this.mEditor = mPrefs!!.edit()
        this.cryptoManager = cryptoManager

    }

    override fun putString(key: String?, value: String?): SharedPreferences.Editor? {
        try {
            val key = MsgDigest.getHashString(key)
            val encryptedValue = cryptoManager!!.encryptText(value!!)
            mEditor!!.putString(key, encryptedValue!!)
        } catch (e: Exception) {
            return null
        }
        return this
    }

    override fun clear(): SharedPreferences.Editor {
        for (key in mPref!!.all.keys) {
            mEditor!!.remove(key)
        }
        return this
    }

    override fun putLong(p0: String?, p1: Long): SharedPreferences.Editor {
        val longInString = java.lang.Long.toString(p1)
        return putString(p0, longInString)!!
    }

    override fun putInt(p0: String?, p1: Int): SharedPreferences.Editor {
        val intInString = Integer.toString(p1)
        return putString(p0, intInString)!!
    }

    override fun remove(p0: String?): SharedPreferences.Editor? {
        try {
            val hashedKey = MsgDigest.getHashString(p0)
            mEditor!!.remove(hashedKey)
        } catch (e: Exception) {
            return null
        }
        return this
    }

    override fun putBoolean(p0: String?, p1: Boolean): SharedPreferences.Editor {
        val boolInString = java.lang.Boolean.toString(p1)
        return putString(p0, boolInString)!!
    }

    override fun putStringSet(p0: String?, p1: MutableSet<String>?): SharedPreferences.Editor? {
        try {
            val hashedKey = MsgDigest.getHashString(p0)
            val eSet = HashSet<String>(p1!!.size)
            p1.mapTo(eSet) { cryptoManager!!.encryptText(it) }
            mEditor!!.putStringSet(hashedKey, eSet)
        } catch (e: Exception) {
            return null
        }
        return this
    }

    override fun commit(): Boolean {
        return mEditor!!.commit()
    }

    override fun putFloat(p0: String?, p1: Float): SharedPreferences.Editor {
        val floatInString = java.lang.Float.toString(p1)
        return putString(p0, floatInString)!!
    }

    override fun apply() {
        mEditor!!.apply()
    }
}