CREATE TABLE IF NOT EXISTS three_ds_server_storage.last_updated
(
    provider_id     CHARACTER VARYING           NOT NULL,
    last_updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT last_updated_pkey PRIMARY KEY (provider_id)
);
