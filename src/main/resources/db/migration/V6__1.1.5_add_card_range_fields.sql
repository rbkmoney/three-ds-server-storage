alter table three_ds_server_storage.card_range
    add column acs_start_protocol_version character varying NOT NULL DEFAULT '2.1.0',
    add column acs_end_protocol_version character varying NOT NULL DEFAULT '2.1.0',
    add column ds_start_protocol_version character varying NOT NULL DEFAULT '2.1.0',
    add column ds_end_protocol_version character varying NOT NULL DEFAULT '2.1.0',
    add column acs_information_indicator character varying;
