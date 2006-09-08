/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Feb 18, 2005
 */
package org.eclipse.mylar.internal.tasks.ui.views;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class TaskRepositoriesTableLabelProvider extends DecoratingLabelProvider implements ITableLabelProvider {

	public TaskRepositoriesTableLabelProvider(ILabelProvider provider, ILabelDecorator decorator) {
		super(provider, decorator);
	}

	public String getColumnText(Object object, int index) {
		switch (index) {
		case 0:
			return null;
		case 1:
			if (object instanceof TaskRepository) {
				TaskRepository repository = (TaskRepository) object;
				if (repository.getRepositoryLabel() != null && repository.getRepositoryLabel().length() > 0) {
					return repository.getRepositoryLabel();
				} else {
					return null;
				}
//				else {
//					return repository.getUrl();
//				}
			} else if (object instanceof AbstractRepositoryConnector) {
				return ((AbstractRepositoryConnector) object).getLabel();
			} else {
				return getText(object);
			}
		case 2:
			if (object instanceof TaskRepository) {
				TaskRepository repository = (TaskRepository) object;
				return repository.getUrl();
			} else if (object instanceof AbstractRepositoryConnector) {
				return ((AbstractRepositoryConnector) object).getLabel();
			} else {
				return getText(object);
			}
		}
		return null;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			return super.getImage(element);
		} else {
			return null;
		}
	}
}
