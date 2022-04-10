package com.geekbrains.tests

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.repository.FakeGitHubRepository
import com.geekbrains.tests.stubs.SchedulerProviderStub
import com.geekbrains.tests.viewmodel.ScreenState
import com.geekbrains.tests.viewmodel.SearchViewModel
import com.nhaarman.mockito_kotlin.verify
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

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()

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
        private const val ERROR_TEXT = "Search results or total count are null"
        private const val EXCEPTION_TEXT = "Response is null or unsuccessful"
    }


    private fun setObserverAndLiveData(): Pair<Observer<ScreenState>, LiveData<ScreenState>> {
        val observer = Observer<ScreenState> {}
        val liveData = searchViewModel.subscribeToLiveData()
        return Pair(observer, liveData)
    }

    private suspend fun performSearchGitHub() = Mockito.`when`(repository.searchGithubAsync(SEARCH_QUERY))

    @Test
    fun searchGitHub_Test(): Unit {
        testCoroutineRule.runBlockingTest {
            performSearchGitHub().thenReturn(
                SearchResponse(1, listOf()))
            searchViewModel.searchGitHub(SEARCH_QUERY)
            verify(repository).searchGithubAsync(SEARCH_QUERY)
        }

    }

    @Test
    fun liveData_TestReturnValueIsNull(): Unit {
        testCoroutineRule.runBlockingTest {
            val (observer, liveData) = setObserverAndLiveData()

            performSearchGitHub().thenReturn(
                SearchResponse(
                    1,
                    listOf()
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

    }

    @Test
    fun liveData_TestReturnValueIsError(): Unit {
        testCoroutineRule.runBlockingTest {
            val (observer, liveData) = setObserverAndLiveData()

            performSearchGitHub().thenReturn(SearchResponse(null, listOf()))

            try {
                liveData.observeForever(observer)
                searchViewModel.searchGitHub(SEARCH_QUERY)
                val value: ScreenState.Error = liveData.value as ScreenState.Error
                Assert.assertEquals(value.error.message, ERROR_TEXT)
            } finally {
                liveData.removeObserver(observer)
            }
        }
    }

    @Test
    fun coroutines_TestException(){
        testCoroutineRule.runBlockingTest {
            val (observer, liveData) = setObserverAndLiveData()

            try {
                liveData.observeForever(observer)
                searchViewModel.searchGitHub(SEARCH_QUERY)

                val value: ScreenState.Error = liveData.value as ScreenState.Error
                Assert.assertEquals(value.error.message, EXCEPTION_TEXT)
            } finally {
                liveData.removeObserver(observer)
            }
        }
    }
}