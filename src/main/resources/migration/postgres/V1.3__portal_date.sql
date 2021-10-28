INSERT INTO users(id, login, password, role, email)
VALUES
(0, 'zero_courier', '1234', 'COURIER', 'email0@gmail.com');

INSERT INTO orders(id, owner_id, courier_id, orders_status, address)
VALUES
(1, 1, 0, 'ORDERED', 'address1'),
(2, 2, 6, 'ASSIGNED', 'address2'),
(3, 3, 6, 'ASSIGNED', 'address3'),
(4, 4, 6, 'DELIVERED', 'address4');

INSERT INTO orders_product(id, orders_id, product_id, amount)
VALUES
(1, 1, 1, 1),
(2, 1, 2, 3),
(3, 2, 3, 1),
(4, 3, 4, 1),
(5, 4, 6, 1);


INSERT INTO product(id, name, publication_date, update_date, description, price, supplier_id, product_status)
VALUES
(7, 'PC4', '2021-10-10', '2021-10-10', 'fast pc4', '50.00', 1, 'SPECIAL_OFFER');

INSERT INTO special_offer(id, product_id, users_id)
VALUES
(1, 7, 1);


INSERT INTO subscriptions_supplier(id, users_id, supplier_id)
VALUES
(1, 2, 1),
(2, 3, 2),
(3, 4, 3);

INSERT INTO subscriptions_category(id, users_id, category_id)
VALUES
(1, 5, 1),
(2, 6, 2);
