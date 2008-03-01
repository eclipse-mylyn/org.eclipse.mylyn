/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

public class EditorUtil {

	public static void setEnabledState(Composite composite, boolean enabled) {
		if (!composite.isDisposed()) {
			composite.setEnabled(enabled);
			for (Control control : composite.getChildren()) {
				control.setEnabled(enabled);
				if (control instanceof Composite) {
					setEnabledState(((Composite) control), enabled);
				}
			}
		}
	}

	/**
	 * Programmatically expand the provided ExpandableComposite, using reflection to fire the expansion listeners (see
	 * bug#70358)
	 * 
	 * @param comp
	 */
	public static void toggleExpandableComposite(boolean expanded, ExpandableComposite comp) {
		if (comp.isExpanded() != expanded) {
			Method method = null;
			try {
				method = comp.getClass().getDeclaredMethod("programmaticToggleState");
				method.setAccessible(true);
				method.invoke(comp);
			} catch (Exception e) {
				// ignore
			}
		}
	}

}
