import kotlin.math.cos
import kotlin.math.sin

enum class EulerOrder {XYZ, YZX, ZXY, ZYX, YXZ, XZY}

// prefer Y.toX
// but if ambiguous, use X.fromY
/*
 * Euler Angles contains both the x y z angle parameters and the order of application
 */
data class EulerAngles(val order: EulerOrder, val x: Float, val y: Float, val z: Float) {
    fun toQuaternion(): Quaternion {
        val cosX = cos(x/2f)
        val cosY = cos(y/2f)
        val cosZ = cos(z/2f)
        val sinX = sin(x/2f)
        val sinY = sin(y/2f)
        val sinZ = sin(z/2f)

        return when (order) {
            EulerOrder.XYZ -> Quaternion(
                cosX*cosY*cosZ - sinX*sinY*sinZ,
                cosY*cosZ*sinX + cosX*sinY*sinZ,
                cosX*cosZ*sinY - cosY*sinX*sinZ,
                cosZ*sinX*sinY + cosX*cosY*sinZ
            )
            EulerOrder.XZY -> Quaternion(
                cosX*cosY*cosZ + sinX*sinY*sinZ,
                cosY*cosZ*sinX - cosX*sinY*sinZ,
                cosX*cosZ*sinY - cosY*sinX*sinZ,
                cosZ*sinX*sinY + cosX*cosY*sinZ
            )
            EulerOrder.YXZ -> Quaternion(
                cosX*cosY*cosZ + sinX*sinY*sinZ,
                cosY*cosZ*sinX + cosX*sinY*sinZ,
                cosX*cosZ*sinY - cosY*sinX*sinZ,
                cosX*cosY*sinZ - cosZ*sinX*sinY
            )
            EulerOrder.YZX -> Quaternion(
                cosX*cosY*cosZ - sinX*sinY*sinZ,
                cosY*cosZ*sinX + cosX*sinY*sinZ,
                cosX*cosZ*sinY + cosY*sinX*sinZ,
                cosX*cosY*sinZ - cosZ*sinX*sinY
            )
            EulerOrder.ZXY -> Quaternion(
                cosX*cosY*cosZ - sinX*sinY*sinZ,
                cosY*cosZ*sinX - cosX*sinY*sinZ,
                cosX*cosZ*sinY + cosY*sinX*sinZ,
                cosZ*sinX*sinY + cosX*cosY*sinZ
            )
            EulerOrder.ZYX -> Quaternion(
                cosX*cosY*cosZ + sinX*sinY*sinZ,
                cosY*cosZ*sinX - cosX*sinY*sinZ,
                cosX*cosZ*sinY + cosY*sinX*sinZ,
                cosX*cosY*sinZ - cosZ*sinX*sinY
            )
        }
    }

    // temp, replace with direct conversion later
    fun toMatrix(): Matrix3 = this.toQuaternion().toMatrix()
}