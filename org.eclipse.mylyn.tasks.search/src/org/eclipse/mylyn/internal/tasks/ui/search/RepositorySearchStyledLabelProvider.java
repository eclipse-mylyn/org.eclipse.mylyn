/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Perforce - enhancements for bug 319469
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.search.internal.ui.text.DecoratingFileSearchLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.misc.StringMatcher;
import org.eclipse.ui.internal.misc.StringMatcher.Position;

/**
 * Repository search styled label provider. Based on {@link DecoratingFileSearchLabelProvider}.
 * 
 * @author Kevin Sawicki
 * @see DecoratingFileSearchLabelProvider
 */
public class RepositorySearchStyledLabelProvider extends DecoratingStyledCellLabelProvider implements
		IPropertyChangeListener, ILabelProvider {

	/**
	 * Color to use to decorate matches.
	 */
	public static final String HIGHLIGHT_BG_COLOR_NAME = "org.eclipse.jdt.ui.ColoredLabels.match_highlight"; //$NON-NLS-1$

	private static class PatternStyledLabelProvider extends StyledCellLabelProvider implements IStyledLabelProvider,
			IColorProvider, IFontProvider {

		private final SearchResultsLabelProvider labelProvider;

		private StringMatcher matcher = null;

		public PatternStyledLabelProvider(SearchResultsLabelProvider provider) {
			this.labelProvider = provider;
		}

		/**
		 * Set the pattern to highlight
		 * 
		 * @param pattern
		 */
		public void setPattern(String pattern) {
			if (pattern != null && pattern.length() > 0) {
				this.matcher = new StringMatcher(pattern, true, false);
			} else {
				this.matcher = null;
			}
		}

		public StyledString getStyledText(Object element) {
			StyledString styled = null;
			String label = this.labelProvider.getText(element);
			if (matcher == null || label.length() == 0) {
				styled = new StyledString(label);
			} else {
				styled = new StyledString();
				int start = 0;
				int end = 0;
				int length = label.length();
				Position position = matcher.find(label, start, length);
				while (position != null) {
					end = position.getStart();
					styled.append(label.substring(start, end));
					start = position.getEnd();
					styled.append(label.substring(end, start), DecoratingFileSearchLabelProvider.HIGHLIGHT_STYLE);
					position = matcher.find(label, start, length);
				}
				if (start > 0 && start < length) {
					styled.append(label.substring(start));
				}
			}
			return styled;
		}

		public Image getImage(Object element) {
			return this.labelProvider.getImage(element);
		}

		public Font getFont(Object element) {
			return this.labelProvider.getFont(element);
		}

		public Color getForeground(Object element) {
			return this.labelProvider.getForeground(element);
		}

		public Color getBackground(Object element) {
			return this.labelProvider.getBackground(element);
		}

	}

	private final ILabelProvider labelProvider;

	/**
	 * Create a new repository search styled label provider that wraps an {@link ILabelProvider}
	 * 
	 * @param labelProvider
	 * @param decorator
	 * @param decorationContext
	 */
	public RepositorySearchStyledLabelProvider(SearchResultsLabelProvider labelProvider, ILabelDecorator decorator,
			IDecorationContext decorationContext) {
		super(new PatternStyledLabelProvider(labelProvider), decorator, decorationContext);
		this.labelProvider = labelProvider;
	}

	/**
	 * @see org.eclipse.jface.viewers.StyledCellLabelProvider#initialize(org.eclipse.jface.viewers.ColumnViewer,
	 *      org.eclipse.jface.viewers.ViewerColumn)
	 */
	@Override
	public void initialize(ColumnViewer viewer, ViewerColumn column) {
		PlatformUI.getPreferenceStore().addPropertyChangeListener(this);
		JFaceResources.getColorRegistry().addListener(this);

		setOwnerDrawEnabled(PlatformUI.getPreferenceStore()
				.getBoolean(IWorkbenchPreferenceConstants.USE_COLORED_LABELS));

		super.initialize(viewer, column);
	}

	/**
	 * Get underyling label provider
	 * 
	 * @return label provider
	 */
	public ILabelProvider getLabelProvider() {
		return this.labelProvider;
	}

	/**
	 * @see org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		PlatformUI.getPreferenceStore().removePropertyChangeListener(this);
		JFaceResources.getColorRegistry().removeListener(this);
		this.labelProvider.dispose();
		super.dispose();
	}

	/**
	 * Set the pattern to highlight
	 * 
	 * @param pattern
	 */
	public void setPattern(String pattern) {
		((PatternStyledLabelProvider) getStyledStringProvider()).setPattern(pattern);
	}

	/**
	 * Refresh the labels on viewer associated with this label provider. This method must be called on the UI-thread.
	 */
	protected void refresh() {
		ColumnViewer viewer = getViewer();
		if (viewer != null) {
			boolean coloredLabels = PlatformUI.getPreferenceStore().getBoolean(
					IWorkbenchPreferenceConstants.USE_COLORED_LABELS);
			if (coloredLabels || coloredLabels != isOwnerDrawEnabled()) {
				setOwnerDrawEnabled(coloredLabels);
				viewer.refresh();
			}
		}
	}

	/**
	 * Schedule a refresh of this label provider. This method can be called from any thread.
	 */
	protected void scheduleRefresh() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				refresh();
			}
		});
	}

	/**
	 * @param event
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (IWorkbenchPreferenceConstants.USE_COLORED_LABELS.equals(property)
				|| HIGHLIGHT_BG_COLOR_NAME.equals(property)) {
			scheduleRefresh();
		}
	}

	/**
	 * Get text of element from underyling label provider
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		return this.labelProvider.getText(element);
	}

	/**
	 * Override preparation of style range to add border dot about highlight regions that don't have colors applied
	 * 
	 * @see org.eclipse.jface.viewers.StyledCellLabelProvider#prepareStyleRange(org.eclipse.swt.custom.StyleRange,
	 *      boolean)
	 */
	@Override
	protected StyleRange prepareStyleRange(StyleRange styleRange, boolean applyColors) {
		boolean addBorder = !applyColors && styleRange.background != null;
		styleRange = super.prepareStyleRange(styleRange, applyColors);
		if (addBorder) {
			styleRange.borderStyle = SWT.BORDER_DOT;
		}
		return styleRange;
	}

}
