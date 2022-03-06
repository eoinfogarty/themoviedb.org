package ie.redstar.moviedb.ui.main.item

import android.view.View
import androidx.annotation.StringRes
import com.xwray.groupie.viewbinding.BindableItem
import ie.redstar.moviedb.R
import ie.redstar.moviedb.databinding.MainListItemTitleBinding

class TitleListItem(
    @StringRes private val title: Int
) : BindableItem<MainListItemTitleBinding>() {

    override fun getLayout() = R.layout.main_list_item_title

    override fun initializeViewBinding(view: View) = MainListItemTitleBinding.bind(view)

    override fun bind(viewBinding: MainListItemTitleBinding, position: Int) {
        viewBinding.title.text = viewBinding.root.context.getString(title)
    }

    override fun getId() = title.hashCode().toLong()

    override fun hashCode() = title.hashCode()

    override fun equals(other: Any?): Boolean {
        return (other as? TitleListItem)?.title == title
    }
}