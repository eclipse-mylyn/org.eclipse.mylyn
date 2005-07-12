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
package org.eclipse.mylar.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasks.TaskListImages;

/**
 * @author Mik Kersten
 */
@Deprecated
public class ToggleIntersectionModeAction extends Action {

    public ToggleIntersectionModeAction() {
        super();
        setText("Intersect Tasskscapes");
        setToolTipText("Intersect Taskscapes");
//        setImageDescriptor(TaskListImages.);
        setActionDefinitionId("org.eclipse.mylar.ui.interest.intersection");
//        setChecked(MylarUiPlugin.getDefault().isGlobalFilteringEnabled());
    }
    
    @Override
    public void run() { 
        setChecked(!isChecked());
        MylarPlugin.log("not implemented", this);
//        MylarUiPlugin.getDefault().setIntersectionMode(isChecked());
//        MylarPlugin.getTaskscapeManager().notifyActivePresentationSettingsChange(ITaskscapeListener.UpdateKind.HIGHLIGHTER);
    }
}
