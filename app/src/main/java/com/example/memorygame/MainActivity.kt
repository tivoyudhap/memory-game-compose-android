package com.example.memorygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.memorygame.entity.CardEntity
import com.example.memorygame.entity.GameEntity
import com.example.memorygame.support.STATE_GAME_NOT_START
import com.example.memorygame.support.STATE_GAME_OVER
import com.example.memorygame.support.STATE_GAME_PAUSED
import com.example.memorygame.support.STATE_GAME_RUNNING
import com.example.memorygame.support.STATE_GAME_WINNER
import com.example.memorygame.ui.theme.BlueSoft
import com.example.memorygame.ui.theme.MemoryGameTheme
import com.example.memorygame.ui.theme.Orange
import com.example.memorygame.ui.theme.OrangeLight
import com.example.memorygame.viewmodel.GameCounterViewModel
import com.example.memorygame.viewmodel.GamePlayViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GameCounterViewModel by viewModels()
    private val gamePlayViewModel: GamePlayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MemoryGameTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .background(color = Orange)
                ) { paddingValues ->
                    Column(modifier = Modifier.padding(paddingValues)) {
                        HeaderContent(viewModel = viewModel)
                        MainContent(gamePlayViewModel = gamePlayViewModel, gameCounterViewModel = viewModel)
                        BottomContent(viewModel = viewModel)
                    }
                }
            }
        }

        gamePlayViewModel.generateInitialImageMapping()

        observeViewModel()
    }

    override fun onDestroy() {
        viewModel.destroy()
        super.onDestroy()
    }

    private fun observeViewModel() {
        gamePlayViewModel.allCardFlippedLiveData.observeForever {
            if (it) {
                viewModel.win()
            }
        }

        viewModel.counterLiveData.observeForever {
            if (it.state == STATE_GAME_NOT_START) {
                gamePlayViewModel.generateInitialImageMapping()
            }
        }
    }
}

@Composable
fun CardView(entity: CardEntity, gamePlayViewModel: GamePlayViewModel, gameCounterViewModel: GameCounterViewModel) {
    val counterState by gameCounterViewModel.counterLiveData.observeAsState()

    val widthCardStandard: Double = (getScreenDimensions().first.value - getScreenDimensions().first.value * 0.1) / 4
    val rotationY by animateFloatAsState(
        targetValue = if (entity.isFlipped) 180f else 0f,
        animationSpec = TweenSpec(durationMillis = 500), label = "rotationY"
    )

    Box(modifier = Modifier
        .width(widthCardStandard.dp)
        .height(widthCardStandard.dp)
        .padding(8.dp)
        .graphicsLayer(rotationY = rotationY)
        .clickable {
            if (counterState?.state == STATE_GAME_RUNNING) {
                gamePlayViewModel.onFlip(entity)
            }
        },
        contentAlignment = Alignment.Center
    ) {
        CardContent(entity = entity, widthCardStandard = widthCardStandard.dp)
    }
}

@Composable
fun MainContent(gamePlayViewModel: GamePlayViewModel, gameCounterViewModel: GameCounterViewModel) {
    val listOfCard by gamePlayViewModel.initiateCardLiveData.observeAsState(emptyList())

    val gridSize = 20
    val columns = 4

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(getScreenDimensions().second.times(0.8f))) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentSize()
        ) {
            (0 until gridSize step columns).map { start ->
                Row {
                    (start until start + columns).forEach { index ->
                        CardView(
                            entity = listOfCard[index],
                            gamePlayViewModel = gamePlayViewModel,
                            gameCounterViewModel = gameCounterViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CardContent(entity: CardEntity, widthCardStandard: Dp) {
    if (!entity.isFlipped) {
        BackContent(widthCardStandard)
    } else {
        FrontContent(entity = entity, widthCardStandard)
    }
}

@Composable
fun HeaderContent(viewModel: GameCounterViewModel) {
    val gameEntity: GameEntity? by viewModel.counterLiveData.observeAsState()

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(getScreenDimensions().second.times(0.1f)),
        contentAlignment = Alignment.BottomCenter) {
        val headerText = when (gameEntity?.state) {
            null -> stringResource(id = R.string.press_play_to_start)
            STATE_GAME_RUNNING -> stringResource(id = R.string.second_left_information, gameEntity?.counter ?: -1, if ((gameEntity?.counter ?: 1) > 1) "s" else "")
            STATE_GAME_PAUSED -> stringResource(id = R.string.game_paused, gameEntity?.counter ?: -1)
            STATE_GAME_WINNER -> stringResource(id = R.string.game_over_winner)
            else -> stringResource(id = R.string.game_over)
        }

        Text(headerText, modifier = Modifier.wrapContentSize(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BottomContent(viewModel: GameCounterViewModel) {
    val state: GameEntity? by viewModel.counterLiveData.observeAsState()

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
                    .width(150.dp)
                    .height(80.dp)
                ,
                colors = ButtonDefaults.buttonColors(containerColor = OrangeLight),
                contentPadding = PaddingValues(4.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = { viewModel.startOrPause() }) {
                Image(
                    painter = painterResource(
                        id =
                        when (state?.state) {
                            STATE_GAME_RUNNING -> R.drawable.rounded_pause_circle
                            STATE_GAME_WINNER -> R.drawable.baseline_restart_alt_24
                            STATE_GAME_OVER -> R.drawable.baseline_restart_alt_24
                            else -> R.drawable.rounded_not_started
                        }
                    ),
                    contentDescription = "Start / Pause",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(Color.Transparent)
                )
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
fun FrontContent(entity: CardEntity, widthCardStandard: Dp) {
    Surface(
        modifier = Modifier
            .width(widthCardStandard)
            .height(widthCardStandard),
        shape = RoundedCornerShape(16.dp),
        color = if (entity.isComplete) Color.Green else Color.Red
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(id = entity.resourceId), contentDescription = "Fruit Image")
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
        color = BlueSoft
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
    Row {
        CardContent(entity = CardEntity(R.drawable.ic_rambutan), widthCardStandard = 100.dp)
        CardContent(entity = CardEntity(R.drawable.ic_watermelon), widthCardStandard = 100.dp)
        CardContent(entity = CardEntity(R.drawable.ic_orange), widthCardStandard = 100.dp)
        CardContent(entity = CardEntity(R.drawable.ic_papaya), widthCardStandard = 100.dp)
    }
}