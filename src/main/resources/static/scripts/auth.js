// auth.js

// Fonction pour ajouter automatiquement le token JWT dans les requêtes HTTP
function addJwtToHeaders(headers = {}) {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
}

// Exemple d'utilisation pour fetch avec authentification
async function fetchWithAuth(url, options = {}) {
    options.headers = addJwtToHeaders(options.headers || {});
    return fetch(url, options);
}

// Fonction pour déconnecter l'utilisateur
function logout() {
    localStorage.removeItem('jwtToken');
    window.location.href = '/api/superadmin/login';
}

// Vérifier si l'utilisateur est connecté pour accéder aux pages nécessitant une connexion
function requireAuth() {
    const token = localStorage.getItem('jwtToken');
    if (!token) {
        window.location.href = '/api/superadmin/login';
    }
}

