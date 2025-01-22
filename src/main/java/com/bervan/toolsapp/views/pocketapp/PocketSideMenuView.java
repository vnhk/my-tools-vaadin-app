package com.bervan.toolsapp.views.pocketapp;

import com.bervan.core.model.BervanLogger;
import com.bervan.pocketapp.pocket.Pocket;
import com.bervan.pocketapp.pocket.PocketService;
import com.bervan.pocketapp.pocketitem.PocketItemService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class PocketSideMenuView extends HorizontalLayout {
    List<PocketItemsListView> pocketItemsListViews = new ArrayList<>();

    public PocketSideMenuView(PocketItemService pocketItemService, PocketService pocketService, BervanLogger log) {
        Set<String> pocketsName = pocketService.load(Pageable.ofSize(10000))
                .stream().map(Pocket::getName)
                .collect(Collectors.toSet());
        for (String pocketName : pocketsName) {
            PocketItemsListView pocketItemsListView = new PocketItemsListView(pocketItemService, pocketService, log, pocketName, pocketsName);
            pocketItemsListViews.add(pocketItemsListView);
            add(pocketItemsListView);
        }
    }

    public void reloadItems() {
        pocketItemsListViews.forEach(PocketItemsListView::reloadItems);
    }
}
