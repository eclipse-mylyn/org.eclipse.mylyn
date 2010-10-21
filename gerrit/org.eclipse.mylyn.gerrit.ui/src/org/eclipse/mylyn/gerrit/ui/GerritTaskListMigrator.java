/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.gerrit.ui;

import java.util.Set;

import org.eclipse.mylyn.gerrit.core.GerritConnector;
import org.eclipse.mylyn.tasks.core.AbstractTaskListMigrator;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.w3c.dom.Element;


/**
 * @author Mikael Kober, Sony Ericsson
 * @author Tomas Westling, Sony Ericsson -
 *         thomas.westling@sonyericsson.com
 */
public class GerritTaskListMigrator extends AbstractTaskListMigrator {

  @Override
  public String getConnectorKind() {
    return GerritConnector.CONNECTOR_KIND;
  }

  @Override
  public Set<String> getQueryElementNames() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getTaskElementName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void migrateQuery(IRepositoryQuery arg0, Element arg1) {
    // TODO Auto-generated method stub

  }

  @Override
  public void migrateTask(ITask arg0, Element arg1) {
    // TODO Auto-generated method stub

  }

}
