package org.openmrs.module.reporting.cohort.query.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.User;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.impl.PatientSetServiceImpl;
import org.openmrs.module.reporting.cohort.query.db.CohortQueryDAO;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface CohortQueryService extends OpenmrsService {
	
	public void setCohortQueryDAO(CohortQueryDAO dao);	
	
	public Cohort getPatientsWithGender(boolean includeMales, boolean includeFemales, boolean includeUnknownGender);
	public Cohort getPatientsWithAgeRange(Integer minAge, DurationUnit minAgeUnit, Integer maxAge, DurationUnit maxAgeUnit, boolean unknownAgeIncluded, Date effectiveDate);

	public Cohort getPatientsHavingProgramEnrollment(List<Program> programs, Date enrolledOnOrAfter, Date enrolledOnOrBefore, Date completedOnOrAfter, Date completedOnOrBefore);
	public Cohort getPatientsInProgram(List<Program> programs, Date onOrAfter, Date onOrBefore);

	public Cohort getPatientsHavingStates(List<ProgramWorkflowState> states, Date startedOnOrAfter, Date startedOnOrBefore, Date endedOnOrAfter, Date endedOnOrBefore);
	public Cohort getPatientsInStates(List<ProgramWorkflowState> states, Date onOrAfter, Date onOrBefore);
	
    public Cohort getPatientsHavingActiveDrugOrders(List<Drug> drugs, Date asOfDate);
    public Cohort getPatientsHavingStartedDrugOrders(List<Drug> drugs, List<Concept> drugSetConcepts, Date startedOnOrAfter, Date startedOnOrBefore);
    public Cohort getPatientsHavingCompletedDrugOrders(List<Drug> drugs, List<Concept> drugSetConcepts, Date completedOnOrAfter, Date completedOnOrBefore);

	public Cohort getPatientsHavingObs(Integer conceptId, TimeModifier timeModifier,
            PatientSetServiceImpl.Modifier modifier, Object value, Date fromDate, Date toDate, List<User> providers, EncounterType encounterType);

	/**
	 * Gets patients having ranged (i.e. Numeric or Date/Time) obs that match a complicated query.
	 * 
	 * @param timeModifier
	 * @param question
	 * @param groupingConcept
	 * @param onOrAfter
	 * @param onOrBefore
	 * @param locationList
	 * @param encounterTypeList
	 * @param operator1
	 * @param value1 if non-null this value controls whether the query looks at value_numeric or value_datetime
	 * @param operator2
	 * @param value2
	 * @return cohort of patients with matching obs
	 * 
	 * @should get patients with any obs of a specified concept
	 * @should get patients whose first obs of a specified concept is in a range
	 * @should get patients whose maximum obs of a specified concept is equals to a specified value
	 */
	public Cohort getPatientsHavingRangedObs(TimeModifier timeModifier, Concept question, Concept groupingConcept,
                                              Date onOrAfter, Date onOrBefore, List<Location> locationList,
                                              List<EncounterType> encounterTypeList, RangeComparator operator1, Object value1,
                                              RangeComparator operator2, Object value2);
	
	/**
	 * Gets patients having discrete (i.e. Text or Coded) obs that match a complicated query.
	 * 
	 * @param timeModifier
	 * @param question
	 * @param groupingConcept
	 * @param onOrAfter
	 * @param onOrBefore
	 * @param locationList
	 * @param encounterTypeList
	 * @param operator
	 * @param valueList if non-null the first value in this list controls whether the query looks at value_text or value_coded
	 * @return cohort of patients with matching obs
	 */
	public Cohort getPatientsHavingDiscreteObs(TimeModifier timeModifier, Concept question, Concept groupingConcept,
	                                           Date onOrAfter, Date onOrBefore, List<Location> locationList,
	                                           List<EncounterType> encounterTypeList, SetComparator operator,
	                                           List<? extends Object> valueList);
	/**
	 * Gets patients having encounters with the following characteristics
	 * 
	 * @param onOrAfter
	 * @param onOrBefore
	 * @param locationList
	 * @param encounterTypeList
	 * @param formList
	 * @param atLeastCount
	 * @param atMostCount
	 * @return cohort of patients matching the query
	 */
	public Cohort getPatientsHavingEncounters(Date onOrAfter, Date onOrBefore, List<Location> locationList,
                                              List<EncounterType> encounterTypeList, List<Form> formList,
                                              Integer atLeastCount, Integer atMostCount);
	
	
	/**
	 * Get patients having person attributes of a particular type or that contain certain values.  
	 * 
	 * @should get patients with an attribute of given attribute type
	 * @should get patients with an attribute of given attribute type and containing given values
	 * @should get patients with an attribute containing given values  
	 * 
	 * @param attribute
	 * @param values
	 * @return	cohort of patients matching the query
	 */
	public Cohort getPatientsHavingPersonAttributes(PersonAttributeType attributeType, List<String> values);
	
	/**
	 * Get patients that match the given query.  
	 * 
	 * TODO The query string should actually be a bean that encapsulates all types of queries (SQL, HQL, MDX).
	 * 
	 * @should return patients that match given query 
	 * @should return empty cohort when query is null
	 * 
	 * @param query	the query string to be executed - this might be sql, hql, etc (with parameters)
	 * @return	cohort of patients matching the query
	 */
	public Cohort executeSqlQuery(String sqlQuery, Map<String,Object> paramMap); 
	
	
	/**
	 * Should probably return a Query object with parameters attached.  
	 * 
	 * @param sqlQuery
	 * @return
	 */
	public List<Parameter> parseSqlQuery(String sqlQuery);
	

	/**
	 * Gets patients who were born or died in a particular date range
	 * 
	 * @param bornOnOrAfter
	 * @param bornOnOrBefore
	 * @param diedOnOrAfter
	 * @param diedOnOrBefore
	 * @return
	 */
	public Cohort getPatientsHavingBirthAndDeath(Date bornOnOrAfter, Date bornOnOrBefore,
	                                             Date diedOnOrAfter, Date diedOnOrBefore);

}