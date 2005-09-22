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
 * Created on Jul 13, 2004
  */
package org.eclipse.mylar.monitor.internal;

import java.util.*;

import org.eclipse.mylar.core.util.DateUtil;



/**
 * @author Mik Kersten
 */
public class UsageSession {

    public static final String START_DATE = "startDate";
    public static final String START_TIME = "startTime";
    public static final String NUM_SECONDS_ELAPSED = "numSecondsElapsed";
    public static final String NUM_KEYSTROKES_JAVA_EDITOR = "numKeystrokesJavaEditor";
    public static final String NUM_SELECTIONS_PKG_EXPLORER = "numSelectionsPkgExplorer";
    public static final String NUM_SELECTIONS_PKG_EXPLORER_MYLAR = "numSelectionsPkgExplorerMylar";
    public static final String NUM_SELECTIONS_PROBLEMS = "numSelectionsProblems";
    public static final String NUM_SELECTIONS_PROBLEMS_MYLAR = "numSelectionsProblemsMylar";
    public static final String NUM_SELECTIONS_JAVA_OUTLINE = "numSelectionsJavaOutline";
    public static final String NUM_SELECTIONS_JAVA_OUTLINE_MYLAR = "numSelectionsJavaOutlineMylar";
    public static final String NUM_SELECTIONS_JAVA_EDITOR = "numSelectionsJavaEditor";
    public static final String NUM_SELECTIONS_JAVA_EDITOR_AUTOFOLD = "numSelectionsJavaEditorAutoFold";
    public static final String NUM_SELECTIONS_SEARCH = "numSelectionsSearch";
    public static final String NUM_SELECTIONS_SEARCH_MYLAR = "numSelectionsSearchMylar";
    public static final String NUM_SELECTIONS_PATHFINDER = "numSelectionsPathfinder";
    public static final String NUM_SELECTIONS_OTHER = "numSelectionsOther";
    
    private List<UsageStatistic> statistics; 
    private static final String DELIM = ", ";
   
	
	public UsageSession() {
	    
	    statistics = new ArrayList<UsageStatistic>();
	    
	    statistics.add(new TemporalStatistic(START_DATE, DateUtil.getFormattedDate(), null));
	    statistics.add(new TemporalStatistic(START_TIME, DateUtil.getFormattedTime(), null));
	    
	    statistics.add(new CardinalStatistic(NUM_KEYSTROKES_JAVA_EDITOR));   
	    statistics.add(new CardinalStatistic(NUM_SECONDS_ELAPSED));
//	    statistics.add(new CardinalStatistic(NUM_SELECTIONS_PATHFINDER));
	    statistics.add(new CardinalStatistic(NUM_SELECTIONS_JAVA_EDITOR));
//	    statistics.add(new CardinalStatistic(NUM_SELECTIONS_JAVA_EDITOR_AUTOFOLD));
	    statistics.add(new CardinalStatistic(NUM_SELECTIONS_JAVA_OUTLINE));
//	    statistics.add(new CardinalStatistic(NUM_SELECTIONS_JAVA_OUTLINE_MYLAR));
	    statistics.add(new CardinalStatistic(NUM_SELECTIONS_OTHER));
	    statistics.add(new CardinalStatistic(NUM_SELECTIONS_PKG_EXPLORER));
//	    statistics.add(new CardinalStatistic(NUM_SELECTIONS_PKG_EXPLORER_MYLAR));
	    statistics.add(new CardinalStatistic(NUM_SELECTIONS_PROBLEMS));
//	    statistics.add(new CardinalStatistic(NUM_SELECTIONS_PROBLEMS_MYLAR));
	    statistics.add(new CardinalStatistic(NUM_SELECTIONS_SEARCH));
//	    statistics.add(new CardinalStatistic(NUM_SELECTIONS_SEARCH_MYLAR));
	}
	
	/**
	 * Read in alphabetical order.
	 * 
	 * @param csv string that this session should be created from
	 */
	public UsageSession(String fromCsvString) {
	    this();
	    
	    StringTokenizer tokenizer = new StringTokenizer(fromCsvString, DELIM);
	    getTemporalStatistic(START_DATE).setTime(tokenizer.nextToken());
	    getTemporalStatistic(START_TIME).setTime(tokenizer.nextToken());
	    getCardinalStatistic(NUM_KEYSTROKES_JAVA_EDITOR).setCount(new Long(tokenizer.nextToken()).longValue());   
	    getCardinalStatistic(NUM_SECONDS_ELAPSED).setCount(new Long(tokenizer.nextToken()).longValue());
//	    getCardinalStatistic(NUM_SELECTIONS_PATHFINDER).setCount(new Long(tokenizer.nextToken()).longValue());
	    getCardinalStatistic(NUM_SELECTIONS_JAVA_EDITOR).setCount(new Long(tokenizer.nextToken()).longValue());
//	    getCardinalStatistic(NUM_SELECTIONS_JAVA_EDITOR_AUTOFOLD).setCount(new Long(tokenizer.nextToken()).longValue());
	    getCardinalStatistic(NUM_SELECTIONS_JAVA_OUTLINE).setCount(new Long(tokenizer.nextToken()).longValue());
//	    getCardinalStatistic(NUM_SELECTIONS_JAVA_OUTLINE_MYLAR).setCount(new Long(tokenizer.nextToken()).longValue());
	    getCardinalStatistic(NUM_SELECTIONS_OTHER).setCount(new Long(tokenizer.nextToken()).longValue());
	    getCardinalStatistic(NUM_SELECTIONS_PKG_EXPLORER).setCount(new Long(tokenizer.nextToken()).longValue());
//	    getCardinalStatistic(NUM_SELECTIONS_PKG_EXPLORER_MYLAR).setCount(new Long(tokenizer.nextToken()).longValue());
	    getCardinalStatistic(NUM_SELECTIONS_PROBLEMS).setCount(new Long(tokenizer.nextToken()).longValue());
//	    getCardinalStatistic(NUM_SELECTIONS_PROBLEMS_MYLAR).setCount(new Long(tokenizer.nextToken()).longValue());
	    getCardinalStatistic(NUM_SELECTIONS_SEARCH).setCount(new Long(tokenizer.nextToken()).longValue());
//	    getCardinalStatistic(NUM_SELECTIONS_SEARCH_MYLAR).setCount(new Long(tokenizer.nextToken()).longValue());    		        
	}

	/**
	 * @return a short one line summary string
	 */
    public String getSummary() {
        long selections = 
//	    	getCardinalStatistic(NUM_SELECTIONS_PATHFINDER).getCount() +
		    getCardinalStatistic(NUM_SELECTIONS_JAVA_EDITOR).getCount() +
//		    getCardinalStatistic(NUM_SELECTIONS_JAVA_EDITOR_AUTOFOLD).getCount() +
		    getCardinalStatistic(NUM_SELECTIONS_JAVA_OUTLINE).getCount() +
//		    getCardinalStatistic(NUM_SELECTIONS_JAVA_OUTLINE_MYLAR).getCount() +
		    getCardinalStatistic(NUM_SELECTIONS_OTHER).getCount() +
		    getCardinalStatistic(NUM_SELECTIONS_PKG_EXPLORER).getCount() +
//		    getCardinalStatistic(NUM_SELECTIONS_PKG_EXPLORER_MYLAR).getCount() +
		    getCardinalStatistic(NUM_SELECTIONS_PROBLEMS).getCount() +
//		    getCardinalStatistic(NUM_SELECTIONS_PROBLEMS_MYLAR).getCount() +
		    getCardinalStatistic(NUM_SELECTIONS_SEARCH).getCount();
//		    getCardinalStatistic(NUM_SELECTIONS_SEARCH_MYLAR).getCount(); 
        if (selections == 0) {
            return "<no data>";
        } else {
	        long keystrokes = getCardinalStatistic(NUM_KEYSTROKES_JAVA_EDITOR).getCount();
	        return "edit ratio: " + (float)keystrokes/selections/10;
        }
    }
	
	/**
	 * @return null if not found
	 */
	public UsageStatistic getStatistic(String statisticHandle) {
	    if (statisticHandle == null) return null;
        for (UsageStatistic statistic : statistics) {
            if (statistic.getHandle().equals(statisticHandle)) {
                return statistic;
            }
        }
	    return null;
	}

	public TemporalStatistic getTemporalStatistic(String statisticHandle) {
	    return (TemporalStatistic)getStatistic(statisticHandle);
	}

	public CardinalStatistic getCardinalStatistic(String statisticHandle) {
	    return (CardinalStatistic)getStatistic(statisticHandle);
	}
	
	
    public List<UsageStatistic> getStatistics() {
        return statistics;
    }
    
    /**
     * @param session
     */
    public void appendData(UsageSession session) {
        for (UsageStatistic statistic : session.getStatistics()) {
            if (statistic instanceof CardinalStatistic) {
                CardinalStatistic cardinal = (CardinalStatistic)statistic;
                getCardinalStatistic(cardinal.getHandle()).increment(cardinal.getCount());
            } 
        }
    }
	
	public String getCsvHeader() {
	    StringBuffer buffer = new StringBuffer();
        for (UsageStatistic statistic : statistics) {
            buffer.append(statistic.getHandle());
            buffer.append(DELIM);
        }
	    return buffer.toString();
	}
	
    public static String formatTimeFromSeconds(long seconds) {
        long hours = seconds/3600;
        long minutes = seconds/60;
        long secondsRemainder = seconds - hours*3600 - minutes*60;
        return hours + " hours, " + minutes + " minutes, " + secondsRemainder + " seconds"; 
    }
	
	/**
	 * @return single line String in comma-separated value format 
	 */
	public String toCsvString() {
	    StringBuffer buffer = new StringBuffer();
        for (UsageStatistic statistic : statistics) {
            buffer.append(statistic.toValueString());
            buffer.append(DELIM);
        }
	    return buffer.toString();	    
	}
	
    /**
     * @return
     */
    public List toFormattedList() {
        List<String> list = new ArrayList<String>();
        for (UsageStatistic statistic : statistics) {
            list.add(statistic.toFormattedString());
        }
        return list;
//	    list.add("startDate: " + startDate);
//	    list.add("startTime: " + startTime); 
//	    list.add("timeSession: " + UsageSession.formatTimeFromSeconds(timeSession));
//        list.add("numJavaEditorKeystrokes: " + numJavaEditorKeystrokes);
//        list.add("numSelectionsOther: " + numSelectionsOther); 
//        list.add("numSelectionsPkgExplorer: " + numSelectionsPkgExplorer); 
//        list.add("numSelectionsMylarPkgExplorer: " + numSelectionsMylarPkgExplorer); 
//        list.add("numSelectionsProblems: " + numSelectionsProblems); 
//        list.add("numSelectionsMylarProblems: " + numSelectionsMylarProblems); 
//        list.add("numSelectionsJavaOutline: " + numSelectionsJavaOutline); 
//        list.add("numSelectionsMylarJavaOutline: " + numSelectionsMylarJavaOutline); 
//        list.add("numSelectionsEditor: " + numSelectionsEditor); 
//        list.add("numSelectionsPathfinder: " + numSelectionsPathfinder); 
//        list.add("numSelectionsSearch: " + numSelectionsSearch);	  
    }
	
	public String toLongString() {
	    return
	    	"------------------\n" + 
	    	getCsvHeader() + "\n" + 
	    	toCsvString() + "\n" + 
	    	"------------------\n";
	}
	
	@Override
    public boolean equals(Object object) {
        if (!(object instanceof UsageSession)) return false;
        UsageSession session = (UsageSession)object;
        return session.getStatistics().equals(statistics);
        
//        return 
//	    	timeSession == session.getTimeSession() &&
//	    	numJavaEditorKeystrokes == session.getNumJavaEditorKeystrokes() &&
//	    	numSelectionsOther == session.getNumSelectionsOther() &&
//	    	numSelectionsPkgExplorer == session.getNumSelectionsPkgExplorer() &&
//	    	numSelectionsMylarPkgExplorer == session.getNumSelectionsMylarPkgExplorer() &&
//	    	numSelectionsProblems == session.getNumSelectionsProblems() &&
//	    	numSelectionsMylarProblems == session.getNumSelectionsMylarProblems() &&
//	    	numSelectionsJavaOutline == session.getNumSelectionsJavaOutline() &&
//	    	numSelectionsMylarJavaOutline == session.getNumSelectionsMylarJavaOutline() &&
//	    	numSelectionsEditor == session.getNumSelectionsEditor() &&
//	    	numSelectionsPathfinder == session.getNumSelectionsPathfinder() &&
//	    	numSelectionsSearch == session.getNumSelectionsSearch();
    }
	
	@Override
	public String toString() {
	    return toCsvString();
	}

//    public String getStartDate() {
//        return startDate;
//    }
//    public void setStartDate(String date) {
//        this.startDate = date;
//    }
//    
//    /**
//     * @return time in seconds
//     */
//    public long getTimeSession() {
//        return timeSession;
//    }
//    
//    public void setTimeSession(long duration) {
//        this.timeSession = duration;
//    }
//    public int getNumSelectionsEditor() {
//        return numSelectionsEditor;
//    }
//    public void incrementSelectionsEditor() {
//        this.numSelectionsEditor++;
//    }
//    public int getNumSelectionsJavaOutline() {
//        return numSelectionsJavaOutline;
//    }
//    public void incrementSelectionsJavaOutline() {
//        this.numSelectionsJavaOutline++;
//    }
//    public int getNumSelectionsMylarJavaOutline() {
//        return numSelectionsMylarJavaOutline;
//    }
//    public void incrementSelectionsMylarJavaOutline() {
//        this.numSelectionsMylarJavaOutline++;
//    }
//    public int getNumSelectionsMylarPkgExplorer() {
//        return numSelectionsMylarPkgExplorer;
//    }
//    public void setNumSelectionsMylarPkgExplorer() {
//        this.numSelectionsMylarPkgExplorer++;
//    }
//    public int getNumSelectionsMylarProblems() {
//        return numSelectionsMylarProblems;
//    }
//    public void incrementSelectionsMylarProblems() {
//        this.numSelectionsMylarProblems++;
//    }
//    public int getNumSelectionsOther() {
//        return numSelectionsOther;
//    }
//    public void incrementSelectionsOther() {
//        this.numSelectionsOther++;
//    }
//    public int getNumSelectionsPathfinder() {
//        return numSelectionsPathfinder;
//    }
//    public void incrementSelectionsPathfinder() {
//        this.numSelectionsPathfinder++;
//    }
//    public int getNumSelectionsPkgExplorer() {
//        return numSelectionsPkgExplorer;
//    }
//    public void incrementSelectionsPkgExplorer() {
//        this.numSelectionsPkgExplorer++;
//    }
//    public int getNumSelectionsProblems() {
//        return numSelectionsProblems;
//    }
//    public void incrementSelectionsProblems() {
//        this.numSelectionsProblems++;
//    }
//    public int getNumSelectionsSearch() {
//        return numSelectionsSearch;
//    }
//    public void incrementSelectionsSearch() {
//        this.numSelectionsSearch++;
//    }
//    public String getStartTime() {
//        return startTime;
//    }
//    public void setStartTime(String startTime) {
//        this.startTime = startTime;
//    }
//    public int getNumJavaEditorKeystrokes() {
//        return numJavaEditorKeystrokes;
//    }
//    public void incrementNumJavaEditorKeystrokes() {
//        this.numJavaEditorKeystrokes++;
//    }
}
