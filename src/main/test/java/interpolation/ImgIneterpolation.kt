package interpolation

fun main() {



}

fun bilinearInterpolation(
    bottomLeft: Double,
    topLeft: Double,
    bottomRight: Double,
    topRight: Double,
    xMin: Double,
    xMax: Double,
    zMin: Double,
    zMax: Double,
    x: Double,
    z: Double
): Double {
    val width = xMax - xMin
    val height = zMax - zMin
    val xDistanceToMaxValue = xMax - x
    val zDistanceToMaxValue = zMax - z
    val xDistanceToMinValue = x - xMin
    val zDistaceToMinValue = z - zMin
    return 1.0 / (width * height) *
            (bottomLeft * xDistanceToMaxValue * zDistanceToMaxValue + bottomRight * xDistanceToMinValue * zDistanceToMaxValue + topLeft * xDistanceToMaxValue * zDistaceToMinValue + topRight * xDistanceToMinValue * zDistaceToMinValue)
}