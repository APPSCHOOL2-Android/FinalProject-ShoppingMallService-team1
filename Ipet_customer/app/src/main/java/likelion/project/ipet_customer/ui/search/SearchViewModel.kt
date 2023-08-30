package likelion.project.ipet_customer.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import likelion.project.ipet_customer.db.local.SearchDB
import likelion.project.ipet_customer.model.Heart
import likelion.project.ipet_customer.model.Search
import likelion.project.ipet_customer.repository.HeartRepository
import likelion.project.ipet_customer.repository.SearchRepository
import likelion.project.ipet_customer.ui.login.LoginViewModel

class SearchViewModel(application: Application): AndroidViewModel(application) {

    val getAllSearches: LiveData<List<Search>>
    private val searchRepository: SearchRepository
    private val heartRepository: HeartRepository

    init {
        val searchDAO = SearchDB.getDB(application)!!.searchDAO()
        searchRepository = SearchRepository(searchDAO)
        heartRepository = HeartRepository()
        getAllSearches = searchRepository.getAllSearches()
    }

    fun insertSearch(search: Search){
        viewModelScope.launch(Dispatchers.IO) {
            searchRepository.insert(search)
        }
    }

    fun addHeart(productIdx: String) {
        viewModelScope.launch {
            val heart = Heart(LoginViewModel.customer.customerId, productIdx)
            heartRepository.setAddHeart(heart)
        }
    }

    fun deleteHeart(productIdx: String) {
        viewModelScope.launch {
            val heart = Heart(LoginViewModel.customer.customerId, productIdx)
            heartRepository.setDeleteHeart(heart)
        }
    }

    class Factory(private val application: Application): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchViewModel(application) as T
        }
    }

}