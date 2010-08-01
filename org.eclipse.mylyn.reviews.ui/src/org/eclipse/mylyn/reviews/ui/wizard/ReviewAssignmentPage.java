package org.eclipse.mylyn.reviews.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ReviewAssignmentPage extends WizardPage {

	private Text reviewerText;
//	private Button openOnFinish;

	protected ReviewAssignmentPage() {
		super("ReviewAssignmentPage");
		setTitle("Review Assignment");
		setDescription("Select the user, who will conduct the review");
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2,false));
		createLabel(composite, "Reviewer");
		reviewerText = new Text(composite, SWT.BORDER);
		reviewerText.setLayoutData(new GridData(SWT.FILL,SWT.TOP, true, false));

		String userName = ((CreateReviewWizard)getWizard()).getModel().getTaskRepository().getUserName();
		if(userName!=null) {
			setPageComplete(true);
			reviewerText.setText(userName);
		}
		reviewerText.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				setPageComplete(!reviewerText.getText().isEmpty());
				
			}
		});
		createLabel(composite, "");
//		openOnFinish = new Button(composite, SWT.CHECK);
//		openOnFinish.setText("Open review task on finish");
		setControl(composite);
	}

	private void createLabel(Composite composite, String string) {
		Label label = new Label(composite,SWT.NONE);
		label.setText(string);
	}

	public String getReviewer() {
		return reviewerText.getText();
	}
	public boolean isOpenReviewOnFinish() {
		return true; //openOnFinish.getSelection();
	}
}
