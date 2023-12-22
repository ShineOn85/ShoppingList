package com.example.shoppinglist.data
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.domain.ShopListRepository
import java.lang.RuntimeException
import kotlin.random.Random

object ShopListRepositoryImpl : ShopListRepository {
    private val shopList = sortedSetOf<ShopItem>({ o1, o2 -> o1.id - o2.id })
    private val liveDataShopList = MutableLiveData<List<ShopItem>>()
    private var autoIncrementId = 0

    init {
        for (i in 0 until 1000){
            val shopItem = ShopItem("Name $i", i, Random.nextBoolean())
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
        return liveDataShopList
    }
    private fun updateList(){
        liveDataShopList.value = shopList.toList()
    }
}