package com.example.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppLanguage
import com.example.ui.theme.CyanNeon
import java.io.File

@Composable
fun TextEditorPreviewDialog(
  file: File,
  initialContent: String,
  language: AppLanguage,
  onSave: (newContent: String) -> Unit,
  onDismiss: () -> Unit
) {
  var content by remember { mutableStateOf(initialContent) }
  val linesCount = content.lines().size
  val charsCount = content.length

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Top Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDismiss, modifier = Modifier.testTag("editor_close_button")) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = file.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "$linesCount ${if (language == AppLanguage.FA) "خط" else "lines"} • $charsCount ${if (language == AppLanguage.FA) "کاراکتر" else "chars"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Button(
            onClick = { onSave(content) },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
            modifier = Modifier.testTag("editor_save_button")
          ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (language == AppLanguage.FA) "ذخیره" else "Save")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Code / Text Area with Monospace Font
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          val scrollState = rememberScrollState()
          BasicTextField(
            value = content,
            onValueChange = { content = it },
            modifier = Modifier
              .fillMaxSize()
              .verticalScroll(scrollState)
              .testTag("editor_text_input"),
            textStyle = TextStyle(
              fontFamily = FontFamily.Monospace,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurface,
              lineHeight = 20.sp
            ),
            cursorBrush = SolidColor(CyanNeon)
          )
        }
      }
    }
  }
}
