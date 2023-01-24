/*
 * MIT License
 * Copyright (c) 2022, Donald F Reynolds
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import kotlin.math.atan2
import kotlin.math.sqrt

data class Vector3(val x: Float, val y: Float, val z: Float) {
    companion object {
        val POS_X = Vector3( 1f,  0f,  0f)
        val POS_Y = Vector3( 0f,  1f,  0f)
        val POS_Z = Vector3( 0f,  0f,  1f)
        val NEG_X = Vector3(-1f,  0f,  0f)
        val NEG_Y = Vector3( 0f, -1f,  0f)
        val NEG_Z = Vector3( 0f,  0f, -1f)
    }

    operator fun unaryMinus() = Vector3(-x, -y, -z)

    operator fun plus(that: Vector3) = Vector3(
        this.x + that.x,
        this.y + that.y,
        this.z + that.z
    )

    operator fun minus(that: Vector3) = Vector3(
        this.x - that.x,
        this.y - that.y,
        this.z - that.z
    )

    /**
     * computes the dot product of this vector with that vector
     * @param that the vector with which to be dotted
     * @return the dot product
     **/
    fun dot(that: Vector3) = this.x*that.x + this.y*that.y + this.z*that.z

    /**
     * computes the cross product of this vector with that vector
     * @param that the vector with which to be crossed
     * @return the cross product
     **/
    fun cross(that: Vector3) = Vector3(
        this.y*that.z - this.z*that.y,
        this.z*that.x - this.x*that.z,
        this.x*that.y - this.y*that.x
    )
    /**
     * computes the square of the length of this vector
     * @return the length squared
     **/
    fun lenSq() = x*x + y*y + z*z

    /**
     * computes the length of this quaternion
     * @return the length
     **/
    fun len() = sqrt(x*x + y*y + z*z)

    /**
     * @return the normalized vector
     **/
    fun unit() = this/len()

    operator fun times(that: Float) = Vector3(
        this.x*that,
        this.y*that,
        this.z*that
    )

    // computes division of this vector3 by a float
    operator fun div(that: Float) = Vector3(
        this.x/that,
        this.y/that,
        this.z/that
    )

    /**
     * computes the angle between this vector with that vector
     * @param that the vector to which the angle is computed
     * @return the angle
     **/
    fun angleTo(that: Vector3): Float = atan2(this.cross(that).len(), this.dot(that))
}

operator fun Float.times(that: Vector3): Vector3 = that*this