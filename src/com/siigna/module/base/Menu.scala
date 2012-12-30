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
import java.awt.{Shape, Color}

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
        Siigna.navigation = false
        'Interaction
      }
    },
    'Interaction -> {
      case MouseDown(_, MouseButtonRight, _) :: tail => {
        Siigna.navigation = true
        End
      }
      case MouseDown(p,_,_) :: tail => {
        println("view dist to p: "+View.center.distanceTo(p))
        // Examine if we have a hit!
        if ((View.center.distanceTo(p) > 100 && View.center.distanceTo(p) < 150) || // The icons on the radius
          (View.center.distanceTo(p) < 30)) { // Center-category

          var module: Option[Module] = None
          //if N E S or W is clicked
          if(View.center.distanceTo(p) > 100) {
            currentCategory.graph.get(direction(p)) foreach(_ match {
              case mc: MenuCategory => {
                
                currentCategory = mc
              }

              case MenuModule(instance, icon) =>  {
                module = instance
              }
            })
          }
          //if C is clicked
          else if(View.center.distanceTo(p) < 30) {
            currentCategory.graph.get(EventC) foreach(_ match {
              case mc: MenuCategory => currentCategory = mc
              println(mc.graph.head._1.rotation)
              case MenuModule(instance, icon) =>  {
                module = instance
              }
            })
          }

          if (module.isDefined) {
            //println("Sender om lidt END(module)")
            //println("Distance to center: " + View.center.distanceTo (p))  //Added to help bugfix: Menu fails
            // if a shape is drawn, and you afterwards zoom very far out  - can be deleted afterwards
            Siigna.navigation = true
            End(module.get)
          }
        } else {                                                   //Added to help bugfix; can be deletet afterwards
          //println("Mouse clicked outside active areas of menu")      //Added to help bugfix; can be deletet afterwards
          //println("Distance to center: " + View.center.distanceTo (p))  //Added to help bugfix; can be deletet afterwards
        }
      }
    }
  )

  /**
   * Paints the menu. If the menu is being initalized we multiply the transformationMatrix with a <code>distanceScale</code> to show an animation
   * of the icons, origining from the center.
   */
  override def paint(g : Graphics, transformation : TransformationMatrix) {
    var m = mousePosition
    val location = TransformationMatrix(View.center, 1).flipY
    val colorAttr = "Color" -> new Color(150, 150, 150)

    if ((View.center.distanceTo(m) > 100 && View.center.distanceTo(mousePosition) < 150) || // The icons on the radius
      (View.center.distanceTo(m) < 30)) { // Center-category
      //var module: Option[ModuleInstance] = None
      //if N E S or W is clicked
      if(View.center.distanceTo(m) > 100) {
        currentCategory.graph.get(direction(m)) foreach(_ match {
          case mc: MenuCategory => {
           println("MC: "+mc)
           g draw LineShape(View.center + mc.graph.head._1.vector * 130  - Vector2D(0,130),m)
           //drawFill(MenuIcons.EventIconFill, MenuIcons.eventColor, location concatenate TransformationMatrix(mc.graph.head._1.vector * 130 - Vector2D(0,130), 1))
          }

          case MenuModule(instance, icon) =>  {
            println("ICON")
          }
        })
      }
    }
    


    def drawFill (fillShape: Array[Vector2D], color : Color, transformation : TransformationMatrix) {
      val fillVector2Ds = fillShape.map(_.transform(transformation))
      val fillScreenX = fillVector2Ds.map(_.x.toInt).toArray
      val fillScreenY = fillVector2Ds.map(_.y.toInt).toArray
      g setColor color
      g.g.fillPolygon(fillScreenX,fillScreenY, fillVector2Ds.size)
    }

    def drawBackground(event : MenuEvent) {
      //draw the background colors)
      event match {
        case EventN =>  drawFill(MenuIcons.CategoryFill, MenuIcons.createColor, location.rotate(360))
        case EventE =>  drawFill(MenuIcons.CategoryFill, MenuIcons.propertiesColor, location.rotate(90))
        case EventS =>  drawFill(MenuIcons.CategoryFill, MenuIcons.modifyColor, location.rotate(180))
        case EventW =>  drawFill(MenuIcons.CategoryFill, MenuIcons.helpersColor, location.rotate(270))
        case EventC =>  drawFill(MenuIcons.CategoryFill, MenuIcons.fileColor, location.rotate(0))
        case _ =>
      }
    }

    def hightlightActive (event : MenuEvent) {
      //draw the background colors)
      event match {
        case EventN =>  drawFill(MenuIcons.EventIconFill, MenuIcons.eventColor, location concatenate TransformationMatrix(event.vector * 130 - Vector2D(0,130), 1))
        case EventE =>  drawFill(MenuIcons.CategoryFill, MenuIcons.propertiesColor, location.rotate(90))
        case EventS =>  drawFill(MenuIcons.CategoryFill, MenuIcons.modifyColor, location.rotate(180))
        case EventW =>  drawFill(MenuIcons.CategoryFill, MenuIcons.helpersColor, location.rotate(270))
        case EventC =>  drawFill(MenuIcons.CategoryFill, MenuIcons.fileColor, location.rotate(0))
        case _ =>
      }
    }


    currentCategory.graph.foreach(t => {
      drawBackground(t._1)
    })

    //a function to draw ICONS and ICON OUTLINES / BACKGROUNDS
    def drawElement(event: MenuEvent, element: MenuElement) {

      val t = location concatenate TransformationMatrix(event.vector * 130, 1)

      // Draw the icons - if the event is the Center we should only transform to the location
      event match {
        case EventC => element.icon.foreach(s => g.draw(s.transform(location)))
        case EventN => {
          drawFill(MenuIcons.EventIconFill, MenuIcons.eventColor, location concatenate TransformationMatrix(event.vector * 130 - Vector2D(0,130), 1))
          MenuIcons.NOutline.foreach(s => g.draw(s.transform(location).addAttributes(colorAttr)))
        }
        case EventE => {
          drawFill(MenuIcons.EventIconFill, MenuIcons.eventColor, location concatenate TransformationMatrix(event.vector * 130 - Vector2D(0,130), 1))
          MenuIcons.EOutline.foreach(s => g.draw(s.transform(location).addAttributes(colorAttr)))
        }
        case EventS => {
          drawFill(MenuIcons.EventIconFill, MenuIcons.eventColor, location concatenate TransformationMatrix(event.vector * 130 - Vector2D(0,130), 1))
          MenuIcons.SOutline.foreach(s => g.draw(s.transform(location).addAttributes(colorAttr)))
        }
        case EventW => {
          drawFill(MenuIcons.EventIconFill, MenuIcons.eventColor, location concatenate TransformationMatrix(event.vector * 130 - Vector2D(0,130), 1))
          MenuIcons.WOutline.foreach(s => g.draw(s.transform(location).addAttributes(colorAttr)))
        }
        case _ => {
          //draw a fill background for the icons
          drawFill(MenuIcons.IconFill, MenuIcons.itemColor, t.rotate(event.rotation+30)) //30 deg. add needed because of icon graphics misalignment.
          //draw the icons
          element.icon.foreach(s => g.draw(s.transform(t)))
          //draw an outline/background color around each drawing tool
          event.icon.foreach(s => g.draw(s.transform(t).addAttributes(colorAttr)))
        }
      }
    }
    //run the drawElement function on the currently active category:
    currentCategory.graph.foreach(t => {
      drawElement(t._1,t._2)
    })

    //Function to draw text in icons and as guides.
    def drawText(event : MenuEvent) {
      val position = Vector2D(View.center.x -18,View.center.y -4)
      def eventText(text : String, size : Int) {
        g.draw(TextShape(text,position - (event.vector * 130),size))
      }
      def circleOutline(e : MenuEvent) = g.draw(CircleShape(View.center,26).transform(TransformationMatrix(- e.vector * 130, 1)).addAttributes(colorAttr))

      event match {
        case EventN => {
          circleOutline(event)
          eventText("Create",9)
        }
        case EventE => {
          circleOutline(event)
          eventText("Properties",6)
        }
        case EventS => {
          circleOutline(event)
          eventText("Modify",9)
        }
        case EventW => {
          circleOutline(event)
          eventText("Helpers",8)
        }
        case EventC => {
          eventText("File",12)
        }
        case _ =>
      }
    }

    currentCategory.graph.foreach(t => {
      drawText(t._1)
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