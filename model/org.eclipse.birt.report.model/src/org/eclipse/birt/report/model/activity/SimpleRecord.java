/*******************************************************************************
* Copyright (c) 2004 Actuate Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  Actuate Corporation  - initial API and implementation
*******************************************************************************/ 

package org.eclipse.birt.report.model.activity;


/**
 * Convenience class to automate routine records that work directly with a
 * design element. Execute, undo and redo call the
 * {@link #perform perform( )} method, making it easy to implement simple
 * operations, especially when a single record implements two different
 * related operations (such as add and delete).
 * <p>
 * Derived commands that must create a "memento" record the initial state
 * should do so in the constructor. This means that the constructor should
 * gather all the data needed to perform the record.
 *
 */

public abstract class SimpleRecord extends AbstractElementRecord
{
	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.model.activity.ActivityRecord#execute()
	 */
	
	public void execute( )
	{
		perform( false );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.model.activity.ActivityRecord#undo()
	 */
	
	public void undo( )
	{
		perform( true );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.model.activity.ActivityRecord#redo()
	 */
	
	public void redo( )
	{
		perform( false );
	}

	/**
	 * Performs the actual operation.
	 * 
	 * @param undo whether to undo (true) or execute/redo (false) the
	 * operation.
	 */
	
	protected abstract void perform( boolean undo );

}
