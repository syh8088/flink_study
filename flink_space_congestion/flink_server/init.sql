CREATE DATABASE space_congestion;

create table space_congestion.space_congestion_event_report
(
    no       bigint auto_increment
        primary key,
    window_start timestamp(3) not null,
    window_end   timestamp(3) not null,
    space_id     varchar(30)  not null,
    space_congestion        double  not null
);