package likelion.project.ipet_customer.ui.shoppingbasket

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import likelion.project.ipet_customer.R
import likelion.project.ipet_customer.databinding.FragmentShoppingBasketBinding
import likelion.project.ipet_customer.databinding.ItemShoppingBasketGroupBuyingBinding
import likelion.project.ipet_customer.databinding.ItemShoppingBasketProductBinding
import likelion.project.ipet_customer.model.Joint
import likelion.project.ipet_customer.model.Product
import likelion.project.ipet_customer.ui.login.LoginViewModel
import likelion.project.ipet_customer.ui.main.MainActivity

class ShoppingBasketFragment : Fragment() {

    lateinit var fragmentShoppingBasketBinding: FragmentShoppingBasketBinding
    lateinit var mainActivity: MainActivity

    val productDataList = mutableListOf<Product>()
    val jointDataList = mutableListOf<Joint>()
    val db = Firebase.firestore
    val customerId = LoginViewModel.customer.customerId

    // 선택된 상품 인덱스를 추적하는 집합
    private val selectedProducts = mutableSetOf<Int>()
    private val selectedGroupProducts = mutableSetOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentShoppingBasketBinding = FragmentShoppingBasketBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentShoppingBasketBinding.run {
            materialToolbarShoppingBasket.run {
                title = "장바구니"

                var fragmentState = arguments?.getString("fragmentState")
                // 내정보에서 장바구니를 클릭했을 때에만, 장바구니 화면에서 뒤로가기 버튼이 보이게함
                if (fragmentState == "Shopping") {
                    setNavigationIcon(R.drawable.ic_back_24dp)
                    setNavigationOnClickListener {
                        mainActivity.removeFragment(MainActivity.SHOPPING_BASKET_FRAGMENT)
                    }
                } else {
                    navigationIcon = null
                }
            }

            Log.d("ShoppingBasketFragment", "customerId : ${customerId}")
            checkProductDB()
            checkJoinDB()

            recyclerGoodsList.run {
                adapter = ShoppingBasketAdapter()
                layoutManager = LinearLayoutManager(context)

                // recyclerGoodsList 가 비어있을 때, 아래의 화면이 나타나개함
                if(adapter?.itemCount == 0) {
                    fragmentShoppingBasketBinding.linearLayoutEmpty.visibility = View.VISIBLE
                }
            }

            recyclerGroupGoodsList.run {
                adapter = ShoppingBasketGroupBuyingAdapter()
                layoutManager = LinearLayoutManager(context)

                // recyclerGroupGoodsList 가 비어있을 때, 아래의 화면이 나타나개함
                if(adapter?.itemCount == 0) {
                    fragmentShoppingBasketBinding.linearLayoutEmptyGroup.visibility = View.VISIBLE
                }
            }

            // 담아둔 상품이 없을 때, 쇼핑하러 가기 버튼
            buttonShoppingBasketGo.run {
                setOnClickListener {
                    mainActivity.selectBottomNavigationItem(R.id.item_bottom_home)
                    mainActivity.replaceFragment(MainActivity.HOME_FRAGMENT, false, null)
                }
            }

            // 공동 구매 상품을 안담았을 때, 공동구매 하러가기 버튼
            buttonShoppingBasketGroupJoin.run {
                setOnClickListener {
                    mainActivity.selectBottomNavigationItem(R.id.item_bottom_joinT)
                    mainActivity.replaceFragment(MainActivity.PRODUCT_JOINT_LIST_FRAGMENT, false, null)
                }
            }

            // 결제하기 버튼
            buttonShoppingBasketPayment.run {
                setOnClickListener {
                    val selectedProductIdxList = selectedProducts.map { position ->
                        productDataList[position].productIdx
                    }

                    val selectedJointIdxList = selectedGroupProducts.map { position ->
                        jointDataList[position].jointIdx
                    }

                    val bundle = Bundle().apply {
                        putStringArrayList("selectedProductIdxList", ArrayList(selectedProductIdxList))
                        putStringArrayList("selectedJointIdxList", ArrayList(selectedJointIdxList))
                    }

                    mainActivity.activityMainBinding.bottomNavigation.visibility = View.GONE
                    mainActivity.replaceFragment(MainActivity.PAYMENT_FRAGMENT, true, bundle)
                }
            }

            // 담아둔 상품의 "선택삭제" 텍스트 클릭 시
            textViewShoppingBasketSelectDelete.setOnClickListener {
                val iterator = selectedProducts.iterator()
                while (iterator.hasNext()) {
                    val selectedIndex = iterator.next()
                    val selectedProduct = productDataList[selectedIndex]

                    // 선택된 제품의 ID를 사용하여 파이어스토어에서 해당 데이터를 삭제한다
                    db.collection("Cart")
                        .whereEqualTo("buyerId", customerId)
                        .whereEqualTo("productIdx", selectedProduct.productIdx) // 선택된 제품의 ID를 조건으로 설정
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                // 파이어스토어에서 해당 문서를 삭제한다
                                db.collection("Cart").document(document.id).delete()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("ShoppingBasketFragment", "Error deleting document: ", exception)
                        }

                    iterator.remove()
                    productDataList.removeAt(selectedIndex)
                }

                fragmentShoppingBasketBinding.recyclerGoodsList.adapter?.notifyDataSetChanged()
                if(productDataList.isEmpty()) {
                    fragmentShoppingBasketBinding.linearLayoutEmpty.visibility = View.VISIBLE
                } else {
                    fragmentShoppingBasketBinding.linearLayoutEmpty.visibility = View.GONE
                }
            }

            // 담아둔 상품의 "전체삭제" 텍스트 클릭 시
            textViewShoppingBasketSelectDeleteAll.setOnClickListener {
                for (product in productDataList) {
                    val productIdx = product.productIdx // 제품 데이터에서 고유 식별자 가져옴

                    db.collection("Cart")
                        .whereEqualTo("buyerId", customerId)
                        .whereEqualTo("productIdx", productIdx)
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                db.collection("Cart").document(document.id).delete()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("ShoppingBasketFragment", "Error deleting document: ", exception)
                        }
                }

                productDataList.clear()
                fragmentShoppingBasketBinding.recyclerGoodsList.adapter?.notifyDataSetChanged()

                fragmentShoppingBasketBinding.linearLayoutEmpty.visibility = View.VISIBLE
            }

            // 담아둔 공동 구매 상품의 "선택삭제" 텍스트 클릭 시
            textViewShoppingBasketGroupSelectDelete.setOnClickListener {
                val iterator = selectedGroupProducts.iterator()
                while (iterator.hasNext()) {
                    val selectedIndex = iterator.next()
                    val selectedJoint = jointDataList[selectedIndex]

                    db.collection("Cart")
                        .whereEqualTo("buyerId", customerId)
                        .whereEqualTo("productIdx", selectedJoint.jointIdx) // 선택된 공동 구매 품목의 ID를 조건으로 설정
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                db.collection("Cart").document(document.id).delete()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("ShoppingBasketFragment", "Error deleting document: ", exception)
                        }

                    iterator.remove()
                    jointDataList.removeAt(selectedIndex)
                }

                fragmentShoppingBasketBinding.recyclerGroupGoodsList.adapter?.notifyDataSetChanged()
                if (jointDataList.isEmpty()) {
                    fragmentShoppingBasketBinding.linearLayoutEmptyGroup.visibility = View.VISIBLE
                } else {
                    fragmentShoppingBasketBinding.linearLayoutEmptyGroup.visibility = View.GONE
                }
            }

            // 담아둔 공동 구매 상품의 "전체삭제" 텍스트 클릭 시
            textViewShoppingBasketGroupSelectDeleteAll.setOnClickListener {
                for (joint in jointDataList) {
                    val jointIdx = joint.jointIdx

                    db.collection("Cart")
                        .whereEqualTo("buyerId", customerId)
                        .whereEqualTo("productIdx", jointIdx)
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                db.collection("Cart").document(document.id).delete()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("ShoppingBasketFragment", "Error deleting document: ", exception)
                        }
                }

                jointDataList.clear()
                fragmentShoppingBasketBinding.recyclerGroupGoodsList.adapter?.notifyDataSetChanged()

                fragmentShoppingBasketBinding.linearLayoutEmptyGroup.visibility = View.VISIBLE
            }

        }

        return fragmentShoppingBasketBinding.root
    }

    private fun checkProductDB() {
        productDataList.clear()

        db.collection("Cart")
            .whereEqualTo("buyerId", customerId)
            .get()
            .addOnSuccessListener { cartResult ->
                val productIdxList = mutableListOf<String>()

                for (cartDocument in cartResult) {
                    val productIdx = cartDocument.getString("productIdx")
                    if (!productIdx.isNullOrEmpty()) {
                        productIdxList.add(productIdx)
                    }
                }

                if (productIdxList.isNotEmpty()) {
                    // 위에서 가져온 productIdxList를 이용하여 Product 컬렉션에서 데이터 조회
                    val productQuery = db.collection("Product").whereIn("productIdx", productIdxList)
                    productQuery.get(com.google.firebase.firestore.Source.CACHE)
                        .addOnSuccessListener { productResult ->
                            for (productDocument in productResult) {
                                val product = productDocument.toObject(Product::class.java)
                                productDataList.add(product)
                            }

                            fragmentShoppingBasketBinding.recyclerGoodsList.adapter?.notifyDataSetChanged()
                            if (productDataList.isEmpty()) {
                                fragmentShoppingBasketBinding.linearLayoutEmpty.visibility = View.VISIBLE
                            } else {
                                fragmentShoppingBasketBinding.linearLayoutEmpty.visibility = View.GONE
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("ShoppingBasketFragment", "Error getting documents: ", exception)
                        }
                } else {
                    // productIdxList가 비어있을 때 처리
                }
            }
            .addOnFailureListener { exception ->
                Log.d("ShoppingBasketFragment", "Error getting documents: ", exception)
            }
    }

    private fun checkJoinDB() {
        jointDataList.clear()

        db.collection("Cart")
            .whereEqualTo("buyerId", customerId)
            .get()
            .addOnSuccessListener { cartResult ->
                val productIdxList = mutableListOf<String>()

                for (cartDocument in cartResult) {
                    val productIdx = cartDocument.getString("productIdx")
                    if (!productIdx.isNullOrEmpty()) {
                        productIdxList.add(productIdx)
                    }
                }

                if (productIdxList.isNotEmpty()) {
                    val jointQuery = db.collection("Joint").whereIn("jointIdx", productIdxList)
                    jointQuery.get(com.google.firebase.firestore.Source.CACHE)
                        .addOnSuccessListener { jointResult ->
                            for (jointDocument in jointResult) {
                                val joint = jointDocument.toObject(Joint::class.java)
                                jointDataList.add(joint)
                            }

                            fragmentShoppingBasketBinding.recyclerGroupGoodsList.adapter?.notifyDataSetChanged()
                            if (jointDataList.isEmpty()) {
                                fragmentShoppingBasketBinding.linearLayoutEmptyGroup.visibility = View.VISIBLE
                            } else {
                                fragmentShoppingBasketBinding.linearLayoutEmptyGroup.visibility = View.GONE
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("ShoppingBasketFragment", "Error getting documents: ", exception)
                        }
                } else {
                    // productIdxList가 비어있을 때 처리
                }
            }
            .addOnFailureListener { exception ->
                Log.d("ShoppingBasketFragment", "Error getting documents: ", exception)
            }
    }

    // 선택한 상품/공동구매 항목에 따라 텍스트 업데이트
    private fun updateTextWithSelectedItems() {
        val totalSelectedPrice = selectedProducts.sumBy { productDataList[it].productPrice.toInt() }
        val totalSelectedGroupPrice = selectedGroupProducts.sumBy { jointDataList[it].jointPrice.toInt() }

        val totalPriceText = if (totalSelectedPrice > 0 || totalSelectedGroupPrice > 0) {
            "${mainActivity.formatNumberToCurrency(totalSelectedPrice.toLong() + totalSelectedGroupPrice.toLong())}원"
        } else {
            "0원"
        }
        fragmentShoppingBasketBinding.textViewShoppingBasketTotalPrice.text = totalPriceText
    }

    // 선택한 상품/공동구매 항목에 따라 버튼 텍스트 업데이트
    private fun updateButtonWithSelectedItems() {
        val totalSelectedPrice = selectedProducts.sumBy { productDataList[it].productPrice.toInt() }
        val totalSelectedGroupPrice = selectedGroupProducts.sumBy { jointDataList[it].jointPrice.toInt() }

        val totalPriceText = if (totalSelectedPrice > 0 || totalSelectedGroupPrice > 0) {
            "합계 ${mainActivity.formatNumberToCurrency(totalSelectedPrice.toLong() + totalSelectedGroupPrice.toLong() + 3000)}원  |  결제하기"
        } else {
            "결제하기"
        }

        fragmentShoppingBasketBinding.buttonShoppingBasketPayment.text = totalPriceText
    }

    inner class ShoppingBasketAdapter : RecyclerView.Adapter<ShoppingBasketAdapter.ShoppingBasketHolder>() {

        inner class ShoppingBasketHolder(itemShoppingBasketProductBinding: ItemShoppingBasketProductBinding) : RecyclerView.ViewHolder(itemShoppingBasketProductBinding.root) {

            val checkBoxProdut : CheckBox
            val imageViewProduct : ImageView
            val textViewProductName : TextView
            val textViewProductCount : TextView
            val textViewProductPrice : TextView

            init {
                checkBoxProdut = itemShoppingBasketProductBinding.checkBoxProdut
                imageViewProduct = itemShoppingBasketProductBinding.imageViewProduct
                textViewProductName = itemShoppingBasketProductBinding.textViewProductName
                textViewProductCount = itemShoppingBasketProductBinding.textViewProductCount
                textViewProductPrice = itemShoppingBasketProductBinding.textViewProductPrice
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingBasketHolder {
            val itemShoppingBasketProductBinding = ItemShoppingBasketProductBinding.inflate(layoutInflater)
            val shoppingBasketHolder = ShoppingBasketHolder(itemShoppingBasketProductBinding)

            itemShoppingBasketProductBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return shoppingBasketHolder
        }

        override fun getItemCount(): Int {
            return productDataList.size
        }

        override fun onBindViewHolder(holder: ShoppingBasketHolder, position: Int) {
            holder.textViewProductName.text = productDataList[position].productTitle
            holder.textViewProductCount.text = "수량 ${productDataList[position].productStock}개"
            holder.textViewProductPrice.text = "${mainActivity.formatNumberToCurrency(productDataList[position].productPrice)}원"

            // productImg의 첫 번째 항목을 가져옴
            val imageUrl = productDataList[position].productImg?.get(0)

            // imageUrl이 null이 아닐 때만 Glide로 로드
            imageUrl?.let {
                Glide.with(holder.itemView.context)
                    .load(it)
                    .into(holder.imageViewProduct)
            }

            holder.checkBoxProdut.isChecked = selectedProducts.contains(position)
            holder.checkBoxProdut.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedProducts.add(position)
                } else {
                    selectedProducts.remove(position)
                }
                updateTextWithSelectedItems()
                updateButtonWithSelectedItems()
            }

        }

    }

    inner class ShoppingBasketGroupBuyingAdapter : RecyclerView.Adapter<ShoppingBasketGroupBuyingAdapter.ShoppingBasketGroupBuyingHolder>() {

        inner class ShoppingBasketGroupBuyingHolder(itemShoppingBasketGroupBuyingBinding: ItemShoppingBasketGroupBuyingBinding) : RecyclerView.ViewHolder(itemShoppingBasketGroupBuyingBinding.root) {

            val checkBoxGroupBuying : CheckBox
            val imageViewGroupBuying : ImageView
            val textViewGroupBuyingName : TextView
            val textViewGroupBuyingCount : TextView
            val textViewGroupBuyingPeriod : TextView
            val textViewGroupBuyingPrice : TextView

            init {
                checkBoxGroupBuying = itemShoppingBasketGroupBuyingBinding.checkBoxGroupBuying
                imageViewGroupBuying = itemShoppingBasketGroupBuyingBinding.imageViewGroupBuying
                textViewGroupBuyingName = itemShoppingBasketGroupBuyingBinding.textViewGroupBuyingName
                textViewGroupBuyingCount = itemShoppingBasketGroupBuyingBinding.textViewGroupBuyingCount
                textViewGroupBuyingPeriod = itemShoppingBasketGroupBuyingBinding.textViewGroupBuyingPeriod
                textViewGroupBuyingPrice = itemShoppingBasketGroupBuyingBinding.textViewGroupBuyingPrice

            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingBasketGroupBuyingHolder {
            val itemShoppingBasketGroupBuyingBinding = ItemShoppingBasketGroupBuyingBinding.inflate(layoutInflater)
            val shoppingBasketGroupBuyingHolder = ShoppingBasketGroupBuyingHolder(itemShoppingBasketGroupBuyingBinding)

            itemShoppingBasketGroupBuyingBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return shoppingBasketGroupBuyingHolder
        }

        override fun getItemCount(): Int {
            return jointDataList.size
        }

        override fun onBindViewHolder(holder: ShoppingBasketGroupBuyingHolder, position: Int) {
            holder.textViewGroupBuyingName.text = jointDataList[position].jointTitle
            holder.textViewGroupBuyingCount.text = "${jointDataList[position].jointMember}명 / ${jointDataList[position].jointTotalMember}명"
            holder.textViewGroupBuyingPeriod.text = jointDataList[position].jointTerm
            holder.textViewGroupBuyingPrice.text = "${mainActivity.formatNumberToCurrency(jointDataList[position].jointPrice)}원"

            val imageUrl = jointDataList[position].jointImg?.get(0)

            imageUrl?.let {
                Glide.with(holder.itemView.context)
                    .load(it)
                    .into(holder.imageViewGroupBuying)
            }

            holder.checkBoxGroupBuying.isChecked = selectedGroupProducts.contains(position)
            holder.checkBoxGroupBuying.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedGroupProducts.add(position)
                } else {
                    selectedGroupProducts.remove(position)
                }
                updateTextWithSelectedItems()
                updateButtonWithSelectedItems()
            }

        }

    }

}