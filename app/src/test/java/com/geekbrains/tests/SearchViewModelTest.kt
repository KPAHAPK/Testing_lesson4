package com.geekbrains.tests

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.presenter.RepositoryContract
import com.geekbrains.tests.repository.FakeGitHubRepository
import com.geekbrains.tests.stubs.SchedulerProviderStub
import com.geekbrains.tests.viewmodel.ScreenState
import com.geekbrains.tests.viewmodel.SearchViewModel
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import org.bouncycastle.crypto.agreement.srp.SRP6Client
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SearchViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var searchViewModel: SearchViewModel

    @Mock
    private lateinit var repository: FakeGitHubRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        searchViewModel = SearchViewModel(repository, SchedulerProviderStub())
    }

    companion object {
        private const val SEARCH_QUERY = "some query"
        private const val ERROR_TEXT = "error"
    }

    @Test
    fun searchGitHub_Test(): Unit {
        performSearchGitHub().thenReturn(Observable.just(
            SearchResponse(1, listOf())))
        searchViewModel.searchGitHub(SEARCH_QUERY)
        verify(repository).searchGithub(SEARCH_QUERY)
    }

    @Test
    fun liveData_TestReturnValueIsNull(): Unit {
        val observer = Observer<ScreenState> {}
        val liveData = searchViewModel.subscribeToLiveData()

        performSearchGitHub().thenReturn(
            Observable.just(
                SearchResponse(
                    1,
                    listOf()
                )
            )
        )

        try {
            liveData.observeForever(observer)
            searchViewModel.searchGitHub(SEARCH_QUERY)
            Assert.assertNotNull(liveData.value)
        } finally {
            liveData.removeObserver(observer)
        }
    }

    @Test
    fun liveData_TestReturnValueIsError(): Unit {
        val observer = Observer<ScreenState>(){}
        val liveData = searchViewModel.subscribeToLiveData()
        val error = Throwable(ERROR_TEXT)

        performSearchGitHub().thenReturn(Observable.error(error))

        try {
            liveData.observeForever(observer)
            searchViewModel.searchGitHub(SEARCH_QUERY)
            val value: ScreenState.Error = liveData.value as ScreenState.Error
            Assert.assertEquals(value.error, error.message)
        } finally {
            liveData.removeObserver(observer)
        }

    }

    private fun performSearchGitHub() = Mockito.`when`(repository.searchGithub(SEARCH_QUERY))
}