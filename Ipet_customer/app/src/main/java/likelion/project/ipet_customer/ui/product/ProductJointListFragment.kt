import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import likelion.project.ipet_customer.databinding.FragmentProductJointListBinding
import likelion.project.ipet_customer.repository.JointRepository
import likelion.project.ipet_customer.ui.main.MainActivity

class ProductJointListFragment : Fragment() {

    lateinit var fragmentProductJointListBinding: FragmentProductJointListBinding
    lateinit var mainActivity: MainActivity
    private lateinit var viewModel: JointViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = JointRepository()
        viewModel = ViewModelProvider(this, JointViewModelFactory(repository)).get(JointViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentProductJointListBinding = FragmentProductJointListBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentProductJointListBinding.run {
            toolbarProductJointList.title = "공동 구매 상품 리스트"

            recyclerProductJointList.run {
                adapter = ProductJointAdapter(viewModel, mainActivity)
                layoutManager = GridLayoutManager(context, 2)
            }
        }
        return fragmentProductJointListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 데이터를 가져와서 UI에 바인딩
        viewModel.loadJointData {
            fragmentProductJointListBinding.recyclerProductJointList.adapter?.notifyDataSetChanged()
        }
    }

}

class JointViewModelFactory (private val repository: JointRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JointViewModel::class.java)) {
            return JointViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
