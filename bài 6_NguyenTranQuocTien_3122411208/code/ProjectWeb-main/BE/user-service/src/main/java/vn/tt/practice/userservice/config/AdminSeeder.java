package vn.tt.practice.userservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.tt.practice.userservice.model.User;
import vn.tt.practice.userservice.repository.UserRepo;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${app.seed.admin.enabled:true}")
    private boolean adminSeedEnabled;

    @Value("${app.seed.admin.username:admin}")
    private String adminUsername;

    @Value("${app.seed.admin.email:admin@gmail.com}")
    private String adminEmail;

    @Value("${app.seed.admin.password:12345}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (!adminSeedEnabled) {
            return;
        }

        if (userRepo.findByEmail(adminEmail).isPresent()) {
            log.info("Admin account already exists for email {}", adminEmail);
            return;
        }

        User admin = User.builder()
                .username(adminUsername)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .isAdmin(true)
                .expirationDate(86400)
                .build();

        userRepo.save(admin);
        log.info("Seeded admin account: {}", adminEmail);
    }
}
