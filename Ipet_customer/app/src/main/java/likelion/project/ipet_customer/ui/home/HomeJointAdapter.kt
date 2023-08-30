package likelion.project.ipet_customer.ui.home

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import likelion.project.ipet_customer.R
import likelion.project.ipet_customer.databinding.ItemProductCardBinding
import likelion.project.ipet_customer.model.Joint
import likelion.project.ipet_customer.ui.main.MainActivity
import java.text.NumberFormat
import java.util.Locale

class HomeJointAdapter(private val mainActivity: MainActivity, val jointsList: MutableList<Joint>): RecyclerView.Adapter<HomeJointAdapter.JointViewHolder>(){

    inner class JointViewHolder(private val binding: ItemProductCardBinding) : RecyclerView.ViewHolder(binding.root){
        var linearLayoutAddMember: LinearLayout = binding.linearLayoutItemAddMember
        var linearLayoutAddCostPrice: LinearLayout = binding.linearLayoutItemAddCostPrice
        var linearLayoutAddTerm: LinearLayout = binding.linearLayoutItemAddTerm

        var imageViewHeart: ImageView = binding.imageViewCardHeart
        var itemJointTerm: TextView = TextView(binding.root.context)
        var itemJointImg: ImageView = binding.imageViewCardThumbnail
        var itemJointTitle: TextView = binding.textViewCardTitle
        var itemJointMember: TextView = TextView(binding.root.context)
        var itemJointMemberIc: ImageView = ImageView(binding.root.context)
        var itemJointPrice: TextView = binding.textViewCardCost
        var itemJointCostPrice: TextView = TextView(binding.root.context)

        init {
            // 추가되는 View들의 test data
            itemJointTerm.text = "08.10 ~ 09.23"
            itemJointMember.text = "9/100"
            itemJointCostPrice.text = "12,000원"
            itemJointMemberIc.setImageResource(R.drawable.ic_groups_24dp)

            // View 추가 및 제거
            linearLayoutAddMember.addView(itemJointMemberIc)
            linearLayoutAddMember.addView(itemJointMember)
            linearLayoutAddCostPrice.addView(itemJointCostPrice)
            linearLayoutAddTerm.addView(itemJointTerm)
            imageViewHeart.visibility = View.GONE

            // 공동 구매 상품 클릭 시
            binding.root.setOnClickListener {
                var bundle = Bundle()
                val readJointIdx = jointsList[adapterPosition].jointIdx
                bundle.putString("readJointIdx", readJointIdx)
                bundle.putString("readToggle", "joint")

                mainActivity.replaceFragment(MainActivity.PRODUCT_INFO_FRAGMENT, true, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JointViewHolder {
        val binding = ItemProductCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val jointViewHolder = JointViewHolder(binding)

        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // linearLayout height 조정
        binding.linearLayoutItemAddTerm.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT

        return jointViewHolder
    }

    override fun getItemCount(): Int = jointsList.size

    override fun onBindViewHolder(holder: JointViewHolder, position: Int) {

        val joint = jointsList[position] // 해당 포지션의 Joint 객체 가져오기

        holder.itemJointCostPrice.run {
            typeface = ResourcesCompat.getFont(holder.itemView.context, R.font.pretendard_regular)
            textSize = 12f
            setTextColor(ContextCompat.getColor(mainActivity, R.color.gray))
            paintFlags = holder.itemJointCostPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        holder.itemJointTerm.run {
            typeface = ResourcesCompat.getFont(holder.itemView.context, R.font.pretendard_regular)
            textSize = 12f
            text = joint.jointTerm
        }

        holder.itemJointMember.run{
            setTextAppearance(R.style.Typography_Regular12)
            text = "${joint.jointMember}/${joint.jointTotalMember}"
        }

        holder.itemJointMemberIc.setColorFilter(ContextCompat.getColor(mainActivity, R.color.brown_200))
        holder.itemJointTitle.text = adjustTitleLength(joint.jointTitle)

        holder.itemJointPrice.text = "${mainActivity.formatNumberToCurrency(joint.jointPrice)}원"
        if (joint.jointImg[0] != "") {
            Glide.with(holder.itemView)
                .load(joint.jointImg[0])
                .into(holder.itemJointImg)
        }
    }

    private fun adjustTitleLength(title : String) : String {
        var adjustTitle = title

        if (adjustTitle.length > 8){
            adjustTitle = adjustTitle.subSequence(0, 8).toString()
            adjustTitle += "..."
        }

        return adjustTitle
    }
}
