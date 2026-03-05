
package com.music.pixelmusic

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.music.pixelmusic.ui.components.PlayerBar
import com.music.pixelmusic.ui.screens.SongListScreen
import com.music.pixelmusic.ui.theme.MusicPlayerTheme
import com.music.pixelmusic.viewmodel.MainViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: MainViewModel = viewModel()
                    val context = LocalContext.current

                    LaunchedEffect(Unit) {
                        viewModel.initializePlayer(context)
                    }

                    LaunchedEffect(Unit) {
                        while (true) {
                            viewModel.updateCurrentPosition()
                            delay(500)
                        }
                    }

                    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_AUDIO
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }
                    val permissionState = rememberPermissionState(permission = permission)

                    LaunchedEffect(permissionState.status.isGranted) {
                        if (permissionState.status.isGranted) {
                            viewModel.loadSongs(context)
                        }
                    }

                    if (permissionState.status.isGranted) {
                        val songs by viewModel.songs.collectAsStateWithLifecycle()
                        val currentSong by viewModel.currentSong.collectAsStateWithLifecycle()
                        val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
                        val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()

                        Column {
                            SongListScreen(
                                songs = songs,
                                currentSong = currentSong,
                                onSongClick = { song -> viewModel.playSong(song) },
                                modifier = Modifier.weight(1f)
                            )
                            PlayerBar(
                                currentSong = currentSong,
                                isPlaying = isPlaying,
                                currentPosition = currentPosition,
                                onPlayPause = { viewModel.playPause() },
                                onSkipPrevious = { viewModel.skipToPrevious() },
                                onSkipNext = { viewModel.skipToNext() },
                                onSeek = { pos -> viewModel.seekTo(pos) },
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("PixelMusic needs access to your audio files")
                            Spacer(modifier = Modifier.height(16.dp))
                            when {
                                permissionState.status.shouldShowRationale -> {
                                    Text("We need permission to read your music files.")
                                    Button(onClick = { permissionState.launchPermissionRequest() }) {
                                        Text("Grant Permission")
                                    }
                                }
                                else -> {
                                    Text("Permission is required to play music.")
                                    Button(onClick = { permissionState.launchPermissionRequest() }) {
                                        Text("Grant Permission")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
