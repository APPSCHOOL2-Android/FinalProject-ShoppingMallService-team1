package likelion.project.ipet_seller.ui.registration

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import likelion.project.ipet_seller.model.Product
import likelion.project.ipet_seller.repository.ProductRepository
import likelion.project.ipet_seller.repository.SellerRepository

class RegistrationViewModel(context: Context) : ViewModel() {
    private val productRepository = ProductRepository()
    private val sellerRepository = SellerRepository(context)

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<Result<String>>(extraBufferCapacity = 1)
    val event = _event.asSharedFlow()

    private val _spinnerEvent = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val spinnerEvent = _spinnerEvent.asSharedFlow()

    fun registerProduct(product: Product) {
        viewModelScope.launch {
            productRepository.registerProduct(product).collect {
                it.onSuccess {
                    _uiState.update {
                        it.copy(
                            isSave = true,
                            throwable = null
                        )
                    }
                }.onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isSave = false,
                            throwable = throwable
                        )
                    }
                    delay(50)
                    _uiState.update {
                        it.copy(
                            throwable = null
                        )
                    }
                }
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            productRepository.updateProduct(product).collect {
                it.onSuccess {
                    _uiState.update {
                        it.copy(
                            isUpdated = true,
                            throwable = null
                        )
                    }
                }.onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isUpdated = false,
                            throwable = throwable
                        )
                    }
                    delay(50)
                    _uiState.update {
                        it.copy(
                            throwable = null
                        )
                    }
                }
            }
        }
    }


    fun onUploadClickEvent(product: Product, status: Int) {
        viewModelScope.launch {
            if (status == 0) {
                registerProduct(product.copy(productSeller = sellerRepository.readSellerIdToLocal().first))
            } else {
                updateProduct(product.copy(productSeller = sellerRepository.readSellerIdToLocal().first))
            }
            uiState.collect {
                when {
                    it.isSave ->  _event.tryEmit(Result.Success("상품 업로드 되었습니다"))
                    it.isUpdated ->  _event.tryEmit(Result.Success("상품 수정 되었습니다"))
                    it.throwable != null -> _event.tryEmit(Result.Error(it.throwable))
                }
            }
        }
    }

    fun setProduct(product: Product) {
        _uiState.update {
            it.copy(product = product)
        }
    }
    fun onSpinnerItemClick(position: Int = 0) {
        _spinnerEvent.tryEmit(position)
    }
}

sealed class Result<out T> {
    data class Success<T>(val message: T) : Result<T>()
    data class Failure<T>(val message: T) : Result<T>()
    data class Error(val error: Throwable) : Result<Nothing>()
}

data class UiState(
    val product: Product = Product(),
    val isUpdated: Boolean = false,
    val isSave: Boolean = false,
    val throwable: Throwable? = null
)