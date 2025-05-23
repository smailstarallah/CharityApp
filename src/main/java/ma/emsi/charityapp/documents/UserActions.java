package ma.emsi.charityapp.documents;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_actions")
public class UserActions {
    @Id
    private String userId;
    @Field("actions")
    private List<Action> actions = new ArrayList<>();

    /**
     * Ajoute une nouvelle action à la liste
     * @param action Action à ajouter
     */
    public void addAction(Action action) {
        if (this.actions == null) {
            this.actions = new ArrayList<>();
        }
        this.actions.add(action);
    }

    /**
     * Crée et ajoute une nouvelle action avec les paramètres fournis
     * @param action Type d'action
     * @param page Page concernée
     * @param details Détails de l'action
     */
    public void logAction(String action, String page, String details) {
        Action newAction = new Action(action, page, details);
        addAction(newAction);
    }

    @Data
    @NoArgsConstructor
    public static class Action {
        private String action;
        private String page;
        private LocalDateTime timestamp;
        private String details;

        public Action(String action, String page, String details) {
            this.action = action;
            this.page = page;
            this.details = details;
            this.timestamp = LocalDateTime.now();
        }
    }
}
