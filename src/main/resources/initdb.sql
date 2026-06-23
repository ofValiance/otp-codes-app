create table users (
    id bigserial primary key,
    login varchar(255) unique not null,
    password_hash varchar(255) not null,
    role varchar(16) not null check (role in ('ADMIN', 'USER'))
);

create table otp_config (
    id int generated always as (1) stored primary key,
    code_length int not null default 6,
    ttl_seconds int not null default 180
);

create table codes (
    id serial primary key,
    user_id bigint not null references users(id) on delete cascade,
    operation_id bigint not null,
    code_hash varchar(255) not null,
    status varchar(16) not null check (status in ('ACTIVE', 'USED', 'EXPIRED')) default 'ACTIVE',
    created_at timestamptz not null default now(),
    expires_at timestamptz not null,
    used_at timestamptz
);

insert into otp_config (code_length, ttl_seconds)
values (6, 300);