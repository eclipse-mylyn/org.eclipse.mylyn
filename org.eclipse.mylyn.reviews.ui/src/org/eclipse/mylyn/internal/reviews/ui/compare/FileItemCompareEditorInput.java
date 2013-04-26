/*******************************************************************************
 * Copyright (c) 2009 Atlassian and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlassian - initial API and implementation
 *     Tasktop Technologies - improvements
 *     Sebastien Dubois (Ericsson) - Improvements for bug 400266
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.compare;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Steffen Pingel
 * @author Sebastien Dubois
 * @author Miles Parker
 */
public class FileItemCompareEditorInput extends CompareEditorInput {

	private final IFileItem file;

	private final ReviewBehavior behavior;

	public FileItemCompareEditorInput(CompareConfiguration configuration, IFileItem file, ReviewBehavior behavior) {
		super(configuration);
		this.file = file;
		this.behavior = behavior;
		setTitle(NLS.bind("Compare {0} {1} and {2}", new Object[] { file.getName(), file.getBase().getDescription(),
				file.getTarget().getDescription() }));
	}

	@Override
	protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		FileItemNode node = new FileItemNode(behavior, file, monitor);
		getCompareConfiguration().setLabelProvider(node, node.getLabelProvider());
		return node;
	}

	@Override
	public Viewer findContentViewer(Viewer oldViewer, ICompareInput input, Composite parent) {
		Viewer contentViewer = super.findContentViewer(oldViewer, input, parent);
		if (input instanceof FileItemNode) {
			ReviewCompareAnnotationSupport support = ReviewCompareAnnotationSupport.getAnnotationSupport(contentViewer);
			support.setReviewItem(((FileItemNode) input).getFileItem(), behavior);
		}
		return contentViewer;
	}

	public IFileItem getFile() {
		return file;
	}

	@Override
	protected void contentsCreated() {
		super.contentsCreated();
		//getAnnotationModelToAttach().focusOnComment();
	}
}