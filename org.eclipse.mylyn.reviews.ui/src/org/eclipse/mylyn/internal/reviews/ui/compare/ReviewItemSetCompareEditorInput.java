/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.compare;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Steffen Pingel
 */
public class ReviewItemSetCompareEditorInput extends CompareEditorInput {

	private final IReviewItemSet items;

	private DiffNode root;

	private final IFileItem selection;

	private final ReviewBehavior behavior;

	public ReviewItemSetCompareEditorInput(CompareConfiguration configuration, IReviewItemSet items,
			IFileItem selection, ReviewBehavior behavior) {
		super(configuration);
		this.items = items;
		this.selection = selection;
		this.behavior = behavior;
		setTitle(items.getName());
	}

	@Override
	protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		if (root != null) {
			return root;
		}

		root = new DiffNode(Differencer.NO_CHANGE);
		for (IReviewItem item : items.getItems()) {
			if (!(item instanceof IFileItem)) {
				continue;
			}

			FileItemNode node = new FileItemNode((IFileItem) item);

			DiffNode parent = findNode(root, node.getPath());
			parent.add(node);
		}

		for (IDiffElement child : root.getChildren()) {
			if (child instanceof FileItemNode) {
				flattenTree((FileItemNode) child);
			}
		}

		return root;
	}

	private void flattenTree(FileItemNode node) {
		mergeChild(node);
		for (IDiffElement child : node.getChildren()) {
			if (child instanceof FileItemNode) {
				flattenTree((FileItemNode) child);
			}
		}
	}

	public void mergeChild(FileItemNode node) {
		if (node.getChildren().length == 1 && isDirectory(node.getChildren()[0])) {
			FileItemNode child = (FileItemNode) node.getChildren()[0];
			node.setName(node.getName() + "/" + child.getName());
			node.remove(child);
			for (IDiffElement element : child.getChildren()) {
				node.add(element);
			}
			mergeChild(node);
		}
	}

	private boolean isDirectory(IDiffElement element) {
		return element instanceof FileItemNode && ((FileItemNode) element).getFileItem() == null;
	}

	private DiffNode findNode(DiffNode root, IPath path) {
		if (path.segmentCount() == 1) {
			return root;
		}

		String name = path.segment(0);

		// try to find existing path segment
		IDiffElement child = root.findChild(name);
		if (child instanceof DiffNode) {
			return findNode((DiffNode) child, path.removeFirstSegments(1));
		}

		// create new path segment
		FileItemNode node = new FileItemNode(name);
		root.add(node);
		return findNode(node, path.removeFirstSegments(1));
	}

	@Override
	public Viewer findContentViewer(Viewer oldViewer, ICompareInput input, Composite parent) {
		Viewer contentViewer = super.findContentViewer(oldViewer, input, parent);
		if (input instanceof FileItemNode && ((FileItemNode) input).getFileItem() != null) {
			final ReviewCompareAnnotationSupport support = ReviewCompareAnnotationSupport.getAnnotationSupport(contentViewer);
			IFileItem item = ((FileItemNode) input).getFileItem();
			getCompareConfiguration().setLeftLabel(NLS.bind("{0}: {1}", item.getTarget().getRevision(), item.getName()));
			getCompareConfiguration().setRightLabel(NLS.bind("{0}: {1}", item.getBase().getRevision(), item.getName()));
			support.setReviewItem(item, behavior);
		}
		return contentViewer;
	}

}
