import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import likelion.project.ipet_customer.R
import likelion.project.ipet_customer.databinding.ItemProductCardBinding
import likelion.project.ipet_customer.model.Joint
import likelion.project.ipet_customer.ui.main.MainActivity

class ProductJointAdapter(
    private val viewModel: JointViewModel,
    private val mainActivity: MainActivity
) : RecyclerView.Adapter<ProductJointAdapter.Holder>() {

    inner class Holder(rowBinding: ItemProductCardBinding):RecyclerView.ViewHolder(rowBinding.root) {
        var linearLayoutAddCostPrice: LinearLayout = rowBinding.linearLayoutItemAddCostPrice
        var linearLayoutAddTerm: LinearLayout = rowBinding.linearLayoutItemAddTerm
        private val imageViewCardHeart: ImageView = rowBinding.imageViewCardHeart
        private val imageViewCardThumbnail: ImageView = rowBinding.imageViewCardThumbnail
        private val textViewCardTitle: TextView = rowBinding.textViewCardTitle
        private val textViewCardCost: TextView = rowBinding.textViewCardCost
        private val textViewCardTerm: TextView = TextView(rowBinding.root.context)
        private val textViewCardMember: TextView = TextView(rowBinding.root.context)
        private val imageViewCardIcon: ImageView = ImageView(rowBinding.root.context)
        private val space: Space = Space(rowBinding.root.context)

        init {
            val spaceLayoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f)
            space.layoutParams = spaceLayoutParams
            linearLayoutAddCostPrice.addView(space)
            linearLayoutAddCostPrice.addView(imageViewCardIcon)
            linearLayoutAddCostPrice.addView(textViewCardMember)
            linearLayoutAddTerm.addView(textViewCardTerm)
            imageViewCardIcon.setImageResource(R.drawable.ic_groups_24dp)
            imageViewCardHeart.visibility = View.GONE
            rowBinding.root.setOnClickListener {
                val bundle = Bundle()
                val readJointIdx = viewModel.getJointList()[adapterPosition].jointIdx
                bundle.putString("readJointIdx", readJointIdx)
                bundle.putString("readToggle", "joint")
                mainActivity.replaceFragment(MainActivity.PRODUCT_INFO_FRAGMENT, true, bundle)
            }
        }

        fun bind(joint: Joint) {
            textViewCardTitle.text = joint.jointTitle
            textViewCardCost.text = "${mainActivity.formatNumberToCurrency(joint.jointPrice)}원"
            textViewCardTerm.run {
                typeface = ResourcesCompat.getFont(itemView.context, R.font.pretendard_regular)
                textSize = 12f
                text = joint.jointTerm
            }
            textViewCardMember.run {
                setTextAppearance(R.style.Typography_Regular12)
                text = "${joint.jointMember}/${joint.jointTotalMember}"
            }
            if (joint.jointImg[0] != "") {
                Glide.with(itemView)
                    .load(joint.jointImg[0])
                    .into(imageViewCardThumbnail)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val rowBinding = ItemProductCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        rowBinding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        rowBinding.linearLayoutItemAddTerm.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        return Holder(rowBinding)
    }

    override fun getItemCount(): Int {
        return viewModel.getJointList().size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val joint = viewModel.getJointList()[position]
        holder.bind(joint)
    }
}
