package com.bervan.toolsapp.views;

import com.bervan.budget.AbstractBudgetView;
import com.bervan.budget.BudgetService;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "/budget", layout = MainLayout.class)
@RolesAllowed("USER")
public class BudgetView extends AbstractBudgetView {
    public BudgetView(BudgetService budgetService) {
        super(budgetService);
    }
}
