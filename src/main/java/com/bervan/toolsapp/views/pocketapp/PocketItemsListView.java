package com.bervan.toolsapp.views.pocketapp;

import com.bervan.common.WysiwygTextArea;
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
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style;

import java.util.*;


public class PocketItemsListView extends VerticalLayout {
    private final BervanLogger log;
    private final PocketService pocketService;
    private final PocketItemService pocketItemService;
    private final ComboBox<String> pocketSelector;
    private final VerticalLayout itemsLayout = new VerticalLayout();
    private List<Div> divs = new ArrayList<>();
    private List<PocketItem> pocketItems;

    public PocketItemsListView(PocketItemService pocketItemService, PocketService pocketService, BervanLogger log, String initPocket, Set<String> pocketsName) {
        this.log = log;
        this.pocketService = pocketService;
        this.pocketItemService = pocketItemService;
        itemsLayout.setClassName("pocket-items-layout");
        itemsLayout.setHeight("90vh");
        itemsLayout.setWidth(getWidthInPx() + "px");

        String pocketName = pocketsName.stream().filter(e -> e.equals(initPocket)).findFirst().get();
        pocketSelector = new ComboBox<>("", pocketsName);
        if (pocketsName.size() > 0) {
            pocketSelector.setValue(pocketName);
            reloadItems(pocketService, itemsLayout, pocketName);
        }

        pocketSelector.addValueChangeListener(comboBoxStringComponentValueChangeEvent -> {
            reloadItems(pocketService, itemsLayout, comboBoxStringComponentValueChangeEvent.getValue());
        });

        add(pocketSelector, itemsLayout);
    }

    public void reloadItems() {
        reloadItems(pocketService, itemsLayout, pocketSelector.getValue());
    }

    private void reloadItems(PocketService pocketService, VerticalLayout itemsLayout, String pocketName) {
        itemsLayout.removeAll();
        divs = new ArrayList<>();
        List<Pocket> pocket = pocketService.loadByName(pocketName);
        if (pocket.size() > 0) {
            pocketItems = pocket.get(0).getPocketItems().stream().filter(e -> !e.getDeleted()).sorted(Comparator.comparing(PocketItem::getOrderInPocket)).toList();
            for (PocketItem pocketItem : pocketItems) {
                Div div = createDraggableDiv(pocketItem);
                div.setClassName("pocket-tile-in-menu");
                divs.add(div);
            }
            itemsLayout.add(divs.toArray(new Div[0]));
        }
    }


    private Div createDraggableDiv(PocketItem pocketItem) {
        Div div = new Div(new H4(pocketItem.getSummary()));
        TextField idHolder = new TextField();
        idHolder.setVisible(false);
        idHolder.setValue(pocketItem.getId().toString());
        div.add(idHolder);
        div.setWidth(getWidthInPx() - 80 + "px");

        Button info = new Button(VaadinIcon.INFO_CIRCLE.create());
        info.addClassName("option-button");
        info.addClickListener(event -> {
            Dialog dialog = new Dialog();
            dialog.setWidth("80vw");
            dialog.setHeight("90vh");

            VerticalLayout dialogLayout = new VerticalLayout();

            HorizontalLayout headerLayout = getDialogTopBarLayout(dialog);

            WysiwygTextArea field = new WysiwygTextArea("editor_pocket_side_menu", pocketItem.getContent());
            field.setWidth("100%");
            field.setHeight("60vh");

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

    private static int getWidthInPx() {
        return 350;
    }

    private HorizontalLayout getDialogTopBarLayout(Dialog dialog) {
        Button closeButton = new Button(new Icon(VaadinIcon.CLOSE));
        closeButton.addClassName("option-button");

        closeButton.addClickListener(e -> dialog.close());
        HorizontalLayout headerLayout = new HorizontalLayout(closeButton);
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.END);
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
