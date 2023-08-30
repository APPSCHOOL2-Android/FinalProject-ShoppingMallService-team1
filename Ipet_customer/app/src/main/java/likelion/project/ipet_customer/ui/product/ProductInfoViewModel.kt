package likelion.project.ipet_customer.ui.product

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import likelion.project.ipet_customer.model.Cart
import likelion.project.ipet_customer.model.Heart
import likelion.project.ipet_customer.model.Joint
import likelion.project.ipet_customer.model.Product
import likelion.project.ipet_customer.model.Review
import likelion.project.ipet_customer.repository.CartRepository
import likelion.project.ipet_customer.repository.HeartRepository
import likelion.project.ipet_customer.repository.JointRepository
import likelion.project.ipet_customer.repository.ProductRepository
import likelion.project.ipet_customer.repository.ReviewRepository
import likelion.project.ipet_customer.ui.login.LoginViewModel

class ProductInfoViewModel : ViewModel() {
    private val productRepository = ProductRepository()
    private val jointRepository = JointRepository()
    private val reviewRepository = ReviewRepository()
    private val cartRepository = CartRepository()
    private val heartRepository = HeartRepository()

    val productLiveData = MutableLiveData<Product>()
    val jointLiveData = MutableLiveData<Joint>()
    val reviewLiveData = MutableLiveData<MutableList<Review>>()

    private var heartListenerRegistration: ListenerRegistration? = null

    fun loadOneProduct(productIdx : String){
        viewModelScope.launch{
            val product = productRepository.getOneProduct(productIdx)
            productLiveData.postValue(product)
        }
    }

    fun loadOneJoint(jointIdx : String){
        viewModelScope.launch{
            val joint = jointRepository.getOneJoint(jointIdx)
            jointLiveData.postValue(joint)
        }
    }

    fun loadSelectReview(productIdx : String){
        viewModelScope.launch{
            val reviews = reviewRepository.selectReview(productIdx)
            reviewLiveData.postValue(reviews)
        }
    }

    fun setAddCart(productIdx : String, productType: String){
        viewModelScope.launch(Dispatchers.IO) {
            val customerId = LoginViewModel.customer.customerId
            val cart = Cart(customerId, productIdx, productType)

            cartRepository.setAddCart(cart)
        }
    }

    fun setAddHeart(productIdx : String){
        viewModelScope.launch{
            val heart = Heart(LoginViewModel.customer.customerId, productIdx)

            heartRepository.setAddHeart(heart)
        }
    }

    fun setDeleteHeart(productIdx : String){
        viewModelScope.launch{
            val heart = Heart(LoginViewModel.customer.customerId, productIdx)

            heartRepository.setDeleteHeart(heart)
        }
    }

    fun registerHeartListener(productIdx: String, callback: (Boolean) -> Unit) {
        heartListenerRegistration?.remove() // 기존 리스너 해제
        val buyerId = LoginViewModel.customer.customerId

        heartListenerRegistration = heartRepository.registerHeartListener(buyerId, productIdx) { isHearted ->
            callback(isHearted)
        }
    }
}