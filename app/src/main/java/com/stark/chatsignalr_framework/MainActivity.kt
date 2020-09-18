package com.stark.chatsignalr_framework


import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stark.chatsignalr_framework.SignalRService.LocalBinder


class MainActivity : AppCompatActivity() {
    private lateinit var recycle: RecyclerView
    private var mensagem: MutableList<Atendimento.Mensagem> = mutableListOf()

    private var mService: SignalRService? = null
    private var mBound = false
    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className: ComponentName,
            service: IBinder
        ) { // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            val binder = service as LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    private val  mensagemViewModel: MensagemViewModel by lazy{
        ViewModelProvider(this, ViewModelFactory()).get(MensagemViewModel::class.java)

    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = ConversarAdapter(mensagem)
        recycle = findViewById(R.id.recycle)
        recycle.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        recycle.adapter = adapter

        val intent = Intent()
        intent.setClass(this@MainActivity, SignalRService::class.java)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)


        findViewById<Button>(R.id.btnEnviar).setOnClickListener {
            if (mBound) {
                val editText = findViewById<EditText>(R.id.edit_text)
                if (editText != null && editText.text.isNotEmpty()) {
                    val message = editText.text.toString()
                    if(Prefs.getSuporte(applicationContext)){
                     if(message == "Atender"){
                         mService?.entrarEmAtendimento()
                     }else if(message == "Negar"){
//                         mService.negarAtendimento()
                     }
                       mService!!.sendMessage(message)
                    }else {
                        mService!!.sendMessage(message)
                    }
                }
            }

        }

//        val checked  = findViewById<Switch>(R.id.suporte)
//        checked.setOnCheckedChangeListener { _, isChecked ->
//            Prefs.setSuporte(applicationContext,isChecked)
            mService?.iniciarConexao()
//        }


        mensagemViewModel.mensagem.observe(this@MainActivity, Observer {
            mensagem.add(Atendimento.Mensagem().apply {
                this.mensagem = it
                this.enviadoPeloSuporte = false
            })
            recycle.adapter?.notifyDataSetChanged()
        })


    }

    override fun onStop() { // Unbind from the service
//        if (mBound) {
//            unbindService(mConnection)
//            mBound = false
//        }
        super.onStop()
    }


    inner class ConversarAdapter(val conversa: MutableList<Atendimento.Mensagem>) :
        RecyclerView.Adapter<ConversarAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(
                LayoutInflater.from(this@MainActivity).inflate(
                    R.layout.conversa,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int = conversa.count()

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val mensagem = conversa[position]

            holder.mensagem.text = mensagem.mensagem
        }


        inner class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val mensagem = v.findViewById<TextView>(R.id.txt_conversa)
        }


    }

}
