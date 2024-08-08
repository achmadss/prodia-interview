package com.achmadss.prodiainterview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.transitions.ScreenTransitionContent
import com.achmadss.prodiainterview.ui.screens.home.HomeScreen
import com.achmadss.prodiainterview.ui.theme.ProdiaInterviewTheme
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI
import soup.compose.material.motion.animation.materialSharedAxisX
import soup.compose.material.motion.animation.rememberSlideDistance

@OptIn(KoinExperimentalAPI::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoinAndroidContext {
                ProdiaInterviewTheme {
                    Navigator(
                        screen = HomeScreen,
                        disposeBehavior = NavigatorDisposeBehavior(
                            disposeNestedNavigators = false, disposeSteps = true
                        )
                    ) { navigator ->
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background),
                        ) {
                            val slideDistance = rememberSlideDistance()
                            ScreenTransition(
                                navigator = navigator,
                                transition = {
                                    materialSharedAxisX(
                                        forward = navigator.lastEvent != StackEvent.Pop,
                                        slideDistance = slideDistance,
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScreenTransition(
    navigator: Navigator,
    transition: AnimatedContentTransitionScope<Screen>.() -> ContentTransform,
    modifier: Modifier = Modifier,
    content: ScreenTransitionContent = { it.Content() },
) {
    AnimatedContent(
        targetState = navigator.lastItem,
        transitionSpec = transition,
        modifier = modifier,
        label = "transition",
    ) { screen ->
        navigator.saveableState("transition", screen) {
            content(screen)
        }
    }
}