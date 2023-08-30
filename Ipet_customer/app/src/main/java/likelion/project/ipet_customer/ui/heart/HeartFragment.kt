package likelion.project.ipet_customer.ui.heart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import likelion.project.ipet_customer.R
import likelion.project.ipet_customer.databinding.FragmentHeartBinding
import likelion.project.ipet_customer.databinding.ItemHeartListBinding
import likelion.project.ipet_customer.ui.main.MainActivity

class HeartFragment : Fragment() {

    lateinit var fragmentHeartBinding: FragmentHeartBinding
    lateinit var mainActivity : MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHeartBinding = FragmentHeartBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentHeartBinding.run {
            toolbarHeart.run {
                title = "찜"
                setNavigationIcon(R.drawable.ic_back_24dp)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.HEART_FRAGMENT)
                }
            }

            recyclerViewHeartList.run {
                adapter = RecyclerViewAdapter()
                layoutManager = LinearLayoutManager(mainActivity)
            }
        }

        return fragmentHeartBinding.root
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){

        inner class ViewHolder(itemBinding : ItemHeartListBinding) : RecyclerView.ViewHolder(itemBinding.root){

            var textViewBrand : TextView
            var textViewTitle : TextView
            var textViewPrice : TextView
            var imageView : ImageView

            init {
                textViewBrand = itemBinding.textViewHeartBrand
                textViewTitle = itemBinding.textViewHeartTitle
                textViewPrice = itemBinding.textViewHeartPrice
                imageView = itemBinding.imageViewHeartImg
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemBinding = ItemHeartListBinding.inflate(layoutInflater)
            val viewHolder = ViewHolder(itemBinding)

            // 항목 View의 가로세로 길이를 설정해준다(터치 때문에)
            val params = RecyclerView.LayoutParams(
                // 가로 길이
                RecyclerView.LayoutParams.MATCH_PARENT,
                // 세로 길이
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            itemBinding.root.layoutParams = params

            return viewHolder
        }

        override fun getItemCount(): Int {
            return 3
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        }
    }
}