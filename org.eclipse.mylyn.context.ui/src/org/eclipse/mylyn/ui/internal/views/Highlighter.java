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

package org.eclipse.mylar.ui.internal.views;

import java.util.*;

import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;


/**
 * @author Mik Kersten
 */
public class Highlighter {
    
    private final int NUM_LEVELS = 40;
    private final List<Color> ELEVATIONS = new ArrayList<Color>();
	
    private String name;
    private Color core;
    private Color landmarkColor; 
    private Color highlightColor;
    private Color black = new Color(Display.getCurrent(), 0, 0, 0);
    private Color base = new Color(Display.getCurrent(), 255, 255, 255);
    private boolean isGradient;
    private boolean isIntersection;
    
    public boolean isIntersection() {
        return isIntersection;
    }
    public void setIntersection(boolean isIntersection) {
        this.isIntersection = isIntersection;
    }
    public Highlighter(String name, Color coreColor, boolean isGradient) {
        this.name = name;
        this.core = coreColor;
        this.isGradient = isGradient;
        initializeGradients();
        initializeHighlight();
        initializeLandmark();
    }
	public Highlighter(String attributes) {
		this.initializeFromString(attributes);
		initializeGradients();
        initializeHighlight();
        initializeLandmark();
	}
    
    public Color getHighlightColor() {
        return highlightColor;
    }
    
    public Color getLandmarkColor() {
        return landmarkColor;
    }
    
    public Color getHighlight(ITaskscapeNode info, boolean isLandmark) {
        if (info.getDegreeOfInterest().getValue() > 0) {
            if (isLandmark) {
                return landmarkColor;
            } else {
                if (isGradient) {
                    return mapDoiToElevation(info);
                } else {
                    return highlightColor;
                }
            }
        } else {
            return base;
        }
    }
    
    public Color mapDoiToElevation(ITaskscapeNode info) {
        if (info == null) return base;
        if (info.getDegreeOfInterest().getValue() < 0) return highlightColor;
        
        int step = 2;
        Color color = base;
        for (Iterator<Color> it = ELEVATIONS.iterator(); it.hasNext();) {
            color = it.next();
            if (info.getDegreeOfInterest().getValue() < step) return color;
            step += 2;
        }
        return color; // darkest color supported
//        return landmarkColor; 
    }
    
    private void initializeHighlight() {
        try {
            int redStep = (int)Math.ceil((core.getRed() + 2*base.getRed())/3);
            int greenStep = (int)Math.ceil((core.getGreen() + 2*base.getGreen())/3);
            int blueStep = (int)Math.ceil((core.getBlue() + 2*base.getBlue())/3);
            
            highlightColor = new Color(Display.getDefault(),
                    redStep,
                    greenStep,
                    blueStep);
        } catch (Throwable t) {
        	MylarPlugin.log(t, "highlighter init failed");
        }
    }

    private void initializeLandmark() {
        try { 
            int redStep = (int)Math.ceil((2*core.getRed() + black.getRed())/3);
            int greenStep = (int)Math.ceil((2*core.getGreen() + black.getGreen())/3);
            int blueStep = (int)Math.ceil((2*core.getBlue() + black.getBlue())/3);
            
            landmarkColor = new Color(Display.getDefault(),
                    redStep,
                    greenStep,
                    blueStep);
        } catch (Throwable t) {
        	MylarPlugin.log(t, "landmark init failed");
        }
    }
    
    private void initializeGradients() {
        try {
            int redStep = (int)Math.ceil((core.getRed() - base.getRed()) / NUM_LEVELS);
            int greenStep = (int)Math.ceil((core.getGreen() - base.getGreen()) / NUM_LEVELS);
            int blueStep = (int)Math.ceil((core.getBlue() - base.getBlue()) / NUM_LEVELS);

            int red = base.getRed() + redStep;
            int green = base.getGreen() + greenStep;
            int blue = base.getBlue() + blueStep;
            for (int i = 0; i < NUM_LEVELS-1; i++) {
                if (red > 255) red = 255;  // TODO: fix this mess   
                if (green > 255) green = 255;
                if (blue > 255) blue = 255;
                if (red < 0) red = 0;
                if (green < 0) green = 0;
                if (blue < 0) blue = 0;
                ELEVATIONS.add(new Color(Display.getDefault(), red, green, blue));
                red += redStep;
                blue += blueStep;
                green += greenStep;
            }       
        } catch (Throwable t) {
        	MylarPlugin.log(t, "gradients failed");
        }
    }
    
    @Override
    public String toString() {
        return name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Color getBase() {
        return base;
    }
    public void setBase(Color base) {
        this.base = base;
    }
    
    public static Color blend(List<Highlighter> highlighters, ITaskscapeNode info, boolean isLandmark) {
        int red = 0;
        int green = 0;
        int blue = 0;
        int num = highlighters.size();
        for (Iterator<Highlighter> it = highlighters.iterator(); it.hasNext();) {
            Highlighter highlighter = it.next();
            Color color = highlighter.getHighlight(info, isLandmark);
            red += color.getRed();
            green += color.getGreen();
            blue += color.getBlue();
        }
        return new Color(Display.getDefault(), red/num, green/num, blue/num);
    }
    public boolean isGradient() {
        return isGradient;
    }
    public void setGradient(boolean isGradient) {
        this.isGradient = isGradient;
    }
	public Color getCore() {
		return core;
	}
	public void setCore(Color core) {
		this.core = core;
		this.initializeGradients();
		this.initializeHighlight();
		this.initializeLandmark();
	}

	public String getType() {
		String res = "";
		if (this.isGradient) {
			res = new String("Gradient");
		} else if (this.isIntersection) {
			res = new String("Intersection");
		} else {
			res = new String("Solid");
		}
		return res;
	}
	
	public String externalizeToString() {
		String result = new String();
		Integer r = new Integer(this.core.getRed());
		Integer g = new Integer(this.core.getGreen());
		Integer b = new Integer(this.core.getBlue());
		result = r.toString() + ";" + g.toString() + ";" + b.toString() + ";"
			+ this.name + ";" + this.getType();		
		return result;
	}
	
	private void initializeFromString(String attributes) {
		String[] data = attributes.split(";");
		
		Integer r = new Integer(data[0]);
		Integer g = new Integer(data[1]);
		Integer b = new Integer(data[2]);
		this.core = new Color(Display.getCurrent(), r.intValue(), g.intValue(), b.intValue());
		this.name = data[3];
		if(data[4].compareTo("Gradient") == 0) {
			this.isGradient = true;
			this.isIntersection = false;
		} else if (data[4].compareTo("Intersection") == 0) {
			this.isGradient = false;
			this.isIntersection = true;
		} else {
			this.isGradient = false;
			this.isIntersection = false;
		}
	}
}
