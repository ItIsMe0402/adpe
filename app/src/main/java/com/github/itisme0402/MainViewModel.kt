package com.github.itisme0402

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _tabIndexLiveData = MutableLiveData<Int>().apply { value = 0 }
    val tabIndexLiveData: LiveData<Int> = _tabIndexLiveData
    var tabIndex: Int
        get() = _tabIndexLiveData.value!!
        set(value) {
            _tabIndexLiveData.value = value
        }
}
