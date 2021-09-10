package com.jventrib.waterdemo

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class WaterRenderer : GLSurfaceView.Renderer {
    private var program: Int = 0

    private var squareCoords = floatArrayOf(
        -1.0f, 1.0f, 0.0f,  // ↖ left top︎
        -1.0f, -1.0f, 0.0f, // ↙︎ left bottom
        1.0f, 1.0f, 0.0f,   // ↗︎ right top
        1.0f, -1.0f, 0.0f   // ↘︎ right bottom
    )

    // initialize vertex byte buffer for shape coordinates
    private val vertexBuffer: FloatBuffer =
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(squareCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(squareCoords)
                position(0)
            }
        }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)

        val vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}"

        val fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}"

        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        program = GLES20.glCreateProgram().also {

            // add the vertex shader to program
            GLES20.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES20.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES20.glLinkProgram(it)
        }

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(program)
        // get handle to vertex shader's vPosition member
        val position = GLES20.glGetAttribLocation(program, "vPosition")
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(position)
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
            position,
            3,
            GLES20.GL_FLOAT,
            false,
            12,
            vertexBuffer
        )
        // get handle to fragment shader's vColor member
        GLES20.glGetUniformLocation(program, "vColor").also { colorHandle ->
            // Set color for drawing the triangle
            GLES20.glUniform4fv(colorHandle, 1, floatArrayOf(0f, 0f, 1f, 1f), 0)
        }
        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 4)
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(position)
    }

    fun loadShader(type: Int, shaderCode: String): Int {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        return GLES20.glCreateShader(type).also { shader ->

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }


}

