/// Pure-Dart parsing of ICY `StreamTitle` frames into a display-ready
/// `TrackMetadata` per `data-model.md §E5`.
///
/// Rules:
///   - If `rawTitle` is null or empty/whitespace → fallback to station name
///     in [TrackMetadata.displayTitle]; artist is null.
///   - If `rawTitle` contains `' - '` → first segment is artist, remainder
///     is title (rejoin if multiple `' - '` so "A - B - C" → artist "A",
///     title "B - C").
///   - Else: entire raw string is the title; artist is null.
library;

class TrackMetadata {
  final String? rawTitle;
  final String? displayArtist;
  final String displayTitle;

  const TrackMetadata({
    required this.rawTitle,
    required this.displayArtist,
    required this.displayTitle,
  });

  bool get hasIcyTitle => rawTitle != null && rawTitle!.trim().isNotEmpty;

  @override
  bool operator ==(Object other) =>
      other is TrackMetadata &&
      other.rawTitle == rawTitle &&
      other.displayArtist == displayArtist &&
      other.displayTitle == displayTitle;

  @override
  int get hashCode => Object.hash(rawTitle, displayArtist, displayTitle);

  @override
  String toString() =>
      'TrackMetadata(raw=$rawTitle, artist=$displayArtist, title=$displayTitle)';
}

TrackMetadata parseIcyTitle(
  String? rawTitle, {
  required String stationFallback,
}) {
  final trimmed = rawTitle?.trim();
  if (trimmed == null || trimmed.isEmpty) {
    return TrackMetadata(
      rawTitle: rawTitle,
      displayArtist: null,
      displayTitle: stationFallback,
    );
  }

  const sep = ' - ';
  final idx = trimmed.indexOf(sep);
  if (idx <= 0 || idx + sep.length >= trimmed.length) {
    return TrackMetadata(
      rawTitle: rawTitle,
      displayArtist: null,
      displayTitle: trimmed,
    );
  }

  final artist = trimmed.substring(0, idx).trim();
  final title = trimmed.substring(idx + sep.length).trim();
  if (artist.isEmpty || title.isEmpty) {
    return TrackMetadata(
      rawTitle: rawTitle,
      displayArtist: null,
      displayTitle: trimmed,
    );
  }

  return TrackMetadata(
    rawTitle: rawTitle,
    displayArtist: artist,
    displayTitle: title,
  );
}
