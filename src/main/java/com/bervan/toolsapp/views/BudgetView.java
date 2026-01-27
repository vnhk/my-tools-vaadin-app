package com.bervan.toolsapp.views;

import com.bervan.budget.AbstractBudgetView;
import com.bervan.budget.BudgetService;
import com.bervan.budget.entry.BudgetEntryService;
import com.bervan.common.config.BervanViewConfig;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = AbstractBudgetView.ROUTE_NAME, layout = MainLayout.class)
@RolesAllowed("USER")
public class BudgetView extends AbstractBudgetView {

    public BudgetView(BudgetService service, BervanViewConfig bervanViewConfig, BudgetEntryService budgetEntryService) {
        super(service, bervanViewConfig, budgetEntryService);
    }
}
