@file:Suppress("unused")

package com.zyc.gear

import java.io.*

/**
 * Caller can be path of file or directory.
 */
fun String.fileSize(): Long {
  return File(this).size()
}

/**
 * Caller can be file or directory.
 */
fun File.size(): Long {
  return if (exists()) {
    if (isDirectory) {
      var size: Long = 0
      val children = listFiles()
      if (!children.isNullOrEmpty()) {
        for (subFile in children) {
          size += subFile.size()
        }
      }
      size
    } else {
      length()
    }
  } else {
    throw IllegalArgumentException("File does not exist!")
  }
}

@Throws(IOException::class)
fun copyFile(originalFilePath: String, destFilePath: String) {
  copyFile(File(originalFilePath), destFilePath)
}

@Throws(IOException::class)
fun copyFile(originalFilePath: String, destFile: File) {
  copyFile(File(originalFilePath), destFile)
}

@Throws(IOException::class)
fun copyFile(originalFile: File, destFilePath: String) {
  copyFile(originalFile, File(destFilePath))
}

@Throws(IOException::class)
fun copyFile(originalFile: File, destFile: File) {
  copy(FileInputStream(originalFile), FileOutputStream(destFile))
}

@Throws(IOException::class)
fun copy(inputStream: InputStream, outputStream: OutputStream) {
  val buf = ByteArray(1024)
  var numRead: Int
  while (inputStream.read(buf).also { numRead = it } != -1) {
    outputStream.write(buf, 0, numRead)
  }
  Utils.close(outputStream, inputStream)
}

/**
 * Caller can be file's absolute path or directories' path.
 */
fun String.deleteFile() {
  File(this).deleteFile()
}

/**
 * Caller can be file or directory.
 */
fun File.deleteFile() {
  if (!exists()) {
    println("The file to be deleted does not exist! File's path is: $path")
  } else {
    deleteFileRecursively(this)
  }
}

/**
 * Invoker must ensure that the file to be deleted exists.
 */
private fun deleteFileRecursively(file: File) {
  if (file.isDirectory) {
    val children = file.listFiles()
    if (!children.isNullOrEmpty()) {
      for (item in children) {
        if (item.isDirectory) {
          deleteFileRecursively(item)
        } else {
          if (!item.delete()) {
            println("Failed in recursively deleting a file, file's path is: " + item.path)
          }
        }
      }
    }
    if (!file.delete()) {
      println("Failed in recursively deleting a directory, directories' path is: " + file.path)
    }
  } else {
    if (!file.delete()) {
      println("Failed in deleting this file, its path is: " + file.path)
    }
  }
}

@Throws(IOException::class)
fun readToString(`in`: Reader): String {
  val reader = BufferedReader(`in`)
  val buffer = StringBuffer()
  var readLine: String
  while (reader.readLine().also { readLine = it } != null) {
    if (readLine.startsWith("#")) {
      continue
    }
    buffer.append(readLine)
    buffer.append("\n")
  }
  reader.close()
  return buffer.toString()
}

@Throws(IOException::class)
fun String.readToString(): String {
  return File(this).readToString()
}

@Throws(IOException::class)
fun File.readToString(): String {
  return readToString(FileReader(this))
}

@Throws(IOException::class)
fun readToString(`is`: InputStream): String {
  return readToString(InputStreamReader(`is`))
}

@Throws(IOException::class)
fun File.readToByteArray(): ByteArray {
  val outputStream = ByteArrayOutputStream(1024)
  copy(FileInputStream(this), outputStream)
  return outputStream.toByteArray()
}

@Throws(IOException::class)
fun writeByteArray(byteArray: ByteArray, fos: FileOutputStream) {
  val outputStream = BufferedOutputStream(fos)
  outputStream.write(byteArray)
  outputStream.close()
}

@Throws(IOException::class)
fun writeString(content: String, filePath: String) {
  writeByteArray(content.toByteArray(), FileOutputStream(filePath))
}

@Throws(IOException::class)
fun writeString(content: String, fos: FileOutputStream) {
  writeByteArray(content.toByteArray(), fos)
}

@Throws(IOException::class)
fun writeString(content: String, file: File) {
  writeString(content, FileOutputStream(file))
}

/**
 * If sequentialPaths are "a","b","c", below method return "a/b/c"
 */
fun joinPath(vararg sequentialPaths: String): String {
  if (sequentialPaths.isEmpty()) {
    return ""
  }
  var result = ""
  for (i in 0 until sequentialPaths.size - 1) {
    result += sequentialPaths[i] + File.separator
  }
  result += sequentialPaths[sequentialPaths.size - 1]
  return result
}

fun trimTail(path: String): String {
  return if (path.endsWith("/")) {
    path.substring(0, path.length - 1)
  } else {
    path
  }
}

fun createDir(vararg paths: String) {
  for (path in paths) {
    val dir = File(path)
    if (!dir.exists()) {
      dir.mkdirs()
    }
  }
}

fun String.toFileName(): String {
  return File(this).name
}

fun isExist(vararg paths: String): Boolean {
  for (path in paths) {
    if (!File(path).exists()) {
      return false
    }
  }
  return true
}

fun checkExistent(paths: Array<String>, firstNotExistPath: StringBuilder) {
  firstNotExistPath.setLength(0)
  for (path in paths) {
    if (!isExist(path)) {
      firstNotExistPath.append(path)
      return
    }
  }
}