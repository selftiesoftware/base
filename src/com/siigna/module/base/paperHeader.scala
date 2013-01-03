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

class paperHeader {
  def unitX(times : Int) = Vector2D(times * Siigna.paperScale, 0)
  val boundary = Drawing.boundary // Get the boundary
  val br = boundary.bottomRight
  val bl = boundary.bottomLeft
  val headerHeight = scala.math.min(boundary.height, boundary.width) * 0.025 // Define header
  val scale = TextShape("Scale 1:"+ (Siigna.paperScale), unitX(-10), headerHeight * 0.55) // Paper scale
  val oversize1 = (boundary.bottomLeft + Vector2D(-2 * Siigna.paperScale, -2 * Siigna.paperScale))
  val getURL = TextShape(" ", Vector2D(0, 0), headerHeight * 0.7)  // Get URL
  val headerWidth  = (scale.boundary.width + getURL.boundary.width) * 1.2
  val oversize2 = (boundary.topRight + Vector2D(2 * Siigna.paperScale, 2 * Siigna.paperScale))
  def transformation(t: TransformationMatrix) : TransformationMatrix = t.concatenate(TransformationMatrix(boundary.bottomRight - Vector2D(headerWidth * 0.99, -headerHeight * 0.8), 1))

  //draw frame to indicate level of openness:
  def openness (t : TransformationMatrix) = PolylineShape(Rectangle2D(oversize1, oversize2)).transform(t).setAttributes("Color" -> new Color(0.25f, 0.85f, 0.25f, 0.20f), "StrokeWidth" -> 4.0)

  // horizontal headerborder
  def horizontal (t : TransformationMatrix) = LineShape(br + Vector2D(0,(6*(Siigna.paperScale))), Vector2D((br.x/2 + bl.x),br.y) + Vector2D(0,(6*(Siigna.paperScale)))).transform(t).setAttribute("StrokeWidth" -> 0.3)

  // vertical headerborder
  def vertical (t : TransformationMatrix) = LineShape(Vector2D((br.x/2 + bl.x),br.y), Vector2D((br.x/2 + bl.x),br.y) + Vector2D(0,(6*(Siigna.paperScale)))).transform(t).setAttribute("StrokeWidth" -> 0.3)

  // paperScale - TODO: letter width: 50% letter spacing: 200%

  //g draw separator
  //val seperator = (getURL.transform(transformation.translate(scale.boundary.topRight + unitX(4))))

  //TODO: draw title and ID
}

