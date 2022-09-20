package com.example.POI.Interface

import com.example.POI.ModelClasses.Users

interface ClickInterface {
    fun addItem(position:Int)
    fun remmoveItem(position: Int)
    fun onCellClickListener()
    fun quantityChanged(listUsers: List<Users>)
}