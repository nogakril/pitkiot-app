package com.example.pitkiot.data

class PitkiotRepository {

    fun getNextWord(): String {
        val words = arrayOf("apple", "banana", "cherry", "date", "elderberry", "fig", "grape")
        return words.random()
    }
}