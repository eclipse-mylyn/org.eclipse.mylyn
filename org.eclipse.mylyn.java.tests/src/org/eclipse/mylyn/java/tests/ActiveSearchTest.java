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

package org.eclipse.mylar.java.tests;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.search.IMylarSearchOperation;
import org.eclipse.mylar.core.tests.support.search.SearchPluginTestHelper;
import org.eclipse.mylar.core.tests.support.search.TestActiveSearchListener;
import org.eclipse.mylar.ide.ui.views.ActiveSearchView;
import org.eclipse.mylar.java.search.JavaReferencesProvider;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class ActiveSearchTest extends AbstractJavaContextTest {

	private ActiveSearchView view;
	
	@Override
    protected void setUp() throws Exception {
		super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
	
	public void testSearchAfterDeletion() throws JavaModelException, PartInitException, IOException, CoreException {
		view = (ActiveSearchView)JavaPlugin.getActivePage().showView(ActiveSearchView.ID);
//		view = .getFromActivePerspective();
    	if (view != null) {
	    	assertEquals(0, view.getViewer().getTree().getItemCount());
	    	
	        IWorkbenchPart part = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart();
	        IMethod m1 = type1.createMethod("void m1() {\n m2() \n}", null, true, null);     
	        IMethod m2 = type1.createMethod("void m2() { }", null, true, null);  
	        StructuredSelection sm2 = new StructuredSelection(m2);
	        monitor.selectionChanged(part, sm2);
	        IMylarContextNode node = manager.handleInteractionEvent(mockInterestContribution(
	        		m2.getHandleIdentifier(), scaling.getLandmark()));
	        assertEquals(1, MylarPlugin.getContextManager().getActiveLandmarks().size());
	                
	        assertEquals(1, search(2, node).size());
	//        assertEquals(1, node.getEdges().size());
	        
	        m1.delete(true, null);
	        assertFalse(m1.exists());
	        
	        assertEquals(0, search(2, node).size());
    	}
	} 
	
	public List<?> search(int dos, IMylarContextNode node){
		if(node == null) return null;
		
		JavaReferencesProvider prov = new JavaReferencesProvider();

		TestActiveSearchListener l =new TestActiveSearchListener(prov);
		IMylarSearchOperation o = prov.getSearchOperation(node, IJavaSearchConstants.REFERENCES, dos);
		if(o == null) return null;
		
		SearchPluginTestHelper.search(o, l);
		return l.getResults();
	}
}
