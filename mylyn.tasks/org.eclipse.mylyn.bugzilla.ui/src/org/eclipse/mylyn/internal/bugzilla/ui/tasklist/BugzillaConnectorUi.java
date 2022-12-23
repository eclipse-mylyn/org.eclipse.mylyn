/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Eugene Kuleshov - improvements
 *     Frank Becker - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.mylyn.commons.ui.PlatformUiUtil;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.ui.BugzillaImages;
import org.eclipse.mylyn.internal.bugzilla.ui.TaskAttachmentHyperlink;
import org.eclipse.mylyn.internal.bugzilla.ui.TaskAttachmentTableEditorHyperlink;
import org.eclipse.mylyn.internal.bugzilla.ui.search.BugzillaSearchPage;
import org.eclipse.mylyn.internal.bugzilla.ui.wizard.NewBugzillaTaskWizard;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentModel;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.LegendElement;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.mylyn.tasks.ui.wizards.RepositoryQueryWizard;

/**
 * @author Mik Kersten
 * @author Robert Elves
 * @author Frank Becker
 */
public class BugzillaConnectorUi extends AbstractRepositoryConnectorUi {

	private static final String BUG = "(?:duplicate of|bug|task)[ \t]*(?:#|:)?[ \t]*(\\d+)"; //$NON-NLS-1$

	private static final String COMMENT = "comment[ \t]*#?[ \t]*(\\d+)"; //$NON-NLS-1$

	private static final String REGEXP_BUG = "(?:\\W||^)(" + BUG + "(?:[ \t]*" + COMMENT + ")?)|(" + COMMENT + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	private static final String REGEXP_ATTACHMENT = "(?:Created (?:an )?)?attachment[ \t]*#?[ \t]*(?:\\(id=)?(\\d+)\\)?"; //$NON-NLS-1$

	private static final Pattern PATTERN_BUG = Pattern.compile(REGEXP_BUG, Pattern.CASE_INSENSITIVE);

	private static final Pattern PATTERN_ATTACHMENT = Pattern.compile(REGEXP_ATTACHMENT, Pattern.CASE_INSENSITIVE);

	private final boolean doAttachmentTableEditorHyperlink;

	public BugzillaConnectorUi() {
		doAttachmentTableEditorHyperlink = PlatformUiUtil.supportsMultipleHyperlinkPresenter();
	}

	@Override
	public String getAccountCreationUrl(TaskRepository taskRepository) {
		return taskRepository.getRepositoryUrl() + "/createaccount.cgi"; //$NON-NLS-1$
	}

	@Override
	public String getAccountManagementUrl(TaskRepository taskRepository) {
		return taskRepository.getRepositoryUrl() + "/userprefs.cgi"; //$NON-NLS-1$
	}

	@Override
	public String getTaskHistoryUrl(TaskRepository taskRepository, ITask task) {
		return taskRepository.getRepositoryUrl() + IBugzillaConstants.URL_BUG_ACTIVITY + task.getTaskId();
	}

	@Override
	public List<LegendElement> getLegendElements() {
		List<LegendElement> legendItems = new ArrayList<LegendElement>();
		legendItems.add(LegendElement.createTask("blocker", BugzillaImages.OVERLAY_CRITICAL)); //$NON-NLS-1$
		legendItems.add(LegendElement.createTask("critical", BugzillaImages.OVERLAY_CRITICAL)); //$NON-NLS-1$
		legendItems.add(LegendElement.createTask("major", BugzillaImages.OVERLAY_CRITICAL)); //$NON-NLS-1$
		legendItems.add(LegendElement.createTask("normal", null)); //$NON-NLS-1$
		legendItems.add(LegendElement.createTask("minor", BugzillaImages.OVERLAY_MAJOR)); //$NON-NLS-1$
		legendItems.add(LegendElement.createTask("enhancement", BugzillaImages.OVERLAY_ENHANCEMENT)); //$NON-NLS-1$
		legendItems.add(LegendElement.createTask("trivial", BugzillaImages.OVERLAY_TRIVIAL)); //$NON-NLS-1$
		return legendItems;
	}

	@Override
	public ImageDescriptor getTaskKindOverlay(ITask task) {
		String severity = task.getAttribute(BugzillaAttribute.BUG_SEVERITY.getKey());
		if (severity != null) {
			// XXX: refactor to use configuration
			if ("blocker".equals(severity) || "critical".equals(severity) || "major".equals(severity)) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				return BugzillaImages.OVERLAY_CRITICAL;
			} else if ("minor".equals(severity)) { //$NON-NLS-1$
				return BugzillaImages.OVERLAY_MAJOR;
			} else if ("enhancement".equals(severity)) { //$NON-NLS-1$
				return BugzillaImages.OVERLAY_ENHANCEMENT;
			} else if ("trivial".equals(severity)) { //$NON-NLS-1$ 
				return BugzillaImages.OVERLAY_TRIVIAL;
			} else {
				return null;
			}
		}
		return super.getTaskKindOverlay(task);
	}

	@Override
	public String getTaskKindLabel(ITask repositoryTask) {
		return IBugzillaConstants.BUGZILLA_TASK_KIND;
	}

	@Override
	public ITaskRepositoryPage getSettingsPage(TaskRepository taskRepository) {
		return new BugzillaRepositorySettingsPage(taskRepository);
	}

	@Override
	public AbstractRepositoryQueryPage getSearchPage(TaskRepository repository, IStructuredSelection selection) {
		return new BugzillaSearchPage(repository);
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository, ITaskMapping selection) {
		return new NewBugzillaTaskWizard(taskRepository, selection);
	}

	@Override
	public IWizard getQueryWizard(TaskRepository repository, IRepositoryQuery query) {
		RepositoryQueryWizard wizard = new RepositoryQueryWizard(repository);
		if (query == null) {
			wizard.addPage(new BugzillaQueryTypeWizardPage(repository));
		} else {
			if (isCustomQuery(query)) {
				wizard.addPage(new BugzillaCustomQueryWizardPage(repository, query));
			} else {
				wizard.addPage(new BugzillaSearchPage(repository, query));
			}
		}
		return wizard;
	}

	@Override
	public boolean hasSearchPage() {
		return true;
	}

	@Override
	public String getConnectorKind() {
		return BugzillaCorePlugin.CONNECTOR_KIND;
	}

	private boolean isCustomQuery(IRepositoryQuery query2) {
		String custom = query2.getAttribute(IBugzillaConstants.ATTRIBUTE_BUGZILLA_QUERY_CUSTOM);
		return custom != null && custom.equals(Boolean.TRUE.toString());
	}

	@Override
	public IWizardPage getTaskAttachmentPage(TaskAttachmentModel model) {
		return new BugzillaTaskAttachmentPage(model);
	}

	@Deprecated
	@Override
	public IHyperlink[] findHyperlinks(TaskRepository repository, String text, int index, int textOffset) {
		return findHyperlinks(repository, null, text, index, textOffset);
	}

	@Override
	public IHyperlink[] findHyperlinks(TaskRepository repository, ITask task, String text, int index, int textOffset) {
		ArrayList<IHyperlink> hyperlinksFound = null;
		Matcher mb = PATTERN_BUG.matcher(text);
		while (mb.find()) {
			if (index == -1 || (index >= mb.start() && index <= mb.end())) {
				TaskHyperlink link = null;
				if (mb.group(1) != null) {
					// bug comment
					Region region = new Region(textOffset + mb.start(1), mb.end(1) - mb.start(1));
					link = new TaskHyperlink(region, repository, mb.group(2));
					if (mb.group(3) != null) {
						link.setSelection(TaskAttribute.PREFIX_COMMENT + mb.group(3));
					}
				} else if (task != null && mb.group(4) != null) {
					// comment
					Region region = new Region(textOffset + mb.start(4), mb.end(4) - mb.start(4));
					link = new TaskHyperlink(region, repository, task.getTaskId());
					link.setSelection(TaskAttribute.PREFIX_COMMENT + mb.group(5));
				}

				if (link != null) {
					if (hyperlinksFound == null) {
						hyperlinksFound = new ArrayList<IHyperlink>();
					}
					hyperlinksFound.add(link);
				}
			}
		}
		Matcher ma = PATTERN_ATTACHMENT.matcher(text);
		while (ma.find()) {
			if (index == -1 || (index >= ma.start() && index <= ma.end())) {
				// attachment
				Region region = new Region(textOffset + ma.start(), ma.end() - ma.start());
				TaskAttachmentHyperlink link0 = new TaskAttachmentHyperlink(region, repository, ma.group(1));
				if (hyperlinksFound == null) {
					hyperlinksFound = new ArrayList<IHyperlink>();
				}
				hyperlinksFound.add(link0);
				if (doAttachmentTableEditorHyperlink) {
					hyperlinksFound.add(new TaskAttachmentTableEditorHyperlink(region, repository, ma.group(1)));
				}
			}
		}

		return (hyperlinksFound != null) ? hyperlinksFound.toArray(new IHyperlink[0]) : null;
	}

}
