package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanNeon
import java.io.File

@Composable
fun BreadcrumbBar(
  breadcrumbs: List<File>,
  onNavigateTo: (File) -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  LaunchedEffect(breadcrumbs.size) {
    scrollState.animateScrollTo(scrollState.maxValue)
  }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .horizontalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    breadcrumbs.forEachIndexed { index, dir ->
      val isLast = index == breadcrumbs.lastIndex
      val isRoot = index == 0

      Surface(
        onClick = { onNavigateTo(dir) },
        shape = RoundedCornerShape(10.dp),
        color = if (isLast) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
          .padding(end = 4.dp)
          .testTag("breadcrumb_item_$index")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (isRoot) {
            Icon(
              Icons.Default.Home,
              contentDescription = "Root",
              tint = if (isLast) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Root",
              style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
              color = if (isLast) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant
            )
          } else {
            Text(
              text = dir.name,
              style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
              color = if (isLast) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      if (!isLast) {
        Icon(
          Icons.Default.ChevronRight,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.size(16.dp).padding(end = 4.dp)
        )
      }
    }
  }
}
