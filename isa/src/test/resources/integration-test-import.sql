
INSERT INTO isa_role (id, name) VALUES (1, 'ROLE_USER');
INSERT INTO isa_role (id, name) VALUES (2, 'ROLE_DRIVER');
INSERT INTO isa_role (id, name) VALUES (3, 'ROLE_ADMIN');


insert into isa_vehicle_type (id, price_per_km, vehicle_type_name)
values (1, '50', 'STANDARD'),
       (2, '200', 'DELUXE'),
       (3, '120', 'COMBI');

INSERT INTO isa_user(id, email, enabled, firstname, is_blocked, last_password_reset_date, lastname, password, username, user_type)
VALUES (1, 'admin@isa-uber.com', true, 'Admin', false, CURRENT_TIMESTAMP, 'Admin', '$2a$10$/UxPf65CGIrC2n/Z5L5CHu/n1NC0DgD0dNKN07NSwpytDL5Add4uS', 'admin', 'ADMIN'),
       (2, 'admin@isa-uber.com', true, 'Dave', false, CURRENT_TIMESTAMP, 'Dave', '$2a$10$fp/z.Rqg8XAwXsUjPEYql.0DaFYCP2aBgqRy6KR9TCx3uU7NLgqP6', 'dave', 'PASSENGER'),
       (3, 'admin@isa-uber.com', true, 'golf', false, CURRENT_TIMESTAMP, 'vozac', '$2a$10$fp/z.Rqg8XAwXsUjPEYql.0DaFYCP2aBgqRy6KR9TCx3uU7NLgqP6', 'golfvozac', 'DRIVER'),
       (4, 'admin@isa-uber.com', true, 'multipla', false, CURRENT_TIMESTAMP, 'vozac', '$2a$10$fp/z.Rqg8XAwXsUjPEYql.0DaFYCP2aBgqRy6KR9TCx3uU7NLgqP6', 'multiplavozac', 'DRIVER'),
       (5, 'admin@isa-uber.com', true, 'mercedes', false, CURRENT_TIMESTAMP, 'vozac', '$2a$10$fp/z.Rqg8XAwXsUjPEYql.0DaFYCP2aBgqRy6KR9TCx3uU7NLgqP6', 'mercedesvozac', 'DRIVER');

INSERT INTO public.isa_user_role(user_id, role_id)
VALUES (1, 3),
       (2, 1),
       (3, 2),
       (4, 2),
       (5, 2);

INSERT INTO isa_location(id, latitude, longitude)
VALUES (1, 45.240356, 19.819715),
       (2, 45.239597, 19.841066),
       (3 ,45.24476, 19.824081);



INSERT INTO isa_vehicle(id, baby_friendly, number_of_seats, pet_friendly, registration_number, vehicle_model, location, vehicle_type)
VALUES (1, false, '4', false, '1234567', 'golf4', 1, 1),
       (2, true, '5', true, '123456', 'multipla', 2, 3),
       (3, false, '3', false, '12345', 'mercedes',3, 2);

INSERT INTO isa_driver(active, driver_license, occupied, id, vehicle_id)
VALUES (true, '123456', false, 3, 1),
       (true, '123456', false, 4, 2),
       (true, '123456', false, 5, 3);

INSERT INTO isa_route(id, estimated_duration, geo, length, route_price, finish_location, start_location)
VALUES (1 ,176000000000,100837,1.326,0,2,1);

