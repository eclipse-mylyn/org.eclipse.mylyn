package org.eclipse.mylar.tasklist.internal;

import org.eclipse.ui.internal.dialogs.PatternFilter;

public class TaskListPatternFilter extends PatternFilter {

	@Override
	public void setPattern(String patternString) {
        if(patternString == null || patternString.startsWith("*")) {
        	super.setPattern(patternString);
        } else {
        	super.setPattern("*" + patternString);
        }
    }
}
