package com.example.features.mainscreen.utils

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerManager(
    context: Context,
    private val coroutineScope: CoroutineScope
) {
    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context.applicationContext).build()
    private var positionUpdateJob: Job? = null

    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.IDLE)
    val playbackState: StateFlow<PlaybackState> = _playbackState

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                _playbackState.value = when (playbackState) {
                    Player.STATE_READY -> {
                        val currentDuration = exoPlayer.duration
                        if (currentDuration > 0) {
                            _duration.value = currentDuration
                        }
                        PlaybackState.READY
                    }
                    Player.STATE_BUFFERING -> PlaybackState.BUFFERING
                    Player.STATE_ENDED -> PlaybackState.ENDED
                    else -> PlaybackState.IDLE
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playbackState.value = if (isPlaying) PlaybackState.PLAYING
                else PlaybackState.PAUSED
            }
        })
    }

    fun preparePlayer(url: String) {
        exoPlayer.setMediaItem(MediaItem.fromUri(url))
        exoPlayer.prepare()
        exoPlayer.play()
        startPositionUpdates()
    }

    fun playPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    private fun startPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = coroutineScope.launch {
            while (true) {
                _currentPosition.value = exoPlayer.currentPosition
                delay(1000)
            }
        }
    }

    fun reset() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        positionUpdateJob?.cancel()
        _currentPosition.value = 0L
        _duration.value = 0L
        _playbackState.value = PlaybackState.IDLE
    }

    fun restartPlayer() {
        seekTo(0)
        exoPlayer.play()
    }

    fun hasEnded(): Boolean {
        val currentPos = currentPosition.value
        val currentDuration = duration.value
        return currentPos >= currentDuration - 1000 && currentDuration > 0
    }

    fun release() {
        exoPlayer.release()
        positionUpdateJob?.cancel()
        _currentPosition.value = 0L
        _duration.value = 0L
        _playbackState.value = PlaybackState.IDLE
    }
}

enum class PlaybackState {
    IDLE, READY, BUFFERING, PLAYING, PAUSED, ENDED
}
