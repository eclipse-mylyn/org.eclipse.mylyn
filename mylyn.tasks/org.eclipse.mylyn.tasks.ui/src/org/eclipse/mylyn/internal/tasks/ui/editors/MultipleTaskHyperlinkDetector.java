/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Sam Davis
 */
public class MultipleTaskHyperlinkDetector extends TaskHyperlinkDetector {

	@Override
	protected List<IHyperlink> detectHyperlinks(ITextViewer textViewer, String content, int index, int contentOffset) {
		List<IHyperlink> result = new ArrayList<IHyperlink>();
		TaskRepository currentRepository = getTaskRepository(textViewer);
		final IHyperlink[] currentRepositoryLinks = detectHyperlinks(currentRepository, content, index, contentOffset);
		if (currentRepositoryLinks != null && currentRepositoryLinks.length > 0) {
			result.addAll(Arrays.asList(currentRepositoryLinks));
			Set<Region> currentRepositoryRegions = new HashSet<Region>();
			for (IHyperlink link : currentRepositoryLinks) {
				currentRepositoryRegions.add(getRegion(link));
			}
			List<TaskRepository> otherRepositories = getTaskRepositories(textViewer);
			otherRepositories.remove(currentRepository);
			for (final TaskRepository repository : otherRepositories) {
				final IHyperlink[] links = detectHyperlinks(repository, content, index, contentOffset);
				if (links != null) {
					for (IHyperlink link : links) {
						// prevent highlighting text that is not already a link for the current repository
						if (currentRepositoryRegions.contains(getRegion(link))) {
							result.add(link);
						}
					}
				}
			}
		}
		if (result.isEmpty()) {
			return null;
		}
		return result;
	}

	protected Region getRegion(IHyperlink link) {
		if (link.getHyperlinkRegion() instanceof Region) {
			return (Region) link.getHyperlinkRegion();
		} else {
			return new Region(link.getHyperlinkRegion().getOffset(), link.getHyperlinkRegion().getLength());
		}
	}

	@Override
	protected List<TaskRepository> getTaskRepositories(ITextViewer textViewer) {
		return TasksUi.getRepositoryManager().getAllRepositories();
	}

}
