INSERT INTO users(login, password, role, email)
VALUES
('arty1', 'AfGuzeGzUV8koEuz0jCJsw==', 'CLIENT', 'artsemy.k@mail.ru'),
('arty2', 'AfGuzeGzUV8koEuz0jCJsw==', 'CLIENT', 'artsemy.k@mail.ru'),
('arty3', 'AfGuzeGzUV8koEuz0jCJsw==', 'CLIENT', 'artsemy.k@mail.ru'),
('arty4', 'AfGuzeGzUV8koEuz0jCJsw==', 'CLIENT', 'artsemy.k@mail.ru'),
('arty5', 'AfGuzeGzUV8koEuz0jCJsw==', 'MANAGER', 'artsemy.k@mail.ru'),
('arty6', 'AfGuzeGzUV8koEuz0jCJsw==', 'COURIER', 'artsemy.k@mail.ru');

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
