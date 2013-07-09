/*
 * Copyright (c) 2008-2013. Siigna is released under the creative common license by-nc-sa. You are free
 *   to Share — to copy, distribute and transmit the work,
 *   to Remix — to adapt the work
 *
 * Under the following conditions:
 *   Attribution —  You must attribute the work to http://siigna.com in the manner specified by the author or licensor (but not in any way that suggests that they endorse you or your use of the work).
 *   Noncommercial — You may not use this work for commercial purposes.
 *   Share Alike — If you alter, transform, or build upon this work, you may distribute the resulting work only under the same or similar license to this one.
 *
 */

package com.siigna.module.base.radialmenu

import com.siigna._
import com.siigna.module.base.Menu

/**
 * A trait that keeps track of the state of a menu and the variables and logic behind.
 */
trait MenuLogic {

  var center : Vector2D = View.center
  var module: Option[Module] = None

  // The radius of the menu, inner and outer perifery and width of the perifery
  var radius = 130
  def innerPeriphery = radius * 0.76
  def peripheryWidth = radius * 0.24
  def outerPeriphery = radius * 1.24

  def mousePosition : Vector2D

  var activeCategory : MenuCategory = Menu.startCategory
  var activeDirection : Option[MenuElement] = None
  var currentCategory : MenuCategory = Menu.dummyCategory

  /**
   * Returns the direction in terms of MenuEvents, calculated from the angle
   * of a given point to the current center of the radial menu.
   */
  def direction(point : Vector2D) = {
    // First re-fit the angle to a positive y-axis (as opposed to the device coordinate system
    // that has a negative y-axis)
    val angle  = Vector2D(point.x - center.x, center.y - point.y).angle
    if      (angle >= 345 || angle < 15)   EventE
    else if (angle >= 15  && angle < 45)   EventENE
    else if (angle >= 45  && angle < 75)   EventNNE
    else if (angle >= 75  && angle < 105)  EventN
    else if (angle >= 105 && angle < 135)  EventNNW
    else if (angle >= 135 && angle < 165)  EventWNW
    else if (angle >= 165 && angle < 195)  EventW
    else if (angle >= 195 && angle < 225)  EventWSW
    else if (angle >= 225 && angle < 255)  EventSSW
    else if (angle >= 255 && angle < 285)  EventS
    else if (angle >= 285 && angle < 315)  EventSSE
    else if (angle >= 315 && angle < 345)  EventESE
    else                                  MenuEventNone
  }

  //calculate if the mouse is above items in the radial menu
  private def hit(p : Vector2D) : Boolean = {
    if((center.distanceTo(p) > innerPeriphery && center.distanceTo(p) < outerPeriphery) || // The icons on the radius
      (center.distanceTo(p) < peripheryWidth)) true // Center-category
    else false
  }

  /**
   * Initialize the menu module in the given category and centered around the given center-point.
   * @param category  The category to start in
   * @param center  The center of the menu-module
   */
  private def initialize(category : MenuCategory, center : Vector2D) {
    // Set the category
    currentCategory = category

    // Disable normal navigation
    Siigna.navigation = false

    // If the screen is too small to fit the menu, decrease the radius to a third of the size of the screen
    if (View.screen.height < radius * 2 || View.screen.width < radius * 2) {
      radius = (math.min(View.screen.height, View.screen.width) / 3).toInt
    }

    // Set the center-point, but make sure there is at least a radius distance from the border
    this.center = if (View.screen.distanceTo(center) < radius) {
      // Calculate the new x and y coordinates to have at least radius distance to the View.screen
      val c = peripheryWidth + 10// A constant to add some space
      val x = if (center.x - c < radius) radius + c
        else if (View.screen.xMax - center.x - c < radius) View.screen.xMax - radius - c
        else center.x
      val y = if (center.y - c < radius) radius + c
      else if (View.screen.yMax - center.y - c < radius) View.screen.yMax - radius - c
      else center.y
      Vector2D(x, y)
    } else center
  }

  def stateMap: StateMap = Map(
    'Start -> {
      case Start(_, c : MenuCategory) :: tail => {
        initialize(c, mousePosition)
        'Interaction
      }
      case e => {
        initialize(Menu.startCategory, mousePosition)
        'Interaction
      }
    },
    'Interaction -> {
      case MouseDown(_, MouseButtonRight, _) :: tail => {
        Siigna.navigation = true
        End
      }
      case MouseMove(p,_,_) :: tail => {
        // Examine if we have a hit!
        if(hit(p)) {

          //if N E S or W is active
          if(center.distanceTo(p) > innerPeriphery) {
            val dir = currentCategory.graph.get(direction(p))
            activeDirection = dir
            dir foreach(_ match {
              case mc: MenuCategory => {
                activeCategory = mc
              }

              case MenuModule(instance, icon) =>  {

                //reset the active category:
                activeCategory = Menu.dummyCategory
                module = instance
              }
            })
          }
          //if C is active
          else if(center.distanceTo(p) < peripheryWidth) {
            currentCategory.graph.get(EventC) foreach(_ match {
              case mc: MenuCategory => activeCategory = mc
              case MenuModule(instance, icon) =>  {
                module = instance
              }
            })
          }
        } else activeCategory = Menu.startCategory
      }
      case MouseDown(p,_,_) :: tail => {
        // if a shape is drawn, and you afterwards zoom very far out  - can be deleted afterwards
        currentCategory = activeCategory
        Siigna.navigation = true
        if(currentCategory.toString == "FileCategory") {
          activeCategory = Menu.startCategory
        }
        if(module.isDefined && activeCategory == Menu.dummyCategory) {
          End(module.get)
        }
      }
    }
  )

}
