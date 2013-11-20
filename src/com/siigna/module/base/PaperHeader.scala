/*
 * Copyright (c) 2008-2013, Selftie Software. Siigna is released under the
 * creative common license by-nc-sa. You are free
 *   to Share — to copy, distribute and transmit the work,
 *   to Remix — to adapt the work
 *
 * Under the following conditions:
 *   Attribution —   You must attribute the work to http://siigna.com in
 *                    the manner specified by the author or licensor (but
 *                    not in any way that suggests that they endorse you
 *                    or your use of the work).
 *   Noncommercial — You may not use this work for commercial purposes.
 *   Share Alike   — If you alter, transform, or build upon this work, you
 *                    may distribute the resulting work only under the
 *                    same or similar license to this one.
 *
 * Read more at http://siigna.com and https://github.com/siigna/main
 */

package com.siigna.module.base

import com.siigna._
import com.siigna.app.model.Drawing._

/**
 * shapes that make up the drawing header, used in module-init and export.
 */
object PaperHeader {

  //define placeholders that are updated when the addActionListener is activated.
  private var cachedHeaderFrame = calculateHeaderFrame
  private var cachedOpenness = calculateOpenness
  private var cachedScaleText = calculateFooterText
  //private var cachedScaleArrows = calculateFooterArrows
  private var cachedSizeArrows = calculatePaperArrows

  //send the functions to Drawing in mainline so that they are updated whenever an action is performed.
  addActionListener((_, _) => {
    cachedHeaderFrame = calculateHeaderFrame
    cachedOpenness = calculateOpenness
    cachedScaleText = calculateFooterText
    //cachedScaleArrows = calculateFooterArrows
    cachedSizeArrows = calculatePaperArrows
  })

  /**
   * We use cachedHeaderFrame because it is defined only when the addActionListener is active
   * @return PolyLineShape defining the header of the drawing
   */
  def headerFrame = cachedHeaderFrame

  /**
   * We use cachedOpenness because it is defined only when the addActionListener is active
   * @return PolyLineShape defining the level of openness of the drawing
   */
  def openness = cachedOpenness

  /**
   * We use cachedScaletext because it is defined only when the addActionListener is active
   * @return a TextShape with the current drawing scale format: (1:XXX)
   */
  def scaleText = cachedScaleText

  /**
   * We use cachedScaleArrows because it is defined only when the addActionListener is active
   * @return a PolylineShape showing where to click to change drawing scale
   */
  //def scaleArrows = cachedScaleArrows

  /**
   * We use cachedSizeArrows because it is defined only when the addActionListener is active
   * @return a PolylineShape showing where to click to change the paper size
   */
  def sizeArrows = cachedSizeArrows

  //horizontal headerborder
  def calculateHeaderFrame = {
    val b = Drawing.boundary
    val br = b.bottomRight
    val bl = b.bottomLeft
    val tr = b.topRight

    /*      tr
    *-----*
    |  p2 | ->  p2x = br.x + (bl.x - br.x)/2
    |  *--p1 -> p1y = br.y + 20
    |  |  |
    *--p3-*
   bl     br
    */

    val p1x = br.x
    val p1y = br.y + 8
    val p2x = if(b.height<b.width) br.x + (bl.x - br.x)/2 else br.x + (bl.x - br.x)/1.5
    val p2y = p1y
    val p3x = p2x
    val p3y = bl.y

    PolylineShape(Vector2D(p1x,p1y),Vector2D(p2x,p2y),Vector2D(p3x,p3y)).setAttribute("StrokeWidth" -> 0.1)
  }

  //a colored frame to indicate level of openness:
  //TODO: make color dynamic on the basis of drawing level of openness
  def calculateOpenness = {
    val b = Drawing.boundary
    val s = Siigna.paperScale
    val oversize1 = b.bottomLeft + Vector2D(-2 * s, -2 * s)
    val oversize2 = b.topRight + Vector2D(2 * s, 2 * s)
    val color = Drawing.attributes.char("Openness") match {
      case Some(Drawing.Openness.COPY) => Siigna.color("colorOpennessCopy")
      case Some(Drawing.Openness.PRIVATE) => Siigna.color("colorOpennessPrivate")
      case _ => Siigna.color("colorOpennessOpen")
    }
    PolylineShape(Rectangle2D(oversize1, oversize2)).setAttributes("Color" -> color.getOrElse("#444444".color), "StrokeWidth" -> 4.0)
  }

  // paper footer text
  def calculateFooterText = {
    val s = Siigna.paperScale
    val b = Drawing.boundaryScale
    val title = Drawing.attributes.string("title").getOrElse("Anonymous drawing") +
                Drawing.attributes.int("id").map(" #" +).getOrElse("")

    //paper size
    val size = {
      val a = Siigna.double("printFormatMin").get.toInt
      if(a == 296) "A3"
      else if(a == 420) "A2"
      else if(a == 593) "A1"
      else if(a == 840) "A0"
      else "A4"
    }


    TextShape(s"$title                 $size             Scale 1: $s", Drawing.boundary.bottomRight - Vector2D(8 * b, -1.7 * b), b * 3,Attributes("TextAlignment" -> Vector2D(1, 1)))
  }

  // paper footer scale adjustment arrows
  def calculateFooterArrows = {
    val b = Drawing.boundaryScale
    val br = Drawing.boundary.bottomRight

    val v1 = br + Vector2D(-1*b,4*b)
    val v2 = br + Vector2D(-2.5*b,6*b)
    val v3 = br + Vector2D(-4*b,4*b)

    val m1 = br + Vector2D(-2.5*b,4*b)
    val m2 = br + Vector2D(-2.5*b,3*b)

    val v4 = br + Vector2D(-1*b,3*b)
    val v5 = br + Vector2D(-2.5*b,1*b)
    val v6 = br + Vector2D(-4*b,3*b)
    PolylineShape(v1,v2,v3,v1,m1,m2,v4,v5,v6,v4).setAttributes("Color" -> "#444444".color, "StrokeWidth" -> 0.1)
  }

  // paper size adjustment arrows
  def calculatePaperArrows = {
    val b = Drawing.boundaryScale
    val br = Drawing.boundary.bottomRight

    val v1 = br + Vector2D(-41*b,4*b)
    val v2 = br + Vector2D(-42.5*b,6*b)
    val v3 = br + Vector2D(-44*b,4*b)

    val m1 = br + Vector2D(-42.5*b,4*b)
    val m2 = br + Vector2D(-42.5*b,3*b)

    val v4 = br + Vector2D(-41*b,3*b)
    val v5 = br + Vector2D(-42.5*b,1*b)
    val v6 = br + Vector2D(-44*b,3*b)
    PolylineShape(v1,v2,v3,v1,m1,m2,v4,v5,v6,v4).setAttributes("Color" -> "#444444".color, "StrokeWidth" -> 0.1)
  }

}

