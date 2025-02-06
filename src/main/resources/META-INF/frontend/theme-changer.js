function setTheme(themeName) {
    const link = document.getElementById('theme-link');

    if (link) {
        link.href = `./static/${themeName}-theme.css`;
    } else {
        const head = document.getElementsByTagName('head')[0];
        const newLink = document.createElement('link');
        newLink.id = 'theme-link';
        newLink.rel = 'stylesheet';
        newLink.type = 'text/css';
        newLink.href = `./static/${themeName}-theme.css`;
        head.appendChild(newLink);
    }
}

// Check if a theme is already set in local storage
const savedTheme = localStorage.getItem('theme');
if (savedTheme) {
    setTheme(savedTheme);
} else {
    setTheme("cyberpunk");
}