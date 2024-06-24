package encryptdecrypt

import java.io.File

fun main(args: Array<String>) {
    val argMap = args.toList().windowed(2, 2).associate { it[0] to it[1] }

    val mode = argMap["-mode"] ?: "enc"
    val data = argMap["-data"] ?: ""
    var key = argMap["-key"] ?.toInt() ?: 0
    val alg = argMap["-alg"] ?: "shift"
    val fileIn = argMap["-in"] ?: ""
    val fileOut = argMap["-out"] ?: ""

    val dataIn = if (data.isNotEmpty() || fileIn.isEmpty()) data else fileIn
    val fromFile = data.isEmpty() && fileIn.isNotEmpty()

    //trim the move if shift and greater than 25
    if (alg == "shift" && key > 25) {
        key %= 26
    }

    //set the move to positive or negative
    val moveCount = when (mode) {
        "enc" -> key
        else -> key * -1
    }

    process(dataIn, fileOut, fromFile, alg, moveCount)
}

fun process(dataIn: String, fileOut: String, fromFile: Boolean, alg: String, moveCount: Int) {

    val data = if (fromFile) File(dataIn).readText() else dataIn
    var result = ""

    for (ch in data) {
       var newChar = when (alg) {
            //shift
            "shift" -> {
                when (ch) {
                    in 'a'..'z' -> (ch.code + moveCount).toChar()
                    in 'A'..'Z' -> (ch.code + moveCount).toChar()
                    else -> ch
                }
            }
            //unicode
            else -> (ch.code + moveCount).toChar()
        }

        if (alg == "shift") {
            //too high, subtract
            if ((ch in 'a'..'z' && newChar > 'z') || (ch in 'A'..'Z' && newChar > 'Z')) newChar -= 26
            //too low, add
            if ((ch in 'a'..'z' && newChar < 'a') || (ch in 'A'..'Z' && newChar < 'A')) newChar += 26
        }
        result += newChar
    }


    //print data either to console or file
    if (fileOut.isNotEmpty()) {
        val file = File(fileOut)
        file.writeText(result)
    } else {
        println(result)
    }
}

