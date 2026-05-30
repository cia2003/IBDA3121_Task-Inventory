package com.mobileapps.taskinventory.view

interface TaskView {
    fun showLoading()
    fun hideLoading()
    fun showData(data: Map<String, Any>)
    fun showMessage(message: String)
}