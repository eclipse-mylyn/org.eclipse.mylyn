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
 * Created on Oct 21, 2004
 */
package org.eclipse.mylar.tasks.bugzilla;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.BugzillaRepository;
import org.eclipse.mylar.bugzilla.search.BugzillaSearchHit;


/**
 * Class to store the DoiInfo of a BugzillaSearchHit
 * 
 * @author sminto
 */
public class BugzillaReportNode {

    private static final long serialVersionUID = 3257004367222419506L;

    /** The BugzillaSearchHit associated with this DoiInfo */
    private BugzillaSearchHit hit;

    /** Whether this search hit was from an exact search like a stack trace */
    private boolean isExact = false;

    /** List of all of the StackTrace's in the given bug */
    private List<StackTrace> stackTraces;

    /** The bug report associated with this DoiInfo */
    private BugReport bug;

    /**
     * Constructor
     * 
     * @param initialValue
     *            The initial Doi value
     * @param hit
     *            The BugzillaSearchHit associated with this DoiInfo
     * @param isExact
     *            Whether the search was exact or not
     */
    public BugzillaReportNode(float initialValue, BugzillaSearchHit hit,
            boolean isExact) { 
        this.hit = hit;
        this.isExact = isExact;
        bug = null;
        stackTraces = new ArrayList<StackTrace>();
    }

    /**
     * Get the bugzilla search hit relating to this DoiInfo
     * 
     * @return The BugzillaSearchHit related to this DoiInfo
     */
    public BugzillaSearchHit getHit() {
        return hit;
    }
    
    @Override
    public String toString() {
        return hit.toString();
    }

    /**
     * Determine if the search hit this represents is exact or not
     * 
     * @return <code>true</code> if the search was exact otherwise
     *         <code>false</code>
     */
    public boolean isExact() {
        return isExact;
    }

    /**
     * Set whether this bug has any exact elements in it - the search used was fully qualified
     * 
     * @param isExact -
     *            Whether there are any exact element matches in it
     */
    public void setExact(boolean isExact) {
        this.isExact = isExact;
    }

    /**
     * Get the bug report associated with this DoiInfo<br>
     * The bug is downloaded if it was not previously
     * 
     * @return Returns the BugReport
     * 
     * @throws IOException
     * @throws LoginException
     * @throws MalformedURLException
     */
    public BugReport getBug() throws MalformedURLException, LoginException, IOException {
        if(bug == null){
            
            // get the bug report
	        bug = BugzillaRepository.getInstance().getBug(
	                        hit.getId());        
        }
		return bug;
    }

    /**
     * Set the bug report associated with this DoiInfo
     * 
     * @param bug -
     *            BugReport that this is associated with
     */
    public void setBug(BugReport bug) {
        this.bug = bug;
    }

    /**
     * Get all of the stack traces contained in the bug
     * 
     * @return Returns a list of StackTrace's
     */
    public List<StackTrace> getStackTraces() {
        return stackTraces;
    }
    
    /**
     * Determine whether the doi info has any stack traces associated with it
     * @return <code>true</code> if there are some stack traces else <code>false</code>
     */
    public boolean hasStackTraces(){
        return !stackTraces.isEmpty();
    }
 
    /**
     * Add a stack trace to this DoiInfo
     * 
     * @param stackTrace -
     *            The StackTrace to add
     */
    public void addStackTrace(StackTrace stackTrace) {
        this.stackTraces.add(stackTrace);
    }

    /**
     * Add an array of stack traces to this DoiInfo
     * 
     * @param stackTracesToAdd -
     *            The StackTraces to add
     */
    public void addStackTraces(StackTrace[] stackTracesToAdd) {
        for (int i = 0; i < stackTracesToAdd.length; i++)
            this.stackTraces.add(stackTracesToAdd[i]);
    }
    
    /**
     * Get the name of the bug report
     * @return The name of the bug report, max 20 characters
     */
    public String getName() {
        final int MAX_LENGTH = 100;
        String description = hit.getDescription();
        int length = description.length(); 
        if (length > MAX_LENGTH) description = description.substring(0, MAX_LENGTH) + "..";
        return "bug " + hit.getId() + ": " + description;
    }

    public String getElementHandle() {
        return "<server-id>;" + hit.getId();
    }
}