package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.*
import com.example.util.FileFormatters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class FileManagerRepository(private val context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("nova_file_manager_prefs", Context.MODE_PRIVATE)

  val rootDirectory: File by lazy {
    val dir = context.getExternalFilesDir(null) ?: context.filesDir
    val novaDir = File(dir, "NovaFiles")
    if (!novaDir.exists()) {
      novaDir.mkdirs()
    }
    novaDir
  }

  val trashDirectory: File by lazy {
    val trash = File(rootDirectory.parentFile ?: rootDirectory, ".nova_trash")
    if (!trash.exists()) {
      trash.mkdirs()
    }
    trash
  }

  suspend fun initializeSampleFilesIfEmpty() = withContext(Dispatchers.IO) {
    val existingFiles = rootDirectory.listFiles()?.filter { !it.name.startsWith(".") }
    if (existingFiles.isNullOrEmpty()) {
      seedSampleFiles()
    }
  }

  private fun seedSampleFiles() {
    try {
      // Documents Folder
      val docs = File(rootDirectory, "Documents").apply { mkdirs() }
      File(docs, "Welcome_NovaFiles.md").writeText(
        """# ✨ خوش آمدید به مدیر فایل پیشرفته Nova Files
Nova Files یک مدیر فایل مدرن، شیک و سریع برای اندروید است.

### امکانات کلیدی:
- 📁 جستجو و ناوبری پیشرفته با مسیر درختی (Breadcrumbs)
- 📊 آنالیز هوشمند حافظه و پاک‌سازی فایل‌های حجیم و تکراری
- 🎨 تم دارک سایبرپانک و نئون با منوی شناور مدرن
- 📝 ویرایشگر متنی توکار برای انواع فایل‌های متنی و اسکریپت
- 🖼️ پیش‌نمایش تصویر و پخش فایل‌های صوتی
- 📦 فشرده‌سازی و استخراج فایل‌های ZIP
- 🗑️ سطل زباله اختصاصی با قابلیت بازیابی (Restore)
- ⭐ نشان‌گذاری فایل‌های برگزیده (Favorites)
- 🌐 پشتیبانی کامل از زبان فارسی و انگلیسی

امیدواریم از تجربه کار با Nova Files لذت ببرید!
""".trimIndent()
      )

      File(docs, "Project_Plan.json").writeText(
        """{
  "project": "Nova Mobile Suite",
  "version": "2.4.0",
  "features": [
    "High performance file engine",
    "Cyberpunk neon interface",
    "Bilingual Persian and English support",
    "Real-time storage analyzer"
  ],
  "status": "Active"
}""".trimIndent()
      )

      File(docs, "Quarterly_Report.csv").writeText(
        """Month,Category,Files_Managed,Storage_Saved_MB
Farvardin,Documents,1420,380
Ordibehesht,Media,2890,920
Khordad,Archives,1120,640
Tir,Projects,3410,1250
""".trimIndent()
      )

      // Projects Folder
      val code = File(rootDirectory, "Projects").apply { mkdirs() }
      File(code, "NovaEngine.kt").writeText(
        """package com.example.filemanager

class NovaEngine {
    fun optimizeStorage(): Long {
        // Fast cleanup scanner
        return 1024 * 1024 * 64L
    }
}
""".trimIndent()
      )
      File(code, "cyberpunk_style.css").writeText(
        """:root {
  --neon-cyan: #00E5FF;
  --neon-violet: #8B5CF6;
  --bg-dark: #090D16;
  --card-surface: #101726;
}
""".trimIndent()
      )

      // Media / Pictures Folder
      val pics = File(rootDirectory, "Pictures").apply { mkdirs() }
      val samplePic = File(pics, "Cyber_Wallpaper.jpg")
      if (!samplePic.exists()) {
        // Copy the generated logo as a sample image if available
        val iconFile = File(context.filesDir.parentFile, "res/drawable/app_logo_1791270863054.jpg")
        if (iconFile.exists()) {
          iconFile.copyTo(samplePic, overwrite = true)
        } else {
          samplePic.writeText("Placeholder Picture Asset Data")
        }
      }

      // Music / Audio Folder
      val audio = File(rootDirectory, "Music").apply { mkdirs() }
      File(audio, "Synthwave_Night.mp3").writeText("Simulated Audio Buffer Data [Nova Synthwave Track 3:45]")
      File(audio, "Ambient_Focus.wav").writeText("Simulated Audio Buffer Data [Ambient Soundscape 4:12]")

      // Downloads Folder
      val downloads = File(rootDirectory, "Downloads").apply { mkdirs() }
      File(downloads, "API_Documentation.pdf").writeText("%PDF-1.4 Nova Files Core Specification Document")
      File(downloads, "Release_Notes_v1.0.txt").writeText("Initial Release of Nova File Manager for Android.")

      // Create a sample zip archive inside Downloads
      val sampleZip = File(downloads, "Templates.zip")
      createSampleZip(sampleZip)

    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun createSampleZip(target: File) {
    try {
      ZipOutputStream(FileOutputStream(target)).use { zos ->
        val entry1 = ZipEntry("readme.txt")
        zos.putNextEntry(entry1)
        zos.write("Nova Files Sample Archive Content".toByteArray())
        zos.closeEntry()

        val entry2 = ZipEntry("config.json")
        zos.putNextEntry(entry2)
        zos.write("""{"theme":"dark","mode":"neon"}""".toByteArray())
        zos.closeEntry()
      }
    } catch (e: Exception) {
      target.writeText("PK... simulated zip payload")
    }
  }

  suspend fun listFiles(
    directory: File,
    searchQuery: String = "",
    categoryFilter: FileCategory? = null,
    sortOption: SortOption = SortOption.NAME,
    sortAscending: Boolean = true
  ): List<FileItem> = withContext(Dispatchers.IO) {
    if (!directory.exists()) return@withContext emptyList()

    val files = directory.listFiles() ?: return@withContext emptyList()
    var items = files.filter { !it.name.startsWith(".") }.map { file ->
      val isDir = file.isDirectory
      val category = FileFormatters.determineCategory(file)
      val isStarred = isStarred(file)
      val size = if (isDir) getFolderSize(file) else file.length()
      val childCount = if (isDir) (file.listFiles()?.count { !it.name.startsWith(".") } ?: 0) else 0

      FileItem(
        file = file,
        isDirectory = isDir,
        name = file.name,
        path = file.absolutePath,
        sizeBytes = size,
        lastModified = file.lastModified(),
        category = category,
        isStarred = isStarred,
        extension = file.extension,
        childCount = childCount
      )
    }

    if (searchQuery.isNotBlank()) {
      val queryLower = searchQuery.trim().lowercase(Locale.ROOT)
      items = items.filter { it.name.lowercase(Locale.ROOT).contains(queryLower) }
    }

    if (categoryFilter != null && categoryFilter != FileCategory.ALL) {
      items = items.filter { !it.isDirectory && it.category == categoryFilter }
    }

    // Sort: Folders first, then by selected option
    items.sortedWith(Comparator { a, b ->
      if (a.isDirectory && !b.isDirectory) return@Comparator -1
      if (!a.isDirectory && b.isDirectory) return@Comparator 1

      val comp = when (sortOption) {
        SortOption.NAME -> a.name.compareTo(b.name, ignoreCase = true)
        SortOption.DATE -> a.lastModified.compareTo(b.lastModified)
        SortOption.SIZE -> a.sizeBytes.compareTo(b.sizeBytes)
        SortOption.TYPE -> a.extension.compareTo(b.extension, ignoreCase = true)
      }
      if (sortAscending) comp else -comp
    })
  }

  suspend fun getAllFilesInStorage(): List<FileItem> = withContext(Dispatchers.IO) {
    val results = mutableListOf<FileItem>()
    fun traverse(dir: File) {
      val children = dir.listFiles() ?: return
      for (child in children) {
        if (child.name.startsWith(".")) continue
        if (child.isDirectory) {
          traverse(child)
        } else {
          results.add(
            FileItem(
              file = child,
              isDirectory = false,
              name = child.name,
              path = child.absolutePath,
              sizeBytes = child.length(),
              lastModified = child.lastModified(),
              category = FileFormatters.determineCategory(child),
              isStarred = isStarred(child),
              extension = child.extension
            )
          )
        }
      }
    }
    traverse(rootDirectory)
    results
  }

  suspend fun createFolder(parent: File, name: String): Result<File> = withContext(Dispatchers.IO) {
    try {
      val safeName = sanitizeFileName(name)
      if (safeName.isBlank()) return@withContext Result.failure(IllegalArgumentException("Name cannot be empty"))
      val folder = File(parent, safeName)
      if (folder.exists()) return@withContext Result.failure(IllegalArgumentException("Folder already exists"))
      if (folder.mkdirs()) Result.success(folder) else Result.failure(Exception("Failed to create folder"))
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun createFile(parent: File, name: String, content: String = ""): Result<File> = withContext(Dispatchers.IO) {
    try {
      val safeName = sanitizeFileName(name)
      if (safeName.isBlank()) return@withContext Result.failure(IllegalArgumentException("Name cannot be empty"))
      val file = File(parent, safeName)
      if (file.exists()) return@withContext Result.failure(IllegalArgumentException("File already exists"))
      file.writeText(content)
      Result.success(file)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun rename(file: File, newName: String): Result<File> = withContext(Dispatchers.IO) {
    try {
      val safeName = sanitizeFileName(newName)
      if (safeName.isBlank()) return@withContext Result.failure(IllegalArgumentException("New name cannot be empty"))
      val target = File(file.parentFile, safeName)
      if (target.exists()) return@withContext Result.failure(IllegalArgumentException("Target already exists"))
      if (file.renameTo(target)) {
        if (isStarred(file)) {
          toggleStar(file)
          toggleStar(target)
        }
        Result.success(target)
      } else {
        Result.failure(Exception("Failed to rename file"))
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun deleteToTrash(file: File): Result<Unit> = withContext(Dispatchers.IO) {
    try {
      val target = File(trashDirectory, "${System.currentTimeMillis()}_${file.name}")
      if (file.renameTo(target)) {
        // save original path mapping in prefs
        prefs.edit().putString("trash_orig_${target.name}", file.absolutePath).apply()
        Result.success(Unit)
      } else {
        // Fallback copy & delete
        file.copyRecursively(target, overwrite = true)
        file.deleteRecursively()
        prefs.edit().putString("trash_orig_${target.name}", file.absolutePath).apply()
        Result.success(Unit)
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun restoreFromTrash(trashFile: File): Result<Unit> = withContext(Dispatchers.IO) {
    try {
      val origPath = prefs.getString("trash_orig_${trashFile.name}", null)
      val destFile = if (origPath != null) {
        val f = File(origPath)
        if (f.parentFile?.exists() != true) f.parentFile?.mkdirs()
        f
      } else {
        File(rootDirectory, trashFile.name.substringAfter("_"))
      }

      val cleanDest = if (destFile.exists()) {
        File(destFile.parentFile, "Restored_${destFile.name}")
      } else destFile

      if (trashFile.renameTo(cleanDest) || trashFile.copyRecursively(cleanDest, overwrite = true)) {
        trashFile.deleteRecursively()
        prefs.edit().remove("trash_orig_${trashFile.name}").apply()
        Result.success(Unit)
      } else {
        Result.failure(Exception("Failed to restore file"))
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun permanentDelete(file: File): Result<Unit> = withContext(Dispatchers.IO) {
    try {
      prefs.edit().remove("trash_orig_${file.name}").apply()
      if (file.deleteRecursively()) Result.success(Unit) else Result.failure(Exception("Failed to delete file"))
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun emptyTrash(): Result<Unit> = withContext(Dispatchers.IO) {
    try {
      trashDirectory.listFiles()?.forEach { it.deleteRecursively() }
      val keysToRemove = prefs.all.keys.filter { it.startsWith("trash_orig_") }
      val editor = prefs.edit()
      keysToRemove.forEach { editor.remove(it) }
      editor.apply()
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun copyFileOrFolder(source: File, destinationDir: File): Result<File> = withContext(Dispatchers.IO) {
    try {
      var dest = File(destinationDir, source.name)
      if (dest.exists()) {
        val baseName = if (source.isDirectory) source.name else source.nameWithoutExtension
        val ext = if (source.isDirectory) "" else ".${source.extension}"
        dest = File(destinationDir, "${baseName}_copy$ext")
      }
      source.copyRecursively(dest, overwrite = true)
      Result.success(dest)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun moveFileOrFolder(source: File, destinationDir: File): Result<File> = withContext(Dispatchers.IO) {
    try {
      val dest = File(destinationDir, source.name)
      if (dest.exists()) {
        return@withContext Result.failure(IllegalArgumentException("Destination file already exists"))
      }
      if (source.renameTo(dest)) {
        Result.success(dest)
      } else {
        source.copyRecursively(dest, overwrite = true)
        source.deleteRecursively()
        Result.success(dest)
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  fun toggleStar(file: File): Boolean {
    val starred = getStarredSet().toMutableSet()
    val isNowStarred = if (starred.contains(file.absolutePath)) {
      starred.remove(file.absolutePath)
      false
    } else {
      starred.add(file.absolutePath)
      true
    }
    prefs.edit().putStringSet("starred_paths", starred).apply()
    return isNowStarred
  }

  fun isStarred(file: File): Boolean {
    return getStarredSet().contains(file.absolutePath)
  }

  private fun getStarredSet(): Set<String> {
    return prefs.getStringSet("starred_paths", emptySet()) ?: emptySet()
  }

  suspend fun getStarredFiles(): List<FileItem> = withContext(Dispatchers.IO) {
    val paths = getStarredSet()
    paths.mapNotNull { path ->
      val f = File(path)
      if (f.exists()) {
        FileItem(
          file = f,
          isDirectory = f.isDirectory,
          name = f.name,
          path = f.absolutePath,
          sizeBytes = if (f.isDirectory) getFolderSize(f) else f.length(),
          lastModified = f.lastModified(),
          category = FileFormatters.determineCategory(f),
          isStarred = true,
          extension = f.extension
        )
      } else null
    }
  }

  suspend fun getTrashFiles(): List<FileItem> = withContext(Dispatchers.IO) {
    val trashFiles = trashDirectory.listFiles() ?: return@withContext emptyList()
    trashFiles.map { f ->
      val origName = f.name.substringAfter("_")
      FileItem(
        file = f,
        isDirectory = f.isDirectory,
        name = origName,
        path = f.absolutePath,
        sizeBytes = if (f.isDirectory) getFolderSize(f) else f.length(),
        lastModified = f.lastModified(),
        category = FileFormatters.determineCategory(f),
        isStarred = false,
        extension = f.extension
      )
    }
  }

  suspend fun getRecentFiles(limit: Int = 10): List<FileItem> = withContext(Dispatchers.IO) {
    val allFiles = getAllFilesInStorage()
    allFiles.sortedByDescending { it.lastModified }.take(limit)
  }

  suspend fun zipFiles(files: List<File>, zipName: String, destinationDir: File): Result<File> = withContext(Dispatchers.IO) {
    try {
      val validName = if (zipName.endsWith(".zip")) zipName else "$zipName.zip"
      val zipFile = File(destinationDir, sanitizeFileName(validName))
      ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
        for (f in files) {
          addToZip(f, "", zos)
        }
      }
      Result.success(zipFile)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  private fun addToZip(file: File, parentPath: String, zos: ZipOutputStream) {
    val entryPath = if (parentPath.isEmpty()) file.name else "$parentPath/${file.name}"
    if (file.isDirectory) {
      val dirEntry = ZipEntry("$entryPath/")
      zos.putNextEntry(dirEntry)
      zos.closeEntry()
      file.listFiles()?.forEach { child ->
        addToZip(child, entryPath, zos)
      }
    } else {
      val entry = ZipEntry(entryPath)
      zos.putNextEntry(entry)
      FileInputStream(file).use { fis ->
        fis.copyTo(zos)
      }
      zos.closeEntry()
    }
  }

  suspend fun unzipFile(zipFile: File, destinationDir: File): Result<File> = withContext(Dispatchers.IO) {
    try {
      val extractFolder = File(destinationDir, zipFile.nameWithoutExtension)
      if (!extractFolder.exists()) extractFolder.mkdirs()

      ZipInputStream(FileInputStream(zipFile)).use { zis ->
        var entry: ZipEntry? = zis.nextEntry
        while (entry != null) {
          val newFile = File(extractFolder, entry.name)
          if (entry.isDirectory) {
            newFile.mkdirs()
          } else {
            newFile.parentFile?.mkdirs()
            FileOutputStream(newFile).use { fos ->
              zis.copyTo(fos)
            }
          }
          zis.closeEntry()
          entry = zis.nextEntry
        }
      }
      Result.success(extractFolder)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun calculateStorageStats(): StorageStats = withContext(Dispatchers.IO) {
    val allFiles = getAllFilesInStorage()
    var imagesBytes = 0L
    var videosBytes = 0L
    var audioBytes = 0L
    var docsBytes = 0L
    var archivesBytes = 0L
    var codeBytes = 0L
    var otherBytes = 0L

    var imagesCount = 0
    var videosCount = 0
    var audioCount = 0
    var docsCount = 0
    var archivesCount = 0
    var codeCount = 0
    var otherCount = 0

    for (item in allFiles) {
      val size = item.sizeBytes
      when (item.category) {
        FileCategory.IMAGES -> { imagesBytes += size; imagesCount++ }
        FileCategory.VIDEOS -> { videosBytes += size; videosCount++ }
        FileCategory.AUDIO -> { audioBytes += size; audioCount++ }
        FileCategory.DOCUMENTS -> { docsBytes += size; docsCount++ }
        FileCategory.ARCHIVES -> { archivesBytes += size; archivesCount++ }
        FileCategory.CODE -> { codeBytes += size; codeCount++ }
        FileCategory.OTHER, FileCategory.ALL -> { otherBytes += size; otherCount++ }
      }
    }

    val usedBytes = imagesBytes + videosBytes + audioBytes + docsBytes + archivesBytes + codeBytes + otherBytes
    // Real disk stats or friendly virtual container stats (e.g. 32 GB or 64 GB representation)
    val totalBytes = rootDirectory.totalSpace.takeIf { it > 0 } ?: (64L * 1024 * 1024 * 1024)
    val freeBytes = (totalBytes - usedBytes).coerceAtLeast(0L)

    val safeTotal = if (usedBytes > 0) usedBytes.toFloat() else 1f
    val statsList = listOf(
      StorageCategoryStat(FileCategory.IMAGES, imagesBytes, imagesCount, (imagesBytes / safeTotal)),
      StorageCategoryStat(FileCategory.VIDEOS, videosBytes, videosCount, (videosBytes / safeTotal)),
      StorageCategoryStat(FileCategory.AUDIO, audioBytes, audioCount, (audioBytes / safeTotal)),
      StorageCategoryStat(FileCategory.DOCUMENTS, docsBytes, docsCount, (docsBytes / safeTotal)),
      StorageCategoryStat(FileCategory.ARCHIVES, archivesBytes, archivesCount, (archivesBytes / safeTotal)),
      StorageCategoryStat(FileCategory.CODE, codeBytes, codeCount, (codeBytes / safeTotal)),
      StorageCategoryStat(FileCategory.OTHER, otherBytes, otherCount, (otherBytes / safeTotal))
    )

    val folderCount = countFolders(rootDirectory)

    StorageStats(
      totalBytes = totalBytes,
      usedBytes = usedBytes,
      freeBytes = freeBytes,
      categoryStats = statsList,
      totalFiles = allFiles.size,
      totalFolders = folderCount
    )
  }

  suspend fun findLargeFiles(minSizeBytes: Long = 100 * 1024): List<FileItem> = withContext(Dispatchers.IO) {
    val allFiles = getAllFilesInStorage()
    allFiles.filter { it.sizeBytes >= minSizeBytes }.sortedByDescending { it.sizeBytes }
  }

  suspend fun findEmptyFolders(): List<FileItem> = withContext(Dispatchers.IO) {
    val emptyDirs = mutableListOf<FileItem>()
    fun check(dir: File) {
      val children = dir.listFiles()?.filter { !it.name.startsWith(".") } ?: return
      if (children.isEmpty() && dir != rootDirectory) {
        emptyDirs.add(
          FileItem(
            file = dir,
            isDirectory = true,
            name = dir.name,
            path = dir.absolutePath,
            sizeBytes = 0L,
            lastModified = dir.lastModified(),
            category = FileCategory.ALL,
            childCount = 0
          )
        )
      } else {
        children.filter { it.isDirectory }.forEach { check(it) }
      }
    }
    check(rootDirectory)
    emptyDirs
  }

  suspend fun cleanCache(): Long = withContext(Dispatchers.IO) {
    var cleanedBytes = 0L
    try {
      val cacheDir = context.cacheDir
      cacheDir.listFiles()?.forEach {
        cleanedBytes += it.length()
        it.deleteRecursively()
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    cleanedBytes
  }

  private fun getFolderSize(dir: File): Long {
    var size = 0L
    dir.listFiles()?.forEach { child ->
      if (!child.name.startsWith(".")) {
        size += if (child.isDirectory) getFolderSize(child) else child.length()
      }
    }
    return size
  }

  private fun countFolders(dir: File): Int {
    var count = 0
    dir.listFiles()?.forEach { child ->
      if (!child.name.startsWith(".") && child.isDirectory) {
        count += 1 + countFolders(child)
      }
    }
    return count
  }

  private fun sanitizeFileName(name: String): String {
    return name.trim().replace(Regex("[\\\\/:*?\"<>|]"), "_")
  }
}
