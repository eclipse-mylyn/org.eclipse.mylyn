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
 * Created on Mar 14, 2005
  */
package org.eclipse.mylar.java.ui.editor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProcessor;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.LazyJavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.MemberProposalInfo;
import org.eclipse.jdt.internal.ui.text.java.ProposalInfo;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.ContextManager;
import org.eclipse.mylar.dt.MylarWebRef;
import org.eclipse.ui.IEditorPart;


/**
 * @author Mik Kersten
 * 
 * HACK: uses reflection to get around accessibility restriction.
 */
public class MylarJavaCompletionProcessor extends JavaCompletionProcessor {

    public MylarJavaCompletionProcessor(IEditorPart editor) {
        super(editor); 
    }
    
    @MylarWebRef(name="Reflection documentation", url="http://www.onjava.com/pub/a/onjava/2003/11/12/reflection.html?page=last")
    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {    
        try {
            Method method = MemberProposalInfo.class.getDeclaredMethod("resolveMember", new Class[] { } );
            method.setAccessible(true);
            ICompletionProposal[] proposals = super.computeCompletionProposals(viewer, offset);
    
            TreeMap<Float, ICompletionProposal> interesting = new TreeMap<Float, ICompletionProposal>();
            List<ICompletionProposal> rest = new ArrayList<ICompletionProposal>();
            for (int i = 0; i < proposals.length; i++) {
                ICompletionProposal proposal = proposals[i];
                ProposalInfo info = null;
                if (proposal instanceof JavaCompletionProposal) {
                    info = ((JavaCompletionProposal)proposal).getProposalInfo();
                } else if (proposal instanceof LazyJavaCompletionProposal) {
                    info = ((LazyJavaCompletionProposal)proposal).getProposalInfo();
                } 
                boolean added = false;
                try { // HACK
                    if (info != null) {
                        IMember member = null; 
                        if (info instanceof MemberProposalInfo) member = (IMember)method.invoke(info, new Object[] { });
                        if (member == null || MylarPlugin.getContextManager().getActiveContext() == null) {
                            // nothing for now
                        	rest.add(proposal);
                        } else {
                        	IMylarContextNode node = MylarPlugin.getContextManager().getNode(member.getHandleIdentifier()); 
                            if (node != null) {
                            	float interest = node.getDegreeOfInterest().getValue();
	                            if (interest > ContextManager.getScalingFactors().getInteresting()) {
	                                interesting.put(-interest, proposal);  // negative to invert sorting order
	                            } else {
	                            	rest.add(proposal);
	                            }
                            }
                        }
                        added = true;
                    }
                } catch (Exception e) {
                	MylarPlugin.log(e, "proposals problem");
                } 
                if (!added) rest.add(proposal);
            }
            if (interesting.keySet().size() == 0) {
                return proposals;
            } else {
                ICompletionProposal[] sorted = new ICompletionProposal[interesting.keySet().size() + rest.size() + 1];
                int i = 0;
                for (Float f : interesting.keySet()) {
                    sorted[i] = interesting.get(f);
                    i++; 
                }    
                if (interesting.keySet().size() > 0) {
                    int replacementOffset = -1;
                    if (sorted[i-1] instanceof JavaCompletionProposal) {
                        replacementOffset = ((JavaCompletionProposal)sorted[i-1]).getReplacementOffset();
                    } else if (sorted[i-1] instanceof LazyJavaCompletionProposal) {
                        replacementOffset = ((LazyJavaCompletionProposal)sorted[i-1]).getReplacementOffset();
                    } else {
                        MylarPlugin.log("Could not create proposal separator for class: " + sorted[i-1].getClass(), this);
                    }
                    sorted[i] = new JavaCompletionProposal("", replacementOffset, 0, null, "----------------", 0);
                    i++;
                } 
                for (ICompletionProposal proposal : rest) {
                    sorted[i] = proposal;
                    i++;
                }
                return sorted;
            }
        } catch (Exception e) {
        	MylarPlugin.log(e, "completion proposal failed");
        }
        return null;
    } 
}

//M6 way of doing it:
//info.getInfo(); // causes getMember() to be called;
//Class infoClass = info.getClass();
//Field field = infoClass.getDeclaredField("fMember");
//field.setAccessible(true);   
//member = (IMember)field.get(info);

