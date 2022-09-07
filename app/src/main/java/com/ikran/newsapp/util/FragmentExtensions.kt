package com.ikran.newsapp.util // ktlint-disable filename

import androidx.fragment.app.FragmentActivity

inline fun <reified T> FragmentActivity.getTopFragment(): T? =
    supportFragmentManager.fragments.firstOrNull()?.let { it as? T }
