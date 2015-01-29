/**
 * Copyright 2014 Atos
 * Contact: Atos <roman.sosa@atos.net>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.atos.sla.evaluation.guarantee;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.ICompensation;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.IViolation;
import eu.atos.sla.monitoring.IMonitoringMetric;

/**
 * Orchestrates the guarantee term evaluation. 
 * Gets the violations with a ServiceLevelEvaluator. Gets the compensations with a BusinessValuesEvaluator.
 *  
 * Usage:
 * <pre>
 * GuaranteeTermEvaluator gte = new GuaranteeTermEvaluator();
 * gte.setServiceLevelEvaluator(...);
 * gte.setBusinessEvaluator(...);
 * 
 * gte.evaluate(...)
 * </pre>
 * @see IGuaranteeTermEvaluator
 * 
 * @author rsosa
 *
 */
public class GuaranteeTermEvaluator implements IGuaranteeTermEvaluator {
	private static Logger logger = LoggerFactory.getLogger(GuaranteeTermEvaluator.class);
	
	IServiceLevelEvaluator serviceLevelEval;
	IBusinessValuesEvaluator businessEval;
	
	public GuaranteeTermEvaluator() {
	}
	
	@Override
	public GuaranteeTermEvaluationResult evaluate(
			IAgreement agreement, IGuaranteeTerm term, List<IMonitoringMetric> metrics, Date now) {

		/*
		 * throws NullPointerException if not property initialized 
		 */
		checkInitialized();
					
		logger.debug("evaluate(agreement={}, term={}, now={})", 
				agreement.getAgreementId(), term.getKpiName(), now);

		final List<IViolation> violations = serviceLevelEval.evaluate(agreement, term, metrics, now);
		logger.debug("Found " + violations.size() + " violations");
		final List<? extends ICompensation> compensations = businessEval.evaluate(agreement, term, violations);
		logger.debug("Found " + compensations.size() + " compensations");
		
		GuaranteeTermEvaluationResult result = new GuaranteeTermEvaluationResult() {
			@Override
			public List<IViolation> getViolations() {
				return violations;
			}
			@Override
			public List<? extends ICompensation> getCompensations() {
				return compensations;
			}
		}; 
		
		return result;
	}
	
	private void checkInitialized() {
		if (serviceLevelEval == null) {
			throw new NullPointerException("serviceLevelEvaluator has not been set");
		}
		if (businessEval == null) {
			throw new NullPointerException("businessEvaluator has not been set");
		}
	}

	public IServiceLevelEvaluator getServiceLevelEvaluator() {
		return serviceLevelEval;
	}

	public void setServiceLevelEvaluator(IServiceLevelEvaluator serviceLevelEval) {
		this.serviceLevelEval = serviceLevelEval;
	}

	public IBusinessValuesEvaluator getBusinessEvaluator() {
		return businessEval;
	}

	public void setBusinessEvaluator(IBusinessValuesEvaluator businessEval) {
		this.businessEval = businessEval;
	}

}