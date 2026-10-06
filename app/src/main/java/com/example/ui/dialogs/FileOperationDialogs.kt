package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActiveDialog
import com.example.data.model.AppLanguage
import com.example.ui.theme.CyanNeon
import com.example.util.FileFormatters

@Composable
fun FileOperationDialogs(
  activeDialog: ActiveDialog?,
  language: AppLanguage,
  onDismiss: () -> Unit,
  onCreateFolder: (String) -> Unit,
  onCreateFile: (String, String) -> Unit,
  onRename: (newName: String) -> Unit,
  onDeleteConfirm: (permanent: Boolean) -> Unit,
  onCompressConfirm: (zipName: String) -> Unit,
  onEmptyTrashConfirm: () -> Unit
) {
  when (activeDialog) {
    is ActiveDialog.NewFolder -> {
      var folderName by remember { mutableStateOf("") }
      AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (language == AppLanguage.FA) "ایجاد پوشه جدید" else "Create New Folder") },
        text = {
          Column {
            OutlinedTextField(
              value = folderName,
              onValueChange = { folderName = it },
              label = { Text(if (language == AppLanguage.FA) "نام پوشه" else "Folder Name") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("dialog_folder_name_input"),
              shape = RoundedCornerShape(12.dp)
            )
          }
        },
        confirmButton = {
          Button(
            onClick = { onCreateFolder(folderName) },
            enabled = folderName.isNotBlank(),
            modifier = Modifier.testTag("dialog_confirm_button")
          ) {
            Text(if (language == AppLanguage.FA) "ایجاد" else "Create")
          }
        },
        dismissButton = {
          TextButton(onClick = onDismiss) {
            Text(if (language == AppLanguage.FA) "انصراف" else "Cancel")
          }
        }
      )
    }

    is ActiveDialog.NewFile -> {
      var fileName by remember { mutableStateOf("") }
      var fileContent by remember { mutableStateOf("") }
      AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (language == AppLanguage.FA) "ایجاد فایل متنی جدید" else "Create New Text File") },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = fileName,
              onValueChange = { fileName = it },
              label = { Text(if (language == AppLanguage.FA) "نام فایل (مثلا notes.txt)" else "File Name (e.g. notes.txt)") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("dialog_file_name_input"),
              shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
              value = fileContent,
              onValueChange = { fileContent = it },
              label = { Text(if (language == AppLanguage.FA) "متن اولیه (اختیاری)" else "Initial Content (Optional)") },
              maxLines = 4,
              modifier = Modifier.fillMaxWidth().testTag("dialog_file_content_input"),
              shape = RoundedCornerShape(12.dp)
            )
          }
        },
        confirmButton = {
          Button(
            onClick = { onCreateFile(fileName, fileContent) },
            enabled = fileName.isNotBlank(),
            modifier = Modifier.testTag("dialog_confirm_button")
          ) {
            Text(if (language == AppLanguage.FA) "ایجاد" else "Create")
          }
        },
        dismissButton = {
          TextButton(onClick = onDismiss) {
            Text(if (language == AppLanguage.FA) "انصراف" else "Cancel")
          }
        }
      )
    }

    is ActiveDialog.Rename -> {
      var newName by remember { mutableStateOf(activeDialog.file.name) }
      AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (language == AppLanguage.FA) "تغییر نام" else "Rename") },
        text = {
          OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text(if (language == AppLanguage.FA) "نام جدید" else "New Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("dialog_rename_input"),
            shape = RoundedCornerShape(12.dp)
          )
        },
        confirmButton = {
          Button(
            onClick = { onRename(newName) },
            enabled = newName.isNotBlank() && newName != activeDialog.file.name,
            modifier = Modifier.testTag("dialog_confirm_button")
          ) {
            Text(if (language == AppLanguage.FA) "تغییر" else "Rename")
          }
        },
        dismissButton = {
          TextButton(onClick = onDismiss) {
            Text(if (language == AppLanguage.FA) "انصراف" else "Cancel")
          }
        }
      )
    }

    is ActiveDialog.Details -> {
      val item = activeDialog.item
      AlertDialog(
        onDismissRequest = onDismiss,
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = CyanNeon)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (language == AppLanguage.FA) "مشخصات آیتم" else "Item Details")
          }
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DetailRow(if (language == AppLanguage.FA) "نام:" else "Name:", item.name)
            DetailRow(if (language == AppLanguage.FA) "نوع:" else "Type:", if (item.isDirectory) "Folder" else item.extension.uppercase())
            DetailRow(
              if (language == AppLanguage.FA) "حجم:" else "Size:",
              if (item.isDirectory) "${item.childCount} items" else FileFormatters.formatFileSize(item.sizeBytes)
            )
            DetailRow(if (language == AppLanguage.FA) "آخرین تغییر:" else "Modified:", FileFormatters.formatDate(item.lastModified))
            DetailRow(if (language == AppLanguage.FA) "مسیر:" else "Path:", item.path)
          }
        },
        confirmButton = {
          Button(onClick = onDismiss, modifier = Modifier.testTag("dialog_close_button")) {
            Text(if (language == AppLanguage.FA) "بستن" else "Close")
          }
        }
      )
    }

    is ActiveDialog.DeleteConfirmation -> {
      AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
        title = {
          Text(
            if (language == AppLanguage.FA)
              if (activeDialog.permanent) "حذف دائمی فایل‌ها" else "انتقال به سطل زباله"
            else
              if (activeDialog.permanent) "Permanently Delete" else "Move to Trash"
          )
        },
        text = {
          Text(
            if (language == AppLanguage.FA)
              if (activeDialog.permanent) "آیا مطمئن هستید که می‌خواهید ${activeDialog.items.size} مورد را برای همیشه حذف کنید؟ این عمل غیرقابل بازگشت است."
              else "آیا مطمئن هستید که می‌خواهید ${activeDialog.items.size} مورد را به سطل زباله منتقل کنید؟"
            else
              if (activeDialog.permanent) "Are you sure you want to permanently delete ${activeDialog.items.size} items? This cannot be undone."
              else "Move ${activeDialog.items.size} items to trash?"
          )
        },
        confirmButton = {
          Button(
            onClick = { onDeleteConfirm(activeDialog.permanent) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.testTag("dialog_delete_confirm_button")
          ) {
            Text(if (language == AppLanguage.FA) "حذف" else "Delete")
          }
        },
        dismissButton = {
          TextButton(onClick = onDismiss) {
            Text(if (language == AppLanguage.FA) "انصراف" else "Cancel")
          }
        }
      )
    }

    is ActiveDialog.CompressToZip -> {
      var zipName by remember { mutableStateOf("Archive_${System.currentTimeMillis() % 10000}.zip") }
      AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (language == AppLanguage.FA) "فشرده‌سازی در فرمت ZIP" else "Compress to ZIP") },
        text = {
          OutlinedTextField(
            value = zipName,
            onValueChange = { zipName = it },
            label = { Text(if (language == AppLanguage.FA) "نام فایل ZIP" else "ZIP File Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("dialog_zip_name_input"),
            shape = RoundedCornerShape(12.dp)
          )
        },
        confirmButton = {
          Button(
            onClick = { onCompressConfirm(zipName) },
            enabled = zipName.isNotBlank(),
            modifier = Modifier.testTag("dialog_confirm_button")
          ) {
            Text(if (language == AppLanguage.FA) "فشرده‌سازی" else "Compress")
          }
        },
        dismissButton = {
          TextButton(onClick = onDismiss) {
            Text(if (language == AppLanguage.FA) "انصراف" else "Cancel")
          }
        }
      )
    }

    is ActiveDialog.EmptyTrashConfirmation -> {
      AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
        title = { Text(if (language == AppLanguage.FA) "خالی کردن سطل زباله" else "Empty Trash") },
        text = {
          Text(
            if (language == AppLanguage.FA)
              "تمام فایل‌های داخل سطل زباله برای همیشه پاک خواهند شد. آیا ادامه می‌دهید؟"
            else
              "All items in trash will be permanently erased. Continue?"
          )
        },
        confirmButton = {
          Button(
            onClick = onEmptyTrashConfirm,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.testTag("dialog_empty_trash_confirm")
          ) {
            Text(if (language == AppLanguage.FA) "خالی کردن" else "Empty")
          }
        },
        dismissButton = {
          TextButton(onClick = onDismiss) {
            Text(if (language == AppLanguage.FA) "انصراف" else "Cancel")
          }
        }
      )
    }

    null -> Unit
  }
}

@Composable
private fun DetailRow(label: String, value: String) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
      color = CyanNeon
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.padding(top = 2.dp)
    )
  }
}
