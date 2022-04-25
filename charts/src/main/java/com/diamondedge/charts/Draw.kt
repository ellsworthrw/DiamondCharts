/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

object Draw {
    val NONE = 0
    val SQUARE = 1
    val SQUARE_OUTLINE = 2
    val CIRCLE = 3
    val CIRCLE_OUTLINE = 4
    val TRIANGLE = 5
    val TRIANGLE_OUTLINE = 6
    val DIAMOND = 7
    val DIAMOND_OUTLINE = 8
    val PLUS = 9
    val ASTERISK = 10
    val CROSS = 11
    val CROSS_DIAGONAL = 12
    val SQUARE_SMALL = 13
    val CIRCLE_SMALL = 14
    val UP_ARROW = 15
    val DOWN_ARROW = 16
    val TRIANGLE_DOWN = 17
    val SMALL_DOT = 18

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
            drawSymbol(g, xc, yc, sizeDp, Draw.CIRCLE, color)
        }
    }

    fun drawSymbol(g: GraphicsContext, xc: Int, yc: Int, size: Float, symbol: Int, color: Long) {
        var yc = yc
        var size = g.dpToPixel(size)
        // Note: size = 7 looks best, odd sizes looks better than even sizes
        // xc, yc is the center coord
        // x, y is the top left coord of bounding box
        var x = xc - size / 2
        var y = yc - size / 2
        val h: Int
        val xPts: IntArray
        val yPts: IntArray
        g.color = color
        //g.setColor( Color.blue );
        when (symbol) {
            SQUARE_OUTLINE -> g.drawRect(x, y, size - 1, size - 1)
            SQUARE -> g.fillRect(x, y, size, size)
            SQUARE_SMALL -> {
                size -= 2
                g.fillRect(xc - size / 2, yc - size / 2, size, size)
            }
            SMALL_DOT -> {
                size = 2
                g.fillRect(xc, yc - 1, size, size)
            }
            CIRCLE_SMALL -> {
                size -= 2
                g.fillOval(xc - size / 2, yc - size / 2, size, size)
            }
            CIRCLE -> g.fillOval(x, y, size, size)
            CIRCLE_OUTLINE -> g.drawOval(x, y, size - 1, size - 1)
            CROSS_DIAGONAL, ASTERISK -> {
                g.drawLine(x, y, x + size - 1, y + size - 1)
                g.drawLine(x, y + size - 1, x + size - 1, y)
                if (symbol != CROSS_DIAGONAL) {
                    g.drawLine(x, yc, x + size - 1, yc)
                    g.drawLine(xc, y, xc, y + size - 1)
                }
            }
            CROSS -> {
                g.drawLine(x, yc, x + size - 1, yc)
                g.drawLine(xc, y, xc, y + size - 1)
            }
            PLUS -> {
                // plus looks better at size = 9, so increase size by 2
                x--
                size += 2
                // draw hor line
                y = yc - 1
                while (y < yc + 2) {
                    g.drawLine(x, y, x + size - 1, y)
                    y++
                }
                // draw vert line
                y = yc - size / 2
                x = xc - 1
                while (x < xc + 2) {
                    g.drawLine(x, y, x, y + size - 1)
                    x++
                }
            }
            UP_ARROW -> {
                g.fillRect(xc - 1, yc + 2, 3, 3)
                yc -= 1
                size += 2
                x = xc - size / 2
                y = yc - size / 2 - 1  // move center 1 closer to base
                xPts = IntArray(3)
                yPts = IntArray(3)
                xPts[0] = x
                xPts[1] = xc
                xPts[2] = x + size - 1
                yPts[0] = y + size - 1
                yPts[1] = y
                yPts[2] = y + size - 1
                g.fillPolygon(xPts, yPts, 3)
                if (symbol == UP_ARROW)
                    yc += 1
            }
            TRIANGLE -> {
                size += 2
                x = xc - size / 2
                y = yc - size / 2 - 1
                xPts = IntArray(3)
                yPts = IntArray(3)
                xPts[0] = x
                xPts[1] = xc
                xPts[2] = x + size - 1
                yPts[0] = y + size - 1
                yPts[1] = y
                yPts[2] = y + size - 1
                g.fillPolygon(xPts, yPts, 3)
                if (symbol == UP_ARROW)
                    yc += 1
            }
            DOWN_ARROW -> {
                g.fillRect(xc - 1, yc - 4, 3, 3)
                yc += 1
                size += 2
                x = xc - size / 2
                y = yc - size / 2 + 2  // move center 1 closer to base
                xPts = IntArray(3)
                yPts = IntArray(3)
                xPts[0] = x
                xPts[1] = xc
                xPts[2] = x + size - 1
                yPts[0] = y
                yPts[1] = y + size - 1
                yPts[2] = y
                g.fillPolygon(xPts, yPts, 3)
                if (symbol == DOWN_ARROW)
                    yc -= 1
            }
            TRIANGLE_DOWN -> {
                size += 2
                x = xc - size / 2
                y = yc - size / 2 + 2
                xPts = IntArray(3)
                yPts = IntArray(3)
                xPts[0] = x
                xPts[1] = xc
                xPts[2] = x + size - 1
                yPts[0] = y
                yPts[1] = y + size - 1
                yPts[2] = y
                g.fillPolygon(xPts, yPts, 3)
                if (symbol == DOWN_ARROW)
                    yc -= 1
            }
            TRIANGLE_OUTLINE -> {
                y--                    // move center 1 closer to base
                g.drawLine(x, y + size - 1, xc, y)
                g.drawLine(xc, y, x + size - 1, y + size - 1)
                g.drawLine(x + size - 1, y + size - 1, x, y + size - 1)
            }
            DIAMOND_OUTLINE -> {
                h = size - 2   // diamond looks better almost twice as tall
                g.drawLine(x, yc, xc, yc - h)
                g.drawLine(xc, yc - h, x + size - 1, yc)
                g.drawLine(x + size - 1, yc, xc, yc + h)
                g.drawLine(xc, yc + h, x, yc)
            }
            DIAMOND -> {
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
        }
        /* draw center point for data for debugging
    g.setColor( Color.black );
    g.drawLine( xc, yc, xc, yc );
    */
    }

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
