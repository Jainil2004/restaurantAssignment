function getToken() {
    return localStorage.getItem("jwt");
}

function getUserInfo() {
    const token = getToken();
    if (!token) return null;
    
    try {
        // Decode JWT payload (basic decoding without verification)
        const payload = JSON.parse(atob(token.split('.')[1]));
        return {
            username: payload.sub,
            role: payload.role,
            country: payload.country
        };
    } catch (e) {
        return null;
    }
}

async function fetchWithAuth(url, options = {}) {
    const token = getToken();
    if (!token) {
        window.location.href = "index.html";
        throw new Error("Not logged in");
    }

    console.log("Making request to:", url);
    console.log("With token:", token);

    const res = await fetch(url, {
        ...options,
        headers: {
            "Authorization": "Bearer " + token,
            ...(options.headers || {})
        }
    });

    console.log("Response status:", res.status);
    console.log("Response headers:", res.headers);

    if (res.status === 401) {
        localStorage.removeItem("jwt");
        window.location.href = "index.html";
        throw new Error("Unauthorized");
    }

    if (!res.ok) {
        const errorText = await res.text();
        console.log("Error response text:", errorText);
        throw new Error(errorText || `HTTP ${res.status}`);
    }

    return res.json();
}
