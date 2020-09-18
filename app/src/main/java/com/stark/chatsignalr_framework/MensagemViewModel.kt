package com.stark.chatsignalr_framework


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MensagemViewModel() : ViewModel() {
    val mensagem : MutableLiveData<String> = MutableLiveData()

    fun EnviarMensagem(m: String){
        mensagem.postValue(m)
    }

    fun ReceberMensagem(m: String){
        mensagem.postValue(m)
    }





}