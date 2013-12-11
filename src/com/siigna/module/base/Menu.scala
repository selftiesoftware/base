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
import com.siigna.module.base.radialmenu._
import java.awt.Color

/**
 * The menu module.
 * This module shows the menu as radial items and categories in 13 places (directions):
 * N, NNE, ENE, E, ESE, SSE, S, SSW, WSW, W, WNW and NNW. There is also a center (C) place.
 */
class Menu extends Module with MenuLogic {

  /**
   * Paints the menu.
   */
  override def paint(g : Graphics, transformation : TransformationMatrix) {
    try {
    // The icons are made to fit a scale starting at 130,
    // so we need to adjust the zoom if the radius changes
    val scale = radius / 130.0
    val location = TransformationMatrix(center, scale).flipY
    val attr = "Color" -> new Color(160, 160, 160, 160)
    val colorAttr = "Color" -> new Color(60, 60, 60, 60)

    // Draws a fill shape
    def drawFill (fillShape: Array[Vector2D], color : Color, transformation : TransformationMatrix) {
      val fillVector2Ds = fillShape.map(_.transform(transformation))
      val fillScreenX = fillVector2Ds.map(_.x.toInt).toArray
      val fillScreenY = fillVector2Ds.map(_.y.toInt).toArray
      g setColor color
      g.AWTGraphics.fillPolygon(fillScreenX,fillScreenY, fillVector2Ds.size)
    }

    // Draws a filled circle
    def drawFillcircle (r : Int, color : Color, offsetVector : Vector2D) {
      g setColor color
      val c = center - Vector2D(25,25) + offsetVector
      g.AWTGraphics.fillOval(c.x.toInt,c.y.toInt,r,r)
    }

    def drawBackground(event : MenuEvent, element : MenuElement) {
      //draw the background colors)
      event match {
        case EventN => drawFill(MenuIcons.CategoryFill,  MenuIcons.createColor, location.rotate(360))
        case EventE => drawFill(MenuIcons.CategoryFill,  MenuIcons.propertiesColor, location.rotate(90))
        case EventS => drawFill(MenuIcons.CategoryFill,  MenuIcons.editColor, location.rotate(180))
        case EventW => drawFill(MenuIcons.CategoryFill,  MenuIcons.helpersColor, location.rotate(270))
        //case EventC => drawFill(MenuIcons.EventIconFill, MenuIcons.fileColor, location)

        case _ =>
      }
    }

    //a function to draw ICONS and ICON OUTLINES / BACKGROUNDS
    def drawElement(event: MenuEvent, element: MenuElement) {

      val t = location concatenate TransformationMatrix(event.vector * radius / scale, 1)

      //color of the four circular Category buttons
      val color = if(element == activeCategory) Color.white else new Color(250, 250, 250, 250)

      // Draw the Menu Category icons and white circular backgrounds.
      // If the event is the Center we should only transform to the location.
      event match {
        case EventN => {
          MenuIcons.NOutline.foreach(s => g.draw(s.transform(location).addAttributes(attr)))
          drawFillcircle(MenuIcons.EventIconFill, color, Vector2D(0,-radius))
        }
        case EventE => {
          MenuIcons.EOutline.foreach(s => g.draw(s.transform(location).addAttributes(attr)))
          drawFillcircle(MenuIcons.EventIconFill, color, Vector2D(radius,0))
        }
        case EventS => {
          MenuIcons.SOutline.foreach(s => g.draw(s.transform(location).addAttributes(attr)))
          drawFillcircle(MenuIcons.EventIconFill, color, Vector2D(0,radius))
        }
        case EventW => {
          MenuIcons.WOutline.foreach(s => g.draw(s.transform(location).addAttributes(attr)))
          drawFillcircle(MenuIcons.EventIconFill, color, Vector2D(-radius,0))
        }
        //case EventC => {
        //  drawFill(MenuIcons.EventIconFill, color, eventT)
        //  element.icon.foreach(s => g.draw(s.transform(location).addAttributes(colorAttr)))
        //}
        case _ => {
          if (direction(mousePosition) == event && center.distanceTo(mousePosition) > innerPeriphery) {
            drawFill(MenuIcons.IconFill, MenuIcons.highlightIcon, t.rotate(event.rotation+30))
          }
          //draw a fill background for the icons
          drawFill(MenuIcons.IconFill, MenuIcons.itemColor, t.rotate(event.rotation+30)) //30 deg. add needed because of icon graphics misalignment.
          //draw the icons
          element.icon.foreach(s => g.draw(s.transform(t)))
          //draw an outline/background color around each drawing tool
          event.icon.foreach(s => g.draw(s.transform(t).addAttributes(colorAttr)))
        }
      }
    }

    //Function to draw text in icons and as guides.
    def drawText(event : MenuEvent) {
      val position = Vector2D(center.x -18,center.y -4)
      def eventText(text : String, size : Int) {
        g.draw(TextShape(text,position - (event.vector * radius),size))
      }
      //draw a line around the four circular category icons
      def circleOutline(e : MenuEvent) {
        g.draw(CircleShape(center,25).transform(TransformationMatrix(- e.vector * radius, 1)).addAttributes("color" -> new Color(30, 30, 30, 30)))
      }

      event match {
        case EventN => {
          circleOutline(event)
          MenuIcons.createIcon.foreach(s => g.draw(s.transform(TransformationMatrix(center - Vector2D(0,radius)))))
          //eventText("Create",9)
        }
        case EventE => {
          circleOutline(event)
          eventText("File",9)
        }
        case EventS => {
          circleOutline(event)
          eventText("Edit",9)
        }
        case EventW => {
          circleOutline(event)
          MenuIcons.helpersIcon.foreach(s => g.draw(s.transform(TransformationMatrix(center - Vector2D(-radius,0)))))
          //eventText("Helpers",9)
        }
        //case EventC => {
        //  eventText("File",12)
        //}
        case _ =>
      }
    }

    // Draw the large category backgrounds
    if (!currentCategory.graph.contains(EventE) &&
      (currentCategory.graph.contains(EventENE) || currentCategory.graph.contains(EventESE))) {
      drawFill(MenuIcons.CategoryFill, currentCategory.color, location.rotate(270))
      MenuIcons.EOutline.foreach(s => g.draw(s.transform(location).addAttributes(colorAttr)))
    }

    if (!currentCategory.graph.contains(EventN) &&
      (currentCategory.graph.contains(EventNNE) || currentCategory.graph.contains(EventNNW))) {
      drawFill(MenuIcons.CategoryFill, currentCategory.color, location)
      MenuIcons.NOutline.foreach(s => g.draw(s.transform(location).addAttributes(colorAttr)))
    }

    if (!currentCategory.graph.contains(EventW) &&
      (currentCategory.graph.contains(EventWNW) || currentCategory.graph.contains(EventWSW))) {
      drawFill(MenuIcons.CategoryFill, currentCategory.color, location.rotate(90))
      MenuIcons.WOutline.foreach(s => g.draw(s.transform(location).addAttributes(colorAttr)))
    }

    if (!currentCategory.graph.contains(EventS) &&
      (currentCategory.graph.contains(EventSSW) || currentCategory.graph.contains(EventSSE))) {
      drawFill(MenuIcons.CategoryFill, currentCategory.color, location.rotate(180))
      MenuIcons.SOutline.foreach(s => g.draw(s.transform(location).addAttributes(colorAttr)))
    }

    // Draw colored backgrounds
    currentCategory.graph.foreach(t => drawBackground(t._1, t._2))

    // Draw the currently active category recursively
    currentCategory.graph.foreach(t => drawElement(t._1,t._2))

    // Draw the text on top
    currentCategory.graph.foreach(t => drawText(t._1))

    // If no center is present, and a parent is available, draw a 'back' button
    if (!currentCategory.graph.contains(EventC) && currentCategory.parent.isDefined) {
      val parent = currentCategory.parent.get
      val color = if (parent == activeCategory) parent.color.brighter() else parent.color
      drawFillcircle(MenuIcons.EventIconFill, color,Vector2D(0,0))
      g.draw(CircleShape(center, peripheryWidth).addAttributes(colorAttr))
      g.draw(TextShape("back", Vector2D(center.x -15, center.y -6), 12))
    }

    // Finally draw the active selection
    def drawTooltip(text : String) {
      g draw TextShape(text, Vector2D(center.x, center.y + peripheryWidth * 2), 10,
        Attributes("TextAlignment" -> Vector2D(0.5, 0.5)))
    }

    activeDirection match {
      case Some(module : MenuModule) => if (module.instance.isDefined) drawTooltip(module.instance.get.toString)
      case _ =>
    }} catch {
      case _ : Throwable =>
    }
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
  val dummyCategory = new MenuCategory {
    val color = Color.white
    val graph = Map[MenuEvent, MenuElement]()
    val parent = None
  }

  // The current active category
  var startCategory : MenuCategory = dummyCategory
}