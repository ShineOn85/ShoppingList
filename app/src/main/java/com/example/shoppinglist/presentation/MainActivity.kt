package com.example.shoppinglist.presentation

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.ShopListApp
import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.presentation.ShopListAdapter.Companion.MAX_PULL_SIZE
import com.example.shoppinglist.presentation.ShopListAdapter.Companion.SHOP_ITEM_DISABLED
import com.example.shoppinglist.presentation.ShopListAdapter.Companion.SHOP_ITEM_ENABLED
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val component by lazy {
        (application as ShopListApp).component
    }

    private lateinit var viewModel: MainViewModel

    @Inject
    lateinit var adapter: ShopListAdapter

    private var shopItemContainer: FragmentContainerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
        shopItemContainer = findViewById(R.id.shop_item_container)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        viewModel.shopList.observe(this) {
            adapter.submitList(it)

        }
        val buttonAddShopItem = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        buttonAddShopItem.setOnClickListener {
            if (isOnePaneMode()) {
                val intent = ShopItemActivity.newIntentAddItem(this)
                startActivity(intent)
            } else launchFragment(ShopItemFragment.newInstanceAddItem())
        }

        thread {
            val cursor = contentResolver.query(
                Uri.parse("content://com.test.shoppinglist/shop_items"),
                null, null, null, null
            )
            while (cursor?.moveToNext() == true) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val enabled = cursor.getInt(cursor.getColumnIndexOrThrow("enabled")) > 0
                val count = cursor.getInt(cursor.getColumnIndexOrThrow("count"))
                val shopItem = ShopItem(name = name, count = count, enabled = enabled, id = id)
                Log.d("MainActivity", shopItem.toString())

            }
            cursor?.close()
        }

    }

    private fun setupRecyclerView() {
        val rvShopList = findViewById<RecyclerView>(R.id.rv_shop_list)
        adapter = ShopListAdapter()
        rvShopList.adapter = adapter
        rvShopList.recycledViewPool.setMaxRecycledViews(SHOP_ITEM_ENABLED, MAX_PULL_SIZE)
        rvShopList.recycledViewPool.setMaxRecycledViews(SHOP_ITEM_DISABLED, MAX_PULL_SIZE)
        setupLongClickListener()
        setupClickListener()
        setupSwipeListener(rvShopList)
    }

    private fun setupSwipeListener(rvShopList: RecyclerView) {
        val itemTouchHelper =
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    TODO("Not yet implemented")
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val shopItem = adapter.currentList[viewHolder.adapterPosition]
                    viewModel.deleteShopItem(shopItem)
                }
            })
        itemTouchHelper.attachToRecyclerView(rvShopList)
    }

    private fun setupClickListener() {
        adapter.onShopItemClickListener = {
            if (isOnePaneMode()) {
                val intent = ShopItemActivity.newIntentEditItem(this, it.id)
                startActivity(intent)
            } else {
                launchFragment(ShopItemFragment.newInstanceEditItem(it.id))
            }
        }
    }

    private fun isOnePaneMode(): Boolean {
        return shopItemContainer == null
    }

    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.shop_item_container, fragment)
            .commit()
    }

    private fun setupLongClickListener() {
        adapter.onShopItemLongClickListener = {
            viewModel.changeEnableState(it)
        }
    }

    override fun onEditingFinished() {
        supportFragmentManager.popBackStack()
    }
}