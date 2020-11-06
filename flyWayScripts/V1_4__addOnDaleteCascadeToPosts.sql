alter table jdbccrud.posts drop foreign key user_id_fk;

alter table jdbccrud.posts
    add constraint user_id_fk
        foreign key (user_id) references users (id)
            on delete cascade;