package com.stark.chatsignalr_framework

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import microsoft.aspnet.signalr.client.ConnectionState
import microsoft.aspnet.signalr.client.Credentials
import microsoft.aspnet.signalr.client.Platform
import microsoft.aspnet.signalr.client.http.java.AndroidPlatformComponent
import microsoft.aspnet.signalr.client.hubs.HubConnection
import microsoft.aspnet.signalr.client.hubs.HubProxy
import microsoft.aspnet.signalr.client.transport.ClientTransport
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport


class SignalRService : Service() {
    private var mHubConnection: HubConnection? = null
    private var mHubProxy: HubProxy? = null
    private var mHandler // to display Toast message
            : Handler? = null
    private val mBinder: IBinder = LocalBinder() // Binder given to clients
    private val  mensagemViewModel: MensagemViewModel by lazy{
        ViewModelFactory.getViewModel("MensagemViewModel") as MensagemViewModel
    }
    private lateinit var clientAtendimento : Atendimento
    override fun onCreate() {
        super.onCreate()
        mHandler = Handler(Looper.getMainLooper())


    }
    override fun onDestroy() {
        mHubConnection?.stop()
        super.onDestroy()
    }


    override fun onBind(intent: Intent?): IBinder? {
        startSignalR()
        return mBinder
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val result = super.onStartCommand(intent, flags, startId)
        startSignalR()
        return result

    }

    private fun startSignalR() {
        Platform.loadPlatformComponent(AndroidPlatformComponent())

        val credentials = Credentials { request -> request.addHeader("User-Name", "BNK") }

        val serverUrl = "http://192.168.1.44/Aniel.Connect/"
        mHubConnection = HubConnection(serverUrl)
        mHubConnection!!.credentials = credentials

        mHubProxy = mHubConnection!!.createHubProxy(SERVER_HUB_CHAT)

        iniciarConexao()


        mHubProxy!!.on("ClienteAguardandoAtendimento",{msg, it ->
//            it.mensagens.add(Atendimento.Mensagem().apply {
//                this.mensagem = msg
//            })
            mensagemViewModel.ReceberMensagem(msg)
        },String::class.java,Atendimento::class.java)


    }


    fun sendMessage(message: String?) {
        mHubProxy!!.invoke(SERVER_METHOD_SEND,"10001", message)
    }
    fun iniciarConexao(){
        val clientTransport: ClientTransport = ServerSentEventsTransport(mHubConnection!!.logger)
        val conectar = {
            val signalRFuture = mHubConnection!!.start(clientTransport)

            try {
                signalRFuture?.get()
            } catch (e: InterruptedException) {
                e.printStackTrace()

            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
        if(mHubConnection?.state == ConnectionState.Connected)
        {
            mHubConnection?.stop()
            conectar()
        }else if (mHubConnection?.state == ConnectionState.Disconnected){
            conectar()
        }

            mHubProxy?.invoke(SERVER_METHOD_INICIAR_CONEXAO_SUPERVISOR, Supervisor().apply {
                idSuporte = 1
                this.idConexaoChat = mHubConnection?.connectionId
                this.status = Supervisor.StatusAtendimento.DISPONIVEL
            })


    }

    fun entrarEmAtendimento() {
        clientAtendimento.suporte = Supervisor().apply {
            idSuporte = 1
            this.idConexaoChat = mHubConnection?.connectionId
            this.status = Supervisor.StatusAtendimento.DISPONIVEL
        }
        mHubProxy?.invoke(SUPERVISR_ENTRAR_EM_ATENDIMENTO, clientAtendimento)
    }


    inner class LocalBinder : Binder() {
        fun  getService() : SignalRService {
            return this@SignalRService
        }
    }

    companion object{
        const val SERVER_METHOD_SEND = "Send"
        const val CLIENT_METHOD_BROADAST_MESSAGE = "envioMensagem"
        const val SERVER_HUB_CHAT = "ChatHub"
        const val  SERVER_METHOD_INICIAR_CONEXAO = "IniciarAtendimento"
        const val SERVER_METHOD_INICIAR_CONEXAO_SUPERVISOR = "iniciarConexaoSuporte"
        const val SUPERVISOR_METHOD_CLIENT_AGUARDANDO_ATENDIMENTO = "ClienteAguardandoAtendimento"
        const val SUPERVISOR_METHOD_CLIENT_ENTRANDO_EM_ATENDIMENTO = "sendFromTecnico"
        const val SUPERVISR_ENTRAR_EM_ATENDIMENTO = "AceitarAtendimento"
    }
}


