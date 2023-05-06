package com.genesys.challenge.redamehali.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.genesys.challenge.redamehali.commons.Constant
import com.genesys.challenge.redamehali.databinding.RandomUserItemBinding
import com.genesys.challenge.redamehali.interfaces.UserClickListener
import com.genesys.challenge.redamehali.models.User
import com.genesys.challenge.redamehali.utils.UiUtil

/**
 * Adapter of the random users. Responsible for creating item views for each row, and binding its data to the view.
 * Adapter uses @param userCLickListener to inform its host that an item was clicked. Uses DiffUtil for
 * calculating the difference between two lists efficiently
 */
class RandomUsersAdapter(private val userClickListener: UserClickListener) : RecyclerView.Adapter<RandomUsersAdapter.RandomUserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RandomUserViewHolder {
        val binding = RandomUserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RandomUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RandomUserViewHolder, position: Int) {
        holder.setData(differ.currentList[holder.adapterPosition])
    }

    override fun getItemCount(): Int = differ.currentList.size

    /**
     * Submits random users list
     * @param userList The list containing users and its meta-data
     */
    fun submitList(userList: List<User>) {
        differ.submitList(userList)
    }

    /**
     * Class for User View holder. Responsible for binding meta-data of a user to its item view
     */
    inner class RandomUserViewHolder(private val binding: RandomUserItemBinding) : RecyclerView.ViewHolder(binding.root) {
        /**
         * Sets user data into item view
         * @param user The object containing the "user" data
         */
        fun setData(user: User) {
            binding.apply {
                // Loads the medium size picture into the image, and circle crops it using Glide
                Glide.with(itemView).load(Uri.parse(user.picture.medium))
                    .apply(RequestOptions.circleCropTransform()).into(userImage)
                firstLastName.text = UiUtil.appendName(user.name.title, user.name.first, user.name.last)
                phoneNumber.text = user.phone
                // Listener to callback the item clicked from the recyclerview
                itemView.setOnClickListener {
                    val bundle = bundleOf()
                    bundle.putParcelable(Constant.USER_BUNDLE_KEY, user)
                    userClickListener.onItemClick(itemView, bundle, adapterPosition)
                }
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return  oldItem.login.uuid == newItem.login.uuid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    companion object {
        const val TAG = "RandomUsersAdapter"
    }
}