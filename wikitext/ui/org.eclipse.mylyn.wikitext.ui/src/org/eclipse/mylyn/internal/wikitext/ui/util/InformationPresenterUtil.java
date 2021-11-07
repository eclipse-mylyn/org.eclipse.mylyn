/*******************************************************************************
 * Copyright (c) 2007, 2021 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.util;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.AbstractInformationControlManager;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.IInformationProviderExtension;
import org.eclipse.jface.text.information.IInformationProviderExtension2;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.mylyn.internal.wikitext.ui.editor.syntax.FastMarkupPartitioner;
import org.eclipse.mylyn.wikitext.ui.viewer.HtmlTextPresenter;
import org.eclipse.swt.widgets.Control;

/**
 * @author David Green
 */
public class InformationPresenterUtil {
	/**
	 * @see InformationPresenter#setSizeConstraints(int, int, boolean, boolean)
	 */
	public static class SizeConstraint {
		int horizontalWidthInChars;

		int verticalWidthInChars;

		boolean enforceAsMinimumSize;

		boolean enforceAsMaximumSize;

		/**
		 * @see InformationPresenter#setSizeConstraints(int, int, boolean, boolean)
		 */
		public SizeConstraint(int horizontalWidthInChars, int verticalWidthInChars, boolean enforceAsMinimumSize,
				boolean enforceAsMaximumSize) {
			super();
			this.horizontalWidthInChars = horizontalWidthInChars;
			this.verticalWidthInChars = verticalWidthInChars;
			this.enforceAsMinimumSize = enforceAsMinimumSize;
			this.enforceAsMaximumSize = enforceAsMaximumSize;
		}
	}

	@SuppressWarnings("deprecation")
	private static final class InformationProvider
			implements IInformationProvider, IInformationProviderExtension, IInformationProviderExtension2 {

		private final IRegion hoverRegion;

		private final Object hoverInfo;

		private final IInformationControlCreator controlCreator;

		InformationProvider(IRegion hoverRegion, Object info, IInformationControlCreator controlCreator) {
			this.hoverRegion = hoverRegion;
			this.hoverInfo = info;
			this.controlCreator = controlCreator;
		}

		@Override
		public IRegion getSubject(ITextViewer textViewer, int invocationOffset) {
			return hoverRegion;
		}

		@Override
		public String getInformation(ITextViewer textViewer, IRegion subject) {
			return hoverInfo.toString();
		}

		@Override
		public Object getInformation2(ITextViewer textViewer, IRegion subject) {
			return hoverInfo;
		}

		@Override
		public IInformationControlCreator getInformationPresenterControlCreator() {
			return controlCreator;
		}
	}

	private static final String DATA_INFORMATION_PRESENTER = InformationPresenterUtil.class.getName()
			+ "#informationPresenter"; //$NON-NLS-1$

	private static final String DATA_INFORMATION_CONTROL_CREATOR = InformationPresenterUtil.class.getName()
			+ "#informationControlCreator"; //$NON-NLS-1$

	/**
	 * Get an information presenter to present the provided HTML content. The returned presenter is ready for displaying
	 * the information, all that is left to do is call {@link InformationPresenter#showInformation()}.
	 *
	 * @param viewer
	 *            the viewer for which the information control should be created
	 * @param constraint
	 *            the size constraint
	 * @param toolBarManager
	 *            the tool bar manager, or null if there should be none
	 * @param htmlContent
	 *            the HTML content to be displayed by the information presenter.
	 * @return the presenter
	 */
	public static InformationPresenter getHtmlInformationPresenter(ISourceViewer viewer, SizeConstraint constraint,
			final ToolBarManager toolBarManager, String htmlContent) {
		Control control = viewer.getTextWidget();
		InformationPresenter presenter = (InformationPresenter) control.getData(DATA_INFORMATION_PRESENTER);

		// bug 270059: ensure that the size/positioning math works by specifying the offet of the
		// current selection.
		int offset = viewer.getSelectedRange().x;

		IInformationControlCreator informationControlCreator;
		if (presenter == null) {

			informationControlCreator = shell -> {
					return new DefaultInformationControl(shell, toolBarManager, new HtmlTextPresenter());
			};

			presenter = new InformationPresenter(informationControlCreator) {
				@Override
				public IInformationProvider getInformationProvider(String contentType) {
					IInformationProvider informationProvider = super.getInformationProvider(contentType);
					if (informationProvider == null) {
						informationProvider = super.getInformationProvider(IDocument.DEFAULT_CONTENT_TYPE);
					}
					return informationProvider;
				}
			};
			presenter.install(viewer);
			presenter.setAnchor(AbstractInformationControlManager.ANCHOR_BOTTOM);
			presenter.setMargins(6, 6); // default values from AbstractInformationControlManager

			presenter.setOffset(offset);

			presenter.install(viewer);
			final InformationPresenter informationPresenter = presenter;
			viewer.getTextWidget().addDisposeListener(e -> {
				try {
					informationPresenter.uninstall();
				} catch (Exception e2) {
				}
				informationPresenter.dispose();
			});
			control.setData(DATA_INFORMATION_PRESENTER, presenter);
			control.setData(DATA_INFORMATION_CONTROL_CREATOR, informationControlCreator);
		} else {
			informationControlCreator = (IInformationControlCreator) control.getData(DATA_INFORMATION_CONTROL_CREATOR);
			presenter.disposeInformationControl();
		}

		presenter.setSizeConstraints(constraint.horizontalWidthInChars, constraint.verticalWidthInChars,
				constraint.enforceAsMinimumSize, constraint.enforceAsMaximumSize);

		InformationProvider informationProvider = new InformationProvider(new org.eclipse.jface.text.Region(offset, 0),
				htmlContent, informationControlCreator);

		for (String contentType : FastMarkupPartitioner.ALL_CONTENT_TYPES) {
			presenter.setInformationProvider(informationProvider, contentType);
		}
		presenter.setInformationProvider(informationProvider, IDocument.DEFAULT_CONTENT_TYPE);

		return presenter;
	}
}
