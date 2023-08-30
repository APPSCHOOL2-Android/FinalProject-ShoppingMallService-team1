package likelion.project.ipet_customer.ui.review

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import likelion.project.ipet_customer.R
import likelion.project.ipet_customer.databinding.FragmentReviewWriteBinding
import likelion.project.ipet_customer.ui.main.MainActivity
import java.io.File

class ReviewWriteFragment : Fragment() {

    lateinit var fragmentReviewWriteBinding: FragmentReviewWriteBinding
    lateinit var reviewAlbumLauncher : ActivityResultLauncher<Intent>
    lateinit var reviewCameraLauncher: ActivityResultLauncher<Intent>
    lateinit var mainActivity: MainActivity

    // 이미지가 저장될 위치
    lateinit var filePath : String
    // 저장된 파일에 접근하기 위한 Uri(import android.uri)
    lateinit var contentUri: Uri

    // 확인할 권한 목록
    val permissionList = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.ACCESS_MEDIA_LOCATION
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentReviewWriteBinding = FragmentReviewWriteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        // 권한 확인
        requestPermissions(permissionList, 0)

        val contract1 = ActivityResultContracts.StartActivityForResult()
        reviewAlbumLauncher = registerForActivityResult(contract1){

            if(it?.resultCode == RESULT_OK){

                // 선택한 이미지에 접근할 수 있는 Uri 객체를 추출한다.
                val uri = it.data?.data

                if (uri != null){
                    // 안드로이드 10(Q) 이상이라면
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

                        // 이미지를 생성할 수 있는 디코더를 생성한다.
                        val source = ImageDecoder.createSource(mainActivity.contentResolver, uri)

                        // Bitmap객체를 생성한다.
                        val bitmap = ImageDecoder.decodeBitmap(source)

                        // 메인 화면의 imageView에 해당 이미지를 설정한다.
                        fragmentReviewWriteBinding.imageViewReviewWriteImg.setImageBitmap(bitmap)

                    } else {
                        // Content Provider를 통해 이미지 데이터 정보를 가져온다.
                        val cursor = mainActivity.contentResolver.query(uri,null,null,null,null)

                        if(cursor != null) {
                            cursor.moveToNext()

                            // 이미지의 경로를 가져온다.
                            val idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                            val source = cursor.getString(idx)

                            // 이미지를 생성하여 보여준다.
                            val bitmap = BitmapFactory.decodeFile(source)
                            fragmentReviewWriteBinding.imageViewReviewWriteImg.setImageBitmap(bitmap)
                        }
                    }
                }
            }
        }

        // 애플리케이션을 위한 외부 저장소 경로를 가져온다.
        // xml/file_path.xml 에 등록한 경로를 가져온다.
        filePath = mainActivity.getExternalFilesDir(null).toString()

        // 사진 활영을 위한 런처
        val contract2 = ActivityResultContracts.StartActivityForResult()

        // 아래의 코드는 썸네일을 가져온다.(사진이 흐릿함)
        reviewCameraLauncher = registerForActivityResult(contract2){

            if (it?.resultCode == RESULT_OK){
                // uri를 이용해 이미지에 접근하여 Bitmap 객체로 생성한다.
                val bitmap = BitmapFactory.decodeFile(contentUri.path)

                // 이미지의 크기롤 조정한다.
                // 이미지의 축소/확대 비율을 구한다.
                val ratio = 1024.0 / bitmap.width
                // 세로 길이를 구한다.
                val targetHeight = (bitmap.height *ratio).toInt()

                // 크기를 조정한 Bitmap을 생성한다.
                val bitmap2 = Bitmap.createScaledBitmap(bitmap, 1024, targetHeight, false)

                // 회전 각도를 가져온다.
                val degree = getDegree(contentUri)

                // 회전 이미지를 생성하기 위한 변환 행렬
                val matrix = Matrix()
                matrix.postRotate(degree.toFloat())

                // 회전 행렬을 적용하여 회전된 이미지를 생성한다.
                // 원본 이미지, 원본 이미지에서의  X좌표, 원본 이미지에서의 Y좌표, 원본 가로 길이, 원본 세로 길이, 변환행렬, 필터 정보
                // 원본 이미지에서 지정된 x, y 좌표를 찍고 지정된 가로 세로 길이만큼의 이미지 데이터를 가져와 변환 행렬을 적용하여 이미지를 변환한다.
                val bitmap3 = Bitmap.createBitmap(bitmap2, 0,0, bitmap2.width, bitmap2.height, matrix, false)

                fragmentReviewWriteBinding.imageViewReviewWriteImg.setImageBitmap(bitmap3)

                // 이미지 파일은 삭제한다.
                val file = File(contentUri.path)
                file.delete()
            }
        }

        fragmentReviewWriteBinding.run {
            toolbarReviewWrite.run {
                title = "리뷰 작성하기"
                setNavigationIcon(R.drawable.ic_back_24dp)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.PRODUCT_INFO_FRAGMENT)
                }
            }
            imageViewReviewWriteGalleryButton.run {
                setOnClickListener {
                    // 앨범에서 사진을 선택할 수 있는 Activity를 실행한다.
                    val newIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                    // 실행할 액티비티의 MIME 타입 설정(이미지로 설정)
                    newIntent.setType("image/*")

                    // 선택할 파일의 타입을 지정(안드로이드 os가 이미지에 대한 사진 작업을 할 수 있도록)
                    val mineType = arrayOf("image/*")
                    newIntent.putExtra(Intent.EXTRA_MIME_TYPES, mineType)

                    // 액티비티 실행
                    reviewAlbumLauncher.launch(newIntent)
                }
            }

            imageViewReviewWriteCameraButton.run {
                setOnClickListener {
                    val newIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    // 촬영될 사진이 저장될 파일 이름
                    val now = System.currentTimeMillis()
                    val fileName = "/temp_${now}.jpg"
                    // 경로 + 파일 이름
                    val picPath = "${filePath}/${fileName}"

                    val file = File(picPath)

                    // 사진이 저장될 경로를 관리할 uri 객체를 생성한다.
                    contentUri = FileProvider.getUriForFile(mainActivity, "likelion.project.ipet_customer.ui.review", file)

                    newIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
                    reviewCameraLauncher.launch(newIntent)
                }
            }
        }

        return fragmentReviewWriteBinding.root
    }

    // 이미지 파일에 기록되어 있는 회전 정보를 가져온다.
    fun getDegree(uri:Uri) : Int{
        var exifInterface: ExifInterface? = null

        // 사진 파일로부터 tag 정보를 관리하는 객체를 추출한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val photoUri = MediaStore.setRequireOriginal(uri)
            // 스트림 추출
            val inputStream = mainActivity.contentResolver.openInputStream(photoUri)
            // ExifInterface 정보를 읽어 온다.
            exifInterface = ExifInterface(inputStream!!)
        } else {
            exifInterface = ExifInterface(uri.path!!)
        }

        var degree = 0

        if (exifInterface != null){
            // 각도 값을 가지고 온다.
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)

            when(orientation){
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        }

        return degree
    }
}