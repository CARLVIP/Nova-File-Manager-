package com.example.util

import androidx.compose.ui.graphics.Color
import com.example.data.model.FileCategory
import com.example.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileFormatters {

  fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, units.size - 1)
    val value = bytes / Math.pow(1024.0, digitGroups.toDouble())
    return if (digitGroups == 0) {
      "$bytes B"
    } else {
      String.format(Locale.US, "%.1f %s", value, units[digitGroups])
    }
  }

  fun formatDate(timestamp: Long): String {
    if (timestamp <= 0) return "-"
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
  }

  fun determineCategory(file: File): FileCategory {
    if (file.isDirectory) return FileCategory.ALL
    val ext = file.extension.lowercase(Locale.ROOT)
    return when (ext) {
      "jpg", "jpeg", "png", "gif", "webp", "bmp", "svg" -> FileCategory.IMAGES
      "mp4", "mkv", "avi", "mov", "webm", "flv", "3gp" -> FileCategory.VIDEOS
      "mp3", "wav", "ogg", "m4a", "flac", "aac" -> FileCategory.AUDIO
      "pdf", "doc", "docx", "txt", "rtf", "odt", "csv", "xls", "xlsx", "ppt", "pptx" -> FileCategory.DOCUMENTS
      "zip", "rar", "7z", "tar", "gz", "bz2" -> FileCategory.ARCHIVES
      "kt", "java", "py", "js", "ts", "html", "css", "json", "xml", "c", "cpp", "sh", "sql", "md" -> FileCategory.CODE
      else -> FileCategory.OTHER
    }
  }

  fun getCategoryColor(category: FileCategory): Color {
    return when (category) {
      FileCategory.ALL -> CyanNeon
      FileCategory.IMAGES -> CyanNeon
      FileCategory.VIDEOS -> PinkNeon
      FileCategory.AUDIO -> VioletNeon
      FileCategory.DOCUMENTS -> AmberNeon
      FileCategory.ARCHIVES -> EmeraldNeon
      FileCategory.CODE -> Color(0xFF60A5FA)
      FileCategory.OTHER -> Color(0xFF94A3B8)
    }
  }

  fun isTextEditable(file: File): Boolean {
    if (file.isDirectory) return false
    val ext = file.extension.lowercase(Locale.ROOT)
    return ext in setOf("txt", "md", "json", "xml", "kt", "java", "py", "js", "ts", "html", "css", "csv", "log", "properties", "sql", "sh", "yaml", "yml", "gradle")
  }

  fun isImage(file: File): Boolean {
    if (file.isDirectory) return false
    val ext = file.extension.lowercase(Locale.ROOT)
    return ext in setOf("jpg", "jpeg", "png", "webp", "bmp", "gif")
  }

  fun isAudio(file: File): Boolean {
    if (file.isDirectory) return false
    val ext = file.extension.lowercase(Locale.ROOT)
    return ext in setOf("mp3", "wav", "ogg", "m4a", "flac", "aac")
  }

  fun isArchive(file: File): Boolean {
    if (file.isDirectory) return false
    val ext = file.extension.lowercase(Locale.ROOT)
    return ext in setOf("zip")
  }
}
