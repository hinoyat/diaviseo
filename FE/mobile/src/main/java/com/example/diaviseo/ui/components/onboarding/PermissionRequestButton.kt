package com.example.diaviseo.ui.components.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import com.example.diaviseo.ui.theme.DiaViseoColors
import androidx.compose.ui.graphics.Color

@Composable
fun PermissionRequestButton(
    modifier: Modifier = Modifier,
    onGranted: () -> Unit = {}
) {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        permissionGranted = result.all { it.value }
        if (permissionGranted) onGranted()
    }

    Button(
        onClick = { launcher.launch(permissions) },
        modifier = modifier.fillMaxWidth(),
        colors = if (permissionGranted)
            ButtonDefaults.buttonColors(containerColor = DiaViseoColors.Deactive)
        else
            ButtonDefaults.buttonColors(containerColor = DiaViseoColors.Deactive)
    ) {
        Text(
            text = if (permissionGranted) "허용 완료" else "카메라, 갤러리 권한 허용하기",
            color = if (permissionGranted) Color.White else Color.Black
        )
    }
}
