package com.music.pixelmusic.viewmodel

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.music.pixelmusic.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private var _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private var _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private var _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private var _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private var _player: ExoPlayer? = null

    fun initializePlayer(context: Context) {
        if (_player == null) {
            _player = ExoPlayer.Builder(context).build()
        }
    }

    fun releasePlayer() {
        _player?.release()
        _player = null
    }

    fun loadSongs(context: Context) {
        viewModelScope.launch {
            val songList = mutableListOf<Song>()
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID
            )

            val selection = " != 0"
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null
            )

            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val title = it.getString(titleColumn) ?: "Unknown"
                    val artist = it.getString(artistColumn) ?: "Unknown"
                    val album = it.getString(albumColumn) ?: "Unknown"
                    val duration = it.getLong(durationColumn)
                    val path = it.getString(dataColumn)
                    val albumId = it.getLong(albumIdColumn)

                    val songUri = Uri.parse("file://")
                    val albumArtUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    )

                    songList.add(
                        Song(
                            id = id,
                            title = title,
                            artist = artist,
                            album = album,
                            duration = duration,
                            uri = songUri,
                            albumArtUri = albumArtUri
                        )
                    )
                }
            }
            _songs.value = songList
        }
    }

    fun playSong(song: Song) {
        _player?.apply {
            val mediaItem = MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setUri(song.uri)
                .build()
            setMediaItem(mediaItem)
            prepare()
            play()
        }
        _currentSong.value = song
    }

    fun playPause() {
        _player?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun seekTo(positionMs: Long) {
        _player?.seekTo(positionMs)
    }

    fun skipToNext() {
        val current = _currentSong.value ?: return
        val currentIndex = _songs.value.indexOf(current)
        if (currentIndex + 1 < _songs.value.size) {
            playSong(_songs.value[currentIndex + 1])
        }
    }

    fun skipToPrevious() {
        val current = _currentSong.value ?: return
        val currentIndex = _songs.value.indexOf(current)
        if (currentIndex - 1 >= 0) {
            playSong(_songs.value[currentIndex - 1])
        }
    }

    fun updateCurrentPosition() {
        _player?.let {
            _currentPosition.value = it.currentPosition
        }
    }
}
