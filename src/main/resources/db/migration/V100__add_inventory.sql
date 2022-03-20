drop sequence if exists item_sequence cascade;
CREATE SEQUENCE item_sequence START 1001;
drop table if exists item cascade;
create table item (
    id text PRIMARY KEY DEFAULT 'ITEM'  || nextval('item_sequence'),
    name text UNIQUE,
    discount_retail numeric,
    discount_wholesale numeric,
    created_on timestamp DEFAULT CURRENT_TIMESTAMP
);

drop sequence if exists procurement_sequence cascade;
CREATE SEQUENCE procurement_sequence START 1001;
drop table if exists procurement cascade;
create table procurement (
    id text PRIMARY KEY DEFAULT 'PROCURE'  || nextval('procurement_sequence'),
    item_id text,
    quantity int,
    price numeric,
    total_price numeric,
    supplier text,
    contact_no int UNIQUE,
    purchased_on timestamp DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(item_id) REFERENCES item (id)
);

drop sequence if exists stock_sequence cascade;
CREATE SEQUENCE stock_sequence START 1001;
drop table if exists stock cascade;
create table stock (
    id text PRIMARY KEY DEFAULT 'STOCK'  || nextval('stock_sequence'),
    item_id text,
    quantity int,
    price numeric,
    total_price numeric,
    created_on timestamp DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(item_id) REFERENCES item (id)
);

drop sequence if exists customer_sequence cascade;
CREATE SEQUENCE customer_sequence START 1001;
drop table if exists customer cascade;
create table customer (
    id text PRIMARY KEY DEFAULT 'CUST'  || nextval('customer_sequence'),
    name text,
    type text,
    contact_no int UNIQUE,
    address text,
    created_on timestamp DEFAULT CURRENT_TIMESTAMP
);

drop sequence if exists orders_sequence cascade;
CREATE SEQUENCE orders_sequence START 1001;
drop table if exists orders cascade;
create table orders (
    id text PRIMARY KEY DEFAULT 'ORDER'  || nextval('orders_sequence'),
    customer_id text,
    item_id text,
    quantity int,
    price numeric,
    discount numeric,
    net_price numeric,
    total_price numeric,
    created_on timestamp DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(customer_id) REFERENCES customer (id),
    FOREIGN KEY(item_id) REFERENCES item (id)
);