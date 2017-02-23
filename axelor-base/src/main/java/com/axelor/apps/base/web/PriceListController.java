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
package com.axelor.apps.base.web;

import com.axelor.apps.base.db.PriceList;
import com.axelor.apps.base.db.repo.PriceListRepository;
import com.axelor.apps.base.service.PriceListService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class PriceListController {
	
	@Inject 
	private PriceListService priceListService;
	
	@Inject
	protected PriceListRepository priceListRepo;
	
	public void historizePriceList(ActionRequest request, ActionResponse response){
		PriceList priceList = request.getContext().asType(PriceList.class);
		priceList = priceListRepo.find(priceList.getId());
		priceList = priceListService.historizePriceList(priceList);
		response.setReload(true);
	}
}
