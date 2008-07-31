/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.viewer;

import java.util.Iterator;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.mylyn.wikitext.ui.viewer.HtmlTextPresenter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;


/**
 * A text hover implementation that finds regions based on annotations, and
 * supports HTML markup in the tooltip string.
 * 
 * @author David Green
 *
 */
public class TextHover extends DefaultTextHover implements ITextHoverExtension {
	private final ISourceViewer sourceViewer;

	public TextHover(ISourceViewer sourceViewer) {
		super(sourceViewer);
		this.sourceViewer = sourceViewer;
	}

	@Override
	protected boolean isIncluded(Annotation annotation) {
		return true;
	}


	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		IAnnotationModel annotationModel = sourceViewer.getAnnotationModel();
		if (annotationModel != null) {
			int start = Integer.MAX_VALUE;
			int end = -1;
			Iterator<?> iterator = annotationModel.getAnnotationIterator();
			while (iterator.hasNext()) {
				Annotation next = (Annotation) iterator.next();
				Position position = annotationModel.getPosition(next);
				if (position.getOffset() <= offset && (position.getLength()+position.getOffset()) >= offset) {
					start = Math.min(start, position.getOffset());
					end = Math.max(end,position.getOffset()+position.getLength());
				}
			}
			if (start <= end && end > -1) {
				return new Region(start,end-start);
			}
		}
		return super.getHoverRegion(textViewer, offset);
	}

	/*
	 * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
	 */
	public IInformationControlCreator getHoverControlCreator() {
		return new IInformationControlCreator() {

			public IInformationControl createInformationControl(Shell parent) {

				String tooltipAffordanceString = null;
				try {
					tooltipAffordanceString = EditorsUI.getTooltipAffordanceString();
				} catch (Exception e) {
					// expected in a non-eclipse environment
				}

				return new DefaultInformationControl(parent, tooltipAffordanceString, new HtmlTextPresenter()) {

					@Override
					public void setLocation(Point location) {
						// prevent the location from being set to where the cursor is: otherwise the popup is displayed
						// and then hidden immediately.
						Point cursorLocation = Display.getCurrent().getCursorLocation();
						if (cursorLocation.y+12 >= location.y) {
							location.y = cursorLocation.y+13;
						}
						super.setLocation(location);
					}

				};
			}
		};
	}
}