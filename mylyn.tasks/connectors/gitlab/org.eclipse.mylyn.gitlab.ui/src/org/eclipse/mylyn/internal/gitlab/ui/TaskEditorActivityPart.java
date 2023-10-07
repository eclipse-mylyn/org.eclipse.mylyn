/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.gitlab.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.mylyn.gitlab.core.GitlabCoreActivator;
import org.eclipse.mylyn.gitlab.ui.GitlabUiActivator;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class TaskEditorActivityPart extends AbstractTaskEditorPart {

    private @NonNull List<TaskAttribute> activityAttributes;
    private boolean hasIncoming = false;
    private Section section;
    private Composite activityComposite;

    public TaskEditorActivityPart() {
	setPartName("Activity Events");
    }

    @Override
    public void createControl(Composite parent, FormToolkit toolkit) {
	initialize();

	section = createSection(parent, toolkit, hasIncoming);
	section.setText(section.getText() + " (" + activityAttributes.size() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
	if (hasIncoming) {
	    expandSection(toolkit, section);
	} else {
	    section.addExpansionListener(new ExpansionAdapter() {
		@Override
		public void expansionStateChanged(ExpansionEvent event) {
		    if (activityComposite == null) {
			expandSection(toolkit, section);
			getTaskEditorPage().reflow();
		    }
		}
	    });
	}
	setSection(toolkit, section);
    }

    private void initialize() {
	activityAttributes = getTaskData().getAttributeMapper().getAttributesByType(getTaskData(),
		GitlabCoreActivator.ATTRIBUTE_TYPE_ACTIVITY);
	Collections.sort(activityAttributes, new Comparator<TaskAttribute>() {

	    @Override
	    public int compare(TaskAttribute o1, TaskAttribute o2) {
		// TODO Auto-generated method stub
		return o1.getId().compareTo(o2.getId());
	    }
	});

	for (TaskAttribute attribute : activityAttributes) {
	    if (getModel().hasIncomingChanges(attribute)) {
		hasIncoming = true;
	    }
	}
    }

    private void expandSection(FormToolkit toolkit, Section section) {
	activityComposite = toolkit.createComposite(section);
	GridLayout la = EditorUtil.createSectionClientLayout();
	la.numColumns = 4;
	activityComposite.setLayout(la);
	GridData gd = new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 1, 1);
	activityComposite.setLayoutData(gd);

	getTaskEditorPage().registerDefaultDropListener(section);

	if (activityAttributes.size() > 0) {
	    createActivityList(toolkit, activityComposite);
	} else {
	    toolkit.createLabel(activityComposite, "No Activity Events");
	}

	toolkit.paintBordersFor(activityComposite);
	section.setClient(activityComposite);
    }

    private void createActivityList(FormToolkit toolkit, Composite attachmentsComposite2) {
	for (TaskAttribute taskAttribute : activityAttributes) {
	    String activityText = taskAttribute.getAttribute(TaskAttribute.COMMENT_TEXT).getValue();
	    String activityType = taskAttribute.getAttribute(GitlabCoreActivator.ATTRIBUTE_TYPE_ACTIVITY).getValue();

	    GitlabCoreActivator.ActivityType xx = GitlabCoreActivator.ActivityType.valueOf(activityType);

	    Label labelIcon = toolkit.createLabel(activityComposite, "");

	    switch (GitlabCoreActivator.ActivityType.valueOf(activityType)) {
	    case CALENDAR:
		labelIcon.setImage(
			GitlabUiActivator.getDefault().getImageRegistry().get(GitlabUiActivator.GITLAB_CALENDAR_FILE));
		break;
	    case PERSON:
		labelIcon.setImage(
			GitlabUiActivator.getDefault().getImageRegistry().get(GitlabUiActivator.GITLAB_PERSON_FILE));
		break;
	    case PENCIL:
		labelIcon.setImage(
			GitlabUiActivator.getDefault().getImageRegistry().get(GitlabUiActivator.GITLAB_PENCIL_FILE));
		break;
	    case UNLOCK:
		labelIcon.setImage(
			GitlabUiActivator.getDefault().getImageRegistry().get(GitlabUiActivator.GITLAB_UNLOCK_FILE));
		break;
	    case LOCK:
		labelIcon.setImage(
			GitlabUiActivator.getDefault().getImageRegistry().get(GitlabUiActivator.GITLAB_LOCK_FILE));
		break;
	    case CLOSED:
		labelIcon.setImage(
			GitlabUiActivator.getDefault().getImageRegistry().get(GitlabUiActivator.GITLAB_CLOSED_FILE));
		break;
	    case REOPEN:
		labelIcon.setImage(
			GitlabUiActivator.getDefault().getImageRegistry().get(GitlabUiActivator.GITLAB_REOPEN_FILE));
		break;
	    case LABEL:
		labelIcon.setImage(
			GitlabUiActivator.getDefault().getImageRegistry().get(GitlabUiActivator.GITLAB_LABEL_FILE));
		break;

	    default:
		labelIcon.setImage(
			GitlabUiActivator.getDefault().getImageRegistry().get(GitlabUiActivator.GITLAB_PICTURE_FILE));
		break;
	    }

	    TaskAttribute author = taskAttribute.getMappedAttribute(TaskAttribute.COMMENT_AUTHOR);
	    if (author != null) {
		toolkit.createLabel(activityComposite, author.getValue());
	    }

	    TaskAttribute commentDate = taskAttribute.getMappedAttribute(TaskAttribute.COMMENT_DATE);
	    if (commentDate != null) {
		toolkit.createLabel(attachmentsComposite2, commentDate.getValue());
	    }
	    StyledText text = new StyledText(attachmentsComposite2,
		    toolkit.getBorderStyle() | toolkit.getOrientation());
	    text.setText(activityText);
	    text.setForeground(toolkit.getColors().getForeground());
	    text.setBackground(toolkit.getColors().getBackground());
	    buildStyledText(text, activityText, toolkit);
	}
    }

    private void buildStyledText(StyledText textControl, String theText, FormToolkit toolkit) {
	String resultText = "";

	String[] parts = theText.split("\\*\\*|\\*\\*\\{\\-|\\{\\-|\\-\\}|\\{\\+|\\+\\}");
	ArrayList<StyleRange> styles = new ArrayList<StyleRange>(parts.length);
	int textIdx = 0;
	StyleRange styleRange = new StyleRange();
	styleRange.start = 0;
	styleRange.fontStyle = SWT.NORMAL;

	int textLen = theText.length();
	for (int i = 0; i < parts.length; i++) {
	    int actPartLen = parts[i].length();
	    textIdx += actPartLen;
	    String marker = textIdx + 2 <= textLen ? theText.substring(textIdx, textIdx + 2) : "  ";
	    if (actPartLen > 0) {
		resultText += parts[i];
		styleRange.length += actPartLen;
	    }
	    if ("**".equals(marker)) {
		styleRange = createNewRangeIfNeeded(resultText, styles, styleRange, actPartLen);
		if (styleRange.fontStyle == SWT.BOLD) {
		    styleRange.fontStyle = SWT.NORMAL;
		} else {
		    styleRange.fontStyle = SWT.BOLD;
		}
	    }
	    if ("{-".equals(marker)) {
		styleRange = createNewRangeIfNeeded(resultText, styles, styleRange, actPartLen);
		styleRange.background = textControl.getDisplay().getSystemColor(SWT.COLOR_RED);
	    }
	    if ("{+".equals(marker)) {
		styleRange = createNewRangeIfNeeded(resultText, styles, styleRange, actPartLen);
		styleRange.background = textControl.getDisplay().getSystemColor(SWT.COLOR_GREEN);
	    }
	    if ("-}".equals(marker) || "+}".equals(marker)) {
		styleRange = createNewRangeIfNeeded(resultText, styles, styleRange, actPartLen);
		styleRange.background = toolkit.getColors().getBackground();
	    }
	    textIdx += 2;
	}
	textControl.setText(resultText);
	if (!styles.isEmpty()) {
	    StyleRange[] ranges = new StyleRange[styles.size()];
	    styles.toArray(ranges);
	    textControl.setStyleRanges((StyleRange[]) ranges);
	}
    }

    private StyleRange createNewRangeIfNeeded(String resultText, ArrayList<StyleRange> styles, StyleRange styleRange,
	    int actPartLen) {
	if (actPartLen > 0) {
	    styles.add(styleRange);
	    StyleRange styleRangeNew = (StyleRange) styleRange.clone();
	    styleRange = styleRangeNew;
	    styleRange.start = resultText.length();
	    styleRange.length = 0;
	}
	return styleRange;
    }

}
