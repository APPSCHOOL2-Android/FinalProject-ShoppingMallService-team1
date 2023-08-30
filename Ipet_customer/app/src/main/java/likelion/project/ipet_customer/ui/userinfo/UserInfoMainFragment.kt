package likelion.project.ipet_customer.ui.userinfo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import likelion.project.ipet_customer.R
import likelion.project.ipet_customer.databinding.FragmentUserInfoMainBinding
import likelion.project.ipet_customer.databinding.ItemUserinfoChangeaddressBinding
import likelion.project.ipet_customer.databinding.ItemUserinfoDrawelcheckBinding
import likelion.project.ipet_customer.ui.login.LoginViewModel
import likelion.project.ipet_customer.ui.main.MainActivity

class UserInfoMainFragment : Fragment() {

    lateinit var fragmentUserInfoMainBinding: FragmentUserInfoMainBinding
    lateinit var mainActivity: MainActivity
    lateinit var userInfoViewModel: UserInfoViewModel
    var fragmentState = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentUserInfoMainBinding = FragmentUserInfoMainBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        userInfoViewModel = UserInfoViewModel()

        fragmentUserInfoMainBinding.run {
            userInfoViewModel.viewModelScope.launch {
                textViewUserInfoDeliveryConfirm.text = userInfoViewModel.getAllStatus()[0].toString()
                textViewUserInfoDeliveryWay.text = userInfoViewModel.getAllStatus()[1].toString()
                textViewUserInfoDeliveryWay2.text = userInfoViewModel.getAllStatus()[2].toString()
            }

            textViewUserInfoAddress.run {
                typeface = ResourcesCompat.getFont(context, R.font.pretendard_regular)
                text = "${LoginViewModel.customer.customerAddressAddress}" +
                        " ${LoginViewModel.customer.customerAddressDetail}"
            }

            toolbarUserInfoMain.run {
                title = "내 정보"
            }

            textViewUserInfoName.run{
                text = LoginViewModel.customer.customerName + "님"
            }

            textViewUserInfoEmail.run{
                text = LoginViewModel.customer.customerEmail
            }

            // 취소/반품/교환
            imageViewUserInfoCancel.setOnClickListener {
                fragmentState = "Cancel"
                val newBundle = Bundle()
                newBundle.putString("fragmentState", fragmentState)
                mainActivity.replaceFragment(MainActivity.ORDER_STATUS_FRAGMENT, true, newBundle)
            }

            // 판매/배송 더보기
            textViewUserInfoDetails.setOnClickListener {
                fragmentState = "Detail"
                val newBundle = Bundle()
                newBundle.putString("fragmentState", fragmentState)
                mainActivity.replaceFragment(MainActivity.ORDER_STATUS_FRAGMENT, true, newBundle)
            }

            // 리뷰
            imageViewUserInfoReview.setOnClickListener {
                fragmentState = "Review"
                val newBundle = Bundle()
                newBundle.putString("fragmentState", fragmentState)
                mainActivity.replaceFragment(MainActivity.ORDER_STATUS_FRAGMENT, true, newBundle)
            }

            // 장바구니
            imageViewUserInfoShopBasket.setOnClickListener {
                fragmentState = "Shopping"
                val newBundle = Bundle()
                newBundle.putString("fragmentState", fragmentState)
                mainActivity.replaceFragment(MainActivity.SHOPPING_BASKET_FRAGMENT, true, newBundle)
            }

            // 쿠폰
            imageViewUserInfoCoupon.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.COUPON_FRAGMENT, true, null)
            }

            // 찜
            imageViewUserInfoFavorite.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.HEART_FRAGMENT, true, null)
            }

            // 로그아웃
            layoutUserInfoLogout.setOnClickListener {
                userInfoViewModel.logout()
                mainActivity.activityMainBinding.bottomNavigation.visibility = View.GONE
                mainActivity.replaceFragment(MainActivity.LOGIN_FRAGMENT, false, null)
            }

            // 배송지 변경
            layoutUserInfoChangeAddress.setOnClickListener {
                val binding = ItemUserinfoChangeaddressBinding.inflate(LayoutInflater.from(context))
                val builder = MaterialAlertDialogBuilder(mainActivity)
                builder.setView(binding.root)
                val dialog = builder.create()

                // 주소 표시
                binding.textViewChangeAddressAddress.text = LoginViewModel.customer.customerAddressAddress

                // 배송지 주소 검색
                binding.buttonChangeAddressAdd.setOnClickListener {
                    mainActivity.replaceFragment(MainActivity.USER_INFO_ADDRESS_FRAGMENT, true, null)
                    dialog.dismiss()
                }

                // 입력 완료
                binding.buttonChangeAddressConfirm.setOnClickListener {
                    Toast.makeText(mainActivity, "상세 주소가 정상적으로 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    // 사용자 입력 상세주소 ViewModel 저장
                    LoginViewModel.customer.customerAddressDetail = binding.editTextChangeAddressDetail.text.toString()
                    userInfoViewModel.saveAddress(LoginViewModel.customer)
                    textViewUserInfoAddress.text = "${LoginViewModel.customer.customerAddressAddress}" +
                            " ${LoginViewModel.customer.customerAddressDetail}"
                    dialog.dismiss()
                }

                if(LoginViewModel.customer.customerAddressAddress == "배송지 미설정") {
                    binding.textViewChangeAddressAddress.visibility = View.GONE
                    binding.editTextChangeAddressDetail.visibility = View.GONE
                    binding.buttonChangeAddressConfirm.visibility = View.GONE
                }
                dialog.show()
            }

            // 회원 탈퇴
            layoutUserInfoWithDrawal.setOnClickListener {
                val binding = ItemUserinfoDrawelcheckBinding.inflate(LayoutInflater.from(context))
                val builder = MaterialAlertDialogBuilder(mainActivity)
                builder.setView(binding.root)
                val dialog = builder.create()

                binding.buttonDrawelCheckYes.setOnClickListener {
                    userInfoViewModel.signOut(LoginViewModel.customer, mainActivity)
                    mainActivity.activityMainBinding.bottomNavigation.visibility = View.GONE
                    mainActivity.replaceFragment(MainActivity.LOGIN_FRAGMENT, false, null)
                    dialog.dismiss()
                }

                binding.buttonDrawelCheckNo.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            }
        }
        return fragmentUserInfoMainBinding.root
    }
}