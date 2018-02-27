/**
 * Author:  Joonas
 * Created: Feb 26, 2018
 *
 * Table creation clauses and mock data for testing.
 *
 * Written for PostgreSQL. Works with SQLite by substituting "SERIAL" with
 * "integer" in table declarations.
 */

CREATE TABLE Smoothie (
    id SERIAL UNIQUE PRIMARY KEY,
    name varchar(255)
);

CREATE TABLE Ingredient (
    id SERIAL UNIQUE PRIMARY KEY,
    name varchar(255)
);

CREATE TABLE SmoothieIngredient (
    id SERIAL UNIQUE PRIMARY KEY,
    smoothie_id integer REFERENCES Smoothie(id) ON DELETE CASCADE,
    ingredient_id integer REFERENCES Ingredient(id) ON DELETE CASCADE,
    ordering integer,
    amount varchar(255),
    info varchar(255)
);

/** Mock data */

INSERT INTO Smoothie (name)
VALUES ('Strawberry Milkshake');

INSERT INTO Smoothie (name)
VALUES ('Chocolate DeLuxe');

INSERT INTO Ingredient (name)
VALUES ('Milk');

INSERT INTO Ingredient (name)
VALUES ('Ice cream');

INSERT INTO Ingredient (name)
VALUES ('Strawberries');

INSERT INTO Ingredient (name)
VALUES ('Chocolate');

INSERT INTO Ingredient (name)
VALUES ('Cream');

INSERT INTO SmoothieIngredient (smoothie_id, ingredient_id, ordering, amount, info)
VALUES (1, 1, 2, '2 dl', 'Fresh');

INSERT INTO SmoothieIngredient (smoothie_id, ingredient_id, ordering, amount, info)
VALUES (1, 2, 3, '1 scoop', 'Whatever flavor you like. Mix well.');

INSERT INTO SmoothieIngredient (smoothie_id, ingredient_id, ordering, amount, info)
VALUES (1, 3, 1, 'A handful', '');

INSERT INTO SmoothieIngredient (smoothie_id, ingredient_id, ordering, amount, info)
VALUES (2, 4, 1, '2 bars', 'Please melt it');

INSERT INTO SmoothieIngredient (smoothie_id, ingredient_id, ordering, amount, info)
VALUES (2, 2, 2, '2 scoops', '');

INSERT INTO SmoothieIngredient (smoothie_id, ingredient_id, ordering, amount, info)
VALUES (2, 1, 3, '3 dl', 'Mix hard');
