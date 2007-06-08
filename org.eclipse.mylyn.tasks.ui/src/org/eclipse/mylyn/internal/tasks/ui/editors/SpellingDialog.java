/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * TODO this is used only for spell checking which is not yet implemented,
 * therefore this is not properly tested
 * 
 * @author Shawn Minto
 */
public class SpellingDialog extends Dialog {

	private String title;

	private Text wordToFix;

	private List suggestions;

	private IDocument document;

	private ICompletionProposal[] proposals;

	protected SpellingDialog(Shell parentShell, String title, IDocument document) {
		super(parentShell);
		this.title = title;
		this.document = document;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Control c = super.createDialogArea(parent);

		Composite spellingComposite = new Composite(parent, SWT.NONE);

		GridLayout spellingLayout = new GridLayout();
		spellingLayout.numColumns = 1;
		spellingComposite.setLayout(spellingLayout);

		wordToFix = new Text(spellingComposite, SWT.BORDER | SWT.READ_ONLY);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.widthHint = 150;
		wordToFix.setLayoutData(gd);

		suggestions = new List(spellingComposite, SWT.BORDER);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.widthHint = 150;
		gd.heightHint = AbstractRepositoryTaskEditor.RADIO_OPTION_WIDTH;
		suggestions.setLayoutData(gd);

		return c;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}

	public void open(String word, ICompletionProposal[] proposals) {
		create();

		this.proposals = proposals;

		wordToFix.setText(word);
		suggestions.removeAll();

		for (int i = 0; i < proposals.length; i++) {
			suggestions.setItem(i, proposals[i].getDisplayString());
		}

		super.open();
	}

	@Override
	protected void handleShellCloseEvent() {
		if (getReturnCode() == Dialog.OK) {
			int i = suggestions.getSelectionIndex();
			if (i > 0 && i < proposals.length)
				proposals[i].apply(document);
		}
		super.handleShellCloseEvent();
	}

}
