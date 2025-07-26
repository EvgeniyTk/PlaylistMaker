package com.example.playlistmaker.library.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.PlaylistsInteractor
import com.example.playlistmaker.library.domain.model.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val interactor: PlaylistsInteractor
) : ViewModel() {

    private val _selectedUri = MutableLiveData<Uri?>()
    val selectedUri: LiveData<Uri?> = _selectedUri
    private val _saveCompleted = MutableLiveData<String>()
    val saveCompleted: LiveData<String> = _saveCompleted

    fun setUri(uri: Uri) {
        _selectedUri.value = uri
    }

    fun savePlaylist(name: String, description: String) {
        viewModelScope.launch {
            val uri = selectedUri.value
            val savedPath = if (uri != null) {
                interactor.saveImageToPrivateStorage(uri, name)
            } else {
                null
            }

            val playlist = Playlist(
                playlistName = name,
                playlistDescription = description,
                imagePath = savedPath
            )

            interactor.addPlaylist(playlist)
            _saveCompleted.postValue(name)
        }
    }
}