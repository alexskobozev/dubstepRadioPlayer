import 'package:flutter/material.dart';

import 'bitrate_dialog.dart';
import 'l10n/strings.dart';
import 'playback/endpoint_catalog.dart';
import 'playback/playback_controller.dart';
import 'playback/playback_state.dart';
import 'theme.dart';

class HomeScreen extends StatelessWidget {
  final PlaybackController controller;
  const HomeScreen({super.key, required this.controller});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(kStrings['app_name']!),
        actions: [
          IconButton(
            icon: const Icon(Icons.settings),
            tooltip: kStrings['action_settings']!,
            onPressed: () =>
                showBitrateDialog(context, controller: controller),
          ),
        ],
      ),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(8),
          child: ValueListenableBuilder<PlaybackState>(
            valueListenable: controller.state,
            builder: (context, state, _) {
              final statusIcon = _statusIcon(state);
              final statusText = _statusText(state);
              final isActive = state is LoadingState || state is PlayingState;
              return Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  Row(
                    children: [
                      Icon(statusIcon, size: 24, color: kTextPrimary),
                      const SizedBox(width: 8),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Text(
                              statusText,
                              style: const TextStyle(
                                fontSize: 18,
                                color: kTextPrimary,
                              ),
                              maxLines: 2,
                              overflow: TextOverflow.ellipsis,
                            ),
                            if (state is LoadingState)
                              ValueListenableBuilder<bool>(
                                valueListenable: controller.slowLoading,
                                builder: (context, slow, _) => slow
                                    ? const Text(
                                        'Still connecting…',
                                        style: TextStyle(
                                          fontSize: 13,
                                          color: kTextPrimary,
                                        ),
                                      )
                                    : const SizedBox.shrink(),
                              ),
                            if (state is PlayingState)
                              ValueListenableBuilder<bool>(
                                valueListenable: controller.buffering,
                                builder: (context, buffering, _) => buffering
                                    ? const Text(
                                        'Buffering…',
                                        style: TextStyle(
                                          fontSize: 13,
                                          color: kTextPrimary,
                                        ),
                                      )
                                    : const SizedBox.shrink(),
                              ),
                          ],
                        ),
                      ),
                    ],
                  ),
                  Expanded(
                    child: Center(
                      child: Padding(
                        padding: const EdgeInsets.all(36),
                        child: Image.asset(
                          'assets/images/img_logo.png',
                          key: const Key('home_logo'),
                          semanticLabel: kStrings['description']!,
                        ),
                      ),
                    ),
                  ),
                  TextButton(
                    onPressed: isActive ? null : controller.play,
                    style: TextButton.styleFrom(
                      foregroundColor: kTextPrimary,
                      padding: const EdgeInsets.symmetric(vertical: 12),
                    ),
                    child: Text(
                      kStrings['play']!.toUpperCase(),
                      style: const TextStyle(
                        fontSize: 36,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  const SizedBox(height: 8),
                  TextButton(
                    onPressed: isActive ? controller.stop : null,
                    style: TextButton.styleFrom(
                      foregroundColor: kTextPrimary,
                      padding: const EdgeInsets.symmetric(vertical: 12),
                    ),
                    child: Text(
                      kStrings['stop']!.toUpperCase(),
                      style: const TextStyle(fontSize: 24),
                    ),
                  ),
                  const SizedBox(height: 16),
                ],
              );
            },
          ),
        ),
      ),
    );
  }

  IconData _statusIcon(PlaybackState state) {
    switch (state) {
      case LoadingState():
        return Icons.hourglass_empty;
      case PlayingState():
        return Icons.graphic_eq;
      case ErrorState():
        return Icons.error_outline;
      case IdleState():
        return Icons.stop;
    }
  }

  String _statusText(PlaybackState state) {
    switch (state) {
      case LoadingState():
        final ep = endpointForUrl(controller.currentUrl);
        return 'Loading ${ep.bitrateKbps} kbps…';
      case PlayingState(:final displayTitle, :final displayArtist):
        if (displayArtist != null && displayArtist.isNotEmpty) {
          return '$displayArtist — $displayTitle';
        }
        return displayTitle;
      case ErrorState():
        return kStrings['cannot_connect']!;
      case IdleState():
        return kStrings['nothing_to_play']!;
    }
  }
}
