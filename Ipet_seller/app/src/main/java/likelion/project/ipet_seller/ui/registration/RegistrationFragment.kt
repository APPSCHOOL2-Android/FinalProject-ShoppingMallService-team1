@file:Suppress("UNREACHABLE_CODE")

package likelion.project.ipet_seller.ui.registration

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import likelion.project.ipet_seller.R
import likelion.project.ipet_seller.databinding.FragmentRegistrationBinding
import likelion.project.ipet_seller.model.Product
import likelion.project.ipet_seller.ui.main.MainActivity

class RegistrationFragment : Fragment() {

    lateinit var fragmentRegistrationBinding: FragmentRegistrationBinding
    lateinit var mainActivity: MainActivity

    lateinit var albumActivityLauncher: ActivityResultLauncher<Intent>
    lateinit var viewModel: RegistrationViewModel
    private var product = Product()

    val permissionList = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_MEDIA_LOCATION
    )

    // 현재 선택된 ImageButton을 저장하는 변수
    private var selectedImageButton: ImageButton? = null

    val animalType = arrayOf("반려동물 선택", "강아지", "고양이")
    val mainCategory = arrayOf("대분류 선택", "사료", "간식", "장난감", "의류", "집")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentRegistrationBinding = FragmentRegistrationBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(
            this,
            RegistrationViewModelFactory(mainActivity)
        )[RegistrationViewModel::class.java]



        requestPermissions(permissionList, 0)

        if (arguments == null) {
            val contract1 = ActivityResultContracts.StartActivityForResult()
            val images = mutableListOf<String>()
            albumActivityLauncher(contract1, images)
            observe(0)
            initView()
        } else {
            var product = Product()
            arguments?.getString("product")?.let {
                product = Json.decodeFromString(Product.serializer(), it)
            }
            val contract1 = ActivityResultContracts.StartActivityForResult()
            val images = product.productImg.toMutableList()
            albumActivityLauncher(contract1, images, product)
            observe(1)
            initView(product)
        }
        return fragmentRegistrationBinding.root
    }

    private fun initView(productTmp: Product) {
        viewModel.setProduct(productTmp)
        val product = viewModel.uiState.value.product
        fragmentRegistrationBinding.run {
            setProductImage()
            setSpinner()
            editTextPrice.setText(product.productPrice.toString())
            editTextStock.setText(product.productStock.toString())
            editTextText.setText(product.productTitle)
            editTextTextMultiLine.setText(product.productText)
            val mainCategoryIdx = mainCategory.indexOf(product.productLcategory)
            val subCategory = getSubCategories(mainCategoryIdx)
            val subCategoryIdx = subCategory.indexOf(product.productScategory)
            val animalTypeIdx = animalType.indexOf(product.productAnimalType)
            selectSpinnerItem(spinnerRegistrationAnimalType, animalTypeIdx, animalType)
            selectSpinnerItem(spinnerRegistrationMainCategory, mainCategoryIdx, mainCategory)
            selectSpinnerItem(spinnerRegistrationSubCategory, subCategoryIdx, subCategory)
            when (product.productImg.size) {
                1 -> Glide.with(fragmentRegistrationBinding.root).load(product.productImg[0])
                    .into(imageButtonRegistration)

                2 -> {
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[0])
                        .into(imageButtonRegistration)
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[1])
                        .into(imageButton2Registration)
                }

                3 -> {
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[0])
                        .into(imageButtonRegistration)
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[1])
                        .into(imageButton2Registration)
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[2])
                        .into(imageButton3Registration)

                }

                4 -> {
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[0])
                        .into(imageButtonRegistration)
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[1])
                        .into(imageButton2Registration)
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[2])
                        .into(imageButton3Registration)
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[3])
                        .into(imageButton4Registration)
                }

                5 -> {
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[0])
                        .into(imageButtonRegistration)
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[1])
                        .into(imageButton2Registration)
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[2])
                        .into(imageButton3Registration)
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[3])
                        .into(imageButton4Registration)
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[4])
                        .into(imageButton5Registration)
                }

                6 -> {
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[0])
                        .into(imageButtonRegistration)
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[1])
                        .into(imageButton2Registration)
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[2])
                        .into(imageButton3Registration)
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[3])
                        .into(imageButton4Registration)
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[4])
                        .into(imageButton5Registration)
                    Glide.with(fragmentRegistrationBinding.root).load(product.productImg[5])
                        .into(imageButton6Registration)
                }
            }

            setToolbar("판매 상품 수정", 1)
        }
    }

    private fun selectSpinnerItem(
        spinner: Spinner,
        mainCategoryIdx: Int,
        categories: Array<String>
    ) {
        val adapter =
            ArrayAdapter(mainActivity, android.R.layout.simple_spinner_dropdown_item, categories)
        spinner.adapter = adapter
        spinner.setSelection(mainCategoryIdx)
    }

    private fun initView() {
        fragmentRegistrationBinding.run {
            setProductImage()
            setSpinner()
            setToolbar("상품 판매 등록", 0)
        }
    }

    private fun albumActivityLauncher(
        contract1: ActivityResultContracts.StartActivityForResult,
        images: MutableList<String>,
        product: Product = Product(),
    ) {
        albumActivityLauncher = registerForActivityResult(contract1) {

            if (it?.resultCode == AppCompatActivity.RESULT_OK) {
                val uri = it.data?.data

                if (uri != null) {
                    val bitmap: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val source = ImageDecoder.createSource(mainActivity.contentResolver, uri)
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        val cursor = mainActivity.contentResolver.query(uri, null, null, null, null)
                        cursor?.use {
                            it.moveToNext()
                            val idx = it.getColumnIndex(MediaStore.Images.Media.DATA)
                            val source = it.getString(idx)
                            BitmapFactory.decodeFile(source)
                        }
                    }
                    images.add(uri.toString())
                    viewModel.setProduct(product.copy(productImg = images))
                    selectedImageButton?.setImageBitmap(bitmap)
                }

            }
        }
    }

    private fun FragmentRegistrationBinding.setToolbar(
        toolbarTitle: String,
        status: Int
    ) {
        toolbarRegistration.run {
            title = toolbarTitle
            setNavigationIcon(R.drawable.ic_back_24dp)
            inflateMenu(R.menu.menu_registration)

            setNavigationOnClickListener {
                mainActivity.removeFragment(MainActivity.REGISTRATION_FRAGMENT)
            }
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.item_save -> {

                        val price = if (editTextPrice.text.toString().isEmpty()) {
                            0L
                        } else {
                            editTextPrice.text.toString().toLong()
                        }
                        val stock = if (editTextStock.text.toString().isEmpty()) {
                            0L
                        } else {
                            editTextStock.text.toString().toLong()
                        }

                        progressBarRegistraintion.visibility = View.VISIBLE
                        val product = viewModel.uiState.value.product
                        viewModel.setProduct(
                            product.copy(
                                productIdx = if (status == 1) product.productIdx else "",
                                productTitle = editTextText.text.toString(),
                                productPrice = price,
                                productText = editTextTextMultiLine.text.toString(),
                                productStock = stock,
                            )
                        )

                        val updatedProduct = viewModel.uiState.value.product
                        viewModel.onUploadClickEvent(
                            Product(
                                productIdx = if (status == 1) product.productIdx else "",
                                productTitle = editTextText.text.toString(),
                                productPrice = price,
                                productImg = updatedProduct.productImg,
                                productAnimalType = updatedProduct.productAnimalType,
                                productLcategory = updatedProduct.productLcategory,
                                productScategory = updatedProduct.productScategory,
                                productText = editTextTextMultiLine.text.toString(),
                                productStock = stock,
                            ), status
                        )
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun FragmentRegistrationBinding.setSpinner() {
        setupSpinner(spinnerRegistrationAnimalType, animalType) { parent, view, position, id ->
            viewModel.setProduct(viewModel.uiState.value.product.copy(productAnimalType = animalType[position]))
        }

        setupSpinner(
            spinnerRegistrationMainCategory,
            mainCategory
        ) { parent, view, position, id ->
            viewModel.setProduct(viewModel.uiState.value.product.copy(productLcategory = mainCategory[position]))
            viewModel.onSpinnerItemClick(position)

            setupSpinner(
                spinnerRegistrationSubCategory,
                getSubCategories(position)
            ) { subParent, subView, subPosition, subId ->
                val subCategory = getSubCategories(position)
                viewModel.setProduct(viewModel.uiState.value.product.copy(productScategory = subCategory[subPosition]))
            }
        }
    }

    private fun FragmentRegistrationBinding.setProductImage() {
        imageButtonRegistration.run {
            setOnClickListener {
                selectedImageButton = this
                launchAlbumPicker()
            }
        }

        imageButton2Registration.run {
            setOnClickListener {
                selectedImageButton = this
                launchAlbumPicker()
            }
        }

        imageButton3Registration.run {
            setOnClickListener {
                selectedImageButton = this
                launchAlbumPicker()
            }
        }

        imageButton4Registration.run {
            setOnClickListener {
                selectedImageButton = this
                launchAlbumPicker()
            }
        }

        imageButton5Registration.run {
            setOnClickListener {
                selectedImageButton = this
                launchAlbumPicker()
            }
        }

        imageButton6Registration.run {
            setOnClickListener {
                selectedImageButton = this
                launchAlbumPicker()
            }
        }

        imageButton7Registration.run {
            setOnClickListener {
                selectedImageButton = this
                launchAlbumPicker()
            }
        }

        imageButton8Registration.run {
            setOnClickListener {
                selectedImageButton = this
                launchAlbumPicker()
            }
        }
    }

    private fun observe(status: Int) {
        lifecycleScope.launch {
            launch {
                viewModel.event.collect {
                    when (it) {
                        is Result.Success -> {
                            showSnackBar(fragmentRegistrationBinding.root, it.message)
                            if (status == 0) {
                                mainActivity.removeFragment(MainActivity.REGISTRATION_FRAGMENT)
                            } else {
                                val bundle = Bundle()
                                val product = viewModel.uiState.value.product
                                bundle.putString("product", Json.encodeToString(product))
                                mainActivity.replaceFragment(
                                    MainActivity.PRDUCT_FRAGMENT,
                                    false,
                                    bundle
                                )
                            }
                            fragmentRegistrationBinding.progressBarRegistraintion.visibility =
                                View.GONE

                        }

                        is Result.Failure -> {
                            showSnackBar(fragmentRegistrationBinding.root, it.message)
                            fragmentRegistrationBinding.progressBarRegistraintion.visibility =
                                View.GONE
                        }

                        is Result.Error -> {
                            showSnackBar(fragmentRegistrationBinding.root, it.error.message!!)
                            fragmentRegistrationBinding.progressBarRegistraintion.visibility =
                                View.GONE
                        }
                    }
                }
            }

            launch {
                viewModel.spinnerEvent.collect {
                    val subCategory = getSubCategories(it)
                    setupSpinner(
                        fragmentRegistrationBinding.spinnerRegistrationSubCategory,
                        subCategory
                    ) { parent, view, position, id ->
                        product = product.copy(productScategory = subCategory[position])
                    }
                    delay(200)
                }
            }
        }
    }

    private fun getSubCategories(it: Int) = when (mainCategory[it]) {
        "사료" -> arrayOf("주니어", "어덜트", "시니어", "다이어트", "건식", "습식")
        "간식" -> arrayOf("껌", "스낵", "육포", "캔", "비스켓", "기타")
        "장난감" -> arrayOf("공", "인형", "큐브", "훈련용품", "스크래쳐", "기타")
        "의류" -> arrayOf("레인코트", "신발", "외투", "원피스", "셔츠", "기타")
        "집" -> arrayOf("계단", "매트", "울타리", "안전문", "하우스", "기타")
        else -> arrayOf("대분류를 선택해주세요")
    }

    private fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun launchAlbumPicker() {
        val newIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        newIntent.type = "image/*"
        val mimeType = arrayOf("image/*")
        newIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
        albumActivityLauncher.launch(newIntent)
    }

    // 스피너 설정 메서드
    private fun setupSpinner(
        spinner: Spinner,
        data: Array<String>,
        onItemSelected: (parent: AdapterView<*>?, view: View?, position: Int, id: Long) -> Unit
    ) {
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            data
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                onItemSelected(parent, view, position, id)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

}