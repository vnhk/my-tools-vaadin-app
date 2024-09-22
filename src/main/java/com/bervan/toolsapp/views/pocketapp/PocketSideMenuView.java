package com.bervan.toolsapp.views.pocketapp;

import com.bervan.core.model.BervanLogger;
import com.bervan.pocketapp.pocket.Pocket;
import com.bervan.pocketapp.pocket.PocketService;
import com.bervan.pocketapp.pocketitem.PocketItem;
import com.bervan.pocketapp.pocketitem.PocketItemService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropEffect;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.dnd.EffectAllowed;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.*;
import java.util.stream.Collectors;


public class PocketSideMenuView extends VerticalLayout {
    private final BervanLogger log;
    private final PocketService pocketService;
    private final PocketItemService pocketItemService;
    private final ComboBox<String> pocketSelector;
    private final VerticalLayout itemsLayout = new VerticalLayout();
    private List<Div> divs = new ArrayList<>();
    private List<PocketItem> pocketItems;

    public PocketSideMenuView(PocketItemService pocketItemService, PocketService pocketService, BervanLogger log) {
        this.log = log;
        this.pocketService = pocketService;
        this.pocketItemService = pocketItemService;


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
        divs = new ArrayList<>();
        Optional<Pocket> pocket = pocketService.loadByName(pocketName);
        pocketItems = pocket.get().getPocketItems().stream().sorted(Comparator.comparing(PocketItem::getOrderInPocket)).toList();
        for (PocketItem pocketItem : pocketItems) {
            Div div = createDraggableDiv(pocketItem);
            div.setClassName("pocket-tile-in-menu");
            div.add(new H4(pocketItem.getContent()));
            divs.add(div);
        }
        itemsLayout.add(divs.toArray(new Div[0]));
    }


    private Div createDraggableDiv(PocketItem pocketItem) {
        Div div = new Div(new H4(pocketItem.getContent()));
        TextField idHolder = new TextField();
        idHolder.setVisible(false);
        idHolder.setValue(pocketItem.getId().toString());
        div.add(idHolder);
        div.getStyle().set("padding", "10px");
        div.getStyle().set("border", "1px solid black");
        div.getStyle().set("background-color", "#f0f0f0");
        div.setWidth("200px");

        DragSource<Div> dragSource = DragSource.create(div);
        dragSource.setEffectAllowed(EffectAllowed.MOVE);

        DropTarget<Div> dropTarget = DropTarget.create(div);
        dropTarget.setDropEffect(DropEffect.MOVE);

        dropTarget.addDropListener(event -> {
            if (event.getDragSourceComponent().isPresent() && event.getComponent() != null) {
                Div draggedDiv = (Div) event.getDragSourceComponent().get();
                Div targetDiv = event.getComponent();

                handleDropEvent(draggedDiv, targetDiv);
            }
        });

        return div;
    }

    private void handleDropEvent(Div draggedDiv, Div targetDiv) {
        int draggedIndex = divs.indexOf(draggedDiv);
        int targetIndex = divs.indexOf(targetDiv);

        if (draggedIndex != targetIndex) {
            divs.remove(draggedIndex);
            divs.add(targetIndex, draggedDiv);

            itemsLayout.removeAll();
            itemsLayout.add(divs.toArray(new Div[0]));

            for (int i = 0; i < divs.size(); i++) {
                TextField idHolder = (TextField) divs.get(i).getComponentAt(1);
                PocketItem pocketItem = pocketItems.stream().filter(e -> e.getId().equals(UUID.fromString(idHolder.getValue())))
                        .findFirst().get();
                pocketItem.setOrderInPocket(i);
                pocketItemService.save(pocketItem);


            }
        }
    }

}
