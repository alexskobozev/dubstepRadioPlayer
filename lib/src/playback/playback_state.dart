/// The user-observable playback state — drives the home-screen button
/// affordance, the in-app status text, and the lock-screen notification.
///
/// Sealed so the `Playing` variant can carry the current track title
/// directly (no out-of-band field, no "playing but title is null" moment).
library;

sealed class PlaybackState {
  const PlaybackState();
}

class IdleState extends PlaybackState {
  const IdleState();
  @override
  String toString() => 'PlaybackState.idle';
}

class LoadingState extends PlaybackState {
  const LoadingState();
  @override
  String toString() => 'PlaybackState.loading';
}

class PlayingState extends PlaybackState {
  final String displayTitle;
  final String? displayArtist;
  const PlayingState({required this.displayTitle, this.displayArtist});

  PlayingState copyWith({String? displayTitle, String? displayArtist}) {
    return PlayingState(
      displayTitle: displayTitle ?? this.displayTitle,
      displayArtist: displayArtist ?? this.displayArtist,
    );
  }

  @override
  String toString() =>
      'PlaybackState.playing(title=$displayTitle, artist=$displayArtist)';
}

class ErrorState extends PlaybackState {
  final Object cause;
  const ErrorState(this.cause);
  @override
  String toString() => 'PlaybackState.error($cause)';
}

const PlaybackState kInitialState = IdleState();
