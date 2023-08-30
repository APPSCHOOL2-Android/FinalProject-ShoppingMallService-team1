import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import likelion.project.ipet_customer.R
import likelion.project.ipet_customer.databinding.ItemProductCardBinding
import likelion.project.ipet_customer.model.Product
import likelion.project.ipet_customer.ui.main.MainActivity
import likelion.project.ipet_customer.ui.search.SearchViewModel

class SearchAdapter(
    private val productList: List<Product>,
    private val mainActivity: MainActivity,
    private val searchViewModel: SearchViewModel
) : RecyclerView.Adapter<SearchAdapter.Holder>() {

    inner class Holder(rowBinding: ItemProductCardBinding) : RecyclerView.ViewHolder(rowBinding.root) {
        val imageViewCardThumbnail: ImageView
        val imageViewCardHeart: ImageView
        val textViewCardTitle: TextView
        val textViewCardCost: TextView

        init {
            var isFavorite = false
            imageViewCardHeart = rowBinding.imageViewCardHeart
            imageViewCardThumbnail = rowBinding.imageViewCardThumbnail
            textViewCardTitle = rowBinding.textViewCardTitle
            textViewCardCost = rowBinding.textViewCardCost
            rowBinding.root.setOnClickListener {
                var bundle = Bundle()
                val readProductIdx = productList[adapterPosition].productIdx
                bundle.putString("readProductIdx", readProductIdx)
                bundle.putString("readToggle", "product")
                mainActivity.replaceFragment(MainActivity.PRODUCT_INFO_FRAGMENT, true, bundle)
            }
            imageViewCardHeart.setOnClickListener {
                isFavorite = !isFavorite
                if(isFavorite) {
                    imageViewCardHeart.setImageResource(R.drawable.ic_favorite_fill_48dp)
                    searchViewModel.addHeart(productList[adapterPosition].productIdx)
                } else {
                    imageViewCardHeart.setImageResource(R.drawable.ic_favorite_48dp)
                    searchViewModel.deleteHeart(productList[adapterPosition].productIdx)
                }
            }
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
