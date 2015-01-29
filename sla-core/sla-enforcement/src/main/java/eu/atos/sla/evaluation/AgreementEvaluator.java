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
package eu.atos.sla.evaluation;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.evaluation.guarantee.IGuaranteeTermEvaluator;
import eu.atos.sla.evaluation.guarantee.IGuaranteeTermEvaluator.GuaranteeTermEvaluationResult;
import eu.atos.sla.monitoring.IMonitoringMetric;

/**
 * Evaluates an agreement.
 * 
 * Usage:
 * <pre>
 * AgreementEvaluator ae = new AgreementEvaluator();
 * ae.setGuaranteeTermEvaluator(...)
 * 
 * ae.evaluate(...)
 * </pre>
 * @author rsosa
 *
 */
public class AgreementEvaluator implements IAgreementEvaluator {

		IGuaranteeTermEvaluator termEval;
	
		public AgreementEvaluator() {
		}
		
		@Override
		public Map<IGuaranteeTerm, GuaranteeTermEvaluationResult> evaluate(IAgreement agreement,
				Map<IGuaranteeTerm, List<IMonitoringMetric>> metricsMap) {

			checkInitialized(false);
			
			Map<IGuaranteeTerm,GuaranteeTermEvaluationResult> result = 
					new HashMap<IGuaranteeTerm,GuaranteeTermEvaluationResult>();
			
			Date now = new Date();
			for (IGuaranteeTerm term : metricsMap.keySet()) {

				List<IMonitoringMetric> metrics = metricsMap.get(term);
				if (metrics.size() > 0) {
					GuaranteeTermEvaluationResult aux = termEval.evaluate(agreement, term, metrics, now);
					result.put(term, aux);
				}
			}

			return result;
		}
		
		private void checkInitialized(boolean checkRetriever) {
			if (termEval == null) {
				throw new NullPointerException("guaranteeTermEvaluator has not been set");
			}
		}
		
		public IGuaranteeTermEvaluator getGuaranteeTermEvaluator() {
			return termEval;
		}

		public void setGuaranteeTermEvaluator(IGuaranteeTermEvaluator termEval) {
			this.termEval = termEval;
		}
	}