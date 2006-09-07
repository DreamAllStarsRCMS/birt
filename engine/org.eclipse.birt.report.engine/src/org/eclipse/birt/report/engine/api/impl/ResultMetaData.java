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

package org.eclipse.birt.report.engine.api.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.birt.core.data.DataType;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.data.engine.api.IBaseExpression;
import org.eclipse.birt.data.engine.api.IBaseQueryDefinition;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IResultMetaData;

public class ResultMetaData implements IResultMetaData
{

	protected String[] selectedColumns;

	public ResultMetaData( IBaseQueryDefinition query, String[] selectedColumns )
	{
		initializeMetaData( query );
		this.selectedColumns = selectedColumns;
	}

	public ResultMetaData( IBaseQueryDefinition query )
	{
		initializeMetaData( query );
		this.selectedColumns = null;
	}

	protected void initializeMetaData( IBaseQueryDefinition query )
	{
		appendMetaData( query );
	}

	private ArrayList metaEntries = new ArrayList( );

	private class MetaDataEntry
	{

		String name;
		int type;

		MetaDataEntry( String name, int type )
		{
			this.name = name;
			this.type = type;
		}
	}

	protected void appendMetaData( IBaseQueryDefinition query )
	{
		Map bindings = query.getResultSetExpressions( );
		Iterator iter = bindings.entrySet( ).iterator( );
		while ( iter.hasNext( ) )
		{
			Map.Entry entry = (Map.Entry) iter.next( );
			String name = (String) entry.getKey( );
			IBaseExpression expr = (IBaseExpression) entry.getValue( );
			int type = expr.getDataType( );
			metaEntries.add( new MetaDataEntry( name, type ) );
		}
	}

	public int getColumnCount( )
	{
		if ( selectedColumns != null )
		{
			return selectedColumns.length;
		}
		return metaEntries.size( );
	}

	public String getColumnName( int index ) throws BirtException
	{
		index = getColumnIndex( index );
		MetaDataEntry entry = (MetaDataEntry) metaEntries.get( index );
		return entry.name;
	}

	public String getColumnAlias( int index ) throws BirtException
	{
		return getColumnName( index );
	}

	public int getColumnType( int index ) throws BirtException
	{
		index = getColumnIndex( index );
		MetaDataEntry entry = (MetaDataEntry) metaEntries.get( index );
		return entry.type;
	}

	public String getColumnTypeName( int index ) throws BirtException
	{
		int type = getColumnType( index );
		return DataType.getName( type );
	}

	public String getColumnLabel( int index ) throws BirtException
	{
		return getColumnName( index );
	}

	private int getColumnIndex( int index ) throws BirtException
	{
		if ( selectedColumns == null )
		{
			return index;
		}

		String name = selectedColumns[index];
		for ( int i = 0; i < metaEntries.size( ); i++ )
		{
			MetaDataEntry entry = (MetaDataEntry) metaEntries.get( i );
			if ( entry.name.equals( name ) )
			{
				return i;
			}
		}
		throw new EngineException( "Invalid Column Index" );
	}

}
