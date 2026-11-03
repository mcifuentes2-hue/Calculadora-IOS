package com.utp.calculadora

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun ShowHistory(
    viewModel: CalculatorViewModel,
    navController: NavController
) {
    val history = viewModel.history
    val orange = colorResource(R.color.operatorButtom)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .safeDrawingPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { navController.popBackStack() }) {
                Text(
                    text = stringResource(R.string.back),
                    color = orange,
                    fontSize = 17.sp
                )
            }
            Text(
                text = stringResource(R.string.historyTitle),
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(
                onClick = { viewModel.clearHistory() },
                enabled = history.isNotEmpty()
            ) {
                Text(
                    text = stringResource(R.string.clearHistory),
                    color = if (history.isNotEmpty()) orange else colorResource(R.color.gray),
                    fontSize = 17.sp
                )
            }
        }

        if (history.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.historyEmpty),
                    color = colorResource(R.color.gray),
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(history) { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(colorResource(R.color.historyCard))
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = item.expression,
                            color = colorResource(R.color.gray),
                            fontSize = 17.sp,
                            textAlign = TextAlign.End
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = item.result,
                            color = Color.White,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Light,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}
