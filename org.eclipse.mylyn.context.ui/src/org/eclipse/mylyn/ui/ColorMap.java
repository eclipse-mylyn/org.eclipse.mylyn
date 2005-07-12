/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Dec 28, 2004
  */
package org.eclipse.mylar.ui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mik Kersten
 */
public class ColorMap {

    public enum GammaSetting {
        LIGHTEN,
        DARKEN,
        STANDARD
    }
    
    private GammaSetting gammaSetting = GammaSetting.STANDARD;
    
    public Color HYPERLINK  = new Color(Display.getDefault(), 0, 0, 255); // TODO: use theme?
        
    public Color PACKAGE_LABEL = new Color(Display.getDefault(), 100, 100, 100);
    public Color TEXT = new Color(Display.getDefault(), 0, 0, 0);
    public Color TRAIL_0 = new Color(Display.getDefault(), 50, 50, 50);
    public Color TRAIL_1 = new Color(Display.getDefault(), 90, 90, 90);
    public Color TRAIL_2 = new Color(Display.getDefault(), 130, 130, 130);
    public Color TRAIL_3 = new Color(Display.getDefault(), 170, 170, 170);
    public Color BORDER = new Color(Display.getDefault(), 255, 0, 0);
    public Color CURRENT = new Color(Display.getDefault(), 255, 154, 49);
    public Color DIRTY = new Color(Display.getDefault(), 255, 255, 190);
    public Color BACKGROUND_COLOR  = new Color(Display.getDefault(), 255, 255, 255); // TODO: use theme?
    public Color WHITE  = new Color(Display.getDefault(), 255, 255, 255); // TODO: use theme?
    public Color GRAY_DARK  = new Color(Display.getDefault(), 70, 70, 70); // TODO: use theme?
    public Color GRAY_MEDIUM  = new Color(Display.getDefault(), 105, 105, 105); // TODO: use theme?
    public Color GRAY_LIGHT  = new Color(Display.getDefault(), 145, 145, 145); // TODO: use theme?
    public Color GRAY_VERY_LIGHT  = new Color(Display.getDefault(), 200, 200, 200); // TODO: use theme?
    public Color ACTIVE  = new Color(Display.getDefault(), 255, 50, 50); // TODO: use theme?
    public Color RELATIONSHIP  = new Color(Display.getDefault(), 32, 104, 157);
    
    public Color HIGLIGHTER_RED_INTERSECTION = new Color(Display.getDefault(), 200, 0, 0);
    
    public Color HIGHLIGHTER_ORANGE_GRADIENT = new Color(Display.getDefault(), 214, 124, 53);
    public Color HIGLIGHTER_BLUE_GRADIENT = new Color(Display.getDefault(), 51, 153, 255);
    
    public Color HIGHLIGHTER_YELLOW = new Color(Display.getDefault(), 255, 238, 99);
    public Color PANTONE_PASTEL_YELLOW = new Color(Display.getDefault(), 244, 238, 175);
    public Color PANTONE_PASTEL_ROSE = new Color(Display.getDefault(), 254, 179, 190);
    public Color PANTONE_PASTEL_MAUVE = new Color(Display.getDefault(), 241, 183, 216);
    public Color PANTONE_PASTEL_PURPLE = new Color(Display.getDefault(), 202, 169, 222);
    public Color PANTONE_PASTEL_BLUE = new Color(Display.getDefault(), 120, 160, 250);
//    public Color PANTONE_PASTEL_BLUE = new Color(Display.getDefault(), 120, 210, 230);
    public Color PANTONE_PASTERL_GREEN = new Color(Display.getDefault(), 162, 231, 215);
    
//  public Color HIGLIGHTER_ROSE = new Color(Display.getDefault(), 240, 195, 196);
//  public Color HIGLIGHTER_YELLOW = new Color(Display.getDefault(), 255, 255, 102);
//  public Color HIGLIGHTER_BLUE = new Color(Display.getDefault(), 51, 153, 255);
//  public Color HIGLIGHTER_MAUVE = new Color(Display.getDefault(), 153, 102, 255);
    
    public GammaSetting getGammaSetting() {
        return gammaSetting;
    }
    
    public void setGammaSetting(GammaSetting gammaSetting) {
        this.gammaSetting = gammaSetting;
    }
    
    
   
}
