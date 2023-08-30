package likelion.project.ipet_customer.ui.product

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import likelion.project.ipet_customer.R
import likelion.project.ipet_customer.databinding.FragmentProductCategoryBinding
import likelion.project.ipet_customer.databinding.ItemProductCardBinding
import likelion.project.ipet_customer.model.Joint
import likelion.project.ipet_customer.model.Product
import likelion.project.ipet_customer.ui.main.MainActivity

class ProductCategoryFragment : Fragment() {

    lateinit var fragmentProductCategoryBinding: FragmentProductCategoryBinding
    lateinit var mainActivity: MainActivity
    var lCategoryState: String? = null
    var sCategoryName: String? = null
    val productList = mutableListOf<Product>()
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentProductCategoryBinding = FragmentProductCategoryBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        sCategoryName = arguments?.getString("sCategoryName")!!
        lCategoryState = arguments?.getString("lCategoryState")!!
        Log.d("sCs", "$lCategoryState , $sCategoryName")

        // 데이터를 이미 가져왔다면 다시 가져오지 않도록 체크
        if (productList.isEmpty()) {
            fragmentProductCategoryBinding.run {
                recyclerProductList.run {
                    adapter = ProductListAdapter(productList, mainActivity)
                    layoutManager = GridLayoutManager(context, 2)
                }

                when(lCategoryState){
                    "사료" -> {setSmallCategory("주니어", "어덜트", "시니어", "다이어트", "건식", "습식")}
                    "간식" -> {setSmallCategory("껌", "스낵", "육포", "캔", "비스켓", "기타")}
                    "장난감" -> {setSmallCategory("공", "인형", "큐브", "훈련용품", "스크래쳐", "기타")}
                    "의류" -> {setSmallCategory("레인코트", "신발", "외투", "원피스", "셔츠", "기타")}
                    "집" -> {setSmallCategory("계단", "매트", "울타리", "안전문", "하우스", "기타")}
                }

                db.collection("Product")
                    .whereEqualTo("productLcategory", lCategoryState)
                    .whereEqualTo("productScategory", sCategoryName)
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val idx = document["productIdx"] as String
                            val animalType = document["productAnimalType"] as String
                            val img = document["productImg"] as ArrayList<*>
                            val price = document["productPrice"] as Long
                            val seller = document["productSeller"] as String
                            val text = document["productText"] as String
                            val title = document["productTitle"] as String
                            val stock = document["productStock"] as Long
                            val lCategory = document["productLcategory"] as String
                            val sCategory = document["productScategory"] as String

                            val item = Product(animalType, idx, img, lCategory, price, sCategory, seller, stock, text, title)
                            productList.add(item)
                            fragmentProductCategoryBinding.recyclerProductList.adapter?.notifyDataSetChanged()
                            val buttons = listOf(buttonCategory1, buttonCategory2, buttonCategory3, buttonCategory4, buttonCategory5, buttonCategory6)

                            for (button in buttons) {
                                if (button.text == sCategoryName) {
                                    button.setBackgroundColor(resources.getColor(R.color.rose_100))
                                    button.setTextColor(Color.WHITE)
                                }
                            }
                        }
                    }

                buttonCategory1.setOnClickListener { setSCategoryAdapter(buttonCategory1) }
                buttonCategory2.setOnClickListener { setSCategoryAdapter(buttonCategory2) }
                buttonCategory3.setOnClickListener { setSCategoryAdapter(buttonCategory3) }
                buttonCategory4.setOnClickListener { setSCategoryAdapter(buttonCategory4) }
                buttonCategory5.setOnClickListener { setSCategoryAdapter(buttonCategory5) }
                buttonCategory6.setOnClickListener { setSCategoryAdapter(buttonCategory6) }
            }
        }

        return fragmentProductCategoryBinding.root
    }

    private fun setSCategoryAdapter(button: Button) {
        fragmentProductCategoryBinding.run {
            setButtonDefault(buttonCategory1)
            setButtonDefault(buttonCategory2)
            setButtonDefault(buttonCategory3)
            setButtonDefault(buttonCategory4)
            setButtonDefault(buttonCategory5)
            setButtonDefault(buttonCategory6)
        }
        button.setBackgroundColor(resources.getColor(R.color.rose_100))
        button.setTextColor(Color.WHITE)

        val newProductList = mutableListOf<Product>() // 새로운 리스트 생성

        db.collection("Product")
            .whereEqualTo("productLcategory", lCategoryState)
            .whereEqualTo("productScategory", button.text.toString())
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val idx = document["productIdx"] as String
                    val animalType = document["productAnimalType"] as String
                    val img = document["productImg"] as ArrayList<*>
                    val price = document["productPrice"] as Long
                    val seller = document["productSeller"] as String
                    val text = document["productText"] as String
                    val title = document["productTitle"] as String
                    val stock = document["productStock"] as Long
                    val lCategory = document["productLcategory"] as String
                    val sCategory = document["productScategory"] as String

                    val item = Product(animalType, idx, img, lCategory, price, sCategory, seller, stock, text, title)
                    newProductList.add(item)
                }

                // 새로운 리스트로 갱신
                productList.clear()
                productList.addAll(newProductList)
                fragmentProductCategoryBinding.recyclerProductList.adapter?.notifyDataSetChanged()
            }
    }

    fun setSmallCategory(cat1: String, cat2: String, cat3: String, cat4: String, cat5: String, cat6: String) {
        fragmentProductCategoryBinding.run {
            buttonCategory1.text = cat1
            buttonCategory2.text = cat2
            buttonCategory3.text = cat3
            buttonCategory4.text = cat4
            buttonCategory5.text = cat5
            buttonCategory6.text = cat6
        }
    }

    companion object {
        fun newInstance(lCategoryState: String, sCategoryName: String): ProductCategoryFragment {
            val fragment = ProductCategoryFragment()
            val args = Bundle()
            args.putString("sCategoryName", sCategoryName)
            args.putString("lCategoryState", lCategoryState)
            fragment.arguments = args
            return fragment
        }
        fun setButtonDefault(button: Button){
            button.setBackgroundColor(Color.WHITE)
            button.setTextColor(Color.rgb(241, 187, 186))
        }

    }
}