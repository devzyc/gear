@file:Suppress("MemberVisibilityCanBePrivate", "DuplicatedCode", "unused")

package com.zyc.gear

import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import java.io.*

/**
 * created by zeng_yong_chang@163.com
 */
object Tars {
  private const val BUFFER = 1024
  private var sCompressSrcParentPath = ""
  
  /**
   * usage: compress("/mnt/sdcard/Download", "/mnt/sdcard/Download.tar");
   *
   * @param srcAbsolutePath can be path of file or directory
   * @param destAbsolutePath suggest ending with a ".tar" suffix
   */
  fun compress(srcAbsolutePath: String, destAbsolutePath: String) {
    compress(File(srcAbsolutePath), destAbsolutePath)
  }
  
  /**
   * @param srcAbsolutePath can be path of file or directory,
   * suggest file's path ending with a ".tar" suffix
   */
  fun compress(srcAbsolutePath: String, destFile: File) {
    compress(File(srcAbsolutePath), destFile)
  }
  
  /**
   * @param srcFile can be file or directory
   * @param destAbsolutePath suggest ending with a ".tar" suffix
   */
  fun compress(srcFile: File, destAbsolutePath: String) {
    val destFile = File(destAbsolutePath)
    try {
      if (!destFile.exists()) {
        destFile.createNewFile()
      }
      compress(srcFile, destFile)
    } catch (e: IOException) {
      e.printStackTrace()
    }
  }
  
  /**
   * @param srcFile can be file or directory,
   * suggest file's path ending with a ".tar" suffix
   */
  fun compress(srcFile: File, destFile: File) {
    try {
      val taos = TarArchiveOutputStream(FileOutputStream(destFile))
      sCompressSrcParentPath = srcFile.parent
      compress(srcFile, taos)
      taos.flush()
      taos.close()
    } catch (e: IOException) {
      e.printStackTrace()
    }
  }
  
  private fun compress(file: File, taos: TarArchiveOutputStream) {
    if (file.isDirectory) {
      compressDir(file, taos)
    } else {
      compressFile(file, taos)
    }
  }
  
  private fun compressDir(dir: File, taos: TarArchiveOutputStream) {
    val files = dir.listFiles()
    if (files != null) {
      if (files.isEmpty()) {
        try {
          taos.putArchiveEntry(createTarArchiveEntry(dir))
          taos.closeArchiveEntry()
        } catch (e: IOException) {
          e.printStackTrace()
        }
      } else {
        for (file in files) {
          compress(file, taos)
        }
      }
    }
  }
  
  private fun createTarArchiveEntry(file: File): TarArchiveEntry {
    /** 不能用绝对路径，举例对于压缩/mnt/sdcard下的abc文件，如果不做截短，压缩文件里面会在abc外面套mnt和sdcard两层空壳，所以要把abc所属路径的/mnt/sdcard去掉  */
    return TarArchiveEntry(file, file.path.replace(sCompressSrcParentPath, ""))
  }
  
  private fun compressFile(file: File, taos: TarArchiveOutputStream) {
    try {
      val bis = BufferedInputStream(FileInputStream(file))
      taos.putArchiveEntry(createTarArchiveEntry(file))
      var count: Int
      val data = ByteArray(BUFFER)
      while (bis.read(data, 0, BUFFER).also { count = it } != -1) {
        taos.write(data, 0, count)
      }
      bis.close()
      taos.closeArchiveEntry()
    } catch (e: IOException) {
      e.printStackTrace()
    }
  }
  
  /**
   * usage: extract("/mnt/sdcard/Download.tar", "/mnt/sdcard") *
   */
  fun extract(srcPath: String, destPath: String) {
    val srcFile = File(srcPath)
    if (srcFile.isFile) {
      extract(srcFile, destPath)
    }
  }
  
  fun extract(srcFile: File, destPath: String) {
    extract(srcFile, File(destPath))
  }
  
  fun extract(srcFile: File, destFile: File) {
    try {
      val tais = TarArchiveInputStream(FileInputStream(srcFile))
      extract(destFile, tais)
      tais.close()
    } catch (e: IOException) {
      e.printStackTrace()
    }
  }
  
  private fun extract(destFile: File, tais: TarArchiveInputStream) {
    var entry: TarArchiveEntry
    try {
      while (tais.nextTarEntry.also { entry = it } != null) {
        val dirFile = File(destFile.path + File.separator + entry.name)
        createParentDirRecursively(dirFile)
        if (entry.isDirectory) {
          dirFile.mkdirs()
        } else {
          extractFile(dirFile, tais)
        }
      }
    } catch (e: IOException) {
      e.printStackTrace()
    }
  }
  
  private fun extractFile(destFile: File, tais: TarArchiveInputStream) {
    val bos: BufferedOutputStream
    try {
      bos = BufferedOutputStream(FileOutputStream(destFile))
      var count: Int
      val data = ByteArray(BUFFER)
      while (tais.read(data, 0, BUFFER).also { count = it } != -1) {
        bos.write(data, 0, count)
      }
      bos.close()
    } catch (e: IOException) {
      e.printStackTrace()
    }
  }
  
  private fun createParentDirRecursively(dirFile: File) {
    val parentFile = dirFile.parentFile
    if (!parentFile.exists()) {
      createParentDirRecursively(parentFile)
      parentFile.mkdir()
    }
  }
}