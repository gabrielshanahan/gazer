INSERT IGNORE INTO user (id, username, email, token)
VALUES ((SELECT UUID_TO_BIN(UUID())), "Applifting", "info@applifting.cz", "93f39e2f-80de-4033-99ee-249d92736a25");

INSERT IGNORE INTO user (id, username, email, token)
VALUES ((SELECT UUID_TO_BIN(UUID())), "Batman", "batman@example.com", "dcb20f8a-5657-4f1b-9f7f-ce65739b359e");