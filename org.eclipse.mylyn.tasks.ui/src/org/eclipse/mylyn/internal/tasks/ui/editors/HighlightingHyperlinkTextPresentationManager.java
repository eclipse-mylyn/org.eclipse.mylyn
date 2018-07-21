/*******************************************************************************
 * Copyright (c) 2007, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.custom.StyleRange;

/**
 * A manager that decorates hyperlinks with the hyperlink color.
 * 
 * @author Steffen Pingel
 */
public class HighlightingHyperlinkTextPresentationManager extends AbstractHyperlinkTextPresentationManager {

	@Override
	protected void decorate(StyleRange styleRange) {
		styleRange.foreground = JFaceResources.getColorRegistry().get(JFacePreferences.ACTIVE_HYPERLINK_COLOR);
	}

}
