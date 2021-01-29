package com.ezike.tobenna.starwarssearch.character_search.ui.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ezike.tobenna.starwarssearch.character_search.R
import com.ezike.tobenna.starwarssearch.character_search.databinding.FragmentSearchBinding
import com.ezike.tobenna.starwarssearch.character_search.navigation.NavigationDispatcher
import com.ezike.tobenna.starwarssearch.character_search.presentation.SearchComponentManager
import com.ezike.tobenna.starwarssearch.character_search.presentation.SearchStateMachine
import com.ezike.tobenna.starwarssearch.character_search.presentation.search.SearchViewState
import com.ezike.tobenna.starwarssearch.character_search.views.search.SearchBarView
import com.ezike.tobenna.starwarssearch.character_search.views.search.SearchHistoryView
import com.ezike.tobenna.starwarssearch.character_search.views.search.SearchResultView
import com.ezike.tobenna.starwarssearch.core.ext.lazyText
import com.ezike.tobenna.starwarssearch.core.ext.onBackPress
import com.ezike.tobenna.starwarssearch.core.ext.viewScope
import com.ezike.tobenna.starwarssearch.presentation.mvi.ViewIntent
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CharacterSearchComponentManager @Inject constructor(
    searchStateMachine: SearchStateMachine
) : SearchComponentManager(searchStateMachine)

object LoadSearchHistory : ViewIntent

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    @Inject
    lateinit var navigator: NavigationDispatcher

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val componentManager: CharacterSearchComponentManager by viewModels()

        val binding = FragmentSearchBinding.bind(view)

        handleBackPress(binding)

        SearchBarView(binding.searchBar, viewScope, componentManager::processIntent)

        componentManager.run {
            subscribe(
                SearchHistoryView(
                    binding.recentSearch,
                    ::processIntent,
                    navigator::openCharacterDetail
                )
            ) { screenState: SearchViewState -> screenState.searchHistoryState }
            subscribe(
                SearchResultView(
                    binding.searchResult,
                    ::processIntent,
                    binding.searchBar.lazyText,
                    navigator::openCharacterDetail
                )
            ) { screenState: SearchViewState -> screenState.searchResultState }

            disposeAll(viewLifecycleOwner)
        }
    }

    private fun handleBackPress(binding: FragmentSearchBinding) {
        onBackPress {
            if (binding.searchBar.text.isNotEmpty()) {
                binding.searchBar.text.clear()
            } else {
                requireActivity().finish()
            }
        }
    }
}
