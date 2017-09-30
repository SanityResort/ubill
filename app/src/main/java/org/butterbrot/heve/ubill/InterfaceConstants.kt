package org.butterbrot.heve.ubill

import android.app.Activity
import android.graphics.Color
import android.support.annotation.ColorInt


object InterfaceConstants {

    val COLOR_AMOUNT_NEGATIV: Int = Color.RED
    @ColorInt val COLOR_AMOUNT_POSITIV: Int = 0xFF1bb510.toInt()
    val COLOR_AMOUNT_ZERO: Int = Color.BLACK

    val PARAM_AMOUNT : String = "ubill.param_amount"
    val PARAM_BILL : String = "ubill.param_bill"
    val PARAM_FELLOWS: String = "ubill.param_fellows"
    val PARAM_FELLOW: String = "ubill.param_fellow"
    val PARAM_FELLOWS_IN_SPLITTINGS = "ubill.param_fellows_in_splittings"
    val PARAM_SPLITS: String = "ubill.param_splits"

    val RC_SELECT_FELLOWS: Int = 1
    val RC_CREATE_FELLOW: Int = 2
    val RC_CREATE_ITEM: Int = 3
    val RC_SPLIT : Int = 4

    val RESULT_CANCELED: Int = Activity.RESULT_CANCELED
    val RESULT_SUCCESS: Int = Activity.RESULT_OK
    val RESULT_KEY: String = "result"

}