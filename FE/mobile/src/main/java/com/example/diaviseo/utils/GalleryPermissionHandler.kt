package com.example.diaviseo.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED

// 권한 이름 분기
fun getGalleryPermission(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
}

// 권한 확인
fun isGalleryPermissionGranted(context: Context): Boolean {
    val permission = getGalleryPermission()
    return ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED
}

// 다시 묻지 않기 등 권한 rationale 체크
fun shouldShowGalleryRationale(activity: Activity): Boolean {
    val permission = getGalleryPermission()
    return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
}

// 설정 화면으로 이동
fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

// 갤러리 런처
@Composable
fun rememberGalleryPickerLauncher(
    onImagePicked: (Uri?) -> Unit
): ManagedActivityResultLauncher<String, Uri?> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = onImagePicked
    )
}

// 권한 요청 런처
@Composable
fun rememberGalleryPermissionLauncher(
    onPermissionGranted: () -> Unit
): ManagedActivityResultLauncher<String, Boolean> {
    val context = LocalContext.current
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("PermissionCheck", "권한 승인 여부: $isGranted")
        if (isGranted) {
            onPermissionGranted()
        } else {
            Toast.makeText(context, "사진 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
