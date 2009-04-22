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

package org.eclipse.mylyn.internal.provisional.commons.ui.editor;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.part.WorkbenchPart;

/**
 * A callback for modifying the title image of an editor. Clients that provide busy animations should implement this
 * interface and delegate to the respective methods in {@link WorkbenchPart}.
 * 
 * @author Shawn Minto
 * @see EditorBusyIndicator
 */
public interface IBusyEditor {

	/**
	 * Updates the title image of the editor to <code>image</code>.
	 * 
	 * @param image
	 *            the image
	 */
	public void setTitleImage(Image image);

	/**
	 * Returns the current title image of the editor.
	 */
	public Image getTitleImage();

}
