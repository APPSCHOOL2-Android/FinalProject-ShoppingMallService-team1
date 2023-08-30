package likelion.project.ipet_customer.ui.userinfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import likelion.project.ipet_customer.model.Customer
import likelion.project.ipet_customer.model.Order
import likelion.project.ipet_customer.repository.OrderRepository
import likelion.project.ipet_customer.repository.UserInfoRepository
import likelion.project.ipet_customer.ui.login.LoginViewModel
import likelion.project.ipet_customer.ui.main.MainActivity

class UserInfoViewModel : ViewModel() {
    private val userInfoRepository = UserInfoRepository()
    private val orderRepository = OrderRepository()

    fun saveAddress(customer: Customer){
        userInfoRepository.setAddress(customer)
    }

    fun logout(){
        LoginViewModel.customer = Customer()
    }

    fun signOut(customer: Customer, mainActivity: MainActivity){
        userInfoRepository.signOut(customer, mainActivity)
    }

    suspend fun getAllStatus(): ArrayList<Long>{
        // 주문 리스트
        val orderList =  orderRepository.getAllOrder()
        // 상품별 주문/배손 상태
        var status0 = 0L
        var status1 = 0L
        var status2 = 0L
        val statusList = ArrayList<Long>()

        for(order in orderList){
            when(order.orderState){
                0L -> {
                    status0++
                }
                1L -> {
                    status1++
                }
                2L -> {
                    status2++
                }
            }
        }
        statusList.add(status0)
        statusList.add(status1)
        statusList.add(status2)

        return statusList
    }
}