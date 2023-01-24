import kotlin.math.*
import kotlin.system.measureTimeMillis

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

fun testEulerSingularity(order: EulerOrder, M: Matrix3, exception: String) {
    for (i in 1..1000) {
        val R = 1e-6f*randMatrix()
        val S = M + R
        if (S.det() <= 0f) return

        val error = (S.toEulerAnglesAssumingOrthonormal(order).toMatrix() - S).norm()
        if (error > 2f*R.norm() + 1e-6f) {
            throw Exception(exception)
        }
    }
}

fun testEulerConversions(order: EulerOrder, exception: String) {
    for (i in 1..1000) {
        val e = EulerAngles(order, 6.28318f*randFloat(), 6.28318f*randFloat(), 6.28318f*randFloat())
        val N = e.toMatrix()
        val M = e.toQuaternion().toMatrix()
        if ((N - M).norm() > 1e-6) {
            throw Exception(exception)
        }
    }
}


fun main() {
    val X90 = Matrix3(
        1f, 0f, 0f,
        0f, 0f, -1f,
        0f, 1f, 0f
    )
    val Y90 = Matrix3(
        0f, 0f, 1f,
        0f, 1f, 0f,
        -1f, 0f, 0f
    )
    val Z90 = Matrix3(
        0f, -1f, 0f,
        1f, 0f, 0f,
        0f, 0f, 1f
    )

    testMatrixOrthonormalize()
    testQuatMatrixConversion()
    testQuaternionArithmetic()

    testEulerConversions(EulerOrder.XYZ, "fromEulerAnglesXYZ Quaternion or Matrix3 accuracy test failed")
    testEulerConversions(EulerOrder.YZX, "fromEulerAnglesYZX Quaternion or Matrix3 accuracy test failed")
    testEulerConversions(EulerOrder.ZXY, "fromEulerAnglesZXY Quaternion or Matrix3 accuracy test failed")
    testEulerConversions(EulerOrder.ZYX, "fromEulerAnglesZYX Quaternion or Matrix3 accuracy test failed")
    testEulerConversions(EulerOrder.YXZ, "fromEulerAnglesYXZ Quaternion or Matrix3 accuracy test failed")
    testEulerConversions(EulerOrder.XZY, "fromEulerAnglesXZY Quaternion or Matrix3 accuracy test failed")

    // EULER ANGLE TESTS
    testEulerConversion(EulerOrder.XYZ, "toEulerAnglesXYZ accuracy test failed")
    testEulerConversion(EulerOrder.YZX, "toEulerAnglesYZX accuracy test failed")
    testEulerConversion(EulerOrder.ZXY, "toEulerAnglesZXY accuracy test failed")
    testEulerConversion(EulerOrder.ZYX, "toEulerAnglesZYX accuracy test failed")
    testEulerConversion(EulerOrder.YXZ, "toEulerAnglesYXZ accuracy test failed")
    testEulerConversion(EulerOrder.XZY, "toEulerAnglesXZY accuracy test failed")

    // test robustness to noise
    testEulerSingularity(EulerOrder.XYZ, Y90, "toEulerAnglesXYZ singularity accuracy test failed")
    testEulerSingularity(EulerOrder.YZX, Z90, "toEulerAnglesYZX singularity accuracy test failed")
    testEulerSingularity(EulerOrder.ZXY, X90, "toEulerAnglesZXY singularity accuracy test failed")
    testEulerSingularity(EulerOrder.ZYX, Y90, "toEulerAnglesZYX singularity accuracy test failed")
    testEulerSingularity(EulerOrder.YXZ, X90, "toEulerAnglesYXZ singularity accuracy test failed")
    testEulerSingularity(EulerOrder.XZY, Z90, "toEulerAnglesXZY singularity accuracy test failed")


    // speed test a linear (align) method against some standard math functions
    var x = Quaternion(1f, 2f, 3f, 4f)

    val dtAlign = measureTimeMillis {
        for (i in 1..10_000_000) {
            val u = Vector3(1f, 0f, 0f)
            val v = Vector3(0f, 1f, 0f)
            // to make sure it is not optimized away
            x = x.align(u, v)
//            internally, x.align is:
//            val U = Quaternion(0f, u)
//            val V = Quaternion(0f, v)
//            x = (V*x/U + (V/U).len()*x)/2f
        }
    }

    var y = x.toMatrix()
    val dtOrthonormalize = measureTimeMillis {
        for (i in 1..10_000_000) {
            // to make sure it is not optimized away
            y = 1.0001f*y.orthonormalize()
//            internally, x.align is:
//            val U = Quaternion(0f, u)
//            val V = Quaternion(0f, v)
//            x = (V*x/U + (V/U).len()*x)/2f
        }
    }


    val dtAtan2 = measureTimeMillis {
        for (i in 1..10_000_000) {
            atan2(1f, 1f) // 45 degrees
        }
    }


    val dtAsin = measureTimeMillis {
        for (i in 1..10_000_000) {
            asin(0.7071f) // 45 degrees
        }
    }

    println(x)

    println(dtAlign) // 213
    println(dtOrthonormalize) // 244
    println(dtAtan2) // 610
    println(dtAsin) // 3558
}