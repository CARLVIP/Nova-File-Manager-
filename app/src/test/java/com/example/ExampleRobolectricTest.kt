package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.FileFormatters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Nova Files", appName)
  }

  @Test
  fun `test file size formatting`() {
    assertEquals("0 B", FileFormatters.formatFileSize(0))
    assertEquals("500 B", FileFormatters.formatFileSize(500))
    assertEquals("1.0 KB", FileFormatters.formatFileSize(1024))
    assertEquals("1.0 MB", FileFormatters.formatFileSize(1024 * 1024))
  }

  @Test
  fun `test file category detection`() {
    val sampleImage = File("picture.png")
    assertTrue(FileFormatters.isImage(sampleImage))

    val sampleText = File("notes.txt")
    assertTrue(FileFormatters.isTextEditable(sampleText))

    val sampleAudio = File("music.mp3")
    assertTrue(FileFormatters.isAudio(sampleAudio))
  }
}
