/*******************************************************************************
 * Copyright (c) 2009, 2015 Atlassian and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Atlassian - initial API and implementation
 *     Robert Munteanu - fix for bug 360549
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.editors.parts;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.reviews.ui.IReviewAction;
import org.eclipse.mylyn.internal.tasks.ui.editors.RichTextEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorExtensions;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.mylyn.reviews.ui.SizeCachingComposite;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorExtension;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * A UI part to represent a comment in a review
 * 
 * @author Shawn Minto
 * @author Thomas Ehrnhoefer
 */
@SuppressWarnings("restriction")
public abstract class AbstractCommentPart<V extends ExpandablePart<IComment, V>> extends ExpandablePart<IComment, V> {

	protected IComment comment;

	protected Control commentTextComposite;

	protected SizeCachingComposite sectionClient;

	private final ReviewBehavior behavior;

	public AbstractCommentPart(IComment comment, ReviewBehavior behavior) {
		Assert.isNotNull(comment);
		Assert.isNotNull(behavior);
		this.comment = comment;
		this.behavior = behavior;
	}

	@Override
	protected String getSectionHeaderText() {
		IUser author = comment.getAuthor();
		return NLS.bind(Messages.AbstractCommentPart_Section_header, //
				author != null ? author.getDisplayName() : Messages.AbstractCommentPart_No_author, //
				DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(comment.getCreationDate()) // 
		);
	}

	public ReviewBehavior getBehavior() {
		return behavior;
	}

	@Override
	protected Comparator<IComment> getComparator() {
		return new Comparator<IComment>() {

			public int compare(IComment o1, IComment o2) {
				if (o1 != null && o2 != null) {
					return o1.getCreationDate().compareTo(o2.getCreationDate());
				}
				return 0;
			}

		};
	}

	// TODO handle changed highlighting properly

	protected final Control createOrUpdateControl(Composite parentComposite, FormToolkit toolkit) {
		Control createdControl = null;
		if (getSection() == null) {

			Control newControl = createControl(parentComposite, toolkit);

			setIncomming(true);

			createdControl = newControl;
		} else {

			if (commentTextComposite != null && !commentTextComposite.isDisposed()) {
				Composite parent = commentTextComposite.getParent();
				commentTextComposite.dispose();
				createCommentArea(toolkit, sectionClient);
				if (parent.getChildren().length > 0) {
					commentTextComposite.moveAbove(parent.getChildren()[0]);
				}

			}
			updateChildren(sectionClient, toolkit, true, comment.getReplies());

			createdControl = getSection();
		}

		if (sectionClient != null && !sectionClient.isDisposed()) {
			sectionClient.clearCache();
		}
		getSection().layout(true, true);

		update();

		return createdControl;
	}

	@Override
	protected Composite createSectionContents(Section section, FormToolkit toolkit) {
		// CHECKSTYLE:MAGIC:OFF
		section.clientVerticalSpacing = 0;
		section.setData(comment);

		sectionClient = new SizeCachingComposite(section, SWT.NONE);
		toolkit.adapt(sectionClient);
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = 0;
		layout.marginLeft = 9;
		layout.marginRight = 11;
		sectionClient.setLayout(layout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(sectionClient);

		createCommentArea(toolkit, sectionClient);

		updateChildren(sectionClient, toolkit, false, comment.getReplies());

		// CHECKSTYLE:MAGIC:ON
		return sectionClient;
	}

	protected void createCommentArea(FormToolkit toolkit, Composite parentComposite) {
		final Composite twoColumnComposite = new Composite(parentComposite, SWT.NONE);
		twoColumnComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
		GridDataFactory.fillDefaults().hint(500, SWT.DEFAULT).applyTo(twoColumnComposite);
		toolkit.adapt(twoColumnComposite);

		final Label avatarLabel = new Label(twoColumnComposite, SWT.NONE);
		toolkit.adapt(avatarLabel, false, false);
//		avatarLabel.setImage(CrucibleUiPlugin.getDefault()
//				.getAvatarsCache()
//				.getAvatarOrDefaultImage(comment.getAuthor(), AvatarSize.LARGE));

		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).applyTo(avatarLabel);

		commentTextComposite = createReadOnlyText(toolkit, twoColumnComposite, comment.getDescription());
		GridDataFactory.fillDefaults().grab(true, true).applyTo(commentTextComposite);
	}

	@Override
	protected String getAnnotationText() {
		return comment.isDraft() ? Messages.AbstractCommentPart_Draft : StringUtils.EMPTY;
	}

	private Control createReadOnlyText(FormToolkit toolkit, Composite composite, String value) {

		int style = SWT.FLAT | SWT.READ_ONLY | SWT.MULTI | SWT.WRAP;

		ITask task = getBehavior().getTask();

		TaskRepository repository = TasksUi.getRepositoryManager()
				.getRepository(task.getConnectorKind(), task.getRepositoryUrl());

		AbstractTaskEditorExtension extension = TaskEditorExtensions.getTaskEditorExtension(repository);

		final RichTextEditor editor = new RichTextEditor(repository, style, null, extension, null);
		editor.setReadOnly(true);
		editor.setText(value);
		editor.createControl(composite, toolkit);

		// HACK: this is to make sure that we can't have multiple things highlighted
		editor.getViewer().getTextWidget().addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				editor.getViewer().getTextWidget().setSelection(0);
			}
		});

		return editor.getControl();
	}

	@Override
	protected boolean canExpand() {
		return comment.getReplies().size() > 0;
	}

	@Override
	protected boolean hasContents() {
		return true;
	}

	@Override
	protected ImageDescriptor getAnnotationImage() {
		return null;
	}

	@Override
	protected List<IReviewAction> getToolbarActions(boolean isExpanded) {
		return Collections.<IReviewAction> emptyList();
	}

}