/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.wizards;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;

/**
 * 
 * @author Jeff Pound
 */
public abstract class AbstractDuplicateDetectingReportWizard extends Wizard {

	private Queue<IWizardPage> queue = new LinkedList<IWizardPage>();

	public AbstractDuplicateDetectingReportWizard() {
		setNeedsProgressMonitor(true);
	}

	public void queuePage(IWizardPage page) {
		queue.add(page);
	}

	public void addQueuedPages() {
		Iterator<IWizardPage> iter = queue.iterator();
		while (iter.hasNext()) {
			addPage(iter.next());
		}
	}

	public boolean canFinish() {
		IWizardPage findDups = getPage(FindRelatedReportsPage.PAGE_NAME);
		if (findDups == null) {
			return super.canFinish();
		}

		return !findDups.equals(getContainer().getCurrentPage()) && super.canFinish();
	}

	public List<AbstractRepositoryTask> getSelectedDuplicates() {
		DisplayRelatedReportsPage displayDups = (DisplayRelatedReportsPage) getPage(DisplayRelatedReportsPage.PAGE_NAME);
		if (displayDups == null) {
			return null;
		}

		return displayDups.getSelectedReports();
	}

	public abstract List<AbstractRepositoryTask> searchForDuplicates(DuplicateDetectionData data);
}
