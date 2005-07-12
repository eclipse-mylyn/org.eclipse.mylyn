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
 * Created on Nov 19, 2004
 */
package org.eclipse.mylar.tasks.search;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.BugzillaPreferences;
import org.eclipse.mylar.bugzilla.core.IBugzillaConstants;


/**
 * Utilities methods for the BugzillaMylarBridge
 * 
 * @author Shawn Minto
 */
public class Util {

    /**
     * List of all of the search operations that can be done <br> all words, any words, regex
     */
    private static final String[] patternOperationValues = { "allwordssubstr", "anywordssubstr", "regexp" };
    /**
     * Sugzilla preferences so that we can get the search params 
     */
    private static IPreferenceStore prefs = BugzillaPlugin.getDefault().getPreferenceStore();
    /**
     * List of all of the resolutions that we can have <br> FIXED, INVALID, WONTFIX, LATER, REMIND, DUPLICATE, WORKSFORME, MOVED, ---
     */
    private static String[] resolutionValues = BugzillaPreferences.queryOptionsToArray(prefs.getString(IBugzillaConstants.RESOLUTION_VALUES));
    /**
     * List of all of the statuses that we can have <br> UNCONFIRMED, NEW, ASSIGNED, REOPENED, RESOLVED, VERIFIED, CLOSED
     */
    private static String[] statusValues = BugzillaPreferences.queryOptionsToArray(prefs.getString(IBugzillaConstants.STATUS_VALUES));

    /**
     * Get the bugzilla url used for searching for exact matches
     * 
     * @param je
     *            The IMember to create the query string for
     * @return A url string for the search
     */
    public static String getExactSearchURL(IMember je) {
        StringBuffer sb = getQueryURLStart();
    
        String long_desc = "";
    
        // get the fully qualified name of the element
        long_desc += BugzillaMylarSearchOperation.getFullyQualifiedName(je);
    
        try{
            // encode the string to be used as a url
            sb.append(URLEncoder.encode(long_desc, Charset.defaultCharset().toString()));
        } catch (UnsupportedEncodingException e)
        {
            // should never get here since we are using the default encoding
        }
        sb.append(getQueryURLEnd());
    
        return sb.toString();
    }

    /**
     * Get the bugzilla url used for searching for inexact matches
     * 
     * @param je
     *            The IMember to create the query string for
     * @return A url string for the search
     */
    public static String getInexactSearchURL(IMember je) {
        StringBuffer sb = getQueryURLStart();
    
    
        String long_desc = "";
    
        // add the member, qualified with just its parents name
        if (!(je instanceof IType))
            long_desc += je.getParent().getElementName()+".";
        long_desc += je.getElementName();
    
        try{
            // encode the string to be used as a url
            sb.append(URLEncoder.encode(long_desc, Charset.defaultCharset().toString()));
        } catch (UnsupportedEncodingException e)
        {
            // should never get here since we are using the default encoding
        }
        sb.append(getQueryURLEnd());
    
        return sb.toString();
    }

    /**
     * Create the end of the bugzilla query URL with all of the status' and resolutions that we want
     * @return StringBuffer with the end of the query URL in it
     */
    public static StringBuffer getQueryURLEnd(){
        
        StringBuffer sb = new StringBuffer();
        
        // add the status and resolutions that we care about
        sb.append("&bug_status=" + statusValues[0]); // UNCONFIRMED
        sb.append("&bug_status=" + statusValues[1]); // NEW
        sb.append("&bug_status=" + statusValues[2]); // ASSIGNED
        sb.append("&bug_status=" + statusValues[3]); // REOPENED
        sb.append("&bug_status=" + statusValues[4]); // RESOLVED
        sb.append("&bug_status=" + statusValues[5]); // VERIFIED
        sb.append("&bug_status=" + statusValues[6]); // CLOSED
    
        sb.append("&resolution=" + resolutionValues[0]); // FIXED
        sb.append("&resolution=" + resolutionValues[3]); // LATER
        sb.append("&resolution=" + "---"); // ---
        return sb;
    }

    /**
     * Create the bugzilla query URL start.
     * 
     * @return The start of the query url as a StringBuffer <br>
     *         Example: https://bugs.eclipse.org/bugs/buglist.cgi?long_desc_type=allwordssubstr&long_desc=
     */
    public static StringBuffer getQueryURLStart() {
        StringBuffer sb = new StringBuffer(BugzillaPlugin.getDefault()
                .getServerName());
    
        if (sb.charAt(sb.length() - 1) != '/') {
            sb.append('/');
        }
        sb.append("buglist.cgi?");
    
        // use the username and password if we have it
        if (BugzillaPreferences.getUserName() != null
                && !BugzillaPreferences.getUserName().equals("")
                && BugzillaPreferences.getPassword() != null
                && !BugzillaPreferences.getPassword().equals("")) {
            try{
                sb.append("GoAheadAndLogIn=1&Bugzilla_login="
                    + URLEncoder.encode(BugzillaPreferences.getUserName(), Charset.defaultCharset().toString())
                    + "&Bugzilla_password="
                    + URLEncoder.encode(BugzillaPreferences.getPassword(), Charset.defaultCharset().toString())
                    + "&");
            } catch (UnsupportedEncodingException e)
            {
                // should never get here since we are using the default encoding
            }
        }
        
        // add the description search type
        sb.append("long_desc_type=");
        sb.append(patternOperationValues[0]); // search for all words
        sb.append("&long_desc=");
    
        return sb;
    }
    
    /**
     * Search the given string for another string
     * @param elementName The name of the element that we are looking for
     * @param comment The text to search for this element name
     * @return <code>true</code> if the element is found in the text else <code>false</code>
     */
    public static boolean hasElementName(String elementName, String comment) {
        
        // setup a regex for the element name
        String regexElement = ".*"+elementName+".*";
        
        // get all of the individual lines for the string
        String[] lines = comment.split("\n");

        // go through each of the lines of the string
        for (int i = 0; i < lines.length; i++) {
        
            if (lines[i].matches(regexElement)){
                return true;
            }
        }
        return false;
    }
}