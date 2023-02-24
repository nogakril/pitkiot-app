package com.example.pitkiot.data

import com.example.pitkiot.data.enums.Role
import com.example.pitkiot.data.enums.Team

data class GameUser(val role: Role?, private val team: Team?) {
    fun isAdmin(): Boolean = role == Role.ADMIN
    fun getTeam(): Team? = team
}