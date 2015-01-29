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
package eu.atos.sla.datamodel.bean;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import eu.atos.sla.datamodel.ICompensation.IPenalty;
import eu.atos.sla.datamodel.ICompensationDefinition.IPenaltyDefinition;

@Entity
@Table(name="penalty")
@Access(AccessType.FIELD)
public class Penalty extends Compensation implements IPenalty {

	private static final IPenaltyDefinition DEFAULT_PENALTY = new PenaltyDefinition();
	
	@ManyToOne(targetEntity = PenaltyDefinition.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "definition_id", referencedColumnName = "id", nullable = false)
	private IPenaltyDefinition definition;
	
	public Penalty() {
		super();
		this.definition = DEFAULT_PENALTY;
	}

	public Penalty(String agreementId, Date datetime, IPenaltyDefinition definition) {
		super(agreementId, datetime, definition);
		this.definition = definition;
	}
	
	@Override
	public IPenaltyDefinition getDefinition() {
		return definition;
	}
	
	@Override
	public String toString() {
		return String.format(
				"Penalty [uuid=%s, agreementId=%s, datetime=%s, definition=%s]", 
				getUuid(), getAgreementId(), getDatetime(), definition);
	}

}
