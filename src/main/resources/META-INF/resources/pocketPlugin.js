// ==UserScript==
// @name         My Tools Pocket
// @namespace    http://tampermonkey.net/
// @version      2024-10-06
// @description  Plugin for my tools pocket
// @author       You
// @icon         https://cdn2.iconfinder.com/data/icons/social-icons-33/128/Pocket-512.png
// @match        *://*/*
// @grant        GM_xmlhttpRequest
// @connect      localhost
// @connect      192.168.1.205:8091
// @require      https://cdn.jsdelivr.net/npm/quill@2.0.2/dist/quill.js
// @run-at       document-start
// @resource     IMPORTED_CSS https://cdn.jsdelivr.net/npm/quill@2.0.2/dist/quill.snow.css
// @grant        GM_getResourceText
// @grant        GM_addStyle
// ==/UserScript==

(function() {
    'use strict';
    const my_css = GM_getResourceText("IMPORTED_CSS");
    GM_addStyle(my_css);

    const HOST = "192.168.1.205:8091";

    // Load pocket names using GM_xmlhttpRequest to avoid CORS issues
    async function loadPocketNames(host) {
        let pre_url = 'http://';

        return new Promise((resolve, reject) => {
            GM_xmlhttpRequest({
                method: 'GET',
                url: pre_url+host+'/pocket/pocket-names',
                onload: function(response) {
                    if (response.status === 200) {
                        const pocketNames = JSON.parse(response.responseText);
                        resolve(pocketNames);
                    } else {
                        reject('Error loading pocket names');
                    }
                },
                onerror: function(err) {
                    reject(err);
                }
            });
        });
    }

    // Function to add data to pocket using GM_xmlhttpRequest
    async function addToPocket(host, pocketRequest) {
        return new Promise((resolve, reject) => {
            GM_xmlhttpRequest({
                method: 'POST',
                url: `http://${host}/pocket/pocket-item`,
                data: JSON.stringify(pocketRequest),
                headers: {
                    'Content-Type': 'application/json'
                },
                onload: function(response) {
                    if (response.status === 200) {
                        console.log('Response from server:', response.responseText);
                        resolve(response.responseText);
                    } else {
                        console.error('Error:', response.responseText);
                        alert("Failed: " + response.responseText);
                        reject(response.responseText);
                    }
                },
                onerror: function(err) {
                    console.error('Error sending request:', err);
                    alert("Error in plugin: " + err);
                    reject(err);
                }
            });
        });
    }

    let shiftPCount = 0;
    let formVisible = false;

    // Monitor for Shift + P key press
    document.addEventListener('keydown', (e) => {
        if (e.key === 'P' && e.shiftKey) {
            shiftPCount++;
            if (shiftPCount === 3) {
                if (!formVisible) {
                    showForm();
                }
                shiftPCount = 0;
            }
        } else {
            shiftPCount = 0; // reset if not Shift+P
        }
    });

    // Show the form with Pocket, Summary, Content and buttons
    async function showForm() {
        formVisible = true;

        // Create form overlay container
        const formContainer = document.createElement('div');
        formContainer.style.position = 'fixed';
        formContainer.style.top = '50%';
        formContainer.style.left = '50%';
        formContainer.style.transform = 'translate(-50%, -50%)';
        formContainer.style.width = '600px';
        formContainer.style.padding = '20px';
        formContainer.style.backgroundColor = '#fff';
        formContainer.style.boxShadow = '0px 0px 10px rgba(0, 0, 0, 0.2)';
        formContainer.style.zIndex = '9999';
        formContainer.style.borderRadius = '8px';

        // Create close button (X) in the top-right corner
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

        // Create dropdown for pocketName
        const pocketLabel = document.createElement('label');
        pocketLabel.textContent = 'Pocket Name:';
        pocketLabel.style.color = 'black';

        const pocketSelect = document.createElement('select');
        const pockets = await loadPocketNames(HOST);
        pockets.forEach(pocket => {
            const option = document.createElement('option');
            option.value = pocket;
            option.textContent = pocket;
            pocketSelect.appendChild(option);
        });

        // Create input for summary
        const summaryLabel = document.createElement('label');
        summaryLabel.textContent = 'Summary:';
        summaryLabel.style.color = 'black';
        const summaryInput = document.createElement('input');
        summaryInput.type = 'text';
        summaryInput.style.color = 'black';
        summaryInput.style.width = '100%';
        summaryInput.style.marginBottom = '10px';

        // Create div for Quill editor
        const contentLabel = document.createElement('label');
        contentLabel.textContent = 'Content:';
        contentLabel.style.color = 'black';
        const quillContainer = document.createElement('div');
        quillContainer.id = 'quillEditor';
        quillContainer.style.height = '400px';
        quillContainer.style.marginBottom = '10px';

        // Create submit button
        const submitButton = document.createElement('button');
        submitButton.textContent = 'Submit';
        submitButton.style.marginTop = '10px';
        submitButton.style.padding = '10px 20px';
        submitButton.style.backgroundColor = '#007bff';
        submitButton.style.color = '#fff';
        submitButton.style.border = 'none';
        submitButton.style.borderRadius = '4px';
        submitButton.style.cursor = 'pointer';

        // Append elements to the form container
        formContainer.appendChild(closeButton);
        formContainer.appendChild(pocketLabel);
        formContainer.appendChild(pocketSelect);
        formContainer.appendChild(document.createElement("hr"));
        formContainer.appendChild(summaryLabel);
        formContainer.appendChild(summaryInput);
        formContainer.appendChild(contentLabel);
        formContainer.appendChild(quillContainer);
        formContainer.appendChild(submitButton);
        document.body.appendChild(formContainer);

        // Initialize Quill editor
        const quill = new Quill('#quillEditor', {
            theme: 'snow'
        });

        // Handle submit button click
        submitButton.addEventListener('click', () => {
            const pocketRequest = {
                content: quill.root.innerHTML,
                summary: summaryInput.value,
                pocketName: pocketSelect.value
            };

            addToPocket(HOST, pocketRequest);

            // Close the form after submission
            formContainer.remove();
            formVisible = false;
        });
    }
})();