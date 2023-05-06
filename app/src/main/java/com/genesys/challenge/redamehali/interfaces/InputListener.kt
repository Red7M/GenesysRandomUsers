package com.genesys.challenge.redamehali.interfaces

import com.genesys.challenge.redamehali.commons.InputTypeEnum

/**
 * Interface listens for input set based on input type
 */
interface InputListener {
  fun onInputSet(value: String, inputType: InputTypeEnum)
}