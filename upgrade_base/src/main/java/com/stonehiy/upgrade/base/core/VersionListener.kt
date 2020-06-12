package com.stonehiy.upgrade.base.core

import com.stonehiy.upgrade.base.Upgrade

interface VersionListener<U : Upgrade> {
    fun success(u: U)

    fun fail(error: String?)
}