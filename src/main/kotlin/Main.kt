import kotlin.math.*

var randSeed = 0
fun randInt(): Int {
    randSeed = (1103515245*randSeed + 12345).mod(2147483648).toInt()
    return randSeed
}

fun randFloat(): Float {
    return randInt().toFloat()/2147483648f
}

fun randMatrixNonUniform(): Matrix3 {
    return EulerAngles(EulerOrder.YXZ, 6.28318f*randFloat(), 6.28318f*randFloat(), 6.28318f*randFloat()).toMatrix()
}

fun testEuler(order: EulerOrder, exception: String) {
    for (i in 1..1000) {
        val M = randMatrixNonUniform()
        val A = M.toEulerAnglesAssumingOrthonormal(order)
        val N = A.toMatrix()
        if ((N - M).norm() > 1e-6f)
            throw Exception(exception)
    }
}

fun main() {
    testEuler(EulerOrder.XYZ, "toEulerAnglesXYZ accuracy test failed")
    testEuler(EulerOrder.YZX, "toEulerAnglesYZX accuracy test failed")
    testEuler(EulerOrder.ZXY, "toEulerAnglesZXY accuracy test failed")
    testEuler(EulerOrder.ZYX, "toEulerAnglesZYX accuracy test failed")
    testEuler(EulerOrder.YXZ, "toEulerAnglesYXZ accuracy test failed")
    testEuler(EulerOrder.XZY, "toEulerAnglesXZY accuracy test failed")


    val ETA = 1.57079632f
    println(Matrix3(0f, 0f, 1f, sin(1f), cos(1f), 0f, -cos(1f), sin(1f), 0f).toEulerAngles(EulerOrder.XYZ))

    //println(Quaternion(1f, 0f, 0f, 0f).align())
    println(Quaternion(1f, 1f, 0f, 0f).angleR()*2f)
}