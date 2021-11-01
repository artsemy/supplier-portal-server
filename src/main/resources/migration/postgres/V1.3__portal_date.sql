INSERT INTO users(login, password, role, email)
VALUES
('zero_courier', '1234', 'COURIER', 'email0@gmail.com');

UPDATE users SET id = 0 WHERE id = 7;

INSERT INTO orders(owner_id, courier_id, orders_status, address)
VALUES
(1, 0, 'ORDERED', 'address1'),
(2, 6, 'ASSIGNED', 'address2'),
(3, 6, 'ASSIGNED', 'address3'),
(4, 6, 'DELIVERED', 'address4');

INSERT INTO orders_product(orders_id, product_id, amount)
VALUES
(1, 1, 1),
(1, 2, 3),
(2, 3, 1),
(3, 4, 1),
(4, 6, 1);


INSERT INTO product(name, publication_date, update_date, description, price, supplier_id, product_status)
VALUES
('PC4', '2021-10-10', '2021-10-10', 'fast pc4', '50.00', 1, 'SPECIAL_OFFER');

INSERT INTO special_offer(product_id, users_id)
VALUES
(7, 1);


INSERT INTO subscriptions_supplier(users_id, supplier_id)
VALUES
(2, 1),
(3, 2),
(4, 3);

INSERT INTO subscriptions_category(users_id, category_id)
VALUES
(5, 1),
(6, 2);
