/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.lang.reflect.Method;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.internal.forms.widgets.FormUtil;

/**
 * @author Steffen Pingel
 */
public class CommonFormUtil {

	public static void disableScrollingOnFocus(ScrolledForm form) {
		form.setData(FormUtil.FOCUS_SCROLLING, Boolean.FALSE);
	}

	public static void ensureVisible(Control control) {
		ScrolledComposite form = FormUtil.getScrolledComposite(control);
		if (form != null) {
			FormUtil.ensureVisible(form, control);
		}
	}

	/**
	 * Programmatically expand the provided ExpandableComposite, using reflection to fire the expansion listeners (see
	 * bug#70358)
	 * 
	 * @param comp
	 */
	public static void setExpanded(ExpandableComposite comp, boolean expanded) {
		if (comp.isExpanded() != expanded) {
			Method method = null;
			try {
				method = ExpandableComposite.class.getDeclaredMethod("programmaticToggleState"); //$NON-NLS-1$
				method.setAccessible(true);
				method.invoke(comp);
			} catch (Exception e) {
				// ignore
			}
		}
	}

}
