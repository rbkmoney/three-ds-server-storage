CREATE SCHEMA IF NOT EXISTS three_ds_server_storage;

CREATE TABLE IF NOT EXISTS three_ds_server_storage.card_range
(
    provider_id CHARACTER VARYING NOT NULL,
    range_start BIGINT            NOT NULL,
    range_end   BIGINT            NOT NULL,
    CONSTRAINT card_range_pkey PRIMARY KEY (provider_id, range_start, range_end)
);

CREATE TABLE IF NOT EXISTS three_ds_server_storage.challenge_flow_transaction_info
(
    transaction_id          CHARACTER VARYING           NOT NULL,
    device_channel          CHARACTER VARYING           NOT NULL,
    decoupled_auth_max_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    acs_dec_con_ind         CHARACTER VARYING           NOT NULL,
    CONSTRAINT challenge_flow_transaction_info_pkey PRIMARY KEY (transaction_id)
);

CREATE TABLE IF NOT EXISTS three_ds_server_storage.serial_num
(
    provider_id CHARACTER VARYING NOT NULL,
    serial_num  CHARACTER VARYING NOT NULL,
    CONSTRAINT serial_num_pkey PRIMARY KEY (provider_id)
);
