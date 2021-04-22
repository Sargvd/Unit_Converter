package converter

import java.lang.Exception

/*enum class metersTable(val ratio: Double) {
    METERS(1.0),
    KILOMETERS(1000.0),
    CENTIMETERS(0.01),
    MILLIMETERS(0.001),
    MILES(1609.35),
    YARDS(0.9144),
    FEET(0.3048),
    INCHES(0.0254),
    NULL(0.0);

    companion object {
        fun to(enum: metersTable, value: Double): Double = value * enum.ratio
        fun from(enum: metersTable, value: Double): Double = value / enum.ratio
    }
}*/

enum class TempTable(val tag: String) {
    CELSIUS("degrees celsius"),
    FAHRENHEIT("degrees fahrenheit"),
    KELVIN("kelvins"),
    NULL("");

    companion object {
        fun convert(fromS: String, toS: String, value: Double): Double {
            var from = NULL
            var to = NULL
            for (e in values()) {
                if (e.tag == fromS.toLowerCase()) from = e
            }
            for (e in values()) {
                if (e.tag == toS.toLowerCase()) to = e
            }
            if (from == NULL || to == NULL) throw Exception("Can't convert from $fromS to $toS.")


            return when (from) {
                CELSIUS -> when(to) {
                    CELSIUS -> value
                    FAHRENHEIT -> value * (9.0/5.0) + 32
                    KELVIN -> value + 273.15
                    NULL -> 0.0
                }
                FAHRENHEIT -> when(to) {
                    FAHRENHEIT -> value
                    CELSIUS -> (value - 32) * (5.0/9.0)
                    KELVIN -> (value + 459.67) * (5.0/9.0)
                    NULL -> 0.0
                }
                KELVIN -> when(to) {
                    KELVIN -> value
                    CELSIUS -> value - 273.15
                    FAHRENHEIT -> value * (9.0/5.0) - 459.67
                    NULL -> 0.0
                }
                NULL -> 0.0
            }
        }
    }
}

fun convertToFromMeters(num: Double, unit: String, to: Boolean): Double {
    val conversionRatio: Double = when(unit) {
        "meters" -> 1.0
        "kilometers" ->  1000.0
        "centimeters" -> 0.01
        "millimeters" -> 0.001
        "miles" -> 1609.35
        "yards" -> 0.9144
        "feet" -> 0.3048
        "inches" -> 0.0254
        else -> 0.0
    }
    return if (to) num*conversionRatio else num/conversionRatio
}

fun convertToFromGrams(num: Double, unit: String, to: Boolean): Double {
    val conversionRatio: Double = when (unit) {
        "grams" -> 1.0
        "kilograms" -> 1000.0
        "milligrams" -> 0.001
        "pounds" -> 453.592
        "ounces" -> 28.3495
        else -> 0.0
    }
    return if (to) num*conversionRatio else num/conversionRatio
}

fun normaliseUnit(unit: String, isSingular: Boolean): String {
    return when(unit.toLowerCase()) {
        "m", "meter", "meters" -> if (isSingular) "meter" else "meters"
        "km", "kilometer", "kilometers" -> if (isSingular) "kilometer" else "kilometers"
        "cm", "centimeter", "centimeters" -> if (isSingular) "centimeter" else "centimeters"
        "mm", "millimeter", "millimeters" -> if (isSingular) "millimeter" else "millimeters"
        "mi", "mile", "miles" -> if (isSingular) "mile" else "miles"
        "yd", "yard", "yards" -> if (isSingular) "yard" else "yards"
        "ft", "foot", "feet" -> if (isSingular) "foot" else "feet"
        "in", "inch", "inches" -> if (isSingular) "inch" else "inches"
        "g", "gram", "grams" -> if (isSingular) "gram" else "grams"
        "kg", "kilogram", "kilograms" -> if (isSingular) "kilogram" else "kilograms"
        "mg", "milligram", "milligrams" -> if (isSingular) "milligram" else "milligrams"
        "lb", "pound", "pounds" -> if (isSingular) "pound" else "pounds"
        "oz", "ounce", "ounces" -> if (isSingular) "ounce" else "ounces"
        "degree celsius", "degrees celsius", "celsius", "dc", "c" -> if (isSingular) "degree Celsius" else "degrees Celsius"
        "degree fahrenheit", "degrees fahrenheit", "fahrenheit", "df", "f" -> if (isSingular) "degree Fahrenheit" else "degrees Fahrenheit"
        "kelvin", "kelvins", "k" -> if (isSingular) "kelvin" else "kelvins"
        else -> "???"
    }
}

fun conversionType(unit: String): String {
    if (unit in listOf("meters","kilometers","centimeters","millimeters","miles","yards","feet","inches")) return "length"
    if (unit in listOf("grams", "kilograms","milligrams","pounds","ounces")) return "weight"
    if (unit in listOf("degrees Celsius", "degrees Fahrenheit", "kelvins")) return "temperature"
    return "unknown"
}

fun convert(numStr: String, unitFrom: String, unitTo: String): String {
    val plUnitFrom = normaliseUnit(unitFrom, false)
    val plUnitTo = normaliseUnit(unitTo, false)

    if (plUnitFrom == "???" || plUnitTo == "???")
        return "Conversion from $plUnitFrom to $plUnitTo is impossible"
    else if (conversionType(plUnitFrom) != conversionType(plUnitTo))
        return "Conversion from $plUnitFrom to $plUnitTo is impossible"

    val num = numStr.toDouble()
    val normFromUnit = normaliseUnit(unitFrom, num == 1.0)
    val normToUnit: String
    when (conversionType(plUnitFrom)) {
        "length" -> {
            if (num < 0.0 ) return "Length shouldn't be negative"
            val inMeters = convertToFromMeters(num, plUnitFrom, true)
            val inTarget = convertToFromMeters(inMeters, plUnitTo, false)
            normToUnit = normaliseUnit(unitTo, inTarget == 1.0)
            return "$num $normFromUnit is $inTarget $normToUnit"
        }
        "weight" -> {
            if (num < 0.0 ) return "Weight shouldn't be negative"
            val inGrams = convertToFromGrams(num, plUnitFrom, true)
            val inTarget = convertToFromGrams(inGrams, plUnitTo, false)
            normToUnit = normaliseUnit(unitTo, inTarget == 1.0)
            return "$num $normFromUnit is $inTarget $normToUnit"
        }
        "temperature" -> {
            val inTarget = TempTable.convert(plUnitFrom,plUnitTo, num)
            normToUnit = normaliseUnit(unitTo, inTarget == 1.0)
            return "$num $normFromUnit is $inTarget $normToUnit"
        }
    }
    return "Units not recognised"
}

fun main() {
    while (true) {
        print("Enter what you want to convert (or exit): ")
        val input = readLine()!!.split(" ")
        if (input[0] == "exit" ) break
        val num = input[0]
        var unitFrom = ""
        var unitTo = ""
        var foundTo = false
        for (i in 1 until input.size) {
            if (input[i] == "to" || input[i] == "in") {
                foundTo = true
                continue
            }
            if (!foundTo) {
                if (unitFrom == "") unitFrom = input[i]
                else unitFrom += " ${input[i]}"
            } else {
                if (unitTo == "") unitTo = input[i]
                else unitTo += " ${input[i]}"
            }
        }

        println(convert(num,unitFrom,unitTo))
    }
}
