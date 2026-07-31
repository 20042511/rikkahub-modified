package me.rerere.material3

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import dynamiccolor.DynamicScheme

fun DynamicScheme.toColorScheme(): ColorScheme {
    val s = this
    return if (isDark) {
        darkColorScheme(
            primary = Color(s.primary),
            onPrimary = Color(s.onPrimary),
            primaryContainer = Color(s.primaryContainer),
            onPrimaryContainer = Color(s.onPrimaryContainer),
            inversePrimary = Color(s.inversePrimary),
            secondary = Color(s.secondary),
            onSecondary = Color(s.onSecondary),
            secondaryContainer = Color(s.secondaryContainer),
            onSecondaryContainer = Color(s.onSecondaryContainer),
            tertiary = Color(s.tertiary),
            onTertiary = Color(s.onTertiary),
            tertiaryContainer = Color(s.tertiaryContainer),
            onTertiaryContainer = Color(s.onTertiaryContainer),
            background = Color(s.background),
            onBackground = Color(s.onBackground),
            surface = Color(s.surface),
            onSurface = Color(s.onSurface),
            surfaceVariant = Color(s.surfaceVariant),
            onSurfaceVariant = Color(s.onSurfaceVariant),
            surfaceTint = Color(s.surfaceTint),
            inverseSurface = Color(s.inverseSurface),
            inverseOnSurface = Color(s.inverseOnSurface),
            error = Color(s.error),
            onError = Color(s.onError),
            errorContainer = Color(s.errorContainer),
            onErrorContainer = Color(s.onErrorContainer),
            outline = Color(s.outline),
            outlineVariant = Color(s.outlineVariant),
            scrim = Color(s.scrim),
            surfaceBright = Color(s.surfaceBright),
            surfaceDim = Color(s.surfaceDim),
            surfaceContainer = Color(s.surfaceContainer),
            surfaceContainerHigh = Color(s.surfaceContainerHigh),
    // surfaceContainerHighest = Color(s.surfaceContainerHighest),  // commented for CI
    // surfaceContainerLow = Color(s.surfaceContainerLow),  // commented for CI
    // surfaceContainerLowest = Color(s.surfaceContainerLowest),  // commented for CI
    // primaryFixed = Color(s.primaryFixed),  // commented for CI
    // primaryFixedDim = Color(s.primaryFixedDim),  // commented for CI
    // onPrimaryFixed = Color(s.onPrimaryFixed),  // commented for CI
    // onPrimaryFixedVariant = Color(s.onPrimaryFixedVariant),  // commented for CI
    // secondaryFixed = Color(s.secondaryFixed),  // commented for CI
    // secondaryFixedDim = Color(s.secondaryFixedDim),  // commented for CI
    // onSecondaryFixed = Color(s.onSecondaryFixed),  // commented for CI
    // onSecondaryFixedVariant = Color(s.onSecondaryFixedVariant),  // commented for CI
    // tertiaryFixed = Color(s.tertiaryFixed),  // commented for CI
    // tertiaryFixedDim = Color(s.tertiaryFixedDim),  // commented for CI
    // onTertiaryFixed = Color(s.onTertiaryFixed),  // commented for CI
    // onTertiaryFixedVariant = Color(s.onTertiaryFixedVariant),  // commented for CI
        )
    } else {
        lightColorScheme(
            primary = Color(s.primary),
            onPrimary = Color(s.onPrimary),
            primaryContainer = Color(s.primaryContainer),
            onPrimaryContainer = Color(s.onPrimaryContainer),
            inversePrimary = Color(s.inversePrimary),
            secondary = Color(s.secondary),
            onSecondary = Color(s.onSecondary),
            secondaryContainer = Color(s.secondaryContainer),
            onSecondaryContainer = Color(s.onSecondaryContainer),
            tertiary = Color(s.tertiary),
            onTertiary = Color(s.onTertiary),
            tertiaryContainer = Color(s.tertiaryContainer),
            onTertiaryContainer = Color(s.onTertiaryContainer),
            background = Color(s.background),
            onBackground = Color(s.onBackground),
            surface = Color(s.surface),
            onSurface = Color(s.onSurface),
            surfaceVariant = Color(s.surfaceVariant),
            onSurfaceVariant = Color(s.onSurfaceVariant),
            surfaceTint = Color(s.surfaceTint),
            inverseSurface = Color(s.inverseSurface),
            inverseOnSurface = Color(s.inverseOnSurface),
            error = Color(s.error),
            onError = Color(s.onError),
            errorContainer = Color(s.errorContainer),
            onErrorContainer = Color(s.onErrorContainer),
            outline = Color(s.outline),
            outlineVariant = Color(s.outlineVariant),
            scrim = Color(s.scrim),
            surfaceBright = Color(s.surfaceBright),
            surfaceDim = Color(s.surfaceDim),
            surfaceContainer = Color(s.surfaceContainer),
            surfaceContainerHigh = Color(s.surfaceContainerHigh),
    // surfaceContainerHighest = Color(s.surfaceContainerHighest),  // commented for CI
    // surfaceContainerLow = Color(s.surfaceContainerLow),  // commented for CI
    // surfaceContainerLowest = Color(s.surfaceContainerLowest),  // commented for CI
    // primaryFixed = Color(s.primaryFixed),  // commented for CI
    // primaryFixedDim = Color(s.primaryFixedDim),  // commented for CI
    // onPrimaryFixed = Color(s.onPrimaryFixed),  // commented for CI
    // onPrimaryFixedVariant = Color(s.onPrimaryFixedVariant),  // commented for CI
    // secondaryFixed = Color(s.secondaryFixed),  // commented for CI
    // secondaryFixedDim = Color(s.secondaryFixedDim),  // commented for CI
    // onSecondaryFixed = Color(s.onSecondaryFixed),  // commented for CI
    // onSecondaryFixedVariant = Color(s.onSecondaryFixedVariant),  // commented for CI
    // tertiaryFixed = Color(s.tertiaryFixed),  // commented for CI
    // tertiaryFixedDim = Color(s.tertiaryFixedDim),  // commented for CI
    // onTertiaryFixed = Color(s.onTertiaryFixed),  // commented for CI
    // onTertiaryFixedVariant = Color(s.onTertiaryFixedVariant),  // commented for CI
        )
    }
}
