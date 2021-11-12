package com.wishnewjam.dubstepfm.playback

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.os.Build
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.wishnewjam.dubstepfm.MainActivity
import com.wishnewjam.dubstepfm.data.RadioStreamEntity
import com.wishnewjam.dubstepfm.data.repository.RadioStreamRepositoryImpl
import com.wishnewjam.dubstepfm.notification.NotificationBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainService : MediaLibraryService() {

    @Inject
    lateinit var notificationBuilder: NotificationBuilder

    @Inject
    lateinit var radioStreamRepositoryImpl: RadioStreamRepositoryImpl

    //    private lateinit var mediaCore: MediaCore
    private val session: MediaLibrarySession = createSession()

    private fun createSession(): MediaLibrarySession {
        val intent = Intent(this, MainActivity::class.java)
        val immutableFlag = if (Build.VERSION.SDK_INT >= 23) FLAG_IMMUTABLE else 0
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, immutableFlag or FLAG_UPDATE_CURRENT)
        val player =
            ExoPlayer.Builder(this)
                .setAudioAttributes(AudioAttributes.DEFAULT, true)
                .build()
//        MediaItemTree.initialize(assets)
        return MediaLibrarySession.Builder(this, player, object : MediaLibrarySession.MediaLibrarySessionCallback {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onGetItem(
                session: MediaLibrarySession,
                browser: MediaSession.ControllerInfo,
                mediaId: String
            ): ListenableFuture<LibraryResult<MediaItem>> {
                val metadata =
                    MediaMetadata.Builder()
//                        .setAlbumTitle(album)
//                        .setTitle(title)
//                        .setArtist(artist)
//                        .setGenre(genre)
//                        .setFolderType(folderType)
//                        .setIsPlayable(isPlayable)
                        .build()
                val mediaItem = MediaItem.Builder()
                    .setMediaId(mediaId)
                    .setMediaMetadata(metadata)
                    .setUri(RadioStreamEntity.default.uri) // TODO: 03.11.2021 handle changing
                    .build()
                return Futures.immediateFuture(LibraryResult.ofItem(mediaItem, null))
            }


        })
//            .setMediaItemFiller(CustomMediaItemFiller())
            .setSessionActivity(pendingIntent)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return session
    }

//    override fun onLoadChildren(
//        parentId: String,
//        result: Result<MutableList<MediaBrowserCompat.MediaItem>>,
//    ) {
//        result.sendResult(mutableListOf())
//    }

//    override fun onGetRoot(
//        clientPackageName: String,
//        clientUid: Int,
//        rootHints: Bundle?,
//    ): BrowserRoot {
//        return BrowserRoot("dubstep.fm", null)
//    }

//    override fun onCreate() {
//        super.onCreate()
//
//        mediaCore =
//            MediaCore(
//                notificationBuilder,
//                radioStreamRepo,
//                { id, notification -> startForeground(id, notification) },
//                { stopForeground(false) },
//            )
//        mediaCore.init(this)
//        sessionToken = mediaCore.token
//    }

//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        if (intent != null) {
//            mediaCore.handleIntent(intent)
//        }
//        return Service.START_STICKY
//    }

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
    }

    override fun onDestroy() {
        super.onDestroy()
        session.release()
    }
}
