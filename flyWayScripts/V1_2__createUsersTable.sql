create table if not exists users
(
    id bigint not null auto_increment primary key,
    first_name varchar(50) not null,
    last_name varchar(50) not null ,
    region_id bigint not null ,
    role enum('ADMIN','MODERATOR','USER'),

    unique(id),
    unique (first_name,last_name),

    constraint users_region_id_fk
     foreign key (region_id) references regions(id)
)