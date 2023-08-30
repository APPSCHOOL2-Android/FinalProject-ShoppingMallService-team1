import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import likelion.project.ipet_customer.databinding.ItemProductCardBinding
import likelion.project.ipet_customer.model.Product
import likelion.project.ipet_customer.ui.main.MainActivity

class SearchAdapter(
    private val productList: List<Product>,
    private val mainActivity: MainActivity,
) : RecyclerView.Adapter<SearchAdapter.Holder>() {

    inner class Holder(rowBinding: ItemProductCardBinding) : RecyclerView.ViewHolder(rowBinding.root) {
        val imageViewCardThumbnail: ImageView
        val imageViewCardHeart: ImageView
        val textViewCardTitle: TextView
        val textViewCardCost: TextView

        init {
            imageViewCardHeart = rowBinding.imageViewCardHeart
            imageViewCardThumbnail = rowBinding.imageViewCardThumbnail
            textViewCardTitle = rowBinding.textViewCardTitle
            textViewCardCost = rowBinding.textViewCardCost
            rowBinding.root.setOnClickListener {
                val bundle = Bundle()
                val readProductIdx = productList[adapterPosition].productIdx
                bundle.putString("readProductIdx", readProductIdx)
                bundle.putString("readToggle", "product")
                mainActivity.replaceFragment(MainActivity.PRODUCT_INFO_FRAGMENT, true, bundle)
            }
            imageViewCardHeart.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val rowBinding = ItemProductCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = Holder(rowBinding)

        rowBinding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return holder
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.textViewCardTitle.text = productList[position].productTitle
        holder.textViewCardCost.text = "${mainActivity.formatNumberToCurrency(productList[position].productPrice)}원"
        if (productList[position].productImg[0] != "") {
            Glide.with(holder.itemView)
                .load(productList[position].productImg[0])
                .into(holder.imageViewCardThumbnail)
        }
    }
}
