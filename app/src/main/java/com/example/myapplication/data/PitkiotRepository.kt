package com.example.myapplication.data

class PitkiotRepository {

    fun getNextWord(): String {
        val words = arrayOf("apple", "banana", "cherry", "date", "elderberry", "fig", "grape")
        return words.random()
    }
}