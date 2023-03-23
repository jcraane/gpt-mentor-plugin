package dev.jamiecraane.gptmentorplugin.ui.common

import java.awt.*
import java.awt.geom.Arc2D
import java.awt.geom.Area
import java.awt.geom.Ellipse2D
import javax.swing.JComponent
import javax.swing.plaf.basic.BasicProgressBarUI

internal class ProgressCircleUI : BasicProgressBarUI() {
    override fun getPreferredSize(c: JComponent): Dimension {
        val d = super.getPreferredSize(c)
        val v = Math.max(d.width, d.height)
        d.setSize(v, v)
        return d
    }

    override fun paint(g: Graphics, c: JComponent) {
        val b = progressBar.insets // area for border
        val barRectWidth = progressBar.width - b.right - b.left
        val barRectHeight = progressBar.height - b.top - b.bottom
        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return
        }

        // draw the cells
        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        )
        g2.paint = progressBar.foreground
        val degree = 360 * progressBar.percentComplete
        val sz = Math.min(barRectWidth, barRectHeight).toDouble()
        val cx = b.left + barRectWidth * .5
        val cy = b.top + barRectHeight * .5
        val or = sz * .5
        val ir = or * .5 //or - 20;
        val inner: Shape = Ellipse2D.Double(cx - ir, cy - ir, ir * 2, ir * 2)
        val outer: Shape = Arc2D.Double(
            cx - or, cy - or, sz, sz, 90 - degree, degree, Arc2D.PIE
        )
        val area = Area(outer)
        area.subtract(Area(inner))
        g2.fill(area)
        g2.dispose()

        // Deal with possible text painting
        if (progressBar.isStringPainted) {
            paintString(g, b.left, b.top, barRectWidth, barRectHeight, 0, b)
        }
    }
}
