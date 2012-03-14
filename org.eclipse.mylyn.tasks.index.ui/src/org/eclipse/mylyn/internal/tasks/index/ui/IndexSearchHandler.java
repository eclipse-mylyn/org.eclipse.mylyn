/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.index.ui;

import java.lang.reflect.Method;

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
	public void adaptTextSearchControl(Text textControl) {
		IContentProposalProvider proposalProvider = new ContentProposalProvider(TasksUiPlugin.getTaskList(), reference);
		final ContentAssistCommandAdapter adapter = new ContentAssistCommandAdapter(textControl,
				new TextContentAdapter(), proposalProvider, null, new char[0], true);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

		if (textControl.getParent().getLayout() instanceof GridLayout) {
			((GridLayout) textControl.getParent().getLayout()).marginLeft = 4;
		}

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
						// TODO e3.6 replace with call to adapter.isProposalPopupOpen()
						boolean popupOpen = false;
						try {
							Method method = ContentProposalAdapter.class.getDeclaredMethod("isProposalPopupOpen"); //$NON-NLS-1$
							popupOpen = (Boolean) method.invoke(adapter);
						} catch (Exception e) {
							// ignore, Eclipse 3.5 does not support this API
						}
						if (!popupOpen) {
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
