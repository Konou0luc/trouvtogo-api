-- Données de référence : catégories (pas de liste en dur dans le code Java)
-- Exécuté après Hibernate si spring.jpa.defer-datasource-initialization=true
INSERT INTO categories (nom, description) VALUES
    ('PHONE', 'Téléphones et accessoires'),
    ('IDENTITY_PAPERS', 'Papiers d''identité'),
    ('KEYS', 'Clés'),
    ('LUGGAGE', 'Bagages'),
    ('WALLET', 'Portefeuilles'),
    ('ELECTRONICS', 'Électronique'),
    ('JEWELRY', 'Bijoux'),
    ('CLOTHING', 'Vêtements'),
    ('PETS', 'Animaux'),
    ('BOOKS', 'Livres'),
    ('OTHER', 'Autre')
ON CONFLICT (nom) DO NOTHING;
