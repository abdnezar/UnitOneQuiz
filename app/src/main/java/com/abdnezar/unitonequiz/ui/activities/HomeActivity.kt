package com.abdnezar.unitonequiz.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.abdnezar.unitonequiz.R
import com.abdnezar.unitonequiz.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    val t = this.javaClass.simpleName
    private lateinit var v: ActivityHomeBinding
    private lateinit var customAB: View
    private lateinit var navController: NavController
    private lateinit var listener: NavController.OnDestinationChangedListener
    private lateinit var userAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        v = ActivityHomeBinding.inflate(layoutInflater)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_gray)
        initCustomActionBar()
        setContentView(v.root)

        v.bottomNavigationView.selectedItemId = R.id.miHome

        userAuth = FirebaseAuth.getInstance()

        navController = findNavController(R.id.navHost)
        v.bottomNavigationView.setupWithNavController(navController)
        listener = NavController.OnDestinationChangedListener { _, navDestination, _ ->
            when (navDestination.id) {
                R.id.homeFragment -> {
                    customAB.findViewById<ImageView>(R.id.ivClose).visibility = View.GONE
                    if (userAuth.currentUser != null)
                        customAB.findViewById<ImageView>(R.id.ivUserAvatar).visibility = View.VISIBLE
                }
                R.id.loginFragment -> {
                    customAB.findViewById<ImageView>(R.id.ivClose).visibility = View.VISIBLE
                    customAB.findViewById<ImageView>(R.id.ivUserAvatar).visibility = View.GONE
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (userAuth.currentUser == null) {
            customAB.findViewById<ImageView>(R.id.ivUserAvatar).visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(listener)

        v.fab.setOnClickListener {
            navController.navigate(R.id.homeFragment)
        }

        customAB.findViewById<ImageView>(R.id.ivClose).setOnClickListener {
            navController.popBackStack()
        }
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(listener)
    }

    private fun initCustomActionBar() {
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setBackgroundDrawable(resources.getDrawable(R.drawable.toolbar_bg, applicationContext.theme))
        supportActionBar!!.setCustomView(R.layout.custom_action_bar)
        customAB = supportActionBar!!.customView
    }
}