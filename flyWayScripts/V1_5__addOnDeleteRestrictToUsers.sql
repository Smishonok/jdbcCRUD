alter table users drop foreign key users_region_id_fk;

alter table users
    add constraint users_region_id_fk
        foreign key (region_id) references regions (id);



