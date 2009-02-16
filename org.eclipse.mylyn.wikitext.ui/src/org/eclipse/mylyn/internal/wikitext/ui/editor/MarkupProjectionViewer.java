/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.editor;

import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.mylyn.internal.wikitext.ui.editor.commands.ShowQuickOutlineCommand;
import org.eclipse.swt.widgets.Composite;

/**
 * extend the viewer to provide access to the reconciler and to provide quick outline capabilities
 */
public class MarkupProjectionViewer extends ProjectionViewer {

	/**
	 * Operation code for quick outline
	 */
	public static final int QUICK_OUTLINE = ShowQuickOutlineCommand.QUICK_OUTLINE;

	private IInformationPresenter outlinePresenter;

	public MarkupProjectionViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler,
			boolean showAnnotationsOverview, int styles) {
		super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);
	}

	public IReconciler getReconciler() {
		return fReconciler;
	}

	@Override
	public void doOperation(int operation) {
		if (operation == QUICK_OUTLINE && outlinePresenter != null) {
			outlinePresenter.showInformation();
			return;
		}
		super.doOperation(operation);
	}

	@Override
	public boolean canDoOperation(int operation) {
		if (operation == QUICK_OUTLINE && outlinePresenter != null) {
			return true;
		}
		return super.canDoOperation(operation);
	}

	@Override
	public void configure(SourceViewerConfiguration configuration) {
		super.configure(configuration);
		if (configuration instanceof MarkupSourceViewerConfiguration) {
			outlinePresenter = ((MarkupSourceViewerConfiguration) configuration).getOutlineInformationPresenter(this);
			outlinePresenter.install(this);
		}
	}
}