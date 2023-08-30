package likelion.project.ipet_customer.ui.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import likelion.project.ipet_customer.R
import likelion.project.ipet_customer.databinding.FragmentProductCategoryBinding
import likelion.project.ipet_customer.databinding.FragmentProductListBinding
import likelion.project.ipet_customer.ui.main.MainActivity

class ProductListFragment : Fragment() {

    lateinit var fragmentProductListBinding: FragmentProductListBinding
    lateinit var fragmentProductCategoryBinding: FragmentProductCategoryBinding
    lateinit var mainActivity: MainActivity
    val fragmentList = mutableListOf<Fragment>()
    var lCategoryState: String? = null
    var sCategoryState: Int = 0
    var sCategoryName: String? = null
    val productListName = arrayOf("사료", "간식", "장난감", "의류", "집")
    val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentProductListBinding = FragmentProductListBinding.inflate(inflater)
        fragmentProductCategoryBinding = FragmentProductCategoryBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        sCategoryState = arguments?.getInt("sCategoryState")!!
        lCategoryState = arguments?.getString("lCategoryState")!!

        when(lCategoryState){
            "사료" -> {defineSmallCategoryName("주니어", "어덜트", "시니어", "다이어트", "건식", "습식")}
            "간식" -> {defineSmallCategoryName("껌", "스낵", "육포", "캔", "비스켓", "기타")}
            "장난감" -> {defineSmallCategoryName("공", "인형", "큐브", "훈련용품", "스크래쳐", "기타")}
            "의류" -> {defineSmallCategoryName("레인코트", "신발", "외투", "원피스", "셔츠", "기타")}
            "집" -> {defineSmallCategoryName("계단", "매트", "울타리", "안전문", "하우스", "기타")}
        }


        fragmentProductListBinding.run {
            pagerProductList.setCurrentItem(3, false)
            toolbarProductList.run {
                title = "상품 목록"

                setNavigationIcon(R.drawable.ic_back_24dp)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.PRODUCT_LIST_FRAGMENT)
                }
            }

            fragmentList.run {
                add(ProductCategoryFragment.newInstance("사료", sCategoryName!!))
                add(ProductCategoryFragment.newInstance("간식", sCategoryName!!))
                add(ProductCategoryFragment.newInstance("장난감", sCategoryName!!))
                add(ProductCategoryFragment.newInstance("의류", sCategoryName!!))
                add(ProductCategoryFragment.newInstance("집", sCategoryName!!))
            }

            pagerProductList.adapter = TabAdapterClass(mainActivity)

            val tabLayoutMediator = TabLayoutMediator(
                tabsProductList, pagerProductList){ tab: TabLayout.Tab, i: Int ->
                tab.text = productListName[i]
            }

            tabsProductList.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val selectedTabPosition = tab?.position ?: 0
                    pagerProductList.setCurrentItem(selectedTabPosition, false)
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            tabLayoutMediator.attach()

            for(i in productListName.indices) {
                if(productListName[i] == lCategoryState) {
                    pagerProductList.setCurrentItem(i, false)
                    break
                }
            }

        }
        return fragmentProductListBinding.root
    }

    fun defineSmallCategoryName(cat1: String, cat2: String, cat3: String, cat4: String, cat5: String, cat6: String){
        when(sCategoryState){
            1 -> {sCategoryName = cat1}
            2 -> {sCategoryName = cat2}
            3 -> {sCategoryName = cat3}
            4 -> {sCategoryName = cat4}
            5 -> {sCategoryName = cat5}
            6 -> {sCategoryName = cat6}
        }
    }

    inner class TabAdapterClass(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return fragmentList.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }
    }
}