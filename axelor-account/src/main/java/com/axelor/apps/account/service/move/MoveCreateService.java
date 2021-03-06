/**
 * Axelor Business Solutions
 *
 * Copyright (C) 2017 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.account.service.move;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axelor.apps.account.db.CashRegister;
import com.axelor.apps.account.db.Journal;
import com.axelor.apps.account.db.Move;
import com.axelor.apps.account.db.MoveLine;
import com.axelor.apps.account.db.PaymentMode;
import com.axelor.apps.account.db.PaymentVoucher;
import com.axelor.apps.account.db.repo.MoveRepository;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.Currency;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.Period;
import com.axelor.apps.base.service.PeriodService;
import com.axelor.apps.base.service.administration.GeneralService;
import com.axelor.apps.base.service.config.CompanyConfigService;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;

public class MoveCreateService {

	private final Logger log = LoggerFactory.getLogger( MethodHandles.lookup().lookupClass() );

	protected PeriodService periodService;
	protected MoveRepository moveRepository;
	protected CompanyConfigService companyConfigService;
	
	protected LocalDate today;

	@Inject
	public MoveCreateService(GeneralService generalService, PeriodService periodService, MoveRepository moveRepository, CompanyConfigService companyConfigService)  {

		this.periodService = periodService;
		this.moveRepository = moveRepository;
		this.companyConfigService = companyConfigService;
		
		today = generalService.getTodayDate();

	}

	
	/**
	 * Créer une écriture comptable à la date du jour impactant la compta.
	 *
	 * @param journal
	 * @param period
	 * @param company
	 * @param invoice
	 * @param partner
	 * @param isReject
	 * 		<code>true = écriture de rejet avec séquence spécifique</code>
	 * @return
	 * @throws AxelorException
	 */
	public Move createMove(Journal journal, Company company, Currency currency, Partner partner, PaymentMode paymentMode, int technicalOriginSelect) throws AxelorException{
		return this.createMove(journal, company, currency, partner, today, paymentMode, technicalOriginSelect);
	}


	/**
	 * Créer une écriture comptable impactant la compta.
	 *
	 * @param journal
	 * @param period
	 * @param company
	 * @param invoice
	 * @param partner
	 * @param dateTime
	 * @param isReject
	 * 		<code>true = écriture de rejet avec séquence spécifique</code>
	 * @return
	 * @throws AxelorException
	 */
	public Move createMove(Journal journal, Company company, Currency currency, Partner partner, LocalDate date, PaymentMode paymentMode, int technicalOriginSelect) throws AxelorException{
		return this.createMove(journal, company, currency, partner, date, paymentMode, technicalOriginSelect, false, false);

	}


	/**
	 * Creating a new generic accounting move
	 * 
	 * @param journal
	 * @param company
	 * @param currency
	 * @param partner
	 * @param date
	 * @param paymentMode
	 * @param technicalOriginSelect
	 * @param ignoreInReminderOk
	 * @param ignoreInAccountingOk
	 * @return
	 * @throws AxelorException
	 */
	public Move createMove(Journal journal, Company company, Currency currency, Partner partner, LocalDate date, PaymentMode paymentMode, 
			int technicalOriginSelect, boolean ignoreInReminderOk, boolean ignoreInAccountingOk) throws AxelorException  {
		log.debug("Creating a new generic accounting move (journal : {}, company : {}", new Object[]{journal.getName(), company.getName()});

		Move move = new Move();

		move.setJournal(journal);
		move.setCompany(company);

		move.setIgnoreInReminderOk(ignoreInReminderOk);
		move.setIgnoreInAccountingOk(ignoreInAccountingOk);

		Period period = periodService.rightPeriod(date, company);

		move.setPeriod(period);
		move.setDate(date);
		move.setMoveLineList(new ArrayList<MoveLine>());
		
		Currency companyCurrency = companyConfigService.getCompanyCurrency(company);
		
		if(companyCurrency != null)  {
			move.setCompanyCurrency(companyCurrency);
			move.setCompanyCurrencyCode(companyCurrency.getCode());
		}
		
		if(currency == null)  {  currency = move.getCompanyCurrency();  }
		if(currency != null)  {
			move.setCurrency(currency);
			move.setCurrencyCode(currency.getCode());
		}
		
		move.setPartner(partner);
		move.setPaymentMode(paymentMode);
		move.setTechnicalOriginSelect(technicalOriginSelect);
		moveRepository.save(move);
		move.setReference("*"+move.getId());

		return move;

	}


	/**
	 * Créer une écriture comptable de toute pièce spécifique à une saisie paiement.
	 *
	 * @param journal
	 * @param period
	 * @param company
	 * @param invoice
	 * @param partner
	 * @param isReject
	 * 		<code>true = écriture de rejet avec séquence spécifique</code>
	 * @param agency
	 * 		L'agence dans laquelle s'effectue le paiement
	 * @return
	 * @throws AxelorException
	 */
	public Move createMove(Journal journal, Company company, PaymentVoucher paymentVoucher, Partner partner, LocalDate date, PaymentMode paymentMode, int technicalOriginSelect, CashRegister cashRegister) throws AxelorException{
		Move move = this.createMove(journal, company, paymentVoucher.getCurrency(), partner, date, paymentMode, technicalOriginSelect);
		move.setCashRegister(cashRegister);
		move.setPaymentVoucher(paymentVoucher);
		return move;
	}


}