package com.example.inventory.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) // Menentukan bahwa id akan otomatis di-generate oleh Room saat item disimpan ke dalam database.
    val id: Int = 0, // ID unik untuk tiap item, menggunakan auto-generate yang berarti Room akan menentukan ID ini.
    val name: String, // Nama item yang akan disimpan.
    val price: Double, // Harga item yang disimpan dalam bentuk angka desimal.
    val quantity: Int // Jumlah item yang ada dalam database.
)

