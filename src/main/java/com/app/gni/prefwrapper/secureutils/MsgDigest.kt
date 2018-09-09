package com.app.gni.prefwrapper.secureutils

import java.security.MessageDigest

/**
 * Created by rajinikanthm on 9/9/18.
 */
object MsgDigest {

    private val ALGO = "SHA-256"
    private val DEFAULT_CHARSET = "UTF-8"
    private val STRING_FORMAT = "%02X"

    fun getHashString(txt: String?): String {
        val msgDigest = MessageDigest.getInstance(ALGO)
        val result = msgDigest.digest(txt!!.toByteArray(charset(DEFAULT_CHARSET)))
        return toHex(result)
    }

    private fun toHex(data: ByteArray): String {
        val sb = StringBuilder()
        for (b in data) {
            sb.append(String.format(STRING_FORMAT, b))
        }
        return sb.toString()
    }

}