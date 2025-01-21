package com.mallow.newsapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

// Set of Material typography styles to start with
val Typography = Typography(

    labelMedium = getTextFont().copy(
        fontWeight = FontWeight.Medium,
        fontSize = Sp14,
        color = Color.White
    ),

    bodyMedium = getTextFont().copy(
        fontWeight = FontWeight.Normal,
        fontSize = Sp14,
        color = Color333333
    ),

    bodySmall = getTextFont().copy(
        fontWeight = FontWeight.Normal,
        fontSize = Sp12,
        color = Color333333
    ),

    bodyLarge = getTextFont().copy(
        fontWeight = FontWeight.Normal,
        fontSize = Sp16,
        color = Color333333
    ),

    displayLarge = getTextFont().copy(
        fontWeight = FontWeight.Bold,
        fontSize = Sp24,
        color = Color.White
    ),
    displayMedium = getTextFont().copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = Sp16,
        color = Color.White
    ),

    titleLarge = getTextFont().copy(
        fontWeight = FontWeight.Bold,
        fontSize = Sp24,
        color = Color.White
    ),
    titleMedium = getTextFont().copy(
        fontWeight = FontWeight.Normal,
        fontSize = Sp16,
        color = Color.White
    ),

    )

val ButtonStyle = getTextFont().copy(
    fontWeight = FontWeight.SemiBold,
    fontSize = Sp16,
    color = Color.White
)

private fun getTextFont() = TextStyle(
    fontFamily = Inter
)