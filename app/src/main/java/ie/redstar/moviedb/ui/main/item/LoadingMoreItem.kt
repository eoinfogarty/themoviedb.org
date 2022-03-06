package ie.redstar.moviedb.ui.main.item

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import ie.redstar.moviedb.R
import ie.redstar.moviedb.databinding.MainListItemLoadingMoreBinding

class LoadingMoreItem : BindableItem<MainListItemLoadingMoreBinding>() {

    override fun getLayout() = R.layout.main_list_item_loading_more

    override fun initializeViewBinding(view: View) = MainListItemLoadingMoreBinding.bind(view)

    override fun bind(viewBinding: MainListItemLoadingMoreBinding, position: Int) {

    }
}