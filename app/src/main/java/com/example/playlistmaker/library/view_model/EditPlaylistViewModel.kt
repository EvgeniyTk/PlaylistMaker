package com.example.playlistmaker.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.PlaylistsInteractor
import com.example.playlistmaker.library.domain.model.Playlist
import kotlinx.coroutines.launch
import androidx.core.net.toUri

class EditPlaylistViewModel(
    private val interactor: PlaylistsInteractor
): NewPlaylistViewModel(interactor) {

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch {
            val playlist = interactor.getPlaylistById(playlistId)
            playlist?.let {
                _playlist.value = it
                setUri(it.imagePath?.toUri())
            }
        }
    }

    override fun savePlaylist(name: String, description: String) {
        val currentPlaylist = playlist.value ?: return
        viewModelScope.launch {
            val uri = selectedUri.value
            val savedPath = if (uri != null && uri.toString() != currentPlaylist.imagePath) {
                interactor.saveImageToPrivateStorage(uri, name)
            } else {
                currentPlaylist.imagePath
            }

            val updatedPlaylist = currentPlaylist.copy(
                playlistName = name,
                playlistDescription = description,
                imagePath = savedPath
            )

            interactor.updatePlaylist(updatedPlaylist)
            _saveCompleted.postValue(name)
        }
    }
}