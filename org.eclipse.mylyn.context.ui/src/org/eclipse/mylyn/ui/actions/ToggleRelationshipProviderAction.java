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
 * Created on Jul 27, 2004
  */
package org.eclipse.mylar.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.search.RelationshipProvider;
import org.eclipse.mylar.ui.MylarUiPlugin;


/**
 * @author Mik Kersten
 */
public class ToggleRelationshipProviderAction extends Action {

    private final String PREFIX = "org.eclipse.mylar.ui.relatedElements.providers";
    private String prefId = "org.eclipse.mylar.ui.relatedElements.providers";
    
    private RelationshipProvider provider;
    
	public ToggleRelationshipProviderAction(RelationshipProvider provider, ImageDescriptor image) {
		super();
        this.provider = provider;
        this.prefId = PREFIX + '.' + provider.getId().toString();
        
		setText(provider.getId().toString());
        setToolTipText(provider.getId().toString());
		setImageDescriptor(image);
		
		boolean checked= MylarUiPlugin.getPrefs().getBoolean(prefId); 
		valueChanged(checked, true); 
	}   
	 
	@Override
	public void run() {
		valueChanged(isChecked(), true);
	}
	
	private void valueChanged(final boolean on, boolean store) {
//        System.err.println(">>> changed: " + prefId);
		setChecked(on);
		if (store) MylarUiPlugin.getPrefs().setValue(prefId, on); //$NON-NLS-1$
		provider.setEnabled(on);
        MylarPlugin.getTaskscapeManager().updateSearchKindEnabled(provider, on);
	}
}
