// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString, com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.constructorsOnlyInvokeFinalMethods, useForLoop, com.instantiations.assist.eclipse.analysis.deserializeabilitySecurity, com.instantiations.assist.eclipse.analysis.disallowReturnMutable, com.instantiations.assist.eclipse.analysis.enforceCloneableUsageSecurity, explicitThisUsage
/*******************************************************************************
 * Copyright (c) 2010 Ericsson Research Canada and others
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 
 * This class implements the dialog used to fill-in the Find review items details
 * This is a modeless-like dialog
 * 
 * Contributors:
 *   Ericsson - Created for Mylyn Reviews. Initial use in R4E
 *   
 ******************************************************************************/

package org.eclipse.mylyn.internal.git.ui;

import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.versions.core.Change;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ChangeType;
import org.eclipse.mylyn.versions.core.ScmCore;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.provider.FileRevision;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Sebastien Dubois
 * @version $Revision$
 */
public class GetChangeSetDialog extends FormDialog {

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------
	
	/**
	 * Field ADD_ANOMALY_DIALOG_TITLE.
	 * (value is ""Enter Anomaly details"")
	 */
	private static final String FIND_REVIEW_ITEMS_DIALOG_TITLE = "Find Review Items";

	/**
	 * Field ADD_ANOMALY_DIALOG_VALUE.
	 * (value is ""Enter the Anomaly title:"")
	 */
	private static final String FIND_REVIEW_ITEMS_DIALOG_VALUE = "Review Item Info";

	/**
	 * Field ADD_COMMENT_DIALOG_VALUE.
	 * (value is ""Enter your comments for the new Anomaly:"")
	 */
	private static final String FIND_REVIEW_ITEMS_DESCRIPTION_DIALOG_VALUE = "Review Item Components";
	
	/**
	 * Field COMMIT_INFO_HEADER_MSG.
	 * (value is ""Commit Information"")
	 */
	private static final String COMMIT_INFO_HEADER_MSG = "Commit Information";
	
	/**
	 * Field COMMIT_COMPONENTS_HEADER_MSG.
	 * (value is ""Committed Components"")
	 */
	private static final String COMMIT_COMPONENTS_HEADER_MSG = "Committed Components";
	
	/**
	 * Field DIALOG_COMBO_MAX_CHARACTERS.
	 * (value is 80)
	 */
	private static final int DIALOG_COMBO_MAX_CHARACTERS = 80;

	
	// ------------------------------------------------------------------------
	// Member variables
	// ------------------------------------------------------------------------
    
    /**
     * Input text widget.
     */
	protected final IProject fInputProject;
	
    /**
     * Field fReviewItemDescriptor.
     */
    protected ChangeSet fSelectedChangeSet = null;
    
    /**
     * Field fMessageText.
     */
    Label fMessageText = null;
    
    /**
     * Field fIdText.
     */
    Label fIdText = null;
    
    /**
     * Field fAuthorNameText.
     */
    Label fAuthorNameText = null;
    
    /**
     * Field fAuthorEmailText.
     */
    Label fAuthorEmailText = null;
    
    /**
     * Field fDateText.
     */
    Label fDateText = null;
    
    /**
     * Field fRepositoryNameText.
     */
    Label fRepositoryNameText = null;
    
    /**
     * Field fChangeList.
     */
    org.eclipse.swt.widgets.List fChangeList = null;
    
	/**
	 * Field fDateFormat.
	 */
	final SimpleDateFormat fDateFormat = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss");

	/**
	 * Field fConnector
	 */
	private ScmConnector fConnector = null;

	/**
	 * Field fRepository
	 */
	private ScmRepository fRepository = null;	
	
	
	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------
    
	/**
	 * Constructor for R4EReviewGroupInputDialog.
	 * @param aParentShell Shell
	 * @param aInputProject IProject
	 */
	public GetChangeSetDialog(Shell aParentShell, IProject aInputProject) {
		super(aParentShell);
    	setBlockOnOpen(true);
		fInputProject = aInputProject;
	}
	
	
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------
    
    /**
     * Method configureShell.
     * @param shell Shell
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
    @Override
	protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(FIND_REVIEW_ITEMS_DIALOG_TITLE);
    }
    
	/**
	 * Configures the dialog form and creates form content. Clients should
	 * override this method.
	 * 
	 * @param mform
	 *            the dialog form
	 */
	@Override
	protected void createFormContent(final IManagedForm mform) {

    	try {
    		fConnector = ScmCore.getConnector(fInputProject); 
    		fRepository = fConnector.getRepository(fInputProject, null);
    		final List<ChangeSet> changeSets = fConnector.getChangeSets(fRepository, null);

    		final FormToolkit toolkit = mform.getToolkit();
    		final ScrolledForm sform = mform.getForm();
    		sform.setExpandVertical(true);

            //Main dialog composite
    		final Composite composite = sform.getBody();
    		composite.setLayout(new GridLayout(4, false));
    		
    		//Add Commit List in drop-down menu
            final Label label = toolkit.createLabel(composite, "Available Commits: ");
            label.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
    		final CCombo commitList = new CCombo(composite, SWT.WRAP | SWT.READ_ONLY);
    		for (ChangeSet changeSet : changeSets) {
    			//TODO: We need to filter out ChangeSets that anr not on the current project
    			commitList.add((changeSet.getMessage().length() > DIALOG_COMBO_MAX_CHARACTERS) ? 
    					changeSet.getMessage().substring(0, DIALOG_COMBO_MAX_CHARACTERS) + "..." : changeSet.getMessage());
    		}
    		commitList.setTextLimit(DIALOG_COMBO_MAX_CHARACTERS);
    		commitList.select(0);
    		fSelectedChangeSet = changeSets.get(0);
    		final GridData textGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
            textGridData.horizontalSpan = 3;
    		commitList.setLayoutData(textGridData);
    		commitList.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
		        	fSelectedChangeSet = changeSets.get(commitList.getSelectionIndex());
					refresh();
					commitList.getText();
				}
				public void widgetDefaultSelected(SelectionEvent e) {
					//Nothing to do
				}
			});
    		
    		createReviewItemDetails(toolkit, sform);
    		createReviewItemComponents(toolkit, sform);

    	} catch (CoreException e) {
        	Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
        			IStatus.OK, e.toString(), e));
    	}
    	return;
	}
    
    /**
     * Method createReviewItemDetails.
     * @param aToolkit FormToolkit
     * @param aParent Composite
     */
    private void createReviewItemDetails(FormToolkit aToolkit, final ScrolledForm aParent) {
        
        GridData textGridData = null;

		//Basic parameters section
        final Section basicSection = aToolkit.createSection(aParent.getBody(), Section.DESCRIPTION | ExpandableComposite.TITLE_BAR |
        		  ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
        final GridData basicSectionGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
        basicSectionGridData.horizontalSpan = 4;
        basicSection.setLayoutData(basicSectionGridData);
        basicSection.setText(COMMIT_INFO_HEADER_MSG);
        basicSection.setDescription(FIND_REVIEW_ITEMS_DIALOG_VALUE);
        basicSection.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(ExpansionEvent e){
				aParent.reflow(true);
				getShell().setSize(getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		});
        final Composite basicSectionClient = aToolkit.createComposite(basicSection);
        basicSectionClient.setLayout(new GridLayout(4, false));
        basicSection.setClient(basicSectionClient);
        
	    //Message
        final Label titlelabel = aToolkit.createLabel(basicSectionClient, "Title: ", SWT.WRAP);
	    titlelabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
	    fMessageText = aToolkit.createLabel(basicSectionClient, fSelectedChangeSet.getMessage(), SWT.NONE);
        textGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
        textGridData.horizontalSpan = 3;
        fMessageText.setLayoutData(textGridData);
	    
	    //Id
        final Label idlabel = aToolkit.createLabel(basicSectionClient, "ID: ", SWT.WRAP);
	    idlabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
        fIdText = aToolkit.createLabel(basicSectionClient, fSelectedChangeSet.getId(), SWT.NONE);
        textGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
        textGridData.horizontalSpan = 3;
        fIdText.setLayoutData(textGridData);
	    
	    //Author Name
        final Label authorNamelabel = aToolkit.createLabel(basicSectionClient, "Author Name: ", SWT.WRAP);
        authorNamelabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
        fAuthorNameText = aToolkit.createLabel(basicSectionClient, fSelectedChangeSet.getAuthor().getName(), SWT.NONE);
        textGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
        textGridData.horizontalSpan = 3;
        fAuthorNameText.setLayoutData(textGridData);
	    
	    //Author Email
        final Label authorEmaillabel = aToolkit.createLabel(basicSectionClient, "Author Email: ", SWT.WRAP);
        authorEmaillabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
        fAuthorEmailText = aToolkit.createLabel(basicSectionClient, fSelectedChangeSet.getAuthor().getEmail(), SWT.NONE);
        textGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
        textGridData.horizontalSpan = 3;
        fAuthorEmailText.setLayoutData(textGridData);
        
	    //Date
        final Label datelabel = aToolkit.createLabel(basicSectionClient, "Date: ", SWT.WRAP);
	    datelabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		final String dateStr = fDateFormat.format(fSelectedChangeSet.getDate());
        fDateText = aToolkit.createLabel(basicSectionClient, dateStr, SWT.NONE);
        textGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
        textGridData.horizontalSpan = 3;
        fDateText.setLayoutData(textGridData);
	    
	    //Repository Name
        final Label messagelabel = aToolkit.createLabel(basicSectionClient, "Repository: ", SWT.WRAP);
	    messagelabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
	    fRepositoryNameText = aToolkit.createLabel(basicSectionClient, fSelectedChangeSet.getRepository().getName(), SWT.NONE);
        textGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
        textGridData.horizontalSpan = 3;
        fRepositoryNameText.setLayoutData(textGridData);
	    
	    basicSectionClient.layout();
    }
    
    /**
     * Method createReviewItemComponents.
     * @param aToolkit FormToolkit
     * @param aParent Composite
     */
    private void createReviewItemComponents(FormToolkit aToolkit, final ScrolledForm aParent) {
        
        //Extra parameters section
        final Section extraSection = aToolkit.createSection(aParent.getBody(), Section.DESCRIPTION | ExpandableComposite.TITLE_BAR |
        		  ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
        final GridData extraSectionGridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        extraSectionGridData.horizontalSpan = 4;
        extraSection.setLayoutData(extraSectionGridData);
        extraSection.setText(COMMIT_COMPONENTS_HEADER_MSG);
        extraSection.setDescription(FIND_REVIEW_ITEMS_DESCRIPTION_DIALOG_VALUE);
        extraSection.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(ExpansionEvent e){
				aParent.reflow(true);
				getShell().setSize(getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		});
        
        final Composite extraSectionClient = aToolkit.createComposite(extraSection);
        extraSectionClient.setLayout(new GridLayout(4, false));
        extraSectionClient.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        extraSection.setClient(extraSectionClient);
              
        //Components List
        fChangeList = new org.eclipse.swt.widgets.List(extraSectionClient, SWT.V_SCROLL | SWT.H_SCROLL);
        updateChangeSet(fSelectedChangeSet);
        for (Change change : fSelectedChangeSet.getChanges()) {
			String path = null;
			if (change.getChangeType().equals(ChangeType.DELETED)) {
				path = change.getBase().getPath();
			} else {
				path = change.getTarget().getPath();
			}
			fChangeList.add(path);
        }
        final GridData data = new GridData(GridData.FILL, GridData.FILL, true, true);
        fChangeList.setLayoutData(data);
    }
    
	/**
	 * Method updateChangeSet.
	 * @param aSelectedChangeSet ChangeSet
	 */
	private void updateChangeSet(ChangeSet aSelectedChangeSet) {
		String changeSetId = aSelectedChangeSet.getId();
//		IFileRevisio
		IFileRevision fileRevision = createFileRevision(changeSetId);
		try {
			fSelectedChangeSet = fConnector.getChangeSet(fRepository, fileRevision, null);
		} catch (CoreException e) {
        	Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
        			IStatus.OK, e.toString(), e));
		}
	}

	/**
	 * Method createFileRevision.
	 * @param changeSetId String
	 * @return IFileRevision
	 */
	private IFileRevision createFileRevision(final String changeSetId) {
		IFileRevision fileRevision = new FileRevision() {
			
			public IFileRevision withAllProperties(IProgressMonitor monitor)
					throws CoreException {
				return null;
			}
			
			public boolean isPropertyMissing() {
				return false;
			}
			
			public IStorage getStorage(IProgressMonitor monitor) throws CoreException {
				return null;
			}

			@Override
			public String getContentIdentifier() {
				return changeSetId;				
			}

			public String getName() {
				return null;
			}
		};
		return fileRevision;
	}


	/**
	 * Method refresh.
	 */
    void refresh() {
    	fMessageText.setText(fSelectedChangeSet.getMessage());
    	fIdText.setText(fSelectedChangeSet.getId());
    	fDateText.setText(fDateFormat.format(fSelectedChangeSet.getDate()));  //BUG!
    	fAuthorNameText.setText(fSelectedChangeSet.getAuthor().getName());
    	fAuthorEmailText.setText(fSelectedChangeSet.getAuthor().getEmail());
    	fRepositoryNameText.setText(fSelectedChangeSet.getRepository().getUrl());
    	fChangeList.removeAll();
        updateChangeSet(fSelectedChangeSet);

        for (Change change : fSelectedChangeSet.getChanges()) {
			String path = null;
			ChangeType type = change.getChangeType();
			if (type.equals(ChangeType.DELETED)) {
				path = change.getBase().getPath();
			} else {
				path = change.getTarget().getPath();
			}

			fChangeList.add(path);
        }
    }
     
	/**
	 * Method isResizable.
	 * @return boolean
	 * @see org.eclipse.jface.dialogs.Dialog#isResizable()
	 */
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	/**
	 * Method getChangeSet.
	 * @return ChangeSet
	 */
	public ChangeSet getChangeSet() {
		return fSelectedChangeSet;
	}
}
