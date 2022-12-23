/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.repositories.ui;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylyn.commons.repositories.core.RepositoryCategory;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.internal.WorkbenchImages;

/**
 * @author Steffen Pingel
 */
public class RepositoryLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object object) {
		if (object instanceof RepositoryCategory) {
			return WorkbenchImages.getImage(ISharedImages.IMG_OBJ_FOLDER);
		}
		return null;
	}

	@Override
	public String getText(Object object) {
		if (object instanceof RepositoryCategory) {
			return ((RepositoryCategory) object).getLabel();
		}
		return null;
	}

}
