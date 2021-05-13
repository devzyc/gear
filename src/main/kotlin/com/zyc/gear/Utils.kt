@file:Suppress("unused")

package com.zyc.gear

import com.google.gson.GsonBuilder
import com.google.gson.internal.`$Gson$Types`
import com.google.gson.stream.JsonWriter
import com.zyc.gear.Reflections.setValue
import java.io.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.MalformedURLException
import java.net.URL
import java.util.*

/**
 * created by zeng_yong_chang@163.com
 */
object Utils {
  
  /** 逗号拼接  */
  @JvmOverloads
  fun list2Concatenated(list: List<String?>, hasOneSpace: Boolean = false): String {
    val sb = StringBuilder()
    for (i in 0 until list.size - 1) {
      sb.append(list[i])
      sb.append(",")
      if (hasOneSpace) {
        sb.append(" ")
      }
    }
    sb.append(list[list.size - 1])
    return sb.toString()
  }
  
  fun printJson(src: Any) {
    val writer: JsonWriter
    try {
      writer = JsonWriter(PrintWriter(System.out))
      writer.setIndent("  ")
      GsonBuilder().setPrettyPrinting().create().toJson(src, src.javaClass, writer)
      writer.close()
    } catch (e: IOException) {
      e.printStackTrace()
    }
  }
  
  fun replaceNullWithEmptyStr(input: Any) {
    Reflections.getAllFields(input.javaClass)
      .forEach { f ->
        f.isAccessible = true
        try {
          if (f.type.isAssignableFrom(String::class.java) && f.get(input) == null) {
            setValue(input, f.name, "")
          }
        } catch (e: IllegalAccessException) {
          e.printStackTrace()
        } catch (e: NoSuchFieldException) {
          e.printStackTrace()
        }
      }
  }
  
  fun classNameOf(ref: Any): String {
    return ref.javaClass.simpleName
  }
  
  fun discardToHourZero(c: Calendar) {
    c.set(Calendar.HOUR_OF_DAY, 0)
    c.set(Calendar.MINUTE, 0)
    c.set(Calendar.SECOND, 0)
    c.set(Calendar.MILLISECOND, 0)
  }
  
  fun getMonthAndDayStr(calendar: Calendar): String {
    return Times.format("MM月dd日", calendar.timeInMillis)
  }
  
  fun close(vararg closeables: Closeable?) {
    for (closeable in closeables) {
      if (closeable != null) {
        try {
          closeable.close()
        } catch (e: IOException) {
          e.printStackTrace()
        }
      }
    }
  }
  
  fun toTwoBitDigit(num: Int): String {
    return String.format("%02d", num)
  }
  
  /**
   * Caution: the parameter "total"(divisor) should not be 0 !
   */
  fun getPercent(num: Long, total: Long): Int {
    return (num.toFloat() / total * 100).toInt()
  }
  
  fun getProgressValue(totalBytes: Long, currentBytes: Long): Int {
    return if (totalBytes == -1L) {
      0
    } else (currentBytes * 100 / totalBytes).toInt()
  }
  
  /**
   * All field should be serializable. If field is not primitive, that class's field also should be
   * serializable.
   */
  fun sizeOf(`object`: Any?): Int {
    if (`object` == null) return 0
    val baos = ByteArrayOutputStream()
    try {
      val oos = ObjectOutputStream(baos)
      oos.writeObject(`object`)
      oos.flush()
      oos.close()
    } catch (e: IOException) {
      e.printStackTrace()
      return 0
    }
    val byteArray: ByteArray = baos.toByteArray()
    return byteArray.size * 6 / 5
  }
  
  fun getPathIfCould(input: String?): String? {
    var path = input
    try {
      path = URL(path).path
    } catch (e: MalformedURLException) {
      e.printStackTrace()
    }
    return path
  }
  
  fun cacheFileIsExistedAndFresh(cacheFile: File, staleMs: Int): Boolean {
    return cacheFile.exists() && System.currentTimeMillis() - cacheFile.lastModified() < staleMs
  }
  
  fun <T> typeFor(clazz: Class<T>?): Type {
    return `$Gson$Types`.newParameterizedTypeWithOwner(null, clazz)
  }
  
  fun typeFor(typeOfTemplate: Type?, typeOfTemplateArg: Type?): ParameterizedType {
    return `$Gson$Types`.newParameterizedTypeWithOwner(null, typeOfTemplate, typeOfTemplateArg)
  }
}