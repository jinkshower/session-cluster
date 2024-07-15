create table member (
    id bigint not null auto_increment,
    email varchar(255) not null,
    password varchar(255) not null,
    name varchar(255) not null,
    primary key (id)
);
