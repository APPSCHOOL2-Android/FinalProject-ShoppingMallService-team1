package likelion.project.ipet_customer.ui.payment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import likelion.project.ipet_customer.R
import likelion.project.ipet_customer.databinding.FragmentPaymentBinding
import likelion.project.ipet_customer.databinding.ItemShoppingBasketProductBinding
import likelion.project.ipet_customer.model.Joint
import likelion.project.ipet_customer.model.Product
import likelion.project.ipet_customer.ui.login.LoginViewModel
import likelion.project.ipet_customer.ui.main.MainActivity

class PaymentFragment : Fragment() {

    lateinit var fragmentPaymentBinding: FragmentPaymentBinding
    lateinit var mainActivity: MainActivity

    val couponDataList = arrayOf(
        "쿠폰을 선택해주세요","아이펫 신규 회원 가입 쿠폰", "전체 항목 10% 할인", "항목3", "항목4", "항목5"
    )

    lateinit var address : String

    val db = Firebase.firestore
    val customerId = LoginViewModel.customer.customerId
    private var selectedProductIdxList: List<String>? = null
    private var selectedJointIdxList: List<String>? = null
    val productDataList = mutableListOf<Product>()
    val jointDataList = mutableListOf<Joint>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentPaymentBinding = FragmentPaymentBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        selectedProductIdxList = arguments?.getStringArrayList("selectedProductIdxList")
        selectedJointIdxList = arguments?.getStringArrayList("selectedJointIdxList")

        if (selectedProductIdxList != null) {
            checkProductDB(selectedProductIdxList!!)
        }

        if (selectedJointIdxList != null) {
            checkJointDB(selectedJointIdxList!!)
        }

        fragmentPaymentBinding.run {

            address = arguments?.getString("address") ?: "대표 배송지를 입력해주세요"
            textViewPaymentAddressRoadNameNew.text = address

            if (address !== "대표 배송지를 입력해주세요") {
                imageViewPaymentAddAddress.visibility = View.GONE
                editTextPaymentAddressDetails.visibility = View.VISIBLE
            }

            toolbarPayment.run {
                title = "주문서 작성"
                setNavigationIcon(R.drawable.ic_back_24dp)
                setNavigationOnClickListener {
                    mainActivity.activityMainBinding.bottomNavigation.visibility = View.VISIBLE
                    mainActivity.removeFragment(MainActivity.PAYMENT_FRAGMENT)
                }
            }

            recyclerPaymentProduct.run {
                adapter = PaymentProductAdapter()
                layoutManager = LinearLayoutManager(context)
            }

            spinnerPaymentCoupon.run {

                // 어뎁터 설정
                val counponSpinner = ArrayAdapter<String>(
                    context,
                    // Spinner가 접혀있을 때의 모양
                    android.R.layout.simple_spinner_item,
                    couponDataList
                )
                // Spinner가 펼쳐져 있을 때의 항목 모양
                counponSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                adapter = counponSpinner

                // 항목을 선택하면 동작하는 리스너
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }

                }

            }

            // 대표 배송지 추가하기를 클릭할 때
            imageViewPaymentAddAddress.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.PAYMENT_ADDRESS_FRAGMENT, false, null)

            }

            // 상세 주소를 입력을 완료할 때
            editTextPaymentAddressDetails.setOnEditorActionListener { v, actionId, event ->

                textViewPaymentAddressRoadNameNew.append("\n${editTextPaymentAddressDetails.text}")
                editTextPaymentAddressDetails.visibility = View.GONE

                true
            }

            // 결제하기 버튼 클릭
            buttonPayment.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.PAYMENT_COMPLETE_FRAGMENT, false, null)
            }

        }

        return fragmentPaymentBinding.root
    }

    private fun checkProductDB(inputProductIdxList: List<String>) {

        if (inputProductIdxList.isNotEmpty()) {
            productDataList.clear()

            db.collection("Cart")
                .whereEqualTo("buyerId", customerId)
                .whereIn("productIdx", inputProductIdxList)
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
                        db.collection("Product")
                            .whereIn("productIdx", productIdxList)
                            .get()
                            .addOnSuccessListener { productResult ->
                                for (productDocument in productResult) {
                                    val product = productDocument.toObject(Product::class.java)
                                    productDataList.add(product)

                                }

                                fragmentPaymentBinding.recyclerPaymentProduct.adapter?.notifyDataSetChanged()
                                // 데이터가 비어있을 때 처리
                                if (productDataList.isEmpty()) {
                                    // 처리 방법 추가
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d("PaymentFragment", "Error getting product documents: ", exception)
                            }
                    } else {
                        // productIdxList가 비어있을 때 처리
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("PaymentFragment", "Error getting cart documents: ", exception)
                }
        }
    }

    private fun checkJointDB(inputJointIdxList: List<String>) {

        if (inputJointIdxList.isNotEmpty()) {
            jointDataList.clear()

            db.collection("Cart")
                .whereEqualTo("buyerId", customerId)
                .whereIn("productIdx", inputJointIdxList)
                .get()
                .addOnSuccessListener { cartResult ->
                    val jointIdxList = mutableListOf<String>()

                    for (cartDocument in cartResult) {
                        val jointIdx = cartDocument.getString("productIdx")
                        if (!jointIdx.isNullOrEmpty()) {
                            jointIdxList.add(jointIdx)
                        }
                    }

                    if (jointIdxList.isNotEmpty()) {
                        db.collection("Joint")
                            .whereIn("jointIdx", jointIdxList)
                            .get()
                            .addOnSuccessListener { jointResult ->
                                for (jointDocument in jointResult) {
                                    val joint = jointDocument.toObject(Joint::class.java)
                                    jointDataList.add(joint)
                                }
                                fragmentPaymentBinding.recyclerPaymentProduct.adapter?.notifyDataSetChanged()
                                // 데이터가 비어있을 때 처리
                                if (jointDataList.isEmpty()) {

                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d("PaymentFragment", "Error getting joint documents: ", exception)
                            }
                    } else {
                        // jointIdxList가 비어있을 때 처리
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("PaymentFragment", "Error getting cart documents: ", exception)
                }

        }
    }


    inner class PaymentProductAdapter : RecyclerView.Adapter<PaymentProductAdapter.PaymentProductViewHolder>() {

        inner class PaymentProductViewHolder(itemShoppingBasketProductBinding: ItemShoppingBasketProductBinding) : RecyclerView.ViewHolder(itemShoppingBasketProductBinding.root) {
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

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): PaymentProductViewHolder {
            val itemShoppingBasketProductBinding = ItemShoppingBasketProductBinding.inflate(layoutInflater)
            val paymentProductViewHolder = PaymentProductViewHolder(itemShoppingBasketProductBinding)

            itemShoppingBasketProductBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return paymentProductViewHolder
        }

        override fun getItemCount(): Int {
            return productDataList.size + jointDataList.size
        }

        override fun onBindViewHolder(holder: PaymentProductViewHolder, position: Int) {
            holder.checkBoxProdut.visibility = View.GONE

            if (position < productDataList.size) {
                bindProduct(holder, position)
            } else {
                bindJoint(holder, position - productDataList.size)
            }

        }

        private fun bindProduct(holder: PaymentProductViewHolder, position: Int) {
            val product = productDataList[position]
            holder.textViewProductName.text = product.productTitle
            holder.textViewProductPrice.text = "${mainActivity.formatNumberToCurrency(product.productPrice)}원"

            val imageUrl = product.productImg?.get(0)
            imageUrl?.let {
                Glide.with(holder.itemView.context)
                    .load(it)
                    .into(holder.imageViewProduct)
            }
        }

        private fun bindJoint(holder: PaymentProductViewHolder, position: Int) {
            val joint = jointDataList[position]
            holder.textViewProductName.text = joint.jointTitle
            holder.textViewProductPrice.text = "${mainActivity.formatNumberToCurrency(joint.jointPrice)}원"

            val imageUrl = joint.jointImg?.get(0)
            imageUrl?.let {
                Glide.with(holder.itemView.context)
                    .load(it)
                    .into(holder.imageViewProduct)
            }
        }


    }

}