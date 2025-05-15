package com.example.diaviseo.ui.components.my

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.*

@Composable
fun MyProfileEditCard(
    navController: NavHostController
) {
    Text(
        text = "회원정보 수정",
        style = regular14,
        color = DiaViseoColors.Placeholder,
        modifier = Modifier
            .clickable{ navController.navigate("edit_profile") }
            .padding(top = 4.dp)
    )
}
