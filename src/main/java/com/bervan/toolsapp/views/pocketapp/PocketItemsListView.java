package com.bervan.toolsapp.views.pocketapp;

import com.bervan.common.AbstractPageView;
import com.bervan.common.BervanButton;
import com.bervan.common.WysiwygTextArea;
import com.bervan.common.service.AuthService;
import com.bervan.encryption.DataCipherException;
import com.bervan.encryption.EncryptionService;
import com.bervan.pocketapp.pocket.Pocket;
import com.bervan.pocketapp.pocket.PocketService;
import com.bervan.pocketapp.pocketitem.PocketItem;
import com.bervan.pocketapp.pocketitem.PocketItemService;
import com.vaadin.flow.component.Key;
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
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Consumer;


@Slf4j
public class PocketItemsListView extends AbstractPageView {
    private final PocketService pocketService;
    private final PocketItemService pocketItemService;
    private final ComboBox<String> pocketSelector;
    private final VerticalLayout itemsLayout = new VerticalLayout();
    private List<Div> divs = new ArrayList<>();
    private List<PocketItem> pocketItems;
    private WysiwygTextArea field;


    public PocketItemsListView(PocketItemService pocketItemService, PocketService pocketService, String initPocket, Set<String> pocketsName) {
        this.pocketService = pocketService;
        this.pocketItemService = pocketItemService;
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
        if (pocket.size() > 0) {
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

            field = getWysiwygTextArea(pocketItem);
            Div buttons = new Div();
            HorizontalLayout buttonLayout = new HorizontalLayout();

            Button deleteButton = new BervanButton(VaadinIcon.TRASH.create());
            deleteButton.addClassName("option-button-warning");
            deleteButton.addClickListener(buttonClickEvent -> {
                pocketItemService.delete(pocketItem);
                reloadItems();
                dialog.close();
            });

            VerticalLayout fieldLayout = new VerticalLayout(field);
            Button editButton = new BervanButton(VaadinIcon.EDIT.create());
            editButton.addClickListener(buttonClickEvent -> {
                try {
                    pocketItem.setContent(field.getValue());
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
                field.setVisible(false);
                encryptCheckbox.setVisible(false);
                editButton.setVisible(false);
                showDecryptForm(dialogLayout, pocketItem, decryptedItem -> {
                    pocketItem.setContent(decryptedItem.getContent());
                    field.setValue(pocketItem.getContent());
                    fieldLayout.remove(field);
                    field = getWysiwygTextArea(pocketItem);
                    fieldLayout.add(field);
                    editButton.setVisible(true);
                    encryptCheckbox.setVisible(true);
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
        WysiwygTextArea field = new WysiwygTextArea("editor_pocket_side_menu", pocketItem.getContent());
        field.setWidth("100%");
        field.setHeight("60vh");
        return field;
    }

    private void showDecryptForm(VerticalLayout dialog, PocketItem encryptedItem, Consumer<PocketItem> onDecryptSuccess) {
        VerticalLayout decryptForm = new VerticalLayout();

        H4 itemTitle = new H4("Encrypted Item");
        itemTitle.getStyle().set("color", "var(--lumo-primary-text-color)");

        PasswordField passwordField = new PasswordField("Decryption Password");
        passwordField.setPlaceholder("Enter password");
        passwordField.setWidthFull();

        Div messageDiv = new Div();
        messageDiv.getStyle().set("display", "none");

        decryptForm.add(itemTitle, passwordField, messageDiv);

        BervanButton decryptButton = new BervanButton("🔓 Decrypt", e -> {
            String password = passwordField.getValue();
            if (password == null || password.trim().isEmpty()) {
                showMessage(messageDiv, "Please enter a password", "error");
                return;
            }

            try {
                String content = pocketItemService.decryptContent(encryptedItem, password);
                encryptedItem.setContent(content);
                onDecryptSuccess.accept(encryptedItem);
                showSuccessNotification("🔓 Item decrypted successfully!");
                dialog.remove(decryptForm);
            } catch (Exception ex) {
                showMessage(messageDiv, "❌ Wrong password or corrupted data", "error");
                passwordField.clear();
                passwordField.focus();
            }
        });

        passwordField.addKeyPressListener(Key.ENTER, e -> decryptButton.click());

        HorizontalLayout buttonsLayout = new HorizontalLayout(decryptButton);
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        decryptForm.add(buttonsLayout);
        dialog.add(decryptForm);
    }

    private void showMessage(Div messageDiv, String text, String type) {
        messageDiv.removeAll();
        messageDiv.add(new Span(text));
        messageDiv.getStyle()
                .set("display", "block")
                .set("padding", "8px")
                .set("border-radius", "4px")
                .set("margin-top", "8px");

        if ("error".equals(type)) {
            messageDiv.getStyle()
                    .set("background-color", "var(--lumo-error-color-10pct)")
                    .set("color", "var(--lumo-error-text-color)")
                    .set("border", "1px solid var(--lumo-error-color-50pct)");
        } else {
            messageDiv.getStyle()
                    .set("background-color", "var(--lumo-success-color-10pct)")
                    .set("color", "var(--lumo-success-text-color)")
                    .set("border", "1px solid var(--lumo-success-color-50pct)");
        }
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
