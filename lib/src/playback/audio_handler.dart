/// Thin `BaseAudioHandler` bridge between the OS and our
/// [PlaybackController]. See
/// `specs/002-stream-playback-migration/contracts/audio-handler.md`.
///
/// Has no retry, no state-transition decisions, and no metadata parsing
/// of its own. It mirrors controller state outward and routes external
/// inputs (notification, lock-screen, Bluetooth, headset) inward.
library;

import 'package:audio_service/audio_service.dart';

import 'playback_controller.dart';
import 'playback_state.dart' as ps;

class DubstepAudioHandler extends BaseAudioHandler {
  final PlaybackController controller;
  final Uri? artUri;

  DubstepAudioHandler(this.controller, {this.artUri}) {
    controller.state.addListener(_publishPlaybackState);
    controller.buffering.addListener(_publishPlaybackState);
    controller.currentItem.addListener(_publishMediaItem);
    // Push initial snapshots so the OS knows what we look like.
    _publishPlaybackState();
    _publishMediaItem();
  }

  // --------------------------------------------------------------
  //  External -> controller
  // --------------------------------------------------------------

  @override
  Future<void> play() => controller.play();

  @override
  Future<void> stop() => controller.stop();

  // --------------------------------------------------------------
  //  Controller -> external (PlaybackState + MediaItem broadcasts)
  // --------------------------------------------------------------

  void _publishPlaybackState() {
    final s = controller.state.value;
    switch (s) {
      case ps.IdleState():
        playbackState.add(PlaybackState(
          controls: const [MediaControl.play],
          systemActions: const {MediaAction.play},
          androidCompactActionIndices: const [0],
          processingState: AudioProcessingState.idle,
          playing: false,
        ));
        break;
      case ps.LoadingState():
        playbackState.add(PlaybackState(
          controls: const [MediaControl.stop],
          systemActions: const {MediaAction.stop},
          androidCompactActionIndices: const [0],
          processingState: AudioProcessingState.loading,
          playing: true,
        ));
        break;
      case ps.PlayingState():
        playbackState.add(PlaybackState(
          controls: const [MediaControl.stop],
          systemActions: const {MediaAction.stop},
          androidCompactActionIndices: const [0],
          processingState: controller.buffering.value
              ? AudioProcessingState.buffering
              : AudioProcessingState.ready,
          playing: true,
        ));
        break;
      case ps.ErrorState(:final cause):
        playbackState.add(PlaybackState(
          controls: const [MediaControl.play],
          systemActions: const {MediaAction.play},
          androidCompactActionIndices: const [0],
          processingState: AudioProcessingState.error,
          playing: false,
          errorMessage: cause.toString(),
        ));
        break;
    }
  }

  void _publishMediaItem() {
    final item = controller.currentItem.value;
    if (item == null) {
      mediaItem.add(null);
      return;
    }
    mediaItem.add(MediaItem(
      id: item.url,
      album: controller.stationFallback,
      title: item.title,
      artist: item.artist,
      artUri: artUri,
    ));
  }
}
