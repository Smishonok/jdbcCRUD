alter table posts drop foreign key user_id_fk;

alter table posts
    add constraint user_id_fk
        foreign key (user_id) references users (id)
            on delete cascade;