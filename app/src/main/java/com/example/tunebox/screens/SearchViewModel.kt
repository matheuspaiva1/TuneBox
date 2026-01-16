package com.example.tunebox.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tunebox.data.models.SpotifyAlbum
import com.example.tunebox.data.models.SpotifyTrack
import com.example.tunebox.data.repository.SpotifyRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val repository: SpotifyRepository,
    private val accessToken: String
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow<List<SearchResultUi>>(emptyList())
    val results: StateFlow<List<SearchResultUi>> = _results.asStateFlow()

    init {
        viewModelScope.launch {
            _query
                .debounce(300)
                .filter { it.isNotBlank() }
                .distinctUntilChanged()
                .collect { query ->
                    search(query)
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        if (newQuery.isBlank()) {
            _results.value = emptyList()
        }
    }

    private fun search(query: String) {
        viewModelScope.launch {
            val searchResults = repository.search(accessToken, query)
            _results.value = searchResults.mapNotNull { item ->
                when (item) {
                    is SpotifyTrack -> SearchResultUi(
                        id = item.id,
                        title = item.name,
                        subtitle = item.artists.joinToString(", ") { it.name },
                        imageUrl = item.album.images.firstOrNull()?.url ?: "",
                        artistImageUrl = item.artists.firstOrNull()?.images?.firstOrNull()?.url ?: "",
                        type = "track"
                    )
                    is SpotifyAlbum -> SearchResultUi(
                        id = item.id,
                        title = item.name,
                        subtitle = item.artists.joinToString(", ") { it.name },
                        imageUrl = item.images.firstOrNull()?.url ?: "",
                        artistImageUrl = item.artists.firstOrNull()?.images?.firstOrNull()?.url ?: "",
                        type = "album"
                    )
                    else -> null
                }
            }
        }
    }
}