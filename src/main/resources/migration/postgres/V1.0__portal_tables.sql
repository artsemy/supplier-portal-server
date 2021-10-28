CREATE TYPE USER_ROLE AS ENUM ('CLIENT', 'MANAGER', 'COURIER');

CREATE TABLE IF NOT EXISTS users (
    id SERIAL,
    login VARCHAR(32) NOT NULL,
    password VARCHAR(32) NOT NULL,
    role USER_ROLE DEFAULT 'CLIENT',
    email VARCHAR(32) NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS groups (
    id SERIAL,
    name VARCHAR(32) NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS users_groups (
    id SERIAL,
    users_id BIGINT NOT NULL,
    groups_id BIGINT NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT users_fk FOREIGN KEY (users_id) REFERENCES users(id),
    CONSTRAINT groups_fk FOREIGN KEY (groups_id) REFERENCES groups(id)
);

CREATE TABLE IF NOT EXISTS supplier (
    id SERIAL,
    name VARCHAR(32) NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS category (
    id SERIAL,
    name VARCHAR(32) NOT NULL,
    PRIMARY KEY(id)
);

CREATE TYPE PRODUCT_STATUS AS ENUM ('IN_PROCESSING', 'AVAILABLE', 'NOT_AVAILABLE', 'SPECIAL_OFFER');

CREATE TABLE IF NOT EXISTS product (
    id SERIAL,
    name VARCHAR(32) NOT NULL,
    publication_date DATE NOT NULL,
    update_date DATE NOT NULL,
    description VARCHAR(500) NOT NULL,
    price VARCHAR(32) NOT NULL,
    supplier_id BIGINT NOT NULL,
    product_status PRODUCT_STATUS DEFAULT 'IN_PROCESSING',
    PRIMARY KEY(id),
    CONSTRAINT supplier_fk FOREIGN KEY (supplier_id) REFERENCES supplier(id)
);

CREATE TABLE IF NOT EXISTS attachment_files (
    id SERIAL,
    product_id BIGINT NOT NULL,
    link VARCHAR(32) NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT product_fk FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE IF NOT EXISTS product_category (
    id SERIAL,
    product_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT product_fk FOREIGN KEY (product_id) REFERENCES product(id),
    CONSTRAINT category_fk FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE IF NOT EXISTS subscriptions_category (
    id SERIAL,
    users_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT users_fk FOREIGN KEY (users_id) REFERENCES users(id),
    CONSTRAINT category_fk FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE IF NOT EXISTS subscriptions_supplier (
    id SERIAL,
    users_id BIGINT NOT NULL,
    supplier_id BIGINT NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT users_fk FOREIGN KEY (users_id) REFERENCES users(id),
    CONSTRAINT supplier_fk FOREIGN KEY (supplier_id) REFERENCES supplier(id)
);

CREATE TYPE ORDER_STATUS AS ENUM ('IN_PROCESSING', 'ORDERED', 'ASSIGNED', 'DELIVERED');

CREATE TABLE IF NOT EXISTS orders (
    id SERIAL,
    owner_id BIGINT NOT NULL,
    courier_id BIGINT NOT NULL,
    orders_status ORDER_STATUS DEFAULT 'IN_PROCESSING',
    address VARCHAR(32) NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT owner_fk FOREIGN KEY (owner_id) REFERENCES users(id),
    CONSTRAINT courier_fk FOREIGN KEY (courier_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS orders_product (
    id SERIAL,
    orders_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    amount INT NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT orders_fk FOREIGN KEY (orders_id) REFERENCES orders(id),
    CONSTRAINT product_fk FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE IF NOT EXISTS special_offer (
    id SERIAL,
    product_id BIGINT NOT NULL,
    users_id BIGINT NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT product_fk FOREIGN KEY (product_id) REFERENCES product(id),
    CONSTRAINT users_fk FOREIGN KEY (users_id) REFERENCES users(id)
);
