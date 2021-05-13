@file:Suppress("unused")

package com.zyc.gear

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Md5s {
  private var hexDigits = charArrayOf(
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
  )
  private var digest: MessageDigest? = null
  
  /**
   * 生成字符串的md5校验值
   */
  fun newMd5String(s: String): String {
    return newMd5String(s.toByteArray())
  }
  
  private fun newMd5String(bytes: ByteArray?): String {
    return if (digest != null) {
      digest!!.update(bytes)
      bufferToHex(digest!!.digest())
    } else {
      ""
    }
  }
  
  /**
   * 生成文件的md5校验值
   */
  fun newFileMd5String(file: File): String {
    var fis: InputStream? = null
    try {
      fis = FileInputStream(file)
      val buffer = ByteArray(1024)
      var numRead: Int
      while (fis.read(buffer).also { numRead = it } > 0) {
        digest!!.update(buffer, 0, numRead)
      }
      fis.close()
    } catch (e: Exception) {
      return ""
    } finally {
      if (fis != null) {
        try {
          fis.close()
        } catch (e: IOException) {
          e.printStackTrace()
        }
      }
    }
    return if (digest != null) {
      bufferToHex(digest!!.digest())
    } else {
      ""
    }
  }
  
  private fun bufferToHex(bytes: ByteArray, m: Int = 0, n: Int = bytes.size): String {
    val buffer = StringBuffer(2 * n)
    val k = m + n
    for (l in m until k) {
      appendHexPair(bytes[l], buffer)
    }
    return buffer.toString()
  }
  
  private fun appendHexPair(bt: Byte, buffer: StringBuffer) {
    val c0 = hexDigits[bt.toInt() and 0xf0 shr 4] // 取字节中高 4 位的数字转换, >>>
    // 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
    val c1 = hexDigits[bt.toInt() and 0xf] // 取字节中低 4 位的数字转换
    buffer.append(c0)
    buffer.append(c1)
  }
  
  init {
    try {
      digest = MessageDigest.getInstance("MD5")
    } catch (e: NoSuchAlgorithmException) {
      System.err.println(Md5s::class.java.name + "初始化失败，MessageDigest不支持MD5Util。")
      e.printStackTrace()
    }
  }
}