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

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.ICompensation;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.IViolation;
import eu.atos.sla.monitoring.IMonitoringMetric;

/**
 * A GuaranteeTermEvaluator performs the evaluation of a guarantee term, consisting in:
 * <ul>
 * <li>A service level evaluation, assessing which metrics are violations.
 * <li>A business evaluation, assessing what penalties are derived from the raised violations.
 * </ul>
 *
 * @see IServiceLevelEvaluator
 * @see IBusinessValuesEvaluator
 * 
 * @author rsosa
 *
 */
public interface IGuaranteeTermEvaluator {
	
	/**
	 * Evaluate violations and penalties for a given guarantee term and a list of metrics.
	 * 
	 * @param agreement that contains the term to evaluate
	 * @param term guarantee term to evaluate
	 * @param metrics list of metrics to evaluated if fulfill the service level of the term.
	 * @param now the evaluation period ends at <code>now</code>.
	 * @return
	 */
	GuaranteeTermEvaluationResult evaluate(
			IAgreement agreement, IGuaranteeTerm term, List<IMonitoringMetric> metrics, Date now);

	/**
	 * Result of the guarantee term evaluation
	 */
	public interface GuaranteeTermEvaluationResult {
		
		List<IViolation> getViolations();
		List<? extends ICompensation> getCompensations();
	}
}

