package com.example.diaviseo.ui.components.my

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.diaviseo.R
import com.example.diaviseo.ui.theme.DiaViseoColors
import com.example.diaviseo.ui.theme.*

@Composable
fun MyHeaderSection(
    userName: String = "김디아",
    navController: NavHostController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = buildAnnotatedString {
                    append(userName)
                    withStyle (style = SpanStyle(color = DiaViseoColors.Basic)) {
                        append(" 님")
                    }
                },
                style = bold20,
                color = DiaViseoColors.Main1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "오늘도 건강하세요!",
                style = bold20,
                color = DiaViseoColors.Unimportant
            )
            Spacer(modifier = Modifier.height(5.dp))
            MyProfileEditCard(navController = navController)
        }

        Image(
            painter = painterResource(id = R.drawable.charac_main_nontext),
            contentDescription = "다이아비서 캐릭터",
            modifier = Modifier.size(64.dp)
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun MyHeaderSectionPreview() {
//    Column(modifier = Modifier.padding(16.dp)) {
//        MyHeaderSection()
//    }
//}
