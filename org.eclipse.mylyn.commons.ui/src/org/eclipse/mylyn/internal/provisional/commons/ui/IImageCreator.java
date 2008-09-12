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

import org.eclipse.swt.graphics.Image;

/**
 * Something capable of creating {@link Image images}
 * 
 * @author Willian Mitsuda
 */
// API-3.0: delete or expand
public interface IImageCreator {

	/**
	 * Creates a {@link Image} object it represents; the caller is responsible for disposing the returned {@link Image}
	 * 
	 * @return Image
	 */
	public Image createImage();

}