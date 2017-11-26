package com.wishnewjam.dubstepfm

import java.io.IOException
import java.net.URL
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.reflect.KClass

/**
 * From http://uniqueculture.net/2010/11/stream-metadata-plain-java/
 */
class IcyStreamMeta(streamUrl: URL) {
    private var streamUrl: URL? = null
    private var metadata: HashMap<String, String> = HashMap()

    /**
     * Get artist using stream's title
     *
     * @return String
     * @throws IOException
     */
    val artist: String
        @Throws(Throwable::class)
        get() {
            if (metadata.isEmpty()) {
                retrieveMetadata()
            }

            if (!metadata.containsKey("StreamTitle"))
                return ""

            val streamTitle = metadata["StreamTitle"]
            val title = streamTitle?.substring(0, streamTitle.indexOf("-"))
            return title?.trim { it <= ' ' } ?: ""
        }

    /**
     * Get title using stream's title
     *
     * @return String
     * @throws IOException
     */
    val title: String
        @Throws(Throwable::class)
        get() {
            if (metadata.isEmpty()) {
                retrieveMetadata()
            }

            if (!metadata.containsKey("StreamTitle"))
                return ""

            val streamTitle = metadata["StreamTitle"]
            val artist = streamTitle?.substring(streamTitle.indexOf("-") + 1)
            return artist?.trim { it <= ' ' } ?: ""
        }

    init {
        setStreamUrl(streamUrl)

    }

    @Throws(Exception::class)
    fun retrieveMetadata() {
        var metaDataOffset = 0
        streamUrl?.openConnection()?.let {
            it.setRequestProperty("Icy-MetaData", "1")
            it.setRequestProperty("Connection", "close")
            it.setRequestProperty("Accept", null)
            it.connect()

            val headers = it.headerFields
            val stream = it.getInputStream()

            if (headers.containsKey("icy-metaint")) {
                // Headers are sent via HTTP
                metaDataOffset = Integer.parseInt(headers["icy-metaint"]?.get(0))
            } else {
                // Headers are sent within a stream
                val strHeaders = StringBuilder()
                var c = 0
                while (c >= 0) {
                    c = stream.read()
                    strHeaders.append(c)
                    if (strHeaders.length > 5 && strHeaders.substring(strHeaders.length - 4, strHeaders.length) == "\r\n\r\n") {
                        // end of headers
                        break
                    }
                }

                // Match headers to get metadata offset within a stream
                val p = Pattern.compile("\\r\\n(icy-metaint):\\s*(.*)\\r\\n")
                val m = p.matcher(strHeaders.toString())
                if (m.find()) {
                    metaDataOffset = Integer.parseInt(m.group(2))
                }
            }

            // In case no data was sent
            if (metaDataOffset == 0) {
                return
            }

            // Read metadata
            var b = 0
            var count = 0
            var metaDataLength = 4080 // 4080 is the max length
            var inData: Boolean
            val metaData = StringBuilder()
            // Stream position should be either at the beginning or right after headers
            while (b != -1) {
                b = stream.read()
                count++

                // Length of the metadata
                if (count == metaDataOffset + 1) {
                    metaDataLength = b * 16
                }

                inData = count > metaDataOffset + 1 && count < metaDataOffset + metaDataLength
                if (inData) {
                    if (b != 0) {
                        metaData.append(b.toChar())
                    }
                }
                if (count > metaDataOffset + metaDataLength) {
                    break
                }

            }

            // Set the data
            parseMetadata(metaData.toString())

            // Close
            stream.close()

        }
    }

    private fun setStreamUrl(streamUrl: URL) {
        this.metadata.clear()
        this.streamUrl = streamUrl
    }

    private fun parseMetadata(metaString: String): Map<String, String> {
        metadata.clear()
        val metaParts = metaString.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val p = Pattern.compile("^([a-zA-Z]+)=\\'([^\\']*)\\'$")
        var m: Matcher
        for (metaPart in metaParts) {
            m = p.matcher(metaPart)
            if (m.find()) {
                metadata.put(m.group(1), m.group(2))
            }
        }

        return metadata
    }
}
