/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.commons.workbench.forms.SectionComposite;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.Before;
import org.junit.Test;

public class AbstractRepositoryQueryPage2Test {

	private final class TestRepositoryQueryPage extends AbstractRepositoryQueryPage2 {
		private String suggestedTitle = "";

		private TestRepositoryQueryPage(String pageName, TaskRepository repository, IRepositoryQuery query) {
			super(pageName, repository, query);
		}

		@Override
		protected String suggestQueryTitle() {
			return suggestedTitle;
		}

		@Override
		protected IWizardContainer getContainer() {
			return super.getContainer();
		}

		@Override
		protected void createPageContent(SectionComposite parent) {
		}

		@Override
		protected void doRefreshControls() {
		}

		@Override
		protected boolean hasRepositoryConfiguration() {
			return false;
		}

		@Override
		protected boolean restoreState(IRepositoryQuery query) {
			return false;
		}

		@Override
		public void applyTo(IRepositoryQuery query) {
		}

		public void setSuggestedTitle(String suggestedTitle) {
			this.suggestedTitle = suggestedTitle;
		}
	}

	private TestRepositoryQueryPage page;

	private IRepositoryQuery savedQuery;

	@Before
	public void setUp() {
		TaskRepository repository = new TaskRepository("mock", "http://mock");
		savedQuery = mock(IRepositoryQuery.class);
		page = new TestRepositoryQueryPage("page", repository, savedQuery);
		RepositoryQueryWizard wizard = new RepositoryQueryWizard(repository);
		WizardDialog dialog = new WizardDialog(WorkbenchUtil.getShell(), wizard);
		wizard.setContainer(dialog);
		wizard.addPage(page);
		page.createControl(WorkbenchUtil.getShell());
		dialog.create();
		// simulate the user making a selection which sets the suggestion:
		page.setSuggestedTitle("suggested title");
	}

	@Test
	public void suggestsTitleWhenTitleEmpty() {
		assertEquals("", page.getQueryTitle());
		page.getContainer().updateButtons();
		assertEquals("suggested title", page.getQueryTitle());
	}

	@Test
	public void doesNotSuggestTitleWhenTitleEdited() {
		page.setQueryTitle("test");
		assertEquals("test", page.getQueryTitle());
		page.getContainer().updateButtons();
		assertEquals("test", page.getQueryTitle());

		page.setQueryTitle("testing");
		page.getContainer().updateButtons();
		assertEquals("testing", page.getQueryTitle());
	}

	@Test
	public void suggestsTitleWhenTitleCleared() {
		page.setQueryTitle("test");
		page.getContainer().updateButtons();
		assertEquals("test", page.getQueryTitle());

		page.setQueryTitle("");
		assertEquals("", page.getQueryTitle());
		page.getContainer().updateButtons();
		assertEquals("suggested title", page.getQueryTitle());
	}

	@Test
	public void suggestsTitleWhenSuggestionChanges() {
		page.getContainer().updateButtons();
		assertEquals("suggested title", page.getQueryTitle());

		page.setSuggestedTitle("another suggested title");
		page.getContainer().updateButtons();
		assertEquals("another suggested title", page.getQueryTitle());
	}

	@Test
	public void suggestsTitleWhenSuggestionEmpty() {
		page.getContainer().updateButtons();
		assertEquals("suggested title", page.getQueryTitle());

		page.setSuggestedTitle("");
		assertEquals("suggested title", page.getQueryTitle());
		page.getContainer().updateButtons();
		assertEquals("", page.getQueryTitle());
	}

	@Test
	public void doesNotSuggestTitleWhenEditedTitleEqualsSuggestion() {
		when(savedQuery.getSummary()).thenReturn("saved title");
		page.setQueryTitle("suggested title");
		page.getContainer().updateButtons();
		assertEquals("suggested title", page.getQueryTitle());

		page.setSuggestedTitle("another suggested title");
		page.getContainer().updateButtons();
		assertEquals("suggested title", page.getQueryTitle());
	}

	@Test
	public void doesNotSuggestTitleWhenSavedTitleEqualsSuggestion() {
		when(savedQuery.getSummary()).thenReturn("suggested title");
		page.setQueryTitle("edited title");
		page.getContainer().updateButtons();
		assertEquals("edited title", page.getQueryTitle());

		page.setSuggestedTitle("another suggested title");
		page.getContainer().updateButtons();
		assertEquals("edited title", page.getQueryTitle());
	}

	@Test
	public void doesNotSuggestTitleWhenEditedTitleEqualsSavedTitle() {
		when(savedQuery.getSummary()).thenReturn("saved title");
		page.setQueryTitle("saved title");
		page.getContainer().updateButtons();
		assertEquals("saved title", page.getQueryTitle());

		page.setSuggestedTitle("another suggested title");
		page.getContainer().updateButtons();
		assertEquals("saved title", page.getQueryTitle());
	}

	@Test
	public void suggestsTitleWhenEditingQueryWithSuggestedTitle() {
		when(savedQuery.getSummary()).thenReturn("suggested title");
		page.setQueryTitle("suggested title");
		page.getContainer().updateButtons();
		assertEquals("suggested title", page.getQueryTitle());

		page.setSuggestedTitle("another suggested title");
		page.getContainer().updateButtons();
		assertEquals("another suggested title", page.getQueryTitle());
	}

	@Test
	public void suggestTitleUpdatesCompletion() {
		page.getContainer().updateButtons();
		assertEquals("suggested title", page.getQueryTitle());
		assertTrue(page.isPageComplete());

		page.setSuggestedTitle("");
		page.getContainer().updateButtons();
		assertFalse(page.isPageComplete());
		assertEquals("", page.getQueryTitle());

		page.getContainer().updateButtons();
		assertFalse(page.isPageComplete());
		assertEquals("", page.getQueryTitle());

		page.setSuggestedTitle("another suggested title");
		page.getContainer().updateButtons();
		assertTrue(page.isPageComplete());
		assertEquals("another suggested title", page.getQueryTitle());
	}

}
