package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.theme.*

@Composable
fun SpeedDialFab(
  language: AppLanguage,
  isSelectionActive: Boolean,
  onNewFolder: () -> Unit,
  onNewFile: () -> Unit,
  onImportFile: () -> Unit,
  onCompressSelected: () -> Unit,
  onQuickClean: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isExpanded by remember { mutableStateOf(false) }

  val rotation by animateFloatAsState(
    targetValue = if (isExpanded) 135f else 0f,
    animationSpec = spring(stiffness = 300f),
    label = "fab_rotation"
  )

  Box(
    modifier = modifier,
    contentAlignment = Alignment.BottomEnd
  ) {
    // Backdrop when expanded
    if (isExpanded) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
          ) { isExpanded = false }
      )
    }

    Column(
      horizontalAlignment = Alignment.End,
      verticalArrangement = Arrangement.spacedBy(14.dp),
      modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
    ) {
      // Sub action buttons
      AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
      ) {
        Column(
          horizontalAlignment = Alignment.End,
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // If items are selected, offer Compress to ZIP
          if (isSelectionActive) {
            SpeedDialItem(
              label = if (language == AppLanguage.FA) "فشرده‌سازی در ZIP" else "Compress to ZIP",
              icon = Icons.Default.Archive,
              gradient = listOf(EmeraldNeon, Color(0xFF059669)),
              testTag = "speed_dial_compress",
              onClick = {
                isExpanded = false
                onCompressSelected()
              }
            )
          }

          // Import File
          SpeedDialItem(
            label = if (language == AppLanguage.FA) "وارد کردن فایل" else "Import File",
            icon = Icons.Default.FileUpload,
            gradient = listOf(VioletNeon, Color(0xFF6D28D9)),
            testTag = "speed_dial_import",
            onClick = {
              isExpanded = false
              onImportFile()
            }
          )

          // New Text File
          SpeedDialItem(
            label = if (language == AppLanguage.FA) "فایل متنی جدید" else "New Text File",
            icon = Icons.Default.NoteAdd,
            gradient = listOf(AmberNeon, Color(0xFFD97706)),
            testTag = "speed_dial_new_file",
            onClick = {
              isExpanded = false
              onNewFile()
            }
          )

          // New Folder
          SpeedDialItem(
            label = if (language == AppLanguage.FA) "پوشه جدید" else "New Folder",
            icon = Icons.Default.CreateNewFolder,
            gradient = listOf(CyanNeon, Color(0xFF0284C7)),
            testTag = "speed_dial_new_folder",
            onClick = {
              isExpanded = false
              onNewFolder()
            }
          )

          // Quick Cleanup
          SpeedDialItem(
            label = if (language == AppLanguage.FA) "پاک‌سازی کش" else "Clean Cache",
            icon = Icons.Default.CleaningServices,
            gradient = listOf(PinkNeon, Color(0xFFBE185D)),
            testTag = "speed_dial_clean",
            onClick = {
              isExpanded = false
              onQuickClean()
            }
          )
        }
      }

      // Main Floating Button with Neon Gradient
      FloatingActionButton(
        onClick = { isExpanded = !isExpanded },
        containerColor = Color.Transparent,
        contentColor = Color.Black,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(8.dp),
        modifier = Modifier
          .size(60.dp)
          .shadow(12.dp, CircleShape, spotColor = CyanNeon)
          .background(
            Brush.linearGradient(
              colors = listOf(CyanNeon, VioletNeon)
            ),
            shape = CircleShape
          )
          .testTag("speed_dial_main_fab")
      ) {
        Icon(
          Icons.Default.Add,
          contentDescription = "Expand Menu",
          modifier = Modifier
            .size(28.dp)
            .rotate(rotation),
          tint = Color.Black
        )
      }
    }
  }
}

@Composable
private fun SpeedDialItem(
  label: String,
  icon: ImageVector,
  gradient: List<Color>,
  testTag: String,
  onClick: () -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.End,
    modifier = Modifier.padding(end = 4.dp)
  ) {
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = MaterialTheme.colorScheme.surfaceVariant,
      tonalElevation = 6.dp,
      modifier = Modifier
        .shadow(4.dp, RoundedCornerShape(12.dp))
        .clickable(onClick = onClick)
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
      )
    }

    Spacer(modifier = Modifier.width(10.dp))

    Box(
      modifier = Modifier
        .size(46.dp)
        .clip(CircleShape)
        .background(Brush.linearGradient(gradient))
        .clickable(onClick = onClick)
        .testTag(testTag),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        icon,
        contentDescription = label,
        tint = Color.White,
        modifier = Modifier.size(22.dp)
      )
    }
  }
}
