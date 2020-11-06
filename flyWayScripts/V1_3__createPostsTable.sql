create table if not exists posts
(
    id           bigint auto_increment primary key,
    user_id      bigint not null,
    content      varchar(500),
    creating_date bigint,
    updating_date bigint,

    unique (id),
    unique (user_id),
    unique (user_id,content, creating_date),

    constraint user_id_fk foreign key (user_id) references users (id)

)