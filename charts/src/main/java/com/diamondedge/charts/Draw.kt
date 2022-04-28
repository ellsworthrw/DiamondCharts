/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

object Draw {

    private var colors: ArrayList<Long>? = null
    internal var defaultFont = Font.Default

    internal fun setFillAttributes(g: GraphicsContext, attr: GraphicAttributes?) {
        if (attr == null)
            return
        if (attr.fill) {
            g.color = attr.color
        }
    }

    internal fun setBorderAttributes(g: GraphicsContext, attr: GraphicAttributes?): Boolean {
        if (attr == null)
            return false
        if (attr.drawBorder) {
            g.color = attr.borderColor
            return true
        }
        return false
    }

    internal fun drawRect(g: GraphicsContext, x: Int, y: Int, width: Int, height: Int, attr: GraphicAttributes) {
        setFillAttributes(g, attr)
        g.fillRect(x, y, width, height)
        if (setBorderAttributes(g, attr)) {
            g.drawRect(x, y, width, height)
        }
    }

    /*
  static void drawVertBar( GraphicsContext g, int left, int bottom, int width, int height, GraphicAttributes attr )
  {
    int y = bottom - height;
    setFillAttributes( g, attr );
    g.fillRect( left, y, width, height );
    if( setBorderAttributes( g, attr ) )
    {
      g.drawRect( left, y, width, height );
    }
  }
  */

    fun drawCircle(g: GraphicsContext, xc: Int, yc: Int, sizeDp: Float, gradient: Gradient?, color: Long) {
        if (gradient != null) {
            val size = g.dpToPixel(sizeDp)
            val x = xc - size / 2
            val y = yc - size / 2
            g.applyGradient(gradient, 1f)
            g.fillOval(x, y, size, size)
        } else {
            drawSymbol(g, xc, yc, sizeDp, SymbolType.CIRCLE, color)
        }
    }

    /**
     * Draws the symbol with its center at xc, yc
     */
    fun drawSymbol(g: GraphicsContext, xc: Int, yc: Int, sizeDp: Float, symbol: SymbolType, color: Long) {
        // Note: size = 7 looks best, odd sizes looks better than even sizes
        // xc, yc is the center coord
        // x, y is the top left coord of bounding box
        val size = g.dpToPixel(sizeDp)
        val x = xc - size / 2
        val y = yc - size / 2
        drawSymbolAt(g, x, y, sizeDp, symbol, color)
    }

    /**
     * Draws the symbol inside the bounding box beginning at x, y
     */
    fun drawSymbolAt(g: GraphicsContext, x: Int, y: Int, sizeDp: Float, symbol: SymbolType, color: Long) {
        var size = g.dpToPixel(sizeDp)
        val xc = x + size / 2
        var yc = y + size / 2
        val h: Int
        val xPts: IntArray
        val yPts: IntArray
        g.color = color
        when (symbol) {
            SymbolType.SQUARE_OUTLINE -> g.drawRect(x, y, size - 1, size - 1)
            SymbolType.SQUARE -> g.fillRect(x, y, size, size)
            SymbolType.SQUARE_SMALL -> {
                size -= 2
                g.fillRect(xc - size / 2, yc - size / 2, size, size)
            }
            SymbolType.SMALL_DOT -> {
                size = 2
                g.fillRect(xc, yc - 1, size, size)
            }
            SymbolType.CIRCLE_SMALL -> {
                size -= 2
                g.fillOval(xc - size / 2, yc - size / 2, size, size)
            }
            SymbolType.CIRCLE -> g.fillOval(x, y, size, size)
            SymbolType.CIRCLE_OUTLINE -> g.drawOval(x, y, size - 1, size - 1)
            SymbolType.CROSS_DIAGONAL, SymbolType.ASTERISK -> {
                g.drawLine(x, y, x + size - 1, y + size - 1)
                g.drawLine(x, y + size - 1, x + size - 1, y)
                if (symbol != SymbolType.CROSS_DIAGONAL) {
                    g.drawLine(x, yc, x + size - 1, yc)
                    g.drawLine(xc, y, xc, y + size - 1)
                }
            }
            SymbolType.CROSS -> {
                g.drawLine(x, yc, x + size - 1, yc)
                g.drawLine(xc, y, xc, y + size - 1)
            }
            SymbolType.PLUS -> {
                // plus looks better at size = 9, so increase size by 2
                var xx = x - 1
                size += 2
                // draw hor line
                var yy = yc - 1
                while (yy < yc + 2) {
                    g.drawLine(xx, yy, xx + size - 1, yy)
                    yy++
                }
                // draw vert line
                yy = yc - size / 2
                xx = xc - 1
                while (xx < xc + 2) {
                    g.drawLine(xx, yy, xx, yy + size - 1)
                    xx++
                }
            }
            SymbolType.UP_ARROW -> {
                g.fillRect(xc - 1, yc + 2, 3, 3)
                yc -= 1
                size += 2
                val xx = xc - size / 2
                val yy = yc - size / 2 - 1  // move center 1 closer to base
                xPts = IntArray(3)
                yPts = IntArray(3)
                xPts[0] = xx
                xPts[1] = xc
                xPts[2] = xx + size - 1
                yPts[0] = yy + size - 1
                yPts[1] = yy
                yPts[2] = yy + size - 1
                g.fillPolygon(xPts, yPts, 3)
                if (symbol == SymbolType.UP_ARROW)
                    yc += 1
            }
            SymbolType.TRIANGLE -> {
                size += 2
                val x = xc - size / 2
                val y = yc - size / 2 - 1
                xPts = IntArray(3)
                yPts = IntArray(3)
                xPts[0] = x
                xPts[1] = xc
                xPts[2] = x + size - 1
                yPts[0] = y + size - 1
                yPts[1] = y
                yPts[2] = y + size - 1
                g.fillPolygon(xPts, yPts, 3)
                if (symbol == SymbolType.UP_ARROW)
                    yc += 1
            }
            SymbolType.DOWN_ARROW -> {
                g.fillRect(xc - 1, yc - 4, 3, 3)
                yc += 1
                size += 2
                val x = xc - size / 2
                val y = yc - size / 2 + 2  // move center 1 closer to base
                xPts = IntArray(3)
                yPts = IntArray(3)
                xPts[0] = x
                xPts[1] = xc
                xPts[2] = x + size - 1
                yPts[0] = y
                yPts[1] = y + size - 1
                yPts[2] = y
                g.fillPolygon(xPts, yPts, 3)
                if (symbol == SymbolType.DOWN_ARROW)
                    yc -= 1
            }
            SymbolType.TRIANGLE_DOWN -> {
                size += 2
                val x = xc - size / 2
                val y = yc - size / 2 + 2
                xPts = IntArray(3)
                yPts = IntArray(3)
                xPts[0] = x
                xPts[1] = xc
                xPts[2] = x + size - 1
                yPts[0] = y
                yPts[1] = y + size - 1
                yPts[2] = y
                g.fillPolygon(xPts, yPts, 3)
                if (symbol == SymbolType.DOWN_ARROW)
                    yc -= 1
            }
            SymbolType.TRIANGLE_OUTLINE -> {
                val y = y - 1                    // move center 1 closer to base
                g.drawLine(x, y + size - 1, xc, y)
                g.drawLine(xc, y, x + size - 1, y + size - 1)
                g.drawLine(x + size - 1, y + size - 1, x, y + size - 1)
            }
            SymbolType.DIAMOND_OUTLINE -> {
                h = size - 2   // diamond looks better almost twice as tall
                g.drawLine(x, yc, xc, yc - h)
                g.drawLine(xc, yc - h, x + size - 1, yc)
                g.drawLine(x + size - 1, yc, xc, yc + h)
                g.drawLine(xc, yc + h, x, yc)
            }
            SymbolType.DIAMOND -> {
                h = size   // diamond looks better twice as tall
                xPts = IntArray(4)
                yPts = IntArray(4)
                xPts[0] = x
                xPts[1] = xc
                xPts[2] = x + size
                xPts[3] = xc
                yPts[0] = yc
                yPts[1] = yc - h
                yPts[2] = yc
                yPts[3] = yc + h
                g.fillPolygon(xPts, yPts, 4)
            }
            SymbolType.NONE -> {}
        }
    }

    fun getTileSize(
        g: GraphicsContext,
        symbol: Any? = null,
        text: String,
        leftMargin: Float = 4f,
        topMargin: Float = 4f,
        rightMargin: Float = 4f,
        bottomMargin: Float = 4f,
        gap: Float = 4f,
    ): Pair<Int, Int> {
        var width = g.dpToPixel(leftMargin) + g.dpToPixel(rightMargin)
        var height = g.fontMetrics.height
        width += g.stringWidth(text)
        if (symbol != null) {
            val (symbolWidth, symbolHeight) = g.getImageSize(symbol)
            width += symbolWidth + g.dpToPixel(gap)
            if (symbolHeight > height) {
                height = symbolHeight
            }
        }
        height += g.dpToPixel(topMargin) + g.dpToPixel(bottomMargin)
        return Pair(width, height)
    }

    fun drawTileCentered(g: GraphicsContext, xc: Int, yc: Int, symbol: Any? = null, text: String, isDarkTheme: Boolean) {
        val (width, height) = getTileSize(g, symbol, text)
        val x = xc - width / 2
        val y = yc - height / 2
        val bg = if (isDarkTheme) Color.darkDarkGray else Color.ltGray
        drawTile(g, x, y, width, height, symbol, text, bg, isDarkTheme)
    }

    fun drawTile(
        g: GraphicsContext,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        symbol: Any? = null,
        text: String,
        tileBgColor: Long,
        isDarkTheme: Boolean,
        leftMargin: Float = 4f,
        gap: Float = 4f,
    ) {
        drawTileOutline(g, x, y, width, height, tileBgColor, isDarkTheme)
        var xx = x + g.dpToPixel(leftMargin)
        if (symbol != null) {
            val (symbolWidth, symbolHeight) = g.getImageSize(symbol)
            g.drawImage(symbol, xx, y + (height - symbolHeight) / 2)
            xx += symbolWidth + g.dpToPixel(gap)
        }

        g.color = Color.defaultTextColor
        g.drawString(text, xx, y + g.fontMetrics.baseline + (height - g.fontMetrics.height) / 2)
    }

    fun drawTileOutline(
        g: GraphicsContext,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        tileBgColor: Long,
        isDarkTheme: Boolean,
        cornerRadius: Float = 12f
    ) {
        val shadowHeight = g.dpToPixel(shadowHeightDp)
        val tileRadius = g.dpToPixel(cornerRadius)

        val gradient = if (isDarkTheme) darKTileBorderGradient else tileBorderGradient
        gradient.bounds.set(x, y + shadowHeight / 2, width, height)
        g.applyGradient(gradient)
        g.setStroke(shadowHeightDp, StrokeStyle.Solid)
        g.drawRoundedRect(x, y + shadowHeight / 2, width, height, tileRadius, tileRadius)

        g.color = tileBgColor
        g.fillRoundedRect(x, y, width, height, tileRadius, tileRadius)
    }

    var shadowHeightDp = 2f

    var tileBorderGradient = Gradient(
        listOf(
            0f to Color.transparent,
            .5f to Color.transparent,
            .9f to Color.black20,
            1f to Color.black10,
        )
    )

    var darKTileBorderGradient = Gradient(
        listOf(
            0f to Color.transparent,
            .5f to Color.transparent,
            .9f to Color.black50,
            1f to Color.black20,
        )
    )

    /*
  public static int DOWNWARD_DIAGONAL_LINE = 0x1;
  public static int UPWARD_DIAGONAL_LINE = 0x2;
  public static int DIAGONAL_CROSS_LINES = DOWNWARD_DIAGONAL_LINE | UPWARD_DIAGONAL_LINE;
  public static int VERTICAL_LINE = 0x4;
  public static int HORIZONTAL_LINE = 0x8;
  public static int CROSS_LINES = VERTICAL_LINE | HORIZONTAL_LINE;

  public void drawPattern( Graphics g, Shape shape, int style )
  {
    g.setClip( shape );
    Rectangle r = shape.getBounds();
    drawPattern2( g, r.x, r.y, r.width, r.height, style );
  }

  public void drawPattern( Graphics g, int x, int y, int w, int h, int style )
  {
    g.clipRect( x, y, w, h );
    drawPattern2( g, x, y, w, h, style );
  }

  private void drawPattern2( Graphics g, int x, int y, int w, int h, int style )
  {
    g.clipRect( x, y, w, h );

    int left = x;
    int right = x + w;
    int bottom = y + h;
    if( (style & DIAGONAL_CROSS_LINES) > 0 )
    {
      if( (style & UPWARD_DIAGONAL_LINE) > 0 )
        left -= h;
      if( (style & DOWNWARD_DIAGONAL_LINE) > 0 )
        right += h;
      for( int i = left; i < right; i += 3 )
      {
        if( (style & UPWARD_DIAGONAL_LINE) > 0 )
          g.drawLine( i, y, i - h, bottom );

        if( (style & DOWNWARD_DIAGONAL_LINE) > 0 )
          g.drawLine( i, y, i + h, bottom );
      }
    }
    else
    {
      if( (style & VERTICAL_LINE) > 0 )
      {
        for( int i = left; i < right; i += 3 )
          g.drawLine( i, y, i, bottom );
      }
      if( (style & HORIZONTAL_LINE) > 0 )
      {
        for( int i = y; i < bottom; i += 3 )
          g.drawLine( x, i, right, i );
      }
    }
  }
*/

    @JvmStatic
    fun getColor(index: Int): Long {
        var c = Color.black
        var nColors = 0
        if (colors == null) {
            nColors = 12
            c = when (index % nColors) {
                0 -> Color.blue
                1 -> Color.cyan
                2 -> Color.green
                3 -> Color.yellow
                4 -> Color.orange
                5 -> Color.red
                6 -> Color.pink
                7 -> Color.magenta
                8 -> Color.purple
                9 -> Color.gray
                10 -> Color.brown
                else -> Color.black
            }
        } else {
            nColors = colors!!.size
            c = colors!![index % nColors]
        }
        //        if ((index / nColors) >= 1) {
        //            for (int i = index / nColors; i > 0; i--)
        //                c = c.darker();
        //        }
        return c
    }

    // allow later SDK to add a set of transluscent colors
    // or a different set of opaque colors
    //
    fun addColor(c: Long) {
        if (colors == null)
            colors = ArrayList()
        colors!!.add(c)
    }
}
