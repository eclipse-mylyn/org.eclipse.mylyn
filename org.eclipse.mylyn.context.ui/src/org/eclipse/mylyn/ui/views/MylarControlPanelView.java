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
 * Created on Feb 4, 2005
  */
package org.eclipse.mylar.ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Mik Kersten
 */
public class MylarControlPanelView extends ViewPart {
   
    public MylarControlPanelView() {
        super();
    }

    @Override
    public void createPartControl(Composite parent) {
    	new MylarControlPanel(this, parent, SWT.NONE);
    }

    @Override
    public void setFocus() {
    	// don't need to set the focus
    }
   
} 