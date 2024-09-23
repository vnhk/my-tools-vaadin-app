package com.bervan.toolsapp.views.pocketapp;

import com.bervan.core.model.BervanLogger;
import com.bervan.pocketapp.pocket.Pocket;
import com.bervan.pocketapp.pocket.PocketService;
import com.bervan.pocketapp.pocketitem.PocketItem;
import com.bervan.pocketapp.pocketitem.PocketItemService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropEffect;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.dnd.EffectAllowed;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
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
        if (pocket.isPresent()) {
            pocketItems = pocket.get().getPocketItems().stream().filter(e -> !e.getDeleted()).sorted(Comparator.comparing(PocketItem::getOrderInPocket)).toList();
            for (PocketItem pocketItem : pocketItems) {
                Div div = createDraggableDiv(pocketItem);
                div.setClassName("pocket-tile-in-menu");
                divs.add(div);
            }
            itemsLayout.add(divs.toArray(new Div[0]));
        }
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
        div.getStyle().set("overflow", "hidden");
        div.getStyle().set("position", "relative");
        div.setWidth("300px");
        div.setMinHeight("200px");
        div.setMaxHeight("200px");

        Button info = new Button(VaadinIcon.INFO_CIRCLE.create());
        info.addClassName("option-button");
        info.addClickListener(event -> {
            Dialog dialog = new Dialog();
            dialog.setWidth("80vw");

            VerticalLayout dialogLayout = new VerticalLayout();

            HorizontalLayout headerLayout = getDialogTopBarLayout(dialog);

            TextArea field = new TextArea("Content");
            field.setWidth("100%");
            field.setHeight("200px");
            field.setValue(pocketItem.getContent());

            Div buttons = new Div();

            Button deleteButton = new Button(VaadinIcon.TRASH.create());
            deleteButton.addClassName("option-button");
            deleteButton.addClassName("option-button-warning");
            deleteButton.getStyle().setMarginRight("5px");
            deleteButton.addClickListener(buttonClickEvent -> {
                pocketItemService.delete(pocketItem);
                reloadItems();
                dialog.close();
            });

            buttons.add(deleteButton);

            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addClassName("option-button");
            editButton.addClickListener(buttonClickEvent -> {
                pocketItem.setContent(field.getValue());
                pocketItemService.save(pocketItem);
                reloadItems();
                dialog.close();
            });

            buttons.add(editButton);

            dialogLayout.add(headerLayout, field, new Hr(), buttons);

            dialog.add(dialogLayout);

            dialog.open();
        });

        Div bottom = new Div();
        bottom.getStyle().set("position", "absolute");
        bottom.getStyle().set("bottom", "20px");
        bottom.getStyle().set("width", "100%");
        bottom.add(new Hr(), info);
        div.add(bottom);

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

    private HorizontalLayout getDialogTopBarLayout(Dialog dialog) {
        Button closeButton = new Button(new Icon(VaadinIcon.CLOSE));
        closeButton.addClassName("option-button");

        closeButton.addClickListener(e -> dialog.close());
        HorizontalLayout headerLayout = new HorizontalLayout(closeButton);
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        return headerLayout;
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
