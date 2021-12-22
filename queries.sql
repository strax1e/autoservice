create table SERVICE
(
    SERVICE_ID   serial primary key,
    SERVICE_NAME varchar(30)      not null,
    PRICE        double precision not null
);

insert into SERVICE(SERVICE_NAME, price)
values ('Покраска', 200),
       ('Смена шин', 200),
       ('Тонирование стекол', 100),
       ('Подкачка шин', 50),
       ('Химчистка', 100),
       ('Ремонт', 400),
       ('Мойка', 50),
       ('Тюнинг', 400);

create table SPECIALIST
(
    SPECIALIST_ID   serial primary key,
    SPECIALIST_NAME varchar(50) not null,
    PHONE_NUMBER    char(12) check (PHONE_NUMBER ~~ '+%'::char(12))
);

insert into SPECIALIST(SPECIALIST_NAME, PHONE_NUMBER)
values ('Олег Грибович', '+79847463946'),
       ('Дмитрий Муравьев', '+79983563984'),
       ('Иван Чай', '+79427458394');

create table SPECIALIST_SERVICE
(
    SPECIALIST_SERVICE_ID serial primary key,
    SERVICE_ID            int not null references SERVICE (SERVICE_ID) on delete cascade,
    SPECIALIST_ID         int not null references SPECIALIST (SPECIALIST_ID) on delete cascade
);

insert into SPECIALIST_SERVICE(SERVICE_ID, SPECIALIST_ID)
values (3, 1),
       (5, 1),
       (1, 2),
       (2, 2),
       (4, 3),
       (8, 3);

create table CLIENT
(
    CLIENT_ID    serial primary key,
    CLIENT_NAME  VARCHAR(50) not null,
    BANK         VARCHAR(100),
    PHONE_NUMBER char(12) check (PHONE_NUMBER ~~ '+%'::char(12))
);

insert into CLIENT(CLIENT_NAME, BANK, PHONE_NUMBER)
values ('Василий Чичиков', '7384388058376993753', '+79480463987'),
       ('Анастасия Романова', '2753205335638462548', '+78647446939');

create table CAR
(
    CAR_REG_NUMBER CHAR(9) primary key,
    CLIENT_ID      int not null references CLIENT (CLIENT_ID) on delete cascade
);

insert into CAR
values ('А774ДЛ078', 1),
       ('Т938ПР132', 2),
       ('Л536АВ078', 1);

create table ISSUED_SERVICE
(
    ISSUED_SERVICE_ID     serial primary key,
    COMPLETION_DATE       date,
    SPECIALIST_SERVICE_ID int,
    CAR_REG_NUMBER        char(9) not null references CAR (CAR_REG_NUMBER)
);

insert into ISSUED_SERVICE(COMPLETION_DATE, SPECIALIST_SERVICE_ID, CAR_REG_NUMBER)
values ('2021-8-13', 1, 'А774ДЛ078'),
       ('2021-8-29', 2, 'Т938ПР132'),
       ('2021-9-08', 4, 'А774ДЛ078'),
       (NULL, 3, 'Л536АВ078'),
       (NULL, 1, 'А774ДЛ078'),
       (NULL, 5, 'А774ДЛ078');

create table "user"
(
    USERNAME varchar(30) primary key,
    PASSWORD varchar(30) not null,
    ROLE     varchar(10) not null
);

insert into "user" (USERNAME, PASSWORD, ROLE)
values ('VC', 'client', 'CLIENT'),
       ('AR', 'client', 'CLIENT'),
       ('admin', 'admin', 'ADMIN'),
       ('OG', 'specialist', 'SPECIALIST'),
       ('DM', 'specialist', 'SPECIALIST'),
       ('IC', 'specialist', 'SPECIALIST');

create table USER_CLIENT
(
    CLIENT_ID int references CLIENT (CLIENT_ID) on delete cascade,
    USERNAME  varchar(30) references "user" (USERNAME) on delete cascade,

    primary key (CLIENT_ID, USERNAME)
);

insert into USER_CLIENT (CLIENT_ID, USERNAME)
values (1, 'VC'),
       (2, 'AR');

create table USER_SPECIALIST
(
    SPECIALIST_ID int references SPECIALIST (SPECIALIST_ID) on delete cascade,
    USERNAME      varchar(30) references "user" (USERNAME) on delete cascade,

    primary key (SPECIALIST_ID, USERNAME)
);

insert into USER_SPECIALIST (SPECIALIST_ID, USERNAME)
values (1, 'OG'),
       (2, 'DM'),
       (3, 'IC');

select CAR.CLIENT_ID, CLIENT_NAME, CAR_REG_NUMBER
from CAR
         left join CLIENT on CAR.CLIENT_ID = CLIENT.CLIENT_ID;

select SPECIALIST_NAME, SERVICE_NAME, PRICE
from ISSUED_SERVICE iss_ser
         left join SPECIALIST_SERVICE spec_ser on iss_ser.SPECIALIST_SERVICE_ID = spec_ser.SPECIALIST_SERVICE_ID
         left join SPECIALIST spec on spec_ser.SPECIALIST_ID = spec.specialist_id
         left join SERVICE ser on spec_ser.SERVICE_ID = ser.service_id
where CAR_REG_NUMBER = 'Л536АВ078'
  AND COMPLETION_DATE IS NULL;

select sum(PRICE)
from ISSUED_SERVICE iss_ser
         left join SPECIALIST_SERVICE spec_ser on iss_ser.SPECIALIST_SERVICE_ID = spec_ser.SPECIALIST_SERVICE_ID
         left join SERVICE ser on spec_ser.SERVICE_ID = ser.service_id
         left join CAR c on iss_ser.CAR_REG_NUMBER = c.car_reg_number
         left join USER_CLIENT UC on c.CLIENT_ID = UC.CLIENT_ID
where USERNAME = 'VC'
group by c.CLIENT_ID;

select car.CAR_REG_NUMBER, CLIENT_NAME, cl.CLIENT_ID
from ISSUED_SERVICE iss_ser
         left join CAR car on iss_ser.CAR_REG_NUMBER = car.CAR_REG_NUMBER
         left join CLIENT cl on car.CLIENT_ID = cl.CLIENT_ID
group by car.CAR_REG_NUMBER, CLIENT_NAME, cl.CLIENT_ID;

select SERVICE_NAME, PRICE, CAR_REG_NUMBER, COMPLETION_DATE
from ISSUED_SERVICE iss_ser
         left join SPECIALIST_SERVICE spec_ser on iss_ser.SPECIALIST_SERVICE_ID = spec_ser.SPECIALIST_SERVICE_ID
         left join SPECIALIST spec on spec_ser.SPECIALIST_ID = spec.specialist_id
         left join SERVICE ser on spec_ser.SERVICE_ID = ser.service_id
where date_part('year', COMPLETION_DATE) = 2021
  and SPECIALIST_NAME = 'Олег Грибович';

select SERVICE_NAME, PRICE, CAR_REG_NUMBER, COMPLETION_DATE
from ISSUED_SERVICE iss_ser
         left join SPECIALIST_SERVICE spec_ser on iss_ser.SPECIALIST_SERVICE_ID = spec_ser.SPECIALIST_SERVICE_ID
         left join SPECIALIST spec on spec_ser.SPECIALIST_ID = spec.specialist_id
         left join SERVICE ser on spec_ser.SERVICE_ID = ser.service_id
where date_part('month', COMPLETION_DATE) = 8
  and SPECIALIST_NAME = 'Олег Грибович'
  and date_part('year', COMPLETION_DATE) = 2021;

select SERVICE_NAME, PRICE, CAR_REG_NUMBER, COMPLETION_DATE
from ISSUED_SERVICE iss_ser
         left join SPECIALIST_SERVICE spec_ser on iss_ser.SPECIALIST_SERVICE_ID = spec_ser.SPECIALIST_SERVICE_ID
         left join SPECIALIST spec on spec_ser.SPECIALIST_ID = spec.specialist_id
         left join SERVICE ser on spec_ser.SERVICE_ID = ser.service_id
where date_part('year', COMPLETION_DATE) = 2021
  and date_part('month', COMPLETION_DATE) = 8
  and date_part('day', COMPLETION_DATE) = 29
  and SPECIALIST_NAME = 'Олег Грибович';

select SERVICE_NAME, PRICE, CAR_REG_NUMBER, COMPLETION_DATE
from ISSUED_SERVICE iss_ser
         left join SPECIALIST_SERVICE spec_ser on iss_ser.SPECIALIST_SERVICE_ID = spec_ser.SPECIALIST_SERVICE_ID
         left join SPECIALIST spec on spec_ser.SPECIALIST_ID = spec.specialist_id
         left join SERVICE ser on spec_ser.SERVICE_ID = ser.service_id
where (date_part('month', COMPLETION_DATE) = 3 * (3 - 1) + 1 or date_part('month', COMPLETION_DATE) = 3 * (3 - 1) + 2 or
       date_part('month', COMPLETION_DATE) = 3 * (3 - 1) + 3)
  and SPECIALIST_NAME = 'Олег Грибович'
  and date_part('year', COMPLETION_DATE) = 2021;

--1 tr
begin;
insert into CLIENT(CLIENT_NAME, BANK, PHONE_NUMBER)
values ('Никита Облепиха', '3886993058377384753', '+79478046398');
insert into CAR
values ('А7719ПР078', 3);
insert into "user" (USERNAME, PASSWORD, ROLE)
values ('NO', 'client', 'CLIENT');
insert into USER_CLIENT (CLIENT_ID, USERNAME)
values (3, 'NO');
commit;
--

-- 2 tr
insert into SPECIALIST(SPECIALIST_NAME, PHONE_NUMBER)
values ('Аркадий Пакетик', '+77484496396');
insert into SPECIALIST_SERVICE(SERVICE_ID, SPECIALIST_ID)
values (7, 4); -- нужна проверка на услугу
insert into "user" (USERNAME, PASSWORD, ROLE)
values ('AP', 'specialist', 'SPECIALIST');
insert into USER_SPECIALIST (SPECIALIST_ID, USERNAME)
values (4, 'AP');
--

insert into SPECIALIST_SERVICE(SERVICE_ID, SPECIALIST_ID) -- нужна проверка на специалиста
values (6, 4);

insert into CAR
values ('А564УА003', 2);

insert into SERVICE(SERVICE_NAME, price)
values ('Полировка кузова', 300);

insert into ISSUED_SERVICE(COMPLETION_DATE, SPECIALIST_SERVICE_ID, CAR_REG_NUMBER) -- нужна проверка на услугу и машину
values ('2021-09-29', 5, 'А564УА003');

delete
from SPECIALIST_SERVICE
where SPECIALIST_SERVICE_ID = 3;

-- 3
select SPECIALIST_ID
from SPECIALIST
where SPECIALIST_NAME = 'NAME';
delete
from "user"
where USERNAME in (select USERNAME from USER_SPECIALIST where SPECIALIST_ID = 3);
delete
from SPECIALIST
where SPECIALIST_ID = 3;
--

-- 4
delete
from "user"
where USERNAME in (select USERNAME from USER_CLIENT where CLIENT_ID = 3);
delete
from CLIENT
where CLIENT_ID = 3;
--

delete
from SERVICE
where SERVICE_ID = 3;

delete
from CAR
where CAR_REG_NUMBER = 'Т938ПР132';

update ISSUED_SERVICE
set COMPLETION_DATE = '2020-01-02'
where issued_service_id = 1
  and COMPLETION_DATE is null
  and SPECIALIST_SERVICE_ID in (
    select SPECIALIST_SERVICE_ID
    from SPECIALIST_SERVICE
    where SPECIALIST_ID in (
        select SPECIALIST_ID
        from USER_SPECIALIST
        where USERNAME = 'OG'));

select ISSUED_SERVICE_ID, CAR_REG_NUMBER, COMPLETION_DATE
from ISSUED_SERVICE
where SPECIALIST_SERVICE_ID in (
    select SPECIALIST_SERVICE_ID
    from SPECIALIST_SERVICE
    where SPECIALIST_ID in (
        select SPECIALIST_ID
        from USER_SPECIALIST
        where USERNAME = 'IC'));

select ISSUED_SERVICE_ID, CAR.CAR_REG_NUMBER, CLIENT_NAME, SPECIALIST_NAME, SERVICE_NAME, COMPLETION_DATE
from ISSUED_SERVICE
         left join CAR on ISSUED_SERVICE.CAR_REG_NUMBER = CAR.CAR_REG_NUMBER
         left join CLIENT on CLIENT.CLIENT_ID = CAR.CLIENT_ID
         left join SPECIALIST_SERVICE on SPECIALIST_SERVICE.SPECIALIST_SERVICE_ID = ISSUED_SERVICE.SPECIALIST_SERVICE_ID
         left join SPECIALIST on SPECIALIST.SPECIALIST_ID = SPECIALIST_SERVICE.SPECIALIST_ID
         left join SERVICE on SERVICE.SERVICE_ID = SPECIALIST_SERVICE.SERVICE_ID;

select CLIENT_ID
from CLIENT
where CLIENT_NAME = '';

select car.CAR_REG_NUMBER, CLIENT_NAME, cl.CLIENT_ID
from ISSUED_SERVICE iss_ser
         left join CAR car on iss_ser.CAR_REG_NUMBER = car.CAR_REG_NUMBER
         left join CLIENT cl on car.CLIENT_ID = cl.CLIENT_ID
where COMPLETION_DATE is null
group by car.CAR_REG_NUMBER, CLIENT_NAME, cl.CLIENT_ID;
