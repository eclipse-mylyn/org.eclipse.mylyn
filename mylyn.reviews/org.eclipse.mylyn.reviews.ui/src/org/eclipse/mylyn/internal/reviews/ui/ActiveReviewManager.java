/*******************************************************************************
 * Copyright (c) 2014, 2015 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.ui.spi.editor.AbstractReviewTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.IFormPage;

public class ActiveReviewManager {

	private IReview review = null;

	private TaskEditor currentPart;

	private final List<IActiveReviewListener> reviewListeners = new ArrayList<>();

	private final IPartListener editorPartListener = new IPartListener() {
		@Override
		public void partOpened(IWorkbenchPart part) {
		}

		@Override
		public void partDeactivated(IWorkbenchPart part) {
		}

		@Override
		public void partClosed(IWorkbenchPart part) {
			if (part == currentPart) {
				currentPart = null;
				setReview(null);
			}
		}

		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
		}

		@Override
		public void partActivated(IWorkbenchPart part) {
			if (part instanceof TaskEditor editor && currentPart != part) {
				IFormPage page = editor.getActivePageInstance();
				if (page instanceof AbstractReviewTaskEditorPage) {
					currentPart = editor;
					AbstractReviewTaskEditorPage reviewPage = (AbstractReviewTaskEditorPage) page;
					setReview(reviewPage.getReview());
				}
			}
		}
	};

	private final IPageListener pageListener = new IPageListener() {

		private IWorkbenchPage activePage;

		@Override
		public void pageOpened(IWorkbenchPage page) {
		}

		@Override
		public void pageClosed(IWorkbenchPage page) {
			pageActivated(null);
		}

		@Override
		public void pageActivated(IWorkbenchPage page) {
			if (page != activePage) {
				if (activePage != null) {
					activePage.removePartListener(editorPartListener);
				}
				if (page != null) {
					page.addPartListener(editorPartListener);
					editorPartListener.partActivated(page.getActiveEditor());
				}
				activePage = page;
			}
		}
	};

	private final IWindowListener windowListener = new IWindowListener() {

		@Override
		public void windowOpened(IWorkbenchWindow window) {
		}

		@Override
		public void windowDeactivated(IWorkbenchWindow window) {
			window.removePageListener(pageListener);
		}

		@Override
		public void windowClosed(IWorkbenchWindow window) {
		}

		@Override
		public void windowActivated(IWorkbenchWindow window) {
			window.addPageListener(pageListener);
			pageListener.pageActivated(window.getActivePage());
		}
	};

	public ActiveReviewManager() {
		PlatformUI.getWorkbench().addWindowListener(windowListener);
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (activeWorkbenchWindow != null) {
			activeWorkbenchWindow.addPageListener(pageListener);
			windowListener.windowActivated(activeWorkbenchWindow);
		}
	}

	public void setReview(IReview review) {
		if (this.review != review) {
			this.review = review;
			if (review != null) {
				for (IActiveReviewListener reviewListener : reviewListeners) {
					reviewListener.reviewActivated(review);
				}
			} else {
				for (IActiveReviewListener reviewListener : reviewListeners) {
					reviewListener.reviewDeactivated();
				}
			}
		}
	}

	public void addReviewListener(IActiveReviewListener listener) {
		reviewListeners.add(listener);
	}

	public void removeReviewListener(IActiveReviewListener listener) {
		reviewListeners.remove(listener);
	}

	public IReview getReview() {
		return review;
	}

	public TaskEditor getCurrentPart() {
		return currentPart;
	}
}
