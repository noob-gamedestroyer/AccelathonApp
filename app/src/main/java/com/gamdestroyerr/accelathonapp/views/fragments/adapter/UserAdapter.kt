package com.gamdestroyerr.accelathonapp.views.fragments.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.gamdestroyerr.accelathonapp.R
import com.gamdestroyerr.accelathonapp.databinding.ItemUserBinding
import com.gamdestroyerr.accelathonapp.model.User
import javax.inject.Inject

class UserAdapter @Inject constructor(
        private val glide: RequestManager
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemUserBinding = ItemUserBinding.bind(itemView)

        val ivImageProfile = itemUserBinding.ivProfileImage
        val tvUsername = itemUserBinding.tvUsername
        val tvApartmentName = itemUserBinding.tvApartmentName
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<User>() {
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }
    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    var users: List<User>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): UserAdapter.UserViewHolder {
        return UserViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.item_user,
                        parent,
                        false,
                )
        )
    }

    override fun onBindViewHolder(holder: UserAdapter.UserViewHolder, position: Int) {
        val user = users[position]
        holder.apply {
            glide.load(user.profilePicture).into(ivImageProfile)

            tvUsername.text = user.username
            tvApartmentName.text = user.apartmentName
            itemView.setOnClickListener {
                onUserClickListener?.let { click ->
                    click(user)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    //onClick for individual post items
    private var onUserClickListener: ((User) -> Unit)? = null


    fun setOnUserClickListener(listener: (User) -> Unit) {
        onUserClickListener = listener
    }
}