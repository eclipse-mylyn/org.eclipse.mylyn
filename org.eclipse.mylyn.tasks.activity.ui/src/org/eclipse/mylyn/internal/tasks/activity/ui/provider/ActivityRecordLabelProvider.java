/*******************************************************************************
 * Copyright (c) 2012 Timur Achmetow and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Timur Achmetow - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.activity.ui.provider;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.activity.core.ActivityEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

/**
 * @author Timur Achmetow
 */
@SuppressWarnings("restriction")
public class ActivityRecordLabelProvider extends LabelProvider implements IStyledLabelProvider {

	@Override
	public Image getImage(Object element) {
		return TasksUiPlugin.getDefault().getBrandingIcon(((ActivityEvent) element).getKind());
	}

	@Override
	public String getText(Object element) {
		return NLS.bind("Change {0}: {1}", ((ActivityEvent) element).getAttributes().get("taskId"), //$NON-NLS-1$ //$NON-NLS-2$
				((ActivityEvent) element).getSummary());
	}

	public StyledString getStyledText(Object element) {
		String text = getText(element);
		if (text != null) {
			StyledString styledString = new StyledString(text);
			String reviewText = NLS.bind("  ({0}, {1})", //$NON-NLS-1$
					((ActivityEvent) element).getAttributes().get("author"), ((ActivityEvent) element).getDate()); //$NON-NLS-1$

			styledString.append(reviewText, StyledString.DECORATIONS_STYLER);

			return styledString;
		}
		return new StyledString();
	}
}