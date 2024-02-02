/*******************************************************************************
 * Copyright (c) 2009, 2016 Atlassian and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Atlassian - initial API and implementation
 *     Guy Perron 423242: Add ability to edit comment from compare navigator popup
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.annotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.source.LineRange;
import org.eclipse.mylyn.commons.ui.ShellDragSupport;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.internal.reviews.ui.IReviewActionListener;
import org.eclipse.mylyn.internal.reviews.ui.editors.parts.CommentPart;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * Popup to show the information about the annotation in
 *
 * @author Shawn Minto
 * @author Guy Perron
 */
public class CommentPopupDialog extends PopupDialog implements IReviewActionListener {
	private static final int MAX_WIDTH = 500;

	private static final int MIN_HEIGHT = 70;

	private static final int ICON_BUFFER = 16;

	private Text helpText;

	private int maxWidth;

	private CommentAnnotationHoverInput annotationInput;

	private FormToolkit toolkit;

	private Composite composite;

	private ScrolledComposite scrolledComposite;

	private CommentInformationControl informationControl;

	private IReviewItem reviewItem;

	private LineRange range;

	private static CommentPopupDialog currentPopupDialog;

	private boolean editable;

	private final boolean isCommentNavigator;

	private List<IComment> commentList;

	private InlineCommentEditor commentEditor;

	/**
	 * Creates a dialog that displays review comments associated to a line in a given file
	 *
	 * @param parent
	 *            the parent shell of the dialog
	 * @param shellStyle
	 *            the SWT styles that will be applied to the dialog
	 * @param reviewitm
	 *            the item/file that is being viewed
	 * @param range
	 *            the line(s) that the comments are associated with
	 * @param isCommentNavigator
	 *            true if the dialog is being created via the Previous/Next comment action (and not via hover)
	 */
	public CommentPopupDialog(Shell parent, int shellStyle, IReviewItem reviewitm, LineRange range,
			boolean isCommentNavigator) {
		super(parent, shellStyle, false, false, false, false, false, null, null);
		reviewItem = reviewitm;
		this.range = range;
		editable = true;
		this.isCommentNavigator = isCommentNavigator;
	}

	/**
	 * Creates the scrolled composite and inner composite for the dialog
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		toolkit = new FormToolkit(CommonFormUtil.getSharedColors());

		scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
		GridLayoutFactory.fillDefaults().applyTo(scrolledComposite);
		GridDataFactory.fillDefaults().grab(false, false).applyTo(scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.getVerticalBar().setPageIncrement(4);
		toolkit.adapt(scrolledComposite);

		composite = toolkit.createComposite(scrolledComposite, SWT.NONE);
		composite.setLayout(new GridLayout());
		scrolledComposite.setContent(composite);
		new ShellDragSupport(composite);

		return scrolledComposite;
	}

	/**
	 * The default close action for the dialog (not force close)
	 */
	@Override
	public boolean close() {
		return close(false);
	}

	/**
	 * Closes the dialog
	 *
	 * @param force
	 *            true if the dialog should close under all conditions, false if the dialog should check for edited text in the editor
	 * @return true if the dialog has been closed, false otherwise
	 */
	protected boolean close(boolean force) {
		if (!force && hasEdits()) {
			return false;
		}
		if (editable) {
			InlineCommentEditor.removeFromEditMap(reviewItem.getId(), range.getStartLine());
		}
		return super.close();
	}

	/**
	 * Disposes the dialog
	 *
	 * @param force
	 *            true if the dialog should be disposed under all conditions, false if the dialog should check for edited text in the editor
	 */
	public void dispose(boolean force) {
		if (force || !hasEdits()) {
			currentPopupDialog = null;

			close(true);
			toolkit.dispose();
		}
	}

	/**
	 * Checks if there are any changes in the comment editor text
	 *
	 * @return true if there is a change with the editor text, false otherwise
	 */
	public boolean hasEdits() {
		return commentEditor != null && commentEditor.hasEdits();
	}

	/**
	 * Sets the focus of the dialog and adjusts the size of the inner composite
	 */
	public void setFocus() {
		getShell().forceFocus();

		if (composite.getChildren().length > 0) {
			composite.getChildren()[0].setFocus();
		}

		Point computeSize = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		if (computeSize.y > scrolledComposite.getSize().y) {
			scrolledComposite.setExpandVertical(true);
			composite.setSize(computeSize);
		}
	}

	/**
	 * Computes the maximum width of the dialog
	 *
	 * @return a {@link Point} containing the max width of the dialog and its default height
	 */
	public Point computeSizeHint() {
		int widthHint = MAX_WIDTH;
		if (maxWidth < widthHint) {
			widthHint = maxWidth;
		}

		return getShell().computeSize(widthHint, SWT.DEFAULT, true);
	}

	public void removeFocusListener(FocusListener listener) {
		composite.removeFocusListener(listener);
	}

	public void addFocusListener(FocusListener listener) {
		composite.addFocusListener(listener);
	}

	public void removeDisposeListener(DisposeListener listener) {
		getShell().removeDisposeListener(listener);
	}

	public void addDisposeListener(DisposeListener listener) {
		getShell().addDisposeListener(listener);
	}

	/**
	 * Checks to see if the active shell is the shell of this dialog
	 *
	 * @return true if the active shell is the dialog's shell, false otherwise
	 */
	public boolean isFocusControl() {
		return getShell().getDisplay().getActiveShell() == getShell();
	}

	protected Composite getScrolledComposite() {
		return scrolledComposite;
	}

	protected Composite getComposite() {
		return composite;
	}

	protected InlineCommentEditor getCommentEditor() {
		return commentEditor;
	}

	protected IReviewItem getReviewItem() {
		return reviewItem;
	}

	protected LineRange getRange() {
		return range;
	}

	protected CommentAnnotationHoverInput getAnnotationInput() {
		return annotationInput;
	}

	protected Text getHelpText() {
		return helpText;
	}

	protected boolean getEditable() {
		return editable;
	}

	protected FormToolkit getToolkit() {
		return toolkit;
	}

	public Rectangle getBounds() {
		return getShell().getBounds();
	}

	public Rectangle getMonitorArea() {
		return getShell().getMonitor().getClientArea();
	}

	public Rectangle computeTrim() {
		return getShell().computeTrim(0, 0, 0, 0);
	}

	public void setSizeConstraints(int newMaxWidth, int newMaxHeight) {
		maxWidth = newMaxWidth;
	}

	/**
	 * Sets the dialog's shell to a position in the monitor (constrained by the size of the user's monitor)
	 *
	 * @param location
	 *            the desired location to place the dialog
	 */
	public void setLocation(Point location) {
		Rectangle bounds = getShell().getBounds();
		Rectangle monitorBounds = getMonitorArea();
		// ensure the popup fits on the shell's monitor
		bounds.x = constrain(location.x, monitorBounds.x, monitorBounds.x + monitorBounds.width - bounds.width);
		bounds.y = constrain(location.y, monitorBounds.y, monitorBounds.y + monitorBounds.height - bounds.height);

		getShell().setLocation(new Point(bounds.x, bounds.y));
	}

	private int constrain(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	/**
	 * Recomputes and sets the size of the dialog (and its shell and composites) to fit its contents
	 */
	protected void recomputeSize() {
		Rectangle bounds = getShell().getBounds();
		Point size = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		scrolledComposite.setMinSize(size);
		int height = constrain(size.y, MIN_HEIGHT, getMonitorArea().height);
		getShell().setSize(size.x, height);
		scrolledComposite.setSize(size.x, height);
		setLocation(new Point(bounds.x, bounds.y));

		if (commentEditor != null) {
			Composite editorComposite = commentEditor.getEditorComposite();

			if (scrolledComposite.getVerticalBar().isVisible() && editorComposite != null) {
				Point commentEditorSize = editorComposite.getSize();
				editorComposite.setSize(commentEditorSize.x - scrolledComposite.getVerticalBar().getSize().x,
						commentEditorSize.y);
			}

			if (editorComposite != null) {
				scrolledComposite.setOrigin(0, Integer.MAX_VALUE);
			}
		}
	}

	public void setHeightBasedOnMouse(int mouseY) {
		int mouseYFromBottom = getMonitorArea().height + getMonitorArea().y - mouseY; // Coordinates are based on the primary monitor
		recomputeSize();
		setSize(MAX_WIDTH, constrain(mouseYFromBottom - ICON_BUFFER, MIN_HEIGHT, getShell().getSize().y));
	}

	/**
	 * Sets the size of the dialog's shell and scrolled composite. The height must be at least minimum height.
	 *
	 * @param width
	 *            the width in pixels
	 * @param height
	 *            the height in pixels
	 */
	public void setSize(int width, int height) {
		Point computeSize = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		if (computeSize.x > width) {
			width = computeSize.x;
		}
		height = Math.max(height, MIN_HEIGHT);

		getShell().setSize(width, height);
		scrolledComposite.setSize(width, height);
	}

	/**
	 * Initializes the comment dialog with the comments (and review item/line range if it wasn't provided on construction) from the
	 * {@link CommentAnnotationHoverInput} provided
	 *
	 * @param input
	 *            the input of the comment dialog
	 */
	public void setInput(Object input) {
		if (input instanceof CommentAnnotationHoverInput) {
			annotationInput = (CommentAnnotationHoverInput) input;

			// clear the composite in case we are re-using it
			for (Control control : composite.getChildren()) {
				control.dispose();
			}

			currentPopupDialog = this;

			commentList = new ArrayList<>();
			for (CommentAnnotation annotation : annotationInput.getAnnotations()) {
				if (reviewItem == null) {
					if (annotation.getComment().getItem() instanceof IReviewItem) {
						reviewItem = (IReviewItem) annotation.getComment().getItem();
					}
				}
				if (range == null) {
					List<ILocation> locations = annotation.getComment().getLocations();
					if (!locations.isEmpty()) {
						ILocation location = locations.get(0);
						if (location instanceof ILineLocation) {
							range = new LineRange(((ILineLocation) location).getRangeMin(), 1);
						}
					}
				}

				if (reviewItem != null && reviewItem.getReview() != null
						&& reviewItem.getReview().getRepository() != null
						&& reviewItem.getReview().getRepository().getAccount() != null
						&& reviewItem.getReview().getRepository().getAccount() != annotation.getComment().getAuthor()
						&& annotation.getComment().isDraft()) {
					continue;
				}

				CommentPart part = new CommentPart(annotation.getComment(), annotationInput.getBehavior());
				commentList.add(annotation.getComment());
				part.hookCustomActionRunListener(this);
				Control control = part.createControl(composite, toolkit);
				toolkit.adapt(control, true, true);
			}

			composite.setBackground(toolkit.getColors().getBackground());

			scrolledComposite.layout(true, true);
			scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

			if (InlineCommentEditor.canAddCommentEditor(reviewItem.getId(), range.getStartLine())) {
				commentEditor = new InlineCommentEditor(this);
				scrolledComposite.addMouseListener(createRemoveCommentEditorListener());
				List<Section> sections = getScrolledCompositeSections();
				for (Section s : sections) {
					recursivelyAddCommentEditorListener(s, createAddCommentEditorListener());
				}

				if (!commentList.isEmpty()) {
					helpText = new Text(composite, SWT.NONE);
					helpText.setText(Messages.CommentPopupDialog_HelpText);
				}
			} else {
				editable = false;
			}

			if (isCommentNavigator) {
				InlineCommentEditor.addToEditMap(reviewItem.getId(), range.getStartLine());
			}

		}
	}

	/**
	 * Helper method to return all of the {@link Section} in the dialog's scrolled composite
	 *
	 * @return the list of {@link Section} that are children of the dialog's scrolled composite
	 */
	protected List<Section> getScrolledCompositeSections() {
		List<Section> sections = new ArrayList<>();
		if (scrolledComposite.getChildren().length != 0 && scrolledComposite.getChildren()[0] instanceof Composite) {
			Composite sectionContainer = (Composite) scrolledComposite.getChildren()[0];
			sections = Arrays.asList(sectionContainer.getChildren())
					.stream()
					.filter(Section.class::isInstance)
					.map(Section.class::cast)
					.collect(Collectors.toUnmodifiableList());
		}
		return sections;
	}

	/**
	 * Adds a mouse listener to the {@link Control} provided and its children
	 *
	 * @param c
	 *            the {@link Control} and its {@link Control} children that you want to add mouse listeners to
	 * @param listener
	 *            the mouse listener that will be added to the provided {@link Control}
	 */
	private void recursivelyAddCommentEditorListener(Control c, MouseListener listener) {
		c.addMouseListener(listener);

		if (c instanceof Composite) {
			Control[] controls = ((Composite) c).getChildren();
			for (Control control : controls) {
				recursivelyAddCommentEditorListener(control, listener);
			}
		}

	}

	/**
	 * Given a {@link Control}, it attempts to find parent {@link Section} recursively
	 *
	 * @param c
	 *            the {@link Control} that you want to find the parent {@link Section} of
	 * @return the parent {@link Section} or null if it can't be found
	 */
	private Section findParentSection(Control c) {
		if (c == null) {
			return null;
		} else if (c instanceof Section) {
			return (Section) c;
		} else {
			return findParentSection(c.getParent());
		}
	}

	/**
	 * Creates a mouse down {@link MouseAdapter} to add the comment editor to the dialog. It will attempt to get the comment associated with
	 * the {@link Control} you clicked on.
	 *
	 * @return the {@link MouseAdapter} that can invoke the comment editor on mouse down events
	 */
	private MouseAdapter createAddCommentEditorListener() {
		return new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				getShell().setVisible(true);

				IComment comment = null;
				if (e.getSource() instanceof Control) {
					Section section;
					if (isCommentNavigator) {
						section = getScrolledCompositeSections().get(0);
					} else {
						section = findParentSection((Control) e.getSource());
					}
					if (section != null && section.getData() != null && section.getData() instanceof IComment) {
						comment = (IComment) section.getData();
					}
				}

				if (comment != null && editable) {
					commentEditor.createControl(composite, comment);
				}
			}
		};
	}

	/**
	 * Creates a mouse down {@link MouseAdapter} to remove the comment editor
	 *
	 * @return the {@link MouseAdapter} that can remove the comment editor on mouse down events
	 */
	private MouseAdapter createRemoveCommentEditorListener() {
		return new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (commentEditor != null) {
					commentEditor.removeControl();
				}
			}
		};
	}

	/**
	 * Helper method to get the last comment draft in the dialog's comment list
	 *
	 * @return the last {@link IComment} in the comment list if there is at least one draft in the comment list or null otherwise
	 */
	protected IComment getLastCommentDraft() {
		return commentList.stream().filter(IComment::isDraft).reduce((first, second) -> second).orElse(null);

	}

	protected void hideHelpText() {
		if (helpText != null) {
			helpText.dispose();
			helpText = null;
		}
	}

	/**
	 * Force closes the dialog when an action is about to run
	 */
	@Override
	public void actionAboutToRun(Action action) {
		close(true);
	}

	/**
	 * Force close the dialog when an action ran
	 */
	@Override
	public void actionRan(Action action) {
		close(true);
	}

	public static CommentPopupDialog getCurrentPopupDialog() {
		return currentPopupDialog;
	}

	public void setInformationControl(CommentInformationControl crucibleInformationControl) {
		informationControl = crucibleInformationControl;
	}

	public CommentInformationControl getInformationControl() {
		return informationControl;
	}
}
