package com.bervan.toolsapp.views.pocketapp;

import com.bervan.common.component.BervanButton;
import com.bervan.common.component.WysiwygTextArea;
import com.bervan.common.config.BervanViewConfig;
import com.bervan.common.config.ClassViewAutoConfigColumn;
import com.bervan.common.service.AuthService;
import com.bervan.common.view.AbstractPageView;
import com.bervan.encryption.DataCipherException;
import com.bervan.encryption.EncryptionService;
import com.bervan.pocketapp.pocket.Pocket;
import com.bervan.pocketapp.pocket.PocketService;
import com.bervan.pocketapp.pocketitem.PocketItem;
import com.bervan.pocketapp.pocketitem.PocketItemService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropEffect;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.dnd.EffectAllowed;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.extern.slf4j.Slf4j;

import java.util.*;


@Slf4j
public class PocketItemsListView extends AbstractPageView {
    private final PocketService pocketService;
    private final BervanViewConfig bervanViewConfig;
    private final PocketItemService pocketItemService;
    private final ComboBox<String> pocketSelector;
    private final VerticalLayout itemsLayout = new VerticalLayout();
    private List<Div> divs = new ArrayList<>();
    private List<PocketItem> pocketItems;

    public PocketItemsListView(PocketItemService pocketItemService, PocketService pocketService, String initPocket, BervanViewConfig bervanViewConfig, Set<String> pocketsName) {
        this.pocketService = pocketService;
        this.pocketItemService = pocketItemService;
        this.bervanViewConfig = bervanViewConfig;
        itemsLayout.setClassName("pocket-items-layout");
        itemsLayout.setHeight("90vh");
        itemsLayout.setWidth(getWidthInPx() + "px");

        String pocketName = pocketsName.stream().filter(e -> e.equals(initPocket)).findFirst().get();
        pocketSelector = new ComboBox<>("", pocketsName);
        if (!pocketsName.isEmpty()) {
            pocketSelector.setValue(pocketName);
            reloadItems(pocketService, itemsLayout, pocketName);
        }

        pocketSelector.addValueChangeListener(comboBoxStringComponentValueChangeEvent -> {
            reloadItems(pocketService, itemsLayout, comboBoxStringComponentValueChangeEvent.getValue());
        });

        add(pocketSelector, itemsLayout);
    }

    private static int getWidthInPx() {
        return 350;
    }

    public void reloadItems() {
        reloadItems(pocketService, itemsLayout, pocketSelector.getValue());
    }

    private void reloadItems(PocketService pocketService, VerticalLayout itemsLayout, String pocketName) {
        itemsLayout.removeAll();
        divs = new ArrayList<>();
        List<Pocket> pocket = pocketService.loadByName(pocketName);
        if (!pocket.isEmpty()) {
            pocketItems = pocket.get(0).getPocketItems().stream().filter(e -> !e.isDeleted() && AuthService.hasAccess(e.getOwners()))
                    .sorted(Comparator.comparing(PocketItem::getOrderInPocket)).toList();
            for (PocketItem pocketItem : pocketItems) {
                Div div = createDraggableDiv(pocketItem);
                div.setClassName("pocket-tile-in-menu");
                divs.add(div);
            }
            itemsLayout.add(divs.toArray(new Div[0]));
            fixMobiles(itemsLayout);
        }

    }

    private void fixMobiles(VerticalLayout itemsLayout) {
        if (!divs.isEmpty()) {
            Div divBottom = new Div();
            divBottom.setClassName("pocket-tile-in-menu");
            divBottom.addClassName("pocket-tile-in-menu-transparent");
            divBottom.setWidth(divs.get(0).getWidth());
            divBottom.setHeight(divs.get(0).getHeight());
            itemsLayout.add(divBottom);
        }
    }

    private Div createDraggableDiv(PocketItem pocketItem) {
        Div div = new Div(new H4(pocketItem.getSummary()));
        TextField idHolder = new TextField();
        idHolder.setVisible(false);
        idHolder.setValue(pocketItem.getId().toString());
        div.add(idHolder);
        div.setWidth(getWidthInPx() - 80 + "px");

        Button info = new BervanButton(VaadinIcon.INFO_CIRCLE.create());
        info.addClickListener(event -> {
            Dialog dialog = new Dialog();
            dialog.setWidth("80vw");
            dialog.setHeight("90vh");

            VerticalLayout dialogLayout = new VerticalLayout();

            HorizontalLayout headerLayout = getDialogTopBarLayout(dialog);

            WysiwygTextArea wysiwygTextArea = getWysiwygTextArea(pocketItem);
            Div buttons = new Div();
            HorizontalLayout buttonLayout = new HorizontalLayout();

            Button deleteButton = new BervanButton(VaadinIcon.TRASH.create());
            deleteButton.addClassName("option-button-warning");
            deleteButton.addClickListener(buttonClickEvent -> {
                pocketItemService.delete(pocketItem);
                reloadItems();
                dialog.close();
            });

            VerticalLayout fieldLayout = new VerticalLayout(wysiwygTextArea);
            Button editButton = new BervanButton(VaadinIcon.EDIT.create());
            editButton.addClickListener(buttonClickEvent -> {
                try {
                    wysiwygTextArea.validate();
                    if (wysiwygTextArea.isInvalid()) {
                        return;
                    }
                    pocketItem.setContent(wysiwygTextArea.getValue());
                    pocketItemService.save(pocketItem);
                    reloadItems();
                    dialog.close();
                } catch (DataCipherException e) {
                    log.error(e.getMessage(), e);
                    showErrorNotification(e.getMessage());
                } catch (Exception e) {
                    log.error("Unable to save changes!", e);
                    showErrorNotification("Unable to save changes!");
                }
            });

            buttonLayout.add(editButton, deleteButton);
            buttonLayout.setWidthFull();

            buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            buttons.add(buttonLayout);

            Checkbox encryptCheckbox = new Checkbox("Encrypt");
            Boolean encrypted = pocketItem.isEncrypted();
            encryptCheckbox.setValue(encrypted != null && encrypted);
            encryptCheckbox.addValueChangeListener(e -> {
                pocketItem.setEncrypted(e.getValue());
            });


            dialogLayout.add(headerLayout, fieldLayout);

            if (pocketItem.isEncrypted() && EncryptionService.isEncrypted(pocketItem.getContent())) {
                encryptCheckbox.setVisible(false);
                editButton.setVisible(false);

                wysiwygTextArea.setOnDecryptionSuccessAction(() -> {
                    encryptCheckbox.setVisible(true);
                    editButton.setVisible(true);
                });
            }

            dialogLayout.add(new Hr(), encryptCheckbox, buttonLayout);

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

    private WysiwygTextArea getWysiwygTextArea(PocketItem pocketItem) {
        ClassViewAutoConfigColumn config = bervanViewConfig.get(PocketItem.class.getSimpleName()).get("content");
        WysiwygTextArea field = new WysiwygTextArea("editor_pocket_side_menu", pocketItem.getContent(), config.isRequired(), config.getMin(), config.getMax());
        field.setWidth("100%");
        field.setHeight("60vh");
        return field;
    }

    private void handleDropEvent(Div draggedDiv, Div targetDiv) {
        int draggedIndex = divs.indexOf(draggedDiv);
        int targetIndex = divs.indexOf(targetDiv);

        if (draggedIndex != targetIndex) {
            divs.remove(draggedIndex);
            divs.add(targetIndex, draggedDiv);

            itemsLayout.removeAll();
            itemsLayout.add(divs.toArray(new Div[0]));
            fixMobiles(itemsLayout);

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
