package com.sentrics.alarmapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

class MainViewModel(coroutineContext: CoroutineContext = Dispatchers.IO) : ViewModel(),
    CoroutineScope {

    private val job = Job()
    override val coroutineContext = coroutineContext + job

    val textLD = MutableLiveData<String>()

    fun fetchDataFromApi() {
        launch {
            delay(2000L)
            val fetch = fetchPost()
            textLD.postValue(fetch)
        }
    }
}

suspend fun fetchPost(): String {
    val result = runCatching {
        executeGetPost()
    }

    return if (result.isSuccess) {
        result.getOrNull()!!.toString()
    } else
        result.exceptionOrNull()!!.toString()
}

suspend fun executeGetPost(): Post {
    val retrofit = WebServiceFactory.createWebServiceApi()
    return retrofit.getPost("1").let { response ->
        when {
            response.isSuccessful -> response.body()?.let {
                it
            }
            else -> throw Exception(response.errorBody()?.string())
        } ?: throw RuntimeException("Error executing API call")
    }
}

//Move to service layer
object WebServiceFactory {

    fun createWebServiceApi(): WebService {
        return createRetrofitCoroutine().create(WebService::class.java)
    }

    private fun createRetrofitCoroutine() =
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}