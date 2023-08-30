package likelion.project.ipet_customer.ui.product

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import likelion.project.ipet_customer.databinding.FragmentProductInfoBottomSheetBinding
import likelion.project.ipet_customer.ui.main.MainActivity

class ProductInfoBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var fragmentProductInfoBottomSheetBinding: FragmentProductInfoBottomSheetBinding
    lateinit var mainActivity: MainActivity
    lateinit var bottomSheetListener: BottomSheetListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentProductInfoBottomSheetBinding = FragmentProductInfoBottomSheetBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentProductInfoBottomSheetBinding.run {
            // 원가 가격 표시
            textviewBottomsheetCostprice.paintFlags =
                textviewBottomsheetCostprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            var num = 1
            // 왼쪽 화살표 버튼
            imagebuttonBottomsheetMinus.setOnClickListener {
                if (num > 1) {
                    num--
                    textviewBottomsheetNumber.text = "$num"
                }
            }
            // 오른쪽 화살표 버튼
            imagebuttonBottomsheetPlus.setOnClickListener {
                num++
                textviewBottomsheetNumber.text = "$num"
            }

            buttonBottomsheetBuy.setOnClickListener {
                bottomSheetListener.onBuyButtonClicked(num) // 구매 버튼 클릭 이벤트 전달
                dismiss()
            }

            buttonBottomsheetCart.setOnClickListener {
                bottomSheetListener.onCartButtonClicked(num) // 장바구니 버튼 클릭 이벤트 전달
                dismiss()
            }
        }

        return fragmentProductInfoBottomSheetBinding.root
    }

    companion object {
        fun newInstance(): ProductInfoBottomSheetFragment {
            return ProductInfoBottomSheetFragment()
        }
    }

    interface BottomSheetListener {
        fun onBuyButtonClicked(num:Int)
        fun onCartButtonClicked(num:Int)
    }
}