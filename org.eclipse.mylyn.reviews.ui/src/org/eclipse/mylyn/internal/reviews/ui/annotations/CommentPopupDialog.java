/*******************************************************************************
 * Copyright (c) 2009 Atlassian and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlassian - initial API and implementation
 *     Guy Perron 423242: Add ability to edit comment from compare navigator popup
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.annotations;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.text.source.LineRange;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.internal.reviews.ui.IReviewActionListener;
import org.eclipse.mylyn.internal.reviews.ui.dialogs.CommentInputDialog;
import org.eclipse.mylyn.internal.reviews.ui.editors.parts.CommentPart;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.internal.core.model.Comment;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Popup to show the information about the annotation in
 * 
 * @author Shawn Minto
 * @author Guy Perron
 */
public class CommentPopupDialog extends PopupDialog implements IReviewActionListener {

	private static final int MAX_WIDTH = 500;

	private int maxWidth;

	private CommentAnnotationHoverInput annotationInput;

	private FormToolkit toolkit;

	private Composite composite;

	private Label focusLabel;

	private ScrolledComposite scrolledComposite;

	private CommentInformationControl informationControl;

	private IReviewItem reviewitem;

	private LineRange range;

	private static CommentPopupDialog currentPopupDialog;

	private CommentInputDialog currentCommentInputDialog = null;

	public final boolean isHoverActive;

	private int lineNumber = 0;

	public CommentPopupDialog(Shell parent, int shellStyle) {
		super(parent, shellStyle, false, false, false, false, false, null, null);
		isHoverActive = true;

	}

	public CommentPopupDialog(Shell parent, int shellStyle, boolean bHoverActive, IReviewItem reviewitm, LineRange range) {
		super(parent, shellStyle, false, false, false, false, false, null, null);
		this.isHoverActive = bHoverActive;
		this.reviewitem = reviewitm;
		this.range = range;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		toolkit = new FormToolkit(CommonFormUtil.getSharedColors());

		scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		toolkit.adapt(scrolledComposite);

		composite = toolkit.createComposite(scrolledComposite, SWT.NONE);
		composite.setLayout(new GridLayout());
		scrolledComposite.setContent(composite);

		return scrolledComposite;
	}

	public void dispose() {
		currentPopupDialog = null;

		close();
		toolkit.dispose();
	}

	public void setFocus() {
		getShell().forceFocus();

		if (focusLabel != null) {
			focusLabel.dispose();
		}

		if (composite.getChildren().length > 0) {
			composite.getChildren()[0].setFocus();
		}

		Point computeSize = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		if (computeSize.y > scrolledComposite.getSize().y) {
			scrolledComposite.setExpandVertical(false);
			composite.setSize(computeSize);
		}
	}

	public Point computeSizeHint() {
		int widthHint = MAX_WIDTH;
		if (maxWidth < widthHint) {
			widthHint = maxWidth;
		}

		return getShell().computeSize(widthHint, SWT.DEFAULT, true);
	}

	public void removeFocusListener(FocusListener listener) {
		currentCommentInputDialog = null;
		composite.removeFocusListener(listener);
	}

	public void addFocusListener(FocusListener listener) {
		composite.addFocusListener(listener);

	}

	public boolean isFocusControl() {
		return getShell().getDisplay().getActiveShell() == getShell();
	}

	public void removeDisposeListener(DisposeListener listener) {
		getShell().removeDisposeListener(listener);

	}

	public void addDisposeListener(DisposeListener listener) {
		getShell().addDisposeListener(listener);
	}

	public Rectangle getBounds() {
		return getShell().getBounds();
	}

	public Rectangle computeTrim() {
		return getShell().computeTrim(0, 0, 0, 0);
	}

	public void setSizeConstraints(int newMaxWidth, int newMaxHeight) {
		this.maxWidth = newMaxWidth;
	}

	public void setLocation(Point location) {
		getShell().setLocation(location);
	}

	public void setSize(int width, int height) {
		Point computeSize = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		if (computeSize.x > width) {
			width = computeSize.x;
		}
		getShell().setSize(width, height);
		scrolledComposite.setSize(width, height);
	}

	public void setInput(Object input) {
		if (input instanceof CommentAnnotationHoverInput) {
			this.annotationInput = (CommentAnnotationHoverInput) input;

			// clear the composite in case we are re-using it
			for (Control control : composite.getChildren()) {
				control.dispose();
			}

			currentPopupDialog = this;

			final Map<Integer, Comment> commentList = new HashMap<Integer, Comment>();

			int count = 0;
			for (CommentAnnotation annotation : annotationInput.getAnnotations()) {

				if ((reviewitem != null)
						&& reviewitem.getReview() != null
						&& reviewitem.getReview().getRepository() != null
						&& reviewitem.getReview().getRepository().getAccount() != null
						&& reviewitem.getReview()
								.getRepository()
								.getAccount()
								.toString()
								.compareTo(((Comment) annotation.getComment()).getAuthor().toString()) != 0
						&& ((Comment) annotation.getComment()).isDraft()) {
					continue;
				}

				CommentPart part = new CommentPart(annotation.getComment(), annotationInput.getBehavior());
				count++;

				commentList.put(new Integer(count), (Comment) annotation.getComment());
				part.hookCustomActionRunListener(this);
				Control control = part.createControl(composite, toolkit);
				toolkit.adapt(control, true, true);
			}

			composite.setBackground(toolkit.getColors().getBackground());

			scrolledComposite.layout(true, true);
			scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

			focusLabel = toolkit.createLabel(composite, ""); //$NON-NLS-1$
			focusLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(focusLabel);

			PixelConverter Pc = new PixelConverter(composite.getFont());

			final int avgHeight = Pc.convertHeightInCharsToPixels(1);

			focusLabel.getParent().addMouseTrackListener(mouseTrackAdapter(commentList, avgHeight));

		} else {
			input = null;
		}

	}

	private MouseTrackAdapter mouseTrackAdapter(final Map<Integer, Comment> commentList, final int avgHeight) {
		return new MouseTrackAdapter() {

			@Override
			public void mouseEnter(MouseEvent e) {
				if (!isHoverActive && currentCommentInputDialog == null) {

					Point size = getShell().getSize();
					dispose();
					Shell ashell = PlatformUI.getWorkbench().getDisplay().getActiveShell();

					currentCommentInputDialog = new CommentInputDialog(ashell, annotationInput.getBehavior(),
							reviewitem, range);

					currentCommentInputDialog.setComments(commentList);
					currentCommentInputDialog.create();
					currentCommentInputDialog.getShell().setText(
							NLS.bind(Messages.CommentInputDialog_LineNumber, getLineNumber()));

					// adjust size to display maximum of 15 lines, which means 5 comments
					if (annotationInput.getAnnotations().size() < 5) {
						size.y = size.y + (annotationInput.getAnnotations().size() * 3 * avgHeight);
					} else {
						size.y = size.y + (15 * avgHeight);
					}
					currentCommentInputDialog.getShell().setSize(size);
					currentCommentInputDialog.open();

				}
			}

			@Override
			public void mouseExit(MouseEvent e) {
				//ignore
			};
		};
	}

	public void actionAboutToRun(Action action) {
		close();
	}

	public void actionRan(Action action) {
		close();
	}

	public static CommentPopupDialog getCurrentPopupDialog() {
		return currentPopupDialog;
	}

	public void setInformationControl(CommentInformationControl crucibleInformationControl) {
		this.informationControl = crucibleInformationControl;
	}

	public CommentInformationControl getInformationControl() {
		return informationControl;
	}

	public void setLineNumber(int lineOfOffset) {
		lineNumber = lineOfOffset + 1;
	}

	public int getLineNumber() {
		return lineNumber;
	}

}
