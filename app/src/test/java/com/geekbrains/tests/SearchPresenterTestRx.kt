package com.geekbrains.tests

import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.presenter.RepositoryContract
import com.geekbrains.tests.presenter.search.SearchPresenter
import com.geekbrains.tests.repository.GitHubRepository
import com.geekbrains.tests.stubs.SchedulerProviderStub
import com.geekbrains.tests.view.search.ViewSearchContract
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class SearchPresenterTestRx {
    private lateinit var presenter: SearchPresenter

    @Mock
    private lateinit var repository: RepositoryContract

    @Mock
    private lateinit var viewContract: ViewSearchContract

    @Before
    fun setUp(): Unit {
        MockitoAnnotations.initMocks(this)
        presenter = SearchPresenter(viewContract, repository, SchedulerProviderStub())
    }

    companion object {
        private const val SEARCH_QUERY = "search query"
        private const val ERROR_TEXT = "error"
    }

    @Test
    fun searchGitHub_Test(): Unit {
        `when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(Observable.just(SearchResponse(1,
            listOf())))

        presenter.searchGitHub(SEARCH_QUERY)
        verify(repository).searchGithub(SEARCH_QUERY)
    }

    @Test
    fun handleRequestError_Test(): Unit {
        `when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(Observable.error(Throwable(
            ERROR_TEXT)))

        presenter.searchGitHub(SEARCH_QUERY)
        verify(viewContract).displayError(ERROR_TEXT)
    }

    @Test
    fun handleResponseError_TotalCountIsNull(): Unit {
        `when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(Observable.just(SearchResponse(null,
            listOf())))

        presenter.searchGitHub(SEARCH_QUERY)
        verify(viewContract).displayError("Search results or total count are null")
    }

    @Test
    fun handleResponseError_TotalCountIsNull_ViewContractMethodOrder(): Unit {
        `when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(Observable.just(SearchResponse(null,
            listOf())))

        presenter.searchGitHub(SEARCH_QUERY)
        val inOrder = inOrder(viewContract)
        inOrder.verify(viewContract).displayLoading(true)
        inOrder.verify(viewContract).displayError("Search results or total count are null")
        inOrder.verify(viewContract).displayLoading(false)
    }

    @Test
    fun handleResponseSuccess(): Unit {
        `when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(Observable.just(SearchResponse(1,
            listOf())))

        presenter.searchGitHub(SEARCH_QUERY)
        verify(viewContract).displaySearchResults(listOf(), 1)
    }
}