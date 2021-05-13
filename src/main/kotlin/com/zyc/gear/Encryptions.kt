@file:Suppress("unused")

package com.zyc.gear

import java.security.MessageDigest

fun String.md5Digest(): String? {
  // 定义数字签名方法, 可用：MD5, SHA-1
  try {
    val md = MessageDigest.getInstance("MD5")
    val b = md.digest(toByteArray(charset("utf-8")))
    return b.byte2HexStr()
  } catch (e: Exception) {
    e.printStackTrace()
  }
  return null
}

fun ByteArray.byte2HexStr(): String {
  val sb = StringBuilder()
  for (i in indices) {
    val s = Integer.toHexString(get(i).toInt() and 0xFF)
    if (s.length == 1) {
      sb.append("0")
    }
    sb.append(s.toUpperCase())
  }
  return sb.toString()
}