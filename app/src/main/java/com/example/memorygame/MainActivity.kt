package com.example.memorygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.memorygame.ui.theme.MemoryGameTheme
import com.example.memorygame.ui.theme.Orange

class MainActivity : ComponentActivity() {

    private val imageMapping: MutableMap<Int, Int> = mutableMapOf(
        1 to R.drawable.ic_banana,
        2 to R.drawable.ic_coconut,
        3 to R.drawable.ic_orange,
        4 to R.drawable.ic_mangosteen,
        5 to R.drawable.ic_papaya,
        6 to R.drawable.ic_rambutan,
        7 to R.drawable.ic_watermelon,
        8 to R.drawable.ic_grape,
        9 to R.drawable.ic_melon,
        10 to R.drawable.ic_cherry
    )

    private val listOfCard: MutableList<Int> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        generateInitialImageMapping()

        setContent {
            MemoryGameTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Orange),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        HeaderContent()
                        MainContent(listOfCard = listOfCard)
                        BottomContent()
                    }
                }
            }
        }
    }

    private fun generateInitialImageMapping() {
        val list: MutableList<Int> = mutableListOf()
        for (a in 0 until 20) {
            list.add(imageMapping.getValue(if (a + 1 > 10) a - 9 else a + 1))
        }

        listOfCard.addAll(list.shuffled())
    }
}

@Composable
fun CardView(image: Int) {
    val widthCardStandard: Double = (getScreenDimensions().first.value - getScreenDimensions().first.value * 0.1) / 4
    var isFlipped by remember { mutableStateOf(false) }
    val rotationY by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = TweenSpec(durationMillis = 500), label = "rotationY"
    )

    Box(modifier = Modifier
        .width(widthCardStandard.dp)
        .height(widthCardStandard.dp)
        .padding(8.dp)
        .graphicsLayer(rotationY = rotationY)
        .clickable { isFlipped = !isFlipped },
        contentAlignment = Alignment.Center
    ) {
        CardContent(isFlipped = isFlipped, image = image, widthCardStandard = widthCardStandard.dp)
    }
}

@Composable
fun MainContent(listOfCard: List<Int>) {
    val gridSize = 20
    val columns = 4

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(getScreenDimensions().second.times(0.8f))) {
        Column(
            modifier = Modifier.align(Alignment.Center).wrapContentSize()
        ) {
            (0 until gridSize step columns).map { start ->
                Row {
                    (start until start + columns).forEach { index ->
                        CardView(listOfCard[index])
                    }
                }
            }
        }
    }
}

@Composable
fun CardContent(isFlipped: Boolean, image: Int, widthCardStandard: Dp) {
    if (!isFlipped) {
        BackContent(widthCardStandard)
    } else {
        FrontContent(image = image, widthCardStandard)
    }
}

@Composable
fun HeaderContent() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(getScreenDimensions().second.times(0.1f))) {

    }
}

@Composable
fun BottomContent() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(getScreenDimensions().second.times(0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                contentPadding = PaddingValues(16.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = { /*TODO*/ }) {
                Text(text = "Hello 1")
            }
            Button(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                contentPadding = PaddingValues(16.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = { /*TODO*/ }) {
                Text(text = "Hello 2")
            }
            Button(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                contentPadding = PaddingValues(16.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = { /*TODO*/ }) {
                Image(painter = painterResource(id = R.drawable.baseline_restart_alt_24), contentDescription = "Refresh")
            }
        }
    }
}

@Composable
fun getScreenDimensions(): Pair<Dp, Dp> {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    return screenWidth to screenHeight
}

@Composable
fun FrontContent(image: Int, widthCardStandard: Dp) {
    Surface(
        modifier = Modifier
            .width(widthCardStandard)
            .height(widthCardStandard),
        shape = RoundedCornerShape(16.dp),
        color = Color.Red
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(id = image), contentDescription = "Fruit Image")
        }
    }
}

@Composable
fun BackContent(widthCardStandard: Dp) {
    Surface(
        modifier = Modifier
            .width(widthCardStandard)
            .height(widthCardStandard),
        shape = RoundedCornerShape(16.dp),
        color = Color.Blue
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
            contentAlignment = Alignment.Center) {
            Image(painter = painterResource(id = R.drawable.logo_memory_game), contentDescription = "Memory Game")
        }
    }
}

@Preview(showBackground = true, device = Devices.NEXUS_5)
@Composable
fun GreetingPreview() {
//    BottomContent()
    Row {
        CardContent(isFlipped = true, image = R.drawable.ic_rambutan, widthCardStandard = 100.dp)
        CardContent(isFlipped = true, image = R.drawable.ic_watermelon, widthCardStandard = 100.dp)
        CardContent(isFlipped = true, image = R.drawable.ic_orange, widthCardStandard = 100.dp)
        CardContent(isFlipped = true, image = R.drawable.ic_papaya, widthCardStandard = 100.dp)
    }
}