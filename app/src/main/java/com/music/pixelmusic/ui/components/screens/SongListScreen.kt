package com.music.pixelmusic.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.music.pixelmusic.model.Song
import com.music.pixelmusic.ui.components.SongItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongListScreen(
    songs: List<Song>,
    currentSong: Song?,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PixelMusic") }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        if (songs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(songs, key = { it.id }) { song ->
                    SongItem(
                        song = song,
                        isNowPlaying = currentSong?.id == song.id,
                        onClick = { onSongClick(song) }
                    )
                }
            }
        }
    }
}