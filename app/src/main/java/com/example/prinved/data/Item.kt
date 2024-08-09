package com.example.prinved.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val vente_espece: Int,
    val vente_om: Int,
    val depot: Int,
    val retrait: Int,
    val stockage: Int,
    val destockage: Int,
    val depense : Int,
    val orange_money: Int,
    val caisse: Int
)
