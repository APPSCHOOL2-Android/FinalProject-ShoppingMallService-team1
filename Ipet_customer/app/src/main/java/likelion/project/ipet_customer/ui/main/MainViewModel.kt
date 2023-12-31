package likelion.project.ipet_customer.ui.main

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import likelion.project.ipet_customer.repository.OnboardingRepository

class MainViewModel(context: Context): ViewModel() {
    private val onboardingRepository = OnboardingRepository(context)

    private val _isFirstVisitor = MutableStateFlow(false)
    val isFistVisitor = _isFirstVisitor.asStateFlow()

    init {
        checkFirstVisitor()
    }

    fun checkFirstVisitor() {
        viewModelScope.launch {
            val response = onboardingRepository.readOnboarding()
            _isFirstVisitor.update {
                response == null
            }
        }
    }
}