@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.zyc.gear

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * created by zeng_yong_chang@163.com
 */
object Times {
  fun format(pattern: String, milliseconds: Long): String {
    return format(pattern, Date(milliseconds))
  }
  
  fun format(pattern: String, date: Date): String {
    return SimpleDateFormat(pattern).format(date)
  }
  
  fun formatHms(pattern: String, millis: Long): String {
    val dateFormat = SimpleDateFormat(pattern)
    dateFormat.timeZone = TimeZone.getTimeZone("GMT+0:00")
    return dateFormat.format(Date(millis))
  }
  
  /**
   * 将以毫秒为单位的时间转化为“小时：分钟：秒”的格式（不足1小时的没有小时部分）。
   * 适用于显示一段视频的时长（有些视频时长是大于1小时，有些是不足1小时的）
   */
  fun formatHMS(millis: Long): String {
    val millisOfOneHour = TimeUnit.HOURS.toMillis(1)
    return if (millis < millisOfOneHour) {
      String.format("%1\$tM:%1\$tS", millis)
    } else {
      String.format("%1\$d:%2\$TM:%2\$TS", millis / millisOfOneHour, millis % millisOfOneHour)
    }
  }
  
  fun discardToHourZero(c: Calendar): Calendar {
    c[Calendar.HOUR_OF_DAY] = 0
    c[Calendar.MINUTE] = 0
    c[Calendar.SECOND] = 0
    c[Calendar.MILLISECOND] = 0
    return c
  }
  
  fun createRandomNowFile(parentDir: File, prefix: String?, suffix: String?): File? {
    return try {
      parentDir.mkdirs()
      var fileName = format("mm:ss", System.currentTimeMillis())
      if (prefix != null) {
        fileName = prefix + fileName
      }
      if (suffix != null) {
        fileName += ".$suffix"
      }
      val file = File(parentDir, fileName)
      file.createNewFile()
      file
    } catch (e: IOException) {
      e.printStackTrace()
      null
    }
  }
  
  fun formatDateTime(date: Date): String {
    val text: String
    val dateTime = date.time
    when {
      isSameDay(dateTime) -> {
        val calendar = GregorianCalendar.getInstance()
        when {
          inOneMinute(dateTime, calendar.timeInMillis) -> {
            return "刚刚"
          }
          inOneHour(dateTime, calendar.timeInMillis) -> {
            return String.format("%d分钟之前", abs(dateTime - calendar.timeInMillis) / 60000)
          }
          else -> {
            calendar.time = date
            val hourOfDay = calendar[Calendar.HOUR_OF_DAY]
            text = when {
              hourOfDay > 17 -> {
                "晚上 hh:mm"
              }
              hourOfDay in 0..6 -> {
                "凌晨 hh:mm"
              }
              hourOfDay in 12..17 -> {
                "下午 hh:mm"
              }
              else -> {
                "上午 hh:mm"
              }
            }
          }
        }
      }
      isYesterday(dateTime) -> {
        text = "昨天 HH:mm"
      }
      isSameYear(dateTime) -> {
        text = "M月d日 HH:mm"
      }
      else -> {
        text = "yyyy-M-d HH:mm"
      }
    }
    
    // 注意，如果使用android.text.format.DateFormat这个工具类，在API 17之前它只支持adEhkMmszy
    return SimpleDateFormat(text, Locale.CHINA).format(date)
  }
  
  private fun inOneMinute(time1: Long, time2: Long): Boolean {
    return abs(time1 - time2) < 60000
  }
  
  private fun inOneHour(time1: Long, time2: Long): Boolean {
    return abs(time1 - time2) < 3600000
  }
  
  private fun isSameDay(time: Long): Boolean {
    val startTime = floorDay(Calendar.getInstance()).timeInMillis
    val endTime = ceilDay(Calendar.getInstance()).timeInMillis
    return time in (startTime + 1) until endTime
  }
  
  private fun isYesterday(time: Long): Boolean {
    val startCal: Calendar = floorDay(Calendar.getInstance())
    startCal.add(Calendar.DAY_OF_MONTH, -1)
    val startTime = startCal.timeInMillis
    val endCal: Calendar = ceilDay(Calendar.getInstance())
    endCal.add(Calendar.DAY_OF_MONTH, -1)
    val endTime = endCal.timeInMillis
    return time in (startTime + 1) until endTime
  }
  
  private fun isSameYear(time: Long): Boolean {
    val startCal: Calendar = floorDay(Calendar.getInstance())
    startCal[Calendar.MONTH] = Calendar.JANUARY
    startCal[Calendar.DAY_OF_MONTH] = 1
    return time >= startCal.timeInMillis
  }
  
  private fun floorDay(startCal: Calendar): Calendar {
    startCal[Calendar.HOUR_OF_DAY] = 0
    startCal[Calendar.MINUTE] = 0
    startCal[Calendar.SECOND] = 0
    startCal[Calendar.MILLISECOND] = 0
    return startCal
  }
  
  private fun ceilDay(endCal: Calendar): Calendar {
    endCal[Calendar.HOUR_OF_DAY] = 23
    endCal[Calendar.MINUTE] = 59
    endCal[Calendar.SECOND] = 59
    endCal[Calendar.MILLISECOND] = 999
    return endCal
  }
}