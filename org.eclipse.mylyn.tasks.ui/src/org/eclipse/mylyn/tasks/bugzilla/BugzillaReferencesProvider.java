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
 * Created on Feb 2, 2005
 */
package org.eclipse.mylar.tasks.bugzilla;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.core.search.IActiveSearchListener;
import org.eclipse.mylar.core.search.IMylarSearchOperation;
import org.eclipse.mylar.core.search.RelationshipProvider;
import org.eclipse.mylar.tasks.bugzilla.search.BugzillaMylarSearch;


/**
 * @author sminto
 */
public class BugzillaReferencesProvider extends RelationshipProvider {

    public static final String ID = "org.eclipse.mylar.bugzilla.search.references";
    public static final String NAME = "Bugilla report references";
    
    public BugzillaReferencesProvider() {
        super(BugzillaStructureBridge.EXTENSION, ID);
    }

    protected boolean acceptElement(IJavaElement javaElement) {
        return javaElement != null 
            && (javaElement instanceof IMember || javaElement instanceof IType);
    }
    
    /**
     * HACK: checking kind as string - don't want the dependancy to mylar.java
     */
    @Override
    protected void findRelated(final ITaskscapeNode node, int degreeOfSeparation) {
        if (!node.getStructureKind().equals("java")) return; 
        IJavaElement javaElement = JavaCore.create(node.getElementHandle());
        if (!acceptElement(javaElement)) {
            return; 
        }
        runJob(node,   degreeOfSeparation);

        //XXX what if degreeOfSeparation is 5?
    }

	@Override
	public IMylarSearchOperation getSearchOperation(ITaskscapeNode node, int limitTo, int degreeOfSepatation) {
		IJavaElement javaElement = JavaCore.create(node.getElementHandle());
		return new BugzillaMylarSearch(degreeOfSepatation, javaElement); 
	}
    
	private void runJob(final ITaskscapeNode node,  final int degreeOfSeparation) {
		BugzillaMylarSearch search = (BugzillaMylarSearch)getSearchOperation(node, 0, degreeOfSeparation);        
		
        search.addListener(new IActiveSearchListener(){

        	private boolean gathered = false;
        	
            public void searchCompleted(List<?> nodes) {
                Iterator<?> itr = nodes.iterator();

                while(itr.hasNext()) {
                    Object o = itr.next();
                    if(o instanceof BugzillaReportNode){
                        BugzillaReportNode bugzillaNode = (BugzillaReportNode)o;
                        incrementInterest(degreeOfSeparation, BugzillaStructureBridge.EXTENSION, bugzillaNode.getElementHandle());
                        }
                    }
                gathered = true;
            }

			public boolean resultsGathered() {
				return gathered;
			}
            
        });
        search.run(new NullProgressMonitor());
	}

    @Override
    protected String getSourceId() {
        return ID;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
