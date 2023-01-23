import kotlin.math.*

var randSeed = 0
fun randInt(): Int {
    randSeed = (1103515245*randSeed + 12345).mod(2147483648).toInt()
    return randSeed
}

fun randFloat(): Float {
    return randInt().toFloat()/2147483648f
}

fun randGaussian(): Float {
    return sqrt(-2f*ln(1f - randFloat()))*cos(PI.toFloat()*randFloat())
}

fun randRotMatrix(): Matrix3 {
    return EulerAngles(EulerOrder.YXZ, 6.28318f*randFloat(), 6.28318f*randFloat(), 6.28318f*randFloat()).toMatrix()
}

fun randMatrix(): Matrix3 {
    return Matrix3(
        randGaussian(), randGaussian(), randGaussian(),
        randGaussian(), randGaussian(), randGaussian(),
        randGaussian(), randGaussian(), randGaussian()
    )
}

fun randQuaternion(): Quaternion {
    return Quaternion(randGaussian(), randGaussian(), randGaussian(), randGaussian())
}

fun testEulerMatrix(order: EulerOrder, M: Matrix3, exception: String) {
    // We convert to euler angles and back and see if they are reasonably similar
    val N = M.toEulerAngles(order).toMatrix()
    if ((N - M).norm() > 1e-6) {
        println("norm error: " + (N - M).norm().toString())
        throw Exception(exception)
    }
}

fun testEulerConversion(order: EulerOrder, exception: String) {
    for (i in 1..1000) {
        testEulerMatrix(order, randRotMatrix(), exception)
    }
}

fun testMatrixOrthonormalize() {
    for (i in 1..1000) {
        val M = randMatrix()

        val N = M.invTranspose().orthonormalize()
        val O = M.orthonormalize()
        if ((N - O).norm() > 1e-5) {
            println("norm error: " + (N - O).norm().toString())
            throw Exception("Matrix orthonormalization accuracy test failed")
        }
    }
}

fun testQuatMatrixConversion() {
    for (i in 1..1000) {
        val M = randRotMatrix()
        val N = (randGaussian()*M.toQuaternion()).toMatrix()
        if ((N - M).norm() > 1e-6) {
            println("norm error: " + (N - M).norm().toString())
            throw Exception("Quaternion Matrix conversion accuracy test failed")
        }
    }
}

fun testQuaternionArithmetic() {
    for (i in 1..1000) {
        val Q = randQuaternion()
        val A = Q*Q.inv() - Quaternion.ONE
        val B = Q.pow(-1f) - Q.inv()
        val C = Q.pow(1f) - Q
        val D = Q.pow(2f) - Q*Q
        val E = 1f/Q - Q.inv()
        val F = Q/Q - Quaternion.ONE
        if (A.len() > 1e-6) {
            throw Exception("Quaternion inv accuracy test failed")
        }
        if (B.len() > 1e-5 || C.len() > 1e-5 || D.len() > 1e-5) {
            throw Exception("Quaternion pow accuracy test failed")
        }
        if (E.len() > 1e-6) {
            throw Exception("Float/Quaternion accuracy test failed")
        }
        if (F.len() > 1e-6) {
            throw Exception("Quaternion/Quaternion accuracy test failed")
        }
    }
}


fun main() {
//    println(Quaternion(1f, 2f, 3f, 4f).inv())
//    println(1f/Quaternion(1f, 2f, 3f, 4f))
//    println(Quaternion(1f, 2f, 3f, 4f).pow(-1f))



    testMatrixOrthonormalize()
    testQuatMatrixConversion()
    testQuaternionArithmetic()


    // EULER ANGLE TESTS
    testEulerConversion(EulerOrder.XYZ, "toEulerAnglesXYZ accuracy test failed")
    testEulerConversion(EulerOrder.YZX, "toEulerAnglesYZX accuracy test failed")
    testEulerConversion(EulerOrder.ZXY, "toEulerAnglesZXY accuracy test failed")
    testEulerConversion(EulerOrder.ZYX, "toEulerAnglesZYX accuracy test failed")
    testEulerConversion(EulerOrder.YXZ, "toEulerAnglesYXZ accuracy test failed")
    testEulerConversion(EulerOrder.XZY, "toEulerAnglesXZY accuracy test failed")

    for (i in 1..1000) {
        val ang = 6.28318f*randFloat()
        val M = Matrix3(0f, 0f, 1f, sin(ang), cos(ang), 0f, -cos(ang), sin(ang), 0f)
        testEulerMatrix(EulerOrder.XYZ, M, "toEulerAnglesXYZ singularity accuracy test failed")
    }

    for (i in 1..1000) {
        val ang = 6.28318f*randFloat()
        val M = Matrix3(0f, -cos(ang), sin(ang), 1f, 0f, 0f, 0f, sin(ang), cos(ang))
        testEulerMatrix(EulerOrder.YZX, M, "toEulerAnglesYZX singularity accuracy test failed")
    }

    for (i in 1..1000) {
        val ang = 6.28318f*randFloat()
        val M = Matrix3(cos(ang), 0f, sin(ang), sin(ang), 0f, -cos(ang), 0f, 1f, 0f)
        testEulerMatrix(EulerOrder.ZXY, M, "toEulerAnglesZXY singularity accuracy test failed")
    }

    for (i in 1..1000) {
        val ang = 6.28318f*randFloat()
        val M = Matrix3(0f, -sin(ang), cos(ang), 0f, cos(ang), sin(ang), -1f, 0f, 0f)
        testEulerMatrix(EulerOrder.ZYX, M, "toEulerAnglesZYX singularity accuracy test failed")
    }

    for (i in 1..1000) {
        val ang = 6.28318f*randFloat()
        val M = Matrix3(cos(ang), sin(ang), 0f, 0f, 0f, -1f, -sin(ang), cos(ang), 0f)
        testEulerMatrix(EulerOrder.YXZ, M, "toEulerAnglesYXZ singularity accuracy test failed")
    }

    for (i in 1..1000) {
        val ang = 6.28318f*randFloat()
        val M = Matrix3(0f, -1f, 0f, cos(ang), 0f, -sin(ang), sin(ang), 0f, cos(ang))
        testEulerMatrix(EulerOrder.XZY, M, "toEulerAnglesXZY singularity accuracy test failed")
    }
}