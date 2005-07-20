/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Jul 27, 2004
  */
package org.eclipse.mylar.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.core.AbstractRelationshipProvider;
import org.eclipse.mylar.core.IDegreeOfSeparation;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;


/**
 * @author Mik Kersten
 */
public class ToggleRelationshipProviderAction extends Action implements IMenuCreator{

//    private final String PREFIX = "org.eclipse.mylar.ui.relatedElements.providers";
//    private String prefId = "org.eclipse.mylar.ui.relatedElements.providers";
    
    IMylarStructureBridge bridge;
    private Menu dropDownMenu = null;
        
	public ToggleRelationshipProviderAction(IMylarStructureBridge bridge) {
		super();
		
        this.bridge = bridge;
//        this.prefId = PREFIX + '.' + provider.getId().toString();
        
        //HACK:
        AbstractRelationshipProvider provider = bridge.getProviders().get(0);
        ImageDescriptor image = MylarUiPlugin.getDefault().getUiBridge(provider.getStructureKind()).getIconForRelationship(provider.getGenericId());
		setText(provider.getId().toString());
        setToolTipText(provider.getId().toString());
		setImageDescriptor(image);

		setMenuCreator(this);	
		
		// HACK: if there are multiple providers, all store the same current DOS
		degreeOfSeparation = bridge.getProviders().get(0).getCurrentDegreeOfSeparation();
		
		if(degreeOfSeparation > 0)
			run();
		
//		boolean checked= MylarUiPlugin.getPrefs().getBoolean(prefId); 
//		valueChanged(checked, true); 
	}   
	 
	@Override
	public void run() {
		MylarPlugin.getContextManager().updateSearchKindEnabled(bridge.getProviders(), degreeOfSeparation);
//		valueChanged(isChecked(), true);
	}
//	
//	private void valueChanged(final boolean on, boolean store) {
//		setChecked(on);
//		if (store) MylarUiPlugin.getPrefs().setValue(prefId, on); //$NON-NLS-1$
//		provider.setEnabled(on);
//        MylarPlugin.getContextManager().updateSearchKindEnabled(provider, on, degreeOfSeparation);
//	}
	
	
	public void dispose() {			
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
			dropDownMenu = null;
		}
	}

	public Menu getMenu(Control parent) {			
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
		}
		dropDownMenu = new Menu(parent);
		addActionsToMenu();
		return dropDownMenu;
	}

	public Menu getMenu(Menu parent) {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
		}
		dropDownMenu = new Menu(parent);
		addActionsToMenu();
		return dropDownMenu;
	}    
	
	private int degreeOfSeparation = 0;
	
	public void addActionsToMenu() {

		// HACK: if there are multiple providers, all store the same current DOS
		degreeOfSeparation = bridge.getProviders().get(0).getCurrentDegreeOfSeparation();
		
		MenuItem menuItem = new MenuItem(dropDownMenu, SWT.NONE);
		menuItem.setText("Degree Of Separation");
		menuItem.setEnabled(false);
		
		menuItem = new MenuItem(dropDownMenu, SWT.SEPARATOR);
		
		for(IDegreeOfSeparation separation: bridge.getDegreesOfSeparation()){
								
			Action degreeOfSeparationSelectionAction = new Action(separation.getDegree() + ": " + separation.getLabel(), AS_CHECK_BOX) {	    		
	    		@Override
				public void run() {
	    			try{
		    			degreeOfSeparation = Integer.parseInt(getId());
		    			MylarPlugin.getContextManager().updateSearchKindEnabled(bridge.getProviders(), degreeOfSeparation);
	    			} catch (NumberFormatException e){
	    				// ignore this for now
	    			}
				}
			};  
			degreeOfSeparationSelectionAction.setId(""+separation.getDegree());
			degreeOfSeparationSelectionAction.setEnabled(true);
			degreeOfSeparationSelectionAction.setToolTipText(separation.getLabel());
			ActionContributionItem item= new ActionContributionItem(degreeOfSeparationSelectionAction);
			item.fill(dropDownMenu, -1);
			
			degreeOfSeparationSelectionAction.setChecked(false);
			if(degreeOfSeparation == 0 && separation.getDegree() == 0){
				degreeOfSeparationSelectionAction.setChecked(true);
			}else if (degreeOfSeparation != 0 && separation.getDegree() != 0 && degreeOfSeparation >= separation.getDegree()) {
				degreeOfSeparationSelectionAction.setChecked(true);
			}
		}
	}
}

