/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench.forms;

import java.lang.reflect.Method;

import org.eclipse.mylyn.internal.commons.workbench.CommonsWorkbenchPlugin;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.internal.forms.widgets.FormUtil;

/**
 * @author Steffen Pingel
 * @since 3.7
 */
public class CommonFormUtil {

	public static FormColors getSharedColors() {
		return CommonsWorkbenchPlugin.getDefault().getFormColors(Display.getDefault());
	}

	public static void disableScrollingOnFocus(ScrolledForm form) {
		form.setData(FormUtil.FOCUS_SCROLLING, Boolean.FALSE);
	}

	public static void ensureVisible(Control control) {
		ScrolledComposite form = FormUtil.getScrolledComposite(control);
		if (form != null) {
			if (control instanceof StyledText) {
				// bug 299392: ensure that the caret is visible for styled text but avoid scrolling form if only a portion of the control is visible
				Point origin = FormUtil.getControlLocation(form, control);
				Point caretLocation = ((StyledText) control).getCaret().getLocation();
				origin.x += caretLocation.x;
				origin.y += caretLocation.y;
				FormUtil.ensureVisible(form, origin, new Point(20, 20));
			} else {
				FormUtil.ensureVisible(form, control);
			}
		}
	}

	/**
	 * Programmatically expand the provided ExpandableComposite, using reflection to fire the expansion listeners (see bug#70358)
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
