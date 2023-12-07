package com.rams.ted.callblocker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.rams.ted.callblocker.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)



val satoshiFont = FontFamily(
    Font(R.font.satoshi_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
    Font(R.font.satoshi_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.satoshi_black, weight = FontWeight.Black, style = FontStyle.Normal),
    Font(R.font.satoshi_blackitalic, weight = FontWeight.Black, style = FontStyle.Italic),
    Font(R.font.satoshi_medium, weight = FontWeight.Medium, style = FontStyle.Normal),
    Font(R.font.satoshi_mediumitalic, weight = FontWeight.Medium, style = FontStyle.Italic),
    Font(R.font.satoshi_lightitalic, weight = FontWeight.Light, style = FontStyle.Italic),
    Font(R.font.satoshi_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
    Font(R.font.satoshi_bolditalic, weight = FontWeight.Bold, style = FontStyle.Italic),
)


val dmSansFont = FontFamily(
    Font(R.font.dmsans_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
    Font(R.font.dmsans_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.dmsans_black, weight = FontWeight.Black, style = FontStyle.Normal),
    Font(R.font.dmsans_blackitalic, weight = FontWeight.Black, style = FontStyle.Italic),
    Font(R.font.dmsans_medium, weight = FontWeight.Medium, style = FontStyle.Normal),
    Font(R.font.dmsans_mediumitalic, weight = FontWeight.Medium, style = FontStyle.Italic),
    Font(R.font.dmsans_lightitalic, weight = FontWeight.Light, style = FontStyle.Italic),
    Font(R.font.dmsans_light, weight = FontWeight.Light, style = FontStyle.Normal),
    Font(R.font.dmsans_thinitalic, weight = FontWeight.Thin, style = FontStyle.Italic),
    Font(R.font.dmsans_thin, weight = FontWeight.Thin, style = FontStyle.Normal),
    Font(R.font.dmsans_extralightitalic, weight = FontWeight.ExtraLight, style = FontStyle.Italic),
    Font(R.font.dmsans_extralight, weight = FontWeight.ExtraLight, style = FontStyle.Normal),
    Font(R.font.dmsans_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
    Font(R.font.dmsans_bolditalic, weight = FontWeight.Bold, style = FontStyle.Italic),
    Font(R.font.dmsans_semibold, weight = FontWeight.SemiBold, style = FontStyle.Normal),
    Font(R.font.dmsans_semibolditalic, weight = FontWeight.SemiBold, style = FontStyle.Italic),
    Font(R.font.dmsans_extrabold, weight = FontWeight.ExtraBold, style = FontStyle.Normal),
    Font(R.font.dmsans_extrabolditalic, weight = FontWeight.ExtraBold, style = FontStyle.Italic),
)
