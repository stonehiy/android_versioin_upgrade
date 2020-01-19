package com.stonehiy.updrade.base.version

import com.stonehiy.updrade.base.Upgrade

interface UpdradeChecker {

    fun getUpdrade(): Upgrade
}