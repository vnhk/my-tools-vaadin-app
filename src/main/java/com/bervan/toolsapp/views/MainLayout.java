package com.bervan.toolsapp.views;


import com.bervan.common.service.AuthService;
import com.bervan.core.model.BervanLogger;
import com.bervan.pocketapp.pocket.PocketService;
import com.bervan.pocketapp.pocketitem.PocketItemService;
import com.bervan.toolsapp.views.canvasapp.CanvasPagesView;
import com.bervan.toolsapp.views.englishepub.NotLearnedWordsView;
import com.bervan.toolsapp.views.filestorage.FileStorageView;
import com.bervan.toolsapp.views.interview.InterviewHomeView;
import com.bervan.toolsapp.views.learninglanguage.LearningAppHomeView;
import com.bervan.toolsapp.views.logsapp.LogItemsTableView;
import com.bervan.toolsapp.views.otpview.OTPGenerateView;
import com.bervan.toolsapp.views.pocketapp.PocketSideMenuView;
import com.bervan.toolsapp.views.pocketapp.PocketTableView;
import com.bervan.toolsapp.views.projectmgmtapp.ProjectListView;
import com.bervan.toolsapp.views.shopapp.ProductsView;
import com.bervan.toolsapp.views.spreadsheetapp.SpreadsheetsView;
import com.bervan.toolsapp.views.streamingplatformapp.VideoListView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./theme-changer.js")
public class MainLayout extends AppLayout {

    private final BervanLogger log;
    private final PocketItemService pocketItemService;
    private static Div sideMenu;
    private static Registration clickListenerRegistration;

    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, String iconClass, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            link.addClassNames("menu-item-link");
            link.setRoute(view);

            Span text = new Span(menuTitle);
            text.addClassNames("menu-item-text");

            link.add(new LineAwesomeIcon(iconClass), text);
            add(link);
            addClickListener(listItemClickEvent -> hideMenu());
        }

        public Class<?> getView() {
            return view;
        }

        @NpmPackage(value = "line-awesome", version = "1.3.0")
        public static class LineAwesomeIcon extends Span {
            public LineAwesomeIcon(String lineawesomeClassnames) {
                addClassNames("menu-item-icon");
                if (!lineawesomeClassnames.isEmpty()) {
                    addClassNames(lineawesomeClassnames);
                }
            }
        }

    }

    private H1 viewTitle;

    public MainLayout(BervanLogger log, PocketService pocketService, PocketItemService pocketItemService) {
        this.log = log;
        this.pocketItemService = pocketItemService;

        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
        PocketSideMenuView pocketSideMenu = new PocketSideMenuView(this.pocketItemService, pocketService, log);
        sideMenu = createSideMenu(pocketSideMenu);
        Button pocketMenuButton = new Button(VaadinIcon.CLIPBOARD.create());
        pocketMenuButton.addClassName("option-button");
        pocketMenuButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        pocketMenuButton.getStyle().set("position", "fixed").set("top", "10px").set("right", "10px");
        pocketMenuButton.addClickListener(event -> {
            pocketSideMenu.reloadItems();
            toggleMenuVisibility();
        });

        if (AuthService.getUserRole().equals("ROLE_USER")) {
            addToNavbar(sideMenu);
            addToNavbar(pocketMenuButton);
        }
    }

    private void toggleMenuVisibility() {
        if (sideMenu.getClassNames().contains("visible")) {
            hideMenu();
        } else {
            showMenu();
        }
    }

    private static void showMenu() {
        sideMenu.addClassName("visible");
        registerClickListener();
    }

    private static void hideMenu() {
        sideMenu.removeClassName("visible");
        unregisterClickListener();
    }

    private static void registerClickListener() {
        clickListenerRegistration = UI.getCurrent().getCurrentView().getElement().addEventListener("click", event -> {

            List<Component> collect = sideMenu.getChildren().collect(Collectors.toList());
            boolean isPocketClicked = isPocketClicked(collect, event.getSource());

            if (!isPocketClicked && !sideMenu.getElement().equals(event.getSource())) {
                hideMenu();
            }
        });
    }

    private static boolean isPocketClicked(List<Component> collect, Element source) {
        for (Component component : collect) {
            if (component.getElement().equals(source)) {
                return true;
            } else {
                if (isPocketClicked(component.getChildren().collect(Collectors.toList()), source)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static void unregisterClickListener() {
        if (clickListenerRegistration != null) {
            clickListenerRegistration.remove();
            clickListenerRegistration = null;
        }
    }

    private Div createSideMenu(Component pocketSideMenu) {
        Div sideMenu = new Div();
        sideMenu.addClassName("side-menu");
        VerticalLayout menuLayout = new VerticalLayout();
        menuLayout.add(pocketSideMenu);

        sideMenu.add(menuLayout);
        return sideMenu;
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames("view-toggle");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames("view-title");

        Header header = new Header(toggle, viewTitle);
        header.addClassNames("view-header");
        return header;
    }

    private Component createDrawerContent() {
        H2 appName = new H2("Tools");
        appName.addClassNames("app-name");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName, createNavigation(), createFooter());
        section.addClassNames("drawer-section");
        return section;
    }

    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("menu-item-container");
        nav.getElement().setAttribute("aria-labelledby", "views");

        // Wrap the links in a list; improves accessibility
        UnorderedList list = new UnorderedList();
        list.addClassNames("navigation-list");
        nav.add(list);

        for (MenuItemInfo menuItem : createMenuItems()) {
            list.add(menuItem);

        }
        return nav;
    }

    private MenuItemInfo[] createMenuItems() {
        if (AuthService.getUserRole().equals("ROLE_USER")) {
            return new MenuItemInfo[]{ //
                    new MenuItemInfo("OTP", "la la-barcode", OTPGenerateView.class), //
                    new MenuItemInfo("Interview", "la la-desktop", InterviewHomeView.class), //
                    new MenuItemInfo("Pocket", "la la-get-pocket", PocketTableView.class), //
                    new MenuItemInfo("Ebook English Words", "la la-book", NotLearnedWordsView.class), //
                    new MenuItemInfo("Learning Language", "la la-language", LearningAppHomeView.class), //
                    new MenuItemInfo("Project Management", "la la-project-diagram", ProjectListView.class), //
                    new MenuItemInfo("File Storage", "la la-cloud-upload", FileStorageView.class), //
                    new MenuItemInfo("Spreadsheets", "las la-file-excel", SpreadsheetsView.class), //
                    new MenuItemInfo("Notepad", "las la-chalkboard", CanvasPagesView.class), //
                    new MenuItemInfo("Streaming", "las la-video", VideoListView.class), //
                    new MenuItemInfo("Shopping", "la la-shopping-cart", ProductsView.class), //
                    new MenuItemInfo("Logs", "la la-database", LogItemsTableView.class), //
                    new MenuItemInfo("Settings", "las la-cog", SettingsView.class), //
            };
        } else if (AuthService.getUserRole().equals("ROLE_STREAMING"))
            return new MenuItemInfo[]{ //
                    new MenuItemInfo("Streaming", "las la-video", VideoListView.class), //
                    new MenuItemInfo("Settings", "las la-cog", SettingsView.class), //
            };

        return new MenuItemInfo[0];
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("footer");

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        setDrawerOpened(false);
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
