import 'package:flutter/material.dart';

import 'l10n/strings.dart';
import 'playback/endpoint_catalog.dart';
import 'playback/playback_controller.dart';
import 'theme.dart';

/// Bitrate options exposed in the gear menu. Derived from
/// `endpoint_catalog.dart` so the dialog and the controller can never
/// disagree on URLs.
const List<RadioEndpoint> kBitrateOptions = kRadioEndpoints;

Future<void> showBitrateDialog(
  BuildContext context, {
  required PlaybackController controller,
}) {
  final currentUrl = controller.currentUrl;
  return showDialog<void>(
    context: context,
    builder: (ctx) => SimpleDialog(
      title: Text(kStrings['choose_bitrate']!),
      children: [
        for (final option in kBitrateOptions)
          SimpleDialogOption(
            onPressed: () {
              Navigator.pop(ctx);
              if (option.url == currentUrl) return;
              controller.setUrl(option.url);
            },
            child: Row(
              children: [
                SizedBox(
                  width: 24,
                  child: option.url == currentUrl
                      ? const Icon(Icons.check,
                          size: 18, color: kTextPrimary)
                      : null,
                ),
                Text(kStrings[option.labelKey]!),
              ],
            ),
          ),
      ],
    ),
  );
}
