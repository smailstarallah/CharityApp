package ma.emsi.charityapp.services;

import ma.emsi.charityapp.documents.UserActions;

import java.util.List;

public interface UserActionsService {
    /**
     * Enregistre une action utilisateur
     *
     * @param userId  ID de l'utilisateur
     * @param action  Type d'action
     * @param page    Page concernée
     * @param details Détails de l'action
     */
    void logAction(String userId, String action, String page, String details);
    List<UserActions> getAllActions();
}
