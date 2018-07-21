/*******************************************************************************
 * Copyright (c) 2012, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Sebastien Dubois (Ericsson) - Improvements for bug 400266
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.compare;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
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
import org.eclipse.swt.widgets.Composite;

/**
 * @author Steffen Pingel
 * @author Sebastien Dubois
 * @author Miles Parker
 */
public class ReviewItemSetCompareEditorInput extends ReviewItemCompareEditorInput {

	private final IReviewItemSet items;

	private DiffNode root;

	public ReviewItemSetCompareEditorInput(CompareConfiguration configuration, IReviewItemSet items,
			IFileItem selection, ReviewBehavior behavior) {
		super(configuration, behavior);
		this.items = items;
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
			IFileItem fileItem = (IFileItem) item;
			if (fileItem.getBase().getContent() != null) {
				FileItemNode node = new FileItemNode(behavior, fileItem, monitor);

				DiffNode parent = findNode(root, node.getPath());
				parent.add(node);
			}
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
			node.setName(node.getName() + "/" + child.getName()); //$NON-NLS-1$
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
			getCompareConfiguration().setLabelProvider(input, ((FileItemNode) input).getLabelProvider());
			//NOTE:  This solves the problem described in bug 402060, but causes a bad listener leak in AbstractTextEditor so we remove it for now
			//updateViewerConfig(contentViewer, (FileItemNode) input);
		}
		return contentViewer;
	}

	//NOTE:  This is a temporary hack to work around the problem described in bug 402060.  It should be removed when the bug is fixed
	protected void updateViewerConfig(Viewer aContentViewer, FileItemNode input) {
		if (aContentViewer instanceof TextMergeViewer) {
			final TextMergeViewer textMergeViewer = (TextMergeViewer) aContentViewer;
			try {
				final Class<TextMergeViewer> clazz = TextMergeViewer.class;
				Field declaredField = clazz.getDeclaredField("isConfigured"); //$NON-NLS-1$
				declaredField.setAccessible(true);
				declaredField.setBoolean(textMergeViewer, false);
				Method declaredMethod = clazz.getDeclaredMethod("updateContent", Object.class, Object.class, //$NON-NLS-1$
						Object.class);
				declaredMethod.setAccessible(true);
				declaredMethod.invoke(textMergeViewer, null, input.getLeft(), input.getRight());
			} catch (Throwable t) {
				//do nothing for now
			}
		}
	}
}
