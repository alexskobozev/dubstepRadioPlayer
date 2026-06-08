import 'package:flutter/material.dart';

const Color kPrimary = Color(0xFF1D1D1D);
const Color kPrimaryDark = Color(0xFF1D1D1D);
const Color kAccent = Color(0xFFFAFAFA);
const Color kTextPrimary = Color(0xFFFAFAFA);
const Color kWindowBackground = Color(0xFF2A2A2A);
const Color kTextSecondary = Color(0xFF00CCFF);

ThemeData buildDubstepTheme() {
  const colorScheme = ColorScheme.dark(
    primary: kPrimary,
    secondary: kTextSecondary,
    surface: kWindowBackground,
    onSurface: kTextPrimary,
  );

  return ThemeData(
    brightness: Brightness.dark,
    colorScheme: colorScheme,
    scaffoldBackgroundColor: kWindowBackground,
    appBarTheme: const AppBarTheme(
      backgroundColor: kWindowBackground,
      foregroundColor: Colors.white,
      elevation: 0,
      centerTitle: false,
    ),
    textTheme: const TextTheme(
      bodyMedium: TextStyle(color: kTextPrimary),
    ),
  );
}
