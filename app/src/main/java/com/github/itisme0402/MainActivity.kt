package com.github.itisme0402

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.itisme0402.content.ContentFragment
import com.github.itisme0402.content.PostListFragment
import com.github.itisme0402.content.UserListFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(this, InjectAllStuffViewModelFactory)
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
            val fragmentClass = when (tabIndex) {
                MainViewModel.TAB_USERS -> UserListFragment::class.java
                MainViewModel.TAB_POSTS -> PostListFragment::class.java
                else -> ContentFragment::class.java
            }
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    fragmentClass,
                    null
                )
                .commit()
        })
    }
}
