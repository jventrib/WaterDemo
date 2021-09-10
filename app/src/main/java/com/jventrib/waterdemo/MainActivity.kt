package com.jventrib.waterdemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.WindowMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.set
import com.jventrib.waterdemo.ui.theme.WaterDemoTheme
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import java.nio.Buffer
import java.nio.IntBuffer
import java.time.Instant
import kotlin.random.Random


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val background = BitmapFactory.decodeStream(resources.openRawResource(R.raw.stone_small))
        setContent {
            WaterDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {


                    WaterBox(
                        background,
                        windowManager.currentWindowMetrics.bounds.width(),
                        windowManager.currentWindowMetrics.bounds.height()
                    )
                }
            }
        }
    }
}

@Composable
fun WaterBox(initialBackground: Bitmap, width: Int, height: Int, modifier: Modifier = Modifier) {
//    val width = initialBackground.width
//    val height = initialBackground.height
    val width = 400
    val height = 300
    val damping = 32

    fun Int.x(): Int {
        return this % width
    }

    fun Int.y(): Int {
        return this / width
    }

    fun index(x: Int, y: Int) = y * width + x

    val buffer1 = IntBuffer.allocate(width * height)
    val buffer2 = IntBuffer.allocate(width * height)
    var currentBuffer by remember {
        mutableStateOf(buffer1, neverEqualPolicy())
    }

    var previousBuffer = buffer2

    //    fun gridA(x: Int, y: Int) = (currentBuffer[index(x, y)])
    fun gridA(x: Int, y: Int): Int {
        val index = index(x, y)
        return (
                previousBuffer[index - 1]
                        + previousBuffer[index + 1]
                        + previousBuffer[index + width]
                        + previousBuffer[index - width]
                ) / 2 - currentBuffer[index]
    }

    var ib = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

    LaunchedEffect(Unit) {
        while (true) {
            awaitFrame()
            val start = System.currentTimeMillis()
            currentBuffer.put(index(Random.nextInt(width), Random.nextInt(height)), 2000)
            for (x in 1..width - 2) {
                for (y in 1..height - 2) {
                    val newValue = gridA(x, y)
                    val damped = newValue - newValue / damping
                    currentBuffer.put(index(x, y), damped)
                }
            }
            // Swap buffer
            val temp = currentBuffer
            currentBuffer = previousBuffer
            previousBuffer = temp
            val end = System.currentTimeMillis()
            Log.d("Water", "Frame time: ${end - start}ms")
        }
    }
    Box {
        Canvas(Modifier.fillMaxSize()) {
            for (index in 0 until currentBuffer.limit()) {
                val i = (currentBuffer[index] + 123).max(255)
                ib.set(index.x(), index.y(), Color.rgb(i, i, i))
            }
            drawImage(ib.asImageBitmap())
        }
    }
}

private fun Int.max(max: Int): Int {
    return when {
        this < 0 -> 0
        this >= max -> max - 1
        else -> this
    }
}


//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun DefaultPreview() {
//    WaterDemoTheme {
//        val background = BitmapFactory.decodeStream(resources.openRawResource(R.raw.stone))
//
//        Greeting("Android")
//    }
//}