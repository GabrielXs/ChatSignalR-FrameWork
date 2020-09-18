package com.stark.chatsignalr_framework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ViewModelFactory() : ViewModelProvider.Factory {

    companion object {
        val hashMapViewModel = HashMap<String, ViewModel>()
        fun addViewModel(key: String, viewModel: ViewModel) {
            hashMapViewModel.put(key, viewModel)
        }

        fun getViewModel(key: String): ViewModel? {
            return hashMapViewModel[key]
        }
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MensagemViewModel::class.java)) {
            val key = "MensagemViewModel"
            if (hashMapViewModel.containsKey(key)) {
                return getViewModel(key) as T
            } else {
                addViewModel(key, MensagemViewModel())
                return hashMapViewModel[key] as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}