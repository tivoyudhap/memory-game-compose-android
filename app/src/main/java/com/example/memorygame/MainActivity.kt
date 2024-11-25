package com.example.memorygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.memorygame.entity.CardEntity
import com.example.memorygame.entity.GameEntity
import com.example.memorygame.support.STATE_GAME_NOT_START
import com.example.memorygame.support.STATE_GAME_OVER
import com.example.memorygame.support.STATE_GAME_PAUSED
import com.example.memorygame.support.STATE_GAME_RUNNING
import com.example.memorygame.support.STATE_GAME_WINNER
import com.example.memorygame.support.TEST_TAG_BOTTOM_BOX
import com.example.memorygame.support.TEST_TAG_CARD_BOX
import com.example.memorygame.support.TEST_TAG_FRONT_CARD
import com.example.memorygame.support.TEST_TAG_GAMEPLAY_BUTTON
import com.example.memorygame.support.TEST_TAG_GAMEPLAY_IMAGE
import com.example.memorygame.support.TEST_TAG_LAZY_VERTICAL_COLUMN
import com.example.memorygame.support.TEST_TAG_MAIN_COLUMN
import com.example.memorygame.support.TEST_TAG_SCAFFOLD
import com.example.memorygame.support.cardStateTag
import com.example.memorygame.support.testTag
import com.example.memorygame.ui.theme.BlueSoft
import com.example.memorygame.ui.theme.MemoryGameTheme
import com.example.memorygame.ui.theme.Orange
import com.example.memorygame.ui.theme.OrangeLight
import com.example.memorygame.viewmodel.GameCounterViewModel
import com.example.memorygame.viewmodel.GamePlayViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
                        .background(color = Orange)
                        .testTag(TEST_TAG_SCAFFOLD)
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .testTag(TEST_TAG_MAIN_COLUMN)
                    ) {
                        HeaderContent(viewModel = viewModel)
                        MainContent(
                            gamePlayViewModel = gamePlayViewModel,
                            gameCounterViewModel = viewModel,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .testTag(TEST_TAG_LAZY_VERTICAL_COLUMN)
                        )
                        BottomContent(viewModel = viewModel)
                    }

                    WinLottieAnimation(gamePlayViewModel, onDismissWinAnimationClicked)
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

    private val onDismissWinAnimationClicked = fun () {
        viewModel.startOrPause()
    }
}

@Composable
fun WinLottieAnimation(gamePlayViewModel: GamePlayViewModel, onDismissWinClicked: () -> Unit) {
    val state = gamePlayViewModel.allCardFlippedLiveData.observeAsState()

    if (state.value == true) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_celebration))

        Box(modifier = Modifier
            .background(Color.Black.copy(alpha = 0.6f))
            .fillMaxWidth()
            .fillMaxHeight()
            .clickable { onDismissWinClicked() },
            contentAlignment = Alignment.Center) {
            Column(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever
                )
            }

            Text("You win! Tap anywhere to dismiss.", color = Color.White)
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
        .testTag(TEST_TAG_CARD_BOX)
        .cardStateTag(isFlipped = entity.isFlipped)
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
fun MainContent(
    gamePlayViewModel: GamePlayViewModel,
    gameCounterViewModel: GameCounterViewModel,
    modifier: Modifier
) {
    val listOfCard by gamePlayViewModel.initiateCardLiveData.observeAsState(emptyList())

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp))
    {
        items(20) {
            CardView(
                entity = listOfCard[it],
                gamePlayViewModel = gamePlayViewModel,
                gameCounterViewModel = gameCounterViewModel
            )
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

    val imageResource = when (state?.state) {
        STATE_GAME_RUNNING -> R.drawable.rounded_pause_circle
        STATE_GAME_WINNER -> R.drawable.baseline_restart_alt_24
        STATE_GAME_OVER -> R.drawable.baseline_restart_alt_24
        else -> R.drawable.rounded_not_started
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(bottom = 32.dp)
        .testTag(TEST_TAG_BOTTOM_BOX)
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        val elevation by animateDpAsState(targetValue = if (isPressed) 2.dp else 8.dp, label = "")

        Button(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.Center)
                .testTag(TEST_TAG_GAMEPLAY_BUTTON),
            colors = ButtonDefaults.buttonColors(containerColor = OrangeLight),
            contentPadding = PaddingValues(4.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(elevation),
            interactionSource = interactionSource,
            onClick = { viewModel.startOrPause() }) {
            Image(
                modifier = Modifier
                    .width(150.dp)
                    .height(40.dp)
                    .background(Color.Transparent)
                    .testTag(TEST_TAG_GAMEPLAY_IMAGE)
                    .testTag(resourceId = imageResource),
                painter = painterResource(id = imageResource),
                contentDescription = "Start / Pause"
            )
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
            .height(widthCardStandard)
            .testTag("${TEST_TAG_FRONT_CARD}_${entity.id}")
            .testTag(entity.resourceId),
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