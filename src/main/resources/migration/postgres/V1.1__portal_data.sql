INSERT INTO users(id, login, password, role, email)
VALUES
(1, 'arty1', '1234', 'CLIENT', 'email1@gmail.com'),
(2, 'arty2', '1234', 'CLIENT', 'email2@gmail.com'),
(3, 'arty3', '1234', 'CLIENT', 'email3@gmail.com'),
(4, 'arty4', '1234', 'CLIENT', 'email4@gmail.com'),
(5, 'arty5', '1234', 'MANAGER', 'email5@gmail.com'),
(6, 'arty6', '1234', 'COURIER', 'email6@gmail.com');

INSERT INTO groups(id, name)
VALUES
(1, 'group1'),
(2, 'group2');

INSERT INTO users_groups(id, users_id, groups_id)
VALUES
(1, 1, 1),
(2, 3, 1),
(3, 2, 2),
(4, 4, 2);

INSERT INTO supplier(id, name)
VALUES
(1, 'sony'),
(2, 'samsung'),
(3, 'apple');

INSERT INTO product(id, name, publication_date, update_date, description, price, supplier_id, product_status)
VALUES
(1, 'PC1', '2021-10-10', '2021-10-10', 'fast pc1', '100.00', 1, 'AVAILABLE'),
(2, 'Note1', '2021-10-10', '2021-10-10', 'fast note1', '100.00', 1, 'AVAILABLE'),
(3, 'PC2', '2021-10-10', '2021-10-10', 'fast pc2', '200.00', 2, 'AVAILABLE'),
(4, 'Note2', '2021-10-10', '2021-10-10', 'fast note2', '200.00', 2, 'AVAILABLE'),
(5, 'PC3', '2021-10-10', '2021-10-10', 'fast pc3', '300.00', 3, 'AVAILABLE'),
(6, 'Note3', '2021-10-10', '2021-10-10', 'fast note3', '300.00', 3, 'NOT_AVAILABLE');

INSERT INTO attachment_files(id, product_id, link)
VALUES
(1, 1, 'link1'),
(2, 2, 'link2'),
(3, 3, 'link3'),
(4, 4, 'link4'),
(5, 5, 'link5'),
(6, 6, 'link6');

INSERT INTO category(id, name)
VALUES
(1, 'PC'),
(2, 'Note');

INSERT INTO product_category(id, product_id, category_id)
VALUES
(1, 1, 1),
(2, 2, 2),
(3, 3, 1),
(4, 4, 2),
(5, 5, 1),
(6, 6, 2);
