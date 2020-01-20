package com.stonehiy.updrade.base.net

import com.stonehiy.updrade.base.Upgrade

interface IVersion<U : Upgrade> {
    fun success(u: U)

    fun fail(error: String?)
}