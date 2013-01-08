/*
 * Copyright (c) 2008-2013. Siigna is released under the creative common license by-nc-sa. You are free
 * to Share — to copy, distribute and transmit the work,
 * to Remix — to adapt the work
 *
 * Under the following conditions:
 * Attribution —  You must attribute the work to http://siigna.com in the manner specified by the author or licensor (but not in any way that suggests that they endorse you or your use of the work).
 * Noncommercial — You may not use this work for commercial purposes.
 * Share Alike — If you alter, transform, or build upon this work, you may distribute the resulting work only under the same or similar license to this one.
 */

package com.siigna.module.base

import com.siigna._
import java.awt.Color

/**
 * shapes that make up the drawing header, used in module-init and export.
 */

object paperHeader {
  
  // horizontal headerborder
  def headerFrame (t : TransformationMatrix, s : Int, b: Rectangle2D) = {
    val br = b.bottomRight
    val bl = b.bottomLeft
    val pt1 = br + Vector2D(0,(6*s))
    val pt2 = Vector2D((br.x/2 + bl.x),br.y) + Vector2D(0,(6*s))
    val pt3 = Vector2D((br.x/2 + bl.x),br.y)
    PolylineShape(pt1,pt2,pt3).transform(t).setAttribute("StrokeWidth" -> 0.3)
  }

  //a colored frame to indicate level of openness:
  //TODO: make color dynamic on the basis of drawing level of openness
  def openness (t : TransformationMatrix, scale: Int, b: Rectangle2D) = {
    val oversize1 = (b.bottomLeft + Vector2D(-2 * scale, -2 * scale))
    val oversize2 = (b.topRight + Vector2D(2 * scale, 2 * scale))
    PolylineShape(Rectangle2D(oversize1, oversize2)).transform(t).setAttributes("Color" -> new Color(0.25f, 0.85f, 0.25f, 0.20f), "StrokeWidth" -> 4.0)
  }
  // paper scale text - TODO: letter width: 50% letter spacing: 200%
  def scaleText (t : TransformationMatrix, s : Int, b: Rectangle2D) = {
    val br = b.bottomRight
    TextShape("Scale 1:"+ s, br + Vector2D(-30*s,5.1*s), s * 4).transform(t) // Paper scale
  }
  //val getURL = TextShape(" ", Vector2D(0, 0), headerHeight * 0.7)  // Get URL

  //g draw separator
  //val seperator = (getURL.transform(transformation.translate(scale.boundary.topRight + unitX(4))))

  //TODO: draw title and ID
}

