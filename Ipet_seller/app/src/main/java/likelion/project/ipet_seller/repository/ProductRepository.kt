package likelion.project.ipet_seller.repository

import android.content.Context
import likelion.project.ipet_seller.db.local.DataStoreDataSource
import likelion.project.ipet_seller.db.remote.ProductDataSource
import likelion.project.ipet_seller.db.remote.SellerDataSource
import likelion.project.ipet_seller.model.Product

class ProductRepository() {
    private val productDataSource = ProductDataSource()

    suspend fun getProducts(productSeller: String) = productDataSource.getProducts(productSeller)
    suspend fun deleteProduct(product: Product) = productDataSource.deleteProducts(product)
    suspend fun registerProduct(product: Product) = productDataSource.registrerProduct(product)
    suspend fun updateProduct(product: Product) = productDataSource.updateProduct(product)
}