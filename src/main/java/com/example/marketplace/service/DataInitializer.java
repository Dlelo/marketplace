package com.example.marketplace.service;

import com.example.marketplace.model.Agent;
import com.example.marketplace.model.Role;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.AgentRepository;
import com.example.marketplace.repository.RoleRepository;
import com.example.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        createDefaultAgent();
    }

    private void createDefaultAgent() {
        String agentEmail = "agent@yayaconnect.com";

        // Skip if already exists
        if (agentRepository.findByUser_Email(agentEmail).isPresent()) {
            return;
        }

        Role agentRole = roleRepository.findByName("AGENT")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("AGENT");
                    r.setDescription("Platform agent managing house helps");
                    return roleRepository.save(r);
                });

        User user = new User();
        user.setName("yayaConnect Agent");
        user.setEmail(agentEmail);
        user.setPhoneNumber("0700000000");
        user.setPassword(passwordEncoder.encode("yaya@2025"));
        user.setRoles(Set.of(agentRole));
        User savedUser = userRepository.save(user);

        Agent agent = new Agent();
        agent.setUser(savedUser);
        agent.setFullName("yayaConnect Agent");
        agent.setEmail(agentEmail);
        agent.setPhoneNumber("0700000000");
        agent.setLocationOfOperation("Nairobi");
        agent.setHomeLocation("Nairobi");
        agent.setVerified(true);
        agentRepository.save(agent);

        log.info("Default yayaConnect agent created successfully");
    }
}
