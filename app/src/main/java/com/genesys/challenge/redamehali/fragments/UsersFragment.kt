package com.genesys.challenge.redamehali.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.genesys.challenge.redamehali.R
import com.genesys.challenge.redamehali.adapters.RandomUsersAdapter
import com.genesys.challenge.redamehali.databinding.UsersFragmentBinding
import com.genesys.challenge.redamehali.interfaces.UserClickListener
import com.genesys.challenge.redamehali.Result
import com.genesys.challenge.redamehali.activities.MainActivity
import com.genesys.challenge.redamehali.commons.Constant
import com.genesys.challenge.redamehali.interfaces.FetchListener
import com.genesys.challenge.redamehali.models.Users
import com.genesys.challenge.redamehali.viewmodels.RandomUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * [UsersFragment] is the user interface of users fetched from the RandomUser API.
 * Fragment also contains filtering chips by gender (Male, Female).
 */
@AndroidEntryPoint
class UsersFragment : Fragment(), FetchListener {

    private var _binding: UsersFragmentBinding? = null
    private val binding get() = _binding!!

    private var randomUsersAdapter : RandomUsersAdapter? = null
    private val randomUsersViewModel : RandomUserViewModel by viewModels()

    private lateinit var mSeed: String
    private var mFetchCount: Int = 0
    private lateinit var users: Users

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = UsersFragmentBinding.inflate(inflater, container, false)

        setViews()
        setConsumers()
        setListeners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If no instance state has been saved previously, we execute getUsers() to make an API Request.
        if (savedInstanceState == null) {
            getUsers()
        }
        // Uses savedInstanceState to retrieve previously saved properties like: seed, fetchCount, and all users.
        else {
            mSeed = savedInstanceState.getString(Constant.SEED_STATE, Constant.SEED_DEFAULT_VALUE)
            mFetchCount = savedInstanceState.getInt(Constant.FETCH_COUNT_STATE, Constant.RESULTS_FETCH_DEFAULT_COUNT)
            users = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Get parcelable using proper support function for version 33 and above
                savedInstanceState.getParcelable(Constant.USERS_BUNDLE_KEY, Users::class.java)!!
            } else {
                // Get parcelable using proper support function for below version 33
                savedInstanceState.getParcelable(Constant.USERS_BUNDLE_KEY)!!
            }

            Log.i(TAG, "onViewCreated: users retrieved from saved state")
            randomUsersAdapter!!.submitList(users.userList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Write and save instance state to avoid reloading from the API on configuration change
        outState.putString(Constant.SEED_STATE, mSeed)
        outState.putInt(Constant.FETCH_COUNT_STATE, mFetchCount)
        outState.putParcelable(Constant.USERS_BUNDLE_KEY, users)
        Log.i(TAG, "onSaveInstanceState: users saved? ${!outState.isEmpty}")
    }

    /**
     * Sets consumers to collects emitted data from producers
     * Collects emission for users and countriesAndUsers.
     */
    private fun setConsumers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                randomUsersViewModel.users.collect { usersResult ->
                    if (usersResult is Result.Success) {
                        users = usersResult.data
                        if (users.userList.isEmpty() || randomUsersAdapter == null) return@collect
                        randomUsersAdapter!!.submitList(users.userList)
                        Log.i(TAG, "SUCCESS")
                    } else if (usersResult is Result.Error) {
                        Log.e(TAG, "Exception ${usersResult.exception} occurred")
                        Toast.makeText(requireContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                randomUsersViewModel.countriesAndUsers.collect { countriesAndUsers ->
                    if (countriesAndUsers is Result.Success) {
                        StatsDialogFragment(countriesAndUsers.data).show(childFragmentManager, StatsDialogFragment.TAG)
                    } else if (countriesAndUsers is Result.Error) {
                        Log.e(TAG, "Exception ${countriesAndUsers.exception} occurred")
                        Toast.makeText(requireContext(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * Sets all views of [UsersFragment]
     */
    private fun setViews() {
        // Passes fragment instance with FetchListener to its respective activity
        (requireActivity() as MainActivity).setFetchListener(this)
        binding.usersRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        // Instantiate RandomUserAdapter
        randomUsersAdapter = RandomUsersAdapter(object : UserClickListener {
            override fun onItemClick(view: View, bundle: Bundle, position: Int) {
                // Navigate from UsersFragment to UserDetailsFragment
                findNavController().navigate(R.id.action_UsersFragment_to_UserDetailsFragment, bundle)
            }
        })
        binding.usersRecyclerView.adapter = randomUsersAdapter
    }

    /**
     * Sets group chips listeners (Male Gender, and Female Gender) and
     * reacts to user input actions.
     */
    private fun setListeners() {
        binding.maleChip.setOnCheckedChangeListener { _, isChecked ->
            // if both filters are checked, gets both gender users
            if (binding.femaleChip.isChecked && isChecked) {
                getUsers()
                return@setOnCheckedChangeListener
            }

            // Avoids un-checking both filters
            if (!binding.femaleChip.isChecked && !isChecked) {
                Toast.makeText(requireContext(), resources.getString(R.string.one_gender_filter_needs_to_be_checked), Toast.LENGTH_SHORT).show()
                binding.maleChip.isChecked = true
                return@setOnCheckedChangeListener
            }

            randomUsersViewModel.getUsersByGender(mFetchCount, "female")
        }

        binding.femaleChip.setOnCheckedChangeListener { _, isChecked ->
            // if both filters are checked, gets both gender users
            if (binding.maleChip.isChecked && isChecked) {
                getUsers()
                return@setOnCheckedChangeListener
            }

            // Avoids un-checking both filters
            if (!binding.maleChip.isChecked && !isChecked) {
                Toast.makeText(requireContext(), resources.getString(R.string.one_gender_filter_needs_to_be_checked), Toast.LENGTH_SHORT).show()
                binding.femaleChip.isChecked = true
                return@setOnCheckedChangeListener
            }

            randomUsersViewModel.getUsersByGender(mFetchCount, "male")
        }
    }

    /**
     * Sets seed and fetchCount global variables and
     * get users from RandomUser API using MVVM architecture
     */
    private fun getUsers() {
        mSeed = (requireActivity() as MainActivity).mSeed
        mFetchCount = (requireActivity() as MainActivity).mResultFetchCount
        randomUsersViewModel.getUsers(mSeed, mFetchCount)
    }

    companion object {
        private const val TAG = "UsersFragment"
    }

    override fun onFetchResult() {
        getUsers()
    }

    /**
     * Get countries and users per country using MVVM architecture
     */
    override fun getCountries() {
        randomUsersViewModel.getCountriesAndUsersPerCountry(users)
    }
}