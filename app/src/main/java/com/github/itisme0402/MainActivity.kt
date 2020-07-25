package com.github.itisme0402

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(this)
                .get(MainViewModel::class.java)
        setContentView(R.layout.activity_main)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewModel.tabIndex = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
        viewModel.tabIndexLiveData.observe(this, Observer { tabIndex ->
            tabLayout.selectTab(tabLayout.getTabAt(tabIndex))
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    ContentFragment::class.java,
                    null
                )
                .commit()
        })
    }
}
