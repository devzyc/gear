@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.zyc.gear

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/** @author zeng_yong_chang@163.com
 */
object ZipUtil {
  fun unZip(srcPath: String, destPath: String) {
    unZip(File(srcPath), File(destPath))
  }
  
  fun unZip(srcFile: File, destFile: File) {
    val bufferSize = 4096
    var bos: BufferedOutputStream
    try {
      val fisForCount = FileInputStream(srcFile)
      val zisForCount = ZipInputStream(BufferedInputStream(fisForCount))
      var totalEntryCnt = 0
      while (zisForCount.nextEntry != null) {
        totalEntryCnt++
      }
      zisForCount.close()
      val fis = FileInputStream(srcFile)
      val zis = ZipInputStream(BufferedInputStream(fis))
      var currentEntryCnt = 0
      var entry: ZipEntry? = zis.nextEntry
      while (entry != null) {
        val entryFile = File(destFile, entry.name)
        val entryDir = entryFile.parentFile
        if (!entryDir.exists()) {
          entryDir.mkdirs()
        }
        if (!entry.isDirectory) {
          bos = BufferedOutputStream(FileOutputStream(entryFile), bufferSize)
          var numRead: Int
          val data = ByteArray(bufferSize)
          while (zis.read(data, 0, bufferSize).also { numRead = it } != -1) {
            bos.write(data, 0, numRead)
          }
          bos.flush()
          bos.close()
        }
        currentEntryCnt++
        println("unzipped entry: $entry")
        println("unzip percentage = " + currentEntryCnt.toFloat() * 100 / totalEntryCnt + "%")
        entry = zis.nextEntry
      }
      zis.close()
    } catch (e: IOException) {
      e.printStackTrace()
    }
  }
  
  /**
   * from "ssdy"
   */
  @Throws(Exception::class)
  fun zipFolder(srcFolderPath: String, destPath: String) {
    val srcFolder = File(srcFolderPath)
    if (!srcFolder.isDirectory) {
      return
    }
    val destFile = File(destPath)
    val parentDir = destFile.parentFile
    if (!parentDir.exists()) {
      parentDir.mkdirs()
    }
    val zos = ZipOutputStream(FileOutputStream(destFile))
    val children = srcFolder.listFiles()
    if (!children.isNullOrEmpty()) {
      for (f in children) {
        val bis = BufferedInputStream(FileInputStream(f))
        zos.putNextEntry(ZipEntry(srcFolder.name + File.separator + f.name))
        var length: Int
        val buffer = ByteArray(8192)
        while (bis.read(buffer).also { length = it } != -1) {
          zos.write(buffer, 0, length)
        }
        bis.close()
      }
    }
    zos.close()
  }
}