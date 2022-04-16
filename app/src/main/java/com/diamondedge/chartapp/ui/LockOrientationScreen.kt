package com.diamondedge.chartapp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LockOrientationScreen(
    navigateUp: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val showLandscape = rememberSaveable { mutableStateOf(false) }
    if (showLandscape.value) {
        LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    }

    Scaffold {
        Column {
            Button(onClick = { showLandscape.value = true }) {
                Text(text = "Landscape")
            }
            Button(onClick = { showLandscape.value = false }) {
                Text(text = "Portrait")
            }
        }
    }
}


private fun requestOrientation(context: Context, orientation: Int) {
    val activity = context.findActivity() ?: return
    activity.requestedOrientation = orientation
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun FavoriteButton(
    isChecked: Boolean,
    onClick: () -> Unit
) {
    IconToggleButton(
        checked = isChecked,
        onCheckedChange = { onClick() }
    ) {
        val transition = updateTransition(isChecked, label = "Checked indicator")

/*
        val tint by transition.animateColor(
            label = "Tint"
        ) { isChecked ->
            if (isChecked) Color.Red else Color.Black
        }
*/

        Icon(
            imageVector = if (isChecked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = null,
//            tint = tint
        )
    }
}

@Preview("Favorite Button")
@Composable
fun FavoriteButtonPreview() {
    val (isChecked, setChecked) = remember { mutableStateOf(false) }
    MaterialTheme {
        Surface {
            FavoriteButton(
                isChecked = isChecked,
                onClick = { setChecked(!isChecked) }
            )
        }
    }
}