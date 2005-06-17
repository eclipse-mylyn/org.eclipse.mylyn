/*******************************************************************************
 * Copyright (c) 2003 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla;

/**
 * @author kvesik
 *
 * Created on Mar 26, 2003
 */

/**
 * Interface for holding Bugzilla constants.
 */
public interface IBugzillaConstants 
{
	
	// The bugzilla plugin id
	static final String PLUGIN_ID = "org.eclipse.mylar.bugzilla";
	
	// The id's of other bugzilla packages
	static final String EXISTING_BUG_EDITOR_ID = PLUGIN_ID + ".ui.existingBugEditor";
	static final String NEW_BUG_EDITOR_ID = PLUGIN_ID + ".ui.newBugEditor";
	static final String SEARCH_PAGE_ID = PLUGIN_ID + ".search.bugzillaSearchPage";
	static final String SEARCH_PAGE_CONTEXT = PLUGIN_ID + ".bugzillaSearchContext";
	static final String EDITOR_PAGE_CONTEXT = PLUGIN_ID + ".bugzillaEditorContext";
	static final String HIT_MARKER_ID = PLUGIN_ID + ".searchHit";
	
	// The is's for hit markers used in the label provider and sorters
	static final String HIT_MARKER_ATTR_ID = "id";
	static final String HIT_MARKER_ATTR_HREF = "href";
	static final String HIT_MARKER_ATTR_DESC = "description";
	static final String HIT_MARKER_ATTR_LABEL = "label";
	static final String HIT_MARKER_ATTR_SEVERITY = "severity";
	static final String HIT_MARKER_ATTR_PRIORITY = "priority";
	static final String HIT_MARKER_ATTR_PLATFORM = "platform";
	static final String HIT_MARKER_ATTR_STATE = "state";
	static final String HIT_MARKER_ATTR_RESULT = "result";
	static final String HIT_MARKER_ATTR_OWNER = "owner";
	static final String HIT_MARKER_ATTR_QUERY = "query";
	
	// Error code
	static final int ERROR_CODE = 1;
	
	//	Bugzilla Preferences keys
	static final String BUGZILLA_SERVER = "BUGZILLA_SERVER";
	static final String MOST_RECENT_QUERY = "MOST_RECENT_QUERY";
	static final String IS_218 = "BUGZILLA_IS_218";
    
    
	// names for the resources used to hold the different attributes of a bug
	static final String STATUS_VALUES = "STATUS_VALUES";
	static final String PRESELECTED_STATUS_VALUES = "PRESELECTED_STATUS_VALUES";
	static final String RESOLUTION_VALUES = "RESOLUTION_VALUES";
	static final String SEVERITY_VALUES = "SEVERITY_VALUES";
	static final String PRIORITY_VALUES = "PRIORITY_VALUES";
	static final String HARDWARE_VALUES = "HARDWARE_VALUES";
	static final String OS_VALUES = "OS_VALUES";
	static final String PRODUCT_VALUES = "PRODUCT_VALUES";
	static final String COMPONENT_VALUES = "COMPONENT_VALUES";
	static final String VERSION_VALUES = "VERSION_VALUES";
	static final String TARGET_VALUES = "TARGET_VALUES";
	
	// Default values for keys
	static final String DEFAULT_BUGZILLA_SERVER = "https://bugs.eclipse.org/bugs";
	
	static final String[] DEFAULT_STATUS_VALUES = {"Unconfirmed", "New", "Assigned", "Reopened", "Resolved", "Verified", "Closed"};
	static final String[] DEFAULT_PRESELECTED_STATUS_VALUES = {"New", "Assigned", "Reopened"};
	static final String[] DEFAULT_RESOLUTION_VALUES = {"Fixed", "Invalid", "Wontfix", "Later", "Remind", "Duplicate", "Worksforme", "Moved"};
	static final String[] DEFAULT_SEVERITY_VALUES = {"blocker", "critical", "major", "normal", "minor", "trivial", "enhancement"};
	static final String[] DEFAULT_PRIORITY_VALUES = {"P1", "P2", "P3", "P4", "P5"};
	static final String[] DEFAULT_HARDWARE_VALUES = {"All", "Macintosh", "PC", "Power PC", "Sun", "Other"};
	static final String[] DEFAULT_OS_VALUES = {"All", "AIX Motif", "Windows 95", "Windows 98", "Windows CE", "Windows ME", "Windows 2000",
			"Windows NT", "Windows XP", "Windows All", "MacOS X", "Linux", "Linux-GTK", "Linux-Motif", "HP-UX", "Neutrino",
			"QNX-Photon", "Solaris", "Unix All", "other"};
	static final String[] DEFAULT_PRODUCT_VALUES = {"AJDT", "AspectJ", "CDT", "EMF", "Equinox", "GEF", "JDT", "PDE", "Platform", "Stellation", "XSD"};
	static final String[] DEFAULT_COMPONENT_VALUES = {"Access Control", "Ant", "Commandline", "Compare", "Compiler", "Core", "Cpp-Extensions", "Debug", 
			"Doc", "Docs", "draw2d", "Dynamic Plugins", "Fine-Grained", "GEF", "Generic-Extensions", "Help", "IDE", "Launcher", "LPEX", "Plugins", 
			"Releng", "Repository", "Script Tests", "Scripting", "Search", "Server", "SWT", "Text", "UI", "Unit Tests", "Update", "VCM",
			"WebDAV", "Windows Support"};
	static final String[] DEFAULT_VERSION_VALUES = {"0.5", "1.0", "2.0", "2.0.1", "2.0.2", "2.1", "2.2", "unspecified"};
	static final String[] DEFAULT_TARGET_VALUES = {"2.0 M1", "Alpha1", "Alpha2", "Alpha3", "Alpha4", "1.0", "1.0 - 20020308", "1.0 - Release", 
			"2.0 M2", "2.0 - 20020308", "2.0 M3", "2.0 - 20020408", "2.0 M4", "2.0 - 20020508", "2.0 M5", "2.0 - Release", "2.0 M6",
			"2.0 F1", "2.0 F2", "2.0 F3", "2.0 F4", "2.0.1", "2.0.2", 
			"2.1", "2.1 M1", "2.1 M2", "2.1 M3", "2.1 M4", "2.1 M5", "2.1 RC1", "2.1 RC2", "2.1 RC3", "2.2", "Future"};
}
