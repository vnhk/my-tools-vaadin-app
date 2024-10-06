const pocketRequest = {
    content: "Some content for the pocket item",
    summary: "Summary of the content",
    pocketName: "Main"
};

async function addToPocket() {
    try {
        let pre = 'http://';
        let host = 'localhost:8081';
        const response = await fetch(pre + host + '/pocket/pocket-item', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(pocketRequest)
        });

        if (response.ok) {
            const result = await response.text();
            console.log('Response from server:', result);
        } else {
            console.error('Error:', response.statusText);
        }
    } catch (error) {
        console.error('Error sending request:', error);
    }
}
