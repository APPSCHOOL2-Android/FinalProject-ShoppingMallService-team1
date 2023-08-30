package likelion.project.ipet_customer.ui.userinfo

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import likelion.project.ipet_customer.R
import likelion.project.ipet_customer.databinding.FragmentUserInfoAddressBinding
import likelion.project.ipet_customer.ui.login.LoginViewModel
import likelion.project.ipet_customer.ui.main.MainActivity

class UserInfoAddressFragment : Fragment() {

    lateinit var fragmentUserInfoAddressBinding: FragmentUserInfoAddressBinding
    lateinit var mainActivity: MainActivity
    val blogAddress = "https://s3.ap-northeast-2.amazonaws.com/ipet.address.insert/index.html"
    val userInfoViewModel = UserInfoViewModel()

    inner class JSInterface {
        @JavascriptInterface
        fun processDATA(data: String?) {
            Log.d("woojugoing", data!!)
            val newBundle = Bundle()
            newBundle.putString("data", data)
            // 주소 데이터 ViewModel 저장
            LoginViewModel.customer.customerAddressAddress = data
            userInfoViewModel.saveAddress(LoginViewModel.customer)
            mainActivity.replaceFragment(MainActivity.USER_INFO_MAIN_FRAGMENT, false, newBundle)
            Toast.makeText(mainActivity, "상세 주소를 이어서 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentUserInfoAddressBinding = FragmentUserInfoAddressBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentUserInfoAddressBinding.run {
            webViewAddress!!.run {
                settings.javaScriptEnabled = true
                addJavascriptInterface(JSInterface(), "Android")
                webViewClient = object: WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        loadUrl("javascript:sample2_execDaumPostcode();")
                    }
                }
                loadUrl(blogAddress)
            }
        }
        return fragmentUserInfoAddressBinding.root
    }
}