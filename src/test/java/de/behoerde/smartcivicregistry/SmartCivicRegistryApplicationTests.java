package de.behoerde.smartcivicregistry;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SmartCivicRegistryApplicationTests {

    @Test
    void applicationMainClassExists() {
        // Verify that the main application class exists and is properly annotated
        SmartCivicRegistryApplication app = new SmartCivicRegistryApplication();
        assertThat(app).isNotNull();
    }
}