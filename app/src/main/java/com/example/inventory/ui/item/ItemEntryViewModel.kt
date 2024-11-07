/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory.ui.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.inventory.data.Item
import java.text.NumberFormat
import com.example.inventory.data.ItemsRepository

/**
 * ViewModel untuk memvalidasi dan menyimpan item ke dalam database Room.
 * Pada ViewModel ini, data yang dimasukkan oleh pengguna akan divalidasi
 * dan kemudian disimpan ke dalam database menggunakan ItemsRepository.
 * ViewModel ini mengelola status UI dari item yang akan dimasukkan.
 */
class ItemEntryViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {

    // Variabel untuk menyimpan status UI, seperti detail item dan validitas input.
    var itemUiState by mutableStateOf(ItemUiState())
        private set

    /**
     * Fungsi untuk memperbarui status UI ketika detail item berubah.
     * Fungsi ini memvalidasi input setiap kali ada perubahan pada detail item.
     */
    fun updateUiState(itemDetails: ItemDetails) {
        // Mengupdate status UI dengan itemDetails baru dan memvalidasi input.
        itemUiState = ItemUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
    }

    /**
     * Fungsi untuk memvalidasi input dari pengguna. Fungsi ini akan memeriksa
     * apakah nama, harga, dan jumlah item sudah diisi dengan benar.
     * Fungsi ini akan dipanggil saat mencoba menyimpan item ke dalam database.
     */
    private fun validateInput(uiState: ItemDetails = itemUiState.itemDetails): Boolean {
        return with(uiState) {
            // Memastikan nama, harga, dan jumlah tidak kosong.
            name.isNotBlank() && price.isNotBlank() && quantity.isNotBlank()
        }
    }

    /**
     * Fungsi untuk menyimpan item ke dalam database. Fungsi ini akan dipanggil
     * jika validasi input berhasil, dan item akan disimpan menggunakan
     * `ItemsRepository`.
     */
    suspend fun saveItem() {
        // Memeriksa apakah input valid sebelum menyimpan item ke dalam database.
        if (validateInput()) {
            // Mengonversi ItemDetails ke Item dan menyimpannya ke database.
            itemsRepository.insertItem(itemUiState.itemDetails.toItem())
        }
    }
}

/**
 * Representasi dari status UI untuk item. Status ini berisi detail item dan
 * validitas input dari pengguna.
 */
data class ItemUiState(
    val itemDetails: ItemDetails = ItemDetails(), // Menyimpan detail item
    val isEntryValid: Boolean = false // Menyimpan status validitas input
)

/**
 * Representasi detail item yang akan dimasukkan oleh pengguna, seperti ID,
 * nama, harga, dan jumlah. Ini adalah data yang akan diinputkan oleh pengguna.
 */
data class ItemDetails(
    val id: Int = 0, // ID item (akan di-generate otomatis oleh Room jika tidak diisi)
    val name: String = "", // Nama item
    val price: String = "", // Harga item dalam bentuk String, akan dikonversi menjadi Double
    val quantity: String = "", // Jumlah item dalam bentuk String, akan dikonversi menjadi Int
)

/**
 * Fungsi ekstensi untuk mengonversi objek ItemDetails menjadi objek Item
 * yang bisa disimpan dalam database Room.
 * Jika harga atau jumlah tidak valid (bukan angka), maka harga akan diset ke 0.0
 * dan jumlah akan diset ke 0.
 */
fun ItemDetails.toItem(): Item = Item(
    id = id, // ID item, yang mungkin akan di-generate otomatis oleh Room
    name = name, // Nama item
    price = price.toDoubleOrNull() ?: 0.0, // Mengonversi harga ke Double, jika gagal set ke 0.0
    quantity = quantity.toIntOrNull() ?: 0 // Mengonversi jumlah ke Integer, jika gagal set ke 0
)

/**
 * Fungsi ekstensi untuk format harga item ke dalam format mata uang yang lebih mudah dibaca.
 * Misalnya, 1000.0 akan diformat menjadi "$1,000.00".
 */
fun Item.formatedPrice(): String {
    // Menggunakan NumberFormat untuk memformat harga menjadi format mata uang.
    return NumberFormat.getCurrencyInstance().format(price)
}

/**
 * Fungsi ekstensi untuk mengonversi objek Item menjadi ItemUiState.
 * Ini berguna untuk menampilkan data item di UI dan menunjukkan status validitas input.
 */
fun Item.toItemUiState(isEntryValid: Boolean = false): ItemUiState = ItemUiState(
    itemDetails = this.toItemDetails(), // Mengonversi Item ke ItemDetails
    isEntryValid = isEntryValid // Status validitas input
)

/**
 * Fungsi ekstensi untuk mengonversi objek Item menjadi ItemDetails.
 * Ini digunakan untuk memudahkan tampilan data item di UI.
 */
fun Item.toItemDetails(): ItemDetails = ItemDetails(
    id = id, // ID item
    name = name, // Nama item
    price = price.toString(), // Harga item dalam bentuk String
    quantity = quantity.toString() // Jumlah item dalam bentuk String
)
