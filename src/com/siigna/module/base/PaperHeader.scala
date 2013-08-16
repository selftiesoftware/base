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

  //send the functions to Drawing in mainline so that they are updated whenever an action is performed.
  addActionListener((_, _) => {
    cachedHeaderFrame = calculateHeaderFrame
    cachedOpenness = calculateOpenness
    cachedScaleText = calculateFooterText
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

  //horizontal headerborder
  def calculateHeaderFrame = {
    val b = Drawing.boundary
    val s = Siigna.paperScale
    val br = b.bottomRight
    val bl = b.bottomLeft
    val pt1 = br + Vector2D(0,6*s)
    val pt2 = Vector2D(br.x/2 + bl.x,br.y) + Vector2D(0,6*s)
    val pt3 = Vector2D(br.x/2 + bl.x,br.y)
    PolylineShape(pt1,pt2,pt3).setAttribute("StrokeWidth" -> 0.3)
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

  // paper footer text - TODO: letter width: 50% letter spacing: 200%
  def calculateFooterText = {
    val s = Siigna.paperScale

    val title = Drawing.attributes.string("title").getOrElse("Anonymous drawing") +
                Drawing.attributes.int("id").map(" #" +).getOrElse("")

    TextShape(s"$title - Scale 1: $s", Drawing.boundary.bottomRight - Vector2D(5 * s, 0), s * 4,
      Attributes("TextAlignment" -> Vector2D(1, 1)))
  }
  //val getURL = TextShape(" ", Vector2D(0, 0), headerHeight * 0.7)  // Get URL

  //g draw separator
  //val seperator = (getURL.transform(transformation.translate(scale.boundary.topRight + unitX(4))))

  //TODO: draw title and ID
}

