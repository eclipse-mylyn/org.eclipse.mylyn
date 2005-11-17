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
 * Created on Apr 18, 2005
  */
package org.eclipse.mylar.ui.views;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.IMylarObject;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.swt.graphics.Image;


/**
 * TODO: refactor edge stuff
 * 
 * @author Mik Kersten
 */
public class MylarDelegatingContextLabelProvider implements ILabelProvider {

	public static boolean qualifyNamesMode = false; // TODO: make non-static
	
    public static boolean isQualifyNamesMode() {
		return qualifyNamesMode;
	}
    
	public static void setQualifyNamesMode(boolean qualify) {
		qualifyNamesMode = qualify;
	}

	public Image getImage(Object element) {
    	if (element instanceof IMylarObject) {
    		ILabelProvider provider = MylarUiPlugin.getDefault().getContextLabelProvider(
    				((IMylarObject)element).getContentType());
            return provider.getImage(element);
        } else {
        	IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(element);
        	ILabelProvider provider = MylarUiPlugin.getDefault().getContextLabelProvider(bridge.getContentType());
        	if (provider != null) return provider.getImage(element);
        }
    	return null;
    }

    public String getText(Object object) {
    	if (object instanceof IMylarObject) {
    		IMylarObject element = (IMylarObject)object;
    		ILabelProvider provider = MylarUiPlugin.getDefault().getContextLabelProvider(element.getContentType());
        	if (MylarUiPlugin.getDefault().isDecorateInterestMode()) { // TODO: move
                return provider.getText(element) + " [" + element.getInterest().getValue() + "]"; 
            } else {
            	return provider.getText(element);
            }
        } else {
        	IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(object);
        	ILabelProvider provider = MylarUiPlugin.getDefault().getContextLabelProvider(bridge.getContentType());
        	if (provider != null) {
	        	if (MylarUiPlugin.getDefault().isDecorateInterestMode()) {
	        		IMylarElement element = MylarPlugin.getContextManager().getElement(bridge.getHandleIdentifier(object));
	        		return provider.getText(object) + " [" + element.getInterest().getValue() + "]"; 
	        	} else {
	        		return provider.getText(object);
	        	}
        	}
        }
    	return "? " + object;
    }

    public boolean isLabelProperty(Object element, String property) {
//    	 TODO: implement?
    	return false;
    }

    public void addListener(ILabelProviderListener listener) {
        // TODO: implement?
    }

    public void dispose() {
        // TODO: implement?        
    }
    
    public void removeListener(ILabelProviderListener listener) {
        // TODO: implement?
    }
}
