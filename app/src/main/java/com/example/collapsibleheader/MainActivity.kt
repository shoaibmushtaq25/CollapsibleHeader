package com.example.collapsibleheader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.collapsibleheader.ui.theme.CollapsibleHeaderTheme
import kotlin.math.roundToInt

private val contents: List<String> = (1..50).map { "Lazy Column Item $it" }
val connection = CollapsingAppBarNestedScrollConnection()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CollapsibleHeaderTheme {
                CollapsibleThing()
            }
        }
    }
}

@Composable
fun CollapsibleThing(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.tertiary
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(connection)
        ) {
            Column(modifier = Modifier.scrollable(
                orientation = Orientation.Vertical,
                // state for Scrollable, describes how consume scroll amount
                state =
                rememberScrollableState { delta ->
                    0f
                }
            )) {
                ExpandedHeader(
                    modifier = Modifier,
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(weight = 1f)
                        .background(Color.White)
                ) {
                    items(contents) {
                        ListItem(item = it)
                    }
                }
            }
        }
    }
}

@Composable
fun ListItem(modifier: Modifier = Modifier, item: String) {
    Text(modifier = modifier.padding(16.dp), text = item, color = Color.Black)
    Divider(color = Color.LightGray, modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
fun HeaderItem(modifier: Modifier, text: String) {
    Box(modifier = modifier) {
        Text(modifier = Modifier.align(Alignment.Center), text = text, color = Color.Black)
    }
}


@Composable
fun ExpandedHeader(modifier: Modifier = Modifier) {
    //To simulate Header Content
    SubcomposeLayout(modifier) { constraints ->

        val headerPlaceable = subcompose("header") {
            Column(modifier = modifier.background(Color.Cyan)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Red)
                        .height(250.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.texture_image),
                        contentDescription = "Header Image",
                        contentScale = ContentScale.Crop,
                    )

                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(56.dp)
                            .background(Color.White)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo Image",
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
                HeaderContent()
                Divider(color = Color.LightGray, modifier = Modifier.height(16.dp))
            }
        }.first().measure(constraints)

        val navBarPlaceable = subcompose("navBar") {
            NavBar()
        }.first().measure(constraints)

        connection.maxHeight = headerPlaceable.height.toFloat()
        connection.minHeight = navBarPlaceable.height.toFloat()

        val space = IntSize(
            constraints.maxWidth,
            headerPlaceable.height + connection.headerOffset.roundToInt()
        )
        layout(space.width, space.height) {
            headerPlaceable.place(0, connection.headerOffset.roundToInt())
            navBarPlaceable.place(
                Alignment.TopCenter.align(
                    IntSize(navBarPlaceable.width, navBarPlaceable.height),
                    space,
                    layoutDirection
                )
            )
        }
    }
}

@Composable
fun NavBar() {
    var alphaValue by remember { mutableFloatStateOf(0f) }

    alphaValue = (3 * (1f - connection.progress)).coerceIn(0f, 1f)

    //To Simulate Navigation BAR
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(
                width = 1.dp, color = Color.Gray.copy(alpha = alphaValue)
            )
            .background(Color.White.copy(alpha = alphaValue))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO: Handle action */ }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black.copy(alpha = alphaValue)
                )
            }

            Text(
                modifier = Modifier.weight(1f),
                text = "Navigation Bar",
                color = Color.Black.copy(alpha = alphaValue)
            )

            IconButton(onClick = { /* TODO: Handle action */ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Search",
                    tint = Color.Black.copy(alpha = alphaValue)
                )
            }
        }
    }
}

@Composable
fun HeaderContent() {
    HeaderItem(
        Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(2.dp)
            )
            .padding(8.dp),
        "Header content item 1",
    )



    HeaderItem(
        Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(2.dp)
            )
            .padding(8.dp),
        "Header content item 2",
    )
}