package com.example.shoppinglist.data
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.domain.ShopListRepository
import java.lang.RuntimeException

object ShopListRepositoryImpl : ShopListRepository {
    private val shopList = mutableListOf<ShopItem>()
    private val liveDataShopList = MutableLiveData<List<ShopItem>>()
    private var autoIncrementId = 0

    init {
        for (i in 0 until 10){
            val shopItem = ShopItem("Name $i", i, true)
            addShopItem(shopItem)
        }
    }
    override fun addShopItem(shopItem: ShopItem) {
        if (shopItem.id == ShopItem.UNDEFINED_ID){
            shopItem.id = autoIncrementId++
        }
        shopList.add(shopItem)
        updateList()
    }

    override fun deleteShopItem(shopItem: ShopItem) {
        shopList.remove(shopItem)
        updateList()
    }

    override fun editShopItem(shopItem: ShopItem) {
        val oldValue = getShopItem(shopItem.id)
        shopList.remove(oldValue)
        addShopItem(shopItem)
    }

    override fun getShopItem(shopItemId: Int): ShopItem {
        return shopList.find { it.id == shopItemId} ?: throw RuntimeException()
    }

    override fun getShopList(): LiveData<List<ShopItem>>  {
        liveDataShopList.value = shopList
        return liveDataShopList
    }
    private fun updateList(){
        liveDataShopList.value = shopList.toList()
    }
}