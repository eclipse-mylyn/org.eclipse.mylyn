package org.eclipse.mylyn.reviews.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ChooseReviewTypeWizardPage extends WizardPage {

	protected ChooseReviewTypeWizardPage() {
		super("pagename");
		setTitle("Review type");
		setDescription("Select the type of the review");
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		Button patchBasedReview = new Button(composite, SWT.RADIO);
		patchBasedReview.setSelection(true);
		patchBasedReview.setText("Patch based review");
		Button changesetBasedReview = new Button(composite, SWT.RADIO);
		changesetBasedReview.setEnabled(false);
		changesetBasedReview.setText("changeset based review");
		this.setControl(composite);

	}

}