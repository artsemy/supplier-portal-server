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

INSERT INTO user_group(id, user_id, group_id)
VALUES
(1, 1, 1),
(2, 3, 1),
(3, 2, 2),
(4, 4, 2);
