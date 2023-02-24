package com.example.pitkiot.data.enums

enum class Team(val customName: String) {
    TEAM_A("red"),
    TEAM_B("green"),
    NONE("No Team");

    companion object {
        fun fromCustomName(customName: String): Team? {
            return values().find { it.customName == customName }
        }
    }
}