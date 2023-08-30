package likelion.project.ipet_customer.ui.order

import androidx.lifecycle.ViewModel
import likelion.project.ipet_customer.model.Order
import likelion.project.ipet_customer.model.Product
import likelion.project.ipet_customer.repository.OrderRepository
import likelion.project.ipet_customer.repository.ProductRepository

class OrderStatusViewModel : ViewModel() {
    private val productRepository = ProductRepository()
    private val orderRepository = OrderRepository()

    suspend fun getAllProduct() : MutableList<Product> {
        return productRepository.getAllProduct()
    }

    suspend fun getAllOrder() : MutableList<Order> {
        return orderRepository.getAllOrder()
    }
}