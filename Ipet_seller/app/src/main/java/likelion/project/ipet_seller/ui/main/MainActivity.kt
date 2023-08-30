package likelion.project.ipet_seller.ui.main

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import likelion.project.ipet_seller.R
import likelion.project.ipet_seller.databinding.ActivityMainBinding
import likelion.project.ipet_seller.ui.home.HomeFragment
import likelion.project.ipet_seller.ui.login.LoginFragment
import likelion.project.ipet_seller.ui.order.OrderStatusFragment
import likelion.project.ipet_seller.ui.product.ProductFragment
import likelion.project.ipet_seller.ui.product.ProductListFragment
import likelion.project.ipet_seller.ui.registration.RegistrationFragment
import likelion.project.ipet_seller.ui.revenue.RevenueFragment
import likelion.project.ipet_seller.ui.signup.SignupFragment

class MainActivity : AppCompatActivity() {

    lateinit var activityMainBinding: ActivityMainBinding

    var newFragment: Fragment? = null
    var oldFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        replaceFragment(LOGIN_FRAGMENT, false, null)
    }

    fun replaceFragment(name: String, addToBackStack: Boolean, bundle: Bundle?) {
        SystemClock.sleep(200)
        setOldFragmentTransition()
        newFragment = getFragmentByName(name)
        newFragment?.arguments = bundle
        setNewFragmentTransition()
        performFragmentTransaction(addToBackStack, name)
    }

    private fun getFragmentByName(name: String): Fragment? {
        return when (name) {
            LOGIN_FRAGMENT -> LoginFragment()
            HOME_FRAGMENT -> HomeFragment()
            SIGNUP_FRAGMENT -> SignupFragment()
            ORDER_STATUS_FRAGMENT -> OrderStatusFragment()
            PRODUCT_LIST_FRAGMENT -> ProductListFragment()
            REGISTRATION_FRAGMENT -> RegistrationFragment()
            REVENUE_FRAGMENT -> RevenueFragment()
            PRDUCT_FRAGMENT -> ProductFragment()
            else -> Fragment()
        }
    }

    private fun setOldFragmentTransition() {
        if (newFragment != null) {
            oldFragment = newFragment
            oldFragment?.apply {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                enterTransition = null
                returnTransition = null
            }
        }
    }

    private fun setNewFragmentTransition() {
        newFragment?.apply {
            exitTransition = null
            reenterTransition = null
            enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        }
    }

    private fun performFragmentTransaction(addToBackStack: Boolean, name: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        newFragment?.let { fragment ->
            fragmentTransaction.replace(R.id.fragmentContainerView_main_container, fragment)
            if (addToBackStack) {
                fragmentTransaction.addToBackStack(name)
            }
            fragmentTransaction.commit()
        }
    }

    fun removeFragment(name: String) {
        supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun removeFragment(name: String, bundle: Bundle) {

    }

    companion object {
        val LOGIN_FRAGMENT = "LoginFragment"
        val HOME_FRAGMENT = "HomeFragment"
        val SIGNUP_FRAGMENT = "SignupFragment"
        val ORDER_STATUS_FRAGMENT = "OrderStatusFragment"
        val PRODUCT_LIST_FRAGMENT = "ProductListFragment"
        val REGISTRATION_FRAGMENT = "RegistrationFragment"
        val REVENUE_FRAGMENT = "RevenueFragment"
        val PRDUCT_FRAGMENT = "ProductFragment"
    }
}