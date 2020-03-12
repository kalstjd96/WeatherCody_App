package com.example.myapplication.Network

import android.os.AsyncTask
import android.util.Log


class NetworkTask : AsyncTask<Map<String, String>, Int, String>() {


    override fun doInBackground(vararg maps: Map<String, String>): String? { // 내가 전송하고 싶은 파라미터

        // Http 요청 준비 작업


        val http = HttpClient.Builder("POST", "http://192.168.56.1:8080/doc/" + maps[0]["MinSung"])


        // Parameter 를 전송한다.
        http.addAllParameters(maps[0])




        //Http 요청 전송
        val post = http.create()
        post.request()

        // 응답 상태코드 가져오기
        val statusCode = post.httpStatusCode

        // 응답 본문 가져오기

        return post.body
        return post.body
    }

    override fun onPostExecute(s: String) {
        Log.d("JSON_RESULT", s)


    }
}

