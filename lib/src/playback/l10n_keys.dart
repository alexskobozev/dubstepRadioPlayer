/// Pure-Dart bridge between bitrate values and the kStrings keys.
/// Lives in its own tiny file so endpoint_catalog.dart doesn't depend on
/// flutter/strings.dart (keeps the catalog test pure-Dart).
const Map<int, String> kLabelKeyByBitrate = {
  24: 'b24kbps',
  64: 'b64kbps',
  128: 'b128kbps',
  256: 'b256kbps',
};
