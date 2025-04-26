package compose.project.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.InspectorValueInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Constraints
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random


@Composable
@Preview
fun App(modifier: Modifier = Modifier) {
    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {}, modifier = Modifier, content = {
                    Text(text = "Add color")
                })
                Button(onClick = {}, modifier = Modifier, content = {
                    Text(text = "Remove first color")
                })
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {}, modifier = modifier, content = {
                    Text(text = "Remove last color")
                })
                Button(onClick = {}, modifier = modifier, content = {
                    Text(text = "Clear colors")
                })
            }

            GridLayout(modifier)
        }
    }
}


@Composable
fun GridLayout(
    modifier: Modifier = Modifier
) {

    val colors = arrayOfNulls<Color>(1)
    colors.forEachIndexed { index, _ ->
        val color = Color(
            Random.nextInt(255),
            Random.nextInt(255),
            Random.nextInt(255),
            Random.nextInt(255),
        )
        colors[index] = color

    }


    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(4)
    ) {
        items(colors.size) { index ->
            val color = colors[index] ?: Color.White
            Box(
                modifier = Modifier
                    .background(color)
                    .squareSize()
            ) {
            }
        }
    }
}


@Stable
fun Modifier.squareSize(
    position: Float = 0.5f,
): Modifier =
    this.then(
        when {
            position == 0.5f -> SquareSizeCenter
            else -> createSquareSizeModifier(position = position)
        }
    )

private val SquareSizeCenter = createSquareSizeModifier(position = 0.5f)

private class SquareSizeModifier(
    private val position: Float,
    inspectorInfo: InspectorInfo.() -> Unit,
) : LayoutModifier, InspectorValueInfo(inspectorInfo) {

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val maxSquare = min(constraints.maxWidth, constraints.maxHeight)
        val minSquare = max(constraints.minWidth, constraints.minHeight)
        val squareExists = (minSquare <= maxSquare)

        val resolvedConstraints = constraints
            .takeUnless { squareExists }
            ?: constraints.copy(maxWidth = maxSquare, maxHeight = maxSquare)

        val placeable = measurable.measure(resolvedConstraints)

        return if (squareExists) {
            val size = max(placeable.width, placeable.height)
            layout(size, size) {
                val x = ((size - placeable.width) * position).toInt()
                val y = ((size - placeable.height) * position).toInt()
                placeable.placeRelative(x, y)
            }
        } else {
            layout(placeable.width, placeable.height) {
                placeable.placeRelative(0, 0)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SquareSizeModifier) return false

        if (position != other.position) return false

        return true
    }

    override fun hashCode(): Int {
        return position.hashCode()
    }
}

@Suppress("ModifierFactoryExtensionFunction", "ModifierFactoryReturnType")
private fun createSquareSizeModifier(
    position: Float,
) =
    SquareSizeModifier(
        position = position,
        inspectorInfo = debugInspectorInfo {
            name = "squareSize"
            properties["position"] = position
        },
    )
