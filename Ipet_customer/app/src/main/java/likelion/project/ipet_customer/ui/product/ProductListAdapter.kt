package likelion.project.ipet_customer.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import likelion.project.ipet_customer.databinding.ItemProductCardBinding
import likelion.project.ipet_customer.model.Product
import likelion.project.ipet_customer.ui.main.MainActivity

class ProductListAdapter(
    private val productList: List<Product>,
    private val mainActivity: MainActivity
) : RecyclerView.Adapter<ProductListAdapter.Holder>() {

    inner class Holder(itemProductCardBinding: ItemProductCardBinding) :
        RecyclerView.ViewHolder(itemProductCardBinding.root) {
        val imageViewCardThumbnail: ImageView
        val textViewCardTitle: TextView
        val textViewCardCost: TextView

        init {
            imageViewCardThumbnail = itemProductCardBinding.imageViewCardThumbnail
            textViewCardTitle = itemProductCardBinding.textViewCardTitle
            textViewCardCost = itemProductCardBinding.textViewCardCost
            itemProductCardBinding.root.setOnClickListener {
                var bundle = Bundle()
                val readProductIdx = productList[adapterPosition].productIdx
                bundle.putString("readProductIdx", readProductIdx)
                bundle.putString("readToggle", "product")
                mainActivity.replaceFragment(MainActivity.PRODUCT_INFO_FRAGMENT, false, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemProductCardBinding =
            ItemProductCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(itemProductCardBinding)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.textViewCardTitle.text = productList[position].productTitle
        holder.textViewCardCost.text = productList[position].productPrice.toString()
        if (productList[position].productImg[0] != "") {
            Glide.with(holder.itemView)
                .load(productList[position].productImg[0])
                .into(holder.imageViewCardThumbnail)
        }
    }
}