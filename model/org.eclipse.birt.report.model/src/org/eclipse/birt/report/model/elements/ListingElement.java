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

package org.eclipse.birt.report.model.elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.birt.report.model.core.ContainerSlot;
import org.eclipse.birt.report.model.core.DesignElement;
import org.eclipse.birt.report.model.core.MultiElementSlot;
import org.eclipse.birt.report.model.util.StringUtil;

/**
 * This class represents the properties and slots common to the List and Table
 * elements.
 *  
 */

public abstract class ListingElement extends ReportItem
{

	/**
	 * Identifies the Header slot. The header prints at the start of the
	 * listing.
	 */

	public static final int HEADER_SLOT = 0;

	/**
	 * Identifies the slot that contains the list of groups.
	 */

	public static final int GROUP_SLOT = 1;

	/**
	 * Identifies the detail slot. The detail section prints for each row from
	 * the data set.
	 */

	public static final int DETAIL_SLOT = 2;

	/**
	 * Identifies the footer slot. The footer slot prints at the end of the
	 * listing and often contains totals.
	 */

	public static final int FOOTER_SLOT = 3;

	/**
	 * The set of slots for the listing.
	 */

	protected ContainerSlot slots[] = null;

	/**
	 * Name of the Sort property.
	 */

	public static final String SORT_PROP = "sort"; //$NON-NLS-1$

	/**
	 * Name of the filter property. This defines the filter criteria to match
	 * the rows to appear.
	 */

	public static final String FILTER_PROP = "filter"; //$NON-NLS-1$

	/**
	 * Name of the on-start property. Script called before the first row is
	 * retrieved from the data set for this element. Called after the data set
	 * is open but before the header band is created.
	 */

	public static final String ON_START_METHOD = "onStart"; //$NON-NLS-1$

	/**
	 * Name of the on-row property. Script called for each row retrieved from
	 * the data set for this element, but before creating any content for that
	 * row.
	 */

	public static final String ON_ROW_METHOD = "onRow"; //$NON-NLS-1$

	/**
	 * Name of the on-finish property. Script called after the last row is read
	 * from the data set for this element, but before the footer band is
	 * created.
	 */

	public static final String ON_FINISH_METHOD = "onFinish"; //$NON-NLS-1$

	/**
	 * Default constructor.
	 */

	public ListingElement( )
	{
		super( );
		initSlots( );
	}

	/**
	 * Constructs the listing element with an optional name.
	 * 
	 * @param theName
	 *            the optional name
	 */

	public ListingElement( String theName )
	{
		super( theName );
		initSlots( );
	}

	/**
	 * Private method to initialize the slots within this element.
	 */

	private void initSlots( )
	{
		slots = new ContainerSlot[getDefn( ).getSlotCount( )];
		for ( int i = 0; i < slots.length; i++ )
			slots[i] = new MultiElementSlot( );
	}

	/**
	 * Makes a clone of this listing element. The cloned element contains all of
	 * the copied slot defined in the original one.
	 * 
	 * @return the cloned listing element.
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Object clone( ) throws CloneNotSupportedException
	{
		ListingElement element = (ListingElement) super.clone( );
		element.initSlots( );

		for ( int i = 0; i < slots.length; i++ )
		{
			element.slots[i] = slots[i].copy( element, i );
		}
		return element;
	}

	/**
	 * Gets the list of groups. Groups are in order from the outer-most (most
	 * general) group to the inner-most (most specific) group.
	 * <p>
	 * The application MUST NOT modify this list. Use the handle class to make
	 * modifications.
	 * 
	 * @return the list of groups. The list contains <code>ListingGroup</code>
	 *         elements.
	 */

	public List getGroups( )
	{
		return slots[GROUP_SLOT].getContents( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.model.core.DesignElement#getSlot(int)
	 */

	public ContainerSlot getSlot( int slot )
	{
		assert slot >= 0 && slot < getDefn( ).getSlotCount( );
		return slots[slot];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.model.core.DesignElement#getDisplayLabel(org.eclipse.birt.report.model.elements.ReportDesign,
	 *      int)
	 */

	public String getDisplayLabel( ReportDesign design, int level )
	{
		String displayLabel = super.getDisplayLabel( design, level );
		if ( level == DesignElement.FULL_LABEL )
		{
			String name = getStringProperty( design, ReportItem.DATA_SET_PROP );
			name = limitStringLength( name );
			if ( !StringUtil.isBlank( name ) )
			{
				displayLabel += "(" + name + ")"; //$NON-NLS-1$//$NON-NLS-2$
			}
		}
		return displayLabel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.model.core.DesignElement#validate(org.eclipse.birt.report.model.elements.ReportDesign)
	 */

	public List validate( ReportDesign design )
	{
		List list = super.validate( design );

		list.addAll( validateStructureList( design, SORT_PROP ) );
		list.addAll( validateStructureList( design, FILTER_PROP ) );

		// Check whether this table/list has data set or its List/Table
		// container has data set.

		if ( getDataSetElement( design ) == null )
		{
			DesignElement container = this;
			int slot = getContainerSlot( );

			boolean dataSetFound = false;
			while ( container.getContainer( ) != null && !dataSetFound )
			{
				if ( ReportDesignConstants.LIST_ITEM
						.equalsIgnoreCase( container.getElementName( ) )
						|| ReportDesignConstants.TABLE_ITEM
								.equalsIgnoreCase( container.getElementName( ) ) )
				{

					if ( ( (ListingElement) container )
							.getDataSetElement( design ) != null )
					{
						dataSetFound = true;
					}
				}

				slot = container.getContainerSlot( );
				container = container.getContainer( );
			}

			// Since element in components slot is considered as incompletely
			// defined.
			// data set is not required on table in components.

			if ( !dataSetFound && ReportDesign.COMPONENT_SLOT != slot )
			{
				list.add( new SemanticError( this,
						SemanticError.MISSING_DATA_SET ) );
			}
		}
		else
		{
			// do the check of the group name

			ArrayList names = new ArrayList( );

			list.addAll( validateGroupName( design, names, null ) );
			list.addAll( validateContents( design, this, names, null ) );

		}

		return list;
	}

	/**
	 * Checks whether the group names in the group slot of list or table are
	 * duplicate. If <code>checkedName</code> is not <code>null</code> or
	 * empty, only checks existed group names with the <code>checkedName</code>.
	 * Otherwise, checks all group names in the listing element.
	 * 
	 * @param design
	 *            the report design
	 * @param names
	 *            the array list to store all the group names
	 * @param checkedName
	 *            the name to check
	 * @return the list of the errors found in validation, each of which is the
	 *         <code>SemanticException</code> object.
	 */

	private List validateGroupName( ReportDesign design, List names,
			String checkedName )
	{
		List list = new ArrayList( );

		// check the groups in the group slot itself

		boolean checkOnlyInputName = !StringUtil.isBlank( checkedName );
		Iterator iter = getGroups( ).iterator( );
		while ( iter.hasNext( ) )
		{
			GroupElement group = (GroupElement) iter.next( );
			String name = group.getStringProperty( design,
					GroupElement.GROUP_NAME_PROP );

			if ( !StringUtil.isBlank( name ) )
			{
				// The following cases will report error:
				// 
				// 1. The name to check equals this group name
				// 2. The name to check is not provided, and this 
				//    group name exists in the set.

				if ( name.equalsIgnoreCase( checkedName )
						|| ( ! checkOnlyInputName
						&& names.contains( name ) ) )
				{
					list.add( new SemanticError( this, new String[]{name},
							SemanticError.DUPLICATE_GROUP_NAME ) );
				}
				else
				{
					names.add( name );
				}
			}
		}

		return list;
	}

	/**
	 * Checks all the slot of the given design element to find whether all the
	 * group names are duplicate. If <code>checkedName</code> is not
	 * <code>null</code> or empty, only checks existed group names with the
	 * <code>checkedName</code>. Otherwise, checks all group names in the all
	 * slots.
	 * 
	 * 
	 * @param design
	 *            the report design
	 * @param element
	 *            the design element to check
	 * @param names
	 *            the array list to store all the group names
	 * @param checkedName
	 *            the name to check
	 * @return the list of the errors found in validation, each of which is the
	 *         <code>SemanticException</code> object.
	 */

	private List validateContents( ReportDesign design, DesignElement element,
			List names, String checkedName )
	{
		assert element != null;

		List list = new ArrayList( );

		// Check contents.

		int count = element.getDefn( ).getSlotCount( );
		for ( int i = 0; i < count; i++ )
		{
			Iterator iter = element.getSlot( i ).iterator( );
			while ( iter.hasNext( ) )
			{
				DesignElement e = (DesignElement) iter.next( );

				if ( e instanceof ListingElement )
				{
					if ( ( (ListingElement) e ).getDataSetElement( design ) != null )
						continue;
					list.addAll( ( (ListingElement) e ).validateGroupName(
							design, names, checkedName ) );
				}
				list.addAll( validateContents( design, e, names, checkedName ) );
			}
		}

		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.model.core.DesignElement#checkContent(org.eclipse.birt.report.model.elements.ReportDesign,
	 *      org.eclipse.birt.report.model.core.DesignElement, int,
	 *      org.eclipse.birt.report.model.core.DesignElement)
	 */

	protected List checkContent( ReportDesign design, DesignElement container,
			int slotId, DesignElement content )
	{
		List errors = super.checkContent( design, container, slotId, content );
		if ( !errors.isEmpty( ) )
			return errors;

		// do the check of the group name

		if ( content instanceof GroupElement )
		{
			String checkedName = (String) content.getLocalProperty( design,
					GroupElement.GROUP_NAME_PROP );
			if ( StringUtil.isBlank( checkedName ) )
				return errors;

			// if the table already has a duplicate group name. return this
			// error.

			List names = new ArrayList( );
			errors.addAll( validateGroupName( design, names, checkedName ) );
			if ( !errors.isEmpty( ) )
				return errors;

			ListingElement topListingElement = null;
			DesignElement tmpContainer = this;

			// finds out the top level listing element for checking

			do
			{
				if ( ( tmpContainer instanceof ListingElement )
						&& ( (ListingElement) tmpContainer )
								.getDataSetElement( design ) != null )

					topListingElement = (ListingElement) tmpContainer;

				tmpContainer = tmpContainer.getContainer( );

			} while ( tmpContainer != null );

			if ( topListingElement == null )
				return errors;

			// do the duplicate group name check

			names = new ArrayList( );
			errors.addAll( topListingElement.validateGroupName( design, names,
					checkedName ) );
			errors.addAll( topListingElement.validateContents( design,
					topListingElement, names, checkedName ) );

		}

		return errors;
	}
}