package com.genesys.challenge.redamehali.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.genesys.challenge.redamehali.R
import com.genesys.challenge.redamehali.commons.Constant
import com.genesys.challenge.redamehali.commons.InputTypeEnum
import com.genesys.challenge.redamehali.databinding.ActivityMainBinding
import com.genesys.challenge.redamehali.interfaces.FetchListener
import com.genesys.challenge.redamehali.interfaces.InputListener
import com.genesys.challenge.redamehali.fragments.SetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * [MainActivity] is the only activity of the project. It directly creates
 * [com.genesys.challenge.redamehali.fragments.UsersFragment] without user input action.
 * Through callback and user input click on an item from the recyclerview, the activity will create
 * [com.genesys.challenge.redamehali.fragments.UserDetailsFragment] with user further details.
 *
 * Also, [MainActivity] has menu item with 4 different options:
 * - Set Seed
 * - Set fetch results count
 * - Fetch users
 * - Users nationalities and countries count
 *
 * Note: items will be discussed further in their respective functions
 *
 * There are "male/female" filter that filters out users based on their gender.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    var mSeed: String = Constant.SEED_DEFAULT_VALUE
    var mResultFetchCount : Int = Constant.RESULTS_FETCH_DEFAULT_COUNT

    private lateinit var fetchListener: FetchListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        // Navigation host fragment of the activity
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button
        return when (item.itemId) {
            // Set seed opens a dialog where user can input a new seed value
            R.id.set_seed -> {
                showDialog(
                    resources.getString(R.string.action_seed),
                    resources.getString(R.string.set_a_seed),
                    InputTypeEnum.SEED_INPUT)
                true
            }
            R.id.set_result_count -> {
                // Set results counts opens a dialog where user can input a new fetch count value
                showDialog(
                    resources.getString(R.string.action_result_count),
                    resources.getString(R.string.set_results_max_is_two_thousand),
                    InputTypeEnum.FETCH_COUNT_INPUT)
                true
            }
            R.id.fetch_results -> {
                // Fetch results retrieves users using the set seed and results count
                fetchListener.onFetchResult()
                true
            }
            R.id.summary_of_countries_and_users_nationality -> {
                // Call for returning a summary of all countries represented and count of users per nationality
                fetchListener.getCountries()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    /**
     * shows custom Fragment dialog that is customized through title, hint, and inputType.
     * @param title The title of the dialog fragment
     * @param hint The hint for the dialog fragment's edit text
     * @param inputType The type of input from the [com.genesys.challenge.redamehali.commons.InputTypeEnum]
     */
    private fun showDialog(title: String, hint: String, inputType: InputTypeEnum) {
        val dialog = SetDialogFragment(title, hint, object : InputListener {
            override fun onInputSet(value: String, inputType: InputTypeEnum) {
                if (inputType == InputTypeEnum.SEED_INPUT) {
                    currentSeed(value)
                } else if (inputType == InputTypeEnum.FETCH_COUNT_INPUT) {
                    val count = value.toInt()
                    currentFetchCount(count)
                }
            }
        }, inputType)
        dialog.show(supportFragmentManager, "DefaultDialog")
    }

    /**
     * Sets seed into the mSeed global variable
     * @param seed The value passed to the global variable
     */
    fun currentSeed(seed: String) {
        mSeed = seed
        Toast.makeText(this, String.format(getString(R.string.seed_is_set_now_to_xyz), mSeed), Toast.LENGTH_SHORT).show()
    }

    /**
     * Sets results fetch count into the mResultFetchCount global variable
     * @param count The value passed to global variable
     */
    fun currentFetchCount(count: Int)  {
        mResultFetchCount = count
        Toast.makeText(this, String.format(getString(R.string.fetch_count_is_set_now_to_xyz), mResultFetchCount), Toast.LENGTH_SHORT).show()
    }

    /**
     * Sets the instance of [com.genesys.challenge.redamehali.interfaces.FetchListener] into the global variable
     * @param fetchListener The value passed to global variable
     */
    fun setFetchListener(fetchListener: FetchListener) {
        this.fetchListener = fetchListener
    }
}