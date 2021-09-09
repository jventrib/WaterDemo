package com.jventrib.waterdemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
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
import kotlin.random.Random


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val background = BitmapFactory.decodeStream(resources.openRawResource(R.raw.stone_small))

        setContent {
            WaterDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    WaterBox(background)
                }
            }
        }
    }
}

@Composable
fun WaterBox(initialBackground: Bitmap, modifier: Modifier = Modifier) {
    val width = initialBackground.width
    val height = initialBackground.height

    fun Int.x(): Int {
        return this % width
    }

    fun Int.y(): Int {
        return this / width
    }

    val buffer1 = IntArray(width * height)
    val buffer2 = IntArray(width * height)
    var currentBuffer by remember {
        mutableStateOf(buffer1, neverEqualPolicy())
    }

    var ib by remember {
        mutableStateOf(
            initialBackground.copy(initialBackground.config, true),
            neverEqualPolicy()
        )
    }
    LaunchedEffect(Unit) {
        while (true) {
            awaitFrame()

            currentBuffer.set(
                Random.nextInt(0, 10) * width + Random.nextInt(0, 10),
                Random.nextInt(255)
            )
            for (index in 0 until currentBuffer.size) {
                val i = currentBuffer[index]
                ib[index.x(), index.y()] = Color.rgb(i, i, i)
            }
            ib = ib
        }
    }
    Box {
        Canvas(Modifier.fillMaxSize()) {
            drawImage(ib.asImageBitmap())
        }
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