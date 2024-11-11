// ==UserScript==
// @name         Enhanced Translation Tool
// @namespace    http://tampermonkey.net/
// @version      2024-10-26
// @description  Plugin for translation and saving with animated loading, validation checks, and generate examples option in request body
// @grant        GM_xmlhttpRequest
// @match        *://*/*
// @connect      localhost
// @connect      192.168.1.205
// @run-at       document-start
// ==/UserScript==

(function() {
    'use strict';

    const HOST = "192.168.1.205:8091";
    const API_KEY = "ABCD";

    let shiftTCount = 0;
    let formVisible = false;

    // Monitor for Shift + T key press
    document.addEventListener('keydown', (e) => {
        if (e.key === 'T' && e.shiftKey) {
            shiftTCount++;
            if (shiftTCount === 3) {
                if (!formVisible) {
                    showForm();
                }
                shiftTCount = 0;
            }
        } else {
            shiftTCount = 0; // reset if not Shift+T
        }
    });

    // Function to show the form modal
    async function showForm() {
        formVisible = true;

        // Get selected text from the webpage
        const markedText = window.getSelection().toString().trim();

        // Create form overlay container
        const formContainer = document.createElement('div');
        formContainer.style.position = 'fixed';
        formContainer.style.top = '50%';
        formContainer.style.left = '50%';
        formContainer.style.transform = 'translate(-50%, -50%)';
        formContainer.style.width = '600px';
        formContainer.style.minHeight = '500px';
        formContainer.style.padding = '20px';
        formContainer.style.backgroundColor = '#fff';
        formContainer.style.boxShadow = '0px 0px 10px rgba(0, 0, 0, 0.2)';
        formContainer.style.zIndex = '9999';
        formContainer.style.borderRadius = '8px';
        formContainer.style.color = 'black';

        // Close button
        const closeButton = document.createElement('button');
        closeButton.textContent = 'X';
        closeButton.style.position = 'absolute';
        closeButton.style.top = '10px';
        closeButton.style.right = '10px';
        closeButton.style.border = 'none';
        closeButton.style.background = 'none';
        closeButton.style.fontSize = '16px';
        closeButton.style.cursor = 'pointer';
        closeButton.onclick = () => {
            formContainer.remove();
            formVisible = false;
        };

        // TextArea for English Text with top margin adjustment
        const englishTextArea = document.createElement('textarea');
        englishTextArea.placeholder = 'English Text';
        englishTextArea.style.width = '100%';
        englishTextArea.style.minHeight = '200px';
        englishTextArea.style.margin = '15px 0 10px 0';
        englishTextArea.style.color = 'black';
        englishTextArea.value = markedText || '';

        // Translate Button
        const translateButton = document.createElement('button');
        translateButton.textContent = 'Translate';
        translateButton.style.padding = '10px 20px';
        translateButton.style.backgroundColor = '#007bff';
        translateButton.style.color = '#fff';
        translateButton.style.border = 'none';
        translateButton.style.borderRadius = '4px';
        translateButton.style.cursor = 'pointer';

        // TextArea for Polish Translation with spinner overlay
        const translationContainer = document.createElement('div');
        translationContainer.style.position = 'relative';
        translationContainer.style.marginBottom = '10px';


        const polishTextArea = document.createElement('textarea');
        polishTextArea.placeholder = 'Polish Translation';
        polishTextArea.style.width = '100%';
        polishTextArea.style.color = 'black';
        polishTextArea.style.resize = 'none';
        polishTextArea.style.minHeight = '200px';
        polishTextArea.style.marginTop = '10px';

        const progressIcon = document.createElement('div');
        progressIcon.style.position = 'absolute';
        progressIcon.style.top = '50%';
        progressIcon.style.left = '50%';
        progressIcon.style.transform = 'translate(-50%, -50%)';
        progressIcon.style.width = '30px';
        progressIcon.style.height = '30px';
        progressIcon.style.border = '4px solid #f3f3f3';
        progressIcon.style.borderTop = '4px solid #007bff';
        progressIcon.style.borderRadius = '50%';
        progressIcon.style.animation = 'spin 1s linear infinite';
        progressIcon.style.display = 'none';

        // Keyframe animation for progress icon
        const styleSheet = document.createElement('style');
        styleSheet.textContent = `
            @keyframes spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
        `;
        document.head.appendChild(styleSheet);

        translationContainer.appendChild(polishTextArea);
        translationContainer.appendChild(progressIcon);

        // Save with sound checkbox
        const saveWithSoundLabel = document.createElement('label');
        const saveWithSoundCheckbox = document.createElement('input');
        saveWithSoundCheckbox.type = 'checkbox';
        saveWithSoundCheckbox.checked = true;
        saveWithSoundLabel.appendChild(saveWithSoundCheckbox);
        saveWithSoundLabel.appendChild(document.createTextNode(' Save with sound'));
        saveWithSoundLabel.style.display = 'block';
        saveWithSoundLabel.style.marginBottom = '10px';

        // Generate examples checkbox (unchecked by default)
        const generateExamplesLabel = document.createElement('label');
        const generateExamplesCheckbox = document.createElement('input');
        generateExamplesCheckbox.type = 'checkbox';
        generateExamplesCheckbox.checked = false;
        generateExamplesLabel.appendChild(generateExamplesCheckbox);
        generateExamplesLabel.appendChild(document.createTextNode(' Generate examples'));
        generateExamplesLabel.style.display = 'block';
        generateExamplesLabel.style.marginBottom = '10px';

        // Save button (initially disabled)
        const saveButton = document.createElement('button');
        saveButton.textContent = 'Save';
        saveButton.style.padding = '10px 20px';
        saveButton.style.backgroundColor = '#007bff';
        saveButton.style.color = '#fff';
        saveButton.style.border = 'none';
        saveButton.style.borderRadius = '4px';
        saveButton.style.cursor = 'pointer';
        saveButton.disabled = true;

        // Append elements to form container
        formContainer.appendChild(closeButton);
        formContainer.appendChild(englishTextArea);
        formContainer.appendChild(translateButton);
        formContainer.appendChild(translationContainer);
        formContainer.appendChild(saveWithSoundLabel);
        formContainer.appendChild(generateExamplesLabel);
        formContainer.appendChild(saveButton);
        document.body.appendChild(formContainer);

        // Show initial loading icon if marked text is present
        if (markedText) {
            toggleLoading(true, progressIcon);
            fetchTranslation(markedText)
                .then((translatedText) => {
                    polishTextArea.value = translatedText;
                    toggleLoading(false, progressIcon);
                    checkInputs(); // Check if inputs are filled after translation
                })
                .catch((error) => {
                    console.error('Translation error:', error);
                    toggleLoading(false, progressIcon);
                });
        }

        // Handle translate button click
        translateButton.addEventListener('click', () => {
            toggleLoading(true, progressIcon);
            fetchTranslation(englishTextArea.value)
                .then((translatedText) => {
                    polishTextArea.value = translatedText;
                    toggleLoading(false, progressIcon);
                    checkInputs(); // Check if inputs are filled after translation
                })
                .catch((error) => {
                    console.error('Translation error:', error);
                    toggleLoading(false, progressIcon);
                });
        });

        // Check if both text areas have values to enable/disable the save button
        function checkInputs() {
            saveButton.disabled = !(englishTextArea.value.trim() && polishTextArea.value.trim());
        }

        // Listen for input changes in text areas to enable save button as needed
        englishTextArea.addEventListener('input', checkInputs);
        polishTextArea.addEventListener('input', checkInputs);

        // Handle save button click
        saveButton.addEventListener('click', () => {
            const translationRequest = {
                englishText: englishTextArea.value,
                polishText: polishTextArea.value,
                saveWithSound: saveWithSoundCheckbox.checked,
                generateExample: generateExamplesCheckbox.checked,
                apiKey: `${API_KEY}`
            };
            saveTranslation(translationRequest);

            // Close the form after submission
            formContainer.remove();
            formVisible = false;
        });
    }

    // Function to fetch translation for given text
    function fetchTranslation(text) {
        const translationRequest = {
            englishText: text,
            apiKey: API_KEY
        };

        return new Promise((resolve, reject) => {
            GM_xmlhttpRequest({
                method: 'POST',
                url: `http://${HOST}/language-learning/translate`,
                data: JSON.stringify(translationRequest),
                headers: {
                    'Content-Type': 'application/json'
                },
                onload: function(response) {
                    if (response.status === 200) {
                        resolve(response.responseText);
                    } else {
                        reject('Translation request failed');
                    }
                },
                onerror: function(err) {
                    reject(err);
                }
            });
        });
    }

    // Function to save translation data
    function saveTranslation(translationRequest) {
        GM_xmlhttpRequest({
            method: 'POST',
            url: `http://${HOST}/language-learning/translation`,
            data: JSON.stringify(translationRequest),
            headers: {
                'Content-Type': 'application/json'
            },
            onload: function(response) {
                if (response.status === 200) {
                    console.log('Saved translation:', response.responseText);
                } else {
                    console.error('Error:', response.responseText);
                    alert("Save failed: " + response.responseText);
                }
            },
            onerror: function(err) {
                console.error('Save error:', err);
                alert("Error in plugin: " + err);
            }
        });
    }

    // Function to toggle loading icon visibility
    function toggleLoading(isLoading, icon) {
        icon.style.display = isLoading ? 'block' : 'none';
    }

})();