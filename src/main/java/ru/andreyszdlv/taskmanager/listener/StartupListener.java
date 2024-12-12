package ru.andreyszdlv.taskmanager.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.taskmanager.service.UserService;

@Component
@RequiredArgsConstructor
@Profile({"dev", "prod"})
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private final UserService userService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        userService.createAdminIfNotExists();
    }
}