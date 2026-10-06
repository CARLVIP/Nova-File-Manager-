package com.example.data.model

import java.io.File

enum class FileCategory(val titleEn: String, val titleFa: String) {
  ALL("All Files", "همه فایل‌ها"),
  IMAGES("Images", "تصاویر"),
  VIDEOS("Videos", "ویدیوها"),
  AUDIO("Audio", "صوت و موسیقی"),
  DOCUMENTS("Documents", "اسناد و مدارک"),
  ARCHIVES("Archives", "فایل‌های فشرده"),
  CODE("Code", "کد و اسکریپت"),
  OTHER("Other", "سایر")
}

enum class SortOption(val titleEn: String, val titleFa: String) {
  NAME("Name", "نام"),
  DATE("Date Modified", "تاریخ تغییر"),
  SIZE("Size", "حجم"),
  TYPE("Type", "نوع فایل")
}

enum class ViewMode {
  LIST,
  GRID
}

enum class NavigationTab(val titleEn: String, val titleFa: String) {
  HOME("Home", "خانه"),
  FILES("Explorer", "مدیریت فایل"),
  CLEANER("Cleaner", "پاک‌سازی حافظه"),
  STARRED("Starred", "نشان‌شده‌ها"),
  TRASH("Trash", "سطل زباله")
}

enum class AppLanguage {
  FA,
  EN
}

enum class ClipboardOperation {
  COPY,
  CUT
}

data class ClipboardState(
  val sourceFiles: List<File>,
  val operation: ClipboardOperation
)

data class FileItem(
  val file: File,
  val isDirectory: Boolean,
  val name: String,
  val path: String,
  val sizeBytes: Long,
  val lastModified: Long,
  val category: FileCategory,
  val isStarred: Boolean = false,
  val extension: String = "",
  val childCount: Int = 0
)

data class StorageCategoryStat(
  val category: FileCategory,
  val bytes: Long,
  val count: Int,
  val percentage: Float
)

data class StorageStats(
  val totalBytes: Long,
  val usedBytes: Long,
  val freeBytes: Long,
  val categoryStats: List<StorageCategoryStat>,
  val totalFiles: Int,
  val totalFolders: Int
)

sealed class ActivePreview {
  data class TextPreview(val file: File, val content: String, val isEditable: Boolean = true) : ActivePreview()
  data class ImagePreview(val file: File) : ActivePreview()
  data class AudioPreview(val file: File, val fileName: String, val sizeBytes: Long) : ActivePreview()
}

sealed class ActiveDialog {
  data class NewFolder(val parentDir: File) : ActiveDialog()
  data class NewFile(val parentDir: File) : ActiveDialog()
  data class Rename(val file: File) : ActiveDialog()
  data class Details(val item: FileItem) : ActiveDialog()
  data class DeleteConfirmation(val items: List<FileItem>, val permanent: Boolean) : ActiveDialog()
  data class CompressToZip(val items: List<FileItem>) : ActiveDialog()
  object EmptyTrashConfirmation : ActiveDialog()
}
