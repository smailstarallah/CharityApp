package ma.emsi.charityapp.repositories;

import ma.emsi.charityapp.documents.UserActions;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserActionsRepository extends MongoRepository<UserActions, String> {
}
