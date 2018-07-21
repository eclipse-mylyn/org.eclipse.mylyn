/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies.
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
package org.eclipse.mylyn.internal.tasks.index.ui;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.internal.tasks.index.core.TaskListIndex;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.search.AbstractSearchHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema.Field;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;

/**
 * @author David Green
 */
public class IndexSearchHandler extends AbstractSearchHandler {

	private IndexReference reference;

	public IndexSearchHandler() {
		reference = new IndexReference();
	}

	@Override
	public Composite createSearchComposite(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayoutFactory.swtDefaults().applyTo(container);

		final Button button = new Button(container, SWT.CHECK);
		button.setText(Messages.IndexSearchHandler_summaryOnly);
		button.setToolTipText(Messages.IndexSearchHandler_summaryOnly_tooltip);
		button.setSelection(true);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Field newDefaultField = button.getSelection()
						? TaskListIndex.FIELD_SUMMARY
						: TaskListIndex.FIELD_CONTENT;
				reference.index().setDefaultField(newDefaultField);
				fireFilterChanged();
			}
		});

		return container;
	}

	@Override
	public PatternFilter createFilter() {
		return new IndexedSubstringPatternFilter(reference.index());
	}

	@Override
	public void adaptTextSearchControl(final Text textControl) {
		// make room for content assist decoration
		if (textControl.getParent().getLayout() instanceof GridLayout) {
			((GridLayout) textControl.getParent().getLayout()).marginLeft = 6;
		}

		// delay execution of to avoid empty key-binding in tooltip: The problem is that the binding service hasn't been 
		// initialized when the decoration is created on startup.
		textControl.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (!textControl.isDisposed()) {
					adaptTextSearchControlInternal(textControl);
				}
			}
		});
	}

	private void adaptTextSearchControlInternal(Text textControl) {
		IContentProposalProvider proposalProvider = new ContentProposalProvider(TasksUiPlugin.getTaskList(), reference);
		final ContentAssistCommandAdapter adapter = new ContentAssistCommandAdapter(textControl,
				new TextContentAdapter(), proposalProvider, null, new char[0], true);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

		// FilteredTree registers a traverse listener that focuses the tree when ENTER is pressed. This 
		// causes focus to be lost when a content proposal is selected. To avoid transfer of focus the 
		// traverse listener registered by FilteredTree is skipped while content assist is being used.
		Listener[] traverseListeners = textControl.getListeners(SWT.Traverse);
		for (final Listener listener : traverseListeners) {
			if (listener.getClass() == TypedListener.class) {
				// replace listener with delegate that filters events
				textControl.removeListener(SWT.Traverse, listener);
				textControl.addListener(SWT.Traverse, new Listener() {
					public void handleEvent(Event event) {
						if (!adapter.isProposalPopupOpen()) {
							listener.handleEvent(event);
						}
					}
				});
			}
		}
	}

	@Override
	public void dispose() {
		reference.dispose();
		reference = null;
	}

}
