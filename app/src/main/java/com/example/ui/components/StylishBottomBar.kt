package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.ClipboardOperation
import com.example.data.model.ClipboardState
import com.example.data.model.NavigationTab
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.VioletNeon

@Composable
fun StylishBottomBar(
  currentTab: NavigationTab,
  onSelectTab: (NavigationTab) -> Unit,
  language: AppLanguage,
  clipboard: ClipboardState?,
  onPasteClipboard: () -> Unit,
  onClearClipboard: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .windowInsetsPadding(WindowInsets.navigationBars),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Floating Clipboard Banner
    AnimatedVisibility(
      visible = clipboard != null,
      enter = slideInVertically(initialOffsetY = { it }),
      exit = slideOutVertically(targetOffsetY = { it })
    ) {
      if (clipboard != null) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          tonalElevation = 8.dp,
          modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(10.dp, RoundedCornerShape(20.dp), spotColor = CyanNeon)
            .border(1.dp, CyanNeon.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                if (clipboard.operation == ClipboardOperation.COPY) Icons.Default.ContentCopy else Icons.Default.ContentCut,
                contentDescription = null,
                tint = CyanNeon,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (language == AppLanguage.FA)
                  "${clipboard.sourceFiles.size} فایل آماده برای جای‌گذاری"
                else
                  "${clipboard.sourceFiles.size} items ready to paste",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Button(
                onClick = onPasteClipboard,
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
                modifier = Modifier
                  .height(34.dp)
                  .testTag("clipboard_paste_button")
              ) {
                Text(
                  text = if (language == AppLanguage.FA) "جای‌گذاری" else "Paste",
                  style = MaterialTheme.typography.labelSmall
                )
              }
              IconButton(
                onClick = onClearClipboard,
                modifier = Modifier
                  .size(34.dp)
                  .padding(start = 4.dp)
              ) {
                Icon(Icons.Default.Close, contentDescription = "Cancel", tint = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
          }
        }
      }
    }

    // Main Floating Nav Bar
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
      tonalElevation = 8.dp,
      modifier = Modifier
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .shadow(16.dp, RoundedCornerShape(24.dp))
        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        NavigationTab.values().forEach { tab ->
          val isSelected = currentTab == tab
          val icon = getTabIcon(tab)
          val label = if (language == AppLanguage.FA) tab.titleFa else tab.titleEn

          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .clickable { onSelectTab(tab) }
              .padding(horizontal = 12.dp, vertical = 6.dp)
              .testTag("nav_tab_${tab.name.lowercase()}")
          ) {
            Box(
              modifier = Modifier
                .size(width = if (isSelected) 44.dp else 36.dp, height = 30.dp)
                .clip(RoundedCornerShape(14.dp))
                .then(
                  if (isSelected) {
                    Modifier.background(Brush.horizontalGradient(listOf(CyanNeon.copy(alpha = 0.25f), VioletNeon.copy(alpha = 0.25f))))
                  } else {
                    Modifier.background(Color.Transparent)
                  }
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                icon,
                contentDescription = label,
                tint = if (isSelected) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = label,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = if (isSelected) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}

fun getTabIcon(tab: NavigationTab): ImageVector {
  return when (tab) {
    NavigationTab.HOME -> Icons.Default.Home
    NavigationTab.FILES -> Icons.Default.Folder
    NavigationTab.CLEANER -> Icons.Default.CleaningServices
    NavigationTab.STARRED -> Icons.Default.Star
    NavigationTab.TRASH -> Icons.Default.Delete
  }
}
