import kotlin.math.*

/**
 * A Quaternion class properly implementing Quaternions as a real component and 3 imaginary components
 * All operations are well-defined
 */
data class Quaternion(val w: Float, val x: Float, val y: Float, val z: Float) {
    companion object {
        val ZERO = Quaternion(0f, 0f, 0f, 0f)
        val ONE = Quaternion(1f, 0f, 0f, 0f)
        val I = Quaternion(0f, 1f, 0f, 0f)
        val J = Quaternion(0f, 0f, 1f, 0f)
        val K = Quaternion(0f, 0f, 0f, 1f)

        // creates a new quaternion representing the rotation about axis v by rotational angle v
        /**
         * creates a new quaternion representing the rotation about axis v by rotational angle of v's length
         * @return the new quaternion
         **/
        fun fromRotationVector(v: Vector3): Quaternion {
            return Quaternion(0f, v.x/2f, v.y/2f, v.z/2f).exp()
        }
    }

    operator fun unaryMinus(): Quaternion = Quaternion(-w, -x, -y, -z)

    operator fun plus(that: Quaternion): Quaternion = Quaternion(
        this.w + that.w,
        this.x + that.x,
        this.y + that.y,
        this.z + that.z
    )

    operator fun plus(that: Float): Quaternion = Quaternion(this.w + that, this.x, this.y, this.z)

    operator fun minus(that: Quaternion): Quaternion = Quaternion(
        this.w - that.w,
        this.x - that.x,
        this.y - that.y,
        this.z - that.z
    )

    operator fun minus(that: Float): Quaternion = Quaternion(this.w - that, this.x, this.y, this.z)

    /**
     * computes the dot product of this quaternion with that quaternion
     * @param that the quaternion with which to be dotted
     * @return the inversed quaternion
     **/
    fun dot(that: Quaternion): Float = this.w*that.w + this.x*that.x + this.y*that.y + this.z*that.z

    /**
     * computes the square of the length of this quaternion
     * @return the length squared
     **/
    fun lenSq(): Float = w*w + x*x + y*y + z*z

    /**
     * computes the length of this quaternion
     * @return the length
     **/
    fun len(): Float = sqrt(w*w + x*x + y*y + z*z)

    /**
     * @return the normalized quaternion
     **/
    fun unit(): Quaternion {
        val m = len()
        return if (m == 0f) ZERO else this/m
    }

    operator fun times(that: Float): Quaternion = Quaternion(
        this.w*that,
        this.x*that,
        this.y*that,
        this.z*that
    )

    operator fun times(that: Quaternion): Quaternion = Quaternion(
        this.w*that.w - this.x*that.x - this.y*that.y - this.z*that.z,
        this.x*that.w + this.w*that.x - this.z*that.y + this.y*that.z,
        this.y*that.w + this.z*that.x + this.w*that.y - this.x*that.z,
        this.z*that.w - this.y*that.x + this.x*that.y + this.w*that.z
    )

    /**
     * computes the inverse of this quaternion
     * @return the inversed quaternion
     **/
    fun inv(): Quaternion {
        val lenSq = lenSq()
        return Quaternion(
            w/lenSq,
            -x/lenSq,
            -y/lenSq,
            -z/lenSq
        )
    }

    operator fun div(that: Float): Quaternion = this*(1f/that)

    /**
     * computes right division, this * that^-1
     **/
    operator fun div(that: Quaternion): Quaternion = this*that.inv()

    /**
     * @return the quaternion with only the w component
     **/
    fun re(): Quaternion = Quaternion(w, 0f, 0f, 0f)

    /**
     * @return the quaternion with only x y z components
     **/
    fun im(): Quaternion = Quaternion(0f, x, y, z)

    /**
     * @return the conjugate of this quaternion
     **/
    fun conj(): Quaternion = Quaternion(w, -x, -y, -z)

    /**
     * computes the logarithm of this quaternion
     * @return the log of this quaternion
     **/
    fun log(): Quaternion {
        val imLen = sqrt(x*x + y*y + z*z)
        val len = sqrt(w*w + x*x + y*y + z*z)

        if (imLen == 0f) {
            return Quaternion(ln(len), x/w, y/w, z/w)
        }

        val ang = atan2(imLen, w)
        return Quaternion(
            ln(len),
            ang/imLen*x,
            ang/imLen*y,
            ang/imLen*z
        )
    }

    /**
     * raises e to the power of this quaternion
     * @return the exponentiated quaternion
     **/
    fun exp(): Quaternion {
        val ang = sqrt(x*x + y*y + z*z)
        val len = exp(w)

        if (ang == 0f) {
            return Quaternion(len, len*x, len*y, len*z)
        }

        val co = cos(ang)
        val si = sin(ang)
        return Quaternion(
            len*co,
            len*si/ang*x,
            len*si/ang*y,
            len*si/ang*z
        )
    }

    /**
     * raises this quaternion to the power of t
     * @param t the power by which to raise this quaternion
     * @return the powered quaternion
     **/
    fun pow(t: Float): Quaternion = (log()*t).exp()

    /**
     * interpolates from this quaternion to that quaternion by t in quaternion space
     * @param that the quaternion to interpolate to
     * @param t the amount to interpolate
     * @return interpolated quaternion
     **/
    fun interp(that: Quaternion, t: Float): Quaternion {
        return if (t == 0f) {
            this
        } else if (t == 1f) {
            that
        } else if (t < 0.5f) {
            (that/this).pow(t)*this
        } else {
            (this/that).pow(1f - t)*that
        }
    }

    /**
     * interpolates from this quaternion to that quaternion by t in rotation space
     * @param that the quaternion to interpolate to
     * @param t the amount to interpolate
     * @return interpolated quaternion
     **/
    fun interpR(that: Quaternion, t: Float): Quaternion {
        return if (this.dot(that) < 0f) {
            this.interp(-that, t)
        } else {
            this.interp(that, t)
        }
    }

    /**
     * linearly interpolates from this quaternion to that quaternion by t in quaternion space
     * @param that the quaternion to interpolate to
     * @param t the amount to interpolate
     * @return interpolated quaternion
     **/
    fun lerp(that: Quaternion, t: Float): Quaternion = (1f - t)*this + t*that

    /**
     * linearly interpolates from this quaternion to that quaternion by t in rotation space
     * @param that the quaternion to interpolate to
     * @param t the amount to interpolate
     * @return interpolated quaternion
     **/
    fun lerpR(that: Quaternion, t: Float): Quaternion {
        return if (this.dot(that) < 0f) {
            this.lerp(-that, t)
        } else {
            this.lerp(that, t)
        }
    }

    /**
     * computes this quaternion's angle in quaternion space
     * @return angle
     **/
    fun angle(): Float = atan2(sqrt(x*x + y*y + z*z), w)

    /**
     * computes this quaternion's angle in rotation space
     * @return angle
     **/
    fun angleR(): Float = 2f* atan2(sqrt(x*x + y*y + z*z), abs(w))

    /**
     * computes the angle between this quaternion and that quaternion in quaternion space
     * @param that the other quaternion
     * @return angle
     **/
    fun angleTo(that: Quaternion): Float = (this/that).angle()

    /**
     * computes the angle between this quaternion and that quaternion in rotation space
     * @param that the other quaternion
     * @return angle
     **/
    fun angleToR(that: Quaternion): Float = (this/that).angleR()

    /**
     * computes the angle this quaternion rotates about the u axis in quaternion space
     * @param u the axis
     * @return angle
     **/
    fun angleAbout(u: Vector3): Float {
        val uDotIm = x*u.x + y*u.y + z*u.z
        val uLen = u.len()
        return atan2(uDotIm, uLen*w)
    }

    /**
     * computes the angle this quaternion rotates about the u axis in rotation space
     * @param u the axis
     * @return angle
     **/
    fun angleAboutR(u: Vector3): Float {
        val uDotIm = x*u.x + y*u.y + z*u.z
        val uLen = u.len()
        return if (uDotIm < 0f) {
            2f*atan2(-uDotIm, -uLen*w)
        } else {
            2f*atan2(uDotIm, uLen*w)
        }
    }

    /**
     * finds Q, the quaternion nearest to this quaternion representing a rotation purely about the global u axis
     * Q is NOT unitized
     * @param u the local direction
     * @return Q
     **/
    fun project(u: Vector3): Quaternion {
        val t = (x*u.x + y*u.y + z*u.z)/u.lenSq()
        return Quaternion(w, t*u.x, t*u.y, t*u.z)
    }

    /**
     * finds Q, the quaternion nearest to this quaternion whose local u direction aligns with the global v direction.
     * Q is NOT unitized
     * @param u the local direction
     * @param v the global direction
     * @return Q
    **/
    fun align(u: Vector3, v: Vector3): Quaternion {
        val U = Quaternion(0f, u.x, u.y, u.z)
        val V = Quaternion(0f, v.x, v.y, v.z)

        return (V*this/U + (V/U).len()*this)/2f
    }

    /**
     * applies this quaternion's rotation to that vector
     * @param that the vector to be transformed
     * @return that vector transformed by this quaternion
     **/
    fun sandwich(that: Vector3): Vector3 {
        val S = this*Quaternion(0f, that.x, that.y, that.z)/this
        return Vector3(S.x, S.y, S.z)
    }

    /**
     * computes this quaternion's rotation axis
     * @return rotation axis
     **/
    fun axis(): Vector3 = Vector3(x, y, z).unit()

    /**
     * computes the rotation vector representing this quaternion's rotation
     * @return rotation vector
     **/
    fun toRotationVector(): Vector3 {
        val log = this.log()
        return Vector3(2f*log.x, 2f*log.y, 2f*log.z)
    }

    /**
     * computes the matrix representing this quaternion's rotation
     * @return rotation matrix
     **/
    fun toMatrix(): Matrix3 {
        val d = lenSq()
        return Matrix3(
            (w*w + x*x - y*y - z*z)/d, 2f*(x*y - w*z)/d, 2f*(w*y + x*z)/d,
            2f*(x*y + w*z)/d, (w*w - x*x + y*y - z*z)/d, 2f*(y*z - w*x)/d,
            2f*(x*z - w*y)/d, 2f*(w*x + y*z)/d, (w*w - x*x - y*y + z*z)/d
        )
    }

    /**
     * computes the euler angles representing this quaternion's rotation
     * @param order the order in which to decompose this quaternion into euler angles
     * @return euler angles
     **/
    fun toEulerAngles(order: EulerOrder): EulerAngles = this.toMatrix().toEulerAnglesAssumingOrthonormal(order)
}

operator fun Float.plus(that: Quaternion): Quaternion = that + this
operator fun Float.minus(that: Quaternion): Quaternion = -that + this
operator fun Float.times(that: Quaternion): Quaternion = that*this
operator fun Float.div(that: Quaternion): Quaternion = that.inv()*this