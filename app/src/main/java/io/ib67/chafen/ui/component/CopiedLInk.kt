package io.ib67.chafen.ui.component

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun PreviewCopiedLink() {
    Surface {
        CopiedLink(link = "https://")
    }
}

@Composable
fun CopiedLink(link: String) {
    val context = LocalContext.current
    Row {
        OutlinedTextField(
            value = link, onValueChange = {}, readOnly = true, singleLine = true,
            label = { Text(text = "链接") },
            leadingIcon = {
                IconButton(onClick = {
                    val clipboard: ClipboardManager =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("address", link)
                    clipboard.setPrimaryClip(clip)

                    Toast.makeText(context, "地址已复制到粘贴板", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "copy")
                }
            },
            trailingIcon = {
                IconButton(onClick = {
                    val intent = Intent().apply {
                        setAction(Intent.ACTION_SEND)
                        putExtra(Intent.EXTRA_TEXT, link)
                        setComponent(
                            ComponentName(
                                "com.tencent.mm",
                                "com.tencent.mm.ui.tools.ShareImgUI"
                            )
                        )
                        setType("text/plain")
                        context.startActivity(Intent.createChooser(this@apply, "分享"))
                    }
                }) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "share")
                }
            }
        )
    }
}