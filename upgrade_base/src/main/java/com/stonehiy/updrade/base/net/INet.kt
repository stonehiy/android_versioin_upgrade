package com.stonehiy.updrade.base.net

import com.stonehiy.updrade.base.Upgrade

interface INet<U : Upgrade> {
    fun success(u: U)

    fun fail(error: String?)
}