package com.bervan.toolsapp.views.pocketapp;

import com.bervan.core.model.BervanLogger;
import com.bervan.pocketapp.pocket.Pocket;
import com.bervan.pocketapp.pocket.PocketService;
import com.bervan.pocketapp.pocketitem.PocketItem;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public class PocketSideMenuView extends VerticalLayout {
    private final BervanLogger log;
    private final PocketService pocketService;
    private final ComboBox<String> pocketSelector;
    private final VerticalLayout itemsLayout = new VerticalLayout();

    public PocketSideMenuView(PocketService pocketService, BervanLogger log) {
        this.log = log;
        this.pocketService = pocketService;


        Set<String> pocketsName = pocketService.load().stream().map(Pocket::getName).collect(Collectors.toSet());
        pocketSelector = new ComboBox<>("", pocketsName);
        if (pocketsName.size() > 0) {
            pocketSelector.setValue(pocketsName.iterator().next());
            reloadItems(pocketService, itemsLayout, pocketsName.iterator().next());
        }

        pocketSelector.addValueChangeListener(comboBoxStringComponentValueChangeEvent -> {
            reloadItems(pocketService, itemsLayout, comboBoxStringComponentValueChangeEvent.getValue());
        });

        add(new H3("Pocket"), pocketSelector, itemsLayout);
    }

    public void reloadItems() {
        reloadItems(pocketService, itemsLayout, pocketSelector.getValue());
    }

    private void reloadItems(PocketService pocketService, VerticalLayout itemsLayout, String pocketName) {
        itemsLayout.removeAll();
        Optional<Pocket> pocket = pocketService.loadByName(pocketName);
        List<PocketItem> pocketItems = pocket.get().getPocketItems().stream().sorted(Comparator.comparing(PocketItem::getOrderInPocket)).toList();
        for (PocketItem pocketItem : pocketItems) {
            Div div = new Div();
            div.setClassName("pocket-tile-in-menu");
            div.add(new H4(pocketItem.getContent()));

            itemsLayout.add(div);
        }
    }
}
