package com.stonehiy.upgrade.base.net

import com.stonehiy.upgrade.base.Upgrade

interface IVersion<U : Upgrade> {
    fun success(u: U)

    fun fail(error: String?)
}