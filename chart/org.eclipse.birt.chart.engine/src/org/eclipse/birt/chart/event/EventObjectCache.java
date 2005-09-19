/***********************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Actuate Corporation - initial API and implementation
 ***********************************************************************/

package org.eclipse.birt.chart.event;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.birt.chart.device.IDisplayServer;
import org.eclipse.birt.chart.engine.i18n.Messages;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.log.ILogger;
import org.eclipse.birt.chart.log.Logger;
import org.eclipse.birt.chart.model.attribute.ColorDefinition;
import org.eclipse.birt.chart.model.attribute.Fill;
import org.eclipse.birt.chart.model.attribute.LineAttributes;
import org.eclipse.birt.chart.plugin.ChartEnginePlugin;

/**
 * An internal cache that provides reusable primitive (and block) rendering
 * event objects A local cache is created per generation sequence so issues with
 * multithreaded access shouldn't arise.
 * 
 */
public class EventObjectCache
{

	private transient Hashtable _htEvents;

	private static ILogger logger = Logger.getLogger( "org.eclipse.birt.chart.engine/event" ); //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public EventObjectCache( )
	{
		super( );
		_htEvents = new Hashtable( );
	}

	/**
	 * @param oSource
	 * @param cType
	 * 
	 * @return An instance of the requested event object that encapsulates
	 *         rendering attributes
	 */
	public final PrimitiveRenderEvent getEventObject( Object oSource,
			Class cType )
	{
		PrimitiveRenderEvent pre = (PrimitiveRenderEvent) _htEvents.get( cType );
		if ( pre == null )
		{
			try
			{
				final Constructor co = cType.getConstructor( new Class[]{
					Object.class
				} );
				pre = (PrimitiveRenderEvent) co.newInstance( new Object[]{
					oSource
				} );
				_htEvents.put( cType, pre );
			}
			catch ( NoSuchMethodException nsmex )
			{
				logger.log( nsmex );
			}
			catch ( InvocationTargetException itex )
			{
				logger.log( itex );
			}
			catch ( IllegalAccessException iaex )
			{
				logger.log( iaex );
			}
			catch ( InstantiationException iex )
			{
				logger.log( iex );
			}
		}
		else
		{
			pre.setSourceObject( oSource );
			pre.reset( );
		}
		return pre;
	}

	/**
	 * 
	 * @param oSource
	 * @param lia
	 * @return
	 * @throws RenderingException
	 */
	protected final boolean validateLineAttributes( Object oSource,
			LineAttributes lia ) throws ChartException
	{
		if ( !lia.isSetStyle( ) || !lia.isSetThickness( ) )
		{
			return false; // NO LINE STYLE = DON'T DRAW LINE
		}
		if ( !lia.isSetVisible( ) )
		{
			throw new ChartException( ChartEnginePlugin.ID,
					ChartException.RENDERING,
					"exception.unset.line.visibility", //$NON-NLS-1$ 
					new Object[]{
						oSource
					},
					ResourceBundle.getBundle( Messages.ENGINE,
							Locale.getDefault( ) ) );
		}

		if ( !lia.isVisible( ) )
		{
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param cdEdge
	 * @param fBackground
	 * @param ids
	 * @return
	 */
	protected final Object validateEdgeColor( ColorDefinition cdEdge,
			Fill fBackground, IDisplayServer ids )
	{
		Object cFG = null;
		if ( cdEdge == null )
		{
			if ( !( fBackground instanceof ColorDefinition ) )
			{
				/*
				 * DefaultLoggerImpl.instance().log(ILogger.WARNING, "Cannot
				 * draw edge with an undefined color because the fill color is
				 * not a solid color");
				 */
				return null;
			}
			else
			{
				cFG = ids.getColor( ( (ColorDefinition) fBackground ).darker( ) );
			}
		}
		else
		{
			cFG = ids.getColor( cdEdge );
		}
		return cFG;
	}
}