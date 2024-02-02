/*******************************************************************************
 * Copyright (c) 2009, 2014 Atlassian and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Atlassian - initial API and implementation
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.editors.parts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.internal.reviews.ui.IReviewAction;
import org.eclipse.mylyn.internal.reviews.ui.IReviewActionListener;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

/**
 * A UI part that is expandable like a tree
 * 
 * @author Shawn Minto
 */
public abstract class ExpandablePart<T extends IComment, V extends ExpandablePart<T, V>> {

	private Section commentSection;

	private boolean isExpanded;

	private boolean enableToolbar = true;

	private boolean isIncomming = false;

	private final List<V> childrenParts;

	private IReviewActionListener actionListener;

	private ToolBarManager toolBarManager;

	private Label annotationImageLabel;

	private Label annotationsTextLabel;

	public ExpandablePart() {
		childrenParts = new ArrayList<>();
	}

	protected void addChildPart(V part) {
		childrenParts.add(part);
	}

	protected List<V> getChildrenParts() {
		return childrenParts;
	}

	public Control createControl(Composite parent, final FormToolkit toolkit) {
		int style = ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT;
		if (canExpand()) {
			style |= ExpandableComposite.TWISTIE;
		}
		style |= ExpandableComposite.EXPANDED;

		commentSection = toolkit.createSection(parent, style);
		updateSectionText();
		commentSection.setTitleBarForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		GridData gd = GridDataFactory.fillDefaults().grab(true, false).create();
		if (!canExpand()) {
			gd.horizontalIndent = 9;
		}
		commentSection.setLayoutData(gd);

		final Composite actionsComposite = createSectionAnnotationsAndToolbar(commentSection, toolkit);

		toolBarManager = new ToolBarManager(SWT.FLAT);

		ToolBar toolbarControl = toolBarManager.createControl(actionsComposite);
		toolkit.adapt(toolbarControl);

		if (commentSection.isExpanded()) {
			isExpanded = true;
			fillToolBar(toolBarManager, isExpanded);
			if (hasContents()) {
				Composite composite = createSectionContents(commentSection, toolkit);
				commentSection.setClient(composite);
				commentSection.addExpansionListener(new ExpansionAdapter() {
					@Override
					public void expansionStateChanged(ExpansionEvent e) {
						fillToolBar(toolBarManager, e.getState());
						if (getSection() != null && !getSection().isDisposed()) {
							getSection().layout();
						}
					}
				});
			}
		}
		return commentSection;
	}

	protected void update() {
		updateSectionText();
		updateAnnotationsArea();
		updateToolbar();
	}

	private void updateSectionText() {
		if (commentSection != null && !commentSection.isDisposed()) {
			commentSection.setText(getSectionHeaderText());
		}
	}

	private void updateToolbar() {
		fillToolBar(toolBarManager, commentSection.isExpanded());
	}

	private void updateAnnotationsArea() {

		ImageDescriptor annotationImage = getAnnotationImage();
		if (annotationImageLabel != null && !annotationImageLabel.isDisposed()) {
			if (annotationImage != null) {
				annotationImageLabel.setImage(CommonImages.getImage(annotationImage));
			} else {
				annotationImageLabel.setImage(null);
			}
		}

		String annotationsText = getAnnotationText();
		if (annotationsText == null) {
			annotationsText = ""; //$NON-NLS-1$
		}
		if (annotationsTextLabel != null && !annotationsTextLabel.isDisposed()) {
			annotationsTextLabel.setText(annotationsText);
		}
	}

	protected boolean canExpand() {
		return true;
	}

	protected boolean hasContents() {
		return canExpand();
	}

	public Section getSection() {
		return commentSection;
	}

	private void fillToolBar(ToolBarManager toolbarManager, boolean expanded) {
		if (!enableToolbar) {
			return;
		}

		List<IReviewAction> toolbarActions = getToolbarActions(expanded);

//		for (Control control : actionsComposite.getChildren()) {
//			if (control instanceof ImageHyperlink) {
//				control.setMenu(null);
//				control.dispose();
//			}
//		}

		toolbarManager.removeAll();

		if (toolbarActions != null) {

			for (final IReviewAction action : toolbarActions) {
				action.setActionListener(actionListener);
				toolbarManager.add(action);
//				ImageHyperlink link = createActionHyperlink(actionsComposite, toolkit, action);
//				if (!action.isEnabled()) {
//					link.setEnabled(false);
//				}
			}
		}
		toolbarManager.markDirty();
		toolbarManager.update(true);
//		actionsComposite.getParent().layout();
	}

	protected ImageHyperlink createActionHyperlink(Composite actionsComposite, FormToolkit toolkit,
			final IAction action) {

		if (action instanceof IReviewAction) {
			((IReviewAction) action).setActionListener(actionListener);
		}
		ImageHyperlink link = toolkit.createImageHyperlink(actionsComposite, SWT.NONE);
		if (action.getImageDescriptor() != null) {
			link.setImage(CommonImages.getImage(action.getImageDescriptor()));
		} else {
			link.setText(action.getText());
		}
		link.setToolTipText(action.getToolTipText());
		link.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				action.run();
			}
		});
		return link;
	}

	/**
	 * @return A composite that image hyperlinks can be placed on
	 */
	protected Composite createSectionAnnotationsAndToolbar(Section section, FormToolkit toolkit) {

		Composite toolbarComposite = toolkit.createComposite(section);
		section.setTextClient(toolbarComposite);
		RowLayout rowLayout = new RowLayout();
		rowLayout.marginTop = 0;
		rowLayout.marginBottom = 0;
		toolbarComposite.setLayout(rowLayout);

		Composite annotationsComposite = toolkit.createComposite(toolbarComposite);

		rowLayout = new RowLayout();
		rowLayout.marginTop = 0;
		rowLayout.marginBottom = 0;
		rowLayout.spacing = 0;

		annotationsComposite.setLayout(rowLayout);

		annotationImageLabel = toolkit.createLabel(annotationsComposite, ""); //$NON-NLS-1$

		annotationsTextLabel = toolkit.createLabel(annotationsComposite, ""); //$NON-NLS-1$

		createCustomAnnotations(annotationsComposite, toolkit);

		updateAnnotationsArea();

//		Composite actionsComposite = toolkit.createComposite(toolbarComposite);
//		actionsComposite.setBackground(null);
//		rowLayout = new RowLayout();
//		rowLayout.marginTop = 0;
//		rowLayout.marginBottom = 0;
//		actionsComposite.setLayout(rowLayout);

		return toolbarComposite;
	}

	public boolean isExpanded() {
		return isExpanded && areChildrenExpanded();
	}

	private boolean areChildrenExpanded() {
		for (V child : childrenParts) {
			if (!child.isExpanded()) {
				return false;
			}
		}
		return true;
	}

	public void setExpanded(boolean expanded) {
		if (expanded != commentSection.isExpanded()) {
			CommonFormUtil.setExpanded(commentSection, expanded);
		}
		for (V child : childrenParts) {
			child.setExpanded(expanded);
		}
	}

	public void hookCustomActionRunListener(IReviewActionListener actionRunListener) {
		actionListener = actionRunListener;
	}

	public IReviewActionListener getActionListener() {
		return actionListener;
	}

	protected void createCustomAnnotations(Composite toolbarComposite, FormToolkit toolkit) {
		// default do nothing
	}

	public void disableToolbar() {
		enableToolbar = false;
	}

	public void setIncomming(boolean newIncomming) {
		isIncomming = newIncomming;
	}

	public boolean isIncomming() {
		return isIncomming;
	}

	public void dispose() {
		if (getSection() != null) {
			getSection().dispose();
		}
	}

	protected final V findPart(T comment) {

		for (V part : childrenParts) {
			if (part.represents(comment)) {
				return part;
			}
		}

		return null;
	}

	protected final void updateChildren(Composite composite, FormToolkit toolkit, boolean shouldHighlight,
			Collection<T> childrenObjects) {

		List<V> toRemove = new ArrayList<>();
		List<V> newParts = new ArrayList<>();

		if (childrenObjects.size() > 0) {
			List<T> generalComments = new ArrayList<>(childrenObjects);
			Collections.sort(generalComments, getComparator());

			// The following code is almost duplicated in the crucible review files part
			Control prevControl = null;

			for (int i = 0; i < generalComments.size(); i++) {
				T comment = generalComments.get(i);

				V oldPart = findPart(comment);

				if (oldPart != null) {
					Control commentControl = oldPart.update(composite, toolkit, comment);
					if (commentControl != null && !commentControl.isDisposed()) {

						GridDataFactory.fillDefaults().grab(true, false).applyTo(commentControl);

						if (prevControl != null) {
							commentControl.moveBelow(prevControl);
						} else if (composite.getChildren().length > 1) {
							commentControl.moveAbove(composite);
						}
						prevControl = commentControl;
					} else {
						Thread.dumpStack();
					}

					newParts.add(oldPart);
				} else {
					V commentPart = createChildPart(comment);
					commentPart.hookCustomActionRunListener(actionListener);
					newParts.add(commentPart);
					Control commentControl = commentPart.createControl(composite, toolkit);

					GridDataFactory.fillDefaults().grab(true, false).applyTo(commentControl);
					if (prevControl != null) {
						commentControl.moveBelow(prevControl);
					} else if (composite.getChildren().length > 1) {
						commentControl.moveAbove(composite);
					}
					prevControl = commentControl;
				}
			}

			for (V part : childrenParts) {
				if (!newParts.contains(part)) {
					toRemove.add(part);
				}
			}

		} else {
			toRemove.addAll(childrenParts);
		}

		for (V part : toRemove) {
			part.dispose();
		}

		childrenParts.clear();
		childrenParts.addAll(newParts);
	}

	protected abstract V createChildPart(T comment);

	protected abstract Control update(Composite parentComposite, FormToolkit toolkit, T newComment);

	protected abstract boolean represents(T comment);

	protected abstract Comparator<T> getComparator();

	protected abstract List<IReviewAction> getToolbarActions(boolean expanded);

	protected abstract String getAnnotationText();

	protected abstract ImageDescriptor getAnnotationImage();

	protected abstract String getSectionHeaderText();

	protected abstract Composite createSectionContents(Section section, FormToolkit toolkit);

}
