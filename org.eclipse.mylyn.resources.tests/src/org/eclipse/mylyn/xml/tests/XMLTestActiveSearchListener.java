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
package org.eclipse.mylar.xml.tests;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.resources.File;
import org.eclipse.mylar.core.AbstractRelationshipProvider;
import org.eclipse.mylar.core.tests.support.search.TestActiveSearchListener;
import org.eclipse.mylar.xml.XmlNodeHelper;
import org.eclipse.search.internal.ui.text.FileSearchResult;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.part.FileEditorInput;


public class XMLTestActiveSearchListener extends TestActiveSearchListener {

	private List<?> results = null;
	
	public XMLTestActiveSearchListener(AbstractRelationshipProvider prov){
		super(prov);
	}
	
	private boolean gathered = false; 
		
	@Override
	public void searchCompleted(List<?> l)
	{

		results = l;
		
	   //  deal with File
       if(l.isEmpty()){
    	   gathered = true;
    	   return;
       }
       
       if (l.get(0) instanceof FileSearchResult){
           FileSearchResult fsr = (FileSearchResult) l.get(0);
   		   List<XmlNodeHelper> nodes = new ArrayList<XmlNodeHelper>();
           Object[] far = fsr.getElements();
           for(int i = 0; i < far.length; i++){
               Match[] mar = fsr.getMatches(far[i]);

               if(far[i] instanceof File){
                   File f = (File)far[i];
                   
                   // change the file into a document
                   FileEditorInput fei = new FileEditorInput(f);
            
                   for(int j = 0; j < mar.length; j++){
                       Match m = mar[j];
                       try{
                           XmlNodeHelper xnode = new XmlNodeHelper(fei, m.getOffset());
                           if(xnode != null){
                               nodes.add(xnode);
                           }
                       } catch(Exception e){
                    	   // don't care about this exception
                       }
                   }
               }
           }
           results = nodes;
       }
       gathered = true;
	}

	@Override
	public boolean resultsGathered() {
		return gathered;
	}
	
	@Override
	public List<?> getResults(){
		return results;
	}

}
