package likelion.project.ipet_seller.db.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import likelion.project.ipet_seller.constant.CollectionId.PRODUCT_COLLECTION
import likelion.project.ipet_seller.model.Product
import java.io.File

class ProductDataSource {
    private val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference
    suspend fun getProducts(productSeller: String): Flow<Result<List<Product>>> {
        return flow {
            runCatching {
                db.collection(PRODUCT_COLLECTION)
                    .whereEqualTo("productSeller", productSeller)
                    .get()
                    .await()
                    .toObjects(Product::class.java)
            }.onSuccess {
                emit(Result.success(it))
            }.onFailure {
                emit(Result.failure(it))
            }
        }
    }

    suspend fun deleteProducts(product: Product): Flow<Result<Boolean>> {
        return flow {
            runCatching {
                db.collection(PRODUCT_COLLECTION)
                    .document(product.productIdx)
                    .delete()
                    .await()
            }.onSuccess {
                emit(Result.success(true))
            }.onFailure {
                emit(Result.failure(it))
            }
        }
    }
    
    suspend fun registrerProduct(product: Product): Flow<Result<Boolean>> {
        return flow {
            kotlin.runCatching {
                checkProduct(product)
                val uploadedImages = mutableListOf<String>()
                val productIdx = db.collection(PRODUCT_COLLECTION).document().id
                product.productImg.forEach {
                    var file = Uri.parse(it)
                    val uploadTask = storageRef.child("productImages/${productIdx}/${file.lastPathSegment}.png")
                        .putFile(file)
                        .await()
                    val downloadUrl = uploadTask.storage.downloadUrl.await()
                    uploadedImages.add(downloadUrl.toString())
                }
                val updatedProduct = product.copy(productIdx = productIdx ,productImg = uploadedImages)
                db.collection(PRODUCT_COLLECTION)
                    .document(productIdx)
                    .set(updatedProduct)
                    .await()
            }.onSuccess {
                emit(Result.success(true))
            }.onFailure {
                emit(Result.failure(it))
            }
        }
    }

    suspend fun updateProduct(product: Product): Flow<Result<Boolean>> {
        return flow {
            runCatching {
                checkProduct(product)
                val uploadedImages = mutableListOf<String>()
                val filtedimages = product.productImg.filter { !it.startsWith("https://firebasestorage.googleapis.com/") }
                filtedimages.forEach {
                    var file = Uri.parse(it)
                    Log.d("업데이트", "${file}")
                    val uploadTask = storageRef.child("productImages/${product.productIdx}/${System.currentTimeMillis()}.png")
                        .putFile(file)
                        .await()
                    val downloadUrl = uploadTask.storage.downloadUrl.await()
                    uploadedImages.add(downloadUrl.toString())
                }
                val productImages = product.productImg.toSet().filter { !it.startsWith("content:") }.toMutableList() + uploadedImages

                db.collection(PRODUCT_COLLECTION)
                    .document(product.productIdx)
                    .set(product.copy(productImg = productImages))
                    .await()
            }.onSuccess {
                emit(Result.success(true))
            }.onFailure {
                emit(Result.failure(it))
            }
        }
    }

    private fun checkProduct(product: Product) {
        require(product.productImg.isNotEmpty()) { throw Exception("하나 이상의 이미지가 필요합니다") }
        require(product.productTitle.isNotEmpty()) { throw Exception("상품명을 작성해주세요") }
        require(product.productPrice != 0L) { throw Exception("가격을 입력해주세요") }
        require(product.productStock != 0L) { throw Exception("수량을 입력해주세요") }
        require(product.productAnimalType != "반려동물 선택") { throw Exception("동물 타입을 선택해주세요") }
        require(product.productLcategory!= "대분류 선택") { throw Exception("대분류 카테고리를 선택해주세요") }
        require(product.productScategory != "소분류 선택") { throw Exception("소분류를 선택해주세요") }
        require(product.productSeller.isNotEmpty()) { throw Exception("판매자 아이디가 없습니다") }
        require(product.productText.isNotEmpty()) { throw Exception("상품에 대한 내용을 작성해주세요") }
    }
}