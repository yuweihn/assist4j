drop table if exists sequence;
create table sequence (
	segment            int(11)                      not null      default 0  comment '分片，从0开始计数',
	name               varchar(255)                 not null,
	current_value      bigint(20)  unsigned         not null,
	create_time        datetime                     not null,
	update_time        datetime                     not null,

	primary      key(segment, name)
) engine=innodb default charset=utf8;
