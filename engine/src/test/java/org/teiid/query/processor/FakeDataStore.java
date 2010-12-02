/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.teiid.api.exception.query.QueryMetadataException;
import org.teiid.client.metadata.ParameterInfo;
import org.teiid.core.TeiidComponentException;
import org.teiid.query.metadata.QueryMetadataInterface;
import org.teiid.query.metadata.StoredProcedureInfo;
import org.teiid.query.metadata.TempMetadataStore;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.unittest.FakeMetadataFacade;
import org.teiid.query.unittest.FakeMetadataFactory;
import org.teiid.query.unittest.FakeMetadataObject;



/** 
 * This is sample data go along with FakeMetaDataFactory and FakeDataManager
 */
@SuppressWarnings("nls")
public class FakeDataStore {
    
    // Helper to create a list of elements - used in creating sample data
    public static List createElements(List elementIDs) { 
        List elements = new ArrayList();
        for(int i=0; i<elementIDs.size(); i++) {
            FakeMetadataObject elementID = (FakeMetadataObject) elementIDs.get(i);            
            ElementSymbol element = new ElementSymbol(elementID.getName());
            elements.add(element);
        }        
        
        return elements;
    }
    
    public static List createElements(List elementIDs, QueryMetadataInterface metadata) throws QueryMetadataException, TeiidComponentException { 
        List elements = new ArrayList();
        for(int i=0; i<elementIDs.size(); i++) {
            Object elementID = elementIDs.get(i);            
            ElementSymbol element = new ElementSymbol(metadata.getFullName(elementID));
            elements.add(element);
        }        
        
        return elements;
    }
    
    private static List getProcResultSetSymbols(List params){
        List result = new ArrayList();
        Iterator iter = params.iterator();
        while(iter.hasNext()){
            SPParameter param = (SPParameter)iter.next();
            if(param.getResultSetColumns() != null){
                result.addAll(param.getResultSetColumns());
            }
        }
        iter = params.iterator();
        while(iter.hasNext()){
            SPParameter param = (SPParameter)iter.next();
            if(param.getParameterType() == ParameterInfo.INOUT || param.getParameterType() == ParameterInfo.RETURN_VALUE) {
                result.add(param.getParameterSymbol());
            }
        }
        return result;
    }
    
    public static void sampleData1(FakeDataManager dataMgr, QueryMetadataInterface metadata) throws QueryMetadataException, TeiidComponentException {
		addTable("pm1.g1", dataMgr, metadata);    
		addTable("pm1.g2", dataMgr, metadata);
		addTable("pm1.g3", dataMgr, metadata);    
		addTable("pm2.g1", dataMgr, metadata);
		addTable("pm2.g2", dataMgr, metadata);
		addTable("pm2.g3", dataMgr, metadata);
		addTable("tm1.g1", dataMgr, metadata);

        //stored procedure pm1.sp1
        TempMetadataStore tempStore = new TempMetadataStore();          
        StoredProcedureInfo procInfo = metadata.getStoredProcedureInfoForProcedure("pm1.sp1"); //$NON-NLS-1$
        List elementSymbols = getProcResultSetSymbols(procInfo.getParameters());
        tempStore.addTempGroup("pm1.sp1", elementSymbols); //$NON-NLS-1$
        Object procID = tempStore.getTempGroupID("pm1.sp1"); //$NON-NLS-1$
        dataMgr.registerTuples(
            procID,
            elementSymbols,
            
            new List[] { 
                Arrays.asList(new Object[] { "a",   new Integer(0) }), //$NON-NLS-1$
                Arrays.asList(new Object[] { null,  new Integer(1)}),
                Arrays.asList(new Object[] { "a",   new Integer(3) }), //$NON-NLS-1$
                Arrays.asList(new Object[] { "c",   new Integer(1)}), //$NON-NLS-1$
                Arrays.asList(new Object[] { "b",   new Integer(2)}), //$NON-NLS-1$
                Arrays.asList(new Object[] { "a",   new Integer(0) }) //$NON-NLS-1$
                } );    
    }

	public static void addTable(String name, FakeDataManager dataMgr,
			QueryMetadataInterface metadata) throws TeiidComponentException,
			QueryMetadataException {
		Object groupID = metadata.getGroupID(name);
        List elementIDs = metadata.getElementIDsInGroupID(groupID);
        List elementSymbols = createElements(elementIDs, metadata);
    
        dataMgr.registerTuples(
            groupID,
            elementSymbols,
            
            new List[] { 
                Arrays.asList(new Object[] { "a",   new Integer(0),     Boolean.FALSE,  new Double(2.0) }), //$NON-NLS-1$
                Arrays.asList(new Object[] { null,  new Integer(1),     Boolean.FALSE,  new Double(1.0) }),
                Arrays.asList(new Object[] { "a",   new Integer(3),     Boolean.TRUE,   new Double(7.0) }), //$NON-NLS-1$
                Arrays.asList(new Object[] { "c",   new Integer(1),     Boolean.TRUE,   null }), //$NON-NLS-1$
                Arrays.asList(new Object[] { "b",   new Integer(2),     Boolean.FALSE,  new Double(0.0) }), //$NON-NLS-1$
                Arrays.asList(new Object[] { "a",   new Integer(0),     Boolean.FALSE,  new Double(2.0) }) //$NON-NLS-1$
                } );
	}

    public static void sampleData2(FakeDataManager dataMgr) throws QueryMetadataException, TeiidComponentException {
		FakeMetadataFacade metadata = FakeMetadataFactory.example1Cached();

		// Group pm1.g1
		FakeMetadataObject groupID = (FakeMetadataObject) metadata
				.getGroupID("pm1.g1"); //$NON-NLS-1$
		List elementIDs = metadata.getElementIDsInGroupID(groupID);
		List elementSymbols = createElements(elementIDs, metadata);

		dataMgr.registerTuples(groupID, elementSymbols,

		new List[] {
				Arrays.asList(new Object[] {
						"a", new Integer(0), Boolean.FALSE, new Double(2.0) }), //$NON-NLS-1$
				Arrays.asList(new Object[] {
						"b", new Integer(1), Boolean.TRUE, null }), //$NON-NLS-1$
				Arrays.asList(new Object[] {
						"c", new Integer(2), Boolean.FALSE, new Double(0.0) }), //$NON-NLS-1$
		});

		// Group pm1.g2
		groupID = (FakeMetadataObject) metadata.getGroupID("pm1.g2"); //$NON-NLS-1$
		elementIDs = metadata.getElementIDsInGroupID(groupID);
		elementSymbols = createElements(elementIDs, metadata);

		dataMgr.registerTuples(groupID, elementSymbols,

		new List[] {
				Arrays.asList(new Object[] {
						"a", new Integer(1), Boolean.TRUE, new Double(2.0) }), //$NON-NLS-1$
				Arrays.asList(new Object[] {
						"b", new Integer(0), Boolean.FALSE, new Double(0.0) }), //$NON-NLS-1$
				Arrays.asList(new Object[] {
						"b", new Integer(5), Boolean.TRUE, new Double(2.0) }), //$NON-NLS-1$
				Arrays.asList(new Object[] {
						"b", new Integer(2), Boolean.FALSE, null }), //$NON-NLS-1$
				Arrays.asList(new Object[] {
						"d", new Integer(2), Boolean.FALSE, new Double(1.0) }), //$NON-NLS-1$
		});

		// Group pm2.g1
		groupID = (FakeMetadataObject) metadata.getGroupID("pm2.g1"); //$NON-NLS-1$
		elementIDs = metadata.getElementIDsInGroupID(groupID);
		elementSymbols = createElements(elementIDs, metadata);

		dataMgr.registerTuples(groupID, elementSymbols,

		new List[] {
				Arrays.asList(new Object[] {
						"b", new Integer(0), Boolean.FALSE, new Double(2.0) }), //$NON-NLS-1$
				Arrays.asList(new Object[] {
						"d", new Integer(3), Boolean.TRUE, new Double(7.0) }), //$NON-NLS-1$
				Arrays.asList(new Object[] {
						"e", new Integer(1), Boolean.TRUE, null }), //$NON-NLS-1$
		});

		// Group pm2.g2
		groupID = (FakeMetadataObject) metadata.getGroupID("pm2.g2"); //$NON-NLS-1$
		elementIDs = metadata.getElementIDsInGroupID(groupID);
		elementSymbols = createElements(elementIDs, metadata);

		dataMgr.registerTuples(groupID, elementSymbols,

		new List[] {
				Arrays.asList(new Object[] {
						"a", new Integer(1), Boolean.TRUE, new Double(2.0) }), //$NON-NLS-1$
				Arrays.asList(new Object[] {
						"b", new Integer(0), Boolean.FALSE, new Double(0.0) }), //$NON-NLS-1$
				Arrays.asList(new Object[] {
						"b", new Integer(5), Boolean.TRUE, new Double(2.0) }), //$NON-NLS-1$
				Arrays.asList(new Object[] {
						"b", new Integer(2), Boolean.FALSE, null }), //$NON-NLS-1$
				Arrays.asList(new Object[] {
						"d", new Integer(2), Boolean.FALSE, new Double(1.0) }), //$NON-NLS-1$
		});

		// Group pm1.table1
		groupID = (FakeMetadataObject) metadata.getGroupID("pm1.table1"); //$NON-NLS-1$
		elementIDs = metadata.getElementIDsInGroupID(groupID);
		elementSymbols = createElements(elementIDs, metadata);

		dataMgr.registerTuples(groupID, elementSymbols,

		new List[] {
				Arrays.asList(new Object[] {
						"a", new Integer(0), Boolean.FALSE, new Double(2.0) }), //$NON-NLS-1$
				Arrays.asList(new Object[] {
						"b", new Integer(1), Boolean.TRUE, null }), //$NON-NLS-1$
				Arrays.asList(new Object[] {
						"c", new Integer(2), Boolean.FALSE, new Double(0.0) }), //$NON-NLS-1$
		});
	}                  

}
