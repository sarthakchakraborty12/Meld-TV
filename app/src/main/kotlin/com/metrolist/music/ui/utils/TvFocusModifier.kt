/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.utils

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Context.isTv(): Boolean {
    return (resources.configuration.uiMode and Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_TELEVISION
}

fun Modifier.tvFocusBorder(
    shape: Shape? = null,
    borderWidth: Dp = 3.dp
): Modifier = composed {
    val context = LocalContext.current
    if (!context.isTv()) {
        return@composed this
    }

    var isFocused by remember { mutableStateOf(false) }
    val strokeColor = MaterialTheme.colorScheme.primary
    val resolvedShape = shape ?: RoundedCornerShape(8.dp)

    this
        .onFocusChanged { isFocused = it.isFocused }
        .then(
            if (isFocused) {
                Modifier.border(width = borderWidth, color = strokeColor, shape = resolvedShape)
            } else {
                Modifier
            }
        )
}

