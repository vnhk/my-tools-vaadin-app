# My Tools Vaadin App - Project Notes

> **IMPORTANT**: Keep this file updated when making significant changes to the codebase. This file serves as persistent memory between Claude Code sessions.

## Overview
Main Vaadin application that aggregates all tool modules (invest-track, learning-language, streaming, etc.)

## Key Architecture

### MainLayout
Central layout with drawer navigation and header.

**Navigation Cleanup:**
When navigating between pages, navigation buttons from `MenuNavigationComponent` must be cleaned up:

```java
@Override
protected void afterNavigation() {
    super.afterNavigation();
    // ... existing code ...

    // Clear navigation buttons from previous page
    UI.getCurrent().getPage().executeJs(
        "document.querySelectorAll('navigation-buttons').forEach(el => el.remove());"
    );
}
```

This ensures buttons don't persist when navigating to pages without `MenuNavigationComponent`.

### Theme System
Located in `src/main/frontend/themes/my-theme/`

**Available Themes:**
- Default (dark)
- `ocean-theme.css` - Light blue ocean theme
- `sunset-theme.css` - Warm sunset colors
- `cyberpunk-theme.css` - Neon cyberpunk style

### Navigation Styling (main-layout.css)

**Full-width Navigation Bar with Glassmorphism:**
```css
.menu-container {
    position: absolute;
    left: 0; top: 0; right: 0;
    width: 100%; height: 100%;
    padding-left: 60px;  /* Space for drawer toggle */
    justify-content: flex-start;  /* Left-aligned icons */
    background: var(--glass-bg);  /* Covers entire top bar */
    backdrop-filter: blur(16px);
    z-index: 1;
}

.view-header {
    position: relative;  /* For absolute positioning of menu-container */
}

.view-toggle {
    z-index: 10;  /* Hamburger icon stays above menu-container */
}

.navigation-button {
    border-radius: 9999px;    /* Full pill shape */
    background: transparent;
    transition: all 0.2s ease-out;
}

.selected-route-button {
    background: var(--glass-nav-selected-bg);
    backdrop-filter: blur(8px);
    box-shadow: var(--glass-nav-selected-glow);
}
```

**Key CSS Variables:**
- `--navigation-button-color` - Inactive button text color
- `--glass-bg` - Glassmorphism background for menu-container
- `--glass-nav-selected-bg`, `--glass-nav-selected-glow` - Active button styling

### MenuItemInfo
Drawer menu items with optional submenus:
```java
new MenuItemInfo("Budget", "la la-money", BudgetDashboardView.class,
    BudgetDashboardView.subMenu())
```

## File Structure

### Java
- `views/MainLayout.java` - Main app layout with drawer
- `views/investtrackapp/BudgetDashboardView.java` - Budget dashboard wrapper
- `views/streamingplatformapp/ProductionListView.java` - Streaming wrapper

### Frontend
- `themes/my-theme/main-layout.css` - Main styles including navigation
- `themes/my-theme/styles.css` - Global styles
- `static/*-theme.css` - Theme variants

## Important Notes

1. **Navigation cleanup in afterNavigation()** - Prevents buttons from persisting across pages
2. **Theme CSS variables** - Each theme defines its own color variables
3. **Line Awesome icons** - Used for drawer menu (`la la-*`)
4. **Vaadin icons** - Used for navigation buttons (`VaadinIcon.*`)
5. **Pocket side menu** - Fixed position clipboard button (top-right)

## Glassmorphism Styling (Themeable)

All glassmorphism styles use CSS variables defined in each theme file (`*-theme.css`).

### CSS Variables (defined per theme)
```css
/* Core glass */
--glass-bg, --glass-blur, --glass-border, --glass-shadow, --glass-shadow-hover
--glass-text-primary, --glass-text-secondary

/* Containers */
--glass-container-gradient, --glass-sidebar-gradient, --glass-sidebar-shadow
--glass-header-bg, --glass-row-border, --glass-row-hover

/* Buttons */
--glass-btn-bg, --glass-btn-hover-bg
--glass-primary-bg, --glass-primary-border, --glass-primary-hover, --glass-primary-glow
--glass-success-*, --glass-warning-*, --glass-danger-* (same pattern)

/* Navigation */
--glass-nav-selected-bg, --glass-nav-selected-text, --glass-nav-selected-hover
--glass-nav-selected-glow, --glass-nav-selected-glow-hover

/* Scrollbar (theme-aware custom scrollbars) */
--scrollbar-width, --scrollbar-border-radius
--scrollbar-track, --scrollbar-thumb, --scrollbar-thumb-hover
```

### Custom Scrollbars
Global custom scrollbar styles are defined in `main-layout.css`:
- Applies to all scrollable elements (body, grids, dialogs, overlays)
- Uses CSS variables from theme files for colors
- Supports both Webkit browsers (Chrome, Safari, Edge) and Firefox
- Each theme defines its own scrollbar colors matching the theme aesthetic

### CSS Classes (`main-layout.css`)
- **glass-btn** - Glassmorphism button (variants: primary, success, warning, danger)
- **glass-container** - Gradient background container
- **glass-card** - Glassmorphism card with blur effect
- **budget-tree-container/toolbar/grid** - Budget tree components
- **logs-toolbar** - Logs filter buttons container
- **pocket-tile-in-menu** - Pocket menu tiles

### Theme Adaptations
Each theme defines colors appropriate to its style:
- **Cyberpunk**: Neon cyan/pink/green on dark background
- **Ocean**: Light blues on white background
- **Sunset**: Warm oranges/reds on cream background
- **Earth**: Earthy browns/olives on dark brown
- **Darkula**: Soft blues/grays on dark gray (IntelliJ-style)
