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

package org.eclipse.mylyn.tasks.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.swt.graphics.Image;

/**
 * @author Steffen Pingel
 * @since 3.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class LegendElement {

	/**
	 * @since 3.0
	 */
	public static LegendElement createTask(String label, ImageDescriptor overlay) {
		return new LegendElement(label, CommonImages.getCompositeTaskImage(TasksUiImages.TASK, overlay, false));
	}

	private final Image image;

	private final String label;

	private LegendElement(String label, Image image) {
		this.label = label;
		this.image = image;
	}

	/**
	 * @since 3.0
	 */
	public void dispose() {
	}

	/**
	 * @since 3.0
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * @since 3.0
	 */
	public String getLabel() {
		return label;
	}

}
