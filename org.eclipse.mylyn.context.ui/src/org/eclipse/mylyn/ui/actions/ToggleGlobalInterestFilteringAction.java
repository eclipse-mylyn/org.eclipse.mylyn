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
import org.eclipse.mylar.core.ITaskscapeListener;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.mylar.ui.MylarUiPlugin;


/**
 * @author Mik Kersten
 */
public class ToggleGlobalInterestFilteringAction extends Action {

    public ToggleGlobalInterestFilteringAction() {
        super();
        setText("Filter uninteresting");
        setToolTipText("Filter uninteresting");
        setImageDescriptor(MylarImages.FILTER_UNINTERESTING);
        setActionDefinitionId("org.eclipse.mylar.ui.interest.filtering");
        setChecked(MylarUiPlugin.getDefault().isGlobalFilteringEnabled());
    }
    
    @Override
    public void run() {
        setChecked(isChecked());
        MylarUiPlugin.getDefault().setGlobalFilteringEnabled(isChecked());
        MylarPlugin.getTaskscapeManager().notifyPostPresentationSettingsChange(ITaskscapeListener.UpdateKind.UPDATE);
//        		MylarPlugin.getTaskscapeManager().notifyPostPresentationSettingsChange(
//        				ITaskscapeListener.UpdateKind.UPDATE);
    }
}

