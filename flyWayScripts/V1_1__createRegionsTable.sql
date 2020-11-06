create table if not exists regions
(
    id   bigint  auto_increment primary key,
    name varchar(50) not null,

    UNIQUE (id),
    UNIQUE (name)
)

