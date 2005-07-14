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
 * Created on May 11, 2005
  */
package org.eclipse.mylar.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.mylar.ui.MylarUiPlugin;


/**
 * @author Mik Kersten
 */
public class ToggleGlobalInterestFilteringAction extends Action {
	
	public static final String ID = "org.eclipse.mylar.ui.interest.filter.global";
	
    public ToggleGlobalInterestFilteringAction() {
        super();
        setText("Apply Mylar to all views");
        setToolTipText("Apply Mylar to all views");
        setImageDescriptor(MylarImages.INTEREST_FILTERING);
        setActionDefinitionId(ID);
//        setChecked(MylarUiPlugin.getDefault().isGlobalFilteringEnabled());
    }
    
    @Override
    public void run() {
        setChecked(isChecked());
        MylarUiPlugin.getDefault().setGlobalFilteringEnabled(isChecked());
        MylarPlugin.getTaskscapeManager().notifyPostPresentationSettingsChange(IMylarContextListener.UpdateKind.UPDATE);
//        		MylarPlugin.getTaskscapeManager().notifyPostPresentationSettingsChange(
//        				ITaskscapeListener.UpdateKind.UPDATE);
    }
}

