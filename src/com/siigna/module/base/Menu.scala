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

package com.siigna.module.base

import com.siigna._
import com.siigna.module.base.radialmenu._
import java.awt.Color

/**
 * The menu module.
 * This module shows the menu as radial items and categories in 13 places (directions):
 * N, NNE, ENE, E, ESE, SSE, S, SSW, WSW, W, WNW and NNW. There is also a center (C) place.
 *
 * When initialized the menu
 */
class Menu extends Module {

  var center: Option[Vector2D] = None

  protected var currentCategory : MenuCategory = Menu.DummyCategory

  def stateMap: StateMap = Map(
    'Start -> {
      case e => {
        currentCategory = Menu.startCategory
        'Interaction
      }
    },
    'Interaction -> {
      case MouseDown(_, MouseButtonRight, _) :: tail => {
        End
      }
      case MouseDown(p,_,_) :: tail => {

        // Examine if we have a hit!
        if ((View.center.distanceTo(p) > 100 && View.center.distanceTo(p) < 150) || // The icons on the radius
            (View.center.distanceTo(p) < 30)) { // Center-category

          var module: Option[ModuleInstance] = None
          currentCategory.graph.get(direction(p)) foreach(_ match {
            case mc: MenuCategory => currentCategory = mc

            case MenuModule(instance, icon) =>  {
              module = Some(instance.copy)
            }
          })
          if (module.isDefined) {
            println("Sender om lidt END(module)")
            println("Distance to center: " + View.center.distanceTo (p))  //Added to help bugfix: Menu fails
            // if a shape is drawn, and you afterwards zoom very far out  - can be deletet afterwards
            End(module.get)
          }
        } else {                                                   //Added to help bugfix; can be deletet afterwards
        println("Mouse clicked outside active areas of menu")      //Added to help bugfix; can be deletet afterwards
        println("Distance to center: " + View.center.distanceTo (p))  //Added to help bugfix; can be deletet afterwards
        }
      }
    }
  )

  /**
   * Paints the menu. If the menu is being initalized we multiply the transformationMatrix with a <code>distanceScale</code> to show an animation
   * of the icons, origining from the center.
   */
  override def paint(g : Graphics, transformation : TransformationMatrix) {

    val location = TransformationMatrix(View.center, 1).flipY

    //a function to draw ICONS and ICON OUTLINES / BACKGROUNDS
    def drawElement(event: MenuEvent, element: MenuElement) {
      val t = location concatenate TransformationMatrix(event.vector * 130, 1)
      // Draw the icons - if the event is the Center we should only transform to the location
      event match {
        case EventC => element.icon.foreach(s => g.draw(s.transform(location)))
        case _ => {
          //draw an outline/background color around each drawing tool
          event.icon.foreach(s => g.draw(s.transform(t)))
          //draw the icons
          element.icon.foreach(s => g.draw(s.transform(t)))
        }
      }
    }

    //a function to draw TEXT inside and OUTLINES around each of the four categories N-E-S-W.
    //TODO: does not work in subcategories without clickable Event.

    def drawCategory(event: MenuEvent) {
      val position = Vector2D(View.center.x -18,View.center.y -4)
      def drawFill (fillShape: Array[Vector2D], color : Color, placement: TransformationMatrix, rotation : Int) {
      val fillVector2Ds = fillShape.map(_.transform(transformation.rotate(rotation)))
      val fillScreenX = fillVector2Ds.map(_.x.toInt).toArray
      val fillScreenY = fillVector2Ds.map(_.y.toInt).toArray

        g setColor color
        g.g.fillPolygon(fillScreenX,fillScreenY, fillVector2Ds.size)
      }

      // Draw the outlines - if the event is the Center draw nothing
      event match {
        //TODO: the C outline is not drawn in sub categories. Maybe a goto start category is not defined yet??
        case EventC => {
          g.draw(TextShape("File",position,10))
          MenuIcons.C.foreach(s => g.draw(s.transform(location)))
        }
        case EventN => {
          g.draw(TextShape("Create",position - (event.vector * 130),10))
          MenuIcons.NOutline.foreach(s => g.draw(s.transform(location)))
          drawFill(MenuIcons.EventFill, MenuIcons.createColor, location, 360)
        }
        case EventE => {
          g.draw(TextShape("Properties",position - (event.vector * 130),8))
          MenuIcons.EOutline.foreach(s => g.draw(s.transform(location)))
          drawFill(MenuIcons.EventFill, MenuIcons.propertiesColor, location, 90)
        }
        case EventS => {
          g.draw(TextShape("Modify",position - (event.vector * 130),10))
          MenuIcons.SOutline.foreach(s => g.draw(s.transform(location)))
          drawFill(MenuIcons.EventFill, MenuIcons.modifyColor, location, 180)
        }
        case EventW => {
          g.draw(TextShape("Helpers",position - (event.vector * 130),8))
          MenuIcons.WOutline.foreach(s => g.draw(s.transform(location)))
          drawFill(MenuIcons.EventFill, MenuIcons.helpersColor, location, 270)
        }
        case _ =>
      }
    }
    //run the drawElement and drawCategory function on the currently active category:
    currentCategory.graph.foreach(t => {
      drawElement(t._1,t._2)
      drawCategory(t._1)
    })
  }

  /**
   * Returns the direction in terms of MenuEvents, calculated from the angle
   * of a given point to the current center of the radial menu.
   */
  def direction(point : Vector2D) = {
    // First re-fit the angle to a positive y-axis (as opposed to the device coordinate system
    // that has a negative y-axis)
    val angle  = Vector2D(point.x - View.center.x, View.center.y - point.y).angle
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
}

/**
 * An immutable object used for values associated with the [[com.siigna.module.base.Menu]]
 * [[com.siigna.module.Module]]. The most important part of this object is to define the
 * [[com.siigna.module.base.radialmenu.MenuCategory]] to begin with. This can be overridden by anyone who
 * have designed their own sets of categories.
 */
object Menu {

  // A simple dummy category
  protected val DummyCategory = new MenuCategory {
    val color = Color.white
    val graph = Map[MenuEvent, MenuElement]()
    val parent = None
  }

  // The current active category
  var startCategory : MenuCategory = DummyCategory
}