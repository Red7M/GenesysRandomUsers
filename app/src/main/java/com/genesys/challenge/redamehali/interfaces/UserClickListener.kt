package com.genesys.challenge.redamehali.interfaces

import android.os.Bundle
import android.view.View

/**
 * Interface listens for view item clicks
 */
interface UserClickListener {
    fun onItemClick(view: View, bundle: Bundle, position: Int)
}