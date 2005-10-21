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
 * Created on Apr 7, 2005
  */
package org.eclipse.mylar.ui;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContextManager;


/**
 * This is a generic interest filter that can be applied to any StructuredViewer.
 * It figures out whether an object is interesting by getting it's handle from the 
 * corresponding structure bridge.
 * 
 * @author Mik Kersten
 */
public class InterestFilter extends ViewerFilter implements IPropertyChangeListener {

	private Object temporarilyUnfiltered = null;
	
	private String excludedMatches = null;
	
	public InterestFilter() {
		MylarUiPlugin.getPrefs().addPropertyChangeListener(this);
	}
	
	@Override
    public boolean select(Viewer viewer, Object parent, Object element) {
		try {
        	if (!(viewer instanceof StructuredViewer)) return true;
        	if (!containsMylarInterestFilter((StructuredViewer)viewer)) return true;
        	if (temporarilyUnfiltered != null && temporarilyUnfiltered.equals(parent)) return true;
        	
            IMylarElement node = null;
            if (element instanceof IMylarElement) {
                node = (IMylarElement)element;
            } else { 
                IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(element);
                if (!bridge.canFilter(element)) return true;    
                if (matchesExclusion(element, bridge)) return true;
                
                String handle = bridge.getHandleIdentifier(element);
                node = MylarPlugin.getContextManager().getElement(handle);
            }
            if (node != null) {
            	if (node.getDegreeOfInterest().isPredicted()) {
            		return false;
            	} else {
            		return node.getDegreeOfInterest().getValue() > MylarContextManager.getScalingFactors().getInteresting();
            	}
            } 
        } catch (Throwable t) {
        	MylarPlugin.log(t, "interest filter failed on viewer: " + viewer.getClass());
        } 
        return false;
    }   
	
	boolean testselect(Viewer viewer, Object parent, Object element) {
		try {
        	if (!(viewer instanceof StructuredViewer)) return true;
        	if (!containsMylarInterestFilter((StructuredViewer)viewer)) return true;
        	if (temporarilyUnfiltered != null && temporarilyUnfiltered.equals(parent)) return true;
        	
            IMylarElement node = null;
            if (element instanceof IMylarElement) {
                node = (IMylarElement)element;
            } else { 
                IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(element);
                if (!bridge.canFilter(element)) return true;    
                if (matchesExclusion(element, bridge)) return true;
                
                String handle = bridge.getHandleIdentifier(element);
                node = MylarPlugin.getContextManager().getElement(handle);
            }
            if (node != null) {
            	if (node.getDegreeOfInterest().isPredicted()) {
            		return false;
            	} else {
            		return node.getDegreeOfInterest().getValue() > MylarContextManager.getScalingFactors().getInteresting();
            	}
            } 
        } catch (Throwable t) {
        	MylarPlugin.log(t, "interest filter failed on viewer: " + viewer.getClass());
        } 
        return false;
	}
	
	private boolean matchesExclusion(Object element, IMylarStructureBridge bridge) {
		if (excludedMatches == null) return false;
		if (excludedMatches.equals("*")) return false;
		try {
			String name = bridge.getName(element);
			return name.matches(excludedMatches.replaceAll("\\*", ".*").replaceAll("\\.", "\\."));
		} catch (Throwable t) {
			return false;
		}
	}

	private boolean containsMylarInterestFilter(StructuredViewer viewer) {
		boolean found = false;
		for (int i = 0; i < viewer.getFilters().length; i++) {
			ViewerFilter filter = viewer.getFilters()[i];
			if (filter instanceof InterestFilter) found = true;
		}
		return found;
	}

	public void setTemporarilyUnfiltered(Object temprarilyUnfiltered) {
		this.temporarilyUnfiltered = temprarilyUnfiltered;
	}
	
	public void resetTemporarilyUnfiltered() {
		this.temporarilyUnfiltered = null;
	}

	public Object getTemporarilyUnfiltered() {
		return temporarilyUnfiltered;
	}

	public String getExcludedMatches() {
		return excludedMatches;
	}

	public void setExcludedMatches(String excludedMatches) {
		this.excludedMatches = excludedMatches;
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (MylarUiPlugin.INTEREST_FILTER_EXCLUSION.equals(event.getProperty())
			&& event.getNewValue() instanceof String) {

			excludedMatches = (String)event.getNewValue();
		}
	}
}
