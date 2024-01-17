package com.example.shoppinglist.domain

import androidx.lifecycle.LiveData

class GetShopListUseCase(private val shopListRepository: ShopListRepository) {
    fun getShopList(): LiveData<List<ShopItem>> {
        return shopListRepository.getShopList()
        val nums = intArrayOf(1,2,3,1)
        for (num in 0..nums.size - 3){
            val a = nums.distinct()
        }

    }

}