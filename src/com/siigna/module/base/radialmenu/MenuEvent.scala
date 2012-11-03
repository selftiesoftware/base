/*
 * Copyright (c) 2012. Siigna is released under the creative common license by-nc-sa. You are free
 * to Share — to copy, distribute and transmit the work,
 * to Remix — to adapt the work
 *
 * Under the following conditions:
 * Attribution —  You must attribute the work to http://siigna.com in the manner specified by the author or licensor (but not in any way that suggests that they endorse you or your use of the work).
 * Noncommercial — You may not use this work for commercial purposes.
 * Share Alike — If you alter, transform, or build upon this work, you may distribute the resulting work only under the same or similar license to this one.
 */

package com.siigna.module.base.radialmenu

import com.siigna.app.model.shape.Shape
import com.siigna.app.model.shape.LineShape
import com.siigna.util.event.Event
import com.siigna.util.geom.Vector2D

/**
 * An overall wrapper of Menu events.
 * Every menu event contains an icon and a Vector2D, used to draw the icon in the
 * right direction.
 */
trait MenuEvent extends Event {
  def icon   : Traversable[Shape]
  def vector : Vector2D
}

case object EventC extends MenuEvent {
  lazy val icon   = MenuIcons.C
  lazy val symbol = 'C
  lazy val vector = Vector2D(0, 0).unit
}
case object EventE extends MenuEvent {
  lazy val icon   = MenuIcons.E
  lazy val symbol = 'E
  lazy val vector = Vector2D( 1, 0).unit
}
case object EventENE extends MenuEvent {
  lazy val icon   = MenuIcons.ENE
  lazy val symbol = 'ENE
  lazy val vector = Vector2D( 0.866, 0.5).unit
}
case object EventNNE extends MenuEvent {
  lazy val icon   = MenuIcons.NNE
  lazy val symbol = 'NNE
  lazy val vector = Vector2D(0.5, 0.866).unit
}
case object EventN extends MenuEvent {
  lazy val icon   = MenuIcons.N
  lazy val symbol = 'N
  lazy val vector = Vector2D(0, 1).unit
}
case object EventNNW extends MenuEvent {
  lazy val icon   = MenuIcons.NNW
  lazy val symbol = 'NNW
  lazy val vector = Vector2D( -0.5, 0.866).unit
}
case object EventWNW extends MenuEvent {
  lazy val icon   = MenuIcons.WNW
  lazy val symbol = 'WNW
  lazy val vector = Vector2D( -0.866, 0.5).unit
}
case object EventW extends MenuEvent {
  lazy val icon   = MenuIcons.W
  lazy val symbol = 'W
  lazy val vector = Vector2D( -1, 0).unit
}
case object EventWSW extends MenuEvent {
  lazy val icon   = MenuIcons.WSW
  lazy val symbol = 'WSW
  lazy val vector = Vector2D( -0.866, -0.5).unit
}
case object EventSSW extends MenuEvent {
  lazy val icon   = MenuIcons.SSW
  lazy val symbol = 'SSW
  lazy val vector = Vector2D( -0.5, -0.866).unit
}
case object EventS extends MenuEvent {
  lazy val icon   = MenuIcons.S
  lazy val symbol = 'S
  lazy val vector = Vector2D( 0, -1).unit
}
case object EventSSE extends MenuEvent {
  lazy val icon   = MenuIcons.SSE
  lazy val symbol = 'SSE
  lazy val vector = Vector2D( 0.5, -0.866).unit
}
case object EventESE extends MenuEvent {
  lazy val icon   = MenuIcons.ESE
  lazy val symbol = 'ESE
  lazy val vector = Vector2D( 0.866, -0.5).unit
}

case object MenuEventNone extends MenuEvent {
  lazy val icon   = Iterable(LineShape(Vector2D(0, 0), Vector2D(0, 0)))
  lazy val symbol = 'MenuEventNone
  lazy val vector = Vector2D(0, 0)
}