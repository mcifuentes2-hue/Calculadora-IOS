package com.utp.calculadora

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlin.math.abs

private val ButtonGap = 12.dp
private val ScreenPadding = 16.dp


@Composable
fun ShowCalculator(
    viewModel: CalculatorViewModel = viewModel(),
    navController: NavController
) {
    val hasError = viewModel.hasError.value
    val displayText = if (hasError) stringResource(R.string.msgError) else viewModel.displayText

    val numberColor = colorResource(R.color.numberButtom)
    val operatorColor = colorResource(R.color.operatorButtom)
    val controlColor = colorResource(R.color.controlButtom)

    val divide = stringResource(R.string.divideSymbol)
    val multiply = stringResource(R.string.multiplySymbol)
    val subtraction = stringResource(R.string.subtractionSymbol)
    val add = stringResource(R.string.addSymbol)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .safeDrawingPadding()
    ) {
        val buttonSize = (maxWidth - ScreenPadding * 2 - ButtonGap * 3) / 4

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = ScreenPadding)
                .padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { navController.navigate("history") }) {
                    Text(
                        text = stringResource(R.string.historyTitle),
                        color = operatorColor,
                        fontSize = 17.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .pointerInput(viewModel) {
                        var deleted = false
                        detectHorizontalDragGestures(
                            onDragStart = { deleted = false },
                            onHorizontalDrag = { _, dragAmount ->
                                if (!deleted && abs(dragAmount) > 4f) {
                                    deleted = true
                                    viewModel.manageClickButtons("≪")
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    text = displayText,
                    fontSize = displayFontSize(displayText.length),
                    fontWeight = FontWeight.Light,
                    color = if (hasError) colorResource(R.color.red) else Color.White,
                    textAlign = TextAlign.End,
                    softWrap = false,
                    maxLines = 1
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(ButtonGap)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGap)
                ) {
                    ShowButton(viewModel.clearLabel, controlColor, viewModel, buttonSize, Color.Black)
                    ShowButton("+/−", controlColor, viewModel, buttonSize, Color.Black)
                    ShowButton("%", controlColor, viewModel, buttonSize, Color.Black)
                    ShowOperatorButton(divide, operatorColor, viewModel, buttonSize)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGap)
                ) {
                    ShowButton("7", numberColor, viewModel, buttonSize)
                    ShowButton("8", numberColor, viewModel, buttonSize)
                    ShowButton("9", numberColor, viewModel, buttonSize)
                    ShowOperatorButton(multiply, operatorColor, viewModel, buttonSize)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGap)
                ) {
                    ShowButton("4", numberColor, viewModel, buttonSize)
                    ShowButton("5", numberColor, viewModel, buttonSize)
                    ShowButton("6", numberColor, viewModel, buttonSize)
                    ShowOperatorButton(subtraction, operatorColor, viewModel, buttonSize)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGap)
                ) {
                    ShowButton("1", numberColor, viewModel, buttonSize)
                    ShowButton("2", numberColor, viewModel, buttonSize)
                    ShowButton("3", numberColor, viewModel, buttonSize)
                    ShowOperatorButton(add, operatorColor, viewModel, buttonSize)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGap)
                ) {
                    ShowButton("0", numberColor, viewModel, buttonSize, wide = true)
                    ShowButton(",", numberColor, viewModel, buttonSize)
                    ShowButton("=", operatorColor, viewModel, buttonSize)
                }
            }
        }
    }
}

@Composable
fun ShowOperatorButton(
    symbol: String,
    color: Color,
    viewModel: CalculatorViewModel,
    buttonSize: Dp
) {
    val isActive = viewModel.isOperatorActive(symbol)
    ShowButton(
        text = symbol,
        color = if (isActive) Color.White else color,
        viewModel = viewModel,
        buttonSize = buttonSize,
        textColor = if (isActive) color else Color.White
    )
}

@Composable
fun ShowButton(
    text: String,
    color: Color,
    viewModel: CalculatorViewModel,
    buttonSize: Dp,
    textColor: Color = Color.White,
    wide: Boolean = false
) {
    val modifier = if (wide) {
        Modifier
            .width(buttonSize * 2 + ButtonGap)
            .height(buttonSize)
    } else {
        Modifier.size(buttonSize)
    }

    Box(
        modifier = modifier
            .clip(if (wide) RoundedCornerShape(50) else CircleShape)
            .background(color)
            .clickable { viewModel.manageClickButtons(text) },
        contentAlignment = if (wide) Alignment.CenterStart else Alignment.Center
    ) {
        Text(
            modifier = if (wide) Modifier.padding(start = buttonSize * 0.4f) else Modifier,
            text = text,
            color = textColor,
            fontSize = if (text.length > 1 || text == "%") 32.sp else 38.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

private fun displayFontSize(length: Int) = when {
    length <= 6 -> 88.sp
    length == 7 -> 78.sp
    length == 8 -> 70.sp
    length == 9 -> 62.sp
    length == 10 -> 56.sp
    length == 11 -> 51.sp
    length == 12 -> 47.sp
    length == 13 -> 43.sp
    else -> 38.sp
}


@Preview(showBackground = true)
@Composable
fun TestView() {
    val navController = rememberNavController()
    ShowCalculator(navController = navController)
}
