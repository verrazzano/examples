-- Copyright (c) 2020, Oracle and/or its affiliates.
-- Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

drop table orders;
drop table order_books;

create table if not exists orders (
  id int not null auto_increment,
  order_date date,
  name char(40),
  street char(40),
  city char(40),
  state char(2),
  primary key(id)
);

create table if not exists order_books (
  order_id int,
  book_id int,
  title char(80),
  foreign key (order_id)
  references orders(id)
  on delete cascade
);

insert into orders (id, order_date, name, street, city, state)
values (1, curdate(), "Bob Down", "12 Main Rd", "Mt Everest", "NJ");

insert into order_books (order_id, book_id, title)
values (1, 2, "Harry Potter");

insert into order_books (order_id, book_id, title)
values (1, 4, "Twilight");
