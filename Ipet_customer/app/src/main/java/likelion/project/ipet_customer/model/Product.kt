package likelion.project.ipet_customer.model

data class Product(
    val productAnimalType: String = "",
    val productIdx: String = "",
    val productImg: ArrayList<*> = arrayListOf<Any>(),
    val productLcategory: String = "",
    val productPrice: Long = 0,
    val productScategory: String = "",
    val productSeller: String = "",
    val productStock: Long = 0,
    val productText: String = "",
    val productTitle: String = ""
)
