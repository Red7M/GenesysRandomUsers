package com.genesys.challenge.redamehali.fragments

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.genesys.challenge.redamehali.commons.Constant
import com.genesys.challenge.redamehali.databinding.UserDetailsFragmentBinding
import com.genesys.challenge.redamehali.models.User
import com.genesys.challenge.redamehali.utils.UiUtil
import dagger.hilt.android.AndroidEntryPoint

/**
 * [UserDetailsFragment] responsible for displaying detailed meta-data of a User
 * after its been selected from the recyclerview UI.
 */
@AndroidEntryPoint
class UserDetailsFragment : Fragment() {

    private var _binding: UserDetailsFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = UserDetailsFragmentBinding.inflate(inflater, container, false)
        getData()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Writes user-data into "user" global variable through bundle access
     */
    private fun getData() {
        user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(Constant.USER_BUNDLE_KEY, User::class.java)!!
        } else {
            requireArguments().getParcelable(Constant.USER_BUNDLE_KEY)!!
        }
        Log.i(TAG, "User is: ${user.name}")
        // Sets views used in the fragment after user pojo populated.
        setViews()
    }

    /**
     * Sets all views of the details Fragment
     */
    private fun setViews() {
        // Primary information
        Glide.with(this).load(Uri.parse(user.picture.large)).apply(RequestOptions.circleCropTransform()).into(binding.userPhoto)
        binding.name.text = UiUtil.appendName(user.name.title, user.name.first, user.name.last)
        binding.dob.text = user.dob.date
        binding.phoneNumber.text = user.phone
        binding.gender.text = user.gender
        binding.location.text = UiUtil.appendLocation(user.location)

        // Secondary information
        binding.email.text = user.email
        binding.username.text = user.login.username
        binding.password.text = user.login.password
        binding.uuid.text = user.login.uuid
    }

    companion object {
        private const val TAG = "UserDetailsFragment"
    }
}