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
 * Created on Feb 18, 2005
  */
package org.eclipse.mylar.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.core.model.InterestComparator;


/**
 * @author Mik Kersten
 */
public class InterestSorter extends ViewerSorter { 

    protected InterestComparator<Object> comparator = new InterestComparator<Object>();
    
    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        return comparator.compare(e1, e2);  
    }
    
}