/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.dataset.definition;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.PatientToEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.PersonToEncounterDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.dataset.column.definition.ColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.SingleColumnDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;

/**
 * DataSetDefinition for Producing a DataSet that has one row per Encounter
 * @see DataSetDefinition
 */
public class EncounterDataSetDefinition extends BaseDataSetDefinition {
	
    public static final long serialVersionUID = 1L;
    
    //***** PROPERTIES *****
    
    @ConfigurationProperty
    private List<Mapped<? extends EncounterQuery>> rowFilters;
    
    @ConfigurationProperty
    private List<ColumnDefinition> columnDefinitions;
 
    //***** CONSTRUCTORS *****
    
    /**
     * Default Constructor
     */
    public EncounterDataSetDefinition() {
    	super();
    }

	/**
	 * Public constructor
	 */
	public EncounterDataSetDefinition(String name) { 
		super(name);
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * Adds a new Column Definition given the passed parameters
	 */
	public void addColumn(String name, DataDefinition dataDefinition, String mappings, DataConverter converter) {
		if (dataDefinition instanceof EncounterDataDefinition) {
			getColumnDefinitions().add(new SingleColumnDefinition(name, dataDefinition, mappings, converter));
		}
		else if (dataDefinition instanceof PatientDataDefinition) {
			EncounterDataDefinition edd = new PatientToEncounterDataDefinition((PatientDataDefinition) dataDefinition);
			getColumnDefinitions().add(new SingleColumnDefinition(name, edd, mappings, converter));
		}
		else if (dataDefinition instanceof PersonDataDefinition) {
			EncounterDataDefinition edd = new PersonToEncounterDataDefinition((PersonDataDefinition) dataDefinition);
			getColumnDefinitions().add(new SingleColumnDefinition(name, edd, mappings, converter));
		}
		else {
			throw new IllegalArgumentException("Unable to add data definition of type " + dataDefinition.getClass().getSimpleName());
		}
	}
	
	/**
	 * Add a new row filter with the passed parameter mappings
	 */
	public void addRowFilter(EncounterQuery filter, String mappings) {
		getRowFilters().add(new Mapped<EncounterQuery>(filter, ParameterizableUtil.createParameterMappings(mappings)));
	}
	
    //***** PROPERTY ACCESS *****
	
	/**
	 * @return the rowFilters
	 */
	public List<Mapped<? extends EncounterQuery>> getRowFilters() {
		if (rowFilters == null) {
			rowFilters = new ArrayList<Mapped<? extends EncounterQuery>>();
		}
		return rowFilters;
	}

	/**
	 * @param rowFilters the rowFilters to set
	 */
	public void setRowFilters(List<Mapped<? extends EncounterQuery>> rowFilters) {
		this.rowFilters = rowFilters;
	}

	/**
	 * @return the columnDefinitions
	 */
	public List<ColumnDefinition> getColumnDefinitions() {
		if (columnDefinitions == null) {
			columnDefinitions = new ArrayList<ColumnDefinition>();
		}
		return columnDefinitions;
	}

	/**
	 * @param columnDefinitions the columnDefinitions to set
	 */
	public void setColumnDefinitions(List<ColumnDefinition> columnDefinitions) {
		this.columnDefinitions = columnDefinitions;
	}
}