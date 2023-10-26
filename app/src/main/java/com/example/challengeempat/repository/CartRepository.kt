package com.example.challengeempat.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.challengeempat.database.CartDao
import com.example.challengeempat.database.CartData
import com.example.challengeempat.database.DatabaseCart
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CartRepository(application: Application) {

    private val cartDao: CartDao
    private val executorService: ExecutorService = Executors.newSingleThreadScheduledExecutor()

    init {
        val database = DatabaseCart.getInstance(application)
        cartDao = database.cartDao()
    }

    fun getAllCartItems(): LiveData<List<CartData>> {
        return cartDao.getAllItemCart()
    }

    fun updateItemQuantity(item: CartData, newQuantity: Int) {
        item.quantity = newQuantity
        executorService.execute { cartDao.update(item) }
    }

    fun deleteCartItem(cartItem: CartData) {
        executorService.execute {
            cartDao.delete(cartItem)
        }
    }
    fun addCartToUpdate(cart: CartData) {
        executorService.execute {
            cartDao.addOrUpdateCartItem(cart)
        }
    }

    fun updateItemNote(item: CartData, newNote: String) {
        executorService.execute {
            item.note = newNote
            cartDao.update(item)
        }
    }
    /*   fun insertData(cart: CartData) {
           executorService.execute { cartDao.insert(cart) }
       }*/
    fun deleteAllItems() {
        executorService.execute { cartDao.deleteALlItems() }
    }
    fun calculateTotalPrice(): LiveData<Int> {
        return cartDao.getAllItemCart().map { cartItems ->
            var totalPrice = 0

            for (cartItem in cartItems) {

                totalPrice += cartItem.hargaPerItem * cartItem.quantity
            }

            totalPrice
        }
    }
}