package com.example.clubhouse.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.clubhouse.R
import com.example.clubhouse.fragments.ContactDetailsFragment
import com.example.clubhouse.fragments.ContactListFragment
import com.example.clubhouse.interfaces.ContactCardClickListener

class MainActivity : AppCompatActivity(), ContactCardClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.mainActivityRootLayout, ContactListFragment())
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCardClick(id: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainActivityRootLayout, ContactDetailsFragment.newInstance(id))
            .addToBackStack(null)
            .commit()
    }
}