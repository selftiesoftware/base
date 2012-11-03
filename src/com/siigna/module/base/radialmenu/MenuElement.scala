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
import com.siigna.module.ModuleInstance

/**
 * An element inside the [[com.siigna.module.base.Menu]]. This can either be a
 * [[com.siigna.module.base.radialmenu.MenuCategory]] (normally positioned at north, south, east and west) or a
 * [[com.siigna.module.base.radialmenu.MenuModule]].
 *
 * MenuCategory is used to layout the visuals and behaviors of the menu and MenuModule is used to forward to
 * modules when clicked on.
 */
trait MenuElement {

  /**
   * The icon of the element, described as a number of [[com.siigna.app.model.shape.Shape]]s.
   * @return  A number of [[com.siigna.app.model.shape.Shape]]s.
   */
  def icon : Traversable[Shape]
}

/**
 * A MenuCategory is an item in the [[com.siigna.module.base.Menu]] that tells the menu what to paint and
 * what to do when the user moves around. Each implementation should write their own <code>graph</code> that
 * explains where in the menu other [[com.siigna.module.base.radialmenu.MenuElement]]s lie (categories or
 * modules) and what happens when they are clicked. The syntax for the map is:
 *
 * {{{
 *   Map[Event -> Module]
 *
 *   // For example
 *   val graph = Map(EventN -> MenuModule(Module('Line), MenuIcons.line))
 * }}}
 *
 * That would give a category that displays a nice icon at north (hopefully resembling a line), and that, when clicked
 * forward to the module named 'Line.
 */
trait MenuCategory extends MenuElement {
  /**
   * The color used for background-filling.
   */
  def color : java.awt.Color

  val icon = MenuIcons.C

  /**
   * The graph describing the action ([[com.siigna.module.base.radialmenu.MenuElement]]) to take given a certain
   * event [[com.siigna.module.base.radialmenu.MenuEvent]]. This is used by the [[com.siigna.module.base.Menu]] to
   * figure our which module or category to forward to.
   * @return  A map of [[com.siigna.module.base.radialmenu.MenuEvent]] linking to a
   *          [[com.siigna.module.base.radialmenu.MenuElement]].
   */
  def graph: Map[MenuEvent, MenuElement]

  /**
   * The parent of the category if any.
   */
  def parent : Option[MenuCategory]
}

/**
 * A MenuItem is an item in the [[com.siigna.module.base.Menu]]. It contains an instance of a
 * [[com.siigna.module.Module]] which the menu can forward to, if it has been clicked.
 * @param instance  The instance to start from the [[com.siigna.module.base.Menu]]
 * @param icon  The icon to draw in the [[com.siigna.module.base.Menu]]
 */
case class MenuModule(instance: ModuleInstance, icon: Traversable[Shape]) extends MenuElement
