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
 *     See git history
 *******************************************************************************/
package org.eclipse.mylyn.internal.gitlab.ui;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.mylyn.commons.workbench.browser.BrowserUtil;
import org.eclipse.mylyn.gitlab.core.GitlabCoreActivator;
import org.eclipse.mylyn.gitlab.ui.GitlabUiActivator;
import org.eclipse.mylyn.internal.gitlab.core.GitlabActivityStyle;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TaskEditorActivityPart extends AbstractTaskEditorPart {

	private @NonNull List<TaskAttribute> activityAttributes;

	private boolean hasIncoming = false;

	private Section section;

	private Composite activityComposite;

	private final Type listOfMyClassObject = new TypeToken<ArrayList<GitlabActivityStyle>>() {
	}.getType();

	private final Gson gson = new Gson();

	public TaskEditorActivityPart() {
		setPartName("Activity Events"); //$NON-NLS-1$
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
		activityAttributes = getTaskData().getAttributeMapper()
				.getAttributesByType(getTaskData(), GitlabCoreActivator.ATTRIBUTE_TYPE_ACTIVITY);
		Collections.sort(activityAttributes, Comparator.comparing(TaskAttribute::getId));

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
			createActivityList(toolkit);
		} else {
			toolkit.createLabel(activityComposite, "No Activity Events"); //$NON-NLS-1$
		}

		toolkit.paintBordersFor(activityComposite);
		section.setClient(activityComposite);
	}

	private void createActivityList(FormToolkit toolkit) {
		for (TaskAttribute taskAttribute : activityAttributes) {
			String activityText = taskAttribute.getAttribute(TaskAttribute.COMMENT_TEXT).getValue();
			String activityType = taskAttribute.getAttribute(GitlabCoreActivator.ATTRIBUTE_TYPE_ACTIVITY).getValue();

			Label labelIcon = toolkit.createLabel(activityComposite, ""); //$NON-NLS-1$
			labelIcon.setImage(GitlabUiActivator.getDefault()
					.getImageRegistry()
					.get(switch (GitlabCoreActivator.ActivityType.valueOf(activityType)) {
						case CALENDAR:
							yield GitlabUiActivator.GITLAB_CALENDAR_FILE;
						case PERSON:
							yield GitlabUiActivator.GITLAB_PERSON_FILE;
						case PENCIL:
							yield GitlabUiActivator.GITLAB_PENCIL_FILE;
						case UNLOCK:
							yield GitlabUiActivator.GITLAB_UNLOCK_FILE;
						case LOCK:
							yield GitlabUiActivator.GITLAB_LOCK_FILE;
						case CLOSED:
							yield GitlabUiActivator.GITLAB_CLOSED_FILE;
						case REOPEN:
							yield GitlabUiActivator.GITLAB_REOPEN_FILE;
						case LABEL:
							yield GitlabUiActivator.GITLAB_LABEL_FILE;
						case DESIGN:
							yield GitlabUiActivator.GITLAB_DESIGN_FILE;
						default:
							yield GitlabUiActivator.GITLAB_PICTURE_FILE;
					}));

			TaskAttribute author = taskAttribute.getMappedAttribute(TaskAttribute.COMMENT_AUTHOR);
			if (author != null) {
				toolkit.createLabel(activityComposite, author.getValue());
			}

			TaskAttribute commentDate = taskAttribute.getMappedAttribute(TaskAttribute.COMMENT_DATE);
			if (commentDate != null) {
				toolkit.createLabel(activityComposite, commentDate.getValue());
			}
			StyledText text = new StyledText(activityComposite, toolkit.getBorderStyle() | toolkit.getOrientation());
			text.setText(activityText);

			text.setForeground(toolkit.getColors().getForeground());
			text.setBackground(toolkit.getColors().getBackground());
			TaskAttribute styleAttribute = taskAttribute
					.getMappedAttribute(GitlabCoreActivator.ATTRIBUTE_TYPE_ACTIVITY_STYLE);
			text.setText(activityText);
			if (styleAttribute != null && styleAttribute.getValue() != null && !styleAttribute.getValue().isEmpty()) {
				ArrayList<GitlabActivityStyle> stylesFromJson = gson.fromJson(styleAttribute.getValue(),
						listOfMyClassObject);
				setStyles(text, stylesFromJson);
				text.addListener(SWT.MouseDown, event -> {
					if ((event.stateMask & SWT.MOD1) != 0) {
						int offset = text.getOffsetAtPoint(new Point(event.x, event.y));
						if (offset != -1) {
							StyleRange style1 = null;
							try {
								style1 = text.getStyleRangeAtOffset(offset);
							} catch (IllegalArgumentException e) {
								// no character under event.x, event.y
							}
							if (style1 != null && style1.underline && style1.underlineStyle == SWT.UNDERLINE_LINK) {
								System.out.println("Click on a Link " + style1.data); //$NON-NLS-1$
								BrowserUtil.openUrl((String) style1.data, BrowserUtil.NO_RICH_EDITOR);
							}
						}
					}
				});
				text.addListener(SWT.MouseHover, event -> {
					int offset = text.getOffsetAtPoint(new Point(event.x, event.y));
					if (offset != -1) {
						StyleRange style1 = null;
						try {
							style1 = text.getStyleRangeAtOffset(offset);
						} catch (IllegalArgumentException e) {
							// no character under event.x, event.y
						}
						if (style1 != null && style1.underline && style1.underlineStyle == SWT.UNDERLINE_LINK) {
							String oldTooltip = text.getToolTipText();
							text.setToolTipText("" + style1.data); //$NON-NLS-1$
							Job job = new Job("Deactivate Tooltip") { //$NON-NLS-1$

								@Override
								protected IStatus run(IProgressMonitor monitor) {
									Display.getDefault().asyncExec(() -> text.setToolTipText(oldTooltip));
									return Status.OK_STATUS;
								}
							};
							job.schedule(8000);
						}
					}
				});
			}
		}
	}

	private void setStyles(StyledText textControl, ArrayList<GitlabActivityStyle> stylesFromJson) {
		StyleRange[] ranges = new StyleRange[stylesFromJson.size()];

		for (int i = 0; i < stylesFromJson.size(); i++) {
			GitlabActivityStyle gitlabActivityStyle = stylesFromJson.get(i);
			StyleRange styleRange = new StyleRange();
			ranges[i] = styleRange;
			styleRange.start = gitlabActivityStyle.getStart();
			styleRange.length = gitlabActivityStyle.getLength();
			if (gitlabActivityStyle.getColor() != GitlabActivityStyle.COLOR_INHERIT_DEFAULT) {
				styleRange.background = textControl.getDisplay().getSystemColor(gitlabActivityStyle.getColor());
			}
			if (gitlabActivityStyle.getFontStyle() == GitlabActivityStyle.UNDERLINE_LINK) {
				styleRange.fontStyle = SWT.NORMAL;
				styleRange.underline = true;
				styleRange.underlineStyle = SWT.UNDERLINE_LINK;
				styleRange.data = gitlabActivityStyle.getUrl();
			} else {
				styleRange.fontStyle = gitlabActivityStyle.getFontStyle();
			}
		}
		textControl.setStyleRanges(ranges);
	}

}
