package likelion.project.ipet_customer.ui.search

import SearchAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import likelion.project.ipet_customer.databinding.FragmentSearchMainBinding
import likelion.project.ipet_customer.model.Product
import likelion.project.ipet_customer.model.Search
import likelion.project.ipet_customer.ui.main.MainActivity
import kotlin.concurrent.thread

class SearchMainFragment : Fragment() {

    lateinit var fragmentSearchMainBinding: FragmentSearchMainBinding
    lateinit var mainActivity: MainActivity
    lateinit var searchviewModel: SearchViewModel
    val productList = mutableListOf<Product>()
    val bestIdxList = mutableListOf<String>()
    val bestSellerList = mutableListOf<Any>()
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentSearchMainBinding = FragmentSearchMainBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        searchviewModel = ViewModelProvider(this, SearchViewModel.Factory(mainActivity.application)).get(SearchViewModel::class.java)

        fragmentSearchMainBinding.run {
            searchviewModel.getAllSearches.observe(viewLifecycleOwner, Observer { search ->
                val searchList = search.map { it.searchData }
                val reverseList = searchList.reversed()
                chip1.text = reverseList.getOrNull(0) ?: "검색어 1"
                chip2.text = reverseList.getOrNull(1) ?: "검색어 2"
                chip3.text = reverseList.getOrNull(2) ?: "검색어 3"
                chip4.text = reverseList.getOrNull(3) ?: "검색어 4"

                val chipTexts = mutableListOf<String>()

                for (i in 0 until 4) {
                    val currentChip = when (i) {
                        0 -> chip1
                        1 -> chip2
                        2 -> chip3
                        3 -> chip4
                        else -> null
                    }

                    if (currentChip != null) {
                        val searchText = reverseList.getOrNull(i)
                        if (searchText != null && !chipTexts.contains(searchText)) {
                            chipTexts.add(searchText)
                            currentChip.text = searchText
                            currentChip.visibility = View.VISIBLE
                        } else {
                            currentChip.visibility = View.GONE
                        }
                    }
                }
            })

            chip1.setOnClickListener { chiptoSearch(chip1) }
            chip2.setOnClickListener { chiptoSearch(chip2) }
            chip3.setOnClickListener { chiptoSearch(chip3) }
            chip4.setOnClickListener { chiptoSearch(chip4) }

            searchViewSearch.run {
                queryHint = "검색어를 입력하세요."
                isSubmitButtonEnabled = true
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (!query.isNullOrBlank()) {
                            performSearch(query.toString())
                            return true
                        }
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText.isNullOrBlank()) {
                            // 검색어가 지워질 때, 기존 레이아웃 표시
                            layoutSearchResult.visibility = View.INVISIBLE
                            layoutSearchNoResult.visibility = View.GONE
                            layoutSearchRecent.visibility = View.VISIBLE
                            layoutSearchBest.visibility = View.VISIBLE

                            thread {
                                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(view?.windowToken, 0)
                            }
                        }
                        return false
                    }
                })
            }

            recyclerViewSearchResult.run {
                adapter = SearchAdapter(productList, mainActivity, searchviewModel)
                layoutManager = GridLayoutManager(context, 2)
            }
        }

        findTop5BestSellersCoroutine()

        return fragmentSearchMainBinding.root
    }

    private fun findTop5BestSellersCoroutine() {
        lifecycleScope.launch {
            findTop5BestSellers()
            fragmentSearchMainBinding.run {
                searchToInfo(buttonSearchMain1, 0)
                searchToInfo(buttonSearchMain2, 1)
                searchToInfo(buttonSearchMain3, 2)
                searchToInfo(buttonSearchMain4, 3)
                searchToInfo(buttonSearchMain5, 4)
            }
        }
    }

    private fun searchToInfo(button: Button, idx: Int) {
        if (idx < bestIdxList.size) {
            button.run {
                text = bestSellerList[idx].toString()
                setOnClickListener {
                    val bundle = Bundle()
                    val readProductIdx = bestIdxList[idx]
                    bundle.putString("readProductIdx", readProductIdx)
                    bundle.putString("readToggle", "product")
                    mainActivity.replaceFragment(MainActivity.PRODUCT_INFO_FRAGMENT, true, bundle)
                }
            }
        } else {
            button.text = "상품 없음"
            button.setOnClickListener(null)
        }
    }

    private fun performSearch(query: String) {
        if (!query.isNullOrBlank()) {
            fragmentSearchMainBinding.layoutSearchRecent.visibility = View.GONE
            fragmentSearchMainBinding.layoutSearchBest.visibility = View.GONE
            val search = Search(0, query)
            searchviewModel.insertSearch(search)
            productList.clear()
            db.collection("Product")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val title = document["productTitle"] as String
                        if (title.contains(query)) {
                            // 검색 결과를 productList에 추가
                            val idx = document["productIdx"] as String
                            val animalType = document["productAnimalType"] as String
                            val img = document["productImg"] as ArrayList<*>
                            val price = document["productPrice"] as Long
                            val seller = document["productSeller"] as String
                            val text = document["productText"] as String
                            val stock = document["productStock"] as Long
                            val lCategory = document["productLcategory"] as String
                            val sCategory = document["productScategory"] as String

                            val item = Product(
                                animalType, idx, img, lCategory, price, sCategory, seller, stock, text, title
                            )
                            productList.add(item)
                        }
                    }
                    // 검색 결과에 따라 레이아웃을 변경
                    if (productList.isEmpty()) {
                        // 검색 결과가 없는 경우
                        fragmentSearchMainBinding.layoutSearchResult.visibility = View.GONE
                        fragmentSearchMainBinding.layoutSearchNoResult.visibility = View.VISIBLE
                    } else {
                        // 검색 결과가 있는 경우
                        fragmentSearchMainBinding.layoutSearchResult.visibility = View.VISIBLE
                        fragmentSearchMainBinding.layoutSearchNoResult.visibility = View.GONE
                    }

                    // 검색 결과를 업데이트
                    fragmentSearchMainBinding.recyclerViewSearchResult.adapter?.notifyDataSetChanged()
                }
        }
    }
    fun chiptoSearch(chip: Chip){
        val searchText = chip.text.toString()
        performSearch(searchText)
    }

    private suspend fun findTop5BestSellers() {
        // Firebase Firestore에서 Order 컬렉션 조회
        val orderResult = db.collection("Order").get().await()
        val salesMap = mutableMapOf<String, Int>()

        for (orderDocument in orderResult) {
            val productIdx = orderDocument["productIdx"] as String
            val orderNumber = orderDocument["orderNumber"] as Long
            if (salesMap.containsKey(productIdx)) {
                val currentSales = salesMap[productIdx] ?: 0
                salesMap[productIdx] = currentSales + orderNumber.toInt()
            } else {
                salesMap[productIdx] = orderNumber.toInt()
            }
        }

        val bestSellers = salesMap.entries.sortedByDescending { it.value }.take(5)
        val bestSellerProductIdxList = bestSellers.map { it.key }
        for (productIdx in bestSellerProductIdxList) {
            val productDocument = db.collection("Product").document(productIdx).get().await()
            val bestSellerIdx = productDocument["productIdx"] as String
            val bestSellerTitle = productDocument["productTitle"] as String
            bestIdxList.add(bestSellerIdx)
            bestSellerList.add(bestSellerTitle)
        }
    }
}
