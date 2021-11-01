INSERT INTO users(login, password, role, email)
VALUES
('arty1', '1234', 'CLIENT', 'email1@gmail.com'),
('arty2', '1234', 'CLIENT', 'email2@gmail.com'),
('arty3', '1234', 'CLIENT', 'email3@gmail.com'),
('arty4', '1234', 'CLIENT', 'email4@gmail.com'),
('arty5', '1234', 'MANAGER', 'email5@gmail.com'),
('arty6', '1234', 'COURIER', 'email6@gmail.com');

INSERT INTO groups(name)
VALUES
('group1'),
('group2');

INSERT INTO users_groups(users_id, groups_id)
VALUES
(1, 1),
(3, 1),
(2, 2),
(4, 2);

INSERT INTO supplier(name)
VALUES
('sony'),
('samsung'),
('apple');

INSERT INTO product(name, publication_date, update_date, description, price, supplier_id, product_status)
VALUES
('PC1', '2021-10-10', '2021-10-10', 'fast pc1', '100.00', 1, 'AVAILABLE'),
('Note1', '2021-10-10', '2021-10-10', 'fast note1', '100.00', 1, 'AVAILABLE'),
('PC2', '2021-10-10', '2021-10-10', 'fast pc2', '200.00', 2, 'AVAILABLE'),
('Note2', '2021-10-10', '2021-10-10', 'fast note2', '200.00', 2, 'AVAILABLE'),
('PC3', '2021-10-10', '2021-10-10', 'fast pc3', '300.00', 3, 'AVAILABLE'),
('Note3', '2021-10-10', '2021-10-10', 'fast note3', '300.00', 3, 'NOT_AVAILABLE');

INSERT INTO attachment_files(product_id, link)
VALUES
(1, 'link1'),
(2, 'link2'),
(3, 'link3'),
(4, 'link4'),
(5, 'link5'),
(6, 'link6');

INSERT INTO category(name)
VALUES
('PC'),
('Note');

INSERT INTO product_category(product_id, category_id)
VALUES
(1, 1),
(2, 2),
(3, 1),
(4, 2),
(5, 1),
(6, 2);
