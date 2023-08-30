package likelion.project.ipet_seller.ui.product

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import likelion.project.ipet_seller.R
import likelion.project.ipet_seller.databinding.FragmentProductBinding
import likelion.project.ipet_seller.model.Product
import likelion.project.ipet_seller.ui.main.MainActivity

class ProductFragment : Fragment() {

    lateinit var fragmentProductBinding: FragmentProductBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentProductBinding = FragmentProductBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        arguments?.getString("product")?.let {
            val product = Json.decodeFromString(Product.serializer(), it)
            Log.d("product", "${product}")
            initView(product)
        }


        return fragmentProductBinding.root
    }

    private fun initView(product: Product?) {
        fragmentProductBinding.run {

            toolbarProduct.run {
                title = product?.productTitle
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.PRDUCT_FRAGMENT)
                }
                setOnMenuItemClickListener {
                    if (it.itemId == R.id.item_product_edit) {
                        val bundle = Bundle()
                        bundle.putString("product", Json.encodeToString(product))
                        mainActivity.replaceFragment(MainActivity.REGISTRATION_FRAGMENT, true, bundle)
                    }
                    true
                }
            }

            Glide.with(root)
                .load(product?.productImg?.get(0))
                .into(imageViewProduct)

            chipProductAniamlType.text = product?.productAnimalType
            chipProductLcategory.text = product?.productLcategory
            chipProductScategory.text = product?.productScategory
            textViewProductTitle.text = product?.productTitle
            textViewProductPrice.text = "가격: ${product?.productPrice}원"
            textViewProductStock.text = "재고: ${product?.productStock}개"
            textViewProductDescription.text = product?.productText
        }
    }
}