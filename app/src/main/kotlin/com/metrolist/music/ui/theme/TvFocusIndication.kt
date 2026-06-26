/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.theme

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material3.ripple
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class TvFocusIndication(val strokeColor: Color) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        val rippleFactory = ripple()
        val rippleNode = rippleFactory.create(interactionSource)
        return TvFocusIndicationNode(interactionSource, rippleNode, strokeColor)
    }
}

private class TvFocusIndicationNode(
    private val interactionSource: InteractionSource,
    private val rippleNode: DelegatableNode,
    private val strokeColor: Color
) : DelegatingNode(), DrawModifierNode {

    private var isFocused = false
    private var isHovered = false

    init {
        delegate(rippleNode)
    }

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is FocusInteraction.Focus -> isFocused = true
                    is FocusInteraction.Unfocus -> isFocused = false
                    is HoverInteraction.Enter -> isHovered = true
                    is HoverInteraction.Exit -> isHovered = false
                }
                invalidateDraw()
            }
        }
    }

    override fun ContentDrawScope.draw() {
        // 1. Draw the actual content of the composable
        drawContent()

        // 2. Draw the standard Material 3 ripple effect
        if (rippleNode is DrawModifierNode) {
            with(rippleNode) {
                draw()
            }
        }

        // 3. Draw the Material You themed stroke border if focused or hovered
        if (isFocused || isHovered) {
            val strokeWidth = 3.dp.toPx()
            
            // If it is square and small, treat it as a circle (e.g. circular buttons)
            val isCircle = size.width == size.height && size.width < 120.dp.toPx()
            
            val cornerRadius = if (isCircle) {
                CornerRadius(size.width / 2, size.height / 2)
            } else {
                CornerRadius(8.dp.toPx(), 8.dp.toPx())
            }

            drawRoundRect(
                color = strokeColor,
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(
                    size.width - strokeWidth,
                    size.height - strokeWidth
                ),
                cornerRadius = cornerRadius,
                style = Stroke(width = strokeWidth)
            )
        }
    }
}
