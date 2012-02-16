/*******************************************************************************
 * Copyright (c) 2012 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Sascha Scholz (SAP) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.egit;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylyn.internal.gerrit.ui.GerritImages;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.graphics.Image;

import com.google.gerrit.reviewdb.Project;

/**
 * @author Sascha Scholz
 */
public class GerritRepositorySearchPageLabelProvider extends LabelProvider {

	private Image gerritImage;

	private Image gitRepositoryImage;

	@Override
	public String getText(Object element) {
		if (element instanceof TaskRepository) {
			TaskRepository repository = (TaskRepository) element;
			return repository.getRepositoryLabel();
		}
		if (element instanceof Project) {
			Project project = (Project) element;
			return project.getName();
		}
		return super.getText(element);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof TaskRepository) {
			if (gerritImage == null) {
				gerritImage = GerritImages.GERRIT.createImage();
			}
			return gerritImage;
		}
		if (element instanceof Project) {
			if (gitRepositoryImage == null) {
				gitRepositoryImage = GerritImages.GIT_REPOSITORY.createImage();
			}
			return gitRepositoryImage;
		}
		return super.getImage(element);
	}

	@Override
	public void dispose() {
		if (gerritImage != null) {
			gerritImage.dispose();
		}
		if (gitRepositoryImage != null) {
			gitRepositoryImage.dispose();
		}
	}

}