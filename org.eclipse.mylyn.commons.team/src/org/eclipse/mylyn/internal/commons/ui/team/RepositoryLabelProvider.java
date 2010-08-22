/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui.team;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylyn.commons.repositories.RepositoryCategory;
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
