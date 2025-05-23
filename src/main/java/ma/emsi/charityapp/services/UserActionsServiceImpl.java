package ma.emsi.charityapp.services;


import lombok.extern.slf4j.Slf4j;
import ma.emsi.charityapp.documents.UserActions;
import ma.emsi.charityapp.repositories.UserActionsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserActionsServiceImpl implements UserActionsService {

    UserActionsRepository userActionsRepository;

    public UserActionsServiceImpl(UserActionsRepository userActionsRepository) {
        this.userActionsRepository = userActionsRepository;
    }

    @Override
    public void logAction(String userId, String action, String page, String details) {
        UserActions userActions = userActionsRepository.findById(userId).orElse(null);
        if (userActions == null) {
            log.info("Creating new UserActions for userId: {}", userId);
            userActions =new UserActions(userId, new ArrayList<>());
        }
        userActions.logAction(action, page, details);
        userActionsRepository.save(userActions);
    }

    @Override
    public List<UserActions> getAllActions() {
        return userActionsRepository.findAll();
    }
}
